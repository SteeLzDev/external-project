package com.zetra.econsig.webservice.soap.compra.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.COMPRADO_PELA_ENTIDADE;
import static com.zetra.econsig.webservice.CamposAPI.CONTRATOS_BLOQ_A_BLOQUEAR;
import static com.zetra.econsig.webservice.CamposAPI.CONTRATO_LIQUIDADO;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_FIM_COMPRA;
import static com.zetra.econsig.webservice.CamposAPI.DATA_INICIO_COMPRA;
import static com.zetra.econsig.webservice.CamposAPI.DIAS_BLOQUEIO;
import static com.zetra.econsig.webservice.CamposAPI.DIAS_SEM_APRV_SDV;
import static com.zetra.econsig.webservice.CamposAPI.DIAS_SEM_INFO_SDV;
import static com.zetra.econsig.webservice.CamposAPI.DIAS_SEM_LIQUIDACAO;
import static com.zetra.econsig.webservice.CamposAPI.DIAS_SEM_PG_SDV;
import static com.zetra.econsig.webservice.CamposAPI.PENDENCIA_COMPRA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SDV_APROVADO;
import static com.zetra.econsig.webservice.CamposAPI.SDV_INFORMADO;
import static com.zetra.econsig.webservice.CamposAPI.SDV_PAGO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.compra.v1.AcompanharCompraContrato;

/**
 * <p>Title: AcompanharCompraContratoAssembler</p>
 * <p>Description: Assembler para AcompanharCompraContrato.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class AcompanharCompraContratoAssembler extends BaseAssembler {

    private AcompanharCompraContratoAssembler() {
        //
    }

    public static Map<CamposAPI, Object> toMap(AcompanharCompraContrato acompanharCompraContrato) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, acompanharCompraContrato.getUsuario());
        parametros.put(SENHA, acompanharCompraContrato.getSenha());
        parametros.put(RSE_MATRICULA, getValue(acompanharCompraContrato.getMatricula()));
        parametros.put(SER_CPF, getValue(acompanharCompraContrato.getCpf()));
        final Long adeNumero = getValue(acompanharCompraContrato.getAdeNumero());
        if ((adeNumero != null) && (adeNumero != Long.MIN_VALUE)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }
        parametros.put(CONTRATO_LIQUIDADO, getValue(acompanharCompraContrato.getContratoLiquidado()));
        parametros.put(COMPRADO_PELA_ENTIDADE, acompanharCompraContrato.getContratosCompradosPelaEntidade());
        parametros.put(DATA_FIM_COMPRA, toDate(acompanharCompraContrato.getDataFimCompra()));
        parametros.put(DATA_INICIO_COMPRA, toDate(acompanharCompraContrato.getDataInicioCompra()));
        final Short diasBloqueio = getValue(acompanharCompraContrato.getDiasParaBloqueio());
        if ((diasBloqueio == null) || (diasBloqueio == Short.MIN_VALUE)) {
            parametros.put(DIAS_BLOQUEIO, null);
        } else {
            parametros.put(DIAS_BLOQUEIO, diasBloqueio);
        }
        final Short diasSemAprovacaoSaldoDevedor = getValue(acompanharCompraContrato.getDiasUteisSemAprovacaoSaldoDevedor());
        if ((diasSemAprovacaoSaldoDevedor == null) || (diasSemAprovacaoSaldoDevedor == Short.MIN_VALUE)) {
            parametros.put(DIAS_SEM_APRV_SDV, null);
        } else {
            parametros.put(DIAS_SEM_APRV_SDV, diasSemAprovacaoSaldoDevedor);
        }
        final Short diasSemInfoSaldoDevedor = getValue(acompanharCompraContrato.getDiasUteisSemInfoSaldoDevedor());
        if ((diasSemInfoSaldoDevedor == null) || (diasSemInfoSaldoDevedor == Short.MIN_VALUE)) {
            parametros.put(DIAS_SEM_INFO_SDV, null);
        } else {
            parametros.put(DIAS_SEM_INFO_SDV, diasSemInfoSaldoDevedor);
        }
        final Short diasSemLiquidacao = getValue(acompanharCompraContrato.getDiasUteisSemLiquidacao());
        if ((diasSemLiquidacao == null) || (diasSemLiquidacao == Short.MIN_VALUE)) {
            parametros.put(DIAS_SEM_LIQUIDACAO, null);
        } else {
            parametros.put(DIAS_SEM_LIQUIDACAO, diasSemLiquidacao);
        }
        final Short diasSemPagamentoSaldoDevedor = getValue(acompanharCompraContrato.getDiasUteisSemPagamentoSaldoDevedor());
        if ((diasSemPagamentoSaldoDevedor == null) || (diasSemPagamentoSaldoDevedor == Short.MIN_VALUE)) {
            parametros.put(DIAS_SEM_PG_SDV, null);
        } else {
            parametros.put(DIAS_SEM_PG_SDV, diasSemPagamentoSaldoDevedor);
        }
        parametros.put(SDV_APROVADO, getValue(acompanharCompraContrato.getSaldoDevedorAprovado()));
        parametros.put(SDV_INFORMADO, getValue(acompanharCompraContrato.getSaldoDevedorInformado()));
        parametros.put(SDV_PAGO, getValue(acompanharCompraContrato.getSaldoDevedorPago()));
        parametros.put(CONTRATOS_BLOQ_A_BLOQUEAR, acompanharCompraContrato.getTemContratosBloqueadosOuABloquear());
        parametros.put(PENDENCIA_COMPRA, acompanharCompraContrato.getTemPendenciaProcessoCompra());
        parametros.put(CONVENIO, getValue(acompanharCompraContrato.getConvenio()));
        parametros.put(CLIENTE, getValue(acompanharCompraContrato.getCliente()));

        return parametros;
    }
}