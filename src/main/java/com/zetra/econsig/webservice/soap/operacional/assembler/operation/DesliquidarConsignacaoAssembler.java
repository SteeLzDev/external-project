package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_OBS;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v7.DesliquidarConsignacao;

/**
 * <p>Title: DesliquidarConsignacaoAssembler</p>
 * <p>Description: Assembler para DesliquidarConsignacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class DesliquidarConsignacaoAssembler extends BaseAssembler {

    private DesliquidarConsignacaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(DesliquidarConsignacao desliquidarConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, desliquidarConsignacao.getUsuario());
        parametros.put(SENHA, desliquidarConsignacao.getSenha());
        parametros.put(ADE_IDENTIFICADOR, getValue(desliquidarConsignacao.getAdeIdentificador()));
        final Long adeNumero = getValue(desliquidarConsignacao.getAdeNumero());
        if ((adeNumero != null) && (adeNumero != Long.MAX_VALUE) && (adeNumero > 0)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(desliquidarConsignacao.getConvenio()));
        parametros.put(CLIENTE, getValue(desliquidarConsignacao.getCliente()));
        parametros.put(TMO_OBS, getValue(desliquidarConsignacao.getObsMotivoOperacao()));
        parametros.put(TMO_IDENTIFICADOR, getValue(desliquidarConsignacao.getCodigoMotivoOperacao()));

        return parametros;
    }
}