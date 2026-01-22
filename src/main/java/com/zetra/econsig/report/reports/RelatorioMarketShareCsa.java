package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioMarketShareCsaQuery;

/**
 * <p> Title: RelatorioMarketShareCsa</p>
 * <p> Description: Relatório Market Share de consignatária de empréstimo consignado.</p>
 * <p> Copyright: Copyright (c) 2013 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioMarketShareCsa extends ReportTemplate {

    public RelatorioMarketShareCsa() {
        hqueries = new ReportHNativeQuery[] {new RelatorioMarketShareCsaQuery()};
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
