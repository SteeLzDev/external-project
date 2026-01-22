package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioPrdPagasPorCsaPeriodoQuery;

/**
 * <p>Title: RelatorioPrdPagasPorCsaPeriodo</p>
 * <p>Description: Relatório de Parcelas Pagas por Consignatária e período</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioPrdPagasPorCsaPeriodo extends ReportTemplate {

    public RelatorioPrdPagasPorCsaPeriodo() {
        hqueries = new ReportHNativeQuery[] {new RelatorioPrdPagasPorCsaPeriodoQuery()};
    }

    @Override
    public void setResponsavel(AcessoSistema responsavel) {
        super.setResponsavel(responsavel);
        ((RelatorioPrdPagasPorCsaPeriodoQuery) hqueries[0]).responsavel = this.responsavel;
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
