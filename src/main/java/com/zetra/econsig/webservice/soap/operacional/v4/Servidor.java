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
 *         <element name="servidor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="matricula" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="estabelecimentoCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="estabelecimento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="orgaoCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="orgao" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="dataNascimento" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="situacaoServidor" type="{SituacaoServidor}SituacaoServidor" minOccurs="0"/>
 *         <element name="categoria" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="dataAdmissao" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         <element name="prazoServidor" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="salarioLiquido" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="salarioBruto" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
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
    "servidor",
    "cpf",
    "matricula",
    "estabelecimentoCodigo",
    "estabelecimento",
    "orgaoCodigo",
    "orgao",
    "dataNascimento",
    "situacaoServidor",
    "categoria",
    "dataAdmissao",
    "prazoServidor",
    "salarioLiquido",
    "salarioBruto"
})
public class Servidor {

    @XmlElement(namespace = "Servidor", required = true)
    protected java.lang.String servidor;
    @XmlElement(namespace = "Servidor", required = true)
    protected java.lang.String cpf;
    @XmlElement(namespace = "Servidor", required = true)
    protected java.lang.String matricula;
    @XmlElement(namespace = "Servidor", required = true)
    protected java.lang.String estabelecimentoCodigo;
    @XmlElement(namespace = "Servidor", required = true)
    protected java.lang.String estabelecimento;
    @XmlElement(namespace = "Servidor", required = true)
    protected java.lang.String orgaoCodigo;
    @XmlElement(namespace = "Servidor", required = true)
    protected java.lang.String orgao;
    @XmlElementRef(name = "dataNascimento", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataNascimento;
    @XmlElementRef(name = "situacaoServidor", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<SituacaoServidor> situacaoServidor;
    @XmlElementRef(name = "categoria", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> categoria;
    @XmlElementRef(name = "dataAdmissao", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<XMLGregorianCalendar> dataAdmissao;
    @XmlElementRef(name = "prazoServidor", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Integer> prazoServidor;
    @XmlElementRef(name = "salarioLiquido", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> salarioLiquido;
    @XmlElementRef(name = "salarioBruto", namespace = "Servidor", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> salarioBruto;

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

}
