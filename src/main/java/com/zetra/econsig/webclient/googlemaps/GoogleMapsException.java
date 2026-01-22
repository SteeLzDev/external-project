package com.zetra.econsig.webclient.googlemaps;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: GoogleMapsException</p>
 * <p>Description: Exception lançada por operações de API do serviço geocodificação.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 */
public class GoogleMapsException extends ZetraException {
    public GoogleMapsException(Throwable ex) {
        super(ex);
    }

    public GoogleMapsException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public GoogleMapsException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
