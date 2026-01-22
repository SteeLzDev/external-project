package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioComssionamentoAgenciamentoAnaliticoQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p>Title: RelatorioAgenciamentoAnalitico</p>
 * <p>Description: Classe de Relatorio de Comissionamento e Agenciamento Analítico </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author:$
 * $Revision$
 * $Date:$
 */
public class RelatorioComissionamentoAgenciamentoAnalitico extends ReportTemplate {

	public RelatorioComissionamentoAgenciamentoAnalitico() {
		hqueries = new ReportHQuery[] { new RelatorioComssionamentoAgenciamentoAnaliticoQuery() };
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
