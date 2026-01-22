package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: AutorizacaoControllerException</p>
 * <p>Description: Exception gerada na ocorrencia de algum erro no controle de autorizações.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AutorizacaoControllerException extends ZetraException {

    protected AutorizacaoControllerException(String message) {
        super(message);
    }

    public AutorizacaoControllerException(Throwable ex) {
        super(ex);
    }

    public AutorizacaoControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public AutorizacaoControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }

    public static AutorizacaoControllerException byMessage(String message) {
        return new AutorizacaoControllerException(message);
    }
}
