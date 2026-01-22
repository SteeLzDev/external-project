package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoDecisaoJudicialQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p>Title: RelatorioSinteticoDecisaoJudicial</p>
 * <p>Description: Report Template para relatório Sintético de Decisões Judiciais</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoDecisaoJudicial extends ReportTemplate {

    public RelatorioSinteticoDecisaoJudicial() {
        this.hqueries = new ReportHQuery [] {new RelatorioSinteticoDecisaoJudicialQuery()};
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
