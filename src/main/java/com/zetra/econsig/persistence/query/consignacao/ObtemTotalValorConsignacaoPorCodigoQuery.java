package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ObtemTotalValorConsignacaoPorCodigoQuery</p>
 * <p>Description: Totaliza o valor dos contratos pelos ADE_CODIGO informados.</p>
 * <p>Copyright: Copyright (c) 2002-2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalValorConsignacaoPorCodigoQuery extends HQuery {

    private final List<String> adeCodigos;

    public ObtemTotalValorConsignacaoPorCodigoQuery(List<String> adeCodigos) {
        this.adeCodigos = adeCodigos;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select sum(ade.adeVlr) ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("where ade.adeCodigo IN (:adeCodigos) ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigos", adeCodigos, query);

        return query;
    }
}
