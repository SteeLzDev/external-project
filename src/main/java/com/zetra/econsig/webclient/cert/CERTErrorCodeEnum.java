package com.zetra.econsig.webclient.cert;

import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: CERTErrorCodeEnum</p>
 * <p>Description: Emumeration com códigos de erros retornados pelo serviço CERT.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: 26071 $
 */
public enum CERTErrorCodeEnum {
    GENERIC_ERROR("Generic error","999")
    ;

    private final String error;
    private final String errorCode;

    private CERTErrorCodeEnum(String error, String errorCode) {
        this.error = error;
        this.errorCode = errorCode;
    }

    public String getError() {
        return error;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public static CERTErrorCodeEnum getByErrorCode(String errorCode) {
        if (TextHelper.isNull(errorCode)) {
            return null;
        }

        if (errorCode.equals(GENERIC_ERROR.getErrorCode())) {
            return GENERIC_ERROR;
        }

        return null;
    }
}
