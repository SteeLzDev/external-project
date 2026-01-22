package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: AuditoriaControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer alguma modificação nas entidades de Auditoria.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AuditoriaControllerException extends ZetraException {

    public AuditoriaControllerException(Throwable ex) {
        super(ex);
    }

    public AuditoriaControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public AuditoriaControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
