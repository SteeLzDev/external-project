package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioOcorrenciaPermissionarioQuery;

/**
 * <p> Title: RelatorioOcorrenciaPermissionario</p>
 * <p> Description: Relatório de Ocorrência de Permissionário</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioOcorrenciaPermissionario extends ReportTemplate {

	public RelatorioOcorrenciaPermissionario() {
        hqueries = new ReportHNativeQuery [] {new RelatorioOcorrenciaPermissionarioQuery()};
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
