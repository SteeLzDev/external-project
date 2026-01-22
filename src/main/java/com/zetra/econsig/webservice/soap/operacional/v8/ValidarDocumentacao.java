//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.5 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v8;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de ValidarDocumentacao complex type.</p>
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.</p>
 * 
 * <pre>{@code
 * <complexType name="ValidarDocumentacao">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="consignataria" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="consignatariaCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="responsavel" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="adeNumero" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="adeIdentificador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="dataReserva" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         <element name="servico" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="codVerba" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="orgao" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="dataInicial" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         <element name="dataFinal" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         <element name="valorParcela" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         <element name="valorLiberado" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         <element name="valorDevido" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         <element name="prazo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="servidor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="quantValidacao" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="origem" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="nomeArquivoRGFrente" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="nomeArquivoRGVerso" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="nomeArquivoMandadoPag" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="nomeArquivoContraCheque" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="nomeArquivoOutro" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="observacao" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValidarDocumentacao", namespace = "ValidarDocumentacao", propOrder = {
    "consignataria",
    "consignatariaCodigo",
    "responsavel",
    "adeNumero",
    "adeIdentificador",
    "dataReserva",
    "servico",
    "codVerba",
    "orgao",
    "dataInicial",
    "dataFinal",
    "valorParcela",
    "valorLiberado",
    "valorDevido",
    "prazo",
    "servidor",
    "cpf",
    "quantValidacao",
    "origem",
    "nomeArquivoRGFrente",
    "nomeArquivoRGVerso",
    "nomeArquivoMandadoPag",
    "nomeArquivoContraCheque",
    "nomeArquivoOutro",
    "observacao"
})
public class ValidarDocumentacao {

    @XmlElement(namespace = "ValidarDocumentacao", required = true)
    protected java.lang.String consignataria;
    @XmlElement(namespace = "ValidarDocumentacao", required = true)
    protected java.lang.String consignatariaCodigo;
    @XmlElement(namespace = "ValidarDocumentacao", required = true)
    protected java.lang.String responsavel;
    @XmlElement(namespace = "ValidarDocumentacao", required = true)
    protected java.lang.String adeNumero;
    @XmlElement(namespace = "ValidarDocumentacao", required = true)
    protected java.lang.String adeIdentificador;
    @XmlElement(namespace = "ValidarDocumentacao", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataReserva;
    @XmlElement(namespace = "ValidarDocumentacao", required = true)
    protected java.lang.String servico;
    @XmlElement(namespace = "ValidarDocumentacao", required = true)
    protected java.lang.String codVerba;
    @XmlElement(namespace = "ValidarDocumentacao", required = true)
    protected java.lang.String orgao;
    @XmlElement(namespace = "ValidarDocumentacao", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataInicial;
    @XmlElement(namespace = "ValidarDocumentacao", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataFinal;
    @XmlElement(namespace = "ValidarDocumentacao")
    protected double valorParcela;
    @XmlElement(namespace = "ValidarDocumentacao")
    protected double valorLiberado;
    @XmlElement(namespace = "ValidarDocumentacao")
    protected double valorDevido;
    @XmlElement(namespace = "ValidarDocumentacao")
    protected int prazo;
    @XmlElement(namespace = "ValidarDocumentacao", required = true)
    protected java.lang.String servidor;
    @XmlElement(namespace = "ValidarDocumentacao", required = true)
    protected java.lang.String cpf;
    @XmlElement(namespace = "ValidarDocumentacao")
    protected int quantValidacao;
    @XmlElement(namespace = "ValidarDocumentacao", required = true)
    protected java.lang.String origem;
    @XmlElement(namespace = "ValidarDocumentacao", required = true, nillable = true)
    protected java.lang.String nomeArquivoRGFrente;
    @XmlElement(namespace = "ValidarDocumentacao", required = true, nillable = true)
    protected java.lang.String nomeArquivoRGVerso;
    @XmlElement(namespace = "ValidarDocumentacao", required = true, nillable = true)
    protected java.lang.String nomeArquivoMandadoPag;
    @XmlElement(namespace = "ValidarDocumentacao", required = true, nillable = true)
    protected java.lang.String nomeArquivoContraCheque;
    @XmlElement(namespace = "ValidarDocumentacao", required = true, nillable = true)
    protected java.lang.String nomeArquivoOutro;
    @XmlElement(namespace = "ValidarDocumentacao", required = true)
    protected java.lang.String observacao;

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
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getConsignatariaCodigo() {
        return consignatariaCodigo;
    }

    /**
     * Define o valor da propriedade consignatariaCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setConsignatariaCodigo(java.lang.String value) {
        this.consignatariaCodigo = value;
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
     * Obtém o valor da propriedade adeNumero.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getAdeNumero() {
        return adeNumero;
    }

    /**
     * Define o valor da propriedade adeNumero.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setAdeNumero(java.lang.String value) {
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
     * Obtém o valor da propriedade orgao.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getOrgao() {
        return orgao;
    }

    /**
     * Define o valor da propriedade orgao.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setOrgao(java.lang.String value) {
        this.orgao = value;
    }

    /**
     * Obtém o valor da propriedade dataInicial.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataInicial() {
        return dataInicial;
    }

    /**
     * Define o valor da propriedade dataInicial.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataInicial(XMLGregorianCalendar value) {
        this.dataInicial = value;
    }

    /**
     * Obtém o valor da propriedade dataFinal.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataFinal() {
        return dataFinal;
    }

    /**
     * Define o valor da propriedade dataFinal.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataFinal(XMLGregorianCalendar value) {
        this.dataFinal = value;
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
     * Obtém o valor da propriedade valorDevido.
     * 
     */
    public double getValorDevido() {
        return valorDevido;
    }

    /**
     * Define o valor da propriedade valorDevido.
     * 
     */
    public void setValorDevido(double value) {
        this.valorDevido = value;
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
     * Obtém o valor da propriedade servidor.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getServidor() {
        return servidor;
    }

    /**
     * Define o valor da propriedade servidor.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setServidor(java.lang.String value) {
        this.servidor = value;
    }

    /**
     * Obtém o valor da propriedade cpf.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getCpf() {
        return cpf;
    }

    /**
     * Define o valor da propriedade cpf.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setCpf(java.lang.String value) {
        this.cpf = value;
    }

    /**
     * Obtém o valor da propriedade quantValidacao.
     * 
     */
    public int getQuantValidacao() {
        return quantValidacao;
    }

    /**
     * Define o valor da propriedade quantValidacao.
     * 
     */
    public void setQuantValidacao(int value) {
        this.quantValidacao = value;
    }

    /**
     * Obtém o valor da propriedade origem.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getOrigem() {
        return origem;
    }

    /**
     * Define o valor da propriedade origem.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setOrigem(java.lang.String value) {
        this.origem = value;
    }

    /**
     * Obtém o valor da propriedade nomeArquivoRGFrente.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getNomeArquivoRGFrente() {
        return nomeArquivoRGFrente;
    }

    /**
     * Define o valor da propriedade nomeArquivoRGFrente.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setNomeArquivoRGFrente(java.lang.String value) {
        this.nomeArquivoRGFrente = value;
    }

    /**
     * Obtém o valor da propriedade nomeArquivoRGVerso.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getNomeArquivoRGVerso() {
        return nomeArquivoRGVerso;
    }

    /**
     * Define o valor da propriedade nomeArquivoRGVerso.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setNomeArquivoRGVerso(java.lang.String value) {
        this.nomeArquivoRGVerso = value;
    }

    /**
     * Obtém o valor da propriedade nomeArquivoMandadoPag.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getNomeArquivoMandadoPag() {
        return nomeArquivoMandadoPag;
    }

    /**
     * Define o valor da propriedade nomeArquivoMandadoPag.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setNomeArquivoMandadoPag(java.lang.String value) {
        this.nomeArquivoMandadoPag = value;
    }

    /**
     * Obtém o valor da propriedade nomeArquivoContraCheque.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getNomeArquivoContraCheque() {
        return nomeArquivoContraCheque;
    }

    /**
     * Define o valor da propriedade nomeArquivoContraCheque.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setNomeArquivoContraCheque(java.lang.String value) {
        this.nomeArquivoContraCheque = value;
    }

    /**
     * Obtém o valor da propriedade nomeArquivoOutro.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getNomeArquivoOutro() {
        return nomeArquivoOutro;
    }

    /**
     * Define o valor da propriedade nomeArquivoOutro.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setNomeArquivoOutro(java.lang.String value) {
        this.nomeArquivoOutro = value;
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
