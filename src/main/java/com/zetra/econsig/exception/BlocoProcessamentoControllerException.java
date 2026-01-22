package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: BlocoProcessamentoControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer alguma modificação nas entidades de Bloco de processamento</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BlocoProcessamentoControllerException extends ZetraException {

    public BlocoProcessamentoControllerException(Throwable ex) {
        super(ex);
    }

    public BlocoProcessamentoControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public BlocoProcessamentoControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
