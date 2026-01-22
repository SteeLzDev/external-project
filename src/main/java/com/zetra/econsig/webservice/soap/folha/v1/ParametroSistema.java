//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.folha.v1;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de ParametroSistema complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="ParametroSistema">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="consignanteCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="codigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="valor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ParametroSistema", namespace = "ParametroSistema", propOrder = {
    "consignanteCodigo",
    "codigo",
    "valor"
})
public class ParametroSistema {

    @XmlElement(namespace = "ParametroSistema", required = true)
    protected java.lang.String consignanteCodigo;
    @XmlElement(namespace = "ParametroSistema", required = true)
    protected java.lang.String codigo;
    @XmlElementRef(name = "valor", namespace = "ParametroSistema", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> valor;

    /**
     * Obtém o valor da propriedade consignanteCodigo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getConsignanteCodigo() {
        return consignanteCodigo;
    }

    /**
     * Define o valor da propriedade consignanteCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setConsignanteCodigo(java.lang.String value) {
        this.consignanteCodigo = value;
    }

    /**
     * Obtém o valor da propriedade codigo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getCodigo() {
        return codigo;
    }

    /**
     * Define o valor da propriedade codigo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setCodigo(java.lang.String value) {
        this.codigo = value;
    }

    /**
     * Obtém o valor da propriedade valor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getValor() {
        return valor;
    }

    /**
     * Define o valor da propriedade valor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setValor(JAXBElement<java.lang.String> value) {
        this.valor = value;
    }

}
