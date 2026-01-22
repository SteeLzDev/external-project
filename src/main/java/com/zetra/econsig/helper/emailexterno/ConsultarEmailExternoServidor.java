package com.zetra.econsig.helper.emailexterno;

import com.zetra.econsig.dto.CustomTransferObject;

/**
 * <p>Title: ConsultarEmailExternoServidor</p>
 * <p>Description: Interface de definição dos métodos para consulta de e-mail de servidores em API externa
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */
public interface ConsultarEmailExternoServidor {

    public CustomTransferObject consultarEmailExternoServidor(String parametro);

}
