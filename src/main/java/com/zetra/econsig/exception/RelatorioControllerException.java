package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: RelatorioControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer algum relatório.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioControllerException extends ZetraException {

    public RelatorioControllerException(Throwable ex) {
        super(ex);
    }

    public RelatorioControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public RelatorioControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
