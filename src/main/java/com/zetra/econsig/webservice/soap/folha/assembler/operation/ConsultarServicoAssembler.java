package com.zetra.econsig.webservice.soap.folha.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.NSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarServico;

/**
 * <p>Title: ConsultarServicoAssembler</p>
 * <p>Description: Assembler para ConsultarServico.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ConsultarServicoAssembler extends BaseAssembler {

    private ConsultarServicoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ConsultarServico consultarServico) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, consultarServico.getUsuario());
        parametros.put(SENHA, consultarServico.getSenha());

        parametros.put(SVC_IDENTIFICADOR, getValue(consultarServico.getCodigoServico()));
        parametros.put(NSE_CODIGO, getValue(consultarServico.getCodigoNaturezaServico()));

        return parametros;
    }
}
