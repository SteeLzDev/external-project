package com.zetra.econsig.persistence.query.calendario;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCalendarioFolhaOrgQuery</p>
 * <p>Description: Lista os registros de calendário da folha do órgão.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCalendarioFolhaOrgQuery extends HQuery {

    public Date cfoPeriodo;

    public Integer anoPeriodo;

    public String orgCodigo;

    public String cfoDataFimMaiorQue;

    public Date cfoPeriodoMaiorQueIgual;

    public boolean count = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        if (!count) {
            corpoBuilder.append("SELECT cfo.orgCodigo, cfo.cfoPeriodo, cfo.cfoDiaCorte, cfo.cfoDataIni, cfo.cfoDataFim, cfo.cfoDataFimAjustes, cfo.cfoApenasReducoes, cfo.cfoDataPrevistaRetorno, cfo.cfoDataIniFiscal, cfo.cfoDataFimFiscal, cfo.cfoNumPeriodo ");
        } else {
            corpoBuilder.append("SELECT count(*) ");
        }

        corpoBuilder.append(" FROM CalendarioFolhaOrg cfo ");
        corpoBuilder.append(" WHERE cfo.orgCodigo = :codigo ");

        if (cfoPeriodo != null) {
            corpoBuilder.append(" AND cfo.cfoPeriodo = :periodo");
        }
        if (anoPeriodo != null) {
            corpoBuilder.append(" AND year(cfo.cfoPeriodo) = :anoPeriodo");
        }
        if (cfoDataFimMaiorQue != null) {
            corpoBuilder.append(" AND cfo.cfoDataFim > :cfoDataFimMaiorQue");
        }

        if (cfoPeriodoMaiorQueIgual != null) {
            corpoBuilder.append(" AND cfo.cfoPeriodo >= :cfoPeriodoMaiorQueIgual");
        }

        if (!count) {
            corpoBuilder.append(" ORDER BY cfo.cfoPeriodo ASC");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("codigo", orgCodigo, query);

        if (cfoPeriodo != null) {
            defineValorClausulaNomeada("periodo", cfoPeriodo, query);
        }
        if (anoPeriodo != null) {
            defineValorClausulaNomeada("anoPeriodo", anoPeriodo, query);
        }
        if (cfoDataFimMaiorQue != null) {
            defineValorClausulaNomeada("cfoDataFimMaiorQue", parseDateTimeString(cfoDataFimMaiorQue), query);
        }
        if (cfoPeriodoMaiorQueIgual != null) {
            defineValorClausulaNomeada("cfoPeriodoMaiorQueIgual", cfoPeriodoMaiorQueIgual, query);
        }

        return query;

    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CFO_ORG_CODIGO,
                Columns.CFO_PERIODO,
                Columns.CFO_DIA_CORTE,
                Columns.CFO_DATA_INI,
                Columns.CFO_DATA_FIM,
                Columns.CFO_DATA_FIM_AJUSTES,
                Columns.CFO_APENAS_REDUCOES,
                Columns.CFO_DATA_PREVISTA_RETORNO,
                Columns.CFO_DATA_INI_FISCAL,
                Columns.CFO_DATA_FIM_FISCAL,
                Columns.CFO_NUM_PERIODO
        };
    }
}
