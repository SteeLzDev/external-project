package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSolicitacaoSaldoDevedorQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p>Title: RelatorioSolicitacaoSaldoDevedor</p>
 * <p>Description: Classe que define consultas a serem aplicadas ao Relatório Solicitação de Saldo Devedor.</p>
 * <p>Copyright: Copyright (c) 2021</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSolicitacaoSaldoDevedor extends ReportTemplate {

    public RelatorioSolicitacaoSaldoDevedor() {
        hqueries = new ReportHQuery [] {new RelatorioSolicitacaoSaldoDevedorQuery()};
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
