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
 * <p>Title: ObtemTotalConsignatariaPorServidorQuery</p>
 * <p>Description: Retorna o total de consignatárias que possuem contratos ativos para o servidor.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalConsignatariaPorServidorQuery extends HQuery {

    public String rseCodigo;
    public String csaCodigo;
    public List<String> adeCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        // Conta com todos os status de contratos que não estão cancelados/liquidados/concluidos ou indeferidos.
        // Enquanto uma negociação de compra não estiver finalizada, ambas as pontas (Aguard. Liquidação e Aguard.
        // Confirmação) serão incluidas no limite de consignatárias.
        List<String> sadCodigos = new ArrayList<String>();
        sadCodigos.addAll(CodedValues.SAD_CODIGOS_ATIVOS_LIMITE);
        sadCodigos.addAll(CodedValues.SAD_CODIGOS_AGUARD_CONF);

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT COUNT(DISTINCT cnv.consignataria.csaCodigo) ");
        corpoBuilder.append(" FROM AutDesconto ade");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv");
        corpoBuilder.append(" WHERE ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(sadCodigos, "','")).append("')");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", CodedValues.NOT_EQUAL_KEY + csaCodigo));
        }

        if (adeCodigos != null && adeCodigos.size() > 0) {
            List<String> codigos = new ArrayList<String>(adeCodigos);
            codigos.add(CodedValues.NOT_EQUAL_KEY);
            corpoBuilder.append(" AND ade.adeCodigo ").append(criaClausulaNomeada("adeCodigos", codigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (adeCodigos != null && adeCodigos.size() > 0) {
            defineValorClausulaNomeada("adeCodigos", adeCodigos, query);
        }

        return query;
    }
}
