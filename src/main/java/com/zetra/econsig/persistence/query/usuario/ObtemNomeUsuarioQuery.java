package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemNomeUsuarioQuery</p>
 * <p>Description: Busca nome do usuário pelo usuCodigo, login ou offset</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemNomeUsuarioQuery extends HQuery {
    public boolean count = false;

    public String usuLogin = null;
    public String usuCodigo = null;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        if (count) {
          return instanciarQuery(session, "SELECT count(*) FROM Usuario usuario");
        }
        StringBuilder sql = new StringBuilder("SELECT usuario.usuNome FROM Usuario usuario WHERE 1=1");
        if (!TextHelper.isNull(usuLogin)) {
            sql.append(" AND usuario.usuLogin ").append(criaClausulaNomeada("usuLogin", usuLogin));
        }
        if (!TextHelper.isNull(usuCodigo)) {
            sql.append(" AND usuario.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
        }
        Query<Object[]> query = instanciarQuery(session, sql.toString());
        if (!count) {
            if (!TextHelper.isNull(usuLogin)) {
                defineValorClausulaNomeada("usuLogin", usuLogin, query);
            }
            if (!TextHelper.isNull(usuCodigo)) {
                defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
            }
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_NOME
        };
    }
}
