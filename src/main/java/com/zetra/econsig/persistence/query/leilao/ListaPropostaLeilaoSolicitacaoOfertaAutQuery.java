package com.zetra.econsig.persistence.query.leilao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: ListaPropostaLeilaoSolicitacaoOfertaAutQuery</p>
 * <p>Description: Lista de propostas de leilão de solicitação que possuem oferta
 * automática a serem efetuadas.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPropostaLeilaoSolicitacaoOfertaAutQuery extends HQuery {

    public String adeCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select ");
        corpoBuilder.append(" pls.plsCodigo, ");
        corpoBuilder.append(" ade.adeCodigo, ");
        corpoBuilder.append(" pls.consignataria.csaCodigo, ");
        corpoBuilder.append(" pls.servico.svcCodigo, ");
        corpoBuilder.append(" ade.adeVlrLiquido, ");
        corpoBuilder.append(" ade.adePrazo, ");
        corpoBuilder.append(" pls.plsOfertaAutDecremento, ");
        corpoBuilder.append(" pls.plsOfertaAutTaxaMin, ");
        corpoBuilder.append(" pls.plsOfertaAutEmail, ");
        corpoBuilder.append(" pls.plsTxtContatoCsa, ");
        corpoBuilder.append(" soa.soaDataValidade, ");
        corpoBuilder.append(" ade.registroServidor.rseCodigo ");

        corpoBuilder.append(" from PropostaLeilaoSolicitacao pls ");
        corpoBuilder.append(" inner join pls.autDesconto ade ");
        corpoBuilder.append(" inner join ade.solicitacaoAutorizacaoSet soa ");

        corpoBuilder.append(" where ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        corpoBuilder.append(" and soa.statusSolicitacao.ssoCodigo = '").append(StatusSolicitacaoEnum.PENDENTE.getCodigo()).append("'");
        corpoBuilder.append(" and soa.tipoSolicitacao.tisCodigo = '").append(TipoSolicitacaoEnum.SOLICITACAO_PROPOSTA_LEILAO_VIA_SIMULACAO.getCodigo()).append("'");

        corpoBuilder.append(" and pls.plsOfertaAutDecremento is not null ");
        corpoBuilder.append(" and pls.plsOfertaAutTaxaMin is not null ");

        // Traz apenas as ofertas automáticas que não sejam a melhor taxa
        corpoBuilder.append(" and pls.plsTaxaJuros > (");
        corpoBuilder.append("     select min(pls2.plsTaxaJuros) from PropostaLeilaoSolicitacao pls2");
        corpoBuilder.append("     where pls.autDesconto.adeCodigo = pls2.autDesconto.adeCodigo ");
        corpoBuilder.append("     and pls.consignataria.csaCodigo <> pls2.consignataria.csaCodigo ");
        corpoBuilder.append(" )");

        corpoBuilder.append(" order by pls.plsTaxaJuros desc");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String [] {
                Columns.PLS_CODIGO,
                Columns.ADE_CODIGO,
                Columns.CSA_CODIGO,
                Columns.SVC_CODIGO,
                Columns.ADE_VLR_LIQUIDO,
                Columns.ADE_PRAZO,
                Columns.PLS_OFERTA_AUT_DECREMENTO,
                Columns.PLS_OFERTA_AUT_TAXA_MIN,
                Columns.PLS_OFERTA_AUT_EMAIL,
                Columns.PLS_TXT_CONTATO_CSA,
                Columns.SOA_DATA_VALIDADE,
                Columns.RSE_CODIGO
        };
    }
}
