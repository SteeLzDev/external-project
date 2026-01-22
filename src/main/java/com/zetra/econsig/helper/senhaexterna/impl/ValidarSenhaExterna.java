package com.zetra.econsig.helper.senhaexterna.impl;

import java.util.Properties;

import com.zetra.econsig.dto.CustomTransferObject;

/**
 * <p>Title: ValidarSenhaExterna</p>
 * <p>Description: Interface para classes específicas de sistema para validação da senha de servidor.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ValidarSenhaExterna {

    public CustomTransferObject validarSenha(String[] parametros, Properties messages);
}
