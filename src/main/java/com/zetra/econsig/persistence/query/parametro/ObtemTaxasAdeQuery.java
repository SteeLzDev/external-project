package com.zetra.econsig.persistence.query.parametro;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemTaxasAdeQuery</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTaxasAdeQuery extends HQuery {

    public String adeCodigo;
    public List<String> tpsCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT psc.pscCodigo, psc.tipoParamSvc.tpsCodigo, psc.pscVlr");
        corpoBuilder.append(" FROM AutDesconto ade");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv");
        corpoBuilder.append(" INNER JOIN cnv.servico svc");
        corpoBuilder.append(" INNER JOIN svc.paramSvcConsignatariaSet psc");

        corpoBuilder.append(" WHERE ");
        corpoBuilder.append("psc.consignataria.csaCodigo = cnv.consignataria.csaCodigo AND ");
        corpoBuilder.append("psc.pscDataIniVig <= current_date() AND (");
        corpoBuilder.append("psc.pscDataFimVig >= current_date() OR ");
        corpoBuilder.append("psc.pscDataFimVig IS NULL)");

        corpoBuilder.append(" AND ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        corpoBuilder.append(" AND psc.tipoParamSvc.tpsCodigo ").append(criaClausulaNomeada("tpsCodigo", tpsCodigos));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        defineValorClausulaNomeada("tpsCodigo", tpsCodigos, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String [] {
                Columns.PSC_CODIGO,
                Columns.PSC_TPS_CODIGO,
                Columns.PSC_VLR
        };
    }
}
