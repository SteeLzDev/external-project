package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
/**
 * <p>Title: RelatorioParcelasProcessadasFuturas</p>
 * <p>Description: Relatorio de parcelas processadas e futuras
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

public class RelatorioParcelasProcessadasFuturas extends ReportTemplate {
    
    public RelatorioParcelasProcessadasFuturas() {
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
