//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.folha.v1;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de SituacaoUsuario complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="SituacaoUsuario">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="ativo" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="bloqueado" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="excluido" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SituacaoUsuario", namespace = "SituacaoUsuario", propOrder = {
    "ativo",
    "bloqueado",
    "excluido"
})
public class SituacaoUsuario {

    @XmlElement(namespace = "SituacaoUsuario")
    protected java.lang.Boolean ativo;
    @XmlElement(namespace = "SituacaoUsuario")
    protected java.lang.Boolean bloqueado;
    @XmlElement(namespace = "SituacaoUsuario")
    protected java.lang.Boolean excluido;

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

}
