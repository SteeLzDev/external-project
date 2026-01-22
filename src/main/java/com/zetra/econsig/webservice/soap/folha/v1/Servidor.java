//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.folha.v1;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de Servidor complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="Servidor">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="servidor" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="cpf" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="matricula" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="estabelecimentoCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="estabelecimento" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="orgaoCodigo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="orgao" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Servidor", namespace = "Servidor", propOrder = {
    "servidor",
    "cpf",
    "matricula",
    "estabelecimentoCodigo",
    "estabelecimento",
    "orgaoCodigo",
    "orgao"
})
public class Servidor {

    @XmlElement(namespace = "Servidor", required = true)
    protected java.lang.String servidor;
    @XmlElement(namespace = "Servidor", required = true)
    protected java.lang.String cpf;
    @XmlElement(namespace = "Servidor", required = true)
    protected java.lang.String matricula;
    @XmlElement(namespace = "Servidor", required = true)
    protected java.lang.String estabelecimentoCodigo;
    @XmlElement(namespace = "Servidor", required = true)
    protected java.lang.String estabelecimento;
    @XmlElement(namespace = "Servidor", required = true)
    protected java.lang.String orgaoCodigo;
    @XmlElement(namespace = "Servidor", required = true)
    protected java.lang.String orgao;

    /**
     * Obtém o valor da propriedade servidor.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getServidor() {
        return servidor;
    }

    /**
     * Define o valor da propriedade servidor.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setServidor(java.lang.String value) {
        this.servidor = value;
    }

    /**
     * Obtém o valor da propriedade cpf.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getCpf() {
        return cpf;
    }

    /**
     * Define o valor da propriedade cpf.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setCpf(java.lang.String value) {
        this.cpf = value;
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
     * Obtém o valor da propriedade estabelecimento.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getEstabelecimento() {
        return estabelecimento;
    }

    /**
     * Define o valor da propriedade estabelecimento.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setEstabelecimento(java.lang.String value) {
        this.estabelecimento = value;
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
     * Obtém o valor da propriedade orgao.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getOrgao() {
        return orgao;
    }

    /**
     * Define o valor da propriedade orgao.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setOrgao(java.lang.String value) {
        this.orgao = value;
    }

}
