package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.AlteracaoADEValorRetornoQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioAlteracaoADEValorRetorno</p>
 * <p> Description: Relatório de alteração de ade valor no retorno.</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioAlteracaoADEValorRetorno extends ReportTemplate {

    public RelatorioAlteracaoADEValorRetorno() {
        hqueries = new ReportHQuery [] {new AlteracaoADEValorRetornoQuery()};
    }

    @Override
    public String getSql(CustomTransferObject criterio) throws DAOException {
        return null;
    }

    @Override
    public void preSqlProcess(Connection conn){ }

    @Override
    public void postSqlProcess(Connection conn){ }

	
}
