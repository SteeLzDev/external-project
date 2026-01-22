package com.zetra.econsig.web.controller.margem;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.servidor.AbstractConsultarServidorWebController;

/**
 * <p>Title: Composicao de Margem</p>
 * <p>Description: Controlador Web para o caso de uso Composição de Margem.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarComposicaoMargem" })
public class ConsultarComposicaoMargemWebController extends AbstractConsultarServidorWebController {

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private ParametroController parametroController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        boolean boolTpcPmtCompMargem = ParamSist.paramEquals(CodedValues.TPC_MOSTRA_COMPOSICAO_MARGEM, CodedValues.TPC_SIM, responsavel);
        if (!boolTpcPmtCompMargem) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            boolean exigeSenhaSerConsultaMargem = parametroController.senhaServidorObrigatoriaConsultaMargem(null, responsavel);

            if (exigeSenhaSerConsultaMargem) {
                model.addAttribute("exibirCampoSenhaAutorizacao", true);
                model.addAttribute("omitirAdeNumero", true);
                return super.iniciar(request, response, session, model);
            } else {
                return listar(request, response, session, model);
            }
        } catch (ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    protected void definirAcaoRetorno(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        ParamSession paramSession = ParamSession.getParamSession(session);
        model.addAttribute("acaoRetorno", SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));
    }

    @RequestMapping(params = { "acao=listar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String strRseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
        if (strRseCodigo.length() > 0) {
            strRseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
        } else {
            strRseCodigo = (String) model.asMap().get("rseCodigo");
        }

        boolean boolTpcPmtCompMargem = ParamSist.paramEquals(CodedValues.TPC_MOSTRA_COMPOSICAO_MARGEM, CodedValues.TPC_SIM, responsavel);

        List<TransferObject> lstCompMargem = null;
        // Valida a senha após a pesquisa, pois caso o RSE_CODIGO não tenha sido passado, será obtido da listagem
        if (!validarSenhaServidor(strRseCodigo, false, request, session, responsavel)) {
            return iniciar(request, response, session, model);
        }

        if (boolTpcPmtCompMargem) {
            // Se permite composição de margem, busca os itens da composição
            try {
                lstCompMargem = autorizacaoController.historicoComposicaoMargem(strRseCodigo, responsavel);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                lstCompMargem = null;
            }
        }

        ParamSession paramSession = ParamSession.getParamSession(session);
        String voltar = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory() + "&RSE_CODIGO=" + strRseCodigo, request);
        if (voltar.contains("consultarComposicaoMargem")) {
            voltar = voltar.replace("&acao=pesquisarServidor", "&acao=iniciar");
        } else if (voltar.contains("consultarMargem")) {
            voltar = voltar.replace("&acao=pesquisarServidor", "&acao=consultar");
        } else {
            voltar = voltar.replace("&acao=pesquisarServidor", "&acao=reservarMargem");
        }

        model.addAttribute("lstCompMargem", lstCompMargem);
        model.addAttribute("rseCodigo", strRseCodigo);
        model.addAttribute("voltar", voltar);
        return viewRedirect("jsp/consultarComposicaoMargem/consultarComposicaoMargem", request, session, model, responsavel);
    }

    @Override
    protected String continuarOperacao(String rseCodigo, String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException, UsuarioControllerException {
        model.addAttribute("rseCodigo", rseCodigo);
        return listar(request, response, session, model);
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws com.zetra.econsig.exception.ViewHelperException {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.composicao.margem.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/consultarComposicaoMargem");
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        return "listar";
    }

    @RequestMapping(params = { "acao=iniciarReserva" })
    public String iniciarReserva(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=reservar" })
    public String reservar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return listar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=renegociar" })
    public String renegociar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return listar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=comprar" })
    public String comprar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return listar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=comp_margem" })
    public String composicaoMargem(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return listar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=despesa_individual" })
    public String despesaIndividual(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return listar(request, response, session, model);
    }
}
