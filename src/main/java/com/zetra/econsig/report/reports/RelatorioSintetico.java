package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioSintetico</p>
 * <p> Description: Relatório Sintético de Consignações</p>
 * <p> Copyright: Copyright (c) 2008 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSintetico extends ReportTemplate {

    public RelatorioSintetico() {
        this.hqueries = new ReportHQuery[] {new RelatorioSinteticoQuery()};
    }
    
    public String getSql(CustomTransferObject criterio) throws DAOException {
        return null;
    }

    public void preSqlProcess(Connection conn) {
    }

    public void postSqlProcess(Connection conn) {
    }

}
