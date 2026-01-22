package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioBeneficiarioDataNascimentoQuery;
import com.zetra.econsig.persistence.query.relatorio.ReportHQuery;

/**
 * <p>Title: RelatorioBeneficiarioDataNascimento</p>
 * <p>Description: Classe que define consultas a serem aplicadas ao Relatório de Beneficiário por Data Nascimento.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioBeneficiarioDataNascimento extends ReportTemplate {

    public RelatorioBeneficiarioDataNascimento() {
        hqueries = new ReportHQuery [] {new RelatorioBeneficiarioDataNascimentoQuery()};
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
