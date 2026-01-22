package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;

/**
 * <p> Title: RelatorioDistribuirConsignacoesPorServicos</p>
 * <p> Description: Relatório gerado no caso de uso Distribuir Consignações por Serviços</p>
 * <p> Copyright: Copyright (c) 2002-2016</p>
 * <p> Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioDistribuirConsignacoesPorServicos extends ReportTemplate{

    public RelatorioDistribuirConsignacoesPorServicos() {
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
