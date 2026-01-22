package com.zetra.econsig.helper.senhaexterna.impl;

import static com.zetra.econsig.helper.senhaexterna.SenhaExterna.KEY_SENHA;

import java.util.Collections;
import java.util.HashMap;
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
 * <p>Title: ValidarSenhaMaceio</p>
 * <p>Description: Validação da senha de servidor para um serviço REST
 *    (get ou post) que retorno uma mensagem "combinada" previamente</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ValidarSenhaVitoria implements ValidarSenhaExterna {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidarSenhaVitoria.class);

    private String url                              = null; // URL
    private String httpClientResultOk               = null; // Valor que indica que a senha está ok. (Regex)
    private String httpClientParamUsuario           = null; // Character set de codificação do resultado.
    private String httpClientParamSenha             = null; // KeyStore para autenticação via HTTPS
    private String httpClientParamEstabelecimento   = null; // Nome do parâmetro com o estabelecimento do servidor.
    private String httpClientParamOrgao             = null; // Nome do parâmetro com o órgão do servidor.
    private String httpClientParamCpf               = null; // Nome do parâmetro com o cpf do servidor.
    public ValidarSenhaVitoria() {
        try {
            url                            = ParamSenhaExternaEnum.HTTPCLIENT_URL.getValor();
            httpClientResultOk             = ParamSenhaExternaEnum.HTTPCLIENT_RESULT_OK.getValor();
            httpClientParamUsuario         = ParamSenhaExternaEnum.HTTPCLIENT_PARAM_USUARIO.getValor();
            httpClientParamSenha           = ParamSenhaExternaEnum.HTTPCLIENT_PARAM_SENHA.getValor();
            httpClientParamEstabelecimento = ParamSenhaExternaEnum.HTTPCLIENT_PARAM_ESTABELECIMENTO.getValor();
            httpClientParamOrgao           = ParamSenhaExternaEnum.HTTPCLIENT_PARAM_ORGAO.getValor();
            httpClientParamCpf             = ParamSenhaExternaEnum.HTTPCLIENT_PARAM_CPF.getValor();
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
        String urlLocal = url;
        String rseMatricula     = parametros[1];
        String serSenha         = parametros[2];
        String estIdentificador = parametros[0];
        String orgIdentificador = parametros[3];
        String cpf              = parametros[5];

        try {

            HashMap<String, String> camposJson = new HashMap<>();
            camposJson.put(httpClientParamUsuario, rseMatricula);
            camposJson.put(httpClientParamSenha, serSenha);

            if (!TextHelper.isNull(httpClientParamEstabelecimento)) {
                camposJson.put(httpClientParamEstabelecimento, estIdentificador);
            }

            if (!TextHelper.isNull(httpClientParamOrgao)) {
                camposJson.put(httpClientParamOrgao, orgIdentificador);
            }

            if (!TextHelper.isNull(httpClientParamCpf)) {
                camposJson.put(httpClientParamCpf, cpf);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            JSONObject jsonObject = new JSONObject(camposJson);
            HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);

            RestTemplate restTemplateSimple = RestTemplateFactory.getRestTemplate(AcessoSistema.getAcessoUsuarioSistema());

            ResponseEntity<String> response = restTemplateSimple.postForEntity(urlLocal, httpEntity, String.class);

            boolean valoresConformeEsperado = true;
            LOG.debug("URI:"+urlLocal+"\tresultado recebido:|" + response.getBody() + "|");

            if (response.getStatusCode().equals(HttpStatus.OK)) {
                camposJson.clear();
                String[] httpResultJsonArray = httpClientResultOk.split(";");

                for (int i = 0; i<httpResultJsonArray.length ; i = i+2) {
                    camposJson.put(httpResultJsonArray[i], httpResultJsonArray[i+1]);
                }

                int qntdParamJson = httpResultJsonArray.length / 2;
                JSONParser parser = new JSONParser();
                JSONObject jsonResultado = (JSONObject) parser.parse(response.getBody());
                JSONObject jsonResultOk = new JSONObject(camposJson);

                for (int i = 0; i < qntdParamJson; i = i+2) {
                    if(!jsonResultado.get(httpResultJsonArray[i]).toString().equals(jsonResultOk.get(httpResultJsonArray[i]).toString())) {
                        valoresConformeEsperado = false;
                    }
                }
            } else {
                valoresConformeEsperado = false;
            }

            if(valoresConformeEsperado) {
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
