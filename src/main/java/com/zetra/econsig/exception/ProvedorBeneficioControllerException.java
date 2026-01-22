package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ProvedorBeneficioControllerException</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author $
 * $Revision$
 * $Date $
 */
public class ProvedorBeneficioControllerException extends ZetraException {

    public ProvedorBeneficioControllerException(Throwable ex) {
        super(ex);
    }

    public ProvedorBeneficioControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public ProvedorBeneficioControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
