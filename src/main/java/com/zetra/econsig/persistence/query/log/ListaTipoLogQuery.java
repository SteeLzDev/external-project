package com.zetra.econsig.persistence.query.log;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTipoLogQuery</p>
 * <p>Description: Listagem de Tipos de Log</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTipoLogQuery extends HQuery {
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "tlo.tloCodigo, " +
                       "tlo.tloDescricao " +
                       "from TipoLog tlo " +
                       "order by tlo.tloDescricao";
        
        return instanciarQuery(session, corpo);
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
    			Columns.TLO_CODIGO,
                Columns.TLO_DESCRICAO
    	};
    }
}
