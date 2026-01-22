package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ValidacaoAmbienteControllerException</p>
 * <p>Description: Exceção gerada na Validação do Ambiente.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ValidacaoAmbienteControllerException extends ZetraException {

    public ValidacaoAmbienteControllerException(Throwable ex) {
        super(ex);
    }

    public ValidacaoAmbienteControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public ValidacaoAmbienteControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
