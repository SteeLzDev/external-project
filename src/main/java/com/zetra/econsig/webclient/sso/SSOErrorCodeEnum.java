package com.zetra.econsig.webclient.sso;

import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: SSOErrorCodeEnum</p>
 * <p>Description: Emumeration com códigos de erros retornados no SSOToken do serviço SSO.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: 26071 $
 */
public enum SSOErrorCodeEnum {
    ACCOUNT_IS_LOCKED("User account is locked", "1"),
    ACCOUNT_IS_DISABLED("User is disabled", "2"),
    ACCOUNT_IS_EXPIRED("User account has expired", "3"),
    ACCOUNT_PASSWORD_IS_EXPIRED("User credentials have expired", "4"),
    GENERIC_ERROR("Generic error","999")
    ;

    private final String error;
    private final String errorCode;

    private SSOErrorCodeEnum(String error, String errorCode) {
        this.error = error;
        this.errorCode = errorCode;
    }

    public String getError() {
        return error;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public static SSOErrorCodeEnum getByErrorCode(String errorCode) {
        if (TextHelper.isNull(errorCode)) {
            return null;
        }

        if (errorCode.equals(ACCOUNT_IS_LOCKED.getErrorCode())) {
            return ACCOUNT_IS_LOCKED;
        } else if (errorCode.equals(ACCOUNT_IS_DISABLED.getErrorCode())) {
            return ACCOUNT_IS_DISABLED;
        } else if (errorCode.equals(ACCOUNT_IS_EXPIRED.getErrorCode())) {
            return ACCOUNT_IS_EXPIRED;
        } else if (errorCode.equals(ACCOUNT_PASSWORD_IS_EXPIRED.getErrorCode())) {
            return ACCOUNT_PASSWORD_IS_EXPIRED;
        } else if (errorCode.equals(GENERIC_ERROR.getErrorCode())) {
            return GENERIC_ERROR;
        }

        return null;
    }
}
