package com.zetra.econsig.webclient.salaryweb;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webclient.util.RestTemplateFactory;

/**
 * <p>Title: SalaryWebClient</p>
 * <p>Description: Cliente de requisições ao SalaryWeb.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: fagner.luiz $
 * $Revision: 28440 $
 * $Date: 2019-12-19 13:11:51 -0300 (qui, 19 dez 2019) $
 */
public class SalaryWebClient {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SalaryWebClient.class);

    public static boolean validaAutenticacao(String idUsuario, String serSenha, AcessoSistema responsavel) throws UsuarioControllerException {
        String urlService = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_SERVICO_SALARY_WEB, responsavel);

        if (TextHelper.isNull(urlService)) {
            // Erro parâmetros não configurados
            LOG.error("Parâmetro de sistema " + CodedValues.TPC_URL_SERVICO_SALARY_WEB + " deve estar configurado para integração com o SalaryWeb.");
            throw new UsuarioControllerException("mensagem.erroInternoSistema", responsavel);
        }

        String token = TextHelper.encode64(idUsuario + ":" + serSenha);

        RestTemplate restTemplate = RestTemplateFactory.getRestTemplate(responsavel);

        ResponseEntity<?> response = restTemplate.exchange(urlService + "/api/auth?token=" + token, HttpMethod.POST, null, String.class);
        if (response != null && response.getStatusCode() != null) {
            LOG.debug("SalaryWeb: retorno " + response.getStatusCode().value());
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                return true;
            }
        } else {
            LOG.error("Não obteve resultado da chamada ao SalaryWeb.");
            return false;
        }

        return false;
    }
}
