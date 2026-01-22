package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ValidacaoMovimentoControllerException</p>
 * <p>Description: Exception gerada na Validação de Movimento.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ValidacaoMovimentoControllerException extends ZetraException {

    public ValidacaoMovimentoControllerException(Throwable ex) {
        super(ex);
    }

    public ValidacaoMovimentoControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public ValidacaoMovimentoControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
