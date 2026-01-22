package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioContratosBeneficiosQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;


/**
 * <p>Title: RelatorioContratosBeneficios</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioContratosBeneficios extends ReportTemplate {

    public RelatorioContratosBeneficios() {
        hqueries =new ReportHQuery[] { new RelatorioContratosBeneficiosQuery() };
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
