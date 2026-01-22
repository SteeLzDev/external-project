package com.zetra.econsig.helper.senhaexterna.impl;

import static com.zetra.econsig.helper.senhaexterna.SenhaExterna.KEY_SENHA;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.helper.rede.HttpHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.ParamSenhaExternaEnum;


/**
 * <p>Title: ValidarSenhaCAAPSML</p>
 * <p>Description: Validação da senha de servidor para sistema CAAPSML</p>
 * <p>Copyright: Copyright (c) 2002-2024</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ValidarSenhaCAAPSML implements ValidarSenhaExterna {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidarSenhaCAAPSML.class);

    private String url                      = null; // URL
    private String httpClientKeystorePath   = null; // KeyStore para autenticação via HTTPS
    private String httpClientKeystorePass   = null; // Senha do KeyStore para autenticação via HTTPS
    private String httpClientParamUsuario   = null; // Nome do parâmetro com o usuário/matricula.
    private String httpClientParamSenha     = null; // Nome do parâmetro com a senha.
    private String httpClientParamFixo      = null; // Junção de chave=valor de parâmetros fixos a serem enviados, separados por &

    public ValidarSenhaCAAPSML() {
        try {
            url                      = ParamSenhaExternaEnum.HTTPCLIENT_URL.getValor();
            httpClientKeystorePass   = ParamSenhaExternaEnum.HTTPCLIENT_KEYSTORE_PASS.getValor();
            httpClientKeystorePath   = ParamSenhaExternaEnum.HTTPCLIENT_KEYSTORE_PATH.getValor();
            httpClientParamUsuario   = ParamSenhaExternaEnum.HTTPCLIENT_PARAM_USUARIO.getValor("login");
            httpClientParamSenha     = ParamSenhaExternaEnum.HTTPCLIENT_PARAM_SENHA.getValor("password");
            httpClientParamFixo      = ParamSenhaExternaEnum.HTTPCLIENT_PARAM_FIXO.getValor();
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
     * @return Um {@link CustomTransferObject} com a senha se ela estiver correta ou com a mensagem de erro.
     *         Se houver alguma outra informação fornecida pelo repositório ela também será colocada neste objeto.
     */
    @Override
    public CustomTransferObject validarSenha(String[] parametros, Properties messages) {
        CustomTransferObject result = new CustomTransferObject();
        HttpPost request = null;

        try {
            // Cria o cliente HTTP e o método POST para validação da senha
            final HttpClient client = HttpHelper.getHttpClient(httpClientKeystorePath, httpClientKeystorePass);

            // Define os parâmetros da requisição HTTP
            final List<NameValuePair> data = new ArrayList<>();
            data.add(new BasicNameValuePair(httpClientParamUsuario, parametros[1]));
            data.add(new BasicNameValuePair(httpClientParamSenha, parametros[2]));

            if (!TextHelper.isNull(httpClientParamFixo)) {
                final String[] valoresParamFixo = httpClientParamFixo.split("&");
                for (final String chaveValorParamFixo : valoresParamFixo) {
                    if (!TextHelper.isNull(chaveValorParamFixo)) {
                        final String[] paramFixo = chaveValorParamFixo.split("=");
                        if (paramFixo.length == 2) {
                            data.add(new BasicNameValuePair(paramFixo[0], paramFixo[1]));
                        }
                    }
                }
            }

            // define parâmetros para método POST
            request = new HttpPost(url);
            request.setEntity(new UrlEncodedFormEntity(data));

            final HttpResponse response = client.execute(request);
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                result.setAttribute(KEY_SENHA, parametros[2]);
            } else {
                result.setAttribute(KEY_SENHA, null);
            }

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            if (request != null) {
                request.releaseConnection();
            }
        }

        return result;
    }
}
