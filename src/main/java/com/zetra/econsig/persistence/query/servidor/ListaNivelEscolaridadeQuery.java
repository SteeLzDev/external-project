package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

public class ListaNivelEscolaridadeQuery extends HQuery{

    public String nesCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select nes.nesCodigo, nes.nesDescricao, nes.nesIdentificador from NivelEscolaridade nes " +
                (!TextHelper.isNull(nesCodigo) ? "where nes.nesCodigo " + criaClausulaNomeada("nesCodigo", nesCodigo) : "") +
                " order by nes.nesDescricao";

        Query<Object[]> query = instanciarQuery(session, corpo);
        if (!TextHelper.isNull(nesCodigo)) {
            defineValorClausulaNomeada("nesCodigo", nesCodigo, query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.NES_CODIGO,
                Columns.NES_DESCRICAO,
                Columns.NES_IDENTIFICADOR
        };
    }

}
