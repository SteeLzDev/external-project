package com.zetra.econsig.persistence.query.proposta;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaPropostaPagamentoDividaQuery</p>
 * <p>Description: Lista de propostas de pagamento da dívida
 * cadastrada no saldo devedor de um contrato.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPropostaPagamentoDividaQuery extends HQuery {

    public String adeCodigo;
    public String csaCodigo;
    public String stpCodigo;
    public boolean arquivado = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select ");
        corpoBuilder.append(" ppd.autDesconto.adeCodigo, ");
        corpoBuilder.append(" ppd.ppdCodigo, ");
        corpoBuilder.append(" ppd.ppdNumero, ");
        corpoBuilder.append(" ppd.ppdValorDivida, ");
        corpoBuilder.append(" ppd.ppdValorParcela, ");
        corpoBuilder.append(" ppd.ppdPrazo, ");
        corpoBuilder.append(" ppd.ppdTaxaJuros, ");
        corpoBuilder.append(" ppd.ppdDataCadastro, ");
        corpoBuilder.append(" ppd.ppdDataValidade, ");
        corpoBuilder.append(" csa.csaCodigo, ");
        corpoBuilder.append(" csa.csaIdentificador, ");
        corpoBuilder.append(" csa.csaNome, ");
        corpoBuilder.append(" csa.csaNomeAbrev, ");
        corpoBuilder.append(" stp.stpCodigo, ");
        corpoBuilder.append(" stp.stpDescricao ");

        corpoBuilder.append(arquivado ? "from HtPropostaPagamentoDivida ppd " : "from PropostaPagamentoDivida ppd ");
        corpoBuilder.append(" inner join ppd.consignataria csa");
        corpoBuilder.append(" inner join ppd.statusProposta stp");

        corpoBuilder.append(" where ppd.autDesconto.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (!TextHelper.isNull(stpCodigo)) {
            corpoBuilder.append(" and stp.stpCodigo ").append(criaClausulaNomeada("stpCodigo", stpCodigo));
        }

        corpoBuilder.append(" order by ppd.ppdDataCadastro, ppd.ppdValorParcela desc");

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
                Columns.PPD_CODIGO,
                Columns.PPD_NUMERO,
                Columns.PPD_VALOR_DIVIDA,
                Columns.PPD_VALOR_PARCELA,
                Columns.PPD_PRAZO,
                Columns.PPD_TAXA_JUROS,
                Columns.PPD_DATA_CADASTRO,
                Columns.PPD_DATA_VALIDADE,
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.STP_CODIGO,
                Columns.STP_DESCRICAO
        };
    }
}
