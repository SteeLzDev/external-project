package com.zetra.econsig.report.dao.hibernate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.persistence.query.ReportQueryInterface;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

import net.sf.jasperreports.engine.JRDataSource;

/**
 * <p> Title: HibernateDataSourceFactory</p>
 * <p> Description: Gera um jasper datasource a partir do resultado de um HQuery</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HibernateDataSourceFactory {

    public static JRDataSource getDataSource(Session session, ReportQueryInterface[] hqueries, TransferObject criterio, Comparator<Object[]> comp, List<Object[]> conteudo) throws DAOException {
        HibernateQueryResultDataSource dataSource = null;
        if (conteudo != null) {
            ReportQueryInterface query = hqueries[0];
            query.setCriterios(criterio);

            if (query.getReportFields() != null) {
                dataSource = new HibernateQueryResultDataSource(conteudo, query.getReportFields());
            } else {
                query.preparar(session);
                dataSource = new HibernateQueryResultDataSource(conteudo, query.getQueryString());
            }

        } else if (hqueries != null) {
            if (hqueries.length == 1 && comp == null) {
                hqueries[0].setCriterios(criterio);
                if (hqueries[0] instanceof ReportHQuery) {
                    dataSource = new HibernateQueryResultDataSource(hqueries[0].executarIterator(session), hqueries[0].getQueryString());
                } else if (hqueries[0] instanceof ReportHNativeQuery) {
                    dataSource = new HibernateQueryResultDataSource(hqueries[0].executarScroll(session), hqueries[0].getQueryString());
                }
            } else {
                List<Object[]> lstResult = new ArrayList<>();

                for (ReportQueryInterface hquery : hqueries) {
                    List<Object[]> partialList = null;
                    hquery.setCriterios(criterio);
                    try {
                        partialList = hquery.executarLista(session);
                    } catch (Exception ex) {
                        throw new DAOException(ex);
                    }

                    lstResult.addAll(partialList);
                }

                if (comp != null) {
                    Collections.sort(lstResult, comp);
                }

                dataSource = new HibernateQueryResultDataSource(lstResult, hqueries[0].getQueryString());
            }
        }

        return dataSource;
    }
}
