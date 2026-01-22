package com.zetra.econsig.persistence.query.sdp.plano;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaNaturezaPlanoQuery</p>
 * <p>Description: Lista Naturezas de plano de desconto cadastradas.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaNaturezaPlanoQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT " +
        "npl.nplCodigo, " +
        "npl.nplDescricao " +
        "from NaturezaPlano npl " +
        "order by npl.nplCodigo";

        return instanciarQuery(session, corpo);
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.NPL_CODIGO,
                Columns.NPL_DESCRICAO
        };
    }

}
