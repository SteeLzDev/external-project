package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ConsignanteControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer alguma modificação nas entidades da Consignante
 * (Consignante, Órgão, Estabelecimento).</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsignanteControllerException extends ZetraException {

    public ConsignanteControllerException(Throwable ex) {
        super(ex);
    }

    public ConsignanteControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public ConsignanteControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
