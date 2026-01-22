package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: EventosTotemControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer alguma pesquisa de eventos Totem </p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo.rosa $
 * $Revision: 16993 $
 * $Date: 2014-05-19 17:20:31 -0300 (seg, 19 mai 2014) $
 */
public class EventosTotemControllerException extends ZetraException {

    public EventosTotemControllerException(Throwable ex) {
        super(ex);
    }

    public EventosTotemControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public EventosTotemControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }

}
