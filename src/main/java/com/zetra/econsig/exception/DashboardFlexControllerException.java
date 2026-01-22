package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

public class DashboardFlexControllerException extends ZetraException {

    public DashboardFlexControllerException(Throwable ex) {
        super(ex);
    }

    public DashboardFlexControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public DashboardFlexControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
