package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: CorrespondenteControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer alguma modificação nas entidades da Correspondente
 * (Consignataria, Correspondente).</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CorrespondenteControllerException extends ZetraException {

    public CorrespondenteControllerException(Throwable ex) {
        super(ex);
    }

    public CorrespondenteControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public CorrespondenteControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
