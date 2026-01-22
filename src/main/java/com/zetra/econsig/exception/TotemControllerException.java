package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TotemControllerException</p>
 * <p>Description: Exception gerada ao se tentar acessos ao Totem.</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TotemControllerException extends ZetraException {
    private static final long serialVersionUID = 1L;

    public TotemControllerException(Throwable ex) {
        super(ex);
    }

    public TotemControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public TotemControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
