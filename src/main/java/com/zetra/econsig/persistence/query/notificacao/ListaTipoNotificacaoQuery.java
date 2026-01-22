package com.zetra.econsig.persistence.query.notificacao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTipoNotificacaoQuery</p>
 * <p>Description: Listagem de tipos de notificação</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTipoNotificacaoQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "tno.tnoCodigo, " +
                       "tno.tnoDescricao, " +
                       "tno.tnoEnvio " +
                       "from TipoNotificacao tno";

        return instanciarQuery(session, corpo);
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
    			Columns.TNO_CODIGO,
                Columns.TNO_DESCRICAO,
                Columns.TNO_ENVIO
    	};
    }
}
