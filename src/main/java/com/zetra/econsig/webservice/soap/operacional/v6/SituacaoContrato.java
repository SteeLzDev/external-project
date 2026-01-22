//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v6;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de SituacaoContrato complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="SituacaoContrato">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="solicitado" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="aguardandoConfirmacao" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="aguardandoDeferimento" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="indeferida" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="deferida" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="emAndamento" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="suspensa" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="cancelada" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="liquidada" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="concluido" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="suspensaPeloConsignante" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="aguardandoLiquidacao" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="estoque" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="estoqueNaoLiberado" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="emCarencia" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="aguardandoLiquidacaoCompra" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="estoqueMensal" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SituacaoContrato", namespace = "SituacaoContrato", propOrder = {
    "solicitado",
    "aguardandoConfirmacao",
    "aguardandoDeferimento",
    "indeferida",
    "deferida",
    "emAndamento",
    "suspensa",
    "cancelada",
    "liquidada",
    "concluido",
    "suspensaPeloConsignante",
    "aguardandoLiquidacao",
    "estoque",
    "estoqueNaoLiberado",
    "emCarencia",
    "aguardandoLiquidacaoCompra",
    "estoqueMensal"
})
public class SituacaoContrato {

    @XmlElement(namespace = "SituacaoContrato", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean solicitado;
    @XmlElement(namespace = "SituacaoContrato", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean aguardandoConfirmacao;
    @XmlElement(namespace = "SituacaoContrato", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean aguardandoDeferimento;
    @XmlElement(namespace = "SituacaoContrato", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean indeferida;
    @XmlElement(namespace = "SituacaoContrato", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean deferida;
    @XmlElement(namespace = "SituacaoContrato", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean emAndamento;
    @XmlElement(namespace = "SituacaoContrato", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean suspensa;
    @XmlElement(namespace = "SituacaoContrato", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean cancelada;
    @XmlElement(namespace = "SituacaoContrato", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean liquidada;
    @XmlElement(namespace = "SituacaoContrato", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean concluido;
    @XmlElement(namespace = "SituacaoContrato", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean suspensaPeloConsignante;
    @XmlElement(namespace = "SituacaoContrato", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean aguardandoLiquidacao;
    @XmlElement(namespace = "SituacaoContrato", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean estoque;
    @XmlElement(namespace = "SituacaoContrato", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean estoqueNaoLiberado;
    @XmlElement(namespace = "SituacaoContrato", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean emCarencia;
    @XmlElement(namespace = "SituacaoContrato", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean aguardandoLiquidacaoCompra;
    @XmlElement(namespace = "SituacaoContrato", required = true, type = java.lang.Boolean.class, nillable = true)
    protected java.lang.Boolean estoqueMensal;

    /**
     * Obtém o valor da propriedade solicitado.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getSolicitado() {
        return solicitado;
    }

    /**
     * Define o valor da propriedade solicitado.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setSolicitado(java.lang.Boolean value) {
        this.solicitado = value;
    }

    /**
     * Obtém o valor da propriedade aguardandoConfirmacao.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getAguardandoConfirmacao() {
        return aguardandoConfirmacao;
    }

    /**
     * Define o valor da propriedade aguardandoConfirmacao.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setAguardandoConfirmacao(java.lang.Boolean value) {
        this.aguardandoConfirmacao = value;
    }

    /**
     * Obtém o valor da propriedade aguardandoDeferimento.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getAguardandoDeferimento() {
        return aguardandoDeferimento;
    }

    /**
     * Define o valor da propriedade aguardandoDeferimento.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setAguardandoDeferimento(java.lang.Boolean value) {
        this.aguardandoDeferimento = value;
    }

    /**
     * Obtém o valor da propriedade indeferida.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getIndeferida() {
        return indeferida;
    }

    /**
     * Define o valor da propriedade indeferida.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setIndeferida(java.lang.Boolean value) {
        this.indeferida = value;
    }

    /**
     * Obtém o valor da propriedade deferida.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getDeferida() {
        return deferida;
    }

    /**
     * Define o valor da propriedade deferida.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setDeferida(java.lang.Boolean value) {
        this.deferida = value;
    }

    /**
     * Obtém o valor da propriedade emAndamento.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getEmAndamento() {
        return emAndamento;
    }

    /**
     * Define o valor da propriedade emAndamento.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setEmAndamento(java.lang.Boolean value) {
        this.emAndamento = value;
    }

    /**
     * Obtém o valor da propriedade suspensa.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getSuspensa() {
        return suspensa;
    }

    /**
     * Define o valor da propriedade suspensa.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setSuspensa(java.lang.Boolean value) {
        this.suspensa = value;
    }

    /**
     * Obtém o valor da propriedade cancelada.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getCancelada() {
        return cancelada;
    }

    /**
     * Define o valor da propriedade cancelada.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setCancelada(java.lang.Boolean value) {
        this.cancelada = value;
    }

    /**
     * Obtém o valor da propriedade liquidada.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getLiquidada() {
        return liquidada;
    }

    /**
     * Define o valor da propriedade liquidada.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setLiquidada(java.lang.Boolean value) {
        this.liquidada = value;
    }

    /**
     * Obtém o valor da propriedade concluido.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getConcluido() {
        return concluido;
    }

    /**
     * Define o valor da propriedade concluido.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setConcluido(java.lang.Boolean value) {
        this.concluido = value;
    }

    /**
     * Obtém o valor da propriedade suspensaPeloConsignante.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getSuspensaPeloConsignante() {
        return suspensaPeloConsignante;
    }

    /**
     * Define o valor da propriedade suspensaPeloConsignante.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setSuspensaPeloConsignante(java.lang.Boolean value) {
        this.suspensaPeloConsignante = value;
    }

    /**
     * Obtém o valor da propriedade aguardandoLiquidacao.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getAguardandoLiquidacao() {
        return aguardandoLiquidacao;
    }

    /**
     * Define o valor da propriedade aguardandoLiquidacao.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setAguardandoLiquidacao(java.lang.Boolean value) {
        this.aguardandoLiquidacao = value;
    }

    /**
     * Obtém o valor da propriedade estoque.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getEstoque() {
        return estoque;
    }

    /**
     * Define o valor da propriedade estoque.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setEstoque(java.lang.Boolean value) {
        this.estoque = value;
    }

    /**
     * Obtém o valor da propriedade estoqueNaoLiberado.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getEstoqueNaoLiberado() {
        return estoqueNaoLiberado;
    }

    /**
     * Define o valor da propriedade estoqueNaoLiberado.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setEstoqueNaoLiberado(java.lang.Boolean value) {
        this.estoqueNaoLiberado = value;
    }

    /**
     * Obtém o valor da propriedade emCarencia.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getEmCarencia() {
        return emCarencia;
    }

    /**
     * Define o valor da propriedade emCarencia.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setEmCarencia(java.lang.Boolean value) {
        this.emCarencia = value;
    }

    /**
     * Obtém o valor da propriedade aguardandoLiquidacaoCompra.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getAguardandoLiquidacaoCompra() {
        return aguardandoLiquidacaoCompra;
    }

    /**
     * Define o valor da propriedade aguardandoLiquidacaoCompra.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setAguardandoLiquidacaoCompra(java.lang.Boolean value) {
        this.aguardandoLiquidacaoCompra = value;
    }

    /**
     * Obtém o valor da propriedade estoqueMensal.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getEstoqueMensal() {
        return estoqueMensal;
    }

    /**
     * Define o valor da propriedade estoqueMensal.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setEstoqueMensal(java.lang.Boolean value) {
        this.estoqueMensal = value;
    }

}
