package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioIntegracaoMapeamentoMultiploQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p>Title: RelatorioIntegracaoMapeamentoMultiplo</p>
 * <p>Description: Report Template para relatório integracao mapeamento multiplo</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioIntegracaoMapeamentoMultiplo extends ReportTemplate {

    public RelatorioIntegracaoMapeamentoMultiplo() {
        hqueries = new ReportHQuery [] {new RelatorioIntegracaoMapeamentoMultiploQuery()};
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
