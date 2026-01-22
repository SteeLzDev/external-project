package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: CoeficienteControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer alguma modificação nas entidades de Coeficiente</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CoeficienteControllerException extends ZetraException {

    public CoeficienteControllerException(Throwable ex) {
        super(ex);
    }

    public CoeficienteControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public CoeficienteControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
