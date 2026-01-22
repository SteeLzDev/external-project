package com.zetra.econsig.persistence.query.log;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTipoEntidadeLogQuery</p>
 * <p>Description: Listagem de Tipos Entidade de Log</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTipoEntidadeLogQuery extends HQuery {
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "ten.tenCodigo, " +
                       "ten.tenDescricao, " +
                       "ten.tenTitulo " +
                       "from TipoEntidade ten " +
                       "where ten.tenAuditoria = 1 " +
                       "order by ten.tenDescricao";
        
        return instanciarQuery(session, corpo);
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
    			Columns.TEN_CODIGO,
                Columns.TEN_DESCRICAO,
                Columns.TEN_TITULO
    	};
    }
}
