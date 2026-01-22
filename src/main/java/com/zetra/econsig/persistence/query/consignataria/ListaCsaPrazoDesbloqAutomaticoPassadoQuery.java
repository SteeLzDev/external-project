package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCsaPrazoDesbloqAutomaticoPassadoQuery</p>
 * <p>Description: Lista as consignatárias que tem prazo para desbloqueio automático passado.</p>
 * <p>Copyright: Copyright (c) 2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCsaPrazoDesbloqAutomaticoPassadoQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        Short csaInativo = CodedValues.STS_INATIVO;
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" select csa.csaCodigo ");
        corpoBuilder.append(" from Consignataria csa ");
        corpoBuilder.append(" where csa.csaAtivo ").append(criaClausulaNomeada("csaInativo", csaInativo));
        corpoBuilder.append(" and csa.csaDataDesbloqAutomatico <= current_date");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaInativo", csaInativo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String [] {
                Columns.CSA_CODIGO,
        };
    }
}
