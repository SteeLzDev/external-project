package com.zetra.econsig.report.reports;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioServidorSaldoDevedorQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

import java.sql.Connection;

public class RelatorioServidorSaldoDevedor extends ReportTemplate{

    public RelatorioServidorSaldoDevedor() {
        hqueries = new ReportHQuery [] {new RelatorioServidorSaldoDevedorQuery()};
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
