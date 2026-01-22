package com.zetra.econsig.persistence.dao.oracle;

import com.zetra.econsig.persistence.dao.ArquivamentoDAO;
import com.zetra.econsig.persistence.dao.ArquivoFaturamentoBeneficioDAO;
import com.zetra.econsig.persistence.dao.ArquivoRescisaoDAO;
import com.zetra.econsig.persistence.dao.AutorizacaoDAO;
import com.zetra.econsig.persistence.dao.BatchScriptDAO;
import com.zetra.econsig.persistence.dao.BeneficiarioDAO;
import com.zetra.econsig.persistence.dao.CalculoMargemDAO;
import com.zetra.econsig.persistence.dao.CalendarioFolhaDAO;
import com.zetra.econsig.persistence.dao.ControleSaldoDvExpMovimentoDAO;
import com.zetra.econsig.persistence.dao.ControleSaldoDvImpRetornoDAO;
import com.zetra.econsig.persistence.dao.DespesaComumDAO;
import com.zetra.econsig.persistence.dao.ExportaArquivoOperadoraDAO;
import com.zetra.econsig.persistence.dao.HistoricoIntegracaoDAO;
import com.zetra.econsig.persistence.dao.HistoricoMargemDAO;
import com.zetra.econsig.persistence.dao.HistoricoRetMovFinDAO;
import com.zetra.econsig.persistence.dao.ImpRetornoDAO;
import com.zetra.econsig.persistence.dao.ImportaArquivoRetornoOperadoraDAO;
import com.zetra.econsig.persistence.dao.ImportaNotaFiscalArquivoFaturamentoBeneficioDAO;
import com.zetra.econsig.persistence.dao.LogDAO;
import com.zetra.econsig.persistence.dao.MargemDAO;
import com.zetra.econsig.persistence.dao.ParamConvenioRegistroServidorDAO;
import com.zetra.econsig.persistence.dao.ParamNseRegistroServidorDAO;
import com.zetra.econsig.persistence.dao.ParamServicoRegistroServidorDAO;
import com.zetra.econsig.persistence.dao.ParametrosDAO;
import com.zetra.econsig.persistence.dao.ParcelaDescontoDAO;
import com.zetra.econsig.persistence.dao.RegraConvenioDAO;
import com.zetra.econsig.persistence.dao.RelatorioAuditoriaDAO;
import com.zetra.econsig.persistence.dao.RelatorioBeneficiariosDAO;
import com.zetra.econsig.persistence.dao.RelatorioConcessoesDeBeneficiosDAO;
import com.zetra.econsig.persistence.dao.RelatorioConciliacaoBeneficioDAO;
import com.zetra.econsig.persistence.dao.RelatorioDAO;
import com.zetra.econsig.persistence.dao.ResultadoRegraValidacaoMovimentoDAO;
import com.zetra.econsig.persistence.dao.ServidorDAO;
import com.zetra.econsig.persistence.dao.ValidacaoFaturamentoBeneficioDAO;
import com.zetra.econsig.persistence.dao.generic.GenericBatchScriptDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;

/**
 * <p>Title: OracleDAOFactory</p>
 * <p>Description: Factory para a criacao de DAO para Oracle.
 * A princípio irá herdar tudo do MySqlDAOFactory para não precisar
 * redefinir todos os métodos, apenas os que tem problemas de execução.
 * Quando terminar a migração para o Hibernate, somente as querys
 * problematicas irão continuar nos DAOs, com isso a herança poderá
 * ser removida.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OracleDAOFactory extends MySqlDAOFactory {

    /** TODO Remover dependência ao MySQL */

    @Override
    public ArquivamentoDAO getArquivamentoDAO() {
        return new OracleArquivamentoDAO();
    }

    @Override
    public ArquivoFaturamentoBeneficioDAO getArquivoFaturamentoBeneficioDAO() {
        return new OracleArquivoFaturamentoBeneficioDAO();
    }

    @Override
    public ArquivoRescisaoDAO getArquivoRescisaoDAO() {
        return new OracleArquivoRescisaoDAO();
    }

    @Override
    public AutorizacaoDAO getAutorizacaoDAO() {
        return new OracleAutorizacaoDAO();
    }

    @Override
    public BatchScriptDAO getBatchScriptDAO() {
        return new GenericBatchScriptDAO();
    }

    @Override
    public CalendarioFolhaDAO getCalendarioFolhaDAO() {
        return new OracleCalendarioFolhaDAO();
    }

    @Override
    public CalculoMargemDAO getCalculoMargemDAO() {
        return new OracleCalculoMargemDAO();
    }

    @Override
    public ControleSaldoDvExpMovimentoDAO getControleSaldoDvExpMovimentoDAO() {
        return new OracleControleSaldoDvExpMovimentoDAO();
    }

    @Override
    public ControleSaldoDvImpRetornoDAO getControleSaldoDvImpRetornoDAO() {
        return new OracleControleSaldoDvImpRetornoDAO();
    }

    @Override
    public DespesaComumDAO getDespesaComumDAO() {
        return new OracleDespesaComumDAO();
    }

    @Override
    public ExportaArquivoOperadoraDAO getExportaArquivoOperadoraDAO() {
        return new OracleExportaArquivoOperadoraDAO();
    }

    @Override
    public HistoricoIntegracaoDAO getHistoricoIntegracaoDAO() {
        return new OracleHistoricoIntegracaoDAO();
    }

    @Override
    public HistoricoMargemDAO getHistoricoMargemDAO() {
        return new OracleHistoricoMargemDAO();
    }

    @Override
    public HistoricoRetMovFinDAO getHistoricoRetMovFinDAO() {
        return new OracleHistoricoRetMovFinDAO();
    }

    @Override
    public ImportaArquivoRetornoOperadoraDAO getImportaArquivoRetornoOperadoraDAO() {
        return new OracleImportaArquivoRetornoOperadoraDAO();
    }

    @Override
    public ImportaNotaFiscalArquivoFaturamentoBeneficioDAO getImportaNotaFiscalArquivoFaturamentoBeneficioDAO() {
        return new OracleImportaNotaFiscalArquivoFaturamentoBeneficioDAO();
    }

    @Override
    public ImpRetornoDAO getImpRetornoDAO() {
        return new OracleImpRetornoDAO();
    }

    @Override
    public LogDAO getLogDAO() {
        return new OracleLogDAO();
    }

    @Override
    public MargemDAO getMargemDAO() {
        return new OracleMargemDAO();
    }

    @Override
    public ParametrosDAO getParametrosDAO() {
        return new OracleParametrosDAO();
    }

    @Override
    public ParamConvenioRegistroServidorDAO getParamConvenioRegistroServidorDAO() {
        return new OracleParamConvenioRegistroServidorDAO();
    }

    @Override
    public ParamNseRegistroServidorDAO getParamNseRegistroServidorDAO() {
        return new OracleParamNseRegistroServidorDAO();
    }

    @Override
    public ParamServicoRegistroServidorDAO getParamServicoRegistroServidorDAO() {
        return new OracleParamServicoRegistroServidorDAO();
    }

    @Override
    public ParcelaDescontoDAO getParcelaDescontoDAO() {
        return new OracleParcelaDescontoDAO();
    }

    @Override
    public RelatorioAuditoriaDAO getRelatorioAuditoriaDAO() {
        return new OracleRelatorioAuditoriaDAO();
    }

    @Override
    public RelatorioBeneficiariosDAO getRelatorioBeneficiariosDao() {
        return new OracleRelatorioBeneficiariosDAO();
    }

    @Override
    public RelatorioConcessoesDeBeneficiosDAO getRelatorioConcessoesDeBeneficiosDAO() {
        return new OracleRelatorioConcessoesDeBeneficiosDAO();
    }

    @Override
    public RelatorioConciliacaoBeneficioDAO getRelatorioConciliacaoBeneficioDAO() {
        return new OracleRelatorioConciliacaoBeneficioDAO();
    }

    @Override
    public ResultadoRegraValidacaoMovimentoDAO getResultadoRegraValidacaoMovimentoDAO() {
        return new OracleResultadoRegraValidacaoMovimentoDAO();
    }

    @Override
    public ServidorDAO getServidorDAO() {
        return new OracleServidorDAO();
    }

    @Override
    public ValidacaoFaturamentoBeneficioDAO getValidacaoFaturamentoBeneficioDAO() {
        return new OracleValidacaoFaturamentoBeneficioDAO();
    }
    
    @Override
    public BeneficiarioDAO getBeneficiarioDAO() {
        return new OracleBeneficiariosDAO();
    }
    
    @Override
    public RelatorioDAO getRelatorioDAO() {
        return new OracleRelatorioDAO();
    }
    
    @Override
    public RegraConvenioDAO getRegraConvenioDAO() {
        return new OracleRegraConvenioDAO();
    }
}
