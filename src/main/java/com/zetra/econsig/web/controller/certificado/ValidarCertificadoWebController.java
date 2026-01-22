package com.zetra.econsig.web.controller.certificado;

import java.security.KeyPair;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import javax.crypto.BadPaddingException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.webclient.cert.CERTClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ValidarCertificadoWebController</p>
 * <p>Description: Controlador Web para validar certificado.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $ $ : 2018-05-17 14:28:30 -0300
 * (Ter, 17 mai 2018) $
 */

@Controller
@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = { "/v3/validarCertificado" })
public class ValidarCertificadoWebController extends AbstractWebController {
    
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidarCertificadoWebController.class);
    
    @Autowired
    private CERTClient certClient;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            SynchronizerToken.saveToken(request);

            HashMap<String, String> retorno = parseState(request, responsavel);
            String usuCodigo = retorno.get(CodedValues.CERT_USU_CODIGO);
            String token = retorno.get(CodedValues.CERT_TOKEN);
            String ipAcesso = retorno.get(CodedValues.CERT_IP_ACESSO);
            String dataStr = retorno.get(CodedValues.CERT_DATA);
            Date data = DateHelper.parse(dataStr, "dd/MM/yyyy HH:mm:ss");

            if (!responsavel.getUsuCodigo().equals(usuCodigo) || !responsavel.getIpUsuario().equals(ipAcesso) || DateHelper.minDiff(data) > 5) {
                // Se passou o tempo então coloca mensagem de erro
                if (TextHelper.isNull(session.getAttribute(CodedValues.MSG_SESSAO_INVALIDA))) {
                    session.setAttribute(CodedValues.MSG_SESSAO_INVALIDA, ApplicationResourcesHelper.getMessage("mensagem.erro.validacao.certificado.digital", null));
                }
                session.setAttribute(CodedValues.SESSAO_INVALIDA, "true");
            }

            if (certClient.validateToken(token, responsavel)) {
                // Se validou, direciona para o dashboard
                session.removeAttribute("valida_certificado_digital");
                return "forward:/v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true";
            }

            if (TextHelper.isNull(session.getAttribute(CodedValues.MSG_SESSAO_INVALIDA))) {
                session.setAttribute(CodedValues.MSG_SESSAO_INVALIDA, ApplicationResourcesHelper.getMessage("mensagem.erro.validacao.certificado.digital", null));
            }
            session.setAttribute(CodedValues.SESSAO_INVALIDA, "true");
            return "forward:/v3/expirarSistema?acao=iniciar";

        } catch (final BadPaddingException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            session.setAttribute(CodedValues.SESSAO_INVALIDA, "true");
            return "forward:/v3/expirarSistema?acao=iniciar";
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            session.setAttribute(CodedValues.SESSAO_INVALIDA, "true");
            return "forward:/v3/expirarSistema?acao=iniciar";
        }
    }

    private static HashMap<String, String> parseState(HttpServletRequest request, AcessoSistema responsavel) throws BadPaddingException, ParseException {
        final KeyPair kp = (KeyPair) request.getSession().getAttribute(CodedValues.CERT_PUBLIC_KEY);
        String jsonDecrypted = RSA.decrypt(new String(Base64.getDecoder().decode(request.getParameter(CodedValues.CERT_STATE).toString())), kp.getPrivate());

        JSONParser parser = new JSONParser();
        final JSONObject jsonObject = (JSONObject) parser.parse(jsonDecrypted);
      
        final HashMap<String, String> additionalDetails = new HashMap<>();
        additionalDetails.put(CodedValues.CERT_TOKEN, request.getParameter(CodedValues.CERT_TOKEN).toString());
        additionalDetails.put(CodedValues.CERT_USU_CODIGO, jsonObject.get(CodedValues.CERT_USU_CODIGO).toString());
        additionalDetails.put(CodedValues.CERT_DATA, jsonObject.get(CodedValues.CERT_DATA).toString());
        additionalDetails.put(CodedValues.CERT_IP_ACESSO, jsonObject.get(CodedValues.CERT_IP_ACESSO).toString());

        return additionalDetails;
    }
}
