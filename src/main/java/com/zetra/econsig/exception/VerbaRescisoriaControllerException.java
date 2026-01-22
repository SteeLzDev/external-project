package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: VerbaRescisoriaControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer alguma modificação nas entidades de Verba Rescisoria RSE</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class VerbaRescisoriaControllerException extends ZetraException {

    public VerbaRescisoriaControllerException(Throwable ex) {
        super(ex);
    }

    public VerbaRescisoriaControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public VerbaRescisoriaControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
