package com.zetra.econsig.persistence.query.leilao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemMaiorNumeroPropostaLeilaoQuery</p>
 * <p>Description: Obtém o maior número de proposta de um leilão.</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemMaiorNumeroPropostaLeilaoQuery extends HQuery {

    public String adeCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select coalesce(max(pls.plsNumero), 0)");
        corpoBuilder.append(" from PropostaLeilaoSolicitacao pls");
        corpoBuilder.append(" where pls.autDesconto.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String [] {
                Columns.PLS_NUMERO
        };
    }
}
