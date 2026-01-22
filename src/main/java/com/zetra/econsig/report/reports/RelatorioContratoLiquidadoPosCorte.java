package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioContratoLiquidadoPosCorteQuery;

/**
 * <p> Title: RelatorioContratoLiquidadoPosCorte</p>
 * <p> Description: Relatório de contrato liquidado após o corte.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioContratoLiquidadoPosCorte extends ReportTemplate {

    public RelatorioContratoLiquidadoPosCorte() {
        hqueries = new ReportHNativeQuery[] {new RelatorioContratoLiquidadoPosCorteQuery()};
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
