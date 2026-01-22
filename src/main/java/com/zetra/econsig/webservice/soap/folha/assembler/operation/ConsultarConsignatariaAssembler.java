package com.zetra.econsig.webservice.soap.folha.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarConsignataria;

/**
 * <p>Title: ConsultarConsignatariaAssembler</p>
 * <p>Description: Assembler para ConsultarConsignataria.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ConsultarConsignatariaAssembler extends BaseAssembler {

    private ConsultarConsignatariaAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ConsultarConsignataria consultarConsignataria) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(CSA_IDENTIFICADOR, getValue(consultarConsignataria.getCodigo()));
        parametros.put(USUARIO, consultarConsignataria.getUsuario());
        parametros.put(SENHA, consultarConsignataria.getSenha());

        return parametros;
    }
}