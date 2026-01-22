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
import jakarta.xml.bind.annotation.XmlSchemaType;
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
 *         <element name="adeNumero" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         <element name="matricula" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="temPendenciaProcessoCompra" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="temContratosBloqueadosOuABloquear" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="diasParaBloqueio" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         <element name="contratosCompradosPelaEntidade" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="saldoDevedorInformado" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="diasUteisSemInfoSaldoDevedor" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         <element name="saldoDevedorAprovado" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="diasUteisSemAprovacaoSaldoDevedor" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         <element name="saldoDevedorPago" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="diasUteisSemPagamentoSaldoDevedor" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         <element name="contratoLiquidado" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="diasUteisSemLiquidacao" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         <element name="dataInicioCompra" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         <element name="dataFimCompra" type="{http://www.w3.org/2001/XMLSchema}date"/>
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
    "matricula",
    "cpf",
    "temPendenciaProcessoCompra",
    "temContratosBloqueadosOuABloquear",
    "diasParaBloqueio",
    "contratosCompradosPelaEntidade",
    "saldoDevedorInformado",
    "diasUteisSemInfoSaldoDevedor",
    "saldoDevedorAprovado",
    "diasUteisSemAprovacaoSaldoDevedor",
    "saldoDevedorPago",
    "diasUteisSemPagamentoSaldoDevedor",
    "contratoLiquidado",
    "diasUteisSemLiquidacao",
    "dataInicioCompra",
    "dataFimCompra"
})
@XmlRootElement(name = "acompanharCompraContrato", namespace = "CompraService-v1_0")
public class AcompanharCompraContrato {

    @XmlElementRef(name = "cliente", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cliente;
    @XmlElementRef(name = "convenio", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> convenio;
    @XmlElement(namespace = "CompraService-v1_0", required = true)
    protected java.lang.String usuario;
    @XmlElement(namespace = "CompraService-v1_0", required = true)
    protected java.lang.String senha;
    @XmlElementRef(name = "adeNumero", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Long> adeNumero;
    @XmlElementRef(name = "matricula", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> matricula;
    @XmlElementRef(name = "cpf", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cpf;
    @XmlElement(namespace = "CompraService-v1_0")
    protected java.lang.Boolean temPendenciaProcessoCompra;
    @XmlElement(namespace = "CompraService-v1_0")
    protected java.lang.Boolean temContratosBloqueadosOuABloquear;
    @XmlElementRef(name = "diasParaBloqueio", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Short> diasParaBloqueio;
    @XmlElement(namespace = "CompraService-v1_0")
    protected java.lang.Boolean contratosCompradosPelaEntidade;
    @XmlElementRef(name = "saldoDevedorInformado", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Boolean> saldoDevedorInformado;
    @XmlElementRef(name = "diasUteisSemInfoSaldoDevedor", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Short> diasUteisSemInfoSaldoDevedor;
    @XmlElementRef(name = "saldoDevedorAprovado", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Boolean> saldoDevedorAprovado;
    @XmlElementRef(name = "diasUteisSemAprovacaoSaldoDevedor", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Short> diasUteisSemAprovacaoSaldoDevedor;
    @XmlElementRef(name = "saldoDevedorPago", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Boolean> saldoDevedorPago;
    @XmlElementRef(name = "diasUteisSemPagamentoSaldoDevedor", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Short> diasUteisSemPagamentoSaldoDevedor;
    @XmlElementRef(name = "contratoLiquidado", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Boolean> contratoLiquidado;
    @XmlElementRef(name = "diasUteisSemLiquidacao", namespace = "CompraService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Short> diasUteisSemLiquidacao;
    @XmlElement(namespace = "CompraService-v1_0", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataInicioCompra;
    @XmlElement(namespace = "CompraService-v1_0", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataFimCompra;

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
     * Obtém o valor da propriedade temPendenciaProcessoCompra.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getTemPendenciaProcessoCompra() {
        return temPendenciaProcessoCompra;
    }

    /**
     * Define o valor da propriedade temPendenciaProcessoCompra.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setTemPendenciaProcessoCompra(java.lang.Boolean value) {
        this.temPendenciaProcessoCompra = value;
    }

    /**
     * Obtém o valor da propriedade temContratosBloqueadosOuABloquear.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getTemContratosBloqueadosOuABloquear() {
        return temContratosBloqueadosOuABloquear;
    }

    /**
     * Define o valor da propriedade temContratosBloqueadosOuABloquear.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setTemContratosBloqueadosOuABloquear(java.lang.Boolean value) {
        this.temContratosBloqueadosOuABloquear = value;
    }

    /**
     * Obtém o valor da propriedade diasParaBloqueio.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public JAXBElement<java.lang.Short> getDiasParaBloqueio() {
        return diasParaBloqueio;
    }

    /**
     * Define o valor da propriedade diasParaBloqueio.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public void setDiasParaBloqueio(JAXBElement<java.lang.Short> value) {
        this.diasParaBloqueio = value;
    }

    /**
     * Obtém o valor da propriedade contratosCompradosPelaEntidade.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getContratosCompradosPelaEntidade() {
        return contratosCompradosPelaEntidade;
    }

    /**
     * Define o valor da propriedade contratosCompradosPelaEntidade.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setContratosCompradosPelaEntidade(java.lang.Boolean value) {
        this.contratosCompradosPelaEntidade = value;
    }

    /**
     * Obtém o valor da propriedade saldoDevedorInformado.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public JAXBElement<java.lang.Boolean> getSaldoDevedorInformado() {
        return saldoDevedorInformado;
    }

    /**
     * Define o valor da propriedade saldoDevedorInformado.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public void setSaldoDevedorInformado(JAXBElement<java.lang.Boolean> value) {
        this.saldoDevedorInformado = value;
    }

    /**
     * Obtém o valor da propriedade diasUteisSemInfoSaldoDevedor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public JAXBElement<java.lang.Short> getDiasUteisSemInfoSaldoDevedor() {
        return diasUteisSemInfoSaldoDevedor;
    }

    /**
     * Define o valor da propriedade diasUteisSemInfoSaldoDevedor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public void setDiasUteisSemInfoSaldoDevedor(JAXBElement<java.lang.Short> value) {
        this.diasUteisSemInfoSaldoDevedor = value;
    }

    /**
     * Obtém o valor da propriedade saldoDevedorAprovado.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public JAXBElement<java.lang.Boolean> getSaldoDevedorAprovado() {
        return saldoDevedorAprovado;
    }

    /**
     * Define o valor da propriedade saldoDevedorAprovado.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public void setSaldoDevedorAprovado(JAXBElement<java.lang.Boolean> value) {
        this.saldoDevedorAprovado = value;
    }

    /**
     * Obtém o valor da propriedade diasUteisSemAprovacaoSaldoDevedor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public JAXBElement<java.lang.Short> getDiasUteisSemAprovacaoSaldoDevedor() {
        return diasUteisSemAprovacaoSaldoDevedor;
    }

    /**
     * Define o valor da propriedade diasUteisSemAprovacaoSaldoDevedor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public void setDiasUteisSemAprovacaoSaldoDevedor(JAXBElement<java.lang.Short> value) {
        this.diasUteisSemAprovacaoSaldoDevedor = value;
    }

    /**
     * Obtém o valor da propriedade saldoDevedorPago.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public JAXBElement<java.lang.Boolean> getSaldoDevedorPago() {
        return saldoDevedorPago;
    }

    /**
     * Define o valor da propriedade saldoDevedorPago.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public void setSaldoDevedorPago(JAXBElement<java.lang.Boolean> value) {
        this.saldoDevedorPago = value;
    }

    /**
     * Obtém o valor da propriedade diasUteisSemPagamentoSaldoDevedor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public JAXBElement<java.lang.Short> getDiasUteisSemPagamentoSaldoDevedor() {
        return diasUteisSemPagamentoSaldoDevedor;
    }

    /**
     * Define o valor da propriedade diasUteisSemPagamentoSaldoDevedor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public void setDiasUteisSemPagamentoSaldoDevedor(JAXBElement<java.lang.Short> value) {
        this.diasUteisSemPagamentoSaldoDevedor = value;
    }

    /**
     * Obtém o valor da propriedade contratoLiquidado.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public JAXBElement<java.lang.Boolean> getContratoLiquidado() {
        return contratoLiquidado;
    }

    /**
     * Define o valor da propriedade contratoLiquidado.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public void setContratoLiquidado(JAXBElement<java.lang.Boolean> value) {
        this.contratoLiquidado = value;
    }

    /**
     * Obtém o valor da propriedade diasUteisSemLiquidacao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public JAXBElement<java.lang.Short> getDiasUteisSemLiquidacao() {
        return diasUteisSemLiquidacao;
    }

    /**
     * Define o valor da propriedade diasUteisSemLiquidacao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public void setDiasUteisSemLiquidacao(JAXBElement<java.lang.Short> value) {
        this.diasUteisSemLiquidacao = value;
    }

    /**
     * Obtém o valor da propriedade dataInicioCompra.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataInicioCompra() {
        return dataInicioCompra;
    }

    /**
     * Define o valor da propriedade dataInicioCompra.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataInicioCompra(XMLGregorianCalendar value) {
        this.dataInicioCompra = value;
    }

    /**
     * Obtém o valor da propriedade dataFimCompra.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataFimCompra() {
        return dataFimCompra;
    }

    /**
     * Define o valor da propriedade dataFimCompra.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataFimCompra(XMLGregorianCalendar value) {
        this.dataFimCompra = value;
    }

}
