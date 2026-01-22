package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoOcorrenciaAutorizacaoQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioSintetico</p>
 * <p> Description: Relatório Sintético de Ocorrencia de Consignações</p>
 * <p> Copyright: Copyright (c) 2008 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoOcorrenciaAutorizacao extends ReportTemplate {

    public RelatorioSinteticoOcorrenciaAutorizacao() {
        hqueries = new ReportHQuery[] {new RelatorioSinteticoOcorrenciaAutorizacaoQuery()};
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
