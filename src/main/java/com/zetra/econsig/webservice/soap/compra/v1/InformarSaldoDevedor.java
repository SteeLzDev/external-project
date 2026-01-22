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
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de anonymous complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="cliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="convenio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="usuario" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="senha" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="adeNumero" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         <element name="valorSaldoDevedor" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         <element name="dataVencimento" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="valorSaldoDevedor2" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="dataVencimento2" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="valorSaldoDevedor3" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="dataVencimento3" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="numeroPrestacoes" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         <element name="banco" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="agencia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="conta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nomeFavorecido" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cnpjFavorecido" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="numeroContrato" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         <element name="linkBoleto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="observacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="detalheSaldoDevedor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="anexoDsdSaldoCompra" type="{Anexo}Anexo" minOccurs="0"/>
 *         <element name="anexoBoletoDsdSaldo" type="{Anexo}Anexo" minOccurs="0"/>
 *         <element name="propostaRefinanciamento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "cliente",
    "convenio",
    "usuario",
    "senha",
    "adeNumero",
    "valorSaldoDevedor",
    "dataVencimento",
    "valorSaldoDevedor2",
    "dataVencimento2",
    "valorSaldoDevedor3",
    "dataVencimento3",
    "numeroPrestacoes",
    "banco",
    "agencia",
    "conta",
    "nomeFavorecido",
    "cnpjFavorecido",
    "numeroContrato",
    "linkBoleto",
    "observacao",
    "detalheSaldoDevedor",
    "anexoDsdSaldoCompra",
    "anexoBoletoDsdSaldo",
    "propostaRefinanciamento"
})
@XmlRootElement(name = "informarSaldoDevedor", namespace = "CompraService-v1_0")
public class InformarSaldoDevedor {

    @XmlElementRef(name = "cliente", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cliente;
    @XmlElementRef(name = "convenio", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> convenio;
    @XmlElement(namespace = "CompraService-v1_0", required = true)
    protected java.lang.String usuario;
    @XmlElement(namespace = "CompraService-v1_0", required = true)
    protected java.lang.String senha;
    @XmlElement(namespace = "CompraService-v1_0")
    protected long adeNumero;
    @XmlElement(namespace = "CompraService-v1_0")
    protected double valorSaldoDevedor;
    @XmlElementRef(name = "dataVencimento", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataVencimento;
    @XmlElementRef(name = "valorSaldoDevedor2", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> valorSaldoDevedor2;
    @XmlElementRef(name = "dataVencimento2", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataVencimento2;
    @XmlElementRef(name = "valorSaldoDevedor3", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> valorSaldoDevedor3;
    @XmlElementRef(name = "dataVencimento3", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataVencimento3;
    @XmlElementRef(name = "numeroPrestacoes", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Short> numeroPrestacoes;
    @XmlElementRef(name = "banco", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> banco;
    @XmlElementRef(name = "agencia", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> agencia;
    @XmlElementRef(name = "conta", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> conta;
    @XmlElementRef(name = "nomeFavorecido", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nomeFavorecido;
    @XmlElementRef(name = "cnpjFavorecido", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cnpjFavorecido;
    @XmlElementRef(name = "numeroContrato", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Long> numeroContrato;
    @XmlElementRef(name = "linkBoleto", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> linkBoleto;
    @XmlElementRef(name = "observacao", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> observacao;
    @XmlElementRef(name = "detalheSaldoDevedor", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> detalheSaldoDevedor;
    @XmlElementRef(name = "anexoDsdSaldoCompra", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<Anexo> anexoDsdSaldoCompra;
    @XmlElementRef(name = "anexoBoletoDsdSaldo", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<Anexo> anexoBoletoDsdSaldo;
    @XmlElementRef(name = "propostaRefinanciamento", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> propostaRefinanciamento;

    /**
     * Obtém o valor da propriedade cliente.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCliente() {
        return cliente;
    }

    /**
     * Define o valor da propriedade cliente.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCliente(JAXBElement<java.lang.String> value) {
        this.cliente = value;
    }

    /**
     * Obtém o valor da propriedade convenio.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getConvenio() {
        return convenio;
    }

    /**
     * Define o valor da propriedade convenio.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setConvenio(JAXBElement<java.lang.String> value) {
        this.convenio = value;
    }

    /**
     * Obtém o valor da propriedade usuario.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getUsuario() {
        return usuario;
    }

    /**
     * Define o valor da propriedade usuario.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setUsuario(java.lang.String value) {
        this.usuario = value;
    }

    /**
     * Obtém o valor da propriedade senha.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getSenha() {
        return senha;
    }

    /**
     * Define o valor da propriedade senha.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setSenha(java.lang.String value) {
        this.senha = value;
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
     * Obtém o valor da propriedade valorSaldoDevedor.
     * 
     */
    public double getValorSaldoDevedor() {
        return valorSaldoDevedor;
    }

    /**
     * Define o valor da propriedade valorSaldoDevedor.
     * 
     */
    public void setValorSaldoDevedor(double value) {
        this.valorSaldoDevedor = value;
    }

    /**
     * Obtém o valor da propriedade dataVencimento.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataVencimento() {
        return dataVencimento;
    }

    /**
     * Define o valor da propriedade dataVencimento.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataVencimento(JAXBElement<XMLGregorianCalendar> value) {
        this.dataVencimento = value;
    }

    /**
     * Obtém o valor da propriedade valorSaldoDevedor2.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getValorSaldoDevedor2() {
        return valorSaldoDevedor2;
    }

    /**
     * Define o valor da propriedade valorSaldoDevedor2.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setValorSaldoDevedor2(JAXBElement<java.lang.Double> value) {
        this.valorSaldoDevedor2 = value;
    }

    /**
     * Obtém o valor da propriedade dataVencimento2.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataVencimento2() {
        return dataVencimento2;
    }

    /**
     * Define o valor da propriedade dataVencimento2.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataVencimento2(JAXBElement<XMLGregorianCalendar> value) {
        this.dataVencimento2 = value;
    }

    /**
     * Obtém o valor da propriedade valorSaldoDevedor3.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getValorSaldoDevedor3() {
        return valorSaldoDevedor3;
    }

    /**
     * Define o valor da propriedade valorSaldoDevedor3.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setValorSaldoDevedor3(JAXBElement<java.lang.Double> value) {
        this.valorSaldoDevedor3 = value;
    }

    /**
     * Obtém o valor da propriedade dataVencimento3.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataVencimento3() {
        return dataVencimento3;
    }

    /**
     * Define o valor da propriedade dataVencimento3.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataVencimento3(JAXBElement<XMLGregorianCalendar> value) {
        this.dataVencimento3 = value;
    }

    /**
     * Obtém o valor da propriedade numeroPrestacoes.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public JAXBElement<java.lang.Short> getNumeroPrestacoes() {
        return numeroPrestacoes;
    }

    /**
     * Define o valor da propriedade numeroPrestacoes.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public void setNumeroPrestacoes(JAXBElement<java.lang.Short> value) {
        this.numeroPrestacoes = value;
    }

    /**
     * Obtém o valor da propriedade banco.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getBanco() {
        return banco;
    }

    /**
     * Define o valor da propriedade banco.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setBanco(JAXBElement<java.lang.String> value) {
        this.banco = value;
    }

    /**
     * Obtém o valor da propriedade agencia.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getAgencia() {
        return agencia;
    }

    /**
     * Define o valor da propriedade agencia.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setAgencia(JAXBElement<java.lang.String> value) {
        this.agencia = value;
    }

    /**
     * Obtém o valor da propriedade conta.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getConta() {
        return conta;
    }

    /**
     * Define o valor da propriedade conta.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setConta(JAXBElement<java.lang.String> value) {
        this.conta = value;
    }

    /**
     * Obtém o valor da propriedade nomeFavorecido.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNomeFavorecido() {
        return nomeFavorecido;
    }

    /**
     * Define o valor da propriedade nomeFavorecido.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNomeFavorecido(JAXBElement<java.lang.String> value) {
        this.nomeFavorecido = value;
    }

    /**
     * Obtém o valor da propriedade cnpjFavorecido.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCnpjFavorecido() {
        return cnpjFavorecido;
    }

    /**
     * Define o valor da propriedade cnpjFavorecido.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCnpjFavorecido(JAXBElement<java.lang.String> value) {
        this.cnpjFavorecido = value;
    }

    /**
     * Obtém o valor da propriedade numeroContrato.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *     
     */
    public JAXBElement<java.lang.Long> getNumeroContrato() {
        return numeroContrato;
    }

    /**
     * Define o valor da propriedade numeroContrato.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *     
     */
    public void setNumeroContrato(JAXBElement<java.lang.Long> value) {
        this.numeroContrato = value;
    }

    /**
     * Obtém o valor da propriedade linkBoleto.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getLinkBoleto() {
        return linkBoleto;
    }

    /**
     * Define o valor da propriedade linkBoleto.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setLinkBoleto(JAXBElement<java.lang.String> value) {
        this.linkBoleto = value;
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
     * Obtém o valor da propriedade detalheSaldoDevedor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getDetalheSaldoDevedor() {
        return detalheSaldoDevedor;
    }

    /**
     * Define o valor da propriedade detalheSaldoDevedor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setDetalheSaldoDevedor(JAXBElement<java.lang.String> value) {
        this.detalheSaldoDevedor = value;
    }

    /**
     * Obtém o valor da propriedade anexoDsdSaldoCompra.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Anexo }{@code >}
     *     
     */
    public JAXBElement<Anexo> getAnexoDsdSaldoCompra() {
        return anexoDsdSaldoCompra;
    }

    /**
     * Define o valor da propriedade anexoDsdSaldoCompra.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Anexo }{@code >}
     *     
     */
    public void setAnexoDsdSaldoCompra(JAXBElement<Anexo> value) {
        this.anexoDsdSaldoCompra = value;
    }

    /**
     * Obtém o valor da propriedade anexoBoletoDsdSaldo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Anexo }{@code >}
     *     
     */
    public JAXBElement<Anexo> getAnexoBoletoDsdSaldo() {
        return anexoBoletoDsdSaldo;
    }

    /**
     * Define o valor da propriedade anexoBoletoDsdSaldo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Anexo }{@code >}
     *     
     */
    public void setAnexoBoletoDsdSaldo(JAXBElement<Anexo> value) {
        this.anexoBoletoDsdSaldo = value;
    }

    /**
     * Obtém o valor da propriedade propostaRefinanciamento.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getPropostaRefinanciamento() {
        return propostaRefinanciamento;
    }

    /**
     * Define o valor da propriedade propostaRefinanciamento.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setPropostaRefinanciamento(JAXBElement<java.lang.String> value) {
        this.propostaRefinanciamento = value;
    }

}
