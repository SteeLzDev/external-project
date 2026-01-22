package com.zetra.econsig.persistence.query.consignacao;

import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarConsignacaoParcelaQuery</p>
 * <p>Description: Lista contratos e suas parcelas</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarConsignacaoParcelaQuery extends HQuery {

    public String rseCodigo;
    public List<String> sadCodigos;
    public List<String> svcCodigos;
    public List<String> csaCodigos;
    public List<Long> adeNumeros;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        List<String> spdCodigo = Arrays.asList(new String[] {CodedValues.SPD_REJEITADAFOLHA,
                CodedValues.SPD_LIQUIDADAFOLHA,
                CodedValues.SPD_LIQUIDADAMANUAL});

        String corpo = "select " +
                       "ade.adeCodigo, " +
                       "ade.adeData, " +
                       "ade.adeVlr, " +
                       "ade.adeNumero, " +
                       "ade.adeAnoMesIni, " +
                       "ade.adeAnoMesFim, " +
                       "rse.rseCodigo, " +
                       "rse.rseMatricula, " +
                       "ser.serNome, " +
                       "ser.serCpf, " +
                       "prd.prdDataDesconto, " +
                       "prd.prdVlrPrevisto, " +
                       "prd.prdVlrRealizado, " +
                       "prd.prdNumero, " +
                       "prd.statusParcelaDesconto.spdCodigo, " +
                       "csa.csaCodigo, " +
                       "csa.csaNomeAbrev ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM AutDesconto ade");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv");
        corpoBuilder.append(" INNER JOIN cnv.consignataria csa");
        corpoBuilder.append(" INNER JOIN ade.registroServidor rse");
        corpoBuilder.append(" INNER JOIN rse.servidor ser");
        corpoBuilder.append(" INNER JOIN ade.parcelaDescontoSet prd");
        corpoBuilder.append(" WHERE prd.statusParcelaDesconto.spdCodigo ").append(criaClausulaNomeada("spdCodigo", spdCodigo));

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" AND rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }

        if (sadCodigos != null && sadCodigos.size() > 0) {
            corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigo", sadCodigos));
        }

        if (svcCodigos != null && svcCodigos.size() > 0) {
            corpoBuilder.append(" AND cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigos));
        }

        if (csaCodigos != null && !csaCodigos.isEmpty()) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigos", csaCodigos));
        }

        if (adeNumeros != null && adeNumeros.size() > 0) {
            corpoBuilder.append(" AND ade.adeNumero ").append(criaClausulaNomeada("adeNumeros", adeNumeros));
        }

        corpoBuilder.append(" ORDER BY prd.prdDataDesconto ASC, prd.prdNumero DESC, ade.adeNumero ASC ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("spdCodigo", spdCodigo, query);

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (sadCodigos != null && sadCodigos.size() > 0) {
            defineValorClausulaNomeada("sadCodigo", sadCodigos, query);
        }

        if (svcCodigos != null && svcCodigos.size() > 0) {
            defineValorClausulaNomeada("svcCodigo", svcCodigos, query);
        }

        if (csaCodigos != null && !csaCodigos.isEmpty()) {
            defineValorClausulaNomeada("csaCodigos", csaCodigos, query);
        }

        if (adeNumeros != null && adeNumeros.size() > 0) {
            defineValorClausulaNomeada("adeNumeros", adeNumeros, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_DATA,
                Columns.ADE_VLR,
                Columns.ADE_NUMERO,
                Columns.ADE_ANO_MES_INI,
                Columns.ADE_ANO_MES_FIM,
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.PRD_DATA_DESCONTO,
                Columns.PRD_VLR_PREVISTO,
                Columns.PRD_VLR_REALIZADO,
                Columns.PRD_NUMERO,
                Columns.SPD_CODIGO,
                Columns.CSA_CODIGO,
                Columns.CSA_NOME_ABREV
        };
    }
}
