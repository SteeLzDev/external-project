package com.zetra.econsig.persistence.query.calendario;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCalFolhaEstAgrupadoFiltroPeriodoQuery</p>
 * <p>Description: Lista os registros de calendário da folha do estabelecimento
 * que estão agrupados com o período passado por parâmetro.</p>
 * <p>Copyright: Copyright (c) 2016</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCalFolhaEstAgrupadoFiltroPeriodoQuery extends HQuery {

    public String estCodigo;
    public Date periodoInicio;
    public Date periodoFim;


    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT cfe.cfePeriodo ");
        corpoBuilder.append("FROM CalendarioFolhaEst cfe ");
        corpoBuilder.append("WHERE cfe.estCodigo = :codigo ");
        corpoBuilder.append("AND cfe.cfePeriodo BETWEEN :periodoIncio AND :periodoFim ");
        corpoBuilder.append("AND cfe.cfeDataFim = cfe.cfeDataIni ");
        corpoBuilder.append("ORDER BY cfe.cfePeriodo ASC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("codigo", estCodigo, query);
        defineValorClausulaNomeada("periodoIncio", periodoInicio, query);
        defineValorClausulaNomeada("periodoFim", periodoFim, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CFE_PERIODO
        };
    }
}
