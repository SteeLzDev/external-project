package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioAlteracaoMultiplasConsignacoesQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioAlteracaoMultiplasConsignacoes</p>
 * <p> Description: Relatório de Alteração de Multiplas Consignações</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioAlteracaoMultiplasConsignacoes extends ReportTemplate {

    public RelatorioAlteracaoMultiplasConsignacoes() {
        hqueries = new ReportHQuery[] {new RelatorioAlteracaoMultiplasConsignacoesQuery()};
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
