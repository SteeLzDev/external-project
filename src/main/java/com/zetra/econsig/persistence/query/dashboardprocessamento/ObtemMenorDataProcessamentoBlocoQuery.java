package com.zetra.econsig.persistence.query.dashboardprocessamento;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemMenorDataProcessamentoBlocoQuery</p>
 * <p>Description: Retorna a primeira data de processamento de blocos.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemMenorDataProcessamentoBlocoQuery extends HQuery {

    public List<String> sbpCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        
        corpoBuilder.append("SELECT MIN(bpr.bprDataProcessamento) ");
        corpoBuilder.append("FROM BlocoProcessamento bpr ");
        corpoBuilder.append("WHERE 1=1 ");
        
        if (sbpCodigos != null && !sbpCodigos.isEmpty()) {
            corpoBuilder.append(" AND bpr.statusBlocoProcessamento.sbpCodigo ").append(criaClausulaNomeada("sbpCodigos", sbpCodigos));
        }
        
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (sbpCodigos != null && !sbpCodigos.isEmpty()) {
            defineValorClausulaNomeada("sbpCodigos", sbpCodigos, query);
        }
        
        return query;        
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.BPR_DATA_PROCESSAMENTO
        };
    }
}
