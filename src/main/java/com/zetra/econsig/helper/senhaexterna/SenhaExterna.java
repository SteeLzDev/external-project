package com.zetra.econsig.helper.senhaexterna;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.rede.HttpHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.senhaexterna.impl.ValidarSenhaExterna;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.MetodoSenhaExternaEnum;
import com.zetra.econsig.values.ParamSenhaExternaEnum;

/**
 * <p>Title: SenhaExterna</p>
 * <p>Description: Helper class para busca de senhas de usuários em outras
 * fontes de dados. A tabela tb_param_senha_externa deve conter as configurações
 * de conexão com esta fonte.</p>
 * <p>Copyright: Copyright (c) 2003-2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
public final class SenhaExterna {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SenhaExterna.class);

    public static final String KEY_CPF           = "cpf";
    public static final String KEY_ERRO          = "erro";
    public static final String KEY_MATRICULA     = "matricula";
    public static final String KEY_RG            = "rg";
    public static final String KEY_SENHA         = "senha";
    public static final String KEY_SENHA_INICIAL = "senha_inicial";

    private String metodo = ""; /* Define o metodo de busca da senha externa:
               NENHUM: não utiliza nenhum método externo (bypass do metodo buscarSenha)
                QUERY: busca em banco de dados
              NRH7UI9: busca em mainframe usando a classe Nrh7ui9
               NRH7UE: busca em mainframe usando a classe Nrh7ue01 ou a classe Nrh7ue09
                   AD: busca em Active Directory usando a classe AD
               SOAPDI: busca usando o cliente SOAP DynamicInvoker
           HTTPCLIENT: busca usando cliente HTTP
                 JAVA: busca usando uma classe java que implementa a interface com.zetra.senhaexterna.ValidarSenhaExterna
               OAUTH2: chama uma URL para autenticação, recebe o token e valida o token
     */

    private String driver   = null; // Driver para acesso a base de dados
    private String url      = null; // Url de conexão com a base de dados
    private String username = null; // Usuário de acesso ao banco
    private String password = null; // Senha de acesso ao banco
    private String query    = null; // Query de consulta da senha
    private String update   = null; // Query de atualização da senha
    private String timeout  = null; // JDBC Login timeout

    private String dominioAD  = null; // Domínio em que está o servidor do Active Directory
    private String servidorAD = null; // Nome do servidor onde está o Active Directory

    private boolean soapDebug          = false; // Define se mostra mensagens de debug
    private String soapServiceUrl      = null; // Define a url do serviço SOAP para validação de senha externa
    private String soapAction          = null; // Cabeçalho SOAPAction enviado na requisição
    private String soapContentType     = null; // Cabeçalho Content-Type enviado na requisição
    private String soapRequestXml      = null; // Modelo XML para a requisição, com as variáveis a serem substituídas pelos parâmetros
    private String soapResponseCharset = null; // Charset usado no recebimento da resposta
    private String soapResponseField   = null; // Campo de resposta que será comparado com o valor esperado para determinar se a requisição foi de sucesso
    private String soapExpectedValue   = null; // Valor esperado para o campo de resposta em caso de sucesso

    private String httpClientMetodo                 = null; // Método de conexão GET ou POST.
    private String httpClientParamUsuario           = null; // Nome do parâmetro com o usuário/matricula.
    private String httpClientParamSenha             = null; // Nome do parâmetro com a senha.
    private String httpClientParamEstabelecimento   = null; // Nome do parâmetro com o estabelecimento do servidor.
    private String httpClientParamOrgao             = null; // Nome do parâmetro com o órgão do servidor.
    private String httpClientParamCpf               = null; // Nome do parâmetro com o cpf do servidor.
    private String httpClientParamIpAcesso          = null; // Nome do parâmetro com o IP de acesso do servidor.
    private String httpClientParamFixo              = null; // Junção de chave=valor de parâmetros fixos a serem enviados, separados por &
    private String httpClientResultOk               = null; // Valor que indica que a senha está ok.
    private String httpClientResultEncoding         = null; // Character set de codificação do resultado.
    private String httpClientRequestEncoding        = null; // Character set de codificação request.
    private String httpClientKeystorePath           = null; // KeyStore para autenticação via HTTPS
    private String httpClientKeystorePass           = null; // Senha do KeyStore para autenticação via HTTPS
    private boolean httpClientParamCpfNumerico      = false; // Indica se o cpf do servidor deve ser enviado apenas com os números.

    private String javaClassName = null; // Nome da classe java para validação

    private Properties messages = null; // Lista de mensagens

    private ValidarSenhaExterna validarSenhaEspecifico = null; // Classe java de validação de senha

    private SenhaExterna() {
        configurar();
    }

    private static class SingletonHelper {
        private static final SenhaExterna instance = new SenhaExterna();
    }

    public static SenhaExterna getInstance() {
        return SingletonHelper.instance;
    }

    private void configurar() {
        messages = new Properties();
        try {
            messages.load(SenhaExterna.class.getClassLoader().getResourceAsStream("SenhaExternaErrorMessages.properties"));

            metodo = ParamSenhaExternaEnum.METODO.getValor();

            driver   = ParamSenhaExternaEnum.DB_DRIVER.getValor();
            url      = ParamSenhaExternaEnum.DB_URL.getValor();
            username = ParamSenhaExternaEnum.DB_USERNAME.getValor();
            password = ParamSenhaExternaEnum.DB_PASSWORD.getValor();
            query    = ParamSenhaExternaEnum.DB_QUERY.getValor();
            update   = ParamSenhaExternaEnum.DB_UPDATE.getValor();
            timeout  = ParamSenhaExternaEnum.DB_TIMEOUT.getValor();

            dominioAD = ParamSenhaExternaEnum.AD_DOMINIO.getValor();
            servidorAD = ParamSenhaExternaEnum.AD_SERVIDOR.getValor();

            soapDebug           = ((ParamSenhaExternaEnum.SOAP_DEBUG.getValor() != null) && "true".equalsIgnoreCase(ParamSenhaExternaEnum.SOAP_DEBUG.getValor()));
            soapServiceUrl      = ParamSenhaExternaEnum.SOAP_SERVICE_URL.getValor();
            soapAction          = ParamSenhaExternaEnum.SOAP_ACTION.getValor();
            soapContentType     = ParamSenhaExternaEnum.SOAP_CONTENT_TYPE.getValor("text/xml; charset=utf-8");
            soapRequestXml      = ParamSenhaExternaEnum.SOAP_REQUEST_XML.getValor();
            soapResponseCharset = ParamSenhaExternaEnum.SOAP_RESPONSE_CHARSET.getValor("utf-8");
            soapResponseField   = ParamSenhaExternaEnum.SOAP_RESPONSE_FIELD.getValor();
            soapExpectedValue   = ParamSenhaExternaEnum.SOAP_RESPONSE_VALUE.getValor("true");

            httpClientMetodo                 = ParamSenhaExternaEnum.HTTPCLIENT_METODO.getValor();
            httpClientParamUsuario           = ParamSenhaExternaEnum.HTTPCLIENT_PARAM_USUARIO.getValor();
            httpClientParamSenha             = ParamSenhaExternaEnum.HTTPCLIENT_PARAM_SENHA.getValor();
            httpClientParamEstabelecimento   = ParamSenhaExternaEnum.HTTPCLIENT_PARAM_ESTABELECIMENTO.getValor();
            httpClientParamOrgao             = ParamSenhaExternaEnum.HTTPCLIENT_PARAM_ORGAO.getValor();
            httpClientParamCpf               = ParamSenhaExternaEnum.HTTPCLIENT_PARAM_CPF.getValor();
            httpClientParamCpfNumerico       = ((ParamSenhaExternaEnum.HTTPCLIENT_PARAM_CPF_NUMERICO.getValor() != null) && "true".equalsIgnoreCase(ParamSenhaExternaEnum.HTTPCLIENT_PARAM_CPF_NUMERICO.getValor()));
            httpClientParamIpAcesso          = ParamSenhaExternaEnum.HTTPCLIENT_PARAM_IP.getValor();
            httpClientParamFixo              = ParamSenhaExternaEnum.HTTPCLIENT_PARAM_FIXO.getValor();
            httpClientResultOk               = ParamSenhaExternaEnum.HTTPCLIENT_RESULT_OK.getValor();
            httpClientResultEncoding         = ParamSenhaExternaEnum.HTTPCLIENT_RESULT_ENCODING.getValor();
            httpClientRequestEncoding        = ParamSenhaExternaEnum.HTTPCLIENT_REQUEST_ENCODING.getValor();
            httpClientKeystorePass           = ParamSenhaExternaEnum.HTTPCLIENT_KEYSTORE_PASS.getValor();
            httpClientKeystorePath           = ParamSenhaExternaEnum.HTTPCLIENT_KEYSTORE_PATH.getValor();

            javaClassName = ParamSenhaExternaEnum.JAVA_CLASS_NAME.getValor();

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }

        // Se o método de autenticação externa for uma classe java, cria uma instância
        // da mesma para que esta seja reutilizada por todas as validações de senha
        if ((metodo != null) && "JAVA".equalsIgnoreCase(metodo) && (validarSenhaEspecifico == null)) {
            try {
                validarSenhaEspecifico = (ValidarSenhaExterna) Class.forName(javaClassName).getDeclaredConstructor().newInstance();
            } catch (final Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    public Connection conectar() throws SQLException {
        try {
            Class.forName(driver);

            // Sets the maximum time in seconds that a driver will wait while attempting to connect to a database.
            final int connectionTimeout = (!TextHelper.isNull(timeout) ? Integer.parseInt(timeout) : 15);
            DriverManager.setLoginTimeout(connectionTimeout);

            return DriverManager.getConnection(url, username, password);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage());
            throw new SQLException(ApplicationResourcesHelper.getMessage("mensagem.erro.unable.connect.database", (AcessoSistema) null, ex.getMessage()));
        }
    }

    public String buscarSenha(String login) {
        try {
            final Connection conn = conectar();
            final PreparedStatement preStat = conn.prepareStatement(query);
            preStat.setString(1, login);

            final ResultSet rs = preStat.executeQuery();
            String result = null;
            if (rs.next()) {
                result = rs.getString(1);
            }
            rs.close();
            preStat.close();
            conn.close();

            return result;
        } catch (final SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
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
    public CustomTransferObject buscarSenha(String[] parametros) {
        CustomTransferObject result = new CustomTransferObject();
        if (metodo == null) {
            metodo = "";
        }

        LOG.debug("SenhaExterna - metodo: " + metodo);

        // QUERY
        if ((result.getAttribute(KEY_SENHA) == null) && metodo.equalsIgnoreCase(MetodoSenhaExternaEnum.DB.getMetodo())) {
            try {
                final Connection conn = conectar();
                final PreparedStatement preStat = conn.prepareStatement(query);
                for (int i = 0; i < parametros.length; i++) {
                    try {
                        preStat.setString(i + 1, parametros[i]);
                    } catch (final Exception ex) {
                    }
                }

                final ResultSet rs = preStat.executeQuery();
                // Verifica se o result possui mais de uma coluna
                // sendo senha_atual e senha_inicial
                final int colunas = rs.getMetaData().getColumnCount();
                if (rs.next()) {
                    String senha = rs.getString(1);
                    if (senha == null) {
                        senha = "";
                    }
                    result.setAttribute(KEY_SENHA, senha);
                    // Retorna a senha_inicial concatenada com a senha_atual.
                    if (colunas > 1) {
                        result.setAttribute(KEY_SENHA_INICIAL, rs.getString(2));
                    }
                }

                // Se existir mais de um login para o servidor, retorna nulo para impedir o logon de servidor errado
                if (rs.next()) {
                    result.setAttribute(KEY_SENHA, null);
                }

                rs.close();
                preStat.close();
                conn.close();
            } catch (final SQLException ex) {
                LOG.error(ex.getMessage());
                result.setAttribute(KEY_SENHA, null);
                result.setAttribute(KEY_ERRO, ex.getMessage());
            }
        }

        // NRH7UI9
        if ((result.getAttribute(KEY_SENHA) == null) && metodo.equalsIgnoreCase(MetodoSenhaExternaEnum.NRH7UI9.getMetodo())) {
            LOG.warn("MÉTODO NRH7UI9 PARA AUTENTICAÇÃO DE SENHA EXTERNA NÃO É MAIS SUPORTADO.");
        }

        // NRH7UE
        if ((result.getAttribute(KEY_SENHA) == null) && metodo.equalsIgnoreCase(MetodoSenhaExternaEnum.NRH7UE.getMetodo())) {
            LOG.warn("MÉTODO NRH7UE PARA AUTENTICAÇÃO DE SENHA EXTERNA NÃO É MAIS SUPORTADO.");
        }

        // ACTIVE DIRECTORY
        if ((result.getAttribute(KEY_SENHA) == null) && metodo.equalsIgnoreCase(MetodoSenhaExternaEnum.AD.getMetodo())) {
            result.setAttribute(KEY_SENHA, AD.validaSenha(dominioAD, servidorAD, parametros[1], parametros[2]));
        }

        // CLASSE JAVA
        if ((result.getAttribute(KEY_SENHA) == null) && metodo.equalsIgnoreCase(MetodoSenhaExternaEnum.JAVA.getMetodo())) {
            if (validarSenhaEspecifico != null) {
                result = validarSenhaEspecifico.validarSenha(parametros, messages);
            } else {
                result.setAttribute(KEY_SENHA, null);
                result.setAttribute(KEY_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.validar.senha.usuario.metodo.autenticacao.invalido", (AcessoSistema) null));
            }
        }

        // SOAP DYNAMIC INVOCATION
        if ((result.getAttribute(KEY_SENHA) == null) && metodo.equalsIgnoreCase(MetodoSenhaExternaEnum.SOAPDI.getMetodo())) {
            LOG.warn("MÉTODO SOAPDI PARA AUTENTICAÇÃO DE SENHA EXTERNA NÃO É MAIS SUPORTADO, UTILIZE O MÉTODO SOAP.");
        }
        if ((result.getAttribute(KEY_SENHA) == null) && metodo.equalsIgnoreCase(MetodoSenhaExternaEnum.SOAP.getMetodo())) {
            final boolean sucesso = SoapServiceHelper.validar(soapServiceUrl, soapAction, soapContentType, soapResponseCharset, soapRequestXml, soapResponseField, soapExpectedValue, soapDebug, parametros);
            if (sucesso) {
                result.setAttribute(KEY_SENHA, parametros[2]);
            } else {
                result.setAttribute(KEY_SENHA, null);
                result.setAttribute(KEY_ERRO, null);
            }
        }

        // HTTP CLIENT POST/GET CONNECTION
        if ((result.getAttribute(KEY_SENHA) == null) && metodo.equalsIgnoreCase(MetodoSenhaExternaEnum.HTTPCLIENT.getMetodo())) {
            HttpUriRequest request = null;
            InputStream inputStream = null;
            HttpResponse response = null;
            String resultado = null;

            try {
                // Cria o cliente HTTP e o método POST para validação da senha
                final HttpClient client = HttpHelper.getHttpClient(httpClientKeystorePath, httpClientKeystorePass);

                // Define os parâmetros da requisição HTTP
                final List<NameValuePair> data = new ArrayList<>();
                if (!TextHelper.isNull(httpClientParamUsuario)) {
                    data.add(new BasicNameValuePair(httpClientParamUsuario, parametros[1]));
                }
                if (!TextHelper.isNull(httpClientParamSenha)) {
                    data.add(new BasicNameValuePair(httpClientParamSenha, parametros[2]));
                }
                if (!TextHelper.isNull(httpClientParamEstabelecimento)) {
                    data.add(new BasicNameValuePair(httpClientParamEstabelecimento, parametros[0]));
                }
                if (!TextHelper.isNull(httpClientParamOrgao)) {
                    data.add(new BasicNameValuePair(httpClientParamOrgao, parametros[3]));
                }
                if (!TextHelper.isNull(httpClientParamCpf)) {
                    final String cpf = (httpClientParamCpfNumerico ? TextHelper.dropSeparator(parametros[5]) : parametros[5]);
                    data.add(new BasicNameValuePair(httpClientParamCpf, cpf));
                }
                if (!TextHelper.isNull(httpClientParamIpAcesso)) {
                    data.add(new BasicNameValuePair(httpClientParamIpAcesso, parametros[4]));
                }
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

                if (TextHelper.isNull(httpClientRequestEncoding)) {
                    httpClientRequestEncoding = "UTF-8";
                }

                if ("GET".equalsIgnoreCase(httpClientMetodo)) {
                    // define parâmetros para método GET
                    final String paramString = URLEncodedUtils.format(data, httpClientRequestEncoding);
                    request = new HttpGet(url + paramString);
                } else {
                    // define parâmetros para método POST
                    request = new HttpPost(url);
                    ((HttpPost) request).setEntity(new UrlEncodedFormEntity(data, httpClientRequestEncoding));
                }

                response = client.execute(request);


                final int statusCode = response.getStatusLine().getStatusCode();
                // Input Stream para receber o resultado da requisição
                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    inputStream = entity.getContent();
                    ByteArrayOutputStream out = null;
                    if (inputStream != null) {
                        // Grava o resultado em um buffer
                        out = new ByteArrayOutputStream();
                        int c = -1;
                        int count = 0;
                        while (((c = inputStream.read()) != -1) && (count++ < 1024)) {
                            out.write(c);
                        }
                    }
                    resultado = (out == null) || TextHelper.isNull(out.toString().trim()) ? "Nenhuma resposta no corpo do HTTP foi enviada pelo sistema remoto." : out.toString().trim();

                    try {
                        resultado = URLDecoder.decode(resultado, httpClientResultEncoding);
                    } catch (final Exception ex) {
                        LOG.error(ex.getMessage());
                    }

                    LOG.debug("URI: " + request.getURI() + " | Matricula: " + parametros[1] + " | CPF: " + parametros[5] + " | Status: " + statusCode + " | Resposta: \"" + resultado + "\"");
                }

                if ((statusCode == HttpStatus.SC_OK) && (resultado != null) && resultado.equals(httpClientResultOk)) {
                    result.setAttribute(KEY_SENHA, parametros[2]);
                } else {
                    result.setAttribute(KEY_SENHA, null);
                    result.setAttribute(KEY_ERRO, statusCode == HttpStatus.SC_OK ? getErrorMessage(URLDecoder.decode(resultado, httpClientResultEncoding)) : null);
                }

            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
            } finally {
                try {
                    if (request != null) {
                        request.abort();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (final IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }

        return result;
    }

    public CustomTransferObject validarSenha(String login, String senha, String ip) {
        if ("serjoao".equals(login)) {
            final CustomTransferObject result = new CustomTransferObject();
            String rg = null;
            String cpf = null;
            String msg = null;

            if ("ser12345".equals(senha)) {
                rg = "123456";
                cpf = "111.111.111-11";
            } else if ("expirada".equals(senha)) {
                msg = "mensagem.erro.senha.expirada.certifique.ativacao";
            }
            if (!TextHelper.isNull(rg)) {
                LOG.debug("SER_LOGIN_EXTERNO:SenhaExterna.java-OK");
                result.setAttribute(SenhaExterna.KEY_SENHA, senha);
                result.setAttribute(SenhaExterna.KEY_RG, rg);
                result.setAttribute(SenhaExterna.KEY_CPF, cpf);
            } else {
                result.setAttribute(SenhaExterna.KEY_ERRO, msg);
            }
            return result;
        }

        return new CustomTransferObject();
    }

    public void atualizarSenha(String[] parametros) throws UsuarioControllerException {
        try {
            final Connection conn = conectar();
            final PreparedStatement preStat = conn.prepareStatement(update);
            for (int i = 0; i < parametros.length; i++) {
                try {
                    preStat.setString(i + 1, parametros[i]);
                } catch (final Exception ex) {
                }
            }
            preStat.executeUpdate();
            preStat.close();
            conn.close();
        } catch (final SQLException ex) {
            throw new UsuarioControllerException(ex);
        }
    }

    public boolean atualizarSenha(String login, String senha, String novaSenha, String ip) throws UsuarioControllerException {
        if ((senha != null) && "expirada".equals(senha)) {
            if ((novaSenha != null) && !"ser12345".equals(novaSenha)) {
                throw new UsuarioControllerException("mesangem.erro.nova.senha.invalida", (AcessoSistema) null);
            }
            return ((senha != null) && (novaSenha != null) && "expirada".equals(senha) && "ser12345".equals(novaSenha));
        }
        return false;
    }

    public String getErrorMessage(Object key) {
        return messages.getProperty(key == null ? "" : key.toString());
    }

    /**
     * Determina os parâmetros a serem passados para o método SOAP de autenticação
     * de acordo com o definido no arquivo de configuração.
     * @param soapMethodParams - Configuração definida para o sistema, valores possíveis:
     *      <ESTABELECIMENTO>; <ORGAO>;
     *      <SENHA>; <IP_ACESSO>;
     *      <MATRICULA>; <MATRICULA(x)>; <MATRICULA(x,y)>
     *      <CPF>; <NUM_CPF>
     *      Separador = ";"
     * @param parametros - Parametros da senha do usuário para validação:
     *      0. estIdentificador: código do estabelecimento doo servidor
     *      1. rseMatricula....: matrícula do servidor
     *      2. serSenha........: senha do servidor
     *      3. orgIdentificador: código do órgão do servidor
     *      4. ip..............: endereço IP de onde o servidor está acessando
     *      5. cpf.............: CPF do servidor
     * @return Um array de Strings com os valores dos parâmetros
     */
    public static String[] parseSoapMethodParams(String soapMethodParams, String[] parametros) {
        if (!TextHelper.isNull(soapMethodParams)) {
            final String estIdentificador = parametros[0];
            final String rseMatricula     = parametros[1];
            final String serSenha         = parametros[2];
            final String orgIdentificador = parametros[3];
            final String ipAcesso         = parametros[4];
            final String cpf              = parametros[5];

            final String[] params = soapMethodParams.split(";");
            for (int i = 0; i < params.length; i++) {
                if ("<ESTABELECIMENTO>".equalsIgnoreCase(params[i].trim())) {
                    params[i] = estIdentificador;
                } else if ("<ORGAO>".equalsIgnoreCase(params[i].trim())) {
                    params[i] = orgIdentificador;
                } else if ("<SENHA>".equalsIgnoreCase(params[i].trim())) {
                    params[i] = serSenha;
                } else if ("<IP_ACESSO>".equalsIgnoreCase(params[i].trim())) {
                    params[i] = ipAcesso;
                } else if ("<MATRICULA>".equalsIgnoreCase(params[i].trim())) {
                    params[i] = rseMatricula;
                } else if (params[i].trim().startsWith("<MATRICULA(")) {
                    int indiceIni = 0;
                    int indiceFim = rseMatricula.length();
                    if (params[i].indexOf(',') != -1) {
                        indiceIni = Integer.parseInt(params[i].substring(params[i].indexOf('(')+1, params[i].indexOf(',')).trim());
                        indiceFim = Integer.parseInt(params[i].substring(params[i].indexOf(',')+1, params[i].indexOf(')')).trim());
                    } else {
                        indiceIni = Integer.parseInt(params[i].substring(params[i].indexOf('(')+1, params[i].indexOf(')')).trim());
                    }
                    if (indiceIni < 0) {
                        indiceIni = rseMatricula.length() + indiceIni;
                    }
                    if (indiceFim < 0) {
                        indiceFim = rseMatricula.length() + indiceFim;
                    }
                    if ((indiceFim < indiceIni) || (indiceIni < 0) || (indiceFim > rseMatricula.length())) {
                        LOG.error("ERRO DE CONFIGURAÇÃO NOS PARÂMETROS DO MÉTODO DE SENHA EXTERNA: " + soapMethodParams);
                    } else {
                        params[i] = rseMatricula.substring(indiceIni, indiceFim);
                    }
                } else if ("<CPF>".equalsIgnoreCase(params[i].trim())) {
                    params[i] = cpf;
                } else if ("<NUM_CPF>".equalsIgnoreCase(params[i].trim())) {
                    params[i] = TextHelper.dropSeparator(cpf);
                } else {
                    params[i] = params[i].trim();
                }
            }

            return params;
        } else {
            return parametros;
        }
    }

    public void reset() {
        SingletonHelper.instance.configurar();
    }
}