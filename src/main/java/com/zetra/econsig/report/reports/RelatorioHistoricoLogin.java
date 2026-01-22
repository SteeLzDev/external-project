package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioHistoricoLoginQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

public class RelatorioHistoricoLogin extends ReportTemplate {

    public RelatorioHistoricoLogin() {
        hqueries = new ReportHQuery [] {new RelatorioHistoricoLoginQuery()};
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
