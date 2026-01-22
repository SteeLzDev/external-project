package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaNaturezaConsignatariaQuery</p>
 * <p>Description: Listagem de Natureza de Consignatária</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author: junio $
 * $Revision: 7963 $
 * $Date: 2012-11-27 21:23:28 -0300 (ter, 27 nov 2012) $
 */
public class ListaNaturezaConsignatariaQuery extends HQuery {

	@Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder sql = new StringBuilder(100);
        sql.append("SELECT ncaCodigo, ncaDescricao, ncaExibeSer ");
        sql.append("FROM NaturezaConsignataria ");
        sql.append("ORDER BY ncaCodigo");

        Query<Object[]> query = instanciarQuery(session, sql.toString());

        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
    			Columns.NCA_CODIGO,
    			Columns.NCA_DESCRICAO,
    			Columns.NCA_EXIBE_SER
    	};
    }
}
