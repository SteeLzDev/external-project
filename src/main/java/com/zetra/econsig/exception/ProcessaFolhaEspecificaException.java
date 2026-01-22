package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ProcessarFolhaControllerBean</p>
 * <p>Description: Controlador para exception classe especifica de processamento sem bloqueio</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class ProcessaFolhaEspecificaException extends ZetraException {

    public ProcessaFolhaEspecificaException(Throwable cause) {
        super(cause);
    }

    public ProcessaFolhaEspecificaException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public ProcessaFolhaEspecificaException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
