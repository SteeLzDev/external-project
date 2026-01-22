package com.zetra.econsig.webservice.soap.folha.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarPerfilUsuario;

/**
 * <p>Title: ConsultarPerfilUsuarioAssembler</p>
 * <p>Description: Assembler para ConsultarPerfilUsuario.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ConsultarPerfilUsuarioAssembler extends BaseAssembler {

    private ConsultarPerfilUsuarioAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ConsultarPerfilUsuario consultarPerfilUsuario) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);
        parametros.put(USUARIO, consultarPerfilUsuario.getUsuario());
        parametros.put(SENHA, consultarPerfilUsuario.getSenha());

        return parametros;
    }
}