package com.zetra.econsig.persistence.query.orgao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaSubOrgaoQuery</p>
 * <p>Description: Listagem de Sub-órgãos</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaSubOrgaoQuery extends HQuery {

    public String orgCodigo;
    public String sboIdentificador;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpo = new StringBuilder();
        corpo.append("SELECT ");
        corpo.append("sbo.sboCodigo, ");
        corpo.append("sbo.sboIdentificador, ");
        corpo.append("sbo.sboDescricao ");
        corpo.append("FROM SubOrgao sbo ");
        corpo.append("WHERE 1=1 ");

        if (!TextHelper.isNull(orgCodigo)) {
            corpo.append("AND sbo.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        if (!TextHelper.isNull(sboIdentificador)) {
            corpo.append(" AND sbo.sboIdentificador ").append(criaClausulaNomeada("sboIdentificador", sboIdentificador));
        }

        corpo.append(" ORDER BY sbo.sboDescricao ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        if (!TextHelper.isNull(sboIdentificador)) {
            defineValorClausulaNomeada("sboIdentificador", sboIdentificador, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
                Columns.SBO_CODIGO,
                Columns.SBO_IDENTIFICADOR,
                Columns.SBO_DESCRICAO
    	};
    }
}
