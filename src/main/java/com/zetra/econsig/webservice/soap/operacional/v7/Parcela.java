//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v7;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Parcela complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="Parcela">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="numero" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="dataDesconto" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="dataRealizado" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="valorPrevisto" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         <element name="valorRealizado" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="observacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Parcela", namespace = "Parcela", propOrder = {
    "numero",
    "dataDesconto",
    "dataRealizado",
    "valorPrevisto",
    "valorRealizado",
    "status",
    "observacao"
})
public class Parcela {

    @XmlElement(namespace = "Parcela")
    protected int numero;
    @XmlElement(namespace = "Parcela", required = true)
    protected java.lang.String dataDesconto;
    @XmlElement(namespace = "Parcela")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataRealizado;
    @XmlElement(namespace = "Parcela")
    protected double valorPrevisto;
    @XmlElement(namespace = "Parcela")
    protected java.lang.Double valorRealizado;
    @XmlElement(namespace = "Parcela", required = true)
    protected java.lang.String status;
    @XmlElement(namespace = "Parcela")
    protected java.lang.String observacao;

    /**
     * Obtém o valor da propriedade numero.
     * 
     */
    public int getNumero() {
        return numero;
    }

    /**
     * Define o valor da propriedade numero.
     * 
     */
    public void setNumero(int value) {
        this.numero = value;
    }

    /**
     * Obtém o valor da propriedade dataDesconto.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getDataDesconto() {
        return dataDesconto;
    }

    /**
     * Define o valor da propriedade dataDesconto.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setDataDesconto(java.lang.String value) {
        this.dataDesconto = value;
    }

    /**
     * Obtém o valor da propriedade dataRealizado.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataRealizado() {
        return dataRealizado;
    }

    /**
     * Define o valor da propriedade dataRealizado.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataRealizado(XMLGregorianCalendar value) {
        this.dataRealizado = value;
    }

    /**
     * Obtém o valor da propriedade valorPrevisto.
     * 
     */
    public double getValorPrevisto() {
        return valorPrevisto;
    }

    /**
     * Define o valor da propriedade valorPrevisto.
     * 
     */
    public void setValorPrevisto(double value) {
        this.valorPrevisto = value;
    }

    /**
     * Obtém o valor da propriedade valorRealizado.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Double }
     *     
     */
    public java.lang.Double getValorRealizado() {
        return valorRealizado;
    }

    /**
     * Define o valor da propriedade valorRealizado.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Double }
     *     
     */
    public void setValorRealizado(java.lang.Double value) {
        this.valorRealizado = value;
    }

    /**
     * Obtém o valor da propriedade status.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getStatus() {
        return status;
    }

    /**
     * Define o valor da propriedade status.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setStatus(java.lang.String value) {
        this.status = value;
    }

    /**
     * Obtém o valor da propriedade observacao.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getObservacao() {
        return observacao;
    }

    /**
     * Define o valor da propriedade observacao.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setObservacao(java.lang.String value) {
        this.observacao = value;
    }

}
