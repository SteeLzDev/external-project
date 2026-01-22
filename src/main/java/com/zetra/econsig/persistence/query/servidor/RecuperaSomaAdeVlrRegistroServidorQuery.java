package com.zetra.econsig.persistence.query.servidor;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: RecuperaSomaAdeVlrRegistroServidorQuery</p>
 * <p>Description: Recupera a soma dos valores dos contratos de um servidor.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RecuperaSomaAdeVlrRegistroServidorQuery extends HQuery {

    private final String rseCodigo;
    private final List<String> svcCodigos;
    private final List<String> sadCodigos;

    public RecuperaSomaAdeVlrRegistroServidorQuery(String rseCodigo, List<String> svcCodigos, List<String> sadCodigos) {
        this.rseCodigo = rseCodigo;
        this.svcCodigos = svcCodigos;
        this.sadCodigos = sadCodigos;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT SUM(ade.adeVlr) AS TOTAL");

        corpoBuilder.append(" from AutDesconto ade");
        corpoBuilder.append(" inner join ade.verbaConvenio vco");
        corpoBuilder.append(" inner join vco.convenio cnv");
        corpoBuilder.append(" inner join ade.registroServidor rse");
        corpoBuilder.append(" WHERE rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            corpoBuilder.append(" AND cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigos));
        }
        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            defineValorClausulaNomeada("svcCodigo", svcCodigos, query);
        }

        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            defineValorClausulaNomeada("sadCodigo", sadCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "TOTAL"
        };
    }
}
