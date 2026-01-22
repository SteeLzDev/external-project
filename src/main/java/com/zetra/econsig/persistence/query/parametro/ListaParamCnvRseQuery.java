package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParamCnvRseQuery</p>
 * <p>Description: Listagem dos parâmetros de convenio do registro servidor.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParamCnvRseQuery extends HQuery {

    public boolean count = false;
    public String rseCodigo;
    public String cnvCodigo;
    public String tpsCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "SELECT " +
                "pcr.pcrVlr, " +
                "pcr.pcrObs, " +
                "pcr.rseCodigo, " +
                "pcr.cnvCodigo, " +
                "pcr.pcrVlrSer ";


        StringBuilder corpoBuilder = new StringBuilder();

        if (count) {
            corpoBuilder.append("SELECT COUNT(*) ");
        } else {
            corpoBuilder.append(corpo);
        }

        corpoBuilder.append(" FROM ParamConvenioRegistroSer pcr ");
        corpoBuilder.append(" WHERE 1=1 ");

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" AND pcr.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }

        if (!TextHelper.isNull(cnvCodigo)) {
            corpoBuilder.append(" AND pcr.cnvCodigo ").append(criaClausulaNomeada("cnvCodigo", cnvCodigo));
        }

        if (!TextHelper.isNull(tpsCodigo)) {
            corpoBuilder.append(" AND pcr.tpsCodigo ").append(criaClausulaNomeada("tpsCodigo", tpsCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (!TextHelper.isNull(cnvCodigo)) {
            defineValorClausulaNomeada("cnvCodigo", cnvCodigo, query);
        }

        if (!TextHelper.isNull(tpsCodigo)) {
            defineValorClausulaNomeada("tpsCodigo", tpsCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PCR_VLR,
                Columns.PCR_OBS,
                Columns.PCR_RSE_CODIGO,
                Columns.PCR_CNV_CODIGO,
                Columns.PCR_VLR_SER
        };
    }
}
