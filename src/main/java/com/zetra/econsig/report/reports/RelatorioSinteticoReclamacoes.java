package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoReclamacoesQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioSinteticoReclamacoes</p>
 * <p> Description: Relatório Sintético de Reclamações</p>
 * <p> Copyright: Copyright (c) 2013 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoReclamacoes extends ReportTemplate {

    public RelatorioSinteticoReclamacoes() {
        hqueries = new ReportHQuery[] {new RelatorioSinteticoReclamacoesQuery()};
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
