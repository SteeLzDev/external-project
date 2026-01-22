package com.zetra.econsig.webclient.faces;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webclient.util.RestTemplateFactory;

/**
 * <p>Title: FacesWebServiceClient</p>
 * <p>Description: Cliente para o serviço do FacesWeb que consulta o cadastro do usuário servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FacesWebServiceClient {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(FacesWebServiceClient.class);

    public static void verificarCadastro(String usuCodigo, String serCpf, AcessoSistema responsavel) throws UsuarioControllerException {
        String urlService = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_SERVICO_FACES_WEB, responsavel);
        String apiKey = (String) ParamSist.getInstance().getParam(CodedValues.TPC_API_KEY_FACES_WEB, responsavel);

        if (TextHelper.isNull(urlService) || TextHelper.isNull(apiKey)) {
            // Erro parâmetros não configurados
            LOG.error("Parâmetros de sistema " + CodedValues.TPC_URL_SERVICO_FACES_WEB + " e " + CodedValues.TPC_API_KEY_FACES_WEB + " devem estar configurados para integração com o FacesWeb.");
            throw new UsuarioControllerException("mensagem.erroInternoSistema", responsavel);
        }

        RestTemplate restTemplate = RestTemplateFactory.getRestTemplate(responsavel);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("ApiKey", apiKey);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<?> response = restTemplate.exchange(urlService + "/api/verify?id=" + serCpf, HttpMethod.GET, httpEntity, String.class);
        if (response != null && response.getStatusCode() != null) {
            LOG.debug("FacesWeb: retorno " + response.getStatusCode().value());
            if (response.getStatusCode().equals(HttpStatus.ACCEPTED)) {
                // Pode autenticar, pois o cadastro foi aprovado. Muda o status do usuário para Ativo
                UsuarioDelegate usuDelegate = new UsuarioDelegate();
                usuDelegate.aprovarCadastroUsuarioSer(usuCodigo, responsavel);
                return;
            } else if (response.getStatusCode().equals(HttpStatus.NOT_ACCEPTABLE)) {
                // Não pode autenticar, pois cadastro ainda está em análise
                throw new UsuarioControllerException("mensagem.erro.autenticacao.cadastro.aguardando.aprovacao", responsavel);
            } else if (response.getStatusCode().equals(HttpStatus.OK)) {
                // Em caso de 200, não pode autenticar pois não fez o cadastro ou foi reprovado
                throw new UsuarioControllerException("mensagem.erro.autenticacao.cadastro.nao.encontrado", responsavel);
            } else {
                LOG.error("Não obteve um dos resultados esperados da chamada ao FacesWeb.");
                throw new UsuarioControllerException("mensagem.erroInternoSistema", responsavel);
            }
        } else {
            // Não obteve resultado, então não deixa autenticar e retorna um erro genérico
            LOG.error("Não obteve resultado da chamada ao FacesWeb.");
            throw new UsuarioControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }
}
