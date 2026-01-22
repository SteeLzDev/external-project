package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: DespesaIndividualControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer alguma modificação nas entidades de Despesa Individual</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DespesaIndividualControllerException extends ZetraException {

    public DespesaIndividualControllerException(Throwable ex) {
        super(ex);
    }

    public DespesaIndividualControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public DespesaIndividualControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
