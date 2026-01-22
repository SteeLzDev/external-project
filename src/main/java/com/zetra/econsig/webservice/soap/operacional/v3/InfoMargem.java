//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v3;

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
    "valorMargemLimite"
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

}
