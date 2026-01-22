package com.zetra.econsig.persistence.query.parametro;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemMaxMinVlrParamSvcCseNseQuery</p>
 * <p>Description: Obtém o valor máximo ou mínimo numérico de um parâmetro de serviço geral de uma natureza de serviço</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemMaxMinVlrParamSvcCseNseQuery extends HQuery {

    public List<String> tpsCodigos;
    public String nseCodigo;
    public boolean buscaMin;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT pse.tipoParamSvc.tpsCodigo,").append(buscaMin ? "min(" : "max(").append("to_decimal_ne(pse.pseVlr, 10, 2)) ");
        corpoBuilder.append("FROM ParamSvcConsignante pse ");
        corpoBuilder.append("INNER JOIN pse.servico svc ");

        corpoBuilder.append("WHERE pse.tipoParamSvc.tpsCodigo ").append(criaClausulaNomeada("tpsCodigo", tpsCodigos));
        corpoBuilder.append(" AND svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        corpoBuilder.append(" AND NULLIF(TRIM(pse.pseVlr), '') IS NOT NULL");

        corpoBuilder.append(" GROUP BY pse.tipoParamSvc.tpsCodigo");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("tpsCodigo", tpsCodigos, query);
        defineValorClausulaNomeada("nseCodigo", nseCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String [] {
                Columns.PSE_TPS_CODIGO,
                Columns.PSE_VLR
        };
    }

}
