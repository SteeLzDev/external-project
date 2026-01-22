package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioInformacaoBancariaDivergenteQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioInformacaoBancariaDivergente</p>
 * <p> Description: Relatório de contratos com informação bancária divergente.</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioInformacaoBancariaDivergente extends ReportTemplate {

    public RelatorioInformacaoBancariaDivergente() {
        this.hqueries = new ReportHQuery[]{ new RelatorioInformacaoBancariaDivergenteQuery() };
    }

    public String getSql(CustomTransferObject criterio) throws DAOException {
        return null;
    }

    public void preSqlProcess(Connection conn){ }
    
    public void postSqlProcess(Connection conn){ }

	
}
