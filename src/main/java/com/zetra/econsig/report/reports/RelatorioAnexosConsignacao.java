package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.relatorio.RelatorioAnexosConsignacaoQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioAnexosConsignacao</p>
 * <p> Description: Relatório de Anexos de Consignação</p>
 * <p> Copyright: Copyright (c) 2017 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioAnexosConsignacao extends ReportTemplate {

    public RelatorioAnexosConsignacao() {
        hqueries = new ReportHQuery[] {new RelatorioAnexosConsignacaoQuery()};
    }

    @Override
    public void setResponsavel(AcessoSistema responsavel) {
        super.setResponsavel(responsavel);
        ((RelatorioAnexosConsignacaoQuery) hqueries[0]).responsavel = this.responsavel;
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
