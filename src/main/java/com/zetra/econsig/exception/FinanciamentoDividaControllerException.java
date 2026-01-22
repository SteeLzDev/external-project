package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: FinanciamentoDividaControllerException</p>
 * <p>Description: Exceção gerada em caso de erro no módulo
 * financiamento de dívida de cartão.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FinanciamentoDividaControllerException extends ZetraException {

    public FinanciamentoDividaControllerException(Throwable ex) {
        super(ex);
    }

    public FinanciamentoDividaControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public FinanciamentoDividaControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
