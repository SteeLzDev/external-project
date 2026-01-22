package com.zetra.econsig.persistence.query.admin;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaBancoQuery</p>
 * <p>Description: Listagem de Bancos</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaBancoQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo = "select " +
                       "bco.bcoCodigo, " +
                       "bco.bcoDescricao, " +
                       "bco.bcoIdentificador, " +
                       "bco.bcoAtivo " +
                       "from Banco bco " +
                       "where bco.bcoAtivo = true " +
                       "order by bco.bcoDescricao";

        return instanciarQuery(session, corpo);
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
    			Columns.BCO_CODIGO,
    			Columns.BCO_DESCRICAO,
    			Columns.BCO_IDENTIFICADOR,
    			Columns.BCO_ATIVO
    	};
    }
}
