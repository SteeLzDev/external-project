package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: UsuarioControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer alguma modificação nas entidades de Usuario</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioControllerException extends ZetraException {

    private UsuarioControllerException(String message) {
        super(message);
    }

    public UsuarioControllerException(Throwable ex) {
        super(ex);
    }

    @Deprecated
    public UsuarioControllerException(String message, String key) {
        super(message);
        setMessageKey(key);
    }

    public UsuarioControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public UsuarioControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }

    public static UsuarioControllerException byMessage(String message) {
        return new UsuarioControllerException(message);
    }
}
