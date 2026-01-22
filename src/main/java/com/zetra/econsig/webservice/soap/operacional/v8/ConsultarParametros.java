//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
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
 *         <element name="codVerba" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="servicoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="orgaoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="estabelecimentoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "codVerba",
    "servicoCodigo",
    "orgaoCodigo",
    "estabelecimentoCodigo",
    "dadosSistema"
})
@XmlRootElement(name = "consultarParametros", namespace = "HostaHostService-v8_0")
public class ConsultarParametros {

    @XmlElementRef(name = "cliente", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cliente;
    @XmlElementRef(name = "convenio", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> convenio;
    @XmlElement(namespace = "HostaHostService-v8_0", required = true)
    protected java.lang.String usuario;
    @XmlElement(namespace = "HostaHostService-v8_0", required = true)
    protected java.lang.String senha;
    @XmlElementRef(name = "codVerba", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> codVerba;
    @XmlElementRef(name = "servicoCodigo", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> servicoCodigo;
    @XmlElement(namespace = "HostaHostService-v8_0")
    protected java.lang.String orgaoCodigo;
    @XmlElement(namespace = "HostaHostService-v8_0")
    protected java.lang.String estabelecimentoCodigo;
    @XmlElement(namespace = "HostaHostService-v8_0")
    protected java.lang.Boolean dadosSistema;

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
     * Obtém o valor da propriedade codVerba.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCodVerba() {
        return codVerba;
    }

    /**
     * Define o valor da propriedade codVerba.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCodVerba(JAXBElement<java.lang.String> value) {
        this.codVerba = value;
    }

    /**
     * Obtém o valor da propriedade servicoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getServicoCodigo() {
        return servicoCodigo;
    }

    /**
     * Define o valor da propriedade servicoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setServicoCodigo(JAXBElement<java.lang.String> value) {
        this.servicoCodigo = value;
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
     * Obtém o valor da propriedade estabelecimentoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getDadosSistema() {
        return dadosSistema;
    }

    /**
     * Define o valor da propriedade estabelecimentoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setDadosSistema(java.lang.Boolean dadosSistema) {
        this.dadosSistema = dadosSistema;
    }

}
