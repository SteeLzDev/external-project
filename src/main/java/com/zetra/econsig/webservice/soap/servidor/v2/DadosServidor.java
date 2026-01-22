//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.servidor.v2;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de DadosServidor complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="DadosServidor">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="servidor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="matricula" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="estabelecimentoCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="estabelecimento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="orgaoCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="orgao" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="sexo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="dataNascimento" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         <element name="estadoCivil" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="identidade" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="pai" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="mae" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="endereco" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="numero" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="complemento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="bairro" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="cidade" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="uf" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="cep" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="telefone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="celular" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="dataAdmissao" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         <element name="prazoServidor" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="nacionalidade" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="carteiraTrabalho" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="pis" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="categoria" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DadosServidor", namespace = "DadosServidor", propOrder = {
    "servidor",
    "cpf",
    "matricula",
    "estabelecimentoCodigo",
    "estabelecimento",
    "orgaoCodigo",
    "orgao",
    "sexo",
    "dataNascimento",
    "estadoCivil",
    "identidade",
    "pai",
    "mae",
    "endereco",
    "numero",
    "complemento",
    "bairro",
    "cidade",
    "uf",
    "cep",
    "telefone",
    "celular",
    "dataAdmissao",
    "prazoServidor",
    "nacionalidade",
    "carteiraTrabalho",
    "pis",
    "categoria",
    "email"
})
public class DadosServidor {

    @XmlElement(namespace = "DadosServidor", required = true)
    protected String servidor;
    @XmlElement(namespace = "DadosServidor", required = true)
    protected String cpf;
    @XmlElement(namespace = "DadosServidor", required = true)
    protected String matricula;
    @XmlElement(namespace = "DadosServidor", required = true)
    protected String estabelecimentoCodigo;
    @XmlElement(namespace = "DadosServidor", required = true)
    protected String estabelecimento;
    @XmlElement(namespace = "DadosServidor", required = true)
    protected String orgaoCodigo;
    @XmlElement(namespace = "DadosServidor", required = true)
    protected String orgao;
    @XmlElement(namespace = "DadosServidor", required = true, nillable = true)
    protected String sexo;
    @XmlElement(namespace = "DadosServidor", required = true, nillable = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataNascimento;
    @XmlElement(namespace = "DadosServidor", required = true, nillable = true)
    protected String estadoCivil;
    @XmlElement(namespace = "DadosServidor", required = true, nillable = true)
    protected String identidade;
    @XmlElement(namespace = "DadosServidor", required = true, nillable = true)
    protected String pai;
    @XmlElement(namespace = "DadosServidor", required = true, nillable = true)
    protected String mae;
    @XmlElement(namespace = "DadosServidor", required = true, nillable = true)
    protected String endereco;
    @XmlElement(namespace = "DadosServidor", required = true, nillable = true)
    protected String numero;
    @XmlElement(namespace = "DadosServidor", required = true, nillable = true)
    protected String complemento;
    @XmlElement(namespace = "DadosServidor", required = true, nillable = true)
    protected String bairro;
    @XmlElement(namespace = "DadosServidor", required = true, nillable = true)
    protected String cidade;
    @XmlElement(namespace = "DadosServidor", required = true, nillable = true)
    protected String uf;
    @XmlElement(namespace = "DadosServidor", required = true, nillable = true)
    protected String cep;
    @XmlElement(namespace = "DadosServidor", required = true, nillable = true)
    protected String telefone;
    @XmlElement(namespace = "DadosServidor", required = true, nillable = true)
    protected String celular;
    @XmlElement(namespace = "DadosServidor", required = true, nillable = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataAdmissao;
    @XmlElement(namespace = "DadosServidor", required = true, type = Integer.class, nillable = true)
    protected Integer prazoServidor;
    @XmlElement(namespace = "DadosServidor", required = true, nillable = true)
    protected String nacionalidade;
    @XmlElement(namespace = "DadosServidor", required = true, nillable = true)
    protected String carteiraTrabalho;
    @XmlElement(namespace = "DadosServidor", required = true, nillable = true)
    protected String pis;
    @XmlElement(namespace = "DadosServidor", required = true, nillable = true)
    protected String categoria;
    @XmlElementRef(name = "email", namespace = "DadosServidor", type = JAXBElement.class, required = false)
    protected JAXBElement<String> email;

    /**
     * Obtém o valor da propriedade servidor.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServidor() {
        return servidor;
    }

    /**
     * Define o valor da propriedade servidor.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServidor(String value) {
        this.servidor = value;
    }

    /**
     * Obtém o valor da propriedade cpf.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Define o valor da propriedade cpf.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCpf(String value) {
        this.cpf = value;
    }

    /**
     * Obtém o valor da propriedade matricula.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMatricula() {
        return matricula;
    }

    /**
     * Define o valor da propriedade matricula.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMatricula(String value) {
        this.matricula = value;
    }

    /**
     * Obtém o valor da propriedade estabelecimentoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstabelecimentoCodigo() {
        return estabelecimentoCodigo;
    }

    /**
     * Define o valor da propriedade estabelecimentoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstabelecimentoCodigo(String value) {
        this.estabelecimentoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade estabelecimento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstabelecimento() {
        return estabelecimento;
    }

    /**
     * Define o valor da propriedade estabelecimento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstabelecimento(String value) {
        this.estabelecimento = value;
    }

    /**
     * Obtém o valor da propriedade orgaoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgaoCodigo() {
        return orgaoCodigo;
    }

    /**
     * Define o valor da propriedade orgaoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgaoCodigo(String value) {
        this.orgaoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade orgao.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgao() {
        return orgao;
    }

    /**
     * Define o valor da propriedade orgao.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgao(String value) {
        this.orgao = value;
    }

    /**
     * Obtém o valor da propriedade sexo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSexo() {
        return sexo;
    }

    /**
     * Define o valor da propriedade sexo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSexo(String value) {
        this.sexo = value;
    }

    /**
     * Obtém o valor da propriedade dataNascimento.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataNascimento() {
        return dataNascimento;
    }

    /**
     * Define o valor da propriedade dataNascimento.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataNascimento(XMLGregorianCalendar value) {
        this.dataNascimento = value;
    }

    /**
     * Obtém o valor da propriedade estadoCivil.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstadoCivil() {
        return estadoCivil;
    }

    /**
     * Define o valor da propriedade estadoCivil.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstadoCivil(String value) {
        this.estadoCivil = value;
    }

    /**
     * Obtém o valor da propriedade identidade.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentidade() {
        return identidade;
    }

    /**
     * Define o valor da propriedade identidade.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentidade(String value) {
        this.identidade = value;
    }

    /**
     * Obtém o valor da propriedade pai.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPai() {
        return pai;
    }

    /**
     * Define o valor da propriedade pai.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPai(String value) {
        this.pai = value;
    }

    /**
     * Obtém o valor da propriedade mae.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMae() {
        return mae;
    }

    /**
     * Define o valor da propriedade mae.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMae(String value) {
        this.mae = value;
    }

    /**
     * Obtém o valor da propriedade endereco.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndereco() {
        return endereco;
    }

    /**
     * Define o valor da propriedade endereco.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndereco(String value) {
        this.endereco = value;
    }

    /**
     * Obtém o valor da propriedade numero.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Define o valor da propriedade numero.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumero(String value) {
        this.numero = value;
    }

    /**
     * Obtém o valor da propriedade complemento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComplemento() {
        return complemento;
    }

    /**
     * Define o valor da propriedade complemento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComplemento(String value) {
        this.complemento = value;
    }

    /**
     * Obtém o valor da propriedade bairro.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBairro() {
        return bairro;
    }

    /**
     * Define o valor da propriedade bairro.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBairro(String value) {
        this.bairro = value;
    }

    /**
     * Obtém o valor da propriedade cidade.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCidade() {
        return cidade;
    }

    /**
     * Define o valor da propriedade cidade.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCidade(String value) {
        this.cidade = value;
    }

    /**
     * Obtém o valor da propriedade uf.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUf() {
        return uf;
    }

    /**
     * Define o valor da propriedade uf.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUf(String value) {
        this.uf = value;
    }

    /**
     * Obtém o valor da propriedade cep.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCep() {
        return cep;
    }

    /**
     * Define o valor da propriedade cep.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCep(String value) {
        this.cep = value;
    }

    /**
     * Obtém o valor da propriedade telefone.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelefone() {
        return telefone;
    }

    /**
     * Define o valor da propriedade telefone.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelefone(String value) {
        this.telefone = value;
    }

    /**
     * Obtém o valor da propriedade celular.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCelular() {
        return celular;
    }

    /**
     * Define o valor da propriedade celular.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCelular(String value) {
        this.celular = value;
    }

    /**
     * Obtém o valor da propriedade dataAdmissao.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataAdmissao() {
        return dataAdmissao;
    }

    /**
     * Define o valor da propriedade dataAdmissao.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataAdmissao(XMLGregorianCalendar value) {
        this.dataAdmissao = value;
    }

    /**
     * Obtém o valor da propriedade prazoServidor.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPrazoServidor() {
        return prazoServidor;
    }

    /**
     * Define o valor da propriedade prazoServidor.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPrazoServidor(Integer value) {
        this.prazoServidor = value;
    }

    /**
     * Obtém o valor da propriedade nacionalidade.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNacionalidade() {
        return nacionalidade;
    }

    /**
     * Define o valor da propriedade nacionalidade.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNacionalidade(String value) {
        this.nacionalidade = value;
    }

    /**
     * Obtém o valor da propriedade carteiraTrabalho.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCarteiraTrabalho() {
        return carteiraTrabalho;
    }

    /**
     * Define o valor da propriedade carteiraTrabalho.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCarteiraTrabalho(String value) {
        this.carteiraTrabalho = value;
    }

    /**
     * Obtém o valor da propriedade pis.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPis() {
        return pis;
    }

    /**
     * Define o valor da propriedade pis.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPis(String value) {
        this.pis = value;
    }

    /**
     * Obtém o valor da propriedade categoria.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCategoria() {
        return categoria;
    }

    /**
     * Define o valor da propriedade categoria.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCategoria(String value) {
        this.categoria = value;
    }

    /**
     * Obtém o valor da propriedade email.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEmail() {
        return email;
    }

    /**
     * Define o valor da propriedade email.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEmail(JAXBElement<String> value) {
        this.email = value;
    }

}
