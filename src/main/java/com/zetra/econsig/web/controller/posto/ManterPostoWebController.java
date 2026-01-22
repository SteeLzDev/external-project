package com.zetra.econsig.web.controller.posto;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.zetra.econsig.exception.PostoRegistroServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.sdp.PostoRegistroServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;
/**
 * <p>Title: ManterPostoWebController</p>
 * <p>Description:Manter Posto(Listar e Editar)</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: anderson.assis $
 * $Revision: 30703 $
 * $Date: 2020-10-23 17:59:17 -0300 (sex., 23 out. 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterPosto" })
public class ManterPostoWebController extends ControlePaginacaoWebController{
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterPostoWebController.class);

    @Autowired
    private PostoRegistroServidorController postoRegistroServidorController;

    @RequestMapping(params = { "acao=listar" })
    public String listarPostos(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model){
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //recupera o usuario corrente - responsavel pela transaçao
        boolean podeEditarPosto = responsavel.temPermissao(CodedValues.FUN_EDT_POSTO);

        //recuperando o campo que foi passado como parametro
        String cse_codigo = "";
        String titulo = "";

        if (responsavel.isCseSup()) {
          //recuperando as informaçoes pelo session
          cse_codigo = responsavel.getCodigoEntidade();
          titulo = responsavel.getNomeEntidade();
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        //caso alguma informaçao esteja faltando
        //redireciona a pagina para uma pagina de mensagem generica
        if (cse_codigo.equals("") || titulo.equals("")) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }


        //List que irá conter todos os postos
        List<?> postos = null;
        String linkRet = "../v3/manterPosto?acao=listar&" + SynchronizerToken.generateToken4URL(request);

        String linkEdicao = "../v3/manterPosto?acao=editar&" + SynchronizerToken.generateToken4URL(request);
        long total = 0;
        try {
          CustomTransferObject criterio = new CustomTransferObject();

          total = postoRegistroServidorController.countPostoRegistroServidor(criterio, responsavel);
          int size = JspHelper.LIMITE;
          int offset = 0;
          try {
            offset = Integer.parseInt(request.getParameter("offset"));
          } catch (Exception ex) {}

          postos = postoRegistroServidorController.lstPostoRegistroServidor(criterio, offset, size, responsavel);

        } catch (Exception ex) {
          session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
          postos = new ArrayList<>();
        }



        // Monta lista de parâmetros através dos parâmetros de request
        Set<String> params = new HashSet<>(request.getParameterMap().keySet());
        params.remove("offset");
        List<String> requestParams = new ArrayList<>(params);
        configurarPaginador("../v3/manterPosto?acao=listar", "rotulo.paginacao.titulo.usuario", (int)total, JspHelper.LIMITE, requestParams, false, request, model);

        model.addAttribute("cse_codigo", cse_codigo);
        model.addAttribute("titulo", titulo);
        model.addAttribute("postos", postos);
        model.addAttribute("linkRet", linkRet);
        model.addAttribute("linkEdicao", linkEdicao);
        model.addAttribute("podeEditarPosto", podeEditarPosto);

        return viewRedirect("jsp/manterPosto/listarPosto", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editarPostos(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model){
      //recupera o usuario corrente - responsavel pela transaçao
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)){
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String posCodigo = request.getParameter("pos");

        //recuperando o campo que foi passado como parametro
        String cse_codigo = "";
        String titulo = "";

        if ((responsavel.isCse() || responsavel.isSup())) {
          //recuperando as informaçoes pelo session
          cse_codigo = responsavel.getCodigoEntidade();
          titulo = responsavel.getNomeEntidade();

        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        //caso alguma informaçao esteja faltando
        //redireciona a pagina para uma pagina de mensagem generica
        if (cse_codigo.equals("") || titulo.equals("")) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }


        //List que irá conter todos os postos
        String linkPaginacao = null;
        String linkRet = "../v3/manterPosto?acao=listar&" + SynchronizerToken.generateToken4URL(request);

        //Busca o posto a ser alterado
        TransferObject posto = null;
        try {
          posto = postoRegistroServidorController.buscaPosto(posCodigo, responsavel);
        } catch (PostoRegistroServidorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String reqColumnsStr = "posDescricao|POS_CODIGO|posIdentificador";
        String msgErro = JspHelper.verificaCamposForm(request, session, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");

        //Realiza o update
        if (request.getParameter("MM_update") != null &&
         request.getParameter("POS_CODIGO") != null &&
         msgErro.length() == 0 &&
         responsavel.temPermissao(CodedValues.FUN_EDT_POSTO)) {
          try {
            posto.setAttribute(Columns.POS_IDENTIFICADOR, JspHelper.verificaVarQryStr(request, "posIdentificador"));
            posto.setAttribute(Columns.POS_DESCRICAO, JspHelper.verificaVarQryStr(request, "posDescricao"));
            posto.setAttribute(Columns.POS_VALOR_SOLDO, !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "posVlrSoldo")) ? new BigDecimal(NumberHelper.reformat(String.valueOf(JspHelper.verificaVarQryStr(request, "posVlrSoldo")), NumberHelper.getLang(), "en")) : null);
            posto.setAttribute(Columns.POS_PERC_TAXA_USO, !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "perTxUso")) ? new BigDecimal(NumberHelper.reformat(String.valueOf(JspHelper.verificaVarQryStr(request, "perTxUso")), NumberHelper.getLang(), "en")) : null);
            posto.setAttribute(Columns.POS_PERC_TAXA_USO_COND, !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "perTxUsoCond")) ? new BigDecimal(NumberHelper.reformat(String.valueOf(JspHelper.verificaVarQryStr(request, "perTxUsoCond")), NumberHelper.getLang(), "en")) : null);

            postoRegistroServidorController.updatePosto(posto, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("rotulo.posto.alterado.sucesso", responsavel));

          } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
          }
        }

        String pos_codigo = (String) posto.getAttribute(Columns.POS_CODIGO);
        String pos_descricao = (String)posto.getAttribute(Columns.POS_DESCRICAO);
        String pos_identificador = (String)posto.getAttribute(Columns.POS_IDENTIFICADOR);
        String pos_valor_soldo = posto.getAttribute(Columns.POS_VALOR_SOLDO).toString();
        String pos_perc_tx_uso = posto.getAttribute(Columns.POS_PERC_TAXA_USO).toString();
        String pos_perc_tx_uso_cond = posto.getAttribute(Columns.POS_PERC_TAXA_USO_COND).toString();

        model.addAttribute("pos_codigo", pos_codigo);
        model.addAttribute("pos_descricao", pos_descricao);
        model.addAttribute("pos_identificador", pos_identificador);
        model.addAttribute("pos_valor_soldo", pos_valor_soldo);
        model.addAttribute("pos_perc_tx_uso", pos_perc_tx_uso);
        model.addAttribute("pos_perc_tx_uso_cond", pos_perc_tx_uso_cond);
        model.addAttribute("linkPaginacao", linkPaginacao);
        model.addAttribute("linkRet", linkRet);
        model.addAttribute("msgErro", msgErro);

        return viewRedirect("jsp/manterPosto/editarPosto", request, session, model, responsavel);
    }

}
