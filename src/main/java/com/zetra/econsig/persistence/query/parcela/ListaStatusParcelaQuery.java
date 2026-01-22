package com.zetra.econsig.persistence.query.parcela;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaStatusParcelaQuery</p>
 * <p>Description: Listagem de status de parcela</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaStatusParcelaQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "spd.spdCodigo," +
                       "spd.spdDescricao " +
                       "from StatusParcelaDesconto spd " +
                       "order by spd.spdDescricao";

        return instanciarQuery(session, corpo);
    }
    
    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SPD_CODIGO,
                Columns.SPD_DESCRICAO
        };
    }
}
