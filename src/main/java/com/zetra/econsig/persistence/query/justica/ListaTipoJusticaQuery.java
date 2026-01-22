package com.zetra.econsig.persistence.query.justica;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTipoJusticaQuery</p>
 * <p>Description: Listagem de Tipos de Justiça</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTipoJusticaQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();

        corpo.append("select ");
        corpo.append("tju.tjuCodigo, ");
        corpo.append("tju.tjuDescricao ");
        corpo.append("from TipoJustica tju ");
        corpo.append("order by tju.tjuDescricao");

        return instanciarQuery(session, corpo.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TJU_CODIGO,
                Columns.TJU_DESCRICAO
        };
    }
}
