package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ViewHelperException</p>
 * <p>Description: Exceção criada nos ViewHelpers</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ViewHelperException extends ZetraException {

    final private boolean senhaExpirada;

    public ViewHelperException(Throwable ex) {
        super(ex);
        this.senhaExpirada = false;
    }

     public ViewHelperException(String messageKey, boolean senhaExpirada, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel,  messageArgs);
        this.senhaExpirada = senhaExpirada;
    }


    public ViewHelperException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
        this.senhaExpirada = false;
    }

    public ViewHelperException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
        this.senhaExpirada = false;
    }

    public boolean isSenhaExpirada(){
        return this.senhaExpirada;
    }
}
