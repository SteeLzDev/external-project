package com.zetra.econsig.helper.xml;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.config.DocumentoTipo;
import com.zetra.econsig.parser.config.ObjectFactory;
import com.zetra.econsig.parser.config.ParametroTipo;
import com.zetra.econsig.parser.config.RegistroTipo;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

/**
 * <p>Title: XmlHelper</p>
 * <p>Description: Class utilitária para manipulação de objetos configurados
 * através de arquivos XML.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class XmlHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(XmlHelper.class);

    // Versão atual do módulo XML
    public static final String VERSAO = "1.2.1";

    /**
     * Transforma um arquivo XML, obtido do Stream de entrada, em uma
     * hierarquia de objetos chamada "Documento".
     *
     * @param entrada
     * @return
     * @throws JAXBException
     */
    public static DocumentoTipo unmarshal(InputStream entrada) throws ParserException {
        try {
            JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);
            Unmarshaller u = jc.createUnmarshaller();
            return ((JAXBElement<DocumentoTipo>) u.unmarshal(entrada)).getValue();
        } catch (JAXBException ex) {
            throw new ParserException("mensagem.erroInternoSistema", AcessoSistema.getAcessoUsuarioSistema(), ex);
        }
    }

    public static DocumentoTipo unmarshal(String nomeArqEntrada) throws ParserException {
        try (FileInputStream in = new FileInputStream(nomeArqEntrada)) {
            return unmarshal(in);
        } catch (IOException ex) {
            throw new ParserException("mensagem.erroInternoSistema", AcessoSistema.getAcessoUsuarioSistema(), ex);
        }
    }

    /**
     * Transforma uma hierarquia de objetos representada por "Documento"
     * em um arquivo XML a ser impresso no Stream de saída.
     *
     * @param documento
     * @param saida
     * @throws JAXBException
     */
    public static void marshal(DocumentoTipo documento, OutputStream saida) throws ParserException {
        try {
            JAXBContext jc = JAXBContext.newInstance(ObjectFactory.class);
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_ENCODING, Charset.defaultCharset().toString()); // "iso-8859-1"
            m.marshal(documento, saida);
        } catch (JAXBException ex) {
            throw new ParserException("mensagem.erroInternoSistema", AcessoSistema.getAcessoUsuarioSistema(), ex);
        }
    }

    /**
     * Gera uma string contendo a saída xml dos parâmetros passados.
     *
     * @param parametros List<Map<String, String>> Lista de mapeamentos que devem ser utilizados para gerar a saída xml.
     * @return Retorna uma string contendo a saída xml dos parâmetros passados.
     */
    public static String gerarXml(List<Map<String, String>> parametros) {
        try {
            ObjectFactory factory = new ObjectFactory();
            DocumentoTipo resultado = factory.createDocumentoTipo();
            resultado.setID(VERSAO);

            // Pega a referência para a lista de registros
            // que inicialmente está vazia
            List<RegistroTipo> registros = resultado.getRegistro();

            // Adiciona o regitro 'RESULTADO'
            RegistroTipo reg = factory.createRegistroTipo();
            reg.setNome(ApplicationResourcesHelper.getMessage("rotulo.xml.resultado", (AcessoSistema) null));
            List<ParametroTipo> attrs = reg.getAtributo();

            for (Map<String, String> m : parametros) {
                for (String chave : m.keySet()) {
                    String valor = m.get(chave);
                    adicionaAtributo(attrs, factory, chave, valor);
                }
            }
            registros.add(reg);

            OutputStream saida = new ByteArrayOutputStream();
            marshal(resultado, saida);
            return saida.toString();

        } catch (ParserException ex) {
            LOG.error("Não foi possível gerar saída XML. " + ex.getMessage());
            return "";
        }
    }

    /**
     * Adiciona atributos a uma lista de atributos
     * @param attrs   : referência para a lista de atributos
     * @param factory : factory para a criação de atributos
     * @param nome    : nome do atributo
     * @param valor   : valor do atributo
     */
    public static void adicionaAtributo(List<ParametroTipo> attrs, ObjectFactory factory, Object nome, Object valor) {
        if (valor instanceof TransferObject to) {
            for (String chave : to.getAtributos().keySet()) {
                adicionaAtributo(attrs, factory, chave, to.getAttribute(chave));
            }
        } else {
            ParametroTipo attr = factory.createParametroTipo();
            attr.setNome(nome.toString());
            if (valor == null) {
                attr.setValor("");
            } else {
                attr.setValor(valor.toString());
            }
            attrs.add(attr);
        }
    }

    /**
     * Adiciona atributos a uma lista de atributos
     * @param attrs     : referência para a lista de atributos
     * @param factory   : factory para a criação de atributos
     * @param atributos : mapa contendo os atributos a serem adicionados
     * @param keyOrder  : ordem dos atributos a serem adicionados
     */
    public static void adicionaAtributo(List<ParametroTipo> attrs, ObjectFactory factory, Map<String, Object> atributos, List<String> keyOrder) {
        if (atributos != null) {
            Set<String> chaves = atributos.keySet();

            if (keyOrder != null) {
                for (String element : keyOrder) {
                    adicionaAtributo(attrs, factory, element, atributos.get(element));
                }
            } else {
                for(Object chave: chaves) {
                    adicionaAtributo(attrs, factory, chave, atributos.get(chave));
                }
            }
        }
    }

    /**
     * Transforma uma lista de parametros, que são pares de chave - valor,
     * em um Map
     * @param parametros : os parâmetros
     * @return : o map com os parâmetros
     */
    public static Map<String, Object> parametrosToMap(List<ParametroTipo> parametros) {
        if (parametros != null) {
            Map<String, Object> retorno = new HashMap<>();
            for (ParametroTipo param : parametros) {
                retorno.put(param.getNome(), param.getValor());
            }
            return retorno;
        } else {
            return null;
        }
    }

    /**
     * Recupera no documento XML o parâmetro com o nome informado.
     * @param nome
     * @param documento
     * @return
     */
    public static String getParametroPorNome(String nome, DocumentoTipo documento) {
        return getParametroPorNome(nome, documento, "");
    }

    /**
     * Recupera no documento XML o parâmetro com o nome informado. Caso não exista
     * retorna o valor default informado.
     * @param nome
     * @param documento
     * @return
     */
    public static String getParametroPorNome(String nome, DocumentoTipo documento, String valorDefault) {
        if (nome != null && documento != null && documento.getParametro() != null && !documento.getParametro().isEmpty()) {
            for (ParametroTipo parametro : documento.getParametro()) {
                if (nome.equalsIgnoreCase(parametro.getNome())) {
                    return parametro.getValor();
                }
            }
        }
        return valorDefault;
    }
}
