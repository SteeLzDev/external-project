package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoAcompCompraQuery;

/**
 * <p>Title: RelatorioSinteticoAcompCompra</p>
 * <p>Description: Report Template para relatório Sintético Acompanhamento de Compra.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioSinteticoAcompCompra extends ReportTemplate {

    public RelatorioSinteticoAcompCompra() {
        this.hqueries = new ReportHNativeQuery [] {new RelatorioSinteticoAcompCompraQuery()};
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
