package com.zetra.econsig.service.folha;

import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_ERRO_INTERNO_SISTEMA;
import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_OCORRENCIA_ENVIO_EMAIL_PROPOSTA_REFINANCIAMENTO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.exception.AgendamentoControllerException;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CompraContratoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.exception.ImportaRetornoException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.folha.retorno.ImportaRetorno;
import com.zetra.econsig.folha.retorno.ImportaRetornoFactory;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.criptografia.CriptografiaArquivos;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.folha.ExportaMovimentoHelper;
import com.zetra.econsig.helper.folha.HistoricoHelper;
import com.zetra.econsig.helper.folha.ProcessaRetorno;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.xml.XmlHelper;
import com.zetra.econsig.job.jobs.ArquivamentoServidoresJob;
import com.zetra.econsig.job.jobs.AtualizaBaseCentralCpfJob;
import com.zetra.econsig.job.process.ProcessaRelatorioIntegracaoSemMapeamento;
import com.zetra.econsig.job.process.ProcessaRelatorioIntegracaoSemProcessamento;
import com.zetra.econsig.job.process.ProcessaRelatorioPercentualRejeito;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTextoZip;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.parser.config.DocumentoTipo;
import com.zetra.econsig.parser.config.MapeamentoTipo;
import com.zetra.econsig.parser.config.ParametroTipo;
import com.zetra.econsig.persistence.BatchManager;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.dao.ArquivoFaturamentoBeneficioDAO;
import com.zetra.econsig.persistence.dao.AutorizacaoDAO;
import com.zetra.econsig.persistence.dao.CalendarioFolhaDAO;
import com.zetra.econsig.persistence.dao.ControleSaldoDvImpRetornoDAO;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.DespesaComumDAO;
import com.zetra.econsig.persistence.dao.HistoricoMargemDAO;
import com.zetra.econsig.persistence.dao.HistoricoRetMovFinDAO;
import com.zetra.econsig.persistence.dao.ImpRetornoDAO;
import com.zetra.econsig.persistence.dao.ParcelaDescontoDAO;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.IgnoraInconsistenciaAdeHome;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacaoHome;
import com.zetra.econsig.persistence.entity.OcorrenciaParcela;
import com.zetra.econsig.persistence.entity.OcorrenciaParcelaHome;
import com.zetra.econsig.persistence.entity.OcorrenciaParcelaPeriodoHome;
import com.zetra.econsig.persistence.entity.ParcelaDesconto;
import com.zetra.econsig.persistence.entity.ParcelaDescontoHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.StatusRegistroServidor;
import com.zetra.econsig.persistence.entity.StatusRegistroServidorHome;
import com.zetra.econsig.persistence.entity.TipoDesconto;
import com.zetra.econsig.persistence.entity.TipoDescontoHome;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoInsereAlteraAConcluirQuery;
import com.zetra.econsig.persistence.query.consignataria.ListaCsaComOcorrenciaSaldoDevedorQuery;
import com.zetra.econsig.persistence.query.orgao.ListaOrgaoIdentificadorQuery;
import com.zetra.econsig.persistence.query.parametro.ListaAdeRefinanciamentoQuery;
import com.zetra.econsig.persistence.query.parametro.ListaCsaPorcentagemCadastradaTpaQuery;
import com.zetra.econsig.persistence.query.parcela.ListaOcorrenciaParcelaAgrupadaQuery;
import com.zetra.econsig.persistence.query.parcela.ListaOcorrenciaParcelaQuery;
import com.zetra.econsig.persistence.query.parcela.ListaParcelaDescontoUsuariosRejeitadosQuery;
import com.zetra.econsig.persistence.query.parcela.ListaParcelasComAcaoEmTipoDescontoQuery;
import com.zetra.econsig.persistence.query.parcela.ListaParcelasForaPeriodoQuery;
import com.zetra.econsig.persistence.query.parcela.ListaParcelasRejeitadasQuery;
import com.zetra.econsig.persistence.query.parcela.ObtemRetornoNaoDesfeitoQuery;
import com.zetra.econsig.persistence.query.parcela.ObtemTotalParcelasPagasAtrasadasQuery;
import com.zetra.econsig.persistence.query.parcela.ObtemTotalParcelasTransfSemRetornoQuery;
import com.zetra.econsig.persistence.query.periodo.ObtemUltimoPeriodoRetornoQuery;
import com.zetra.econsig.persistence.query.retorno.ListaHistoricoConclusaoRetornoQuery;
import com.zetra.econsig.persistence.query.retorno.ListaOrgaoSemProcessamentoQuery;
import com.zetra.econsig.report.config.ConfigRelatorio;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.agendamento.AgendamentoController;
import com.zetra.econsig.service.compra.CompraContratoController;
import com.zetra.econsig.service.consignacao.CancelarConsignacaoController;
import com.zetra.econsig.service.consignacao.LiquidarConsignacaoController;
import com.zetra.econsig.service.consignacao.SuspenderConsignacaoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.AcaoEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoHistoricoMargemEnum;
import com.zetra.econsig.values.TipoAgendamentoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ImpRetornoControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ImpRetornoControllerBean implements ImpRetornoController {
    public static final String NOME_ARQUIVO_ENTRADA = "fileName";
    public static final String ENTRADA_IMP_RETORNO = "entradaImpRetorno";
    public static final String TRADUTOR_IMP_RETORNO = "tradutorImpRetorno";
    public static final String DIRETORIO_ENTRADA = "pathEntrada";
    public static final String DIRETORIO_SAIDA = "pathSaida";

    private static final String PRD_DATA_REALIZADO = "PRD_DATA_REALIZADO";

    private static final String ANO_MES_DESCONTO = "ANO_MES_DESCONTO";

    private static final String ARQUIVO_DE = "Arquivo de ";

    private static final String LINHAS_NAO_PROCESSADAS = "Linhas não processadas: ";

    private static final String AD_ES_LIQUIDADAS = "ADEs liquidadas = ";

    private static final String AD_ES_ALTERADAS = "ADEs alteradas = ";

    private static final String SITUACAO = "SITUACAO";

    private static final String PRD_CODIGO = "PRD_CODIGO";

    private static final String PRD_NUMERO = "PRD_NUMERO";

    private static final String ADE_TIPO_VLR = "ADE_TIPO_VLR";

    private static final String YYYY_MM_DD = "yyyy-MM-dd";

    private static final String ADE_CODIGO = "ADE_CODIGO";

    private static final String SPD_CODIGOS = "SPD_CODIGOS";

    private static final String PRD_NUMERO_2 = "PRD_NUMERO_2";

    private static final String PRD_NUMERO_1 = "PRD_NUMERO_1";

    private static final String PRD_VLR_PREVISTO = "PRD_VLR_PREVISTO";

    private static final String ART_FERIAS = "ART_FERIAS";

    private static final String OCP_OBS = "OCP_OBS";

    private static final String TOTAL_DE_LINHAS_LIDAS = "Total de linhas lidas = ";

    private static final String LINHAS_LIDAS = "Linhas lidas = ";

    private static final String CSA_IDENTIFICADOR = "CSA_IDENTIFICADOR";

    private static final String CNV_COD_VERBA = "CNV_COD_VERBA";

    private static final String PRD_VLR_REALIZADO = "PRD_VLR_REALIZADO";

    private static final String PERIODO = "PERIODO";

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImpRetornoControllerBean.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private CompraContratoController compraController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private CancelarConsignacaoController cancelarConsignacaoController;

    @Autowired
    private ExportaMovimentoController exportaMovimentoController;

    @Autowired
    private ImpCadMargemController impCadMargemController;

    @Autowired
    private LiquidarConsignacaoController liquidarController;

    @Autowired
    private RelatorioController relatorioController;

    @Autowired
    private AgendamentoController agendamentoController;

    @Autowired
    private PeriodoController periodoController;

    @Autowired
    private MargemController margemController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private SuspenderConsignacaoController suspenderConsignacaoController;

    @Override
    public void criarTabelasImportacaoRetorno(AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cria.tabelas.importacao.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

            final ImpRetornoDAO retDAO = DAOFactory.getDAOFactory().getImpRetornoDAO();
            retDAO.criarTabelasImportacaoRetorno();

            // Cria tabela consolidada do calendário folha, para rotinas na folha quinzenal
            if (!PeriodoHelper.folhaMensal(responsavel)) {
                final CalendarioFolhaDAO calDAO = DAOFactory.getDAOFactory().getCalendarioFolhaDAO();
                calDAO.criarTabelaCalendarioQuinzenal(responsavel);
            }

            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cria.tabelas.importacao.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
        } catch (final DAOException ex) {
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Executa em uma mesma transação a importação de margem, geração e importação de transferidos,
     * importação do arquivo de retorno, conclusão do retorno, importação de sem processamento e
     * geração dos relatórios de integração para que, caso ocorra erro, o processo completo seja revertido.
     * @param nomeArquivoMargem
     * @param nomeArquivoRetorno
     * @param orgCodigo
     * @param estCodigo
     * @param responsavel
     * @throws ImpRetornoControllerException
     */
    @Override
    @SuppressWarnings({"java:S1141", "java:S3358"})
    public void importarMargemRetorno(String nomeArquivoMargem, String nomeArquivoRetorno, String orgCodigo, String estCodigo, AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            final String tipoEntidade = !TextHelper.isNull(estCodigo) ? "EST" : !TextHelper.isNull(orgCodigo) ? "ORG" : "CSE";
            final String codigoEntidade = !TextHelper.isNull(estCodigo) ? estCodigo : !TextHelper.isNull(orgCodigo) ? orgCodigo : CodedValues.CSE_CODIGO_SISTEMA;

            String nomeArqTransferidos = null;
            final String caminhoArquivoMargem = ProcessaRetorno.obtemArquivoProcessamento(ProcessaRetorno.MARGEM, nomeArquivoMargem, tipoEntidade, codigoEntidade, responsavel);

            final ServidorDelegate serDelegate = new ServidorDelegate();
            try {
                LOG.debug("INÍCIO - IMPORTAÇÃO DE MARGENS (" + nomeArquivoMargem + "): " + DateHelper.getSystemDatetime());

                nomeArqTransferidos = serDelegate.importaCadastroMargens(caminhoArquivoMargem, tipoEntidade, codigoEntidade, true, true, responsavel);

                LOG.debug("FIM - IMPORTAÇÃO DE MARGENS (" + nomeArquivoMargem + "): " + DateHelper.getSystemDatetime());

            } catch (final ServidorControllerException ex) {
                LOG.error("ERRO - IMPORTAÇÃO DE MARGENS (" + nomeArquivoMargem + "): " + DateHelper.getSystemDatetime());
                throw new ViewHelperException(ex);
            }

            if ((nomeArqTransferidos == null) || "".equals(nomeArqTransferidos)) {
                LOG.info("Nenhum arquivo de transferidos gerado");
            } else {
                final String caminhoArquivoTransferidos = ProcessaRetorno.obtemArquivoProcessamento(ProcessaRetorno.TRANSFERIDOS, nomeArqTransferidos, tipoEntidade, codigoEntidade, responsavel);

                try {
                    LOG.debug("INÍCIO - IMPORTAÇÃO DE TRANSFERIDOS (" + nomeArqTransferidos + "): " + DateHelper.getSystemDatetime());
                    final boolean qtdLinhasArqTransferidosAcimaPermitido = impCadMargemController.qtdLinhasArqTransferidosAcimaPermitido(caminhoArquivoTransferidos, tipoEntidade, codigoEntidade, responsavel);
                    if (qtdLinhasArqTransferidosAcimaPermitido) {
                        LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.log.warn.cadMargem.qtd.linhas.arq.transferidos.acima.permitido.arg0", responsavel, nomeArqTransferidos));
                    }
                    serDelegate.importaServidoresTransferidos(caminhoArquivoTransferidos, tipoEntidade, codigoEntidade, responsavel);
                    LOG.debug("FIM - IMPORTAÇÃO DE TRANSFERIDOS (" + nomeArqTransferidos + "): " + DateHelper.getSystemDatetime());
                } catch (final ServidorControllerException ex) {
                    LOG.error("ERRO - IMPORTAÇÃO DE TRANSFERIDOS (" + nomeArqTransferidos + "): " + DateHelper.getSystemDatetime());
                    throw new ViewHelperException(ex);
                }
            }

            // Realiza a importação do retorno
            LOG.debug("INÍCIO - IMPORTAÇÃO DE RETORNO (" + nomeArquivoRetorno + "): " + DateHelper.getSystemDatetime());
            importarRetornoIntegracao(nomeArquivoRetorno, orgCodigo, estCodigo, ProcessaRetorno.RETORNO, null, responsavel);
            LOG.debug("FIM - IMPORTAÇÃO DE RETORNO (" + nomeArquivoRetorno + "): " + DateHelper.getSystemDatetime());

            // Realiza a conclusão do processamento do retorno
            LOG.debug("INÍCIO - CONCLUSÃO DE RETORNO: " + DateHelper.getSystemDatetime());
            finalizarIntegracaoFolha(tipoEntidade, codigoEntidade, responsavel);
            LOG.debug("FIM - CONCLUSÃO DE RETORNO: " + DateHelper.getSystemDatetime());

            if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_IMPORTACAO_AUTOM_SEM_PROC, CodedValues.TPC_SIM, responsavel)) {
                // Realiza a importação automática de sem processamento
                LOG.debug("INÍCIO - IMPORTAÇÃO DO SEM PROCESSAMENTO: " + DateHelper.getSystemDatetime());
                final String absolutePath = ParamSist.getDiretorioRaizArquivos();
                final String pathCritica = absolutePath + File.separatorChar + "relatorio" + File.separatorChar + "cse" + File.separatorChar + "integracao";

                final List<TransferObject> linhasSemProcessamento = getLinhasSemProcessamento(responsavel);
                final HistoricoHelper historicoHelper = new HistoricoHelper();
                historicoHelper.importaSemProcessamento(linhasSemProcessamento, pathCritica, responsavel);
                LOG.debug("FIM - IMPORTAÇÃO DO SEM PROCESSAMENTO: " + DateHelper.getSystemDatetime());
            } else {
                LOG.debug("Sistema não importa automaticamente contratos sem processamento.");
            }

            // Gera os relatórios de integração
            LOG.debug("INÍCIO - RELATÓRIO DE INTEGRAÇÃO: " + DateHelper.getSystemDatetime());
            relatorioController.geraRelatorioIntegracao(estCodigo, orgCodigo, responsavel);
            LOG.debug("FIM - RELATÓRIO DE INTEGRAÇÃO: " + DateHelper.getSystemDatetime());

        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (ex instanceof ImpRetornoControllerException) {
                throw (ImpRetornoControllerException) ex;
            }
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Importa o retorno da integração. O parser é utilizado para realizar a decodificação
     * das informações do arquivo de entrada. Um escritor de memória é utilizado, para
     * que as informações estejam disponíveis através de um HashMap.
     *
     * @param nomeArquivo : arquivo contendo o retorno da integraçao
     * @param orgCodigo   : código do órgão, caso a operação esteja sendo realizada por usuário de órgão
     * @param estCodigo   : código do estabelecimento, caso a operação esteja sendo realizada por usuário de órgão com perfil de Estabelecimento
     * @param tipo        : critica, retorno ou atrasado
     * @param periodoRetAtrasado : Período do retorno atrasado
     * @param responsavel : responsável pela importação
     * @throws ConsignanteControllerException
     * @throws ImpRetornoControllerException
     */
    @Override
    @SuppressWarnings({"java:S899","java:S1141"})
    public void importarRetornoIntegracao(String nomeArquivo, String orgCodigo, String estCodigo, String tipo, Date periodoRetAtrasado, AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            final ImpRetornoControllerConf conf = new ImpRetornoControllerConf();
            boolean atrasadoSomaParcela = false;

            // Tipo de importação, se é retorno crítica ou retorno atrasado
            int tipoImportacaoRetorno = -1;
            if (ProcessaRetorno.CRITICA.equalsIgnoreCase(tipo)) {
                tipoImportacaoRetorno = CodedValues.TIPO_RETORNO_CRITICA;
            } else if (ProcessaRetorno.RETORNO.equalsIgnoreCase(tipo)) {
                tipoImportacaoRetorno = CodedValues.TIPO_RETORNO_NORMAL;
            } else if (ProcessaRetorno.ATRASADO.equalsIgnoreCase(tipo) || ProcessaRetorno.ATRASADO_SOMA_PARCELA.equalsIgnoreCase(tipo)) {
                tipoImportacaoRetorno = CodedValues.TIPO_RETORNO_ATRASADO;
                atrasadoSomaParcela = ProcessaRetorno.ATRASADO_SOMA_PARCELA.equalsIgnoreCase(tipo);
                tipo = ProcessaRetorno.ATRASADO;
            } else if (ProcessaRetorno.CRITICA_ATRASADO.equalsIgnoreCase(tipo)) {
                tipoImportacaoRetorno = CodedValues.TIPO_RETORNO_ATRASADO;
            }

            String tipoEntidade = "CSE";
            List<String> entCodigos = null;
            List<String> orgCodigos = null;
            List<String> estCodigos = null;
            if ((orgCodigo != null) && !"".equals(orgCodigo)) {
                orgCodigos = new ArrayList<>();
                orgCodigos.add(orgCodigo);
                tipoEntidade = "ORG";
                entCodigos = orgCodigos;
            }
            if ((estCodigo != null) && !"".equals(estCodigo)) {
                estCodigos = new ArrayList<>();
                estCodigos.add(estCodigo);
                tipoEntidade = "EST";
                entCodigos = estCodigos;
            }

            // Arquivos de configuração para importar retorno
            final Map<String, String> arquivosConfiguracao = buscaArquivosConfiguracao(nomeArquivo, tipo, estCodigo, orgCodigo, responsavel);
            final String fileName = arquivosConfiguracao.get(NOME_ARQUIVO_ENTRADA);
            final String entradaImpRetorno = arquivosConfiguracao.get(ENTRADA_IMP_RETORNO);
            final String tradutorImpRetorno = arquivosConfiguracao.get(TRADUTOR_IMP_RETORNO);
            final String pathSaida = arquivosConfiguracao.get(DIRETORIO_SAIDA);

            // Imprime de onde vem os arquivos de configuração
            LOG.debug("XML Entrada  = " + entradaImpRetorno);
            LOG.debug("XML Tradutor = " + tradutorImpRetorno);
            LOG.debug("Arq Entrada  = " + fileName);
            LOG.debug("Dir Saida    = " + pathSaida);

            ImportaRetorno importadorRetorno = null;
            final String importadorRetornoClassName = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CLASSE_IMPORTADOR_RETORNO, responsavel);
            if (!TextHelper.isNull(importadorRetornoClassName)) {
                importadorRetorno = ImportaRetornoFactory.getImportadorRetorno(importadorRetornoClassName, tipoImportacaoRetorno, orgCodigo, estCodigo);
                if (importadorRetorno.sobreporImportacaoRetorno()) {
                    importadorRetorno.importarRetornoIntegracao(fileName, orgCodigo, estCodigo, tipo, responsavel);
                    return;
                }
            }

            // Parâmetro que diz se a exportação é apenas inicial
            final boolean exportaMensal = !ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, responsavel);
            // Pega o parâmetros consolida_descontos
            final boolean consolida = ParamSist.paramEquals(CodedValues.TPC_CONSOLIDA_DESCONTOS_RETORNO, CodedValues.TPC_SIM, responsavel);

            final DAOFactory daoFactory = DAOFactory.getDAOFactory();
            final AutorizacaoDAO adeDAO = daoFactory.getAutorizacaoDAO();
            final ParcelaDescontoDAO prdDAO = daoFactory.getParcelaDescontoDAO();
            final ImpRetornoDAO retDAO = daoFactory.getImpRetornoDAO();
            final HistoricoRetMovFinDAO hrmDAO = daoFactory.getHistoricoRetMovFinDAO();
            final HistoricoMargemDAO hmaDAO = daoFactory.getHistoricoMargemDAO();
            final CalendarioFolhaDAO calDAO = daoFactory.getCalendarioFolhaDAO();
            final ArquivoFaturamentoBeneficioDAO afbDAO = daoFactory.getArquivoFaturamentoBeneficioDAO();

            // DESENV-10533: valor realizado no retorno atraso deve ser somado ao valor realizado no histórico de parcela
            retDAO.setRetAtrasadoSomaAparcela(atrasadoSomaParcela);
            prdDAO.retornoAtrasadoSomandoAParcela(atrasadoSomaParcela);
            adeDAO.setRetAtrasadoSomaAparcela(atrasadoSomaParcela);

            // Calcula o período, e recupera a data base (mês/ano)
            final String periodoRetorno = recuperaPeriodoRetorno(tipoImportacaoRetorno, periodoRetAtrasado, orgCodigos, estCodigos, responsavel);

            if (tipoImportacaoRetorno != CodedValues.TIPO_RETORNO_ATRASADO) {
                // Executa as validações de período, parcelas e verbas
                exportaMovimentoController.validarExportacaoMovimento(orgCodigos, estCodigos, true, responsavel);
            }

            // Executa as validações específicas do retorno
            try {
                validarImportacaoRetorno(tipoImportacaoRetorno, orgCodigos, estCodigos, responsavel);
            } catch (final ConsignanteControllerException ex) {
                throw new ImpRetornoControllerException(ex);
            }

            calDAO.consolidarCalendarioFolha(orgCodigos, estCodigos, responsavel);

            // Pré-processamento da importação do retorno usando método da classe específica do gestor.
            if (importadorRetorno != null) {
                LOG.debug("pre-processamento importacao retorno: " + DateHelper.getSystemDatetime());
                importadorRetorno.preImportacaoRetorno();
                LOG.debug("fim - pre-processamento importacao retorno " + DateHelper.getSystemDatetime());
            }

            // Começa o processamento. Verifica quais órgãos estão começando o retorno agora.
            final ListaOrgaoSemProcessamentoQuery orgaoSemProcessamentoQuery = new ListaOrgaoSemProcessamentoQuery();
            orgaoSemProcessamentoQuery.orgCodigos = orgCodigos;
            orgaoSemProcessamentoQuery.estCodigos = estCodigos;
            final List<String> orgaos = orgaoSemProcessamentoQuery.executarLista();
            String chaveHistMargem = null;
            // Se tem órgãos que estão começando o retorno, grava histórico
            // de margem para estes órgãos
            if ((orgaos != null) && !orgaos.isEmpty()) {
                chaveHistMargem = hmaDAO.iniciarHistoricoMargem(orgaos, null, null, OperacaoHistoricoMargemEnum.IMPORTACAO_RET_MOV_FIN);
            }

            // Se é importação de retorno normal e permite dois períodos abertos
            // move as parcelas do período futuro para status de aguardando processamento
            if ((tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_NORMAL) &&
                    ParamSist.paramEquals(CodedValues.TPC_PERMITE_DOIS_PERIODOS_EXPORTACAO_ABERTOS, CodedValues.TPC_SIM, responsavel)) {
                prdDAO.alterarStatusParcelasPosPeriodo(orgCodigos, estCodigos, CodedValues.SPD_EMPROCESSAMENTO, CodedValues.SPD_AGUARD_PROCESSAMENTO);
            }

            // Insere parcelas para contratos que não têm parcela para o período atual.
            if (tipoImportacaoRetorno != CodedValues.TIPO_RETORNO_ATRASADO) {
                LOG.debug("insereParcelasFaltantes: " + DateHelper.getSystemDatetime());
                prdDAO.insereParcelasFaltantes(orgCodigos, estCodigos, null, CodedValues.INTEGRACAO_RETORNO, responsavel);
                LOG.debug("fim - insereParcelasFaltantes: " + DateHelper.getSystemDatetime());
            }

            // Passa para null os campos "folha" e para 'N' o campo ade_paga de todos os contratos
            // que possuem parcelas em aberto do orgão/estabelecimento que estão sendo importados
            if (tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_NORMAL) {
                LOG.debug("zeraCamposFolha: " + DateHelper.getSystemDatetime());
                retDAO.zeraCamposFolha(orgCodigos, estCodigos, null, responsavel);
                LOG.debug("fim - zeraCamposFolha: " + DateHelper.getSystemDatetime());

                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_MULTIPLAS_LINHAS_RETORNO_MESMA_PRD, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                    LOG.debug("zeraInformacaoRetornoParcelas: " + DateHelper.getSystemDatetime());
                    retDAO.zeraInformacaoRetornoParcelas(orgCodigos, estCodigos, responsavel);
                    LOG.debug("fim - zeraInformacaoRetornoParcelas: " + DateHelper.getSystemDatetime());
                }
            }

            // Pré-processamento da importação do arquivo de retorno usando método da classe específica do gestor.
            if (importadorRetorno != null) {
                LOG.debug("pre-processamento importacao arquivo retorno: " + DateHelper.getSystemDatetime());
                importadorRetorno.preImportacaoArquivoRetorno();
                LOG.debug("fim - pre-processamento importacao arquivo retorno " + DateHelper.getSystemDatetime());
            }

            final boolean moduloBeneficio = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, CodedValues.TPC_SIM, responsavel);
            // Se é módulo de benefício, remove da tabela de arquivo de faturamento os dados do período informado
            if (moduloBeneficio && (tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_NORMAL)) {
                LOG.debug("=== INICIA REMOÇÃO ARQUIVO FATURAMENTO BENEFÍCIO: " + DateHelper.getSystemDatetime());
                afbDAO.removerArquivoFaturamentoBeneficio(orgCodigos, estCodigos, periodoRetorno);
                LOG.debug("=== FINALIZA REMOÇÃO ARQUIVO FATURAMENTO BENEFÍCIO: " + DateHelper.getSystemDatetime());
            }

            // Lista de códigos de autorizações que serão alteradas
            final List<String> adeCodigosAlteracao = new ArrayList<>();
            // Lista de códigos de autorizações que serão liquidados
            final List<String> adeCodigosLiquidacao = new ArrayList<>();
            // HashMap que guarda o tipo envio das críticas, para geração da ocorrência de relançamento
            final HashMap<String, String> adeTipoEnvio = new HashMap<>();
            // Buffer para armazenar as linhas do arquivo de retorno que não foram encontradas no sistema
            final StringBuilder descarte = new StringBuilder();
            /*
             *  Lista com as linhas que deverão ser processadas novamente pois não foram
             *  encontradas parcelas para os critérios no nível atual.
             *  Deve ser um LinkedHashMap ou outro Map que tenha acesso ordenado às entradas
             *  na mesma seqüência em que foram incluídas.
             */
            final LinkedHashMap<String, Map<String, Object>> linhasSemProcessamento = new LinkedHashMap<>();

            final java.sql.Date hoje = new java.sql.Date(Calendar.getInstance().getTimeInMillis());
            LOG.debug("IMPORTA RETORNO: " + DateHelper.getSystemDatetime());
            importarArquivoRetorno(fileName, entradaImpRetorno, tradutorImpRetorno, tipoImportacaoRetorno,
                    exportaMensal, consolida, hoje, adeCodigosAlteracao, adeCodigosLiquidacao,
                    adeTipoEnvio, descarte, linhasSemProcessamento,
                    orgCodigos, estCodigos, periodoRetorno, periodoRetAtrasado, importadorRetorno, adeDAO, retDAO, prdDAO, conf, responsavel);
            LOG.debug("FIM IMPORTA RETORNO: " + DateHelper.getSystemDatetime());

            // Pós-processamento da importação do arquivo de retorno usando método da classe específica do gestor.
            if (importadorRetorno != null) {
                LOG.debug("pos-processamento importacao arquivo retorno: " + DateHelper.getSystemDatetime());
                importadorRetorno.posImportacaoArquivoRetorno();
                LOG.debug("fim - pos-processamento importacao arquivo retorno " + DateHelper.getSystemDatetime());
            }

            LOG.debug("ATUALIZA ADE: " + DateHelper.getSystemDatetime());
            /* Faz pagas++ e sad_codigo = 'Em Andamento' apenas se a parcela foi Liquidada
               e se é uma importação de retorno. Se for uma importação de crítica, e a
               exportação é apenas inicial, cria ocorrência para relançamento da ADE no
               próximo período. */
            if (!exportaMensal && (tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_CRITICA)) {
                LOG.debug("insereOcorrenciaRelancamento: " + DateHelper.getSystemDatetime());
                adeDAO.insereOcorrenciaRelancamento(adeTipoEnvio, (responsavel != null ? responsavel.getUsuCodigo() : null));
            } else if ((tipoImportacaoRetorno != CodedValues.TIPO_RETORNO_CRITICA) && !adeCodigosAlteracao.isEmpty()) {
                LOG.debug("atualizaAdeExportadas: " + DateHelper.getSystemDatetime());
                adeDAO.atualizaAdeExportadas(orgCodigos, estCodigos, adeCodigosAlteracao, tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_ATRASADO, responsavel);
            }
            LOG.debug("FIM ATUALIZA ADE: " + DateHelper.getSystemDatetime());
            /* Se for uma importação total de retorno chama as rotinas que
             * 1. testados os parâmetros que assim determinam, conclui os contratos não-pagos.
             * 2. conclui os contratos aguardando liquidação fruto de renegociação.
             * 3. conclui os contratos aguardando liquidação de compra e defere(cancela) os contratos que geradores da compra.
             * 4. conclui despesas comuns, caso tenha habilitado módulo de controle de despesas de prefeitura de aeronáutica
             * 5. cancela contratos com relacionamento de insere/altera sem confirmação automática cuja origem são contratos concluídos
             */
            if ((tipoImportacaoRetorno != CodedValues.TIPO_RETORNO_CRITICA) &&
                    (tipoImportacaoRetorno != CodedValues.TIPO_RETORNO_ATRASADO)) {
                adeDAO.concluiAdesNaoPagas(responsavel);
                adeDAO.concluiAdesAguardLiquid(responsavel);
                adeDAO.concluiAdesNaoIntegramFolha(orgCodigos, estCodigos, responsavel);
                adeDAO.concluiAdesServidorExcluido(orgCodigos, estCodigos, responsavel);
                adeDAO.concluiAdesLancamentoNaoPagos(orgCodigos, estCodigos, responsavel);
                concluiContratosAguardLiquidCompra(responsavel);
                concluiDespesasComum(periodoRetorno, responsavel);
                cancelaRelacionamentosInsereAltera(responsavel);
            }
            if (!adeCodigosLiquidacao.isEmpty()) {
                // Liquida os contratos que foram marcados para serem quitados.
                // O motivo pode ser a demissão do servidor, morte, etc...
                LOG.debug("LIQUIDA ADE: " + DateHelper.getSystemDatetime());
                // Verifica dentre as ade's selecionadas para liquidação
                // quais delas são passíveis desta operação
                final List<String> adeCodigos = retDAO.getAdeCodigosPermiteLiquidacao(adeCodigosLiquidacao);
                liquidaConsignacaoParaQuitacao(adeCodigos, responsavel);
                LOG.debug("FIM LIQUIDA ADE: " + DateHelper.getSystemDatetime());
            }
            if (tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_NORMAL) {
                LOG.debug("PARCELA SEM RETORNO: " + DateHelper.getSystemDatetime());
                // Altera o status das parcelas que não foram retornadas para 'Sem Retorno'
                prdDAO.parcelasSemRetorno(orgCodigo, estCodigo, (responsavel != null ? responsavel.getUsuCodigo() : null));
                LOG.debug("FIM PARCELA SEM RETORNO: " + DateHelper.getSystemDatetime());
                
                final Object paramConcluiContratosSuspensosPassaramDataFim = ParamSist.getInstance().getParam(CodedValues.TPC_CONCLUI_CONTRATOS_SUSPENSOS_QUE_PASSARAM_DA_DATA_FIM, responsavel);

                final String strParamConcluiContratosSuspensosPassaramDataFim = paramConcluiContratosSuspensosPassaramDataFim != null ? paramConcluiContratosSuspensosPassaramDataFim.toString() : CodedValues.CONCLUI_CONTRATOS_SUSPENSOS_DESABILITADO;

                if(CodedValues.CONCLUI_CONTRATOS_SUSPENSOS_PELA_CSA.equals(strParamConcluiContratosSuspensosPassaramDataFim)){                  
                    LOG.debug("CONCLUI ADEs SUSPENSAS PELA CSA COM A DATA FIM ULTRAPASSADA: " + DateHelper.getSystemDatetime());
                    //Conclui contratos suspensos pela CSA que ultrapassaram a data fim
                    adeDAO.concluiAdesSuspensasPorDataFim(CodedValues.CONCLUI_CONTRATOS_SUSPENSOS_PELA_CSA, responsavel);
                    LOG.debug("CONCLUI ADEs SUSPENSAS PELA CSA COM A DATA FIM ULTRAPASSADA: " + DateHelper.getSystemDatetime());
                } else if(CodedValues.CONCLUI_CONTRATOS_SUSPENSOS_PELA_CSE.equals(strParamConcluiContratosSuspensosPassaramDataFim)) {      
                    LOG.debug("CONCLUI ADEs SUSPENSAS PELA CSE COM A DATA FIM ULTRAPASSADA: " + DateHelper.getSystemDatetime());
                    //Conclui contratos suspensos pela CSE que ultrapassaram a data fim
                    adeDAO.concluiAdesSuspensasPorDataFim(CodedValues.CONCLUI_CONTRATOS_SUSPENSOS_PELA_CSE, responsavel);
                    LOG.debug("CONCLUI ADEs SUSPENSAS PELA CSE COM A DATA FIM ULTRAPASSADA: " + DateHelper.getSystemDatetime());
                } else if(CodedValues.CONCLUI_CONTRATOS_SUSPENSOS_PELA_CSA_CSE.equals(strParamConcluiContratosSuspensosPassaramDataFim)) {
                    LOG.debug("CONCLUI ADEs SUSPENSAS PELA CSA E CSE COM A DATA FIM ULTRAPASSADA: " + DateHelper.getSystemDatetime());
                    //Conclui contratos suspensos pela CSA e CSE que ultrapassaram a data fim
                    adeDAO.concluiAdesSuspensasPorDataFim(CodedValues.CONCLUI_CONTRATOS_SUSPENSOS_PELA_CSA_CSE, responsavel);
                    LOG.debug("CONCLUI ADEs SUSPENSAS PELA CSA E CSE COM A DATA FIM ULTRAPASSADA: " + DateHelper.getSystemDatetime());
                }

            }

            // Se é módulo de benefício, popula tabela de arquivo de faturamento os dados do período
            if (moduloBeneficio && (tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_NORMAL)) {
                LOG.debug("=== INICIO INSERE ARQUIVO FATURAMENTO BENEFÍCIO: " + DateHelper.getSystemDatetime());
                afbDAO.inserirArquivoFaturamentoBeneficio(orgCodigos, estCodigos, periodoRetorno);
                LOG.debug("=== FIM INSERE ARQUIVO FATURAMENTO BENEFÍCIO: " + DateHelper.getSystemDatetime());
            }


            LOG.debug("ARQUIVOS CSE: " + DateHelper.getSystemDatetime());
            // Gera o path de relatórios de processamento
            final File dir = new File(pathSaida);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new ImpRetornoControllerException("mensagem.erro.criacao.diretorio", responsavel, dir.getAbsolutePath());
            }
            // Gera relatórios dos sem mapeamento
            String nomeArqSaida = pathSaida + File.separatorChar + "sem_mapeamento_" + DateHelper.format(hoje, "dd-MM-yyyy-HHmmss") + ".txt";
            retDAO.geraArqLinhasSemMapeamento(nomeArqSaida);

            // Gera relatório em XLS
            if (ParamSist.paramEquals(CodedValues.TPC_GERA_RELATORIO_INTEGRACAO_XLS, CodedValues.TPC_SIM, responsavel)) {
                geraRelatorioIntegracaoSemMapeamentoXLS(nomeArqSaida, entradaImpRetorno, tradutorImpRetorno, pathSaida, responsavel);
            }

            // Grava arquivo contendo as parcelas não encontradas no sistema
            if (descarte.length() > 0) {
                nomeArqSaida = pathSaida + File.separatorChar + "sem_processamento_" + DateHelper.format(hoje, "dd-MM-yyyy-HHmmss") + ".txt";
                try(PrintWriter arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaida)))) {
                    arqSaida.print(descarte);
                }

                // Gera relatório em XLS
                if (ParamSist.paramEquals(CodedValues.TPC_GERA_RELATORIO_INTEGRACAO_XLS, CodedValues.TPC_SIM, responsavel)) {
                    geraRelatorioIntegracaoSemProcessamentoXLS(nomeArqSaida, entradaImpRetorno, tradutorImpRetorno, pathSaida, responsavel);
                }
            }
            LOG.debug("FIM ARQUIVOS CSE: " + DateHelper.getSystemDatetime());
            if (tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_NORMAL) {
                // Gera o relatório de repasse
                geraRelatorioRepasse(responsavel);
            }
            if (tipoImportacaoRetorno != CodedValues.TIPO_RETORNO_ATRASADO) {
                // Inicia o histórico de conclusão de retorno (Será finalizado na conclusao do retorno)
                // Não cria histórico de conclusão de retorno para retorno atrasado, pois o mesmo nunca é concluido.
                hrmDAO.iniciarHistoricoConclusaoRetorno(orgCodigos, estCodigos, periodoRetorno, chaveHistMargem);
            }

            // Renomeia o arquivo de entrada depois de concluido com sucesso
            final File arquivo = new File(fileName);

            // Quando o processamento é de crítica atrasada o relatório integração deve ser baseado no XML do arquivo de crítica e por isso o arquivo de retorno movido não deve ser considerado.
            final String extensao = !ProcessaRetorno.CRITICA_ATRASADO.equalsIgnoreCase(tipo) ? ".prc" : ".prc.ok";
            arquivo.renameTo(new File(fileName + extensao));

            // Recalcula a margem dos servidores: Verifica parâmetro de sistema
            final boolean recalculaMargem = ParamSist.getBoolParamSist(CodedValues.TPC_RECALCULA_MARGEM_IMP_RETORNO, responsavel);
            if (recalculaMargem) {
                margemController.recalculaMargem(tipoEntidade, entCodigos, responsavel);
            }

            // Pos-processamento da importação do retorno usando método da classe específica do gestor.
            if (importadorRetorno != null) {
                LOG.debug("pos-processamento importacao retorno: " + DateHelper.getSystemDatetime());
                importadorRetorno.posImportacaoRetorno();
                LOG.debug("fim - pos-processamento importacao retorno " + DateHelper.getSystemDatetime());
            }

            // Grava o log de auditoria
            final LogDelegate log = new LogDelegate(responsavel, Log.FOLHA, Log.IMPORTACAO_RETORNO, Log.LOG_INFORMACAO);
            log.setOrgao(orgCodigo);
            log.setEstabelecimento(estCodigo);
            log.write();

            String msgOce = CodedValues.TOC_IMPORTACAO_RETORNO;

            switch (tipoImportacaoRetorno) {
                case CodedValues.TIPO_RETORNO_NORMAL:
                    msgOce = CodedValues.TOC_IMPORTACAO_RETORNO;
                    break;
                case CodedValues.TIPO_RETORNO_CRITICA:
                    msgOce = CodedValues.TOC_CRITICA_RETORNO;
                    break;
                case CodedValues.TIPO_RETORNO_ATRASADO:
                    msgOce = CodedValues.TOC_RETORNO_ATRASADO;
                    break;
                default:
                    break;
            }

            // log de ocorrência de consignante
            consignanteController.createOcorrenciaCse(msgOce, responsavel);

            // Se o código folha está habilitado, limpa o código folha das tabelas
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITAR_EDICAO_CODIGO_FOLHA, CodedValues.TPC_SIM, responsavel)){
            	try {
                    consignanteController.limparCodigoFolha(responsavel);
                } catch (final Exception ex) {
                	// Não faz rollback e nem dá exceção, pois não é um processo essencial do retorno
                    LOG.error(ex.getMessage(), ex);
                }
            }
        } catch (final LogControllerException ex) {
            // Não faz rollback e nem dá exceção
            LOG.error(ex.getMessage(), ex);
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (ex instanceof ImpRetornoControllerException) {
                throw (ImpRetornoControllerException) ex;
            }
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * importa registros de inconsistências analisadas exteriormente após processamento de retorno. Estas
     * serão armazenadas da tabela de inconsistências a ignorar na próxima análise.
     * @param adeNumero - contrato no qual se identificou a inconsistência
     * @param iiaObs - observação a ser inserida na ocorrência de parcela
     * @param iiaItem - código da regra de inconsistência
     * @param iiaDate - data do registro de inconsistência
     * @param responsavel
     * @throws ImpRetornoControllerException
     */
    @Override
    public void importarRegraInconsistencia(String adeCodigo, String iiaObs, Short iiaItem, java.util.Date iiaData, Boolean iiaPermanente, AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            IgnoraInconsistenciaAdeHome.create(adeCodigo, iiaItem, iiaObs, iiaData, responsavel.getUsuLogin(), iiaPermanente);

            final List<String> spdList = new ArrayList<>();
            spdList.add(CodedValues.SPD_REJEITADAFOLHA);

            final ListaOcorrenciaParcelaQuery lstOcp = new ListaOcorrenciaParcelaQuery();
            lstOcp.adeCodigo = adeCodigo;
            lstOcp.orderASC = false;
            lstOcp.spdCodigos = spdList;

            final List<TransferObject> parcelas = lstOcp.executarDTO();

            if ((parcelas != null) && !parcelas.isEmpty()) {
                final TransferObject parcelaRejeitada = parcelas.get(0);

                // insere na última parcela rejeitada a observação da inconsistência importada
                final OcorrenciaParcela ocpRejeitada = OcorrenciaParcelaHome.findByPrimaryKey((String) parcelaRejeitada.getAttribute(Columns.OCP_CODIGO));
                ocpRejeitada.setOcpObs(ocpRejeitada.getOcpObs() + " - " + iiaObs);
                AbstractEntityHome.update(ocpRejeitada);
            }
        } catch (CreateException | UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ImpRetornoControllerException("mensagem.erro.inconsistencias.criar.regra", responsavel);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel);
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException("mensagem.erro.inconsistencias.criar.regra", responsavel);
        }
    }

    /**
     * Através da listagem da tabela periodo exportação, retorna a data base do período PEX_PERIODO,
     * distinta, não sendo permitido que haja duas diferentes, o que irá ocasionar um erro.
     * @param tipoImportacaoRetorno : Tipo de Importação de Retorno
     * @param periodoRetAtrasado : Período Atrasado de Retorno
     * @param orgCodigos : Códigos dos órgãos, caso a operação esteja sendo realizada por usuário de órgão
     * @param estCodigos : Códigos dos estabelecimentos, caso a operação esteja sendo realizada por usuário de órgão com perfil de Estabelecimento
     * @param responsavel : Responsável pela operação
     * @return
     * @throws ImpRetornoControllerException
     */
    @Override
    public String recuperaPeriodoRetorno(int tipoImportacaoRetorno, Date periodoRetAtrasado, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ImpRetornoControllerException {
        return recuperaPeriodosRetorno(tipoImportacaoRetorno, periodoRetAtrasado, orgCodigos, estCodigos, responsavel).get(0);
    }

    @Override
    public List<String> recuperaPeriodosRetorno(int tipoImportacaoRetorno, Date periodoRetAtrasado, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            final List<String> periodos = new ArrayList<>();

            // Define o último período pela tabela de histórico de exportação e imprime o período
            List<TransferObject> periodoExportacao = null;
            if (tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_ATRASADO) {
                periodoExportacao = periodoController.obtemPeriodoImpRetornoAtrasado(orgCodigos, estCodigos, true, periodoRetAtrasado, responsavel);
            } else {
                periodoExportacao = periodoController.obtemPeriodoImpRetorno(orgCodigos, estCodigos, true, responsavel);
            }

            LOG.debug("Período de Exportação:");
            ExportaMovimentoHelper.imprimePeriodoExportacao(periodoExportacao);

            final List<TransferObject> periodoDistinto = TextHelper.groupConcat(periodoExportacao, new String[]{Columns.PEX_PERIODO}, new String[]{Columns.PEX_ORG_CODIGO}, ",", false, true);
            if ((periodoDistinto != null) && !periodoDistinto.isEmpty()) {
                if ((periodoDistinto.size() > 1) && !ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_RETORNO, CodedValues.TPC_SIM, responsavel)) {
                    LOG.error("Períodos encontrados por órgãos: ");
                    for (final TransferObject registroPeriodo : periodoDistinto) {
                        LOG.error(registroPeriodo.getAttribute(Columns.PEX_PERIODO) + " : " + registroPeriodo.getAttribute(Columns.PEX_ORG_CODIGO));
                    }

                    throw new ImpRetornoControllerException("mensagem.erro.retorno.periodo.multiplo", responsavel);
                } else {
                    for (final TransferObject registroPeriodo : periodoDistinto) {
                        periodos.add(DateHelper.format((java.util.Date) registroPeriodo.getAttribute(Columns.PEX_PERIODO), YYYY_MM_DD));
                    }
                }
            } else {
                throw new ImpRetornoControllerException("mensagem.erro.retorno.periodo.ausente", responsavel);
            }

            return periodos;
        } catch (final PeriodoException ex) {
            throw new ImpRetornoControllerException(ex);
        }
    }

    /**
     * Executa validações referente à importação de retorno.
     * @param tipoImportacaoRetorno : Tipo de importação, se é retorno crítica ou retorno atrasado
     * @param orgCodigos  : No caso de retorno por órgão, informa a lista de órgãos
     * @param estCodigos  : No caso de retorno por estabelecimento, informa a lista de estabelecimentos
     * @param responsavel : Usuário responsável pela operação
     * @param periodoRetAtrasado: utilizada para calcular o periodo do retorno
     * @throws ConsignanteControllerException
     * @throws ImpRetornoControllerException
     * @throws ParseException
     */
    private void validarImportacaoRetorno(int tipoImportacaoRetorno, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ConsignanteControllerException, ImpRetornoControllerException, ParseException {
        try {
            // Valida se existem parcelas pagas em retorno atrasado no dia de hoje, com
            // data base anterior à data inicial do contrato, que possam comprometer o ajuste
            // do prazo dos contratos.
            if (tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_ATRASADO) {
                // Não passa orgCodigos/estCodigos para a query pois a atualização também não leva em conta esta informação
                final int totalPagasAtrasadas = new ObtemTotalParcelasPagasAtrasadasQuery(orgCodigos, estCodigos).executarContador();
                if (totalPagasAtrasadas > 0) {
                    LOG.error("Valida parcelas pagas em retorno atrasado: ERRO");
                    throw new ConsignanteControllerException("mensagem.erro.retorno.atrasado.ja.executado.hoje", responsavel);
                } else {
                    LOG.info("Valida parcelas pagas em retorno atrasado: OK");
                }
            }

            // Valida se não existe histórico de conclusão de retorno não desfeito. Caso positivo, retorna erro.
            if (tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_NORMAL){
                    final ObtemRetornoNaoDesfeitoQuery retornoNaoDesfeito = new ObtemRetornoNaoDesfeitoQuery(orgCodigos, estCodigos);
                    final List<TransferObject> retorno = retornoNaoDesfeito.executarDTO();

                    if ((retorno != null) && !retorno.isEmpty()) {
                        LOG.error("Valida histórico de conclusão de retorno não desfeito: ERRO");
                        final TransferObject periodo = retorno.get(0);
                        final Date dataRetornada = (Date) periodo.getAttribute(Columns.PEX_PERIODO);
                        final String periodoConcluido = DateHelper.format(dataRetornada, "MM/yyyy");
                        throw new ConsignanteControllerException("mensagem.erro.retorno.nao.desfeito", responsavel, periodoConcluido);
                    } else {
                        LOG.info("Não existe histórico de conclusão de retorno não desfeito: OK");
                    }
                }

        } catch (final HQueryException ex) {
            throw new ConsignanteControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Recupera os arquivos de configuração da importação do retorno, crítica ou atrasado,
     * de acordo com as configurações do sistema e das entidades.
     * @param nomeArquivo
     * @param tipo
     * @param estCodigo
     * @param orgCodigo
     * @param responsavel
     * @return
     * @throws ImpRetornoControllerException
     */
    @Override
    public Map<String, String> buscaArquivosConfiguracao(String tipo, String estCodigo, String orgCodigo, AcessoSistema responsavel) throws ImpRetornoControllerException {
        // Pega parâmetros de configuração do sistema
        final ParamSist ps = ParamSist.getInstance();

        // Diretório Raiz eConsig e dos arquivos de configuração
        final String absolutePath = ParamSist.getDiretorioRaizArquivos();
        final String pathConf = absolutePath + File.separatorChar + "conf";

        // Configura o caminho onde os arquivos de saida do processamento (relatórios de integração)
        // serão armazenados, ao final do processamento
        final String pathSaida = absolutePath
                + File.separatorChar + "relatorio"
                + File.separatorChar + "cse"
                + File.separatorChar + "integracao";

        // Configura o caminho do arquivo de importação baseado no tipo do processamento
        // e se a importação é a nível de órgão
        String pathEntrada = absolutePath
                + File.separatorChar + ((!ProcessaRetorno.ATRASADO.equalsIgnoreCase(tipo) && !ProcessaRetorno.ATRASADO_SOMA_PARCELA.equalsIgnoreCase(tipo) && !ProcessaRetorno.CRITICA_ATRASADO.equalsIgnoreCase(tipo)) ? tipo : "retornoatrasado");

        // Arquivos de configuração para importar retorno
        final String nomeEntradaImpRetorno  = (String) ps.getParam(!ProcessaRetorno.CRITICA.equalsIgnoreCase(tipo) && !ProcessaRetorno.CRITICA_ATRASADO.equalsIgnoreCase(tipo) ? CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_RETORNO  : CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_CRITICA,  responsavel);
        final String nomeTradutorImpRetorno = (String) ps.getParam(!ProcessaRetorno.CRITICA.equalsIgnoreCase(tipo) && !ProcessaRetorno.CRITICA_ATRASADO.equalsIgnoreCase(tipo) ? CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_RETORNO : CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_CRITICA, responsavel);

        // Configura o caminho dos arquivos de configuração da tradução do retorno
        String entradaImpRetorno = null;
        String tradutorImpRetorno = null;

        // Verifica se a importação é a nível de órgão/estabelecimento, se for procura os arquivos de configuração do
        // diretório especifico. Caso não seja, busca do diretório raiz de configuração.
        if (!TextHelper.isNull(orgCodigo)) {
            // Arquivos de configuração por Órgão
            entradaImpRetorno = pathConf + File.separatorChar + "cse" + File.separatorChar + orgCodigo + File.separatorChar + nomeEntradaImpRetorno;
            tradutorImpRetorno = pathConf + File.separatorChar + "cse" + File.separatorChar + orgCodigo + File.separatorChar + nomeTradutorImpRetorno;
            if (responsavel.isCseSup()) {
                pathEntrada += File.separatorChar + "cse";
            } else {
                pathEntrada += File.separatorChar + "cse" + File.separatorChar + orgCodigo;
            }
        } else if (!TextHelper.isNull(estCodigo)) {
            // Arquivos de configuração por Estabelecimento
            entradaImpRetorno = pathConf + File.separatorChar + "est" + File.separatorChar + estCodigo + File.separatorChar + nomeEntradaImpRetorno;
            tradutorImpRetorno = pathConf + File.separatorChar + "est" + File.separatorChar + estCodigo + File.separatorChar + nomeTradutorImpRetorno;
            if (responsavel.isCseSup()) {
                pathEntrada += File.separatorChar + "est";
            } else {
                pathEntrada += File.separatorChar + "est" + File.separatorChar + estCodigo;
            }
        } else {
            pathEntrada += File.separatorChar + "cse";
        }

        // Se não é importação por órgão/estabelecimento, ou é mas o arquivo não existe,
        // obtém os arquivos de configuração no path raiz.
        if (TextHelper.isNull(entradaImpRetorno) || !(new File(entradaImpRetorno).exists())) {
            entradaImpRetorno = pathConf + File.separatorChar + nomeEntradaImpRetorno;
        }
        if (TextHelper.isNull(tradutorImpRetorno) || !(new File(tradutorImpRetorno).exists())) {
            tradutorImpRetorno = pathConf + File.separatorChar + nomeTradutorImpRetorno;
        }

        // Verifica se os arquivos de configuração, seja por órgão/estabelecimento ou do padrão, existem e podem ser lidos
        final File arqConfEntrada  = new File(entradaImpRetorno);
        final File arqConfTradutor = new File(tradutorImpRetorno);
        if (!arqConfEntrada.exists() || !arqConfEntrada.canRead() ||
                !arqConfTradutor.exists() || !arqConfTradutor.canRead()) {
            throw new ImpRetornoControllerException("mensagem.erro.retorno.arquivos.conf.ausentes", responsavel);
        }

        final Map<String, String> retorno = new HashMap<>();
        retorno.put(ENTRADA_IMP_RETORNO, entradaImpRetorno);
        retorno.put(TRADUTOR_IMP_RETORNO, tradutorImpRetorno);
        retorno.put(DIRETORIO_ENTRADA, pathEntrada);
        retorno.put(DIRETORIO_SAIDA, pathSaida);
        return retorno;
    }

    @Override
    public Map<String, String> buscaArquivosConfiguracao(String nomeArquivo, String tipo, String estCodigo, String orgCodigo, AcessoSistema responsavel) throws ImpRetornoControllerException {
        final Map<String, String> retorno = buscaArquivosConfiguracao(tipo, estCodigo, orgCodigo, responsavel);
        final String pathEntrada = retorno.get(DIRETORIO_ENTRADA);

        // Verifica se o arquivo de entrada existe e pode ser lido
        String fileName = null;
        if (nomeArquivo != null) {
            if (Files.exists(Paths.get(nomeArquivo))) {
                // Se o arquivo foi passado com caminho completo, então não precisa usar concatenar o pathEntrada
                fileName = nomeArquivo;
            } else {
                fileName = pathEntrada + File.separatorChar + nomeArquivo;
            }
            LOG.info(ARQUIVO_DE + tipo + ": \"" + fileName +"\"");
            final File arqEntrada = new File(fileName);
            if (!arqEntrada.exists() || !arqEntrada.canRead()) {
                if (!arqEntrada.exists()) {
                    LOG.error(ARQUIVO_DE + tipo + " \"" + fileName +"\" não existe!");
                } else {
                    LOG.error(ARQUIVO_DE + tipo + " \"" + fileName +"\" não pode ser lido!");
                }
                throw new ImpRetornoControllerException("mensagem.erro.retorno.arquivo.entrada.invalido", responsavel, nomeArquivo);
            }

            if (fileName.endsWith(".crypt")) {
                final File arquivoPlano = CriptografiaArquivos.descriptografarArquivo(fileName, true, responsavel);
                if (arquivoPlano != null) {
                    fileName = arquivoPlano.getAbsolutePath();
                }
            }

            // Chama rotina para gerenciamento do diretório de importação de retorno
            limparDiretorioImportacao(pathEntrada, responsavel);
        }

        retorno.put(NOME_ARQUIVO_ENTRADA, fileName);
        return retorno;
    }

    /**
     * Verifica se no diretório de importação existe algum arquivo já
     * processado mas não finalizado (.prc)
     * Se o parâmetro de sistema disser que estes arquivos devem ser
     * removidos, o sistema irá renomeá-los para (.prc.nok)
     * Os arquivos (.prc.ok) são os processados que foram traduzidos
     * para as consignatárias pela rotina de conclusão de retorno.
     * @param diretorio   : diretório de importação
     * @param responsavel : usuário responsavel
     */
    private void limparDiretorioImportacao(String diretorio, AcessoSistema responsavel) {
        final Object param = ParamSist.getInstance().getParam(CodedValues.TPC_LIMPA_DIRETORIO_IMPORTACAO_RETORNO, responsavel);
        final boolean limpaDiretorio = ((param == null) || CodedValues.TPC_SIM.equals(param.toString()));

        if (limpaDiretorio) {
            final FilenameFilter filtro = (file, fileName) -> (fileName.endsWith(".prc"));

            final File dir = new File(diretorio);
            if (dir.exists()) {
                final File[] arquivos = dir.listFiles(filtro);
                if ((arquivos != null) && (arquivos.length > 0)) {
                    String nome = null;
                    for (final File arquivo : arquivos) {
                        nome = arquivo.getAbsolutePath();
                        FileHelper.rename(nome, nome + ".nok");
                    }
                }
            }
        }
    }

    /**
     * Gera relatório de repasse.
     * @param responsavel
     */
    private void geraRelatorioRepasse(AcessoSistema responsavel) {
        // Verifica parâmetro de sistema antes de gerar relatório de repasse
        final String paramGeraRelatorioRepasse = (String) ParamSist.getInstance().getParam(CodedValues.TPC_GERA_RELATORIO_REPASSE, responsavel);
        if (CodedValues.TPC_SIM.equals(paramGeraRelatorioRepasse)) {
            try {
                LOG.debug("RELATORIO DE REPASSE: " + DateHelper.getSystemDatetime());
                relatorioController.gerarRelatorioRepasse(responsavel);
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
            } finally {
                LOG.debug("FIM RELATORIO DE REPASSE: " + DateHelper.getSystemDatetime());
            }
        }
    }

    /**
     * Realiza a importação do arquivo de retorno
     * @param nomeArquivo
     * @param entradaImpRetorno
     * @param tradutorImpRetorno
     * @param tipoImportacaoRetorno
     * @param exportaMensal
     * @param consolida
     * @param hoje
     * @param adeCodigosAlteracao
     * @param adeCodigosLiquidacao
     * @param adeTipoEnvio
     * @param descarte
     * @param linhasSemProcessamento
     * @param orgCodigos
     * @param estCodigos
     * @param periodoRetorno
     * @param periodoRetAtrasado
     * @param importadorRetorno
     * @param adeDAO
     * @param retDAO
     * @param prdDAO
     * @param responsavel
     * @throws ImpRetornoControllerException
     */
    @SuppressWarnings("java:S107")
    private void importarArquivoRetorno(String nomeArquivo, String entradaImpRetorno, String tradutorImpRetorno,
            int tipoImportacaoRetorno, boolean exportaMensal, boolean consolida, Date hoje,
            List<String> adeCodigosAlteracao, List<String> adeCodigosLiquidacao, HashMap<String, String> adeTipoEnvio,
            StringBuilder descarte, Map<String, Map<String, Object>> linhasSemProcessamento,
            List<String> orgCodigos, List<String> estCodigos, String periodoRetorno, Date periodoRetAtrasado, ImportaRetorno importadorRetorno,
            AutorizacaoDAO adeDAO, ImpRetornoDAO retDAO, ParcelaDescontoDAO prdDAO, ImpRetornoControllerConf conf, AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            // Hash que recebe os dados do que serão lidos do arquivo de entrada
            final Map<String, Object> entrada = new HashMap<>();
            // Configura o leitor de acordo com o arquivo de entrada
            LeitorArquivoTexto leitor = null;
            if (nomeArquivo.toLowerCase().endsWith(".zip")) {
                leitor = new LeitorArquivoTextoZip(entradaImpRetorno, nomeArquivo);
            } else {
                leitor = new LeitorArquivoTexto(entradaImpRetorno, nomeArquivo);
            }
            // Prepara tradução do arquivo de retorno.
            final Escritor escritor = new EscritorMemoria(entrada);
            final Tradutor tradutor = new Tradutor(tradutorImpRetorno, leitor, escritor);
            // Busca as configurações XML que informam quais são os campos chave e em que
            // ordem eles devem ser excluídos na busca por parcelas.
            String[] ordemExcCamposChave = {};
            String[] camposChave = {};
            for (final ParametroTipo param : leitor.getConfig().getParametro()) {
                if ("chave_identificacao".equalsIgnoreCase(param.getNome())) {
                    camposChave = TextHelper.split(param.getValor(), ";");
                } else if ("ordem_exc_campos_chave".equalsIgnoreCase(param.getNome())) {
                    ordemExcCamposChave = TextHelper.split(param.getValor(), ";");
                    if (ordemExcCamposChave == null) {
                        ordemExcCamposChave = new String[]{};
                    }
                }
            }

            // CRIA TABELA COM O ARQUIVO DE RETORNO
            LOG.debug("CRIA TABELA RETORNO: " + DateHelper.getSystemDatetime());
            final List<String> camposChaveIdent = new ArrayList<>();
            final int numLinhasSemMapeamento = carregaArquivoRetorno(linhasSemProcessamento, entrada, leitor, tradutor, camposChave,
                    camposChaveIdent, tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_CRITICA,
                    tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_NORMAL, hoje,
                    orgCodigos, estCodigos, periodoRetorno, periodoRetAtrasado, true, retDAO, responsavel);
            LOG.debug("FIM CRIA TABELA RETORNO: " + DateHelper.getSystemDatetime());
            LOG.debug("Linhas sem mapeamento: " + numLinhasSemMapeamento);

            conf.adeAlteradasAnterior = 0;
            conf.adeLiquidadasAnterior = 0;
            if (linhasSemProcessamento.size() > 0) {
                // Pré-processamento da fase 1 de importação usando método da classe específica do gestor.
                if (importadorRetorno != null) {
                    LOG.debug("pre-processamento fase 1: " + DateHelper.getSystemDatetime());
                    importadorRetorno.preFase1ImportacaoRetorno();
                    LOG.debug("fim - pre-processamento fase 1 " + DateHelper.getSystemDatetime());
                }

                // 1. PAGA PARCELAS CUJO VALOR É O QUE ESTÁ NO ARQUIVO DE RETORNO (DESCONTO TOTAL)
                LOG.debug("FASE 1: " + DateHelper.getSystemDatetime());
                pagaParcelasDescontoTotal(linhasSemProcessamento, adeCodigosAlteracao, adeCodigosLiquidacao, adeTipoEnvio,
                                          camposChaveIdent, tipoImportacaoRetorno, exportaMensal, true, retDAO, responsavel);
                LOG.debug(AD_ES_ALTERADAS + (adeCodigosAlteracao.size() - conf.adeAlteradasAnterior));
                LOG.debug(AD_ES_LIQUIDADAS + (adeCodigosLiquidacao.size() - conf.adeLiquidadasAnterior));
                LOG.debug(LINHAS_NAO_PROCESSADAS + linhasSemProcessamento.size());
                conf.adeAlteradasAnterior = adeCodigosAlteracao.size();
                conf.adeLiquidadasAnterior = adeCodigosLiquidacao.size();
                LOG.debug("FIM FASE 1: " + DateHelper.getSystemDatetime());

                // Pós-processamento da fase 1 de importação usando método da classe específica do gestor.
                if (importadorRetorno != null) {
                    LOG.debug("pos-processamento fase 1: " + DateHelper.getSystemDatetime());
                    importadorRetorno.posFase1ImportacaoRetorno();
                    LOG.debug("fim - pos-processamento fase 1 " + DateHelper.getSystemDatetime());
                }
            }

            if (linhasSemProcessamento.size() > 0) {
                // Pre-processamento da fase 2 de importação usando método da classe específica do gestor.
                if (importadorRetorno != null) {
                    LOG.debug("pre-processamento fase 2: " + DateHelper.getSystemDatetime());
                    importadorRetorno.preFase2ImportacaoRetorno();
                    LOG.debug("fim - pre-processamento fase 2 " + DateHelper.getSystemDatetime());
                }

                // 2. PAGA PARCELAS CUJO VALOR CONSOLIDADO É O DESCONTO TOTAL DO RETORNO.
                LOG.debug("FASE 2: " + DateHelper.getSystemDatetime());
                final boolean agrupaPeriodo = ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_RETORNO, CodedValues.TPC_SIM, responsavel);
                if (consolida || agrupaPeriodo) {
                    // Seleciona das linhas sem processamento, somente aquelas com possibilidade de pagamento
                    // exato consolidado.
                    pagaParcelasDescontoTotalConsolidado(linhasSemProcessamento,
                                                         adeCodigosAlteracao, adeCodigosLiquidacao, adeTipoEnvio, camposChaveIdent,
                                                         tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_CRITICA,
                                                         tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_ATRASADO,
                                                         exportaMensal, false, false, adeDAO, retDAO, prdDAO, responsavel);

                    if (agrupaPeriodo) {
                        // Se permite agrupar períodos de exportação, tenta realizar o pagamento consolidado agrupando
                        // pelo período da parcela, caso o arquivo contenha o campo ANO_MES_DESCONTO
                        final List<String> camposChaveIdentAgrupado = new ArrayList<>(camposChaveIdent);
                        camposChaveIdentAgrupado.add(ANO_MES_DESCONTO);

                        pagaParcelasDescontoTotalConsolidado(linhasSemProcessamento,
                                                             adeCodigosAlteracao, adeCodigosLiquidacao, adeTipoEnvio, camposChaveIdentAgrupado,
                                                             tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_CRITICA,
                                                             tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_ATRASADO,
                                                             exportaMensal, true, false, adeDAO, retDAO, prdDAO, responsavel);

                        // Se permite agrupar períodos de exportação, tenta realizar o pagamento consolidado agrupando
                        // pelo código da consignação, pois a folha pode enviar a soma de parcelas de vários períodos
                        // da mesma consignação em uma linha apenas
                        final List<String> camposChaveIdentAgrupadoAde = new ArrayList<>(camposChaveIdent);
                        camposChaveIdentAgrupadoAde.add(ADE_CODIGO);

                        pagaParcelasDescontoTotalConsolidado(linhasSemProcessamento,
                                                             adeCodigosAlteracao, adeCodigosLiquidacao, adeTipoEnvio, camposChaveIdentAgrupadoAde,
                                                             tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_CRITICA,
                                                             tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_ATRASADO,
                                                             exportaMensal, false, true, adeDAO, retDAO, prdDAO, responsavel);
                    }
                } else {
                    LOG.debug("Sistema não consolida.");
                }

                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_MULTIPLAS_LINHAS_RETORNO_MESMA_PRD, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                    pagaParcelasDescontoTotalConsolidadoInverso(linhasSemProcessamento,
                                                                adeCodigosAlteracao, adeCodigosLiquidacao, adeTipoEnvio,
                                                                tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_CRITICA,
                                                                tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_ATRASADO,
                                                                exportaMensal, adeDAO, retDAO, prdDAO, periodoRetAtrasado, responsavel);
                }

                LOG.debug(AD_ES_ALTERADAS + (adeCodigosAlteracao.size() - conf.adeAlteradasAnterior));
                LOG.debug(AD_ES_LIQUIDADAS + (adeCodigosLiquidacao.size() - conf.adeLiquidadasAnterior));
                LOG.debug(LINHAS_NAO_PROCESSADAS + linhasSemProcessamento.size());
                conf.adeAlteradasAnterior = adeCodigosAlteracao.size();
                conf.adeLiquidadasAnterior = adeCodigosLiquidacao.size();
                LOG.debug("FIM FASE 2: " + DateHelper.getSystemDatetime());

                // Pos-processamento da fase 2 de importação usando método da classe específica do gestor.
                if (importadorRetorno != null) {
                    LOG.debug("pos-processamento fase 2: " + DateHelper.getSystemDatetime());
                    importadorRetorno.posFase2ImportacaoRetorno();
                    LOG.debug("fim - pos-processamento fase 2 " + DateHelper.getSystemDatetime());
                }
            }

            if (linhasSemProcessamento.size() > 0) {
                // Pre-processamento da fase 3 de importação usando método da classe específica do gestor.
                if (importadorRetorno != null) {
                    LOG.debug("pre-processamento fase 3: " + DateHelper.getSystemDatetime());
                    importadorRetorno.preFase3ImportacaoRetorno();
                    LOG.debug("fim - pre-processamento fase 3 " + DateHelper.getSystemDatetime());
                }

                // 3. TENTA ENCONTRAR QUALQUER ADE/PARCELA QUE POSSA SER PAGA
                LOG.debug("FASE 3: " + DateHelper.getSystemDatetime());

                // 3.1. PAGA PARCELAS QUE SÓ EXISTE UMA LINHA NO ARQUIVO DE RETORNO PARA UMA PARCELA NO SISTEMA
                LOG.debug("FASE 3.1: " + DateHelper.getSystemDatetime());
                pagaParcelasDescontoTotal(linhasSemProcessamento, adeCodigosAlteracao, adeCodigosLiquidacao, adeTipoEnvio,
                                          camposChaveIdent, tipoImportacaoRetorno, exportaMensal, false, retDAO, responsavel);
                LOG.debug(AD_ES_ALTERADAS + (adeCodigosAlteracao.size() - conf.adeAlteradasAnterior));
                LOG.debug(AD_ES_LIQUIDADAS + (adeCodigosLiquidacao.size() - conf.adeLiquidadasAnterior));
                LOG.debug(LINHAS_NAO_PROCESSADAS + linhasSemProcessamento.size());
                conf.adeAlteradasAnterior = adeCodigosAlteracao.size();
                conf.adeLiquidadasAnterior = adeCodigosLiquidacao.size();
                LOG.debug("FIM FASE 3.1: " + DateHelper.getSystemDatetime());

                // 3.2 PAGA PARCELAS DE ACORDO COM OS FILTROS PRESENTES NO ARQUIVO, NAS ORDENS DE PRIORIDADES
                LOG.debug("FASE 3.2: " + DateHelper.getSystemDatetime());
                pagaParcelasParciais(linhasSemProcessamento, adeTipoEnvio, adeCodigosAlteracao, adeCodigosLiquidacao,
                                     camposChaveIdent, ordemExcCamposChave, tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_CRITICA,
                                     tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_ATRASADO, exportaMensal, consolida,
                                     adeDAO, retDAO, prdDAO, responsavel);
                LOG.debug(AD_ES_ALTERADAS + (adeCodigosAlteracao.size() - conf.adeAlteradasAnterior));
                LOG.debug(AD_ES_LIQUIDADAS + (adeCodigosLiquidacao.size() - conf.adeLiquidadasAnterior));
                LOG.debug(LINHAS_NAO_PROCESSADAS + linhasSemProcessamento.size());
                conf.adeAlteradasAnterior = adeCodigosAlteracao.size();
                conf.adeLiquidadasAnterior = adeCodigosLiquidacao.size();
                LOG.debug("FIM FASE 3.2: " + DateHelper.getSystemDatetime());

                LOG.debug("FIM FASE 3: " + DateHelper.getSystemDatetime());

                // Pos-processamento da fase 3 de importação usando método da classe específica do gestor.
                if (importadorRetorno != null) {
                    LOG.debug("pos-processamento fase 3: " + DateHelper.getSystemDatetime());
                    importadorRetorno.posFase3ImportacaoRetorno();
                    LOG.debug("fim - pos-processamento fase 3 " + DateHelper.getSystemDatetime());
                }
            }

            if (linhasSemProcessamento.size() > 0) {
                // Pre-processamento da fase 4 de importação usando método da classe específica do gestor.
                if (importadorRetorno != null) {
                    LOG.debug("pre-processamento fase 4: " + DateHelper.getSystemDatetime());
                    importadorRetorno.preFase4ImportacaoRetorno();
                    LOG.debug("fim - pre-processamento fase 4 " + DateHelper.getSystemDatetime());
                }

                // 4. FAZ O PROCESSAMENTO DE FÉRIAS
                LOG.debug("FASE 4: " + DateHelper.getSystemDatetime());
                if (ParamSist.getBoolParamSist(CodedValues.TPC_TEM_PROCESSAMENTO_FERIAS, AcessoSistema.getAcessoUsuarioSistema())) {
                    if (tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_NORMAL) {
                        executarProcessamentoFerias(linhasSemProcessamento, adeTipoEnvio, adeCodigosAlteracao, adeCodigosLiquidacao,
                                                    camposChaveIdent, ordemExcCamposChave, tipoImportacaoRetorno, exportaMensal, consolida,
                                                    retDAO, prdDAO, conf, responsavel);
                    }
                } else {
                    LOG.debug("Sistema não faz processamento de descontos de férias.");
                }
                LOG.debug("FIM FASE 4: " + DateHelper.getSystemDatetime());

                // Pos-processamento da fase 4 de importação usando método da classe específica do gestor.
                if (importadorRetorno != null) {
                    LOG.debug("pos-processamento fase 4: " + DateHelper.getSystemDatetime());
                    importadorRetorno.posFase4ImportacaoRetorno();
                    LOG.debug("fim - pos-processamento fase 4 " + DateHelper.getSystemDatetime());
                }
            }

            if (linhasSemProcessamento.size() > 0) {
                // Pre-processamento da fase 5 de importação usando método da classe específica do gestor.
                if (importadorRetorno != null) {
                    LOG.debug("pre-processamento fase 5: " + DateHelper.getSystemDatetime());
                    importadorRetorno.preFase5ImportacaoRetorno();
                    LOG.debug("fim - pre-processamento fase 5 " + DateHelper.getSystemDatetime());
                }

                // 5. SALVA LINHAS NÃO PROCESSADAS
                LOG.debug("FASE 5: " + DateHelper.getSystemDatetime());
                for (final Entry<String, Map<String, Object>> entry : linhasSemProcessamento.entrySet()) {
                    descarte.append(entry.getValue().get("LINHASP")).append(System.lineSeparator());
                }
                LOG.debug(AD_ES_ALTERADAS + (adeCodigosAlteracao.size() - conf.adeAlteradasAnterior));
                LOG.debug(AD_ES_LIQUIDADAS + (adeCodigosLiquidacao.size() - conf.adeLiquidadasAnterior));
                LOG.debug("Linhas não processadas = " + linhasSemProcessamento.size());
                conf.adeAlteradasAnterior = adeCodigosAlteracao.size();
                conf.adeLiquidadasAnterior = adeCodigosLiquidacao.size();
                LOG.debug("FIM FASE 5: " + DateHelper.getSystemDatetime());

                // Pos-processamento da fase 5 de importação usando método da classe específica do gestor.
                if (importadorRetorno != null) {
                    LOG.debug("pos-processamento fase 5: " + DateHelper.getSystemDatetime());
                    importadorRetorno.posFase5ImportacaoRetorno();
                    LOG.debug("fim - pos-processamento fase 5 " + DateHelper.getSystemDatetime());
                }
            }
            LOG.debug("ADEs alteradas (total) = " + adeCodigosAlteracao.size());
            LOG.debug("ADEs liquidadas (total) = " + adeCodigosLiquidacao.size());
            LOG.debug("Linhas não processadas (total) = " + linhasSemProcessamento.size());
            conf.adeAlteradasAnterior = adeCodigosAlteracao.size();
            conf.adeLiquidadasAnterior = adeCodigosLiquidacao.size();
        } catch (final ImportaRetornoException ex) {
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Carrega o arquivo de retorno passada para o banco de dados.
     * @param nomeArquivo
     * @param orgCodigo
     * @param estCodigo
     * @param responsavel
     * @return Quantidade de linhas sem mapeamento
     * @throws ImpRetornoControllerException
     */
    @Override
    public int carregaArquivoRetorno(String nomeArquivo, AcessoSistema responsavel) throws ImpRetornoControllerException {
        final Map<String, String> nomeArquivosConfiguracao = buscaArquivosConfiguracao(nomeArquivo, ProcessaRetorno.RETORNO, null, null, responsavel);
        final String fileName = nomeArquivosConfiguracao.get(NOME_ARQUIVO_ENTRADA);
        final String entradaImpRetorno = nomeArquivosConfiguracao.get(ENTRADA_IMP_RETORNO);
        final String tradutorImpRetorno = nomeArquivosConfiguracao.get(TRADUTOR_IMP_RETORNO);
        // Hash que recebe os dados do que serão lidos do arquivo de entrada
        final Map<String, Object> entrada = new HashMap<>();
        // Configura o leitor de acordo com o arquivo de entrada
        LeitorArquivoTexto leitor = null;
        if (fileName.toLowerCase().endsWith(".zip")) {
            leitor = new LeitorArquivoTextoZip(entradaImpRetorno, fileName);
        } else {
            leitor = new LeitorArquivoTexto(entradaImpRetorno, fileName);
        }
        // Prepara tradução do arquivo de retorno.
        final Escritor escritor = new EscritorMemoria(entrada);
        final Tradutor tradutor = new Tradutor(tradutorImpRetorno, leitor, escritor);

        final DAOFactory daoFactory = DAOFactory.getDAOFactory();
        final ImpRetornoDAO retDAO = daoFactory.getImpRetornoDAO();

        return carregaArquivoRetorno(null, entrada, leitor, tradutor, null, null, false, false, null, null, null, null, null, false, retDAO, responsavel);
    }

    @Override
    public void iniciarCargaArquivoRetorno(String nomeArquivoEntrada, boolean mantemArqRetorno, List<String> orgCodigos, List<String> estCodigos, ImpRetornoDAO retDAO, AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            if (retDAO == null) {
                retDAO = DAOFactory.getDAOFactory().getImpRetornoDAO();
            }

            // Busca parâmetro de sistema que diz se o arquivo de retorno deve ser mantido
            List<String> orgIdentRemocao = null;
            if (mantemArqRetorno) {
                /*
                 * Busca os órgãos que ainda não começaram o processamento de retorno.
                 * Estes códigos de órgãos serão usados para limpar a tabela de
                 * arquivo de retorno.
                 */
                final ListaOrgaoSemProcessamentoQuery orgaoSemProcessamentoQuery = new ListaOrgaoSemProcessamentoQuery();
                orgaoSemProcessamentoQuery.orgCodigos = orgCodigos;
                orgaoSemProcessamentoQuery.estCodigos = estCodigos;
                final List<String> orgCodigosRemocao = orgaoSemProcessamentoQuery.executarLista();
                if ((orgCodigosRemocao != null) && !orgCodigosRemocao.isEmpty()) {
                    final ListaOrgaoIdentificadorQuery query = new ListaOrgaoIdentificadorQuery();
                    query.orgCodigos = orgCodigosRemocao;
                    orgIdentRemocao = query.executarLista();
                }
            }
            // Inicia o carregamento do arquivo de retorno para a tabela.
            // Remove desta tabela os registros dos órgãos selecionados anteriormente.

            // Recupera o nome do arquivo com o diretório do código do órgão para evitar duplicidade pelo nome do arquivo na tabela de armazenamento do arquivo
            final String[] partesNomeArquivo = nomeArquivoEntrada.split(File.separator);
            String nomeArquivo = null;
            if (partesNomeArquivo.length == 1) {
                nomeArquivo = partesNomeArquivo[0];
            } else if (partesNomeArquivo.length > 1) {
                nomeArquivo = (!"cse".equals(partesNomeArquivo[partesNomeArquivo.length - 2]) ? partesNomeArquivo[partesNomeArquivo.length - 2] + "-" : "")
                               + partesNomeArquivo[partesNomeArquivo.length - 1];
            }

            retDAO.iniciaCargaArquivoRetorno(nomeArquivo, mantemArqRetorno, orgIdentRemocao);

            // Verifica se existe algum registro antigo na tabela de arquivo retorno
            if (mantemArqRetorno && (retDAO.countArquivoTabelaRetorno(nomeArquivo) > 0)) {
                throw new ImpRetornoControllerException("mensagem.erro.retorno.registros.invalidos.tabela", responsavel);
            }
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Traduz cada linha do arquivo de retorno, criando assim uma tabela no banco de dados
     * e um Map na memória com as linhas traduzidas.
     */
    @SuppressWarnings("java:S107")
    private int carregaArquivoRetorno(Map<String, Map<String, Object>> linhasTraduzidas, Map<String, Object> entrada,
            LeitorArquivoTexto leitor, Tradutor tradutor, String[] camposChave,
            List<String> camposChaveIdent, boolean critica, boolean retorno,
            Date hoje, List<String> orgCodigos, List<String> estCodigos, String periodoRetorno, Date periodoRetAtrasado,
            boolean traduzLinhas, ImpRetornoDAO retDAO, AcessoSistema responsavel)
                    throws ImpRetornoControllerException {
        if (camposChaveIdent == null) {
            camposChaveIdent = new ArrayList<>();
        }
        // Campos que, se estiverem no arquivo, devem ser usados obrigatoriamente
        // como chaves de identificação.
        camposChaveIdent.add("EST_IDENTIFICADOR");
        camposChaveIdent.add("ORG_IDENTIFICADOR");
        camposChaveIdent.add(CSA_IDENTIFICADOR);
        camposChaveIdent.add("SVC_IDENTIFICADOR");
        camposChaveIdent.add(CNV_COD_VERBA);
        camposChaveIdent.add("RSE_MATRICULA");
        camposChaveIdent.add(PRD_VLR_PREVISTO);
        camposChaveIdent.add("SER_CPF");
        // Adiciona os campos chave específicos do arquivo de configuração à lista.
        if (camposChave != null) {
            for (final String element : camposChave) {
                if (!camposChaveIdent.contains(element)) {
                    camposChaveIdent.add(element);
                }
            }
        }
        if (critica || retorno) {
            camposChaveIdent.add(SPD_CODIGOS);
        } else {
            // Se é importação de retorno atrasado, então procura pela parcela que esteja com
            // a data de desconto igual a data passada
            camposChaveIdent.add(ANO_MES_DESCONTO);
        }

        try {
            final boolean mantemArqRetorno = ParamSist.paramEquals(CodedValues.TPC_MANTEM_ARMAZENADO_ARQ_RETORNO, CodedValues.TPC_SIM, responsavel);
            iniciarCargaArquivoRetorno(leitor.getNomeArquivo(), mantemArqRetorno, orgCodigos, estCodigos, retDAO, responsavel);

            // Cache de convênios existentes. Utilizado para não precisar ir ao banco em busca de convênio a cada linha.
            final Map<String, String> mapVerbaRef = convenioController.getMapCnvCodVerbaRef();
            final Map<String, String> mapVerbaFerias = convenioController.getMapCnvCodVerbaFerias();

            final HashMap<String, List<TransferObject>> conveniosMap = new HashMap<>();
            boolean linhaMapeada = false;
            int numLinhasSemMapeamento = 0;
            tradutor.iniciaTraducao();
            while (tradutor.traduzProximo()) {
                if (TextHelper.isNull(entrada.get(PERIODO))) {
                    if (periodoRetAtrasado != null) {
                        entrada.put(PERIODO, DateHelper.format(periodoRetAtrasado, YYYY_MM_DD));
                    } else {
                        // Assume o período configurado para o retorno
                        entrada.put(PERIODO, periodoRetorno);
                    }
                }
                linhaMapeada = retDAO.insereLinhaTabelaRetorno(entrada, leitor.getLinha(), leitor.getNumeroLinha(), conveniosMap, mapVerbaRef, mapVerbaFerias, responsavel);
                if (linhaMapeada) {
                    if (traduzLinhas) {
                        traduzLinhaArquivo(linhasTraduzidas, camposChaveIdent, entrada, leitor.getLinha(), leitor.getNumeroLinha(), critica, retorno, hoje, responsavel);
                    }
                } else {
                    numLinhasSemMapeamento++;
                }
            }
            tradutor.encerraTraducao();
            retDAO.encerraCargaArquivoRetorno(mantemArqRetorno);

            // Se tem processamento de férias, verifica se as linhas marcadas como férias
            // na tabela de retorno estão corretamente identificadas no hash de linhas traduzidas
            if (ParamSist.paramEquals(CodedValues.TPC_TEM_PROCESSAMENTO_FERIAS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                final List<Integer> linhasFerias = retDAO.getLinhasProcessamentoFerias();
                if ((linhasFerias != null) && !linhasFerias.isEmpty()) {
                    for (Integer idLinhaFerias : linhasFerias) {
                        final String idLinha = idLinhaFerias.toString();
                        // Tem que criar novo hash, uma vez que os dados não podem ser modificados
                        // Quando a linha de férias por algum motivo não for mapeada (ex: verba não existe) não deve parar o processamento. Deve continuar
                        // para que o hash de dados sejam preenchidos.
                        if (TextHelper.isNull(linhasTraduzidas.get(idLinha))) {
                            LOG.debug("id_linha Férias: " + idLinha + " não mapeada, possívemente a verba enviada não existe.");
                            continue;
                        }
                        final Map<String, Object> dadosLinha = new HashMap<>(linhasTraduzidas.get(idLinha));

                        // Definir ART_FERIAS = 1 para os casos assim definidos no banco de dados
                        dadosLinha.put(ART_FERIAS, "1");

                        // Definir o campo OCP_OBS = OCP_OBS_PARCELA_COM_RETORNO_FERIAS,
                        // caso esteja com a observação padrão OCP_OBS_PARCELA_COM_RETORNO
                        final String ocpObs = (String) dadosLinha.get(OCP_OBS);
                        if ((ocpObs != null) && ocpObs.equals(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.com.retorno", responsavel))) {
                            dadosLinha.put(OCP_OBS, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.com.retorno.ferias", responsavel));
                        }

                        // Atualiza os dados no cache global de linhas
                        linhasTraduzidas.put(idLinha, Collections.unmodifiableMap(dadosLinha));
                    }
                }
            }

            return numLinhasSemMapeamento;
        } catch (ConvenioControllerException | DAOException | ParserException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Executa pagamento total de parcelas
     * @param linhasSemProcessamento
     * @param adeCodigosAlteracao
     * @param adeCodigosLiquidacao
     * @param adeTipoEnvio
     * @param camposChaveIdent
     * @param tipoImportacaoRetorno
     * @param exportaMensal
     * @param retDAO
     * @param responsavel
     * @throws ImpRetornoControllerException
     */
    @SuppressWarnings("java:S107")
    private void pagaParcelasDescontoTotal(Map<String, Map<String, Object>> linhasSemProcessamento, List<String> adeCodigosAlteracao,
            List<String> adeCodigosLiquidacao, HashMap<String, String> adeTipoEnvio, List<String> camposChaveIdent,
            int tipoImportacaoRetorno, boolean exportaMensal, boolean valorExato, ImpRetornoDAO retDAO, AcessoSistema responsavel) throws ImpRetornoControllerException {
        // Status de parcelas que devem ser utilizados na busca.
        final List<String> spdCodigos = new ArrayList<>();
        spdCodigos.add(CodedValues.SPD_EMPROCESSAMENTO);
        spdCodigos.add(CodedValues.SPD_SEM_RETORNO);
        final CustomTransferObject criterioDescontoTotal = new CustomTransferObject();
        if ((tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_CRITICA) || (tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_NORMAL)) {
            criterioDescontoTotal.setAttribute(SPD_CODIGOS, spdCodigos);
        } else {
            criterioDescontoTotal.setAttribute(ANO_MES_DESCONTO, "S");
        }

        final Iterator<Map.Entry<String, Map<String, Object>>> itLinha = linhasSemProcessamento.entrySet().iterator();
        Map<String, Object> entrada = null;
        // Pega uma linha do mapa para determinar quais chaves compõe o arquivo de retorno.
        if (itLinha.hasNext()) {
            final Map.Entry<String, Map<String, Object>> entry = itLinha.next();
            entrada = entry.getValue();
        }
        if (entrada != null) {
            for (final String atributo : entrada.keySet()) {
                if (!SPD_CODIGOS.equalsIgnoreCase(atributo) &&
                    !ANO_MES_DESCONTO.equalsIgnoreCase(atributo) &&
                    !PRD_VLR_PREVISTO.equalsIgnoreCase(atributo) &&
                    camposChaveIdent.contains(atributo)) {
                    criterioDescontoTotal.setAttribute(atributo, "S");
                }
            }
        }
        try {
            retDAO.criaTabelaParcelasRetorno(camposChaveIdent, criterioDescontoTotal, tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_ATRASADO);
            retDAO.selecionaParcelasPagamentoExato(valorExato);
            retDAO.pagaParcelasSelecionadasDescontoTotal(tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_ATRASADO, responsavel);
            retDAO.associarLinhaRetornoParcelaExata(false);
            // Salva lista de ADEs alteradas, a lista de tipos de envio, se for o caso, e atualiza linhas sem processamento.
            retDAO.getAdeCodigosAlteracao(linhasSemProcessamento, adeCodigosAlteracao, adeTipoEnvio, exportaMensal,
                    tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_ATRASADO, tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_CRITICA, false);
            // Salva lista de ADEs liquidadas.
            retDAO.getAdeCodigosLiquidacao(adeCodigosLiquidacao, false);
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Executa pagamento total consolidado de parcelas
     * @param linhasSemProcessamento
     * @param adeCodigosAlteracao
     * @param adeCodigosLiquidacao
     * @param adeTipoEnvio
     * @param camposChaveIdent
     * @param critica
     * @param atrasado
     * @param exportaMensal
     * @param agrupaPorPeriodo
     * @param adeDAO
     * @param retDAO
     * @param prdDAO
     * @param responsavel
     * @throws ImpRetornoControllerException
     */
    @SuppressWarnings("java:S107")
    private void pagaParcelasDescontoTotalConsolidado(Map<String, Map<String, Object>> linhasSemProcessamento,
            List<String> adeCodigosAlteracao, List<String> adeCodigosLiquidacao,
            HashMap<String, String> adeTipoEnvio, List<String> camposChaveIdent,
            boolean critica, boolean atrasado, boolean exportaMensal, boolean agrupaPorPeriodo, boolean agrupaPorAdeCodigo,
            AutorizacaoDAO adeDAO, ImpRetornoDAO retDAO, ParcelaDescontoDAO prdDAO, AcessoSistema responsavel) throws ImpRetornoControllerException {
        final List<String> cci = new ArrayList<>(camposChaveIdent);
        cci.remove(PRD_VLR_PREVISTO);

        boolean pagouTodas;
        CustomTransferObject criterio;
        TransferObject parcela;
        List<TransferObject> parcelas;
        String adeCodigo;
        String adeTipoVlr;
        Short prdNumero;
        Integer prdCodigo;
        final List<String> linhasPagas = new ArrayList<>();

        final Map<String, Map<String, Object>> linhasPagamentoConsolidado = buscaLinhasConsolidacaoExata(linhasSemProcessamento, false, agrupaPorPeriodo, agrupaPorAdeCodigo, retDAO, responsavel);
        LOG.debug("Linhas candidatas a pagamento total consolidado = " + linhasPagamentoConsolidado.size());
        String numLinha;
        int linha = 0;
        try {
            // DESENV-10310 : Faz a limpeza da session (por causa da incapacidade do hibernate).
            final BatchManager batman = new BatchManager(SessionUtil.getSession());
            for (final Entry<String, Map<String, Object>> entry : linhasPagamentoConsolidado.entrySet()) {
                numLinha = entry.getKey();
                pagouTodas = false;
                criterio = new CustomTransferObject();
                criterio.setAtributos(entry.getValue());
                parcelas = prdDAO.getPrdEmProcessamento(cci, criterio, atrasado);
                if ((parcelas != null) && !parcelas.isEmpty()) {
                    // Vê se a soma das parcelas é igual ao valor passado
                    Iterator<TransferObject> itp = parcelas.iterator();
                    BigDecimal soma = new BigDecimal("0");
                    while (itp.hasNext()) {
                        parcela = itp.next();
                        soma = soma.add(new BigDecimal(parcela.getAttribute(PRD_VLR_PREVISTO).toString()));
                    }
                    final BigDecimal vlrRealizado = new BigDecimal(criterio.getAttribute(PRD_VLR_REALIZADO).toString());
                    // processa somente se todas as parcelas foram pagas
                    if (vlrRealizado.compareTo(soma) == 0) {
                        pagouTodas = true;
                        itp = parcelas.iterator();
                        while (itp.hasNext()) {
                            parcela = itp.next();
                            adeCodigo = parcela.getAttribute(ADE_CODIGO).toString();
                            prdNumero = (parcela.getAttribute(PRD_NUMERO) != null ? Short.valueOf(parcela.getAttribute(PRD_NUMERO).toString()) : null);
                            prdCodigo = NumberHelper.objectToInteger(parcela.getAttribute(PRD_CODIGO));

                            adeTipoVlr = (parcela.getAttribute(ADE_TIPO_VLR) != null ? parcela.getAttribute(ADE_TIPO_VLR).toString() : CodedValues.TIPO_VLR_FIXO);
                            // Envia o valor previsto como realizado, já que o total consolidado é igual ao valor pago
                            final BigDecimal vlrPrevisto = new BigDecimal(parcela.getAttribute(PRD_VLR_PREVISTO).toString());
                            retornoADEParcela(atrasado, exportaMensal, critica, adeCodigo, prdNumero, prdCodigo, adeTipoVlr,
                                    vlrPrevisto, vlrPrevisto, numLinha,
                                    criterio.getAtributos(), adeTipoEnvio, adeCodigosAlteracao,
                                    adeCodigosLiquidacao, adeDAO, retDAO, prdDAO, responsavel);
                        }
                    }
                }
                if (pagouTodas) {
                    linhasPagas.add(numLinha);
                }
                if ((++linha % 1000) == 0) {
                    LOG.debug(LINHAS_LIDAS + linha);
                }
                // DESENV-10310 : Faz a limpeza da session (por causa da incapacidade do hibernate).
                batman.iterate();
            }

            LOG.debug(TOTAL_DE_LINHAS_LIDAS + linha);
            // Remove da lista de linhas sem processamento aquelas que foram pagas.
            final Iterator<String> it2 = linhasPagas.iterator();
            while (it2.hasNext()){
                numLinha = it2.next();
                linhasSemProcessamento.remove(numLinha);
                retDAO.marcaLinhaConsolidadaComoProcessada(numLinha);
            }
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Efetua o pagamento total consolidado inverso, ou seja aquele em que várias linhas do arquivo de retorno ao
     * serem consolidadas, o valor total realizado é o valor previsto da parcela.
     * @param linhasSemProcessamento
     * @param adeCodigosAlteracao
     * @param adeCodigosLiquidacao
     * @param adeTipoEnvio
     * @param camposChaveIdent
     * @param critica
     * @param atrasado
     * @param exportaMensal
     * @param adeDAO
     * @param retDAO
     * @param prdDAO
     * @param responsavel
     * @throws ImpRetornoControllerException
     */
    @SuppressWarnings("java:S107")
    private void pagaParcelasDescontoTotalConsolidadoInverso(Map<String, Map<String, Object>> linhasSemProcessamento,
            List<String> adeCodigosAlteracao, List<String> adeCodigosLiquidacao,
            HashMap<String, String> adeTipoEnvio, boolean critica, boolean atrasado, boolean exportaMensal,
            AutorizacaoDAO adeDAO, ImpRetornoDAO retDAO, ParcelaDescontoDAO prdDAO, Date periodoRetAtrasado, AcessoSistema responsavel) throws ImpRetornoControllerException {

        try {
            final Map<String, List<Map<String, Object>>> linhasPagamentoConsolidado = new HashMap<>();

            retDAO.criaTabelaConsolidacaoInversaExata();

            // Busca os números daquelas linhas que têm possibilidade de pagamento consolidado exato.
            final List<TransferObject> linhasConsolidacaoExata = retDAO.buscaLinhasConsolidacaoInversaExata();

            // A partir dos códigos de ADE, monta um Map com as linhas para pagamento consolidado inverso.
            for (final TransferObject linhaConsolidacaoExata : linhasConsolidacaoExata) {
                final String adeCodigo = linhaConsolidacaoExata.getAttribute("ade_codigo").toString();

                List<Map<String, Object>> listaLinhas = linhasPagamentoConsolidado.get(adeCodigo);
                if (listaLinhas == null) {
                    listaLinhas = new ArrayList<>();
                    linhasPagamentoConsolidado.put(adeCodigo, listaLinhas);
                }

                final String numLinha = linhaConsolidacaoExata.getAttribute("id_linha").toString();
                final Map<String, Object> mapeamentoLinha = linhasSemProcessamento.get(numLinha);
                if (mapeamentoLinha != null) {
                    mapeamentoLinha.put("ID_LINHA", numLinha);
                    mapeamentoLinha.put(ADE_CODIGO, adeCodigo);
                    listaLinhas.add(mapeamentoLinha);
                }
            }

            LOG.debug("Parcelas candidatas a pagamento total consolidado inverso = " + linhasPagamentoConsolidado.size());

            final List<String> cci = new ArrayList<>();
            cci.add(ADE_CODIGO);
            cci.add(SPD_CODIGOS);
            if (atrasado) {
                cci.add(ANO_MES_DESCONTO);
            }

            final CustomTransferObject criterio = new CustomTransferObject();

            int linha = 0;
            final List<String> linhasPagas = new ArrayList<>();

            for (final Entry<String,List<Map<String,Object>>> entry : linhasPagamentoConsolidado.entrySet()) {
                final List<Map<String, Object>> listaLinhas = entry.getValue();
                final String adeCodigo = entry.getKey();

                final List<String> spdCodigos = new ArrayList<>();
                spdCodigos.add(CodedValues.SPD_EMPROCESSAMENTO);
                spdCodigos.add(CodedValues.SPD_SEM_RETORNO);
                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_MULTIPLAS_LINHAS_RETORNO_MESMA_PRD, CodedValues.TPC_SIM, responsavel)) {
                    // Se permite que múltiplas linhas paguem a mesma parcela no retorno, então ao buscar as parcelas,
                    // o sistema deve busar inclusive aquelas que já estão com informação de pagamento, de modo que possa
                    // incrementar o valor pago
                    spdCodigos.add(CodedValues.SPD_LIQUIDADAFOLHA);
                    spdCodigos.add(CodedValues.SPD_REJEITADAFOLHA);
                }

                criterio.setAttribute(ADE_CODIGO, adeCodigo);
                criterio.setAttribute(SPD_CODIGOS, spdCodigos);

                if (atrasado) {
                    criterio.setAttribute(ANO_MES_DESCONTO, DateHelper.format(periodoRetAtrasado, YYYY_MM_DD));
                }
                final List<TransferObject> parcelas = prdDAO.getPrdEmProcessamento(cci, criterio, atrasado);

                if ((parcelas != null) && (parcelas.size() == 1)) {
                    final TransferObject parcela = parcelas.get(0);
                    final Short prdNumero = (parcela.getAttribute(PRD_NUMERO) != null ? Short.valueOf(parcela.getAttribute(PRD_NUMERO).toString()) : null);
                    final Integer prdCodigo = NumberHelper.objectToInteger(parcela.getAttribute(PRD_CODIGO));
                    final String adeTipoVlr = (parcela.getAttribute(ADE_TIPO_VLR) != null ? parcela.getAttribute(ADE_TIPO_VLR).toString() : CodedValues.TIPO_VLR_FIXO);

                    // Vê se a soma das linhas é igual ao valor passado
                    BigDecimal soma = new BigDecimal("0");
                    for (final Map<String, Object> mapeamentoLinha : listaLinhas) {
                        soma = soma.add(new BigDecimal(mapeamentoLinha.get(PRD_VLR_REALIZADO).toString()));
                    }
                    final BigDecimal vlrPrevisto = new BigDecimal(parcela.getAttribute(PRD_VLR_PREVISTO).toString());

                    // processa somente se todas as parcelas foram pagas
                    if (vlrPrevisto.compareTo(soma) == 0) {
                        for (final Map<String, Object> mapeamentoLinha : listaLinhas) {
                            final BigDecimal vlrRealizado = new BigDecimal((String) mapeamentoLinha.get(PRD_VLR_REALIZADO));
                            final String numLinha = (String) mapeamentoLinha.get("ID_LINHA");

                            // Envia o valor realizado de cada linha, já que o total consolidado é igual ao valor pago
                            retornoADEParcela(atrasado, exportaMensal, critica, adeCodigo, prdNumero, prdCodigo, adeTipoVlr,
                                              vlrPrevisto, vlrRealizado, numLinha, mapeamentoLinha, adeTipoEnvio, adeCodigosAlteracao,
                                              adeCodigosLiquidacao, adeDAO, retDAO, prdDAO, responsavel);

                            linhasPagas.add(numLinha);
                        }
                    }
                }
                if ((++linha % 1000) == 0) {
                    LOG.debug(LINHAS_LIDAS + linha);
                }
            }

            LOG.debug(TOTAL_DE_LINHAS_LIDAS + linha);
            // Remove da lista de linhas sem processamento aquelas que foram pagas.
            for (final String numLinha : linhasPagas) {
                linhasSemProcessamento.remove(numLinha);
                retDAO.marcaLinhaConsolidadaComoProcessada(numLinha);
            }
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Executa pagamento parcial de parcelas
     * @param linhasSemProcessamento
     * @param adeTipoEnvio
     * @param adeCodigosAlteracao
     * @param adeCodigosLiquidacao
     * @param camposChaveIdent
     * @param ordemExcCamposChave
     * @param critica
     * @param atrasado
     * @param exportaMensal
     * @param consolida
     * @param adeDAO
     * @param retDAO
     * @param prdDAO
     * @param responsavel
     * @throws ImpRetornoControllerException
     */
    @Override
    public void pagaParcelasParciais(Map<String, Map<String, Object>> linhasSemProcessamento,  HashMap<String, String> adeTipoEnvio,
            List<String> adeCodigosAlteracao, List<String> adeCodigosLiquidacao,
            List<String> camposChaveIdent, String[] ordemExcCamposChave, boolean critica, boolean atrasado,
            boolean exportaMensal, boolean consolida,
            AutorizacaoDAO adeDAO, ImpRetornoDAO retDAO, ParcelaDescontoDAO prdDAO, AcessoSistema responsavel) throws ImpRetornoControllerException {
        String adeCodigo;
        String adeTipoVlr;
        String prdVlr;
        Short prdNumero;
        Integer prdCodigo;
        final List<String> linhasPagas = new ArrayList<>();
        final List<String> cci = new ArrayList<>();
        String numLinha;
        int linha = 0;
        try {
            // DESENV-10310 : Faz a limpeza da session (por causa da incapacidade do hibernate).
            final BatchManager batman = new BatchManager(SessionUtil.getSession());
            for (final Entry<String, Map<String, Object>> entry : linhasSemProcessamento.entrySet()) {
                numLinha = entry.getKey();
                // processa somente se for linha de desconto normal
                if (!"1".equals(entry.getValue().get(ART_FERIAS))) {
                    cci.clear();
                    cci.addAll(camposChaveIdent);
                    if (ParamSist.paramEquals(CodedValues.TPC_PRIORIZA_PAGAMENTO_PRIORIDADE_SVC_CNV_RETORNO, CodedValues.TPC_SIM, responsavel)) {
                        cci.remove(PRD_VLR_PREVISTO);
                    }

                    final CustomTransferObject criterio = new CustomTransferObject();
                    criterio.setAtributos(entry.getValue());
                    boolean encontrou = false;
                    final boolean linhaComRejeito = ((criterio.getAttribute("SITUACAO") != null) && "I".equalsIgnoreCase(criterio.getAttribute("SITUACAO").toString()));

                    int i = 0;
                    String campo;
                    // TENTA ENCONTRAR UMA PARCELA COM MENOS RIGOR NA CHAVE DE IDENTIFICAÇÃO
                    BigDecimal vlrRetorno = new BigDecimal(criterio.getAttribute(PRD_VLR_REALIZADO).toString());
                    while (!encontrou && (i <= ordemExcCamposChave.length)) {
                        final List<TransferObject> parcelas = prdDAO.getPrdEmProcessamento(cci, criterio, atrasado);
                        if ((parcelas != null) && !parcelas.isEmpty()) {

                            if (ParamSist.paramEquals(CodedValues.TPC_PRIORIZA_PAGAMENTO_EXATO_RETORNO, CodedValues.TPC_SIM, responsavel)) {
                                /*
                                 * Itera sobre as parcelas somente pagando aquelas cujo valor enviado pela folha é maior
                                 * ou igual ao valor previsto da parcela. Ao pagar uma parcela, o saldo remanescente também
                                 * será comparado com o valor da próxima parcela, e só poderá ser usado para pagamento se o
                                 * valor for maior ou igual ao valor previsto da parcela. Somente quando não existirem mais
                                 * parcelas com valor suficiente para pagamento exato, o saldo remanescente será usado para
                                 * pagamento parcial no contrato mais antigo, conforme as prioridades atuais.
                                 */
                                boolean utilizouLinha = false;
                                final List<TransferObject> parcelasRemanescentes = new ArrayList<>();
                                final Iterator<TransferObject> itPar = parcelas.iterator();
                                while (itPar.hasNext()) {
                                    final TransferObject parcelaCandidata = itPar.next();
                                    final BigDecimal vlrPrevisto = new BigDecimal(parcelaCandidata.getAttribute(PRD_VLR_PREVISTO).toString());

                                    if (vlrRetorno.doubleValue() >= vlrPrevisto.doubleValue()) {
                                        // Se o valor remanescente de retorno for maior que o valor da parcela.
                                        BigDecimal vlrRealizado = vlrPrevisto.min(vlrRetorno);

                                        if (!itPar.hasNext() && parcelasRemanescentes.isEmpty()) {
                                            // Se é a última parcela, e não tem remanescentes, então o valor total é maior que a soma das parcelas.
                                            // Registra na última parcela o restante do valor pago.
                                            vlrRealizado = vlrRetorno;
                                        }

                                        // Subtrai do valor de retorno o valor utilizado
                                        vlrRetorno = vlrRetorno.subtract(vlrRealizado);

                                        adeCodigo = parcelaCandidata.getAttribute(ADE_CODIGO).toString();
                                        prdNumero = (parcelaCandidata.getAttribute(PRD_NUMERO) != null ? Short.valueOf(parcelaCandidata.getAttribute(PRD_NUMERO).toString()) : null);
                                        prdCodigo = NumberHelper.objectToInteger(parcelaCandidata.getAttribute(PRD_CODIGO));
                                        adeTipoVlr = (parcelaCandidata.getAttribute(ADE_TIPO_VLR) != null ? parcelaCandidata.getAttribute(ADE_TIPO_VLR).toString() : CodedValues.TIPO_VLR_FIXO);

                                        retornoADEParcela(atrasado, exportaMensal, critica, adeCodigo, prdNumero, prdCodigo, adeTipoVlr, vlrPrevisto, vlrRealizado, numLinha,
                                                          criterio.getAtributos(), adeTipoEnvio, adeCodigosAlteracao,
                                                          adeCodigosLiquidacao, adeDAO, retDAO, prdDAO, responsavel);

                                    } else {
                                        parcelasRemanescentes.add(parcelaCandidata);
                                    }
                                }
                                if (!parcelasRemanescentes.isEmpty()) {
                                    for (final TransferObject parcelasRemanescente : parcelasRemanescentes) {
                                        // Se ainda tem valor de retorno para pagamento, itera sobre as parcelas remanescentes,
                                        // realizando o pagamento. Caso não tenha valor mas seja uma linha com rejeito, se a
                                        // linha ainda não foi utilizada ou é consolidado, itera sobre as demais anotando
                                        // a informação de rejeição da parcela
                                        if ((vlrRetorno.signum() > 0) || (linhaComRejeito && (!utilizouLinha || consolida))) {
                                            final BigDecimal vlrPrevisto = new BigDecimal(parcelasRemanescente.getAttribute(PRD_VLR_PREVISTO).toString());
                                            final BigDecimal vlrRealizado = vlrPrevisto.min(vlrRetorno);

                                            // Subtrai do valor de retorno o valor utilizado
                                            vlrRetorno = vlrRetorno.subtract(vlrRealizado).max(BigDecimal.ZERO);

                                            adeCodigo = parcelasRemanescente.getAttribute(ADE_CODIGO).toString();
                                            prdNumero = (parcelasRemanescente.getAttribute(PRD_NUMERO) != null ? Short.valueOf(parcelasRemanescente.getAttribute(PRD_NUMERO).toString()) : null);
                                            prdCodigo = NumberHelper.objectToInteger(parcelasRemanescente.getAttribute(PRD_CODIGO));
                                            adeTipoVlr = (parcelasRemanescente.getAttribute(ADE_TIPO_VLR) != null ? parcelasRemanescente.getAttribute(ADE_TIPO_VLR).toString() : CodedValues.TIPO_VLR_FIXO);

                                            retornoADEParcela(atrasado, exportaMensal, critica, adeCodigo, prdNumero, prdCodigo, adeTipoVlr, vlrPrevisto, vlrRealizado, numLinha,
                                                              criterio.getAtributos(), adeTipoEnvio, adeCodigosAlteracao,
                                                              adeCodigosLiquidacao, adeDAO, retDAO, prdDAO, responsavel);

                                            utilizouLinha = true;
                                        }
                                    }
                                }

                            } else {
                                /*
                                 * Itera sobre a lista de parcelas retornadas, podendo fazer pagamentos parciais.
                                 * Recupera o valor enviado no arquivo, vlr_retorno, e compara com o valor previsto, vlr_previsto.
                                 * Se a lista possuir apenas uma parcela, esta será paga em totalidade com o vlr_retorno.
                                 * Se a lista possuir mais de uma parcela, compara o valor previsto com o valor do retorno.
                                 * Se o valor do retorno é menor ou igual ao valor previsto, paga somente uma parcela e sai do loop.
                                 * Se o valor do retorno é maior que o valor previsto, paga a parcela e calcula o resíduo,
                                 * continua pagando as parcela enquanto tiver resíduo ou enquanto não for a última parcela,
                                 * sendo que esta receberá o valor integral do resíduo, podendo ser maior ou menor
                                 * do que o esperado.
                                 */
                                final Iterator<TransferObject> itPar = parcelas.iterator();
                                boolean haMaisParcelasParaProcessar = itPar.hasNext();
                                while (haMaisParcelasParaProcessar) {
                                    final TransferObject parcela = itPar.next();
                                    final BigDecimal vlrPrevisto = new BigDecimal(parcela.getAttribute(PRD_VLR_PREVISTO).toString());

                                    if (!itPar.hasNext()) {
                                        // Se existe uma só parcela.
                                        prdVlr = String.valueOf(vlrRetorno);
                                        haMaisParcelasParaProcessar = false;
                                    } else if (vlrRetorno.doubleValue() <= vlrPrevisto.doubleValue()) {
                                        // Se o valor de retorno é menor que o valor da parcela.
                                        prdVlr = String.valueOf(vlrRetorno);
                                        vlrRetorno = new BigDecimal("0");

                                        /*
                                         * Se o sistema não consolida ou se o retorno não foi de rejeite, então
                                         * não processa mais nenhuma parcela. No caso contrário, todas as parcelas
                                         * encontradas terão o retorno de rejeite registrado.
                                         */
                                        if (!consolida || !linhaComRejeito) {
                                            haMaisParcelasParaProcessar = false;
                                        }
                                    } else {
                                        // Se o valor remanescente de retorno for maior que o valor da parcela.
                                        prdVlr = String.valueOf(vlrPrevisto);
                                        vlrRetorno = vlrRetorno.subtract(vlrPrevisto);
                                    }
                                    adeCodigo = parcela.getAttribute(ADE_CODIGO).toString();
                                    prdNumero = (parcela.getAttribute(PRD_NUMERO) != null ? Short.valueOf(parcela.getAttribute(PRD_NUMERO).toString()) : null);
                                    prdCodigo = NumberHelper.objectToInteger(parcela.getAttribute(PRD_CODIGO));
                                    adeTipoVlr = (parcela.getAttribute(ADE_TIPO_VLR) != null ? parcela.getAttribute(ADE_TIPO_VLR).toString() : CodedValues.TIPO_VLR_FIXO);

                                    retornoADEParcela(atrasado, exportaMensal, critica, adeCodigo, prdNumero, prdCodigo, adeTipoVlr, vlrPrevisto, new BigDecimal(prdVlr), numLinha,
                                                      criterio.getAtributos(), adeTipoEnvio, adeCodigosAlteracao,
                                                      adeCodigosLiquidacao, adeDAO, retDAO, prdDAO, responsavel);
                                }
                            }

                            encontrou = true;
                        }
                        if (!encontrou && (i < ordemExcCamposChave.length)) {
                            campo = ordemExcCamposChave[i];
                            cci.remove(campo);
                        }
                        i++;
                        // DESENV-10310 : Faz a limpeza da session (por causa da incapacidade do hibernate).
                        batman.iterate();
                    }
                    if (encontrou) {
                        linhasPagas.add(numLinha);
                    }
                    if ((++linha % 1000) == 0) {
                        LOG.debug(LINHAS_LIDAS + linha);
                    }
                }
                // DESENV-10310 : Faz a limpeza da session (por causa da incapacidade do hibernate).
                batman.iterate();
            }

            LOG.debug(TOTAL_DE_LINHAS_LIDAS + linha);
            // Remove da lista de linhas sem processamento aquelas que foram pagas.
            final Iterator<String> itPagas = linhasPagas.iterator();
            while (itPagas.hasNext()) {
                numLinha = itPagas.next();
                linhasSemProcessamento.remove(numLinha);
                retDAO.marcaLinhaConsolidadaComoProcessada(numLinha);
            }
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Traduz as linhas do arquivo de retorno
     * @param linhasTraduzidas
     * @param camposChaveIdent
     * @param entrada
     * @param linha
     * @param numLinha
     * @param critica
     * @param retorno
     * @param hoje
     * @throws ImpRetornoControllerException
     */
    @SuppressWarnings("java:S107")
    private void traduzLinhaArquivo(Map<String, Map<String, Object>> linhasTraduzidas, List<String> camposChaveIdent,
            Map<String, Object> entrada, String linha, int numLinha, boolean critica,
            boolean retorno, Date hoje, AcessoSistema responsavel) throws ImpRetornoControllerException {
     // Lista dos status de parcela que devem ser capturados
        final List<String> spdCodigos = new ArrayList<>();
        spdCodigos.add(CodedValues.SPD_EMPROCESSAMENTO);
        spdCodigos.add(CodedValues.SPD_SEM_RETORNO);
        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_MULTIPLAS_LINHAS_RETORNO_MESMA_PRD, CodedValues.TPC_SIM, responsavel)) {
            // Se permite que múltiplas linhas paguem a mesma parcela no retorno, então ao buscar as parcelas,
            // o sistema deve busar inclusive aquelas que já estão com informação de pagamento, de modo que possa
            // incrementar o valor pago
            spdCodigos.add(CodedValues.SPD_LIQUIDADAFOLHA);
            spdCodigos.add(CodedValues.SPD_REJEITADAFOLHA);
        }

        String spdCodigo   = null;
        final String periodo     = (String) entrada.get(PERIODO);
        final String sinalPrdVlr = (entrada.get("SINAL_VLR_REALIZADO") != null ? (String) entrada.get("SINAL_VLR_REALIZADO") : "1");
        String prdVlr      = (entrada.get(PRD_VLR_REALIZADO)  != null ? (String) entrada.get(PRD_VLR_REALIZADO) : "0");
        String prdData     = (String) entrada.get(PRD_DATA_REALIZADO);
        String ocpObs      = (String) entrada.get(OCP_OBS);
        final String situacao    = (String) entrada.get(SITUACAO);   // Resultado da operação I (Indeferida), D (Deferida), Q (Quitação)
        final String artFerias   = (String) entrada.get(ART_FERIAS);

        // Converte o valor fornecido da parcela
        BigDecimal valorRealizado = null;
        try {
            valorRealizado = new BigDecimal(prdVlr).multiply(new BigDecimal(sinalPrdVlr));
            prdVlr = NumberHelper.format(valorRealizado.doubleValue(), "en", 2, 2);
        } catch (final Exception ex) {
            throw new ImpRetornoControllerException("mensagem.erro.retorno.valor.realizado.incorreto", responsavel);
        }

        // Se Situação igual a Q e Valor igual a Zero, ou situação igual a I, então a parcela deve ser rejeitada
        if ((situacao != null) && ("I".equals(situacao) || "Q".equals(situacao))) {
            if ("I".equals(situacao) || ("Q".equals(situacao) && "0.00".equals(prdVlr))) {
                spdCodigo = CodedValues.SPD_REJEITADAFOLHA;
            } else {
                spdCodigo = CodedValues.SPD_LIQUIDADAFOLHA;
            }
        } else {
            spdCodigo = CodedValues.SPD_LIQUIDADAFOLHA;
        }

        // Se é processamento das críticas e a situação não é rejeitada, então não faz nada
        if (CodedValues.SPD_LIQUIDADAFOLHA.equals(spdCodigo) && critica) {
            return;
        }

        // Cria mensagem de ocorrência de retorno
        if (!TextHelper.isNull(ocpObs)) {
            ocpObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.retorno.arg0", responsavel, ocpObs);
        } else if ("1".equals(artFerias)) {
            ocpObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.com.retorno.ferias", responsavel);
        } else if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_MULTIPLAS_LINHAS_RETORNO_MESMA_PRD, CodedValues.TPC_SIM, responsavel)) {
            ocpObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.com.retorno.valor", responsavel, NumberHelper.format(valorRealizado.doubleValue(), NumberHelper.getLang(), 2, 2));
        } else {
            ocpObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.com.retorno", responsavel);
        }

        // Seta a data de retorno da parcela
        if (prdData == null) {
            prdData = hoje.toString();
        }

        final CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAtributos(entrada);
        criterio.setAttribute(PRD_VLR_REALIZADO, prdVlr);
        criterio.setAttribute(PRD_DATA_REALIZADO, prdData);
        criterio.setAttribute(OCP_OBS, ocpObs);

        if (entrada.get(PRD_VLR_PREVISTO) == null) {
            // Só sobrepõe o valor previsto caso não esteja no arquivo de entrada
            criterio.setAttribute(PRD_VLR_PREVISTO, prdVlr);
        }

        if (critica || retorno) {
            criterio.setAttribute(SPD_CODIGOS, spdCodigos);
        } else {
            setCriterioPeriodo(periodo, criterio, responsavel);
        }

        criterio.setAttribute("LINHASP", linha);
        if (linhasTraduzidas == null) {
            linhasTraduzidas = new HashMap<>();
        }
        linhasTraduzidas.put(String.valueOf(numLinha), new HashMap<>(criterio.getAtributos()));
    }

    private void setCriterioPeriodo(String periodo, CustomTransferObject criterio, AcessoSistema responsavel) throws ImpRetornoControllerException {
        if (!TextHelper.isNull(periodo)) {
            try {
                if (PeriodoHelper.folhaMensal(responsavel)) {
                    periodo = DateHelper.reformat(periodo, YYYY_MM_DD, "yyyy-MM-01");
                }
            } catch (final ParseException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            criterio.setAttribute(ANO_MES_DESCONTO, periodo);
        } else {
            throw new ImpRetornoControllerException("mensagem.erro.retorno.atrasado.periodo.ausente", responsavel);
        }
    }

    /**
     * Seleciona das linhas sem processamento, somente aquelas com possibilidade de pagamento exato consolidado.
     * @param linhasSemProcessamento
     * @param ferias
     * @param retDAO
     * @param responsavel
     * @return
     * @throws ImpRetornoControllerException
     */
    private Map<String, Map<String, Object>> buscaLinhasConsolidacaoExata(Map<String, Map<String, Object>> linhasSemProcessamento,
            boolean ferias, boolean agrupaPorPeriodo, boolean agrupaPorAdeCodigo, ImpRetornoDAO retDAO, AcessoSistema responsavel) throws ImpRetornoControllerException {
        final LinkedHashMap<String, Map<String, Object>> linhasConsolidacaoExataMap = new LinkedHashMap<>();
        try {
            // Cria tabela para contratos de consolidação
            if (ferias) {
                retDAO.criaTabelaConsolidacaoExataFerias();
            } else {
                retDAO.criaTabelaConsolidacaoExata(agrupaPorPeriodo, agrupaPorAdeCodigo);
            }
            // Busca os números daquelas linhas que têm possibilidade de pagamento
            // consolidado exato.
            final List<TransferObject> linhasConsolidacaoExata = retDAO.buscaLinhasConsolidacaoExata(ferias, agrupaPorAdeCodigo);
            // A partir dos números das linhas, monta um Map.
            for (final TransferObject linhaConsolidacaoExata : linhasConsolidacaoExata) {
                final String numLinha = linhaConsolidacaoExata.getAttribute("id_linha").toString();
                final Map<String, Object> mapeamentoLinha = linhasSemProcessamento.get(numLinha);
                if (agrupaPorAdeCodigo) {
                    if (!linhasConsolidacaoExataMap.containsKey(numLinha)) {
                        final String adeCodigo = linhaConsolidacaoExata.getAttribute("ade_codigo").toString();
                        mapeamentoLinha.put(ADE_CODIGO, adeCodigo);
                        linhasConsolidacaoExataMap.put(numLinha, mapeamentoLinha);
                    }
                } else {
                    linhasConsolidacaoExataMap.put(numLinha, mapeamentoLinha);
                }
            }
            return linhasConsolidacaoExataMap;
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Realiza a atualização da parcela paga em retorno
     * @param atrasado
     * @param exportaMensal
     * @param critica
     * @param adeCodigo
     * @param prdNumero
     * @param prdVlrPrevisto
     * @param prdVlrRealizado
     * @param numLinha
     * @param entrada
     * @param adeTipoEnvio
     * @param adeCodigosAlteracao
     * @param adeCodigosLiquidacao
     * @param adeDAO
     * @param retDAO
     * @param prdDAO
     * @param responsavel
     * @throws ImpRetornoControllerException
     */
    @SuppressWarnings("java:S107")
    private void retornoADEParcela(boolean atrasado, boolean exportaMensal, boolean critica, String adeCodigo, Short prdNumero, Integer prdCodigo, String adeTipoVlr,
            BigDecimal prdVlrPrevisto, BigDecimal prdVlrRealizado, String numLinha, Map<String, Object> entrada, Map<String, String> adeTipoEnvio,
            List<String> adeCodigosAlteracao, List<String> adeCodigosLiquidacao,
            AutorizacaoDAO adeDAO, ImpRetornoDAO retDAO, ParcelaDescontoDAO prdDAO, AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            final Date prdDataDesconto  = DateHelper.toSQLDate(DateHelper.parse((String) entrada.get(PERIODO), YYYY_MM_DD));
            final String prdData        = (String) entrada.get(PRD_DATA_REALIZADO);
            final String ocpObs         = (String) entrada.get(OCP_OBS);
            String spdCodigo      = (String) entrada.get(SITUACAO);
            final String tdeCodigo      = (String) entrada.get("TDE_CODIGO");
            String tipoEnvio      = (String) entrada.get("TIPO_ENVIO");
            final String adeAnoMesIni   = (String) entrada.get("ADE_ANO_MES_INI");
            final String adeAnoMesFim   = (String) entrada.get("ADE_ANO_MES_FIM");
            final String adePrazo       = (String) entrada.get("ADE_PRAZO");
            final boolean quitacao = ("Q".equals(spdCodigo));
            spdCodigo = ("I".equals(spdCodigo)) ? CodedValues.SPD_REJEITADAFOLHA : CodedValues.SPD_LIQUIDADAFOLHA;
            tipoEnvio = (tipoEnvio == null) ? "I" : tipoEnvio;

            // A ocorrência de retorno parcial deve ser grava somente quando o tipo valor for fixo
            final String tocCodigo = ((prdVlrPrevisto.compareTo(prdVlrRealizado) > 0) && CodedValues.SPD_LIQUIDADAFOLHA.equals(spdCodigo) && CodedValues.TIPO_VLR_FIXO.equals(adeTipoVlr) ? CodedValues.TOC_RETORNO_PARCIAL : CodedValues.TOC_RETORNO);

            if (atrasado) {
                // Remove as ocorrencias de retorno anteriores
                OcorrenciaParcelaHome.deleteByPrdCodigoTocCodigo(prdCodigo, CodedValues.TOC_CODIGOS_RETORNO_PARCELA);
            }

            // Cria a ocorrência de retorno
            criarOcorrenciaParcela(prdCodigo, tocCodigo, ocpObs, atrasado, responsavel);

            // Guarda os códigos das autorizações para posteriormente modificar
            // os campos sad_codigo e ade_pagas, ou inserir ocorrências de relançamento
            if (!exportaMensal && critica) {
                adeTipoEnvio.put(adeCodigo, tipoEnvio);
            } else if (CodedValues.SPD_LIQUIDADAFOLHA.equals(spdCodigo) ||
                    (atrasado && CodedValues.SPD_REJEITADAFOLHA.equals(spdCodigo))) {
                adeCodigosAlteracao.add(adeCodigo);
            }
            if (quitacao) {
                // Se o status indica uma liquidação de contrato, coloca o código da autorização
                // em uma lista para depois realizar a liquidação
                adeCodigosLiquidacao.add(adeCodigo);
            }

            // Atualizar a tabela de parcela desconto (vlr_realizado, data_realizado, spd_codigo)
            // com valor que veio no arquivo prd_vlr
            prdDAO.liquidaParcelas(prdCodigo, prdData, NumberHelper.format(prdVlrRealizado.doubleValue(), "en"), spdCodigo, tdeCodigo, atrasado);
            adeDAO.atualizaValorFolha(adeCodigo, prdVlrRealizado, adePrazo, adeAnoMesIni, adeAnoMesFim);

            // Associa a linha utilizada para pagar a parcela à parcela+contrato
            retDAO.associarLinhaRetornoParcela(adeCodigo, prdNumero, prdDataDesconto, Integer.parseInt(numLinha));

            // Executa ações para esta ADE
            if (ParamSist.paramEquals(CodedValues.TPC_EXECUTAR_ACOES_POR_TIPO_DESCONTO_IMPORTACAO_RETORNO, CodedValues.TPC_SIM, responsavel)) {
                executarAcoesTipoDesconto(adeCodigo, tdeCodigo, responsavel);
            }

        } catch (DAOException | CreateException | UpdateException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    private void executarAcoesTipoDesconto(String adeCodigo, String tdeCodigo, AcessoSistema responsavel) throws ImpRetornoControllerException {
        if (!TextHelper.isNull(tdeCodigo)) {
            TipoDesconto tde = null;
            try {
                tde = TipoDescontoHome.findByPrimaryKey(tdeCodigo);
            } catch (final FindException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            if ((tde != null) && (tde.getAcao() != null) && (tde.getAcao().getAcaCodigo() != null)) {
                final String acaCodigo = tde.getAcao().getAcaCodigo();
                String srsCodigoNovo = null;

                if (acaCodigo.equals(AcaoEnum.BLOQUEAR_SERVIDOR.getCodigo())) {
                    srsCodigoNovo = CodedValues.SRS_BLOQUEADO;
                } else if (acaCodigo.equals(AcaoEnum.EXCLUIR_SERVIDOR.getCodigo())) {
                    srsCodigoNovo = CodedValues.SRS_EXCLUIDO;
                } else if (acaCodigo.equals(AcaoEnum.REGISTRAR_FALECIMENTO_SERVIDOR.getCodigo())) {
                    srsCodigoNovo = CodedValues.SRS_FALECIDO;
                } else {
                    LOG.warn("Ação por tipo de desconto não mapeada.");
                }

                if (!TextHelper.isNull(srsCodigoNovo)) {
                    try {
                        // Busca o RSE pelo ADE
                        final RegistroServidor rse = RegistroServidorHome.findByAutDesconto(adeCodigo);
                        final String srsCodigoAtual = rse.getStatusRegistroServidor().getSrsCodigo();

                        // Se não estiver já no status novo, então altera para o status
                        // e ria ocorrência de alteração de status
                        if (!srsCodigoAtual.equals(srsCodigoNovo)) {
                            final StatusRegistroServidor srsNovo = StatusRegistroServidorHome.findByPrimaryKey(srsCodigoNovo);
                            final StatusRegistroServidor srsAntigo = StatusRegistroServidorHome.findByPrimaryKey(srsCodigoAtual);
                            final String orsObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.situacao.alterado.de.arg0.para.arg1", responsavel, srsAntigo.getSrsDescricao(), srsNovo.getSrsDescricao());

                            RegistroServidorHome.alterarStatusRegistroServidor(rse.getRseCodigo(), srsCodigoNovo);
                            servidorController.criaOcorrenciaRSE(rse.getRseCodigo(), CodedValues.TOC_RSE_ALTERACAO_STATUS_SERVIDOR, orsObs, null, responsavel);
                        }
                    } catch (FindException | UpdateException | ServidorControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
                    }
                }
            }
        }
    }

    private void executarAcoesTipoDescontoGeral(List<String> orgCodigo, List<String> estCodigo, AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            // Lista parcelas com tipo de desconto que possui ação cadastrada
            final ListaParcelasComAcaoEmTipoDescontoQuery query = new ListaParcelasComAcaoEmTipoDescontoQuery(orgCodigo, estCodigo);
            final List<TransferObject> ades = query.executarDTO();

            // Para cada ADE chama o método de execução das ações
            for (final TransferObject ade : ades) {
                final String adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
                final String tdeCodigo = ade.getAttribute(Columns.TDE_CODIGO).toString();
                executarAcoesTipoDesconto(adeCodigo, tdeCodigo, responsavel);
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Executa a conclusão do retorno da folha
     * @param tipoEntidade
     * @param codigoEntidade
     * @param responsavel
     * @throws ImpRetornoControllerException
     */
    @Override
    public void finalizarIntegracaoFolha(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            final String orgCodigo = ("ORG".equalsIgnoreCase(tipoEntidade)) ? codigoEntidade : null;
            final String estCodigo = ("EST".equalsIgnoreCase(tipoEntidade)) ? codigoEntidade : null;
            final String usuCodigo = (responsavel != null ? responsavel.getUsuCodigo() : CodedValues.USU_CODIGO_SISTEMA);

            List<String> entCodigos = null;
            if (codigoEntidade != null) {
                entCodigos = new ArrayList<>();
                entCodigos.add(codigoEntidade);
            }
            List<String> orgCodigos = null;
            List<String> estCodigos = null;
            if (tipoEntidade != null) {
                if ("EST".equalsIgnoreCase(tipoEntidade)) {
                    estCodigos = entCodigos;
                } else if ("ORG".equalsIgnoreCase(tipoEntidade)) {
                    orgCodigos = entCodigos;
                }
            }

            ImportaRetorno importadorRetorno = null;
            final String importadorRetornoClassName = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CLASSE_IMPORTADOR_RETORNO, responsavel);
            if (!TextHelper.isNull(importadorRetornoClassName)) {
                importadorRetorno = ImportaRetornoFactory.getImportadorRetorno(importadorRetornoClassName, CodedValues.TIPO_RETORNO_CONCLUSAO, orgCodigo, estCodigo);
                if (importadorRetorno.sobreporConclusaoRetorno()) {
                    importadorRetorno.finalizarIntegracaoFolha(tipoEntidade, codigoEntidade, responsavel);
                    return;
                }
            }

            if (importadorRetorno != null) {
                LOG.debug("pre-processamento conclusao retorno: " + DateHelper.getSystemDatetime());
                importadorRetorno.preConclusaoImportacaoRetorno();
                LOG.debug("fim - pre-processamento conclusao retorno " + DateHelper.getSystemDatetime());
            }

            final DAOFactory daoFactory = DAOFactory.getDAOFactory();
            final AutorizacaoDAO adeDAO = daoFactory.getAutorizacaoDAO();
            final ParcelaDescontoDAO prdDAO = daoFactory.getParcelaDescontoDAO();
            final ImpRetornoDAO retDAO = daoFactory.getImpRetornoDAO();
            final HistoricoRetMovFinDAO hrmDAO = daoFactory.getHistoricoRetMovFinDAO();
            final HistoricoMargemDAO hmaDAO = daoFactory.getHistoricoMargemDAO();

            final Date hoje = DateHelper.toSQLDate(DateHelper.getSystemDate());
            final Date proximoPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
            // Cria a ocorrência para as parcelas sem o retorno da folha
            prdDAO.criaOcorrenciaSemRetorno(tipoEntidade, codigoEntidade, responsavel);
            // Rejeita todas as parcelas que estão em processamento
            prdDAO.liquidaParcelas(hoje.toString(), CodedValues.SPD_REJEITADAFOLHA, tipoEntidade, codigoEntidade);
            // Chama rotinas de controle / correção de saldo devedor
            ajustarContratosSaldoDevedor(orgCodigos, estCodigos, proximoPeriodo, responsavel);
            // Define o período do retorno
            final List<String> periodos = recuperaPeriodosRetorno(CodedValues.TIPO_RETORNO_NORMAL, null, orgCodigos, estCodigos, responsavel);
            // Coloca em andamento os contratos em estoque que foram pagos no mes atual.
            adeDAO.retiraDoEstoque(null, tipoEntidade, codigoEntidade, usuCodigo);
            // Coloca em estoque os contratos que não foram pagos este mês.
            final boolean colocaEmEstoque = ParamSist.paramEquals(CodedValues.TPC_COLOCA_ESTOQUE_ADE_NAO_PAGA, CodedValues.TPC_SIM, responsavel);
            if (colocaEmEstoque) {
                adeDAO.colocaEmEstoque(null, periodos.get(0), tipoEntidade, codigoEntidade, usuCodigo);
            }
            // Gera os relatórios de percentual de rejeito para a entidade e para as consignatárias
            geraRelatorioPercentualRejeito(periodos.get(0), orgCodigos, estCodigos, responsavel);

            // Atualiza o valor das ADE cujo parametro de serviço diz para atualizar.
            retDAO.atualizarAdeVlrRetorno(DateHelper.format(proximoPeriodo, YYYY_MM_DD), null);

            // DESENV-16729 - Alteração do status de servidor no processamento do retorno
            if (ParamSist.paramEquals(CodedValues.TPC_EXECUTAR_ACOES_POR_TIPO_DESCONTO_IMPORTACAO_RETORNO, CodedValues.TPC_SIM, responsavel)) {
                executarAcoesTipoDescontoGeral(orgCodigos, estCodigos, responsavel);

                acaoSuspenderContratoParcelaRejeitada(responsavel);
            }

            // DESENV-5578 - Envio de e-mail ao rejeitar parcela
            enviarEmailServidorContratosRejeitados(periodos, orgCodigos, estCodigos, responsavel);

            // DESENV-16879 - Parcelas que são de contratos com forma de pagamento boleto, ou seja, que tem o tda_codigo 55 com o valor de boleto para o periodo processado devem ser liquidadas.
            if(ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, CodedValues.TPC_SIM, responsavel)
               && ParamSist.paramEquals(CodedValues.TPC_PERMITE_RESERVA_SAUDE_SEM_FLUXO_MODULO_SAUDE, CodedValues.TPC_SIM, responsavel)){
                LOG.debug("LIQUIDA PARCELAS DE CONTRATOS DO TIPO BOLETO: " + DateHelper.getSystemDatetime());
                prdDAO.liquidaParcelasPagamentoBoleto(orgCodigos, estCodigos, periodos, responsavel);
                adeDAO.atualizaAdeExportadas(orgCodigos, estCodigos, null, false, responsavel);
            }

            // Move as parcelas do período
            prdDAO.moverParcelasIntegradas(orgCodigos, estCodigos, null, periodos, false);

            // Se permite que dois períodos estejam abertos, verifica se parcelas do período futuro
            // foram pagas em férias e também restaura o status das parcelas futuras
            if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_DOIS_PERIODOS_EXPORTACAO_ABERTOS, CodedValues.TPC_SIM, responsavel)) {
                if (ParamSist.paramEquals(CodedValues.TPC_TEM_PROCESSAMENTO_FERIAS, CodedValues.TPC_SIM, responsavel)) {
                    prdDAO.removerParcelasPosPeriodoPagaEmFerias(orgCodigos, estCodigos);
                }
                prdDAO.alterarStatusParcelasPosPeriodo(orgCodigos, estCodigos, CodedValues.SPD_AGUARD_PROCESSAMENTO, CodedValues.SPD_EMPROCESSAMENTO);
            }

            // Finaliza o histórico de conclusão de retorno: deve ser feito antes do cálculo de margem
            hrmDAO.finalizarHistoricoConclusaoRetorno(orgCodigos, estCodigos);

            // Recalcula a margem dos servidores: Verifica parâmetro de sistema (Default: Para recalcular
            // a margem mesmo que não tenha o parâmetro)
            final Object param = ParamSist.getInstance().getParam(CodedValues.TPC_RECALCULA_MARGEM_CONCLUSAO_RETORNO, responsavel);
            final boolean recalculaMargem = ((param == null) || CodedValues.TPC_SIM.equals(param));
            if (recalculaMargem) {
                // Inicia gravação de histórico de margem, caso não exista
                hmaDAO.iniciarHistoricoMargemCasoNaoExista(orgCodigos, estCodigos, null, OperacaoHistoricoMargemEnum.IMPORTACAO_RET_MOV_FIN);
                // Efetual recálculo de margem sem gravação do histórico: o histórico normalmente terá
                // sido iniciado pelo retorno, ou pelo comando acima, caso a conclusão não seja precedida
                // da importação do retorno
                margemController.recalculaMargem(tipoEntidade, entCodigos, responsavel);
            }
            // Finaliza o histórico de margem
            hmaDAO.finalizarHistoricoMargem(orgCodigos, estCodigos, null, OperacaoHistoricoMargemEnum.IMPORTACAO_RET_MOV_FIN);

            if (importadorRetorno != null) {
                LOG.debug("pos-processamento conclusao retorno: " + DateHelper.getSystemDatetime());
                importadorRetorno.posConclusaoImportacaoRetorno();
                LOG.debug("fim - pos-processamento conclusao retorno " + DateHelper.getSystemDatetime());
            }

            // Se é importação por órgão/estabelecimento, verifica se existem parcelas não atualizadas
            // na tabela do período para órgãos que já tiveram o processamento finalizado, provavelmente
            // por transferências para estes órgãos. Se existir, rejeita as parcelas.
            if (!ParamSist.paramEquals(CodedValues.TPC_REJEITA_PRD_SEM_RETORNO_ORG_PROCESSADOS, CodedValues.TPC_NAO, responsavel)
                    && (tipoEntidade != null) && !"CSE".equalsIgnoreCase(tipoEntidade)) {
                final ObtemTotalParcelasTransfSemRetornoQuery prdSemRetQuery = new ObtemTotalParcelasTransfSemRetornoQuery();
                prdSemRetQuery.agrupaPorOrgao = true;
                prdSemRetQuery.periodo = periodos.get(0);
                final List<TransferObject> orgaosPrdSemRet = prdSemRetQuery.executarDTO();
                if ((orgaosPrdSemRet != null) && !orgaosPrdSemRet.isEmpty()) {
                    LOG.warn("EXISTEM ÓRGÃOS JÁ PROCESSADOS COM PARCELAS SEM RETORNO NA TABELA DO PERÍODO: AS PARCELAS SERÃO REJEITADAS COM INFORMAÇÃO DE SEM RETORNO.");
                    final List<String> orgCodPrdSemRet = new ArrayList<>();
                    for (final TransferObject orgaoPrdSemRet : orgaosPrdSemRet) {
                        final String orgao = orgaoPrdSemRet.getAttribute("ORGAO").toString();
                        final int totalPrdSemRet = Integer.parseInt(orgaoPrdSemRet.getAttribute("QTD").toString());
                        // Inclui na lista para a movimentação de parcelas entre a tabela do período e a histórica
                        orgCodPrdSemRet.add(orgao);
                        LOG.debug("TOTAL PARCELAS SEM RETORNO DO ÓRGÃO \"" + orgao + "\": " + totalPrdSemRet);
                        // Cria a ocorrência para as parcelas sem o retorno da folha
                        prdDAO.criaOcorrenciaRetorno(CodedValues.TOC_RETORNO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.sem.retorno", responsavel), "ORG", orgao, usuCodigo);
                        // Rejeita todas as parcelas que estão em processamento
                        prdDAO.liquidaParcelas(hoje.toString(), CodedValues.SPD_REJEITADAFOLHA, "ORG", orgao);
                    }

                    // DESENV-5578 - Envio de e-mail ao rejeitar parcela
                    enviarEmailServidorContratosRejeitados(periodos, orgCodPrdSemRet, null, responsavel);

                    // Move as parcelas do período
                    prdDAO.moverParcelasIntegradas(orgCodPrdSemRet, null, null, periodos, true);
                }
            }



            // Verifica se as consignatárias bloqueadas por saldo devedor podem ser desbloqueadas na conclusão do retorno.
            verificarDesbloqueioAutomaticoSaldoDevedorConsignataria(responsavel);

            // Se o parâmetro de URL do Centralizador mobile estiver setado, então agenda processo
            // de atualização da base de dados de CPFs do Centralizador
            agendarAtualizacaoBaseCentralCpf(responsavel);

            // Agenda arquivamento de servidores excluídos
            agendarArquivamentoServidor(orgCodigos, estCodigos, responsavel);

            // Verifica % de parcelas pagas e envia email cadastrado para CSA sobre alerta de Refinanciamento
            enviarEmailAlertaParcelasPagasCsa(responsavel);

            // Grava log da operação
            final LogDelegate log = new LogDelegate(responsavel, Log.FOLHA, Log.IMPORTACAO_RETORNO, Log.LOG_INFORMACAO);
            log.setEstabelecimento(estCodigo);
            log.setOrgao(orgCodigo);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.finalizando.integracao.com.folha", responsavel));
            log.write();

            // log de ocorrência de consignante
            consignanteController.createOcorrenciaCse(CodedValues.TOC_CONCLUSAO_RETORNO, responsavel);
        } catch (final LogControllerException ex) {
            // Não faz rollback e nem dá exceção
            LOG.error(ex.getMessage(), ex);
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (ex instanceof ImpRetornoControllerException) {
                throw (ImpRetornoControllerException) ex;
            }
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
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

    private void enviarEmailServidorContratosRejeitados(List<String> periodos, List<String> orgCodigo, List<String> estCodigo, AcessoSistema responsavel) throws DAOException {
    	// verificar se o parametro está habilitado
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_EMAIL_CONTRATOS_REJEITADOS_FOLHA, CodedValues.NOTIFICA_CONTRATOS_REJEITADOS_FOLHA_OPCIONAL, responsavel) ||
                ParamSist.paramEquals(CodedValues.TPC_HABILITA_EMAIL_CONTRATOS_REJEITADOS_FOLHA, CodedValues.NOTIFICA_CONTRATOS_REJEITADOS_FOLHA_OBRIGATORIO, responsavel)) {

    		LOG.debug("GERA RELATÓRIOS DE PARA ENVIAR PARA SERVIDOR DE CONTRATOS REJEITADOS");

    		final boolean notificaoObrigatoria = ParamSist.paramEquals(CodedValues.TPC_HABILITA_EMAIL_CONTRATOS_REJEITADOS_FOLHA, CodedValues.NOTIFICA_CONTRATOS_REJEITADOS_FOLHA_OBRIGATORIO, responsavel);

    		for (final String periodo : periodos) {

    			final Map<TransferObject, String> map = new HashMap<>();

    			// verificar quais usuários estão habilitados para receber emails que possuem contratos rejeitados
    			final ListaParcelaDescontoUsuariosRejeitadosQuery parcelaDescontoQuery = new ListaParcelaDescontoUsuariosRejeitadosQuery(periodo, orgCodigo, estCodigo, notificaoObrigatoria);
    			final List<TransferObject> usuarios = parcelaDescontoQuery.executarDTO();

    			for (final TransferObject usuario : usuarios) {

    				// buscar contratos rejeitados por usuários
    				final ListaParcelasRejeitadasQuery parcelasRejeitadasQuery = new ListaParcelasRejeitadasQuery(periodo, (String) usuario.getAttribute(Columns.USU_CODIGO), orgCodigo, estCodigo, notificaoObrigatoria);
    				final List<TransferObject> parcelas = parcelasRejeitadasQuery.executarDTO();

    				// montar arquivo com os contratos rejeitados
    				if((parcelas !=null) && !parcelas.isEmpty()) {
    				    final String tabela = gerarTabelaEmailParcelasRejeitadas(parcelas,responsavel);
    				    map.put(usuario, tabela);
    				}
    			}

    			// enviar email para usuário
    			enviarEmail(map);

    		}

    		LOG.debug("FIM RELATÓRIOS DE PARA ENVIAR PARA SERVIDOR DE CONTRATOS REJEITADOS");

    	}

	}

	private void enviarEmail(Map<TransferObject, String> map) {

		final Set<Map.Entry<TransferObject, String>> entrySet = map.entrySet();

		for (final Map.Entry<TransferObject, String> entry : entrySet) {

			final TransferObject usuario = entry.getKey();

			final String corpoEmail = entry.getValue();
			final String email = (String) usuario.getAttribute(Columns.SER_EMAIL);
			if(TextHelper.isNull(email)) {
			    LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.erro.email.contratos.rejeitados.servidor.nao.tem.email", AcessoSistema.getAcessoUsuarioSistema(), (String) usuario.getAttribute(Columns.SER_CODIGO)));
			    continue;
			}
			try {
				EnviaEmailHelper.enviaEmailServidorContratosRejeitados(email, corpoEmail);
			} catch (final ViewHelperException e) {
				LOG.error(e.getMessage(), e);
			}
		}

	}

	private String gerarTabelaEmailParcelasRejeitadas(List<TransferObject> parcelas, AcessoSistema responsavel) {

		final StringBuilder builder = new StringBuilder();
		 builder.append("<table border=\"1\">");
         builder.append("    <thead>");
         builder.append("        <tr style=\"color: #fff; background: #831d1c;\">");
         builder.append("            <th>").append(ApplicationResourcesHelper.getMessage("rotulo.tabela.email.contratos.rejeitados.servidor.numero.parcela", responsavel)).append("</th>");
         builder.append("            <th>").append(ApplicationResourcesHelper.getMessage("rotulo.tabela.email.contratos.rejeitados.servidor.data.processamento", responsavel)).append("</th>");
         builder.append("            <th>").append(ApplicationResourcesHelper.getMessage("rotulo.tabela.email.contratos.rejeitados.servidor.motivo", responsavel)).append("</th>");
         builder.append("            <th>").append(ApplicationResourcesHelper.getMessage("rotulo.tabela.email.contratos.rejeitados.servidor.numero.ade", responsavel)).append("</th>");
         builder.append("            <th>").append(ApplicationResourcesHelper.getMessage("rotulo.tabela.email.contratos.rejeitados.servidor.numero.identificador", responsavel)).append("</th>");
         builder.append("            <th>").append(ApplicationResourcesHelper.getMessage("rotulo.tabela.email.contratos.rejeitados.servidor.prazo", responsavel)).append("</th>");
         builder.append("            <th>").append(ApplicationResourcesHelper.getMessage("rotulo.tabela.email.contratos.rejeitados.servidor.valor", responsavel)).append("</th>");
         builder.append("        </tr>");
         builder.append("    </thead>");
         builder.append("    <tbody>");

		for (final TransferObject parcela : parcelas) {

			final String prdNumero = parcela.getAttribute(Columns.PRD_NUMERO) !=null ? String.valueOf(parcela.getAttribute(Columns.PRD_NUMERO)) : null;
			final String prdDataRealizado = DateHelper.format((Date) parcela.getAttribute(Columns.PRD_DATA_REALIZADO), "dd/MM/yyyy");

			String descricao = (String) parcela.getAttribute(Columns.TDE_DESCRICAO);

			if (TextHelper.isNull(descricao)) {
				descricao = (String) parcela. getAttribute(Columns.OCP_OBS);
			} else if (!TextHelper.isNull(parcela. getAttribute(Columns.OCP_OBS))) {
				descricao = descricao + " - " + (String) parcela. getAttribute(Columns.OCP_OBS);
			} else {
				descricao = (String) parcela.getAttribute(Columns.SPD_DESCRICAO);
			}

			final Long adeNumero = (Long) parcela.getAttribute(Columns.ADE_NUMERO);
			final String adeIdentificador = (String) parcela.getAttribute(Columns.ADE_IDENTIFICADOR);
			final Integer adePrazo = (Integer) parcela.getAttribute(Columns.ADE_PRAZO);
			final String adeVlr =  NumberHelper.format(((BigDecimal)parcela.getAttribute(Columns.ADE_VLR)).doubleValue(), NumberHelper.getLang(), true);

			builder.append("<tr>");
			builder.append("  <td>").append(prdNumero).append("</td>");
			builder.append("  <td>").append(prdDataRealizado).append("</td>");
			builder.append("  <td>").append(descricao).append("</td>");
			builder.append("  <td>").append(adeNumero).append("</td>");
			builder.append("  <td>").append(adeIdentificador).append("</td>");
			builder.append("  <td>").append(!TextHelper.isNull(adePrazo) ? adePrazo : ApplicationResourcesHelper.getMessage("rotulo.indeterminado.singular", responsavel)).append("</td>");
			builder.append("  <td>").append(adeVlr).append("</td>");
			builder.append("</tr>");
		}

        builder.append("    </tbody>");
        builder.append("</table>");
		return builder.toString();
	}

	private void geraRelatorioPercentualRejeito(String periodo, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) {
        LOG.debug("GERA RELATÓRIOS DE PERCENTUAL DE REJEITO");
        final Processo processo = new ProcessaRelatorioPercentualRejeito(periodo, orgCodigos, estCodigos, responsavel);
        // Processo deve ser executado para utilizar a mesma Thread
        processo.run();
        LOG.debug("FIM RELATÓRIOS DE PERCENTUAL DE REJEITO");
    }

    /**
     * Desfaz o último retorno importado, voltando as consignações e
     * parcelas a situação anterior a importação.
     * @param orgCodigo         : código do órgão, caso a operação esteja sendo realizada por usuário de órgão
     * @param estCodigo         : código do estabelecimento, caso a operação esteja sendo realizada por usuário de órgão com perfil de Estabelecimento
     * @param recalcularMargem  : true para recalcular as margens dos servidores
     * @param desfazerMovimento : true para desfazer antes o último movimento
     * @param parcelas          : seleção dos períodos, caso de processamento de férias, que devem ser desfeitos
     * @param responsavel       : responsável pela operação
     * @throws ImpRetornoControllerException
     */
    @Override
    public void desfazerUltimoRetorno(String orgCodigo, String estCodigo, boolean recalcularMargem, boolean desfazerMovimento, String[] parcelas, AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            final DAOFactory factory = DAOFactory.getDAOFactory();
            // Transforma os atributos
            final String tipoEntidade = (!TextHelper.isNull(estCodigo) ? "EST" : (!TextHelper.isNull(orgCodigo) ? "ORG" : "CSE"));
            List<String> entCodigos = null;
            List<String> orgCodigos = null;
            List<String> estCodigos = null;
            if ("EST".equals(tipoEntidade)) {
                entCodigos = new ArrayList<>();
                entCodigos.add(estCodigo);
                estCodigos = entCodigos;
            } else if ("ORG".equals(tipoEntidade)) {
                entCodigos = new ArrayList<>();
                entCodigos.add(orgCodigo);
                orgCodigos = entCodigos;
            }
            // Pega o último periodo de retorno
            final String periodo = DateHelper.format(getUltimoPeriodoRetorno(orgCodigo, estCodigo, responsavel), YYYY_MM_DD);
            LOG.debug("============================================================================================================");
            final ImpRetornoDAO retDAO = factory.getImpRetornoDAO();

            // Se o usuário optou por desfazer o movimento financeiro, executa rotina para desfazê-lo
            if (desfazerMovimento) {
                LOG.debug("=== DESFAZ ANTES O ÚLTIMO MOVIMENTO FINANCEIRO EXPORTADO");
                retDAO.desfazerUltimoMovimento(orgCodigo, estCodigo, periodo);
            }

            LOG.debug("=== DESFAZENDO ULTIMO RETORNO : PERIODO[" + periodo + "]");
            // Chama rotina para desfazer o retorno
            retDAO.desfazerUltimoRetorno(orgCodigo, estCodigo, periodo, parcelas, null);
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_SDP, CodedValues.TPC_SIM, responsavel)) {
                // Desfaz conclusão de despesas comuns
                final DespesaComumDAO decDAO = DAOFactory.getDAOFactory().getDespesaComumDAO();
                decDAO.desfazerConclusaoDespesasComum(periodo);
            }

            // Finaliza o histórico de margem (caso não esteja concluído)
            LOG.debug("=== FINALIZA O HISTÓRICO DE MARGEM DO ULTIMO RETORNO");
            final HistoricoMargemDAO hmaDAO = factory.getHistoricoMargemDAO();
            hmaDAO.finalizarHistoricoMargem(orgCodigos, estCodigos, null, OperacaoHistoricoMargemEnum.IMPORTACAO_RET_MOV_FIN);

            // Finaliza o histórico de conclusão de retorno (caso não esteja concluído)
            LOG.debug("=== FINALIZA O HISTÓRICO DE CONCLUSAO DO ULTIMO RETORNO");
            final HistoricoRetMovFinDAO hrmDAO = factory.getHistoricoRetMovFinDAO();
            hrmDAO.desfazerHistoricoConclusaoRetorno(orgCodigos, estCodigos, periodo);

            // Se usuário optou por recalcular margem, então inclui histório
            // de margem para esse recalculo
            if (recalcularMargem) {
                // Inicia gravação de histórico de margem
                LOG.debug("=== INICIA NOVO HISTÓRICO DE MARGEM");
                hmaDAO.iniciarHistoricoMargem(orgCodigos, estCodigos, null, OperacaoHistoricoMargemEnum.REVERSAO_RET_MOV_FIN);
                // Recalcula margem dos servidores
                LOG.debug("=== RECALCULA MARGEM DOS SERVIDORES");
                margemController.recalculaMargem(tipoEntidade, entCodigos, responsavel);
                // Finaliza o historico de margem
                LOG.debug("=== FINALIZA HISTÓRICO DE MARGEM");
                hmaDAO.finalizarHistoricoMargem(orgCodigos, estCodigos, null, OperacaoHistoricoMargemEnum.REVERSAO_RET_MOV_FIN);
            }

            final boolean moduloBeneficio = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, CodedValues.TPC_SIM, responsavel);
            // Se é módulo de benefício, remove da tabela de arquivo de faturamento os dados do período informado
            if (moduloBeneficio) {
                LOG.debug("=== INICIA REMOÇÃO ARQUIVO FATURAMENTO BENEFÍCIO: " + DateHelper.getSystemDatetime());
                final ArquivoFaturamentoBeneficioDAO afbDAO = factory.getArquivoFaturamentoBeneficioDAO();
                afbDAO.removerArquivoFaturamentoBeneficio(orgCodigos, estCodigos, periodo);
                LOG.debug("=== FINALIZA REMOÇÃO ARQUIVO FATURAMENTO BENEFÍCIO: " + DateHelper.getSystemDatetime());
            }

            // Grava log da operação
            final LogDelegate log = new LogDelegate(responsavel, Log.FOLHA, Log.IMPORTACAO_RETORNO, Log.LOG_INFORMACAO);
            log.setEstabelecimento(estCodigo);
            log.setOrgao(orgCodigo);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.desfazendo.ultimo.retorno.periodo.arg0", responsavel, periodo));
            log.write();

            // Grava registro em ocorrência de consignante
            consignanteController.createOcorrenciaCse(CodedValues.TOC_DESFAZER_RETORNO, responsavel);

            LOG.debug("============================================================================================================");
        } catch (final LogControllerException ex) {
            // Não faz rollback e nem dá exceção
            LOG.error(ex.getMessage(), ex);
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (ex instanceof ImpRetornoControllerException) {
                throw (ImpRetornoControllerException) ex;
            }
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Obtém através do histórico de conclusão o periodo do
     * último retorno importado no sistema. Caso não exista histórico,
     * calcula o último período de acordo com o dia de corte e a data atual
     * @param orgCodigo    : código do órgão, caso a operação esteja sendo realizada por usuário de órgão
     * @param estCodigo    : código do estabelecimento, caso a operação esteja sendo realizada por usuário de órgão com perfil de Estabelecimento
     * @param responsavel  : responsavel pela operação
     * @return
     * @throws ImpRetornoControllerException
     */
    @Override
    public java.util.Date getUltimoPeriodoRetorno(String orgCodigo, String estCodigo, AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            // Pesquisa os históricos de conclusão de retorno
            final ObtemUltimoPeriodoRetornoQuery ultPeriodoQuery = new ObtemUltimoPeriodoRetornoQuery();
            ultPeriodoQuery.orgCodigo = orgCodigo;
            ultPeriodoQuery.estCodigo = estCodigo;
            final List<Date> ultPeriodoList = ultPeriodoQuery.executarLista();

            if ((ultPeriodoList != null) && !ultPeriodoList.isEmpty() && (ultPeriodoList.get(0) != null)) {
                return ultPeriodoList.get(0);
            } else {
                // Se não tem histórico de conclusão de retorno (provavelmente porque
                // nunca houve um retorno importado) então calcula o último período
                // baseado no calendário folha
                return PeriodoHelper.getInstance().getPeriodoAnterior(orgCodigo, responsavel);
            }
        } catch (HQueryException | PeriodoException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Verifica se já existe período com parcelas exportadas que seja diferente do período passado por parâmetro
     * @param orgCodigo    : código do órgão, caso a operação esteja sendo realizada por usuário de órgão
     * @param estCodigo    : código do estabelecimento, caso a operação esteja sendo realizada por usuário de órgão com perfil de Estabelecimento
     * @param periodo      : período ao qual as parcelas não devem estar dentro
     * @param responsavel  : responsavel pela operação
     * @return
     * @throws ImpRetornoControllerException
     */
    @Override
    public boolean existeOutroPeriodoExportado(String orgCodigo, String estCodigo, java.util.Date periodo, AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            return !(new ListaParcelasForaPeriodoQuery(TextHelper.objectToStringList(orgCodigo), TextHelper.objectToStringList(estCodigo), periodo).executarDTO().isEmpty());
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Retorna as linhas que ficaram sem processamento para importação no sistema (caso exista)
     * @param responsavel
     * @return
     * @throws ImpRetornoControllerException
     */
    @Override
    public List<TransferObject> getLinhasSemProcessamento(AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            final ImpRetornoDAO retDAO = DAOFactory.getDAOFactory().getImpRetornoDAO();
            return retDAO.getLinhasSemProcessamento();
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Executa rotina de abatimento e ajuste de saldo devedor para contratos que obtiveram
     * retorno no período.
     * @param orgCodigos   : código dos órgãos, caso a operação esteja sendo realizada por usuário de órgão
     * @param estCodigos   : código dos estabelecimentos, caso a operação esteja sendo realizada por usuário de órgão com perfil de Estabelecimento
     * @param responsavel  : responsável pela operação
     * @throws ImpRetornoControllerException
     */
    private void ajustarContratosSaldoDevedor(List<String> orgCodigos, List<String> estCodigos, Date periodoAtual, AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            final ControleSaldoDvImpRetornoDAO dao = DAOFactory.getDAOFactory().getControleSaldoDvImpRetornoDAO();
            dao.abaterSaldoDevedor(orgCodigos, estCodigos);
            dao.concluirContratosSaldoDevedorZerado(orgCodigos, estCodigos);
            dao.concluirContratosNaoPagosNoExercicio(orgCodigos, estCodigos);
            dao.estenderPrazo(orgCodigos, estCodigos);
            dao.reimplantarContratosNaoPagos(orgCodigos, estCodigos);

            final List<TransferObject> contratos = dao.listarContratosCorrecaoOutroServico(orgCodigos, estCodigos);

            if ((contratos != null) && !contratos.isEmpty()) {
                // Os novos contratos de correção estarão deferidos, com valores zerados (ade_vlr e ade_vlr_sdo_mov/ret)
                final String sadCodigo = CodedValues.SAD_DEFERIDA;
                final String usuCodigo = CodedValues.USU_CODIGO_SISTEMA;
                final String adeTipoVlr = CodedValues.TIPO_VLR_FIXO;
                final String adeCodReg = CodedValues.COD_REG_DESCONTO;
                final String tocCodigo = CodedValues.TOC_TARIF_RESERVA;
                final String tntCodigo = CodedValues.TNT_CORRECAO_SALDO;
                final String ocaObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.inclusao.correcao.saldo", responsavel);
                final BigDecimal adeVlr = new BigDecimal("0.00");
                final BigDecimal adeVlrSdo = new BigDecimal("0.00");
                final String periodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);

                for (final TransferObject next : contratos) {
                    final String adeCodigoOrigem = (String) next.getAttribute("ADE_CODIGO_ORIGEM");
                    final String vcoCodigo = (String) next.getAttribute("VCO_CODIGO");
                    final String rseCodigo = (String) next.getAttribute("RSE_CODIGO");
                    final Date adeAnoMesIni = DateHelper.toSQLDate((java.util.Date) next.getAttribute("ADE_ANO_MES_INI"));
                    final Short adeIncMargem = !TextHelper.isNull(next.getAttribute("ADE_INC_MARGEM")) ? Short.valueOf(next.getAttribute("ADE_INC_MARGEM").toString()) : CodedValues.INCIDE_MARGEM_SIM;
                    final Short adeIntFolha = !TextHelper.isNull(next.getAttribute("ADE_INT_FOLHA")) ? Short.valueOf(next.getAttribute("ADE_INT_FOLHA").toString()) : CodedValues.INTEGRA_FOLHA_SIM;

                    // Através dos dados da tabela temporária, insere novos contratos de correção
                    // de saldo devedor, com valores de saldo iguais a zero.
                    final AutDesconto adeBean = AutDescontoHome.create(sadCodigo, vcoCodigo, rseCodigo, null, usuCodigo,
                            "", null, adeCodReg, null, 0, adeAnoMesIni, null, adeAnoMesIni, null, adeVlr,
                            null, null, null, null, null, null, adeTipoVlr, adeIntFolha, adeIncMargem,
                            null, null, null, adeVlrSdo, adeVlrSdo, null, null, null, null, null, periodicidade, null);

                    final String adeCodigoDestino = adeBean.getAdeCodigo();
                    LOG.debug("CONTRATO DE CORRECAO DE SALDO DEVEDOR: " + adeCodigoDestino);

                    // Cria as ocorrências de inserção de contratos de correção
                    OcorrenciaAutorizacaoHome.create(adeCodigoDestino, tocCodigo, usuCodigo, ocaObs, null, null, responsavel.getIpUsuario(), null, periodoAtual, null);

                    // Insere o relacionamento de correção de saldo devedor entre os
                    // dois contratos: o principal (origem) e o de correção (destino)
                    RelacionamentoAutorizacaoHome.create(adeCodigoOrigem, adeCodigoDestino, tntCodigo, usuCodigo);
                }
            }
        } catch (final NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException("mensagem.erro.retorno.saldo.devedor.parametros.ausentes", responsavel, ex);
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException("mensagem.erro.retorno.saldo.devedor.erro.execucao", responsavel, ex);
        } catch (final CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ImpRetornoControllerException("mensagem.erro.retorno.saldo.devedor.erro.criar.contratos", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstHistoricoConclusaoRetorno(String orgCodigo, int qtdeMesesPesquisa, String periodo, AcessoSistema responsavel) throws ImpRetornoControllerException {
        return lstHistoricoConclusaoRetorno(orgCodigo, qtdeMesesPesquisa, periodo, false, responsavel);
    }

    /**
     * Recupera o histórico de conclusão de retorno.
     * @param orgCodigo Órgão a ser pesquisado.
     * @param qtdeMesesPesquisa Quantidade de meses de histórico.
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    @Override
    public List<TransferObject> lstHistoricoConclusaoRetorno(String orgCodigo, int qtdeMesesPesquisa, String periodo, boolean ordemDescrescente, AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            final ListaHistoricoConclusaoRetornoQuery query = new ListaHistoricoConclusaoRetornoQuery(qtdeMesesPesquisa, periodo);
            query.orgCodigo = orgCodigo;
            query.ordemDescrescente = ordemDescrescente;

            return query.executarDTO();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Cria uma ocorrencia de parcela para uma parcela específica
     * @param prdCodigo
     * @param tocCodigo
     * @param ocpObs
     * @param atrasado
     * @param responsavel
     * @throws CreateException
     */
    private void criarOcorrenciaParcela(Integer prdCodigo, String tocCodigo, String ocpObs, boolean atrasado, AcessoSistema responsavel) throws CreateException {
        final String usuCodigo = (responsavel != null ? responsavel.getUsuCodigo() : CodedValues.USU_CODIGO_SISTEMA);
        if (atrasado) {
            OcorrenciaParcelaHome.create(prdCodigo, tocCodigo, ocpObs, usuCodigo);
        } else {
            OcorrenciaParcelaPeriodoHome.create(prdCodigo, tocCodigo, ocpObs, usuCodigo);
        }
    }

    /**
     * Processa as linhas conm informação de férias.
     * @param linhasSemProcessamento
     * @param adeTipoEnvio
     * @param adeCodigosAlteracao
     * @param adeCodigosLiquidacao
     * @param camposChaveIdent
     * @param ordemExcCamposChave
     * @param tipoImportacaoRetorno
     * @param exportaMensal
     * @param consolida
     * @param retDAO
     * @param prdDAO
     * @param responsavel
     * @throws ImpRetornoControllerException
     */
    @SuppressWarnings("java:S107")
    private void executarProcessamentoFerias(Map<String, Map<String, Object>> linhasSemProcessamento, HashMap<String, String> adeTipoEnvio,
            List<String> adeCodigosAlteracao, List<String> adeCodigosLiquidacao, List<String> camposChaveIdent,
            String[] ordemExcCamposChave, int tipoImportacaoRetorno, boolean exportaMensal, boolean consolida,
            ImpRetornoDAO retDAO, ParcelaDescontoDAO prdDAO, ImpRetornoControllerConf conf, AcessoSistema responsavel) throws ImpRetornoControllerException {
        if (linhasSemProcessamento.size() > 0) {
            // 4.1. PAGA PARCELAS CUJO VALOR É O QUE ESTÁ NO ARQUIVO DE RETORNO (DESCONTO TOTAL)
            LOG.debug("FASE 4.1: " + DateHelper.getSystemDatetime());
            pagaParcelasDescontoTotalFerias(linhasSemProcessamento, adeCodigosAlteracao, adeCodigosLiquidacao, adeTipoEnvio,
                                            camposChaveIdent, tipoImportacaoRetorno, exportaMensal, retDAO, responsavel);
            LOG.debug(AD_ES_ALTERADAS + (adeCodigosAlteracao.size() - conf.adeAlteradasAnterior));
            LOG.debug(AD_ES_LIQUIDADAS + (adeCodigosLiquidacao.size() - conf.adeLiquidadasAnterior));
            LOG.debug(LINHAS_NAO_PROCESSADAS + linhasSemProcessamento.size());
            conf.adeAlteradasAnterior = adeCodigosAlteracao.size();
            conf.adeLiquidadasAnterior = adeCodigosLiquidacao.size();
            LOG.debug("FIM FASE 4.1: " + DateHelper.getSystemDatetime());
        }

        if (linhasSemProcessamento.size() > 0) {
            // 4.2. PAGA PARCELAS CUJO VALOR CONSOLIDADO É O DESCONTO TOTAL DO RETORNO.
            LOG.debug("FASE 4.2: " + DateHelper.getSystemDatetime());
            if (consolida) {
                // Seleciona das linhas sem processamento, somente aquelas com possibilidade de pagamento
                // exato consolidado.
                pagaParcelasDescontoTotalConsolidadoFerias(linhasSemProcessamento,
                                                           adeCodigosAlteracao, adeCodigosLiquidacao, adeTipoEnvio, camposChaveIdent,
                                                           tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_CRITICA,
                                                           exportaMensal, retDAO, prdDAO, responsavel);
            } else {
                LOG.debug("Sistema não consolida.");
            }
            LOG.debug(AD_ES_ALTERADAS + (adeCodigosAlteracao.size() - conf.adeAlteradasAnterior));
            LOG.debug(AD_ES_LIQUIDADAS + (adeCodigosLiquidacao.size() - conf.adeLiquidadasAnterior));
            LOG.debug(LINHAS_NAO_PROCESSADAS + linhasSemProcessamento.size());
            conf.adeAlteradasAnterior = adeCodigosAlteracao.size();
            conf.adeLiquidadasAnterior = adeCodigosLiquidacao.size();
            LOG.debug("FIM FASE 4.2: " + DateHelper.getSystemDatetime());
        }

        if (linhasSemProcessamento.size() > 0) {
            // 4.3. TENTA ENCONTRAR QUALQUER ADE/PARCELA QUE POSSA SER PAGA
            LOG.debug("FASE 4.3: " + DateHelper.getSystemDatetime());
            pagaParcelasParciaisFerias(linhasSemProcessamento, adeTipoEnvio, adeCodigosAlteracao, adeCodigosLiquidacao,
                                       camposChaveIdent, ordemExcCamposChave, tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_CRITICA,
                                       exportaMensal, consolida, retDAO, prdDAO, responsavel);
            LOG.debug(AD_ES_ALTERADAS + (adeCodigosAlteracao.size() - conf.adeAlteradasAnterior));
            LOG.debug(AD_ES_LIQUIDADAS + (adeCodigosLiquidacao.size() - conf.adeLiquidadasAnterior));
            LOG.debug(LINHAS_NAO_PROCESSADAS + linhasSemProcessamento.size());
            conf.adeAlteradasAnterior = adeCodigosAlteracao.size();
            conf.adeLiquidadasAnterior = adeCodigosLiquidacao.size();
            LOG.debug("FIM FASE 4.3: " + DateHelper.getSystemDatetime());
        }
    }

    /**
     *
     * @param linhasSemProcessamento
     * @param adeCodigosAlteracao
     * @param adeCodigosLiquidacao
     * @param adeTipoEnvio
     * @param camposChaveIdent
     * @param tipoImportacaoRetorno
     * @param exportaMensal
     * @param retDAO
     * @param responsavel
     * @throws ImpRetornoControllerException
     */
    @SuppressWarnings("java:S107")
    private void pagaParcelasDescontoTotalFerias(Map<String, Map<String, Object>> linhasSemProcessamento, List<String> adeCodigosAlteracao,
            List<String> adeCodigosLiquidacao, HashMap<String, String> adeTipoEnvio, List<String> camposChaveIdent,
            int tipoImportacaoRetorno, boolean exportaMensal,
            ImpRetornoDAO retDAO, AcessoSistema responsavel) throws ImpRetornoControllerException {
        final CustomTransferObject criterioDescontoTotal = new CustomTransferObject();

        final Iterator<Map.Entry<String, Map<String, Object>>> itLinha = linhasSemProcessamento.entrySet().iterator();
        Map<String, Object> entrada = null;
        // Pega uma linha do mapa para determinar quais chaves compõe o arquivo de retorno.
        if (itLinha.hasNext()) {
            final Map.Entry<String, Map<String, Object>> entry = itLinha.next();
            entrada = entry.getValue();
        }
        if (entrada != null) {
            for (final String atributo : entrada.keySet()) {
                if (!SPD_CODIGOS.equalsIgnoreCase(atributo) &&
                    !ANO_MES_DESCONTO.equalsIgnoreCase(atributo) &&
                    !PRD_VLR_PREVISTO.equalsIgnoreCase(atributo) &&
                    camposChaveIdent.contains(atributo)) {
                    criterioDescontoTotal.setAttribute(atributo, "S");
                }
            }
        }
        // Não precisa dos critérios abaixo.
        criterioDescontoTotal.setAttribute(SPD_CODIGOS, "N");
        criterioDescontoTotal.setAttribute(ANO_MES_DESCONTO, "N");
        criterioDescontoTotal.setAttribute(PRD_VLR_PREVISTO, "N");

        try {
            retDAO.criaTabelaParcelasRetornoFerias(camposChaveIdent, criterioDescontoTotal, tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_ATRASADO);
            retDAO.selecionaParcelasPagamentoExatoFerias();
            retDAO.pagaParcelasSelecionadasDescontoTotalFerias(responsavel);
            retDAO.associarLinhaRetornoParcelaExata(true);
            // Salva lista de ADEs alteradas, a lista de tipos de envio, se for o caso, e atualiza linhas sem processamento.
            retDAO.getAdeCodigosAlteracao(linhasSemProcessamento, adeCodigosAlteracao, adeTipoEnvio, exportaMensal,
                                          tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_ATRASADO, tipoImportacaoRetorno == CodedValues.TIPO_RETORNO_CRITICA, true);
            // Salva lista de ADEs liquidadas.
            retDAO.getAdeCodigosLiquidacao(adeCodigosLiquidacao, true);
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Executa pagamento total consolidado de parcelas de férias
     * @param linhasSemProcessamento
     * @param adeCodigosAlteracao
     * @param adeCodigosLiquidacao
     * @param adeTipoEnvio
     * @param camposChaveIdent
     * @param critica
     * @param atrasado
     * @param exportaMensal
     * @param retDAO
     * @param prdDAO
     * @param responsavel
     * @throws ImpRetornoControllerException
     */
    @SuppressWarnings("java:S107")
    private void pagaParcelasDescontoTotalConsolidadoFerias(Map<String, Map<String, Object>> linhasSemProcessamento,
            List<String> adeCodigosAlteracao, List<String> adeCodigosLiquidacao,
            HashMap<String, String> adeTipoEnvio, List<String> camposChaveIdent,
            boolean critica, boolean exportaMensal,
            ImpRetornoDAO retDAO, ParcelaDescontoDAO prdDAO, AcessoSistema responsavel) throws ImpRetornoControllerException {
        final List<String> cci = new ArrayList<>(camposChaveIdent);
        cci.remove(PRD_VLR_PREVISTO);

        boolean pagouTodas;
        TransferObject criterio;
        TransferObject parcela;
        List<TransferObject> parcelas;
        String adeCodigo;
        String adeTipoVlr;
        Integer prdNumero;
        Integer prdNumero1;
        Integer prdNumero2;
        BigDecimal prdVlrPrevisto;
        final List<String> linhasPagas = new ArrayList<>();

        final Map<String, Map<String, Object>> linhasPagamentoConsolidado = buscaLinhasConsolidacaoExata(linhasSemProcessamento, true, false, false, retDAO, responsavel);
        LOG.debug("Linhas candidatas a pagamento total consolidado = " + linhasPagamentoConsolidado.size());
        String numLinha;
        int linha = 0;
        try {
            // DESENV-10310 : Faz a limpeza da session (por causa da incapacidade do hibernate).
            final BatchManager batman = new BatchManager(SessionUtil.getSession());
            for (final Entry<String, Map<String, Object>> entry : linhasPagamentoConsolidado.entrySet()) {
                numLinha = entry.getKey();
                pagouTodas = false;
                criterio = new CustomTransferObject();
                criterio.setAtributos(entry.getValue());
                parcelas = prdDAO.getPrdProcessamentoFerias(cci, criterio);
                if ((parcelas != null) && !parcelas.isEmpty()) {
                    // Vê se a soma das parcelas é igual ao valor passado
                    Iterator<TransferObject> itp = parcelas.iterator();
                    BigDecimal soma = new BigDecimal("0");
                    while (itp.hasNext()) {
                        parcela = itp.next();
                        soma = soma.add(new BigDecimal(parcela.getAttribute(PRD_VLR_PREVISTO).toString()));
                    }
                    final BigDecimal vlrRealizado = new BigDecimal(criterio.getAttribute(PRD_VLR_REALIZADO).toString());
                    // processa somente se todas as parcelas foram pagas
                    if (vlrRealizado.compareTo(soma) == 0) {
                        pagouTodas = true;
                        itp = parcelas.iterator();
                        while (itp.hasNext()) {
                            parcela = itp.next();
                            adeCodigo = parcela.getAttribute(ADE_CODIGO).toString();
                            adeTipoVlr = (parcela.getAttribute(ADE_TIPO_VLR) != null ? parcela.getAttribute(ADE_TIPO_VLR).toString() : CodedValues.TIPO_VLR_FIXO);
                            prdVlrPrevisto = new BigDecimal(parcela.getAttribute(PRD_VLR_PREVISTO).toString());
                            prdNumero1 = Integer.valueOf(TextHelper.isNull(parcela.getAttribute(PRD_NUMERO_1)) ? "0" : parcela.getAttribute(PRD_NUMERO_1).toString());
                            prdNumero2 = Integer.valueOf(TextHelper.isNull(parcela.getAttribute(PRD_NUMERO_2)) ? "0" : parcela.getAttribute(PRD_NUMERO_2).toString());
                            prdNumero = (prdNumero1.compareTo(prdNumero2) < 0) ? prdNumero2 : prdNumero1;
                            retornoADEParcelaFerias(exportaMensal, critica, adeCodigo, prdNumero, adeTipoVlr, prdVlrPrevisto, prdVlrPrevisto, numLinha,
                                                    criterio.getAtributos(), adeTipoEnvio, adeCodigosAlteracao,
                                                    adeCodigosLiquidacao, retDAO, responsavel);
                        }
                    }
                }
                if (pagouTodas) {
                    linhasPagas.add(numLinha);
                }
                if ((++linha % 1000) == 0) {
                    LOG.debug(LINHAS_LIDAS + linha);
                }
                // DESENV-10310 : Faz a limpeza da session (por causa da incapacidade do hibernate).
                batman.iterate();
            }

            LOG.debug(TOTAL_DE_LINHAS_LIDAS + linha);
            // Remove da lista de linhas sem processamento aquelas que foram pagas.
            final Iterator<String> it2 = linhasPagas.iterator();
            while (it2.hasNext()){
                numLinha = it2.next();
                linhasSemProcessamento.remove(numLinha);
                retDAO.marcaLinhaConsolidadaComoProcessada(numLinha);
            }
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Realiza a atualização da parcela paga em retorno de férias.
     * @param exportaMensal
     * @param critica
     * @param adeCodigo
     * @param prdNumero
     * @param prdVlrPrevisto
     * @param prdVlrRealizado
     * @param numLinha
     * @param entrada
     * @param adeTipoEnvio
     * @param adeCodigosAlteracao
     * @param adeCodigosLiquidacao
     * @param retDAO
     * @param prdDAO
     * @param responsavel
     * @throws ImpRetornoControllerException
     */
    @SuppressWarnings("java:S107")
    private void retornoADEParcelaFerias(boolean exportaMensal, boolean critica,  String adeCodigo, Integer prdNumero, String adeTipoVlr,
            BigDecimal prdVlrPrevisto, BigDecimal prdVlrRealizado, String numLinha,
            Map<String, Object> entrada, HashMap<String, String> adeTipoEnvio,
            List<String> adeCodigosAlteracao, List<String> adeCodigosLiquidacao,
            ImpRetornoDAO retDAO, AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            final Date prdDataDesconto  = DateHelper.toSQLDate(DateHelper.parse((String) entrada.get(PERIODO), YYYY_MM_DD));
            final Date prdDataRealizado = DateHelper.toSQLDate(DateHelper.parse((String) entrada.get(PRD_DATA_REALIZADO), YYYY_MM_DD));
            final String ocpObs         = (String) entrada.get(OCP_OBS);
            final String situacao       = (String) entrada.get(SITUACAO);
            final String tdeCodigo      = (String) entrada.get("TDE_CODIGO");
            String tipoEnvio      = (String) entrada.get("TIPO_ENVIO");
            final boolean quitacao = ("Q".equals(situacao));
            final String spdCodigo = ("I".equals(situacao)) ? CodedValues.SPD_REJEITADAFOLHA : CodedValues.SPD_LIQUIDADAFOLHA;
            tipoEnvio = (tipoEnvio == null) ? "I" : tipoEnvio;

            // A ocorrência de retorno parcial deve ser grava somente quando o tipo valor for fixo
            final String tocCodigo = ((prdVlrPrevisto.compareTo(prdVlrRealizado) > 0) && CodedValues.SPD_LIQUIDADAFOLHA.equals(spdCodigo)  && CodedValues.TIPO_VLR_FIXO.equals(adeTipoVlr) ? CodedValues.TOC_RETORNO_PARCIAL_FERIAS : CodedValues.TOC_RETORNO_FERIAS);

            // Se a parcela de férias a ser criada está com número Zero, significa que o
            // contrato não possui parcelas nem na tabela história, nem na tabela do período
            if ((prdNumero == null) || (prdNumero == 0)) {
                prdNumero = 1;
            }

            // Criar a parcela
            final ParcelaDesconto prdBean = ParcelaDescontoHome.create(adeCodigo, prdNumero.shortValue(), tdeCodigo, spdCodigo, prdDataDesconto, prdDataRealizado, prdVlrPrevisto, prdVlrRealizado);
            // Cria a ocorrência de retorno
            criarOcorrenciaParcela(prdBean.getPrdCodigo(), tocCodigo, ocpObs, true, responsavel);

            // Associa a linha utilizada para pagar a parcela à parcela+contrato
            retDAO.associarLinhaRetornoParcela(adeCodigo, prdNumero.shortValue(), prdDataDesconto, Integer.parseInt(numLinha));

            // Executa ações para esta ADE
            if (ParamSist.paramEquals(CodedValues.TPC_EXECUTAR_ACOES_POR_TIPO_DESCONTO_IMPORTACAO_RETORNO, CodedValues.TPC_SIM, responsavel)) {
                executarAcoesTipoDesconto(adeCodigo, tdeCodigo, responsavel);
            }

            // Guarda os códigos das autorizações para posteriormente modificar
            // os campos sad_codigo e ade_pagas, ou inserir ocorrências de relançamento
            if (!exportaMensal && critica) {
                adeTipoEnvio.put(adeCodigo, tipoEnvio);
            } else if (CodedValues.SPD_LIQUIDADAFOLHA.equals(spdCodigo)) {
                adeCodigosAlteracao.add(adeCodigo);
            }
            if (quitacao) {
                // Se o status indica uma liquidação de contrato, coloca o código da autorização
                // em uma lista para depois realizar a liquidação
                adeCodigosLiquidacao.add(adeCodigo);
            }
        } catch (DAOException | CreateException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Executa pagamento parcial de parcelas de férias
     * @param linhasSemProcessamento
     * @param adeTipoEnvio
     * @param adeCodigosAlteracao
     * @param adeCodigosLiquidacao
     * @param camposChaveIdent
     * @param ordemExcCamposChave
     * @param critica
     * @param atrasado
     * @param exportaMensal
     * @param consolida
     * @param retDAO
     * @param prdDAO
     * @param responsavel
     * @throws ImpRetornoControllerException
     */
    @SuppressWarnings("java:S107")
    private void pagaParcelasParciaisFerias(Map<String, Map<String, Object>> linhasSemProcessamento, HashMap<String, String> adeTipoEnvio,
            List<String> adeCodigosAlteracao, List<String> adeCodigosLiquidacao,
            List<String> camposChaveIdent, String[] ordemExcCamposChave, boolean critica,
            boolean exportaMensal, boolean consolida,
            ImpRetornoDAO retDAO, ParcelaDescontoDAO prdDAO, AcessoSistema responsavel) throws ImpRetornoControllerException {
        List<TransferObject> parcelas;
        TransferObject criterio;
        TransferObject parcela;
        String adeCodigo;
        String adeTipoVlr;
        String prdVlrRealizado;
        Integer prdNumero;
        Integer prdNumero1;
        Integer prdNumero2;
        BigDecimal prdVlrPrevisto;

        final List<String> linhasPagas = new ArrayList<>();
        final List<String> cci = new ArrayList<>();
        String numLinha;
        int linha = 0;
        try {
            // DESENV-10310 : Faz a limpeza da session (por causa da incapacidade do hibernate).
            final BatchManager batman = new BatchManager(SessionUtil.getSession());
            for (final Entry<String, Map<String, Object>> entry : linhasSemProcessamento.entrySet()) {
                numLinha = entry.getKey();
                // processa somente se for linha de desconto de férias
                if ("1".equals(entry.getValue().get(ART_FERIAS))) {
                    cci.clear();
                    cci.addAll(camposChaveIdent);
                    criterio = new CustomTransferObject();
                    criterio.setAtributos(entry.getValue());
                    boolean encontrou = false;
                    int i = 0;
                    String campo;
                    // TENTA ENCONTRAR UMA PARCELA COM MENOS RIGOR NA CHAVE DE IDENTIFICAÇÃO
                    BigDecimal vlrRetorno = new BigDecimal(criterio.getAttribute(PRD_VLR_REALIZADO).toString());
                    while (!encontrou && (i <= ordemExcCamposChave.length)) {
                        parcelas = prdDAO.getPrdProcessamentoFerias(cci, criterio);
                        if ((parcelas != null) && !parcelas.isEmpty()) {
                            final Iterator<TransferObject> itPar = parcelas.iterator();
                            /*
                             * Itera sobre a lista de parcelas retornadas, podendo fazer pagamentos parciais.
                             * Recupera o valor enviado no arquivo, vlr_retorno, e compara com o valor previsto, vlr_previsto.
                             * Se a lista possuir apenas uma parcela, esta será paga em totalidade com o vlr_retorno.
                             * Se a lista possuir mais de uma parcela, compara o valor previsto com o valor do retorno.
                             * Se o valor do retorno é menor ou igual ao valor previsto, paga somente uma parcela e sai do loop.
                             * Se o valor do retorno é maior que o valor previsto, paga a parcela e calcula o resíduo,
                             * continua pagando as parcela enquanto tiver resíduo ou enquanto não for a última parcela,
                             * sendo que esta receberá o valor integral do resíduo, podendo ser maior ou menor
                             * do que o esperado.
                             */

                            String statusRetornoParcela = null;
                            boolean haMaisParcelasParaProcessar = itPar.hasNext();
                            while (haMaisParcelasParaProcessar) {
                                parcela = itPar.next();
                                final BigDecimal vlrPrevisto = new BigDecimal(parcela.getAttribute(PRD_VLR_PREVISTO).toString());

                                if (!itPar.hasNext()) {
                                    // Se existe uma só parcela.
                                    prdVlrRealizado = String.valueOf(vlrRetorno);
                                    haMaisParcelasParaProcessar = false;
                                } else if (vlrRetorno.doubleValue() <= vlrPrevisto.doubleValue()) {
                                    // Se o valor de retorno é menor que o valor da parcela.
                                    prdVlrRealizado = String.valueOf(vlrRetorno);
                                    vlrRetorno = new BigDecimal("0");

                                    /*
                                     * Se o sistema não consolida ou se o retorno não foi de rejeite, então
                                     * não processa mais nenhuma parcela. No caso contrário, todas as parcelas
                                     * encontradas terão o retorno de rejeite registrado.
                                     */
                                    statusRetornoParcela = (String) criterio.getAttribute(SITUACAO);
                                    if (!consolida || (statusRetornoParcela == null) || !"I".equals(statusRetornoParcela)) {
                                        haMaisParcelasParaProcessar = false;
                                    }
                                } else {
                                    // Se o valor remanescente de retorno for maior que o valor da parcela.
                                    prdVlrRealizado = String.valueOf(vlrPrevisto);
                                    vlrRetorno = vlrRetorno.subtract(vlrPrevisto);
                                }
                                adeCodigo = parcela.getAttribute(ADE_CODIGO).toString();
                                adeTipoVlr = (parcela.getAttribute(ADE_TIPO_VLR) != null ? parcela.getAttribute(ADE_TIPO_VLR).toString() : CodedValues.TIPO_VLR_FIXO);
                                prdVlrPrevisto = new BigDecimal(parcela.getAttribute(PRD_VLR_PREVISTO).toString());
                                prdNumero1 = Integer.valueOf(TextHelper.isNull(parcela.getAttribute(PRD_NUMERO_1)) ? "0" : parcela.getAttribute(PRD_NUMERO_1).toString());
                                prdNumero2 = Integer.valueOf(TextHelper.isNull(parcela.getAttribute(PRD_NUMERO_2)) ? "0" : parcela.getAttribute(PRD_NUMERO_2).toString());
                                prdNumero = (prdNumero1.compareTo(prdNumero2) < 0) ? prdNumero2 : prdNumero1;
                                if (TextHelper.isNull(entry.getValue().get(PERIODO))) {
                                    final String periodoFerias = DateHelper.format((java.util.Date) parcela.getAttribute("PRD_DATA_DESCONTO"), YYYY_MM_DD);
                                    criterio.setAttribute(PERIODO, periodoFerias);
                                }
                                retornoADEParcelaFerias(exportaMensal, critica, adeCodigo, prdNumero, adeTipoVlr, prdVlrPrevisto, new BigDecimal(prdVlrRealizado), numLinha,
                                                        criterio.getAtributos(), adeTipoEnvio, adeCodigosAlteracao,
                                                        adeCodigosLiquidacao, retDAO, responsavel);
                            }
                            encontrou = true;
                        }
                        if (!encontrou && (i < ordemExcCamposChave.length)) {
                            campo = ordemExcCamposChave[i];
                            cci.remove(campo);
                        }
                        i++;
                    }
                    if (encontrou) {
                        linhasPagas.add(numLinha);
                    }
                    if ((++linha % 1000) == 0) {
                        LOG.debug(LINHAS_LIDAS + linha);
                    }
                }
                // DESENV-10310 : Faz a limpeza da session (por causa da incapacidade do hibernate).
                batman.iterate();
            }

            LOG.debug(TOTAL_DE_LINHAS_LIDAS + linha);
            // Remove da lista de linhas sem processamento aquelas que foram pagas.
            final Iterator<String> itPagas = linhasPagas.iterator();
            while (itPagas.hasNext()) {
                numLinha = itPagas.next();
                linhasSemProcessamento.remove(numLinha);
                retDAO.marcaLinhaConsolidadaComoProcessada(numLinha);
            }
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    private void concluiContratosAguardLiquidCompra(AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            compraController.concluiContratosAguardLiquidCompra(responsavel);
        } catch (final CompraContratoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException("mensagem.erro.executar.conclusao.contratos.aguardando.liquidacao.compra", responsavel, ex);
        }
    }

    @Override
    public void concluiDespesasComum(String periodoRetorno, AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_SDP, CodedValues.TPC_SIM, responsavel)) {
                LOG.debug("*************************************************************************************");
                LOG.debug("INICIO CONCLUSAO DE DESPESA COMUM");
                // Realiza a conclusão das despesas comuns
                final DespesaComumDAO decDAO = DAOFactory.getDAOFactory().getDespesaComumDAO();
                decDAO.concluirDespesasComum(periodoRetorno, responsavel);
                LOG.debug("FIM CONCLUSAO DE DESPESA COMUM");
                LOG.debug("*************************************************************************************");
            }
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException("mensagem.erro.retorno.concluir.despesa.comum", responsavel, ex);
        }
    }

    @Override
    public void cancelaRelacionamentosInsereAltera(AcessoSistema responsavel) throws ImpRetornoControllerException {
        final ListaConsignacaoInsereAlteraAConcluirQuery lstInsAltQuery = new ListaConsignacaoInsereAlteraAConcluirQuery();
        try {
            final List<TransferObject> lstInsAlt = lstInsAltQuery.executarDTO();
            for (final TransferObject adeTO : lstInsAlt) {
                cancelarConsignacaoController.cancelar((String) adeTO.getAttribute(Columns.RAD_ADE_CODIGO_DESTINO), responsavel);
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        } catch (final AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(ex.getMessageKey(), responsavel, ex);
        }
    }

    private void liquidaConsignacaoParaQuitacao(List<String> adeCodigos, AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            if ((adeCodigos != null) && !adeCodigos.isEmpty()) {
                final Iterator<String> it = adeCodigos.iterator();
                String adeCodigo = null;

                while (it.hasNext()) {
                    adeCodigo = it.next();
                    LOG.debug("LIQUIDANDO ADE: " + adeCodigo);
                    liquidarController.liquidar(adeCodigo, null, null, responsavel);
                }
            }
        } catch (final AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException("mensagem.erro.retorno.liquidar.quitacao", responsavel, ex);
        }
    }

    @Override
    public void geraRelatorioIntegracaoSemMapeamentoXLS(String nomeArqSaida, String entradaImpRetorno, String tradutorImpRetorno, String pathSaida, AcessoSistema responsavel) {
        if (!TextHelper.isNull(nomeArqSaida) && (new File(nomeArqSaida).length() == 0L)) {
            return;
        }
        // Hash que recebe os dados do que serão lidos do arquivo de entrada
        final Map<String, Object> entrada = new HashMap<>();
        final List<TransferObject> dataSetList = new ArrayList<>();
        TransferObject dataSet = null;

        final LeitorArquivoTexto leitor = new LeitorArquivoTexto(entradaImpRetorno, nomeArqSaida);
        // Prepara tradução do arquivo de retorno.
        final Escritor escritor = new EscritorMemoria(entrada);
        final Tradutor tradutor = new Tradutor(tradutorImpRetorno, leitor, escritor);

        try {
            final DocumentoTipo documento = XmlHelper.unmarshal(new FileInputStream(tradutorImpRetorno));

            String[] camposRelatorio = null;
            String[] nomeCamposRelatorio = null;
            if (documento.getParametro() != null) {
                for (final ParametroTipo param : documento.getParametro()) {
                    if ("CAMPOS_RELATORIO".equalsIgnoreCase(param.getNome())) {
                        camposRelatorio = param.getValor().split(";");
                    } else if ("NOME_CAMPOS_RELATORIO".equalsIgnoreCase(param.getNome())) {
                        nomeCamposRelatorio = param.getValor().split(";");
                    }
                }
            }

            final List<MapeamentoTipo> atributos = documento.getMapeamento();

            tradutor.iniciaTraducao();
            while (tradutor.traduzProximo()) {
                final Iterator<MapeamentoTipo> atributosIterator = atributos.iterator();
                MapeamentoTipo atributo = null;
                dataSet = new CustomTransferObject();
                while(atributosIterator.hasNext()){
                    atributo = atributosIterator.next();
                    if ((camposRelatorio != null) && (nomeCamposRelatorio != null) && (camposRelatorio.length>0) && (camposRelatorio.length == nomeCamposRelatorio.length)) {
                        int i=0;
                        for (i=0; i<camposRelatorio.length; i++) {
                            if (atributo.getSaida().equalsIgnoreCase(camposRelatorio[i])) {
                                dataSet.setAttribute(nomeCamposRelatorio[i], entrada.get(atributo.getSaida()));
                                break;
                            }
                        }
                    } else {
                        dataSet.setAttribute(atributo.getSaida(), entrada.get(atributo.getSaida()));
                    }
                }
                dataSetList.add(dataSet);
            }

            tradutor.encerraTraducao();

            //pega as colunas que serao geradas no relatorio
            List<String> campos = new ArrayList<>();
            if ((camposRelatorio != null) && (nomeCamposRelatorio != null) && (camposRelatorio.length>0) && (camposRelatorio.length == nomeCamposRelatorio.length)) {
                campos = Arrays.asList(nomeCamposRelatorio);
            } else {
                final Iterator<MapeamentoTipo> atributosIterator = atributos.iterator();
                MapeamentoTipo atributo = null;
                while(atributosIterator.hasNext()){
                    atributo = atributosIterator.next();
                    campos.add(atributo.getSaida());
                }
            }

            final Relatorio relatorio = ConfigRelatorio.getInstance().getRelatorio("integracao_sem_mapeamento");
            final Map<String, String[]> parameterMap = new HashMap<>();
            parameterMap.put(ReportManager.REPORT_DIR_EXPORT, new String[] {pathSaida});

            final ProcessaRelatorioIntegracaoSemMapeamento processaRelatorioIntegracaoSemMapeamento = new ProcessaRelatorioIntegracaoSemMapeamento(relatorio, parameterMap, campos, dataSetList, null, responsavel);
            processaRelatorioIntegracaoSemMapeamento.run();
        } catch (ParserException | FileNotFoundException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void geraRelatorioIntegracaoSemProcessamentoXLS(String nomeArqSaida, String entradaImpRetorno, String tradutorImpRetorno, String pathSaida, AcessoSistema responsavel) {
        // Hash que recebe os dados do que serão lidos do arquivo de entrada
        final Map<String, Object> entrada = new HashMap<>();
        final List<TransferObject> dataSetList = new ArrayList<>();
        TransferObject dataSet = null;

        final LeitorArquivoTexto leitor = new LeitorArquivoTexto(entradaImpRetorno, nomeArqSaida);
        // Prepara tradução do arquivo de retorno.
        final Escritor escritor = new EscritorMemoria(entrada);
        final Tradutor tradutor = new Tradutor(tradutorImpRetorno, leitor, escritor);

        try {
            final DocumentoTipo documento = XmlHelper.unmarshal(new FileInputStream(tradutorImpRetorno));

            String[] camposRelatorio = null;
            String[] nomeCamposRelatorio = null;
            if (documento.getParametro() != null) {
                for (final ParametroTipo param : documento.getParametro()) {
                    if ("CAMPOS_RELATORIO".equalsIgnoreCase(param.getNome())) {
                        camposRelatorio = param.getValor().split(";");
                    } else if ("NOME_CAMPOS_RELATORIO".equalsIgnoreCase(param.getNome())) {
                        nomeCamposRelatorio = param.getValor().split(";");
                    }
                }
            }

            final List<MapeamentoTipo> atributos = documento.getMapeamento();

            tradutor.iniciaTraducao();
            while (tradutor.traduzProximo()) {
                final Iterator<MapeamentoTipo> atributosIterator = atributos.iterator();
                MapeamentoTipo atributo = null;
                dataSet = new CustomTransferObject();
                while(atributosIterator.hasNext()){
                    atributo = atributosIterator.next();
                    if ((camposRelatorio != null) && (nomeCamposRelatorio != null) && (camposRelatorio.length>0) && (camposRelatorio.length == nomeCamposRelatorio.length)) {
                        int i=0;
                        for (i=0; i<camposRelatorio.length; i++) {
                            if (atributo.getSaida().equalsIgnoreCase(camposRelatorio[i])) {
                                dataSet.setAttribute(nomeCamposRelatorio[i], entrada.get(atributo.getSaida()));
                                break;
                            }
                        }
                    } else {
                        dataSet.setAttribute(atributo.getSaida(), entrada.get(atributo.getSaida()));
                    }
                }
                dataSetList.add(dataSet);
            }

            tradutor.encerraTraducao();

            //pega as colunas que serao geradas no relatorio
            List<String> campos = new ArrayList<>();
            if ((camposRelatorio != null) && (nomeCamposRelatorio != null) && (camposRelatorio.length>0) && (camposRelatorio.length == nomeCamposRelatorio.length)) {
                campos = Arrays.asList(nomeCamposRelatorio);
            } else {
                final Iterator<MapeamentoTipo> atributosIterator = atributos.iterator();
                MapeamentoTipo atributo = null;
                while(atributosIterator.hasNext()){
                    atributo = atributosIterator.next();
                    campos.add(atributo.getSaida());
                }
            }

            final Relatorio relatorio = ConfigRelatorio.getInstance().getRelatorio("integracao_sem_processamento");
            final Map<String, String[]> parameterMap = new HashMap<>();
            parameterMap.put(ReportManager.REPORT_DIR_EXPORT, new String[] {pathSaida});

            final ProcessaRelatorioIntegracaoSemProcessamento processaRelatorioIntegracaoSemProcessamento = new ProcessaRelatorioIntegracaoSemProcessamento(relatorio, parameterMap, campos, dataSetList, null, responsavel);
            processaRelatorioIntegracaoSemProcessamento.run();
        } catch (ParserException | FileNotFoundException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Obtém o agrupamento das ocorrências de parcela de retorno do período
     * informado e dos subsequentes para listar as opções de desfazer retorno.
     * @param ultPeriodoRetorno
     * @param orgCodigo
     * @param estCodigo
     * @param responsavel
     * @return
     * @throws ImpRetornoControllerException
     */
    @Override
    public List<TransferObject> getHistoricoParcelasAgrupado(java.util.Date ultPeriodoRetorno, String orgCodigo, String estCodigo, AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            final ListaOcorrenciaParcelaAgrupadaQuery query = new ListaOcorrenciaParcelaAgrupadaQuery();
            query.prdDataDesconto = ultPeriodoRetorno;
            query.estCodigo = estCodigo;
            query.orgCodigo = orgCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            throw new ImpRetornoControllerException(ex);
        }
    }

    /**
     * Verifica se as consignatárias bloqueadas por saldo devedor podem ser desbloqueadas na conclusão do retorno.
     * Usado para desbloqueio de consignatárias que tiveram os contratos com pendência de saldo devedor concluídos.
     * @param responsavel Responsavel pela operacao
     * @return void
     * @throws ImpRetornoControllerException
     */
    private void verificarDesbloqueioAutomaticoSaldoDevedorConsignataria(AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            final ListaCsaComOcorrenciaSaldoDevedorQuery query = new ListaCsaComOcorrenciaSaldoDevedorQuery();
            final Iterator<ConsignatariaTransferObject> it = query.executarDTO(ConsignatariaTransferObject.class).iterator();
            ConsignatariaTransferObject csa = null;
            while (it.hasNext()) {
                csa = it.next();
                if (csa.getAttribute(Columns.CSA_CODIGO) != null) {
                    consignatariaController.verificarDesbloqueioAutomaticoConsignataria((String) csa.getAttribute(Columns.CSA_CODIGO), responsavel);
                }
            }
        } catch (HQueryException | ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    @Override
    public void agendarAtualizacaoBaseCentralCpf(AcessoSistema responsavel) {
        final String urlCentralizador = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_CENTRALIZADOR_MOBILE, responsavel);
        if (!TextHelper.isNull(urlCentralizador)) {
            final TransferObject to = new CustomTransferObject();
            to.setAttribute(Columns.AGD_TAG_CODIGO, TipoAgendamentoEnum.PERIODICO_DIARIO.getCodigo());
            to.setAttribute(Columns.AGD_DATA_PREVISTA, DateHelper.toDateString(DateHelper.addDays(DateHelper.getSystemDate(), 1)));
            to.setAttribute(Columns.AGD_JAVA_CLASS_NAME, AtualizaBaseCentralCpfJob.class.getName());
            to.setAttribute(Columns.AGD_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.agendamento.titulo.atualizacao.base.cpf.job", responsavel));

            final Map<String, List<String>> parametros = null;

            try {
                agendamentoController.insereAgendamento(to, parametros, 1, responsavel);
            } catch (final AgendamentoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void agendarArquivamentoServidor(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) {
        try {
            final Object objQtdDiasArquivarSerExcluido = ParamSist.getInstance().getParam(CodedValues.TPC_QTD_DIAS_ARQUIVAR_SERVIDOR_EXCLUIDO_IMPORTACAO_MARGEM, responsavel);
            int qtdDiasArquivarSerExcluido = 0;
            if (!TextHelper.isNull(objQtdDiasArquivarSerExcluido)) {
                qtdDiasArquivarSerExcluido = TextHelper.parseIntErrorSafe(objQtdDiasArquivarSerExcluido, 0);
            }

            if (qtdDiasArquivarSerExcluido > 0) {
                final TransferObject to = new CustomTransferObject();
                to.setAttribute(Columns.AGD_TAG_CODIGO, TipoAgendamentoEnum.PERIODICO_DIARIO.getCodigo());
                to.setAttribute(Columns.AGD_DATA_PREVISTA, DateHelper.toDateString(DateHelper.addDays(DateHelper.getSystemDate(), 1)));
                to.setAttribute(Columns.AGD_JAVA_CLASS_NAME, ArquivamentoServidoresJob.class.getName());
                to.setAttribute(Columns.AGD_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.agendamento.titulo.arquivamento.ser.job", responsavel));

                final Map<String, List<String>> parametros = new HashMap<>();

                if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
                    parametros.put(Columns.ORG_CODIGO, orgCodigos);
                }
                if ((estCodigos != null) && !estCodigos.isEmpty()) {
                    parametros.put(Columns.EST_CODIGO, estCodigos);
                }

                agendamentoController.insereAgendamento(to, parametros, 1, responsavel);
            }

        } catch (final AgendamentoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void atualizarCsaCodigoTbArqRetorno(AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            final ImpRetornoDAO retDAO = DAOFactory.getDAOFactory().getImpRetornoDAO();
            retDAO.atualizarCsaCodigoTbArqRetorno();
        } catch (final DAOException ex) {
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Ajusta o tipo de retorno que o arquivo representa analisando o campo PERIODO nas linhas, se este estiver presente
     * @param nomeArquivo
     * @param orgCodigo
     * @param estCodigo
     * @param tipo
     * @param responsavel
     * @return
     * @throws ImpRetornoControllerException
     */
    @Override
    public String ajustaTipoRetornoPeloPeriodo(String nomeArquivo, String orgCodigo, String estCodigo, String tipo, AcessoSistema responsavel) throws ImpRetornoControllerException {
        List<String> orgCodigos = null;
        List<String> estCodigos = null;
        if ((orgCodigo != null) && !"".equals(orgCodigo)) {
            orgCodigos = new ArrayList<>();
            orgCodigos.add(orgCodigo);
        }
        if ((estCodigo != null) && !"".equals(estCodigo)) {
            estCodigos = new ArrayList<>();
            estCodigos.add(estCodigo);
        }

        // Arquivos de configuração para importar retorno
        final Map<String, String> arquivosConfiguracao = buscaArquivosConfiguracao(nomeArquivo, tipo, estCodigo, orgCodigo, responsavel);
        String filePath = arquivosConfiguracao.get(NOME_ARQUIVO_ENTRADA);
        final String entradaImpRetorno = arquivosConfiguracao.get(ENTRADA_IMP_RETORNO);
        final String tradutorImpRetorno = arquivosConfiguracao.get(TRADUTOR_IMP_RETORNO);

        try {
            // Calcula o período, e recupera a data base (mês/ano)
            final java.util.Date periodoRetorno = DateHelper.parse(recuperaPeriodoRetorno(CodedValues.TIPO_RETORNO_NORMAL, null, orgCodigos, estCodigos, responsavel), YYYY_MM_DD);

            final java.util.Date ultimoPeriodoRetorno = getUltimoPeriodoRetorno(orgCodigo, estCodigo, responsavel);

            // Hash que recebe os dados do que serão lidos do arquivo de entrada
            final Map<String, Object> entrada = new HashMap<>();
            // Configura o leitor de acordo com o arquivo de entrada
            LeitorArquivoTexto leitor = null;
            if (nomeArquivo.toLowerCase().endsWith(".zip")) {
                leitor = new LeitorArquivoTextoZip(entradaImpRetorno, filePath);
            } else {
                leitor = new LeitorArquivoTexto(entradaImpRetorno, filePath);
            }
            // Prepara tradução do arquivo de retorno.
            final Escritor escritor = new EscritorMemoria(entrada);
            final Tradutor tradutor = new Tradutor(tradutorImpRetorno, leitor, escritor);

            final Set<java.util.Date> periodosNoRetorno = new HashSet<>();
            StringBuilder linhasIndeferidas = null;

            tradutor.iniciaTraducao();
            while (tradutor.traduzProximo()) {
                if (TextHelper.isNull(entrada.get(PERIODO))) {
                    return tipo;
                }

                if (!TextHelper.isNull(entrada.get(SITUACAO)) && "I".equals(entrada.get(SITUACAO))) {
                    if (linhasIndeferidas == null) {
                        linhasIndeferidas = new StringBuilder(leitor.getLinha());
                    } else {
                        linhasIndeferidas.append(System.lineSeparator()).append(leitor.getLinha());
                    }
                }

                periodosNoRetorno.add(DateHelper.parse((String) entrada.get(PERIODO), YYYY_MM_DD));
            }
            tradutor.encerraTraducao();

            final List<java.util.Date> periodosAtrasados = periodosNoRetorno.stream().filter(periodo -> periodo.before(periodoRetorno)).collect(Collectors.toList());
            final List<java.util.Date> periodosFerias = periodosNoRetorno.stream().filter(periodo -> periodo.compareTo(periodoRetorno) > 0).collect(Collectors.toList());

            final List<java.util.Date> periodoAtual = periodosNoRetorno.stream().filter(periodo -> periodo.compareTo(periodoRetorno) == 0 ).collect(Collectors.toList());


            // Se arquivo possui parcelas de períodos atrasados ou do período atual de retorno, este será processado como retorno atrasado caso o periodo de retorno já houver sido concluído.
            if (((periodosAtrasados != null) && !periodosAtrasados.isEmpty()) || ((periodoAtual != null) && !periodoAtual.isEmpty() && (periodoRetorno.compareTo(ultimoPeriodoRetorno) == 0))) {
                if ((periodosFerias == null) || periodosFerias.isEmpty()) {
                    if (ProcessaRetorno.CRITICA.equals(tipo)) {
                        if (linhasIndeferidas == null) {
                            throw new ImpRetornoControllerException("mensagem.erro.sem.linha.rejeitos.critica.atrasada", responsavel);
                        }

                        final String caminhoAbsoluto = new File(arquivosConfiguracao.get(NOME_ARQUIVO_ENTRADA)).getAbsolutePath();
                        final String absPath = caminhoAbsoluto.substring(0, caminhoAbsoluto.lastIndexOf(File.separator) + 1);
                        final String arqCriticaAtrasadoNome = absPath + nomeArquivo.substring(0, nomeArquivo.lastIndexOf(".")) + "_critica_atrasada_" +
                                                        (String) entrada.get(PERIODO) + nomeArquivo.substring(nomeArquivo.lastIndexOf("."), nomeArquivo.length());

                        // Renomear o arquivo de critica atrasada para .prc para este ser identificado pelo relatório de integração
                        final File arquivo = new File(caminhoAbsoluto);
                        arquivo.renameTo(new File(caminhoAbsoluto + ".prc"));

                        // salva apenas linhas de rejeito no arquivo de crítica, que serão processados como atrasados
                        FileHelper.saveByteArrayToFile(linhasIndeferidas.toString().getBytes(), arqCriticaAtrasadoNome);
                        filePath = arqCriticaAtrasadoNome;
                        final int indicePonto = nomeArquivo.lastIndexOf(".");
                        nomeArquivo = nomeArquivo.substring(0, indicePonto) + "_critica_atrasa" + nomeArquivo.substring(indicePonto, nomeArquivo.length());

                        LOG.info("Arquivo de crítica contém apenas parcelas de períodos passados. Linhas de rejeitos serão processadas à parte como retorno atrasado no arquivo: " + nomeArquivo);
                    }

                    moverArquivoPastaAtrasado(filePath, nomeArquivo, orgCodigo, estCodigo, responsavel);

                    LOG.info("Encontrado apenas parcelas de períodos anteriores ao atual. Retorno processado como atrasado.");
                    return ProcessaRetorno.CRITICA.equals(tipo) ? ProcessaRetorno.CRITICA_ATRASADO : ProcessaRetorno.ATRASADO;
                } else {
                    throw new ImpRetornoControllerException("mensagem.erro.combinacao.periodos.invalida", responsavel);
                }
            } else {
                return tipo;
            }
        } catch (ParserException  | ParseException | IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }

    }

    private void moverArquivoPastaAtrasado( String fileName, String nomeArquivo, String orgCodigo, String estCodigo, AcessoSistema responsavel) throws IOException {
        final String absolutePath = ParamSist.getDiretorioRaizArquivos();
        // Configura o caminho do arquivo de importação baseado no tipo do processamento
        // e se a importação é a nível de órgão
        final StringBuilder pathDestino = new StringBuilder(absolutePath + File.separatorChar + "retornoatrasado");

        // Verifica se a importação é a nível de órgão/estabelecimento, se for procura os arquivos de configuração do
        // diretório especifico. Caso não seja, busca do diretório raiz de configuração.
        if (!TextHelper.isNull(orgCodigo)) {
            if (responsavel.isCseSup()) {
                pathDestino.append(File.separatorChar).append("cse");
            } else {
                pathDestino.append(File.separatorChar).append("cse").append(File.separatorChar + orgCodigo);
            }
        } else if (!TextHelper.isNull(estCodigo)) {
            if (responsavel.isCseSup()) {
                pathDestino.append(File.separatorChar).append("est");
            } else {
                pathDestino.append(File.separatorChar).append("est").append(File.separatorChar + estCodigo);
            }
        } else {
            pathDestino.append(File.separatorChar).append("cse");
        }

        if (!Files.exists(Paths.get(pathDestino.toString()))) {
            Files.createDirectory(Paths.get(pathDestino.toString()));
        }

        pathDestino.append(File.separatorChar + nomeArquivo);

        Files.move(Paths.get(fileName), Paths.get(pathDestino.toString()));
    }

    private static class ImpRetornoControllerConf {
        int adeAlteradasAnterior = 0;
        int adeLiquidadasAnterior = 0;
    }

    private void enviarEmailAlertaParcelasPagasCsa(AcessoSistema responsavel) throws ImpRetornoControllerException {
        try {
            ParametroController parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);
            ListaCsaPorcentagemCadastradaTpaQuery listaCsaPorcentagemCadastradaTpaQuery = new ListaCsaPorcentagemCadastradaTpaQuery();
            List<TransferObject> csaTpaList = listaCsaPorcentagemCadastradaTpaQuery.executarDTO();

            if (!csaTpaList.isEmpty()) {
                for (TransferObject csaTpa : csaTpaList) {
                    String tpaPorcentagem = (String) csaTpa.getAttribute(Columns.PSE_VLR);
                    String csaCodigo = (String) csaTpa.getAttribute(Columns.CSA_CODIGO);
                    String csaNome = (String) csaTpa.getAttribute(Columns.CSA_NOME);
                    String tpaEmail = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_EMAIL_CSA_ALERTA_REFINANCIAMENTO, responsavel);
                    ArrayList<Long> adeNumeros = new ArrayList<>();
                    ArrayList<String> adeCodigos = new ArrayList<>();
                    if (!tpaEmail.isEmpty() && tpaPorcentagem != null) {
                        ListaAdeRefinanciamentoQuery listaAdeRefinanciamentoQuery = new ListaAdeRefinanciamentoQuery();
                        listaAdeRefinanciamentoQuery.csaCodigo = csaCodigo;
                        List<TransferObject> contratos = listaAdeRefinanciamentoQuery.executarDTO();
                        if (!contratos.isEmpty()) {
                            for (TransferObject contrato : contratos) {
                                Long adeNumero = (Long) contrato.getAttribute(Columns.ADE_NUMERO);
                                Integer prazo = (Integer) contrato.getAttribute(Columns.ADE_PRAZO);
                                Integer adePagas = (Integer) contrato.getAttribute(Columns.ADE_PRD_PAGAS);
                                String adeCodigo = (String) contrato.getAttribute(Columns.ADE_CODIGO);

                                float porcentagemPaga = ((float) (adePagas * 100) / prazo);
                                if (porcentagemPaga >= Float.parseFloat(tpaPorcentagem)) {
                                    adeNumeros.add(adeNumero);
                                    adeCodigos.add(adeCodigo);
                                }
                            }
                        }
                        if(!adeNumeros.isEmpty()) {
                            String[] emailCsa = tpaEmail.split(";");
                            Long[] adeNums = new Long[0];
                            String ade = Arrays.toString(adeNumeros.toArray(adeNums)).replace("[", "").replace("]", "");
                            for (String email : emailCsa) {
                                EnviaEmailHelper.enviarEmailOfertaRefinanciamentoCsa(email, csaNome, tpaPorcentagem, ade);
                            }

                            for (String adeCodigo : adeCodigos) {
                                OcorrenciaAutorizacaoHome.create(adeCodigo, CodedValues.TOC_ALERTA_PERCENTUAL_PARCELA_PAGA_ENVIADA_EMAIL, responsavel.getUsuCodigo(),
                                        ApplicationResourcesHelper.getMessage(MENSAGEM_OCORRENCIA_ENVIO_EMAIL_PROPOSTA_REFINANCIAMENTO, responsavel), null, null, responsavel.getIpUsuario(), DateHelper.getSystemDatetime(),
                                        PeriodoHelper.getInstance().getPeriodoAtual(responsavel.getOrgCodigo(), responsavel), null);
                            }
                        }
                    }
                }
            }
        } catch (final HQueryException | ViewHelperException | ParametroControllerException | CreateException | PeriodoException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ImpRetornoControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }
}
