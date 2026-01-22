package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaIncidenciasMargemQuery</p>
 * <p>Description: Lista as incidências de margens cadastradas no sistema.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaIncidenciasMargemQuery extends HQuery {
    
    private String tpsIncideMargem = CodedValues.TPS_INCIDE_MARGEM;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                "tps.tpsCodigo, " +
                "pse.pseVlr ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from TipoParamSvc tps");
        corpoBuilder.append(" inner join tps.paramSvcConsignanteSet pse ");
        corpoBuilder.append(" where 1=1 ");

        corpoBuilder.append(" AND tps.tpsCodigo ").append(criaClausulaNomeada("tpsCodigo", tpsIncideMargem));
        
        corpoBuilder.append(" group by tps.tpsCodigo, pse.pseVlr");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("tpsCodigo", tpsIncideMargem, query);

        return query;
    }
    
    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TPS_CODIGO,
                Columns.PSE_VLR
        };
    }
}
