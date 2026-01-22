package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoMovFinQuery;

/**
 * <p>Title: RelatorioSinteticoMovFinQuery</p>
 * <p>Description: Report Template para relatório Mov. Financeira</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoMovFin extends ReportTemplate {

    public RelatorioSinteticoMovFin() {
        hqueries = new ReportHNativeQuery [] {new RelatorioSinteticoMovFinQuery()};
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
