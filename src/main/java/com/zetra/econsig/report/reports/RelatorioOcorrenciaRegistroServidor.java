package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioOcorrenciaRegistroServidorQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioOcorrenciaRegistroServidor</p>
 * <p> Description: Relatório de Ocorrência de Registro Servidor</p>
 * <p> Copyright: Copyright (c) 2002-2016</p>
 * <p> Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioOcorrenciaRegistroServidor extends ReportTemplate {

    public RelatorioOcorrenciaRegistroServidor() {
        hqueries = new ReportHQuery [] {new RelatorioOcorrenciaRegistroServidorQuery()};
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
