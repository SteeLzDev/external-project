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
 *         <element name="estabelecimentoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="orgaoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="matricula" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="senha" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="loginServidor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="adeNumero" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         <element name="adeIdentificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="consignatariaCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "estabelecimentoCodigo",
    "orgaoCodigo",
    "matricula",
    "senha",
    "loginServidor",
    "adeNumero",
    "adeIdentificador",
    "consignatariaCodigo",
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
    "situacaoContrato"
})
@XmlRootElement(name = "detalharConsultaConsignacaoServidor", namespace = "ServidorService-v2_0")
public class DetalharConsultaConsignacaoServidor {

    @XmlElementRef(name = "estabelecimentoCodigo", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> estabelecimentoCodigo;
    @XmlElementRef(name = "orgaoCodigo", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> orgaoCodigo;
    @XmlElement(namespace = "ServidorService-v2_0", required = true)
    protected String matricula;
    @XmlElement(namespace = "ServidorService-v2_0", required = true)
    protected String senha;
    @XmlElementRef(name = "loginServidor", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> loginServidor;
    @XmlElementRef(name = "adeNumero", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<Long> adeNumero;
    @XmlElementRef(name = "adeIdentificador", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> adeIdentificador;
    @XmlElementRef(name = "consignatariaCodigo", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> consignatariaCodigo;
    @XmlElementRef(name = "correspondenteCodigo", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> correspondenteCodigo;
    @XmlElementRef(name = "servicoCodigo", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> servicoCodigo;
    @XmlElementRef(name = "codigoVerba", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> codigoVerba;
    @XmlElementRef(name = "sdvSolicitado", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<Boolean> sdvSolicitado;
    @XmlElementRef(name = "sdvSolicitadoNaoCadastrado", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<Boolean> sdvSolicitadoNaoCadastrado;
    @XmlElementRef(name = "sdvSolicitadoCadastrado", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<Boolean> sdvSolicitadoCadastrado;
    @XmlElementRef(name = "sdvNaoSolicitado", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<Boolean> sdvNaoSolicitado;
    @XmlElementRef(name = "periodo", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> periodo;
    @XmlElementRef(name = "dataInclusaoInicio", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataInclusaoInicio;
    @XmlElementRef(name = "dataInclusaoFim", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataInclusaoFim;
    @XmlElementRef(name = "integraFolha", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<Short> integraFolha;
    @XmlElementRef(name = "codigoMargem", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<Short> codigoMargem;
    @XmlElementRef(name = "indice", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> indice;
    @XmlElementRef(name = "situacaoContrato", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<SituacaoContrato> situacaoContrato;

    /**
     * Obtém o valor da propriedade estabelecimentoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEstabelecimentoCodigo() {
        return estabelecimentoCodigo;
    }

    /**
     * Define o valor da propriedade estabelecimentoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEstabelecimentoCodigo(JAXBElement<String> value) {
        this.estabelecimentoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade orgaoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getOrgaoCodigo() {
        return orgaoCodigo;
    }

    /**
     * Define o valor da propriedade orgaoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setOrgaoCodigo(JAXBElement<String> value) {
        this.orgaoCodigo = value;
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
     * Obtém o valor da propriedade senha.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Define o valor da propriedade senha.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSenha(String value) {
        this.senha = value;
    }

    /**
     * Obtém o valor da propriedade loginServidor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLoginServidor() {
        return loginServidor;
    }

    /**
     * Define o valor da propriedade loginServidor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLoginServidor(JAXBElement<String> value) {
        this.loginServidor = value;
    }

    /**
     * Obtém o valor da propriedade adeNumero.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public JAXBElement<Long> getAdeNumero() {
        return adeNumero;
    }

    /**
     * Define o valor da propriedade adeNumero.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Long }{@code >}
     *     
     */
    public void setAdeNumero(JAXBElement<Long> value) {
        this.adeNumero = value;
    }

    /**
     * Obtém o valor da propriedade adeIdentificador.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAdeIdentificador() {
        return adeIdentificador;
    }

    /**
     * Define o valor da propriedade adeIdentificador.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAdeIdentificador(JAXBElement<String> value) {
        this.adeIdentificador = value;
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
     * Obtém o valor da propriedade correspondenteCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCorrespondenteCodigo() {
        return correspondenteCodigo;
    }

    /**
     * Define o valor da propriedade correspondenteCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCorrespondenteCodigo(JAXBElement<String> value) {
        this.correspondenteCodigo = value;
    }

    /**
     * Obtém o valor da propriedade servicoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getServicoCodigo() {
        return servicoCodigo;
    }

    /**
     * Define o valor da propriedade servicoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setServicoCodigo(JAXBElement<String> value) {
        this.servicoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade codigoVerba.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCodigoVerba() {
        return codigoVerba;
    }

    /**
     * Define o valor da propriedade codigoVerba.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCodigoVerba(JAXBElement<String> value) {
        this.codigoVerba = value;
    }

    /**
     * Obtém o valor da propriedade sdvSolicitado.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getSdvSolicitado() {
        return sdvSolicitado;
    }

    /**
     * Define o valor da propriedade sdvSolicitado.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setSdvSolicitado(JAXBElement<Boolean> value) {
        this.sdvSolicitado = value;
    }

    /**
     * Obtém o valor da propriedade sdvSolicitadoNaoCadastrado.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getSdvSolicitadoNaoCadastrado() {
        return sdvSolicitadoNaoCadastrado;
    }

    /**
     * Define o valor da propriedade sdvSolicitadoNaoCadastrado.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setSdvSolicitadoNaoCadastrado(JAXBElement<Boolean> value) {
        this.sdvSolicitadoNaoCadastrado = value;
    }

    /**
     * Obtém o valor da propriedade sdvSolicitadoCadastrado.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getSdvSolicitadoCadastrado() {
        return sdvSolicitadoCadastrado;
    }

    /**
     * Define o valor da propriedade sdvSolicitadoCadastrado.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setSdvSolicitadoCadastrado(JAXBElement<Boolean> value) {
        this.sdvSolicitadoCadastrado = value;
    }

    /**
     * Obtém o valor da propriedade sdvNaoSolicitado.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getSdvNaoSolicitado() {
        return sdvNaoSolicitado;
    }

    /**
     * Define o valor da propriedade sdvNaoSolicitado.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setSdvNaoSolicitado(JAXBElement<Boolean> value) {
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
     *     {@link JAXBElement }{@code <}{@link Short }{@code >}
     *     
     */
    public JAXBElement<Short> getIntegraFolha() {
        return integraFolha;
    }

    /**
     * Define o valor da propriedade integraFolha.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Short }{@code >}
     *     
     */
    public void setIntegraFolha(JAXBElement<Short> value) {
        this.integraFolha = value;
    }

    /**
     * Obtém o valor da propriedade codigoMargem.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Short }{@code >}
     *     
     */
    public JAXBElement<Short> getCodigoMargem() {
        return codigoMargem;
    }

    /**
     * Define o valor da propriedade codigoMargem.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Short }{@code >}
     *     
     */
    public void setCodigoMargem(JAXBElement<Short> value) {
        this.codigoMargem = value;
    }

    /**
     * Obtém o valor da propriedade indice.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getIndice() {
        return indice;
    }

    /**
     * Define o valor da propriedade indice.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setIndice(JAXBElement<String> value) {
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

}
