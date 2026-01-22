//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.folha.v1;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Arquivo complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="Arquivo">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="nome" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="tamanho" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="dataModificacao" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         <element name="codigoOrgao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codigoEstabelecimento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Arquivo", namespace = "Arquivo", propOrder = {
    "nome",
    "tamanho",
    "dataModificacao",
    "codigoOrgao",
    "codigoEstabelecimento"
})
public class Arquivo {

    @XmlElement(namespace = "Arquivo", required = true)
    protected java.lang.String nome;
    @XmlElement(namespace = "Arquivo", required = true)
    protected java.lang.String tamanho;
    @XmlElement(namespace = "Arquivo", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataModificacao;
    @XmlElementRef(name = "codigoOrgao", namespace = "Arquivo", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> codigoOrgao;
    @XmlElementRef(name = "codigoEstabelecimento", namespace = "Arquivo", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> codigoEstabelecimento;

    /**
     * Obtém o valor da propriedade nome.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getNome() {
        return nome;
    }

    /**
     * Define o valor da propriedade nome.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setNome(java.lang.String value) {
        this.nome = value;
    }

    /**
     * Obtém o valor da propriedade tamanho.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getTamanho() {
        return tamanho;
    }

    /**
     * Define o valor da propriedade tamanho.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setTamanho(java.lang.String value) {
        this.tamanho = value;
    }

    /**
     * Obtém o valor da propriedade dataModificacao.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataModificacao() {
        return dataModificacao;
    }

    /**
     * Define o valor da propriedade dataModificacao.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataModificacao(XMLGregorianCalendar value) {
        this.dataModificacao = value;
    }

    /**
     * Obtém o valor da propriedade codigoOrgao.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCodigoOrgao() {
        return codigoOrgao;
    }

    /**
     * Define o valor da propriedade codigoOrgao.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCodigoOrgao(JAXBElement<java.lang.String> value) {
        this.codigoOrgao = value;
    }

    /**
     * Obtém o valor da propriedade codigoEstabelecimento.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCodigoEstabelecimento() {
        return codigoEstabelecimento;
    }

    /**
     * Define o valor da propriedade codigoEstabelecimento.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCodigoEstabelecimento(JAXBElement<java.lang.String> value) {
        this.codigoEstabelecimento = value;
    }

}
