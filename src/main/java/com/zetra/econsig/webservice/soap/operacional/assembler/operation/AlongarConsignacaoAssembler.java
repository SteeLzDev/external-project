package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_INDICE;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_IOF;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_MENS_VINC;
import static com.zetra.econsig.webservice.CamposAPI.ADE_VLR_TAC;
import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
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

import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.AlongarConsignacao;

/**
 * <p>Title: AlongarConsignacaoAssembler</p>
 * <p>Description: Assembler para AlongarConsignacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class AlongarConsignacaoAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AlongarConsignacaoAssembler.class);

    private AlongarConsignacaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(AlongarConsignacao alongarConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, alongarConsignacao.getUsuario());
        parametros.put(SENHA, alongarConsignacao.getSenha());
        parametros.put(SER_SENHA, alongarConsignacao.getSenhaServidor());
        parametros.put(TOKEN, alongarConsignacao.getTokenAutServidor());
        parametros.put(SERVICO_CODIGO, getValue(alongarConsignacao.getServicoCodigo()));
        parametros.put(DATA_NASC, getValueAsDate(alongarConsignacao.getDataNascimento()));

        final Double adeVlr = alongarConsignacao.getValorParcela();
        if (adeVlr.equals(Double.NaN)) {
            parametros.put(VALOR_PARCELA, null);
        } else {
            parametros.put(VALOR_PARCELA, adeVlr);
        }
        if ((alongarConsignacao.getPrazo() == Integer.MIN_VALUE) || (alongarConsignacao.getPrazo() <= 0)) {
            parametros.put(PRAZO, null);
        } else {
            parametros.put(PRAZO, alongarConsignacao.getPrazo());
        }

        final Double vlrLib = getValue(alongarConsignacao.getValorLiberado());
        if ((vlrLib == null) || vlrLib.equals(Double.NaN)) {
            parametros.put(VALOR_LIBERADO, null);
        } else {
            parametros.put(VALOR_LIBERADO, vlrLib);
        }
        parametros.put(CNV_COD_VERBA, getValue(alongarConsignacao.getCodVerba()));
        parametros.put(COR_IDENTIFICADOR, getValue(alongarConsignacao.getCorrespondenteCodigo()));
        final Integer carencia = getValue(alongarConsignacao.getCarencia());
        if ((carencia == null) || (carencia == Integer.MAX_VALUE) || (carencia <= 0)) {
            parametros.put(ADE_CARENCIA, null);
        } else {
            parametros.put(ADE_CARENCIA, getValue(alongarConsignacao.getCarencia()));
        }

        final Double vlrTac = getValue(alongarConsignacao.getValorTac());
        if ((vlrTac == null) || vlrTac.equals(Double.NaN)) {
            parametros.put(ADE_VLR_TAC, null);
        } else {
            parametros.put(ADE_VLR_TAC, vlrTac);
        }
        parametros.put(ADE_INDICE, getValue(alongarConsignacao.getIndice()));

        final Double vlrIof = getValue(alongarConsignacao.getValorIof());
        if ((vlrIof == null) || vlrIof.equals(Double.NaN)) {
            parametros.put(ADE_VLR_IOF, null);
        } else {
            parametros.put(ADE_VLR_IOF, vlrIof);
        }

        final Double vlrMenVinc = getValue(alongarConsignacao.getValorMensVin());
        if ((vlrMenVinc == null) || vlrMenVinc.equals(Double.NaN)) {
            parametros.put(ADE_VLR_MENS_VINC, null);
        } else {
            parametros.put(ADE_VLR_MENS_VINC, vlrMenVinc);
        }

        parametros.put(ADE_IDENTIFICADOR, getValue(alongarConsignacao.getAdeIdentificador()));
        final Long adeNumero = getValue(alongarConsignacao.getAdeNumero());
        if ((adeNumero != null) && (adeNumero > 0) && (adeNumero != Long.MAX_VALUE)) {
            parametros.put(ADE_NUMERO, adeNumero);
        }

        parametros.put(RSE_MATRICULA, alongarConsignacao.getMatricula());
        parametros.put(SER_CPF, getValue(alongarConsignacao.getCpf()));
        parametros.put(ORG_IDENTIFICADOR, getValue(alongarConsignacao.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(alongarConsignacao.getEstabelecimentoCodigo()));
        parametros.put(RSE_BANCO, getValue(alongarConsignacao.getBanco()));
        parametros.put(RSE_AGENCIA, getValue(alongarConsignacao.getAgencia()));
        parametros.put(RSE_CONTA, getValue(alongarConsignacao.getConta()));
        parametros.put(CONVENIO, getValue(alongarConsignacao.getConvenio()));
        parametros.put(CLIENTE, getValue(alongarConsignacao.getCliente()));
        parametros.put(SER_LOGIN, alongarConsignacao.getLoginServidor());
        parametros.put(NOVO_ADE_IDENTIFICADOR, getValue(alongarConsignacao.getNovoAdeIdentificador()));
        parametros.put(NSE_CODIGO, getValue(alongarConsignacao.getNaturezaServicoCodigo()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v6.AlongarConsignacao alongarConsignacao) {
       final com.zetra.econsig.webservice.soap.operacional.v1.AlongarConsignacao alongarConsignacaoV1 = new com.zetra.econsig.webservice.soap.operacional.v1.AlongarConsignacao();
        try {
            BeanUtils.copyProperties(alongarConsignacaoV1, alongarConsignacao);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }
        return toMap(alongarConsignacaoV1);
    }
}