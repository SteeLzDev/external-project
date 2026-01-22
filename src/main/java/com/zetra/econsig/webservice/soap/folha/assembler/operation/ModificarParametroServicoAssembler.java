package com.zetra.econsig.webservice.soap.folha.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.PSE_VLR;
import static com.zetra.econsig.webservice.CamposAPI.PSE_VLR_REF;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TPS_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.ModificarParametroServico;
import com.zetra.econsig.webservice.soap.folha.v1.ParametroServico;

/**
 * <p>Title: ModificarParametroServicoAssembler</p>
 * <p>Description: Assembler para ModificarParametroServico.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ModificarParametroServicoAssembler extends BaseAssembler {

    private ModificarParametroServicoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ModificarParametroServico modificarParametroServico) {
        final ParametroServico parametroServico = modificarParametroServico.getParametroServico();

        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);
        parametros.put(USUARIO, modificarParametroServico.getUsuario());
        parametros.put(SENHA, modificarParametroServico.getSenha());
        parametros.put(SVC_IDENTIFICADOR, parametroServico.getServicoCodigo());
        parametros.put(TPS_CODIGO, parametroServico.getCodigo());
        parametros.put(PSE_VLR, getValue(parametroServico.getValor()));
        parametros.put(PSE_VLR_REF, getValue(parametroServico.getValorRef()));

        return parametros;
    }
}