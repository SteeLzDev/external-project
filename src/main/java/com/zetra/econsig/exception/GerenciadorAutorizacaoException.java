package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: GerenciadorAutorizacaoException</p>
 * <p>Description: Classe para tratamento de exceções geradas pelos gerenciadores de autorização.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GerenciadorAutorizacaoException extends ZetraException {

    public GerenciadorAutorizacaoException(Throwable cause) {
        super(cause);
    }

    public GerenciadorAutorizacaoException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public GerenciadorAutorizacaoException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
