package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioTaxasEfetivasQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioTaxasEfetivas</p>
 * <p> Description: Relatório Ranking de Taxa Efetivas</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioTaxasEfetivas extends ReportTemplate {

    public RelatorioTaxasEfetivas() {
        this.hqueries = new ReportHQuery [] {new RelatorioTaxasEfetivasQuery()};
    }

    public String getSql(CustomTransferObject criterio) throws DAOException {
        return null;
    }

    public void preSqlProcess(Connection conn) {
    }
    
    public void postSqlProcess(Connection conn) { 
    }

}

