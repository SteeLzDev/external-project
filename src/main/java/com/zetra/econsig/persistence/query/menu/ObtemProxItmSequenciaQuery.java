package com.zetra.econsig.persistence.query.menu;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemProxItmSequenciaQuery</p>
 * <p>Description: Obtem próximo código de sequência de item menu.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemProxItmSequenciaQuery extends HQuery {

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();        
        corpoBuilder.append("select max(itm.itmSequencia + 1) as ITM_SEQUENCIA from ItemMenu itm ");
        
        return instanciarQuery(session, corpoBuilder.toString());
    }
    
    protected String[] getFields() {        
        return new String[] {
                Columns.ITM_SEQUENCIA
        };
    }
}
