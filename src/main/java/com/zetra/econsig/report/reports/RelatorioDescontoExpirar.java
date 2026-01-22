package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.relatorio.RelatorioDescontoExpirarQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

public class RelatorioDescontoExpirar extends ReportTemplate {

	public RelatorioDescontoExpirar() {
        hqueries = new ReportHQuery[] {new RelatorioDescontoExpirarQuery()};
    }

	@Override
	public void setResponsavel(AcessoSistema responsavel) {
		super.setResponsavel(responsavel);
		((RelatorioDescontoExpirarQuery) hqueries[0]).responsavel = this.responsavel;
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
