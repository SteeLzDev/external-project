package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaLimiteMargemCsaOrgByCsaCodigoQuery</p>
 * <p>Description: Listagem de Órgãos e seus limites de margem</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author: junio $
 * $Revision: 7963 $
 * $Date: 2012-11-27 21:23:28 -0300 (ter, 27 nov 2012) $
 */
public class ListaLimiteMargemCsaOrgByCsaCodigoQuery extends HQuery {

    public String csaCodigo;

	@Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder sql = new StringBuilder(100);
        sql.append("SELECT lmc.orgCodigo, lmc.marCodigo, lmc.lmcValor ");
        sql.append("FROM LimiteMargemCsaOrg lmc ");
        sql.append("WHERE 1=1 ");
        if(!TextHelper.isNull(csaCodigo)) {
            sql.append("AND lmc.csaCodigo = :csaCodigo");
        }

        // Essa ordenação não pode ser alterada pelo inicio do orgCodigo por causa da lógica implementada para visualização da tela de editar do caso de uso de editar limite margem csa por órgão
        sql.append(" ORDER BY lmc.orgCodigo ");

        Query<Object[]> query = instanciarQuery(session, sql.toString());
        if(!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
    			Columns.LMC_ORG_CODIGO,
                Columns.LMC_MAR_CODIGO,
    			Columns.LMC_VALOR
    	};
    }
}
