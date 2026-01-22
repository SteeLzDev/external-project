package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: BeneficioException</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author gustavo.novaes$
 * $Revision$
 * $Date 2018-05-08 12:20:00 -0300 (ter, 08 mai 2018) $
 */
public class BeneficioControllerException extends ZetraException {

    public BeneficioControllerException(Throwable ex) {
        super(ex);
    }

    public BeneficioControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public BeneficioControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
