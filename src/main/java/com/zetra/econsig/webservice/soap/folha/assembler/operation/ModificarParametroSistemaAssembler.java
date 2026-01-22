package com.zetra.econsig.webservice.soap.folha.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.CSE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PSI_VLR;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TPC_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.ModificarParametroSistema;
import com.zetra.econsig.webservice.soap.folha.v1.ParametroSistema;

/**
 * <p>Title: ModificarParametroSistemaAssembler</p>
 * <p>Description: Assembler para ModificarParametroSistema.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ModificarParametroSistemaAssembler extends BaseAssembler {

    private ModificarParametroSistemaAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ModificarParametroSistema modificarParametroSistema) {
        final ParametroSistema parametroSistema = modificarParametroSistema.getParametroSistema();

        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);
        parametros.put(USUARIO, modificarParametroSistema.getUsuario());
        parametros.put(SENHA, modificarParametroSistema.getSenha());
        parametros.put(CSE_IDENTIFICADOR, parametroSistema.getConsignanteCodigo());
        parametros.put(TPC_CODIGO, parametroSistema.getCodigo());
        parametros.put(PSI_VLR, getValue(parametroSistema.getValor()));

        return parametros;
    }
}