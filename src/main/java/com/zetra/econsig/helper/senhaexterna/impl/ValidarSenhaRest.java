package com.zetra.econsig.helper.senhaexterna.impl;

import static com.zetra.econsig.helper.senhaexterna.SenhaExterna.KEY_ERRO;
import static com.zetra.econsig.helper.senhaexterna.SenhaExterna.KEY_SENHA;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.helper.rede.HttpHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.ParamSenhaExternaEnum;

/**
 * <p>Title: ValidarSenhaRest</p>
 * <p>Description: Validação da senha de servidor para um serviço REST, i.e. um serviço HTPP
 *    (get ou post) que retorno uma mensagem "combinada" previamente</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ValidarSenhaRest implements ValidarSenhaExterna {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidarSenhaRest.class);

    private String url                                = null; // URL
    private String httpClientMetodo                   = null; // Método de conexão GET ou POST.
    private String httpClientParamUsuario             = null; // Nome do parâmetro com o usuário/matricula.
    private String httpClientParamSenha               = null; // Nome do parâmetro com a senha.
    private String httpClientParamEstabelecimento     = null; // Nome do parâmetro com o estabelecimento do servidor.
    private String httpClientParamOrgao               = null; // Nome do parâmetro com o órgão do servidor.
    private String httpClientResultOk                 = null; // Valor que indica que a senha está ok. (Regex)
    private String httpClientResultEncoding           = null; // Character set de codificação do resultado.
    private String httpClientKeystorePath             = null; // KeyStore para autenticação via HTTPS
    private String httpClientKeystorePass             = null; // Senha do KeyStore para autenticação via HTTPS
    private String httpClientParamUsuarioTipo         = null; // Para se fazer customizações no envio da matrícula.
    // Ex: numerico=Retira caracteres não numéricos da matrícula

    private Pattern okPattern;
    private Matcher matcher;

    public ValidarSenhaRest() {
        try {
            url                              = ParamSenhaExternaEnum.HTTPCLIENT_URL.getValor();
            httpClientMetodo                 = ParamSenhaExternaEnum.HTTPCLIENT_METODO.getValor();
            httpClientParamUsuario           = ParamSenhaExternaEnum.HTTPCLIENT_PARAM_USUARIO.getValor();
            httpClientParamSenha             = ParamSenhaExternaEnum.HTTPCLIENT_PARAM_SENHA.getValor();
            httpClientParamEstabelecimento   = ParamSenhaExternaEnum.HTTPCLIENT_PARAM_ESTABELECIMENTO.getValor();
            httpClientParamOrgao             = ParamSenhaExternaEnum.HTTPCLIENT_PARAM_ORGAO.getValor();
            httpClientResultOk               = ParamSenhaExternaEnum.HTTPCLIENT_RESULT_OK.getValor();
            httpClientResultEncoding         = ParamSenhaExternaEnum.HTTPCLIENT_RESULT_ENCODING.getValor();
            httpClientKeystorePass           = ParamSenhaExternaEnum.HTTPCLIENT_KEYSTORE_PASS.getValor();
            httpClientKeystorePath           = ParamSenhaExternaEnum.HTTPCLIENT_KEYSTORE_PATH.getValor();
            httpClientParamUsuarioTipo       = ParamSenhaExternaEnum.HTTPCLIENT_PARAM_USUARIO_TIPO.getValor();

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
     * @param messages Mapeamento de mensagens de sucesso/erro específico do sistema
     * @return Um {@link CustomTransferObject} com a senha se ela estiver correta ou com a mensagem de erro.
     *         Se houver alguma outra informação fornecida pelo repositório ela também será colocada neste objeto.
     */
    @Override
    public CustomTransferObject validarSenha(String[] parametros, Properties messages) {
        CustomTransferObject result = new CustomTransferObject();

        String estabelecimento = parametros[0];
        String matricula = parametros[1];
        String senha = parametros[2];
        String orgao = parametros[3];

        // Apaga os caracteres não numéricos da matrícula
        if (httpClientParamUsuarioTipo != null && httpClientParamUsuarioTipo.equalsIgnoreCase("numerico")) {
            matricula = matricula.replaceAll("\\D", "");
        }

        try {
            HttpUriRequest metodo = null;
            InputStream inputStream = null;
            HttpResponse response = null;
            String resultado = null;

            try {
                // Cria o cliente HTTP e o método POST para validação da senha
                HttpClient client = HttpHelper.getHttpClient(httpClientKeystorePath, httpClientKeystorePass);

                // Define os parâmetros
                List<NameValuePair> data = new ArrayList<>();
                if (!TextHelper.isNull(httpClientParamUsuario)) {
                    data.add(new BasicNameValuePair(httpClientParamUsuario, matricula));
                }
                if (!TextHelper.isNull(httpClientParamSenha)) {
                    data.add(new BasicNameValuePair(httpClientParamSenha, senha));
                }
                if (!TextHelper.isNull(httpClientParamEstabelecimento)) {
                    data.add(new BasicNameValuePair(httpClientParamEstabelecimento, estabelecimento));
                }
                if (!TextHelper.isNull(httpClientParamOrgao)) {
                    data.add(new BasicNameValuePair(httpClientParamOrgao, orgao));
                }

                String paramString = URLEncodedUtils.format(data, "UTF-8");

                if ("GET".equalsIgnoreCase(httpClientMetodo)) {
                    // define parâmetros para método GET
                    metodo = new HttpGet(url + paramString);
                } else {
                    metodo = new HttpPost(url);
                    // define parâmetros para método POST
                    ((HttpPost) metodo).setEntity(new UrlEncodedFormEntity(data));
                }

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
