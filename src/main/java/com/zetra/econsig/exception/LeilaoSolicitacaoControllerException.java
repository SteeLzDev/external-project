package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: LeilaoSolicitacaoControllerException</p>
 * <p>Description: Exceção gerada em caso de erro no módulo
 * de leilão de solicitação via simulação pelo servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LeilaoSolicitacaoControllerException extends ZetraException {

    public LeilaoSolicitacaoControllerException(Throwable ex) {
        super(ex);
    }

    public LeilaoSolicitacaoControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public LeilaoSolicitacaoControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
