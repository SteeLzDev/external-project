package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: BoletoServidorControllerException</p>
 * <p>Description: Exception gerada nas operações sobre boleto servidor</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BoletoServidorControllerException extends ZetraException {

    public BoletoServidorControllerException(Throwable ex) {
        super(ex);
    }

    public BoletoServidorControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public BoletoServidorControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
