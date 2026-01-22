package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioRepasseQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;


/**
 * <p> Title: RelatorioRepasse</p>
 * <p> Description: Relatório Repasse</p>
 * <p> Copyright: Copyright (c) 2014 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioRepasse extends ReportTemplate {

    public RelatorioRepasse() {
        hqueries = new ReportHQuery [] {new RelatorioRepasseQuery()};
    }

    @Override
    public void preSqlProcess(Connection conn) {
    }

    @Override
    public void postSqlProcess(Connection conn) {
    }

    @Override
    @Deprecated
    public String getSql(CustomTransferObject criterio) throws DAOException {
        return null;
    }

}
