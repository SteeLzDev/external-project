package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TipoMotivoOperacaoControllerException</p>
 * <p>Description: Exceção de tipo de motivo da operacao</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoMotivoOperacaoControllerException extends ZetraException {

    public TipoMotivoOperacaoControllerException(Throwable ex) {
        super(ex);
    }

    public TipoMotivoOperacaoControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public TipoMotivoOperacaoControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
