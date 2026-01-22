package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ExportaArquivosBeneficioControllerException</p>
 * <p>Description: Exception gerada no fluxo de exportação de arquivo de beneficio.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ExportaArquivosBeneficioControllerException extends ZetraException {

    public ExportaArquivosBeneficioControllerException(Throwable ex) {
        super(ex);
    }

    public ExportaArquivosBeneficioControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public ExportaArquivosBeneficioControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }

}
