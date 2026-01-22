package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioProvisionamentoMargemQuery;

/**
 * <p> Title: RelatorioProvisionamentoMargem</p>
 * <p> Description: Relatório de provisionamento de margem.</p>
 * <p> Copyright: Copyright (c) 2008 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioProvisionamentoMargem extends ReportTemplate {
    public RelatorioProvisionamentoMargem() {
        hqueries = new ReportHNativeQuery[]{new RelatorioProvisionamentoMargemQuery()};
    }

    @Override
    public void setResponsavel(AcessoSistema responsavel) {
        super.setResponsavel(responsavel);
        ((RelatorioProvisionamentoMargemQuery) hqueries[0]).responsavel = this.responsavel;
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
