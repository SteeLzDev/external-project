package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoDescontosQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioSinteticoDescontos</p>
 * <p> Description: Relatório Sintético de Descontos de Permissionários</p>
 * <p> Copyright: Copyright (c) 2002-2016</p>
 * <p> Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoDescontos extends ReportTemplate {

    public RelatorioSinteticoDescontos() {
        hqueries = new ReportHQuery[] {new RelatorioSinteticoDescontosQuery()};
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
