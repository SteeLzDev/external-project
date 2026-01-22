//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v8;

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
 *         <element name="adeNumero" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         <element name="adeIdentificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="novoAdeIdentificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="valorParcela" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         <element name="valorLiberado" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="valorTac" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="valorIof" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="valorMensVin" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="prazo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="senhaServidor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="loginServidor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="tokenAutServidor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="indice" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="carencia" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="taxaJuros" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="periodo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="anexo" type="{Anexo}Anexo" minOccurs="0"/>
 *         <element name="codigoMotivoOperacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="obsMotivoOperacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "adeIdentificador",
    "novoAdeIdentificador",
    "valorParcela",
    "valorLiberado",
    "valorTac",
    "valorIof",
    "valorMensVin",
    "prazo",
    "senhaServidor",
    "loginServidor",
    "tokenAutServidor",
    "indice",
    "carencia",
    "taxaJuros",
    "periodo",
    "anexo",
    "codigoMotivoOperacao",
    "obsMotivoOperacao"
})
@XmlRootElement(name = "alterarConsignacao", namespace = "HostaHostService-v8_0")
public class AlterarConsignacao {

    @XmlElementRef(name = "cliente", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cliente;
    @XmlElementRef(name = "convenio", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> convenio;
    @XmlElement(namespace = "HostaHostService-v8_0", required = true)
    protected java.lang.String usuario;
    @XmlElement(namespace = "HostaHostService-v8_0", required = true)
    protected java.lang.String senha;
    @XmlElementRef(name = "adeNumero", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Long> adeNumero;
    @XmlElementRef(name = "adeIdentificador", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> adeIdentificador;
    @XmlElementRef(name = "novoAdeIdentificador", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> novoAdeIdentificador;
    @XmlElement(namespace = "HostaHostService-v8_0")
    protected double valorParcela;
    @XmlElementRef(name = "valorLiberado", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> valorLiberado;
    @XmlElementRef(name = "valorTac", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> valorTac;
    @XmlElementRef(name = "valorIof", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> valorIof;
    @XmlElementRef(name = "valorMensVin", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> valorMensVin;
    @XmlElement(namespace = "HostaHostService-v8_0")
    protected int prazo;
    @XmlElementRef(name = "senhaServidor", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> senhaServidor;
    @XmlElement(namespace = "HostaHostService-v8_0")
    protected java.lang.String loginServidor;
    @XmlElement(namespace = "HostaHostService-v8_0")
    protected java.lang.String tokenAutServidor;
    @XmlElementRef(name = "indice", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> indice;
    @XmlElementRef(name = "carencia", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Integer> carencia;
    @XmlElementRef(name = "taxaJuros", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> taxaJuros;
    @XmlElementRef(name = "periodo", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> periodo;
    @XmlElementRef(name = "anexo", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<Anexo> anexo;
    @XmlElementRef(name = "codigoMotivoOperacao", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> codigoMotivoOperacao;
    @XmlElementRef(name = "obsMotivoOperacao", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> obsMotivoOperacao;

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
     * Obtém o valor da propriedade adeIdentificador.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getAdeIdentificador() {
        return adeIdentificador;
    }

    /**
     * Define o valor da propriedade adeIdentificador.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setAdeIdentificador(JAXBElement<java.lang.String> value) {
        this.adeIdentificador = value;
    }

    /**
     * Obtém o valor da propriedade novoAdeIdentificador.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNovoAdeIdentificador() {
        return novoAdeIdentificador;
    }

    /**
     * Define o valor da propriedade novoAdeIdentificador.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNovoAdeIdentificador(JAXBElement<java.lang.String> value) {
        this.novoAdeIdentificador = value;
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
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getValorLiberado() {
        return valorLiberado;
    }

    /**
     * Define o valor da propriedade valorLiberado.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setValorLiberado(JAXBElement<java.lang.Double> value) {
        this.valorLiberado = value;
    }

    /**
     * Obtém o valor da propriedade valorTac.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getValorTac() {
        return valorTac;
    }

    /**
     * Define o valor da propriedade valorTac.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setValorTac(JAXBElement<java.lang.Double> value) {
        this.valorTac = value;
    }

    /**
     * Obtém o valor da propriedade valorIof.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getValorIof() {
        return valorIof;
    }

    /**
     * Define o valor da propriedade valorIof.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setValorIof(JAXBElement<java.lang.Double> value) {
        this.valorIof = value;
    }

    /**
     * Obtém o valor da propriedade valorMensVin.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getValorMensVin() {
        return valorMensVin;
    }

    /**
     * Define o valor da propriedade valorMensVin.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setValorMensVin(JAXBElement<java.lang.Double> value) {
        this.valorMensVin = value;
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
     * Obtém o valor da propriedade senhaServidor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getSenhaServidor() {
        return senhaServidor;
    }

    /**
     * Define o valor da propriedade senhaServidor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setSenhaServidor(JAXBElement<java.lang.String> value) {
        this.senhaServidor = value;
    }

    /**
     * Obtém o valor da propriedade loginServidor.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getLoginServidor() {
        return loginServidor;
    }

    /**
     * Define o valor da propriedade loginServidor.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setLoginServidor(java.lang.String value) {
        this.loginServidor = value;
    }

    /**
     * Obtém o valor da propriedade tokenAutServidor.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getTokenAutServidor() {
        return tokenAutServidor;
    }

    /**
     * Define o valor da propriedade tokenAutServidor.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setTokenAutServidor(java.lang.String value) {
        this.tokenAutServidor = value;
    }

    /**
     * Obtém o valor da propriedade indice.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getIndice() {
        return indice;
    }

    /**
     * Define o valor da propriedade indice.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setIndice(JAXBElement<java.lang.String> value) {
        this.indice = value;
    }

    /**
     * Obtém o valor da propriedade carencia.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *     
     */
    public JAXBElement<java.lang.Integer> getCarencia() {
        return carencia;
    }

    /**
     * Define o valor da propriedade carencia.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *     
     */
    public void setCarencia(JAXBElement<java.lang.Integer> value) {
        this.carencia = value;
    }

    /**
     * Obtém o valor da propriedade taxaJuros.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getTaxaJuros() {
        return taxaJuros;
    }

    /**
     * Define o valor da propriedade taxaJuros.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setTaxaJuros(JAXBElement<java.lang.Double> value) {
        this.taxaJuros = value;
    }

    /**
     * Obtém o valor da propriedade periodo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getPeriodo() {
        return periodo;
    }

    /**
     * Define o valor da propriedade periodo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setPeriodo(JAXBElement<java.lang.String> value) {
        this.periodo = value;
    }

    /**
     * Obtém o valor da propriedade anexo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Anexo }{@code >}
     *     
     */
    public JAXBElement<Anexo> getAnexo() {
        return anexo;
    }

    /**
     * Define o valor da propriedade anexo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Anexo }{@code >}
     *     
     */
    public void setAnexo(JAXBElement<Anexo> value) {
        this.anexo = value;
    }

    /**
     * Obtém o valor da propriedade codigoMotivoOperacao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCodigoMotivoOperacao() {
        return codigoMotivoOperacao;
    }

    /**
     * Define o valor da propriedade codigoMotivoOperacao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCodigoMotivoOperacao(JAXBElement<java.lang.String> value) {
        this.codigoMotivoOperacao = value;
    }

    /**
     * Obtém o valor da propriedade obsMotivoOperacao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getObsMotivoOperacao() {
        return obsMotivoOperacao;
    }

    /**
     * Define o valor da propriedade obsMotivoOperacao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setObsMotivoOperacao(JAXBElement<java.lang.String> value) {
        this.obsMotivoOperacao = value;
    }

}
