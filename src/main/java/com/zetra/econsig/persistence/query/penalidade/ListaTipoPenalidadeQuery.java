package com.zetra.econsig.persistence.query.penalidade;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTipoPenalidadeQuery</p>
 * <p>Description: Listagem de Tipos de Penalidade</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTipoPenalidadeQuery extends HQuery {

    private String tpeCodigo;

    public ListaTipoPenalidadeQuery() {
    }

    public ListaTipoPenalidadeQuery(String tpeCodigo) {
        this.tpeCodigo = tpeCodigo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        corpo.append("select ");
        corpo.append("tpe.tpeCodigo, ");
        corpo.append("tpe.tpeDescricao, ");
        corpo.append("tpe.tpePrazoPenalidade ");
        corpo.append("from TipoPenalidade tpe ");
        corpo.append("where 1 = 1 ");

        if (!TextHelper.isNull(tpeCodigo)) {
            corpo.append(" and tpe.tpeCodigo ").append(criaClausulaNomeada("tpeCodigo", tpeCodigo));
        }

        corpo.append(" order by tpe.tpeDescricao");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(tpeCodigo)) {
            defineValorClausulaNomeada("tpeCodigo", tpeCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TPE_CODIGO,
                Columns.TPE_DESCRICAO,
                Columns.TPE_PRAZO_PENALIDADE
        };
    }
}
