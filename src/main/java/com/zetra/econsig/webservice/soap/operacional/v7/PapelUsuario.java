//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v7;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de PapelUsuario complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="PapelUsuario">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="cse" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="org" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="csa" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="cor" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="ser" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="sup" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PapelUsuario", namespace = "PapelUsuario", propOrder = {
    "cse",
    "org",
    "csa",
    "cor",
    "ser",
    "sup"
})
public class PapelUsuario {

    @XmlElement(namespace = "PapelUsuario")
    protected java.lang.Boolean cse;
    @XmlElement(namespace = "PapelUsuario")
    protected java.lang.Boolean org;
    @XmlElement(namespace = "PapelUsuario")
    protected java.lang.Boolean csa;
    @XmlElement(namespace = "PapelUsuario")
    protected java.lang.Boolean cor;
    @XmlElement(namespace = "PapelUsuario")
    protected java.lang.Boolean ser;
    @XmlElement(namespace = "PapelUsuario")
    protected java.lang.Boolean sup;

    /**
     * Obtém o valor da propriedade cse.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getCse() {
        return cse;
    }

    /**
     * Define o valor da propriedade cse.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setCse(java.lang.Boolean value) {
        this.cse = value;
    }

    /**
     * Obtém o valor da propriedade org.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getOrg() {
        return org;
    }

    /**
     * Define o valor da propriedade org.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setOrg(java.lang.Boolean value) {
        this.org = value;
    }

    /**
     * Obtém o valor da propriedade csa.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getCsa() {
        return csa;
    }

    /**
     * Define o valor da propriedade csa.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setCsa(java.lang.Boolean value) {
        this.csa = value;
    }

    /**
     * Obtém o valor da propriedade cor.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getCor() {
        return cor;
    }

    /**
     * Define o valor da propriedade cor.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setCor(java.lang.Boolean value) {
        this.cor = value;
    }

    /**
     * Obtém o valor da propriedade ser.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getSer() {
        return ser;
    }

    /**
     * Define o valor da propriedade ser.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setSer(java.lang.Boolean value) {
        this.ser = value;
    }

    /**
     * Obtém o valor da propriedade sup.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getSup() {
        return sup;
    }

    /**
     * Define o valor da propriedade sup.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setSup(java.lang.Boolean value) {
        this.sup = value;
    }

}
