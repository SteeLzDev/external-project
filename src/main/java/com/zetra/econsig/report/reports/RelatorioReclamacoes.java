package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioReclamacoesQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p>Title: RelatorioReclamacoes</p>
 * <p>Description: Classe que define consultas a serem aplicadas ao Relatório de Reclamações.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioReclamacoes extends ReportTemplate {

    public RelatorioReclamacoes() {
        hqueries = new ReportHQuery [] {new RelatorioReclamacoesQuery()};
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
