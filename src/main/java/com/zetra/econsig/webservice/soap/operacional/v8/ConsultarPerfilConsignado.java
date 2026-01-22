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
 *         <element name="matricula" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "matricula",
    "cpf",
    "orgaoCodigo",
    "estabelecimentoCodigo"
})
@XmlRootElement(name = "consultarPerfilConsignado", namespace = "HostaHostService-v8_0")
public class ConsultarPerfilConsignado {

    @XmlElementRef(name = "cliente", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> cliente;
    @XmlElementRef(name = "convenio", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> convenio;
    @XmlElement(namespace = "HostaHostService-v8_0", required = true)
    protected String usuario;
    @XmlElement(namespace = "HostaHostService-v8_0", required = true)
    protected String senha;
    @XmlElement(namespace = "HostaHostService-v8_0")
    protected String matricula;
    @XmlElementRef(name = "cpf", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> cpf;
    @XmlElementRef(name = "orgaoCodigo", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> orgaoCodigo;
    @XmlElementRef(name = "estabelecimentoCodigo", namespace = "HostaHostService-v8_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> estabelecimentoCodigo;

    /**
     * Obtém o valor da propriedade cliente.
     * 
     * @return
     *     possible object is
     *     {@link jakarta.xml.bind.JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCliente() {
        return cliente;
    }

    /**
     * Define o valor da propriedade cliente.
     * 
     * @param value
     *     allowed object is
     *     {@link jakarta.xml.bind.JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCliente(JAXBElement<String> value) {
        this.cliente = value;
    }

    /**
     * Obtém o valor da propriedade convenio.
     * 
     * @return
     *     possible object is
     *     {@link jakarta.xml.bind.JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getConvenio() {
        return convenio;
    }

    /**
     * Define o valor da propriedade convenio.
     * 
     * @param value
     *     allowed object is
     *     {@link jakarta.xml.bind.JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setConvenio(JAXBElement<String> value) {
        this.convenio = value;
    }

    /**
     * Obtém o valor da propriedade usuario.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Define o valor da propriedade usuario.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsuario(String value) {
        this.usuario = value;
    }

    /**
     * Obtém o valor da propriedade senha.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Define o valor da propriedade senha.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSenha(String value) {
        this.senha = value;
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
     * Obtém o valor da propriedade cpf.
     * 
     * @return
     *     possible object is
     *     {@link jakarta.xml.bind.JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getCpf() {
        return cpf;
    }

    /**
     * Define o valor da propriedade cpf.
     * 
     * @param value
     *     allowed object is
     *     {@link jakarta.xml.bind.JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setCpf(JAXBElement<String> value) {
        this.cpf = value;
    }

    /**
     * Obtém o valor da propriedade orgaoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link jakarta.xml.bind.JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getOrgaoCodigo() {
        return orgaoCodigo;
    }

    /**
     * Define o valor da propriedade orgaoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link jakarta.xml.bind.JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setOrgaoCodigo(JAXBElement<String> value) {
        this.orgaoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade estabelecimentoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link jakarta.xml.bind.JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getEstabelecimentoCodigo() {
        return estabelecimentoCodigo;
    }

    /**
     * Define o valor da propriedade estabelecimentoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link jakarta.xml.bind.JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEstabelecimentoCodigo(JAXBElement<String> value) {
        this.estabelecimentoCodigo = value;
    }

}
