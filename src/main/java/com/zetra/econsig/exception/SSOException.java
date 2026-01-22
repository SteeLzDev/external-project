package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.webclient.sso.SSOErrorCodeEnum;

/**
 * <p>Title: SSOException</p>
 * <p>Description: Exception lançada por operações de API do serviço SSO.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: 26071 $
 */
public class SSOException extends ZetraException {
    private SSOErrorCodeEnum ssoError;

    public SSOException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public SSOException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }

    public SSOException(Throwable cause) {
        super(cause);
    }

    public SSOErrorCodeEnum getSsoError() {
        return ssoError;
    }

    public void setSsoError(SSOErrorCodeEnum ssoError) {
        this.ssoError = ssoError;
    }


}
