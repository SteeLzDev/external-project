package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: FaturamentoBeneficioException</p>
 * <p>Description: Exception para operações no faturamento de benefícios </p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author gustavo.novaes$
 * $Revision: 24216 $
 * $Date 2018-05-08 12:20:00 -0300 (ter, 08 mai 2018) $
 */

public class FaturamentoBeneficioControllerException extends ZetraException {

    public FaturamentoBeneficioControllerException(Throwable ex) {
        super(ex);
    }

    public FaturamentoBeneficioControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public FaturamentoBeneficioControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }

}
