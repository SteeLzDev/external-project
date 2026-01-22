package com.zetra.econsig.persistence.query.basecalc;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTipoBaseCalculoQuery</p>
 * <p>Description: Lista tipos de base de cálculos disponíveis.</p>
 * <p>Copyright: Copyright (c) 2002-20014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTipoBaseCalculoQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "tbc.tbcCodigo, " +
                       "tbc.tbcDescricao " +
                       "from TipoBaseCalculo tbc " +
                       "order by tbc.tbcDescricao";

        return instanciarQuery(session, corpo);
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TBC_CODIGO,
                Columns.TBC_DESCRICAO
        };
    }
}
