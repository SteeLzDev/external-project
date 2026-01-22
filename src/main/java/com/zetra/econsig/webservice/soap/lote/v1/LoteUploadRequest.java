//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.lote.v1;

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
 *         <element name="tipo" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="leiaute" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="lote" type="{Anexo}Anexo"/>
 *         <element name="identificador" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "tipo",
    "leiaute",
    "lote",
    "identificador"
})
@XmlRootElement(name = "loteUploadRequest", namespace = "LoteService-v1_0")
public class LoteUploadRequest {

    @XmlElement(namespace = "LoteService-v1_0", required = true)
    protected java.lang.String usuario;
    @XmlElement(namespace = "LoteService-v1_0", required = true)
    protected java.lang.String senha;
    @XmlElement(namespace = "LoteService-v1_0", required = true)
    protected java.lang.String tipo;
    @XmlElement(namespace = "LoteService-v1_0", required = true)
    protected java.lang.String leiaute;
    @XmlElement(namespace = "LoteService-v1_0", required = true)
    protected Anexo lote;
    @XmlElement(namespace = "LoteService-v1_0", required = true)
    protected java.lang.String identificador;

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
     * Obtém o valor da propriedade tipo.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getTipo() {
        return tipo;
    }

    /**
     * Define o valor da propriedade tipo.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setTipo(java.lang.String value) {
        this.tipo = value;
    }

    /**
     * Obtém o valor da propriedade leiaute.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getLeiaute() {
        return leiaute;
    }

    /**
     * Define o valor da propriedade leiaute.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setLeiaute(java.lang.String value) {
        this.leiaute = value;
    }

    /**
     * Obtém o valor da propriedade lote.
     * 
     * @return
     *     possible object is
     *     {@link Anexo }
     *     
     */
    public Anexo getLote() {
        return lote;
    }

    /**
     * Define o valor da propriedade lote.
     * 
     * @param value
     *     allowed object is
     *     {@link Anexo }
     *     
     */
    public void setLote(Anexo value) {
        this.lote = value;
    }

    /**
     * Obtém o valor da propriedade identificador.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.String }
     *     
     */
    public java.lang.String getIdentificador() {
        return identificador;
    }

    /**
     * Define o valor da propriedade identificador.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.String }
     *     
     */
    public void setIdentificador(java.lang.String value) {
        this.identificador = value;
    }

}
