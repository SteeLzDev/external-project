package com.zetra.econsig.persistence.dao;

import java.util.Properties;

import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.persistence.dao.oracle.OracleDAOFactory;
import com.zetra.econsig.report.dao.ReportDAO;

/**
 * <p>Title: DAOFactory </p>
 * <p>Description: Abstract Factory DAOFactory</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class DAOFactory {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DAOFactory.class);

    // List of DAO types supported by the factory
    private static final int MYSQL  = 1;
//  private static final int MSSQL  = 2;
    private static final int ORACLE = 3;
    private static final int PENTEST_MYSQL = 4;

    // Constante com a factory selecionada no arquivo de configuração
    public static final int FACTORY;

    static {
        // Abre o arquivo de configuração do sistema "application.properties" lê o profile
        // configurado na aplicação. Não é possível usar injeção do Spring aqui pois o
        // Custom Dialect depdende desta configuração o que gera uma dependência cruzada.
        int property = MYSQL;
        try {
            Properties env = new Properties();
            env.load(DAOFactory.class.getClassLoader().getResourceAsStream("application.properties"));
            String profile = env.getProperty("spring.profiles.active").toLowerCase();

            if (!TextHelper.isNull(System.getenv("spring.profiles.active"))) {
            	profile = System.getenv("spring.profiles.active").toLowerCase();
            }

            LOG.debug("Profile: " + profile);
            if (profile.endsWith("_oracle") || profile.endsWith("_orcl")) {
                property = ORACLE;
            } else if (profile.equals("pentest")) {
                property = MYSQL;
            } else if (!profile.endsWith("_mysql")) {
                LOG.debug("Unknow Profile: " + profile);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
        FACTORY = property;
    }

    /**
     * Retorna a fabrica de objetos de acesso aos dados
     * configurada de acordo com o arquivo de propriedades
     * do sistema
     * @return
     */
    public static DAOFactory getDAOFactory() {
        switch (FACTORY) {
            case MYSQL:
                return new MySqlDAOFactory();
            case ORACLE:
            	return new OracleDAOFactory();
            case PENTEST_MYSQL:
            	return new MySqlDAOFactory();
            default:
                return null;
        }
    }

    public static boolean isMysql() {
        return FACTORY == MYSQL;
    }

    public static boolean isOracle() {
        return FACTORY == ORACLE;
    }

    public static boolean isPentestMysql() {
        return FACTORY == MYSQL;
    }

    // There will be a method for each DAO that can be
    // created. The concrete factories will have to
    // implement these methods.
    public abstract ServidorDAO getServidorDAO();

    public abstract AutorizacaoDAO getAutorizacaoDAO();

    public abstract ParcelaDescontoDAO getParcelaDescontoDAO();

    public abstract ParametrosDAO getParametrosDAO();

    public abstract RelatorioDAO getRelatorioDAO();

    public abstract RelatorioAuditoriaDAO getRelatorioAuditoriaDAO();

    public abstract HistoricoIntegracaoDAO getHistoricoIntegracaoDAO();

    public abstract ControleSaldoDvExpMovimentoDAO getControleSaldoDvExpMovimentoDAO();

    public abstract ControleSaldoDvImpRetornoDAO getControleSaldoDvImpRetornoDAO();

    public abstract ImpRetornoDAO getImpRetornoDAO();

    public abstract HistoricoMargemDAO getHistoricoMargemDAO();

    public abstract HistoricoMovFinDAO getHistoricoMovFinDAO();

    public abstract HistoricoRetMovFinDAO getHistoricoRetMovFinDAO();

    public abstract ResultadoRegraValidacaoMovimentoDAO getResultadoRegraValidacaoMovimentoDAO();

    public abstract ParamConvenioRegistroServidorDAO getParamConvenioRegistroServidorDAO();

    public abstract ParamServicoRegistroServidorDAO getParamServicoRegistroServidorDAO();

    public abstract MargemDAO getMargemDAO();

    public abstract ReportDAO getReportDAO();

    public abstract ConsigBIDAO getConsigBIDAO();

    public abstract LogDAO getLogDAO();

    public abstract CalculoMargemDAO getCalculoMargemDAO();

    public abstract DespesaComumDAO getDespesaComumDAO();

    public abstract CalendarioFolhaDAO getCalendarioFolhaDAO();

    public abstract ArquivamentoDAO getArquivamentoDAO();

    public abstract ParamNseRegistroServidorDAO getParamNseRegistroServidorDAO();

    public abstract ParamCsaRegistroServidorDAO getParamCsaRegistroServidorDAO();

    public abstract PontuacaoServidorDAO getPontuacaoServidorDAO();

    public abstract RelatorioConciliacaoBeneficioDAO getRelatorioConciliacaoBeneficioDAO();

    public abstract BatchScriptDAO getBatchScriptDAO();

    public abstract RelatorioBeneficiariosDAO getRelatorioBeneficiariosDao();

    public abstract RelatorioConcessoesDeBeneficiosDAO getRelatorioConcessoesDeBeneficiosDAO();

    public abstract ExportaArquivoOperadoraDAO getExportaArquivoOperadoraDAO();

    public abstract ImportaArquivoRetornoOperadoraDAO getImportaArquivoRetornoOperadoraDAO();

    public abstract ArquivoFaturamentoBeneficioDAO getArquivoFaturamentoBeneficioDAO();

    public abstract ValidacaoFaturamentoBeneficioDAO getValidacaoFaturamentoBeneficioDAO();

    public abstract ImportaNotaFiscalArquivoFaturamentoBeneficioDAO getImportaNotaFiscalArquivoFaturamentoBeneficioDAO();

    public abstract ArquivoRescisaoDAO getArquivoRescisaoDAO();

    public abstract BeneficiarioDAO getBeneficiarioDAO();
    
    public abstract RegraConvenioDAO getRegraConvenioDAO();
}
