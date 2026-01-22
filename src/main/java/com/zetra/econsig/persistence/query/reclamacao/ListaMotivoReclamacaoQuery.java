package com.zetra.econsig.persistence.query.reclamacao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaMotivoReclamacaoQuery</p>
 * <p>Description: Listagem de Tipo de Motivo de Reclamação</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaMotivoReclamacaoQuery extends HQuery {

    public boolean count = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder("select");

        if (!count) {
            corpo.append(" tmr.tmrCodigo, ");
            corpo.append(" tmr.tmrDescricao ");
        } else {
            corpo.append(" count(*) as QTDE ");
        }

        corpo.append(" from TipoMotivoReclamacao tmr ");
        corpo.append(" where 1 = 1 ");

        if (!count) {
            corpo.append(" order by tmr.tmrDescricao asc");
        }

        return instanciarQuery(session, corpo.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TMR_CODIGO,
                Columns.TMR_DESCRICAO
        };
    }
}
