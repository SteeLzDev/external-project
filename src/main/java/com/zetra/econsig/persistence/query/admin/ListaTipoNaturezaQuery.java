package com.zetra.econsig.persistence.query.admin;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTipoNaturezaQuery</p>
 * <p>Description: Listagem de Naturezas de Relacionamentos</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTipoNaturezaQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "tnt.tntCodigo, " +
                       "tnt.tntCseAltera, " +
                       "tnt.tntSupAltera, " +
                       "tnt.tntDescricao " +
                       "from TipoNatureza tnt";

        return instanciarQuery(session, corpo);
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
    			Columns.TNT_CODIGO,
                Columns.TNT_CSE_ALTERA,
                Columns.TNT_SUP_ALTERA,
                Columns.TNT_DESCRICAO
    	};
    }
}
