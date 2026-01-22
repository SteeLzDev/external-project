package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: SolicitacaoSuporteAPIException</p>
 * <p>Description: Exception gerada na gerência de solicitações de suporte.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SolicitacaoSuporteAPIException extends ZetraException {

    public SolicitacaoSuporteAPIException(Throwable cause) {
        super(cause);
    }

    public SolicitacaoSuporteAPIException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public SolicitacaoSuporteAPIException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
