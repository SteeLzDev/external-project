package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.webclient.creditotrabalhador.PebErrorCodeEnum;

/**
 * <p>Title: PebException</p>
 * <p>Description: Exception lançada por operações de API do serviço Peb.</p>
 * <p>Copyright: Copyright (c) 2002-2025</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: 26071 $
 */
public class PebException extends ZetraException {

    private PebErrorCodeEnum error;

    public PebException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public PebException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }

    public PebException(Throwable cause) {
        super(cause);
    }

    public PebErrorCodeEnum gePebError() {
        return error;
    }

    public void setPebError(PebErrorCodeEnum error) {
        this.error = error;
    }

}
