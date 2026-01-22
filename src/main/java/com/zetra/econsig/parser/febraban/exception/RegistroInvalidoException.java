package com.zetra.econsig.parser.febraban.exception;

/**
 * <p>Title: RegistroInvalidoException</p>
 * <p>Description: Exceção que invalida um registro de um lote de um arquivo do
 * padrão FEBRABAN CNAB 240 </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor Lucas
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegistroInvalidoException extends FebrabanException {

    public RegistroInvalidoException(String message) {
        super(message);
    }
}
