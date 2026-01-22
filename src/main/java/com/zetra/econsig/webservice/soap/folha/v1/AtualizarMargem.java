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
 *         <element name="usuario" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="senha" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="matricula" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="orgaoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="estabelecimentoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="margem1" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="margem2" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="margem3" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
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
    "usuario",
    "senha",
    "matricula",
    "cpf",
    "orgaoCodigo",
    "estabelecimentoCodigo",
    "margem1",
    "margem2",
    "margem3"
})
@XmlRootElement(name = "atualizarMargem", namespace = "FolhaService-v1_0")
public class AtualizarMargem {

    @XmlElement(namespace = "FolhaService-v1_0", required = true)
    protected java.lang.String usuario;
    @XmlElement(namespace = "FolhaService-v1_0", required = true)
    protected java.lang.String senha;
    @XmlElement(namespace = "FolhaService-v1_0")
    protected java.lang.String matricula;
    @XmlElementRef(name = "cpf", namespace = "FolhaService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> cpf;
    @XmlElementRef(name = "orgaoCodigo", namespace = "FolhaService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> orgaoCodigo;
    @XmlElementRef(name = "estabelecimentoCodigo", namespace = "FolhaService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> estabelecimentoCodigo;
    @XmlElementRef(name = "margem1", namespace = "FolhaService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> margem1;
    @XmlElementRef(name = "margem2", namespace = "FolhaService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> margem2;
    @XmlElementRef(name = "margem3", namespace = "FolhaService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.Double> margem3;

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
     * Obtém o valor da propriedade matricula.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getMatricula() {
        return matricula;
    }

    /**
     * Define o valor da propriedade matricula.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setMatricula(java.lang.String value) {
        this.matricula = value;
    }

    /**
     * Obtém o valor da propriedade cpf.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCpf() {
        return cpf;
    }

    /**
     * Define o valor da propriedade cpf.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCpf(JAXBElement<java.lang.String> value) {
        this.cpf = value;
    }

    /**
     * Obtém o valor da propriedade orgaoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getOrgaoCodigo() {
        return orgaoCodigo;
    }

    /**
     * Define o valor da propriedade orgaoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setOrgaoCodigo(JAXBElement<java.lang.String> value) {
        this.orgaoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade estabelecimentoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getEstabelecimentoCodigo() {
        return estabelecimentoCodigo;
    }

    /**
     * Define o valor da propriedade estabelecimentoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setEstabelecimentoCodigo(JAXBElement<java.lang.String> value) {
        this.estabelecimentoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade margem1.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getMargem1() {
        return margem1;
    }

    /**
     * Define o valor da propriedade margem1.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setMargem1(JAXBElement<java.lang.Double> value) {
        this.margem1 = value;
    }

    /**
     * Obtém o valor da propriedade margem2.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getMargem2() {
        return margem2;
    }

    /**
     * Define o valor da propriedade margem2.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setMargem2(JAXBElement<java.lang.Double> value) {
        this.margem2 = value;
    }

    /**
     * Obtém o valor da propriedade margem3.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public JAXBElement<java.lang.Double> getMargem3() {
        return margem3;
    }

    /**
     * Define o valor da propriedade margem3.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.Double }{@code >}
     *     
     */
    public void setMargem3(JAXBElement<java.lang.Double> value) {
        this.margem3 = value;
    }

}
