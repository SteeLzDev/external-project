package com.zetra.econsig.persistence.query.margem;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaMargemComServicoAtivoQuery</p>
 * <p>Description: Listagem margens que tenham serviço ativo no sistema.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaMargemComServicoAtivoQuery extends HQuery {

	@Override
	protected Query<Object[]> preparar(Session session) throws HQueryException {
		final String corpo = "SELECT "
                + "pse.pseVlr "
                + "from ParamSvcConsignante pse "
                + "inner join pse.servico svc  "
                + "where svc.svcAtivo = " + CodedValues.STS_ATIVO + " "
                + "and pse.tipoParamSvc.tpsCodigo = '" + CodedValues.TPS_INCIDE_MARGEM + "' "
                + "group by pse.pseVlr";


		return instanciarQuery(session, corpo);
	}

	@Override
	protected String[] getFields() {
		return new String[] {
				Columns.MAR_CODIGO
		};
	}

}
