package com.zetra.econsig.report.reports;

import java.sql.Connection;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.RelatorioAuditoriaDAO;

/**
 * <p> Title: RelatorioAuditoria</p>
 * <p> Description: Relatório de Auditoria</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioAuditoria extends ReportTemplate {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RelatorioAuditoria.class);

    public RelatorioAuditoria() {
    }

    @Override
    public String getSql(CustomTransferObject criterio) throws DAOException {
        try {
            RelatorioAuditoriaDAO relatorioDAO = DAOFactory.getDAOFactory().getRelatorioAuditoriaDAO();
            return relatorioDAO.select(getParameters(), getQueryParams());
        } catch (Exception e) {
            LOG.debug("Não foi possível gerar o Relatório de Auditoria.", e);
            if (e instanceof DAOException) {
                throw (DAOException)e;
            } else  {
                throw new DAOException("mensagem.erro.gerar.relatorio.auditoria", (AcessoSistema) null, e);
            }
        }
    }

    @Override
    public void preSqlProcess(Connection conn){
        LOG.debug("Pre sql process!!!");
        try {
            RelatorioAuditoriaDAO relatorioDAO = DAOFactory.getDAOFactory().getRelatorioAuditoriaDAO();
            relatorioDAO.preparaDadosRelatorio(getParameters());
        } catch (Exception e) {
            LOG.debug("Não foi possível gerar o Relatório de Auditoria.", e);
        }
    }

    @Override
    public void postSqlProcess(Connection conn){
        LOG.debug("Pos sql process!!!");
        // TODO Chamar excluir tabela e/ou guardar resultados
    }

}
