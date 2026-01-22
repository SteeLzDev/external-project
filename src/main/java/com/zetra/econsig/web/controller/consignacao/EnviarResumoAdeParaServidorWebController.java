package com.zetra.econsig.web.controller.consignacao;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sms.SMSHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: EnviarResumoAdeParaServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Enviar Resumo de Consignação para o Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/enviarResumoAdeParaServidor" })
public class EnviarResumoAdeParaServidorWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarResumoAdeParaServidorWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @RequestMapping(params = { "acao=enviar" })
    public String enviar(@RequestParam(value = "ADE_CODIGO", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            // Obtém os dados da ADE
            CustomTransferObject autdes = TransferObjectHelper.mascararUsuarioHistorico(pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel), null, responsavel);
            String adeNumero = autdes.getAttribute(Columns.ADE_NUMERO).toString();
            String sadDescricao = autdes.getAttribute(Columns.SAD_DESCRICAO) != null ? autdes.getAttribute(Columns.SAD_DESCRICAO).toString() : "";
            String csaNome = (String) autdes.getAttribute(Columns.CSA_NOME_ABREV);
            if (TextHelper.isNull(csaNome)) {
                csaNome = (String) autdes.getAttribute(Columns.CSA_NOME);
            }
            if (csaNome.length() > 50) {
                csaNome = csaNome.substring(0, 47) + "...";
            }

            // Obtém os dados do servidor, para verificar se possui e-mail ou celular cadastrados
            String serEmail = (String) autdes.getAttribute(Columns.SER_EMAIL);
            String serCelular = (String) autdes.getAttribute(Columns.SER_CELULAR);

            boolean resumoEnviadoEmail = false;
            boolean resumoEnviadoSMS = false;

            // Enviar o resumo da ADE por e-mail
            if (!TextHelper.isNull(serEmail)) {
                EnviaEmailHelper.enviarEmailResumoConsignacao(serEmail, autdes, responsavel);
                resumoEnviadoEmail = true;
            }

            // Enviar o resumo da ADE por SMS
            if (!TextHelper.isNull(serCelular)) {
                // Credenciais para envio do SMS
                String accountSid = ParamSist.getInstance().getParam(CodedValues.TPC_SID_CONTA_SMS, responsavel).toString();
                String authToken = ParamSist.getInstance().getParam(CodedValues.TPC_TOKEN_AUTENTICACAO_SMS, responsavel).toString();
                String fromNumber = ParamSist.getInstance().getParam(CodedValues.TPC_NUMERO_REMETENTE_SMS, responsavel).toString();

                if (TextHelper.isNull(accountSid) || TextHelper.isNull(authToken) || TextHelper.isNull(fromNumber)) {
                    LOG.warn("Necessário habilitar os parâmetros de sistema " + CodedValues.TPC_SID_CONTA_SMS + ", " + CodedValues.TPC_TOKEN_AUTENTICACAO_SMS + ", " + CodedValues.TPC_NUMERO_REMETENTE_SMS + " para envio de SMS.");
                    throw new ViewHelperException("mensagem.erro.sms.enviar", responsavel);

                } else {
                    String celularDestinatario = LocaleHelper.formataCelular(serCelular);
                    if (!TextHelper.isNull(celularDestinatario)) {
                        String corpo = ApplicationResourcesHelper.getMessage("mensagem.sms.resumo.consignacao", responsavel, adeNumero, csaNome, sadDescricao);
                        new SMSHelper(accountSid, authToken, fromNumber).send(celularDestinatario, TextHelper.removeAccent(corpo));
                        resumoEnviadoSMS = true;
                    }
                }
            }

            if (resumoEnviadoEmail || resumoEnviadoSMS) {
                // Registra log de auditoria
                try {
                    LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.ENVIAR_RESUMO_CONSIGNACAO, Log.LOG_INFORMACAO);
                    log.setAutorizacaoDesconto(adeCodigo);
                    if (!TextHelper.isNull(serEmail)) {
                        log.addChangedField(Columns.SER_EMAIL, serEmail);
                    }
                    if (!TextHelper.isNull(serCelular)) {
                        log.addChangedField(Columns.SER_CELULAR, serCelular);
                    }
                    log.write();
                } catch (LogControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                }

                // Envia mensagem de sucesso
                if (resumoEnviadoEmail && resumoEnviadoSMS) {
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso.enviar.resumo.consignacao.servidor.ambos", responsavel));
                } else if (resumoEnviadoEmail) {
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso.enviar.resumo.consignacao.servidor.email", responsavel));
                } else if (resumoEnviadoSMS) {
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso.enviar.resumo.consignacao.servidor.sms", responsavel));
                }
            } else {
                // Envia mensagem de erro
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.enviar.resumo.consignacao.servidor", responsavel));
            }

            // Volta à operação anterior
            ParamSession paramSession = ParamSession.getParamSession(session);
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
