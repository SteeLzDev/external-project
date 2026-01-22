//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.servidor.v2;

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
 *         <element name="podeSolicitar" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="podeSimular" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="simulacoes" type="{Simulacao}Simulacao" maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="servicos" type="{Servico}Servico" maxOccurs="unbounded" minOccurs="0"/>
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
    "podeSolicitar",
    "podeSimular",
    "simulacoes",
    "servicos"
})
@XmlRootElement(name = "simularConsignacaoServidorResponse", namespace = "ServidorService-v2_0")
public class SimularConsignacaoServidorResponse {

    @XmlElement(namespace = "ServidorService-v2_0")
    protected boolean sucesso;
    @XmlElementRef(name = "codRetorno", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> codRetorno;
    @XmlElement(namespace = "ServidorService-v2_0", required = true)
    protected String mensagem;
    @XmlElementRef(name = "podeSolicitar", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<Boolean> podeSolicitar;
    @XmlElementRef(name = "podeSimular", namespace = "ServidorService-v2_0", type = JAXBElement.class, required = false)
    protected JAXBElement<Boolean> podeSimular;
    @XmlElement(namespace = "ServidorService-v2_0", nillable = true)
    protected List<Simulacao> simulacoes;
    @XmlElement(namespace = "ServidorService-v2_0", nillable = true)
    protected List<Servico> servicos;

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
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCodRetorno() {
        return codRetorno;
    }

    /**
     * Define o valor da propriedade codRetorno.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCodRetorno(JAXBElement<String> value) {
        this.codRetorno = value;
    }

    /**
     * Obtém o valor da propriedade mensagem.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMensagem() {
        return mensagem;
    }

    /**
     * Define o valor da propriedade mensagem.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMensagem(String value) {
        this.mensagem = value;
    }

    /**
     * Obtém o valor da propriedade podeSolicitar.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getPodeSolicitar() {
        return podeSolicitar;
    }

    /**
     * Define o valor da propriedade podeSolicitar.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setPodeSolicitar(JAXBElement<Boolean> value) {
        this.podeSolicitar = value;
    }

    /**
     * Obtém o valor da propriedade podeSimular.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public JAXBElement<Boolean> getPodeSimular() {
        return podeSimular;
    }

    /**
     * Define o valor da propriedade podeSimular.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Boolean }{@code >}
     *     
     */
    public void setPodeSimular(JAXBElement<Boolean> value) {
        this.podeSimular = value;
    }

    /**
     * Gets the value of the simulacoes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the simulacoes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSimulacoes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Simulacao }
     * 
     * 
     * @return
     *     The value of the simulacoes property.
     */
    public List<Simulacao> getSimulacoes() {
        if (simulacoes == null) {
            simulacoes = new ArrayList<>();
        }
        return this.simulacoes;
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

}
