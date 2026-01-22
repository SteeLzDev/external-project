//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.folha.v1;

import java.util.ArrayList;
import java.util.List;
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
 *         <element name="nome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="senha" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="telefone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="perfilCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="funcaoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="centralizador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="tipoEntidade" type="{PapelUsuario}PapelUsuario" minOccurs="0"/>
 *         <element name="entidadeCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="entidadeMaeCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="situacaoUsuario" type="{SituacaoUsuario}SituacaoUsuario" minOccurs="0"/>
 *         <element name="novoLogin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "senha",
    "email",
    "cpf",
    "telefone",
    "perfilCodigo",
    "funcaoCodigo",
    "centralizador",
    "tipoEntidade",
    "entidadeCodigo",
    "entidadeMaeCodigo",
    "situacaoUsuario",
    "novoLogin"
})
public class Usuario {

    @XmlElement(namespace = "Usuario", required = true)
    protected java.lang.String login;
    @XmlElement(namespace = "Usuario")
    protected java.lang.String nome;
    @XmlElementRef(name = "senha", namespace = "Usuario", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> senha;
    @XmlElementRef(name = "email", namespace = "Usuario", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> email;
    @XmlElementRef(name = "cpf", namespace = "Usuario", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cpf;
    @XmlElementRef(name = "telefone", namespace = "Usuario", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> telefone;
    @XmlElementRef(name = "perfilCodigo", namespace = "Usuario", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> perfilCodigo;
    @XmlElement(namespace = "Usuario", nillable = true)
    protected List<java.lang.String> funcaoCodigo;
    @XmlElementRef(name = "centralizador", namespace = "Usuario", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> centralizador;
    @XmlElement(namespace = "Usuario")
    protected PapelUsuario tipoEntidade;
    @XmlElementRef(name = "entidadeCodigo", namespace = "Usuario", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> entidadeCodigo;
    @XmlElementRef(name = "entidadeMaeCodigo", namespace = "Usuario", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> entidadeMaeCodigo;
    @XmlElement(namespace = "Usuario")
    protected SituacaoUsuario situacaoUsuario;
    @XmlElement(namespace = "Usuario")
    protected java.lang.String novoLogin;

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
     * Obtém o valor da propriedade senha.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getSenha() {
        return senha;
    }

    /**
     * Define o valor da propriedade senha.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setSenha(JAXBElement<java.lang.String> value) {
        this.senha = value;
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
     * Obtém o valor da propriedade perfilCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getPerfilCodigo() {
        return perfilCodigo;
    }

    /**
     * Define o valor da propriedade perfilCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setPerfilCodigo(JAXBElement<java.lang.String> value) {
        this.perfilCodigo = value;
    }

    /**
     * Gets the value of the funcaoCodigo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the funcaoCodigo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFuncaoCodigo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link java.lang.String }
     * 
     * 
     * @return
     *     The value of the funcaoCodigo property.
     */
    public List<java.lang.String> getFuncaoCodigo() {
        if (funcaoCodigo == null) {
            funcaoCodigo = new ArrayList<>();
        }
        return this.funcaoCodigo;
    }

    /**
     * Obtém o valor da propriedade centralizador.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCentralizador() {
        return centralizador;
    }

    /**
     * Define o valor da propriedade centralizador.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCentralizador(JAXBElement<java.lang.String> value) {
        this.centralizador = value;
    }

    /**
     * Obtém o valor da propriedade tipoEntidade.
     * 
     * @return
     *     possible object is
     *     {@link PapelUsuario }
     *     
     */
    public PapelUsuario getTipoEntidade() {
        return tipoEntidade;
    }

    /**
     * Define o valor da propriedade tipoEntidade.
     * 
     * @param value
     *     allowed object is
     *     {@link PapelUsuario }
     *     
     */
    public void setTipoEntidade(PapelUsuario value) {
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

    /**
     * Obtém o valor da propriedade entidadeMaeCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getEntidadeMaeCodigo() {
        return entidadeMaeCodigo;
    }

    /**
     * Define o valor da propriedade entidadeMaeCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setEntidadeMaeCodigo(JAXBElement<java.lang.String> value) {
        this.entidadeMaeCodigo = value;
    }

    /**
     * Obtém o valor da propriedade situacaoUsuario.
     * 
     * @return
     *     possible object is
     *     {@link SituacaoUsuario }
     *     
     */
    public SituacaoUsuario getSituacaoUsuario() {
        return situacaoUsuario;
    }

    /**
     * Define o valor da propriedade situacaoUsuario.
     * 
     * @param value
     *     allowed object is
     *     {@link SituacaoUsuario }
     *     
     */
    public void setSituacaoUsuario(SituacaoUsuario value) {
        this.situacaoUsuario = value;
    }

    /**
     * Obtém o valor da propriedade novoLogin.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getNovoLogin() {
        return novoLogin;
    }

    /**
     * Define o valor da propriedade novoLogin.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setNovoLogin(java.lang.String value) {
        this.novoLogin = value;
    }

}
