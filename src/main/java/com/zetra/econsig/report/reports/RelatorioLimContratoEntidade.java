package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.relatorio.LimiteContratoEntidadeQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioLimContratoEntidade</p>
 * <p> Description: Relatório de Limite de Contrato por Entidade</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioLimContratoEntidade extends ReportTemplate {

    public RelatorioLimContratoEntidade() {
        this.hqueries = new ReportHQuery[] {new LimiteContratoEntidadeQuery()};
    }
    
    @Override
    public void setResponsavel(AcessoSistema responsavel) {        
        super.setResponsavel(responsavel);
        ((LimiteContratoEntidadeQuery) this.hqueries[0]).responsavel = this.responsavel;
    }

    public String getSql(CustomTransferObject criterio) throws DAOException {
        return null;
    }

    public void preSqlProcess(Connection conn) {
    }

    public void postSqlProcess(Connection conn) {
    }

}