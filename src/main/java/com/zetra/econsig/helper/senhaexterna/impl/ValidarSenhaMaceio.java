package com.zetra.econsig.helper.senhaexterna.impl;

import static com.zetra.econsig.helper.senhaexterna.SenhaExterna.KEY_ERRO;
import static com.zetra.econsig.helper.senhaexterna.SenhaExterna.KEY_SENHA;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.helper.rede.HttpHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.ParamSenhaExternaEnum;


/**
 * <p>Title: ValidarSenhaMaceio</p>
 * <p>Description: Validação da senha de servidor para um serviço REST, i.e. um serviço HTPP
 *    (get ou post) que retorno uma mensagem "combinada" previamente</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ValidarSenhaMaceio implements ValidarSenhaExterna {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidarSenhaMaceio.class);

    private String url                      = null; // URL
    private String httpClientResultOk       = null; // Valor que indica que a senha está ok. (Regex)
    private String httpClientResultEncoding = null; // Character set de codificação do resultado.
    private String httpClientKeystorePath   = null; // KeyStore para autenticação via HTTPS
    private String httpClientKeystorePass   = null; // Senha do KeyStore para autenticação via HTTPS

    private Pattern okPattern;
    private Matcher matcher;

    public ValidarSenhaMaceio() {
        try {
            url                      = ParamSenhaExternaEnum.HTTPCLIENT_URL.getValor();
            httpClientResultOk       = ParamSenhaExternaEnum.HTTPCLIENT_RESULT_OK.getValor();
            httpClientResultEncoding = ParamSenhaExternaEnum.HTTPCLIENT_RESULT_ENCODING.getValor();
            httpClientKeystorePass   = ParamSenhaExternaEnum.HTTPCLIENT_KEYSTORE_PASS.getValor();
            httpClientKeystorePath   = ParamSenhaExternaEnum.HTTPCLIENT_KEYSTORE_PATH.getValor();

            if (httpClientResultOk != null){
                okPattern = Pattern.compile(httpClientResultOk, Pattern.DOTALL | Pattern.MULTILINE);
            } else {
                LOG.debug("httpclient.resultok não encontrado. Utilizando a string \"OK\"");
                okPattern = Pattern.compile("OK", Pattern.DOTALL | Pattern.MULTILINE);
            }
            matcher = okPattern.matcher("");

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
        //String estIdentificador = parametros[0];
        String rseMatricula     = parametros[1];
        String serSenha         = parametros[2];
        //String orgIdentificador = parametros[3];
        //String ipAcesso         = parametros[4];
        //String cpf              = parametros[5];
        String rseMatriculaInst = parametros[6];

        try {
            HttpUriRequest metodo = null;
            InputStream inputStream = null;
            HttpResponse response = null;
            String resultado = null;

            try {
                // Cria o cliente HTTP e o método POST para validação da senha
                HttpClient client = HttpHelper.getHttpClient(httpClientKeystorePath, httpClientKeystorePass);

                // Constroi a URL para acesso REST
                urlLocal = urlLocal.replace("<MATRICULA>", rseMatricula);
                urlLocal = urlLocal.replace("<SENHA>", serSenha);
                urlLocal = urlLocal.replace("<MATRICULAINST>", rseMatriculaInst);

                // define parâmetros para método GET
                metodo = new HttpGet(urlLocal);
                response = client.execute(metodo);

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    // Input Stream para receber o resultado da requisição
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        inputStream = entity.getContent();
                        // Grava o resultado em um buffer
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        int c = -1;
                        while ((c = inputStream.read()) != -1) {
                            out.write(c);
                        }
                        resultado = out.toString().trim();
                    }
                }

                LOG.debug("URI:"+metodo.getURI()+"\tresultado recebido do HTTPClient:|" + URLDecoder.decode(resultado, httpClientResultEncoding) + "|");
                if (resultado != null && match(resultado)) {
                    result.setAttribute(KEY_SENHA, parametros[2]);
                } else {
                    result.setAttribute(KEY_SENHA, null);
                    result.setAttribute(KEY_ERRO, getErrorMessage(URLDecoder.decode(resultado, httpClientResultEncoding), messages));
                }

            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            } finally {
                try {
                    if (metodo != null) {
                        metodo.abort();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            result.setAttribute(KEY_SENHA, null);
        }
        return result;
    }

    public String getErrorMessage(Object key, Properties messages) {

        // Se a chave não é válida, retorna.
        if (messages == null || TextHelper.isNull(key)) {
            return "";
        }

        // Retorna a primeira que encontrar
        for (Object element : messages.keySet()) {
            String k = (String) element;
            if (Pattern.compile(k.toString(), Pattern.DOTALL | Pattern.MULTILINE).matcher(key.toString()).find()){
                return messages.getProperty(k.toString());
            }
        }
        return "";
    }

    private boolean match(String res) {
        matcher.reset(res);
        return (matcher.find());
    }
}
