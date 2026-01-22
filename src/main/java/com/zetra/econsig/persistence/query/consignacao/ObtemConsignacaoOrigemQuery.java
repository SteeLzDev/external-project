package com.zetra.econsig.persistence.query.consignacao;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import org.hibernate.Session;
import org.hibernate.query.Query;


public class ObtemConsignacaoOrigemQuery extends HQuery {

    public String adeCodigoDestino;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select ");
        corpoBuilder.append("tad.adeCodigo, ");
        corpoBuilder.append("tad.adeData, ");
        corpoBuilder.append("tad.adeAnoMesIni, ");
        corpoBuilder.append("tad.adeAnoMesFim, ");
        corpoBuilder.append("tad.adePrazo, ");
        corpoBuilder.append("tad.adeVlr, ");
        corpoBuilder.append("tad.adePrdPagasTotal ");
        corpoBuilder.append("from RelacionamentoAutorizacao rad ");
        corpoBuilder.append("inner join AutDesconto tad on (tad.adeCodigo = rad.adeCodigoOrigem) ");
        corpoBuilder.append("WHERE rad.adeCodigoDestino").append(criaClausulaNomeada("adeCodigo", adeCodigoDestino));
        corpoBuilder.append(" AND rad.tntCodigo").append(criaClausulaNomeada("tntCodigo", CodedValues.TNT_SOLICITACAO_PORTABILIDADE));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigoDestino, query);
        defineValorClausulaNomeada("tntCodigo", CodedValues.TNT_SOLICITACAO_PORTABILIDADE, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.ADE_CODIGO,
                Columns.ADE_DATA,
                Columns.ADE_ANO_MES_INI,
                Columns.ADE_ANO_MES_FIM,
                Columns.ADE_PRAZO,
                Columns.ADE_VLR,
                Columns.ADE_PRD_PAGAS_TOTAL,
        };
    }
}