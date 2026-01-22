package com.zetra.econsig.webservice.soap.compra.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.ANEXO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.OBS;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.compra.v1.InformarPagamentoSaldoDevedor;

/**
 * <p>Title: InformarPagamentoSaldoDevedorAssembler</p>
 * <p>Description: Assembler para InformarPagamentoSaldoDevedor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class InformarPagamentoSaldoDevedorAssembler extends BaseAssembler {

    private InformarPagamentoSaldoDevedorAssembler() {
        //
    }

    public static Map<CamposAPI, Object> toMap(InformarPagamentoSaldoDevedor informarPagamentoSaldoDevedor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(CLIENTE, getValue(informarPagamentoSaldoDevedor.getCliente()));
        parametros.put(CONVENIO, getValue(informarPagamentoSaldoDevedor.getConvenio()));
        parametros.put(USUARIO, informarPagamentoSaldoDevedor.getUsuario());
        parametros.put(SENHA, informarPagamentoSaldoDevedor.getSenha());
        final long adeNumero = informarPagamentoSaldoDevedor.getAdeNumero();
        if ((adeNumero == Long.MAX_VALUE) || (adeNumero <= 0)) {
            parametros.put(ADE_NUMERO, null);
        } else {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(OBS, getValue(informarPagamentoSaldoDevedor.getObservacao()));
        parametros.put(ANEXO, getValue(informarPagamentoSaldoDevedor.getAnexo()));

        return parametros;
    }
}