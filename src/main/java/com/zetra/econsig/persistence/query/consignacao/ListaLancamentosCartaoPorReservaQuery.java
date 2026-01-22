package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

public class ListaLancamentosCartaoPorReservaQuery extends HQuery {

    public String adeCodigo;
    public boolean verificaHistorico;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ");
        corpoBuilder.append("ade.adeNumero, ");
        corpoBuilder.append("prd.prdDataDesconto, ");
        corpoBuilder.append("prd.prdVlrRealizado, ");
        corpoBuilder.append("spd.spdDescricao, ");
        corpoBuilder.append("ocp.ocpData, ");
        corpoBuilder.append("usu.usuLogin, ");
        corpoBuilder.append("ocp.ocpObs ");
        if(!verificaHistorico) {
            corpoBuilder.append("FROM RelacionamentoAutorizacao rad ");
        } else {
            corpoBuilder.append(" FROM HtRelacionamentoAdeDestino rad ");
        }
        corpoBuilder.append("INNER JOIN rad.autDescontoByAdeCodigoDestino ade ");
        corpoBuilder.append("INNER JOIN rad.tipoNatureza tnt ");
        corpoBuilder.append("INNER JOIN ade.parcelaDescontoSet prd ");
        corpoBuilder.append("INNER JOIN prd.statusParcelaDesconto spd ");
        corpoBuilder.append("INNER JOIN prd.ocorrenciaParcelaSet ocp ");
        corpoBuilder.append("INNER JOIN ocp.usuario usu ");
        corpoBuilder.append("WHERE rad.adeCodigoOrigem ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        corpoBuilder.append("AND rad.tntCodigo = '").append(CodedValues.TNT_CARTAO).append("' ");
        corpoBuilder.append("ORDER BY prd.prdDataDesconto ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.ADE_NUMERO,
                Columns.PRD_DATA_DESCONTO,
                Columns.PRD_VLR_REALIZADO,
                Columns.SPD_DESCRICAO,
                Columns.OCP_DATA,
                Columns.USU_LOGIN,
                Columns.OCP_OBS
        };
    }
}
