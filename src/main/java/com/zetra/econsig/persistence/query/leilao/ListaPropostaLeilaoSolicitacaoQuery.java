package com.zetra.econsig.persistence.query.leilao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaPropostaLeilaoSolicitacaoQuery</p>
 * <p>Description: Lista de propostas de leilão de solicitação.</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPropostaLeilaoSolicitacaoQuery extends HQuery {

    public String adeCodigo;
    public String csaCodigo;
    public String stpCodigo;
    public boolean arquivado = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select ");
        corpoBuilder.append(" pls.autDesconto.adeCodigo, ");
        corpoBuilder.append(" pls.plsCodigo, ");
        corpoBuilder.append(" pls.plsNumero, ");
        corpoBuilder.append(" pls.plsValorLiberado, ");
        corpoBuilder.append(" pls.plsValorParcela, ");
        corpoBuilder.append(" pls.plsPrazo, ");
        corpoBuilder.append(" pls.plsTaxaJuros, ");
        corpoBuilder.append(" pls.plsDataCadastro, ");
        corpoBuilder.append(" pls.plsDataValidade, ");
        corpoBuilder.append(" pls.plsOfertaAutDecremento, ");
        corpoBuilder.append(" pls.plsOfertaAutTaxaMin, ");
        corpoBuilder.append(" pls.plsOfertaAutEmail, ");
        corpoBuilder.append(" pls.plsTxtContatoCsa, ");
        corpoBuilder.append(" csa.csaCodigo, ");
        corpoBuilder.append(" csa.csaIdentificador, ");
        corpoBuilder.append(" csa.csaNome, ");
        corpoBuilder.append(" csa.csaNomeAbrev, ");
        corpoBuilder.append(" pls.servico.svcCodigo, ");
        corpoBuilder.append(" stp.stpCodigo, ");
        corpoBuilder.append(" stp.stpDescricao ");

        corpoBuilder.append(arquivado ? "from HtPropostaLeilaoSolicitacao pls " : "from PropostaLeilaoSolicitacao pls ");
        corpoBuilder.append(" inner join pls.consignataria csa");
        corpoBuilder.append(" inner join pls.statusProposta stp");

        corpoBuilder.append(" where pls.autDesconto.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (!TextHelper.isNull(stpCodigo)) {
            corpoBuilder.append(" and stp.stpCodigo ").append(criaClausulaNomeada("stpCodigo", stpCodigo));
        }

        corpoBuilder.append(" order by pls.plsNumero DESC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (!TextHelper.isNull(stpCodigo)) {
            defineValorClausulaNomeada("stpCodigo", stpCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String [] {
                Columns.ADE_CODIGO,
                Columns.PLS_CODIGO,
                Columns.PLS_NUMERO,
                Columns.PLS_VALOR_LIBERADO,
                Columns.PLS_VALOR_PARCELA,
                Columns.PLS_PRAZO,
                Columns.PLS_TAXA_JUROS,
                Columns.PLS_DATA_CADASTRO,
                Columns.PLS_DATA_VALIDADE,
                Columns.PLS_OFERTA_AUT_DECREMENTO,
                Columns.PLS_OFERTA_AUT_TAXA_MIN,
                Columns.PLS_OFERTA_AUT_EMAIL,
                Columns.PLS_TXT_CONTATO_CSA,
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.SVC_CODIGO,
                Columns.STP_CODIGO,
                Columns.STP_DESCRICAO
        };
    }
}
