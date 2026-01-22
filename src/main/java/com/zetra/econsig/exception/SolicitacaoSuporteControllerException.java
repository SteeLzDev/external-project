package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: SolicitacaoSuporteControllerException</p>
 * <p>Description: Exceção criada nas solicitações de suporte</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SolicitacaoSuporteControllerException extends ZetraException {

    public SolicitacaoSuporteControllerException(Throwable ex) {
        super(ex);
    }

    public SolicitacaoSuporteControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public SolicitacaoSuporteControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
