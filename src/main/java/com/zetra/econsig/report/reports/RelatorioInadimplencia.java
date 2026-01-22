package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioInadimplenciaQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p>Title: RelatorioInadimplencia</p>
 * <p>Description: RelatorioInadimplencia</p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioInadimplencia extends ReportTemplate {

    public RelatorioInadimplencia() {
        hqueries = new ReportHQuery [] {new RelatorioInadimplenciaQuery()};
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
