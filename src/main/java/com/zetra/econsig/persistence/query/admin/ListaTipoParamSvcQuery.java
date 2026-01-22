package com.zetra.econsig.persistence.query.admin;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTipoParamSvcQuery</p>
 * <p>Description: Listagem de Tipos de Parâmetros de Serviço</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTipoParamSvcQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "tps.tpsCodigo, " +
                       "tps.tpsDescricao, " +
                       "tps.tpsCseAltera, " +
                       "tps.tpsCsaAltera, " +
                       "tps.tpsSupAltera, " +
                       "tps.tpsPodeSobreporRse " +
                       "from TipoParamSvc tps";

        return instanciarQuery(session, corpo);
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
    			Columns.TPS_CODIGO,
                Columns.TPS_DESCRICAO,
                Columns.TPS_CSE_ALTERA,
                Columns.TPS_CSA_ALTERA,
                Columns.TPS_SUP_ALTERA,
                Columns.TPS_PODE_SOBREPOR_RSE
    	};
    }
}
