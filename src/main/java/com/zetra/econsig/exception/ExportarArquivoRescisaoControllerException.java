package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ExportarArquivoRescisaoControllerException</p>
 * <p>Description: Exception gerada para erros na exportação de arquivos do módulo de rescisão</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ExportarArquivoRescisaoControllerException extends ZetraException {

    public ExportarArquivoRescisaoControllerException(Throwable ex) {
        super(ex);
    }

    public ExportarArquivoRescisaoControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public ExportarArquivoRescisaoControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
