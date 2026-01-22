package com.zetra.econsig.persistence.query.consignacao;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class ObtemQtdAdeReservaCartaoSemLancamentoQuery extends HQuery {
    public String csaCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select count(ade.adeCodigo) ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("WHERE cnv.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" and svc.nseCodigo = '").append(CodedValues.NSE_CARTAO).append("' ");
        corpoBuilder.append(" and ade.sadCodigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "','")).append("') ");

        corpoBuilder.append("AND NOT EXISTS (select 1 from RelacionamentoAutorizacao rau ");
        corpoBuilder.append("WHERE rau.adeCodigoOrigem = ade.adeCodigo ");
        corpoBuilder.append("AND rau.tntCodigo = '").append(CodedValues.TNT_CARTAO).append("')");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        return query;
    }
}
