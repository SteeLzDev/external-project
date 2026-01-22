package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;

/**
 * <p> Title: RelatorioSolicitacaoSuporte</p>
 * <p> Description: Relatório de Solicitação de Suporte</p>
 * <p> Copyright: Copyright (c) 2002-2016</p>
 * <p> Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSolicitacaoSuporte extends ReportTemplate{

    public RelatorioSolicitacaoSuporte() {
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
