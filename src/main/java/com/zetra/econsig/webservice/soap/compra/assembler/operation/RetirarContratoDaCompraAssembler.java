package com.zetra.econsig.webservice.soap.compra.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.OBS;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TMO_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.TMO_OBS;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.compra.v1.RetirarContratoDaCompra;

/**
 * <p>Title: RetirarContratoDaCompraAssembler</p>
 * <p>Description: Assembler para RetirarContratoDaCompra.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class RetirarContratoDaCompraAssembler extends BaseAssembler {

    private RetirarContratoDaCompraAssembler() {
        //
    }

    public static Map<CamposAPI, Object> toMap(RetirarContratoDaCompra retirarContratoDaCompra) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(CLIENTE, getValue(retirarContratoDaCompra.getCliente()));
        parametros.put(CONVENIO, getValue(retirarContratoDaCompra.getConvenio()));
        parametros.put(USUARIO, retirarContratoDaCompra.getUsuario());
        parametros.put(SENHA, retirarContratoDaCompra.getSenha());
        final long adeNumero = retirarContratoDaCompra.getAdeNumero();
        if ((adeNumero == Long.MAX_VALUE) || (adeNumero <= 0)) {
            parametros.put(ADE_NUMERO, null);
        } else {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(OBS, getValue(retirarContratoDaCompra.getObservacao()));
        parametros.put(TMO_OBS, getValue(retirarContratoDaCompra.getObsMotivoOperacao()));
        parametros.put(TMO_IDENTIFICADOR, getValue(retirarContratoDaCompra.getCodigoMotivoOperacao()));

        return parametros;
    }
}