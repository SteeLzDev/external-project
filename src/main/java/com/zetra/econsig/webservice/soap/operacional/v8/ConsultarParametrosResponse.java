//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v8;

import java.util.ArrayList;
import java.util.List;
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
 *         <element name="sucesso" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         <element name="codRetorno" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="mensagem" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="servicos" type="{Servico}Servico" maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="parametroSet" type="{ParametroSet}ParametroSet" minOccurs="0"/>
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
    "sucesso",
    "codRetorno",
    "mensagem",
    "servicos",
    "parametroSet"
})
@XmlRootElement(name = "consultarParametrosResponse", namespace = "HostaHostService-v8_0")
public class ConsultarParametrosResponse {

    @XmlElement(namespace = "HostaHostService-v8_0")
    protected boolean sucesso;
    @XmlElementRef(name = "codRetorno", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> codRetorno;
    @XmlElement(namespace = "HostaHostService-v8_0", required = true)
    protected java.lang.String mensagem;
    @XmlElement(namespace = "HostaHostService-v8_0", nillable = true)
    protected List<Servico> servicos;
    @XmlElementRef(name = "parametroSet", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<ParametroSet> parametroSet;

    /**
     * Obtém o valor da propriedade sucesso.
     * 
     */
    public boolean isSucesso() {
        return sucesso;
    }

    /**
     * Define o valor da propriedade sucesso.
     * 
     */
    public void setSucesso(boolean value) {
        this.sucesso = value;
    }

    /**
     * Obtém o valor da propriedade codRetorno.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCodRetorno() {
        return codRetorno;
    }

    /**
     * Define o valor da propriedade codRetorno.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCodRetorno(JAXBElement<java.lang.String> value) {
        this.codRetorno = value;
    }

    /**
     * Obtém o valor da propriedade mensagem.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getMensagem() {
        return mensagem;
    }

    /**
     * Define o valor da propriedade mensagem.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setMensagem(java.lang.String value) {
        this.mensagem = value;
    }

    /**
     * Gets the value of the servicos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the servicos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getServicos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Servico }
     * 
     * 
     * @return
     *     The value of the servicos property.
     */
    public List<Servico> getServicos() {
        if (servicos == null) {
            servicos = new ArrayList<>();
        }
        return this.servicos;
    }

    /**
     * Obtém o valor da propriedade parametroSet.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ParametroSet }{@code >}
     *     
     */
    public JAXBElement<ParametroSet> getParametroSet() {
        return parametroSet;
    }

    /**
     * Define o valor da propriedade parametroSet.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ParametroSet }{@code >}
     *     
     */
    public void setParametroSet(JAXBElement<ParametroSet> value) {
        this.parametroSet = value;
    }

}
