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
 *         <element name="cliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="convenio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="usuario" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="senha" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="primeiroNome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ultimoNome" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="dataNascimento" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="matricula" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="estabelecimentoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="orgaoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="situacao" type="{SituacaoServidor}SituacaoServidor" minOccurs="0"/>
 *         <element name="dataSaida" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="dataUltimoSalario" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="dataRetorno" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="pedidoDemissao" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="salario" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="proventos" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="codigoMotivoOperacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="obsMotivoOperacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "cliente",
    "convenio",
    "usuario",
    "senha",
    "primeiroNome",
    "ultimoNome",
    "cpf",
    "dataNascimento",
    "matricula",
    "estabelecimentoCodigo",
    "orgaoCodigo",
    "situacao",
    "dataSaida",
    "dataUltimoSalario",
    "dataRetorno",
    "pedidoDemissao",
    "salario",
    "proventos",
    "codigoMotivoOperacao",
    "obsMotivoOperacao"
})
@XmlRootElement(name = "editarStatusServidor", namespace = "HostaHostService-v7_0")
public class EditarStatusServidor {

    @XmlElementRef(name = "cliente", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cliente;
    @XmlElementRef(name = "convenio", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> convenio;
    @XmlElement(namespace = "HostaHostService-v7_0", required = true)
    protected java.lang.String usuario;
    @XmlElement(namespace = "HostaHostService-v7_0", required = true)
    protected java.lang.String senha;
    @XmlElementRef(name = "primeiroNome", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> primeiroNome;
    @XmlElementRef(name = "ultimoNome", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> ultimoNome;
    @XmlElementRef(name = "cpf", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cpf;
    @XmlElementRef(name = "dataNascimento", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataNascimento;
    @XmlElementRef(name = "matricula", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> matricula;
    @XmlElementRef(name = "estabelecimentoCodigo", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> estabelecimentoCodigo;
    @XmlElementRef(name = "orgaoCodigo", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> orgaoCodigo;
    @XmlElementRef(name = "situacao", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<SituacaoServidor> situacao;
    @XmlElementRef(name = "dataSaida", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataSaida;
    @XmlElementRef(name = "dataUltimoSalario", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataUltimoSalario;
    @XmlElementRef(name = "dataRetorno", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataRetorno;
    @XmlElementRef(name = "pedidoDemissao", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Boolean> pedidoDemissao;
    @XmlElementRef(name = "salario", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> salario;
    @XmlElementRef(name = "proventos", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> proventos;
    @XmlElementRef(name = "codigoMotivoOperacao", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> codigoMotivoOperacao;
    @XmlElementRef(name = "obsMotivoOperacao", namespace = "HostaHostService-v7_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> obsMotivoOperacao;

    /**
     * Obtém o valor da propriedade cliente.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCliente() {
        return cliente;
    }

    /**
     * Define o valor da propriedade cliente.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCliente(JAXBElement<java.lang.String> value) {
        this.cliente = value;
    }

    /**
     * Obtém o valor da propriedade convenio.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getConvenio() {
        return convenio;
    }

    /**
     * Define o valor da propriedade convenio.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setConvenio(JAXBElement<java.lang.String> value) {
        this.convenio = value;
    }

    /**
     * Obtém o valor da propriedade usuario.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getUsuario() {
        return usuario;
    }

    /**
     * Define o valor da propriedade usuario.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setUsuario(java.lang.String value) {
        this.usuario = value;
    }

    /**
     * Obtém o valor da propriedade senha.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getSenha() {
        return senha;
    }

    /**
     * Define o valor da propriedade senha.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setSenha(java.lang.String value) {
        this.senha = value;
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
     * Obtém o valor da propriedade matricula.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getMatricula() {
        return matricula;
    }

    /**
     * Define o valor da propriedade matricula.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setMatricula(JAXBElement<java.lang.String> value) {
        this.matricula = value;
    }

    /**
     * Obtém o valor da propriedade estabelecimentoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getEstabelecimentoCodigo() {
        return estabelecimentoCodigo;
    }

    /**
     * Define o valor da propriedade estabelecimentoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setEstabelecimentoCodigo(JAXBElement<java.lang.String> value) {
        this.estabelecimentoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade orgaoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getOrgaoCodigo() {
        return orgaoCodigo;
    }

    /**
     * Define o valor da propriedade orgaoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setOrgaoCodigo(JAXBElement<java.lang.String> value) {
        this.orgaoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade situacao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SituacaoServidor }{@code >}
     *     
     */
    public JAXBElement<SituacaoServidor> getSituacao() {
        return situacao;
    }

    /**
     * Define o valor da propriedade situacao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SituacaoServidor }{@code >}
     *     
     */
    public void setSituacao(JAXBElement<SituacaoServidor> value) {
        this.situacao = value;
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
     * Obtém o valor da propriedade dataUltimoSalario.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataUltimoSalario() {
        return dataUltimoSalario;
    }

    /**
     * Define o valor da propriedade dataUltimoSalario.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataUltimoSalario(JAXBElement<XMLGregorianCalendar> value) {
        this.dataUltimoSalario = value;
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
     * Obtém o valor da propriedade pedidoDemissao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public JAXBElement<java.lang.Boolean> getPedidoDemissao() {
        return pedidoDemissao;
    }

    /**
     * Define o valor da propriedade pedidoDemissao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public void setPedidoDemissao(JAXBElement<java.lang.Boolean> value) {
        this.pedidoDemissao = value;
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
     * Obtém o valor da propriedade proventos.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getProventos() {
        return proventos;
    }

    /**
     * Define o valor da propriedade proventos.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setProventos(JAXBElement<java.lang.Double> value) {
        this.proventos = value;
    }

    /**
     * Obtém o valor da propriedade codigoMotivoOperacao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCodigoMotivoOperacao() {
        return codigoMotivoOperacao;
    }

    /**
     * Define o valor da propriedade codigoMotivoOperacao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCodigoMotivoOperacao(JAXBElement<java.lang.String> value) {
        this.codigoMotivoOperacao = value;
    }

    /**
     * Obtém o valor da propriedade obsMotivoOperacao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getObsMotivoOperacao() {
        return obsMotivoOperacao;
    }

    /**
     * Define o valor da propriedade obsMotivoOperacao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setObsMotivoOperacao(JAXBElement<java.lang.String> value) {
        this.obsMotivoOperacao = value;
    }

}
