package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: DefinicaoTaxaJurosControllerException</p>
 * <p>Description: Arquivo de exceção para o caso de uso definição de regra de taxa de juros</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author rodrigo.rosa$
 * $Revision: 24467 $
 * $Date 2019-04-10 12:20:00 -0300 (qua, 10 abr 2019) $
 */
public class DefinicaoTaxaJurosControllerException extends ZetraException {

    public DefinicaoTaxaJurosControllerException(Throwable ex) {
        super(ex);
    }

    public DefinicaoTaxaJurosControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public DefinicaoTaxaJurosControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
