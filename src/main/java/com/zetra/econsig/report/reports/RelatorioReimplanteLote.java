package com.zetra.econsig.report.reports;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioReimplanteLoteQuery;

import java.sql.Connection;

 /**
 * <p>Title: RelatorioReimplanteLote</p>
 * <p>Description: Classe que gera relatorio de contratos reimplantados.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class RelatorioReimplanteLote extends ReportTemplate {

    public RelatorioReimplanteLote() {
        hqueries = new ReportHNativeQuery[]{new RelatorioReimplanteLoteQuery()};
    }

    @Override
    public void preSqlProcess(Connection conn) {
        // TODO document why this method is empty
    }

    @Override
    public void postSqlProcess(Connection conn) {
        // TODO document why this method is empty
    }

    @Override
    public String getSql(CustomTransferObject criterio) throws DAOException {

        return null;
    }
}
