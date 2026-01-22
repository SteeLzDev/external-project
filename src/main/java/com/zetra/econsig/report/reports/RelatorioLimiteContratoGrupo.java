package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.LimiteContratoGrupoQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioLimiteContratoGrupo</p>
 * <p> Description: Relatório de Limite de Contrato por Grupo de Serviço</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioLimiteContratoGrupo extends ReportTemplate {

    public RelatorioLimiteContratoGrupo() {
        this.hqueries = new ReportHQuery[] {new LimiteContratoGrupoQuery()};
    }
    
 /*   @Override
    public void setResponsavel(AcessoSistema responsavel) {        
        super.setResponsavel(responsavel);
        ((LimiteContratoGrupoQuery) this.hqueries[0]).responsavel = this.responsavel;
    }*/

    public String getSql(CustomTransferObject criterio) throws DAOException {
        return null;
    }

    public void preSqlProcess(Connection conn) {
    }

    public void postSqlProcess(Connection conn) {
    }

}
