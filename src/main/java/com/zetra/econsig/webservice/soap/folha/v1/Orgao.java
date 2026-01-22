//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.folha.v1;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Orgao complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="Orgao">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="codigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="nome" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="codigoEstabelecimento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="nomeAbreviado" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cnpj" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="responsavel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="numero" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="logradouro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="complemento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="bairro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cidade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="uf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cep" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="telefone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="fax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="diaCorte" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="diaRepasse" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="ativo" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         <element name="responsavel2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="responsavel3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cargoResponsavel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cargoResponsavel2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cargoResponsavel3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="telefoneResponsavel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="telefoneResponsavel2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="telefoneResponsavel3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ipAcesso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ddnsAcesso" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Orgao", namespace = "Orgao", propOrder = {
    "codigo",
    "nome",
    "codigoEstabelecimento",
    "nomeAbreviado",
    "cnpj",
    "email",
    "responsavel",
    "numero",
    "logradouro",
    "complemento",
    "bairro",
    "cidade",
    "uf",
    "cep",
    "telefone",
    "fax",
    "diaCorte",
    "diaRepasse",
    "ativo",
    "responsavel2",
    "responsavel3",
    "cargoResponsavel",
    "cargoResponsavel2",
    "cargoResponsavel3",
    "telefoneResponsavel",
    "telefoneResponsavel2",
    "telefoneResponsavel3",
    "ipAcesso",
    "ddnsAcesso"
})
public class Orgao {

    @XmlElement(namespace = "Orgao", required = true)
    protected java.lang.String codigo;
    @XmlElement(namespace = "Orgao", required = true)
    protected java.lang.String nome;
    @XmlElement(namespace = "Orgao", required = true)
    protected java.lang.String codigoEstabelecimento;
    @XmlElementRef(name = "nomeAbreviado", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nomeAbreviado;
    @XmlElementRef(name = "cnpj", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cnpj;
    @XmlElementRef(name = "email", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> email;
    @XmlElementRef(name = "responsavel", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> responsavel;
    @XmlElementRef(name = "numero", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Integer> numero;
    @XmlElementRef(name = "logradouro", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> logradouro;
    @XmlElementRef(name = "complemento", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> complemento;
    @XmlElementRef(name = "bairro", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> bairro;
    @XmlElementRef(name = "cidade", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cidade;
    @XmlElementRef(name = "uf", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> uf;
    @XmlElementRef(name = "cep", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cep;
    @XmlElementRef(name = "telefone", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> telefone;
    @XmlElementRef(name = "fax", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> fax;
    @XmlElementRef(name = "diaCorte", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Integer> diaCorte;
    @XmlElementRef(name = "diaRepasse", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Integer> diaRepasse;
    @XmlElementRef(name = "ativo", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Short> ativo;
    @XmlElementRef(name = "responsavel2", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> responsavel2;
    @XmlElementRef(name = "responsavel3", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> responsavel3;
    @XmlElementRef(name = "cargoResponsavel", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cargoResponsavel;
    @XmlElementRef(name = "cargoResponsavel2", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cargoResponsavel2;
    @XmlElementRef(name = "cargoResponsavel3", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cargoResponsavel3;
    @XmlElementRef(name = "telefoneResponsavel", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> telefoneResponsavel;
    @XmlElementRef(name = "telefoneResponsavel2", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> telefoneResponsavel2;
    @XmlElementRef(name = "telefoneResponsavel3", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> telefoneResponsavel3;
    @XmlElementRef(name = "ipAcesso", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> ipAcesso;
    @XmlElementRef(name = "ddnsAcesso", namespace = "Orgao", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> ddnsAcesso;

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
     * Obtém o valor da propriedade codigoEstabelecimento.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getCodigoEstabelecimento() {
        return codigoEstabelecimento;
    }

    /**
     * Define o valor da propriedade codigoEstabelecimento.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setCodigoEstabelecimento(java.lang.String value) {
        this.codigoEstabelecimento = value;
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
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCep() {
        return cep;
    }

    /**
     * Define o valor da propriedade cep.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCep(JAXBElement<java.lang.String> value) {
        this.cep = value;
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
     * Obtém o valor da propriedade diaCorte.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *     
     */
    public JAXBElement<java.lang.Integer> getDiaCorte() {
        return diaCorte;
    }

    /**
     * Define o valor da propriedade diaCorte.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *     
     */
    public void setDiaCorte(JAXBElement<java.lang.Integer> value) {
        this.diaCorte = value;
    }

    /**
     * Obtém o valor da propriedade diaRepasse.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *     
     */
    public JAXBElement<java.lang.Integer> getDiaRepasse() {
        return diaRepasse;
    }

    /**
     * Define o valor da propriedade diaRepasse.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *     
     */
    public void setDiaRepasse(JAXBElement<java.lang.Integer> value) {
        this.diaRepasse = value;
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

}
