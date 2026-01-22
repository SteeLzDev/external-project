package com.zetra.econsig.persistence.query.parametro;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class ListaCsaPorcentagemCadastradaTpaQuery extends HQuery {
    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        corpo.append(" SELECT tpa.csaCodigo, tpa.tpaCodigo, tpa.pcsVlr, tc.csaNome FROM ");
        corpo.append(" ParamConsignataria tpa INNER JOIN Consignataria tc on tpa.csaCodigo = tc.csaCodigo ");
        corpo.append(" WHERE tpa.tpaCodigo ").append(criaClausulaNomeada("tpaCodigo", CodedValues.TPA_PERCENTUAL_PARCELA_PAGA_ALERTA_OFERTA_REFINACIAMENTO)).append(" and tpa.pcsVlr is not null");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        defineValorClausulaNomeada("tpaCodigo", CodedValues.TPA_PERCENTUAL_PARCELA_PAGA_ALERTA_OFERTA_REFINACIAMENTO, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.TPA_CODIGO,
                Columns.PSE_VLR,
                Columns.CSA_NOME
        };
    }
}
