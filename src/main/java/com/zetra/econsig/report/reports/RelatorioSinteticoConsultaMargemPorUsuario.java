package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoConsultaMargemPorUsuarioQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioSinteticoConsultaMargemPorUsuario</p>
 * <p> Description: Relatorio sintético de consulta de margem por usuário.</p>
 * <p> Copyright: Copyright (c) 2019 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author: marlon.silva $
 * $Revision: $
 * $Date: 2019-06-12 10:38:00 -0300 (qua, 12 jun 2019) $
 */

public class RelatorioSinteticoConsultaMargemPorUsuario extends ReportTemplate {

    public RelatorioSinteticoConsultaMargemPorUsuario() {
        hqueries = new ReportHQuery[] {new RelatorioSinteticoConsultaMargemPorUsuarioQuery()};
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
