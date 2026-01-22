package com.zetra.econsig.persistence.query.consignacao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListaConsignacaoPrdSemRetornoQuery</p>
 * <p>Description: Listagem de consignações em que as parcelas do período não possuem retorno</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoPrdSemRetornoQuery extends HQuery {

    public String tipoEntidade;
    public String codigoEntidade;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        List<String> spdCodigos = new ArrayList<>();
        spdCodigos.add(CodedValues.SPD_EMPROCESSAMENTO);
        spdCodigos.add(CodedValues.SPD_REJEITADAFOLHA);
        spdCodigos.add(CodedValues.SPD_SEM_RETORNO);

        corpoBuilder.append(" SELECT DISTINCT prd.autDesconto.adeCodigo ");
        corpoBuilder.append(" FROM ParcelaDescontoPeriodo prd ");

        if (!TextHelper.isNull(tipoEntidade) && !TextHelper.isNull(codigoEntidade) && (tipoEntidade.equalsIgnoreCase("EST") || tipoEntidade.equalsIgnoreCase("ORG"))) {
            corpoBuilder.append(" INNER JOIN prd.autDesconto ade ");
            corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco ");
            corpoBuilder.append(" INNER JOIN vco.convenio cnv ");
            corpoBuilder.append(" INNER JOIN cnv.orgao org ");
        }

        corpoBuilder.append(" WHERE prd.statusParcelaDesconto.spdCodigo IN ('").append(TextHelper.join(spdCodigos, "','")).append("') ");

        if (!TextHelper.isNull(tipoEntidade) && !TextHelper.isNull(codigoEntidade)) {
            if (tipoEntidade.equalsIgnoreCase("EST")) {
                corpoBuilder.append(" AND org.estabelecimento.estCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            } else if (tipoEntidade.equalsIgnoreCase("ORG")) {
                corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            }
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(tipoEntidade) && !TextHelper.isNull(codigoEntidade) && (tipoEntidade.equalsIgnoreCase("EST") || tipoEntidade.equalsIgnoreCase("ORG"))) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        return query;
    }
}
