package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioTarifacaoQuery;

/**
 * <p> Title: RelatorioVlrRecebimento</p>
 * <p> Description: Relatório de Tarifação</p>
 * <p> Copyright: Copyright (c) 2008 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioVlrRecebimento extends ReportTemplate {

    public RelatorioVlrRecebimento() {
        this.hqueries = new ReportHNativeQuery[] {new RelatorioTarifacaoQuery()};
    }
    
    public String getSql(CustomTransferObject criterio) throws DAOException {
        return null;
    }

    public void preSqlProcess(Connection conn) {
    }

    public void postSqlProcess(Connection conn) {
    }

}
