//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.folha.v1;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de TipoArquivo complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="TipoArquivo">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="bloqueioServidor" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="contracheque" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="critica" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="desligado" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="dirf" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="falecido" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="margem" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="margemComplementar" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="movimento" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="relatorioIntegracao" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="retorno" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="retornoAtrasado" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="transferidos" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TipoArquivo", namespace = "TipoArquivo", propOrder = {
    "bloqueioServidor",
    "contracheque",
    "critica",
    "desligado",
    "dirf",
    "falecido",
    "margem",
    "margemComplementar",
    "movimento",
    "relatorioIntegracao",
    "retorno",
    "retornoAtrasado",
    "transferidos"
})
public class TipoArquivo {

    @XmlElement(namespace = "TipoArquivo")
    protected java.lang.Boolean bloqueioServidor;
    @XmlElement(namespace = "TipoArquivo")
    protected java.lang.Boolean contracheque;
    @XmlElement(namespace = "TipoArquivo")
    protected java.lang.Boolean critica;
    @XmlElement(namespace = "TipoArquivo")
    protected java.lang.Boolean desligado;
    @XmlElement(namespace = "TipoArquivo")
    protected java.lang.Boolean dirf;
    @XmlElement(namespace = "TipoArquivo")
    protected java.lang.Boolean falecido;
    @XmlElement(namespace = "TipoArquivo")
    protected java.lang.Boolean margem;
    @XmlElement(namespace = "TipoArquivo")
    protected java.lang.Boolean margemComplementar;
    @XmlElement(namespace = "TipoArquivo")
    protected java.lang.Boolean movimento;
    @XmlElement(namespace = "TipoArquivo")
    protected java.lang.Boolean relatorioIntegracao;
    @XmlElement(namespace = "TipoArquivo")
    protected java.lang.Boolean retorno;
    @XmlElement(namespace = "TipoArquivo")
    protected java.lang.Boolean retornoAtrasado;
    @XmlElement(namespace = "TipoArquivo")
    protected java.lang.Boolean transferidos;

    /**
     * Obtém o valor da propriedade bloqueioServidor.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getBloqueioServidor() {
        return bloqueioServidor;
    }

    /**
     * Define o valor da propriedade bloqueioServidor.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setBloqueioServidor(java.lang.Boolean value) {
        this.bloqueioServidor = value;
    }

    /**
     * Obtém o valor da propriedade contracheque.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getContracheque() {
        return contracheque;
    }

    /**
     * Define o valor da propriedade contracheque.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setContracheque(java.lang.Boolean value) {
        this.contracheque = value;
    }

    /**
     * Obtém o valor da propriedade critica.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getCritica() {
        return critica;
    }

    /**
     * Define o valor da propriedade critica.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setCritica(java.lang.Boolean value) {
        this.critica = value;
    }

    /**
     * Obtém o valor da propriedade desligado.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getDesligado() {
        return desligado;
    }

    /**
     * Define o valor da propriedade desligado.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setDesligado(java.lang.Boolean value) {
        this.desligado = value;
    }

    /**
     * Obtém o valor da propriedade dirf.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getDirf() {
        return dirf;
    }

    /**
     * Define o valor da propriedade dirf.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setDirf(java.lang.Boolean value) {
        this.dirf = value;
    }

    /**
     * Obtém o valor da propriedade falecido.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getFalecido() {
        return falecido;
    }

    /**
     * Define o valor da propriedade falecido.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setFalecido(java.lang.Boolean value) {
        this.falecido = value;
    }

    /**
     * Obtém o valor da propriedade margem.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getMargem() {
        return margem;
    }

    /**
     * Define o valor da propriedade margem.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setMargem(java.lang.Boolean value) {
        this.margem = value;
    }

    /**
     * Obtém o valor da propriedade margemComplementar.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getMargemComplementar() {
        return margemComplementar;
    }

    /**
     * Define o valor da propriedade margemComplementar.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setMargemComplementar(java.lang.Boolean value) {
        this.margemComplementar = value;
    }

    /**
     * Obtém o valor da propriedade movimento.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getMovimento() {
        return movimento;
    }

    /**
     * Define o valor da propriedade movimento.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setMovimento(java.lang.Boolean value) {
        this.movimento = value;
    }

    /**
     * Obtém o valor da propriedade relatorioIntegracao.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getRelatorioIntegracao() {
        return relatorioIntegracao;
    }

    /**
     * Define o valor da propriedade relatorioIntegracao.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setRelatorioIntegracao(java.lang.Boolean value) {
        this.relatorioIntegracao = value;
    }

    /**
     * Obtém o valor da propriedade retorno.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getRetorno() {
        return retorno;
    }

    /**
     * Define o valor da propriedade retorno.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setRetorno(java.lang.Boolean value) {
        this.retorno = value;
    }

    /**
     * Obtém o valor da propriedade retornoAtrasado.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getRetornoAtrasado() {
        return retornoAtrasado;
    }

    /**
     * Define o valor da propriedade retornoAtrasado.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setRetornoAtrasado(java.lang.Boolean value) {
        this.retornoAtrasado = value;
    }

    /**
     * Obtém o valor da propriedade transferidos.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getTransferidos() {
        return transferidos;
    }

    /**
     * Define o valor da propriedade transferidos.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setTransferidos(java.lang.Boolean value) {
        this.transferidos = value;
    }

}
