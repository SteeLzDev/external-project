//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.folha.v1;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
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
 *         <element name="copiarConvenios" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         <element name="codigoOrgaoaCopiar" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="Orgao" type="{Orgao}Orgao"/>
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
    "copiarConvenios",
    "codigoOrgaoaCopiar",
    "orgao"
})
@XmlRootElement(name = "cadastrarOrgao", namespace = "FolhaService-v1_0")
public class CadastrarOrgao {

    @XmlElement(namespace = "FolhaService-v1_0", required = true)
    protected java.lang.String usuario;
    @XmlElement(namespace = "FolhaService-v1_0", required = true)
    protected java.lang.String senha;
    @XmlElement(namespace = "FolhaService-v1_0")
    protected java.lang.Boolean copiarConvenios;
    @XmlElement(namespace = "FolhaService-v1_0")
    protected java.lang.String codigoOrgaoaCopiar;
    @XmlElement(name = "Orgao", namespace = "FolhaService-v1_0", required = true)
    protected Orgao orgao;

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
     * Obtém o valor da propriedade copiarConvenios.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean getCopiarConvenios() {
        return copiarConvenios;
    }

    /**
     * Define o valor da propriedade copiarConvenios.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setCopiarConvenios(java.lang.Boolean value) {
        this.copiarConvenios = value;
    }

    /**
     * Obtém o valor da propriedade codigoOrgaoaCopiar.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getCodigoOrgaoaCopiar() {
        return codigoOrgaoaCopiar;
    }

    /**
     * Define o valor da propriedade codigoOrgaoaCopiar.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setCodigoOrgaoaCopiar(java.lang.String value) {
        this.codigoOrgaoaCopiar = value;
    }

    /**
     * Obtém o valor da propriedade orgao.
     * 
     * @return
     *     possible object is
     *     {@link Orgao }
     *     
     */
    public Orgao getOrgao() {
        return orgao;
    }

    /**
     * Define o valor da propriedade orgao.
     * 
     * @param value
     *     allowed object is
     *     {@link Orgao }
     *     
     */
    public void setOrgao(Orgao value) {
        this.orgao = value;
    }

}
