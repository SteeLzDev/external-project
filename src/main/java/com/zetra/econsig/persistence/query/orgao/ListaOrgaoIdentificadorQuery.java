package com.zetra.econsig.persistence.query.orgao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ListaOrgaoIdentificadorQuery</p>
 * <p>Description: Listagem de identificadores de órgãos</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaOrgaoIdentificadorQuery extends HQuery {

    public List<String> orgCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT org.orgIdentificador FROM Orgao org ");

        if (orgCodigos != null && orgCodigos.size() > 0) {
            corpoBuilder.append(" WHERE org.orgCodigo ").append(criaClausulaNomeada("orgCodigos", orgCodigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (orgCodigos != null && orgCodigos.size() > 0) {
            defineValorClausulaNomeada("orgCodigos", orgCodigos, query);
        }

        return query;
    }
}
