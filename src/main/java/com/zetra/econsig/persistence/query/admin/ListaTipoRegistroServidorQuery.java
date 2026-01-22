package com.zetra.econsig.persistence.query.admin;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTipoRegistroServidorQuery</p>
 * <p>Description: Listagem de Tipo de Registro Servidor</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTipoRegistroServidorQuery extends HQuery {
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "trs.trsCodigo, " +
                       "trs.trsDescricao " +
                       "from TipoRegistroServidor trs";
        
        return instanciarQuery(session, corpo);
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
                Columns.TRS_CODIGO,
                Columns.TRS_DESCRICAO
    	};
    }
}
