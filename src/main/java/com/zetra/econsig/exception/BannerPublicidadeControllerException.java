package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

public class BannerPublicidadeControllerException extends ZetraException {

    public BannerPublicidadeControllerException(Throwable ex) {
        super(ex);
    }

    public BannerPublicidadeControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public BannerPublicidadeControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
