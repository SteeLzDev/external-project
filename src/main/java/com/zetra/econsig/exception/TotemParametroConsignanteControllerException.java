package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TotemParametroConsignanteControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer manutenção nos parâmetros do Totem.</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TotemParametroConsignanteControllerException extends TotemControllerException {
    private static final long serialVersionUID = 1L;

    public TotemParametroConsignanteControllerException(Throwable ex) {
        super(ex);
    }

    public TotemParametroConsignanteControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public TotemParametroConsignanteControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
