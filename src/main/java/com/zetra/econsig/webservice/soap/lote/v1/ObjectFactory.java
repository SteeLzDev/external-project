//
// Este arquivo foi gerado pela Eclipse Implementation of JAXB, v4.0.3
// Consulte https://eclipse-ee4j.github.io/jaxb-ri
// Todas as modificações neste arquivo serão perdidas após a recompilação do esquema de origem.
//


package com.zetra.econsig.webservice.soap.lote.v1;

import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the com.zetra.econsig.webservice.soap.lote.v1 package.
 * <p>An ObjectFactory allows you to programmatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.zetra.econsig.webservice.soap.lote.v1
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LoteUploadRequest }
     *
     * @return
     *     the new instance of {@link LoteUploadRequest }
     */
    public LoteUploadRequest createLoteUploadRequest() {
        return new LoteUploadRequest();
    }

    /**
     * Create an instance of {@link Anexo }
     *
     * @return
     *     the new instance of {@link Anexo }
     */
    public Anexo createAnexo() {
        return new Anexo();
    }

    /**
     * Create an instance of {@link LoteUploadResponse }
     *
     * @return
     *     the new instance of {@link LoteUploadResponse }
     */
    public LoteUploadResponse createLoteUploadResponse() {
        return new LoteUploadResponse();
    }

    /**
     * Create an instance of {@link LoteResultRequest }
     *
     * @return
     *     the new instance of {@link LoteResultRequest }
     */
    public LoteResultRequest createLoteResultRequest() {
        return new LoteResultRequest();
    }

    /**
     * Create an instance of {@link LoteResultResponse }
     *
     * @return
     *     the new instance of {@link LoteResultResponse }
     */
    public LoteResultResponse createLoteResultResponse() {
        return new LoteResultResponse();
    }

}
