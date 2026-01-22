//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v6;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de InfoMargem complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="InfoMargem">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="estabelecimentoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="estabelecimento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="orgaoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="orgao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="categoria" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="servidor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="matricula" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="dataNascimento" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="dataAdmissao" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="prazoServidor" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="valorMargem" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         <element name="textoMargem" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="valorMargem2" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         <element name="textoMargem2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="valorMargem3" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         <element name="textoMargem3" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="valorMargemLimite" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="identidade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="dataIdentidade" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="ufIdentidade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="emissorIdentidade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cidadeNascimento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nacionalidade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="sexo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="estadoCivil" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="endereco" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="numero" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="complemento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="bairro" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cidade" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="uf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cep" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="telefone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="celular" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="salario" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="dataSaida" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="banco" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="agencia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="conta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cargoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cargoDescricao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="habitacaoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="habitacaoDescricao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="escolaridadeCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="escolaridadeDescricao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="qtdFilhos" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InfoMargem", namespace = "InfoMargem", propOrder = {
    "estabelecimentoCodigo",
    "estabelecimento",
    "orgaoCodigo",
    "orgao",
    "categoria",
    "servidor",
    "cpf",
    "matricula",
    "dataNascimento",
    "dataAdmissao",
    "prazoServidor",
    "valorMargem",
    "textoMargem",
    "valorMargem2",
    "textoMargem2",
    "valorMargem3",
    "textoMargem3",
    "valorMargemLimite",
    "identidade",
    "dataIdentidade",
    "ufIdentidade",
    "emissorIdentidade",
    "cidadeNascimento",
    "nacionalidade",
    "sexo",
    "estadoCivil",
    "endereco",
    "numero",
    "complemento",
    "bairro",
    "cidade",
    "uf",
    "cep",
    "telefone",
    "celular",
    "salario",
    "dataSaida",
    "banco",
    "agencia",
    "conta",
    "cargoCodigo",
    "cargoDescricao",
    "habitacaoCodigo",
    "habitacaoDescricao",
    "escolaridadeCodigo",
    "escolaridadeDescricao",
    "qtdFilhos"
})
public class InfoMargem {

    @XmlElement(namespace = "InfoMargem")
    protected java.lang.String estabelecimentoCodigo;
    @XmlElement(namespace = "InfoMargem")
    protected java.lang.String estabelecimento;
    @XmlElement(namespace = "InfoMargem")
    protected java.lang.String orgaoCodigo;
    @XmlElement(namespace = "InfoMargem")
    protected java.lang.String orgao;
    @XmlElement(namespace = "InfoMargem")
    protected java.lang.String categoria;
    @XmlElement(namespace = "InfoMargem", required = true)
    protected java.lang.String servidor;
    @XmlElement(namespace = "InfoMargem")
    protected java.lang.String cpf;
    @XmlElement(namespace = "InfoMargem", required = true)
    protected java.lang.String matricula;
    @XmlElement(namespace = "InfoMargem")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataNascimento;
    @XmlElement(namespace = "InfoMargem")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataAdmissao;
    @XmlElement(namespace = "InfoMargem", required = true, type = java.lang.Integer.class, nillable = true)
    protected java.lang.Integer prazoServidor = 0;
    @XmlElement(namespace = "InfoMargem", required = true, type = java.lang.Double.class, nillable = true)
    protected java.lang.Double valorMargem;
    @XmlElement(namespace = "InfoMargem", required = true, nillable = true)
    protected java.lang.String textoMargem;
    @XmlElement(namespace = "InfoMargem", required = true, type = java.lang.Double.class, nillable = true)
    protected java.lang.Double valorMargem2;
    @XmlElement(namespace = "InfoMargem", required = true, nillable = true)
    protected java.lang.String textoMargem2;
    @XmlElement(namespace = "InfoMargem", required = true, type = java.lang.Double.class, nillable = true)
    protected java.lang.Double valorMargem3;
    @XmlElement(namespace = "InfoMargem", required = true, nillable = true)
    protected java.lang.String textoMargem3;
    @XmlElementRef(name = "valorMargemLimite", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> valorMargemLimite;
    @XmlElementRef(name = "identidade", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> identidade;
    @XmlElementRef(name = "dataIdentidade", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataIdentidade;
    @XmlElementRef(name = "ufIdentidade", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> ufIdentidade;
    @XmlElementRef(name = "emissorIdentidade", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> emissorIdentidade;
    @XmlElementRef(name = "cidadeNascimento", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cidadeNascimento;
    @XmlElementRef(name = "nacionalidade", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nacionalidade;
    @XmlElementRef(name = "sexo", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> sexo;
    @XmlElementRef(name = "estadoCivil", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> estadoCivil;
    @XmlElementRef(name = "endereco", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> endereco;
    @XmlElementRef(name = "numero", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> numero;
    @XmlElementRef(name = "complemento", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> complemento;
    @XmlElementRef(name = "bairro", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> bairro;
    @XmlElementRef(name = "cidade", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cidade;
    @XmlElementRef(name = "uf", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> uf;
    @XmlElementRef(name = "cep", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cep;
    @XmlElementRef(name = "telefone", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> telefone;
    @XmlElementRef(name = "celular", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> celular;
    @XmlElementRef(name = "salario", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> salario;
    @XmlElementRef(name = "dataSaida", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataSaida;
    @XmlElementRef(name = "banco", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> banco;
    @XmlElementRef(name = "agencia", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> agencia;
    @XmlElementRef(name = "conta", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> conta;
    @XmlElementRef(name = "cargoCodigo", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cargoCodigo;
    @XmlElementRef(name = "cargoDescricao", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cargoDescricao;
    @XmlElementRef(name = "habitacaoCodigo", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> habitacaoCodigo;
    @XmlElementRef(name = "habitacaoDescricao", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> habitacaoDescricao;
    @XmlElementRef(name = "escolaridadeCodigo", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> escolaridadeCodigo;
    @XmlElementRef(name = "escolaridadeDescricao", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> escolaridadeDescricao;
    @XmlElementRef(name = "qtdFilhos", namespace = "InfoMargem", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Integer> qtdFilhos;

    /**
     * Obtém o valor da propriedade estabelecimentoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getEstabelecimentoCodigo() {
        return estabelecimentoCodigo;
    }

    /**
     * Define o valor da propriedade estabelecimentoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setEstabelecimentoCodigo(java.lang.String value) {
        this.estabelecimentoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade estabelecimento.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getEstabelecimento() {
        return estabelecimento;
    }

    /**
     * Define o valor da propriedade estabelecimento.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setEstabelecimento(java.lang.String value) {
        this.estabelecimento = value;
    }

    /**
     * Obtém o valor da propriedade orgaoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getOrgaoCodigo() {
        return orgaoCodigo;
    }

    /**
     * Define o valor da propriedade orgaoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setOrgaoCodigo(java.lang.String value) {
        this.orgaoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade orgao.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getOrgao() {
        return orgao;
    }

    /**
     * Define o valor da propriedade orgao.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setOrgao(java.lang.String value) {
        this.orgao = value;
    }

    /**
     * Obtém o valor da propriedade categoria.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getCategoria() {
        return categoria;
    }

    /**
     * Define o valor da propriedade categoria.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setCategoria(java.lang.String value) {
        this.categoria = value;
    }

    /**
     * Obtém o valor da propriedade servidor.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getServidor() {
        return servidor;
    }

    /**
     * Define o valor da propriedade servidor.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setServidor(java.lang.String value) {
        this.servidor = value;
    }

    /**
     * Obtém o valor da propriedade cpf.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getCpf() {
        return cpf;
    }

    /**
     * Define o valor da propriedade cpf.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setCpf(java.lang.String value) {
        this.cpf = value;
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
     *     {@link java.lang.Integer }
     *     
     */
    public java.lang.Integer getPrazoServidor() {
        return prazoServidor;
    }

    /**
     * Define o valor da propriedade prazoServidor.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Integer }
     *     
     */
    public void setPrazoServidor(java.lang.Integer value) {
        this.prazoServidor = value;
    }

    /**
     * Obtém o valor da propriedade valorMargem.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Double }
     *     
     */
    public java.lang.Double getValorMargem() {
        return valorMargem;
    }

    /**
     * Define o valor da propriedade valorMargem.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Double }
     *     
     */
    public void setValorMargem(java.lang.Double value) {
        this.valorMargem = value;
    }

    /**
     * Obtém o valor da propriedade textoMargem.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getTextoMargem() {
        return textoMargem;
    }

    /**
     * Define o valor da propriedade textoMargem.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setTextoMargem(java.lang.String value) {
        this.textoMargem = value;
    }

    /**
     * Obtém o valor da propriedade valorMargem2.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Double }
     *     
     */
    public java.lang.Double getValorMargem2() {
        return valorMargem2;
    }

    /**
     * Define o valor da propriedade valorMargem2.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Double }
     *     
     */
    public void setValorMargem2(java.lang.Double value) {
        this.valorMargem2 = value;
    }

    /**
     * Obtém o valor da propriedade textoMargem2.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getTextoMargem2() {
        return textoMargem2;
    }

    /**
     * Define o valor da propriedade textoMargem2.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setTextoMargem2(java.lang.String value) {
        this.textoMargem2 = value;
    }

    /**
     * Obtém o valor da propriedade valorMargem3.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Double }
     *     
     */
    public java.lang.Double getValorMargem3() {
        return valorMargem3;
    }

    /**
     * Define o valor da propriedade valorMargem3.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Double }
     *     
     */
    public void setValorMargem3(java.lang.Double value) {
        this.valorMargem3 = value;
    }

    /**
     * Obtém o valor da propriedade textoMargem3.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getTextoMargem3() {
        return textoMargem3;
    }

    /**
     * Define o valor da propriedade textoMargem3.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setTextoMargem3(java.lang.String value) {
        this.textoMargem3 = value;
    }

    /**
     * Obtém o valor da propriedade valorMargemLimite.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getValorMargemLimite() {
        return valorMargemLimite;
    }

    /**
     * Define o valor da propriedade valorMargemLimite.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setValorMargemLimite(JAXBElement<java.lang.Double> value) {
        this.valorMargemLimite = value;
    }

    /**
     * Obtém o valor da propriedade identidade.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getIdentidade() {
        return identidade;
    }

    /**
     * Define o valor da propriedade identidade.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setIdentidade(JAXBElement<java.lang.String> value) {
        this.identidade = value;
    }

    /**
     * Obtém o valor da propriedade dataIdentidade.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataIdentidade() {
        return dataIdentidade;
    }

    /**
     * Define o valor da propriedade dataIdentidade.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataIdentidade(JAXBElement<XMLGregorianCalendar> value) {
        this.dataIdentidade = value;
    }

    /**
     * Obtém o valor da propriedade ufIdentidade.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getUfIdentidade() {
        return ufIdentidade;
    }

    /**
     * Define o valor da propriedade ufIdentidade.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setUfIdentidade(JAXBElement<java.lang.String> value) {
        this.ufIdentidade = value;
    }

    /**
     * Obtém o valor da propriedade emissorIdentidade.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getEmissorIdentidade() {
        return emissorIdentidade;
    }

    /**
     * Define o valor da propriedade emissorIdentidade.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setEmissorIdentidade(JAXBElement<java.lang.String> value) {
        this.emissorIdentidade = value;
    }

    /**
     * Obtém o valor da propriedade cidadeNascimento.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCidadeNascimento() {
        return cidadeNascimento;
    }

    /**
     * Define o valor da propriedade cidadeNascimento.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCidadeNascimento(JAXBElement<java.lang.String> value) {
        this.cidadeNascimento = value;
    }

    /**
     * Obtém o valor da propriedade nacionalidade.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNacionalidade() {
        return nacionalidade;
    }

    /**
     * Define o valor da propriedade nacionalidade.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNacionalidade(JAXBElement<java.lang.String> value) {
        this.nacionalidade = value;
    }

    /**
     * Obtém o valor da propriedade sexo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getSexo() {
        return sexo;
    }

    /**
     * Define o valor da propriedade sexo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setSexo(JAXBElement<java.lang.String> value) {
        this.sexo = value;
    }

    /**
     * Obtém o valor da propriedade estadoCivil.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getEstadoCivil() {
        return estadoCivil;
    }

    /**
     * Define o valor da propriedade estadoCivil.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setEstadoCivil(JAXBElement<java.lang.String> value) {
        this.estadoCivil = value;
    }

    /**
     * Obtém o valor da propriedade endereco.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getEndereco() {
        return endereco;
    }

    /**
     * Define o valor da propriedade endereco.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setEndereco(JAXBElement<java.lang.String> value) {
        this.endereco = value;
    }

    /**
     * Obtém o valor da propriedade numero.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNumero() {
        return numero;
    }

    /**
     * Define o valor da propriedade numero.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNumero(JAXBElement<java.lang.String> value) {
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
     * Obtém o valor da propriedade celular.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCelular() {
        return celular;
    }

    /**
     * Define o valor da propriedade celular.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCelular(JAXBElement<java.lang.String> value) {
        this.celular = value;
    }

    /**
     * Obtém o valor da propriedade salario.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getSalario() {
        return salario;
    }

    /**
     * Define o valor da propriedade salario.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setSalario(JAXBElement<java.lang.Double> value) {
        this.salario = value;
    }

    /**
     * Obtém o valor da propriedade dataSaida.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataSaida() {
        return dataSaida;
    }

    /**
     * Define o valor da propriedade dataSaida.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataSaida(JAXBElement<XMLGregorianCalendar> value) {
        this.dataSaida = value;
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
     * Obtém o valor da propriedade cargoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCargoCodigo() {
        return cargoCodigo;
    }

    /**
     * Define o valor da propriedade cargoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCargoCodigo(JAXBElement<java.lang.String> value) {
        this.cargoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade cargoDescricao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCargoDescricao() {
        return cargoDescricao;
    }

    /**
     * Define o valor da propriedade cargoDescricao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCargoDescricao(JAXBElement<java.lang.String> value) {
        this.cargoDescricao = value;
    }

    /**
     * Obtém o valor da propriedade habitacaoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getHabitacaoCodigo() {
        return habitacaoCodigo;
    }

    /**
     * Define o valor da propriedade habitacaoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setHabitacaoCodigo(JAXBElement<java.lang.String> value) {
        this.habitacaoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade habitacaoDescricao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getHabitacaoDescricao() {
        return habitacaoDescricao;
    }

    /**
     * Define o valor da propriedade habitacaoDescricao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setHabitacaoDescricao(JAXBElement<java.lang.String> value) {
        this.habitacaoDescricao = value;
    }

    /**
     * Obtém o valor da propriedade escolaridadeCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getEscolaridadeCodigo() {
        return escolaridadeCodigo;
    }

    /**
     * Define o valor da propriedade escolaridadeCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setEscolaridadeCodigo(JAXBElement<java.lang.String> value) {
        this.escolaridadeCodigo = value;
    }

    /**
     * Obtém o valor da propriedade escolaridadeDescricao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getEscolaridadeDescricao() {
        return escolaridadeDescricao;
    }

    /**
     * Define o valor da propriedade escolaridadeDescricao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setEscolaridadeDescricao(JAXBElement<java.lang.String> value) {
        this.escolaridadeDescricao = value;
    }

    /**
     * Obtém o valor da propriedade qtdFilhos.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *     
     */
    public JAXBElement<java.lang.Integer> getQtdFilhos() {
        return qtdFilhos;
    }

    /**
     * Define o valor da propriedade qtdFilhos.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *     
     */
    public void setQtdFilhos(JAXBElement<java.lang.Integer> value) {
        this.qtdFilhos = value;
    }

}
