package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ZetraException</p>
 * <p>Description: Exceção gerada para limite de tentativa excedida.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LimiteTentativaExcedidaException extends ZetraException {

    public LimiteTentativaExcedidaException(Throwable cause) {
        super(cause);
    }

    public LimiteTentativaExcedidaException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public LimiteTentativaExcedidaException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
