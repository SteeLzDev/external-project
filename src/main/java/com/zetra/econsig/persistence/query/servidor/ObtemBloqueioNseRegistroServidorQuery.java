package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemBloqueioNaturezaServicoRegistroServidor</p>
 * <p>Description: Retorna um Map com a contagem das naturezas de serviço que o servidor
 * possui um bloqueio cadastrado + a contagem das naturezas de serviço que o servidor
 * não possui bloqueio.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemBloqueioNseRegistroServidorQuery extends HQuery {

    public String rseCodigo;
    public String nseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select CASE COALESCE(TRIM(pnr.pnrVlr), 'D') WHEN 'D' THEN 'D' WHEN '' THEN 'D' ELSE 'B' END AS TIPO, COUNT(*) AS TOTAL ");
        corpoBuilder.append(" from NaturezaServico nse");
        corpoBuilder.append(" left outer join nse.paramNseRegistroSerSet pnr WITH ");
        corpoBuilder.append(" pnr.tpsCodigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO).append("' and ");
        corpoBuilder.append(" pnr.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        if (!TextHelper.isNull(nseCodigo)) {
            corpoBuilder.append(" where nse.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }

        corpoBuilder.append(" group by CASE COALESCE(TRIM(pnr.pnrVlr), 'D') WHEN 'D' THEN 'D' WHEN '' THEN 'D' ELSE 'B' END");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        return query;
    }
}
