package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.relatorio.RelatorioConsignacoesQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioDescontosQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioDescontos</p>
 * <p> Description: Relatório de Descontos</p>
 * <p> Copyright: Copyright (c) 2013 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioDescontos extends ReportTemplate {

    public RelatorioDescontos() {
        hqueries = new ReportHQuery[] {new RelatorioDescontosQuery()};
    }

    @Override
    public void setResponsavel(AcessoSistema responsavel) {
        super.setResponsavel(responsavel);
        ((RelatorioConsignacoesQuery) hqueries[0]).responsavel = this.responsavel;
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