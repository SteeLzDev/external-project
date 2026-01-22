package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: SaldoDevedorControllerException</p>
 * <p>Description: Exception gerada na ocorrencia de algum erro no controle de saldo devedor.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SaldoDevedorControllerException extends ZetraException {

    public SaldoDevedorControllerException(Throwable ex) {
        super(ex);
    }

    public SaldoDevedorControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public SaldoDevedorControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
