package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

public class NotificacaoUsuarioControllerException extends ZetraException {

    private static final long serialVersionUID = -1620352132939584164L;

    public NotificacaoUsuarioControllerException(Throwable cause) {
        super(cause);
    }

    public NotificacaoUsuarioControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public NotificacaoUsuarioControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }

}
