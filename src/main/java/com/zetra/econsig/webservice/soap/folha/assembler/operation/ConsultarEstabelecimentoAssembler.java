package com.zetra.econsig.webservice.soap.folha.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarEstabelecimento;

/**
 * <p>Title: ConsultarEstabelecimentoAssembler</p>
 * <p>Description: Assembler para ConsultarEstabelecimento.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ConsultarEstabelecimentoAssembler extends BaseAssembler {

    private ConsultarEstabelecimentoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ConsultarEstabelecimento consultarEstabelecimento) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(EST_IDENTIFICADOR, getValue(consultarEstabelecimento.getCodigoEstabelecimento()));
        parametros.put(USUARIO, consultarEstabelecimento.getUsuario());
        parametros.put(SENHA, consultarEstabelecimento.getSenha());

        return parametros;
    }
}