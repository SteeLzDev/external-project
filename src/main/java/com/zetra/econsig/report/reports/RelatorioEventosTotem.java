package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioEventosTotemQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioEventosTotem</p>
 * <p> Description: Relatório Consignatarias</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author: rodrigo.rosa $
 * $Revision: 20137 $
 * $Date: 2016-03-11 14:57:36 -0300 (sex, 11 mar 2016) $
 */
public class RelatorioEventosTotem extends ReportTemplate {

    public RelatorioEventosTotem() {
        hqueries = new ReportHQuery [] {new RelatorioEventosTotemQuery()};
    }

    @Override
    public void preSqlProcess(Connection conn) {

    }

    @Override
    public void postSqlProcess(Connection conn) {

    }

    @Override
    public String getSql(CustomTransferObject criterio) throws DAOException {
        return null;
    }

}
