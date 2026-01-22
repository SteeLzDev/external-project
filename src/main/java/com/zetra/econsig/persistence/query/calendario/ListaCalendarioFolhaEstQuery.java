package com.zetra.econsig.persistence.query.calendario;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCalendarioFolhaEstQuery</p>
 * <p>Description: Lista os registros de calendário da folha do estabelecimento.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCalendarioFolhaEstQuery extends HQuery {

    public Date cfePeriodo;

    public Integer anoPeriodo;

    public String estCodigo;

    public String cfeDataFimMaiorQue;

    public Date cfePeriodoMaiorQueIgual;

    public boolean count = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        if (!count) {
            corpoBuilder.append("SELECT cfe.estCodigo, cfe.cfePeriodo, cfe.cfeDiaCorte, cfe.cfeDataIni, cfe.cfeDataFim, cfe.cfeDataFimAjustes, cfe.cfeApenasReducoes, cfe.cfeDataPrevistaRetorno, cfe.cfeDataIniFiscal, cfe.cfeDataFimFiscal, cfe.cfeNumPeriodo ");
        } else {
            corpoBuilder.append("SELECT count(*) ");
        }

        corpoBuilder.append(" FROM CalendarioFolhaEst cfe ");
        corpoBuilder.append(" WHERE cfe.estCodigo = :codigo ");

        if (cfePeriodo != null) {
            corpoBuilder.append(" AND cfe.cfePeriodo = :periodo");
        }
        if (anoPeriodo != null) {
            corpoBuilder.append(" AND year(cfe.cfePeriodo) = :anoPeriodo");
        }
        if (cfeDataFimMaiorQue != null) {
            corpoBuilder.append(" AND cfe.cfeDataFim > :cfeDataFimMaiorQue");
        }
        if (cfePeriodoMaiorQueIgual != null) {
            corpoBuilder.append(" AND cfe.cfePeriodo >= :cfePeriodoMaiorQueIgual");
        }

        if (!count) {
            corpoBuilder.append(" ORDER BY cfe.cfePeriodo ASC");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("codigo", estCodigo, query);

        if (cfePeriodo != null) {
            defineValorClausulaNomeada("periodo", cfePeriodo, query);
        }
        if (anoPeriodo != null) {
            defineValorClausulaNomeada("anoPeriodo", anoPeriodo, query);
        }
        if (cfeDataFimMaiorQue != null) {
            defineValorClausulaNomeada("cfeDataFimMaiorQue", parseDateTimeString(cfeDataFimMaiorQue), query);
        }
        if (cfePeriodoMaiorQueIgual != null) {
            defineValorClausulaNomeada("cfePeriodoMaiorQueIgual", cfePeriodoMaiorQueIgual, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CFE_EST_CODIGO,
                Columns.CFE_PERIODO,
                Columns.CFE_DIA_CORTE,
                Columns.CFE_DATA_INI,
                Columns.CFE_DATA_FIM,
                Columns.CFE_DATA_FIM_AJUSTES,
                Columns.CFE_APENAS_REDUCOES,
                Columns.CFE_DATA_PREVISTA_RETORNO,
                Columns.CFE_DATA_INI_FISCAL,
                Columns.CFE_DATA_FIM_FISCAL,
                Columns.CFE_NUM_PERIODO
        };
    }
}
