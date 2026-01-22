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
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.operacional.v1.RenegociarConsignacao;

/**
 * <p>Title: AnEmptyAssembler</p>
 * <p>Description: Assembler para AnEmpty.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class RenegociarConsignacaoAssembler extends BaseAssembler {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RenegociarConsignacaoAssembler.class);

    private RenegociarConsignacaoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(RenegociarConsignacao renegociarConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, renegociarConsignacao.getUsuario());
        parametros.put(SENHA, renegociarConsignacao.getSenha());
        parametros.put(SER_SENHA, renegociarConsignacao.getSenhaServidor());
        parametros.put(TOKEN, renegociarConsignacao.getTokenAutServidor());
        parametros.put(SERVICO_CODIGO, getValue(renegociarConsignacao.getServicoCodigo()));
        parametros.put(DATA_NASC, getValueAsDate(renegociarConsignacao.getDataNascimento()));
        parametros.put(VALOR_PARCELA, renegociarConsignacao.getValorParcela());

        final Double adeVlr = renegociarConsignacao.getValorParcela();
        if ((adeVlr == null) || adeVlr.equals(Double.NaN)) {
            parametros.put(VALOR_PARCELA, null);
        } else {
            parametros.put(VALOR_PARCELA, adeVlr);
        }
        if ((renegociarConsignacao.getPrazo() == Integer.MIN_VALUE) || (renegociarConsignacao.getPrazo() <= 0)) {
            parametros.put(PRAZO, null);
        } else {
            parametros.put(PRAZO, renegociarConsignacao.getPrazo());
        }

        final Double vlrLib = getValue(renegociarConsignacao.getValorLiberado());
        if ((vlrLib == null) || vlrLib.equals(Double.NaN)) {
            parametros.put(VALOR_LIBERADO, null);
        } else {
            parametros.put(VALOR_LIBERADO, vlrLib);
        }
        parametros.put(CNV_COD_VERBA, getValue(renegociarConsignacao.getCodVerba()));
        parametros.put(COR_IDENTIFICADOR, getValue(renegociarConsignacao.getCorrespondenteCodigo()));
        final Integer carencia = getValue(renegociarConsignacao.getCarencia());
        if (((carencia == null) || (carencia == Integer.MIN_VALUE)) || (carencia <= 0)) {
            parametros.put(ADE_CARENCIA, null);
        } else {
            parametros.put(ADE_CARENCIA, getValue(renegociarConsignacao.getCarencia()));
        }

        final Double vlrTac = getValue(renegociarConsignacao.getValorTac());
        if ((vlrTac == null) || vlrTac.equals(Double.NaN)) {
            parametros.put(ADE_VLR_TAC, null);
        } else {
            parametros.put(ADE_VLR_TAC, vlrTac);
        }
        parametros.put(ADE_INDICE, getValue(renegociarConsignacao.getIndice()));

        final Double vlrIof = getValue(renegociarConsignacao.getValorIof());
        if ((vlrIof == null) || vlrIof.equals(Double.NaN)) {
            parametros.put(ADE_VLR_IOF, null);
        } else {
            parametros.put(ADE_VLR_IOF, vlrIof);
        }

        final Double vlrMenVinc = getValue(renegociarConsignacao.getValorMensVin());
        if ((vlrMenVinc == null) || vlrMenVinc.equals(Double.NaN)) {
            parametros.put(ADE_VLR_MENS_VINC, null);
        } else {
            parametros.put(ADE_VLR_MENS_VINC, vlrMenVinc);
        }

        final List<String> adeIdentificadores = renegociarConsignacao.getAdeIdentificador();
        if ((adeIdentificadores != null) && !adeIdentificadores.isEmpty() && !TextHelper.isNull(adeIdentificadores.get(0))) {
            parametros.put(ADE_IDENTIFICADOR, renegociarConsignacao.getAdeIdentificador());
        } else {
            parametros.put(ADE_IDENTIFICADOR, null);
        }

        final List<Long> adeNumeros = renegociarConsignacao.getAdeNumeros();
        if ((adeNumeros != null) && !adeNumeros.isEmpty()) {
            final List<Long> adeNumeroArray = new ArrayList<>();
            for (final Long adeNumero : adeNumeros) {
                if ((adeNumero != null) && (adeNumero > 0) && (adeNumero != Long.MAX_VALUE)) {
                    adeNumeroArray.add(adeNumero);
                }
            }
            parametros.put(ADE_NUMERO, adeNumeroArray);
        }

        parametros.put(RSE_MATRICULA, renegociarConsignacao.getMatricula());
        parametros.put(SER_CPF, getValue(renegociarConsignacao.getCpf()));
        parametros.put(ORG_IDENTIFICADOR, getValue(renegociarConsignacao.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(renegociarConsignacao.getEstabelecimentoCodigo()));
        parametros.put(RSE_BANCO, getValue(renegociarConsignacao.getBanco()));
        parametros.put(RSE_AGENCIA, getValue(renegociarConsignacao.getAgencia()));
        parametros.put(RSE_CONTA, getValue(renegociarConsignacao.getConta()));
        parametros.put(CONVENIO, getValue(renegociarConsignacao.getConvenio()));
        parametros.put(CLIENTE, getValue(renegociarConsignacao.getCliente()));
        parametros.put(SER_LOGIN, renegociarConsignacao.getLoginServidor());
        parametros.put(NOVO_ADE_IDENTIFICADOR, getValue(renegociarConsignacao.getNovoAdeIdentificador()));
        parametros.put(NSE_CODIGO, getValue(renegociarConsignacao.getNaturezaServicoCodigo()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v6.RenegociarConsignacao renegociarConsignacao) {
        final RenegociarConsignacao renegociarConsignacaoV1 = new RenegociarConsignacao();
        try {
            BeanUtils.copyProperties(renegociarConsignacaoV1, renegociarConsignacao);
            renegociarConsignacaoV1.getAdeIdentificador().addAll(renegociarConsignacao.getAdeIdentificador());
            renegociarConsignacaoV1.getAdeNumeros().addAll(renegociarConsignacao.getAdeNumeros());
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOG.warn(e.getMessage(), e);
        }
        return toMap(renegociarConsignacaoV1);
    }
}