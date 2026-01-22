package com.zetra.econsig.webservice.soap.endpoint.interceptor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.soap.saaj.support.SaajUtils;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.xml.validation.XmlValidator;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.zetra.econsig.helper.texto.TextHelper;

import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFault;
import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPPart;

/**
 * <p>Title: SoapEnvelopeInterceptor</p>
 * <p>Description: Ajusta o formato do xml devolvido para o padrão antigo.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@Component
public class SoapEnvelopeInterceptor extends PayloadValidatingInterceptor {
    private static final String PREFERRED_PREFIX = "soapenv";

    private static final Map<String, XmlValidator> validators = new HashMap<>();

    static {
        validators.put("CompraService-v1_0", new SimpleXsdSchema(new ClassPathResource("/wsdl/xsd/compra/CompraService.xsd")).createValidator());
        validators.put("FolhaService-v1_0", new SimpleXsdSchema(new ClassPathResource("/wsdl/xsd/folha/FolhaService.xsd")).createValidator());
        validators.put("LoteService-v1_0", new SimpleXsdSchema(new ClassPathResource("/wsdl/xsd/lote/LoteService.xsd")).createValidator());
        validators.put("HostaHostService-v1_0", new SimpleXsdSchema(new ClassPathResource("/wsdl/xsd/operacional/HostaHostService.xsd")).createValidator());
        validators.put("HostaHostService-v2_0", new SimpleXsdSchema(new ClassPathResource("/wsdl/xsd/operacional/HostaHostService-v2_0.xsd")).createValidator());
        validators.put("HostaHostService-v3_0", new SimpleXsdSchema(new ClassPathResource("/wsdl/xsd/operacional/HostaHostService-v3_0.xsd")).createValidator());
        validators.put("HostaHostService-v4_0", new SimpleXsdSchema(new ClassPathResource("/wsdl/xsd/operacional/HostaHostService-v4_0.xsd")).createValidator());
        validators.put("HostaHostService-v6_0", new SimpleXsdSchema(new ClassPathResource("/wsdl/xsd/operacional/HostaHostService-v6_0.xsd")).createValidator());
        validators.put("HostaHostService-v7_0", new SimpleXsdSchema(new ClassPathResource("/wsdl/xsd/operacional/HostaHostService-v7_0.xsd")).createValidator());
        validators.put("HostaHostService-v8_0", new SimpleXsdSchema(new ClassPathResource("/wsdl/xsd/operacional/HostaHostService-v8_0.xsd")).createValidator());
        validators.put("ServidorService-v1_0", new SimpleXsdSchema(new ClassPathResource("/wsdl/xsd/servidor/ServidorService.xsd")).createValidator());
        validators.put("ServidorService-v2_0", new SimpleXsdSchema(new ClassPathResource("/wsdl/xsd/servidor/ServidorService-v2_0.xsd")).createValidator());
        validators.put("ServidorService-v3_0", new SimpleXsdSchema(new ClassPathResource("/wsdl/xsd/servidor/ServidorService-v3_0.xsd")).createValidator());
    }

    public SoapEnvelopeInterceptor() {
        setValidateRequest(true);
        setValidateResponse(true);
        setXsdSchema(new SimpleXsdSchema(new ClassPathResource("/wsdl/xsd/operacional/HostaHostService.xsd")));
    }

    private void alterSoapEnvelope(SaajSoapMessage soapResponse) throws SOAPException {
        final SOAPMessage soapMessage = soapResponse.getSaajMessage();
        final SOAPPart soapPart = soapMessage.getSOAPPart();
        final SOAPEnvelope envelope = soapPart.getEnvelope();
        final SOAPHeader header = soapMessage.getSOAPHeader();
        final SOAPBody body = soapMessage.getSOAPBody();
        final SOAPFault fault = body.getFault();
        final String uri = envelope.getNamespaceURI(envelope.getPrefix());
        envelope.removeNamespaceDeclaration(envelope.getPrefix());
        envelope.addNamespaceDeclaration(PREFERRED_PREFIX, uri);
        envelope.setPrefix(PREFERRED_PREFIX);
        header.setPrefix(PREFERRED_PREFIX);
        body.setPrefix(PREFERRED_PREFIX);
        if (fault != null) {
            fault.setPrefix(PREFERRED_PREFIX);
        }
    }

    private XmlValidator getValidator(SOAPMessage soapMessage) throws SOAPException {
        final SOAPPart soapPart = soapMessage.getSOAPPart();
        final SOAPEnvelope envelope = soapPart.getEnvelope();
        final SOAPBody body = soapMessage.getSOAPBody();
        final SOAPElement element = SaajUtils.getFirstBodyElement(body);
        final String prefix = element.getPrefix();
        if (TextHelper.isNull(prefix)) {
            throw new SOAPException();
        }
        String namespace = envelope.getNamespaceURI(prefix);
        if (TextHelper.isNull(namespace)) {
            namespace = element.getNamespaceURI(prefix);
            if (TextHelper.isNull(namespace)) {
                namespace = body.getNamespaceURI(prefix);
                if (TextHelper.isNull(namespace)) {
                    throw new SOAPException();
                }
            }
        }
        return validators.get(namespace);
    }

    @Override
    public boolean handleRequest(MessageContext messageContext, Object endpoint) throws IOException, TransformerException, SAXException {
        final Source requestSource = getValidationRequestSource(messageContext.getRequest());
        if (requestSource != null) {
            try {
                final SAXParseException[] errors = getValidator(((SaajSoapMessage) messageContext.getRequest()).getSaajMessage()).validate(requestSource);
                if (!ObjectUtils.isEmpty(errors)) {
                    return handleRequestValidationErrors(messageContext, errors);
                } else if (logger.isDebugEnabled()) {
                    logger.debug("Request message validated");
                }
            } catch (final SOAPException ex) {
                throw new SAXException(ex.getMessage(), ex);
            }
        }
        return true;
    }

    @Override
    public boolean handleResponse(MessageContext messageContext, Object endpoint) throws IOException, SAXException {
        final SaajSoapMessage response = (SaajSoapMessage) messageContext.getResponse();
        try {
            alterSoapEnvelope(response);

            final Source responseSource = getValidationResponseSource(messageContext.getResponse());
            if (responseSource != null) {
                final SAXParseException[] errors = getValidator(response.getSaajMessage()).validate(responseSource);
                if (!ObjectUtils.isEmpty(errors)) {
                    return handleResponseValidationErrors(messageContext, errors);
                } else if (logger.isDebugEnabled()) {
                    logger.debug("Response message validated");
                }
            }
        } catch (final SOAPException ex) {
            throw new SAXException(ex.getMessage(), ex);
        }
        return false;
    }

    @Override
    public boolean handleFault(MessageContext messageContext, Object endpoint) throws Exception {
        final SaajSoapMessage response = (SaajSoapMessage) messageContext.getResponse();
        alterSoapEnvelope(response);
        return true;
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Object endpoint, Exception ex) {
        //
    }
}
