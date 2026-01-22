package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.relatorio.RelatorioGerencialCseQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p> Title: RelatorioTermoUsoPrivacidade</p>
 * <p> Description: politica de privacidade em PDF</p>
 * <p> Copyright: Copyright (c) 2017 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author: rodrigo $
 * $Revision: 21157 $
 * $Date: 2017-03-29 11:30:06 -0300 (qua, 29 mar 2017) $
 */
public class PoliticaPrivacidade extends ReportTemplate {

    public PoliticaPrivacidade() {
        hqueries = new ReportHQuery [] {new RelatorioGerencialCseQuery()};
    }

    @Override
    public void setResponsavel(AcessoSistema responsavel) {
        super.setResponsavel(responsavel);
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
