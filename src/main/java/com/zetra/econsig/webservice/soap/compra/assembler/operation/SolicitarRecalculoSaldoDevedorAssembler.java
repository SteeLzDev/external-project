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
import com.zetra.econsig.webservice.soap.compra.v1.SolicitarRecalculoSaldoDevedor;

/**
 * <p>Title: SolicitarRecalculoSaldoDevedorAssembler</p>
 * <p>Description: Assembler para SolicitarRecalculoSaldoDevedor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class SolicitarRecalculoSaldoDevedorAssembler extends BaseAssembler {

    private SolicitarRecalculoSaldoDevedorAssembler() {
        //
    }

    public static Map<CamposAPI, Object> toMap(SolicitarRecalculoSaldoDevedor solicitarRecalculoSaldo) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(CLIENTE, getValue(solicitarRecalculoSaldo.getCliente()));
        parametros.put(CONVENIO, getValue(solicitarRecalculoSaldo.getConvenio()));
        parametros.put(USUARIO, solicitarRecalculoSaldo.getUsuario());
        parametros.put(SENHA, solicitarRecalculoSaldo.getSenha());
        final long adeNumero = solicitarRecalculoSaldo.getAdeNumero();
        if ((adeNumero == Long.MAX_VALUE) || (adeNumero <= 0)) {
            parametros.put(ADE_NUMERO, null);
        } else {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(OBS, getValue(solicitarRecalculoSaldo.getObservacao()));

        return parametros;
    }
}