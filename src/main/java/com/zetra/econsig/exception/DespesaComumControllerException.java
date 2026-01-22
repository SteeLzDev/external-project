package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: DespesaComumControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer alguma modificação nas entidades de Despesa Comum</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DespesaComumControllerException extends ZetraException {

    public DespesaComumControllerException(Throwable ex) {
        super(ex);
    }

    public DespesaComumControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public DespesaComumControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
