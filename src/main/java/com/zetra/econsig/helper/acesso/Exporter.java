package com.zetra.econsig.helper.acesso;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.zetra.econsig.dto.entidade.AcessoTransferObject;
import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: Exporter</p>
 * <p>Description: Transforma um {@link AcessoTransferObject} em XML.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Exporter {
    private final AcessoTransferObject acessoTO;
    private boolean indentOutput = true;

    public Exporter(AcessoTransferObject acessoTO) {
        this.acessoTO = acessoTO;
    }

    public Exporter setIndented(boolean indented) {
        indentOutput = indented;
        return this;
    }

    public String toXml() {
        String xml = null;
        try {
            StreamResult streamResult = new StreamResult(new StringWriter());

            SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
            // SAX2.0 ContentHandler.
            TransformerHandler hd = tf.newTransformerHandler();
            Transformer serializer = hd.getTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            //serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "acesso.dtd");
            serializer.setOutputProperty(OutputKeys.INDENT, indentOutput ? "yes" : "no");
            hd.setResult(streamResult);
            hd.startDocument();
            AttributesImpl atts = new AttributesImpl();
            atts.addAttribute("", "", "versao", "CDATA", acessoTO.getVersao());
            hd.startElement("", "", "acesso", atts);

            atts.clear();
            String value = TextHelper.isNull(acessoTO.getLogin()) ? "" : acessoTO.getLogin();
            hd.startElement("", "", "login", atts);
            hd.characters(value.toCharArray(), 0, value.length());
            hd.endElement("", "", "login");

            value = TextHelper.isNull(acessoTO.getSenha()) ? "" : acessoTO.getSenha();
            hd.startElement("", "", "senha", atts);
            hd.characters(value.toCharArray(), 0, value.length());
            hd.endElement("", "", "senha");

            value = TextHelper.isNull(acessoTO.getComando()) ? "" : acessoTO.getComando();
            hd.startElement("", "", "comando", atts);
            hd.characters(value.toCharArray(), 0, value.length());
            hd.endElement("", "", "comando");

            value = TextHelper.isNull(acessoTO.getConsignataria()) ? "" : acessoTO.getConsignataria();
            hd.startElement("", "", "consignataria", atts);
            hd.characters(value.toCharArray(), 0, value.length());
            hd.endElement("", "", "consignataria");

            value = TextHelper.isNull(acessoTO.getURIAcesso()) ? "" : acessoTO.getURIAcesso();
            hd.startElement("", "", "uri", atts);
            hd.characters(value.toCharArray(), 0, value.length());
            hd.endElement("", "", "uri");

            value = TextHelper.isNull(acessoTO.getTimestampCentralizador()) ? "" : acessoTO.getTimestampCentralizador();
            hd.startElement("", "", "tcentralizador", atts);
            hd.characters(value.toCharArray(), 0, value.length());
            hd.endElement("", "", "tcentralizador");

            value = TextHelper.isNull(acessoTO.getTimestampEconsig()) ? "" : acessoTO.getTimestampEconsig();
            hd.startElement("", "", "teconsig", atts);
            hd.characters(value.toCharArray(), 0, value.length());
            hd.endElement("", "", "teconsig");

            value = TextHelper.isNull(acessoTO.getResultado()) ? "" : acessoTO.getResultado();
            hd.startElement("", "", "resultado", atts);
            hd.characters(value.toCharArray(), 0, value.length());
            hd.endElement("", "", "resultado");

            hd.endElement("", "", "acesso");
            hd.endDocument();
            xml = streamResult.getWriter().toString();
        } catch (TransformerConfigurationException ex) {
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (TransformerFactoryConfigurationError ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        }
        return xml;
    }
}
