package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoPorRseCnvQuery</p>
 * <p>Description: Listagem de Consignações de um registro servidor em um ou vários convênios</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 * @param <E>
 */
public class ListaConsignacaoPorRseCnvQuery extends HQuery {

    public String rseCodigo;
    public String cnvCodigo;
    public List<String> sadCodigos;
    public String adePeriodicidade;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                "ade.adeCodigo, " +
                "ade.adeNumero, " +
                "ade.adeVlr, " +
                "ade.adeData, " +
                "ade.adeAnoMesIni, " +
                "ade.adeAnoMesFim, " +
                "ade.adePrazo, " +
                "ade.adePrdPagas, " +
                "ade.adeCodReg, " +
                "ade.adeIndice, " +
                "ade.adePeriodicidade, " +
                "ade.statusAutorizacaoDesconto.sadCodigo, " +
                "vco.convenio.cnvCodigo, " +
                "prd.prdNumero, " +
                "ade.adeIncMargem, " +
                "ade.adeIdentificador, " +
                "ade.adeVlrLiquido ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("left outer join ade.parcelaDescontoPeriodoSet prd WITH ");
        corpoBuilder.append("prd.statusParcelaDesconto.spdCodigo = '").append(CodedValues.SPD_EMPROCESSAMENTO).append("'");

        corpoBuilder.append(" where ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        if (!TextHelper.isNull(cnvCodigo)) {
            corpoBuilder.append(" AND vco.convenio.cnvCodigo ").append(criaClausulaNomeada("cnvCodigo", cnvCodigo));
        }

        if (sadCodigos != null && sadCodigos.size() > 0) {
            corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigos));
        }

        if (!TextHelper.isNull(adePeriodicidade)) {
            corpoBuilder.append(" AND ade.adePeriodicidade ").append(criaClausulaNomeada("adePeriodicidade", adePeriodicidade));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        if (!TextHelper.isNull(cnvCodigo)) {
            defineValorClausulaNomeada("cnvCodigo", cnvCodigo, query);
        }

        if (sadCodigos != null && sadCodigos.size() > 0) {
            defineValorClausulaNomeada("sadCodigo", sadCodigos, query);
        }

        if (!TextHelper.isNull(adePeriodicidade)) {
            defineValorClausulaNomeada("adePeriodicidade", adePeriodicidade, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_VLR,
                Columns.ADE_DATA,
                Columns.ADE_ANO_MES_INI,
                Columns.ADE_ANO_MES_FIM,
                Columns.ADE_PRAZO,
                Columns.ADE_PRD_PAGAS,
                Columns.ADE_COD_REG,
                Columns.ADE_INDICE,
                Columns.ADE_PERIODICIDADE,
                Columns.ADE_SAD_CODIGO,
                Columns.CNV_CODIGO,
                Columns.PRD_NUMERO,
                Columns.ADE_INC_MARGEM,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_VLR_LIQUIDO
        };
    }
}
