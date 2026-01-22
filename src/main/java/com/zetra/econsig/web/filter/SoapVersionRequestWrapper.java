package com.zetra.econsig.web.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.ws.soap.saaj.support.SaajUtils;

import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.webservice.soap.util.ApiVersionMapper;
import com.zetra.econsig.webservice.soap.util.VersionInfo;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.MimeHeaders;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPMessage;

/**
 * <p>Title: SoapVersionRequestWrapper</p>
 * <p>Description: Wrapper para request para substituir o namespace no envelope SOAP.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Leonel Martins
 */
class SoapVersionRequestWrapper extends HttpServletRequestWrapper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SoapVersionRequestWrapper.class);

    private final ApiVersionMapper apiVersionMapper;
    private final ByteArrayReader input;
    private boolean usingReader;

    // identifies empty tag i.e <tag1></tag1> or <tag1/>
    private static final String EMPTY_VALUED_TAG_REGEX = "<(\\w+:\\w+)></\\1>|<(\\w+:\\w+)/>";

    public SoapVersionRequestWrapper(ApiVersionMapper apiVersionMapper, HttpServletRequest request) throws IOException  {
        super(request);
        this.apiVersionMapper = apiVersionMapper;
        usingReader = false;
        input = new ByteArrayReader(replaceNamespace(request));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // will error out, if in use
        if (usingReader) {
            super.getInputStream();
        }
        usingReader = true;
        return input.getStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        // will error out, if in use
        if (usingReader) {
            super.getReader();
        }
        usingReader = true;
        return input.getReader();
    }

    private String getLocalPart(String message) {
        String localPart = "";
        try {
            final MessageFactory mf = MessageFactory.newInstance();
            final MimeHeaders header = new MimeHeaders();
            header.addHeader("Content-Type", "text/xml");
            final ByteArrayInputStream bais = new ByteArrayInputStream(message.getBytes());
            final SOAPMessage soapMessage = mf.createMessage(header, bais);
            final SOAPBody soapBody = soapMessage.getSOAPBody();
            localPart = SaajUtils.getFirstBodyElement(soapBody).getElementName().getLocalName();
        } catch (final Exception ex) {
        	LOG.error(message, ex);
        }
        return localPart;
    }

    private byte[] replaceNamespace(HttpServletRequest request) throws IOException {
    	String content = new String(request.getInputStream().readAllBytes());
    	if ((content.indexOf("<soapenv:Envelope") != -1) && (content.indexOf("</soapenv:Envelope>") == -1)) {
    		content += "</soapenv:Envelope>";
    	}

        if (TextHelper.isNull(content)) {
            return new byte[0];
        }

    	final String localPart = getLocalPart(content);
    	String namespace = request.getPathInfo().replace("/", "");
    	final VersionInfo info = new VersionInfo(namespace);
    	namespace = apiVersionMapper.getNamespace(namespace, localPart);
    	if ((namespace != null) && !content.contains(namespace) && !namespace.equals(info.getService())) {
    		content = content.replace(info.getService(), namespace);
    	}
    	content = content.replaceAll(EMPTY_VALUED_TAG_REGEX, "");
    	return content.getBytes();
    }

    private static class ByteArrayServletStream extends ServletInputStream {
        private final ByteArrayInputStream bais;

        public ByteArrayServletStream(ByteArrayInputStream bais) {
            this.bais = bais;
        }

        @Override
        public boolean isFinished() {
            return bais.available() <= 0;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            System.out.println("setReadListener with " + readListener);
        }

        @Override
        public int read() throws IOException {
            return bais.read();
        }
    }

    private static class ByteArrayReader {
        private final ByteArrayInputStream bais;
        private final BufferedReader br;
        private final ServletInputStream sis;

        public ByteArrayReader(byte[] buffer) {
            bais = new ByteArrayInputStream(buffer);
            br = new BufferedReader(new InputStreamReader(bais));
            sis = new ByteArrayServletStream(bais);
        }

        public BufferedReader getReader() {
            return br;
        }

        public ServletInputStream getStream() {
            return sis;
        }
    }
}
