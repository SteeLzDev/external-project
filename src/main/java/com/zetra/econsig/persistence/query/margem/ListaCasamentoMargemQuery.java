package com.zetra.econsig.persistence.query.margem;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCasamentoMargemQuery</p>
 * <p>Description: Listagem de configuração de casamento de margem.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCasamentoMargemQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" SELECT cam.camGrupo, cam.marCodigo, cam.camTipo, cam.camSequencia");
        corpoBuilder.append(" FROM CasamentoMargem cam ");
        corpoBuilder.append(" ORDER BY cam.camGrupo, cam.camSequencia");

        return instanciarQuery(session, corpoBuilder.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CAM_GRUPO,
                Columns.CAM_MAR_CODIGO,
                Columns.CAM_TIPO,
                Columns.CAM_SEQUENCIA
        };
    }  
}
