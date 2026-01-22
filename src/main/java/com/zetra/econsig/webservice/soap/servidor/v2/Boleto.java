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
 * <p>Classe Java de Boleto complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="Boleto">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="servidor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
 *         <element name="matricula" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="dataAdmissao" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         <element name="prazoServidor" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="categoria" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="estabelecimento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="orgao" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="estabelecimentoCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="orgaoCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="orgaoEndereco" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="orgaoNumero" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="orgaoComplemento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="orgaoBairro" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="orgaoCidade" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="orgaoUf" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="orgaoCep" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="orgaoTelefone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="consignataria" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="codVerba" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="ranking" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         <element name="servico" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="valorLiberado" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         <element name="dataReserva" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         <element name="dataInicial" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         <element name="dataFinal" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         <element name="valorParcela" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         <element name="adeNumero" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         <element name="adeIdentificador" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="indice" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="prazo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="pagas" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         <element name="taxaJuros" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="situacao" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="servicoCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="statusCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="consignatariaCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="responsavel" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Boleto", namespace = "Boleto", propOrder = {
    "servidor",
    "cpf",
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
    "matricula",
    "dataAdmissao",
    "prazoServidor",
    "categoria",
    "estabelecimento",
    "orgao",
    "estabelecimentoCodigo",
    "orgaoCodigo",
    "orgaoEndereco",
    "orgaoNumero",
    "orgaoComplemento",
    "orgaoBairro",
    "orgaoCidade",
    "orgaoUf",
    "orgaoCep",
    "orgaoTelefone",
    "consignataria",
    "codVerba",
    "ranking",
    "servico",
    "valorLiberado",
    "dataReserva",
    "dataInicial",
    "dataFinal",
    "valorParcela",
    "adeNumero",
    "adeIdentificador",
    "indice",
    "prazo",
    "pagas",
    "taxaJuros",
    "situacao",
    "servicoCodigo",
    "statusCodigo",
    "consignatariaCodigo",
    "responsavel"
})
public class Boleto {

    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String servidor;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String cpf;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String sexo;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataNascimento;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String estadoCivil;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String identidade;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String pai;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String mae;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String endereco;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String numero;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String complemento;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String bairro;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String cidade;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String uf;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String cep;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String telefone;
    @XmlElement(namespace = "Boleto", required = true)
    protected String matricula;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataAdmissao;
    @XmlElement(namespace = "Boleto", required = true, type = Integer.class, nillable = true)
    protected Integer prazoServidor;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String categoria;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String estabelecimento;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String orgao;
    @XmlElement(namespace = "Boleto", required = true)
    protected String estabelecimentoCodigo;
    @XmlElement(namespace = "Boleto", required = true)
    protected String orgaoCodigo;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String orgaoEndereco;
    @XmlElement(namespace = "Boleto", required = true, type = Integer.class, nillable = true)
    protected Integer orgaoNumero;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String orgaoComplemento;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String orgaoBairro;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String orgaoCidade;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String orgaoUf;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String orgaoCep;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String orgaoTelefone;
    @XmlElement(namespace = "Boleto", required = true)
    protected String consignataria;
    @XmlElement(namespace = "Boleto", required = true)
    protected String codVerba;
    @XmlElement(namespace = "Boleto", required = true, type = Short.class, nillable = true)
    protected Short ranking;
    @XmlElement(namespace = "Boleto", required = true)
    protected String servico;
    @XmlElement(namespace = "Boleto", required = true, type = Double.class, nillable = true)
    protected Double valorLiberado;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataReserva;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataInicial;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataFinal;
    @XmlElement(namespace = "Boleto")
    protected double valorParcela;
    @XmlElement(namespace = "Boleto")
    protected long adeNumero;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String adeIdentificador;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected String indice;
    @XmlElement(namespace = "Boleto")
    protected int prazo;
    @XmlElement(namespace = "Boleto")
    protected int pagas;
    @XmlElementRef(name = "taxaJuros", namespace = "Boleto", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> taxaJuros;
    @XmlElement(namespace = "Boleto", required = true)
    protected String situacao;
    @XmlElement(namespace = "Boleto", required = true)
    protected String servicoCodigo;
    @XmlElement(namespace = "Boleto", required = true)
    protected String statusCodigo;
    @XmlElementRef(name = "consignatariaCodigo", namespace = "Boleto", type = JAXBElement.class, required = false)
    protected JAXBElement<String> consignatariaCodigo;
    @XmlElement(namespace = "Boleto", required = true)
    protected String responsavel;

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
     * Obtém o valor da propriedade orgaoEndereco.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgaoEndereco() {
        return orgaoEndereco;
    }

    /**
     * Define o valor da propriedade orgaoEndereco.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgaoEndereco(String value) {
        this.orgaoEndereco = value;
    }

    /**
     * Obtém o valor da propriedade orgaoNumero.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOrgaoNumero() {
        return orgaoNumero;
    }

    /**
     * Define o valor da propriedade orgaoNumero.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOrgaoNumero(Integer value) {
        this.orgaoNumero = value;
    }

    /**
     * Obtém o valor da propriedade orgaoComplemento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgaoComplemento() {
        return orgaoComplemento;
    }

    /**
     * Define o valor da propriedade orgaoComplemento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgaoComplemento(String value) {
        this.orgaoComplemento = value;
    }

    /**
     * Obtém o valor da propriedade orgaoBairro.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgaoBairro() {
        return orgaoBairro;
    }

    /**
     * Define o valor da propriedade orgaoBairro.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgaoBairro(String value) {
        this.orgaoBairro = value;
    }

    /**
     * Obtém o valor da propriedade orgaoCidade.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgaoCidade() {
        return orgaoCidade;
    }

    /**
     * Define o valor da propriedade orgaoCidade.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgaoCidade(String value) {
        this.orgaoCidade = value;
    }

    /**
     * Obtém o valor da propriedade orgaoUf.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgaoUf() {
        return orgaoUf;
    }

    /**
     * Define o valor da propriedade orgaoUf.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgaoUf(String value) {
        this.orgaoUf = value;
    }

    /**
     * Obtém o valor da propriedade orgaoCep.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgaoCep() {
        return orgaoCep;
    }

    /**
     * Define o valor da propriedade orgaoCep.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgaoCep(String value) {
        this.orgaoCep = value;
    }

    /**
     * Obtém o valor da propriedade orgaoTelefone.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrgaoTelefone() {
        return orgaoTelefone;
    }

    /**
     * Define o valor da propriedade orgaoTelefone.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrgaoTelefone(String value) {
        this.orgaoTelefone = value;
    }

    /**
     * Obtém o valor da propriedade consignataria.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConsignataria() {
        return consignataria;
    }

    /**
     * Define o valor da propriedade consignataria.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConsignataria(String value) {
        this.consignataria = value;
    }

    /**
     * Obtém o valor da propriedade codVerba.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodVerba() {
        return codVerba;
    }

    /**
     * Define o valor da propriedade codVerba.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodVerba(String value) {
        this.codVerba = value;
    }

    /**
     * Obtém o valor da propriedade ranking.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getRanking() {
        return ranking;
    }

    /**
     * Define o valor da propriedade ranking.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setRanking(Short value) {
        this.ranking = value;
    }

    /**
     * Obtém o valor da propriedade servico.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServico() {
        return servico;
    }

    /**
     * Define o valor da propriedade servico.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServico(String value) {
        this.servico = value;
    }

    /**
     * Obtém o valor da propriedade valorLiberado.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getValorLiberado() {
        return valorLiberado;
    }

    /**
     * Define o valor da propriedade valorLiberado.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setValorLiberado(Double value) {
        this.valorLiberado = value;
    }

    /**
     * Obtém o valor da propriedade dataReserva.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataReserva() {
        return dataReserva;
    }

    /**
     * Define o valor da propriedade dataReserva.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataReserva(XMLGregorianCalendar value) {
        this.dataReserva = value;
    }

    /**
     * Obtém o valor da propriedade dataInicial.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataInicial() {
        return dataInicial;
    }

    /**
     * Define o valor da propriedade dataInicial.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataInicial(XMLGregorianCalendar value) {
        this.dataInicial = value;
    }

    /**
     * Obtém o valor da propriedade dataFinal.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataFinal() {
        return dataFinal;
    }

    /**
     * Define o valor da propriedade dataFinal.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataFinal(XMLGregorianCalendar value) {
        this.dataFinal = value;
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
     * Obtém o valor da propriedade adeNumero.
     * 
     */
    public long getAdeNumero() {
        return adeNumero;
    }

    /**
     * Define o valor da propriedade adeNumero.
     * 
     */
    public void setAdeNumero(long value) {
        this.adeNumero = value;
    }

    /**
     * Obtém o valor da propriedade adeIdentificador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdeIdentificador() {
        return adeIdentificador;
    }

    /**
     * Define o valor da propriedade adeIdentificador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdeIdentificador(String value) {
        this.adeIdentificador = value;
    }

    /**
     * Obtém o valor da propriedade indice.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndice() {
        return indice;
    }

    /**
     * Define o valor da propriedade indice.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndice(String value) {
        this.indice = value;
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
     * Obtém o valor da propriedade pagas.
     * 
     */
    public int getPagas() {
        return pagas;
    }

    /**
     * Define o valor da propriedade pagas.
     * 
     */
    public void setPagas(int value) {
        this.pagas = value;
    }

    /**
     * Obtém o valor da propriedade taxaJuros.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getTaxaJuros() {
        return taxaJuros;
    }

    /**
     * Define o valor da propriedade taxaJuros.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setTaxaJuros(JAXBElement<Double> value) {
        this.taxaJuros = value;
    }

    /**
     * Obtém o valor da propriedade situacao.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSituacao() {
        return situacao;
    }

    /**
     * Define o valor da propriedade situacao.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSituacao(String value) {
        this.situacao = value;
    }

    /**
     * Obtém o valor da propriedade servicoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServicoCodigo() {
        return servicoCodigo;
    }

    /**
     * Define o valor da propriedade servicoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServicoCodigo(String value) {
        this.servicoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade statusCodigo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusCodigo() {
        return statusCodigo;
    }

    /**
     * Define o valor da propriedade statusCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusCodigo(String value) {
        this.statusCodigo = value;
    }

    /**
     * Obtém o valor da propriedade consignatariaCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getConsignatariaCodigo() {
        return consignatariaCodigo;
    }

    /**
     * Define o valor da propriedade consignatariaCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setConsignatariaCodigo(JAXBElement<String> value) {
        this.consignatariaCodigo = value;
    }

    /**
     * Obtém o valor da propriedade responsavel.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponsavel() {
        return responsavel;
    }

    /**
     * Define o valor da propriedade responsavel.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponsavel(String value) {
        this.responsavel = value;
    }

}
