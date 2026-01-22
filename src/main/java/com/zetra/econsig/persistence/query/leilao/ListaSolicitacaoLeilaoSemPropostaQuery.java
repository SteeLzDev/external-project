package com.zetra.econsig.persistence.query.leilao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: ListaSolicitacaoLeilaoSemPropostaQuery</p>
 * <p>Description: Lista solicitação de leilões que só possuem a proposta inicial.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaSolicitacaoLeilaoSemPropostaQuery extends HQuery {

    public String adeCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ade.adeCodigo");
        corpoBuilder.append(" FROM AutDesconto ade");
        corpoBuilder.append(" INNER JOIN ade.solicitacaoAutorizacaoSet soa");
        corpoBuilder.append(" INNER JOIN ade.propostaLeilaoSolicitacaoSet pls");
        corpoBuilder.append(" INNER JOIN ade.coeficienteDescontoSet cde");
        corpoBuilder.append(" INNER JOIN cde.coeficiente cft");
        corpoBuilder.append(" WHERE soa.tipoSolicitacao.tisCodigo = '").append(TipoSolicitacaoEnum.SOLICITACAO_PROPOSTA_LEILAO_VIA_SIMULACAO.getCodigo()).append("'");
        corpoBuilder.append(" AND soa.statusSolicitacao.ssoCodigo = '").append(StatusSolicitacaoEnum.PENDENTE.getCodigo()).append("'");
        corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_SOLICITADO).append("'");

        // É a proposta inicial com a mesma taxa da consignação
        corpoBuilder.append(" AND pls.plsNumero = 1");
        corpoBuilder.append(" AND pls.plsTaxaJuros = cft.cftVlr");

        // Não tem outra proposta que não a inicial
        corpoBuilder.append(" AND NOT EXISTS (SELECT 1 FROM PropostaLeilaoSolicitacao pls2 WHERE pls2.autDesconto.adeCodigo = ade.adeCodigo AND pls2.plsCodigo <> pls.plsCodigo)");

        if (!TextHelper.isNull(adeCodigo)) {
            corpoBuilder.append(" AND ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(adeCodigo)) {
            defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
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
