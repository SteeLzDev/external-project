package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ArquivoDirfControllerException</p>
 * <p>Description: Exception gerada nas operações sobre arquivo de DIRF</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ArquivoDirfControllerException extends ZetraException {

    public ArquivoDirfControllerException(Throwable ex) {
        super(ex);
    }

    public ArquivoDirfControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public ArquivoDirfControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
