package com.zetra.econsig.web.controller.reclamacao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ReclamacaoRegistroServidorControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.servidor.ReclamacaoRegistroServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarReclamacao" })
public class EditarReclamacaoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarReclamacaoWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ReclamacaoRegistroServidorController reclamacaoRegistroServidorController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ParseException, ReclamacaoRegistroServidorControllerException, ConsignatariaControllerException {
      AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

      // Valida o token de sessão para evitar a chamada direta à operação
      if (!TextHelper.isNull(request.getParameter("pesquisar")) && !SynchronizerToken.isTokenValid(request)) {
        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
      }
      SynchronizerToken.saveToken(request);

      Object paramCsaPermitidosRrs = ParamSist.getInstance().getParam(CodedValues.TPC_SER_RELATA_RECLAMACAO_APENAS_CSA_COM_ADE, responsavel);
      boolean rrsApenasPraCsaComAde = (paramCsaPermitidosRrs == null) ? true: ((paramCsaPermitidosRrs.equals("S")) ? true:false);

      boolean podeCriarReclamacao = responsavel.temPermissao(CodedValues.FUN_EDT_RECLAMACAO);

      String csaCodigo = null;
      String serCodigo = null;
      StringBuilder queryString = new StringBuilder("");
      List<String> motivosCodigos = null;

      CustomTransferObject criteriosPesquisa = new CustomTransferObject();

      //Define valores padrão para filtro de data inicial e final, caso seja obrigatório o filtro
      String filtroDataFim = "";
      String filtroDataIni = "";

      if (!TextHelper.isNull(request.getParameter("periodoIni"))) {
          filtroDataIni = request.getParameter("periodoIni");
          criteriosPesquisa.setAttribute("periodoIni", filtroDataIni);
          queryString.append("periodoIni(").append(filtroDataIni);
      }
      if (!TextHelper.isNull(request.getParameter("periodoFim"))) {
          filtroDataFim = request.getParameter("periodoFim");
          criteriosPesquisa.setAttribute("periodoFim", filtroDataFim);
          if (!queryString.toString().equals("")) {
              queryString.append("|");
          }
          queryString.append("periodoFim(").append(filtroDataFim);
      }

      if (!TextHelper.isNull(request.getParameter("RSE_MATRICULA"))) {
          criteriosPesquisa.setAttribute(Columns.RSE_MATRICULA, request.getParameter("RSE_MATRICULA"));

          if (!queryString.toString().equals("")) {
              queryString.append("|");
          }
          queryString.append("RSE_MATRICULA(").append(request.getParameter("RSE_MATRICULA"));
      }

      if (responsavel.isSer()) {
          serCodigo = responsavel.getSerCodigo();
      }
      csaCodigo = (!TextHelper.isNull(csaCodigo)) ? csaCodigo : (request.getParameter("CSA_CODIGO") != null) ? request.getParameter("CSA_CODIGO"): "";
      serCodigo = (!TextHelper.isNull(serCodigo)) ? serCodigo : request.getParameter("SER_CODIGO");

      criteriosPesquisa.setAttribute(Columns.CSA_CODIGO, csaCodigo);
      if (!TextHelper.isNull(csaCodigo)) {
         if (!queryString.toString().equals("")) {
            queryString.append("|");
         }
         queryString.append("CSA_CODIGO(").append(csaCodigo);
      }

      criteriosPesquisa.setAttribute(Columns.SER_CODIGO, serCodigo);
      if (!TextHelper.isNull(serCodigo)) {
          if (!queryString.toString().equals("")) {
             queryString.append("|");
          }
          queryString.append(Columns.getColumnName(Columns.SER_CODIGO)).append("=").append(serCodigo);
       }

      if (!TextHelper.isNull(request.getParameter("SER_CPF"))) {
          criteriosPesquisa.setAttribute(Columns.SER_CPF, request.getParameter("SER_CPF"));
          if (!queryString.toString().equals("")) {
              queryString.append("|");
           }
           queryString.append("SER_CPF(").append(request.getParameter("SER_CPF"));
      }

      if (!TextHelper.isNull(request.getParameter("TMR_CODIGO"))) {
          String[] tmrCodigoArray =  request.getParameterValues("TMR_CODIGO");
          if (tmrCodigoArray != null && tmrCodigoArray.length > 0) {
              motivosCodigos = Arrays.asList(tmrCodigoArray);
              criteriosPesquisa.setAttribute(Columns.TMR_CODIGO, motivosCodigos);
          }
      }

      int total = reclamacaoRegistroServidorController.countReclamacaoRegistroServidor(criteriosPesquisa, responsavel);
      int size = JspHelper.LIMITE;
      int offset = 0;
      try {
        offset = Integer.parseInt(request.getParameter("offset"));
      } catch (Exception ex) {}

      List<TransferObject> reclamacoes = reclamacaoRegistroServidorController.listaReclamacaoRegistroServidor(criteriosPesquisa, offset, size, responsavel);

      // Monta lista de parâmetros através dos parâmetros de request
      Set<String> params = new HashSet<>(request.getParameterMap().keySet());

      // Ignora os parâmetros abaixo
      params.remove("offset");
      params.remove("back");
      params.remove("linkRet");
      params.remove("linkRet64");
      params.remove("eConsig.page.token");
      params.remove("_skip_history_");
      params.remove("pager");
      params.remove("acao");

      List<String> requestParams = new ArrayList<>(params);
      String linkAction = request.getRequestURI() + "?acao=iniciar";
      configurarPaginador(linkAction, "rotulo.paginacao.titulo.consignataria", total, size, requestParams, false, request, model);

      // Busca os tipos de motivos de reclamação
      List<TransferObject> tiposReclamacao = reclamacaoRegistroServidorController.lstTipoMotivoReclamacao(0, -1, responsavel);

      List<TransferObject> consignatarias = null;
      if (rrsApenasPraCsaComAde && responsavel.isSer()) {
         consignatarias = consignatariaController.lstConsignatariaSerTemAde(serCodigo, responsavel);
      } else {
          consignatarias = consignatariaController.lstConsignatarias(null, responsavel);
      }

      model.addAttribute("rrsApenasPraCsaComAde", rrsApenasPraCsaComAde);
      model.addAttribute("podeCriarReclamacao", podeCriarReclamacao);
      model.addAttribute("csaCodigo", csaCodigo);
      model.addAttribute("serCodigo", serCodigo);
      model.addAttribute("motivosCodigos", motivosCodigos);
      model.addAttribute("filtroDataIni", filtroDataIni);
      model.addAttribute("filtroDataFim", filtroDataFim);
      model.addAttribute("reclamacoes", reclamacoes);
      model.addAttribute("tiposReclamacao", tiposReclamacao);
      model.addAttribute("consignatarias", consignatarias);

      return viewRedirect("jsp/editarReclamacao/listarReclamacao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=detalharReclamacao" } )
    public String detalharReclamacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ReclamacaoRegistroServidorControllerException {
      AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

      // Valida o token de sessão para evitar a chamada direta à operação
      if (request.getParameter("rrs_codigo") != null && !SynchronizerToken.isTokenValid(request)) {
        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
      }
      SynchronizerToken.saveToken(request);

      String rrsCodigo = JspHelper.verificaVarQryStr(request, "rrs_codigo");

      CustomTransferObject rrs = null;
      List<TransferObject> rrsMotivos = null;
      try {
          // busca dados da reclamação
          rrs = reclamacaoRegistroServidorController.buscaReclamacao(rrsCodigo, responsavel);
          // busca os motivos da reclamação
          rrsMotivos = reclamacaoRegistroServidorController.lstReclamacaoMotivo(rrsCodigo, responsavel);
      } catch (Exception ex) {
        LOG.error(ex.getMessage(), ex);
        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
      }

      model.addAttribute("rrs", rrs);
      model.addAttribute("rrsMotivos", rrsMotivos);

      return viewRedirect("jsp/editarReclamacao/detalharReclamacao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editarReclamacao" } )
    public String editarReclamacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ReclamacaoRegistroServidorControllerException, InstantiationException, IllegalAccessException, ConsignatariaControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        int tamMaxMsg = 65000; // Como o texto é concatenado na tb_log (LOG_OBS), não posso usar 65535

        try {
            tamMaxMsg = !ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_MSG_RECLAMACAO, responsavel).toString().equals("0") ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_MSG_RECLAMACAO, responsavel).toString()) : tamMaxMsg;
        } catch (Exception ex) {}

        Object paramCsaPermitidosRrs = ParamSist.getInstance().getParam(CodedValues.TPC_SER_RELATA_RECLAMACAO_APENAS_CSA_COM_ADE, responsavel);
        boolean rrsApenasPraCsaComAde = (paramCsaPermitidosRrs == null) ? true: ((paramCsaPermitidosRrs.equals("S")) ? true : false);

        String serCodigo = responsavel.getSerCodigo();
        String rrsMensagem = null;
        String csaCodigo = null;
        String rseCodigo = null;
        List<String> motivosCodigos = null;
        boolean mensagemLida = JspHelper.verificaVarQryStr(request, "MSG_CHECK").equals("true");
        if (mensagemLida) {
            rrsMensagem = JspHelper.verificaVarQryStr(request, "MENSAGEM");
            csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO_DESTINO");
            rseCodigo = responsavel.getRseCodigo();
            String[] tmrCodigoArray =  request.getParameterValues("TMR_CODIGO");
            if (tmrCodigoArray != null && tmrCodigoArray.length > 0) {
                motivosCodigos = Arrays.asList(tmrCodigoArray);
            }
        }

        //Busca os tipos de motivos de reclamação
        List<TransferObject> tiposReclamacao = reclamacaoRegistroServidorController.lstTipoMotivoReclamacao(0, -1, responsavel);
        if (!TextHelper.isNull(rrsMensagem)) {
            // grava o registro de reclamação
            CustomTransferObject novaReclamacao = new CustomTransferObject();
            novaReclamacao.setAttribute(Columns.RRS_RSE_CODIGO, rseCodigo);
            novaReclamacao.setAttribute(Columns.RRS_CSA_CODIGO, csaCodigo);
            novaReclamacao.setAttribute(Columns.RRS_DATA, DateHelper.getSystemDatetime());
            novaReclamacao.setAttribute(Columns.RRS_TEXTO, rrsMensagem);
            novaReclamacao.setAttribute(Columns.RRS_IP_ACESSO, JspHelper.getRemoteAddr(request));
            novaReclamacao.setAttribute(Columns.TMR_CODIGO, motivosCodigos);
            try {
                reclamacaoRegistroServidorController.createReclamacaoRegistroServidor(novaReclamacao, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.criar.reclamacao.sucesso", responsavel));
                ParamSession paramSession = ParamSession.getParamSession(session);
                request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                return "jsp/redirecionador/redirecionar";
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }

        List<TransferObject> consignatarias = null;
        if(mensagemLida) {
            if (rrsApenasPraCsaComAde && responsavel.isSer()) {
                consignatarias = consignatariaController.lstConsignatariaSerTemAde(serCodigo, responsavel);
            } else {
                consignatarias = consignatariaController.lstConsignatarias(null, responsavel);
            }
        }

        model.addAttribute("tamMaxMsg", tamMaxMsg);
        model.addAttribute("rrsApenasPraCsaComAde", rrsApenasPraCsaComAde);
        model.addAttribute("mensagemLida", mensagemLida);
        model.addAttribute("serCodigo", serCodigo);
        model.addAttribute("rrsMensagem", rrsMensagem);
        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("tiposReclamacao", tiposReclamacao);
        model.addAttribute("consignatarias", consignatarias);

        return viewRedirect("jsp/editarReclamacao/editarReclamacao", request, session, model, responsavel);
    }
}