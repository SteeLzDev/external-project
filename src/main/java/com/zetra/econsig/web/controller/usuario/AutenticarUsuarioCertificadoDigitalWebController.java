package com.zetra.econsig.web.controller.usuario;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.CertificadoDigital;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AutenticarEuConsigoMaisWebController</p>
 * <p>Description: Controlador Web para o caso de uso de single singon para o euconsigoMais.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo $
 * $Revision: 26106 $
 * $Date: 2019-01-16 11:17:49 -0200 (qua, 16 jan 2019) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/autenticarUsuarioCertificadoDigital" })
public class AutenticarUsuarioCertificadoDigitalWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AutenticarUsuarioCertificadoDigitalWebController.class);

    @RequestMapping(params = { "acao=iniciar" }, method = { RequestMethod.GET, RequestMethod.POST })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        int tempo = 1;

        try {
            if (session.getAttribute("valida_certificado_digital") != null) {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.informacao.validando.certificado.digital", null));

                tempo = request.getParameter("tempoAtual") != null ? Integer.parseInt(request.getParameter("tempoAtual").toString()) + 1 : 1;

                if ((tempo > 4) || (!TextHelper.isNull(session.getAttribute(CodedValues.MSG_SESSAO_INVALIDA)))) {
                    // Se passou o tempo então coloca mensagem de erro
                    if (TextHelper.isNull(session.getAttribute(CodedValues.MSG_SESSAO_INVALIDA))) {
                        session.setAttribute(CodedValues.MSG_SESSAO_INVALIDA, ApplicationResourcesHelper.getMessage("mensagem.erro.validacao.certificado.digital", null));
                    }
                    session.setAttribute(CodedValues.SESSAO_INVALIDA, "true");
                }

            } else {
                return "forward:/v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true";
            }

            //validar_certificado_digital.jsp

            if (TextHelper.isNull(responsavel.getUsuCodigo())) {
                response.sendRedirect("/v3/expirarSistema?acao=iniciar");
            }

            try {
                final X509Certificate[] certs = (X509Certificate[]) request.getAttribute("jakarta.servlet.request.X509Certificate");
                if (certs != null) {
                    for (final X509Certificate cert : certs) {
                        if (cert.getSubjectX500Principal() != null) {
                            validateCert(cert, session, responsavel);
                            break;
                        }
                    }
                } else {
                    final String sslClientCert = request.getHeader("ssl_client_cert");
                    if (!TextHelper.isNull(sslClientCert)) {
                        final byte[] decoded = Base64.getMimeDecoder().decode(sslClientCert.replace("-----BEGIN CERTIFICATE-----", "").replace("-----END CERTIFICATE-----", ""));
                        final InputStream targetStream = new ByteArrayInputStream(decoded);
                        final X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(targetStream);
                        validateCert(cert, session, responsavel);
                    } else {
                        LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.erro.certificado.digital.obrigatorio", responsavel));
                    }
                }
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }

        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }

        model.addAttribute("tempo", tempo);

        return viewRedirect("jsp/autenticarUsuario/autenticarUsuarioCertificadoDigital", request, session, model, responsavel);
    }

    /**
     * Valida o certificado de cliente enviado pelo usuário. E atualiza a HttpSession do usuário com o resultado da validação.
     * @param cert
     * @param session
     * @param responsavel
     * @throws UsuarioControllerException
     * @throws ConsignatariaControllerException
     */
    private void validateCert(X509Certificate cert, HttpSession session, AcessoSistema responsavel) throws UsuarioControllerException, ConsignatariaControllerException {
        final Boolean validou = CertificadoDigital.getInstance().validarCertificado(cert, responsavel);
        if ((validou != null) && validou.equals(Boolean.TRUE)) {
            session.removeAttribute("valida_certificado_digital");
        } else {
            session.setAttribute(CodedValues.MSG_SESSAO_INVALIDA, ApplicationResourcesHelper.getMessage("mensagem.erro.validacao.informacoes.certificado.digital", responsavel));
        }
    }
}