//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v7;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Usuario complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="Usuario">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="login" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="nome" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="telefone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ipAcesso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="dnsAcesso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="dataFimVigencia" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="perfil" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="tipoEntidade" type="{PapelUsuario}PapelUsuario" minOccurs="0"/>
 *         <element name="entidadeCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Usuario", namespace = "Usuario", propOrder = {
    "login",
    "nome",
    "email",
    "cpf",
    "telefone",
    "ipAcesso",
    "dnsAcesso",
    "dataFimVigencia",
    "perfil",
    "tipoEntidade",
    "entidadeCodigo"
})
public class Usuario {

    @XmlElement(namespace = "Usuario", required = true)
    protected java.lang.String login;
    @XmlElement(namespace = "Usuario", required = true)
    protected java.lang.String nome;
    @XmlElementRef(name = "email", namespace = "Usuario", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> email;
    @XmlElementRef(name = "cpf", namespace = "Usuario", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cpf;
    @XmlElementRef(name = "telefone", namespace = "Usuario", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> telefone;
    @XmlElementRef(name = "ipAcesso", namespace = "Usuario", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> ipAcesso;
    @XmlElementRef(name = "dnsAcesso", namespace = "Usuario", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> dnsAcesso;
    @XmlElementRef(name = "dataFimVigencia", namespace = "Usuario", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataFimVigencia;
    @XmlElement(namespace = "Usuario", required = true)
    protected java.lang.String perfil;
    @XmlElementRef(name = "tipoEntidade", namespace = "Usuario", type = JAXBElement.class, required = false)
    protected JAXBElement<PapelUsuario> tipoEntidade;
    @XmlElementRef(name = "entidadeCodigo", namespace = "Usuario", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> entidadeCodigo;

    /**
     * Obtém o valor da propriedade login.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getLogin() {
        return login;
    }

    /**
     * Define o valor da propriedade login.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setLogin(java.lang.String value) {
        this.login = value;
    }

    /**
     * Obtém o valor da propriedade nome.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getNome() {
        return nome;
    }

    /**
     * Define o valor da propriedade nome.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setNome(java.lang.String value) {
        this.nome = value;
    }

    /**
     * Obtém o valor da propriedade email.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getEmail() {
        return email;
    }

    /**
     * Define o valor da propriedade email.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setEmail(JAXBElement<java.lang.String> value) {
        this.email = value;
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
     * Obtém o valor da propriedade telefone.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getTelefone() {
        return telefone;
    }

    /**
     * Define o valor da propriedade telefone.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setTelefone(JAXBElement<java.lang.String> value) {
        this.telefone = value;
    }

    /**
     * Obtém o valor da propriedade ipAcesso.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getIpAcesso() {
        return ipAcesso;
    }

    /**
     * Define o valor da propriedade ipAcesso.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setIpAcesso(JAXBElement<java.lang.String> value) {
        this.ipAcesso = value;
    }

    /**
     * Obtém o valor da propriedade dnsAcesso.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getDnsAcesso() {
        return dnsAcesso;
    }

    /**
     * Define o valor da propriedade dnsAcesso.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setDnsAcesso(JAXBElement<java.lang.String> value) {
        this.dnsAcesso = value;
    }

    /**
     * Obtém o valor da propriedade dataFimVigencia.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataFimVigencia() {
        return dataFimVigencia;
    }

    /**
     * Define o valor da propriedade dataFimVigencia.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataFimVigencia(JAXBElement<XMLGregorianCalendar> value) {
        this.dataFimVigencia = value;
    }

    /**
     * Obtém o valor da propriedade perfil.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getPerfil() {
        return perfil;
    }

    /**
     * Define o valor da propriedade perfil.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setPerfil(java.lang.String value) {
        this.perfil = value;
    }

    /**
     * Obtém o valor da propriedade tipoEntidade.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link PapelUsuario }{@code >}
     *     
     */
    public JAXBElement<PapelUsuario> getTipoEntidade() {
        return tipoEntidade;
    }

    /**
     * Define o valor da propriedade tipoEntidade.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link PapelUsuario }{@code >}
     *     
     */
    public void setTipoEntidade(JAXBElement<PapelUsuario> value) {
        this.tipoEntidade = value;
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

}
