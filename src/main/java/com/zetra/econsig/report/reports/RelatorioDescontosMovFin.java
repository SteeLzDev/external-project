package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioDescontosMovFinQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioDescontosMovFin</p>
 * <p> Description: Relatório de Descontos (Movimentoação financeira) </p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioDescontosMovFin extends ReportTemplate {

    public RelatorioDescontosMovFin() {
        hqueries = new ReportHQuery[]{new RelatorioDescontosMovFinQuery()};
    }

    @Override
    public String getSql(CustomTransferObject criterio) throws DAOException {
        return null;
    }

    @Override
    public void preSqlProcess(Connection conn){ }

    @Override
    public void postSqlProcess(Connection conn){ }

}
