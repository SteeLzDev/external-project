package com.zetra.econsig.helper.rede;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.ssl.SSLContexts;

import com.zetra.econsig.config.SysConfig;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileInfo;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

/**
 * <p>Title: HttpHelper</p>
 * <p>Description: Helper Class para conexões programáticas HTTP ao sistema eConsig.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author: fagner.luiz $
 * $Revision: 28993 $
 * $Date: 2020-03-05 10:18:26 -0300 (qui, 05 mar 2020) $
 */
public class HttpHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(HttpHelper.class);

    public static final String REQUEST_QUERY_STRING_JSON = "queryString";
    public static final String MULTI_PARTPARAM_REQUEST_PARAMS = "multiPartparam";
    public static final String ARQUIVO_REQUEST_PARAM_NAME = "arquivoParam";
    public static final int TIMEOUT = 30000;
    public static final int REQUEST_TIMEOUT = 30000;
    public static final int SOCKET_TIMEOUT = 30000;

    public enum SessionKeysEnum {

        CONTEXT_PATH("1"),
        SESSION_ID("2"),
        REQUEST_URL("3"),
        REQUEST_TOKEN("4"),
        REQUEST_USER_AGENT("5"),
        SESSION_COOKIE_NAME("6");

        private final String codigo;

        private SessionKeysEnum(String codigo) {
            this.codigo = codigo;
        }

        public String getCodigo() {
            return codigo;
        }

        public static SessionKeysEnum recuperaAcao(String codigo) {
            SessionKeysEnum chave = null;

            for (final SessionKeysEnum aca : SessionKeysEnum.values()) {
                if (aca.getCodigo().equals(codigo)) {
                    chave = aca;
                    break;
                }
            }

            if (chave == null) {
                throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.session.key.invalido", (AcessoSistema) null));
            }

            return chave;
        }

        public final boolean equals(SessionKeysEnum other) {
            return this==other || getCodigo().equals(other.getCodigo());
        }
    }


    /**
     * Configura o HttpClient a usar um TrustStore passado por parâmetro
     * para validar o endereço do servidor de senha externa de acordo com
     * o respositório de certificados SSL.
     * @param base
     * @param trustStorePath
     * @param trustStorePass
     * @return
     */
    public static HttpClient getHttpClient(String trustStorePath, String trustStorePass) {
        String timeOut = System.getProperty("httpclient.timeout");
        String requestTimeout = System.getProperty("httpclient.request.timeout");
        String socketTimeout = System.getProperty("httpclient.socket.timeout");
        
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(TextHelper.isNull(timeOut) ? TIMEOUT : Integer.valueOf(timeOut))
                .setConnectionRequestTimeout(TextHelper.isNull(requestTimeout) ? REQUEST_TIMEOUT : Integer.valueOf(requestTimeout))
                .setSocketTimeout(TextHelper.isNull(socketTimeout) ? SOCKET_TIMEOUT : Integer.valueOf(socketTimeout))
                .build();
        
        if (!TextHelper.isNull(trustStorePath) && !TextHelper.isNull(trustStorePass)) {
            try {
                LOG.debug("Keystore Path: " + trustStorePath);
                File trustStoreFile = null;
                if (trustStorePath.startsWith("file://")) {
                    trustStoreFile = new File(new URI(trustStorePath));
                } else {
                    trustStoreFile = new File(trustStorePath);
                }

                final HostnameVerifier verifier = (hostname, session) -> true;

                final SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(trustStoreFile, trustStorePass.toCharArray(), new TrustSelfSignedStrategy()).build();
                final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null, verifier);

                return HttpClients.custom()
                        .setDefaultRequestConfig(requestConfig)
                        .setSSLSocketFactory(sslsf)
                        .setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
                        .build();
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
        
        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()))
                .build();
    }

    /**
     * Executa uma requisição POST ao sistema econsig dentro de uma sessão corrente
     * @param sessionConfig - configurações da sessão corrente
     * @param acessoRecurso - acessoRecurso que se quer fazer a requisição
     * @param paramsRequisicao - parâmetros de request necessários em formato JSON, sendo que o elemento queryString é uma lista de chave e valor de query strings da requisição,
     *                           multiPartparam de parâmetros textuais enviadas como part de um MultiPart request
     *                           e arquivoParam dos arquivos binários enviados também como part de um MultiPart request
     * @return
     * @throws MalformedURLException
     * @throws IOException
     * @throws ProtocolException
     * @throws UnsupportedEncodingException
     */
    public static HttpURLConnection eConsigPostHttpRequest(Map<HttpHelper.SessionKeysEnum, String> sessionConfig, com.zetra.econsig.persistence.entity.AcessoRecurso acessoRecurso
                                                          , JsonObject paramsRequisicao,  Collection<FileInfo> arquivos, AcessoSistema responsavel) throws ZetraException {
        DataOutputStream out = null;
        HttpURLConnection con = null;
        final boolean isMultiPart = arquivos != null && !arquivos.isEmpty() && paramsRequisicao.getJsonObject(HttpHelper.ARQUIVO_REQUEST_PARAM_NAME) != null;
        final String lineFeed = "\r\n";

        final String boundary = "-----------------------------" + Long.toString(System.currentTimeMillis());

        if (SysConfig.get().getActiveProfile() != null && SysConfig.get().getActiveProfile().startsWith("dev")) {
            // Desabilita verificação de nome do certificado em caso de acesso HTTPS local
            desabilitaSSL();
        }

        try {
            if (!isMultiPart) {
                final URL url = URI.create(sessionConfig.get(HttpHelper.SessionKeysEnum.REQUEST_URL) + acessoRecurso.getAcrRecurso()).toURL();
                LOG.info(url);

                con = (HttpURLConnection) url.openConnection();

                con.setRequestMethod("POST");
                con.setUseCaches(false);
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("Cookie", sessionConfig.get(HttpHelper.SessionKeysEnum.SESSION_COOKIE_NAME) + "=" + sessionConfig.get(HttpHelper.SessionKeysEnum.SESSION_ID));
                con.setRequestProperty("user-agent", sessionConfig.get(HttpHelper.SessionKeysEnum.REQUEST_USER_AGENT));

                con.connect();

                out = new DataOutputStream(con.getOutputStream());

                final JsonObject queryStringObjct = paramsRequisicao.getJsonObject(HttpHelper.REQUEST_QUERY_STRING_JSON);

                final Map<String, List<String>> params = new HashMap<>();

                if (queryStringObjct != null) {
                    final Set<String> jsonKeys = queryStringObjct.keySet();
                    if (jsonKeys != null) {
                        jsonKeys.forEach(key -> {
                            HttpHelper.addParamHttpRequest(params, key, queryStringObjct.get(key));
                        });
                    }
                }

                final StringBuilder postData = new StringBuilder();
                for (final Map.Entry<String, List<String>> param : params.entrySet()) {
                    for (final String paramValue: param.getValue()) {
                        if (postData.length() != 0) {
                            postData.append('&');
                        }

                        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(paramValue), "UTF-8"));
                    }
                }
                postData.append('&').append(sessionConfig.get(HttpHelper.SessionKeysEnum.REQUEST_TOKEN));
                final byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                //con.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                out.write(postDataBytes);
                out.flush();
            } else {
                final JsonObject queryStringObjct = paramsRequisicao.getJsonObject(HttpHelper.REQUEST_QUERY_STRING_JSON);

                final Map<String, List<String>> params = new HashMap<>();

                if (queryStringObjct != null) {
                    final Set<String> jsonKeys = queryStringObjct.keySet();
                    if (jsonKeys != null) {
                        jsonKeys.forEach(key -> {
                            HttpHelper.addParamHttpRequest(params, key, queryStringObjct.get(key));
                        });
                    }
                }

                final StringBuilder postData = new StringBuilder();
                for (final Map.Entry<String, List<String>> param : params.entrySet()) {
                    for (final String paramValue: param.getValue()) {
                        if (postData.length() != 0) {
                            postData.append('&');
                        }

                        postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(paramValue), "UTF-8"));
                    }
                }
                postData.append('&').append(sessionConfig.get(HttpHelper.SessionKeysEnum.REQUEST_TOKEN));


                final URL url = URI.create(sessionConfig.get(HttpHelper.SessionKeysEnum.REQUEST_URL) + acessoRecurso.getAcrRecurso() + "?" + postData.toString()).toURL();
                LOG.info(url);

                con = (HttpURLConnection) url.openConnection();

                con.setRequestMethod("POST");
                con.setUseCaches(false);
                con.setDoOutput(true);
                con.setDoInput(true);
                long tamTotalArqs = 0;
                for (final FileInfo fileInfo: arquivos) {
                    tamTotalArqs += fileInfo.fileSize;
                }
                con.setRequestProperty("Content-Length",Long.toString(tamTotalArqs));
                con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                con.setRequestProperty("connection","keep-alive");
                con.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                con.setRequestProperty("Accept-Encoding","gzip, deflate");
                con.setConnectTimeout(30000);
                con.setRequestProperty("Cookie", sessionConfig.get(HttpHelper.SessionKeysEnum.SESSION_COOKIE_NAME) + "=" + sessionConfig.get(HttpHelper.SessionKeysEnum.SESSION_ID));
                con.setRequestProperty("user-agent", sessionConfig.get(HttpHelper.SessionKeysEnum.REQUEST_USER_AGENT));

                con.connect();

                out = new DataOutputStream(con.getOutputStream());
                final JsonObject nomeArqParam = paramsRequisicao.getJsonObject(HttpHelper.ARQUIVO_REQUEST_PARAM_NAME);

                final JsonValue fileReqstParam = nomeArqParam.get(HttpHelper.ARQUIVO_REQUEST_PARAM_NAME);
                for (final FileInfo fileInfo: arquivos) {
                    HttpHelper.addFilePart(out, fileReqstParam.toString(), fileInfo.fileName, fileInfo.fileContent, boundary);
                }

                final JsonObject multiPartStringObjct = paramsRequisicao.getJsonObject(HttpHelper.MULTI_PARTPARAM_REQUEST_PARAMS);
                final Set<String> jsonKeys = multiPartStringObjct.keySet();
                if (jsonKeys != null) {
                    for (final String key: jsonKeys) {
                        HttpHelper.addFormFieldMultiPart(out, key, multiPartStringObjct.get(key).toString().replace("\"", "").replace("[", "").replace("]", ""), boundary);
                    }
                }

                out.writeBytes("--" + boundary + "--" + lineFeed);
                out.flush();
            }
        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException e) {
                    LOG.error(e.getMessage(), e);
                    throw new ZetraException("mensagem.erroInternoSistema", responsavel, e);
                }
            }
        }

        return con;
    }

    private static void addFormFieldMultiPart(DataOutputStream writer, String name, String value, String boundary) throws IOException {
        final String [] valores = value.toString().split(",");

        if (valores.length == 1) {
            final String lineEnd = "\r\n";

            writer.writeBytes("--" + boundary + lineEnd);
            writer.writeBytes("Content-Disposition: form-data; name=\"" + name + "\"" + lineEnd);
            writer.writeBytes(lineEnd);
            writer.writeBytes(valores[0].toString().replace("\"", "").replace("[", "").replace("]", "") + lineEnd);

            writer.flush();
        } else {
            for (final String valor: valores) {
                addFormFieldMultiPart(writer, name, valor, boundary);
            }
        }
    }

    private static void addFilePart(DataOutputStream outputStream, String fieldName, String fileName, byte [] file, String boundary) throws IOException {
        final String lineFeed = "\r\n";

        outputStream.writeBytes("--" + boundary + lineFeed);
        outputStream.writeBytes("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"" + lineFeed);
        final String contentType = URLConnection.guessContentTypeFromName(fileName);
        outputStream.writeBytes("Content-Type: " + (!TextHelper.isNull(contentType) ? contentType : "text/plain") + lineFeed);
        outputStream.writeBytes(lineFeed);
        outputStream.flush();

        outputStream.write(file);

        outputStream.flush();
        outputStream.writeBytes(lineFeed);
        outputStream.flush();
    }

    private static void addParamHttpRequest(Map<String, List<String>> params, String chave, JsonValue value) {
        final String [] valores = value.toString().split(",");

        if (valores.length == 1 || value.getValueType() != JsonValue.ValueType.ARRAY) {
            params.computeIfAbsent(chave, k -> new ArrayList<>()).add(value.toString().replace("\"", ""));
        } else {
            for (final String valor: valores) {
                params.computeIfAbsent(chave, k -> new ArrayList<>()).add(valor.toString().replace("\"", "").replace("[", "").replace("]", ""));
            }
        }
    }

    // Desabilita a verificação de validade de certificados SSL
    public static void desabilitaSSL() {
        // Cria um TrustManager que não valida certificado algum.
        final TrustManager[] trustAllCerts = { new javax.net.ssl.X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        } };

        // Instala o validador de certificados.
        try {
            final SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (final Exception ex) {
        }

        // Desabilita a verificação de hostname
        HttpsURLConnection.setDefaultHostnameVerifier ((hostname, session) -> true);
    }
}
