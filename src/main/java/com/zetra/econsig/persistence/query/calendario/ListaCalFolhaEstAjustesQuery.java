package com.zetra.econsig.persistence.query.calendario;

import java.util.Date;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCalFolhaEstAjustesQuery</p>
 * <p>Description: Lista os períodos do calendário da folha do estabelecimento que estão no período de ajustes.</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCalFolhaEstAjustesQuery extends HQuery {

    public String estCodigo;
    public Date dataLimite;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT cfe.cfePeriodo ");
        corpoBuilder.append("FROM CalendarioFolhaEst cfe ");
        corpoBuilder.append("WHERE cfe.estCodigo = :codigo ");
        corpoBuilder.append("AND current_date() between cfe.cfeDataFim and cfe.cfeDataFimAjustes ");
        if (dataLimite != null) {
            corpoBuilder.append("AND cfe.cfePeriodo <= :dataLimite ");
        }
        corpoBuilder.append("ORDER BY cfe.cfePeriodo ASC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("codigo", estCodigo, query);
        if (dataLimite != null) {
            defineValorClausulaNomeada("dataLimite", dataLimite, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CFE_PERIODO
        };
    }
}
