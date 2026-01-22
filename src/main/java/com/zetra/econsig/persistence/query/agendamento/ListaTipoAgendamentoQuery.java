package com.zetra.econsig.persistence.query.agendamento;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTipoAgendamentoQuery</p>
 * <p>Description: Listagem de tipos de agendamento.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTipoAgendamentoQuery extends HQuery {
    
    private List<String> tagCodigos;
    
    public ListaTipoAgendamentoQuery(List<String> tagCodigos) {
        this.tagCodigos = tagCodigos;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder(); 
        corpo.append("select ");
        corpo.append("tag.tagCodigo, ");
        corpo.append("tag.tagDescricao ");
        corpo.append("from TipoAgendamento tag ");
        corpo.append("where 1 = 1 ");
        if (tagCodigos != null && !tagCodigos.isEmpty()) {
            corpo.append(" and tag.tagCodigo ").append(criaClausulaNomeada("tagCodigos", tagCodigos));
        }
        corpo.append(" order by tag.tagCodigo ASC");
        
        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (tagCodigos != null && !tagCodigos.isEmpty()) {
            defineValorClausulaNomeada("tagCodigos", tagCodigos, query);
        }
        
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TAG_CODIGO,
                Columns.TAG_DESCRICAO
                };
    }
}
