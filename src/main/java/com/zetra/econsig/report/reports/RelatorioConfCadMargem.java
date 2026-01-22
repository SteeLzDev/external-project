package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialCseQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioConfCadMargem</p>
 * <p> Description: Relatório de conferencia de cadastro de margem.</p>
 * <p> Copyright: Copyright (c) 2008 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class RelatorioConfCadMargem extends ReportTemplate{

    public RelatorioConfCadMargem() {
        hqueries = new ReportHQuery [] {new RelatorioGerencialCseQuery()};
    }

    @Override
    public void setResponsavel(AcessoSistema responsavel) {
        super.setResponsavel(responsavel);
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