package com.zetra.econsig.persistence.query.cidade;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCidadeUfQuery</p>
 * <p>Description: Listagem de cidades e unidade federativa.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCidadeUfQuery extends HQuery {
    public String ufCod;
    public String termo;
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "select cid.cidCodigo, cid.cidNome, cid.uf.ufCod, cid.cidDdd, cid.cidCodigoIbge ";
        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append("from Cidade cid ");
        corpoBuilder.append("where 1=1 ");
        if(!TextHelper.isNull(ufCod)) {
            corpoBuilder.append(" and uf.ufCod ").append(criaClausulaNomeada("ufCod", ufCod));
        }
        if (!TextHelper.isNull(termo)) {
            corpoBuilder.append("and ").append(criaClausulaNomeada("cid.cidNome", "termo", termo + CodedValues.LIKE_MULTIPLO));
        }
        corpoBuilder.append("order by cid.cidNome ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if(!TextHelper.isNull(ufCod)) {
            defineValorClausulaNomeada("ufCod", ufCod, query);
        }

        if (!TextHelper.isNull(termo)) {
            defineValorClausulaNomeada("termo", termo + CodedValues.LIKE_MULTIPLO, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] { Columns.CID_CODIGO, Columns.CID_NOME, Columns.CID_UF_CODIGO, Columns.CID_DDD, Columns.CID_CODIGO_IBGE };
    }
}
