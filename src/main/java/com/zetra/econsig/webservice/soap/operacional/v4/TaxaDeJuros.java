//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3 
// Consulte https://eclipse-ee4j.github.io/jaxb-ri 
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem. 
//


package com.zetra.econsig.webservice.soap.operacional.v4;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java de TaxaDeJuros complex type.
 * 
 * <p>O seguinte fragmento do esquema especifica o conteúdo esperado contido dentro desta classe.
 * 
 * <pre>{@code
 * <complexType name="TaxaDeJuros">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="prazo" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         <element name="taxa" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaxaDeJuros", namespace = "TaxaDeJuros", propOrder = {
    "prazo",
    "taxa"
})
public class TaxaDeJuros {

    @XmlElement(namespace = "TaxaDeJuros")
    protected long prazo;
    @XmlElement(namespace = "TaxaDeJuros")
    protected double taxa;

    /**
     * Obtém o valor da propriedade prazo.
     * 
     */
    public long getPrazo() {
        return prazo;
    }

    /**
     * Define o valor da propriedade prazo.
     * 
     */
    public void setPrazo(long value) {
        this.prazo = value;
    }

    /**
     * Obtém o valor da propriedade taxa.
     * 
     */
    public double getTaxa() {
        return taxa;
    }

    /**
     * Define o valor da propriedade taxa.
     * 
     */
    public void setTaxa(double value) {
        this.taxa = value;
    }

}
