package com.zetra.econsig.webservice.soap.operacional.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.CLIENTE;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_LIBERADO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_PARCELA;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;

/**
 * <p>Title: SimularConsignacaoAssembler</p>
 * <p>Description: Assembler para SimularConsignacao.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class SimularConsignacaoAssembler extends BaseAssembler {

    private SimularConsignacaoAssembler() {
    }
    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.operacional.v1.SimularConsignacao simularConsignacao) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(USUARIO, simularConsignacao.getUsuario());
        parametros.put(SENHA, simularConsignacao.getSenha());
        parametros.put(SERVICO_CODIGO, getValue(simularConsignacao.getServicoCodigo()));
        parametros.put(VALOR_PARCELA, simularConsignacao.getValorParcela());

        final Double adeVlr = simularConsignacao.getValorParcela();
        if (adeVlr.equals(Double.NaN) || (adeVlr == 0.0)) {
            parametros.put(VALOR_PARCELA, null);
        } else {
            parametros.put(VALOR_PARCELA, adeVlr);
        }
        final Integer adePrazo = getValue(simularConsignacao.getPrazo());
        if ((adePrazo == null) || (adePrazo == Integer.MAX_VALUE) || (adePrazo <= 0)) {
            parametros.put(PRAZO, null);
        } else {
            parametros.put(PRAZO, adePrazo);
        }

        final Double vlrLib = getValue(simularConsignacao.getValorLiberado());
        if ((vlrLib == null) || vlrLib.equals(Double.NaN)) {
            parametros.put(VALOR_LIBERADO, null);
        } else {
            parametros.put(VALOR_LIBERADO, vlrLib);
        }
        parametros.put(CNV_COD_VERBA, getValue(simularConsignacao.getCodVerba()));
        parametros.put(RSE_MATRICULA, simularConsignacao.getMatricula());
        parametros.put(SER_CPF, getValue(simularConsignacao.getCpf()));
        parametros.put(ORG_IDENTIFICADOR, getValue(simularConsignacao.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(simularConsignacao.getEstabelecimentoCodigo()));
        parametros.put(CONVENIO, getValue(simularConsignacao.getConvenio()));
        parametros.put(CLIENTE, getValue(simularConsignacao.getCliente()));

        return parametros;
    }
}