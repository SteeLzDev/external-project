package com.zetra.econsig.persistence.query.parametro;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarParamSvcCorQuery</p>
 * <p>Description: Lista parâmetros de serviços de um Correspondente.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarParamSvcCorQuery extends HQuery {
    public List<String> svcCodigos;
    public List<String> corCodigos;
    public List<String> tpsCodigos;
    public boolean ativo;
    public boolean dataIniVigIndiferente;
    public String psoVlr;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT " +
                       "pso.tipoParamSvc.tpsDescricao, " +
                       "pso.tipoParamSvc.tpsCodigo, " +
                       "pso.psoVlr, " +
                       "pso.psoVlrRef, " +
                       "pso.psoDataIniVig, " +
                       "pso.psoDataFimVig, " +
                       "pso.correspondente.corCodigo," +
                       "pso.servico.svcCodigo," +
                       "pso.psoCodigo";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM ParamSvcCorrespondente pso ");
        corpoBuilder.append(" WHERE 1 = 1 ");

        if (!TextHelper.isNull(psoVlr)) {
            corpoBuilder.append(" AND pso.psoVlr ").append(criaClausulaNomeada("psoVlr", psoVlr));
        }

        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            corpoBuilder.append(" AND pso.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigos));
        }

        if (corCodigos != null && !corCodigos.isEmpty()) {
            corpoBuilder.append(" AND pso.correspondente.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigos));
        }

        if (tpsCodigos != null && !tpsCodigos.isEmpty()) {
            corpoBuilder.append(" AND pso.tipoParamSvc.tpsCodigo ").append(criaClausulaNomeada("tpsCodigo", tpsCodigos));
        }

        if (ativo) {
            corpoBuilder.append(" AND pso.psoDataIniVig <= current_date() ");
            corpoBuilder.append(" AND (pso.psoDataFimVig >= current_date() ");
            corpoBuilder.append(" OR pso.psoDataFimVig IS NULL)");
            corpoBuilder.append(" ORDER BY pso.correspondente.corCodigo");

        } else {
            if (!dataIniVigIndiferente) {
                corpoBuilder.append(" AND pso.psoDataIniVig IS NULL");
            }
            corpoBuilder.append(" AND pso.psoDataFimVig IS NULL");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(psoVlr)) {
            defineValorClausulaNomeada("psoVlr", psoVlr, query);
        }

        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            defineValorClausulaNomeada("svcCodigo", svcCodigos, query);
        }

        if (corCodigos != null && !corCodigos.isEmpty()) {
            defineValorClausulaNomeada("corCodigo", corCodigos, query);
        }

        if (tpsCodigos != null && !tpsCodigos.isEmpty()) {
            defineValorClausulaNomeada("tpsCodigo", tpsCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TPS_DESCRICAO,
                Columns.TPS_CODIGO,
                Columns.PSO_VLR,
                Columns.PSO_VLR_REF,
                Columns.PSO_DATA_INI_VIG,
                Columns.PSO_DATA_FIM_VIG,
                Columns.PSO_COR_CODIGO,
                Columns.PSO_SVC_CODIGO,
                Columns.PSO_CODIGO
        };
    }
}
