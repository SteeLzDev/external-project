/**
 *
 */
package com.zetra.econsig.web.filter;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: EConsigAuthenticationEntryPoint</p>
 * <p>Description: Classe para fazer o tratamento de usuário com certificado.</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
public class EConsigAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EConsigAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!TextHelper.isNull(request.getHeader("ssl_client_cert"))) {
                    if (!TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_SERVICO_CERT, responsavel))) {
                        request.getRequestDispatcher("/v3/solicitarCertificado?acao=iniciar").forward(request, response);
                        return;
                    } else {
                        request.getRequestDispatcher("/v3/solicitarCertificadoDigital?acao=iniciar").forward(request, response);
                    }

        } else {
            /**
             * DESENV-17859 : Se chegou até aaqui é porque o usuário deve passar seu certificado e não o fez.
             */
            LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.erro.certificado.digital.obrigatorio", responsavel));

            final HttpSession session = request.getSession();
            if (session != null) {
                // Invalida a sessão atual
                session.invalidate();
                // Força a criação de uma nova sessão
                request.getSession(true);
            }

            request.setAttribute("mensagemSessaoExpirada", ApplicationResourcesHelper.getMessage("mensagem.erro.validacao.certificado.digital", responsavel));
            request.getRequestDispatcher("/v3/expirarSistema?acao=iniciar").forward(request, response);
        }
    }
}
