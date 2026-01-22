package com.zetra.econsig.webservice.soap.compra.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_INDICE;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_IOF;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_MENS_VINC;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_TAC;
import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.ANEXO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_CARENCIA;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.COR_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.DATA_NASC;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.NOVO_ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.NSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.SER_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TOKEN;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_LIBERADO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_PARCELA;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.compra.v1.ComprarContrato;

/**
 * <p>Title: ComprarContratoAssembler</p>
 * <p>Description: Assembler para ComprarContrato.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ComprarContratoAssembler extends BaseAssembler {

    private ComprarContratoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ComprarContrato comprarContrato) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, comprarContrato.getUsuario());
        parametros.put(SENHA, comprarContrato.getSenha());
        parametros.put(SER_SENHA, comprarContrato.getSenhaServidor());
        parametros.put(TOKEN, comprarContrato.getTokenAutServidor());
        parametros.put(SERVICO_CODIGO, getValue(comprarContrato.getServicoCodigo()));
        parametros.put(DATA_NASC, getValue(comprarContrato.getDataNascimento()));
        parametros.put(VALOR_PARCELA, comprarContrato.getValorParcela());

        final Double adeVlr = comprarContrato.getValorParcela();
        if ((adeVlr == null) || adeVlr.equals(Double.NaN)) {
            parametros.put(VALOR_PARCELA, null);
        } else {
            parametros.put(VALOR_PARCELA, adeVlr);
        }
        if ((comprarContrato.getPrazo() == Integer.MIN_VALUE) || (comprarContrato.getPrazo() <= 0)) {
            parametros.put(PRAZO, null);
        } else {
            parametros.put(PRAZO, comprarContrato.getPrazo());
        }

        final Double vlrLib = getValue(comprarContrato.getValorLiberado());
        if ((vlrLib == null) || vlrLib.equals(Double.NaN)) {
            parametros.put(VALOR_LIBERADO, null);
        } else {
            parametros.put(VALOR_LIBERADO, vlrLib);
        }
        parametros.put(CNV_COD_VERBA, getValue(comprarContrato.getCodVerba()));
        parametros.put(COR_IDENTIFICADOR, getValue(comprarContrato.getCorrespondenteCodigo()));
        final Integer carencia = getValue(comprarContrato.getCarencia());
        if ((carencia == null) || (carencia == Integer.MAX_VALUE) || (carencia <= 0)) {
            parametros.put(ADE_CARENCIA, null);
        } else {
            parametros.put(ADE_CARENCIA, carencia);
        }

        final Double vlrTac = getValue(comprarContrato.getValorTac());
        if ((vlrTac == null) || vlrTac.equals(Double.NaN)) {
            parametros.put(ADE_VLR_TAC, null);
        } else {
            parametros.put(ADE_VLR_TAC, vlrTac);
        }
        parametros.put(ADE_INDICE, getValue(comprarContrato.getIndice()));

        final Double vlrIof = getValue(comprarContrato.getValorIof());
        if ((vlrIof == null) || vlrIof.equals(Double.NaN)) {
            parametros.put(ADE_VLR_IOF, null);
        } else {
            parametros.put(ADE_VLR_IOF, vlrIof);
        }

        final Double vlrMenVinc = getValue(comprarContrato.getValorMensVin());
        if ((vlrMenVinc == null) || vlrMenVinc.equals(Double.NaN)) {
            parametros.put(ADE_VLR_MENS_VINC, null);
        } else {
            parametros.put(ADE_VLR_MENS_VINC, vlrMenVinc);
        }

        final List<String> adeIdentificadores = comprarContrato.getAdeIdentificador();
        if ((adeIdentificadores != null) && !adeIdentificadores.isEmpty() && !TextHelper.isNull(adeIdentificadores.get(0))) {
            parametros.put(ADE_IDENTIFICADOR, comprarContrato.getAdeIdentificador());
        } else {
            parametros.put(ADE_IDENTIFICADOR, null);
        }

        final List<Long> adeNumeros = comprarContrato.getAdeNumeros();
        if (adeNumeros != null) {
            final List<Long> adeNumeroArray = new ArrayList<>();
            for (final Long adeNumero : adeNumeros) {
                if ((adeNumero != null) && (adeNumero != Long.MAX_VALUE) && (adeNumero > 0)) {
                    adeNumeroArray.add(adeNumero);
                }
            }
            parametros.put(ADE_NUMERO, adeNumeroArray);
        }

        parametros.put(RSE_MATRICULA, comprarContrato.getMatricula());
        parametros.put(SER_CPF, getValue(comprarContrato.getCpf()));
        parametros.put(ORG_IDENTIFICADOR, getValue(comprarContrato.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(comprarContrato.getEstabelecimentoCodigo()));
        parametros.put(RSE_BANCO, getValue(comprarContrato.getBanco()));
        parametros.put(RSE_AGENCIA, getValue(comprarContrato.getAgencia()));
        parametros.put(RSE_CONTA, getValue(comprarContrato.getConta()));
        parametros.put(CONVENIO, getValue(comprarContrato.getConvenio()));
        parametros.put(CLIENTE, getValue(comprarContrato.getCliente()));
        parametros.put(SER_LOGIN, comprarContrato.getLoginServidor());
        parametros.put(NOVO_ADE_IDENTIFICADOR, getValue(comprarContrato.getNovoAdeIdentificador()));
        parametros.put(NSE_CODIGO, getValue(comprarContrato.getNaturezaServicoCodigo()));
        parametros.put(ANEXO, getValue(comprarContrato.getAnexo()));

        return parametros;
    }
}