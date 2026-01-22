//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v3;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Anexo complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="Anexo">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="nomeArquivo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="arquivo" type="{http://schemas.xmlsoap.org/soap/encoding/}base64"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Anexo", namespace = "Anexo", propOrder = {
    "nomeArquivo",
    "arquivo"
})
public class Anexo {

    @XmlElement(namespace = "Anexo", required = true)
    protected java.lang.String nomeArquivo;
    @XmlElement(namespace = "Anexo", required = true)
    protected byte[] arquivo;

    /**
     * Obtém o valor da propriedade nomeArquivo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getNomeArquivo() {
        return nomeArquivo;
    }

    /**
     * Define o valor da propriedade nomeArquivo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setNomeArquivo(java.lang.String value) {
        this.nomeArquivo = value;
    }

    /**
     * Obtém o valor da propriedade arquivo.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getArquivo() {
        return arquivo;
    }

    /**
     * Define o valor da propriedade arquivo.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setArquivo(byte[] value) {
        this.arquivo = value;
    }

}
