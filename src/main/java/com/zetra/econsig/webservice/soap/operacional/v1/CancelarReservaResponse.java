//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v1;

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
 *         <element name="boleto" type="{Boleto}Boleto" minOccurs="0"/>
 *         <element name="historicos" type="{Historico}Historico" maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="resumos" type="{Resumo}Resumo" maxOccurs="unbounded" minOccurs="0"/>
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
    "boleto",
    "historicos",
    "resumos"
})
@XmlRootElement(name = "cancelarReservaResponse", namespace = "HostaHostService-v1_0")
public class CancelarReservaResponse {

    @XmlElement(namespace = "HostaHostService-v1_0")
    protected boolean sucesso;
    @XmlElementRef(name = "codRetorno", namespace = "HostaHostService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> codRetorno;
    @XmlElement(namespace = "HostaHostService-v1_0", required = true)
    protected java.lang.String mensagem;
    @XmlElementRef(name = "boleto", namespace = "HostaHostService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<Boleto> boleto;
    @XmlElement(namespace = "HostaHostService-v1_0", nillable = true)
    protected List<Historico> historicos;
    @XmlElement(namespace = "HostaHostService-v1_0", nillable = true)
    protected List<Resumo> resumos;

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
     * Obtém o valor da propriedade boleto.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     *     
     */
    public JAXBElement<Boleto> getBoleto() {
        return boleto;
    }

    /**
     * Define o valor da propriedade boleto.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boleto }{@code >}
     *     
     */
    public void setBoleto(JAXBElement<Boleto> value) {
        this.boleto = value;
    }

    /**
     * Gets the value of the historicos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the historicos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHistoricos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Historico }
     * 
     * 
     * @return
     *     The value of the historicos property.
     */
    public List<Historico> getHistoricos() {
        if (historicos == null) {
            historicos = new ArrayList<>();
        }
        return this.historicos;
    }

    /**
     * Gets the value of the resumos property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the resumos property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResumos().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Resumo }
     * 
     * 
     * @return
     *     The value of the resumos property.
     */
    public List<Resumo> getResumos() {
        if (resumos == null) {
            resumos = new ArrayList<>();
        }
        return this.resumos;
    }

}
