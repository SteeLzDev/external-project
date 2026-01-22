package com.zetra.econsig.persistence.query.cep;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCepQuery</p>
 * <p>Description: Listagem de cep do Brasil.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 26246 $
 * $Date: 2019-02-14 09:27:49 -0200 (qui, 14 fev 2019) $
 */

public class ListaCepQuery extends HQuery {

    public String cepCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select cep.cepCodigo, cep.cepLogradouro, cep.cepBairro, cep.cepCidade, cep.cepEstado, cep.cepEstadoSigla");
        corpoBuilder.append(" from Cep cep ");
        corpoBuilder.append(" where cep.cepCodigo = :cepCodigo ");
        corpoBuilder.append(" order by cep.cepCodigo");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("cepCodigo", (TextHelper.isNull(cepCodigo) ? "N/D" : cepCodigo), query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CEP_CODIGO,
                Columns.CEP_LOGRADOURO,
                Columns.CEP_BAIRRO,
                Columns.CEP_CIDADE,
                Columns.CEP_ESTADO,
                Columns.CEP_ESTADO_SIGLA
        };
    }
}
