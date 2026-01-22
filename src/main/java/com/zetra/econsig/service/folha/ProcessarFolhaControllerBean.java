package com.zetra.econsig.service.folha;

import static com.zetra.econsig.service.folha.ImpRetornoControllerBean.ENTRADA_IMP_RETORNO;
import static com.zetra.econsig.service.folha.ImpRetornoControllerBean.NOME_ARQUIVO_ENTRADA;
import static com.zetra.econsig.service.folha.ImpRetornoControllerBean.TRADUTOR_IMP_RETORNO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.google.gson.Gson;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.folha.especifica.ProcessaFolhaEspecifica;
import com.zetra.econsig.folha.especifica.ProcessaFolhaEspecificaFactory;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.criptografia.CriptografiaArquivos;
import com.zetra.econsig.helper.folha.CacheDependenciasServidor;
import com.zetra.econsig.helper.folha.ProcessaRetorno;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaBlocosProcessamentoFolha;
import com.zetra.econsig.job.process.ProcessaDesfazProcessamentoFolha;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.ITradutor;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTextoSimpletl;
import com.zetra.econsig.parser.LeitorArquivoTextoZip;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.parser.TradutorSimpletl;
import com.zetra.econsig.parser.config.ParametroTipo;
import com.zetra.econsig.persistence.BatchManager;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.dao.AutorizacaoDAO;
import com.zetra.econsig.persistence.dao.CalculoMargemDAO;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.HistoricoRetMovFinDAO;
import com.zetra.econsig.persistence.dao.ImpRetornoDAO;
import com.zetra.econsig.persistence.dao.ParcelaDescontoDAO;
import com.zetra.econsig.persistence.dao.ServidorDAO;
import com.zetra.econsig.persistence.entity.BlocoProcessamentoHome;
import com.zetra.econsig.persistence.entity.Estabelecimento;
import com.zetra.econsig.persistence.entity.EstabelecimentoHome;
import com.zetra.econsig.persistence.entity.HistoricoMargemRegistroServidorHome;
import com.zetra.econsig.persistence.entity.HistoricoMediaMargem;
import com.zetra.econsig.persistence.entity.HistoricoMediaMargemHome;
import com.zetra.econsig.persistence.entity.HistoricoMediaMargemId;
import com.zetra.econsig.persistence.entity.HistoricoProcMargem;
import com.zetra.econsig.persistence.entity.HistoricoProcMargemCseHome;
import com.zetra.econsig.persistence.entity.HistoricoProcMargemEstHome;
import com.zetra.econsig.persistence.entity.HistoricoProcMargemHome;
import com.zetra.econsig.persistence.entity.HistoricoProcMargemOrgHome;
import com.zetra.econsig.persistence.entity.HistoricoProcessamento;
import com.zetra.econsig.persistence.entity.HistoricoProcessamentoHome;
import com.zetra.econsig.persistence.entity.MargemRegistroServidor;
import com.zetra.econsig.persistence.entity.MargemRegistroServidorHome;
import com.zetra.econsig.persistence.entity.MargemRegistroServidorId;
import com.zetra.econsig.persistence.entity.OcorrenciaRegistroServidorHome;
import com.zetra.econsig.persistence.entity.Orgao;
import com.zetra.econsig.persistence.entity.OrgaoHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoPrdSemRetornoQuery;
import com.zetra.econsig.persistence.query.folha.ListarCodigosCsaComConvenioAtivoQuery;
import com.zetra.econsig.persistence.query.folha.ListarConveniosProcessamentoQuery;
import com.zetra.econsig.persistence.query.folha.ListarHistoricoProcMargemProcessamentoQuery;
import com.zetra.econsig.persistence.query.folha.ListarHistoricoProcessamentoQuery;
import com.zetra.econsig.persistence.query.folha.ListarLinhasBlocosProcessamentoQuery;
import com.zetra.econsig.persistence.query.folha.ListarLinhasBlocosSemProcessamentoQuery;
import com.zetra.econsig.persistence.query.folha.ListarRegistroServidorProcessadoQuery;
import com.zetra.econsig.persistence.query.folha.ListarRegistroServidorSemBlocoProcessamentoQuery;
import com.zetra.econsig.persistence.query.folha.ListarServidoresProcessamentoQuery;
import com.zetra.econsig.persistence.query.folha.ObtemBlocoQuery;
import com.zetra.econsig.persistence.query.folha.ObtemBlocosProcessamentoAgrupadosQuery;
import com.zetra.econsig.persistence.query.folha.ObtemBlocosProcessamentoRegistroSerQuery;
import com.zetra.econsig.persistence.query.folha.ObtemConvenioProcessamentoQuery;
import com.zetra.econsig.persistence.query.folha.ObtemTotalBlocosProcessamentoQuery;
import com.zetra.econsig.persistence.query.folha.ObtemTotalParcelasPeriodoProcessamentoQuery;
import com.zetra.econsig.persistence.query.margem.ListaHistoricoMargemQuery;
import com.zetra.econsig.persistence.query.margem.ListaHistoricoMediaMargemQuery;
import com.zetra.econsig.persistence.query.margem.ListaMargemRegistroServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ListaRegistroServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ObtemRegistroServidorQuery;
import com.zetra.econsig.persistence.query.servidor.RecuperaMargemExtraMediaTotalQuery;
import com.zetra.econsig.persistence.query.servidor.RecuperaMargemMediaTotalQuery;
import com.zetra.econsig.service.compra.CompraContratoController;
import com.zetra.econsig.service.consignacao.SuspenderConsignacaoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoHistoricoMargemEnum;
import com.zetra.econsig.values.StatusBlocoProcessamentoEnum;
import com.zetra.econsig.values.TipoBlocoProcessamentoEnum;

/**
 * <p>Title: ProcessarFolhaControllerBean</p>
 * <p>Description: Controlador para processamento do resultado da folha: margem, retorno, etc.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ProcessarFolhaControllerBean implements ProcessarFolhaController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessarFolhaControllerBean.class);

    private static final String PERIODO = "PERIODO";

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private MargemController margemController;

    @Autowired
    private CompraContratoController compraContratoController;

    @Autowired
    private ExportaMovimentoController exportaMovimentoController;

    @Autowired
    private ImpRetornoController impRetornoController;

    @Autowired
    private ImpCadMargemController impCadMargemController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private RelatorioController relatorioController;

    @Autowired
    private SuspenderConsignacaoController suspenderConsignacaoController;

    @Override
    public void prepararProcessamento(String nomeArquivoMargem, String nomeArquivoRetorno, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ZetraException {
        Connection h2Conn = null;
        PreparedStatement h2PreStatSer = null;
        PreparedStatement h2PreStatCnvComVerba = null;
        PreparedStatement h2PreStatCnvSemVerba = null;
        ProcessaFolhaEspecifica classeEspecificaPython = null;
        try {

            String exportadorClassPython = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CLASSE_ESPECIFICA_PROCESSAMENTO_SEM_BLOQUEIO, responsavel);
            if (!TextHelper.isNull(exportadorClassPython)) {
                classeEspecificaPython = ProcessaFolhaEspecificaFactory.getExportador(exportadorClassPython);
            }

            if (classeEspecificaPython != null) {
                String tipoCodigoEntidade = tipoEntidade + "-" + codigoEntidade;
                classeEspecificaPython.prePrepararProcessamento(nomeArquivoMargem, nomeArquivoRetorno, tipoCodigoEntidade, responsavel.getUsuCodigo());
            }

            String arquivoMargem = ProcessaRetorno.obtemArquivoProcessamento(ProcessaRetorno.MARGEM, nomeArquivoMargem, tipoEntidade, codigoEntidade, responsavel);
            String arquivoRetorno = ProcessaRetorno.obtemArquivoProcessamento(ProcessaRetorno.RETORNO, nomeArquivoRetorno, tipoEntidade, codigoEntidade, responsavel);

            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.folha.arquivo.margem.nome", responsavel, arquivoMargem));
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.folha.arquivo.retorno.nome", responsavel, arquivoRetorno));

            // Inicializa a lista de códigos de entidades
            List<String> orgCodigos = (AcessoSistema.ENTIDADE_ORG.equalsIgnoreCase(tipoEntidade) ? Arrays.asList(codigoEntidade) : null);
            List<String> estCodigos = (AcessoSistema.ENTIDADE_EST.equalsIgnoreCase(tipoEntidade) ? Arrays.asList(codigoEntidade) : null);

            // Calcula o período, e recupera a data base (mês/ano)
            String periodoRetorno = impRetornoController.recuperaPeriodoRetorno(CodedValues.TIPO_RETORNO_NORMAL, null, orgCodigos, estCodigos, responsavel);
            Date periodo = DateHelper.parseExceptionSafe(periodoRetorno, "yyyy-MM-dd");

            // Executa as validações de período, parcelas e verbas
            exportaMovimentoController.validarExportacaoMovimento(orgCodigos, estCodigos, true, responsavel);

            // Valida se existem blocos de períodos anteriores ainda não processados
            ObtemTotalBlocosProcessamentoQuery totalBlocosQuery = new ObtemTotalBlocosProcessamentoQuery();
            totalBlocosQuery.tipoEntidade = tipoEntidade;
            totalBlocosQuery.codigoEntidade = codigoEntidade;
            totalBlocosQuery.sbpCodigos = Arrays.asList(
                    StatusBlocoProcessamentoEnum.PREPARANDO.getCodigo(),
                    StatusBlocoProcessamentoEnum.AGUARD_PROCESSAMENTO.getCodigo(),
                    StatusBlocoProcessamentoEnum.EM_PROCESSAMENTO.getCodigo());
            int qtdBlocos = totalBlocosQuery.executarContador();
            if (qtdBlocos > 0) {
                throw new ZetraException("mensagem.erro.processar.folha.blocos.nao.processados", responsavel);
            }

            // Remove os blocos do processamento anterior
            BlocoProcessamentoHome.removerBlocos(tipoEntidade, codigoEntidade);

            // Valida se não há outro processo de preparação ou execução em andamento
            if (ControladorProcessos.getInstance().existeProcessoAtivo()) {
                throw new ZetraException("mensagem.erro.processar.folha.processo.em.execucao", responsavel);
            }
            // Suspende execução dos demais processos paralelos
            ControladorProcessos.getInstance().suspenderProcessos();

            // Abre conexão com banco H2 em memória para criação de cache
            Class.forName("org.h2.Driver");
            h2Conn = DriverManager.getConnection("jdbc:h2:mem:myDb", "sa", "sa");

            // Constrói os caches de servidor e convenio
            criarCacheServidores(tipoEntidade, codigoEntidade, h2Conn, responsavel);
            criarCacheConvenios(tipoEntidade, codigoEntidade, h2Conn, responsavel);
            h2PreStatSer = criarStatementBuscaServidor(h2Conn, responsavel);
            h2PreStatCnvComVerba = criarStatementBuscaConvenioPorVerba(h2Conn, responsavel);
            h2PreStatCnvSemVerba = criarStatementBuscaConvenioSemVerba(h2Conn, responsavel);

            // Cria registro de histórico de processamento
            HistoricoProcessamento historicoProcessamento = new HistoricoProcessamento();
            historicoProcessamento.setHprPeriodo(periodo);

            // Executa limpeza da tabela de arquivo retorno
            boolean mantemArqRetorno = ParamSist.paramEquals(CodedValues.TPC_MANTEM_ARMAZENADO_ARQ_RETORNO, CodedValues.TPC_SIM, responsavel);
            impRetornoController.iniciarCargaArquivoRetorno(nomeArquivoRetorno, mantemArqRetorno, orgCodigos, estCodigos, null, responsavel);

            // Carrega os blocos de margem e retorno
            carregarBlocosMargem(arquivoMargem, tipoEntidade, codigoEntidade, periodo, h2PreStatSer, historicoProcessamento, responsavel);
            carregarBlocosRetorno(arquivoRetorno, tipoEntidade, codigoEntidade, periodo, h2PreStatSer, h2PreStatCnvComVerba, h2PreStatCnvSemVerba, historicoProcessamento, responsavel);

            // Realiza as validações sobre os blocos de margem e retorno são carregados
            validarBlocosMargem(tipoEntidade, codigoEntidade, responsavel);
            validarBlocosRetorno(tipoEntidade, codigoEntidade, periodo, responsavel);

            // Salva o histórico de processamento
            historicoProcessamento = HistoricoProcessamentoHome.create(historicoProcessamento, tipoEntidade, codigoEntidade);

            // Pelas verbas mapeadas gera os relatórios de integração folha das consignatárias. Neste momento somente o relatório padrão, que corresponde ao leiaute do retorno, será gerado.
            gerarRelatorioIntegracao(periodo, tipoEntidade, codigoEntidade, responsavel);

            // Inicia o histórico de conclusão de retorno para o período do processamento.
            HistoricoRetMovFinDAO hrmDAO = DAOFactory.getDAOFactory().getHistoricoRetMovFinDAO();
            hrmDAO.iniciarHistoricoConclusaoRetorno(orgCodigos, estCodigos, periodoRetorno, null);

            // Inicia o histórico de processamento de margem
            iniciarHistoricoMargem(periodoRetorno, tipoEntidade, codigoEntidade, responsavel);

            // Altera os blocos para status 2 - Aguard. Processamento, e termina o processo corrente.
            BlocoProcessamentoHome.atualizarStatusBlocos(StatusBlocoProcessamentoEnum.PREPARANDO, StatusBlocoProcessamentoEnum.AGUARD_PROCESSAMENTO, tipoEntidade, codigoEntidade);

            if (classeEspecificaPython != null) {
                String tipoCodigoEntidade = tipoEntidade + "-" + codigoEntidade;
                classeEspecificaPython.posPrepararProcessamento(nomeArquivoMargem, nomeArquivoRetorno, tipoCodigoEntidade, responsavel.getUsuCodigo());
            }

            // Ao final do processo de preparação, iniciar o processo de execução.
            ProcessaBlocosProcessamentoFolha processo = new ProcessaBlocosProcessamentoFolha(historicoProcessamento, responsavel);
            processo.start();
            ControladorProcessos.getInstance().incluir(ProcessaBlocosProcessamentoFolha.CHAVE, processo);

        } catch (SQLException | ClassNotFoundException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);

        } catch (ZetraException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw ex;

        } finally {
            // Reativa a execução dos demais processos paralelos
            ControladorProcessos.getInstance().reativarProcessos();

            DBHelper.closeStatement(h2PreStatSer);
            DBHelper.closeStatement(h2PreStatCnvComVerba);
            DBHelper.closeStatement(h2PreStatCnvSemVerba);
            DBHelper.releaseConnection(h2Conn);
        }
    }

    /**
     * Cria uma tabela em um banco de dados de memória para buscar e validar os dados de servidor com os informados no arquivo
     * @param tipoEntidade
     * @param codigoEntidade
     * @param h2Conn
     * @param responsavel
     * @throws ZetraException
     */
    private void criarCacheServidores(String tipoEntidade, String codigoEntidade, Connection h2Conn, AcessoSistema responsavel) throws ZetraException {
        Statement h2Stat = null;
        PreparedStatement h2PreStat = null;
        try {
            h2Stat = h2Conn.createStatement();

            StringBuilder sql = new StringBuilder();
            sql.append("DROP TABLE servidores IF EXISTS");
            LOG.debug(sql.toString());
            h2Stat.execute(sql.toString());

            sql.setLength(0);
            sql.append("CREATE TABLE servidores (");
            sql.append("  RSE_CODIGO varchar(32) NOT NULL,");
            sql.append("  RSE_MATRICULA varchar(20) NOT NULL,");
            sql.append("  SRS_CODIGO varchar(32) NOT NULL,");
            sql.append("  SER_CODIGO varchar(32) NOT NULL,");
            sql.append("  SER_CPF varchar(19) NOT NULL,");
            sql.append("  EST_CODIGO varchar(32) NOT NULL,");
            sql.append("  EST_IDENTIFICADOR varchar(40) NOT NULL,");
            sql.append("  EST_CNPJ varchar(19),");
            sql.append("  ORG_CODIGO varchar(32) NOT NULL,");
            sql.append("  ORG_IDENTIFICADOR varchar(40) NOT NULL,");
            sql.append("  ORG_CNPJ varchar(19),");
            sql.append("  PRIMARY KEY (RSE_CODIGO)");
            sql.append(")");
            LOG.debug(sql.toString());
            h2Stat.execute(sql.toString());

            sql.setLength(0);
            sql.append("CREATE INDEX IX01 ON servidores (RSE_MATRICULA)");
            LOG.debug(sql.toString());
            h2Stat.execute(sql.toString());

            sql.setLength(0);
            sql.append("INSERT INTO servidores (RSE_CODIGO, RSE_MATRICULA, SRS_CODIGO, SER_CODIGO, SER_CPF, EST_CODIGO, EST_IDENTIFICADOR, EST_CNPJ, ORG_CODIGO, ORG_IDENTIFICADOR, ORG_CNPJ) ");
            sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
            h2PreStat = h2Conn.prepareStatement(sql.toString());

            ListarServidoresProcessamentoQuery queryServidor = new ListarServidoresProcessamentoQuery();
            queryServidor.tipoEntidade = tipoEntidade;
            queryServidor.codigoEntidade = codigoEntidade;
            List<TransferObject> servidores = queryServidor.executarDTO();
            if (servidores != null && !servidores.isEmpty()) {
                LOG.debug("Servidores encontrados: " + servidores.size());
                for (TransferObject servidor : servidores) {
                    h2PreStat.setString(1,  (String) servidor.getAttribute(Columns.RSE_CODIGO));
                    h2PreStat.setString(2,  (String) servidor.getAttribute(Columns.RSE_MATRICULA));
                    h2PreStat.setString(3,  (String) servidor.getAttribute(Columns.SRS_CODIGO));
                    h2PreStat.setString(4,  (String) servidor.getAttribute(Columns.SER_CODIGO));
                    h2PreStat.setString(5,  (String) servidor.getAttribute(Columns.SER_CPF));
                    h2PreStat.setString(6,  (String) servidor.getAttribute(Columns.EST_CODIGO));
                    h2PreStat.setString(7,  (String) servidor.getAttribute(Columns.EST_IDENTIFICADOR));
                    h2PreStat.setString(8,  (String) servidor.getAttribute(Columns.EST_CNPJ));
                    h2PreStat.setString(9,  (String) servidor.getAttribute(Columns.ORG_CODIGO));
                    h2PreStat.setString(10, (String) servidor.getAttribute(Columns.ORG_IDENTIFICADOR));
                    h2PreStat.setString(11, (String) servidor.getAttribute(Columns.ORG_CNPJ));
                    h2PreStat.execute();
                }
            }
        } catch (SQLException ex) {
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {
            DBHelper.closeStatement(h2Stat);
            DBHelper.closeStatement(h2PreStat);
        }
    }

    /**
     * Cria uma tabela em um banco de dados de memória para buscar e validar os dados de convênio com os informados no arquivo
     * @param tipoEntidade
     * @param codigoEntidade
     * @param h2Conn
     * @throws ZetraException
     */
    private void criarCacheConvenios(String tipoEntidade, String codigoEntidade, Connection h2Conn, AcessoSistema responsavel) throws ZetraException {
        Statement h2Stat = null;
        PreparedStatement h2PreStat = null;
        try {
            h2Stat = h2Conn.createStatement();

            StringBuilder sql = new StringBuilder();
            sql.append("DROP TABLE convenios IF EXISTS");
            LOG.debug(sql.toString());
            h2Stat.execute(sql.toString());

            sql.setLength(0);
            sql.append("CREATE TABLE convenios (");
            sql.append("  CNV_CODIGO varchar(32) NOT NULL,");
            sql.append("  CNV_COD_VERBA varchar(32) NOT NULL,");
            sql.append("  SVC_CODIGO varchar(32) NOT NULL,");
            sql.append("  SVC_IDENTIFICADOR varchar(40) NOT NULL,");
            sql.append("  CSA_CODIGO varchar(32) NOT NULL,");
            sql.append("  CSA_IDENTIFICADOR varchar(40) NOT NULL,");
            sql.append("  CSA_CNPJ varchar(19),");
            sql.append("  EST_CODIGO varchar(32) NOT NULL,");
            sql.append("  EST_IDENTIFICADOR varchar(40) NOT NULL,");
            sql.append("  EST_CNPJ varchar(19),");
            sql.append("  ORG_CODIGO varchar(32) NOT NULL,");
            sql.append("  ORG_IDENTIFICADOR varchar(40) NOT NULL,");
            sql.append("  ORG_CNPJ varchar(19),");
            sql.append("  PRIMARY KEY (CNV_CODIGO)");
            sql.append(")");
            LOG.debug(sql.toString());
            h2Stat.execute(sql.toString());

            sql.setLength(0);
            sql.append("CREATE INDEX IX02 ON convenios (CNV_COD_VERBA)");
            LOG.debug(sql.toString());
            h2Stat.execute(sql.toString());

            sql.setLength(0);
            sql.append("INSERT INTO convenios (CNV_CODIGO, CNV_COD_VERBA, SVC_CODIGO, SVC_IDENTIFICADOR, CSA_CODIGO, CSA_IDENTIFICADOR, CSA_CNPJ, EST_CODIGO, EST_IDENTIFICADOR, EST_CNPJ, ORG_CODIGO, ORG_IDENTIFICADOR, ORG_CNPJ) ");
            sql.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
            h2PreStat = h2Conn.prepareStatement(sql.toString());

            ListarConveniosProcessamentoQuery queryConvenio = new ListarConveniosProcessamentoQuery();
            queryConvenio.tipoEntidade = tipoEntidade;
            queryConvenio.codigoEntidade = codigoEntidade;
            List<TransferObject> convenios = queryConvenio.executarDTO();
            if (convenios != null && !convenios.isEmpty()) {
                LOG.debug("Convenios encontrados: " + convenios.size());
                for (TransferObject convenio : convenios) {
                    h2PreStat.setString(1,  (String) convenio.getAttribute(Columns.CNV_CODIGO));
                    h2PreStat.setString(2,  (String) convenio.getAttribute(Columns.CNV_COD_VERBA));
                    h2PreStat.setString(3,  (String) convenio.getAttribute(Columns.SVC_CODIGO));
                    h2PreStat.setString(4,  (String) convenio.getAttribute(Columns.SVC_IDENTIFICADOR));
                    h2PreStat.setString(5,  (String) convenio.getAttribute(Columns.CSA_CODIGO));
                    h2PreStat.setString(6,  (String) convenio.getAttribute(Columns.CSA_IDENTIFICADOR));
                    h2PreStat.setString(7,  (String) convenio.getAttribute(Columns.CSA_CNPJ));
                    h2PreStat.setString(8,  (String) convenio.getAttribute(Columns.EST_CODIGO));
                    h2PreStat.setString(9,  (String) convenio.getAttribute(Columns.EST_IDENTIFICADOR));
                    h2PreStat.setString(10, (String) convenio.getAttribute(Columns.EST_CNPJ));
                    h2PreStat.setString(11, (String) convenio.getAttribute(Columns.ORG_CODIGO));
                    h2PreStat.setString(12, (String) convenio.getAttribute(Columns.ORG_IDENTIFICADOR));
                    h2PreStat.setString(13, (String) convenio.getAttribute(Columns.ORG_CNPJ));
                    h2PreStat.execute();
                }
            }
        } catch (SQLException ex) {
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {
            DBHelper.closeStatement(h2Stat);
            DBHelper.closeStatement(h2PreStat);
        }
    }

    private PreparedStatement criarStatementBuscaServidor(Connection h2Conn, AcessoSistema responsavel) throws ZetraException {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT RSE_CODIGO, RSE_MATRICULA, SER_CPF, EST_CODIGO, EST_IDENTIFICADOR, ORG_CODIGO, ORG_IDENTIFICADOR ");
            sql.append("FROM servidores ");
            sql.append("WHERE RSE_MATRICULA = ? ");
            sql.append("AND COALESCE(?, SER_CPF) = SER_CPF ");
            sql.append("AND COALESCE(?, EST_CODIGO) = EST_CODIGO ");
            sql.append("AND COALESCE(?, ORG_CODIGO) = ORG_CODIGO ");
            sql.append("AND COALESCE(?, EST_IDENTIFICADOR) = EST_IDENTIFICADOR ");
            sql.append("AND COALESCE(?, ORG_IDENTIFICADOR) = ORG_IDENTIFICADOR ");
            sql.append("AND COALESCE(COALESCE(?, EST_CNPJ), '') = COALESCE(EST_CNPJ, '') ");
            sql.append("AND COALESCE(COALESCE(?, ORG_CNPJ), '') = COALESCE(ORG_CNPJ, '') ");
            sql.append("ORDER BY SRS_CODIGO");
            return h2Conn.prepareStatement(sql.toString());
        } catch (SQLException ex) {
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private PreparedStatement criarStatementBuscaConvenioPorVerba(Connection h2Conn, AcessoSistema responsavel) throws ZetraException {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT CNV_CODIGO, CNV_COD_VERBA, SVC_IDENTIFICADOR, CSA_IDENTIFICADOR, EST_CODIGO, EST_IDENTIFICADOR, ORG_CODIGO, ORG_IDENTIFICADOR ");
            sql.append("FROM convenios ");
            sql.append("WHERE 1=1 ");
            sql.append("AND ? = CNV_COD_VERBA ");
            sql.append("AND COALESCE(?, EST_CODIGO) = EST_CODIGO ");
            sql.append("AND COALESCE(?, ORG_CODIGO) = ORG_CODIGO ");
            sql.append("AND COALESCE(?, SVC_IDENTIFICADOR) = SVC_IDENTIFICADOR ");
            sql.append("AND COALESCE(?, CSA_IDENTIFICADOR) = CSA_IDENTIFICADOR ");
            sql.append("AND COALESCE(?, EST_IDENTIFICADOR) = EST_IDENTIFICADOR ");
            sql.append("AND COALESCE(?, ORG_IDENTIFICADOR) = ORG_IDENTIFICADOR ");
            sql.append("AND COALESCE(COALESCE(?, CSA_CNPJ), '') = COALESCE(CSA_CNPJ, '') ");
            sql.append("AND COALESCE(COALESCE(?, EST_CNPJ), '') = COALESCE(EST_CNPJ, '') ");
            sql.append("AND COALESCE(COALESCE(?, ORG_CNPJ), '') = COALESCE(ORG_CNPJ, '') ");
            return h2Conn.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException ex) {
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private PreparedStatement criarStatementBuscaConvenioSemVerba(Connection h2Conn, AcessoSistema responsavel) throws ZetraException {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT CNV_CODIGO, CNV_COD_VERBA, SVC_IDENTIFICADOR, CSA_IDENTIFICADOR, EST_CODIGO, EST_IDENTIFICADOR, ORG_CODIGO, ORG_IDENTIFICADOR ");
            sql.append("FROM convenios ");
            sql.append("WHERE 1=1 ");
            sql.append("AND COALESCE(?, EST_CODIGO) = EST_CODIGO ");
            sql.append("AND COALESCE(?, ORG_CODIGO) = ORG_CODIGO ");
            sql.append("AND COALESCE(?, SVC_IDENTIFICADOR) = SVC_IDENTIFICADOR ");
            sql.append("AND COALESCE(?, CSA_IDENTIFICADOR) = CSA_IDENTIFICADOR ");
            sql.append("AND COALESCE(?, EST_IDENTIFICADOR) = EST_IDENTIFICADOR ");
            sql.append("AND COALESCE(?, ORG_IDENTIFICADOR) = ORG_IDENTIFICADOR ");
            sql.append("AND COALESCE(COALESCE(?, CSA_CNPJ), '') = COALESCE(CSA_CNPJ, '') ");
            sql.append("AND COALESCE(COALESCE(?, EST_CNPJ), '') = COALESCE(EST_CNPJ, '') ");
            sql.append("AND COALESCE(COALESCE(?, ORG_CNPJ), '') = COALESCE(ORG_CNPJ, '') ");
            return h2Conn.prepareStatement(sql.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException ex) {
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void carregarBlocosMargem(String arquivoMargem, String tipoEntidade, String codigoEntidade, Date periodo, PreparedStatement h2PreStatSer, HistoricoProcessamento historicoProcessamento, AcessoSistema responsavel) throws ZetraException {
        ResultSet h2Rset = null;

        try {
            // Recupera o codigo do orgao/estabelecimento
            String orgCodigo = (tipoEntidade != null && tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) ? codigoEntidade : null);
            String estCodigo = (tipoEntidade != null && tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST) ? codigoEntidade : null);

            // Se o arquivo não existe, verifica se foi criptogradado
            if (!Files.exists(Paths.get(arquivoMargem)) && Files.exists(Paths.get(arquivoMargem + ".crypt"))) {
                arquivoMargem += ".crypt";
            }

            // Verifica se os arquivos estão criptografados, e caso estejam, faz a descriptografia
            if (arquivoMargem.endsWith(".crypt")) {
                File arquivoPlano = CriptografiaArquivos.descriptografarArquivo(arquivoMargem, true, responsavel);
                if (arquivoPlano != null) {
                    arquivoMargem = arquivoPlano.getAbsolutePath();
                }
            }

            // Determina os nomes dos arquivos de configuração da rotina de carga de margem
            String[] nomesArquivos = impCadMargemController.obtemArquivosConfiguracao(arquivoMargem, estCodigo, orgCodigo, responsavel);
            String nomeArqConfEntrada = nomesArquivos[0];
            String nomeArqConfTradutor = nomesArquivos[1];
            String nomeArqConfUnico = nomesArquivos[4];

            // Salva os dados do arquivo de margem no histórico de processamento
            historicoProcessamento.setHprArquivoMargem(arquivoMargem);
            historicoProcessamento.setHprLinhasArquivoMargem(FileHelper.getNumberOfLines(arquivoMargem));

            // Mover os arquivos para extensão .prc para indicar que estão sendo processados
            try {
                Files.move(Paths.get(arquivoMargem), Paths.get(arquivoMargem + ".prc"), StandardCopyOption.REPLACE_EXISTING);
                arquivoMargem += ".prc";
            } catch (IOException ex) {
                throw new ZetraException("mensagem.erro.renomear.arquivo.processamento", responsavel, ex);
            }

            // Lê o arquivo de margem e para cada linha:
            HashMap<String, Object> entrada = new HashMap<>();
            EscritorMemoria escritor = new EscritorMemoria(entrada);
            LeitorArquivoTexto leitor = null;
            ITradutor tradutor = null;

            if (!TextHelper.isNull(nomeArqConfUnico)) {
                leitor = new LeitorArquivoTextoSimpletl(nomeArqConfUnico, arquivoMargem);
                tradutor = new TradutorSimpletl(nomeArqConfUnico, (LeitorArquivoTextoSimpletl)leitor, escritor);

                // Salva no histórico de processamento o nome dos arquivos de configuração utilizados
                historicoProcessamento.setHprConfEntradaMargem(nomeArqConfUnico);
                historicoProcessamento.setHprConfTradutorMargem(nomeArqConfUnico);
            } else {
                // Faz a importação do arquivo da folha
                if (arquivoMargem.toLowerCase().endsWith(".zip.prc")) {
                    leitor = new LeitorArquivoTextoZip(nomeArqConfEntrada, arquivoMargem);
                } else {
                    leitor = new LeitorArquivoTexto(nomeArqConfEntrada, arquivoMargem);
                }
                tradutor = new Tradutor(nomeArqConfTradutor, leitor, escritor);

                // Salva no histórico de processamento o nome dos arquivos de configuração utilizados
                historicoProcessamento.setHprConfEntradaMargem(nomeArqConfEntrada);
                historicoProcessamento.setHprConfTradutorMargem(nomeArqConfTradutor);
            }

            // Objeto usado para converter o Map com campos de entrada em JSON
            Gson gson = new Gson();

            // Contador de linhas
            int contador = 0;

            long tempoGastoH2 = 0;
            long tempoGastoMySQL = 0;

            // Gerenciador de sessão do Hibernate para processos em batch
            Session session = SessionUtil.getSession();
            BatchManager batman = new BatchManager(session);

            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.traducao.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            tradutor.iniciaTraducao();
            while (tradutor.traduzProximo()) {
                contador++;
                if (contador % 1000 == 0) {
                    LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.linhas.lidas.arg0", responsavel, String.valueOf(contador)));
                }

                // Coloca colchetes ao redor para evitar que espaços ao final sejam perdidos
                String linhaEntrada = "[" + leitor.getLinha() + "]";

                // Recupera as informações de Estabelecimento, órgão, Matrícula e CPF para gravação de identificação do bloco de processamento.
                String rseCodigo = null;
                String estIdentificador = (String) entrada.get("EST_IDENTIFICADOR");
                String orgIdentificador = (String) entrada.get("ORG_IDENTIFICADOR");
                String rseMatricula = (String) entrada.get("RSE_MATRICULA");
                String serCpf = (String) entrada.get("SER_CPF");
                String estCodigoRegistro = (!TextHelper.isNull(estCodigo) ? estCodigo : (String) entrada.get("EST_CODIGO"));
                String orgCodigoRegistro = (!TextHelper.isNull(orgCodigo) ? orgCodigo : (String) entrada.get("ORG_CODIGO"));

                long ini = Calendar.getInstance().getTimeInMillis();

                h2PreStatSer.setString(1, rseMatricula);
                h2PreStatSer.setString(2, serCpf);
                h2PreStatSer.setString(3, estCodigoRegistro);
                h2PreStatSer.setString(4, orgCodigoRegistro);
                h2PreStatSer.setString(5, estIdentificador);
                h2PreStatSer.setString(6, orgIdentificador);
                h2PreStatSer.setString(7, (String) entrada.get("EST_CNPJ"));
                h2PreStatSer.setString(8, (String) entrada.get("ORG_CNPJ"));
                h2Rset = h2PreStatSer.executeQuery();
                if (h2Rset.next()) {
                    rseCodigo = h2Rset.getString("RSE_CODIGO");
                    orgCodigoRegistro = h2Rset.getString("ORG_CODIGO");
                    estCodigoRegistro = h2Rset.getString("EST_CODIGO");
                    estIdentificador = h2Rset.getString("EST_IDENTIFICADOR");
                    orgIdentificador = h2Rset.getString("ORG_IDENTIFICADOR");
                    rseMatricula = h2Rset.getString("RSE_MATRICULA");
                    serCpf = h2Rset.getString("SER_CPF");
                }
                h2Rset.close();

                long fim = Calendar.getInstance().getTimeInMillis();
                tempoGastoH2 += (fim - ini);

                ini = Calendar.getInstance().getTimeInMillis();

                // Cria um novo bloco com esta identificação do servidor, e outra coluna com o JSON (*2) das demais informações traduzidas.
                BlocoProcessamentoHome.create(StatusBlocoProcessamentoEnum.PREPARANDO, TipoBlocoProcessamentoEnum.MARGEM, periodo, contador, linhaEntrada, leitor.getNumeroLinha(), gson.toJson(entrada),
                        null, null, null, estIdentificador, orgIdentificador, rseMatricula, serCpf, null, null, rseCodigo, null, estCodigoRegistro, orgCodigoRegistro, session);

                fim = Calendar.getInstance().getTimeInMillis();
                tempoGastoMySQL += (fim - ini);

                batman.iterate();
            }
            batman.finish();
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.total.linhas.lidas.arg0", responsavel, String.valueOf(contador)));

            tradutor.encerraTraducao();
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.traducao.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

            // Mover os arquivos para extensão .prc.ok para indicar que não serão mais utilizados
            try {
                Files.move(Paths.get(arquivoMargem), Paths.get(arquivoMargem + ".ok"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                throw new ZetraException("mensagem.erro.renomear.arquivo.processamento", responsavel, ex);
            }

            LOG.info("Tempo H2 (ms)...: " + tempoGastoH2);
            LOG.info("Tempo MySQL (ms): " + tempoGastoMySQL);

        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            DBHelper.closeResultSet(h2Rset);
        }
    }

    private void carregarBlocosRetorno(String arquivoRetorno, String tipoEntidade, String codigoEntidade, Date periodo, PreparedStatement h2PreStatSer, PreparedStatement h2PreStatCnvComVerba, PreparedStatement h2PreStatCnvSemVerba, HistoricoProcessamento historicoProcessamento, AcessoSistema responsavel) throws ZetraException {
        ResultSet h2Rset = null;

        try {
            // Recupera o codigo do orgao/estabelecimento
            String orgCodigo = (tipoEntidade != null && tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) ? codigoEntidade : null);
            String estCodigo = (tipoEntidade != null && tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST) ? codigoEntidade : null);

            // Se o arquivo não existe, verifica se foi criptogradado
            if (!Files.exists(Paths.get(arquivoRetorno)) && Files.exists(Paths.get(arquivoRetorno + ".crypt"))) {
                arquivoRetorno += ".crypt";
            }

            // Se o arquivo estiver criptografado, então realiza a descriptografia
            if (arquivoRetorno.endsWith(".crypt")) {
                File arquivoPlano = CriptografiaArquivos.descriptografarArquivo(arquivoRetorno, true, responsavel);
                if (arquivoPlano != null) {
                    arquivoRetorno = arquivoPlano.getAbsolutePath();
                }
            }

            // Recupera as configurações de entrada e tradutor
            Map<String, String> nomeArquivosConfiguracao = impRetornoController.buscaArquivosConfiguracao(arquivoRetorno, ProcessaRetorno.RETORNO, estCodigo, orgCodigo, responsavel);
            String fileName = nomeArquivosConfiguracao.get(NOME_ARQUIVO_ENTRADA);
            String entradaImpRetorno = nomeArquivosConfiguracao.get(ENTRADA_IMP_RETORNO);
            String tradutorImpRetorno = nomeArquivosConfiguracao.get(TRADUTOR_IMP_RETORNO);

            // Move o arquivo para extensão .prc para indicar que estão sendo processado
            try {
                Files.move(Paths.get(fileName), Paths.get(fileName + ".prc"), StandardCopyOption.REPLACE_EXISTING);
                fileName += ".prc";
            } catch (IOException ex) {
                throw new ZetraException("mensagem.erro.renomear.arquivo.processamento", responsavel, ex);
            }

            // Hash que recebe os dados do que serão lidos do arquivo de entrada
            Map<String, Object> entrada = new HashMap<>();
            // Configura o leitor de acordo com o arquivo de entrada
            LeitorArquivoTexto leitor = null;
            if (fileName.toLowerCase().endsWith(".zip.prc")) {
                leitor = new LeitorArquivoTextoZip(entradaImpRetorno, fileName);
            } else {
                leitor = new LeitorArquivoTexto(entradaImpRetorno, fileName);
            }
            // Prepara tradução do arquivo de retorno.
            Escritor escritor = new EscritorMemoria(entrada);
            Tradutor tradutor = new Tradutor(tradutorImpRetorno, leitor, escritor);

            // Busca as configurações XML que informam quais são os campos chave e em que
            // ordem eles devem ser excluídos na busca por parcelas.
            String[] ordemExcCamposChave = {};
            String[] camposChave = {};

            for (ParametroTipo param : leitor.getConfig().getParametro()) {
                if (param.getNome().equalsIgnoreCase("chave_identificacao")) {
                    camposChave = TextHelper.split(param.getValor(), ";");
                } else if (param.getNome().equalsIgnoreCase("ordem_exc_campos_chave")) {
                    ordemExcCamposChave = TextHelper.split(param.getValor(), ";");
                    if (ordemExcCamposChave == null) {
                        ordemExcCamposChave = new String[]{};
                    }
                }
            }

            // Campos que, se estiverem no arquivo, devem ser usados obrigatoriamente
            // como chaves de identificação.
            List<String> camposChaveIdent = new ArrayList<>();
            camposChaveIdent.add("EST_IDENTIFICADOR");
            camposChaveIdent.add("ORG_IDENTIFICADOR");
            camposChaveIdent.add("CSA_IDENTIFICADOR");
            camposChaveIdent.add("SVC_IDENTIFICADOR");
            camposChaveIdent.add("CNV_COD_VERBA");
            camposChaveIdent.add("RSE_MATRICULA");
            camposChaveIdent.add("PRD_VLR_PREVISTO");
            camposChaveIdent.add("SPD_CODIGOS");
            // Adiciona os campos chave específicos do arquivo de configuração à lista.
            if (camposChave != null) {
                for (String element : camposChave) {
                    if (!camposChaveIdent.contains(element)) {
                        camposChaveIdent.add(element);
                    }
                }
            }

            // Salva no histórico de processamento o nome dos arquivos de configuração utilizados
            historicoProcessamento.setHprArquivoRetorno(arquivoRetorno);
            historicoProcessamento.setHprLinhasArquivoRetorno(FileHelper.getNumberOfLines(fileName));
            historicoProcessamento.setHprConfEntradaRetorno(entradaImpRetorno);
            historicoProcessamento.setHprConfTradutorRetorno(tradutorImpRetorno);
            historicoProcessamento.setHprChaveIdentificacao(TextHelper.join(camposChaveIdent, ","));
            historicoProcessamento.setHprOrdemExcCamposChave(TextHelper.join(ordemExcCamposChave, ","));

            // Objeto usado para converter o Map com campos de entrada em JSON
            Gson gson = new Gson();

            // Contador de linhas
            int contador = 0;

            long tempoGastoH2 = 0;
            long tempoGastoMySQL = 0;

            // Gerenciador de sessão do Hibernate para processos em batch
            Session session = SessionUtil.getSession();
            BatchManager batman = new BatchManager(session);

            // Query para tentar identificar o convênio caso os dados de verba, serviço e consignatária não sejam suficientes
            ObtemConvenioProcessamentoQuery cnvQuery = new ObtemConvenioProcessamentoQuery();
            cnvQuery.tipoEntidade = tipoEntidade;
            cnvQuery.codigoEntidade = codigoEntidade;

            // Lê o arquivo de retorno e para cada linha:
            // Recupera as informações de Estabelecimento, órgão, Matrícula e CPF para gravação de identificação do bloco de processamento.
            // Recupera as informações de Código de Verba, Estabelecimento, órgão, Serviço, Consignatária, ADE Número e ADE Índice para gravação de identificação do bloco de processamento.
            // Cria um novo bloco com esta identificação do servidor e convênio, e outra coluna com o JSON (*2) das demais informações traduzidas.
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.traducao.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            tradutor.iniciaTraducao();
            while (tradutor.traduzProximo()) {
                contador++;
                if (contador % 1000 == 0) {
                    LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.linhas.lidas.arg0", responsavel, String.valueOf(contador)));
                }

                // Coloca colchetes ao redor para evitar que espaços ao final sejam perdidos
                String linhaEntrada = "[" + leitor.getLinha() + "]";

                String rseCodigo = null;
                String rseMatricula = (String) entrada.get("RSE_MATRICULA");
                String serCpf = (String) entrada.get("SER_CPF");
                String estIdentificador = (String) entrada.get("EST_IDENTIFICADOR");
                String orgIdentificador = (String) entrada.get("ORG_IDENTIFICADOR");

                String estCodigoRegistro = (!TextHelper.isNull(estCodigo) ? estCodigo : (String) entrada.get("EST_CODIGO"));
                String orgCodigoRegistro = (!TextHelper.isNull(orgCodigo) ? orgCodigo : (String) entrada.get("ORG_CODIGO"));

                String cnvCodigo = null;
                String cnvCodVerba = (String) entrada.get("CNV_COD_VERBA");
                String svcIdentificador = (String) entrada.get("SVC_IDENTIFICADOR");
                String csaIdentificador = (String) entrada.get("CSA_IDENTIFICADOR");

                Long adeNumero = (TextHelper.isNum(entrada.get("ADE_NUMERO")) ? Long.valueOf(entrada.get("ADE_NUMERO").toString()) : null);
                String adeIndice = (!TextHelper.isNull(entrada.get("ADE_INDICE")) ? entrada.get("ADE_INDICE").toString() : null);

                String prdSituacao = (String) entrada.get("SITUACAO"); // Resultado da operação I (Indeferida), D (Deferida), Q (Quitação)
                String tipoEnvio = (String) entrada.get("TIPO_ENVIO"); // Operação enviada: I,A,E
                String artFerias = (String) entrada.get("ART_FERIAS");
                String ocpObs = (String) entrada.get("OCP_OBS");

                if (!TextHelper.isNull(ocpObs)) {
                    ocpObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.retorno.obs.parcela", responsavel) + ": " + ocpObs;
                } else if ("1".equals(artFerias)) {
                    ocpObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.com.retorno.ferias", responsavel);
                } else {
                    ocpObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.com.retorno", responsavel);
                }
                if (TextHelper.isNull(prdSituacao)) {
                    prdSituacao = "D";
                }
                if (TextHelper.isNull(tipoEnvio)) {
                    tipoEnvio = "I";
                }

                // Lista dos status de parcela que devem ser capturados
                List<String> spdCodigos = new ArrayList<>();
                spdCodigos.add(CodedValues.SPD_EMPROCESSAMENTO);
                spdCodigos.add(CodedValues.SPD_SEM_RETORNO);
                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_MULTIPLAS_LINHAS_RETORNO_MESMA_PRD, CodedValues.TPC_SIM, responsavel)) {
                    // Se permite que múltiplas linhas paguem a mesma parcela no retorno, então ao buscar as parcelas,
                    // o sistema deve busar inclusive aquelas que não estão com informação de pagamento, de modo que possa
                    // incrementar o valor pago
                    spdCodigos.add(CodedValues.SPD_LIQUIDADAFOLHA);
                    spdCodigos.add(CodedValues.SPD_REJEITADAFOLHA);
                }

                // Subistitui o valor dos campo com o resultado do tratamento
                entrada.put("OCP_OBS", ocpObs);
                entrada.put("SITUACAO", prdSituacao);
                entrada.put("TIPO_ENVIO", tipoEnvio);
                entrada.put("SPD_CODIGOS", spdCodigos);

                // Salva o valor previsto como sendo o valor realizado, caso o campo valor previsto não exista.
                // é necessário para que o pagamento dê preferência para as parcelas com o valor igual ao pago.
                if (TextHelper.isNull(entrada.get("PRD_VLR_PREVISTO")) && !TextHelper.isNull(entrada.get("PRD_VLR_REALIZADO"))) {
                    entrada.put("PRD_VLR_PREVISTO", entrada.get("PRD_VLR_REALIZADO"));
                }

                long ini = Calendar.getInstance().getTimeInMillis();

                h2PreStatSer.setString(1, rseMatricula);
                h2PreStatSer.setString(2, serCpf);
                h2PreStatSer.setString(3, estCodigoRegistro);
                h2PreStatSer.setString(4, orgCodigoRegistro);
                h2PreStatSer.setString(5, estIdentificador);
                h2PreStatSer.setString(6, orgIdentificador);
                h2PreStatSer.setString(7, (String) entrada.get("EST_CNPJ"));
                h2PreStatSer.setString(8, (String) entrada.get("ORG_CNPJ"));
                h2Rset = h2PreStatSer.executeQuery();
                if (h2Rset.next()) {
                    rseCodigo = h2Rset.getString("RSE_CODIGO");
                    orgCodigoRegistro = h2Rset.getString("ORG_CODIGO");
                    estCodigoRegistro = h2Rset.getString("EST_CODIGO");
                    estIdentificador = h2Rset.getString("EST_IDENTIFICADOR");
                    orgIdentificador = h2Rset.getString("ORG_IDENTIFICADOR");
                    rseMatricula = h2Rset.getString("RSE_MATRICULA");
                    serCpf = h2Rset.getString("SER_CPF");
                }
                h2Rset.close();

                if (!TextHelper.isNull(cnvCodVerba)) {
                    h2PreStatCnvComVerba.setString(1,  cnvCodVerba);
                    h2PreStatCnvComVerba.setString(2,  estCodigoRegistro);
                    h2PreStatCnvComVerba.setString(3,  orgCodigoRegistro);
                    h2PreStatCnvComVerba.setString(4,  svcIdentificador);
                    h2PreStatCnvComVerba.setString(5,  csaIdentificador);
                    h2PreStatCnvComVerba.setString(6,  estIdentificador);
                    h2PreStatCnvComVerba.setString(7,  orgIdentificador);
                    h2PreStatCnvComVerba.setString(8,  (String) entrada.get("CSA_CNPJ"));
                    h2PreStatCnvComVerba.setString(9,  (String) entrada.get("EST_CNPJ"));
                    h2PreStatCnvComVerba.setString(10, (String) entrada.get("ORG_CNPJ"));
                    h2Rset = h2PreStatCnvComVerba.executeQuery();
                } else {
                    h2PreStatCnvSemVerba.setString(1,  estCodigoRegistro);
                    h2PreStatCnvSemVerba.setString(2,  orgCodigoRegistro);
                    h2PreStatCnvSemVerba.setString(3,  svcIdentificador);
                    h2PreStatCnvSemVerba.setString(4,  csaIdentificador);
                    h2PreStatCnvSemVerba.setString(5,  estIdentificador);
                    h2PreStatCnvSemVerba.setString(6,  orgIdentificador);
                    h2PreStatCnvSemVerba.setString(7,  (String) entrada.get("CSA_CNPJ"));
                    h2PreStatCnvSemVerba.setString(8,  (String) entrada.get("EST_CNPJ"));
                    h2PreStatCnvSemVerba.setString(9,  (String) entrada.get("ORG_CNPJ"));
                    h2Rset = h2PreStatCnvSemVerba.executeQuery();
                }

                long fim = Calendar.getInstance().getTimeInMillis();
                tempoGastoH2 += (fim - ini);

                ini = Calendar.getInstance().getTimeInMillis();

                if (h2Rset.next()) {
                    if (h2Rset.last() && h2Rset.getRow() == 1) {
                        // Só obtém os dados, caso tenha retornado apenas uma linha
                        cnvCodigo = h2Rset.getString("CNV_CODIGO");
                        orgCodigoRegistro = h2Rset.getString("ORG_CODIGO");
                        estCodigoRegistro = h2Rset.getString("EST_CODIGO");
                        svcIdentificador = h2Rset.getString("SVC_IDENTIFICADOR");
                        csaIdentificador = h2Rset.getString("CSA_IDENTIFICADOR");
                        estIdentificador = h2Rset.getString("EST_IDENTIFICADOR");
                        orgIdentificador = h2Rset.getString("ORG_IDENTIFICADOR");
                    } else {
                        // Recupera os CNV_CODIGOs retornados, e faz pesquisa pelas consignações para tentar identificar a qual convênio a linha se refere
                        List<String> cnvCodigos = new ArrayList<>();
                        if (h2Rset.first()) {
                            do {
                                cnvCodigos.add(h2Rset.getString("CNV_CODIGO"));
                            } while (h2Rset.next());
                        }

                        if (!cnvCodigos.isEmpty()) {
                            cnvQuery.cnvCodigos = cnvCodigos;
                            cnvQuery.rseCodigo = rseCodigo;
                            cnvQuery.rseMatricula = rseMatricula;
                            cnvQuery.serCpf = serCpf;
                            cnvQuery.adeNumero = adeNumero;
                            cnvQuery.adeIndice = adeIndice;
                            cnvCodigos = cnvQuery.executarLista();
                            if (cnvCodigos != null && cnvCodigos.size() == 1) {
                                cnvCodigo = cnvCodigos.get(0);

                                // Percorre o resultSet novamente para obter os demais dados
                                h2Rset.first();
                                do {
                                    if (cnvCodigo.equals(h2Rset.getString("CNV_CODIGO"))) {
                                        orgCodigoRegistro = h2Rset.getString("ORG_CODIGO");
                                        estCodigoRegistro = h2Rset.getString("EST_CODIGO");
                                        svcIdentificador = h2Rset.getString("SVC_IDENTIFICADOR");
                                        csaIdentificador = h2Rset.getString("CSA_IDENTIFICADOR");
                                        estIdentificador = h2Rset.getString("EST_IDENTIFICADOR");
                                        orgIdentificador = h2Rset.getString("ORG_IDENTIFICADOR");
                                        break;
                                    }
                                } while (h2Rset.next());
                            }
                        }
                    }
                }
                h2Rset.close();

                // Cria um novo bloco com esta identificação do servidor, e outra coluna com o JSON (*2) das demais informações traduzidas.
                BlocoProcessamentoHome.create(StatusBlocoProcessamentoEnum.PREPARANDO, TipoBlocoProcessamentoEnum.RETORNO, periodo, contador, linhaEntrada, leitor.getNumeroLinha(), gson.toJson(entrada),
                        cnvCodVerba, svcIdentificador, csaIdentificador, estIdentificador, orgIdentificador, rseMatricula, serCpf, adeNumero, adeIndice, rseCodigo, cnvCodigo, estCodigoRegistro, orgCodigoRegistro, session);

                fim = Calendar.getInstance().getTimeInMillis();
                tempoGastoMySQL += (fim - ini);

                batman.iterate();
            }
            batman.finish();
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.total.linhas.lidas.arg0", responsavel, String.valueOf(contador)));

            tradutor.encerraTraducao();
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.traducao.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

            LOG.info("Tempo H2 (ms)...: " + tempoGastoH2);
            LOG.info("Tempo MySQL (ms): " + tempoGastoMySQL);

            // Mover os arquivos para extensão .prc.ok para indicar que não serão mais utilizados
            try {
                Files.move(Paths.get(fileName), Paths.get(fileName + ".ok"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
                throw new ZetraException("mensagem.erro.renomear.arquivo.processamento", responsavel, ex);
            }
        } catch (SQLException ex) {
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {
            DBHelper.closeResultSet(h2Rset);
        }
    }

    private void validarBlocosMargem(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ZetraException {
        // Realiza exclusão dos registros servidores que não foram identificados em nenhum dos blocos:
        ListarRegistroServidorSemBlocoProcessamentoQuery rseExclusaoQuery = new ListarRegistroServidorSemBlocoProcessamentoQuery();
        // Caso o processamento seja a nível de órgão ou estabelecimento, somente servidores destas entidades que não foram carregados em nenhum bloco deve ser excluído.
        rseExclusaoQuery.tipoEntidade = tipoEntidade;
        rseExclusaoQuery.codigoEntidade = codigoEntidade;
        List<String> rseCodigoExclusao = rseExclusaoQuery.executarLista();

        // Validar o percentual de variação de quantidade de servidores (Parâmetro de sistema 241), considerando a quantidade dos que serão excluídos com a quantidade dos blocos enviados.
        Object tpcPercMaxSerAtivo = ParamSist.getInstance().getParam(CodedValues.TPC_PERC_MAX_VAR_SER_ATIVO_CAD_MARGENS, responsavel);
        if (!TextHelper.isNull(tpcPercMaxSerAtivo)) {
            float percMaxVarSerAtivo = TextHelper.isNotNumeric((String)tpcPercMaxSerAtivo) ? 0 : Float.parseFloat(tpcPercMaxSerAtivo.toString());

            // Faz COUNT da quantidade de blocos de margem
            ObtemTotalBlocosProcessamentoQuery query = new ObtemTotalBlocosProcessamentoQuery();
            query.tipoEntidade = tipoEntidade;
            query.codigoEntidade = codigoEntidade;
            query.tbpCodigos = Arrays.asList(TipoBlocoProcessamentoEnum.MARGEM.getCodigo());
            float qtdBlocosMargem = query.executarContador();

            // Calcula o percentual de variação
            if (rseCodigoExclusao.size() / qtdBlocosMargem > percMaxVarSerAtivo) {
                throw new ServidorControllerException("mensagem.erro.percentual.maximo.variacao.servidores.ativos.atingido.arg0.arg1.arg2", responsavel, String.valueOf(percMaxVarSerAtivo), String.valueOf(rseCodigoExclusao.size()), String.valueOf(qtdBlocosMargem));
            }

            String orsObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.exclusao.carga.margem", responsavel);
            String tocCodigo = CodedValues.TOC_RSE_EXCLUSAO_POR_CARGA_MARGEM;

            // Realiza a exclussão de cada servidor:
            for (String rseCodigo : rseCodigoExclusao) {
                // Altera o status para 3 (SRS_CODIGO = 3) e Zera as margens (RSE_MARGEM_X = 0, MRS_MARGEM = 0).
                RegistroServidorHome.excluirRegistroServidor(rseCodigo);
                MargemRegistroServidorHome.zerarMargemRegistroServidor(rseCodigo);
                // Cria ocorrência de exclusão pela carga de margem (TOC_CODIGO = 67).
                OcorrenciaRegistroServidorHome.create(rseCodigo, tocCodigo, responsavel.getUsuCodigo(), orsObs, responsavel.getIpUsuario(), null);
            }
        }
    }

    private void validarBlocosRetorno(String tipoEntidade, String codigoEntidade, Date periodo, AcessoSistema responsavel) throws ZetraException {
        // Faz COUNT de parcelas aguardando retorno do período para validações
        ObtemTotalParcelasPeriodoProcessamentoQuery totalParcelasPer = new ObtemTotalParcelasPeriodoProcessamentoQuery();
        totalParcelasPer.periodo = periodo;
        totalParcelasPer.tipoEntidade = tipoEntidade;
        totalParcelasPer.codigoEntidade = codigoEntidade;
        int qtdParcelasPeriodo = totalParcelasPer.executarContador();

        // Criar um parâmetro de sistema para validar o percentual de linhas sem mapeamento aceitas pelo sistema em relação à quantidade de parcelas aguardando retorno, default Zero, não aceitando nada. Se o parâmetro for nulo, não faz a valicação.
        Object tpcPercMaxBlocosSemMapeamento = ParamSist.getInstance().getParam(CodedValues.TPC_PERC_MAX_BLOCOS_SEM_MAPEAMENTO_CONVENIO, responsavel);
        if (!TextHelper.isNull(tpcPercMaxBlocosSemMapeamento)) {
            float percMaxBlocosSemMapeamento = TextHelper.isNotNumeric(tpcPercMaxBlocosSemMapeamento.toString()) ? 0 : Float.parseFloat(tpcPercMaxBlocosSemMapeamento.toString());

            // Faz COUNT da quantidade de blocos de retorno sem mapeamento
            ObtemTotalBlocosProcessamentoQuery query = new ObtemTotalBlocosProcessamentoQuery();
            query.tipoEntidade = tipoEntidade;
            query.codigoEntidade = codigoEntidade;
            query.tbpCodigos = Arrays.asList(TipoBlocoProcessamentoEnum.RETORNO.getCodigo());
            query.convenioMapeado = false;
            float qtdBlocosSemMapeamento = query.executarContador();
            if (qtdBlocosSemMapeamento / qtdParcelasPeriodo > percMaxBlocosSemMapeamento) {
                throw new ZetraException("mensagem.erro.percentual.maximo.blocos.sem.mapeamento.atingido.arg0.arg1.arg2", responsavel, String.valueOf(percMaxBlocosSemMapeamento), String.valueOf(qtdBlocosSemMapeamento), String.valueOf(qtdParcelasPeriodo));
            }
        }

        // Criar um parâmetro de sistema para validar o percentual de rejeito aceito pelo sistema, calculado pela quantidade de blocos com convênio mapeado e a quantidade de parcelas aguardando retorno, default 5%. Se o parâmetro for nulo, não faz a valicação.
        // A valicação do item anterior é aproximada, visto que não dá para saber se as parcelas serão ou não pagas antes do processo execução da rotina, mas evita por exemplo que seja enviado uma quantidade muito desproporcional de linhas com relação às parcelas aguardando retorno.
        Object tpcPercMaxVarParcelasBlocos = ParamSist.getInstance().getParam(CodedValues.TPC_PERC_MAX_VARIACAO_PARCELAS_POR_BLOCOS, responsavel);
        if (!TextHelper.isNull(tpcPercMaxVarParcelasBlocos)) {
            float percMaxVarParcelasBlocos = TextHelper.isNotNumeric(tpcPercMaxVarParcelasBlocos.toString()) ? 0 : Float.parseFloat(tpcPercMaxVarParcelasBlocos.toString());

            // Faz COUNT da quantidade de blocos de retorno com mapeamento
            ObtemTotalBlocosProcessamentoQuery query = new ObtemTotalBlocosProcessamentoQuery();
            query.tipoEntidade = tipoEntidade;
            query.codigoEntidade = codigoEntidade;
            query.tbpCodigos = Arrays.asList(TipoBlocoProcessamentoEnum.RETORNO.getCodigo());
            query.convenioMapeado = true;
            float qtdBlocosComMapeamento = query.executarContador();
            if ((qtdBlocosComMapeamento - qtdParcelasPeriodo) / qtdParcelasPeriodo > percMaxVarParcelasBlocos) {
                throw new ZetraException("mensagem.erro.percentual.maximo.variacao.parcelas.por.blocos.atingido.arg0.arg1.arg2", responsavel, String.valueOf(percMaxVarParcelasBlocos), String.valueOf(qtdBlocosComMapeamento), String.valueOf(qtdParcelasPeriodo));
            }
        }
    }

    private void gerarRelatorioIntegracao(Date periodo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ZetraException {
        // Determina sufixo para nome dos arquivos de integração de CSA caso o processamento seja feito por ORG/EST
        String sufixoNomeRel = "";
        if (AcessoSistema.ENTIDADE_ORG.equalsIgnoreCase(tipoEntidade)) {
            Orgao org = OrgaoHome.findByPrimaryKey(codigoEntidade);
            Estabelecimento est = EstabelecimentoHome.findByPrimaryKey(org.getEstabelecimento().getEstCodigo());
            sufixoNomeRel = est.getEstIdentificador() + "_" + org.getOrgIdentificador() + "_";
        } else if (AcessoSistema.ENTIDADE_EST.equalsIgnoreCase(tipoEntidade)) {
            Estabelecimento est = EstabelecimentoHome.findByPrimaryKey(codigoEntidade);
            sufixoNomeRel = est.getEstIdentificador() + "_";
        }

        // Diretório Raiz eConsig e do diretório de saida
        String raizArquivos = ParamSist.getDiretorioRaizArquivos();
        String diretorioSaida = raizArquivos + File.separatorChar + "relatorio" + File.separatorChar + "csa" + File.separatorChar + "integracao" + File.separatorChar;

        // Define o nome do arquivo do relatório de integração
        String prefixo = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.retorno.prefixo", responsavel);
        String hoje = DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss");
        String periodoRelatorio = DateHelper.toPeriodString(periodo).replace("/", "");
        String nomeArquivo = prefixo + sufixoNomeRel + hoje + "_" + periodoRelatorio;

        ListarLinhasBlocosProcessamentoQuery linhasCsaQuery = new ListarLinhasBlocosProcessamentoQuery();
        linhasCsaQuery.tipoEntidade = tipoEntidade;
        linhasCsaQuery.codigoEntidade = codigoEntidade;
        linhasCsaQuery.tbpCodigos = Arrays.asList(TipoBlocoProcessamentoEnum.RETORNO.getCodigo());

        ListarCodigosCsaComConvenioAtivoQuery csaQuery = new ListarCodigosCsaComConvenioAtivoQuery();
        csaQuery.tipoEntidade = tipoEntidade;
        csaQuery.codigoEntidade = codigoEntidade;
        List<String> csaCodigos = csaQuery.executarLista();
        for (String csaCodigo : csaCodigos) {
            String nomeArqSaida = diretorioSaida + csaCodigo + File.separatorChar + nomeArquivo + ".txt";

            try {
                FileUtils.forceMkdir(new File(diretorioSaida + csaCodigo));
            } catch (IOException ex) {
                throw new ZetraException("mensagem.erro.diretorio.inexistente", responsavel, ex);
            }

            linhasCsaQuery.csaCodigo = csaCodigo;
            List<String> linhasMapeadas = linhasCsaQuery.executarLista();
            if (linhasMapeadas != null && !linhasMapeadas.isEmpty()) {
                try (PrintWriter arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaida)))) {
                    for (String linha : linhasMapeadas) {
                        if (!TextHelper.isNull(linha)) {
                            // Remove os colchetes de início e fim
                            arqSaida.println(linha.substring(1, linha.length() - 1));
                        }
                    }
                } catch (IOException ex) {
                    throw new ZetraException("mensagem.erro.relatorio.integracao.gravar.arquivo", responsavel, ex);
                }

                if (ParamSist.paramEquals(CodedValues.TPC_GERAR_RELATORIOS_INTEGRACAO_COMPACTADOS, CodedValues.TPC_SIM, responsavel)) {
                    try {
                        // Compacta o relatório TXT e remove, deixando apenas o ZIP
                        FileHelper.zipAndRemove(nomeArqSaida);
                    } catch (IOException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        }
    }

    private void gerarRelatorioIntegracaoCse(String periodo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ZetraException {
        LOG.debug("ARQUIVOS CSE: " + DateHelper.getSystemDatetime());

        // Diretório Raiz eConsig e do diretório de saida
        String raizArquivos = ParamSist.getDiretorioRaizArquivos();

        // Diretório onde serão gravados os relatórios de integração
        String diretorioSaida = raizArquivos + File.separatorChar + "relatorio" + File.separatorChar + "cse" + File.separatorChar + "integracao" + File.separatorChar;
        if (AcessoSistema.ENTIDADE_ORG.equalsIgnoreCase(tipoEntidade)) {
            diretorioSaida += codigoEntidade + File.separatorChar;
        }

        try {
            FileUtils.forceMkdir(new File(diretorioSaida));
        } catch (IOException ex) {
            throw new ZetraException("mensagem.erro.diretorio.inexistente", responsavel, ex);
        }

        // Recupera o codigo do orgao/estabelecimento
        String orgCodigo = (tipoEntidade != null && tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) ? codigoEntidade : null);
        String estCodigo = (tipoEntidade != null && tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST) ? codigoEntidade : null);

        // Recupera as configurações de entrada e tradutor
        Map<String, String> nomeArquivosConfiguracao = impRetornoController.buscaArquivosConfiguracao(ProcessaRetorno.RETORNO, estCodigo, orgCodigo, responsavel);
        String entradaImpRetorno = nomeArquivosConfiguracao.get(ENTRADA_IMP_RETORNO);
        String tradutorImpRetorno = nomeArquivosConfiguracao.get(TRADUTOR_IMP_RETORNO);

        // Define o nome do arquivo do relatório de integração
        String prefixoSemProcessamento = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.integracao.sem.processamento", responsavel);
        String prefixoSemMapeamento = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.integracao.sem.mapeamento", responsavel);
        String hoje = DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss");
        String nomeArquivoSemProcessamento = diretorioSaida + prefixoSemProcessamento + "_" + hoje + "_" + periodo + ".txt";
        String nomeArquivoSemMapeamento = diretorioSaida + prefixoSemMapeamento + "_" + hoje + "_" + periodo + ".txt";

        ListarLinhasBlocosSemProcessamentoQuery query = new ListarLinhasBlocosSemProcessamentoQuery();
        query.tipoEntidade = tipoEntidade;
        query.codigoEntidade = codigoEntidade;

        query.semProcessamento = true;
        List<String> linhasSemProcessamento = query.executarLista();
        if (linhasSemProcessamento != null && !linhasSemProcessamento.isEmpty()) {
            try (PrintWriter arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArquivoSemProcessamento)))) {
                for (String linha : linhasSemProcessamento) {
                    if (!TextHelper.isNull(linha)) {
                        // Remove os colchetes de início e fim
                        arqSaida.println(linha.substring(1, linha.length() - 1));
                    }
                }
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ZetraException("mensagem.erro.relatorio.integracao.gravar.arquivo", responsavel, ex);
            }
            if (ParamSist.paramEquals(CodedValues.TPC_GERAR_RELATORIOS_INTEGRACAO_COMPACTADOS, CodedValues.TPC_SIM, responsavel)) {
                // Compacta o relatório TXT e remove, deixando apenas o ZIP
                try {
                    FileHelper.zipAndRemove(nomeArquivoSemProcessamento);
                } catch (IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
            // Gera relatório em XLS
            if (ParamSist.paramEquals(CodedValues.TPC_GERA_RELATORIO_INTEGRACAO_XLS, CodedValues.TPC_SIM, responsavel)) {
                impRetornoController.geraRelatorioIntegracaoSemProcessamentoXLS(nomeArquivoSemProcessamento, entradaImpRetorno, tradutorImpRetorno, diretorioSaida, responsavel);
            }
        }

        query.semProcessamento = false;
        List<String> linhasSemMapeamento = query.executarLista();
        if (linhasSemMapeamento != null && !linhasSemMapeamento.isEmpty()) {
            try (PrintWriter arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArquivoSemMapeamento)))) {
                for (String linha : linhasSemMapeamento) {
                    if (!TextHelper.isNull(linha)) {
                        // Remove os colchetes de início e fim
                        arqSaida.println(linha.substring(1, linha.length() - 1));
                    }
                }
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ZetraException("mensagem.erro.relatorio.integracao.gravar.arquivo", responsavel, ex);
            }
            if (ParamSist.paramEquals(CodedValues.TPC_GERAR_RELATORIOS_INTEGRACAO_COMPACTADOS, CodedValues.TPC_SIM, responsavel)) {
                // Compacta o relatório TXT e remove, deixando apenas o ZIP
                try {
                    FileHelper.zipAndRemove(nomeArquivoSemMapeamento);
                } catch (IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
            // Gera relatório em XLS
            if (ParamSist.paramEquals(CodedValues.TPC_GERA_RELATORIO_INTEGRACAO_XLS, CodedValues.TPC_SIM, responsavel)) {
                impRetornoController.geraRelatorioIntegracaoSemMapeamentoXLS(nomeArquivoSemMapeamento, entradaImpRetorno, tradutorImpRetorno, diretorioSaida, responsavel);
            }
        }

        LOG.debug("FIM ARQUIVOS CSE: " + DateHelper.getSystemDatetime());
    }

    private List<TransferObject> recuperaMargemMediaTotal(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            RecuperaMargemMediaTotalQuery query = new RecuperaMargemMediaTotalQuery();
            query.orgCodigos = orgCodigos;
            query.estCodigos = estCodigos;
            query.recuperaRseExcluido = false;
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<TransferObject> recuperaMargemExtraMediaTotal(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            RecuperaMargemExtraMediaTotalQuery query = new RecuperaMargemExtraMediaTotalQuery();
            query.orgCodigos = orgCodigos;
            query.estCodigos = estCodigos;
            query.recuperaRseExcluido = false;
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private int contarRegistroServidor(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            ListaRegistroServidorQuery query = new ListaRegistroServidorQuery();
            query.count = true;
            query.orgCodigos = orgCodigos;
            query.estCodigos = estCodigos;
            query.recuperaRseExcluido = false;
            return query.executarContador();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void iniciarHistoricoMargem(String periodo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ZetraException {
        int totalSerAtivoAntigo = 0;
        List<TransferObject> margemMediaTotalAntiga = null;
        List<TransferObject> margemExtraMediaTotalAntiga = new ArrayList<>();

        List<String> orgCodigos = null;
        List<String> estCodigos = null;
        if (!TextHelper.isNull(tipoEntidade) && !TextHelper.isNull(codigoEntidade)) {
            if (tipoEntidade.equalsIgnoreCase("EST")) {
                estCodigos = new ArrayList<>();
                estCodigos.add(codigoEntidade);
            } else if (tipoEntidade.equalsIgnoreCase("ORG")) {
                orgCodigos = new ArrayList<>();
                orgCodigos.add(codigoEntidade);
            }
        }

        // Recupera a quantidade de servidores ativos antes de iniciar o processamento
        totalSerAtivoAntigo = contarRegistroServidor(orgCodigos, estCodigos, responsavel);

        // Recupera a média de margem antes de iniciar o processamento
        margemMediaTotalAntiga = recuperaMargemMediaTotal(orgCodigos, estCodigos, responsavel);

        // Recupera a média das margens extras antes de iniciar o processamento
        margemExtraMediaTotalAntiga = recuperaMargemExtraMediaTotal(orgCodigos, estCodigos, responsavel);

        // Cria media margem antes de iniciar o processamento
        TransferObject margemMediaAntiga = margemMediaTotalAntiga.iterator().next();
        float margemAntiga = 0;
        if (!TextHelper.isNull(margemMediaAntiga.getAttribute("RSE_MARGEM"))) {
            margemAntiga = Float.parseFloat(margemMediaAntiga.getAttribute("RSE_MARGEM").toString());
        }
        float margem2Antiga = 0;
        if (!TextHelper.isNull(margemMediaAntiga.getAttribute("RSE_MARGEM_2"))) {
            margem2Antiga = Float.parseFloat(margemMediaAntiga.getAttribute("RSE_MARGEM_2").toString());
        }
        float margem3Antiga = 0;
        if (!TextHelper.isNull(margemMediaAntiga.getAttribute("RSE_MARGEM_3"))) {
            margem3Antiga = Float.parseFloat(margemMediaAntiga.getAttribute("RSE_MARGEM_3").toString());
        }
        float margemAtual = 0;
        float margem2Atual = 0;
        float margem3Atual = 0;

        Map<Short, Map<String, BigDecimal>> mediaMargem = new HashMap<>();
        Map<String, BigDecimal> medias = null;

        // Margem 1
        medias = new HashMap<>();
        medias.put(Columns.HMM_MEDIA_MARGEM_ANTES, BigDecimal.valueOf(margemAntiga));
        medias.put(Columns.HMM_MEDIA_MARGEM_DEPOIS, BigDecimal.valueOf(margemAtual));
        mediaMargem.put(CodedValues.INCIDE_MARGEM_SIM, medias);

        // Margem 2
        medias = new HashMap<>();
        medias.put(Columns.HMM_MEDIA_MARGEM_ANTES, BigDecimal.valueOf(margem2Antiga));
        medias.put(Columns.HMM_MEDIA_MARGEM_DEPOIS, BigDecimal.valueOf(margem2Atual));
        mediaMargem.put(CodedValues.INCIDE_MARGEM_SIM_2, medias);

        // Margem 3
        medias = new HashMap<>();
        medias.put(Columns.HMM_MEDIA_MARGEM_ANTES, BigDecimal.valueOf(margem3Antiga));
        medias.put(Columns.HMM_MEDIA_MARGEM_DEPOIS, BigDecimal.valueOf(margem3Atual));
        mediaMargem.put(CodedValues.INCIDE_MARGEM_SIM_3, medias);

        // Margem extra
        for (TransferObject margemExtraMediaAntiga : margemExtraMediaTotalAntiga) {
            float margemExtraAntiga = 0;
            if (!TextHelper.isNull(margemExtraMediaAntiga.getAttribute("MRS_MARGEM"))) {
                margemExtraAntiga = Float.parseFloat(margemExtraMediaAntiga.getAttribute("MRS_MARGEM").toString());
            }
            float margemExtraAtual = 0;

            if (margemExtraAntiga != 0) {
                medias = new HashMap<>();
                medias.put(Columns.HMM_MEDIA_MARGEM_ANTES, BigDecimal.valueOf(margemExtraAntiga));
                medias.put(Columns.HMM_MEDIA_MARGEM_DEPOIS, BigDecimal.valueOf(margemExtraAtual));
                mediaMargem.put(Short.valueOf(String.valueOf(margemExtraMediaAntiga.getAttribute("MAR_CODIGO"))), medias);
            }
        }

        // Salva histórico do processamento da importação das margens
        TransferObject historicoMargem = new CustomTransferObject();
        historicoMargem.setAttribute(Columns.HPM_PERIODO, periodo);
        historicoMargem.setAttribute(Columns.HPM_QTD_SERVIDORES_ANTES, totalSerAtivoAntigo);
        historicoMargem.setAttribute(Columns.HPM_QTD_SERVIDORES_DEPOIS, 0);
        List<Short> lstMarCodigosExtra = new ArrayList<>();
        for (TransferObject margemExtra : margemExtraMediaTotalAntiga) {
            if (!TextHelper.isNull(margemExtra.getAttribute("MAR_CODIGO"))) {
                lstMarCodigosExtra.add(Short.valueOf(margemExtra.getAttribute("MAR_CODIGO").toString()));
            }
        }
        margemController.createHistoricoMargem(historicoMargem, mediaMargem, lstMarCodigosExtra, orgCodigos, estCodigos, responsavel);
    }

    private void finalizarHistoricoMargem(String periodo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ZetraException {
        int totalSerAtivoAtual = 0;
        List<TransferObject> margemMediaTotalAtual = null;
        List<TransferObject> margemExtraMediaTotalAtual = new ArrayList<>();

        List<String> orgCodigos = null;
        List<String> estCodigos = null;
        if (!TextHelper.isNull(tipoEntidade) && !TextHelper.isNull(codigoEntidade)) {
            if (tipoEntidade.equalsIgnoreCase("EST")) {
                estCodigos = new ArrayList<>();
                estCodigos.add(codigoEntidade);
            } else if (tipoEntidade.equalsIgnoreCase("ORG")) {
                orgCodigos = new ArrayList<>();
                orgCodigos.add(codigoEntidade);
            }
        }

        // Recupera a quantidade de servidores ativos ao finalizar o processamento
        totalSerAtivoAtual = contarRegistroServidor(orgCodigos, estCodigos, responsavel);

        // Recupera a média de margem ao finalizar o processamento
        margemMediaTotalAtual = recuperaMargemMediaTotal(orgCodigos, estCodigos, responsavel);

        // Recupera a média das margens extras ao finalizar o processamento
        margemExtraMediaTotalAtual = recuperaMargemExtraMediaTotal(orgCodigos, estCodigos, responsavel);

        // Recupera os históricos iniciados antes do processamento
        ListaHistoricoMediaMargemQuery query = new ListaHistoricoMediaMargemQuery();
        query.estCodigos = estCodigos;
        query.orgCodigos = orgCodigos;
        query.hpmPeriodo = DateHelper.parseExceptionSafe(periodo, "yyyy-MM-dd");
        List<TransferObject> historicosMediaMargemNaoFinalizados = query.executarDTO();

        if (historicosMediaMargemNaoFinalizados != null && !historicosMediaMargemNaoFinalizados.isEmpty()) {
            List<Long> hpmCodigosAtualizados = new ArrayList<>();
            for (TransferObject historico : historicosMediaMargemNaoFinalizados) {
                // Atualiza somente os históricos que ainda não foram finalizados
                if (((BigDecimal) historico.getAttribute(Columns.HMM_MEDIA_MARGEM_DEPOIS)).doubleValue() > 0) {
                    continue;
                }

                float hmmMediaMargemDepois = 0;

                // Atualiza o histórico de processamento de margem com a quantidade de servidores depois do processamento
                Long hpmCodigo = (Long) historico.getAttribute(Columns.HPM_CODIGO);
                if (!hpmCodigosAtualizados.contains(hpmCodigo)) {
                    HistoricoProcMargem hpm = HistoricoProcMargemHome.findByPrimaryKey(hpmCodigo);
                    hpm.setHpmQtdServidoresDepois(totalSerAtivoAtual);
                    HistoricoProcMargemHome.update(hpm);
                    hpmCodigosAtualizados.add(hpmCodigo);
                }

                // Atualiza o histórico de média de margem com a média depois do processamento
                if (margemMediaTotalAtual != null && !margemMediaTotalAtual.isEmpty()) {
                    TransferObject margemMediaAtual = margemMediaTotalAtual.iterator().next();
                    Short marCodigo = (Short) historico.getAttribute(Columns.HMM_MAR_CODIGO);

                    // recupera o histórico
                    HistoricoMediaMargemId id = new  HistoricoMediaMargemId();
                    id.setHpmCodigo(hpmCodigo);
                    id.setMarCodigo(marCodigo);
                    HistoricoMediaMargem historicoMediaMargem = HistoricoMediaMargemHome.findByPrimaryKey(id);

                    if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                        hmmMediaMargemDepois = margemMediaAtual.getAttribute("RSE_MARGEM") != null ? Float.parseFloat(margemMediaAtual.getAttribute("RSE_MARGEM").toString()) : 0;
                    } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                        hmmMediaMargemDepois = margemMediaAtual.getAttribute("RSE_MARGEM_2") != null ? Float.parseFloat(margemMediaAtual.getAttribute("RSE_MARGEM_2").toString()) : 0;
                    } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                        hmmMediaMargemDepois = margemMediaAtual.getAttribute("RSE_MARGEM_3") != null ? Float.parseFloat(margemMediaAtual.getAttribute("RSE_MARGEM_3").toString()) : 0;
                    } else {
                        // Verifica margens extras
                        for (TransferObject margemExtraMediaAtual : margemExtraMediaTotalAtual) {
                            if (marCodigo.equals(margemExtraMediaAtual.getAttribute("MAR_CODIGO"))) {
                                hmmMediaMargemDepois = margemExtraMediaAtual.getAttribute("MRS_MARGEM") != null ? Float.parseFloat(margemExtraMediaAtual.getAttribute("MRS_MARGEM").toString()): 0;
                            }
                        }
                    }

                    // Atualiza a média margem
                    historicoMediaMargem.setHmmMediaMargemDepois(BigDecimal.valueOf(hmmMediaMargemDepois));
                    HistoricoMediaMargemHome.update(historicoMediaMargem);
                }
            }
        }
    }

    private void excluirHistoricoMargemNaoFinalizado(String periodo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ZetraException {
        List<String> orgCodigos = null;
        List<String> estCodigos = null;
        if (!TextHelper.isNull(tipoEntidade) && !TextHelper.isNull(codigoEntidade)) {
            if (tipoEntidade.equalsIgnoreCase("EST")) {
                estCodigos = new ArrayList<>();
                estCodigos.add(codigoEntidade);
            } else if (tipoEntidade.equalsIgnoreCase("ORG")) {
                orgCodigos = new ArrayList<>();
                orgCodigos.add(codigoEntidade);
            }
        }

        // Recupera todos os históricos do período de processamento
        ListarHistoricoProcMargemProcessamentoQuery query = new ListarHistoricoProcMargemProcessamentoQuery();
        query.estCodigos = estCodigos;
        query.orgCodigos = orgCodigos;
        query.hpmPeriodo = DateHelper.parseExceptionSafe(periodo, "yyyy-MM-dd");
        List<TransferObject> historicosMediaMargemNaoFinalizados = query.executarDTO();

        if (historicosMediaMargemNaoFinalizados != null && !historicosMediaMargemNaoFinalizados.isEmpty()) {
            List<Long> hpmCodigosAtualizados = new ArrayList<>();
            for (TransferObject historico : historicosMediaMargemNaoFinalizados) {
                // Recupera o histórico de processamento de margem
                Long hpmCodigo = (Long) historico.getAttribute(Columns.HPM_CODIGO);
                if (!hpmCodigosAtualizados.contains(hpmCodigo)) {
                    hpmCodigosAtualizados.add(hpmCodigo);
                }
            }

            if (hpmCodigosAtualizados != null && !hpmCodigosAtualizados.isEmpty()) {
                for (Long hpmCodigo : hpmCodigosAtualizados) {
                    // Remove o histórico de média de margem
                    HistoricoMediaMargemHome.removerHistorico(hpmCodigo);

                    // Remove o registro de histórico por EST/ORG/CSE
                    HistoricoProcMargemEstHome.removerHistorico(hpmCodigo);
                    HistoricoProcMargemOrgHome.removerHistorico(hpmCodigo);
                    HistoricoProcMargemCseHome.removerHistorico(hpmCodigo);

                    // Remove o histórico de processamento de margem
                    HistoricoProcMargem hpm = HistoricoProcMargemHome.findByPrimaryKey(hpmCodigo);
                    HistoricoProcMargemHome.remove(hpm);
                }
            }
        }
    }

    @Override
    public List<TransferObject> listarHistoricoProcessamento(Date hprPeriodo, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ZetraException {
        return listarHistoricoProcessamento(hprPeriodo, orgCodigos, estCodigos, -1, -1, false, responsavel);
    }

    @Override
    public List<TransferObject> listarHistoricoProcessamento(Date hprPeriodo, List<String> orgCodigos, List<String> estCodigos, int offset, int count, boolean orderDesc, AcessoSistema responsavel) throws ZetraException {
        try {
            ListarHistoricoProcessamentoQuery query = new ListarHistoricoProcessamentoQuery();
            query.hprPeriodo = hprPeriodo;
            query.orgCodigos = orgCodigos;
            query.estCodigos = estCodigos;
            query.orderDesc = orderDesc;

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (count != -1) {
                query.maxResults = count;
            }

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<HistoricoProcessamento> obterProcessamentosNaoFinalizados(AcessoSistema responsavel) throws ZetraException {
        return HistoricoProcessamentoHome.findProcessamentosNaoFinalizados();
    }

    @Override
    public List<Integer> obterBlocosAguardProcessamento(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ZetraException {
        try {
            ObtemBlocosProcessamentoAgrupadosQuery query = new ObtemBlocosProcessamentoAgrupadosQuery();
            query.tipoEntidade = tipoEntidade;
            query.codigoEntidade = codigoEntidade;
            query.sbpCodigos = Arrays.asList(StatusBlocoProcessamentoEnum.AGUARD_PROCESSAMENTO.getCodigo());
            return query.executarLista();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<Integer> obterBlocosProcessados(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ZetraException {
        try {
            ObtemBlocosProcessamentoAgrupadosQuery query = new ObtemBlocosProcessamentoAgrupadosQuery();
            query.tipoEntidade = tipoEntidade;
            query.codigoEntidade = codigoEntidade;
            query.sbpCodigos = Arrays.asList(StatusBlocoProcessamentoEnum.PROCESSADO_COM_SUCESSO.getCodigo());
            return query.executarLista();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<TransferObject> obterBlocosMesmoRse(Integer bprCodigo) throws ZetraException {
        ObtemBlocoQuery query = new ObtemBlocoQuery(bprCodigo);
        List<TransferObject> blocos = query.executarDTO();
        if (blocos != null && !blocos.isEmpty()) {
            TransferObject bloco = blocos.get(0);
            String rseCodigo = (String) bloco.getAttribute(Columns.RSE_CODIGO);
            String rseMatricula = (String) bloco.getAttribute(Columns.RSE_MATRICULA);
            String orgIdentificador = (String) bloco.getAttribute(Columns.ORG_IDENTIFICADOR);
            String estIdentificador = (String) bloco.getAttribute(Columns.EST_IDENTIFICADOR);

            if (TextHelper.isNull(rseCodigo) && TextHelper.isNull(rseMatricula)) {
                return new ObtemBlocosProcessamentoRegistroSerQuery(bprCodigo).executarDTO();
            } else {
                return new ObtemBlocosProcessamentoRegistroSerQuery(rseCodigo, rseMatricula, orgIdentificador, estIdentificador).executarDTO();
            }
        }

        return null;
    }

    @Override
    public void processarBloco(Integer bprCodigo, CacheDependenciasServidor cacheEntidades, HistoricoProcessamento processamento, AcessoSistema responsavel) throws ZetraException {
        AutorizacaoDAO adeDAO = DAOFactory.getDAOFactory().getAutorizacaoDAO();
        ParcelaDescontoDAO prdDAO = DAOFactory.getDAOFactory().getParcelaDescontoDAO();
        ImpRetornoDAO retDAO = DAOFactory.getDAOFactory().getImpRetornoDAO();
        ServidorDAO serDAO = DAOFactory.getDAOFactory().getServidorDAO();
        ProcessaFolhaEspecifica classeEspecificaPython = null;

        // Define o nome do arquivo de retorno para registro na tabela de associação
        retDAO.setNomeArqRetorno(processamento.getHprArquivoRetorno());

        // Objeto usado para converter o Map com campos de entrada em JSON, e vice-verba
        Gson gson = new Gson();

        String exportadorClassPython = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CLASSE_ESPECIFICA_PROCESSAMENTO_SEM_BLOQUEIO, responsavel);
        if (!TextHelper.isNull(exportadorClassPython)) {
            classeEspecificaPython = ProcessaFolhaEspecificaFactory.getExportador(exportadorClassPython);
        }

        if (classeEspecificaPython != null) {
            classeEspecificaPython.preProcessarBloco(bprCodigo, responsavel.getUsuCodigo());
        }

        // Para cada registro servidor que possui bloco não processado:
        // 01) Recupera os blocos de processamento pela ordem de presença nos arquivos de entrada.
        List<TransferObject> blocos = obterBlocosMesmoRse(bprCodigo);
        if (blocos == null || blocos.isEmpty()) {
            return;
        }

        // 02) Salva os valores de margem folha para gravação do histórico de margem ao final.
        BigDecimal margemRest1Antes = new BigDecimal(0.00);
        BigDecimal margemRest2Antes = new BigDecimal(0.00);
        BigDecimal margemRest3Antes = new BigDecimal(0.00);
        List<MargemTO> lstMargensAntes = null;
        BigDecimal margemRest1Depois = new BigDecimal(0.00);
        BigDecimal margemRest2Depois = new BigDecimal(0.00);
        BigDecimal margemRest3Depois = new BigDecimal(0.00);
        List<TransferObject> lstMargemRestDepois = null;

        String rseOrgCodigo = null;
        String rseCodigo = null;
        String periodo = null;
        for (TransferObject bloco : blocos) {
            periodo = bloco.getAttribute(Columns.BPR_PERIODO).toString();
            if (bloco.getAttribute(Columns.RSE_CODIGO) != null) {
                rseCodigo = bloco.getAttribute(Columns.RSE_CODIGO).toString();
                break;
            }
        }
        if (!TextHelper.isNull(rseCodigo)) {
            // Busca os valores de margem do servidor para gravação do histórico
            RegistroServidor rseBean = RegistroServidorHome.findByPrimaryKey(rseCodigo);
            margemRest1Antes = rseBean.getRseMargemRest();
            margemRest2Antes = rseBean.getRseMargemRest2();
            margemRest3Antes = rseBean.getRseMargemRest3();
            // Margem extra
            ListaMargemRegistroServidorQuery query = new ListaMargemRegistroServidorQuery(false);
            query.rseCodigo = rseCodigo;
            lstMargensAntes = query.executarDTO(MargemTO.class);
            // órgão do registro servidor
            rseOrgCodigo = rseBean.getOrgao().getOrgCodigo();
        }

        // 03) Processa o bloco do tipo margem, criando/atualizando os dados de servidor, registro servidor e suas dependências, como já ocorre atualmente na carga de margem que não é feita pelo XML de saída.
        for (TransferObject bloco : blocos) {
            if (StatusBlocoProcessamentoEnum.AGUARD_PROCESSAMENTO.equals(bloco.getAttribute(Columns.SBP_CODIGO).toString()) &&
                    TipoBlocoProcessamentoEnum.MARGEM.equals(bloco.getAttribute(Columns.TBP_CODIGO).toString())) {
                bprCodigo = (Integer) bloco.getAttribute(Columns.BPR_CODIGO);

                // Altera o status do bloco para em processamento
                BlocoProcessamentoHome.atualizarStatusBloco(bprCodigo, StatusBlocoProcessamentoEnum.EM_PROCESSAMENTO, null);

                // Converte o JSON de campos em um Map
                Map<String, Object> campos = gson.fromJson((String) bloco.getAttribute(Columns.BPR_CAMPOS), Map.class);

                if (classeEspecificaPython != null && rseCodigo != null) {
                    classeEspecificaPython.preProcessarBlocoMargem(bprCodigo, rseCodigo, (String) bloco.getAttribute(Columns.BPR_CAMPOS), responsavel.getUsuCodigo());
                }

                // Processa o bloco de margem
                String msgErro = null;
                try {
                    msgErro = impCadMargemController.processarLinhaMargem(campos, cacheEntidades, periodo, responsavel);
                } catch (ServidorControllerException ex) {
                    msgErro = ex.getMessage();
                }

                if (TextHelper.isNull(rseCodigo)) {
                    // Busca o registro servidor criado na importação dos blocos de margem
                    ObtemRegistroServidorQuery query = new ObtemRegistroServidorQuery();
                    query.estCodigo = (String) campos.get("EST_CODIGO");
                    query.estIdentificador = (String) campos.get("EST_IDENTIFICADOR");
                    query.estCnpj = (String) campos.get("EST_CNPJ");
                    query.orgCodigo = (String) campos.get("ORG_CODIGO");
                    query.orgIdentificador = (String) campos.get("ORG_IDENTIFICADOR");
                    query.orgCnpj = (String) campos.get("ORG_CNPJ");
                    query.rseMatricula = (String) campos.get("RSE_MATRICULA");
                    query.serCpf = (String) campos.get("SER_CPF");
                    List<TransferObject> rseCodigos = query.executarDTO();
                    if (rseCodigos != null && !rseCodigos.isEmpty()) {
                        rseCodigo = (String) rseCodigos.get(0).getAttribute(Columns.RSE_CODIGO);
                        Date rseDataCarga = (Date) rseCodigos.get(0).getAttribute(Columns.RSE_DATA_CARGA);
                        Date dataPrimeiraOrs = (Date) rseCodigos.get(0).getAttribute(Columns.ORS_DATA);
                        // Cria ocorrência de registro servidor
                        if (dataPrimeiraOrs != null && dataPrimeiraOrs.compareTo(rseDataCarga) < 0) {
                            // Cria ocorrência de reativação para o servidor
                            servidorController.criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_REATIVACAO_POR_CARGA_MARGEM, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.reativacao.carga.margem", responsavel), null, responsavel);
                        } else {
                            // Cria ocorrência de inclusão para o servidor
                            servidorController.criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_INCLUSAO_POR_CARGA_MARGEM, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.inclusao.carga.margem", responsavel), null, responsavel);
                        }
                    }
                }

                // Altera o status do bloco para processado
                BlocoProcessamentoHome.atualizarStatusBloco(bprCodigo, TextHelper.isNull(msgErro) ? StatusBlocoProcessamentoEnum.PROCESSADO_COM_SUCESSO : StatusBlocoProcessamentoEnum.PROCESSADO_COM_ERRO, msgErro);

                if (classeEspecificaPython != null && rseCodigo != null) {
                    classeEspecificaPython.posProcessarBlocoMargem(bprCodigo, rseCodigo, (String) bloco.getAttribute(Columns.BPR_CAMPOS), responsavel.getUsuCodigo());
                }

            }
        }

        // 04) Verifica se existem transferências de consignações a serem executadas para o registro servidor, ou seja, algum outro servidor, de mesmo CPF, conforme as regras do parâmetro de sistema 86.
        if (!TextHelper.isNull(rseCodigo)) {
            List<TransferObject> transferidos = serDAO.obtemServidoresTransferidos(rseCodigo, responsavel);
            if (transferidos != null && !transferidos.isEmpty()) {
                for (TransferObject transferido : transferidos) {
                	// Altera o formato da data para evitar problemas na conversão do tipo datetime para representação json usando a biblioteca gson.
                	try {
                		String dataMudanca = transferido.getAttribute("DATA_MUDANCA").toString();
                		if (DateHelper.verifyPattern(dataMudanca, "yyyy-MM-dd'T'HH:mm:ss")) {
                			transferido.setAttribute("DATA_MUDANCA", DateHelper.reformat(dataMudanca, "yyyy-MM-dd'T'HH:mm:ss","yyyy-MM-dd HH:mm:ss"));
                		}
                	} catch (ParseException ex) {
                		LOG.error(ex.getMessage(), ex);
                	}

                    // Cria o bloco de servidor transferido
                    Map<String, Object> entrada = transferido.getAtributos();

                    String estCodigo = entrada.get("NOVO_CODIGO_ESTABELECIMENTO").toString();
                    String estIdentificador = entrada.get("NOVO_ESTABELECIMENTO").toString();
                    String orgCodigo = entrada.get("NOVO_CODIGO_ORGAO").toString();
                    String orgIdentificador = entrada.get("NOVO_ORGAO").toString();
                    String rseMatricula = entrada.get("NOVA_MATRICULA").toString();
                    String serCpf = entrada.get("SER_CPF").toString();

                    // Cria um novo bloco com esta identificação do servidor, e outra coluna com o JSON (*2) das demais informações traduzidas.
                    bprCodigo = BlocoProcessamentoHome.create(StatusBlocoProcessamentoEnum.EM_PROCESSAMENTO, TipoBlocoProcessamentoEnum.TRANSFERIDO, processamento.getHprPeriodo(), 0, "</>", 0, gson.toJson(entrada),
                            null, null, null, estIdentificador, orgIdentificador, rseMatricula, serCpf, null, null, rseCodigo, null, estCodigo, orgCodigo).getBprCodigo();

                    if (classeEspecificaPython != null) {
                        String out = gson.toJson(entrada);
                        classeEspecificaPython.preProcessarBlocoTransferidos(bprCodigo, rseCodigo, out, responsavel.getUsuCodigo());
                    }

                    // Processa o bloco de transferidos
                    String msgErro = null;
                    try {
                        // 05) Realiza as transferências de consignações, caso existam, seguindo a rotina atual de transferência existente.
                        msgErro = impCadMargemController.processarLinhaTransferidos(entrada, responsavel);
                    } catch (ZetraException ex) {
                        msgErro = ex.getMessage();
                    }

                    // Altera o status do bloco para processado
                    BlocoProcessamentoHome.atualizarStatusBloco(bprCodigo, TextHelper.isNull(msgErro) ? StatusBlocoProcessamentoEnum.PROCESSADO_COM_SUCESSO : StatusBlocoProcessamentoEnum.PROCESSADO_COM_ERRO, msgErro);

                    if (classeEspecificaPython != null) {
                        String out = gson.toJson(entrada);
                        classeEspecificaPython.posProcessarBlocoTransferidos(bprCodigo, rseCodigo, out, responsavel.getUsuCodigo());
                    }

                }
            }
        }

        // 06) Verifica se é necessário criar parcelas para as consignações do registro servidor, conforme regra atual do método ParcelaDescontoDAO.insereParcelasFaltantes.
        if (!TextHelper.isNull(rseCodigo)) {
            prdDAO.insereParcelasFaltantes(null, null, Arrays.asList(rseCodigo), CodedValues.INTEGRACAO_RETORNO, responsavel);
            retDAO.zeraCamposFolha(null, null, Arrays.asList(rseCodigo), responsavel);
        } else {
            // Quando o rseCodigo não está preenchido, indica que o servidor provavelmente não está ativo e/ou não foi enviado na margem.
            // Tenta recuperar o RSE_CODIGO utilizando os dados dos blocos de processamento para aplicar as regras de parcelas faltantes e zerar os campos folha.
            for (TransferObject bloco : blocos) {
                // Converte o JSON de campos em um Map
                Map<String, Object> campos = gson.fromJson((String) bloco.getAttribute(Columns.BPR_CAMPOS), Map.class);

                ObtemRegistroServidorQuery query = new ObtemRegistroServidorQuery();
                query.estCodigo = (String) campos.get("EST_CODIGO");
                query.estIdentificador = (String) campos.get("EST_IDENTIFICADOR");
                query.estCnpj = (String) campos.get("EST_CNPJ");
                query.orgCodigo = (String) campos.get("ORG_CODIGO");
                query.orgIdentificador = (String) campos.get("ORG_IDENTIFICADOR");
                query.orgCnpj = (String) campos.get("ORG_CNPJ");
                query.rseMatricula = (String) campos.get("RSE_MATRICULA");
                query.serCpf = (String) campos.get("SER_CPF");
                List<TransferObject> rseCodigos = query.executarDTO();
                if (rseCodigos != null && !rseCodigos.isEmpty()) {
                    String rseCodigoNaoMapeado = (String) rseCodigos.get(0).getAttribute(Columns.RSE_CODIGO);
                    prdDAO.insereParcelasFaltantes(null, null, Arrays.asList(rseCodigoNaoMapeado), CodedValues.INTEGRACAO_RETORNO, responsavel);
                    retDAO.zeraCamposFolha(null, null, Arrays.asList(rseCodigoNaoMapeado), responsavel);
                    break;
                }
            }
        }

        // 07) Processa os blocos de retorno para o registro servidor, conforme regra da fase 3 de pagamento, ou seja começando com a maior quantidade de chaves de localização de parcelas, e vai diminuindo a exigência para identificação da linha.
        // Parâmetro que diz se a exportação é apenas inicial
        boolean exportaMensal = !ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, responsavel);
        // Pega o parâmetros consolida_descontos
        boolean consolida = ParamSist.paramEquals(CodedValues.TPC_CONSOLIDA_DESCONTOS_RETORNO, CodedValues.TPC_SIM, responsavel);

        // Busca as configurações XML que informam quais são os campos chave e em que
        // ordem eles devem ser excluídos na busca por parcelas.
        String[] ordemExcCamposChave = processamento.getHprOrdemExcCamposChave().split(",");
        List<String> camposChave = Arrays.asList(processamento.getHprChaveIdentificacao().split(","));

        // Lista de códigos de autorizações que serão alteradas
        List<String> adeCodigosAlteracao = new ArrayList<>();
        // Lista de códigos de autorizações que serão liquidados
        List<String> adeCodigosLiquidacao = new ArrayList<>();
        // HashMap que guarda o tipo envio das críticas, para geração da ocorrência de relançamento
        HashMap<String, String> adeTipoEnvio = new HashMap<>();

        for (TransferObject bloco : blocos) {
            if (StatusBlocoProcessamentoEnum.AGUARD_PROCESSAMENTO.equals(bloco.getAttribute(Columns.SBP_CODIGO).toString()) &&
                    TipoBlocoProcessamentoEnum.RETORNO.equals(bloco.getAttribute(Columns.TBP_CODIGO).toString())) {
                bprCodigo = (Integer) bloco.getAttribute(Columns.BPR_CODIGO);
                Integer bprNumLinha = (Integer) bloco.getAttribute(Columns.BPR_NUM_LINHA);

                // Altera o status do bloco para em processamento
                BlocoProcessamentoHome.atualizarStatusBloco(bprCodigo, StatusBlocoProcessamentoEnum.EM_PROCESSAMENTO, null);

                // Converte o JSON de campos em um Map
                Map<String, Object> campos = gson.fromJson((String) bloco.getAttribute(Columns.BPR_CAMPOS), Map.class);
                if (TextHelper.isNull(campos.get(PERIODO))) {
                    // Assume o período do bloco de processameto
                    campos.put(PERIODO, DateHelper.format((Date) bloco.getAttribute(Columns.BPR_PERIODO), "yyyy-MM-dd"));
                }

                if (classeEspecificaPython != null) {
                    classeEspecificaPython.preProcessarBlocoRetorno(bprCodigo, rseCodigo, (String) bloco.getAttribute(Columns.BPR_CAMPOS), responsavel.getUsuCodigo());
                }

                Map<String, Map<String, Object>> linhasSemProcessamento = new HashMap<>();
                linhasSemProcessamento.put(bprNumLinha.toString(), campos);

                // Processa o bloco de retorno
                String msgErro = null;
                try {
                    impRetornoController.pagaParcelasParciais(linhasSemProcessamento, adeTipoEnvio, adeCodigosAlteracao, adeCodigosLiquidacao, camposChave, ordemExcCamposChave, false, false, exportaMensal, consolida, adeDAO, retDAO, prdDAO, responsavel);
                } catch (ImpRetornoControllerException ex) {
                    msgErro = ex.getMessage();
                }

                // Altera o status do bloco para processado
                BlocoProcessamentoHome.atualizarStatusBloco(bprCodigo, TextHelper.isNull(msgErro) ? StatusBlocoProcessamentoEnum.PROCESSADO_COM_SUCESSO : StatusBlocoProcessamentoEnum.PROCESSADO_COM_ERRO, msgErro);

                if (classeEspecificaPython != null) {
                    classeEspecificaPython.posProcessarBlocoRetorno(bprCodigo, rseCodigo, (String) bloco.getAttribute(Columns.BPR_CAMPOS), responsavel.getUsuCodigo());
                }

            }
        }

        // 08) Rejeita as demais parcelas de consignações do registro servidor que não tiveram retorno.
        if (!TextHelper.isNull(rseCodigo)) {
            // Cria a ocorrência para as parcelas sem o retorno da folha
            prdDAO.criaOcorrenciaSemRetorno("RSE", rseCodigo, responsavel);
            // Rejeita todas as parcelas que estão em processamento
            prdDAO.liquidaParcelas(null, CodedValues.SPD_REJEITADAFOLHA, "RSE", rseCodigo);
        }

        // 09) Atualiza as consignações do registro servidor, sejam aquelas que tiveram parcelas pagas, ou não. As que foram pagas, deve-se incrementar o número de parcelas pagas, e atualizar o status. As não pagas, podem sofrer conclusão antecipada ou reimplante automático.
        if (!adeCodigosAlteracao.isEmpty()) {
            // Passa TRUE como "atrasado" para que só atualize as consignações passadas em "adeCodigosAlteracao"
            adeDAO.atualizaAdeExportadas(null, null, adeCodigosAlteracao, true, responsavel);
        }

        // 10) Atualiza o valor das ADE cujo parametro de serviço diz para atualizar.
        if (!TextHelper.isNull(rseCodigo) && !TextHelper.isNull(rseOrgCodigo)) {
            Date proximoPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(rseOrgCodigo, responsavel);
            retDAO.atualizarAdeVlrRetorno(DateHelper.format(proximoPeriodo, "yyyy-MM-dd"), rseCodigo);
        }

        // 11) Move as parcelas do período do retorno para a tabela histórica.
        if (!TextHelper.isNull(rseCodigo)) {
            prdDAO.moverParcelasIntegradasPorRse(rseCodigo, DateHelper.format(processamento.getHprPeriodo(), "yyyy-MM-dd"));
        }

        if (!TextHelper.isNull(rseCodigo)) {
            // 12) Recalcula a margem do registro servidor.
            try {
                margemController.recalculaMargem("RSE", Arrays.asList(rseCodigo), serDAO, true, false, responsavel);
                lstMargemRestDepois = serDAO.buscarMargemServidor(rseCodigo, responsavel);
            } catch (DAOException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
            }

            // 13) Grava o histórico de margem referente ao processamento de retorno do período.
            // Busca os valores de margem do servidor para gravação do histórico
            if (lstMargemRestDepois != null && !lstMargemRestDepois.isEmpty()) {
                for (TransferObject margemDepois : lstMargemRestDepois) {
                    Map<String, Object> entrada = margemDepois.getAtributos();
                    margemRest1Depois = NumberHelper.objectToBigDecimal(entrada.get("RSE_MARGEM_REST"));
                    margemRest2Depois = NumberHelper.objectToBigDecimal(entrada.get("RSE_MARGEM_REST_2"));
                    margemRest3Depois = NumberHelper.objectToBigDecimal(entrada.get("RSE_MARGEM_REST_3"));
                }
            }
            if (margemRest1Antes != null && margemRest1Depois != null && !margemRest1Antes.equals(margemRest1Depois)) {
                HistoricoMargemRegistroServidorHome.create(rseCodigo, CodedValues.INCIDE_MARGEM_SIM, null, OperacaoHistoricoMargemEnum.IMPORTACAO_RET_MOV_FIN.getCodigo(), margemRest1Antes, margemRest1Depois);
            }
            if (margemRest2Antes != null && margemRest2Depois != null && !margemRest2Antes.equals(margemRest2Depois)) {
                HistoricoMargemRegistroServidorHome.create(rseCodigo, CodedValues.INCIDE_MARGEM_SIM_2, null, OperacaoHistoricoMargemEnum.IMPORTACAO_RET_MOV_FIN.getCodigo(), margemRest2Antes, margemRest2Depois);
            }
            if (margemRest3Antes != null && margemRest3Depois != null && !margemRest3Antes.equals(margemRest3Depois)) {
                HistoricoMargemRegistroServidorHome.create(rseCodigo, CodedValues.INCIDE_MARGEM_SIM_3, null, OperacaoHistoricoMargemEnum.IMPORTACAO_RET_MOV_FIN.getCodigo(), margemRest3Antes, margemRest3Depois);
            }
            // Margem extra
            ListaMargemRegistroServidorQuery query = new ListaMargemRegistroServidorQuery(false);
            query.rseCodigo = rseCodigo;
            List<MargemTO> lstMargensDepois = query.executarDTO(MargemTO.class);

            // Itera sobre os registros de margem depois do processamento, e caso o registro "antes" não exista
            // será considerado como Zero
            if (lstMargensDepois != null && !lstMargensDepois.isEmpty()) {
                for (MargemTO margemDepois : lstMargensDepois) {

                    if (classeEspecificaPython != null) {
                        classeEspecificaPython.preProcessarRecalculoMargem(bprCodigo, rseCodigo, responsavel.getUsuCodigo());
                    }

                    BigDecimal margemRestAntes = BigDecimal.ZERO;
                    if (lstMargensAntes != null && !lstMargensAntes.isEmpty()) {
                        for (MargemTO margemAntes : lstMargensAntes) {
                            if (margemDepois.getMarCodigo().equals(margemAntes.getMarCodigo()) && margemAntes.getMrsMargemRest() != null) {
                                margemRestAntes = margemAntes.getMrsMargemRest();
                                break;
                            }
                        }
                    }

                    BigDecimal margemRestDepois = margemDepois.getMrsMargemRest() != null ? margemDepois.getMrsMargemRest() : BigDecimal.ZERO;
                    if (margemRestAntes.compareTo(margemRestDepois) != 0) {
                        HistoricoMargemRegistroServidorHome.create(rseCodigo, margemDepois.getMarCodigo(), null, OperacaoHistoricoMargemEnum.IMPORTACAO_RET_MOV_FIN.getCodigo(), margemRestAntes, margemRestDepois);
                    }

                    if (classeEspecificaPython != null) {
                        classeEspecificaPython.posProcessarRecalculoMargem(bprCodigo, rseCodigo, responsavel.getUsuCodigo());
                    }

                }
            }
        }

        if (classeEspecificaPython != null) {
            classeEspecificaPython.posProcessarBloco(bprCodigo, responsavel.getUsuCodigo());
        }
    }

    @Override
    public void finalizarProcessamento(String tipoEntidade, String codigoEntidade, HistoricoProcessamento processamento, AcessoSistema responsavel) throws ZetraException {
        try {
            Date hoje = DateHelper.toSQLDate(DateHelper.getSystemDate());
            ProcessaFolhaEspecifica classeEspecificaPython = null;

            List<String> orgCodigos = null;
            List<String> estCodigos = null;
            if (!TextHelper.isNull(tipoEntidade) && !TextHelper.isNull(codigoEntidade)) {
                if (tipoEntidade.equalsIgnoreCase("EST")) {
                    estCodigos = new ArrayList<>();
                    estCodigos.add(codigoEntidade);
                } else if (tipoEntidade.equalsIgnoreCase("ORG")) {
                    orgCodigos = new ArrayList<>();
                    orgCodigos.add(codigoEntidade);
                }
            }

            String exportadorClassPython = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CLASSE_ESPECIFICA_PROCESSAMENTO_SEM_BLOQUEIO, responsavel);
            if (!TextHelper.isNull(exportadorClassPython)) {
                classeEspecificaPython = ProcessaFolhaEspecificaFactory.getExportador(exportadorClassPython);
            }

            if (classeEspecificaPython != null) {
                String tipoCodigoEntidade = tipoEntidade + "-" + codigoEntidade;
                classeEspecificaPython.preFinalizarProcessamento(processamento.getHprArquivoMargem(), processamento.getHprArquivoRetorno(), tipoCodigoEntidade, responsavel.getUsuCodigo());
            }


            AutorizacaoDAO adeDAO = DAOFactory.getDAOFactory().getAutorizacaoDAO();
            ParcelaDescontoDAO prdDAO = DAOFactory.getDAOFactory().getParcelaDescontoDAO();
            HistoricoRetMovFinDAO hrmDAO = DAOFactory.getDAOFactory().getHistoricoRetMovFinDAO();
            CalculoMargemDAO marDAO = DAOFactory.getDAOFactory().getCalculoMargemDAO();

            // Define o período do retorno
            List<String> periodos = impRetornoController.recuperaPeriodosRetorno(CodedValues.TIPO_RETORNO_NORMAL, null, orgCodigos, estCodigos, responsavel);

            // Obtém o código das consignações sem retorno
            ListaConsignacaoPrdSemRetornoQuery query = new ListaConsignacaoPrdSemRetornoQuery();
            query.tipoEntidade = tipoEntidade;
            query.codigoEntidade = codigoEntidade;
            List<String> adeCodigos = query.executarLista();

            // Cria a ocorrência para as parcelas sem o retorno da folha
            prdDAO.criaOcorrenciaSemRetorno(tipoEntidade, codigoEntidade, responsavel);
            // Rejeita todas as parcelas que estão em processamento
            prdDAO.liquidaParcelas(hoje.toString(), CodedValues.SPD_REJEITADAFOLHA, tipoEntidade, codigoEntidade);

            // Atualiza as consignações que não foram pagas, podem sofrer conclusão antecipada ou reimplante automático.
            adeDAO.atualizaAdeExportadas(orgCodigos, estCodigos, adeCodigos, false, responsavel);
            adeDAO.concluiAdesNaoPagas(responsavel);
            adeDAO.concluiAdesAguardLiquid(responsavel);
            adeDAO.concluiAdesNaoIntegramFolha(orgCodigos, estCodigos, responsavel);
            adeDAO.concluiAdesServidorExcluido(orgCodigos, estCodigos, responsavel);
            adeDAO.concluiAdesLancamentoNaoPagos(orgCodigos, estCodigos, responsavel);
            compraContratoController.concluiContratosAguardLiquidCompra(responsavel);
            impRetornoController.concluiDespesasComum(periodos.get(0), responsavel);
            impRetornoController.cancelaRelacionamentosInsereAltera(responsavel);

            // Coloca em andamento os contratos em estoque que foram pagos no mes atual.
            adeDAO.retiraDoEstoque(adeCodigos, tipoEntidade, codigoEntidade, responsavel.getUsuCodigo());
            // Coloca em estoque os contratos que não foram pagos este mês.
            if (ParamSist.paramEquals(CodedValues.TPC_COLOCA_ESTOQUE_ADE_NAO_PAGA, CodedValues.TPC_SIM, responsavel)) {
                adeDAO.colocaEmEstoque(adeCodigos, periodos.get(0), tipoEntidade, codigoEntidade, responsavel.getUsuCodigo());
            }

            // DESENV-16879 - Parcelas que são de contratos com forma de pagamento boleto, ou seja, que tem o tda_codigo 55 com o valor de boleto para o periodo processado devem ser liquidadas.
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, CodedValues.TPC_SIM, responsavel)
                    && ParamSist.paramEquals(CodedValues.TPC_PERMITE_RESERVA_SAUDE_SEM_FLUXO_MODULO_SAUDE, CodedValues.TPC_SIM, responsavel)) {
                prdDAO.liquidaParcelasPagamentoBoleto(orgCodigos, estCodigos, periodos, responsavel);
            }

            acaoSuspenderContratoParcelaRejeitada(responsavel);

            // Move as parcelas do período do retorno para a tabela histórica.
            prdDAO.moverParcelasIntegradas(orgCodigos, estCodigos, null, periodos, true);

            // Gera os relatórios de integração customizados, ou seja, os que são gerados a partir dos dados do sistema.
            relatorioController.geraRelatorioIntegracao(estCodigos != null && !estCodigos.isEmpty() ? estCodigos.get(0) : null, orgCodigos != null && !orgCodigos.isEmpty() ? orgCodigos.get(0) : null, responsavel);

            // Pelas linhas não processadas gera os relatórios de integração folha do consignante: sem processamento e sem mapeamento
            gerarRelatorioIntegracaoCse(periodos.get(0), tipoEntidade, codigoEntidade, responsavel);

            // Cancela eventuais blocos não processados em virtute de algum erro
            BlocoProcessamentoHome.atualizarStatusBlocos(StatusBlocoProcessamentoEnum.AGUARD_PROCESSAMENTO, StatusBlocoProcessamentoEnum.CANCELADO, tipoEntidade, codigoEntidade);

            // Finaliza o histórico de conclusão de retorno para o período do processamento, indicando o fim do processo.
            hrmDAO.finalizarHistoricoConclusaoRetorno(orgCodigos, estCodigos);

            ////DESENV-16846 - Média margem folha
            int qntPeriodoMediaMargem = ParamSist.getIntParamSist(CodedValues.TPC_QTD_PERIODO_CALCULO_MEDIA_MARGEM, 12, responsavel);
            if (qntPeriodoMediaMargem > 0) {
                marDAO.calcularMediaMargem(qntPeriodoMediaMargem);
            }

            // Finaliza o histórico de processamento e média de margem do período processado
            finalizarHistoricoMargem(periodos.get(0), tipoEntidade, codigoEntidade, responsavel);

            // Finaliza o histórico de processamento
            processamento.setHprDataFim(DateHelper.getSystemDatetime());
            HistoricoProcessamentoHome.update(processamento);

            // Se o parâmetro de URL do Centralizador mobile estiver setado, então agenda processo
            // de atualização da base de dados de CPFs do Centralizador
            impRetornoController.agendarAtualizacaoBaseCentralCpf(responsavel);

            // Agenda arquivamento de servidores excluídos
            impRetornoController.agendarArquivamentoServidor(orgCodigos, estCodigos, responsavel);

            if (classeEspecificaPython != null) {
                String tipoCodigoEntidade = tipoEntidade + "-" + codigoEntidade;
                classeEspecificaPython.posFinalizarProcessamento(processamento.getHprArquivoMargem(), processamento.getHprArquivoRetorno(), tipoCodigoEntidade, responsavel.getUsuCodigo());
            }

        } catch (ZetraException ex) {
            // Este processo de finalização deverá ser executado em uma única transação devendo desfazer todas as alterações em caso de erro.
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private void acaoSuspenderContratoParcelaRejeitada(AcessoSistema responsavel) throws AutorizacaoControllerException {
        //DESENV-17375: suspende contratos que tiveram a parcela rejeitada no retorno
        if (ParamSist.paramEquals(CodedValues.TPC_EXECUTAR_ACOES_POR_TIPO_DESCONTO_IMPORTACAO_RETORNO, CodedValues.TPC_SIM, responsavel) &&
                ParamSist.paramEquals(CodedValues.TPC_SUSPENDER_CONTRATO_PARCELA_REJEITADA_RETORNO, CodedValues.TPC_SIM, responsavel) &&
                (responsavel.temPermissao(CodedValues.FUN_SUSP_CONSIGNACAO) || responsavel.temPermissao(CodedValues.FUN_SUSP_AVANCADA_CONSIGNACAO))) {
            suspenderConsignacaoController.suspenderContratosParcelaRejeitada(responsavel);
        }
    }

    @Override
    public void interromperProcessamento(Date bprPeriodo, String observacao, AcessoSistema responsavel) throws ZetraException {
        try {
            String strPeriodo = DateHelper.format(bprPeriodo, "yyyy-MM-dd");
            String orsObs = "";
            String tocCodigo = "";

            LOG.debug("INÍCIO - Interrompe o processamento dos blocos [" + strPeriodo + "]: " + responsavel.getUsuCodigo());

            HistoricoRetMovFinDAO hrmDAO = DAOFactory.getDAOFactory().getHistoricoRetMovFinDAO();

            // 01) Verifica se tem processo ativo e, caso encontre, interrompe o processamento em execução
            if (!ControladorProcessos.getInstance().processoAtivo(ProcessaBlocosProcessamentoFolha.CHAVE)) {
                throw new ZetraException("mensagem.erro.exibir.dashboard.processamento.nao.encontrado", responsavel);
            }
            // Interrompe o processo principal
            ProcessaBlocosProcessamentoFolha processoFolha = (ProcessaBlocosProcessamentoFolha) ControladorProcessos.getInstance().getProcesso(ProcessaBlocosProcessamentoFolha.CHAVE);
            processoFolha.interromper();
            try {
                // Aguarda a interrupção total do processo para iniciar as fases de reversão do processamento folha
                processoFolha.join();
            } catch (InterruptedException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            // 02) Cria ocorrência de consignante
            consignanteController.createOcorrenciaCse(CodedValues.TOC_PROCESSAMENTO_INTERROMPIDO, observacao, responsavel);

            // 03) Remove os blocos do processamento que ainda não iniciaram processamento
            List<String> sbpCodigos = new ArrayList<>();
            sbpCodigos.add(StatusBlocoProcessamentoEnum.AGUARD_PROCESSAMENTO.getCodigo());
            sbpCodigos.add(StatusBlocoProcessamentoEnum.PREPARANDO.getCodigo());
            BlocoProcessamentoHome.removerBlocos(sbpCodigos);

            // 04) Realiza o bloqueio de cada servidor que foi excluído no processamento interrompido
            orsObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.bloqueio.carga.margem", responsavel);
            tocCodigo = CodedValues.TOC_RSE_REATIVACAO_POR_CARGA_MARGEM;
            // Lista servidores excluídos no processamento
            ListarRegistroServidorProcessadoQuery rseExcluidoQuery = new ListarRegistroServidorProcessadoQuery();
            rseExcluidoQuery.bprPeriodo = bprPeriodo;
            rseExcluidoQuery.srsCodigos = Arrays.asList(CodedValues.SRS_EXCLUIDO);
            rseExcluidoQuery.tocCodigos = Arrays.asList(CodedValues.TOC_RSE_EXCLUSAO_POR_CARGA_MARGEM);
            List<String> rseCodigoExcluidos = rseExcluidoQuery.executarLista();
            for (String rseCodigo : rseCodigoExcluidos) {
                // Altera o status para bloqueado (SRS_CODIGO = 2)
                RegistroServidorHome.bloquearRegistroServidor(rseCodigo);
                // Cria ocorrência de reativação pela carga de margem (TOC_CODIGO = 92).
                OcorrenciaRegistroServidorHome.create(rseCodigo, tocCodigo, responsavel.getUsuCodigo(), orsObs, responsavel.getIpUsuario(), null);
            }

            // 05) Realiza a exclusão de cada servidor que foi incluído ou reativado no processamento interrompido
            orsObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.exclusao.carga.margem", responsavel);
            tocCodigo = CodedValues.TOC_RSE_EXCLUSAO_POR_CARGA_MARGEM;
            // Lista servidores incluídos ou reativados no processamento
            ListarRegistroServidorProcessadoQuery rseIncluidoQuery = new ListarRegistroServidorProcessadoQuery();
            rseIncluidoQuery.bprPeriodo = bprPeriodo;
            rseIncluidoQuery.srsCodigos = Arrays.asList(CodedValues.SRS_ATIVO);
            rseIncluidoQuery.tocCodigos = Arrays.asList(CodedValues.TOC_RSE_INCLUSAO_POR_CARGA_MARGEM, CodedValues.TOC_RSE_REATIVACAO_POR_CARGA_MARGEM);
            List<String> rseCodigoIncluidos = rseIncluidoQuery.executarLista();
            for (String rseCodigo : rseCodigoIncluidos) {
                // Altera o status para excluído (SRS_CODIGO = 3)
                RegistroServidorHome.excluirRegistroServidor(rseCodigo);
                // Cria ocorrência de exclusão pela carga de margem (TOC_CODIGO = 67).
                OcorrenciaRegistroServidorHome.create(rseCodigo, tocCodigo, responsavel.getUsuCodigo(), orsObs, responsavel.getIpUsuario(), null);
            }

            // 06) Inicia processo para desfazer o processamento de margem e retorno dos blocos
            ProcessaDesfazProcessamentoFolha processo = new ProcessaDesfazProcessamentoFolha(strPeriodo, responsavel);
            processo.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.processamento.folha.interromper", responsavel));
            processo.start();
            ControladorProcessos.getInstance().incluir(ProcessaDesfazProcessamentoFolha.CHAVE, processo);
            try {
                processo.join();
            } catch (InterruptedException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            // 07) Exclui os históricos de processamento e média de margem que foram criados no início do processamento
            excluirHistoricoMargemNaoFinalizado(strPeriodo, null, null, responsavel);

            // 08) Desfaz o histórico de conclusão do retorno
            hrmDAO.desfazerHistoricoConclusaoRetorno(null, null, strPeriodo);

            // 09) Exclui o histórico de processamento
            List<HistoricoProcessamento> historicos = obterProcessamentosNaoFinalizados(responsavel);
            if (historicos != null && !historicos.isEmpty()) {
                for (HistoricoProcessamento historico : historicos) {
                    HistoricoProcessamentoHome.remove(historico);
                }
            }

            // 10) Remove os demais blocos que restaram do processamento interrompido
            BlocoProcessamentoHome.removerBlocos(null, null);

            LOG.debug("FIM - Interrompe o processamento dos blocos [" + strPeriodo + "]: " + responsavel.getUsuCodigo());
        } catch (ZetraException ex) {
            // Este processo de finalização deverá ser executado em uma única transação devendo desfazer todas as alterações em caso de erro.
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void desfazerProcessamentoBloco(Integer bprCodigo, AcessoSistema responsavel) throws ZetraException {
        String rseCodigo = "";
        String bprPeriodo = "";

        boolean temBlocoMargem = false;
        boolean temBlocoRetorno = false;
        Date periodoIni = DateHelper.getSystemDatetime();
        Date periodoFim = DateHelper.getSystemDatetime();

        BigDecimal margemRest1Antes = new BigDecimal(0.00);
        BigDecimal margemRest2Antes = new BigDecimal(0.00);
        BigDecimal margemRest3Antes = new BigDecimal(0.00);
        List<MargemTO> lstMargensAntes = null;
        BigDecimal margemRest1Depois = new BigDecimal(0.00);
        BigDecimal margemRest2Depois = new BigDecimal(0.00);
        BigDecimal margemRest3Depois = new BigDecimal(0.00);
        List<TransferObject> lstMargemRestDepois = null;

        ImpRetornoDAO retDAO = DAOFactory.getDAOFactory().getImpRetornoDAO();

        // Para cada registro servidor que possui bloco processado:
        // 01) Recupera os blocos de processamento pela ordem de presença nos arquivos de entrada.
        List<TransferObject> blocos = new ObtemBlocosProcessamentoRegistroSerQuery(bprCodigo).executarDTO();
        if (blocos != null && !blocos.isEmpty()) {
            for (TransferObject bloco : blocos) {
                // Analisa somente blocos processados com sucesso
                String sbpCodigo = (String) bloco.getAttribute(Columns.SBP_CODIGO);
                if (StatusBlocoProcessamentoEnum.PROCESSADO_COM_SUCESSO.getCodigo().equals(sbpCodigo)) {
                    // Recupera o registro servidor
                    if (TextHelper.isNull(rseCodigo)) {
                        rseCodigo = (String) bloco.getAttribute(Columns.RSE_CODIGO);
                    } else {
                        // Busca o registro servidor criado na importação dos blocos de margem
                        ObtemRegistroServidorQuery query = new ObtemRegistroServidorQuery();
                        query.estCodigo = (String) bloco.getAttribute(Columns.EST_CODIGO);
                        query.estIdentificador = (String) bloco.getAttribute(Columns.EST_IDENTIFICADOR);
                        query.orgCodigo = (String) bloco.getAttribute(Columns.ORG_CODIGO);
                        query.orgIdentificador = (String) bloco.getAttribute(Columns.ORG_IDENTIFICADOR);
                        query.rseMatricula = (String) bloco.getAttribute(Columns.RSE_MATRICULA);
                        query.serCpf = (String) bloco.getAttribute(Columns.SER_CPF);
                        List<TransferObject> rseCodigos = query.executarDTO();
                        if (rseCodigos != null && !rseCodigos.isEmpty()) {
                            rseCodigo = (String) rseCodigos.get(0).getAttribute(Columns.RSE_CODIGO);
                        }
                    }
                    // Recupera o período
                    if (TextHelper.isNull(bprPeriodo)) {
                        bprPeriodo = bloco.getAttribute(Columns.BPR_PERIODO).toString();
                    }
                    // Recupera o tipo do bloco de processamento
                    String tbpCodigo = (String) bloco.getAttribute(Columns.TBP_CODIGO);
                    if (TipoBlocoProcessamentoEnum.MARGEM.getCodigo().equals(tbpCodigo)) {
                        temBlocoMargem = true;
                    } else if (TipoBlocoProcessamentoEnum.RETORNO.getCodigo().equals(tbpCodigo)) {
                        temBlocoRetorno = true;
                    }
                    // Recupera a menor data de processamento
                    if ((temBlocoMargem || temBlocoRetorno) && bloco.getAttribute(Columns.BPR_DATA_PROCESSAMENTO) != null) {
                        Date bprDataProcessamento = (Date) bloco.getAttribute(Columns.BPR_DATA_PROCESSAMENTO);
                        if (bprDataProcessamento.compareTo(periodoIni) < 0) {
                            periodoIni = bprDataProcessamento;
                        }
                    }
                }
            }
        }

        // 02) Chama rotina para desfazer o retorno para reverter parcelas pagas e atualizar as ADEs do registro servidor
        if (temBlocoRetorno && !TextHelper.isNull(rseCodigo)) {
            retDAO.desfazerUltimoRetorno(null, null, bprPeriodo, null, rseCodigo);
        }

        // 03) Restaura a margem anterior dos servidores que tiveram histórico de margem registrado no processamento
        // Considera que os blocos que não tiverem o RSE_CODIGO preenchido são blocos de servidores incluídos e não tem
        // necessidade de restaurar a margem
        if (temBlocoMargem && !TextHelper.isNull(rseCodigo)) {
            RegistroServidor rse = RegistroServidorHome.findByPrimaryKey(rseCodigo);
            // Recupera os valores de margens antes para gravar histórico
            margemRest1Antes = rse.getRseMargemRest();
            margemRest2Antes = rse.getRseMargemRest2();
            margemRest3Antes = rse.getRseMargemRest3();
            // Margem extra
            ListaMargemRegistroServidorQuery query = new ListaMargemRegistroServidorQuery(false);
            query.rseCodigo = rseCodigo;
            lstMargensAntes = query.executarDTO(MargemTO.class);

            // Verificar se existe histórico de margem de registro servidor com data maior que a data inicial do processamento
            ListaHistoricoMargemQuery historicoMargemQuery = new ListaHistoricoMargemQuery(responsavel);
            historicoMargemQuery.rseCodigo = rseCodigo;
            historicoMargemQuery.periodoIni = periodoIni;
            historicoMargemQuery.periodoFim = periodoFim;
            historicoMargemQuery.hmrOperacao = OperacaoHistoricoMargemEnum.IMPORTACAO_RET_MOV_FIN.getCodigo();
            List<TransferObject> historicos = historicoMargemQuery.executarDTO();
            if (historicos != null && !historicos.isEmpty()) {
                List<Short> marCodigos = new ArrayList<>();
                for (TransferObject historico : historicos) {
                    Short marCodigo = (Short) historico.getAttribute(Columns.MAR_CODIGO);
                    // Caso tenha mais de um histórico, pega o último
                    if (marCodigos.isEmpty() || !marCodigos.contains(marCodigo)) {
                        marCodigos.add(marCodigo);
                        BigDecimal hmrMargemAntes = NumberHelper.objectToBigDecimal(historico.getAttribute(Columns.HMR_MARGEM_ANTES));
                        // Calcula a margem que ser ser restaurada a partir da margem usada do registro servidor e da margem restante do último histórico
                        if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                            // Margem 1
                            rse.setRseMargem(hmrMargemAntes.add(rse.getRseMargemUsada()));
                        } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                            // Margem 2
                            rse.setRseMargem2(hmrMargemAntes.add(rse.getRseMargemUsada2()));
                        } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                            // Margem 3
                            rse.setRseMargem3(hmrMargemAntes.add(rse.getRseMargemUsada3()));
                        } else {
                            // Margem extra
                            try {
                                MargemRegistroServidorId id = new MargemRegistroServidorId(marCodigo, rse.getRseCodigo());
                                MargemRegistroServidor mrs = MargemRegistroServidorHome.findByPrimaryKey(id);
                                mrs.setMrsMargem(hmrMargemAntes.add(mrs.getMrsMargemUsada()));
                                MargemRegistroServidorHome.update(mrs);
                            } catch (UpdateException ex) {
                                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                                throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
                            }
                        }
                    }
                }
                // Altera a margem do servidor
                RegistroServidorHome.update(rse);
            }
        }

        // 04) Recalcula a margem e gera novo histórico de margem para o registro servidor com a operação de reversão de retorno
        if (!TextHelper.isNull(rseCodigo) && (temBlocoMargem || temBlocoRetorno)) {
            ServidorDAO serDAO = DAOFactory.getDAOFactory().getServidorDAO();
            try {
                margemController.recalculaMargem("RSE", Arrays.asList(rseCodigo), null, true, false, responsavel);
                lstMargemRestDepois = serDAO.buscarMargemServidor(rseCodigo, responsavel);
            } catch (DAOException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
            }

            // Inicia gravação de histórico de margem do registro servidor
            if (lstMargemRestDepois != null && !lstMargemRestDepois.isEmpty()) {
                for (TransferObject margemDepois : lstMargemRestDepois) {
                    Map<String, Object> entrada = margemDepois.getAtributos();
                    margemRest1Depois = NumberHelper.objectToBigDecimal(entrada.get("RSE_MARGEM_REST"));
                    margemRest2Depois = NumberHelper.objectToBigDecimal(entrada.get("RSE_MARGEM_REST_2"));
                    margemRest3Depois = NumberHelper.objectToBigDecimal(entrada.get("RSE_MARGEM_REST_3"));
                }
            }
            if (margemRest1Antes != null && margemRest1Depois != null && !margemRest1Antes.equals(margemRest1Depois)) {
                HistoricoMargemRegistroServidorHome.create(rseCodigo, CodedValues.INCIDE_MARGEM_SIM, null, OperacaoHistoricoMargemEnum.REVERSAO_RET_MOV_FIN.getCodigo(), margemRest1Antes, margemRest1Depois);
            }
            if (margemRest2Antes != null && margemRest2Depois != null && !margemRest2Antes.equals(margemRest2Depois)) {
                HistoricoMargemRegistroServidorHome.create(rseCodigo, CodedValues.INCIDE_MARGEM_SIM_2, null, OperacaoHistoricoMargemEnum.REVERSAO_RET_MOV_FIN.getCodigo(), margemRest2Antes, margemRest2Depois);
            }
            if (margemRest3Antes != null && margemRest3Depois != null && !margemRest3Antes.equals(margemRest3Depois)) {
                HistoricoMargemRegistroServidorHome.create(rseCodigo, CodedValues.INCIDE_MARGEM_SIM_3, null, OperacaoHistoricoMargemEnum.REVERSAO_RET_MOV_FIN.getCodigo(), margemRest3Antes, margemRest3Depois);
            }
            // Margem extra
            ListaMargemRegistroServidorQuery query = new ListaMargemRegistroServidorQuery(false);
            query.rseCodigo = rseCodigo;
            List<MargemTO> lstMargensDepois = query.executarDTO(MargemTO.class);

            // Itera sobre os registros de margem depois do processamento, e caso o registro "antes" não exista
            // será considerado como Zero
            if (lstMargensDepois != null && !lstMargensDepois.isEmpty()) {
                for (MargemTO margemDepois : lstMargensDepois) {
                    BigDecimal margemRestAntes = BigDecimal.ZERO;
                    if (lstMargensAntes != null && !lstMargensAntes.isEmpty()) {
                        for (MargemTO margemAntes : lstMargensAntes) {
                            if (margemDepois.getMarCodigo().equals(margemAntes.getMarCodigo()) && margemAntes.getMrsMargemRest() != null) {
                                margemRestAntes = margemAntes.getMrsMargemRest();
                                break;
                            }
                        }
                    }

                    BigDecimal margemRestDepois = margemDepois.getMrsMargemRest() != null ? margemDepois.getMrsMargemRest() : BigDecimal.ZERO;
                    if (margemRestAntes.compareTo(margemRestDepois) != 0) {
                        HistoricoMargemRegistroServidorHome.create(rseCodigo, margemDepois.getMarCodigo(), null, OperacaoHistoricoMargemEnum.REVERSAO_RET_MOV_FIN.getCodigo(), margemRestAntes, margemRestDepois);
                    }
                }
            }
        }
    }

}


