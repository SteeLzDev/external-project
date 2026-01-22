package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.webclient.cert.CERTErrorCodeEnum;

/**
 * <p>Title: CERTException</p>
 * <p>Description: Exception lançada por operações de API do serviço CERT.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: 26071 $
 */
public class CERTException extends ZetraException {
    
    private CERTErrorCodeEnum error;

    public CERTException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public CERTException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }

    public CERTException(Throwable cause) {
        super(cause);
    }

    public CERTErrorCodeEnum getCertError() {
        return error;
    }

    public void setCertError(CERTErrorCodeEnum error) {
        this.error = error;
    }

}
