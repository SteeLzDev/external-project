package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParamSvcRseQuery</p>
 * <p>Description: Listagem dos parâmetros de servico do registro servidor.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParamSvcRseQuery extends HQuery {

    public String rseCodigo;
    public String svcCodigo;
    public String tpsCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "SELECT " +
                "psr.psrVlr, " +
                "psr.psrObs, " +
                "psr.rseCodigo, " +
                "psr.svcCodigo, " +
                "psr.psrAlteradoPeloServidor ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM ParamServicoRegistroSer psr ");
        corpoBuilder.append(" INNER JOIN psr.servico svc ");
        corpoBuilder.append(" INNER JOIN psr.tipoParamSvc tps ");
        corpoBuilder.append(" WHERE 1=1 ");

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" AND psr.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }

        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" AND svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        if (!TextHelper.isNull(tpsCodigo)) {
            corpoBuilder.append(" AND tps.tpsCodigo ").append(criaClausulaNomeada("tpsCodigo", tpsCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        if (!TextHelper.isNull(tpsCodigo)) {
            defineValorClausulaNomeada("tpsCodigo", tpsCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PSR_VLR,
                Columns.PSR_OBS,
                Columns.PSR_RSE_CODIGO,
                Columns.PSR_SVC_CODIGO,
                Columns.PSR_ALTERADO_PELO_SERVIDOR
        };
    }
}
