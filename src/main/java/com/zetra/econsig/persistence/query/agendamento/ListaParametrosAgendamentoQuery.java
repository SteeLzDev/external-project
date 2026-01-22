package com.zetra.econsig.persistence.query.agendamento;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParametrosAgendamentoQuery</p>
 * <p>Description: Listagem de parâmetros de agendamento.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParametrosAgendamentoQuery extends HQuery {

    private final String agdCodigo;

    public ListaParametrosAgendamentoQuery(String agdCodigo) {
        this.agdCodigo = agdCodigo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpo = new StringBuilder();
        corpo.append("select ");
        corpo.append("pag.pagCodigo, ");
        corpo.append("pag.agdCodigo, ");
        corpo.append("pag.pagNome, ");
        corpo.append("pag.pagValor ");

        corpo.append("from ParametroAgendamento pag ");
        corpo.append("where 1 = 1 ");

        if ((agdCodigo != null) && !agdCodigo.isEmpty()) {
            corpo.append(" and pag.agendamento.agdCodigo ").append(criaClausulaNomeada("agdCodigo", agdCodigo));
        }

        corpo.append(" order by pag.agdCodigo asc, pag.pagNome asc ");

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if ((agdCodigo != null) && !agdCodigo.isEmpty()) {
            defineValorClausulaNomeada("agdCodigo", agdCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PAG_CODIGO,
                Columns.PAG_AGD_CODIGO,
                Columns.PAG_NOME,
                Columns.PAG_VALOR
        };
    }
}
