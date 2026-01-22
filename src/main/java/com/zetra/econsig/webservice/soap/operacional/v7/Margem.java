//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v7;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Margem complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="Margem">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="codigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="descricao" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="valorFolha" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="valorUsado" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="valorDisponivel" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="valorLimite" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="mensagem" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Margem", namespace = "Margem", propOrder = {
    "codigo",
    "descricao",
    "valorFolha",
    "valorUsado",
    "valorDisponivel",
    "valorLimite",
    "mensagem"
})
public class Margem {

    @XmlElement(namespace = "Margem", required = true, nillable = true)
    protected java.lang.String codigo;
    @XmlElement(namespace = "Margem", required = true, nillable = true)
    protected java.lang.String descricao;
    @XmlElementRef(name = "valorFolha", namespace = "Margem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> valorFolha;
    @XmlElementRef(name = "valorUsado", namespace = "Margem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> valorUsado;
    @XmlElementRef(name = "valorDisponivel", namespace = "Margem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> valorDisponivel;
    @XmlElementRef(name = "valorLimite", namespace = "Margem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> valorLimite;
    @XmlElementRef(name = "mensagem", namespace = "Margem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> mensagem;

    /**
     * Obtém o valor da propriedade codigo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getCodigo() {
        return codigo;
    }

    /**
     * Define o valor da propriedade codigo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setCodigo(java.lang.String value) {
        this.codigo = value;
    }

    /**
     * Obtém o valor da propriedade descricao.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getDescricao() {
        return descricao;
    }

    /**
     * Define o valor da propriedade descricao.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setDescricao(java.lang.String value) {
        this.descricao = value;
    }

    /**
     * Obtém o valor da propriedade valorFolha.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getValorFolha() {
        return valorFolha;
    }

    /**
     * Define o valor da propriedade valorFolha.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setValorFolha(JAXBElement<java.lang.Double> value) {
        this.valorFolha = value;
    }

    /**
     * Obtém o valor da propriedade valorUsado.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getValorUsado() {
        return valorUsado;
    }

    /**
     * Define o valor da propriedade valorUsado.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setValorUsado(JAXBElement<java.lang.Double> value) {
        this.valorUsado = value;
    }

    /**
     * Obtém o valor da propriedade valorDisponivel.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getValorDisponivel() {
        return valorDisponivel;
    }

    /**
     * Define o valor da propriedade valorDisponivel.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setValorDisponivel(JAXBElement<java.lang.Double> value) {
        this.valorDisponivel = value;
    }

    /**
     * Obtém o valor da propriedade valorLimite.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getValorLimite() {
        return valorLimite;
    }

    /**
     * Define o valor da propriedade valorLimite.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setValorLimite(JAXBElement<java.lang.Double> value) {
        this.valorLimite = value;
    }

    /**
     * Obtém o valor da propriedade mensagem.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getMensagem() {
        return mensagem;
    }

    /**
     * Define o valor da propriedade mensagem.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setMensagem(JAXBElement<java.lang.String> value) {
        this.mensagem = value;
    }

}
