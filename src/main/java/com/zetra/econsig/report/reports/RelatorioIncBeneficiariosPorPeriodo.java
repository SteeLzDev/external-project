package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioIncBeneficiariosPorPeriodoQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p>Title: RelatorioIncBeneficiariosPorPeriodo</p>
 * <p>Description: Relatorio de inclusão de beneficiários por período</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioIncBeneficiariosPorPeriodo extends ReportTemplate {

    public RelatorioIncBeneficiariosPorPeriodo() {
        hqueries = new ReportHQuery[] { new RelatorioIncBeneficiariosPorPeriodoQuery() };
    }

    @Override
    public String getSql(CustomTransferObject criterio) throws DAOException {
        return null;
    }

    @Override
    public void postSqlProcess(Connection conn) {
    }

    @Override
    public void preSqlProcess(Connection conn) {
    }
}
