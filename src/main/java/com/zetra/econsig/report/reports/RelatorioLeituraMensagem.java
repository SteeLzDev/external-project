package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioLeituraMensagemQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;


/**
 * <p> Title: RelatorioLeituraMensagem</p>
 * <p> Description: Relatório de Confirmações de Leituras de Mensagens</p>
 * <p> Copyright: Copyright (c) 2015 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioLeituraMensagem extends ReportTemplate {

    public RelatorioLeituraMensagem() {
        hqueries = new ReportHQuery [] {new RelatorioLeituraMensagemQuery()};
    }


    @Override
    public void preSqlProcess(Connection conn) {
    }

    @Override
    public void postSqlProcess(Connection conn) {
    }

    @Override
    @Deprecated
    public String getSql(CustomTransferObject criterio) throws DAOException {
        return null;
    }

}
