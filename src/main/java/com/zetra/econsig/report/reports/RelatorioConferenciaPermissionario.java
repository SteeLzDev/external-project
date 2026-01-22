package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioConferenciaPermissionarioQuery;

/**
 * <p> Title: RelatorioConferenciaPermissionario</p>
 * <p> Description: Relatório de Conferência de Permissionário</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioConferenciaPermissionario extends ReportTemplate {

	public RelatorioConferenciaPermissionario() {
        hqueries = new ReportHNativeQuery [] {new RelatorioConferenciaPermissionarioQuery()};
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
