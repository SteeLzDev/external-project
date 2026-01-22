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
 * <p>Classe Java de Servico complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="Servico">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="codigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="descricao" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="codigoNaturezaServico" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="naturezaServico" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ativo" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         <element name="observacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="prioridade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Servico", namespace = "Servico", propOrder = {
    "codigo",
    "descricao",
    "codigoNaturezaServico",
    "naturezaServico",
    "ativo",
    "observacao",
    "prioridade"
})
public class Servico {

    @XmlElement(namespace = "Servico", required = true)
    protected java.lang.String codigo;
    @XmlElement(namespace = "Servico", required = true)
    protected java.lang.String descricao;
    @XmlElementRef(name = "codigoNaturezaServico", namespace = "Servico", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> codigoNaturezaServico;
    @XmlElementRef(name = "naturezaServico", namespace = "Servico", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> naturezaServico;
    @XmlElementRef(name = "ativo", namespace = "Servico", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Short> ativo;
    @XmlElementRef(name = "observacao", namespace = "Servico", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> observacao;
    @XmlElementRef(name = "prioridade", namespace = "Servico", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> prioridade;

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
     * Obtém o valor da propriedade codigoNaturezaServico.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCodigoNaturezaServico() {
        return codigoNaturezaServico;
    }

    /**
     * Define o valor da propriedade codigoNaturezaServico.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCodigoNaturezaServico(JAXBElement<java.lang.String> value) {
        this.codigoNaturezaServico = value;
    }

    /**
     * Obtém o valor da propriedade naturezaServico.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNaturezaServico() {
        return naturezaServico;
    }

    /**
     * Define o valor da propriedade naturezaServico.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNaturezaServico(JAXBElement<java.lang.String> value) {
        this.naturezaServico = value;
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
     * Obtém o valor da propriedade observacao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getObservacao() {
        return observacao;
    }

    /**
     * Define o valor da propriedade observacao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setObservacao(JAXBElement<java.lang.String> value) {
        this.observacao = value;
    }

    /**
     * Obtém o valor da propriedade prioridade.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getPrioridade() {
        return prioridade;
    }

    /**
     * Define o valor da propriedade prioridade.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setPrioridade(JAXBElement<java.lang.String> value) {
        this.prioridade = value;
    }

}
