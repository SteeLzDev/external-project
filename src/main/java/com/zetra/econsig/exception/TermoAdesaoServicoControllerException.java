package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TermoAdesaoServicoControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer alguma modificação nas entidades de TermoAdesaoServico</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TermoAdesaoServicoControllerException extends ZetraException {

    public TermoAdesaoServicoControllerException(Throwable ex) {
        super(ex);
    }

    public TermoAdesaoServicoControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public TermoAdesaoServicoControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
