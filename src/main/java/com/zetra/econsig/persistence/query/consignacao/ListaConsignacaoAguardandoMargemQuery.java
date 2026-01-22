package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoAguardandoMargemQuery</p>
 * <p>Description: Listagem de Consignações ordenadas por ade_data</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoAguardandoMargemQuery extends HQuery {

    public String rseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ade.adeCodigo, ade.adeVlr, ade.adeData, svc.svcCodigo, svc.svcPrioridade, pex.pexPeriodo, pex.pexDataFim, pse003.pseVlr, pse230.pseVlr, pse231.pseVlr ");
        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.servico svc ");
        corpoBuilder.append("INNER JOIN cnv.orgao org ");
        corpoBuilder.append("INNER JOIN org.periodoExportacaoSet pex ");
        corpoBuilder.append("LEFT OUTER JOIN svc.paramSvcConsignanteSet pse003 WITH pse003.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("' ");
        corpoBuilder.append("LEFT OUTER JOIN svc.paramSvcConsignanteSet pse230 WITH pse230.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_PERCENTUAL_BASE_CALC_DESCONTO_EM_FILA).append("' ");
        corpoBuilder.append("LEFT OUTER JOIN svc.paramSvcConsignanteSet pse231 WITH pse231.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_BASE_CALC_DESCONTO_EM_FILA).append("' ");
        corpoBuilder.append("WHERE ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_AGUARD_MARGEM).append("' ");
        corpoBuilder.append("AND ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo)).append(" ");
        corpoBuilder.append("AND ade.adeAnoMesIni <= pex.pexPeriodo ");
        corpoBuilder.append("ORDER BY ade.adeData ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_VLR,
                Columns.ADE_DATA,
                Columns.SVC_CODIGO,
                Columns.SVC_PRIORIDADE,
                Columns.PEX_PERIODO,
                Columns.PEX_DATA_FIM,
                "INCIDE_MARGEM",
                "PERCENTUAL_BASE_CALC",
                "BASE_CALC"
        };
    }
}
