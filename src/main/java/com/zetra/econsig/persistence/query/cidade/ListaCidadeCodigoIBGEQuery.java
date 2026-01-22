package com.zetra.econsig.persistence.query.cidade;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCidadeCodigoIBGEQuery</p>
 * <p>Description: Listagem de cidades e unidade federativa com filtro do codigo ibge</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCidadeCodigoIBGEQuery extends HQuery {
    public String cidCodigoIbge;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "select cid.cidCodigo, cid.cidNome, cid.uf.ufCod, cid.cidDdd, cid.cidCodigoIbge ";
        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append("from Cidade cid ");
        corpoBuilder.append("where 1=1 ");

        if(!TextHelper.isNull(cidCodigoIbge)) {
            corpoBuilder.append(" and cid.cidCodigoIbge ").append(criaClausulaNomeada("cidCodigoIbge", cidCodigoIbge));
        }

        corpoBuilder.append("order by cid.cidNome ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if(!TextHelper.isNull(cidCodigoIbge)) {
            defineValorClausulaNomeada("cidCodigoIbge", cidCodigoIbge, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] { Columns.CID_CODIGO, Columns.CID_NOME, Columns.CID_UF_CODIGO, Columns.CID_DDD, Columns.CID_CODIGO_IBGE };
    }
}