package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: FormularioPesquisaControllerException</p>
 * <p>Description: Exceção de formulário de pesquisa</p>
 * <p>Copyright: Copyright (c) 2002-2025</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FormularioPesquisaControllerException extends ZetraException {

    public FormularioPesquisaControllerException(Throwable ex) {
        super(ex);
    }

    public FormularioPesquisaControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public FormularioPesquisaControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
