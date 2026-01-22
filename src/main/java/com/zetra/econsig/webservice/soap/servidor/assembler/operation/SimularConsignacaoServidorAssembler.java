package com.zetra.econsig.webservice.soap.servidor.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_LIBERADO;
import static com.zetra.econsig.webservice.CamposAPI.VALOR_PARCELA;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.servidor.v1.SimularConsignacaoServidor;

/**
 * <p>Title: getValue(simularConsignacaoServidorAssembler</p>
 * <p>Description: Assembler para getValue(simularConsignacaoServidor.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class SimularConsignacaoServidorAssembler extends BaseAssembler {

    private SimularConsignacaoServidorAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(SimularConsignacaoServidor simularConsignacaoServidor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(SENHA, simularConsignacaoServidor.getSenha());
        parametros.put(SERVICO_CODIGO, getValue(simularConsignacaoServidor.getServicoCodigo()));

        final Double adeVlr = getValue(simularConsignacaoServidor.getValorParcela());
        if (adeVlr.equals(Double.NaN) || (adeVlr == 0.0)) {
            parametros.put(VALOR_PARCELA, null);
        } else {
            parametros.put(VALOR_PARCELA, adeVlr);
        }

        final Double vlrLib = getValue(simularConsignacaoServidor.getValorLiberado());
        if (vlrLib.equals(Double.NaN) || (vlrLib == 0.0)) {
            parametros.put(VALOR_LIBERADO, null);
        } else {
            parametros.put(VALOR_LIBERADO, vlrLib);
        }

        final Integer prazo = getValue(simularConsignacaoServidor.getPrazo());
        if ((prazo == null) || (prazo == Integer.MIN_VALUE) || (prazo <= 0)) {
            parametros.put(PRAZO, null);
        } else {
            parametros.put(PRAZO, prazo);
        }

        parametros.put(RSE_MATRICULA, simularConsignacaoServidor.getMatricula());
        parametros.put(ORG_IDENTIFICADOR, getValue(simularConsignacaoServidor.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(simularConsignacaoServidor.getEstabelecimentoCodigo()));
        parametros.put(SER_LOGIN, getValue(simularConsignacaoServidor.getLoginServidor()));

        return parametros;
    }

    public static Map<CamposAPI, Object> toMap(com.zetra.econsig.webservice.soap.servidor.v2.SimularConsignacaoServidor simularConsignacaoServidor) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);

        parametros.put(SENHA, simularConsignacaoServidor.getSenha());
        parametros.put(SERVICO_CODIGO, getValue(simularConsignacaoServidor.getServicoCodigo()));

        final Double adeVlr = getValue(simularConsignacaoServidor.getValorParcela());
        if (adeVlr.equals(Double.NaN) || (adeVlr == 0.0)) {
            parametros.put(VALOR_PARCELA, null);
        } else {
            parametros.put(VALOR_PARCELA, adeVlr);
        }

        final Double vlrLib = getValue(simularConsignacaoServidor.getValorLiberado());
        if (vlrLib.equals(Double.NaN) || (vlrLib == 0.0)) {
            parametros.put(VALOR_LIBERADO, null);
        } else {
            parametros.put(VALOR_LIBERADO, vlrLib);
        }

        final Integer prazo = getValue(simularConsignacaoServidor.getPrazo());
        if ((prazo == null) || (prazo == Integer.MIN_VALUE) || (prazo <= 0)) {
            parametros.put(PRAZO, null);
        } else {
            parametros.put(PRAZO, prazo);
        }

        parametros.put(RSE_MATRICULA, simularConsignacaoServidor.getMatricula());
        parametros.put(ORG_IDENTIFICADOR, getValue(simularConsignacaoServidor.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(simularConsignacaoServidor.getEstabelecimentoCodigo()));
        parametros.put(SER_LOGIN, getValue(simularConsignacaoServidor.getLoginServidor()));

        return parametros;
    }
}