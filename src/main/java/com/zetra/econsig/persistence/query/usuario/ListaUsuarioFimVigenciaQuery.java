package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaUsuarioFimVigenciaQuery</p>
 * <p>Description: Lista os usuários que devem ser bloqueados por fim de vigencia</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaUsuarioFimVigenciaQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" SELECT ");
        corpoBuilder.append(" usuario.usuCodigo ");
        corpoBuilder.append(" FROM Usuario usuario ");

        corpoBuilder.append(" WHERE ");
        corpoBuilder.append(" usuario.usuDataFimVig IS NOT NULL ");
        corpoBuilder.append(" AND usuario.statusLogin.stuCodigo ").append(criaClausulaNomeada("statusAtivo", CodedValues.STU_ATIVO));
        corpoBuilder.append(" AND (to_days(usuario.usuDataFimVig) - to_days(current_date())) < 1");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("statusAtivo", CodedValues.STU_ATIVO, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_CODIGO
        };
    }
}