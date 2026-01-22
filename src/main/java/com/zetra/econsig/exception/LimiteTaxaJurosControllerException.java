package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: LimiteTaxaJurosControllerException</p>
 * <p>Description: Exception gerada na manipulação de limite de taxa de juros </p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LimiteTaxaJurosControllerException extends ZetraException{

    public LimiteTaxaJurosControllerException(Throwable ex) {
        super(ex);
    }

    public LimiteTaxaJurosControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public LimiteTaxaJurosControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
