package com.zetra.econsig.persistence.query.seguranca;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaNivelSegurancaQuery</p>
 * <p>Description: Lista os níveis de segurança.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaNivelSegurancaQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT nsg.nsgCodigo, nsg.nsgDescricao ");
        corpoBuilder.append("FROM NivelSeguranca nsg ");
        corpoBuilder.append("ORDER BY 1");
        
        return instanciarQuery(session, corpoBuilder.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.NSG_CODIGO,
                Columns.NSG_DESCRICAO
        };
    }
}
