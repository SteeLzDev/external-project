//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v8;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de ParametroSet complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="ParametroSet">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="svcDescricao" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="tamMinMatriculaServidor" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="tamMaxMatriculaServidor" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="qtdMaxParcelas" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="exigeCpfMatriculaPesquisa" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="validaCpfPesquisa" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="exigeTac" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="exigeCadMensVinc" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="exigeCadVlrLiberado" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="exigeIof" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="validaDataNascReserva" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="exigeInfoBancaria" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="validaInfoBancaria" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="alteraAutMargemNegativa" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="exigeSenhaServReservarRenegociar" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="exigeSenhaServConsMargem" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="exigeSenhaServAltContrato" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="permiteAltContrato" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="permiteRenegociarContrato" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="qtdMinPrdPgsParaRenegociarAut" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="visualizaMargem" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="visualizaMargemNegativa" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="diaCorte" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         <element name="periodoAtual" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         <element name="permiteCompraContrato" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="diasInfoSaldoDevedor" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         <element name="diasAprovSaldoDevedor" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         <element name="diasInfoPgSaldoDevedor" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         <element name="diasLiquidacaoAdeCompra" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         <element name="usaCet" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParametroSet", namespace = "ParametroSet", propOrder = {
    "svcDescricao",
    "tamMinMatriculaServidor",
    "tamMaxMatriculaServidor",
    "qtdMaxParcelas",
    "exigeCpfMatriculaPesquisa",
    "validaCpfPesquisa",
    "exigeTac",
    "exigeCadMensVinc",
    "exigeCadVlrLiberado",
    "exigeIof",
    "validaDataNascReserva",
    "exigeInfoBancaria",
    "validaInfoBancaria",
    "alteraAutMargemNegativa",
    "exigeSenhaServReservarRenegociar",
    "exigeSenhaServConsMargem",
    "exigeSenhaServAltContrato",
    "permiteAltContrato",
    "permiteRenegociarContrato",
    "qtdMinPrdPgsParaRenegociarAut",
    "visualizaMargem",
    "visualizaMargemNegativa",
    "diaCorte",
    "periodoAtual",
    "permiteCompraContrato",
    "diasInfoSaldoDevedor",
    "diasAprovSaldoDevedor",
    "diasInfoPgSaldoDevedor",
    "diasLiquidacaoAdeCompra",
    "usaCet"
})
public class ParametroSet {

    @XmlElement(namespace = "ParametroSet", required = true, nillable = true)
    protected java.lang.String svcDescricao;
    @XmlElement(namespace = "ParametroSet", required = true, type = java.lang.Integer.class, nillable = true)
    protected java.lang.Integer tamMinMatriculaServidor;
    @XmlElement(namespace = "ParametroSet", required = true, type = java.lang.Integer.class, nillable = true)
    protected java.lang.Integer tamMaxMatriculaServidor;
    @XmlElementRef(name = "qtdMaxParcelas", namespace = "ParametroSet", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Integer> qtdMaxParcelas;
    @XmlElement(namespace = "ParametroSet", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean exigeCpfMatriculaPesquisa;
    @XmlElement(namespace = "ParametroSet", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean validaCpfPesquisa;
    @XmlElement(namespace = "ParametroSet", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean exigeTac;
    @XmlElement(namespace = "ParametroSet", type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean exigeCadMensVinc;
    @XmlElement(namespace = "ParametroSet", type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean exigeCadVlrLiberado;
    @XmlElement(namespace = "ParametroSet", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean exigeIof;
    @XmlElement(namespace = "ParametroSet", type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean validaDataNascReserva;
    @XmlElement(namespace = "ParametroSet", type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean exigeInfoBancaria;
    @XmlElement(namespace = "ParametroSet", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean validaInfoBancaria;
    @XmlElement(namespace = "ParametroSet", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean alteraAutMargemNegativa;
    @XmlElement(namespace = "ParametroSet", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean exigeSenhaServReservarRenegociar;
    @XmlElement(namespace = "ParametroSet", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean exigeSenhaServConsMargem;
    @XmlElement(namespace = "ParametroSet", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean exigeSenhaServAltContrato;
    @XmlElement(namespace = "ParametroSet", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean permiteAltContrato;
    @XmlElement(namespace = "ParametroSet", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean permiteRenegociarContrato;
    @XmlElement(namespace = "ParametroSet", required = true, type = java.lang.Integer.class, nillable = true)
    protected java.lang.Integer qtdMinPrdPgsParaRenegociarAut;
    @XmlElement(namespace = "ParametroSet", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean visualizaMargem;
    @XmlElement(namespace = "ParametroSet", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean visualizaMargemNegativa;
    @XmlElement(namespace = "ParametroSet", required = true, type = java.lang.Short.class, nillable = true)
    protected java.lang.Short diaCorte;
    @XmlElement(namespace = "ParametroSet", required = true, nillable = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar periodoAtual;
    @XmlElement(namespace = "ParametroSet", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean permiteCompraContrato;
    @XmlElementRef(name = "diasInfoSaldoDevedor", namespace = "ParametroSet", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Short> diasInfoSaldoDevedor;
    @XmlElementRef(name = "diasAprovSaldoDevedor", namespace = "ParametroSet", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Short> diasAprovSaldoDevedor;
    @XmlElementRef(name = "diasInfoPgSaldoDevedor", namespace = "ParametroSet", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Short> diasInfoPgSaldoDevedor;
    @XmlElementRef(name = "diasLiquidacaoAdeCompra", namespace = "ParametroSet", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Short> diasLiquidacaoAdeCompra;
    @XmlElementRef(name = "usaCet", namespace = "ParametroSet", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Boolean> usaCet;

    /**
     * Obtém o valor da propriedade svcDescricao.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getSvcDescricao() {
        return svcDescricao;
    }

    /**
     * Define o valor da propriedade svcDescricao.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setSvcDescricao(java.lang.String value) {
        this.svcDescricao = value;
    }

    /**
     * Obtém o valor da propriedade tamMinMatriculaServidor.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Integer }
     *     
     */
    public java.lang.Integer getTamMinMatriculaServidor() {
        return tamMinMatriculaServidor;
    }

    /**
     * Define o valor da propriedade tamMinMatriculaServidor.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Integer }
     *     
     */
    public void setTamMinMatriculaServidor(java.lang.Integer value) {
        this.tamMinMatriculaServidor = value;
    }

    /**
     * Obtém o valor da propriedade tamMaxMatriculaServidor.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Integer }
     *     
     */
    public java.lang.Integer getTamMaxMatriculaServidor() {
        return tamMaxMatriculaServidor;
    }

    /**
     * Define o valor da propriedade tamMaxMatriculaServidor.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Integer }
     *     
     */
    public void setTamMaxMatriculaServidor(java.lang.Integer value) {
        this.tamMaxMatriculaServidor = value;
    }

    /**
     * Obtém o valor da propriedade qtdMaxParcelas.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *     
     */
    public JAXBElement<java.lang.Integer> getQtdMaxParcelas() {
        return qtdMaxParcelas;
    }

    /**
     * Define o valor da propriedade qtdMaxParcelas.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *     
     */
    public void setQtdMaxParcelas(JAXBElement<java.lang.Integer> value) {
        this.qtdMaxParcelas = value;
    }

    /**
     * Obtém o valor da propriedade exigeCpfMatriculaPesquisa.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getExigeCpfMatriculaPesquisa() {
        return exigeCpfMatriculaPesquisa;
    }

    /**
     * Define o valor da propriedade exigeCpfMatriculaPesquisa.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setExigeCpfMatriculaPesquisa(java.lang.Boolean value) {
        this.exigeCpfMatriculaPesquisa = value;
    }

    /**
     * Obtém o valor da propriedade validaCpfPesquisa.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getValidaCpfPesquisa() {
        return validaCpfPesquisa;
    }

    /**
     * Define o valor da propriedade validaCpfPesquisa.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setValidaCpfPesquisa(java.lang.Boolean value) {
        this.validaCpfPesquisa = value;
    }

    /**
     * Obtém o valor da propriedade exigeTac.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getExigeTac() {
        return exigeTac;
    }

    /**
     * Define o valor da propriedade exigeTac.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setExigeTac(java.lang.Boolean value) {
        this.exigeTac = value;
    }

    /**
     * Obtém o valor da propriedade exigeCadMensVinc.
     * 
     */
    public java.lang.Boolean isExigeCadMensVinc() {
        return exigeCadMensVinc;
    }

    /**
     * Define o valor da propriedade exigeCadMensVinc.
     * 
     */
    public void setExigeCadMensVinc(java.lang.Boolean value) {
        this.exigeCadMensVinc = value;
    }

    /**
     * Obtém o valor da propriedade exigeCadVlrLiberado.
     * 
     */
    public java.lang.Boolean isExigeCadVlrLiberado() {
        return exigeCadVlrLiberado;
    }

    /**
     * Define o valor da propriedade exigeCadVlrLiberado.
     * 
     */
    public void setExigeCadVlrLiberado(java.lang.Boolean value) {
        this.exigeCadVlrLiberado = value;
    }

    /**
     * Obtém o valor da propriedade exigeIof.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getExigeIof() {
        return exigeIof;
    }

    /**
     * Define o valor da propriedade exigeIof.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setExigeIof(java.lang.Boolean value) {
        this.exigeIof = value;
    }

    /**
     * Obtém o valor da propriedade validaDataNascReserva.
     * 
     */
    public java.lang.Boolean isValidaDataNascReserva() {
        return validaDataNascReserva;
    }

    /**
     * Define o valor da propriedade validaDataNascReserva.
     * 
     */
    public void setValidaDataNascReserva(java.lang.Boolean value) {
        this.validaDataNascReserva = value;
    }

    /**
     * Obtém o valor da propriedade exigeInfoBancaria.
     * 
     */
    public java.lang.Boolean isExigeInfoBancaria() {
        return exigeInfoBancaria;
    }

    /**
     * Define o valor da propriedade exigeInfoBancaria.
     * 
     */
    public void setExigeInfoBancaria(java.lang.Boolean value) {
        this.exigeInfoBancaria = value;
    }

    /**
     * Obtém o valor da propriedade validaInfoBancaria.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getValidaInfoBancaria() {
        return validaInfoBancaria;
    }

    /**
     * Define o valor da propriedade validaInfoBancaria.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setValidaInfoBancaria(java.lang.Boolean value) {
        this.validaInfoBancaria = value;
    }

    /**
     * Obtém o valor da propriedade alteraAutMargemNegativa.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getAlteraAutMargemNegativa() {
        return alteraAutMargemNegativa;
    }

    /**
     * Define o valor da propriedade alteraAutMargemNegativa.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setAlteraAutMargemNegativa(java.lang.Boolean value) {
        this.alteraAutMargemNegativa = value;
    }

    /**
     * Obtém o valor da propriedade exigeSenhaServReservarRenegociar.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getExigeSenhaServReservarRenegociar() {
        return exigeSenhaServReservarRenegociar;
    }

    /**
     * Define o valor da propriedade exigeSenhaServReservarRenegociar.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setExigeSenhaServReservarRenegociar(java.lang.Boolean value) {
        this.exigeSenhaServReservarRenegociar = value;
    }

    /**
     * Obtém o valor da propriedade exigeSenhaServConsMargem.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getExigeSenhaServConsMargem() {
        return exigeSenhaServConsMargem;
    }

    /**
     * Define o valor da propriedade exigeSenhaServConsMargem.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setExigeSenhaServConsMargem(java.lang.Boolean value) {
        this.exigeSenhaServConsMargem = value;
    }

    /**
     * Obtém o valor da propriedade exigeSenhaServAltContrato.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getExigeSenhaServAltContrato() {
        return exigeSenhaServAltContrato;
    }

    /**
     * Define o valor da propriedade exigeSenhaServAltContrato.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setExigeSenhaServAltContrato(java.lang.Boolean value) {
        this.exigeSenhaServAltContrato = value;
    }

    /**
     * Obtém o valor da propriedade permiteAltContrato.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getPermiteAltContrato() {
        return permiteAltContrato;
    }

    /**
     * Define o valor da propriedade permiteAltContrato.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setPermiteAltContrato(java.lang.Boolean value) {
        this.permiteAltContrato = value;
    }

    /**
     * Obtém o valor da propriedade permiteRenegociarContrato.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getPermiteRenegociarContrato() {
        return permiteRenegociarContrato;
    }

    /**
     * Define o valor da propriedade permiteRenegociarContrato.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setPermiteRenegociarContrato(java.lang.Boolean value) {
        this.permiteRenegociarContrato = value;
    }

    /**
     * Obtém o valor da propriedade qtdMinPrdPgsParaRenegociarAut.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Integer }
     *     
     */
    public java.lang.Integer getQtdMinPrdPgsParaRenegociarAut() {
        return qtdMinPrdPgsParaRenegociarAut;
    }

    /**
     * Define o valor da propriedade qtdMinPrdPgsParaRenegociarAut.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Integer }
     *     
     */
    public void setQtdMinPrdPgsParaRenegociarAut(java.lang.Integer value) {
        this.qtdMinPrdPgsParaRenegociarAut = value;
    }

    /**
     * Obtém o valor da propriedade visualizaMargem.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getVisualizaMargem() {
        return visualizaMargem;
    }

    /**
     * Define o valor da propriedade visualizaMargem.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setVisualizaMargem(java.lang.Boolean value) {
        this.visualizaMargem = value;
    }

    /**
     * Obtém o valor da propriedade visualizaMargemNegativa.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getVisualizaMargemNegativa() {
        return visualizaMargemNegativa;
    }

    /**
     * Define o valor da propriedade visualizaMargemNegativa.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setVisualizaMargemNegativa(java.lang.Boolean value) {
        this.visualizaMargemNegativa = value;
    }

    /**
     * Obtém o valor da propriedade diaCorte.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Short }
     *     
     */
    public java.lang.Short getDiaCorte() {
        return diaCorte;
    }

    /**
     * Define o valor da propriedade diaCorte.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Short }
     *     
     */
    public void setDiaCorte(java.lang.Short value) {
        this.diaCorte = value;
    }

    /**
     * Obtém o valor da propriedade periodoAtual.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPeriodoAtual() {
        return periodoAtual;
    }

    /**
     * Define o valor da propriedade periodoAtual.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPeriodoAtual(XMLGregorianCalendar value) {
        this.periodoAtual = value;
    }

    /**
     * Obtém o valor da propriedade permiteCompraContrato.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getPermiteCompraContrato() {
        return permiteCompraContrato;
    }

    /**
     * Define o valor da propriedade permiteCompraContrato.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setPermiteCompraContrato(java.lang.Boolean value) {
        this.permiteCompraContrato = value;
    }

    /**
     * Obtém o valor da propriedade diasInfoSaldoDevedor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public JAXBElement<java.lang.Short> getDiasInfoSaldoDevedor() {
        return diasInfoSaldoDevedor;
    }

    /**
     * Define o valor da propriedade diasInfoSaldoDevedor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public void setDiasInfoSaldoDevedor(JAXBElement<java.lang.Short> value) {
        this.diasInfoSaldoDevedor = value;
    }

    /**
     * Obtém o valor da propriedade diasAprovSaldoDevedor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public JAXBElement<java.lang.Short> getDiasAprovSaldoDevedor() {
        return diasAprovSaldoDevedor;
    }

    /**
     * Define o valor da propriedade diasAprovSaldoDevedor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public void setDiasAprovSaldoDevedor(JAXBElement<java.lang.Short> value) {
        this.diasAprovSaldoDevedor = value;
    }

    /**
     * Obtém o valor da propriedade diasInfoPgSaldoDevedor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public JAXBElement<java.lang.Short> getDiasInfoPgSaldoDevedor() {
        return diasInfoPgSaldoDevedor;
    }

    /**
     * Define o valor da propriedade diasInfoPgSaldoDevedor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public void setDiasInfoPgSaldoDevedor(JAXBElement<java.lang.Short> value) {
        this.diasInfoPgSaldoDevedor = value;
    }

    /**
     * Obtém o valor da propriedade diasLiquidacaoAdeCompra.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public JAXBElement<java.lang.Short> getDiasLiquidacaoAdeCompra() {
        return diasLiquidacaoAdeCompra;
    }

    /**
     * Define o valor da propriedade diasLiquidacaoAdeCompra.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public void setDiasLiquidacaoAdeCompra(JAXBElement<java.lang.Short> value) {
        this.diasLiquidacaoAdeCompra = value;
    }

    /**
     * Obtém o valor da propriedade usaCet.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public JAXBElement<java.lang.Boolean> getUsaCet() {
        return usaCet;
    }

    /**
     * Define o valor da propriedade usaCet.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public void setUsaCet(JAXBElement<java.lang.Boolean> value) {
        this.usaCet = value;
    }

}
