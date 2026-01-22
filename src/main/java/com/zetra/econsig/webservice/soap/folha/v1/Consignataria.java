//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.folha.v1;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Consignataria complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="Consignataria">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="codigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="nome" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cnpj" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="responsavel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="logradouro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="numero" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="complemento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="bairro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cidade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="uf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cep" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="telefone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="fax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="numeroBanco" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="numeroConta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="numeroAgencia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="digitoConta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ativo" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         <element name="responsavel2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="responsavel3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cargoResponsavel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cargoResponsavel2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cargoResponsavel3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="telefoneResponsavel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="telefoneResponsavel2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="telefoneResponsavel3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="textoContato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="contato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="telefoneContato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="endereco2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nomeAbreviado" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="grupoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codigoInterno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="dataExpiracao" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         <element name="numeroContrato" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ipAcesso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ddnsAcesso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="exigeEnderecoAcesso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="unidadeOrganizacional" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="naturezaCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Consignataria", namespace = "Consignataria", propOrder = {
    "codigo",
    "nome",
    "email",
    "cnpj",
    "responsavel",
    "logradouro",
    "numero",
    "complemento",
    "bairro",
    "cidade",
    "uf",
    "cep",
    "telefone",
    "fax",
    "numeroBanco",
    "numeroConta",
    "numeroAgencia",
    "digitoConta",
    "ativo",
    "responsavel2",
    "responsavel3",
    "cargoResponsavel",
    "cargoResponsavel2",
    "cargoResponsavel3",
    "telefoneResponsavel",
    "telefoneResponsavel2",
    "telefoneResponsavel3",
    "textoContato",
    "contato",
    "telefoneContato",
    "endereco2",
    "nomeAbreviado",
    "grupoCodigo",
    "codigoInterno",
    "dataExpiracao",
    "numeroContrato",
    "ipAcesso",
    "ddnsAcesso",
    "exigeEnderecoAcesso",
    "unidadeOrganizacional",
    "naturezaCodigo"
})
public class Consignataria {

    @XmlElement(namespace = "Consignataria", required = true)
    protected java.lang.String codigo;
    @XmlElement(namespace = "Consignataria", required = true)
    protected java.lang.String nome;
    @XmlElementRef(name = "email", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> email;
    @XmlElementRef(name = "cnpj", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cnpj;
    @XmlElementRef(name = "responsavel", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> responsavel;
    @XmlElementRef(name = "logradouro", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> logradouro;
    @XmlElementRef(name = "numero", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Integer> numero;
    @XmlElementRef(name = "complemento", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> complemento;
    @XmlElementRef(name = "bairro", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> bairro;
    @XmlElementRef(name = "cidade", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cidade;
    @XmlElementRef(name = "uf", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> uf;
    @XmlElement(namespace = "Consignataria")
    protected java.lang.String cep;
    @XmlElement(namespace = "Consignataria")
    protected java.lang.String telefone;
    @XmlElementRef(name = "fax", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> fax;
    @XmlElementRef(name = "numeroBanco", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> numeroBanco;
    @XmlElementRef(name = "numeroConta", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> numeroConta;
    @XmlElementRef(name = "numeroAgencia", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> numeroAgencia;
    @XmlElementRef(name = "digitoConta", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> digitoConta;
    @XmlElementRef(name = "ativo", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Short> ativo;
    @XmlElementRef(name = "responsavel2", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> responsavel2;
    @XmlElementRef(name = "responsavel3", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> responsavel3;
    @XmlElementRef(name = "cargoResponsavel", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cargoResponsavel;
    @XmlElementRef(name = "cargoResponsavel2", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cargoResponsavel2;
    @XmlElementRef(name = "cargoResponsavel3", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cargoResponsavel3;
    @XmlElementRef(name = "telefoneResponsavel", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> telefoneResponsavel;
    @XmlElementRef(name = "telefoneResponsavel2", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> telefoneResponsavel2;
    @XmlElementRef(name = "telefoneResponsavel3", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> telefoneResponsavel3;
    @XmlElementRef(name = "textoContato", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> textoContato;
    @XmlElementRef(name = "contato", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> contato;
    @XmlElementRef(name = "telefoneContato", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> telefoneContato;
    @XmlElementRef(name = "endereco2", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> endereco2;
    @XmlElementRef(name = "nomeAbreviado", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nomeAbreviado;
    @XmlElement(namespace = "Consignataria")
    protected java.lang.String grupoCodigo;
    @XmlElement(namespace = "Consignataria")
    protected java.lang.String codigoInterno;
    @XmlElementRef(name = "dataExpiracao", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataExpiracao;
    @XmlElementRef(name = "numeroContrato", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> numeroContrato;
    @XmlElementRef(name = "ipAcesso", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> ipAcesso;
    @XmlElementRef(name = "ddnsAcesso", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> ddnsAcesso;
    @XmlElementRef(name = "exigeEnderecoAcesso", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> exigeEnderecoAcesso;
    @XmlElementRef(name = "unidadeOrganizacional", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> unidadeOrganizacional;
    @XmlElementRef(name = "naturezaCodigo", namespace = "Consignataria", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> naturezaCodigo;

    /**
     * Obtém o valor da propriedade codigo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getCodigo() {
        return codigo;
    }

    /**
     * Define o valor da propriedade codigo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setCodigo(java.lang.String value) {
        this.codigo = value;
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
     * Obtém o valor da propriedade cnpj.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCnpj() {
        return cnpj;
    }

    /**
     * Define o valor da propriedade cnpj.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCnpj(JAXBElement<java.lang.String> value) {
        this.cnpj = value;
    }

    /**
     * Obtém o valor da propriedade responsavel.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getResponsavel() {
        return responsavel;
    }

    /**
     * Define o valor da propriedade responsavel.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setResponsavel(JAXBElement<java.lang.String> value) {
        this.responsavel = value;
    }

    /**
     * Obtém o valor da propriedade logradouro.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getLogradouro() {
        return logradouro;
    }

    /**
     * Define o valor da propriedade logradouro.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setLogradouro(JAXBElement<java.lang.String> value) {
        this.logradouro = value;
    }

    /**
     * Obtém o valor da propriedade numero.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *     
     */
    public JAXBElement<java.lang.Integer> getNumero() {
        return numero;
    }

    /**
     * Define o valor da propriedade numero.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *     
     */
    public void setNumero(JAXBElement<java.lang.Integer> value) {
        this.numero = value;
    }

    /**
     * Obtém o valor da propriedade complemento.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getComplemento() {
        return complemento;
    }

    /**
     * Define o valor da propriedade complemento.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setComplemento(JAXBElement<java.lang.String> value) {
        this.complemento = value;
    }

    /**
     * Obtém o valor da propriedade bairro.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getBairro() {
        return bairro;
    }

    /**
     * Define o valor da propriedade bairro.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setBairro(JAXBElement<java.lang.String> value) {
        this.bairro = value;
    }

    /**
     * Obtém o valor da propriedade cidade.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCidade() {
        return cidade;
    }

    /**
     * Define o valor da propriedade cidade.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCidade(JAXBElement<java.lang.String> value) {
        this.cidade = value;
    }

    /**
     * Obtém o valor da propriedade uf.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getUf() {
        return uf;
    }

    /**
     * Define o valor da propriedade uf.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setUf(JAXBElement<java.lang.String> value) {
        this.uf = value;
    }

    /**
     * Obtém o valor da propriedade cep.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getCep() {
        return cep;
    }

    /**
     * Define o valor da propriedade cep.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setCep(java.lang.String value) {
        this.cep = value;
    }

    /**
     * Obtém o valor da propriedade telefone.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getTelefone() {
        return telefone;
    }

    /**
     * Define o valor da propriedade telefone.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setTelefone(java.lang.String value) {
        this.telefone = value;
    }

    /**
     * Obtém o valor da propriedade fax.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getFax() {
        return fax;
    }

    /**
     * Define o valor da propriedade fax.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setFax(JAXBElement<java.lang.String> value) {
        this.fax = value;
    }

    /**
     * Obtém o valor da propriedade numeroBanco.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNumeroBanco() {
        return numeroBanco;
    }

    /**
     * Define o valor da propriedade numeroBanco.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNumeroBanco(JAXBElement<java.lang.String> value) {
        this.numeroBanco = value;
    }

    /**
     * Obtém o valor da propriedade numeroConta.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNumeroConta() {
        return numeroConta;
    }

    /**
     * Define o valor da propriedade numeroConta.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNumeroConta(JAXBElement<java.lang.String> value) {
        this.numeroConta = value;
    }

    /**
     * Obtém o valor da propriedade numeroAgencia.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNumeroAgencia() {
        return numeroAgencia;
    }

    /**
     * Define o valor da propriedade numeroAgencia.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNumeroAgencia(JAXBElement<java.lang.String> value) {
        this.numeroAgencia = value;
    }

    /**
     * Obtém o valor da propriedade digitoConta.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getDigitoConta() {
        return digitoConta;
    }

    /**
     * Define o valor da propriedade digitoConta.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setDigitoConta(JAXBElement<java.lang.String> value) {
        this.digitoConta = value;
    }

    /**
     * Obtém o valor da propriedade ativo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public JAXBElement<java.lang.Short> getAtivo() {
        return ativo;
    }

    /**
     * Define o valor da propriedade ativo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public void setAtivo(JAXBElement<java.lang.Short> value) {
        this.ativo = value;
    }

    /**
     * Obtém o valor da propriedade responsavel2.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getResponsavel2() {
        return responsavel2;
    }

    /**
     * Define o valor da propriedade responsavel2.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setResponsavel2(JAXBElement<java.lang.String> value) {
        this.responsavel2 = value;
    }

    /**
     * Obtém o valor da propriedade responsavel3.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getResponsavel3() {
        return responsavel3;
    }

    /**
     * Define o valor da propriedade responsavel3.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setResponsavel3(JAXBElement<java.lang.String> value) {
        this.responsavel3 = value;
    }

    /**
     * Obtém o valor da propriedade cargoResponsavel.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCargoResponsavel() {
        return cargoResponsavel;
    }

    /**
     * Define o valor da propriedade cargoResponsavel.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCargoResponsavel(JAXBElement<java.lang.String> value) {
        this.cargoResponsavel = value;
    }

    /**
     * Obtém o valor da propriedade cargoResponsavel2.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCargoResponsavel2() {
        return cargoResponsavel2;
    }

    /**
     * Define o valor da propriedade cargoResponsavel2.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCargoResponsavel2(JAXBElement<java.lang.String> value) {
        this.cargoResponsavel2 = value;
    }

    /**
     * Obtém o valor da propriedade cargoResponsavel3.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCargoResponsavel3() {
        return cargoResponsavel3;
    }

    /**
     * Define o valor da propriedade cargoResponsavel3.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCargoResponsavel3(JAXBElement<java.lang.String> value) {
        this.cargoResponsavel3 = value;
    }

    /**
     * Obtém o valor da propriedade telefoneResponsavel.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getTelefoneResponsavel() {
        return telefoneResponsavel;
    }

    /**
     * Define o valor da propriedade telefoneResponsavel.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setTelefoneResponsavel(JAXBElement<java.lang.String> value) {
        this.telefoneResponsavel = value;
    }

    /**
     * Obtém o valor da propriedade telefoneResponsavel2.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getTelefoneResponsavel2() {
        return telefoneResponsavel2;
    }

    /**
     * Define o valor da propriedade telefoneResponsavel2.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setTelefoneResponsavel2(JAXBElement<java.lang.String> value) {
        this.telefoneResponsavel2 = value;
    }

    /**
     * Obtém o valor da propriedade telefoneResponsavel3.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getTelefoneResponsavel3() {
        return telefoneResponsavel3;
    }

    /**
     * Define o valor da propriedade telefoneResponsavel3.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setTelefoneResponsavel3(JAXBElement<java.lang.String> value) {
        this.telefoneResponsavel3 = value;
    }

    /**
     * Obtém o valor da propriedade textoContato.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getTextoContato() {
        return textoContato;
    }

    /**
     * Define o valor da propriedade textoContato.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setTextoContato(JAXBElement<java.lang.String> value) {
        this.textoContato = value;
    }

    /**
     * Obtém o valor da propriedade contato.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getContato() {
        return contato;
    }

    /**
     * Define o valor da propriedade contato.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setContato(JAXBElement<java.lang.String> value) {
        this.contato = value;
    }

    /**
     * Obtém o valor da propriedade telefoneContato.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getTelefoneContato() {
        return telefoneContato;
    }

    /**
     * Define o valor da propriedade telefoneContato.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setTelefoneContato(JAXBElement<java.lang.String> value) {
        this.telefoneContato = value;
    }

    /**
     * Obtém o valor da propriedade endereco2.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getEndereco2() {
        return endereco2;
    }

    /**
     * Define o valor da propriedade endereco2.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setEndereco2(JAXBElement<java.lang.String> value) {
        this.endereco2 = value;
    }

    /**
     * Obtém o valor da propriedade nomeAbreviado.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNomeAbreviado() {
        return nomeAbreviado;
    }

    /**
     * Define o valor da propriedade nomeAbreviado.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNomeAbreviado(JAXBElement<java.lang.String> value) {
        this.nomeAbreviado = value;
    }

    /**
     * Obtém o valor da propriedade grupoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getGrupoCodigo() {
        return grupoCodigo;
    }

    /**
     * Define o valor da propriedade grupoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setGrupoCodigo(java.lang.String value) {
        this.grupoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade codigoInterno.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getCodigoInterno() {
        return codigoInterno;
    }

    /**
     * Define o valor da propriedade codigoInterno.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setCodigoInterno(java.lang.String value) {
        this.codigoInterno = value;
    }

    /**
     * Obtém o valor da propriedade dataExpiracao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataExpiracao() {
        return dataExpiracao;
    }

    /**
     * Define o valor da propriedade dataExpiracao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataExpiracao(JAXBElement<XMLGregorianCalendar> value) {
        this.dataExpiracao = value;
    }

    /**
     * Obtém o valor da propriedade numeroContrato.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNumeroContrato() {
        return numeroContrato;
    }

    /**
     * Define o valor da propriedade numeroContrato.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNumeroContrato(JAXBElement<java.lang.String> value) {
        this.numeroContrato = value;
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
     * Obtém o valor da propriedade ddnsAcesso.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getDdnsAcesso() {
        return ddnsAcesso;
    }

    /**
     * Define o valor da propriedade ddnsAcesso.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setDdnsAcesso(JAXBElement<java.lang.String> value) {
        this.ddnsAcesso = value;
    }

    /**
     * Obtém o valor da propriedade exigeEnderecoAcesso.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getExigeEnderecoAcesso() {
        return exigeEnderecoAcesso;
    }

    /**
     * Define o valor da propriedade exigeEnderecoAcesso.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setExigeEnderecoAcesso(JAXBElement<java.lang.String> value) {
        this.exigeEnderecoAcesso = value;
    }

    /**
     * Obtém o valor da propriedade unidadeOrganizacional.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    /**
     * Define o valor da propriedade unidadeOrganizacional.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setUnidadeOrganizacional(JAXBElement<java.lang.String> value) {
        this.unidadeOrganizacional = value;
    }

    /**
     * Obtém o valor da propriedade naturezaCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNaturezaCodigo() {
        return naturezaCodigo;
    }

    /**
     * Define o valor da propriedade naturezaCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNaturezaCodigo(JAXBElement<java.lang.String> value) {
        this.naturezaCodigo = value;
    }

}
