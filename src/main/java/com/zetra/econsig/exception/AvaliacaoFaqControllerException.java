package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: FaqControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer alguma modificação nas entidades de AvaliacaoFaq</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: $
 * $Date: $
 */
public class AvaliacaoFaqControllerException extends ZetraException {

    public AvaliacaoFaqControllerException(Throwable ex) {
        super(ex);
    }

    public AvaliacaoFaqControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public AvaliacaoFaqControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
