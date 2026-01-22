package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ContaServidorBloqueadoCnvQuery</p>
 * <p>Description: Conta quantos bloqueios servidores em convênio existem.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ContaServidorBloqueadoCnvQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select count(*) AS total ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from ParamConvenioRegistroSer pcr ");
        corpoBuilder.append(" WHERE pcr.tpsCodigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO).append("' ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "total"
        };
    }
}
