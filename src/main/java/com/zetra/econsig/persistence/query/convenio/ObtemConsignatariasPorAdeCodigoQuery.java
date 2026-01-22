package com.zetra.econsig.persistence.query.convenio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ObtemConsignatariasPorAdeCodigoQuery</p>
 * <p>Description: Lista de Consignatárias distintas ligadas aos contratos dados.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemConsignatariasPorAdeCodigoQuery extends HQuery {

    public List<String> adeCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT distinct(cnv.consignataria.csaCodigo) as CONSIGNATARIAS";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Convenio cnv ");
        corpoBuilder.append(" inner join cnv.verbaConvenioSet vco ");
        corpoBuilder.append(" inner join vco.autDescontoSet ade ");

        corpoBuilder.append(" WHERE ade.adeCodigo ").append(criaClausulaNomeada("adeCodigos", adeCodigos));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigos", adeCodigos, query);

        return query;
    }

    @Override
    protected String[] getFields() {

        return new String[] {
                "CONSIGNATARIAS"
        };
    }

}
