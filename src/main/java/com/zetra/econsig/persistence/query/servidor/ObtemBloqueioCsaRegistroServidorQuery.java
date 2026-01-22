package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemBloqueioNaturezaServicoRegistroServidor</p>
 * <p>Description: Retorna um Map com a contagem das consignatáriasque o servidor
 * possui um bloqueio cadastrado + a contagem das consignatárias que o servidor
 * não possui bloqueio.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemBloqueioCsaRegistroServidorQuery extends HQuery {

    public String rseCodigo;
    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select CASE COALESCE(TRIM(pcsa.prcVlr), 'D') WHEN 'D' THEN 'D' WHEN '' THEN 'D' ELSE 'B' END AS TIPO, COUNT(*) AS TOTAL ");
        corpoBuilder.append(" from Consignataria csa");
        corpoBuilder.append(" left outer join csa.paramCsaRegistroSerSet pcsa WITH ");
        corpoBuilder.append(" pcsa.tpaCodigo = '").append(CodedValues.TPA_QTD_CONTRATOS_POR_CSA).append("' and ");
        corpoBuilder.append(" pcsa.rseCodigo = :rseCodigo");
        corpoBuilder.append(" group by CASE COALESCE(TRIM(pcsa.prcVlr), 'D') WHEN 'D' THEN 'D' WHEN '' THEN 'D' ELSE 'B' END");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "TIPO",
                "TOTAL"
        };
    }
}
