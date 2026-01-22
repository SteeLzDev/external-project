package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: CoeficienteCorrecaoControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer alguma modificação nas entidades de Coeficiente de Correcao</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CoeficienteCorrecaoControllerException extends ZetraException {

    public CoeficienteCorrecaoControllerException(Throwable ex) {
        super(ex);
    }

    public CoeficienteCorrecaoControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public CoeficienteCorrecaoControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
