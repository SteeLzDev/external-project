package com.zetra.econsig.persistence.query.calendario;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCalendarioFolhaCseQuery</p>
 * <p>Description: Lista os registros de calendário da folha geral.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCalendarioFolhaCseQuery extends HQuery {

    public Date cfcPeriodo;

    public Integer anoPeriodo;

    public String cseCodigo;

    public String cfcDataFimMaiorQue;

    public Date cfcPeriodoMaiorQueIgual;

    public boolean count = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        if (!count) {
            corpoBuilder.append("SELECT cfc.cseCodigo, cfc.cfcPeriodo, cfc.cfcDiaCorte, cfc.cfcDataIni, cfc.cfcDataFim, cfc.cfcDataFimAjustes, cfc.cfcApenasReducoes, cfc.cfcDataPrevistaRetorno, cfc.cfcDataIniFiscal, cfc.cfcDataFimFiscal, cfc.cfcNumPeriodo ");
        } else {
            corpoBuilder.append("SELECT count(*) ");
        }

        corpoBuilder.append(" FROM CalendarioFolhaCse cfc ");
        corpoBuilder.append(" WHERE cfc.cseCodigo = :codigo ");

        if (cfcPeriodo != null) {
            corpoBuilder.append(" AND cfc.cfcPeriodo = :periodo");
        }
        if (anoPeriodo != null) {
            corpoBuilder.append(" AND year(cfc.cfcPeriodo) = :anoPeriodo");
        }
        if (cfcDataFimMaiorQue != null) {
            corpoBuilder.append(" AND cfc.cfcDataFim > :cfcDataFimMaiorQue");
        }
        if (cfcPeriodoMaiorQueIgual != null) {
            corpoBuilder.append(" AND cfc.cfcPeriodo >= :cfcPeriodoMaiorQueIgual");
        }

        if (!count) {
            corpoBuilder.append(" ORDER BY cfc.cfcPeriodo ASC");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("codigo", cseCodigo, query);

        if (cfcPeriodo != null) {
            defineValorClausulaNomeada("periodo", cfcPeriodo, query);
        }
        if (anoPeriodo != null) {
            defineValorClausulaNomeada("anoPeriodo", anoPeriodo, query);
        }
        if (cfcDataFimMaiorQue != null) {
            defineValorClausulaNomeada("cfcDataFimMaiorQue", parseDateTimeString(cfcDataFimMaiorQue), query);
        }
        if (cfcPeriodoMaiorQueIgual != null) {
            defineValorClausulaNomeada("cfcPeriodoMaiorQueIgual", cfcPeriodoMaiorQueIgual, query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CFC_CSE_CODIGO,
                Columns.CFC_PERIODO,
                Columns.CFC_DIA_CORTE,
                Columns.CFC_DATA_INI,
                Columns.CFC_DATA_FIM,
                Columns.CFC_DATA_FIM_AJUSTES,
                Columns.CFC_APENAS_REDUCOES,
                Columns.CFC_DATA_PREVISTA_RETORNO,
                Columns.CFC_DATA_INI_FISCAL,
                Columns.CFC_DATA_FIM_FISCAL,
                Columns.CFC_NUM_PERIODO
        };
    }
}
