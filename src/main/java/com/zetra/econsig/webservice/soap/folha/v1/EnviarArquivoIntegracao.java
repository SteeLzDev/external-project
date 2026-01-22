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
 *         <element name="tipoArquivo" type="{TipoArquivo}TipoArquivo"/>
 *         <element name="nomeArquivo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="arquivo" type="{http://schemas.xmlsoap.org/soap/encoding/}base64"/>
 *         <element name="observacao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codigoOrgao" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codigoEstabelecimento" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "tipoArquivo",
    "nomeArquivo",
    "arquivo",
    "observacao",
    "codigoOrgao",
    "codigoEstabelecimento"
})
@XmlRootElement(name = "enviarArquivoIntegracao", namespace = "FolhaService-v1_0")
public class EnviarArquivoIntegracao {

    @XmlElement(namespace = "FolhaService-v1_0", required = true)
    protected java.lang.String usuario;
    @XmlElement(namespace = "FolhaService-v1_0", required = true)
    protected java.lang.String senha;
    @XmlElement(namespace = "FolhaService-v1_0", required = true)
    protected TipoArquivo tipoArquivo;
    @XmlElement(namespace = "FolhaService-v1_0", required = true)
    protected java.lang.String nomeArquivo;
    @XmlElement(namespace = "FolhaService-v1_0", required = true)
    protected byte[] arquivo;
    @XmlElement(namespace = "FolhaService-v1_0")
    protected java.lang.String observacao;
    @XmlElement(namespace = "FolhaService-v1_0")
    protected java.lang.String codigoOrgao;
    @XmlElementRef(name = "codigoEstabelecimento", namespace = "FolhaService-v1_0", type = JAXBElement.class, required = false)
    protected JAXBElement<java.lang.String> codigoEstabelecimento;

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
     * Obtém o valor da propriedade tipoArquivo.
     * 
     * @return
     *     possible object is
     *     {@link TipoArquivo }
     *     
     */
    public TipoArquivo getTipoArquivo() {
        return tipoArquivo;
    }

    /**
     * Define o valor da propriedade tipoArquivo.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoArquivo }
     *     
     */
    public void setTipoArquivo(TipoArquivo value) {
        this.tipoArquivo = value;
    }

    /**
     * Obtém o valor da propriedade nomeArquivo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getNomeArquivo() {
        return nomeArquivo;
    }

    /**
     * Define o valor da propriedade nomeArquivo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setNomeArquivo(java.lang.String value) {
        this.nomeArquivo = value;
    }

    /**
     * Obtém o valor da propriedade arquivo.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getArquivo() {
        return arquivo;
    }

    /**
     * Define o valor da propriedade arquivo.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setArquivo(byte[] value) {
        this.arquivo = value;
    }

    /**
     * Obtém o valor da propriedade observacao.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getObservacao() {
        return observacao;
    }

    /**
     * Define o valor da propriedade observacao.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setObservacao(java.lang.String value) {
        this.observacao = value;
    }

    /**
     * Obtém o valor da propriedade codigoOrgao.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getCodigoOrgao() {
        return codigoOrgao;
    }

    /**
     * Define o valor da propriedade codigoOrgao.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setCodigoOrgao(java.lang.String value) {
        this.codigoOrgao = value;
    }

    /**
     * Obtém o valor da propriedade codigoEstabelecimento.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public JAXBElement<java.lang.String> getCodigoEstabelecimento() {
        return codigoEstabelecimento;
    }

    /**
     * Define o valor da propriedade codigoEstabelecimento.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link java.lang.String }{@code >}
     *     
     */
    public void setCodigoEstabelecimento(JAXBElement<java.lang.String> value) {
        this.codigoEstabelecimento = value;
    }

}
