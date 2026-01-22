package com.zetra.econsig.web.controller.certificado;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.exception.CERTException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.webclient.cert.CERTClient;
import com.zetra.econsig.webclient.cert.CERTErrorCodeEnum;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>Title: SolicitarCertificadoWebController</p>
 * <p>Description: Controlador Web para solicitar certificado.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $ $ : 2018-05-17 14:28:30 -0300
 * (Ter, 17 mai 2018) $
 */

@Controller
@RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, value = { "/v3/solicitarCertificado" })
public class SolicitarCertificadoWebController extends AbstractWebController {
    
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SolicitarCertificadoWebController.class);

    @Autowired
    private ConsignanteController consignanteController;
    
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciarValidacao(HttpServletRequest request, HttpServletResponse response) throws CERTException, IOException, ConsignanteControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String urlBase = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_BASE_SERVICO_CERT, responsavel);
        String urlSistema = (String) ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel);

        if (TextHelper.isNull(urlBase) || TextHelper.isNull(urlSistema)) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.cert", responsavel));
            final CERTException exception = new CERTException("mensagem.usuarioSenhaInvalidos", responsavel);
            exception.setCertError(CERTErrorCodeEnum.GENERIC_ERROR);
            throw exception;
        }

        if (!TextHelper.isNull(urlSistema)) {
            urlSistema = urlSistema.endsWith("/") ? urlSistema : urlSistema + "/";
        }
        final String urlCallBack = urlSistema + "v3/validarCertificado?acao=iniciar";

        ConsignanteTransferObject cse = consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
        String cseCodigo = !TextHelper.isNull(cse.getAttribute(Columns.CSE_IDENTIFICADOR_INTERNO)) ? cse.getAttribute(Columns.CSE_IDENTIFICADOR_INTERNO).toString() : cse.getAttribute(Columns.CSE_CODIGO).toString();

        String state = CERTClient.prepareState(request, responsavel);

        StringBuilder urlCertRequest = new StringBuilder();
        urlCertRequest.append(urlBase).append("/cert/request?");
        urlCertRequest.append("returnUrl=").append(URLEncoder.encode(urlCallBack, StandardCharsets.UTF_8));
        urlCertRequest.append("&state=").append(state);
        urlCertRequest.append("&systemCode=").append(Base64.getEncoder().encodeToString(cseCodigo.getBytes()));

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(urlCertRequest.toString(), request)));
        return "jsp/redirecionador/redirecionar";
    }
}
