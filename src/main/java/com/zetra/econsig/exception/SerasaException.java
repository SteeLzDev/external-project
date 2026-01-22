package com.zetra.econsig.exception;


import com.zetra.econsig.helper.seguranca.AcessoSistema;

public class SerasaException extends ZetraException {
    public SerasaException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public SerasaException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }

    public SerasaException(Throwable cause) {
        super(cause);
    }

    
}
