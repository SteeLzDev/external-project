package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: RegraLimiteOperacaoController</p>
 * <p>Description: Exceção lançada sobre operações em regras de limite de operação</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 */
public class RegraLimiteOperacaoControllerException extends ZetraException {

    public RegraLimiteOperacaoControllerException(Throwable ex) {
        super(ex);
    }

    public RegraLimiteOperacaoControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public RegraLimiteOperacaoControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }

    @Override
    public void setMessage(String message) {
        super.setMessage(message);
    }
}
