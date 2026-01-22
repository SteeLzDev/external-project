package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioDocumentoBeneficiarioTipoValidadeQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

public class RelatorioDocumentoBeneficiarioTipoValidade extends ReportTemplate {

    public RelatorioDocumentoBeneficiarioTipoValidade() {
        hqueries = new ReportHQuery[] { new RelatorioDocumentoBeneficiarioTipoValidadeQuery() };
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
