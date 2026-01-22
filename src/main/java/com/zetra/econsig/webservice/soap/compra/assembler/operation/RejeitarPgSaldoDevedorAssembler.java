package com.zetra.econsig.webservice.soap.compra.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.OBS;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.compra.v1.RejeitarPgSaldoDevedor;

/**
 * <p>Title: RejeitarPgSaldoDevedorAssembler</p>
 * <p>Description: Assembler para RejeitarPgSaldoDevedor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class RejeitarPgSaldoDevedorAssembler extends BaseAssembler {

    private RejeitarPgSaldoDevedorAssembler() {
        //
    }

    public static Map<CamposAPI, Object> toMap(RejeitarPgSaldoDevedor rejeitarPgSaldoDevedor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(CLIENTE, getValue(rejeitarPgSaldoDevedor.getCliente()));
        parametros.put(CONVENIO, getValue(rejeitarPgSaldoDevedor.getConvenio()));
        parametros.put(USUARIO, rejeitarPgSaldoDevedor.getUsuario());
        parametros.put(SENHA, rejeitarPgSaldoDevedor.getSenha());
        final long adeNumero = rejeitarPgSaldoDevedor.getAdeNumero();
        if ((adeNumero == Long.MAX_VALUE) || (adeNumero <= 0)) {
            parametros.put(ADE_NUMERO, null);
        } else {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(OBS, getValue(rejeitarPgSaldoDevedor.getObservacao()));

        return parametros;
    }
}