//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.5 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v8;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de StatusValidacao complex type.</p>
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.</p>
 * 
 * <pre>{@code
 * <complexType name="StatusValidacao">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="pendente" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="aprovada" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StatusValidacao", namespace = "StatusValidacao", propOrder = {
    "pendente",
    "aprovada"
})
public class StatusValidacao {

    @XmlElement(namespace = "StatusValidacao")
    protected java.lang.Boolean pendente;
    @XmlElement(namespace = "StatusValidacao")
    protected java.lang.Boolean aprovada;

    /**
     * Obtém o valor da propriedade pendente.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getPendente() {
        return pendente;
    }

    /**
     * Define o valor da propriedade pendente.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setPendente(java.lang.Boolean value) {
        this.pendente = value;
    }

    /**
     * Obtém o valor da propriedade aprovada.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getAprovada() {
        return aprovada;
    }

    /**
     * Define o valor da propriedade aprovada.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setAprovada(java.lang.Boolean value) {
        this.aprovada = value;
    }

}
