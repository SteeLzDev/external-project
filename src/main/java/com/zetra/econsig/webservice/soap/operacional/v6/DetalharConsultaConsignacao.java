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
 *         <element name="adeNumero" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         <element name="adeIdentificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="matricula" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="orgaoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="estabelecimentoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="correspondenteCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="servicoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codigoVerba" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="sdvSolicitado" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="sdvSolicitadoNaoCadastrado" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="sdvSolicitadoCadastrado" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="sdvNaoSolicitado" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="periodo" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="dataInclusaoInicio" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="dataInclusaoFim" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="integraFolha" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         <element name="codigoMargem" type="{http://www.w3.org/2001/XMLSchema}short" minOccurs="0"/>
 *         <element name="indice" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="situacaoContrato" type="{SituacaoContrato}SituacaoContrato" minOccurs="0"/>
 *         <element name="situacaoServidor" type="{SituacaoServidor}SituacaoServidor" minOccurs="0"/>
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
    "adeNumero",
    "adeIdentificador",
    "matricula",
    "cpf",
    "orgaoCodigo",
    "estabelecimentoCodigo",
    "correspondenteCodigo",
    "servicoCodigo",
    "codigoVerba",
    "sdvSolicitado",
    "sdvSolicitadoNaoCadastrado",
    "sdvSolicitadoCadastrado",
    "sdvNaoSolicitado",
    "periodo",
    "dataInclusaoInicio",
    "dataInclusaoFim",
    "integraFolha",
    "codigoMargem",
    "indice",
    "situacaoContrato",
    "situacaoServidor"
})
@XmlRootElement(name = "detalharConsultaConsignacao", namespace = "HostaHostService-v6_0")
public class DetalharConsultaConsignacao {

    @XmlElementRef(name = "cliente", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cliente;
    @XmlElementRef(name = "convenio", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> convenio;
    @XmlElement(namespace = "HostaHostService-v6_0", required = true)
    protected java.lang.String usuario;
    @XmlElement(namespace = "HostaHostService-v6_0", required = true)
    protected java.lang.String senha;
    @XmlElementRef(name = "adeNumero", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Long> adeNumero;
    @XmlElementRef(name = "adeIdentificador", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> adeIdentificador;
    @XmlElement(namespace = "HostaHostService-v6_0")
    protected java.lang.String matricula;
    @XmlElementRef(name = "cpf", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cpf;
    @XmlElementRef(name = "orgaoCodigo", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> orgaoCodigo;
    @XmlElementRef(name = "estabelecimentoCodigo", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> estabelecimentoCodigo;
    @XmlElementRef(name = "correspondenteCodigo", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> correspondenteCodigo;
    @XmlElementRef(name = "servicoCodigo", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> servicoCodigo;
    @XmlElementRef(name = "codigoVerba", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> codigoVerba;
    @XmlElementRef(name = "sdvSolicitado", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Boolean> sdvSolicitado;
    @XmlElementRef(name = "sdvSolicitadoNaoCadastrado", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Boolean> sdvSolicitadoNaoCadastrado;
    @XmlElementRef(name = "sdvSolicitadoCadastrado", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Boolean> sdvSolicitadoCadastrado;
    @XmlElementRef(name = "sdvNaoSolicitado", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Boolean> sdvNaoSolicitado;
    @XmlElementRef(name = "periodo", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> periodo;
    @XmlElementRef(name = "dataInclusaoInicio", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataInclusaoInicio;
    @XmlElementRef(name = "dataInclusaoFim", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataInclusaoFim;
    @XmlElementRef(name = "integraFolha", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Short> integraFolha;
    @XmlElementRef(name = "codigoMargem", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Short> codigoMargem;
    @XmlElementRef(name = "indice", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> indice;
    @XmlElementRef(name = "situacaoContrato", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<SituacaoContrato> situacaoContrato;
    @XmlElementRef(name = "situacaoServidor", namespace = "HostaHostService-v6_0", type = JAXBElement.class, required = false)
    protected JAXBElement<SituacaoServidor> situacaoServidor;

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
     * Obtém o valor da propriedade adeNumero.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *     
     */
    public JAXBElement<java.lang.Long> getAdeNumero() {
        return adeNumero;
    }

    /**
     * Define o valor da propriedade adeNumero.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *     
     */
    public void setAdeNumero(JAXBElement<java.lang.Long> value) {
        this.adeNumero = value;
    }

    /**
     * Obtém o valor da propriedade adeIdentificador.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getAdeIdentificador() {
        return adeIdentificador;
    }

    /**
     * Define o valor da propriedade adeIdentificador.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setAdeIdentificador(JAXBElement<java.lang.String> value) {
        this.adeIdentificador = value;
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
     * Obtém o valor da propriedade correspondenteCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCorrespondenteCodigo() {
        return correspondenteCodigo;
    }

    /**
     * Define o valor da propriedade correspondenteCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCorrespondenteCodigo(JAXBElement<java.lang.String> value) {
        this.correspondenteCodigo = value;
    }

    /**
     * Obtém o valor da propriedade servicoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getServicoCodigo() {
        return servicoCodigo;
    }

    /**
     * Define o valor da propriedade servicoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setServicoCodigo(JAXBElement<java.lang.String> value) {
        this.servicoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade codigoVerba.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCodigoVerba() {
        return codigoVerba;
    }

    /**
     * Define o valor da propriedade codigoVerba.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCodigoVerba(JAXBElement<java.lang.String> value) {
        this.codigoVerba = value;
    }

    /**
     * Obtém o valor da propriedade sdvSolicitado.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public JAXBElement<java.lang.Boolean> getSdvSolicitado() {
        return sdvSolicitado;
    }

    /**
     * Define o valor da propriedade sdvSolicitado.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public void setSdvSolicitado(JAXBElement<java.lang.Boolean> value) {
        this.sdvSolicitado = value;
    }

    /**
     * Obtém o valor da propriedade sdvSolicitadoNaoCadastrado.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public JAXBElement<java.lang.Boolean> getSdvSolicitadoNaoCadastrado() {
        return sdvSolicitadoNaoCadastrado;
    }

    /**
     * Define o valor da propriedade sdvSolicitadoNaoCadastrado.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public void setSdvSolicitadoNaoCadastrado(JAXBElement<java.lang.Boolean> value) {
        this.sdvSolicitadoNaoCadastrado = value;
    }

    /**
     * Obtém o valor da propriedade sdvSolicitadoCadastrado.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public JAXBElement<java.lang.Boolean> getSdvSolicitadoCadastrado() {
        return sdvSolicitadoCadastrado;
    }

    /**
     * Define o valor da propriedade sdvSolicitadoCadastrado.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public void setSdvSolicitadoCadastrado(JAXBElement<java.lang.Boolean> value) {
        this.sdvSolicitadoCadastrado = value;
    }

    /**
     * Obtém o valor da propriedade sdvNaoSolicitado.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public JAXBElement<java.lang.Boolean> getSdvNaoSolicitado() {
        return sdvNaoSolicitado;
    }

    /**
     * Define o valor da propriedade sdvNaoSolicitado.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Boolean }{@code >}
     *     
     */
    public void setSdvNaoSolicitado(JAXBElement<java.lang.Boolean> value) {
        this.sdvNaoSolicitado = value;
    }

    /**
     * Obtém o valor da propriedade periodo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getPeriodo() {
        return periodo;
    }

    /**
     * Define o valor da propriedade periodo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setPeriodo(JAXBElement<XMLGregorianCalendar> value) {
        this.periodo = value;
    }

    /**
     * Obtém o valor da propriedade dataInclusaoInicio.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataInclusaoInicio() {
        return dataInclusaoInicio;
    }

    /**
     * Define o valor da propriedade dataInclusaoInicio.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataInclusaoInicio(JAXBElement<XMLGregorianCalendar> value) {
        this.dataInclusaoInicio = value;
    }

    /**
     * Obtém o valor da propriedade dataInclusaoFim.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDataInclusaoFim() {
        return dataInclusaoFim;
    }

    /**
     * Define o valor da propriedade dataInclusaoFim.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDataInclusaoFim(JAXBElement<XMLGregorianCalendar> value) {
        this.dataInclusaoFim = value;
    }

    /**
     * Obtém o valor da propriedade integraFolha.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public JAXBElement<java.lang.Short> getIntegraFolha() {
        return integraFolha;
    }

    /**
     * Define o valor da propriedade integraFolha.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public void setIntegraFolha(JAXBElement<java.lang.Short> value) {
        this.integraFolha = value;
    }

    /**
     * Obtém o valor da propriedade codigoMargem.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public JAXBElement<java.lang.Short> getCodigoMargem() {
        return codigoMargem;
    }

    /**
     * Define o valor da propriedade codigoMargem.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Short }{@code >}
     *     
     */
    public void setCodigoMargem(JAXBElement<java.lang.Short> value) {
        this.codigoMargem = value;
    }

    /**
     * Obtém o valor da propriedade indice.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getIndice() {
        return indice;
    }

    /**
     * Define o valor da propriedade indice.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setIndice(JAXBElement<java.lang.String> value) {
        this.indice = value;
    }

    /**
     * Obtém o valor da propriedade situacaoContrato.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SituacaoContrato }{@code >}
     *     
     */
    public JAXBElement<SituacaoContrato> getSituacaoContrato() {
        return situacaoContrato;
    }

    /**
     * Define o valor da propriedade situacaoContrato.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SituacaoContrato }{@code >}
     *     
     */
    public void setSituacaoContrato(JAXBElement<SituacaoContrato> value) {
        this.situacaoContrato = value;
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

}
