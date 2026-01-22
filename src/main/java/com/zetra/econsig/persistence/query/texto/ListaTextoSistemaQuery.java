package com.zetra.econsig.persistence.query.texto;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTextoSistemaQuery</p>
 * <p>Description: Listagem de texto sistema.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTextoSistemaQuery extends HQuery {

    public boolean count = false;

    public String texChave;

    public String texTexto;

    public String texDataAlteracao;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder("SELECT");
        if (count) {
            corpoBuilder.append(" COUNT(*)");
        } else {
            corpoBuilder.append(" tex.texChave, tex.texTexto, tex.texDataAlteracao");
        }
        corpoBuilder.append(" FROM TextoSistema tex WHERE 1=1");
        if (!TextHelper.isNull(texChave)) {
            corpoBuilder.append(" AND tex.texChave ").append(criaClausulaNomeada("texChave", texChave));
        }
        if (!TextHelper.isNull(texTexto)) {
            corpoBuilder.append(" AND tex.texTexto ").append(criaClausulaNomeada("texTexto", texTexto));
        }
        if (!TextHelper.isNull(texDataAlteracao)) {
            corpoBuilder.append(" AND tex.texDataAlteracao ").append(criaClausulaNomeada("texDataAlteracao", texDataAlteracao));
        }
        corpoBuilder.append(" ORDER BY tex.texChave");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(texChave)) {
            defineValorClausulaNomeada("texChave", texChave, query);
        }
        if (!TextHelper.isNull(texTexto)) {
            defineValorClausulaNomeada("texTexto", texTexto, query);
        }
        if (!TextHelper.isNull(texDataAlteracao)) {
            defineValorClausulaNomeada("texDataAlteracao", texDataAlteracao, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TEX_CHAVE,
                Columns.TEX_TEXTO,
                Columns.TEX_DATA_ALTERACAO
        };
    }
}
