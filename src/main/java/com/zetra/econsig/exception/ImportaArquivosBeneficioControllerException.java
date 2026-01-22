package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ImportaArquivosBeneficioControllerException</p>
 * <p>Description: Exception gerada no fluxo de importação de arquivo de beneficio.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportaArquivosBeneficioControllerException extends ZetraException {

    public ImportaArquivosBeneficioControllerException(Throwable ex) {
        super(ex);
    }

    public ImportaArquivosBeneficioControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public ImportaArquivosBeneficioControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }

}
