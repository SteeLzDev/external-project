package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ReclamacaoRegistroServidorControllerException</p>
 * <p>Description: Exception gerada na manipulação de reclamação de servidor</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ReclamacaoRegistroServidorControllerException extends ZetraException{

    public ReclamacaoRegistroServidorControllerException(Throwable ex) {
        super(ex);
    }

    public ReclamacaoRegistroServidorControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public ReclamacaoRegistroServidorControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
