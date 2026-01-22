package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ConvenioControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer alguma modificação nas entidades de Posto
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PostoRegistroServidorControllerException extends ZetraException {

    public PostoRegistroServidorControllerException(Throwable ex) {
        super(ex);
    }

    public PostoRegistroServidorControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public PostoRegistroServidorControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
