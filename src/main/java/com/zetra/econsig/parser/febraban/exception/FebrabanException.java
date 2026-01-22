package com.zetra.econsig.parser.febraban.exception;

/**
 * <p>Title: FebrabanException</p>
 * <p>Description: Exceção pai da hierarquia de exceções do
 * padrão FEBRABAN CNAB 240 </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor Lucas
 * $Author$
 * $Revision$
 * $Date$
 */
public class FebrabanException extends Exception {
    public FebrabanException(String message) {
        super(message);
    }
}
