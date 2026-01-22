//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.folha.v1;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de MovimentoFinanceiro complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="MovimentoFinanceiro">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="periodo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="servidor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="matricula" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="estabelecimentoCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="estabelecimento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="orgaoCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="orgao" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="consignatariaCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="consignataria" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="servicoCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="servico" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="codVerba" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="dataReserva" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         <element name="dataInicial" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         <element name="dataFinal" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         <element name="valorParcela" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         <element name="prazo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="pagas" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="adeNumero" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         <element name="indice" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="operacao" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MovimentoFinanceiro", namespace = "MovimentoFinanceiro", propOrder = {
    "periodo",
    "servidor",
    "cpf",
    "matricula",
    "estabelecimentoCodigo",
    "estabelecimento",
    "orgaoCodigo",
    "orgao",
    "consignatariaCodigo",
    "consignataria",
    "servicoCodigo",
    "servico",
    "codVerba",
    "dataReserva",
    "dataInicial",
    "dataFinal",
    "valorParcela",
    "prazo",
    "pagas",
    "adeNumero",
    "indice",
    "operacao"
})
public class MovimentoFinanceiro {

    @XmlElement(namespace = "MovimentoFinanceiro", required = true)
    protected java.lang.String periodo;
    @XmlElement(namespace = "MovimentoFinanceiro", required = true)
    protected java.lang.String servidor;
    @XmlElement(namespace = "MovimentoFinanceiro", required = true, nillable = true)
    protected java.lang.String cpf;
    @XmlElement(namespace = "MovimentoFinanceiro", required = true)
    protected java.lang.String matricula;
    @XmlElement(namespace = "MovimentoFinanceiro", required = true)
    protected java.lang.String estabelecimentoCodigo;
    @XmlElement(namespace = "MovimentoFinanceiro", required = true)
    protected java.lang.String estabelecimento;
    @XmlElement(namespace = "MovimentoFinanceiro", required = true)
    protected java.lang.String orgaoCodigo;
    @XmlElement(namespace = "MovimentoFinanceiro", required = true)
    protected java.lang.String orgao;
    @XmlElement(namespace = "MovimentoFinanceiro", required = true)
    protected java.lang.String consignatariaCodigo;
    @XmlElement(namespace = "MovimentoFinanceiro", required = true)
    protected java.lang.String consignataria;
    @XmlElement(namespace = "MovimentoFinanceiro", required = true)
    protected java.lang.String servicoCodigo;
    @XmlElement(namespace = "MovimentoFinanceiro", required = true)
    protected java.lang.String servico;
    @XmlElement(namespace = "MovimentoFinanceiro", required = true)
    protected java.lang.String codVerba;
    @XmlElement(namespace = "MovimentoFinanceiro", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataReserva;
    @XmlElement(namespace = "MovimentoFinanceiro", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataInicial;
    @XmlElement(namespace = "MovimentoFinanceiro", required = true, nillable = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataFinal;
    @XmlElement(namespace = "MovimentoFinanceiro")
    protected double valorParcela;
    @XmlElement(namespace = "MovimentoFinanceiro", required = true, type = java.lang.Integer.class, nillable = true)
    protected java.lang.Integer prazo;
    @XmlElement(namespace = "MovimentoFinanceiro", required = true, type = java.lang.Integer.class, nillable = true)
    protected java.lang.Integer pagas;
    @XmlElement(namespace = "MovimentoFinanceiro")
    protected long adeNumero;
    @XmlElement(namespace = "MovimentoFinanceiro", required = true, nillable = true)
    protected java.lang.String indice;
    @XmlElement(namespace = "MovimentoFinanceiro", required = true)
    protected java.lang.String operacao;

    /**
     * Obtém o valor da propriedade periodo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getPeriodo() {
        return periodo;
    }

    /**
     * Define o valor da propriedade periodo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setPeriodo(java.lang.String value) {
        this.periodo = value;
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
     * Obtém o valor da propriedade matricula.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getMatricula() {
        return matricula;
    }

    /**
     * Define o valor da propriedade matricula.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setMatricula(java.lang.String value) {
        this.matricula = value;
    }

    /**
     * Obtém o valor da propriedade estabelecimentoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getEstabelecimentoCodigo() {
        return estabelecimentoCodigo;
    }

    /**
     * Define o valor da propriedade estabelecimentoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setEstabelecimentoCodigo(java.lang.String value) {
        this.estabelecimentoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade estabelecimento.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getEstabelecimento() {
        return estabelecimento;
    }

    /**
     * Define o valor da propriedade estabelecimento.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setEstabelecimento(java.lang.String value) {
        this.estabelecimento = value;
    }

    /**
     * Obtém o valor da propriedade orgaoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getOrgaoCodigo() {
        return orgaoCodigo;
    }

    /**
     * Define o valor da propriedade orgaoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setOrgaoCodigo(java.lang.String value) {
        this.orgaoCodigo = value;
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
     * Obtém o valor da propriedade prazo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Integer }
     *     
     */
    public java.lang.Integer getPrazo() {
        return prazo;
    }

    /**
     * Define o valor da propriedade prazo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Integer }
     *     
     */
    public void setPrazo(java.lang.Integer value) {
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
     * Obtém o valor da propriedade operacao.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getOperacao() {
        return operacao;
    }

    /**
     * Define o valor da propriedade operacao.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setOperacao(java.lang.String value) {
        this.operacao = value;
    }

}
