package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioBloqueioServidorQuery;

/**
 * <p> Title: RelatorioBloqueioServidor</p>
 * <p> Description: Relatório de Bloqueio de Servidor</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioBloqueioServidor extends ReportTemplate {

    public RelatorioBloqueioServidor() {
        hqueries = new ReportHNativeQuery[] {new RelatorioBloqueioServidorQuery()};
    }

    @Override
    public String getSql(CustomTransferObject criterio) throws DAOException {
        return null;
    }

    @Override
    public void preSqlProcess(Connection conn) {
    }

    @Override
    public void postSqlProcess(Connection conn) {
    }
}
