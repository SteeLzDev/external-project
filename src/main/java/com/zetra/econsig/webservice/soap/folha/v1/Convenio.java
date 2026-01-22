//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.folha.v1;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Convenio complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="Convenio">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="codigoServico" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="codigoOrgao" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="codigoConsignataria" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="codigoEstabelecimento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="verbaConvenio" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="verbaConvenioRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="verbaConvenioFerias" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nomeOrgao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nomeConsignataria" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="descricaoServico" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Convenio", namespace = "Convenio", propOrder = {
    "codigoServico",
    "codigoOrgao",
    "codigoConsignataria",
    "codigoEstabelecimento",
    "verbaConvenio",
    "verbaConvenioRef",
    "verbaConvenioFerias",
    "nomeOrgao",
    "nomeConsignataria",
    "descricaoServico"
})
public class Convenio {

    @XmlElement(namespace = "Convenio", required = true)
    protected java.lang.String codigoServico;
    @XmlElement(namespace = "Convenio", required = true)
    protected java.lang.String codigoOrgao;
    @XmlElement(namespace = "Convenio", required = true)
    protected java.lang.String codigoConsignataria;
    @XmlElement(namespace = "Convenio", required = true)
    protected java.lang.String codigoEstabelecimento;
    @XmlElement(namespace = "Convenio", required = true)
    protected java.lang.String verbaConvenio;
    @XmlElementRef(name = "verbaConvenioRef", namespace = "Convenio", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> verbaConvenioRef;
    @XmlElementRef(name = "verbaConvenioFerias", namespace = "Convenio", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> verbaConvenioFerias;
    @XmlElementRef(name = "nomeOrgao", namespace = "Convenio", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nomeOrgao;
    @XmlElementRef(name = "nomeConsignataria", namespace = "Convenio", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nomeConsignataria;
    @XmlElementRef(name = "descricaoServico", namespace = "Convenio", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> descricaoServico;

    /**
     * Obtém o valor da propriedade codigoServico.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getCodigoServico() {
        return codigoServico;
    }

    /**
     * Define o valor da propriedade codigoServico.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setCodigoServico(java.lang.String value) {
        this.codigoServico = value;
    }

    /**
     * Obtém o valor da propriedade codigoOrgao.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getCodigoOrgao() {
        return codigoOrgao;
    }

    /**
     * Define o valor da propriedade codigoOrgao.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setCodigoOrgao(java.lang.String value) {
        this.codigoOrgao = value;
    }

    /**
     * Obtém o valor da propriedade codigoConsignataria.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getCodigoConsignataria() {
        return codigoConsignataria;
    }

    /**
     * Define o valor da propriedade codigoConsignataria.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setCodigoConsignataria(java.lang.String value) {
        this.codigoConsignataria = value;
    }

    /**
     * Obtém o valor da propriedade codigoEstabelecimento.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getCodigoEstabelecimento() {
        return codigoEstabelecimento;
    }

    /**
     * Define o valor da propriedade codigoEstabelecimento.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setCodigoEstabelecimento(java.lang.String value) {
        this.codigoEstabelecimento = value;
    }

    /**
     * Obtém o valor da propriedade verbaConvenio.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getVerbaConvenio() {
        return verbaConvenio;
    }

    /**
     * Define o valor da propriedade verbaConvenio.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setVerbaConvenio(java.lang.String value) {
        this.verbaConvenio = value;
    }

    /**
     * Obtém o valor da propriedade verbaConvenioRef.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getVerbaConvenioRef() {
        return verbaConvenioRef;
    }

    /**
     * Define o valor da propriedade verbaConvenioRef.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setVerbaConvenioRef(JAXBElement<java.lang.String> value) {
        this.verbaConvenioRef = value;
    }

    /**
     * Obtém o valor da propriedade verbaConvenioFerias.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getVerbaConvenioFerias() {
        return verbaConvenioFerias;
    }

    /**
     * Define o valor da propriedade verbaConvenioFerias.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setVerbaConvenioFerias(JAXBElement<java.lang.String> value) {
        this.verbaConvenioFerias = value;
    }

    /**
     * Obtém o valor da propriedade nomeOrgao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNomeOrgao() {
        return nomeOrgao;
    }

    /**
     * Define o valor da propriedade nomeOrgao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNomeOrgao(JAXBElement<java.lang.String> value) {
        this.nomeOrgao = value;
    }

    /**
     * Obtém o valor da propriedade nomeConsignataria.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNomeConsignataria() {
        return nomeConsignataria;
    }

    /**
     * Define o valor da propriedade nomeConsignataria.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNomeConsignataria(JAXBElement<java.lang.String> value) {
        this.nomeConsignataria = value;
    }

    /**
     * Obtém o valor da propriedade descricaoServico.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getDescricaoServico() {
        return descricaoServico;
    }

    /**
     * Define o valor da propriedade descricaoServico.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setDescricaoServico(JAXBElement<java.lang.String> value) {
        this.descricaoServico = value;
    }

}
