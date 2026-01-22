package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaStatusConsignacaoQuery</p>
 * <p>Description: Listagem de status de consignação</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaStatusConsignacaoQuery extends HQuery {

    public List<String> sadCodigos;
    public boolean filtraApenasSadExibeSim = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        corpo.append("select ");
        corpo.append("sad.sadCodigo,");
        corpo.append("sad.sadDescricao ");
        corpo.append("from StatusAutorizacaoDesconto sad ");
        corpo.append("where 1 = 1 ");

        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            corpo.append(" and sad.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        }

        if(filtraApenasSadExibeSim) {
            corpo.append(" and sad.sadExibe = 'S'");
        }

        corpo.append(" order by sad.sadSequencia, sad.sadDescricao ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SAD_CODIGO,
                Columns.SAD_DESCRICAO
        };
    }
}
