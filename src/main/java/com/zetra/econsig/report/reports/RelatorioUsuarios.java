package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.relatorio.RelatorioUsuariosQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioUsuario</p>
 * <p> Description: Relatório Usuário</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioUsuarios extends ReportTemplate {

    public RelatorioUsuarios() {
        this.hqueries = new ReportHQuery [] {new RelatorioUsuariosQuery()};
    }
    
    @Override
    public void setResponsavel(AcessoSistema responsavel) {        
        super.setResponsavel(responsavel);
        ((RelatorioUsuariosQuery) this.hqueries[0]).responsavel = this.responsavel;
    }

    public String getSql(CustomTransferObject criterio) throws DAOException {
        return null;
    }

    public void preSqlProcess(Connection conn) {
    }

    public void postSqlProcess(Connection conn) {
    }

}
