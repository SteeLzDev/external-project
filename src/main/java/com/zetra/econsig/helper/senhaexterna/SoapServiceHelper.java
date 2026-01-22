package com.zetra.econsig.helper.senhaexterna;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SoapServiceHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SoapServiceHelper.class);

    static boolean validar(String serviceUrl, String soapAction, String contentType, String responseCharset, String requestXml, String responseField, String expectedValue, boolean debug, String[] param) {
        try {
            final String xmlInput = requestXml
                    .replaceAll("__estIdentificador__", param[0])
                    .replaceAll("__rseMatricula__", param[1])
                    .replaceAll("__serSenha__", param[2])
                    .replaceAll("__orgIdentificador__", param[3])
                    .replaceAll("__ipAcesso__", param[4])
                    .replaceAll("__serCpf__", param[5])
                    .replaceAll("__rseMatriculaInst__", param[6])
                    ;

            if (debug) {
                LOG.debug("Request: " + requestXml
                        .replaceAll("__estIdentificador__", param[0])
                        .replaceAll("__rseMatricula__", param[1])
                        .replaceAll("__serSenha__", "********")
                        .replaceAll("__orgIdentificador__", param[3])
                        .replaceAll("__ipAcesso__", param[4])
                        .replaceAll("__serCpf__", param[5])
                        .replaceAll("__rseMatriculaInst__", param[6])
                        );
            }

            final byte[] buffer = xmlInput.getBytes();
            final ByteArrayOutputStream bout = new ByteArrayOutputStream();
            bout.write(buffer);

            final HttpURLConnection httpConn = (HttpURLConnection) new URI(serviceUrl).toURL().openConnection();
            httpConn.setRequestProperty("Content-Length", String.valueOf(buffer.length));
            httpConn.setRequestProperty("Content-Type", contentType);
            httpConn.setRequestProperty("SOAPAction", soapAction);
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);

            try (final OutputStream out = httpConn.getOutputStream()) {
                out.write(buffer);
            }

            final StringBuilder output = new StringBuilder();

            try (final InputStreamReader isr = new InputStreamReader(httpConn.getInputStream(), Charset.forName(responseCharset))) {
                final BufferedReader in = new BufferedReader(isr);

                String responseString = null;
                while ((responseString = in.readLine()) != null) {
                    output.append(responseString);
                }
            }

            if (debug) {
                LOG.debug("Response: " + output.toString());
            }

            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final InputSource is = new InputSource(new StringReader(output.toString()));
            final Document document = db.parse(is);
            final NodeList nodeList = document.getElementsByTagName(responseField);
            final String response = nodeList.item(0).getTextContent();

            return expectedValue != null && expectedValue.equalsIgnoreCase(response);

        } catch (DOMException | URISyntaxException | IOException | ParserConfigurationException | SAXException ex) {
            LOG.error(ex.getMessage(), ex);
            return false;
        }
    }
}