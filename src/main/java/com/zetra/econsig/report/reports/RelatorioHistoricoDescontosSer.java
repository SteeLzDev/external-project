package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioHistoricoDescontosSerQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p>Title: RelatorioOcorrenciaAutorizacao</p>
 * <p>Description: RelatorioOcorrenciaAutorizacao</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioHistoricoDescontosSer extends ReportTemplate {

    public RelatorioHistoricoDescontosSer() {
        hqueries = new ReportHQuery [] {new RelatorioHistoricoDescontosSerQuery()};
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
