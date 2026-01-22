//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.servidor.v1;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
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
    "textoMargem3"
})
public class InfoMargem {

    @XmlElement(namespace = "InfoMargem")
    protected String estabelecimentoCodigo;
    @XmlElement(namespace = "InfoMargem")
    protected String estabelecimento;
    @XmlElement(namespace = "InfoMargem")
    protected String orgaoCodigo;
    @XmlElement(namespace = "InfoMargem")
    protected String orgao;
    @XmlElement(namespace = "InfoMargem")
    protected String categoria;
    @XmlElement(namespace = "InfoMargem", required = true)
    protected String servidor;
    @XmlElement(namespace = "InfoMargem")
    protected String cpf;
    @XmlElement(namespace = "InfoMargem", required = true)
    protected String matricula;
    @XmlElement(namespace = "InfoMargem")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataNascimento;
    @XmlElement(namespace = "InfoMargem")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataAdmissao;
    @XmlElement(namespace = "InfoMargem", required = true, type = Integer.class, nillable = true)
    protected Integer prazoServidor;
    @XmlElement(namespace = "InfoMargem", required = true, type = Double.class, nillable = true)
    protected Double valorMargem;
    @XmlElement(namespace = "InfoMargem", required = true, nillable = true)
    protected String textoMargem;
    @XmlElement(namespace = "InfoMargem", required = true, type = Double.class, nillable = true)
    protected Double valorMargem2;
    @XmlElement(namespace = "InfoMargem", required = true, nillable = true)
    protected String textoMargem2;
    @XmlElement(namespace = "InfoMargem", required = true, type = Double.class, nillable = true)
    protected Double valorMargem3;
    @XmlElement(namespace = "InfoMargem", required = true, nillable = true)
    protected String textoMargem3;

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
     * Obtém o valor da propriedade valorMargem.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getValorMargem() {
        return valorMargem;
    }

    /**
     * Define o valor da propriedade valorMargem.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setValorMargem(Double value) {
        this.valorMargem = value;
    }

    /**
     * Obtém o valor da propriedade textoMargem.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTextoMargem() {
        return textoMargem;
    }

    /**
     * Define o valor da propriedade textoMargem.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTextoMargem(String value) {
        this.textoMargem = value;
    }

    /**
     * Obtém o valor da propriedade valorMargem2.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getValorMargem2() {
        return valorMargem2;
    }

    /**
     * Define o valor da propriedade valorMargem2.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setValorMargem2(Double value) {
        this.valorMargem2 = value;
    }

    /**
     * Obtém o valor da propriedade textoMargem2.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTextoMargem2() {
        return textoMargem2;
    }

    /**
     * Define o valor da propriedade textoMargem2.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTextoMargem2(String value) {
        this.textoMargem2 = value;
    }

    /**
     * Obtém o valor da propriedade valorMargem3.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getValorMargem3() {
        return valorMargem3;
    }

    /**
     * Define o valor da propriedade valorMargem3.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setValorMargem3(Double value) {
        this.valorMargem3 = value;
    }

    /**
     * Obtém o valor da propriedade textoMargem3.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTextoMargem3() {
        return textoMargem3;
    }

    /**
     * Define o valor da propriedade textoMargem3.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTextoMargem3(String value) {
        this.textoMargem3 = value;
    }

}
