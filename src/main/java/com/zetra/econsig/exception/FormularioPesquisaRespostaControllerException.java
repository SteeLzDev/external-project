package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: FormularioPesquisaRespostaControllerException</p>
 * <p>Description: Exceção de formulário de pesquisa resposta</p>
 * <p>Copyright: Copyright (c) 2002-2025</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FormularioPesquisaRespostaControllerException extends ZetraException {

    public FormularioPesquisaRespostaControllerException(Throwable ex) {
        super(ex);
    }

    public FormularioPesquisaRespostaControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public FormularioPesquisaRespostaControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
