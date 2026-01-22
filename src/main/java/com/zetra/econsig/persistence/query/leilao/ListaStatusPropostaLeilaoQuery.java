package com.zetra.econsig.persistence.query.leilao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaStatusPropostaLeilaoQuery</p>
 * <p>Description: Listagem de Status de Proposta de Leilão de Solicitação</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date
 */
public class ListaStatusPropostaLeilaoQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
    	StringBuilder corpoBuilder = new StringBuilder();
    	corpoBuilder.append("select stp.stpCodigo, stp.stpDescricao from StatusProposta stp order by stp.stpDescricao");

        return instanciarQuery(session, corpoBuilder.toString());
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
                Columns.STP_CODIGO,
                Columns.STP_DESCRICAO
    	};
    }
}
