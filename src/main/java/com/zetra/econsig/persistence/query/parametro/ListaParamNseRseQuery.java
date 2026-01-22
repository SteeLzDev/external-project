package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParamNseRseQuery</p>
 * <p>Description: Listagem dos parâmetros de natureza de servico do registro servidor.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParamNseRseQuery extends HQuery {

    public String rseCodigo;
    public String nseCodigo;
    public String tpsCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "SELECT " +
                "pnr.pnrVlr, " +
                "pnr.pnrObs, " +
                "pnr.rseCodigo, " +
                "pnr.nseCodigo, " +
                "pnr.pnrAlteradoPeloServidor ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM ParamNseRegistroSer pnr ");
        corpoBuilder.append(" INNER JOIN pnr.naturezaServico nse ");
        corpoBuilder.append(" INNER JOIN pnr.tipoParamSvc tps ");
        corpoBuilder.append(" WHERE 1=1 ");

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" AND pnr.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }

        if (!TextHelper.isNull(nseCodigo)) {
            corpoBuilder.append(" AND nse.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }

        if (!TextHelper.isNull(tpsCodigo)) {
            corpoBuilder.append(" AND tps.tpsCodigo ").append(criaClausulaNomeada("tpsCodigo", tpsCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        if (!TextHelper.isNull(tpsCodigo)) {
            defineValorClausulaNomeada("tpsCodigo", tpsCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PNR_VLR,
                Columns.PNR_OBS,
                Columns.PNR_RSE_CODIGO,
                Columns.PNR_NSE_CODIGO,
                Columns.PNR_ALTERADO_PELO_SERVIDOR
        };
    }
}
