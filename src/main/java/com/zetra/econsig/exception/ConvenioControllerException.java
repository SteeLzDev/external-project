package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ConvenioControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer alguma modificação nas entidades de Convenio
 * (Convenio, Serviço).</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConvenioControllerException extends ZetraException {

    public ConvenioControllerException(Throwable ex) {
        super(ex);
    }

    public ConvenioControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public ConvenioControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
