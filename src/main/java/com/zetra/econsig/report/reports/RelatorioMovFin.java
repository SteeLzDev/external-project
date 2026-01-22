package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.relatorio.RelatorioMovFinQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioMovFin</p>
 * <p> Description: Relatório de Movimentação financeira</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioMovFin extends ReportTemplate {

    public RelatorioMovFin() {
        hqueries = new ReportHQuery[]{new RelatorioMovFinQuery()};
    }

    @Override
    public String getSql(CustomTransferObject criterio) throws DAOException {
        return null;
    }

    @Override
    public void setResponsavel(AcessoSistema responsavel) {
        super.setResponsavel(responsavel);
        ((RelatorioMovFinQuery) hqueries[0]).responsavel = this.responsavel;
    }

    @Override
    public void preSqlProcess(Connection conn){ }

    @Override
    public void postSqlProcess(Connection conn){ }

}
