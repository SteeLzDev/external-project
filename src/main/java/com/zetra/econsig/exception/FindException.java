package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: FindException</p>
 * <p>Description: Exceção para erro na consulta de entidade.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FindException extends ZetraException {

    protected FindException(String message) {
        super(message);
    }

    public FindException(Throwable cause) {
        super(cause);
    }

    public FindException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public FindException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }

    public static FindException byMessage(String message) {
        return new FindException(message);
    }
}
