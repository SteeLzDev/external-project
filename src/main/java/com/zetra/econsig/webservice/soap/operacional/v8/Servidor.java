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
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Servidor complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="Servidor">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="estabelecimentoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="estabelecimento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="orgaoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="orgao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="categoria" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="servidor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="matricula" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="dataNascimento" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="dataAdmissao" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="prazoServidor" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="situacaoServidor" type="{SituacaoServidor}SituacaoServidor" minOccurs="0"/>
 *         <element name="margens" type="{Margem}Margem" maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="salarioLiquido" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="salarioBruto" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
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
 *         <element name="postoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="postoDescricao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nomeMae" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nomePai" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cartProf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="pis" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="email" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nomeConjuge" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nomeMeio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ultimoNome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="primeiroNome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="descontosComp" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="descontosFacu" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="outrosDescontos" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="matriculaInst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="dataRetorno" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="qtdFilhos" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="dados" type="{DadoAdicional}DadoAdicional" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Servidor", namespace = "Servidor", propOrder = {
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
    "situacaoServidor",
    "margens",
    "salarioLiquido",
    "salarioBruto",
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
    "postoCodigo",
    "postoDescricao",
    "nomeMae",
    "nomePai",
    "cartProf",
    "pis",
    "email",
    "nomeConjuge",
    "nomeMeio",
    "ultimoNome",
    "primeiroNome",
    "descontosComp",
    "descontosFacu",
    "outrosDescontos",
    "matriculaInst",
    "dataRetorno",
    "qtdFilhos",
    "dados"
})
public class Servidor {

    @XmlElement(namespace = "Servidor")
    protected java.lang.String estabelecimentoCodigo;
    @XmlElement(namespace = "Servidor")
    protected java.lang.String estabelecimento;
    @XmlElement(namespace = "Servidor")
    protected java.lang.String orgaoCodigo;
    @XmlElement(namespace = "Servidor")
    protected java.lang.String orgao;
    @XmlElementRef(name = "categoria", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> categoria;
    @XmlElement(namespace = "Servidor")
    protected java.lang.String servidor;
    @XmlElement(namespace = "Servidor")
    protected java.lang.String cpf;
    @XmlElement(namespace = "Servidor")
    protected java.lang.String matricula;
    @XmlElementRef(name = "dataNascimento", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataNascimento;
    @XmlElementRef(name = "dataAdmissao", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataAdmissao;
    @XmlElementRef(name = "prazoServidor", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Integer> prazoServidor;
    @XmlElementRef(name = "situacaoServidor", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<SituacaoServidor> situacaoServidor;
    @XmlElement(namespace = "Servidor", nillable = true)
    protected List<Margem> margens;
    @XmlElementRef(name = "salarioLiquido", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> salarioLiquido;
    @XmlElementRef(name = "salarioBruto", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> salarioBruto;
    @XmlElementRef(name = "identidade", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> identidade;
    @XmlElementRef(name = "dataIdentidade", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataIdentidade;
    @XmlElementRef(name = "ufIdentidade", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> ufIdentidade;
    @XmlElementRef(name = "emissorIdentidade", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> emissorIdentidade;
    @XmlElementRef(name = "cidadeNascimento", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cidadeNascimento;
    @XmlElementRef(name = "nacionalidade", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nacionalidade;
    @XmlElementRef(name = "sexo", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> sexo;
    @XmlElementRef(name = "estadoCivil", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> estadoCivil;
    @XmlElementRef(name = "endereco", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> endereco;
    @XmlElementRef(name = "numero", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> numero;
    @XmlElementRef(name = "complemento", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> complemento;
    @XmlElementRef(name = "bairro", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> bairro;
    @XmlElementRef(name = "cidade", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cidade;
    @XmlElementRef(name = "uf", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> uf;
    @XmlElementRef(name = "cep", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cep;
    @XmlElementRef(name = "telefone", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> telefone;
    @XmlElementRef(name = "celular", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> celular;
    @XmlElementRef(name = "salario", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> salario;
    @XmlElementRef(name = "dataSaida", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataSaida;
    @XmlElementRef(name = "banco", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> banco;
    @XmlElementRef(name = "agencia", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> agencia;
    @XmlElementRef(name = "conta", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> conta;
    @XmlElementRef(name = "cargoCodigo", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cargoCodigo;
    @XmlElementRef(name = "cargoDescricao", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cargoDescricao;
    @XmlElementRef(name = "habitacaoCodigo", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> habitacaoCodigo;
    @XmlElementRef(name = "habitacaoDescricao", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> habitacaoDescricao;
    @XmlElementRef(name = "postoCodigo", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> postoCodigo;
    @XmlElementRef(name = "postoDescricao", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> postoDescricao;
    @XmlElementRef(name = "escolaridadeCodigo", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> escolaridadeCodigo;
    @XmlElementRef(name = "escolaridadeDescricao", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> escolaridadeDescricao;
    @XmlElementRef(name = "nomeMae", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nomeMae;
    @XmlElementRef(name = "nomePai", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nomePai;
    @XmlElementRef(name = "cartProf", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cartProf;
    @XmlElementRef(name = "pis", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> pis;
    @XmlElementRef(name = "email", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> email;
    @XmlElementRef(name = "nomeConjuge", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nomeConjuge;
    @XmlElementRef(name = "nomeMeio", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> nomeMeio;
    @XmlElementRef(name = "ultimoNome", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> ultimoNome;
    @XmlElementRef(name = "primeiroNome", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> primeiroNome;
    @XmlElementRef(name = "descontosComp", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> descontosComp;
    @XmlElementRef(name = "descontosFacu", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> descontosFacu;
    @XmlElementRef(name = "outrosDescontos", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> outrosDescontos;
    @XmlElementRef(name = "matriculaInst", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> matriculaInst;
    @XmlElementRef(name = "dataRetorno", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataRetorno;
    @XmlElementRef(name = "qtdFilhos", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Integer> qtdFilhos;
    @XmlElement(namespace = "Servidor", nillable = true)
    protected List<DadoAdicional> dados;

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
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCategoria() {
        return categoria;
    }

    /**
     * Define o valor da propriedade categoria.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCategoria(JAXBElement<java.lang.String> value) {
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
     * Obtém o valor da propriedade dataAdmissao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataAdmissao() {
        return dataAdmissao;
    }

    /**
     * Define o valor da propriedade dataAdmissao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataAdmissao(JAXBElement<XMLGregorianCalendar> value) {
        this.dataAdmissao = value;
    }

    /**
     * Obtém o valor da propriedade prazoServidor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *     
     */
    public JAXBElement<java.lang.Integer> getPrazoServidor() {
        return prazoServidor;
    }

    /**
     * Define o valor da propriedade prazoServidor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *     
     */
    public void setPrazoServidor(JAXBElement<java.lang.Integer> value) {
        this.prazoServidor = value;
    }

    /**
     * Obtém o valor da propriedade situacaoServidor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SituacaoServidor }{@code >}
     *     
     */
    public JAXBElement<SituacaoServidor> getSituacaoServidor() {
        return situacaoServidor;
    }

    /**
     * Define o valor da propriedade situacaoServidor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SituacaoServidor }{@code >}
     *     
     */
    public void setSituacaoServidor(JAXBElement<SituacaoServidor> value) {
        this.situacaoServidor = value;
    }

    /**
     * Gets the value of the margens property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the margens property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMargens().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Margem }
     * 
     * 
     * @return
     *     The value of the margens property.
     */
    public List<Margem> getMargens() {
        if (margens == null) {
            margens = new ArrayList<>();
        }
        return this.margens;
    }

    /**
     * Obtém o valor da propriedade salarioLiquido.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getSalarioLiquido() {
        return salarioLiquido;
    }

    /**
     * Define o valor da propriedade salarioLiquido.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setSalarioLiquido(JAXBElement<java.lang.Double> value) {
        this.salarioLiquido = value;
    }

    /**
     * Obtém o valor da propriedade salarioBruto.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getSalarioBruto() {
        return salarioBruto;
    }

    /**
     * Define o valor da propriedade salarioBruto.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setSalarioBruto(JAXBElement<java.lang.Double> value) {
        this.salarioBruto = value;
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
     * Obtém o valor da propriedade postoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getPostoCodigo() {
        return postoCodigo;
    }

    /**
     * Define o valor da propriedade postoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setPostoCodigo(JAXBElement<java.lang.String> value) {
        this.postoCodigo = value;
    }
    
    /**
     * Obtém o valor da propriedade postoDescricao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getPostoDescricao() {
        return postoDescricao;
    }

    /**
     * Define o valor da propriedade postoDescricao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setPostoDescricao(JAXBElement<java.lang.String> value) {
        this.postoDescricao = value;
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
     * Obtém o valor da propriedade nomeMae.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNomeMae() {
        return nomeMae;
    }

    /**
     * Define o valor da propriedade nomeMae.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNomeMae(JAXBElement<java.lang.String> value) {
        this.nomeMae = value;
    }

    /**
     * Obtém o valor da propriedade nomePai.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNomePai() {
        return nomePai;
    }

    /**
     * Define o valor da propriedade nomePai.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNomePai(JAXBElement<java.lang.String> value) {
        this.nomePai = value;
    }

    /**
     * Obtém o valor da propriedade cartProf.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCartProf() {
        return cartProf;
    }

    /**
     * Define o valor da propriedade cartProf.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCartProf(JAXBElement<java.lang.String> value) {
        this.cartProf = value;
    }

    /**
     * Obtém o valor da propriedade pis.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getPis() {
        return pis;
    }

    /**
     * Define o valor da propriedade pis.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setPis(JAXBElement<java.lang.String> value) {
        this.pis = value;
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
     * Obtém o valor da propriedade nomeConjuge.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNomeConjuge() {
        return nomeConjuge;
    }

    /**
     * Define o valor da propriedade nomeConjuge.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNomeConjuge(JAXBElement<java.lang.String> value) {
        this.nomeConjuge = value;
    }

    /**
     * Obtém o valor da propriedade nomeMeio.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getNomeMeio() {
        return nomeMeio;
    }

    /**
     * Define o valor da propriedade nomeMeio.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setNomeMeio(JAXBElement<java.lang.String> value) {
        this.nomeMeio = value;
    }

    /**
     * Obtém o valor da propriedade ultimoNome.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getUltimoNome() {
        return ultimoNome;
    }

    /**
     * Define o valor da propriedade ultimoNome.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setUltimoNome(JAXBElement<java.lang.String> value) {
        this.ultimoNome = value;
    }

    /**
     * Obtém o valor da propriedade primeiroNome.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getPrimeiroNome() {
        return primeiroNome;
    }

    /**
     * Define o valor da propriedade primeiroNome.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setPrimeiroNome(JAXBElement<java.lang.String> value) {
        this.primeiroNome = value;
    }

    /**
     * Obtém o valor da propriedade descontosComp.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getDescontosComp() {
        return descontosComp;
    }

    /**
     * Define o valor da propriedade descontosComp.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setDescontosComp(JAXBElement<java.lang.Double> value) {
        this.descontosComp = value;
    }

    /**
     * Obtém o valor da propriedade descontosFacu.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getDescontosFacu() {
        return descontosFacu;
    }

    /**
     * Define o valor da propriedade descontosFacu.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setDescontosFacu(JAXBElement<java.lang.Double> value) {
        this.descontosFacu = value;
    }

    /**
     * Obtém o valor da propriedade outrosDescontos.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getOutrosDescontos() {
        return outrosDescontos;
    }

    /**
     * Define o valor da propriedade outrosDescontos.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setOutrosDescontos(JAXBElement<java.lang.Double> value) {
        this.outrosDescontos = value;
    }

    /**
     * Obtém o valor da propriedade matriculaInst.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getMatriculaInst() {
        return matriculaInst;
    }

    /**
     * Define o valor da propriedade matriculaInst.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setMatriculaInst(JAXBElement<java.lang.String> value) {
        this.matriculaInst = value;
    }

    /**
     * Obtém o valor da propriedade dataRetorno.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataRetorno() {
        return dataRetorno;
    }

    /**
     * Define o valor da propriedade dataRetorno.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataRetorno(JAXBElement<XMLGregorianCalendar> value) {
        this.dataRetorno = value;
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

    /**
     * Gets the value of the dados property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the dados property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDados().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DadoAdicional }
     * 
     * 
     * @return
     *     The value of the dados property.
     */
    public List<DadoAdicional> getDados() {
        if (dados == null) {
            dados = new ArrayList<>();
        }
        return this.dados;
    }
}
