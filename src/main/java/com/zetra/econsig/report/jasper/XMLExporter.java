package com.zetra.econsig.report.jasper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: XMLExporter</p>
 * <p>Description: Exporta um documento JasperReports formato CSV para o formato TXT.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class XMLExporter {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(XMLExporter.class);

    private DocumentBuilderFactory domFactory = null;

    private DocumentBuilder domBuilder = null;

    private AcessoSistema responsavel = null;

    public XMLExporter(AcessoSistema responsavel) {
        try {
            this.responsavel = responsavel;
            domFactory = DocumentBuilderFactory.newInstance();
            domBuilder = domFactory.newDocumentBuilder();
        } catch (FactoryConfigurationError | ParserConfigurationException ex) {
            System.err.println(ex.toString());
        }

    }

    public void convertFile(String csvFileName, String xmlFileName, String delimiter) {
        BufferedReader csvReader = null;
        try {
            final Document newDoc = domBuilder.newDocument();

            // Root element
            final Element rootElement = newDoc.createElement("XMLCreators");
            newDoc.appendChild(rootElement);

            // Read csv file
            csvReader = new BufferedReader(new FileReader(csvFileName));

            int line = 0;
            final List<String> headers = new ArrayList<>();

            String text = null;
            while ((text = csvReader.readLine()) != null) {
                final String[] rowValues = text.split(delimiter, -1);

                if (line == 0) { // Header row
                    for (final String col : rowValues) {
                        headers.add(Normalizer.normalize(col, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").replaceAll("\\p{Punct}", "").replace(' ', '_'));
                    }
                } else { // Data row
                    final Element rowElement = newDoc.createElement("row");
                    rootElement.appendChild(rowElement);

                    for (int col = 0; col < headers.size(); col++) {
                        final String header = headers.get(col);
                        String value = null;

                        if (col < rowValues.length) {
                            value = rowValues[col];
                        } else {
                            // Default value
                            value = "";
                        }

                        final Element curElement = newDoc.createElement(header);
                        curElement.appendChild(newDoc.createTextNode(value));
                        rowElement.appendChild(curElement);
                    }
                }
                line++;
            }

            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlFileName),"UTF-8"));

                final TransformerFactory tranFactory = TransformerFactory.newInstance();
                final Transformer aTransformer = tranFactory.newTransformer();
                aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
                aTransformer.setOutputProperty(OutputKeys.METHOD, "xml");
                aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                final Source src = new DOMSource(newDoc);
                final Result result = new StreamResult(writer);
                aTransformer.transform(src, result);

                writer.flush();
            } catch (final Exception ex) {
                LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel), ex);
            } finally {
                try {
                    writer.close();
                } catch (final Exception e) {
                    LOG.warn("Não foi possível fechar escritor XML.", e);
                }
            }

        } catch (final IOException ex) {
            LOG.warn("Não foi possível ler o arquivo.", ex);
        } finally {
            try {
                if (csvReader != null) {
                    csvReader.close();
                }
            } catch (final IOException ex) {
                LOG.warn("Não foi possível fechar leitor CSV.", ex);
            }
        }
    }
}
