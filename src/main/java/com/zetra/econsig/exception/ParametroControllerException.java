package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ParametroControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer alguma modificação nos Parametros
 * (Tarifação, Serviço, Sistema, ...).</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParametroControllerException extends ZetraException {

    public ParametroControllerException(Throwable ex) {
        super(ex);
    }

    public ParametroControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public ParametroControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
