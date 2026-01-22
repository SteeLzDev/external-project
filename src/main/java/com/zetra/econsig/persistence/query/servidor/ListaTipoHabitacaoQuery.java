package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

public class ListaTipoHabitacaoQuery extends HQuery{

    public String thaCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select tha.thaCodigo, tha.thaDescricao, tha.thaIdentificador from TipoHabitacao tha " +
                (!TextHelper.isNull(thaCodigo) ? "where tha.thaCodigo " + criaClausulaNomeada("thaCodigo", thaCodigo) : "") +
                " order by tha.thaCodigo";

        Query<Object[]> query = instanciarQuery(session, corpo);
        if (!TextHelper.isNull(thaCodigo)) {
            defineValorClausulaNomeada("thaCodigo", thaCodigo, query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.THA_CODIGO,
                Columns.THA_DESCRICAO,
                Columns.THA_IDENTIFICADOR
        };
    }
}
