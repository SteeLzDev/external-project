//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v8;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de AnexoConsignacao complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="AnexoConsignacao">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="nomeArquivo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="tipoArquivo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="descricaoTipoArquivo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="arquivo" type="{http://schemas.xmlsoap.org/soap/encoding/}base64" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AnexoConsignacao", namespace = "AnexoConsignacao", propOrder = {
    "nomeArquivo",
    "tipoArquivo",
    "descricaoTipoArquivo",
    "arquivo"
})
public class AnexoConsignacao {

    @XmlElement(namespace = "AnexoConsignacao", required = true)
    protected java.lang.String nomeArquivo;
    @XmlElement(namespace = "AnexoConsignacao", required = true)
    protected java.lang.String tipoArquivo;
    @XmlElement(namespace = "AnexoConsignacao", required = true)
    protected java.lang.String descricaoTipoArquivo;
    @XmlElementRef(name = "arquivo", namespace = "AnexoConsignacao", type = JAXBElement.class, required = false)
    protected JAXBElement<byte[]> arquivo;

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
     * Obtém o valor da propriedade tipoArquivo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getTipoArquivo() {
        return tipoArquivo;
    }

    /**
     * Define o valor da propriedade tipoArquivo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setTipoArquivo(java.lang.String value) {
        this.tipoArquivo = value;
    }

    /**
     * Obtém o valor da propriedade descricaoTipoArquivo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getDescricaoTipoArquivo() {
        return descricaoTipoArquivo;
    }

    /**
     * Define o valor da propriedade descricaoTipoArquivo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setDescricaoTipoArquivo(java.lang.String value) {
        this.descricaoTipoArquivo = value;
    }

    /**
     * Obtém o valor da propriedade arquivo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     *     
     */
    public JAXBElement<byte[]> getArquivo() {
        return arquivo;
    }

    /**
     * Define o valor da propriedade arquivo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link byte[]}{@code >}
     *     
     */
    public void setArquivo(JAXBElement<byte[]> value) {
        this.arquivo = value;
    }

}
