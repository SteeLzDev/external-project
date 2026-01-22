package com.zetra.econsig.persistence.query.texto;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTextoSistemaQuery</p>
 * <p>Description: Listagem de texto sistema do Mobile.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaMobileTextoSistemaQuery extends HQuery {

    private final String texChave;
    private final Date texDataAlteracao;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder("SELECT tex.texChave, tex.texTexto, tex.texDataAlteracao");
        corpoBuilder.append(" FROM TextoSistema tex WHERE 1=1");
        if (!TextHelper.isNull(texChave)) {
            corpoBuilder.append(" AND tex.texChave ").append(criaClausulaNomeada("texChave", texChave));
        } else {
            corpoBuilder.append(" AND tex.texChave like 'mobile.%'");
        }
        if (!TextHelper.isNull(texDataAlteracao)) {
            corpoBuilder.append(" AND (tex.texDataAlteracao = NULL OR tex.texDataAlteracao > :texDataAlteracao)");
        }
        corpoBuilder.append(" ORDER BY tex.texChave");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(texChave)) {
            defineValorClausulaNomeada("texChave", texChave, query);
        }
        if (!TextHelper.isNull(texDataAlteracao)) {
            defineValorClausulaNomeada("texDataAlteracao", texDataAlteracao, query);
        }

        return query;
    }


    public ListaMobileTextoSistemaQuery(String texChave, Date texDataAlteracao) {
        super();
        this.texChave = texChave;
        this.texDataAlteracao = texDataAlteracao;
    }


    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TEX_CHAVE,
                Columns.TEX_TEXTO,
                Columns.TEX_DATA_ALTERACAO,
        };
    }
}
