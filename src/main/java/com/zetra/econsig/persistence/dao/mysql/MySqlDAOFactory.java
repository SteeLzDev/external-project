package com.zetra.econsig.persistence.dao.mysql;

import com.zetra.econsig.persistence.dao.ArquivamentoDAO;
import com.zetra.econsig.persistence.dao.ArquivoFaturamentoBeneficioDAO;
import com.zetra.econsig.persistence.dao.ArquivoRescisaoDAO;
import com.zetra.econsig.persistence.dao.AutorizacaoDAO;
import com.zetra.econsig.persistence.dao.BatchScriptDAO;
import com.zetra.econsig.persistence.dao.BeneficiarioDAO;
import com.zetra.econsig.persistence.dao.CalculoMargemDAO;
import com.zetra.econsig.persistence.dao.CalendarioFolhaDAO;
import com.zetra.econsig.persistence.dao.ConsigBIDAO;
import com.zetra.econsig.persistence.dao.ControleSaldoDvExpMovimentoDAO;
import com.zetra.econsig.persistence.dao.ControleSaldoDvImpRetornoDAO;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.DespesaComumDAO;
import com.zetra.econsig.persistence.dao.ExportaArquivoOperadoraDAO;
import com.zetra.econsig.persistence.dao.HistoricoIntegracaoDAO;
import com.zetra.econsig.persistence.dao.HistoricoMargemDAO;
import com.zetra.econsig.persistence.dao.HistoricoMovFinDAO;
import com.zetra.econsig.persistence.dao.HistoricoRetMovFinDAO;
import com.zetra.econsig.persistence.dao.ImpRetornoDAO;
import com.zetra.econsig.persistence.dao.ImportaArquivoRetornoOperadoraDAO;
import com.zetra.econsig.persistence.dao.ImportaNotaFiscalArquivoFaturamentoBeneficioDAO;
import com.zetra.econsig.persistence.dao.LogDAO;
import com.zetra.econsig.persistence.dao.MargemDAO;
import com.zetra.econsig.persistence.dao.ParamConvenioRegistroServidorDAO;
import com.zetra.econsig.persistence.dao.ParamCsaRegistroServidorDAO;
import com.zetra.econsig.persistence.dao.ParamNseRegistroServidorDAO;
import com.zetra.econsig.persistence.dao.ParamServicoRegistroServidorDAO;
import com.zetra.econsig.persistence.dao.ParametrosDAO;
import com.zetra.econsig.persistence.dao.ParcelaDescontoDAO;
import com.zetra.econsig.persistence.dao.PontuacaoServidorDAO;
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
import com.zetra.econsig.report.dao.GenericReportDAO;
import com.zetra.econsig.report.dao.ReportDAO;

/**
 * <p>Title: MySqlDAOFactory</p>
 * <p>Description: Factory para a criacao de DAO para MySql</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlDAOFactory extends DAOFactory {

    public static final String SEPARADOR = ",";

    @Override
    public ServidorDAO getServidorDAO() {
        return new MySqlServidorDAO();
    }

    @Override
    public AutorizacaoDAO getAutorizacaoDAO() {
        return new MySqlAutorizacaoDAO();
    }

    @Override
    public ParametrosDAO getParametrosDAO() {
        return new MySqlParametrosDAO();
    }

    @Override
    public ParcelaDescontoDAO getParcelaDescontoDAO() {
        return new MySqlParcelaDescontoDAO();
    }

    @Override
    public HistoricoIntegracaoDAO getHistoricoIntegracaoDAO() {
        return new MySqlHistoricoIntegracaoDAO();
    }

    @Override
    public RelatorioDAO getRelatorioDAO() {
        return new MySqlRelatorioDAO();
    }

    @Override
    public RelatorioAuditoriaDAO getRelatorioAuditoriaDAO() {
        return new MySqlRelatorioAuditoriaDAO();
    }

    @Override
    public ControleSaldoDvExpMovimentoDAO getControleSaldoDvExpMovimentoDAO() {
        return new MySqlControleSaldoDvExpMovimentoDAO();
    }

    @Override
    public ControleSaldoDvImpRetornoDAO getControleSaldoDvImpRetornoDAO() {
        return new MySqlControleSaldoDvImpRetornoDAO();
    }

    @Override
    public ImpRetornoDAO getImpRetornoDAO() {
        return new MySqlImpRetornoDAO();
    }

    @Override
    public HistoricoMargemDAO getHistoricoMargemDAO() {
        return new MySqlHistoricoMargemDAO();
    }

    @Override
    public HistoricoMovFinDAO getHistoricoMovFinDAO() {
        return new MySqlHistoricoMovFinDAO();
    }

    @Override
    public HistoricoRetMovFinDAO getHistoricoRetMovFinDAO() {
        return new MySqlHistoricoRetMovFinDAO();
    }

    @Override
    public ResultadoRegraValidacaoMovimentoDAO getResultadoRegraValidacaoMovimentoDAO() {
        return new MySqlResultadoRegraValidacaoMovimentoDAO();
    }

    @Override
    public ParamConvenioRegistroServidorDAO getParamConvenioRegistroServidorDAO() {
        return new MySqlParamConvenioRegistroServidorDAO();
    }

    @Override
    public ParamServicoRegistroServidorDAO getParamServicoRegistroServidorDAO() {
        return new MySqlParamServicoRegistroServidorDAO();
    }

    @Override
    public MargemDAO getMargemDAO() {
        return new MySqlMargemDAO();
    }

    @Override
    public ReportDAO getReportDAO() {
        return new GenericReportDAO();
    }

    @Override
    public ConsigBIDAO getConsigBIDAO() {
        return new MySqlConsigBIDAO();
    }

    @Override
    public LogDAO getLogDAO() {
        return new MySqlLogDAO();
    }

    @Override
    public CalculoMargemDAO getCalculoMargemDAO() {
        return new MySqlCalculoMargemDAO();
    }

    @Override
    public DespesaComumDAO getDespesaComumDAO() {
        return new MySqlDespesaComumDAO();
    }

    @Override
    public CalendarioFolhaDAO getCalendarioFolhaDAO() {
        return new MySqlCalendarioFolhaDAO();
    }

    @Override
    public ArquivamentoDAO getArquivamentoDAO() {
        return new MySqlArquivamentoDAO();
    }

    @Override
    public ParamNseRegistroServidorDAO getParamNseRegistroServidorDAO() {
        return new MySqlParamNseRegistroServidorDAO();
    }

    @Override
    public PontuacaoServidorDAO getPontuacaoServidorDAO() {
        return new MySqlPontuacaoServidorDAO();
    }

    @Override
    public RelatorioConciliacaoBeneficioDAO getRelatorioConciliacaoBeneficioDAO() {
        return new MySqlRelatorioConciliacaoBeneficioDAO();
    }

    @Override
    public BatchScriptDAO getBatchScriptDAO() {
        return new GenericBatchScriptDAO();
    }

    @Override
    public RelatorioBeneficiariosDAO getRelatorioBeneficiariosDao() {
        return new MySqlRelatorioBeneficiariosDAO();
    }

    @Override
    public RelatorioConcessoesDeBeneficiosDAO getRelatorioConcessoesDeBeneficiosDAO() {
        return new MySqlRelatorioConcessoesDeBeneficiosDAO();
    }

    @Override
    public ExportaArquivoOperadoraDAO getExportaArquivoOperadoraDAO() {
        return new MysqlExportaArquivoOperadoraDAO();
    }

    @Override
    public ImportaArquivoRetornoOperadoraDAO getImportaArquivoRetornoOperadoraDAO() {
        return new MysqlImportaArquivoRetornoOperadoraDAO();
    }

    @Override
    public ArquivoFaturamentoBeneficioDAO getArquivoFaturamentoBeneficioDAO() {
        return new MySqlArquivoFaturamentoBeneficioDAO();
    }

    @Override
    public ValidacaoFaturamentoBeneficioDAO getValidacaoFaturamentoBeneficioDAO() {
        return new MySqlValidacaoFaturamentoBeneficioDAO();
    }

    @Override
    public ImportaNotaFiscalArquivoFaturamentoBeneficioDAO getImportaNotaFiscalArquivoFaturamentoBeneficioDAO() {
        return new MySqlImportaNotaFiscalArquivoFaturamentoBeneficioDAO();
    }

    @Override
    public ArquivoRescisaoDAO getArquivoRescisaoDAO() {
        return new MySqlArquivoRescisaoDAO();
    }

    @Override
    public BeneficiarioDAO getBeneficiarioDAO() {
        return new MysqlBeneficiariosDAO();
    }

	@Override
	public ParamCsaRegistroServidorDAO getParamCsaRegistroServidorDAO() {
		return new MySqlParamCsaRegistroServidorDAO();
	}
	
	@Override
	public RegraConvenioDAO getRegraConvenioDAO() {
		return new MySqlRegraConvenioDAO();
	}
}
