//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v8;

import java.util.ArrayList;
import java.util.List;
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
 *         <element name="adeNumeros" type="{http://www.w3.org/2001/XMLSchema}long" maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="adeIdentificador" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="novoAdeIdentificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="dataNascimento" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="valorParcela" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         <element name="valorLiberado" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="codVerba" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="servicoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="prazo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="carencia" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="senhaServidor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="loginServidor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="tokenAutServidor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="correspondenteCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="valorTac" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="indice" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="valorIof" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="valorMensVin" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="matricula" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="orgaoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="estabelecimentoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="banco" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="agencia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="conta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="taxaJuros" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="naturezaServicoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "adeNumeros",
    "adeIdentificador",
    "novoAdeIdentificador",
    "dataNascimento",
    "valorParcela",
    "valorLiberado",
    "codVerba",
    "servicoCodigo",
    "prazo",
    "carencia",
    "senhaServidor",
    "loginServidor",
    "tokenAutServidor",
    "correspondenteCodigo",
    "valorTac",
    "indice",
    "valorIof",
    "valorMensVin",
    "matricula",
    "cpf",
    "orgaoCodigo",
    "estabelecimentoCodigo",
    "banco",
    "agencia",
    "conta",
    "taxaJuros",
    "naturezaServicoCodigo"
})
@XmlRootElement(name = "renegociarConsignacao", namespace = "HostaHostService-v8_0")
public class RenegociarConsignacao {

    @XmlElementRef(name = "cliente", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cliente;
    @XmlElementRef(name = "convenio", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> convenio;
    @XmlElement(namespace = "HostaHostService-v8_0", required = true)
    protected java.lang.String usuario;
    @XmlElement(namespace = "HostaHostService-v8_0", required = true)
    protected java.lang.String senha;
    @XmlElement(namespace = "HostaHostService-v8_0", nillable = true)
    protected List<java.lang.Long> adeNumeros;
    @XmlElement(namespace = "HostaHostService-v8_0", nillable = true)
    protected List<java.lang.String> adeIdentificador;
    @XmlElementRef(name = "novoAdeIdentificador", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> novoAdeIdentificador;
    @XmlElementRef(name = "dataNascimento", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataNascimento;
    @XmlElement(namespace = "HostaHostService-v8_0")
    protected double valorParcela;
    @XmlElementRef(name = "valorLiberado", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> valorLiberado;
    @XmlElementRef(name = "codVerba", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> codVerba;
    @XmlElementRef(name = "servicoCodigo", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> servicoCodigo;
    @XmlElement(namespace = "HostaHostService-v8_0")
    protected int prazo;
    @XmlElementRef(name = "carencia", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Integer> carencia;
    @XmlElement(namespace = "HostaHostService-v8_0")
    protected java.lang.String senhaServidor;
    @XmlElement(namespace = "HostaHostService-v8_0")
    protected java.lang.String loginServidor;
    @XmlElement(namespace = "HostaHostService-v8_0")
    protected java.lang.String tokenAutServidor;
    @XmlElementRef(name = "correspondenteCodigo", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> correspondenteCodigo;
    @XmlElementRef(name = "valorTac", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> valorTac;
    @XmlElementRef(name = "indice", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> indice;
    @XmlElementRef(name = "valorIof", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> valorIof;
    @XmlElementRef(name = "valorMensVin", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> valorMensVin;
    @XmlElement(namespace = "HostaHostService-v8_0")
    protected java.lang.String matricula;
    @XmlElementRef(name = "cpf", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cpf;
    @XmlElementRef(name = "orgaoCodigo", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> orgaoCodigo;
    @XmlElementRef(name = "estabelecimentoCodigo", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> estabelecimentoCodigo;
    @XmlElementRef(name = "banco", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> banco;
    @XmlElementRef(name = "agencia", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> agencia;
    @XmlElementRef(name = "conta", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> conta;
    @XmlElementRef(name = "taxaJuros", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> taxaJuros;
    @XmlElementRef(name = "naturezaServicoCodigo", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> naturezaServicoCodigo;

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
     * Gets the value of the adeNumeros property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the adeNumeros property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdeNumeros().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link java.lang.Long }
     * 
     * 
     * @return
     *     The value of the adeNumeros property.
     */
    public List<java.lang.Long> getAdeNumeros() {
        if (adeNumeros == null) {
            adeNumeros = new ArrayList<>();
        }
        return this.adeNumeros;
    }

    /**
     * Gets the value of the adeIdentificador property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the adeIdentificador property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdeIdentificador().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link java.lang.String }
     * 
     * 
     * @return
     *     The value of the adeIdentificador property.
     */
    public List<java.lang.String> getAdeIdentificador() {
        if (adeIdentificador == null) {
            adeIdentificador = new ArrayList<>();
        }
        return this.adeIdentificador;
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
     * Obtém o valor da propriedade dataNascimento.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataNascimento() {
        return dataNascimento;
    }

    /**
     * Define o valor da propriedade dataNascimento.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataNascimento(JAXBElement<XMLGregorianCalendar> value) {
        this.dataNascimento = value;
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
     * Obtém o valor da propriedade codVerba.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCodVerba() {
        return codVerba;
    }

    /**
     * Define o valor da propriedade codVerba.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCodVerba(JAXBElement<java.lang.String> value) {
        this.codVerba = value;
    }

    /**
     * Obtém o valor da propriedade servicoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getServicoCodigo() {
        return servicoCodigo;
    }

    /**
     * Define o valor da propriedade servicoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setServicoCodigo(JAXBElement<java.lang.String> value) {
        this.servicoCodigo = value;
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
     * Obtém o valor da propriedade senhaServidor.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getSenhaServidor() {
        return senhaServidor;
    }

    /**
     * Define o valor da propriedade senhaServidor.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setSenhaServidor(java.lang.String value) {
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
     * Obtém o valor da propriedade correspondenteCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCorrespondenteCodigo() {
        return correspondenteCodigo;
    }

    /**
     * Define o valor da propriedade correspondenteCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCorrespondenteCodigo(JAXBElement<java.lang.String> value) {
        this.correspondenteCodigo = value;
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
     * Obtém o valor da propriedade orgaoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getOrgaoCodigo() {
        return orgaoCodigo;
    }

    /**
     * Define o valor da propriedade orgaoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setOrgaoCodigo(JAXBElement<java.lang.String> value) {
        this.orgaoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade estabelecimentoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getEstabelecimentoCodigo() {
        return estabelecimentoCodigo;
    }

    /**
     * Define o valor da propriedade estabelecimentoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setEstabelecimentoCodigo(JAXBElement<java.lang.String> value) {
        this.estabelecimentoCodigo = value;
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
     * Obtém o valor da propriedade naturezaServicoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNaturezaServicoCodigo() {
        return naturezaServicoCodigo;
    }

    /**
     * Define o valor da propriedade naturezaServicoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNaturezaServicoCodigo(JAXBElement<java.lang.String> value) {
        this.naturezaServicoCodigo = value;
    }

}
