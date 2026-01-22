package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioOcorrenciaUsuarioQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p>Title: RelatorioOcorrenciaUsuario</p>
 * <p>Description: Classe que define consultas a serem aplicadas ao Relatório Ocorrência de Usuário
 * de lote</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioOcorrenciaUsuario extends ReportTemplate {

    public RelatorioOcorrenciaUsuario() {
        this.hqueries = new ReportHQuery [] {new RelatorioOcorrenciaUsuarioQuery()};
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
