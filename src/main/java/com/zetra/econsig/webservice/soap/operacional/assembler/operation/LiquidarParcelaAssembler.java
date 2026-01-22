package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_DESCONTO;
import static com.zetra.econsig.webservice.CamposAPI.OBSERVACAO;
import static com.zetra.econsig.webservice.CamposAPI.PRD_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_REALIZADO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v3.LiquidarParcela;

/**
 * <p>Title: LiquidarParcelaAssembler</p>
 * <p>Description: Assembler para LiquidarParcela.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class LiquidarParcelaAssembler extends BaseAssembler {

    private LiquidarParcelaAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(LiquidarParcela liquidarParcela) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, liquidarParcela.getUsuario());
        parametros.put(SENHA, liquidarParcela.getSenha());
        parametros.put(ADE_IDENTIFICADOR, getValue(liquidarParcela.getAdeIdentificador()));

        final Long adeNumero = getValue(liquidarParcela.getAdeNumero());
        if ((adeNumero != null) && (adeNumero != Long.MAX_VALUE) && (adeNumero > 0)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONVENIO, getValue(liquidarParcela.getConvenio()));
        parametros.put(CLIENTE, getValue(liquidarParcela.getCliente()));

        parametros.put(DATA_DESCONTO, getValue(liquidarParcela.getDataDesconto()));
        final Short prdNumero = getValue(liquidarParcela.getPrdNumero());
        if ((prdNumero != null) && (prdNumero != Short.MAX_VALUE) && (prdNumero > 0)) {
            parametros.put(PRD_NUMERO, prdNumero);
        }

        final Double vlrRealizado = liquidarParcela.getValorRealizado();
        if ((vlrRealizado == null) || vlrRealizado.equals(Double.NaN)) {
            parametros.put(VALOR_REALIZADO, null);
        } else {
            parametros.put(VALOR_REALIZADO, vlrRealizado);
        }

        final String observacao = liquidarParcela.getObservacao();
        if (!TextHelper.isNull(observacao)) {
            parametros.put(OBSERVACAO, observacao);
        }

        return parametros;
    }
}