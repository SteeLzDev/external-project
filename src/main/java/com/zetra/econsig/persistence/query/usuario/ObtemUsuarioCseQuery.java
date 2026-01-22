package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p> Title: ObtemUsuarioCseQuery</p>
 * <p> Description: Listagem de usuários de consignante</p>
 * <p> Copyright: Copyright (c) 2002-2016</p>
 * <p> Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemUsuarioCseQuery extends HQuery {

	@Override
	public Query<Object[]> preparar(Session session) throws HQueryException {

		StringBuilder corpoBuilder = new StringBuilder();

		corpoBuilder.append("SELECT CASE WHEN usu.statusLogin.stuCodigo = '").append(CodedValues.STU_EXCLUIDO).append("' then usu.usuTipoBloq else usu.usuLogin end ");
		corpoBuilder.append("FROM Usuario usu ");
		corpoBuilder.append("INNER JOIN usu.usuarioCseSet usuarioCse ");

		Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

		return query;
	}

	@Override
	protected String[] getFields() {
		return new String[] { Columns.USU_LOGIN };
	}
}
