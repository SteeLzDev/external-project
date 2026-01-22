package com.zetra.econsig.persistence.query.margem;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;

/**
 * <p>Title: ListaPenultimoPeriodoHistoricoMargemQuery</p>
 * <p>Description: Lista Penultimo Periodo Historico Margem Query.</p>
 * <p>Copyright: Copyright (c) 2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: 26246 $
 * $Date: 2021-03-02 09:27:49 -0200 (ter, 2 mar 2021) $
 */
public class ListaPenultimoPeriodoHistoricoMargemQuery extends HNativeQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" SELECT hma_periodo as PENULTIMO_PERIODO ");
        corpoBuilder.append(" FROM tb_historico_margem_folha hma ");
        corpoBuilder.append(" GROUP BY hma_periodo ORDER BY hma_periodo DESC");

        return instanciarQuery(session, corpoBuilder.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "PENULTIMO_PERIODO"
        };
    }
    
    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
