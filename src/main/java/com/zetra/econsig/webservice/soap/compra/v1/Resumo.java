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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Resumo complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="Resumo">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="adeNumero" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         <element name="adeIdentificador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="indice" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="responsavel" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="servico" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="codVerba" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="dataReserva" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         <element name="valorParcela" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         <element name="prazo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="pagas" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="situacao" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="servicoCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="statusCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="consignataria" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="consignatariaCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Resumo", namespace = "Resumo", propOrder = {
    "adeNumero",
    "adeIdentificador",
    "indice",
    "responsavel",
    "servico",
    "codVerba",
    "dataReserva",
    "valorParcela",
    "prazo",
    "pagas",
    "situacao",
    "servicoCodigo",
    "statusCodigo",
    "consignataria",
    "consignatariaCodigo"
})
public class Resumo {

    @XmlElement(namespace = "Resumo")
    protected long adeNumero;
    @XmlElement(namespace = "Resumo", required = true, nillable = true)
    protected java.lang.String adeIdentificador;
    @XmlElement(namespace = "Resumo", required = true, nillable = true)
    protected java.lang.String indice;
    @XmlElement(namespace = "Resumo", required = true)
    protected java.lang.String responsavel;
    @XmlElement(namespace = "Resumo", required = true)
    protected java.lang.String servico;
    @XmlElement(namespace = "Resumo", required = true, nillable = true)
    protected java.lang.String codVerba;
    @XmlElement(namespace = "Resumo", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataReserva;
    @XmlElement(namespace = "Resumo")
    protected double valorParcela;
    @XmlElement(namespace = "Resumo")
    protected int prazo;
    @XmlElement(namespace = "Resumo", required = true, type = java.lang.Integer.class, nillable = true)
    protected java.lang.Integer pagas;
    @XmlElement(namespace = "Resumo", required = true)
    protected java.lang.String situacao;
    @XmlElement(namespace = "Resumo", required = true)
    protected java.lang.String servicoCodigo;
    @XmlElement(namespace = "Resumo", required = true)
    protected java.lang.String statusCodigo;
    @XmlElement(namespace = "Resumo", required = true)
    protected java.lang.String consignataria;
    @XmlElementRef(name = "consignatariaCodigo", namespace = "Resumo", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> consignatariaCodigo;

    /**
     * Obtém o valor da propriedade adeNumero.
     * 
     */
    public long getAdeNumero() {
        return adeNumero;
    }

    /**
     * Define o valor da propriedade adeNumero.
     * 
     */
    public void setAdeNumero(long value) {
        this.adeNumero = value;
    }

    /**
     * Obtém o valor da propriedade adeIdentificador.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getAdeIdentificador() {
        return adeIdentificador;
    }

    /**
     * Define o valor da propriedade adeIdentificador.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setAdeIdentificador(java.lang.String value) {
        this.adeIdentificador = value;
    }

    /**
     * Obtém o valor da propriedade indice.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getIndice() {
        return indice;
    }

    /**
     * Define o valor da propriedade indice.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setIndice(java.lang.String value) {
        this.indice = value;
    }

    /**
     * Obtém o valor da propriedade responsavel.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getResponsavel() {
        return responsavel;
    }

    /**
     * Define o valor da propriedade responsavel.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setResponsavel(java.lang.String value) {
        this.responsavel = value;
    }

    /**
     * Obtém o valor da propriedade servico.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getServico() {
        return servico;
    }

    /**
     * Define o valor da propriedade servico.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setServico(java.lang.String value) {
        this.servico = value;
    }

    /**
     * Obtém o valor da propriedade codVerba.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getCodVerba() {
        return codVerba;
    }

    /**
     * Define o valor da propriedade codVerba.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setCodVerba(java.lang.String value) {
        this.codVerba = value;
    }

    /**
     * Obtém o valor da propriedade dataReserva.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataReserva() {
        return dataReserva;
    }

    /**
     * Define o valor da propriedade dataReserva.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataReserva(XMLGregorianCalendar value) {
        this.dataReserva = value;
    }

    /**
     * Obtém o valor da propriedade valorParcela.
     * 
     */
    public double getValorParcela() {
        return valorParcela;
    }

    /**
     * Define o valor da propriedade valorParcela.
     * 
     */
    public void setValorParcela(double value) {
        this.valorParcela = value;
    }

    /**
     * Obtém o valor da propriedade prazo.
     * 
     */
    public int getPrazo() {
        return prazo;
    }

    /**
     * Define o valor da propriedade prazo.
     * 
     */
    public void setPrazo(int value) {
        this.prazo = value;
    }

    /**
     * Obtém o valor da propriedade pagas.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Integer }
     *     
     */
    public java.lang.Integer getPagas() {
        return pagas;
    }

    /**
     * Define o valor da propriedade pagas.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Integer }
     *     
     */
    public void setPagas(java.lang.Integer value) {
        this.pagas = value;
    }

    /**
     * Obtém o valor da propriedade situacao.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getSituacao() {
        return situacao;
    }

    /**
     * Define o valor da propriedade situacao.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setSituacao(java.lang.String value) {
        this.situacao = value;
    }

    /**
     * Obtém o valor da propriedade servicoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getServicoCodigo() {
        return servicoCodigo;
    }

    /**
     * Define o valor da propriedade servicoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setServicoCodigo(java.lang.String value) {
        this.servicoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade statusCodigo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getStatusCodigo() {
        return statusCodigo;
    }

    /**
     * Define o valor da propriedade statusCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setStatusCodigo(java.lang.String value) {
        this.statusCodigo = value;
    }

    /**
     * Obtém o valor da propriedade consignataria.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getConsignataria() {
        return consignataria;
    }

    /**
     * Define o valor da propriedade consignataria.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setConsignataria(java.lang.String value) {
        this.consignataria = value;
    }

    /**
     * Obtém o valor da propriedade consignatariaCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getConsignatariaCodigo() {
        return consignatariaCodigo;
    }

    /**
     * Define o valor da propriedade consignatariaCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setConsignatariaCodigo(JAXBElement<java.lang.String> value) {
        this.consignatariaCodigo = value;
    }

}
