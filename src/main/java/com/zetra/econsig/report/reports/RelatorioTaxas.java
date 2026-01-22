package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioTaxasQuery;

/**
 * <p> Title: RelatorioTaxas</p>
 * <p> Description: Relatório Ranking Taxa Juros</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioTaxas extends ReportTemplate {

    public RelatorioTaxas() {
        hqueries = new ReportHNativeQuery [] {
                new RelatorioTaxasQuery(1),
                new RelatorioTaxasQuery(2),
                new RelatorioTaxasQuery(3),
                new RelatorioTaxasQuery(4)};
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
