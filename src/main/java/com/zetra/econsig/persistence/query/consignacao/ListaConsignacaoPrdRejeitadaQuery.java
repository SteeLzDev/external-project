package com.zetra.econsig.persistence.query.consignacao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoPrdRejeitadaQuery</p>
 * <p>Description: Listagem de consignações de uma natureza de um registro servidor que possuem parcelas rejeitadas</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoPrdRejeitadaQuery extends HQuery {

    public String rseCodigo;
    public String nseCodigo;
    public String csaCodigo;
    public List<String> adeCodigosRenegociacao;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        // Busca consignações abertas
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);
        sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
        // Para bloqueio específico para a CSA, considerar também consignações concluídas
        if (!TextHelper.isNull(csaCodigo)) {
            sadCodigos.add(CodedValues.SAD_CONCLUIDO);
        }

        corpoBuilder.append(" SELECT COUNT(*) ");
        corpoBuilder.append(" FROM AutDesconto ade ");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv ");
        corpoBuilder.append(" INNER JOIN cnv.servico svc ");
        corpoBuilder.append(" WHERE ade.statusAutorizacaoDesconto.sadCodigo IN ('").append(TextHelper.join(sadCodigos, "','")).append("') ");
        corpoBuilder.append(" AND ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        // E que possuam parcelas rejeitadas
        corpoBuilder.append(" AND EXISTS (SELECT 1 FROM ade.parcelaDescontoSet prd ");
        corpoBuilder.append(" WHERE prd.statusParcelaDesconto.spdCodigo = '").append(CodedValues.SPD_REJEITADAFOLHA).append("') ");

        if (!TextHelper.isNull(nseCodigo)) {
            corpoBuilder.append(" AND svc.naturezaServico.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (adeCodigosRenegociacao != null && !adeCodigosRenegociacao.isEmpty()) {
            List<String> codigos = new ArrayList<>(adeCodigosRenegociacao);
            codigos.add(CodedValues.NOT_EQUAL_KEY);
            corpoBuilder.append(" AND ade.adeCodigo ").append(criaClausulaNomeada("adeCodigos", codigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (adeCodigosRenegociacao != null && !adeCodigosRenegociacao.isEmpty()) {
            defineValorClausulaNomeada("adeCodigos", adeCodigosRenegociacao, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO
        };
    }
}
