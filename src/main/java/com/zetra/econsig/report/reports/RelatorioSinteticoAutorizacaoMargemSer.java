package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoAutorizacaoMargemSerQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioSintetico</p>
 * <p> Description: Relatório Sintético de Autorizacao de Margem pelo Servidor</p>
 * <p> Copyright: Copyright (c) 2008 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoAutorizacaoMargemSer extends ReportTemplate {

    public RelatorioSinteticoAutorizacaoMargemSer() {
        hqueries = new ReportHQuery[] { new RelatorioSinteticoAutorizacaoMargemSerQuery() };
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
