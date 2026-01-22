package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSaldoDevedorServidorQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p>Title: RelatorioSaldoDevedorServidor</p>
 * <p>Description: Classe template para relatorio de saldo devedor servidor
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */


public class RelatorioSaldoDevedorServidor extends ReportTemplate{

    public RelatorioSaldoDevedorServidor() {
        hqueries = new ReportHQuery [] {new RelatorioSaldoDevedorServidorQuery()};
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
