package com.zetra.econsig.parser.febraban.exception;

/**
 * <p>Title: LoteInvalidoException</p>
 * <p>Description: Exceção que invalida um lote de um arquivo do
 * padrão FEBRABAN CNAB 240 </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor Lucas
 * $Author$
 * $Revision$
 * $Date$
 */
public class LoteInvalidoException extends FebrabanException {

    public static final String OCORRENCIA_LOTE_INVALIDO = "HA"; // HA = Lote Não Aceito

    public LoteInvalidoException() {
        super(OCORRENCIA_LOTE_INVALIDO);
    }

    public LoteInvalidoException(String message) {
        super(message);
    }
}
