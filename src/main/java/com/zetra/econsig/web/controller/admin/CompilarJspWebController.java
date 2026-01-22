package com.zetra.econsig.web.controller.admin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: VerificarStatusSistemaWebController</p>
 * <p>Description: Controlador Web para a página de status do sistema.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
public class CompilarJspWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CompilarJspWebController.class);

    public static final String JSP_PRECOMPILE_PARAM = "jsp_precompile";
    public static final String WEB_INF_DIR = "/WEB-INF";
    public static final String WEBAPP_DIR = "/src/main/webapp";

    @Value("**/*.jsp")
    private Resource[] jspResources;

    @RequestMapping(value = { "/v3/compilar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
        String ipsAcessoLiberado = (String) ParamSist.getInstance().getParam(CodedValues.TPC_IPS_LIBERADOS_PAGINA_ADMINISTRACAO, responsavel);
        if (TextHelper.isNull(ipsAcessoLiberado)) {
            ipsAcessoLiberado = "127.0.0.1";
        }

        if (!JspHelper.validaDDNS(JspHelper.getRemoteAddr(request), ipsAcessoLiberado)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.status.erro.acesso.negado", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String include = request.getParameter("include");
        if (!TextHelper.isNull(include)) {
            model.addAttribute("include", TextHelper.decode64(include));

        } else {
            String urlServer = request.getScheme() + "://" + request.getLocalAddr() + ":" + request.getLocalPort() + request.getContextPath();

            StringWriter out = new StringWriter();
            PrintWriter writer = new PrintWriter(out);

            int fileCount = 0;
            int errorCount = 0;

            List<String> jspFiles = getJspFiles();
            if (jspFiles.isEmpty()) {
                jspFiles = getJspFilesInWar();
            }

            for (String name : jspFiles) {
                String uri = null;
                String urlRequest = null;

                if (name.indexOf(WEB_INF_DIR) >= 0) {
                    uri = name.substring(name.indexOf(WEB_INF_DIR));
                    urlRequest = urlServer + "/v3/compilar?include=" + TextHelper.encode64(uri);
                } else {
                    if (name.indexOf(WEBAPP_DIR) >= 0) {
                        uri = name.substring(name.indexOf(WEBAPP_DIR) + WEBAPP_DIR.length());
                    } else {
                        uri = name.substring(name.indexOf("*/") + 1);
                    }
                    urlRequest = urlServer + uri + "?" + JSP_PRECOMPILE_PARAM + "=true";
                }

                String result = doGet(urlRequest);
                if (result != null) {
                    // Só imprime se resultado diferente de nulo
                    writer.println(uri + " : " + result);
                    errorCount++;
                }
                fileCount++;
            }

            model.addAttribute("result", out.toString());
            model.addAttribute("fileCount", fileCount);
            model.addAttribute("errorCount", errorCount);
        }

        return viewRedirectNoSuffix("jsp/compilarJsp/exibirResultadoCompilacao", request, session, model, responsavel);
    }

    private List<String> getJspFiles() {
        List<String> fileNames = new ArrayList<>();
        if (jspResources != null) {
            Arrays.stream(jspResources).forEach(r -> {
                try {
                    fileNames.add(r.getURI().toASCIIString());
                } catch (IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            });
        }
        return fileNames;
    }

    private List<String> getJspFilesInWar() {
        List<String> fileNames = new ArrayList<>();
        JarFile jarFile = null;
        try {
            URLClassLoader loader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
            URL urlBase = loader.getResource("/");
            String urlString = urlBase.toString();
            String filePath = urlString.substring(urlString.indexOf(File.separatorChar), urlString.indexOf('!'));
            jarFile = new JarFile(filePath);
            jarFile.entries().asIterator().forEachRemaining(f -> {
                if (f.getName().toLowerCase().endsWith(".jsp")) {
                    fileNames.add("/" + f.getName());
                }
            });

        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }

        return fileNames;
    }

    private String doGet(String jspPath) {
        String text = null;
        HttpURLConnection connection = null;

        try {
            URL dataURL = URI.create(jspPath).toURL();

            if (jspPath.startsWith("https://")) {

                // Configure the SSLContext with a TrustManager
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
                SSLContext.setDefault(ctx);

                connection = (HttpsURLConnection) dataURL.openConnection();
                ((HttpsURLConnection) connection).setHostnameVerifier((arg0, arg1) -> true);

                if (connection != null) {
                    if (connection.getResponseCode() != 200) {
                        // Só retorna mensagem caso não tenha dado certo
                        text = connection.getResponseCode() + " " + connection.getResponseMessage();
                    }
                }


            } else {
                // Open a URLConnection
                connection = (HttpURLConnection) dataURL.openConnection();
            }

            if (connection != null) {
                if (connection.getResponseCode() != 200) {
                    // Só retorna mensagem caso não tenha dado certo
                    text = connection.getResponseCode() + " " + connection.getResponseMessage();
                }
            } else {
                text = "null";
            }

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return text;
    }

    private static class DefaultTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}
