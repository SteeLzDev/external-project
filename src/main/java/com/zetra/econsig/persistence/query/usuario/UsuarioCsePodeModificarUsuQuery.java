package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: UsuarioCsePodeModificarUsuQuery</p>
 * <p>Description: Verifica se o usuário de CSE pode modificar um outro usuário,
 * que não seja usuário de suporte.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioCsePodeModificarUsuQuery extends HQuery {

    public String usuCodigoAfetado;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" SELECT 1 FROM Usuario usu");
        corpoBuilder.append(" WHERE usu.usuCodigo ").append(criaClausulaNomeada("usuCodigoAfetado", usuCodigoAfetado));
        corpoBuilder.append(" AND COALESCE(usu.usuVisivel,'S') = 'S' ");
        corpoBuilder.append(" AND NOT EXISTS (SELECT 1 FROM UsuarioSup sup");
        corpoBuilder.append(" WHERE sup.usuCodigo ").append(criaClausulaNomeada("usuCodigoAfetado", usuCodigoAfetado)).append(")");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("usuCodigoAfetado", usuCodigoAfetado, query);
        return query;
    }
}
