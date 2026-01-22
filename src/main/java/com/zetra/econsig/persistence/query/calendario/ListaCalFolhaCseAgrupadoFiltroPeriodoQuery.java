package com.zetra.econsig.persistence.query.calendario;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCalFolhaOrgAgrupadoFiltroPeriodoQuery</p>
 * <p>Description: Lista os registros de calendário da folha do consignante
 * que estão agrupados com o período passado por parâmetro.</p>
 * <p>Copyright: Copyright (c) 2016</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCalFolhaCseAgrupadoFiltroPeriodoQuery extends HQuery {

    public String cseCodigo;
    public Date periodoInicio;
    public Date periodoFim;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT cfc.cfcPeriodo ");
        corpoBuilder.append("FROM CalendarioFolhaCse cfc ");
        corpoBuilder.append("WHERE cfc.cseCodigo = :codigo ");
        corpoBuilder.append("AND cfc.cfcPeriodo BETWEEN :periodoIncio AND :periodoFim ");
        corpoBuilder.append("AND cfc.cfcDataFim = cfc.cfcDataIni ");
        corpoBuilder.append("ORDER BY cfc.cfcPeriodo ASC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("codigo", cseCodigo, query);
        defineValorClausulaNomeada("periodoIncio", periodoInicio, query);
        defineValorClausulaNomeada("periodoFim", periodoFim, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CFC_PERIODO
        };
    }
}
