package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemValorTotalContratosAtivosRsePorMargemQuery</p>
 * <p>Description: Retorna a soma de todos ativos do registro servidor que incidem em determinada margem.</p>
 * <p>Copyright: Copyright (c) 2025</p>
 */
public class ObtemValorTotalContratosAtivosRsePorMargemQuery extends HQuery{

    public String rseCodigo;
    public Short marCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT ");
        corpoBuilder.append("SUM(CASE WHEN COALESCE(pse4.pseVlr, 'F') = 'F' THEN ade.adeVlr ");
        corpoBuilder.append("         WHEN COALESCE(pse4.pseVlr, 'P') = 'P' THEN COALESCE(ade.adeVlrFolha, 0) ");
        corpoBuilder.append("         ELSE 0 END");
        corpoBuilder.append(") AS soma_compulsorios ");
        corpoBuilder.append("FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.servico svc ");
        corpoBuilder.append("LEFT JOIN svc.paramSvcConsignanteSet pse4 WITH pse4.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_TIPO_VLR).append("' ");
        corpoBuilder.append("WHERE ade.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "','")).append("') ");
        corpoBuilder.append("AND ade.adeIncMargem = :marCodigo ");
        corpoBuilder.append("AND ade.rseCodigo = :rseCodigo ");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("marCodigo", marCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {"total"};
    }
}
