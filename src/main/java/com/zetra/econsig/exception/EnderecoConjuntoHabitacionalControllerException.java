package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: EnderecoConjuntoHabitacionalException</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnderecoConjuntoHabitacionalControllerException extends ZetraException {

    public EnderecoConjuntoHabitacionalControllerException(Throwable ex) {
        super(ex);
    }

    public EnderecoConjuntoHabitacionalControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public EnderecoConjuntoHabitacionalControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
