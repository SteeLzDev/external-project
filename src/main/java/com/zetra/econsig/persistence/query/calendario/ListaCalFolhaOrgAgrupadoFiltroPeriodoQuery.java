package com.zetra.econsig.persistence.query.calendario;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCalFolhaOrgAgrupadoFiltroPeriodoQuery</p>
 * <p>Description: Lista os registros de calendário da folha do órgão
 * que estão agrupados com o período passado por parâmetro.</p>
 * <p>Copyright: Copyright (c) 2016</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCalFolhaOrgAgrupadoFiltroPeriodoQuery extends HQuery {

    public String orgCodigo;
    public Date periodoInicio;
    public Date periodoFim;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT cfo.cfoPeriodo ");
        corpoBuilder.append("FROM CalendarioFolhaOrg cfo ");
        corpoBuilder.append("WHERE cfo.orgCodigo = :codigo ");
        corpoBuilder.append("AND cfo.cfoPeriodo BETWEEN :periodoIncio AND :periodoFim ");
        corpoBuilder.append("AND cfo.cfoDataFim = cfo.cfoDataIni ");
        corpoBuilder.append("ORDER BY cfo.cfoPeriodo ASC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("codigo", orgCodigo, query);
        defineValorClausulaNomeada("periodoIncio", periodoInicio, query);
        defineValorClausulaNomeada("periodoFim", periodoFim, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CFO_PERIODO
        };
    }
}
