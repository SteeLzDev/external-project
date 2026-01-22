package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaEstadoCivilQuery</p>
 * <p>Description: Listagem de estado civil.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaEstadoCivilQuery extends HQuery {

    public String estCvlCodigo;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select estCvlCodigo, estCvlDescricao from EstadoCivil " +
                (!TextHelper.isNull(estCvlCodigo) ? "where estCvlCodigo " + criaClausulaNomeada("estCvlCodigo", estCvlCodigo) : "") +
                " order by estCvlDescricao";

        Query<Object[]> query = instanciarQuery(session, corpo);
        if (!TextHelper.isNull(estCvlCodigo)) {
            defineValorClausulaNomeada("estCvlCodigo", estCvlCodigo, query);
        }
        return query;
    }
    
    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.EST_CIVIL_CODIGO,
                Columns.EST_CIVIL_DESCRICAO
        };
    }
}
