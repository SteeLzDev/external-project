package com.zetra.econsig.webservice.soap.folha.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarOrgao;

/**
 * <p>Title: ConsultarOrgaoAssembler</p>
 * <p>Description: Assembler para ConsultarOrgao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ConsultarOrgaoAssembler extends BaseAssembler {

    private ConsultarOrgaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ConsultarOrgao consultarOrgao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(ORG_IDENTIFICADOR, getValue(consultarOrgao.getCodigoOrgao()));
        parametros.put(EST_IDENTIFICADOR, getValue(consultarOrgao.getCodigoEstabelecimento()));
        parametros.put(USUARIO, consultarOrgao.getUsuario());
        parametros.put(SENHA, consultarOrgao.getSenha());

        return parametros;
    }
}