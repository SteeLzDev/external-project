package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p> Title: ReportControllerException </p>
 * <p> Description: /p>
 * <p> Copyright: Copyright (c) 2002-2014</p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ReportControllerException extends ZetraException {

    public ReportControllerException(Throwable ex) {
        super(ex);
    }

    public ReportControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public ReportControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
