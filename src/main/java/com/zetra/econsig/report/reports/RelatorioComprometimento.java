package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioComprometimentoQuery;

/**
 * <p> Title: RelatorioComprometimento</p>
 * <p> Description: Relatório de Comprometimento da Margem</p>
 * <p> Copyright: Copyright (c) 2008 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioComprometimento extends ReportTemplate {

    public RelatorioComprometimento() {
        hqueries = new ReportHNativeQuery[] {new RelatorioComprometimentoQuery()};
    }

    @Override
    public void setResponsavel(AcessoSistema responsavel) {
        super.setResponsavel(responsavel);
        ((RelatorioComprometimentoQuery) hqueries[0]).responsavel = this.responsavel;
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
