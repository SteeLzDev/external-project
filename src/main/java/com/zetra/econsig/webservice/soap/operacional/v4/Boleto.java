//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v4;

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
    protected java.lang.String servidor;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String cpf;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String sexo;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataNascimento;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String estadoCivil;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String identidade;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String pai;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String mae;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String endereco;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String numero;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String complemento;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String bairro;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String cidade;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String uf;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String cep;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String telefone;
    @XmlElement(namespace = "Boleto", required = true)
    protected java.lang.String matricula;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataAdmissao;
    @XmlElement(namespace = "Boleto", required = true, type = java.lang.Integer.class, nillable = true)
    protected java.lang.Integer prazoServidor;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String categoria;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String estabelecimento;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String orgao;
    @XmlElement(namespace = "Boleto", required = true)
    protected java.lang.String estabelecimentoCodigo;
    @XmlElement(namespace = "Boleto", required = true)
    protected java.lang.String orgaoCodigo;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String orgaoEndereco;
    @XmlElement(namespace = "Boleto", required = true, type = java.lang.Integer.class, nillable = true)
    protected java.lang.Integer orgaoNumero;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String orgaoComplemento;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String orgaoBairro;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String orgaoCidade;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String orgaoUf;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String orgaoCep;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String orgaoTelefone;
    @XmlElement(namespace = "Boleto", required = true)
    protected java.lang.String consignataria;
    @XmlElement(namespace = "Boleto", required = true)
    protected java.lang.String codVerba;
    @XmlElement(namespace = "Boleto", required = true, type = java.lang.Short.class, nillable = true)
    protected java.lang.Short ranking;
    @XmlElement(namespace = "Boleto", required = true)
    protected java.lang.String servico;
    @XmlElement(namespace = "Boleto", required = true, type = java.lang.Double.class, nillable = true)
    protected java.lang.Double valorLiberado;
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
    protected java.lang.String adeIdentificador;
    @XmlElement(namespace = "Boleto", required = true, nillable = true)
    protected java.lang.String indice;
    @XmlElement(namespace = "Boleto")
    protected int prazo;
    @XmlElement(namespace = "Boleto")
    protected int pagas;
    @XmlElementRef(name = "taxaJuros", namespace = "Boleto", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> taxaJuros;
    @XmlElement(namespace = "Boleto", required = true)
    protected java.lang.String situacao;
    @XmlElement(namespace = "Boleto", required = true)
    protected java.lang.String servicoCodigo;
    @XmlElement(namespace = "Boleto", required = true)
    protected java.lang.String statusCodigo;
    @XmlElementRef(name = "consignatariaCodigo", namespace = "Boleto", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> consignatariaCodigo;
    @XmlElement(namespace = "Boleto", required = true)
    protected java.lang.String responsavel;

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
     * Obtém o valor da propriedade sexo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getSexo() {
        return sexo;
    }

    /**
     * Define o valor da propriedade sexo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setSexo(java.lang.String value) {
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
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getEstadoCivil() {
        return estadoCivil;
    }

    /**
     * Define o valor da propriedade estadoCivil.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setEstadoCivil(java.lang.String value) {
        this.estadoCivil = value;
    }

    /**
     * Obtém o valor da propriedade identidade.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getIdentidade() {
        return identidade;
    }

    /**
     * Define o valor da propriedade identidade.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setIdentidade(java.lang.String value) {
        this.identidade = value;
    }

    /**
     * Obtém o valor da propriedade pai.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getPai() {
        return pai;
    }

    /**
     * Define o valor da propriedade pai.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setPai(java.lang.String value) {
        this.pai = value;
    }

    /**
     * Obtém o valor da propriedade mae.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getMae() {
        return mae;
    }

    /**
     * Define o valor da propriedade mae.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setMae(java.lang.String value) {
        this.mae = value;
    }

    /**
     * Obtém o valor da propriedade endereco.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getEndereco() {
        return endereco;
    }

    /**
     * Define o valor da propriedade endereco.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setEndereco(java.lang.String value) {
        this.endereco = value;
    }

    /**
     * Obtém o valor da propriedade numero.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getNumero() {
        return numero;
    }

    /**
     * Define o valor da propriedade numero.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setNumero(java.lang.String value) {
        this.numero = value;
    }

    /**
     * Obtém o valor da propriedade complemento.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getComplemento() {
        return complemento;
    }

    /**
     * Define o valor da propriedade complemento.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setComplemento(java.lang.String value) {
        this.complemento = value;
    }

    /**
     * Obtém o valor da propriedade bairro.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getBairro() {
        return bairro;
    }

    /**
     * Define o valor da propriedade bairro.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setBairro(java.lang.String value) {
        this.bairro = value;
    }

    /**
     * Obtém o valor da propriedade cidade.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getCidade() {
        return cidade;
    }

    /**
     * Define o valor da propriedade cidade.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setCidade(java.lang.String value) {
        this.cidade = value;
    }

    /**
     * Obtém o valor da propriedade uf.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getUf() {
        return uf;
    }

    /**
     * Define o valor da propriedade uf.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setUf(java.lang.String value) {
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
     * Obtém o valor da propriedade orgaoEndereco.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getOrgaoEndereco() {
        return orgaoEndereco;
    }

    /**
     * Define o valor da propriedade orgaoEndereco.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setOrgaoEndereco(java.lang.String value) {
        this.orgaoEndereco = value;
    }

    /**
     * Obtém o valor da propriedade orgaoNumero.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Integer }
     *     
     */
    public java.lang.Integer getOrgaoNumero() {
        return orgaoNumero;
    }

    /**
     * Define o valor da propriedade orgaoNumero.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Integer }
     *     
     */
    public void setOrgaoNumero(java.lang.Integer value) {
        this.orgaoNumero = value;
    }

    /**
     * Obtém o valor da propriedade orgaoComplemento.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getOrgaoComplemento() {
        return orgaoComplemento;
    }

    /**
     * Define o valor da propriedade orgaoComplemento.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setOrgaoComplemento(java.lang.String value) {
        this.orgaoComplemento = value;
    }

    /**
     * Obtém o valor da propriedade orgaoBairro.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getOrgaoBairro() {
        return orgaoBairro;
    }

    /**
     * Define o valor da propriedade orgaoBairro.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setOrgaoBairro(java.lang.String value) {
        this.orgaoBairro = value;
    }

    /**
     * Obtém o valor da propriedade orgaoCidade.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getOrgaoCidade() {
        return orgaoCidade;
    }

    /**
     * Define o valor da propriedade orgaoCidade.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setOrgaoCidade(java.lang.String value) {
        this.orgaoCidade = value;
    }

    /**
     * Obtém o valor da propriedade orgaoUf.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getOrgaoUf() {
        return orgaoUf;
    }

    /**
     * Define o valor da propriedade orgaoUf.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setOrgaoUf(java.lang.String value) {
        this.orgaoUf = value;
    }

    /**
     * Obtém o valor da propriedade orgaoCep.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getOrgaoCep() {
        return orgaoCep;
    }

    /**
     * Define o valor da propriedade orgaoCep.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setOrgaoCep(java.lang.String value) {
        this.orgaoCep = value;
    }

    /**
     * Obtém o valor da propriedade orgaoTelefone.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getOrgaoTelefone() {
        return orgaoTelefone;
    }

    /**
     * Define o valor da propriedade orgaoTelefone.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setOrgaoTelefone(java.lang.String value) {
        this.orgaoTelefone = value;
    }

    /**
     * Obtém o valor da propriedade consignataria.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getConsignataria() {
        return consignataria;
    }

    /**
     * Define o valor da propriedade consignataria.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setConsignataria(java.lang.String value) {
        this.consignataria = value;
    }

    /**
     * Obtém o valor da propriedade codVerba.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getCodVerba() {
        return codVerba;
    }

    /**
     * Define o valor da propriedade codVerba.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setCodVerba(java.lang.String value) {
        this.codVerba = value;
    }

    /**
     * Obtém o valor da propriedade ranking.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Short }
     *     
     */
    public java.lang.Short getRanking() {
        return ranking;
    }

    /**
     * Define o valor da propriedade ranking.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Short }
     *     
     */
    public void setRanking(java.lang.Short value) {
        this.ranking = value;
    }

    /**
     * Obtém o valor da propriedade servico.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getServico() {
        return servico;
    }

    /**
     * Define o valor da propriedade servico.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setServico(java.lang.String value) {
        this.servico = value;
    }

    /**
     * Obtém o valor da propriedade valorLiberado.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Double }
     *     
     */
    public java.lang.Double getValorLiberado() {
        return valorLiberado;
    }

    /**
     * Define o valor da propriedade valorLiberado.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Double }
     *     
     */
    public void setValorLiberado(java.lang.Double value) {
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
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getAdeIdentificador() {
        return adeIdentificador;
    }

    /**
     * Define o valor da propriedade adeIdentificador.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setAdeIdentificador(java.lang.String value) {
        this.adeIdentificador = value;
    }

    /**
     * Obtém o valor da propriedade indice.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getIndice() {
        return indice;
    }

    /**
     * Define o valor da propriedade indice.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setIndice(java.lang.String value) {
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
     * Obtém o valor da propriedade situacao.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getSituacao() {
        return situacao;
    }

    /**
     * Define o valor da propriedade situacao.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setSituacao(java.lang.String value) {
        this.situacao = value;
    }

    /**
     * Obtém o valor da propriedade servicoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getServicoCodigo() {
        return servicoCodigo;
    }

    /**
     * Define o valor da propriedade servicoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setServicoCodigo(java.lang.String value) {
        this.servicoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade statusCodigo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getStatusCodigo() {
        return statusCodigo;
    }

    /**
     * Define o valor da propriedade statusCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setStatusCodigo(java.lang.String value) {
        this.statusCodigo = value;
    }

    /**
     * Obtém o valor da propriedade consignatariaCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getConsignatariaCodigo() {
        return consignatariaCodigo;
    }

    /**
     * Define o valor da propriedade consignatariaCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setConsignatariaCodigo(JAXBElement<java.lang.String> value) {
        this.consignatariaCodigo = value;
    }

    /**
     * Obtém o valor da propriedade responsavel.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getResponsavel() {
        return responsavel;
    }

    /**
     * Define o valor da propriedade responsavel.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setResponsavel(java.lang.String value) {
        this.responsavel = value;
    }

}
