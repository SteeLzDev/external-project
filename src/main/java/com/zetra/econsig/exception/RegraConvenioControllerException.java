package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ConsignatariaControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer alguma modificação nas entidades da RegraConvenio</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraConvenioControllerException extends ZetraException {

    public RegraConvenioControllerException(Throwable ex) {
        super(ex);
    }

    public RegraConvenioControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public RegraConvenioControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
