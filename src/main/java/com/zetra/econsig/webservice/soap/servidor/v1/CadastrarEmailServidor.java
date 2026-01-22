//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.servidor.v1;

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
 *         <element name="estabelecimentoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="orgaoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="matricula" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="email" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="usuario" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="senhaUsuario" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="protocoloCadastroEmail" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="banco" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="agencia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="conta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="exigeGrupoPerguntas" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "estabelecimentoCodigo",
    "orgaoCodigo",
    "matricula",
    "email",
    "usuario",
    "senhaUsuario",
    "protocoloCadastroEmail",
    "banco",
    "agencia",
    "conta",
    "exigeGrupoPerguntas"
})
@XmlRootElement(name = "cadastrarEmailServidor", namespace = "ServidorService-v1_0")
public class CadastrarEmailServidor {

    @XmlElementRef(name = "estabelecimentoCodigo", namespace = "ServidorService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> estabelecimentoCodigo;
    @XmlElementRef(name = "orgaoCodigo", namespace = "ServidorService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> orgaoCodigo;
    @XmlElement(namespace = "ServidorService-v1_0", required = true)
    protected String matricula;
    @XmlElement(namespace = "ServidorService-v1_0", required = true)
    protected String email;
    @XmlElement(namespace = "ServidorService-v1_0", required = true)
    protected String usuario;
    @XmlElement(namespace = "ServidorService-v1_0", required = true)
    protected String senhaUsuario;
    @XmlElement(namespace = "ServidorService-v1_0", required = true)
    protected String protocoloCadastroEmail;
    @XmlElementRef(name = "banco", namespace = "ServidorService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> banco;
    @XmlElementRef(name = "agencia", namespace = "ServidorService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> agencia;
    @XmlElementRef(name = "conta", namespace = "ServidorService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> conta;
    @XmlElementRef(name = "exigeGrupoPerguntas", namespace = "ServidorService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> exigeGrupoPerguntas;

    /**
     * Obtém o valor da propriedade estabelecimentoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
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
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setEstabelecimentoCodigo(JAXBElement<String> value) {
        this.estabelecimentoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade orgaoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
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
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setOrgaoCodigo(JAXBElement<String> value) {
        this.orgaoCodigo = value;
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
     * Obtém o valor da propriedade email.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define o valor da propriedade email.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
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
     * Obtém o valor da propriedade senhaUsuario.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSenhaUsuario() {
        return senhaUsuario;
    }

    /**
     * Define o valor da propriedade senhaUsuario.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSenhaUsuario(String value) {
        this.senhaUsuario = value;
    }

    /**
     * Obtém o valor da propriedade protocoloCadastroEmail.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProtocoloCadastroEmail() {
        return protocoloCadastroEmail;
    }

    /**
     * Define o valor da propriedade protocoloCadastroEmail.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProtocoloCadastroEmail(String value) {
        this.protocoloCadastroEmail = value;
    }

    /**
     * Obtém o valor da propriedade banco.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getBanco() {
        return banco;
    }

    /**
     * Define o valor da propriedade banco.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setBanco(JAXBElement<String> value) {
        this.banco = value;
    }

    /**
     * Obtém o valor da propriedade agencia.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getAgencia() {
        return agencia;
    }

    /**
     * Define o valor da propriedade agencia.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setAgencia(JAXBElement<String> value) {
        this.agencia = value;
    }

    /**
     * Obtém o valor da propriedade conta.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getConta() {
        return conta;
    }

    /**
     * Define o valor da propriedade conta.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setConta(JAXBElement<String> value) {
        this.conta = value;
    }

    /**
     * Obtém o valor da propriedade exigeGrupoPerguntas.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getExigeGrupoPerguntas() {
        return exigeGrupoPerguntas;
    }

    /**
     * Define o valor da propriedade exigeGrupoPerguntas.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setExigeGrupoPerguntas(JAXBElement<String> value) {
        this.exigeGrupoPerguntas = value;
    }

}
