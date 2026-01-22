package com.zetra.econsig.parser.febraban.exception;

/**
 * <p>Title: ArquivoInvalidoException</p>
 * <p>Description: Exceção que invalida um arquivo de lote do
 * padrão FEBRABAN CNAB 240 </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor Lucas
 * $Author$
 * $Revision$
 * $Date$
 */
public class ArquivoInvalidoException extends FebrabanException {

    public static final String OCORRENCIA_ARQUIVO_INVALIDO = "HI"; // HI =  Arquivo não aceito

    public ArquivoInvalidoException() {
        super(OCORRENCIA_ARQUIVO_INVALIDO);
    }

    public ArquivoInvalidoException(String message) {
        super(message);
    }
}
