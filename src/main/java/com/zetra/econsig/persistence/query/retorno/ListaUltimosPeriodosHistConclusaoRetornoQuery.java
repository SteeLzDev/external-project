package com.zetra.econsig.persistence.query.retorno;

import java.text.ParseException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaUltimosPeriodosHistConclusaoRetornoQuery</p>
 * <p>Description: Lista os últimos períodos em que houve conclusão de retorno.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaUltimosPeriodosHistConclusaoRetornoQuery extends HQuery {

    public List<String> orgCodigos;
    public String periodo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = null;
        corpo = "SELECT " +
                "hcr.hcrPeriodo ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM HistoricoConclusaoRetorno hcr ");
        corpoBuilder.append(" WHERE 1 = 1 ");
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" and hcr.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }
        if (!TextHelper.isNull(periodo)) {
            corpoBuilder.append(" AND hcr.hcrPeriodo <= :periodo");
        }
        corpoBuilder.append(" AND hcr.hcrDesfeito = 'N' ");
        corpoBuilder.append(" GROUP BY hcr.hcrPeriodo ");
        corpoBuilder.append(" ORDER BY hcr.hcrPeriodo DESC ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(periodo)) {
            try {
                defineValorClausulaNomeada("periodo", DateHelper.parse(periodo, "yyyy-MM-dd"), query);
            } catch (ParseException ex) {
                throw new HQueryException(ex);
            }
        }
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.HCR_PERIODO
        };
    }
}
