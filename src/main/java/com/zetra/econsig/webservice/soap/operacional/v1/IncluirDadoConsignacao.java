//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v1;

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
 *         <element name="cliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="convenio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="usuario" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="senha" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="adeNumero" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         <element name="adeIdentificador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="dadoCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="dadoValor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "adeNumero",
    "adeIdentificador",
    "dadoCodigo",
    "dadoValor"
})
@XmlRootElement(name = "incluirDadoConsignacao", namespace = "HostaHostService-v1_0")
public class IncluirDadoConsignacao {

    @XmlElementRef(name = "cliente", namespace = "HostaHostService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cliente;
    @XmlElementRef(name = "convenio", namespace = "HostaHostService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> convenio;
    @XmlElement(namespace = "HostaHostService-v1_0", required = true)
    protected java.lang.String usuario;
    @XmlElement(namespace = "HostaHostService-v1_0", required = true)
    protected java.lang.String senha;
    @XmlElementRef(name = "adeNumero", namespace = "HostaHostService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Long> adeNumero;
    @XmlElementRef(name = "adeIdentificador", namespace = "HostaHostService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> adeIdentificador;
    @XmlElement(namespace = "HostaHostService-v1_0", required = true)
    protected java.lang.String dadoCodigo;
    @XmlElementRef(name = "dadoValor", namespace = "HostaHostService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> dadoValor;

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
        this.cliente = value;
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
        this.convenio = value;
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
        this.usuario = value;
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
        this.senha = value;
    }

    /**
     * Obtém o valor da propriedade adeNumero.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *     
     */
    public JAXBElement<java.lang.Long> getAdeNumero() {
        return adeNumero;
    }

    /**
     * Define o valor da propriedade adeNumero.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Long }{@code >}
     *     
     */
    public void setAdeNumero(JAXBElement<java.lang.Long> value) {
        this.adeNumero = value;
    }

    /**
     * Obtém o valor da propriedade adeIdentificador.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getAdeIdentificador() {
        return adeIdentificador;
    }

    /**
     * Define o valor da propriedade adeIdentificador.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setAdeIdentificador(JAXBElement<java.lang.String> value) {
        this.adeIdentificador = value;
    }

    /**
     * Obtém o valor da propriedade dadoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getDadoCodigo() {
        return dadoCodigo;
    }

    /**
     * Define o valor da propriedade dadoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setDadoCodigo(java.lang.String value) {
        this.dadoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade dadoValor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getDadoValor() {
        return dadoValor;
    }

    /**
     * Define o valor da propriedade dadoValor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setDadoValor(JAXBElement<java.lang.String> value) {
        this.dadoValor = value;
    }

}
