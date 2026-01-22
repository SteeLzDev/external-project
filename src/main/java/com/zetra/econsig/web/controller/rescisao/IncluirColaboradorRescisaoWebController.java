package com.zetra.econsig.web.controller.rescisao;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.VerbaRescisoriaControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.rescisao.VerbaRescisoriaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.servidor.AbstractConsultarServidorWebController;

/**
 * <p>Title: IncluirColaboradorRescisaoWebController</p>
 * <p>Description: Web Controller para inclusão de colaborador na lista de candidatos à rescisão contratual</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/incluirColaboradorRescisao" })
public class IncluirColaboradorRescisaoWebController extends AbstractConsultarServidorWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(IncluirColaboradorRescisaoWebController.class);

    @Autowired
    VerbaRescisoriaController verbaRescisoriaController;
    
    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.listar.colaborador.rescisao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/incluirColaboradorRescisao");
        model.addAttribute("omitirAdeNumero", true);
    }
    
    @Override
    protected void definirAcaoRetorno(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        model.addAttribute("acaoRetorno", SynchronizerToken.updateTokenInURL("../v3/listarColaboradoresRescisao?acao=iniciar", request));
    }
    
    @Override
    protected String tratarSevidorNaoEncontrado(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        // Repassa o token salvo, pois o método irá revalidar o token
        request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

        // Retorna para operação de pesquisar de servidor
        return super.iniciar(request, response, session, model);
    }
    
    @Override
    protected String continuarOperacao(String rseCodigo, String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException, UsuarioControllerException {
        return incluir(rseCodigo, request, response, session, model);
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        return "incluir";
    }

    @RequestMapping(params = { "acao=incluir" })
    public String incluir(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Inclui o colaborador selecionado como candidato na lista de rescisão contratual
        if (rseCodigo != null && !TextHelper.isNull(rseCodigo)) {
            try {
                verbaRescisoriaController.incluirCandidatoVerbaRescisoria(rseCodigo, responsavel);
            } catch (VerbaRescisoriaControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        // redireciona para a lista de colaboradores candidatos à rescisão contratual
        return "forward:/v3/listarColaboradoresRescisao?acao=iniciar&RSE_CODIGO=" + rseCodigo + "&_skip_history_=true";
    }
}
