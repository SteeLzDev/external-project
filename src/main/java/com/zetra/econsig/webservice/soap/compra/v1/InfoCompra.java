//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.compra.v1;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de InfoCompra complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="InfoCompra">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="adeNumero" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         <element name="codigoConsignataria" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nomeConsignataria" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nomeServidor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="matricula" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="dataCompra" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         <element name="dataInfoSaldoDevedor" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         <element name="valorSaldoDevedor" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="dataAprovacaoSaldoDevedor" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         <element name="dataPagamentoSaldoDevedor" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         <element name="situacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InfoCompra", namespace = "InfoCompra", propOrder = {
    "adeNumero",
    "codigoConsignataria",
    "nomeConsignataria",
    "nomeServidor",
    "matricula",
    "cpf",
    "dataCompra",
    "dataInfoSaldoDevedor",
    "valorSaldoDevedor",
    "dataAprovacaoSaldoDevedor",
    "dataPagamentoSaldoDevedor",
    "situacao"
})
public class InfoCompra {

    @XmlElementRef(name = "adeNumero", namespace = "InfoCompra", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Long> adeNumero;
    @XmlElementRef(name = "codigoConsignataria", namespace = "InfoCompra", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> codigoConsignataria;
    @XmlElementRef(name = "nomeConsignataria", namespace = "InfoCompra", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nomeConsignataria;
    @XmlElementRef(name = "nomeServidor", namespace = "InfoCompra", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nomeServidor;
    @XmlElementRef(name = "matricula", namespace = "InfoCompra", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> matricula;
    @XmlElementRef(name = "cpf", namespace = "InfoCompra", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cpf;
    @XmlElementRef(name = "dataCompra", namespace = "InfoCompra", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataCompra;
    @XmlElementRef(name = "dataInfoSaldoDevedor", namespace = "InfoCompra", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataInfoSaldoDevedor;
    @XmlElementRef(name = "valorSaldoDevedor", namespace = "InfoCompra", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> valorSaldoDevedor;
    @XmlElementRef(name = "dataAprovacaoSaldoDevedor", namespace = "InfoCompra", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataAprovacaoSaldoDevedor;
    @XmlElementRef(name = "dataPagamentoSaldoDevedor", namespace = "InfoCompra", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataPagamentoSaldoDevedor;
    @XmlElementRef(name = "situacao", namespace = "InfoCompra", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> situacao;

    /**
     * Obtém o valor da propriedade adeNumero.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *     
     */
    public JAXBElement<java.lang.Long> getAdeNumero() {
        return adeNumero;
    }

    /**
     * Define o valor da propriedade adeNumero.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *     
     */
    public void setAdeNumero(JAXBElement<java.lang.Long> value) {
        this.adeNumero = value;
    }

    /**
     * Obtém o valor da propriedade codigoConsignataria.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCodigoConsignataria() {
        return codigoConsignataria;
    }

    /**
     * Define o valor da propriedade codigoConsignataria.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCodigoConsignataria(JAXBElement<java.lang.String> value) {
        this.codigoConsignataria = value;
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
     * Obtém o valor da propriedade nomeServidor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNomeServidor() {
        return nomeServidor;
    }

    /**
     * Define o valor da propriedade nomeServidor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNomeServidor(JAXBElement<java.lang.String> value) {
        this.nomeServidor = value;
    }

    /**
     * Obtém o valor da propriedade matricula.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getMatricula() {
        return matricula;
    }

    /**
     * Define o valor da propriedade matricula.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setMatricula(JAXBElement<java.lang.String> value) {
        this.matricula = value;
    }

    /**
     * Obtém o valor da propriedade cpf.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCpf() {
        return cpf;
    }

    /**
     * Define o valor da propriedade cpf.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCpf(JAXBElement<java.lang.String> value) {
        this.cpf = value;
    }

    /**
     * Obtém o valor da propriedade dataCompra.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataCompra() {
        return dataCompra;
    }

    /**
     * Define o valor da propriedade dataCompra.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataCompra(JAXBElement<XMLGregorianCalendar> value) {
        this.dataCompra = value;
    }

    /**
     * Obtém o valor da propriedade dataInfoSaldoDevedor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataInfoSaldoDevedor() {
        return dataInfoSaldoDevedor;
    }

    /**
     * Define o valor da propriedade dataInfoSaldoDevedor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataInfoSaldoDevedor(JAXBElement<XMLGregorianCalendar> value) {
        this.dataInfoSaldoDevedor = value;
    }

    /**
     * Obtém o valor da propriedade valorSaldoDevedor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getValorSaldoDevedor() {
        return valorSaldoDevedor;
    }

    /**
     * Define o valor da propriedade valorSaldoDevedor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setValorSaldoDevedor(JAXBElement<java.lang.Double> value) {
        this.valorSaldoDevedor = value;
    }

    /**
     * Obtém o valor da propriedade dataAprovacaoSaldoDevedor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataAprovacaoSaldoDevedor() {
        return dataAprovacaoSaldoDevedor;
    }

    /**
     * Define o valor da propriedade dataAprovacaoSaldoDevedor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataAprovacaoSaldoDevedor(JAXBElement<XMLGregorianCalendar> value) {
        this.dataAprovacaoSaldoDevedor = value;
    }

    /**
     * Obtém o valor da propriedade dataPagamentoSaldoDevedor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataPagamentoSaldoDevedor() {
        return dataPagamentoSaldoDevedor;
    }

    /**
     * Define o valor da propriedade dataPagamentoSaldoDevedor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataPagamentoSaldoDevedor(JAXBElement<XMLGregorianCalendar> value) {
        this.dataPagamentoSaldoDevedor = value;
    }

    /**
     * Obtém o valor da propriedade situacao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getSituacao() {
        return situacao;
    }

    /**
     * Define o valor da propriedade situacao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setSituacao(JAXBElement<java.lang.String> value) {
        this.situacao = value;
    }

}
