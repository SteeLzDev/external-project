package com.zetra.econsig.webclient.crm;

import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: CRMErrorCodeEnum</p>
 * <p>Description: Emumeration com códigos de erros retornados pelo serviço CRM.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: 26071 $
 */
public enum CRMErrorCodeEnum {
    GENERIC_ERROR("Generic error","999")
    ;

    private final String error;
    private final String errorCode;

    private CRMErrorCodeEnum(String error, String errorCode) {
        this.error = error;
        this.errorCode = errorCode;
    }

    public String getError() {
        return error;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public static CRMErrorCodeEnum getByErrorCode(String errorCode) {
        if (TextHelper.isNull(errorCode)) {
            return null;
        }

        if (errorCode.equals(GENERIC_ERROR.getErrorCode())) {
            return GENERIC_ERROR;
        }

        return null;
    }
}
