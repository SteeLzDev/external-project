package com.zetra.econsig.helper.totem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

import com.zetra.econsig.exception.TotemControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: TotemCHelper</p>
 * <p>Description: Classe cliente para acessar o Totem.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TotemHelper implements Serializable {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TotemHelper.class);
    private static final long serialVersionUID = 1L;

    private final AcessoSistema responsavel;

    public TotemHelper(AcessoSistema responsavel) {
        this.responsavel = responsavel;
    }

    public HttpClient wrapHttpClient(String trustStorePath, String trustStorePass) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, URISyntaxException {
        CloseableHttpClient httpClient;

        if (!TextHelper.isNull(trustStorePath) && !TextHelper.isNull(trustStorePass)) {
            LOG.debug("Keystore Path: " + trustStorePath);
            File trustStoreFile = null;
            if (trustStorePath.startsWith("file://")) {
                trustStoreFile = new File(new URI(trustStorePath));
            } else {
                trustStoreFile = new File(trustStorePath);
            }

            HostnameVerifier verifier = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(trustStoreFile, trustStorePass.toCharArray(), new TrustSelfSignedStrategy()).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, verifier);
            httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

        } else {
            TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
            HostnameVerifier hostnameVerifier = (s, sslSession) -> true;
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
            httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        }
        return httpClient;
    }

    public void limparCache() throws TotemControllerException {
        String url = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TOTEM_URL_LIMPAR_CACHE, responsavel);
        if (TextHelper.isNull(url)) {
            return;
        }

        HttpUriRequest request = null;
        InputStream inputStream = null;
        HttpResponse response = null;

        try {
            // Cria o cliente HTTP e o método POST para validação da senha
            String httpClientKeystorePath = null;
            String httpClientKeystorePass = null;
            HttpClient client = wrapHttpClient(httpClientKeystorePath, httpClientKeystorePass);
            request = new HttpGet(url);
            response = client.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            LOG.debug("retorno " + statusCode);
        } catch(Exception ex) {
          LOG.error(ex.getLocalizedMessage(), ex);
          throw new TotemControllerException(ex);
        } finally {
            try {
                if (request != null) {
                    request.abort();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }
}
