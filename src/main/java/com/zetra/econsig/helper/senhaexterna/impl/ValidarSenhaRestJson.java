package com.zetra.econsig.helper.senhaexterna.impl;

import static com.zetra.econsig.helper.senhaexterna.SenhaExterna.KEY_SENHA;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.ParamSenhaExternaEnum;
import com.zetra.econsig.webclient.util.RestTemplateFactory;


/**
 * <p>Title: ValidarSenhaRestJson</p>
 * <p>Description: Validação da senha de servidor para um serviço REST
 *    (get ou post) que retorno uma mensagem "combinada" previamente</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ValidarSenhaRestJson implements ValidarSenhaExterna {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidarSenhaRestJson.class);

    private String url                    = null; // URL
    private String resultCampo            = null; // Nome do campo que será procurado o valor esperado para o resultado
    private String resultValor            = null; // Valor que indica que a senha está OK
    private String paramUsuario           = null; // Character set de codificação do resultado.
    private String paramSenha             = null; // KeyStore para autenticação via HTTPS
    private String paramEstabelecimento   = null; // Nome do parâmetro com o estabelecimento do servidor.
    private String paramOrgao             = null; // Nome do parâmetro com o órgão do servidor.
    private String paramCpf               = null; // Nome do parâmetro com o cpf do servidor.

    public ValidarSenhaRestJson() {
        try {
            url                  = ParamSenhaExternaEnum.REST_URL.getValor();
            resultCampo          = ParamSenhaExternaEnum.REST_RESULT_CAMPO.getValor("message");   // Obrigatório, default: message
            resultValor          = ParamSenhaExternaEnum.REST_RESULT_VALOR.getValor("OK");        // Obrigatório, default: OK
            paramUsuario         = ParamSenhaExternaEnum.REST_PARAM_USUARIO.getValor("matricula");// Obrigatório, default: matricula
            paramSenha           = ParamSenhaExternaEnum.REST_PARAM_SENHA.getValor("senha");      // Obrigatório, default: senha
            paramEstabelecimento = ParamSenhaExternaEnum.REST_PARAM_ESTABELECIMENTO.getValor();
            paramOrgao           = ParamSenhaExternaEnum.REST_PARAM_ORGAO.getValor();
            paramCpf             = ParamSenhaExternaEnum.REST_PARAM_CPF.getValor();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Valida a senha em um repositório externo.
     * @param parametros Parametros da senha do usuário para validação:
     *                   0. estIdentificador: código do estabelecimento doo servidor
     *                   1. rseMatricula....: matrícula do servidor
     *                   2. serSenha........: senha do servidor
     *                   3. orgIdentificador: código do órgão do servidor
     *                   4. ip..............: endereço IP de onde o servidor está acessando
     *                   5. cpf.............: CPF do servidor
     *                   6. rseMatriculaInst: Matrícula alternativa (Não é utilizado pelo sistema, usada em Maceio).
     *                   <MATRICULA>, <MATRICULAINST> e <SENHA> são substituidos na URL
     * @param messages Mapeamento de mensagens de sucesso/erro específico do sistema
     * @return Um {@link CustomTransferObject} com a senha se ela estiver correta ou com a mensagem de erro.
     *         Se houver alguma outra informação fornecida pelo repositório ela também será colocada neste objeto.
     */
    @Override
    public CustomTransferObject validarSenha(String[] parametros, Properties messages) {
        CustomTransferObject result = new CustomTransferObject();
        String rseMatricula     = parametros[1];
        String serSenha         = parametros[2];
        String estIdentificador = parametros[0];
        String orgIdentificador = parametros[3];
        String cpf              = parametros[5];

        try {
            Map<String, String> camposJson = new HashMap<>();
            camposJson.put(paramUsuario, rseMatricula);
            camposJson.put(paramSenha, serSenha);

            if (!TextHelper.isNull(paramEstabelecimento)) {
                camposJson.put(paramEstabelecimento, estIdentificador);
            }

            if (!TextHelper.isNull(paramOrgao)) {
                camposJson.put(paramOrgao, orgIdentificador);
            }

            if (!TextHelper.isNull(paramCpf)) {
                camposJson.put(paramCpf, cpf);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            JSONObject jsonObject = new JSONObject(camposJson);
            HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);

            RestTemplate restTemplateSimple = RestTemplateFactory.getRestTemplate(AcessoSistema.getAcessoUsuarioSistema());
            ResponseEntity<String> response = restTemplateSimple.postForEntity(url, httpEntity, String.class);

            boolean resultadoConformeEsperado = false;
            LOG.debug("URI:" + url + "\tresultado recebido:|" + response.getBody() + "|");

            if (response.getStatusCode().equals(HttpStatus.OK)) {
                JSONParser parser = new JSONParser();
                JSONObject jsonResultado = (JSONObject) parser.parse(response.getBody());

                String jsonCampoResultado = (String) jsonResultado.get(resultCampo);
                resultadoConformeEsperado = jsonCampoResultado.equals(resultValor);
            }

            if (resultadoConformeEsperado) {
                result.setAttribute(KEY_SENHA, serSenha);
            } else {
                result.setAttribute(KEY_SENHA, null);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            result.setAttribute(KEY_SENHA, null);
        }
        return result;
    }
}
