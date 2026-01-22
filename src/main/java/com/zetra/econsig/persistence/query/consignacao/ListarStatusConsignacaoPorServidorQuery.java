package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarStatusConsignacaoPorServidorQuery</p>
 * <p>Description: Listagem de status por consignações do servidor</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarStatusConsignacaoPorServidorQuery extends HQuery {

    public String rseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        
        StringBuilder builder = new StringBuilder();
        
        builder.append("select sad.sadCodigo, sad.sadDescricao, sad.sadSequencia from AutDesconto ade ");
        builder.append("join ade.statusAutorizacaoDesconto sad ");
        
        builder.append("where ade.registroServidor.rseCodigo").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        
        builder.append("group by sad.sadCodigo, sad.sadDescricao, sad.sadSequencia");
        
        Query<Object[]> query = instanciarQuery(session, builder.toString());
        
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        
        return query;

        
    }

    @Override
    protected String[] getFields() {
        return new String[] {
            Columns.SAD_CODIGO,
            Columns.SAD_DESCRICAO,
            Columns.SAD_SEQUENCIA    
        };
    }
}
