package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParamCsaRseQuery</p>
 * <p>Description: Listagem dos parâmetros de consignatária do registro servidor.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParamCsaRseQuery extends HQuery {

    public String rseCodigo;
    public String csaCodigo;
    public String tpaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "SELECT " +
                "prc.prcVlr, " +
                "prc.prcObs, " +
                "prc.rseCodigo ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM ParamConsignatariaRegistroSer prc ");
        corpoBuilder.append(" INNER JOIN prc.registroServidor rse ");
        corpoBuilder.append(" INNER JOIN prc.consignataria csa ");
        corpoBuilder.append(" INNER JOIN prc.tipoParamConsignataria tpa ");
        corpoBuilder.append(" WHERE 1=1 ");

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" AND prc.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(tpaCodigo)) {
            corpoBuilder.append(" AND tpa.tpaCodigo ").append(criaClausulaNomeada("tpaCodigo", tpaCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(tpaCodigo)) {
            defineValorClausulaNomeada("tpaCodigo", tpaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PRC_VLR,
                Columns.PRC_OBS,
                Columns.PRC_RSE_CODIGO
        };
    }
}
