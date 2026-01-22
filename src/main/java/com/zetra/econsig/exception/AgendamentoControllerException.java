package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: AgendamentoControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer alguma modificação na entidade Agendamento.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AgendamentoControllerException extends ZetraException {

    public AgendamentoControllerException(Throwable ex) {
        super(ex);
    }

    public AgendamentoControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public AgendamentoControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
