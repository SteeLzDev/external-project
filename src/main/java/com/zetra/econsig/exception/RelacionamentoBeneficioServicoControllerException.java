package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: RelacionamentoBeneficioServicoControllerException</p>
 * <p>Description: Classe Exception para o caso de uso de importação de historico beneficio</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelacionamentoBeneficioServicoControllerException extends ZetraException {

    public RelacionamentoBeneficioServicoControllerException(Throwable ex) {
        super(ex);
    }

    public RelacionamentoBeneficioServicoControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public RelacionamentoBeneficioServicoControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
