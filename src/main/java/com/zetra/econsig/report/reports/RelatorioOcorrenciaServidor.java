package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioOcorrenciaServidorQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioOcorrenciaServidor</p>
 * <p> Description: Relatório de Ocorrência de Servidor</p>
 * <p> Copyright: Copyright (c) 2002-2016</p>
 * <p> Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioOcorrenciaServidor extends ReportTemplate {

    public RelatorioOcorrenciaServidor() {
        hqueries = new ReportHQuery [] {new RelatorioOcorrenciaServidorQuery()};
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
