package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.webclient.crm.CRMErrorCodeEnum;

/**
 * <p>Title: SSOException</p>
 * <p>Description: Exception lançada por operações de API do serviço CRM.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: 26071 $
 */
public class CRMException extends ZetraException {
    
    private CRMErrorCodeEnum error;

    public CRMException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public CRMException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }

    public CRMException(Throwable cause) {
        super(cause);
    }

    public CRMErrorCodeEnum getCrmError() {
        return error;
    }

    public void setCrmError(CRMErrorCodeEnum error) {
        this.error = error;
    }

}
