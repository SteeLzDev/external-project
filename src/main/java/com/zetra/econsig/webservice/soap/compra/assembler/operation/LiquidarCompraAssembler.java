package com.zetra.econsig.webservice.soap.compra.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.OBS;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.compra.v1.LiquidarCompra;

/**
 * <p>Title: LiquidarCompraAssembler</p>
 * <p>Description: Assembler para LiquidarCompra.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class LiquidarCompraAssembler extends BaseAssembler {

    private LiquidarCompraAssembler() {
        //
    }

    public static Map<CamposAPI, Object> toMap(LiquidarCompra liquidarCompra) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(CLIENTE, getValue(liquidarCompra.getCliente()));
        parametros.put(CONVENIO, getValue(liquidarCompra.getConvenio()));
        parametros.put(USUARIO, liquidarCompra.getUsuario());
        parametros.put(SENHA, liquidarCompra.getSenha());
        final long adeNumero = liquidarCompra.getAdeNumero();
        if ((adeNumero == Long.MAX_VALUE) || (adeNumero <= 0)) {
            parametros.put(ADE_NUMERO, null);
        } else {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(OBS, getValue(liquidarCompra.getObsMotivoOperacao()));
        parametros.put(TMO_IDENTIFICADOR, getValue(liquidarCompra.getCodigoMotivoOperacao()));

        return parametros;
    }
}