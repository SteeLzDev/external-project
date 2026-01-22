package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

public class NotificacaoDispositivoControllerException extends ZetraException {

    public NotificacaoDispositivoControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public NotificacaoDispositivoControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }

}
