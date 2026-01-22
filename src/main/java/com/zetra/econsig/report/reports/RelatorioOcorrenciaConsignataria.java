package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.relatorio.RelatorioOcorrenciaConsignatariaQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioOcorrenciaConsignataria </p>
 * <p> Description: Relatório de Ocorrências Consignatárias </p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioOcorrenciaConsignataria extends ReportTemplate {

    public RelatorioOcorrenciaConsignataria() {
        this.hqueries = new ReportHQuery[] {new RelatorioOcorrenciaConsignatariaQuery()};
    }
    
    @Override
    public void setResponsavel(AcessoSistema responsavel) {        
        super.setResponsavel(responsavel);
        ((RelatorioOcorrenciaConsignatariaQuery) this.hqueries[0]).responsavel = this.responsavel;
    }

    public String getSql(CustomTransferObject criterio) throws DAOException {
        return null;
    }

    public void preSqlProcess(Connection conn) {
    }

    public void postSqlProcess(Connection conn) {
    }

}
