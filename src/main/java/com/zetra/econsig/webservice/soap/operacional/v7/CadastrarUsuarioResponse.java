//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v7;

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
 *         <element name="sucesso" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="codRetorno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="mensagem" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="entidadeCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="entidadeNome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nomeUsuario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="loginUsuario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="senhaUsuario" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "sucesso",
    "codRetorno",
    "mensagem",
    "entidadeCodigo",
    "entidadeNome",
    "nomeUsuario",
    "loginUsuario",
    "senhaUsuario"
})
@XmlRootElement(name = "cadastrarUsuarioResponse", namespace = "HostaHostService-v7_0")
public class CadastrarUsuarioResponse {

    @XmlElement(namespace = "HostaHostService-v7_0")
    protected boolean sucesso;
    @XmlElementRef(name = "codRetorno", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> codRetorno;
    @XmlElement(namespace = "HostaHostService-v7_0", required = true)
    protected java.lang.String mensagem;
    @XmlElementRef(name = "entidadeCodigo", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> entidadeCodigo;
    @XmlElementRef(name = "entidadeNome", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> entidadeNome;
    @XmlElementRef(name = "nomeUsuario", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nomeUsuario;
    @XmlElementRef(name = "loginUsuario", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> loginUsuario;
    @XmlElementRef(name = "senhaUsuario", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> senhaUsuario;

    /**
     * Obtém o valor da propriedade sucesso.
     * 
     */
    public boolean isSucesso() {
        return sucesso;
    }

    /**
     * Define o valor da propriedade sucesso.
     * 
     */
    public void setSucesso(boolean value) {
        this.sucesso = value;
    }

    /**
     * Obtém o valor da propriedade codRetorno.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCodRetorno() {
        return codRetorno;
    }

    /**
     * Define o valor da propriedade codRetorno.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCodRetorno(JAXBElement<java.lang.String> value) {
        this.codRetorno = value;
    }

    /**
     * Obtém o valor da propriedade mensagem.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getMensagem() {
        return mensagem;
    }

    /**
     * Define o valor da propriedade mensagem.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setMensagem(java.lang.String value) {
        this.mensagem = value;
    }

    /**
     * Obtém o valor da propriedade entidadeCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getEntidadeCodigo() {
        return entidadeCodigo;
    }

    /**
     * Define o valor da propriedade entidadeCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setEntidadeCodigo(JAXBElement<java.lang.String> value) {
        this.entidadeCodigo = value;
    }

    /**
     * Obtém o valor da propriedade entidadeNome.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getEntidadeNome() {
        return entidadeNome;
    }

    /**
     * Define o valor da propriedade entidadeNome.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setEntidadeNome(JAXBElement<java.lang.String> value) {
        this.entidadeNome = value;
    }

    /**
     * Obtém o valor da propriedade nomeUsuario.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNomeUsuario() {
        return nomeUsuario;
    }

    /**
     * Define o valor da propriedade nomeUsuario.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNomeUsuario(JAXBElement<java.lang.String> value) {
        this.nomeUsuario = value;
    }

    /**
     * Obtém o valor da propriedade loginUsuario.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getLoginUsuario() {
        return loginUsuario;
    }

    /**
     * Define o valor da propriedade loginUsuario.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setLoginUsuario(JAXBElement<java.lang.String> value) {
        this.loginUsuario = value;
    }

    /**
     * Obtém o valor da propriedade senhaUsuario.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getSenhaUsuario() {
        return senhaUsuario;
    }

    /**
     * Define o valor da propriedade senhaUsuario.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setSenhaUsuario(JAXBElement<java.lang.String> value) {
        this.senhaUsuario = value;
    }

}
