package com.zetra.econsig.web.controller.margem;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.KYCHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.consignacao.AbstractIncluirConsignacaoWebController;

/**
 * <p>Title: ExecutarKYCWebController</p>
 * <p>Description: Controlador Web para o casos de uso do fluxo do KYC.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/executarKYC" })
public class ExecutarKYCWebController extends AbstractIncluirConsignacaoWebController {

    @Override
    protected String getFunCodigo() {
        return CodedValues.FUN_RES_MARGEM;
    }

    @Override
    protected String validarServicoOperacao(String svcCodigo, String rseCodigo, Map<String, String> parametrosPlano, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(svcCodigo)) {
            svcCodigo = request.getParameter("SVC_CODIGO");
        }
        if (TextHelper.isNull(svcCodigo)) {
            throw new ViewHelperException("mensagem.erro.servico.nao.informado", responsavel);
        }
        return svcCodigo;
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.solicitar.consignacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/executarKYC");
        model.addAttribute("tipoOperacao", "salvarPanNumber");
    }

    @RequestMapping(params = { "acao=salvarPanNumber" })
    public String salvarPanNumber(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!responsavel.isSer() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);
        try {
            String serCodigo = responsavel.getSerCodigo();
            String panNumber = JspHelper.verificaVarQryStr(request, "panNumber");
            KYCHelper kycHelper = new KYCHelper(serCodigo, responsavel);
            kycHelper.setPanNumber(panNumber);
        } catch (ServidorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        ParamSession paramSession = ParamSession.getParamSession(session);
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(params = { "acao=finalizar" })
    public String finalizar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!responsavel.isSer() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);
        try {
            String serCodigo = responsavel.getSerCodigo();
            KYCHelper kycHelper = new KYCHelper(serCodigo, responsavel);
            String status = kycHelper.checkKYC(kycHelper.getStatus());
            if (status.equals(KYCHelper.CHECK_KYC_STATUS_PENDING)) {
                model.addAttribute("linkExternoKYC", ParamSist.getInstance().getParam(CodedValues.TPC_KYC_URL_JORNADA_VALIDACAO, responsavel));
                return viewRedirect("jsp/reservarMargem/finalizarKYC", request, session, model, responsavel);
            } else if (status.equals(KYCHelper.CHECK_KYC_STATUS_VALID)) {
                // KYC é válido, verificar se precisa salvar o novo dado e então pode continuar para a reserva
                kycHelper.validar();
            } else {
                // Faz uma segunta tentativa conforme solicitado pelo cliente.
                status = kycHelper.checkKYC(kycHelper.getStatus());
                if (status.equals(KYCHelper.CHECK_KYC_STATUS_PENDING)) {
                    model.addAttribute("linkExternoKYC", ParamSist.getInstance().getParam(CodedValues.TPC_KYC_URL_JORNADA_VALIDACAO, responsavel));
                    return viewRedirect("jsp/reservarMargem/finalizarKYC", request, session, model, responsavel);
                } else if (status.equals(KYCHelper.CHECK_KYC_STATUS_VALID)) {
                    // KYC é válido, verificar se precisa salvar o novo dado e então pode continuar para a reserva
                    kycHelper.validar();
                } else {
                    // Segunda tentativa deu erro. Mostra mensagem de erro para o usuário/servidor.
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.kyc.falha.na.requisicao", responsavel, status));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }
        } catch (ServidorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.kyc.finalizado", responsavel));
        ParamSession paramSession = ParamSession.getParamSession(session);
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }
}
