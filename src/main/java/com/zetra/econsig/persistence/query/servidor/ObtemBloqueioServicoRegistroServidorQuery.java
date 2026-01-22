package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemBloqueioServicoRegistroServidorQuery</p>
 * <p>Description: Retorna um Map com a contagem dos serviços que o servidor
 * possui um bloqueio cadastrado + a contagem dos serviços que o servidor
 * não possui bloqueio.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemBloqueioServicoRegistroServidorQuery extends HQuery {

    public String rseCodigo;
    public String svcCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select CASE WHEN NULLIF(TRIM(psr.psrVlr), '') IS NULL THEN 'D' ELSE 'B' END AS TIPO, COUNT(*) AS TOTAL ");
        corpoBuilder.append(" from Servico svc");
        corpoBuilder.append(" left outer join svc.paramServicoRegistroSerSet psr WITH ");
        corpoBuilder.append(" psr.tpsCodigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO).append("' and ");
        corpoBuilder.append(" psr.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" where svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        corpoBuilder.append(" group by CASE WHEN NULLIF(TRIM(psr.psrVlr), '') IS NULL THEN 'D' ELSE 'B' END");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        return query;
    }

    @Override
    public String[] getFields() {
        return  new String[] {"TIPO", "TOTAL"};
    }
}
