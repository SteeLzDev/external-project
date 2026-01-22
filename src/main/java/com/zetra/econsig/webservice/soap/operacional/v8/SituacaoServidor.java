//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v8;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de SituacaoServidor complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="SituacaoServidor">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="ativo" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="bloqueado" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="excluido" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="falecido" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="pendente" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SituacaoServidor", namespace = "SituacaoServidor", propOrder = {
    "ativo",
    "bloqueado",
    "excluido",
    "falecido",
    "pendente"
})
public class SituacaoServidor {

    @XmlElement(namespace = "SituacaoServidor", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean ativo;
    @XmlElement(namespace = "SituacaoServidor", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean bloqueado;
    @XmlElement(namespace = "SituacaoServidor", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean excluido;
    @XmlElement(namespace = "SituacaoServidor", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean falecido;
    @XmlElement(namespace = "SituacaoServidor", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean pendente;

    /**
     * Obtém o valor da propriedade ativo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getAtivo() {
        return ativo;
    }

    /**
     * Define o valor da propriedade ativo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setAtivo(java.lang.Boolean value) {
        this.ativo = value;
    }

    /**
     * Obtém o valor da propriedade bloqueado.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getBloqueado() {
        return bloqueado;
    }

    /**
     * Define o valor da propriedade bloqueado.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setBloqueado(java.lang.Boolean value) {
        this.bloqueado = value;
    }

    /**
     * Obtém o valor da propriedade excluido.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getExcluido() {
        return excluido;
    }

    /**
     * Define o valor da propriedade excluido.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setExcluido(java.lang.Boolean value) {
        this.excluido = value;
    }

    /**
     * Obtém o valor da propriedade falecido.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getFalecido() {
        return falecido;
    }

    /**
     * Define o valor da propriedade falecido.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setFalecido(java.lang.Boolean value) {
        this.falecido = value;
    }

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

}
