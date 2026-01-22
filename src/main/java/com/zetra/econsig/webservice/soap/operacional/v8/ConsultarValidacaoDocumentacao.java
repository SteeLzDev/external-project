//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.5
// Consulte https://eclipse-ee4j.github.io/jaxb-ri
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem.
//


package com.zetra.econsig.webservice.soap.operacional.v8;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de anonymous complex type.</p>
 *
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.</p>
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
 *         <element name="statusValidacao" type="{StatusValidacao}StatusValidacao" minOccurs="0"/>
 *         <element name="pagina" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
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
    "statusValidacao",
    "pagina"
})
@XmlRootElement(name = "consultarValidacaoDocumentacao", namespace = "HostaHostService-v8_0")
public class ConsultarValidacaoDocumentacao {

    @XmlElementRef(name = "cliente", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cliente;

    @XmlElementRef(name = "convenio", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> convenio;

    @XmlElement(namespace = "HostaHostService-v8_0", required = true)
    protected java.lang.String usuario;

    @XmlElement(namespace = "HostaHostService-v8_0", required = true)
    protected java.lang.String senha;

    @XmlElementRef(name = "statusValidacao", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<StatusValidacao> statusValidacao;

    @XmlElementRef(name = "pagina", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Integer> pagina;

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
        cliente = value;
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
        convenio = value;
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
        usuario = value;
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
        senha = value;
    }

    /**
     * Obtém o valor da propriedade statusValidacao.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link StatusValidacao }{@code >}
     *
     */
    public JAXBElement<StatusValidacao> getStatusValidacao() {
        return statusValidacao;
    }

    /**
     * Define o valor da propriedade statusValidacao.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link StatusValidacao }{@code >}
     *
     */
    public void setStatusValidacao(JAXBElement<StatusValidacao> value) {
        statusValidacao = value;
    }

    /**
     * Obtém o valor da propriedade pagina.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *
     */
    public JAXBElement<java.lang.Integer> getPagina() {
        return pagina;
    }

    /**
     * Define o valor da propriedade pagina.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Integer }{@code >}
     *
     */
    public void setPagina(JAXBElement<java.lang.Integer> value) {
        pagina = value;
    }

}
