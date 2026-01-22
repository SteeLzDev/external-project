package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ExportaMovimentoException</p>
 * <p>Description: Exception gerada nas classes específicas de movimento financeiro.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TermoAdesaoAcaoException extends ZetraException {

    public TermoAdesaoAcaoException(Throwable cause) {
        super(cause);
    }

    public TermoAdesaoAcaoException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public TermoAdesaoAcaoException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
