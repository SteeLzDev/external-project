package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaBloqueioNaturezaServicoServidorQuery</p>
 * <p>Description: Listagem de naturezas de serviço do servidor, juntamente com os
 * bloqueios de natureza de serviço, caso existam.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaBloqueioNaturezaServicoServidorQuery extends HQuery {

    public String rseCodigo;
    public String nseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                "nse.nseCodigo, " +
                "nse.nseDescricao, " +
                "pnr.pnrVlr, " +
                "pnr.pnrObs, " +
                "pnr.pnrAlteradoPeloServidor ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from NaturezaServico nse ");
        corpoBuilder.append(" left outer join nse.paramNseRegistroSerSet pnr WITH ");
        corpoBuilder.append(" pnr.tpsCodigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO).append("' and ");
        corpoBuilder.append(" pnr.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        if (!TextHelper.isNull(nseCodigo)) {
            corpoBuilder.append(" where nse.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }

        corpoBuilder.append(" order by nse.nseDescricao");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.NSE_CODIGO,
                Columns.NSE_DESCRICAO,
                Columns.PNR_VLR,
                Columns.PNR_OBS,
                Columns.PNR_ALTERADO_PELO_SERVIDOR
        };
    }
}
