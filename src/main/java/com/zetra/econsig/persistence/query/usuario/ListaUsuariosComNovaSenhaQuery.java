package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaUsuariosComNovaSenhaQuery</p>
 * <p>Description: Lista usuários com novas senhas a ativar</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaUsuariosComNovaSenhaQuery extends HQuery {

    public boolean count = false;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        if (count) {
            corpoBuilder.append("select count(*) ");
        } else {
            corpoBuilder.append("select usu.usuCodigo ");
        }
        corpoBuilder.append(" from Usuario usu ");
        corpoBuilder.append(" where usu.usuNovaSenha ").append(criaClausulaNomeada("usuNovaSenha", CodedValues.IS_NOT_NULL_KEY));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_CODIGO
        };
    }

}
