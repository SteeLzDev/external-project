package com.zetra.econsig.helper.usuario;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.json.simple.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webclient.util.EconsigResponseErrorHandler;
import com.zetra.econsig.webclient.util.RestTemplateFactory;

/**
 * <p>Title: KYCHelper</p>
 * <p>Description: Classe auxiliar para gerenciar o fluxo de KYC.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class KYCHelper implements Serializable {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(KYCHelper.class);
    private static final long serialVersionUID = 1L;

    /**
     * ------------------------------------------------------------------------------------------------
     * DESENV-13591 :
     * 10.2.8.1) Os valores possíveis para o parâmetro STATUS são:
     *    STATUS = 0, Indica que a API não está funcionando. Exibir mensagem para o usuário conforme
     *                item 5 e enviar e-mail para o time técnico do banco conforme item 6.
     *
     *    STATUS = 1, Indica KYC foi verificado e que o cliente está válido. Gravar um registro de
     *                dados tipo de dado adicional criado no item 1 e do servidor (tb_dados_servidor)
     *                para o servidor/funcionário em questão, com o novo continuar fluxo de
     *                simular/solicitar consignação normalmente.
     *
     *    STATUS = 2, Indica que um dos sistemas está fora do ar. Realizar novamente a chamada
     *                "CheckKYC" e caso não obtenha sucesso, exibir mensagem para o usuário conforme
     *                item 5 e enviar e-mail para o time técnico do banco conforme item 6.
     *
     *    STATUS = 3, Indica falta de resposta do CAMS Web service. Realizar novamente a chamada
     *                "CheckKYC" e caso não obtenha sucesso, exibir mensagem para o usuário conforme
     *                item 5 e enviar e-mail para o time técnico do banco conforme item 6.
     *
     *    STATUS = 4, Abrir nova aba com a url da jornada de validação KYC conforme parâmetro do
     *                item 3. Na tela do eConsig, exibir um botão para que o colaborador possa
     *                confirmar que finalizou o KYC, a mensagem do botão será conforme item 7. Ao
     *                clicar  no botão, o sistema deve começar a jornada novamente no passo 10. Exibir
     *                também um  botão Cancelar, para que o colaborador tenha a opção de cancelar a
     *                solicitação.
     * ------------------------------------------------------------------------------------------------
     */
    public static final String CHECK_KYC_STATUS_INVALID = "0";

    public static final String CHECK_KYC_STATUS_VALID = "1";

    public static final String CHECK_KYC_STATUS_OFFLINE = "2";

    public static final String CHECK_KYC_STATUS_CAMS_WS_ERROR = "3";

    public static final String CHECK_KYC_STATUS_PENDING = "4";

    private final String serCodigo;

    private final AcessoSistema responsavel;

    private final ServidorDelegate serDelegate;

    public KYCHelper(String serCodigo, AcessoSistema responsavel) {
        this.serCodigo = serCodigo;
        this.responsavel = responsavel;
        serDelegate = new ServidorDelegate();
    }

    /**
     *
     * ------------------------------------------------------------------------------------------------
     * DESENV-13591 :
     * ------------------------------------------------------------------------------------------------
     * @return
     * @throws ServidorControllerException
     */
    public boolean validou() throws ServidorControllerException {
        return !TextHelper.isNull(serDelegate.getValorDadoServidor(serCodigo, CodedValues.TDA_DATA_HORA_VALIDACAO_KYC));
    }

    public void validar() throws ServidorControllerException {
        final String dadValor = DateHelper.toDateTimeString(DateHelper.getSystemDatetime());
        serDelegate.setValorDadoServidor(serCodigo, CodedValues.TDA_DATA_HORA_VALIDACAO_KYC, dadValor, responsavel);
    }

    /**
     * Busca o valor do PAN Number do servidor conforme configuração do sistema.
     * ------------------------------------------------------------------------------------------------
     * DESENV-13591 :
     * 8.2) Este parâmetro deve ser configurado com um nome da coluna da tb_servidor, como SER_CPF,
     * SER_NRO_IDT, SER_CART_PROF, SER_PIS, ou se o PAN number corresponder a um dado na
     * tb_dados_servidor, deve ser configurado com o código do tipo de dado adicional.
     * ------------------------------------------------------------------------------------------------
     * @return O PAN Number do servidor conforme configuração do sistema
     * @throws ServidorControllerException
     */
    public String getPanNumber() throws ServidorControllerException {
        final String source = (String) ParamSist.getInstance().getParam(CodedValues.TPC_KYC_PAN_NUMBER_SOURCE, responsavel);
        if (TextHelper.isNull(source)) {
            throw new ServidorControllerException("mensagem.erro.kyc.configuracao.invalida", responsavel);
        }
        final String column = Columns.TB_SERVIDOR + "." + source.toLowerCase();
        String panNumber = null;
        if (Columns.isColumn(column)) {
            final ServidorTransferObject servidor = serDelegate.findServidor(serCodigo, responsavel);
            panNumber = (String) servidor.getAttribute(column);
        } else {
            panNumber = serDelegate.getValorDadoServidor(serCodigo, source);
        }

        return panNumber;
    }

    public void setPanNumber(String panNumber) throws ServidorControllerException {
        final String source = (String) ParamSist.getInstance().getParam(CodedValues.TPC_KYC_PAN_NUMBER_SOURCE, responsavel);
        if (TextHelper.isNull(source)) {
            throw new ServidorControllerException("mensagem.erro.kyc.configuracao.invalida", responsavel);
        }
        final String column = Columns.TB_SERVIDOR + "." + source.toLowerCase();
        if (Columns.isColumn(column)) {
            final ServidorTransferObject servidor = serDelegate.findServidor(serCodigo, responsavel);
            servidor.setAttribute(column, panNumber);
            serDelegate.updateServidor(servidor, responsavel);
        } else {
            serDelegate.setValorDadoServidor(serCodigo, source, panNumber, responsavel);
        }
    }

    /**
    *
    * ------------------------------------------------------------------------------------------------
    * DESENV-13591 :
    * ------------------------------------------------------------------------------------------------
    * @param csaCodigo
    */
    public void enviarNotificacaoErroKYC(String csaCodigo, String panNumber, String status) {
        EnviaEmailHelper.enviaEmailNotificacaoCsaErroKYC(csaCodigo, panNumber, status, responsavel);
    }

    private RestTemplate getRestTemplate() {
        RestTemplate restTemplate;
        if (TextHelper.isNull(System.getProperty("kyc.proxy.host"))) {
            restTemplate = RestTemplateFactory.getRestTemplate(responsavel);
        } else {
            final String username = System.getProperty("kyc.proxy.user");
            final String password = System.getProperty("kyc.proxy.pass");
            final String proxyUrl = System.getProperty("kyc.proxy.host");
            final int port = Integer.parseInt(System.getProperty("kyc.proxy.port"));
            final BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(
                new AuthScope(proxyUrl, port),
                new UsernamePasswordCredentials(username, password.toCharArray())
            );
            final HttpHost myProxy = new HttpHost(proxyUrl, port);
            final HttpClientBuilder clientBuilder = HttpClientBuilder.create();

            clientBuilder.setProxy(myProxy).setDefaultCredentialsProvider(credsProvider).disableCookieManagement();

            final HttpClient httpClient = clientBuilder.build();
            final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            factory.setHttpClient(httpClient);
            restTemplate = new RestTemplate(factory);
            restTemplate.setErrorHandler(new EconsigResponseErrorHandler(responsavel));
        }
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON));
        restTemplate.getMessageConverters().add(0, converter);

        return restTemplate;
    }
   /**
     *
     * ------------------------------------------------------------------------------------------------
     * DESENV-13591 :
     * 10.2.6) Se existe, o sistema deve executar uma chamada à operação "GetTaxStatus" do serviço
     * InvestorService, passando os parâmetros definidos no arquivo Check KYC.docx e conforme os
     * parâmetros configurados no item 4.
     * Exemplo de chamada com os dados:
     * curl --location --request POST 'https://recycle.icicipruamc.com/Distributorsvcs/InvestorService.svc/JSON/GetTaxStatus?oauth_signature=5mSUhtxZMaOpCiVZOhaL+r+IyVc%3D&oauth_consumer_key=Sftgyh_UAT&oauth_timestamp=1554880440&oauth_nonce=28566793' \
     * --header 'Content-Type: application/json' \
     * --header 'Authorization: Basic U2Z0Z3loX1VBVDpGbHBvNWdfVUFU' \
     * --header 'Content-Type: application/json' \
     * --data-raw '{}'
     * ------------------------------------------------------------------------------------------------
     * @return
     * @throws ServidorControllerException
     */
    public String getStatus() throws ServidorControllerException {
        final String url = (String) ParamSist.getInstance().getParam(CodedValues.TPC_KYC_API_URL_GETTAXSTATUS, responsavel);
        final String key = (String) ParamSist.getInstance().getParam(CodedValues.TPC_KYC_API_CONSUMER_KEY, responsavel);
        final String secret = (String) ParamSist.getInstance().getParam(CodedValues.TPC_KYC_API_CONSUMER_SECRET, responsavel);
        final String resultKey = (String) ParamSist.getInstance().getParam(CodedValues.TPC_KYC_GETSTATUS_RESULT_KEY, responsavel);
        final String resultValue = (String) ParamSist.getInstance().getParam(CodedValues.TPC_KYC_GETSTATUS_RESULT_VALUE, responsavel);
        final String fieldName = (String) ParamSist.getInstance().getParam(CodedValues.TPC_KYC_GETSTATUS_RESULT_FIELD_NAME, responsavel);
        if (TextHelper.isNull(url) ||
            TextHelper.isNull(key) ||
            TextHelper.isNull(secret)||
            TextHelper.isNull(resultKey)||
            TextHelper.isNull(resultValue)||
            TextHelper.isNull(fieldName)) {
            throw new ServidorControllerException("mensagem.erro.kyc.configuracao.invalida", responsavel);
        }

        final OAuthHeaderGenerator generator = new OAuthHeaderGenerator(key, secret, null, null);
        final String nonce = generator.getNonce();
        final String timestamp = generator.getTimestamp();

        final Map<String, String> requestParams = new HashMap<>();
        requestParams.put("oauth_consumer_key", key);
        requestParams.put("oauth_timestamp", timestamp);
        requestParams.put("oauth_nonce", nonce);

        final HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Authorization", "Basic " + new String(Base64.getEncoder().encodeToString((key + ":" + secret).getBytes())));
        headers.set("Content-Type", "application/json");
        final HttpEntity<?> entity = new HttpEntity<>(new HashMap<String, String>(), headers);

        final String baseSignatureString = generator.generateSignatureBaseString("POST", url, requestParams, nonce, timestamp);
        final String signature = generator.encryptUsingHmacSHA1(baseSignatureString);

        final StringBuilder uri = new StringBuilder(url);
        uri.append(url.indexOf('?') == -1 ? "?" : "&").append("oauth_signature=").append(signature);
        for (final Map.Entry<String, String> entry : requestParams.entrySet()) {
            uri.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }

        final RestTemplate restTemplate = getRestTemplate();
        try {
            LOG.debug("curl -v --location --request POST '" + uri.toString() + "' " +
                      "--header 'Content-Type: application/json' " +
                      "--header 'Authorization: Basic " + new String(Base64.getEncoder().encodeToString((key + ":" + secret).getBytes())) + "' " +
                      "--header 'Content-Type: application/json' " +
                      "--data-raw '{}'");
            final ResponseEntity<?> restResponse = restTemplate.exchange(uri.toString(), HttpMethod.POST, entity, ArrayList.class);
            if ((restResponse != null) && (restResponse.getStatusCode() != null)) {
                LOG.debug("GetTaxStatus: retorno " + restResponse.getStatusCode().value());
                if (restResponse.getStatusCode().equals(HttpStatus.OK)) {
                    LOG.debug(restResponse.getBody());
                    @SuppressWarnings("unchecked")
                    final
                    ArrayList<HashMap<String, String>> values = (ArrayList<HashMap<String, String>>)restResponse.getBody();
                    for (final HashMap<String, String> entry : values) {
                        if ((entry.get(resultKey) != null) && entry.get(resultKey).equals(resultValue)) {
                            return entry.get(fieldName);
                        }
                    }
                }
            }
        } catch(final Exception ex) {
            if ((ex.getMessage() != null) && (ex.getMessage().indexOf("no suitable HttpMessageConverter") != -1)) {
                final ResponseEntity<?> restResponse = restTemplate.exchange(uri.toString(), HttpMethod.POST, entity, String.class);
                LOG.debug("restResponse.statusCode = " + restResponse.getStatusCode());
                LOG.debug("restResponse.body =\n" + restResponse.getBody());
            } else {
                LOG.error(ex.getLocalizedMessage(), ex);
            }
            throw new ServidorControllerException(ex);
        }

        return null;
    }

    /**
     *
     * ------------------------------------------------------------------------------------------------
     * DESENV-13591 :
     * 10.2.7) Após, fazer uma chamada à operação "CheckKYC" do serviço InvestorService, enviando no
     * parâmetro TaxStatus o TAX_CODE retornado pela operação "GetTaxStatus" para o parâmetro
     * TAX_DESC = "Individual".
     * 10.2.7.1) Demais parâmetros devem ser enviados conforme definido no arquivo Check KYC.docx e
     * conforme os parâmetros configurados no item 4.
     * 10.2.7.2) Os dados da requisição devem ser criptografados utilizando lógica de criptografia RSA.
     * Exemplo da lógica de criptografia é encontrado no arquivo: Dot Net Sample code in C#.zip
     * 10.2.7.3) O campo IsNewVersion precisa ser igual "FTM".
     * Exemplo de chamada com dos dados:
     * curl --location --request POST 'https://recycle.icicipruamc.com/Distributorsvcs/InvestorService.svc/JSON/CheckKYC?oauth_signature=wWvNTdr9JAYnTX5eaCOf3qywWmk%3D&oauth_consumer_key=Sftgyh_UAT&oauth_timestamp=1554880440&oauth_nonce=28566793' \
     * --header 'Content-Type: application/json' \
     * --header 'Authorization: Basic U2Z0Z3loX1VBVDpGbHBvNWdfVUFU' \
     * --header 'Content-Type: application/json' \
     * --data-raw '{"FirstPan":"LgTEQNYlYHBnYfNagqnTuFXx3dW0shCVIZNw3jFl4Eat1C4N%2F3moJMLOYVnMy%2FqYZMHmJiG9IBl7meW4vS2g%2Fw%3D%3D",
     *     "SecPan": "",
     *     "ThirdPan": "",
     *     "TaxStatus":"if8kAxyLqUiQV6od4JTj1bWyI3nRQq9746jrJthRqT9BPFtRf6JaIG7vge0M%2B%2FwSOfQo0sze605HAf3PIDEuYw%3D%3D",
     *     "IsNewVersion":"IFFmB5iBc2PVdELy%2FfkprBshhtfiwl0VhMQsCjrsGWupODmWtZ2hwed4o9PeSVuiUMPW0lSTCeVI9zPcHsL4Pw%3D%3D"
     * }'
     * ------------------------------------------------------------------------------------------------
     * @param taxCode
     * @return
     */
    public String checkKYC(String taxCode) throws ServidorControllerException {
        LOG.debug("taxCode = " + taxCode);
        final String url = (String) ParamSist.getInstance().getParam(CodedValues.TPC_KYC_API_URL_CHECKKYC, responsavel);
        final String key = (String) ParamSist.getInstance().getParam(CodedValues.TPC_KYC_API_CONSUMER_KEY, responsavel);
        final String secret = (String) ParamSist.getInstance().getParam(CodedValues.TPC_KYC_API_CONSUMER_SECRET, responsavel);
        final String modulusString = (String) ParamSist.getInstance().getParam(CodedValues.TPC_KYC_RSA_MODULUS, responsavel);
        final String publicExponentString = (String) ParamSist.getInstance().getParam(CodedValues.TPC_KYC_RSA_PUBLIC_EXPONENT, responsavel);
        final String isNewVersion = (String) ParamSist.getInstance().getParam(CodedValues.TPC_KYC_CHECKKYC_IS_NEW_VERSION, responsavel);
        final String fieldName = (String) ParamSist.getInstance().getParam(CodedValues.TPC_KYC_CHECKKYC_RESULT_FIELD_NAME, responsavel);
        if (TextHelper.isNull(url) ||
            TextHelper.isNull(key) ||
            TextHelper.isNull(secret) ||
            TextHelper.isNull(modulusString) ||
            TextHelper.isNull(publicExponentString) ||
            TextHelper.isNull(fieldName) ||
            TextHelper.isNull(isNewVersion)) {
            throw new ServidorControllerException("mensagem.erro.kyc.configuracao.invalida", responsavel);
        }

        final OAuthHeaderGenerator generator = new OAuthHeaderGenerator(key, secret, null, null);
        final String nonce = generator.getNonce();
        final String timestamp = generator.getTimestamp();

        final Map<String, String> body = new HashMap<>();
        body.put("FirstPan", generator.encode(encryptStringRSA(getPanNumber(), modulusString, publicExponentString)));
        body.put("SecPan", "");
        body.put("ThirdPan", "");
        body.put("TaxStatus", generator.encode(encryptStringRSA(taxCode, modulusString, publicExponentString)));
        body.put("IsNewVersion", generator.encode(encryptStringRSA(isNewVersion, modulusString, publicExponentString)));
        final HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("Authorization", "Basic " + new String(Base64.getEncoder().encodeToString((key + ":" + secret).getBytes())));
        headers.set("Content-Type", "application/json");
        final HttpEntity<?> entity = new HttpEntity<>(body, headers);

        final Map<String, String> requestParams = new HashMap<>();
        requestParams.put("oauth_consumer_key", key);
        requestParams.put("oauth_timestamp", timestamp);
        requestParams.put("oauth_nonce", nonce);

        final String baseSignatureString = generator.generateSignatureBaseString("POST", url, requestParams, nonce, timestamp);
        final String signature = generator.encryptUsingHmacSHA1(baseSignatureString);

        final StringBuilder uri = new StringBuilder(url);
        uri.append(url.indexOf('?') == -1 ? "?" : "&").append("oauth_signature=").append(signature);
        for (final Map.Entry<String, String> entry : requestParams.entrySet()) {
            uri.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }

        final RestTemplate restTemplate = getRestTemplate();
        try {
            LOG.debug("curl -v --location --request POST '" + uri.toString() + "' " +
                      "--header 'Content-Type: application/json' " +
                      "--header 'Authorization: Basic " + new String(Base64.getEncoder().encodeToString((key + ":" + secret).getBytes())) + "' " +
                      "--header 'Content-Type: application/json' " +
                      "--data-raw '" + new JSONObject(body).toJSONString() + "'");
            final ResponseEntity<?> restResponse = restTemplate.exchange(uri.toString(), HttpMethod.POST, entity, HashMap.class);
            if ((restResponse != null) && (restResponse.getStatusCode() != null)) {
                LOG.debug("CheckKYC: retorno " + restResponse.getStatusCode().value());
                if (restResponse.getStatusCode().equals(HttpStatus.OK)) {
                    LOG.debug(restResponse.getBody());
                    @SuppressWarnings("unchecked")
                    final
                    HashMap<String, String> values = (HashMap<String, String>)restResponse.getBody();
                    return ((values == null) || (values.get(fieldName) == null)) ? CHECK_KYC_STATUS_INVALID : values.get(fieldName);
                }
            }
        } catch(final Exception ex) {
            if ((ex.getMessage() != null) && (ex.getMessage().indexOf("no suitable HttpMessageConverter") != -1)) {
                final ResponseEntity<?> restResponse = restTemplate.exchange(uri.toString(), HttpMethod.POST, entity, String.class);
                LOG.debug("restResponse.statusCode = " + restResponse.getStatusCode());
                LOG.debug("restResponse.body =\n" + restResponse.getBody());
            } else {
                LOG.error(ex.getLocalizedMessage(), ex);
            }
            throw new ServidorControllerException(ex);
        }
        return CHECK_KYC_STATUS_INVALID;
    }

    /**
     *
     * @param inputString
     * @param modulusString
     * @param publicExponentString
     * @return
     * @throws ServidorControllerException
     */
    private static String encryptStringRSA(String inputString, String modulusString, String publicExponentString) throws ServidorControllerException {
        final byte[] modulusBytes = Base64.getDecoder().decode(modulusString);
        final byte[] exponentBytes = Base64.getDecoder().decode(publicExponentString);
        final BigInteger modulus = new BigInteger(1, modulusBytes);
        final BigInteger publicExponent = new BigInteger(1, exponentBytes);

        final RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(modulus, publicExponent);
        try {
            final KeyFactory fact = KeyFactory.getInstance("RSA");
            final PublicKey pubKey = fact.generatePublic(rsaPubKey);
            final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            final byte[] plainBytes = inputString.getBytes("UTF-8"); // "UTF-16LE"
            final byte[] cipherData = cipher.doFinal(plainBytes);
            return Base64.getEncoder().encodeToString(cipherData);
        } catch (GeneralSecurityException | UnsupportedEncodingException ex) {
            throw new ServidorControllerException(ex);
        }
    }

    public class OAuthHeaderGenerator {
        private final String consumerKey;
        private final String consumerSecret;
        private final String signatureMethod;
        private final String token;
        private final String tokenSecret;
        private final String version;

        public OAuthHeaderGenerator(String consumerKey, String consumerSecret, String token, String tokenSecret) {
            this.consumerKey = consumerKey;
            this.consumerSecret = consumerSecret;
            this.token = token;
            this.tokenSecret = tokenSecret;
            signatureMethod = "HMAC-SHA1";
            version = "1.0";
        }

        private static final String oauth_consumer_key = "oauth_consumer_key";
        private static final String oauth_token = "oauth_token";
        private static final String oauth_signature_method = "oauth_signature_method";
        private static final String oauth_timestamp = "oauth_timestamp";
        private static final String oauth_nonce = "oauth_nonce";
        private static final String oauth_version = "oauth_version";
        private static final String oauth_signature = "oauth_signature";
        private static final String HMAC_SHA1 = "HmacSHA1";

        /**
         * Generates oAuth 1.0a header which can be passed as Authorization header
         *
         * @param httpMethod
         * @param url
         * @param requestParams
         * @return
         */
        public String generateHeader(String httpMethod, String url, Map<String, String> requestParams) {
            final StringBuilder base = new StringBuilder();
            final String nonce = getNonce();
            final String timestamp = getTimestamp();
            final String baseSignatureString = generateSignatureBaseString(httpMethod, url, requestParams, nonce, timestamp);
            final String signature = encryptUsingHmacSHA1(baseSignatureString);
            base.append("OAuth ");
            append(base, oauth_consumer_key, consumerKey);
            append(base, oauth_token, token);
            append(base, oauth_signature_method, signatureMethod);
            append(base, oauth_timestamp, timestamp);
            append(base, oauth_nonce, nonce);
            append(base, oauth_version, version);
            append(base, oauth_signature, signature);
            base.deleteCharAt(base.length() - 1);
            System.out.println("header : " + base.toString());
            return base.toString();
        }

        /**
         * Generate base string to generate the oauth_signature
         *
         * @param httpMethod
         * @param url
         * @param requestParams
         * @return
         */
        private String generateSignatureBaseString(String httpMethod, String url, Map<String, String> requestParams, String nonce, String timestamp) {
            final Map<String, String> params = new HashMap<>();
            requestParams.entrySet().forEach(entry -> {
                put(params, entry.getKey(), entry.getValue());
            });
            put(params, oauth_consumer_key, consumerKey);
            put(params, oauth_nonce, nonce);
            put(params, oauth_signature_method, signatureMethod);
            put(params, oauth_timestamp, timestamp);
            if (!TextHelper.isNull(token)) {
                put(params, oauth_token, token);
            }
            put(params, oauth_version, version);
            final Map<String, String> sortedParams = params.entrySet().stream().sorted(Map.Entry.comparingByKey())
                                                     .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
            final StringBuilder base = new StringBuilder();
            sortedParams.entrySet().forEach(entry -> {
                base.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            });
            base.deleteCharAt(base.length() - 1);
            final String baseString = httpMethod.toUpperCase() + "&" + encode(url) + "&" + encode(base.toString());
            return baseString;
        }

        private String encryptUsingHmacSHA1(String input) {
            String secret = new StringBuilder().append(encode(consumerSecret)).append("&").toString();
            if (!TextHelper.isNull(tokenSecret)) {
                secret += encode(tokenSecret);
            }
            final byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            final SecretKey key = new SecretKeySpec(keyBytes, HMAC_SHA1);
            Mac mac;
            try {
                mac = Mac.getInstance(HMAC_SHA1);
                mac.init(key);
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                e.printStackTrace();
                return null;
            }
            final byte[] signatureBytes = mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(signatureBytes));
        }

        /**
         * Percentage encode String as per RFC 3986, Section 2.1
         *
         * @param value
         * @return
         */
        private String encode(String value) {
            if (TextHelper.isNull(value)) {
                return "";
            }
            String encoded = "";
            try {
                encoded = URLEncoder.encode(value, "UTF-8");
            } catch (final Exception e) {
                e.printStackTrace();
            }
            final StringBuilder sb = new StringBuilder();
            char focus;
            for (int i = 0; i < encoded.length(); i++) {
                focus = encoded.charAt(i);
                if (focus == '*') {
                    sb.append("%2A");
                } else if (focus == '+') {
                    sb.append("%20");
                } else if ((focus == '%') && ((i + 1) < encoded.length()) && (encoded.charAt(i + 1) == '7') && (encoded.charAt(i + 2) == 'E')) {
                    sb.append('~');
                    i += 2;
                } else {
                    sb.append(focus);
                }
            }
            return sb.toString().toString();
        }

        private void put(Map<String, String> map, String key, String value) {
            map.put(encode(key), encode(value));
        }

        private void append(StringBuilder builder, String key, String value) {
            builder.append(encode(key)).append("=\"").append(encode(value)).append("\",");
        }

        private String getNonce() {
            final int leftLimit = 48; // numeral '0'
            final int rightLimit = 122; // letter 'z'
            final int targetStringLength = 10;
            final SecureRandom random = new SecureRandom();

            final String generatedString = random.ints(leftLimit, rightLimit + 1).filter(i -> ((i <= 57) || (i >= 65)) && ((i <= 90) || (i >= 97))).limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
            return generatedString;
        }

        private String getTimestamp() {
            return Math.round((new Date()).getTime() / 1000.0) + "";
        }
    }
}
