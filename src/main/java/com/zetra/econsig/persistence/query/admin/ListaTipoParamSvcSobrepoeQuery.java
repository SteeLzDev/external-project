package com.zetra.econsig.persistence.query.admin;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTipoParamSvcQuery</p>
 * <p>Description: Listagem de Tipos de Parâmetros de Serviço</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 26246 $
 * $Date: 2019-02-14 09:27:49 -0200 (qui, 14 fev 2019) $
 */
public class ListaTipoParamSvcSobrepoeQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "tps.tpsCodigo, " +
                       "tps.tpsDescricao, " +
                       "tps.tpsCseAltera, " +
                       "tps.tpsCsaAltera, " +
                       "tps.tpsSupAltera, " +
                       "tps.tpsPodeSobreporRse " +
                       "from TipoParamSvc tps " +
                       "where tps.tpsPodeSobreporRse =" + "'" + CodedValues.TPC_SIM + "'";

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
