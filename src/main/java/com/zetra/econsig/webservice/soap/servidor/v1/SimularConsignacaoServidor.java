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
 *         <element name="senha" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="loginServidor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="servicoCodigo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="valorParcela" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="valorLiberado" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         <element name="prazo" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
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
    "senha",
    "loginServidor",
    "servicoCodigo",
    "valorParcela",
    "valorLiberado",
    "prazo"
})
@XmlRootElement(name = "simularConsignacaoServidor", namespace = "ServidorService-v1_0")
public class SimularConsignacaoServidor {

    @XmlElementRef(name = "estabelecimentoCodigo", namespace = "ServidorService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> estabelecimentoCodigo;
    @XmlElementRef(name = "orgaoCodigo", namespace = "ServidorService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> orgaoCodigo;
    @XmlElement(namespace = "ServidorService-v1_0", required = true)
    protected String matricula;
    @XmlElement(namespace = "ServidorService-v1_0", required = true)
    protected String senha;
    @XmlElementRef(name = "loginServidor", namespace = "ServidorService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> loginServidor;
    @XmlElementRef(name = "servicoCodigo", namespace = "ServidorService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<String> servicoCodigo;
    @XmlElementRef(name = "valorParcela", namespace = "ServidorService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> valorParcela;
    @XmlElementRef(name = "valorLiberado", namespace = "ServidorService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<Double> valorLiberado;
    @XmlElementRef(name = "prazo", namespace = "ServidorService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> prazo;

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
     * Obtém o valor da propriedade loginServidor.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getLoginServidor() {
        return loginServidor;
    }

    /**
     * Define o valor da propriedade loginServidor.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setLoginServidor(JAXBElement<String> value) {
        this.loginServidor = value;
    }

    /**
     * Obtém o valor da propriedade servicoCodigo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public JAXBElement<String> getServicoCodigo() {
        return servicoCodigo;
    }

    /**
     * Define o valor da propriedade servicoCodigo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     
     */
    public void setServicoCodigo(JAXBElement<String> value) {
        this.servicoCodigo = value;
    }

    /**
     * Obtém o valor da propriedade valorParcela.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getValorParcela() {
        return valorParcela;
    }

    /**
     * Define o valor da propriedade valorParcela.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setValorParcela(JAXBElement<Double> value) {
        this.valorParcela = value;
    }

    /**
     * Obtém o valor da propriedade valorLiberado.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public JAXBElement<Double> getValorLiberado() {
        return valorLiberado;
    }

    /**
     * Define o valor da propriedade valorLiberado.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Double }{@code >}
     *     
     */
    public void setValorLiberado(JAXBElement<Double> value) {
        this.valorLiberado = value;
    }

    /**
     * Obtém o valor da propriedade prazo.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getPrazo() {
        return prazo;
    }

    /**
     * Define o valor da propriedade prazo.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setPrazo(JAXBElement<Integer> value) {
        this.prazo = value;
    }

}
