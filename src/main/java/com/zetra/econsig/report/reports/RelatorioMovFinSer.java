package com.zetra.econsig.report.reports;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.relatorio.RelatorioMovFinSerQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

import java.sql.Connection;

/**
 * <p>Title: RelatorioMovFinSerQuery</p>
 * <p>Description: Relatório de Movimentação financeira do servidor</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class RelatorioMovFinSer extends ReportTemplate {


    public RelatorioMovFinSer() {
        hqueries = new ReportHQuery[]{new RelatorioMovFinSerQuery()};
    }

    @Override
    public String getSql(CustomTransferObject criterio) throws DAOException {
        return null;
    }

    @Override
    public void setResponsavel(AcessoSistema responsavel) {
        super.setResponsavel(responsavel);
        ((RelatorioMovFinSerQuery) hqueries[0]).responsavel = this.responsavel;
    }

    @Override
    public void preSqlProcess(Connection conn) {
        // TODO document why this method is empty
    }

    @Override
    public void postSqlProcess(Connection conn) {
        // TODO document why this method is empty
    }

}
