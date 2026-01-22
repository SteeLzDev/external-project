package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ImportaContrachequesException</p>
 * <p>Description: Classe para tratamento de exceções geradas pelos processos de importação
 * de arquivos de contracheque.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportaContrachequesException extends ZetraException {

    public ImportaContrachequesException(Throwable cause) {
        super(cause);
    }

    public ImportaContrachequesException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public ImportaContrachequesException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}