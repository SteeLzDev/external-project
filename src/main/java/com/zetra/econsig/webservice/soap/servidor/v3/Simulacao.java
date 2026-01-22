//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.servidor.v3;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Simulacao complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="Simulacao">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="consignatariaCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="consignataria" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="valorLiberado" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         <element name="valorParcela" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         <element name="ranking" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         <element name="taxaJuros" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         <element name="servico" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="servicoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Simulacao", namespace = "Simulacao", propOrder = {
    "consignatariaCodigo",
    "consignataria",
    "valorLiberado",
    "valorParcela",
    "ranking",
    "taxaJuros",
    "servico",
    "servicoCodigo"
})
public class Simulacao {

    @XmlElement(namespace = "Simulacao", required = true)
    protected String consignatariaCodigo;
    @XmlElement(namespace = "Simulacao", required = true)
    protected String consignataria;
    @XmlElement(namespace = "Simulacao")
    protected double valorLiberado;
    @XmlElement(namespace = "Simulacao")
    protected double valorParcela;
    @XmlElement(namespace = "Simulacao")
    protected short ranking;
    @XmlElement(namespace = "Simulacao")
    protected double taxaJuros;
    @XmlElementRef(name = "servico", namespace = "Simulacao", type = JAXBElement.class, required = false)
    protected JAXBElement<String> servico;
    @XmlElementRef(name = "servicoCodigo", namespace = "Simulacao", type = JAXBElement.class, required = false)
    protected JAXBElement<String> servicoCodigo;

    /**
     * Obtém o valor da propriedade consignatariaCodigo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConsignatariaCodigo() {
        return consignatariaCodigo;
    }

    /**
     * Define o valor da propriedade consignatariaCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConsignatariaCodigo(String value) {
        this.consignatariaCodigo = value;
    }

    /**
     * Obtém o valor da propriedade consignataria.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConsignataria() {
        return consignataria;
    }

    /**
     * Define o valor da propriedade consignataria.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConsignataria(String value) {
        this.consignataria = value;
    }

    /**
     * Obtém o valor da propriedade valorLiberado.
     * 
     */
    public double getValorLiberado() {
        return valorLiberado;
    }

    /**
     * Define o valor da propriedade valorLiberado.
     * 
     */
    public void setValorLiberado(double value) {
        this.valorLiberado = value;
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
     * Obtém o valor da propriedade ranking.
     * 
     */
    public short getRanking() {
        return ranking;
    }

    /**
     * Define o valor da propriedade ranking.
     * 
     */
    public void setRanking(short value) {
        this.ranking = value;
    }

    /**
     * Obtém o valor da propriedade taxaJuros.
     * 
     */
    public double getTaxaJuros() {
        return taxaJuros;
    }

    /**
     * Define o valor da propriedade taxaJuros.
     * 
     */
    public void setTaxaJuros(double value) {
        this.taxaJuros = value;
    }

    /**
     * Obtém o valor da propriedade servico.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getServico() {
        return servico;
    }

    /**
     * Define o valor da propriedade servico.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setServico(JAXBElement<String> value) {
        this.servico = value;
    }

    /**
     * Obtém o valor da propriedade servicoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getServicoCodigo() {
        return servicoCodigo;
    }

    /**
     * Define o valor da propriedade servicoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setServicoCodigo(JAXBElement<String> value) {
        this.servicoCodigo = value;
    }

}
