package com.zetra.econsig.exception;

import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: UsuarioControllerException</p>
 * <p>Description: Exception gerada ao se tentar fazer alguma modificação nas entidades de Usuario</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class VisualizarDashboardCredenciamentoCsaWebControllerException extends ZetraException {

    public VisualizarDashboardCredenciamentoCsaWebControllerException(Throwable ex) {
        super(ex);
    }

    public VisualizarDashboardCredenciamentoCsaWebControllerException(String messageKey, AcessoSistema responsavel, String... messageArgs) {
        super(messageKey, responsavel, messageArgs);
    }

    public VisualizarDashboardCredenciamentoCsaWebControllerException(String messageKey, AcessoSistema responsavel, Throwable cause, String... messageArgs) {
        super(messageKey, responsavel, cause, messageArgs);
    }
}
