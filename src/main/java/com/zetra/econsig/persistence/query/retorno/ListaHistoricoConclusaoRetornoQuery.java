package com.zetra.econsig.persistence.query.retorno;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p> Title: ListaHistoricoConclusaoRetornoQuery</p>
 * <p> Description: Lista o histórico de conclusão de retorno, selecionando o registro mais recente para cada período.</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaHistoricoConclusaoRetornoQuery extends HQuery {

    public boolean count = false;
    public String orgCodigo;
    public String periodo;
    public int qtdeMesesPesquisa = 24;
    public boolean ordemDescrescente = false;

    public ListaHistoricoConclusaoRetornoQuery(int qtdeMesesPesquisa) {
        this.qtdeMesesPesquisa = qtdeMesesPesquisa;
    }

    public ListaHistoricoConclusaoRetornoQuery(int qtdeMesesPesquisa, String periodo) {
        this.qtdeMesesPesquisa = qtdeMesesPesquisa;
        this.periodo = periodo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = null;
        corpo = "SELECT " +
                "hcr.hcrDataFim, " +
                "hcr.hcrPeriodo, " +
                "max(hcr.hcrChaveHistMargem)";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM HistoricoConclusaoRetorno hcr ");
        corpoBuilder.append(" WHERE 1 = 1 ");
        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" and hcr.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }
        corpoBuilder.append(" AND hcr.hcrDataFim = (SELECT MAX(hcrMax.hcrDataFim) ");
        corpoBuilder.append("                       FROM HistoricoConclusaoRetorno hcrMax ");
        corpoBuilder.append("                       WHERE hcrMax.hcrPeriodo = hcr.hcrPeriodo ");
        corpoBuilder.append("                         AND hcrMax.orgCodigo = hcr.orgCodigo) ");
        if (!TextHelper.isNull(periodo)) {
            corpoBuilder.append(" AND to_year_month(hcr.hcrPeriodo) > to_year_month(add_month(:periodo, -1 * :qtdeMesesPesquisa)) ");
        } else {
            corpoBuilder.append(" AND to_year_month(hcr.hcrPeriodo) >= to_year_month(add_month(current_date(), -1 * :qtdeMesesPesquisa)) ");
        }
        corpoBuilder.append(" AND hcr.hcrDesfeito = 'N' ");
        corpoBuilder.append(" GROUP BY hcr.hcrPeriodo, hcr.hcrDataFim ");
        if(!ordemDescrescente) {
            corpoBuilder.append(" ORDER BY hcr.hcrPeriodo ASC ");
        } else {
            corpoBuilder.append(" ORDER BY hcr.hcrPeriodo DESC ");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        if (!TextHelper.isNull(periodo)) {
            defineValorClausulaNomeada("periodo", parseDateString(periodo), query);
        }
        if (!TextHelper.isNull(qtdeMesesPesquisa)) {
            defineValorClausulaNomeada("qtdeMesesPesquisa", qtdeMesesPesquisa, query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.HCR_DATA_FIM,
                Columns.HCR_PERIODO,
                Columns.HCR_CHAVE_HIST_MARGEM
        };
    }
}
