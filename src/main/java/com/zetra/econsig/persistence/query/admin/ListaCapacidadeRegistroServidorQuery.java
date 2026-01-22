package com.zetra.econsig.persistence.query.admin;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCapacidadeRegistroServidorQuery</p>
 * <p>Description: Listagem de Capacidade Civil de Registro Servidor</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCapacidadeRegistroServidorQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "cap.capCodigo, " +
                       "cap.capDescricao " +
                       "from CapacidadeRegistroSer cap";

        return instanciarQuery(session, corpo);
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
                Columns.CAP_CODIGO,
                Columns.CAP_DESCRICAO
    	};
    }
}
