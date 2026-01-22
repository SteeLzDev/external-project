package com.zetra.econsig.web.controller.termoadesao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.TermoAdesaoServicoTO;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.TermoAdesaoServicoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.sistema.TermoAdesaoServicoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ManterTermoAdesaoServicoWebController</p>
 * <p>Description: Controlador Web para o caso de uso termo de adesão de serviço.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: moises.souza $
 * $Date: 2018-11-09 15:53:04 -0200 (sex, 09 nov 2018) $
 */
@Controller
@RequestMapping(value = { "/v3/manterTermoAdesaoServico" }, method = { RequestMethod.POST })
public class ManterTermoAdesaoServicoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterTermoAdesaoServicoWebController.class);

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private TermoAdesaoServicoController termoAdesaoController;

    @RequestMapping(params = { "acao=listarServicos" })
    public String listarServicos(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!responsavel.isCsa() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String csaCodigo = "";
        String csaNome = "";

        if (responsavel.isCsa()) {
            csaCodigo = responsavel.getCodigoEntidade();
            csaNome = responsavel.getNomeEntidade();
        } else if (responsavel.isCseSup()) {
            final String csa = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
            if (!TextHelper.isNull(csa)) {
                csaCodigo = csa.split(";")[0];
                csaNome = csa.split(";")[1];
            }
        }

        //caso alguma informaçao esteja faltando
        //redireciona a pagina para uma pagina de mensagem generica
        if (csaCodigo.equals("") || csaNome.equals("")) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        //List que irá conter todos os serviços
        List<TransferObject> servicos = new ArrayList<>();
        final String linkRet = request.getRequestURI() + "?acao=listarServicos";
        final String linkEdicao = request.getRequestURI() + "?acao=editarTermoAdesao";
        final String linkVisualizar = request.getRequestURI() + "?acao=revisarTermoAdesao";

        int total = 0;
        int size = 0;
        try {
            servicos = servicoController.selectServicosComParametro(CodedValues.TPS_EXIGE_ACEITE_TERMO_ADESAO, null, csaCodigo, "1", false, responsavel);
            size = JspHelper.LIMITE;
            total = servicos.size();

            for (final TransferObject servico : servicos) {
                final String svcCodigo = (String)servico.getAttribute(Columns.SVC_CODIGO);
                final String svcIdentificador = (String)servico.getAttribute(Columns.SVC_IDENTIFICADOR);

                final Set<String> codigos = new TreeSet<>();

                // Caso tenha mais de um codigo de verba
                final List<TransferObject> convenios = convenioController.getCnvCodVerba(svcCodigo, csaCodigo, responsavel);
                for (final TransferObject convenio : convenios) {
                    final String codVerba = (String) convenio.getAttribute(Columns.CNV_COD_VERBA);
                    if (!TextHelper.isNull(codVerba)) {
                        codigos.add(codVerba);
                    }
                }
                String codVerba = svcIdentificador;
                if (codigos.size() > 0) {
                    codVerba = TextHelper.join(codigos.toArray(), ", ");
                }
                servico.setAttribute(Columns.CNV_COD_VERBA, codVerba);
            }
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            servicos = new ArrayList<>();
        }

        configurarPaginador(linkRet, "rotulo.listar.servicos.titulo", total, size, null, false, request, model);

        final ParamSession paramSession = ParamSession.getParamSession(session);
        final String voltar = TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));
        model.addAttribute("voltar", voltar);

        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("csaNome", csaNome);
        model.addAttribute("linkRet", linkRet);
        model.addAttribute("linkEdicao", linkEdicao);
        model.addAttribute("linkVisualizar", linkVisualizar);
        model.addAttribute("servicos", servicos);

        return viewRedirect("jsp/manterTermoAdesaoServico/listarServicos", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editarTermoAdesao" })
    public String editarTermoAdesao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!responsavel.isCsa() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String csaCodigo = null;
        String csaNome = null;
        if (responsavel.isCsa()) {
            csaCodigo = responsavel.getCodigoEntidade();
            csaNome = responsavel.getNomeEntidade();
        } else {
            final String csa = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
            if (!TextHelper.isNull(csa)) {
                csaCodigo = csa.split(";")[0];
                csaNome = csa.split(";")[1];
            }
        }
        final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
        final String svcDescricao = JspHelper.verificaVarQryStr(request, "SVC_DESCRICAO");

        if (TextHelper.isNull(csaCodigo) || TextHelper.isNull(svcCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        TermoAdesaoServicoTO terAdsTO = null;
        String operacao = "";
        String terAdsTexto = "";
        try {
            terAdsTO = termoAdesaoController.findTermoAdesaoServico(new TermoAdesaoServicoTO(csaCodigo, svcCodigo, null), responsavel);
            terAdsTexto = terAdsTO.getTasTexto();
            operacao = "editar";
        } catch (final Exception ex) {
            // se der erro, pode ser que não exista
            operacao = "inserir";
        }

        final ParamSession paramSession = ParamSession.getParamSession(session);
        final String voltar = TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));
        model.addAttribute("voltar", voltar);

        model.addAttribute("terAdsTexto", terAdsTexto);
        model.addAttribute("operacao", operacao);
        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("csaNome", csaNome);
        model.addAttribute("svcCodigo", svcCodigo);
        model.addAttribute("svcDescricao", svcDescricao);

        return viewRedirect("jsp/manterTermoAdesaoServico/editarTermoAdesao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvarTermoAdesao" })
    public String salvarTermoAdesao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException, TermoAdesaoServicoControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!responsavel.isCsa() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String terAdsTexto = JspHelper.verificaVarQryStr(request, "innerTemp");
        final String operacao = JspHelper.verificaVarQryStr(request, "operacao");
        String csaCodigo = null;

        if (responsavel.isCsa()) {
            csaCodigo = responsavel.getCodigoEntidade();
        } else {
            final String csa = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
            if (!TextHelper.isNull(csa)) {
                csaCodigo = csa.split(";")[0];
            }
        }
        final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");

        if (TextHelper.isNull(csaCodigo) || TextHelper.isNull(svcCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            TermoAdesaoServicoTO terAdsTO = null;
            //checa se o texto esta vazio ou null
            if (!terAdsTexto.equals("") && terAdsTexto != null) {
                terAdsTexto = terAdsTexto.replaceAll("&quot;", "\"");
                //se for alteração
                if (operacao.endsWith("editar")) {
                    terAdsTO = termoAdesaoController.findTermoAdesaoServico(new TermoAdesaoServicoTO(csaCodigo, svcCodigo, null), responsavel);
                    terAdsTO.setTasTexto(terAdsTexto);
                    termoAdesaoController.updateTermoAdesaoServico(terAdsTO, responsavel);
                    //se for inclusão
                } else if (operacao.endsWith("inserir")) {
                    terAdsTO = new TermoAdesaoServicoTO(csaCodigo, svcCodigo, null);
                    terAdsTO.setTasTexto(terAdsTexto);
                    termoAdesaoController.createTermoAdesaoServico(terAdsTO, responsavel);
                }
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.termo.adesao.sucesso", responsavel));
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.termo.adesao.texto", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }
        final ParamSession paramSession = ParamSession.getParamSession(session);
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(params = { "acao=revisarTermoAdesao" })
    public String revisarTermoAdesao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!responsavel.isCsa() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
        final String svcDescricao = JspHelper.verificaVarQryStr(request, "SVC_DESCRICAO");
        String terAdsTexto = "";

        String csaCodigo = null;
        String csaNome = null;

        if (responsavel.isCsa()) {
            // Recuperando as informaçoes da consignatária pela sessão do usuário
            csaCodigo = responsavel.getCodigoEntidade();
            csaNome = responsavel.getNomeEntidade();
        } else if (responsavel.isCor()) {
            // Recuperando as informaçoes da consignatária pela sessão do usuário
            csaCodigo = responsavel.getCodigoEntidadePai();
            csaNome = responsavel.getNomeEntidadePai();
        } else {
            // Se é servidor, CSE ou ORG, obtém as informações pela requisição
            final String csa = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
            if (!TextHelper.isNull(csa)) {
                csaCodigo = csa.split(";")[0];
                csaNome = csa.split(";")[1];
            }
        }

        if (TextHelper.isNull(csaCodigo) || TextHelper.isNull(svcCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        TermoAdesaoServicoTO terAdsTO = null;
        try {
            terAdsTO = termoAdesaoController.findTermoAdesaoServico(new TermoAdesaoServicoTO(csaCodigo, svcCodigo, null), responsavel);
            terAdsTexto = terAdsTO.getTasTexto();
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String titulo = ApplicationResourcesHelper.getMessage("rotulo.termo.adesao.singular", responsavel);
        titulo += " - " + csaNome.toUpperCase() + " - " + svcDescricao.toUpperCase();

        final ParamSession paramSession = ParamSession.getParamSession(session);
        final String voltar = TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));
        model.addAttribute("voltar", voltar);

        model.addAttribute("svcCodigo", svcCodigo);
        model.addAttribute("svcDescricao", svcDescricao);
        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("csaNome", csaNome);
        model.addAttribute("terAdsTexto", terAdsTexto);
        model.addAttribute("titulo", titulo);

        return viewRedirect("jsp/manterTermoAdesaoServico/revisarTermoAdesao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=excluirTermoAdesao" })
    public String excluirTermoAdesao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!responsavel.isCsa() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String csaCodigo = "";

        if (responsavel.isCsa()) {
            //recuperando as informaçoes pelo session
            csaCodigo = responsavel.getCodigoEntidade();

        } else if (responsavel.isCseSup()) {
            final String csa[] = JspHelper.verificaVarQryStr(request, "CSA_CODIGO").split(";");
            csaCodigo = csa[0];
        }

        //caso alguma informaçao esteja faltando
        //redireciona a pagina para uma pagina de mensagem generica
        if (csaCodigo.equals("")) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            final TermoAdesaoServicoTO terAdsTO = new TermoAdesaoServicoTO(csaCodigo, JspHelper.verificaVarQryStr(request, "SVC_CODIGO"), null);
            termoAdesaoController.removeTermoAdesaoServico(terAdsTO, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.excluir.termo.adesao.sucesso", responsavel));
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ALERT, ex.getMessage());
        }

        final ParamSession paramSession = ParamSession.getParamSession(session);
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }
}
