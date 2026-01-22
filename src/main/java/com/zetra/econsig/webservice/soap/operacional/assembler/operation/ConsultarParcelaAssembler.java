package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_DESCONTO;
import static com.zetra.econsig.webservice.CamposAPI.PRD_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;

/**
 * <p>Title: ConsultarParcelaAssembler</p>
 * <p>Description: Assembler para ConsultarParcela.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ConsultarParcelaAssembler extends BaseAssembler {

    private ConsultarParcelaAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v3.ConsultarParcela consultarParcela) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(ADE_IDENTIFICADOR, getValue(consultarParcela.getAdeIdentificador()));
        final Long adeNumero = getValue(consultarParcela.getAdeNumero());
        if ((adeNumero != null) && (adeNumero != Long.MAX_VALUE) && (adeNumero > 0)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(DATA_DESCONTO, getValue(consultarParcela.getDataDesconto()));
        final Short prdNumero = getValue(consultarParcela.getPrdNumero());
        if ((prdNumero != null) && (prdNumero != Short.MAX_VALUE) && (prdNumero > 0)) {
            parametros.put(PRD_NUMERO, prdNumero);
        }
        parametros.put(SENHA, consultarParcela.getSenha());
        parametros.put(CONVENIO, getValue(consultarParcela.getConvenio()));
        parametros.put(CLIENTE, getValue(consultarParcela.getCliente()));
        parametros.put(USUARIO, consultarParcela.getUsuario());

        return parametros;
    }
}