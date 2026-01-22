//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.folha.v1;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Perfil complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="Perfil">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="codigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="descricao" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="ativo" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         <element name="dataExpiracao" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="ipAcesso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ddnsAcesso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Perfil", namespace = "Perfil", propOrder = {
    "codigo",
    "descricao",
    "ativo",
    "dataExpiracao",
    "ipAcesso",
    "ddnsAcesso"
})
public class Perfil {

    @XmlElement(namespace = "Perfil", required = true)
    protected java.lang.String codigo;
    @XmlElement(namespace = "Perfil", required = true)
    protected java.lang.String descricao;
    @XmlElementRef(name = "ativo", namespace = "Perfil", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Short> ativo;
    @XmlElementRef(name = "dataExpiracao", namespace = "Perfil", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataExpiracao;
    @XmlElementRef(name = "ipAcesso", namespace = "Perfil", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> ipAcesso;
    @XmlElementRef(name = "ddnsAcesso", namespace = "Perfil", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> ddnsAcesso;

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
     * Obtém o valor da propriedade ativo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public JAXBElement<java.lang.Short> getAtivo() {
        return ativo;
    }

    /**
     * Define o valor da propriedade ativo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public void setAtivo(JAXBElement<java.lang.Short> value) {
        this.ativo = value;
    }

    /**
     * Obtém o valor da propriedade dataExpiracao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataExpiracao() {
        return dataExpiracao;
    }

    /**
     * Define o valor da propriedade dataExpiracao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataExpiracao(JAXBElement<XMLGregorianCalendar> value) {
        this.dataExpiracao = value;
    }

    /**
     * Obtém o valor da propriedade ipAcesso.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getIpAcesso() {
        return ipAcesso;
    }

    /**
     * Define o valor da propriedade ipAcesso.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setIpAcesso(JAXBElement<java.lang.String> value) {
        this.ipAcesso = value;
    }

    /**
     * Obtém o valor da propriedade ddnsAcesso.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getDdnsAcesso() {
        return ddnsAcesso;
    }

    /**
     * Define o valor da propriedade ddnsAcesso.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setDdnsAcesso(JAXBElement<java.lang.String> value) {
        this.ddnsAcesso = value;
    }

}
