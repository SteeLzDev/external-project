package com.zetra.econsig.webclient.creditotrabalhador;

import com.zetra.econsig.helper.texto.TextHelper;
public enum PebErrorCodeEnum {
    GENERIC_ERROR("Generic error", "999");

    private final String error;
    private final String errorCode;

    private PebErrorCodeEnum(String error, String errorCode) {
        this.error = error;
        this.errorCode = errorCode;
    }

    public String getError() { return error; }
    public String getErrorCode() { return errorCode; }

    public static PebErrorCodeEnum getByErrorCode(String errorCode) {
        if(TextHelper.isNull(errorCode)){
            return null;
        }

        if(errorCode.equals(GENERIC_ERROR.getErrorCode())){
            return GENERIC_ERROR;
        }

        return null;
    }

}
