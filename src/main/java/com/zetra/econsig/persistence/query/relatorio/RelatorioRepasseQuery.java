package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;

import org.hibernate.Session;

/**
 * <p>Title: RelatorioRepasseQuery</p>
 * <p>Description: Query feita pela exigencia da classe HibernateDataSourceFactory.</p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioRepasseQuery extends ReportHQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        corpo.append(" SELECT 1 as CSA_IDENTIFICADOR, 1 as CSA_NOME, 1 as VALOR_APURADO, 1 as VALOR_TARIFACAO, 1 as VALOR_LIQUIDO, ");
        corpo.append(" 1 as ORDEM_EXTRA ");
        corpo.append(" FROM Consignante ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        return query;
    }


    @Override
    protected String[] getFields() {
        return new String[] {
                "CSA_IDENTIFICADOR",
                "CSA_NOME",
                "VALOR_APURADO",
                "VALOR_TARIFACAO",
                "VALOR_LIQUIDO",
                "ORDEM_EXTRA"
        };
    }
}