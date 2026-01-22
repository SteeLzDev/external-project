package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: PlanoDescontoControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer alguma modificação nas entidades da Plano de desconto
 * (Consignataria, Correspondente).</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PlanoDescontoControllerException extends ZetraException {

    public PlanoDescontoControllerException(Throwable cause) {
        super(cause);
    }

    public PlanoDescontoControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public PlanoDescontoControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
