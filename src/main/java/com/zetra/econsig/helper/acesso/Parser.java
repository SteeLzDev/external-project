package com.zetra.econsig.helper.acesso;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.zetra.econsig.dto.entidade.AcessoTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: Parser</p>
 * <p>Description: Cria um {@link AcessoTransferObject} a partir de um XML.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Parser {

    public AcessoTransferObject toTransferObject(String xml) throws ZetraException {
        try {
            return toTransferObject(xml.getBytes("iso-8859-1"));
        } catch (UnsupportedEncodingException ex) {
            throw new ZetraException(ex);
        }
    }

    public AcessoTransferObject toTransferObject(byte[] xml) throws ZetraException {
        String versao = null;
        String login = null;
        String senha = null;
        String comando = null;
        String consignataria = null;
        String uri = null;
        String tcentralizador = null;
        String teconsig = null;
        String resultado = null;

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new ByteArrayInputStream(xml));
            Node acesso = doc.getFirstChild();
            NamedNodeMap atributos = acesso.getAttributes();
            for (int i = 0; i < atributos.getLength(); i++) {
                if ("versao".equals(atributos.item(i).getNodeName())) {
                    versao = atributos.item(i).getTextContent();
                }
            }
            NodeList nodes = acesso.getChildNodes();
            for (int j = 0; j < nodes.getLength(); j++) {
                Node atributo = nodes.item(j);
                if ("login".equals(atributo.getNodeName())) {
                    login = atributo.getTextContent();
                } else if ("senha".equals(atributo.getNodeName())) {
                    senha = atributo.getTextContent();
                } else if ("comando".equals(atributo.getNodeName())) {
                    comando = atributo.getTextContent();
                } else if ("consignataria".equals(atributo.getNodeName())) {
                    consignataria = atributo.getTextContent();
                } else if ("uri".equals(atributo.getNodeName())) {
                    uri = atributo.getTextContent();
                } else if ("tcentralizador".equals(atributo.getNodeName())) {
                    tcentralizador = atributo.getTextContent();
                } else if ("teconsig".equals(atributo.getNodeName())) {
                    teconsig = atributo.getTextContent();
                } else if ("resultado".equals(atributo.getNodeName())) {
                    resultado = atributo.getTextContent();
                }
            }
        } catch (ParserConfigurationException ex) {
            throw new ZetraException(ex);
        } catch (SAXException ex) {
            throw new ZetraException(ex);
        } catch (IOException ex) {
            throw new ZetraException(ex);
        }

        AcessoTransferObject acessoTo = new AcessoTransferObject(login, senha);
        if (TextHelper.isNull(login) &&
            TextHelper.isNull(senha) &&
            TextHelper.isNull(comando) &&
            TextHelper.isNull(consignataria) &&
            TextHelper.isNull(uri)) {
           throw new ZetraException("mensagem.erro.xml.parser.informacao.necessaria", (AcessoSistema) null);
        } else if (!acessoTo.getVersao().equals(versao)) {
            throw new ZetraException("mensagem.erro.xml.parser.numero.versao", (AcessoSistema) null, acessoTo.getVersao(), versao);
        }
        acessoTo.setComando(comando);
        acessoTo.setConsignataria(consignataria);
        acessoTo.setURIAcesso(uri);
        if (tcentralizador != null) {
            acessoTo.setTimestampCentralizador(tcentralizador);
            if (teconsig != null) {
                acessoTo.setTimestampEconsig(teconsig);
                if (resultado != null) {
                    acessoTo.setResultado(resultado);
                }
            }
        }

        return acessoTo;
    }
}
