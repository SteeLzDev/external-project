package com.zetra.econsig.helper.lote;

import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_ERRO_INTERNO_SISTEMA;
import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_LINHA_INVALIDA;
import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_MULTIPLOS_SERVIDORES_ENCONTRADOS;
import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_QTD_PARCELAS_INVALIDA;
import static com.zetra.econsig.values.CodedValues.TPA_SIM;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.zetra.econsig.delegate.HistoricoArquivoDelegate;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.parametros.AlterarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.ConfirmarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.LiquidarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.RenegociarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.DespesaIndividualControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.consignacao.ReservaMargemHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamCsa;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleRestricaoAcesso;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.servico.NaturezaRelSvc;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.xml.XmlHelper;
import com.zetra.econsig.parser.LeitorArquivoFebraban;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTextoZip;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.config.DocumentoTipo;
import com.zetra.econsig.parser.febraban.Analisador;
import com.zetra.econsig.persistence.entity.ControleProcessamentoLote;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.persistence.entity.TipoLancamento;
import com.zetra.econsig.service.beneficios.CalcularSubsidioBeneficioController;
import com.zetra.econsig.service.cartaocredito.ValidadorCartaoCreditoController;
import com.zetra.econsig.service.consignacao.AlterarConsignacaoController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.CancelarConsignacaoController;
import com.zetra.econsig.service.consignacao.ConfirmarConsignacaoController;
import com.zetra.econsig.service.consignacao.DeferirConsignacaoController;
import com.zetra.econsig.service.consignacao.LiquidarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignacao.RenegociarConsignacaoController;
import com.zetra.econsig.service.consignacao.ReservarMargemController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.lote.LoteController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusBlocoProcessamentoEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: LoteHelper.java</p>
 * <p>Description: Helper Class para importações de arquivos de lote.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class LoteHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LoteHelper.class);

    private static final String ADE_ABERTA_RENEGOCIACAO = "ADE_ABERTA_RENEGOCIACAO";
    private static final String ADE_ALT_ENT_BLOQUEADAS = "ADE_ALT_ENT_BLOQUEADAS";
    private static final String ADE_ANO_MES_FIM = "ADE_ANO_MES_FIM";
    private static final String ADE_ANO_MES_FIM_REF = "ADE_ANO_MES_FIM_REF";
    private static final String ADE_ANO_MES_INI = "ADE_ANO_MES_INI";
    private static final String ADE_ANO_MES_INI_REF = "ADE_ANO_MES_INI_REF";
    private static final String ADE_CARENCIA = "ADE_CARENCIA";
    private static final String ADE_IDENTIFICADOR = "ADE_IDENTIFICADOR";
    private static final String ADE_IDENTIFICADOR_VERIFICAR = "ADE_IDENTIFICADOR_VERIFICAR";
    private static final String ADE_INDICE = "ADE_INDICE";
    private static final String ADE_LIQUIDADA_RENEGOCIACAO = "ADE_LIQUIDADA_RENEGOCIACAO";
    private static final String ADE_MENSALIDADE_BENEFICIO = "ADE_MENSALIDADE_BENEFICIO";
    private static final String ADE_PERIODICIDADE = "ADE_PERIODICIDADE";
    private static final String ADE_PRAZO = "ADE_PRAZO";
    private static final String ADE_PRZ_NAO_CADASTRADO = "ADE_PRZ_NAO_CADASTRADO";
    private static final String ADE_TAXA_JUROS = "ADE_TAXA_JUROS";
    private static final String ADE_VLR = "ADE_VLR";
    private static final String ADE_VLR_LIQUIDO = "ADE_VLR_LIQUIDO";
    private static final String ADE_VLR_TAC = "ADE_VLR_TAC";
    private static final String ADE_VLR_MENS_VINC = "ADE_VLR_MENS_VINC";
    private static final String ADE_VLR_IOF = "ADE_VLR_IOF";
    private static final String ALTERACAO_AVANCADA = "ALTERACAO_AVANCADA";
    private static final String CBE_CODIGO = "CBE_CODIGO";
    private static final String CBE_NUMERO = "CBE_NUMERO";
    private static final String CNV_COD_VERBA = "CNV_COD_VERBA";
    private static final String COD_REG = "COD_REG";
    private static final String DAD_VALOR = "DAD_VALOR_";
    private static final String EST_IDENTIFICADOR = "EST_IDENTIFICADOR";
    private static final String LINHA_INVALIDA = "LINHA_INVALIDA";
    private static final String MATRICULA_SER_NA_CSA = "MATRICULA_SER_NA_CSA";
    private static final String MODALIDADE_OPERACAO = "MODALIDADE_OPERACAO";
    private static final String NSE_CODIGO = "NSE_CODIGO";
    private static final String OCA_OBS = "OCA_OBS";
    private static final String OPERACAO = "OPERACAO";
    private static final String ORG_IDENTIFICADOR = "ORG_IDENTIFICADOR";
    private static final String RENEGOCIACAO = "RENEGOCIACAO";
    private static final String RENEGOCIAR = "RENEGOCIAR";
    private static final String RESERVAR = "RESERVAR";
    private static final String RSE_AGENCIA_SAL = "RSE_AGENCIA_SAL";
    private static final String RSE_BANCO_SAL = "RSE_BANCO_SAL";
    private static final String RSE_CONTA_SAL = "RSE_CONTA_SAL";
    private static final String RSE_MATRICULA = "RSE_MATRICULA";
    private static final String SER_CPF = "SER_CPF";
    private static final String SVC_IDENTIFICADOR = "SVC_IDENTIFICADOR";
    private static final String TLA_CODIGO = "TLA_CODIGO";
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String _1_ = "1";
    private static final String _0_ = "0";
    private static final String _S_ = "S";
    private static final String _N_ = "N";

    private static final int TAMANHO_MSG_ERRO_DEFAULT = 100;
    private static final String COMPLEMENTO_DEFAULT = " ";

    /** Comandos permitidos na importação de lote */
    public static final String INCLUSAO    = "I";
    public static final String ALTERACAO   = "A";
    public static final String EXCLUSAO    = "E";
    public static final String CONFIRMACAO = "C";

    /** Consignatária da importação de lote, e responsável pela importação **/
    private final String csaCodigo;
    private final String corCodigo;
    private final String tipoEntidade;
    private final String codigoEntidade;

    /** Nome (com o caminho completo) do arquivo a ser importado **/
    private String nomeArquivoLote;
    private String pastaArquivoLote;
    private String entradaImpLote;
    private String tradutorImpLote;

    /** Dados sobre a linha corrente sendo processada **/
    private String csaCodigoCorrente;
    private Integer numLinhaCorrente;

    /** Se deve apenas validar o lote **/
    private final boolean validar;
    /** Se deve validar status de servidor e convênio **/
    private final boolean serAtivo;
    private final boolean cnvAtivo;

    /** Lote leiaute CNAB TS9 **/
    private boolean loteFebraban;
    /** Delimitador usado no lote **/
    private String delimitador;

    /** Responsável pelo processamento do lote **/
    private final AcessoSistema responsavel;

    /** Totalizadores de registros */
    private int totalIncluidos;
    private int totalExcluidos;
    private int totalAlterados;
    private int totalConfirmados;
    private int totalRegistros;
    private int totalProblema;

    /** Cache de parâmetros **/
    private final Map<String, Map<String, Object>> cacheParametrosCnv;
    private final Map<String, Set<Integer>> cachePrazos;

    /** Controllers necessários para importação de lote */
    private final LoteController loteController;
    private final ParametroController parametroController;
    private final AutorizacaoController autorizacaoController;
    private final ServidorController servidorController;
    private final ReservarMargemController reservarController;
    private final AlterarConsignacaoController alterarController;
    private final LiquidarConsignacaoController liquidarController;
    private final CancelarConsignacaoController cancelarController;
    private final RenegociarConsignacaoController renegociarController;
    private final ConfirmarConsignacaoController confirmarController;
    private final ValidadorCartaoCreditoController validadorCartaoCreditoController;
    private final DeferirConsignacaoController deferirConsignacaoController;
    private final PesquisarConsignacaoController pesquisarConsignacaoController;
    private final CalcularSubsidioBeneficioController calcularSubsidioBeneficioController;

    /** Permite incluir contratos com data atrasada */
    private final boolean permiteLoteAtrasado;
    /** Período definido pelo usuário CSE/ORG para as linhas do lote */
    private final Date periodoConfiguravel;
    /** Permite redução do valor de lançamento de cartão */
    private final boolean permiteReducaoLancamentoCartao;
    /** Controle de erro na renegociação **/
    private boolean erroRenegociacao;

    public LoteHelper(String csaCodigo, String corCodigo, boolean validar, boolean serAtivo, boolean cnvAtivo, boolean permiteLoteAtrasado, boolean permiteReducaoLancamentoCartao, Date periodoConfiguravel, AcessoSistema responsavel) {
        this.csaCodigo = csaCodigo;
        this.corCodigo = corCodigo;
        this.validar = validar;
        this.serAtivo = serAtivo;
        this.cnvAtivo = cnvAtivo;
        this.permiteLoteAtrasado = permiteLoteAtrasado;
        this.permiteReducaoLancamentoCartao = permiteReducaoLancamentoCartao;
        this.periodoConfiguravel = periodoConfiguravel;
        this.responsavel = responsavel;

        if (responsavel.isCsaCor()) {
            tipoEntidade = responsavel.getTipoEntidade();
            codigoEntidade = responsavel.getCodigoEntidade();
        } else if (!TextHelper.isNull(corCodigo)) {
            tipoEntidade = AcessoSistema.ENTIDADE_COR;
            codigoEntidade = corCodigo;
        } else if (!TextHelper.isNull(csaCodigo)) {
            tipoEntidade = AcessoSistema.ENTIDADE_CSA;
            codigoEntidade = csaCodigo;
        } else {
            tipoEntidade = AcessoSistema.ENTIDADE_CSE;
            codigoEntidade = null;
        }

        // Inicializa os caches de parâmetros
        cacheParametrosCnv = new HashMap<>();
        cachePrazos = new HashMap<>();

        // Inicializa os controllers necessarios
        try {
            loteController = ApplicationContextProvider.getApplicationContext().getBean(LoteController.class);
            parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);
            autorizacaoController = ApplicationContextProvider.getApplicationContext().getBean("autorizacaoController", AutorizacaoController.class);
            reservarController = ApplicationContextProvider.getApplicationContext().getBean("reservarMargemController", ReservarMargemController.class);
            servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
            alterarController = ApplicationContextProvider.getApplicationContext().getBean(AlterarConsignacaoController.class);
            liquidarController = ApplicationContextProvider.getApplicationContext().getBean(LiquidarConsignacaoController.class);
            cancelarController = ApplicationContextProvider.getApplicationContext().getBean(CancelarConsignacaoController.class);
            renegociarController = ApplicationContextProvider.getApplicationContext().getBean("renegociarConsignacaoController", RenegociarConsignacaoController.class);
            confirmarController = ApplicationContextProvider.getApplicationContext().getBean(ConfirmarConsignacaoController.class);
            validadorCartaoCreditoController = ApplicationContextProvider.getApplicationContext().getBean(ValidadorCartaoCreditoController.class);
            deferirConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean("deferirConsignacaoController", DeferirConsignacaoController.class);
            pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
            calcularSubsidioBeneficioController = ApplicationContextProvider.getApplicationContext().getBean(CalcularSubsidioBeneficioController.class);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RuntimeException(ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel));
        }
    }

    /**
     * Método que inicia a importação de lote realizando a configuração dos objetos de
     * tradução do arquivo (leitor/tradutor/escritor) a partir dos XMLs de configuração
     * e do arquivo de entrada.
     * @param nomeArqXmlEntrada
     * @param nomeArqXmlTradutor
     * @param nomeArquivoEntrada
     * @throws ViewHelperException
     */
    private void iniciarImportacao(String nomeArqXmlEntrada, String nomeArqXmlTradutor, String nomeArquivoEntrada) throws ViewHelperException {
        try {
            final ControleRestricaoAcesso.RestricaoAcesso restricao = ControleRestricaoAcesso.possuiRestricaoAcesso(responsavel);
            if (restricao.getGrauRestricao() != ControleRestricaoAcesso.GrauRestricao.SemRestricao) {
                throw new ViewHelperException("rotulo.critica.operacao.temporariamente.indisponivel", responsavel, restricao.getDescricao());
            }
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.critica.operacao.temporariamente.indisponivel", responsavel);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            LOG.debug((validar ? "IMPORTACAO - VALIDAR: " : "IMPORTACAO - PROCESSAR: ") + csaCodigo + " - Arquivo: " + nomeArquivoEntrada + " -> " + DateHelper.getSystemDatetime());
            LOG.debug("Tipo de Entidade: " + tipoEntidade + "\t Codigo: " + codigoEntidade + "\t" + DateHelper.getSystemDatetime());
        } else {
            if (!responsavel.isCseSup()) {
                throw new ViewHelperException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel);
            }
            LOG.debug((validar ? "IMPORTACAO - VALIDAR: " : "IMPORTACAO - PROCESSAR: ") + "MÚLTIPLAS CONSIGNATÁRIAS" + " - Arquivo: " + nomeArquivoEntrada + " -> " + DateHelper.getSystemDatetime());
        }
        LOG.debug("XML de entrada: " + nomeArqXmlEntrada + "\t XML de Tradução: " + nomeArqXmlTradutor);

        // Pega parâmetros de configuração do sistema
        final String absolutePath = ParamSist.getDiretorioRaizArquivos();
        final String pathLote = absolutePath + File.separatorChar + "conf" + File.separatorChar + "lote" + File.separatorChar;
        final String pathLoteDefault = absolutePath + File.separatorChar + "conf" + File.separatorChar + "lote" + File.separatorChar + "xml" + File.separatorChar;

        if (!TextHelper.isNull(csaCodigo)) {
            entradaImpLote = pathLote + csaCodigo + File.separatorChar + nomeArqXmlEntrada;
            tradutorImpLote = pathLote + csaCodigo + File.separatorChar + nomeArqXmlTradutor;
        }

        final String entradaImpLoteDefault = pathLoteDefault + nomeArqXmlEntrada;
        final String tradutorImpLoteDefault = pathLoteDefault + nomeArqXmlTradutor;

        File arqConfEntrada = null;
        File arqConfTradutor = null;

        if (!TextHelper.isNull(csaCodigo)) {
            arqConfEntrada = new File(entradaImpLote);
            arqConfTradutor = new File(tradutorImpLote);

            if (!arqConfEntrada.exists() || !arqConfTradutor.exists()) {
                final File arqConfEntradaDefault = new File(entradaImpLoteDefault);
                final File arqConfTradutorDefault = new File(tradutorImpLoteDefault);
                if (!arqConfEntradaDefault.exists() || !arqConfTradutorDefault.exists()) {
                    throw new ViewHelperException("mensagem.erro.lote.arquivos.configuracao.importacao.ausentes", responsavel);
                } else {
                    arqConfEntrada = arqConfEntradaDefault;
                    arqConfTradutor = arqConfTradutorDefault;
                    entradaImpLote = entradaImpLoteDefault;
                    tradutorImpLote = tradutorImpLoteDefault;
                }
            }
            // Verifica o arquivo de entrada de dados
            pastaArquivoLote = absolutePath + File.separatorChar + "lote" + File.separatorChar + tipoEntidade.toLowerCase() + File.separatorChar + codigoEntidade + File.separatorChar;
            nomeArquivoLote = pastaArquivoLote + nomeArquivoEntrada;
        } else {
            final File arqConfEntradaDefault = new File(entradaImpLoteDefault);
            final File arqConfTradutorDefault = new File(tradutorImpLoteDefault);
            if (!arqConfEntradaDefault.exists() || !arqConfTradutorDefault.exists()) {
                throw new ViewHelperException("mensagem.erro.lote.arquivos.configuracao.importacao.ausentes", responsavel);
            } else {
                arqConfEntrada = arqConfEntradaDefault;
                arqConfTradutor = arqConfTradutorDefault;
                entradaImpLote = entradaImpLoteDefault;
                tradutorImpLote = tradutorImpLoteDefault;
            }

            pastaArquivoLote = absolutePath + File.separatorChar + "lote" + File.separatorChar + "cse" + File.separatorChar;
            nomeArquivoLote = pastaArquivoLote + nomeArquivoEntrada;
        }

        final File arqEntrada = new File(nomeArquivoLote);
        if (!arqEntrada.exists()) {
            LOG.error("Arquivo não encontrado: \"" + nomeArquivoLote + "\"");
            throw new ViewHelperException("mensagem.erro.lote.arquivo.nao.encontrado", responsavel);
        }
        // Validar permissão de escrita na pasta de entrada que será usada como saída da crítica
        final File pastaSaida = new File(pastaArquivoLote);
        if (!pastaSaida.exists() && !pastaSaida.mkdirs()) {
            throw new ViewHelperException("mensagem.erro.criacao.diretorio", responsavel, pastaSaida.getAbsolutePath());
        } else if (!pastaSaida.canWrite()) {
            throw new ViewHelperException("mensagem.erro.escrita.diretorio", responsavel, pastaSaida.getAbsolutePath());
        }

        // Adiciona o lote ao controle de processamento paralelo
        ControleLote.getInstance().adicionar(nomeArquivoLote, responsavel);

        // Controle utilizado para manter o status do processamento no banco, caso o sistema seja reiniciado enquanto o lote é processado
        ControleProcessamentoLote controleProcessamentoLote = null;

        try {
            // Verifica o identificador do arquivo de configuração de entrada, para determinar
            // se é um arquivo no padrão FEBRABAN
            final DocumentoTipo docEntrada = XmlHelper.unmarshal(new FileInputStream(arqConfEntrada));
            loteFebraban = ((docEntrada != null) && CodedValues.CODIGO_ID_FEBRABAN.equalsIgnoreCase(docEntrada.getID()));
            delimitador = XmlHelper.getParametroPorNome("delimitador", docEntrada);

            // Busca o registro de controle de processamento (no SOAP é criado no endpoint pois é passado o nome do arquivo no centralizador)
            controleProcessamentoLote = loteController.findProcessamentoByArquivoeConsig(nomeArquivoLote);
            if (controleProcessamentoLote == null) {
                controleProcessamentoLote = loteController.incluirProcessamento(null, nomeArquivoLote, CodedValues.CPL_UPLOAD_SUCESSO, responsavel);
            }

            if (controleProcessamentoLote.getCplStatus().equals(CodedValues.CPL_UPLOAD_SUCESSO)) {
                final Date periodoProc = (permiteLoteAtrasado && (periodoConfiguravel != null)) ? periodoConfiguravel : PeriodoHelper.getInstance().getPeriodoAtual(null, responsavel);

                // Carrega blocos de importação caso o status do processamento seja de CPL_UPLOAD_SUCESSO
                loteController.carregarBlocosLote(nomeArquivoLote, entradaImpLote, tradutorImpLote, csaCodigo, periodoProc, controleProcessamentoLote, responsavel);

                // Atualiza status dos blocos para aguard. processamento
                loteController.atualizarStatusBlocos(nomeArquivoLote, csaCodigo, StatusBlocoProcessamentoEnum.PREPARANDO, StatusBlocoProcessamentoEnum.AGUARD_PROCESSAMENTO, responsavel);

                // Altera o status para informar que está processamento
                controleProcessamentoLote.setCplStatus(CodedValues.CPL_PROCESSANDO);
                controleProcessamentoLote.setCplParametros(converterParametrosJson());
                loteController.alterarProcessamento(controleProcessamentoLote, responsavel);

                // Inclui histórico do arquivo a ser importado/validado
                final String harObs = ApplicationResourcesHelper.getMessage(validar ? "rotulo.lote.arquivo.validar" : "rotulo.lote.arquivo.processar", responsavel);
                final Date harDataProc = DateHelper.getSystemDatetime();
                final HistoricoArquivoDelegate hisArqDelegate = new HistoricoArquivoDelegate();
                hisArqDelegate.createHistoricoArquivo(tipoEntidade, codigoEntidade, TipoArquivoEnum.ARQUIVO_LOTE, nomeArquivoLote, harObs, harDataProc, periodoProc, _1_, responsavel.getFunCodigo(), responsavel);

                // Grava Log para auditoria
                final LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, (!validar ? Log.IMPORTACAO_ARQ_LOTE : Log.VALIDACAO_ARQ_LOTE), Log.LOG_INFORMACAO);
                log.setConsignataria(csaCodigo);
                log.setCorrespondente(corCodigo);
                log.add(ApplicationResourcesHelper.getMessage("rotulo.log.arquivo", responsavel, nomeArquivoEntrada));
                log.add(ApplicationResourcesHelper.getMessage("rotulo.log.numero.linhas.arquivo", responsavel, String.valueOf(FileHelper.getNumberOfLines(nomeArquivoLote))));
                log.add(ApplicationResourcesHelper.getMessage("rotulo.log.leiaute", responsavel, nomeArqXmlEntrada, nomeArqXmlTradutor));
                log.write();
            }
        } catch (final ZetraException | FileNotFoundException ex) {
            LOG.error(ex.getMessage(), ex);

            if (ex instanceof final ParserException pex) {
                final String messageKey = pex.getMessageKey();
                if ((messageKey.indexOf("mensagem.erro.tradutor.linha.cabecalho.entrada.invalida") != -1) ||
                    (messageKey.indexOf("mensagem.erro.leitor.arquivo.numero.maximo.linhas") != -1) ||
                    (messageKey.indexOf("mensagem.erro.leitor.arquivo.formato.incorreto.linha") != -1) ||
                    (messageKey.indexOf("mensagem.erro.linha.arquivo.entrada.formato.incorreto") != -1)) {
                    throw new ViewHelperException(pex);
                }
            }

            throw new ViewHelperException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }
    }

    /**
     * Realiza a importação/validação do arquivo de lote "nomeArquivoEntrada" utilizando os leiautes
     * XML "nomeArqXmlEntrada" e "nomeArqXmlTradutor".
     * @param nomeArqXmlEntrada
     * @param nomeArqXmlTradutor
     * @param nomeArquivoEntrada
     * @return
     * @throws ViewHelperException
     */
    public String importarLote(String nomeArqXmlEntrada, String nomeArqXmlTradutor, String nomeArquivoEntrada) throws ViewHelperException {
        String estIdentificadorUsuario = null;
        String orgIdentificadorUsuario = null;

        // Se é usuário de órgão, carrega os campos de identificador de EST ou ORG para definir como critério
        // para busca de servidor e consignações, limitando as operações a estas entidades
        if (responsavel.isOrg()) {
            try {
                final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
                final EstabelecimentoTransferObject est = consignanteController.findEstabelecimento(responsavel.getEstCodigo(), responsavel);
                estIdentificadorUsuario = est.getEstIdentificador();

                if (!responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                    final OrgaoTransferObject org = consignanteController.findOrgao(responsavel.getOrgCodigo(), responsavel);
                    orgIdentificadorUsuario = org.getOrgIdentificador();
                }
            } catch (final ConsignanteControllerException ex) {
                throw new ViewHelperException(ex);
            }
        }

        // Parâmetro de obrigatoriedade de CPF e Matricula para pesquisa de servidor
        boolean requerMatriculaCpf = false;
        try {
            requerMatriculaCpf = parametroController.requerMatriculaCpf(true, responsavel);
        } catch (final ParametroControllerException ex) {
            throw new ViewHelperException(ex);
        }

        // Objeto usado para converter o Map com campos de entrada em JSON, e vice-verba
        final Gson gson = new Gson();

        try {
            // Executa inicialização do processamento
            iniciarImportacao(nomeArqXmlEntrada, nomeArqXmlTradutor, nomeArquivoEntrada);

            // Busca os blocos carregados que ainda aguardam processamento
            final List<TransferObject> blocos = loteController.lstBlocoProcessamentoLote(nomeArquivoLote, csaCodigo, StatusBlocoProcessamentoEnum.AGUARD_PROCESSAMENTO, responsavel);

            if ((blocos != null) && !blocos.isEmpty()) {
                // Executa os blocos de lote identificados
                for (final TransferObject bloco : blocos) {
                    processarBloco(bloco, gson, requerMatriculaCpf, estIdentificadorUsuario, orgIdentificadorUsuario);
                }
            }

            // Executa finalização, com geração do arquivo de crítica
            return finalizarImportacao(nomeArquivoEntrada);

        } catch (final ZetraException ex) {
            final ControleProcessamentoLote controleProcessamentoLote = loteController.findProcessamentoByArquivoeConsig(nomeArquivoLote);
            if (controleProcessamentoLote != null) {
                try {
                    if (CanalEnum.WEB.equals(responsavel.getCanal())) {
                        // Se é processamento de lote via Web, remove o controle de processamento pois não existe
                        // operação de consumo do resultado, que pode ser acessado a qualquer momento pelo usuário.
                        loteController.excluirProcessamento(controleProcessamentoLote, responsavel);
                    } else {
                        // Altera o status para informar que ocorreu falha no processamento
                        controleProcessamentoLote.setCplStatus(CodedValues.CPL_PROCESSADO_FALHA);
                        loteController.alterarProcessamento(controleProcessamentoLote, responsavel);
                    }
                } catch (final ZetraException ex2) {
                    LOG.error(ex2.getMessage(), ex2);
                }
            }

            throw new ViewHelperException(ex);
        } finally {
            ControleLote.getInstance().remover(nomeArquivoLote, responsavel);
        }
    }

    private void processarBloco(TransferObject bloco, Gson gson, boolean requerMatriculaCpf, String estIdentificadorUsuario, String orgIdentificadorUsuario) {
        try {
            // Converte o JSON de campos em um Map
            @SuppressWarnings("unchecked")
            final Map<String, Object> entrada = gson.fromJson((String) bloco.getAttribute(Columns.BPL_CAMPOS), Map.class);

            numLinhaCorrente = (Integer) bloco.getAttribute(Columns.BPL_NUM_LINHA);
            csaCodigoCorrente = (!TextHelper.isNull(csaCodigo) ? csaCodigo : bloco.getAttribute(Columns.CSA_CODIGO).toString());

            // Obtém as ocorrencias
            final String ocorrencias = entrada.get("OCORRENCIAS") != null ? entrada.get("OCORRENCIAS").toString() : "";
            final String ocorrenciasHeader = entrada.get("OCORRENCIAS_HEADER") != null ? entrada.get("OCORRENCIAS_HEADER").toString() : "";

            // Se lote febraban e a linha já tiver uma ocorrencia de registro ou de lote, então invalida a linha
            if (loteFebraban && (!"".equals(ocorrencias) || !"".equals(ocorrenciasHeader))) {
                entrada.put(LINHA_INVALIDA, _S_);
            }

            if ((entrada.get(LINHA_INVALIDA) == null) || _N_.equals(entrada.get(LINHA_INVALIDA).toString())) {
                // Valida map de entrada
                final Map<String, Object> entradaValida = loteController.validaEntrada(entrada);

                // Sobrescreve o mapa resultado da tradução com o identificador do EST ou ORG do usuário
                if (responsavel.isOrg()) {
                    entradaValida.put(EST_IDENTIFICADOR, estIdentificadorUsuario);
                    entradaValida.put(ORG_IDENTIFICADOR, orgIdentificadorUsuario);
                }

                if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, CodedValues.TPC_SIM, responsavel)) {
                    // identifica o tipo de arquivo que está sendo processado
                    final String arqIdentificador = (String) entradaValida.get("ARQ_IDENTIFICADOR");
                    // se for arquivo de Operadora de Benefícios, busca dados adicionais do servidor e verba
                    if ((arqIdentificador != null) && ("OPERADORA".equalsIgnoreCase(arqIdentificador) || "FATURAMENTO".equalsIgnoreCase(arqIdentificador))) {
                        final String csaOperadora = csaCodigoCorrente;
                        LOG.debug("Identificador do arquivo... " + arqIdentificador);
                        // valida o número do contrato do benefício (carteirinha)
                        final String cbeNumero = (String) entradaValida.get(CBE_NUMERO);
                        final String tlaCodigo = (String) entradaValida.get(TLA_CODIGO);
                        if (TextHelper.isNull(cbeNumero)) {
                            LOG.debug("O número do cliente no contrato de benefício (carteirinha) deve ser informado.");
                            throw new ViewHelperException("mensagem.informe.numero.contrato.beneficio", responsavel);
                        }
                        if (TextHelper.isNull(tlaCodigo) && TextHelper.isNull(entradaValida.get(CNV_COD_VERBA))) {
                            LOG.debug("O tipo de lançamento de benefício ou o código de verba deve ser informado.");
                            throw new ViewHelperException("mensagem.informe.tipo.lancamento.beneficio.ou.verba", responsavel);
                        }
                        // recupera dados adicionais do servidor através da mensalidade
                        List<TransferObject> adeMensalidade = null;
                        if (!TextHelper.isNull(tlaCodigo)) {
                            adeMensalidade = loteController.buscarConsignacaoMensalidadeBeneficio(csaOperadora, cbeNumero, tlaCodigo, responsavel);
                        } else if (!TextHelper.isNull(entradaValida.get(CNV_COD_VERBA))) {
                            adeMensalidade = loteController.buscarConsignacaoMensalidadeBeneficioVerbaDestino(csaOperadora, cbeNumero, (String) entradaValida.get(CNV_COD_VERBA), responsavel);
                        }
                        if ((adeMensalidade != null) && !adeMensalidade.isEmpty()) {
                            final String adeCodigoMensalidade = (String) adeMensalidade.get(0).getAttribute(Columns.ADE_CODIGO);
                            entradaValida.put(ADE_MENSALIDADE_BENEFICIO, adeCodigoMensalidade);
                            final String svcCodigoMensalidade = (String) adeMensalidade.get(0).getAttribute(Columns.SVC_CODIGO);
                            // preenche o código do contrato do benefício
                            entradaValida.put(CBE_CODIGO, adeMensalidade.get(0).getAttribute(Columns.CBE_CODIGO));
                            // preenche o tipo de lançamento
                            if (TextHelper.isNull(tlaCodigo) && !TextHelper.isNull(entradaValida.get(CNV_COD_VERBA))) {
                                final List<TransferObject> relSvc = loteController.buscarRelacionamentoServicoVerbaDestino(csaOperadora, (String) entradaValida.get(CNV_COD_VERBA), responsavel);
                                if ((relSvc != null) && !relSvc.isEmpty()) {
                                    final String tntCodigoLancamento = (String) relSvc.get(0).getAttribute(Columns.RSV_TNT_CODIGO);
                                    final TipoLancamento tipoLancamento = loteController.buscarTipoLancamentoPorTntCodigo(tntCodigoLancamento, responsavel);
                                    entradaValida.put(TLA_CODIGO, tipoLancamento.getTlaCodigo());
                                }
                            }
                            // preenche dados do servidor caso não tenham sido informados
                            if (TextHelper.isNull(entradaValida.get(RSE_MATRICULA))) {
                                entradaValida.put(RSE_MATRICULA, adeMensalidade.get(0).getAttribute(Columns.RSE_MATRICULA));
                            }
                            if (TextHelper.isNull(entradaValida.get(SER_CPF))) {
                                entradaValida.put(SER_CPF, adeMensalidade.get(0).getAttribute(Columns.SER_CPF));
                            }
                            if (TextHelper.isNull(entradaValida.get(ORG_IDENTIFICADOR))) {
                                entradaValida.put(ORG_IDENTIFICADOR, adeMensalidade.get(0).getAttribute(Columns.ORG_IDENTIFICADOR));
                            }

                            // busca os dados de autorização de desconto de mensalidade (tb_dados_autorizacao_desconto)
                            final List<TransferObject> dadosConsignacaoMensalidade = autorizacaoController.lstDadoAutDesconto(adeCodigoMensalidade, null, VisibilidadeTipoDadoAdicionalEnum.LOTE, responsavel);
                            for (final TransferObject dado : dadosConsignacaoMensalidade) {
                                final String tdaCodigo = (String) dado.getAttribute(Columns.TDA_CODIGO);
                                if (TextHelper.isNull(entradaValida.get(DAD_VALOR + tdaCodigo))) {
                                    // preenche os campos que não vieram na entrada com os dados da ADE de mensalidade
                                    entradaValida.put(DAD_VALOR + tdaCodigo, dado.getAttribute(Columns.DAD_VALOR));
                                }
                            }

                            // busca o serviço de origem para o tipo de lançamento informado
                            if (TextHelper.isNull(entradaValida.get(CNV_COD_VERBA))) {
                                final Servico svcDestino = loteController.buscarServicoDestinoRelacionamentoBeneficio(svcCodigoMensalidade, tlaCodigo, responsavel);
                                entradaValida.put(SVC_IDENTIFICADOR, svcDestino.getSvcIdentificador());
                            }
                        }
                    }
                }

                // Valida informação de matrícula/cpf
                validaMatriculaCpf(csaCodigoCorrente, (String) entradaValida.get(RSE_MATRICULA), (String) entradaValida.get(SER_CPF), requerMatriculaCpf, responsavel);

                final boolean tpaAdeIdentificadorUnicoViaLote = adeIdentificadorUnicoViaLote(csaCodigoCorrente, responsavel);
                final String operacao = entradaValida.get(OPERACAO).toString();

                if (tpaAdeIdentificadorUnicoViaLote && ((entradaValida.get(ADE_IDENTIFICADOR) == null) || "".equals(entradaValida.get(ADE_IDENTIFICADOR)))) {
                    throw new ViewHelperException("mensagem.ade.identificador.obrigatorio", responsavel);
                }

                if ((entradaValida.get(ADE_IDENTIFICADOR) == null) || "".equals(entradaValida.get(ADE_IDENTIFICADOR))) {
                    final String dataAtualFormatada = DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMdd");
                    final String adeIdentificadorPadroLote = ApplicationResourcesHelper.getMessage("rotulo.lote.ade.identificador", responsavel, dataAtualFormatada);
                    entradaValida.put(ADE_IDENTIFICADOR, adeIdentificadorPadroLote);
                } else if (validaAdeIdentificadorUnico(csaCodigoCorrente, entrada.get(ADE_IDENTIFICADOR).toString(), operacao, responsavel)){
                    // Seta identificador usado para busca do contrato
                    if ((entradaValida.get(ADE_IDENTIFICADOR_VERIFICAR) == null) || "".equals(entradaValida.get(ADE_IDENTIFICADOR_VERIFICAR))) {
                        entradaValida.put(ADE_IDENTIFICADOR_VERIFICAR, entrada.get(ADE_IDENTIFICADOR));
                    }
                } else {
                    throw new ViewHelperException("mensagem.ade.identificador.unico.csa", responsavel);
                }

                // Seta a função de acordo com a operação
                if (INCLUSAO.equalsIgnoreCase(operacao)) {
                    responsavel.setFunCodigo(CodedValues.FUN_INCLUSAO_VIA_LOTE);
                } else if (ALTERACAO.equalsIgnoreCase(operacao)) {
                    responsavel.setFunCodigo(CodedValues.FUN_ALTERACAO_VIA_LOTE);
                } else if (EXCLUSAO.equalsIgnoreCase(operacao)) {
                    responsavel.setFunCodigo(CodedValues.FUN_EXCLUSAO_VIA_LOTE);
                } else if (CONFIRMACAO.equalsIgnoreCase(operacao)) {
                    responsavel.setFunCodigo(CodedValues.FUN_CONFIRMACAO_VIA_LOTE);
                }

                if (INCLUSAO.equalsIgnoreCase(operacao)) {
                    // confere se responsável tem permissão para inclusão via lote
                    if (!validar && !responsavel.temPermissao(CodedValues.FUN_INCLUSAO_VIA_LOTE)) {
                        throw new ViewHelperException("mensagem.erro.lote.usuario.sem.permissao.inclusao", responsavel);
                    }

                    final boolean permiteAlterarAdeSemMotivoOperacao = permiteAlterarAdeSemMotivoOperacao(csaCodigoCorrente, responsavel);

                    final String tmoCodigo = loteController.validaMotivoOperacao(entradaValida, CodedValues.FUN_INCLUSAO_VIA_LOTE, permiteAlterarAdeSemMotivoOperacao, responsavel);
                    if (tmoCodigo != null) {
                        entradaValida.put(Columns.TMO_CODIGO, tmoCodigo);
                    }

                    // Operação de inserção de reserva via lote
                    importaReservaMargem(entradaValida, null);

                    // Ao ocorrer erro na renegociação, reverte para inclusão e marca erroRenegociacao=true, caso a inclusão
                    // seja realizada com sucesso, reinicializa variável para false
                    erroRenegociacao = false;
                } else {
                    if (!validar) {
                        if (ALTERACAO.equalsIgnoreCase(operacao) && !responsavel.temPermissao(CodedValues.FUN_ALTERACAO_VIA_LOTE)) {
                            // confere se responsável tem permissão para inclusão via lote
                            throw new ViewHelperException("mensagem.erro.lote.usuario.sem.permissao.alteracao", responsavel);
                        } else if (EXCLUSAO.equalsIgnoreCase(operacao) && !responsavel.temPermissao(CodedValues.FUN_EXCLUSAO_VIA_LOTE)) {
                            // confere se responsável tem permissão para exclusão via lote
                            throw new ViewHelperException("mensagem.erro.lote.usuario.sem.permissao.exclusao", responsavel);
                        } else if (CONFIRMACAO.equalsIgnoreCase(operacao) && !responsavel.temPermissao(CodedValues.FUN_CONFIRMACAO_VIA_LOTE)) {
                            // confere se responsável tem permissão para confirmação via lote
                            throw new ViewHelperException("mensagem.erro.lote.usuario.sem.permissao.confirmacao", responsavel);
                        }
                    }

                    final boolean alteracaoAvancada = responsavel.isCseSup() && (!TextHelper.isNull(entradaValida.get(ALTERACAO_AVANCADA)) && _1_.equals(entradaValida.get(ALTERACAO_AVANCADA)));
                    final boolean permiteAlterarAdeSemMotivoOperacao = permiteAlterarAdeSemMotivoOperacao(csaCodigoCorrente, responsavel);

                    // verificação de motivo de operação para operação de liquidação ou confirmação de reserva
                    final String funCodigo = (
                            EXCLUSAO.equalsIgnoreCase(operacao) ? CodedValues.FUN_LIQ_CONTRATO :
                                CONFIRMACAO.equalsIgnoreCase(operacao) ? CodedValues.FUN_CONF_RESERVA :
                                    ALTERACAO.equalsIgnoreCase(operacao) && alteracaoAvancada ? CodedValues.FUN_ALT_AVANCADA_CONSIGNACAO :
                                        CodedValues.FUN_ALT_CONSIGNACAO
                            );
                    final String tmoCodigo = loteController.validaMotivoOperacao(entradaValida, funCodigo, permiteAlterarAdeSemMotivoOperacao, responsavel);
                    if (tmoCodigo != null) {
                        entradaValida.put(Columns.TMO_CODIGO, tmoCodigo);
                    }

                    final CustomTransferObject criterio = montaCriterioDeBusca(entradaValida, operacao);

                    // Realiza modificação de contrato de acordo com especificado na linha de lote.
                    importaModificacaoConsignacao(operacao, entradaValida, criterio);
                }

            } else if (loteFebraban) {
                // Se lote febraban então as ocorrencias serão as mensagens de erro
                final String msgErro = ocorrenciasHeader + ocorrencias;
                salvarCriticaLoteFebraban(msgErro, false);
            } else {
                totalProblema++;
                final String msgErro = _S_.equalsIgnoreCase(entrada.get(LINHA_INVALIDA).toString()) ? ApplicationResourcesHelper.getMessage(MENSAGEM_LINHA_INVALIDA, responsavel) + "." : entrada.get(LINHA_INVALIDA).toString();
                salvarCriticaLotePadrao(msgErro);
            }
        } catch (final Exception e) {
            String mensagem = e.getMessage();
            if (e instanceof final ZetraException ze) {
                mensagem = ze.getResourcesMessage(loteFebraban ? ZetraException.MENSAGEM_LOTE_FEBRABAN : ZetraException.MENSAGEM_LOTE);
                if (!loteFebraban && (ze.getMessageKey() != null) && MENSAGEM_LINHA_INVALIDA.equals(ze.getMessageKey())) {
                    // Se não é febraban e é erro de linha inválida, concatena
                    // a descrição da exceção na mensagem de erro.
                    mensagem += ": " + e.getMessage();
                } else if (loteFebraban && (ze.getMessageKey() == null)) {
                    // Se é febraban e não tem código de mensagem, coloca o código
                    // padrão de linha inválida à frente da mensagem de erro
                    mensagem = formataMsgErro(Analisador.OCORRENCIA_INVALIDO_PADRAO, Analisador.COMPLEMENTO_DEFAULT_CAMPO_OCORRENCIAS, Analisador.TAMANHO_CAMPO_OCORRENCIAS, true) + mensagem;
                }
            } else if (loteFebraban) {
                // Se não é exceção do sistema e é lote febraban, coloca o código
                // padrão de linha inválida à frente da mensagem de erro
                mensagem = formataMsgErro(Analisador.OCORRENCIA_INVALIDO_PADRAO, Analisador.COMPLEMENTO_DEFAULT_CAMPO_OCORRENCIAS, Analisador.TAMANHO_CAMPO_OCORRENCIAS, true) + mensagem;
            }
            totalProblema++;

            if (loteFebraban) {
                // Se é renegociação que não localiza contrato no método filtraServidoresRenegociacao(), o procedimento reverte para inclusão.
                // Se um erro ocorrer nesta inclusão, concatena o código de erro original (HY) ao da inclusão.
                if (erroRenegociacao) {
                    erroRenegociacao = false;
                    final String codigo = mensagem.substring(0, 10);
                    if (codigo.trim().length() > 8) {
                        LOG.warn("Não foi possível adicionar novo código ao código Febraban pois o mesmo já possui 5 códigos.");
                    } else {
                        mensagem = ("HY" + codigo).substring(0, 10) + mensagem.substring(10);
                    }
                }
                salvarCriticaLoteFebraban(mensagem, false);
            } else {
                // Gera linha para arquivo de crítica
                salvarCriticaLotePadrao(mensagem);
            }
        }
    }

    private String finalizarImportacao(String nomeArquivoEntrada) throws ViewHelperException {
        if (!validar) {
            excluiAdesNaoAlteradasLoteTotal(csaCodigo, corCodigo, pastaArquivoLote, responsavel);
        }

        // Mapa contendo as críticas já com o conteúdo das linhas pelo número da linha (TreeMap para manter ordem pelo numero da linha)
        final Map<Integer, String> critica = new TreeMap<>();

        try {
            // Busca os blocos processados para geração do arquivo de crítica
            final List<TransferObject> blocosProcessados = loteController.lstBlocoProcessamentoLote(nomeArquivoLote, csaCodigo, null, responsavel);
            for (final TransferObject bloco : blocosProcessados) {
                // Mensagem de crítica gerada para a linha
                final String msgCritica = (String) bloco.getAttribute(Columns.BPL_CRITICA);
                if (!TextHelper.isNull(msgCritica)) {
                    final Integer numLinha = (Integer) bloco.getAttribute(Columns.BPL_NUM_LINHA);
                    // Recupera linha de entrada para geração do arquivo de crítica
                    String linha = bloco.getAttribute(Columns.BPL_LINHA).toString();
                    // Remover colchetes incluídos na carga dos blocos
                    linha = linha.substring(1, linha.length() - 1);

                    if (loteFebraban) {
                        critica.put(numLinha, gerarLinhaArquivoSaida(linha, delimitador, msgCritica, true));
                    } else {
                        critica.put(numLinha, linha + delimitador + formataMsgErro(msgCritica, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    }
                }
            }
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
        }

        String nomeArqSaidaZip = null;
        if (!critica.isEmpty()) {
            try {
                // Se tem críticas a serem geradas, cria novamente o leitor para tratar cabeçalhos/rodapés de
                // lote e leiautes complexos como o Febraban CNAB TS9
                // Configura o leitor de acordo com o arquivo de entrada
                final LeitorArquivoTexto leitor;
                if (loteFebraban) {
                    leitor = new LeitorArquivoFebraban(entradaImpLote, nomeArquivoLote);
                } else if (nomeArquivoLote.toLowerCase().endsWith(".zip") || nomeArquivoLote.toLowerCase().endsWith(".zip.prc")) {
                    leitor = new LeitorArquivoTextoZip(entradaImpLote, nomeArquivoLote);
                } else {
                    leitor = new LeitorArquivoTexto(entradaImpLote, nomeArquivoLote);
                }
                leitor.iniciaLeitura();

                // Começa na linha de número 1
                int numLinha = 1;

                while (leitor.le() != null) {
                    if ((numLinha == 1) && !TextHelper.isNull(leitor.getLinhaHeader())) {
                        // Imprime a linha de header no arquivo
                        critica.put(numLinha++, gerarLinhaArquivoSaida(leitor.getLinhaHeader(), delimitador, null, loteFebraban));
                    }

                    if (loteFebraban) {
                        // Gera linha de saida para todos os registros
                        final LeitorArquivoFebraban leitorFebraban = (LeitorArquivoFebraban) leitor;

                        // Verifica se devem ser escritos os headers e footers
                        if (leitorFebraban.getLinhaFooterLoteAtual() != null) {
                            critica.put(numLinha++, gerarLinhaArquivoSaida(leitorFebraban.getLinhaFooterLoteAtual(), delimitador, null, true));
                            leitorFebraban.setLinhaFooterLoteAtual(null);
                        }
                        if (leitorFebraban.getLinhaHeaderLoteAtual() != null) {
                            critica.put(numLinha++, gerarLinhaArquivoSaida(leitorFebraban.getLinhaHeaderLoteAtual(), delimitador, null, true));
                            leitorFebraban.setLinhaHeaderLoteAtual(null);
                        }
                    }

                    numLinha++;
                }

                if (!TextHelper.isNull(leitor.getLinhaFooter())) {
                    if (loteFebraban) {
                        // Gera linha de saida para o footer
                        final LeitorArquivoFebraban leitorFebraban = (LeitorArquivoFebraban) leitor;
                        // Verifica se devem ser escritos os footers de lote
                        if (leitorFebraban.getLinhaFooterLoteAtual() != null) {
                            critica.put(numLinha++, gerarLinhaArquivoSaida(leitorFebraban.getLinhaFooterLoteAtual(), delimitador, null, loteFebraban));
                            leitorFebraban.setLinhaFooterLoteAtual(null);
                        }
                    }
                    // Imprime a linha de footer no arquivo
                    critica.put(numLinha++, gerarLinhaArquivoSaida(leitor.getLinhaFooter(), delimitador, null, loteFebraban));
                }

                leitor.encerraLeitura();
            } catch (final ParserException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ViewHelperException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
            }

            // Grava de crítica com o resultado dos comandos que retornaram algum erro
            LOG.debug("ARQUIVOS CRITICA: " + DateHelper.getSystemDatetime());

            final String nomeArqEntrada = nomeArquivoLote.substring(pastaArquivoLote.length());
            final String nomeArqSaida = pastaArquivoLote + ApplicationResourcesHelper.getMessage(validar ? "rotulo.nome.arquivo.validacao.prefixo" : "rotulo.nome.arquivo.critica.prefixo", responsavel)
                                      + nomeArqEntrada + "_" + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss");

            final String nomeArqSaidaTxt = nomeArqSaida + ".txt";
            LOG.debug("nomeArqSaidaTxt: " + nomeArqSaidaTxt);

            try (PrintWriter arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaidaTxt)))) {
                if (validar && !loteFebraban) {
                    arqSaida.println(formataMsgErro("", "-", TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.lote.arquivo", responsavel, nomeArqEntrada), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.lote.data", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "dd/MM/yyyy-HHmmss")), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro("", "-", TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.lote.total.registros.validados", responsavel, String.valueOf(totalRegistros)), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.lote.total.registros.validados.inclusao", responsavel, String.valueOf(totalIncluidos)), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.lote.total.registros.validados.alteracao", responsavel, String.valueOf(totalAlterados)), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.lote.total.registros.validados.exclusao", responsavel, String.valueOf(totalExcluidos)), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.lote.total.registros.validados.confirmacao", responsavel, String.valueOf(totalConfirmados)), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.lote.total.registros.invalidos", responsavel, String.valueOf(totalProblema)), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro("", "-", TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro("", COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                }

                // Imprime as linhas de critica no arquivo
                for (final String linha : critica.values()) {
                    arqSaida.println(linha);
                }
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ViewHelperException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
            }

            LOG.debug("FIM ARQUIVOS CRITICA: " + DateHelper.getSystemDatetime());

            try {
                // Compacta os arquvivos gerados em apenas um
                LOG.debug("compacta os arquivos: " + DateHelper.getSystemDatetime());
                nomeArqSaidaZip = nomeArqSaida + ".zip";
                FileHelper.zip(nomeArqSaidaTxt, nomeArqSaidaZip);
                LOG.debug("fim - compacta os arquivos: " + DateHelper.getSystemDatetime());
                FileHelper.delete(nomeArqSaidaTxt);
            } catch (final IOException ex) {
                // Ignora erro de compactação ou remoção do arquivo, já que o lote foi
                // processado corretamente, evitando demais problemas
                LOG.error(ex.getMessage(), ex);
            }
        }

        // Busca o registro de controle de processamento para atualizar o status e o nome do arquivo de crítica
        final ControleProcessamentoLote controleProcessamentoLote = loteController.findProcessamentoByArquivoeConsig(nomeArquivoLote);
        if (controleProcessamentoLote != null) {
            try {
                if (CanalEnum.WEB.equals(responsavel.getCanal())) {
                    // Se é processamento de lote via Web, remove o controle de processamento pois não existe
                    // operação de consumo do resultado, que pode ser acessado a qualquer momento pelo usuário.
                    loteController.excluirProcessamento(controleProcessamentoLote, responsavel);
                } else {
                    // Nos demais canais, muda o status para processado, que a operação de consumo do resultado
                    // irá alterar/remover o controle quando necessário
                    controleProcessamentoLote.setCplArquivoCritica(nomeArqSaidaZip);
                    controleProcessamentoLote.setCplStatus(CodedValues.CPL_PROCESSADO_SUCESSO);
                    loteController.alterarProcessamento(controleProcessamentoLote, responsavel);

                    // Quando está processamento uma validação de lote via rest, para não existir arquivos duplicados
                    // removemos do sistema o arquivo que foi feito upload para validação, pois, ou ele será novamente importado
                    // ou será um novo arquivo, talvez com o mesmo nome.
                    if (CanalEnum.REST.equals(responsavel.getCanal()) && validar) {
                        final String absolutePath = ParamSist.getDiretorioRaizArquivos();
                        if (!TextHelper.isNull(csaCodigo)) {
                            pastaArquivoLote = absolutePath + File.separatorChar + "lote" + File.separatorChar + tipoEntidade.toLowerCase() + File.separatorChar + codigoEntidade + File.separatorChar;
                            nomeArquivoLote = pastaArquivoLote + nomeArquivoEntrada;
                        } else {
                            pastaArquivoLote = absolutePath + File.separatorChar + "lote" + File.separatorChar + "cse" + File.separatorChar;
                            nomeArquivoLote = pastaArquivoLote + nomeArquivoEntrada;
                        }
                        FileHelper.delete(nomeArquivoLote);
                    }
                }
            } catch (final ZetraException | IOException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ViewHelperException(ex);
            }
        }

        if (!validar) {
            // Log do resultado geral da importação
            logResumoProcessoLote();

            // Executa desbloqueio automático da consignatária
            try {
                if (!TextHelper.isNull(csaCodigo)) {
                    final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
                    consignatariaController.verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel);
                }
            } catch (final ConsignatariaControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ViewHelperException(ex);
            }
        }

        if (!TextHelper.isNull(csaCodigo)) {
            LOG.debug("FIM IMPORTACAO: " + csaCodigo + " - " + DateHelper.getSystemDatetime());
        } else {
            LOG.debug("FIM IMPORTACAO MÚLTIPLAS CONSIGNATÁRIAS: " + " - " + DateHelper.getSystemDatetime());
        }

        return nomeArqSaidaZip;
    }

    /**
     * Se parâmetro de sistema TPC_LOTE_CONTEM_TODAS_ADES_ATIVAS estiver habilitado, indica que é uma importação
     * de lote de todos os contratos ativos no sistema. Se algum contrato ativo não tiver sido alterado em algum lote
     * no período (pode-se ter mais de uma importação por período, exemplo: um lote complementar), este será liquidado
     * ou cancelado
     * @param pathSaida - caminho para o arquivo de critica de liquidações/cancelamentos, caso houver
     * @param responsavel
     */
    private void excluiAdesNaoAlteradasLoteTotal(String csaCodigo, String corCodigo, String pathSaida, AcessoSistema responsavel) {
    	if (ParamSist.getBoolParamSist(CodedValues.TPC_LOTE_CONTEM_TODAS_ADES_ATIVAS, responsavel)) {

    		try {
    			final java.sql.Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(null, responsavel);

    			final List<TransferObject> adesNaoAlteradas = pesquisarConsignacaoController.lstContratosCsaSemOcorrencias(csaCodigo, corCodigo, List.of(CodedValues.TOC_ALTERACAO_VIA_LOTE_COM_TODOS_ADES),
    					CodedValues.SAD_CODIGOS_ATIVOS, periodoAtual, -1, -1, responsavel);

    			final Set<String> adesAguardandoDef = adesNaoAlteradas.stream().filter(ade -> CodedValues.SAD_CODIGOS_AGUARD_DEF.contains(ade.getAttribute(Columns.SAD_CODIGO)))
    					.map(ade -> (String)ade.getAttribute(Columns.ADE_CODIGO))
    					.collect(Collectors.toSet());

    			cancelaAdesNaoAlteradasTransacoesIndividuais(pathSaida, responsavel, periodoAtual,	adesAguardandoDef);

    			final Set<String> adesDeferidasAndamento = adesNaoAlteradas.stream().filter(ade -> CodedValues.SAD_CODIGOS_DEFERIDAS_OU_ANDAMENTO.contains(ade.getAttribute(Columns.SAD_CODIGO)))
    					.map(ade -> (String) ade.getAttribute(Columns.ADE_CODIGO))
    					.collect(Collectors.toSet());

    			liquidaAdesNaoAlteradasTransacoesIndividuais(pathSaida, responsavel, periodoAtual,	adesDeferidasAndamento);
    		} catch (PeriodoException | AutorizacaoControllerException ex) {
    			responsavel.setFunCodigo(CodedValues.FUN_IMPORTACAO_VIA_LOTE);
    			final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.IMPORTACAO_ARQ_LOTE, Log.LOG_ERRO);
    			try {
    				log.add(ApplicationResourcesHelper.getMessage("mensagem.lote.erro.busca.valores.periodo", responsavel));
    				log.write();
    			} catch (final LogControllerException e) {
    				LOG.error(e.getMessage(), e);
    			}
    		}
    	}
    }

    /**
     * cancela ades não alteradas no lote em transações de banco individuais para cada ade
     * @param pathSaida
     * @param responsavel
     * @param periodoExportacao
     * @param adesAguardandoDef
     */
    private void cancelaAdesNaoAlteradasTransacoesIndividuais(String pathSaida, AcessoSistema responsavel, Date periodoExportacao, Set<String> adesAguardandoDef) {
    	PrintWriter arqCritica = null;

    	try {
    		for (final String naoDeferida: adesAguardandoDef) {
    			try {
    				cancelarController.cancelar(naoDeferida, responsavel);
    			} catch (final AutorizacaoControllerException e) {
    				if (arqCritica == null) {
    					try {
    						arqCritica = new PrintWriter(new BufferedWriter(new FileWriter(pathSaida + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.lote.total.nao.canceladas", responsavel,
    						        DateHelper.format(periodoExportacao, "dd_MM_yyyy")))));
    					} catch (final IOException e1) {
    						LOG.error(e1.getMessage(), e1);
    					}
    				}

    				if (arqCritica != null) {
    					arqCritica.println(ApplicationResourcesHelper.getMessage("mensagem.lote.erro.ade.nao.pode.cancelar", responsavel, naoDeferida, e.getMessage()));
    				}
    			}
    		}
    	} finally {
    		if (arqCritica != null) {
    			arqCritica.close();
    		}
    	}
    }

	/**
     * liquida ades não alteradas no lote em transações de banco individuais para cada ade
     * @param pathSaida
     * @param responsavel
     * @param periodoExportacao
     * @param adesAguardandoDef
     */
	private void liquidaAdesNaoAlteradasTransacoesIndividuais(String pathSaida, AcessoSistema responsavel, Date periodoExportacao, Set<String> adesDeferidasAndamento) {
		PrintWriter arqCritica = null;

		try {
			for (final String naoDeferida: adesDeferidasAndamento) {
				try {
				    liquidarController.liquidar(naoDeferida, null, null, responsavel);
				} catch (final AutorizacaoControllerException e) {
					if (arqCritica == null) {
						try {
							arqCritica = new PrintWriter(new BufferedWriter(new FileWriter(pathSaida + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.lote.total.nao.liquidadas", responsavel,
							        DateHelper.format(periodoExportacao, "dd_MM_yyyy")))));
						} catch (final IOException e1) {
							LOG.error(e1.getMessage(), e1);
						}
					}

					if (arqCritica != null) {
						arqCritica.println(ApplicationResourcesHelper.getMessage("mensagem.lote.erro.ade.nao.pode.liquidar", responsavel, naoDeferida, e.getMessage()));
					}

				}
			}
		} finally {
			if (arqCritica != null) {
				arqCritica.close();
			}
		}
	}

	private void logResumoProcessoLote() {
        responsavel.setFunCodigo(CodedValues.FUN_IMPORTACAO_VIA_LOTE);
        final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.IMPORTACAO_ARQ_LOTE, Log.LOG_INFORMACAO);
        try {
            log.add(ApplicationResourcesHelper.getMessage("mensagem.lote.resumo.importacao", responsavel, String.valueOf(totalRegistros), String.valueOf(totalIncluidos), String.valueOf(totalAlterados), String.valueOf(totalExcluidos), String.valueOf(totalConfirmados), String.valueOf(totalProblema)));
            log.write();
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Monta criterio de busca de consignações para alteração ou exclusão
     * @param entradaValida
     * @param operacao
     * @return
     */
    private CustomTransferObject montaCriterioDeBusca(Map<String, Object> entradaValida, String operacao) {
        final BigDecimal adeVlr = (BigDecimal) entradaValida.get(ADE_VLR);
        BigDecimal adeVlrVerificar = (BigDecimal) entradaValida.get("ADE_VLR_VERIFICAR");
        final String adeIndice = (String) entradaValida.get(ADE_INDICE);
        final Date adeAnoMesIni = (Date) entradaValida.get(ADE_ANO_MES_INI);
        final String codReg = (entradaValida.get(COD_REG) != null) && !"".equals(entradaValida.get(COD_REG).toString()) ? entradaValida.get(COD_REG).toString() : CodedValues.COD_REG_DESCONTO;
        final String adeIdentificador = (String) entradaValida.get(ADE_IDENTIFICADOR_VERIFICAR);

        // Utiliza a chave de ADE_VLR ou ADE_VLR_VERIFICAR na busca para excluir ou alterar um contrato
        if (adeVlrVerificar != null) {
            // Se o adeVlrVerificar foi mapeado, verifica se ele é maior que zero
            adeVlrVerificar = (adeVlrVerificar.signum() == 1) ? adeVlrVerificar : null;
        } else if ((adeVlr != null) && (adeVlr.signum() == 1) && EXCLUSAO.equalsIgnoreCase(operacao)) {
            // Se o adeVlr é diferente de nulo e é positivo (maior que zero)
            // utiliza o adeVlr como chave, SOMENTE EM OPERAÇÃO DE EXCLUSÃO,
            // POIS NA ALTERAÇÃO O VALOR ENVIADO É O NOVO VALOR.
            adeVlrVerificar = adeVlr;
        }

        // Lista os criterios para a busca do contrato a alterar/excluir
        final CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("lote", Boolean.TRUE);
        criterio.setAttribute("tipo", tipoEntidade);
        criterio.setAttribute("codigo", codigoEntidade);
        criterio.setAttribute(OPERACAO, operacao);
        criterio.setAttribute(Columns.ADE_INDICE, adeIndice);
        criterio.setAttribute(Columns.ADE_ANO_MES_INI, DateHelper.format(adeAnoMesIni, YYYY_MM_DD));
        criterio.setAttribute(Columns.ADE_VLR, (adeVlrVerificar != null ? adeVlrVerificar.toString() : null));
        criterio.setAttribute(Columns.ADE_COD_REG, codReg);
        criterio.setAttribute(Columns.ADE_IDENTIFICADOR, adeIdentificador);

        return criterio;
    }

    private static String gerarLinhaArquivoSaida(String linha, String delimitador, String mensagem, boolean loteFebraban) {
        if (!loteFebraban) {
            // Se não for lote febraban, concatena a mensagem de erro no final da linha de entrada
            mensagem = (mensagem == null ? "" : mensagem);
            return (linha + delimitador + formataMsgErro(mensagem, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
        } else {
            // Se lote febraban, se ocorrencia nula, então coloca ocorrencia de sucesso
            mensagem = (mensagem == null ? Analisador.OCORRENCIA_SUCESSO : mensagem);
            // Monta a linha de saida, colocando as ocorrencias no lugar certo
            return (linha.substring(0, Analisador.POSICAO_INICIO_OCORRENCIAS) + formataMsgErro(mensagem, Analisador.COMPLEMENTO_DEFAULT_CAMPO_OCORRENCIAS, Analisador.TAMANHO_CAMPO_OCORRENCIAS, true));
        }
    }

    private static String formataMsgErro(String mensagem, String complemento, int tamanho, boolean alinhaEsquerda) {
        mensagem = (mensagem == null ? "" : mensagem);
        return TextHelper.removeAccent(TextHelper.formataMensagem(mensagem, complemento, tamanho, alinhaEsquerda)).toUpperCase();
    }

    private void salvarCriticaLoteFebraban(String msgCritica, boolean expandir) {
        try {
            final String msg = expandir ? ApplicationResourcesHelper.getMessage(msgCritica + ZetraException.MENSAGEM_LOTE_FEBRABAN, responsavel) : msgCritica;
            loteController.atualizarBlocoLote(nomeArquivoLote, numLinhaCorrente, StatusBlocoProcessamentoEnum.PROCESSADO_COM_SUCESSO, msg, responsavel);
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private void salvarCriticaLotePadrao(String msgCritica) {
        try {
            loteController.atualizarBlocoLote(nomeArquivoLote, numLinhaCorrente, StatusBlocoProcessamentoEnum.PROCESSADO_COM_SUCESSO, msgCritica, responsavel);
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Método que fará a inserção de uma nova reserva a partir de uma linha de lote.
     * @param entradaValida
     * @param dadosServidorConvenio
     * @throws AutorizacaoControllerException
     * @throws ViewHelperException
     * @throws PeriodoException
     * @throws BeneficioControllerException
     * @throws ParametroControllerException
     */
    private void insereReservaMargem(Map<String, Object> entradaValida, Map<String, Object> dadosServidorConvenio) throws AutorizacaoControllerException, ViewHelperException, PeriodoException, BeneficioControllerException, ParametroControllerException, FindException {
        AcessoSistema responsavelReserva = null;
        try {
            responsavelReserva = (AcessoSistema) responsavel.clone();
            responsavelReserva.setFunCodigo(CodedValues.FUN_INCLUSAO_VIA_LOTE);
        } catch (final CloneNotSupportedException e1) {
            throw new ViewHelperException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel);
        }

        String mensagemCritica = null;

        final String codReg = !TextHelper.isNull(entradaValida.get(COD_REG)) ? entradaValida.get(COD_REG).toString() : CodedValues.COD_REG_DESCONTO;
        final String cnvCodigo = dadosServidorConvenio.get(Columns.CNV_CODIGO).toString();
        final String svcCodigo = dadosServidorConvenio.get(Columns.SVC_CODIGO).toString();
        final String rseCodigo = dadosServidorConvenio.get(Columns.RSE_CODIGO).toString();
        final String orgCodigo = dadosServidorConvenio.get(Columns.ORG_CODIGO).toString();

        final Map<String, Object> paramCnv = cacheParametrosCnv.get(cnvCodigo);

        // Filtra da lista de tuplas os registros ligados a entidades não ativas
        validaEntidadesAtivas(dadosServidorConvenio, entradaValida);

        // Valida os prazos para o serviço alvo
        validaPrazoCadastrado(entradaValida, dadosServidorConvenio);

        // Valida data de nascimento quando obrigatória na reserva
        validaDataNasc(entradaValida, paramCnv, dadosServidorConvenio);

        // Se não for estorno:
        if (!CodedValues.COD_REG_ESTORNO.equals(codReg)) {
            // Valida o vínculo do servidor com os bloqueios de vínculo da entidade
            validaVinculoServidor(entradaValida, dadosServidorConvenio);

            // Valida informações bancárias quando obrigatórias
            loteController.validaInfBancariaObrigatoria(dadosServidorConvenio, paramCnv, entradaValida);

            // Valuda informações obrigatórias do convênio/serviço
            loteController.validaInfObrigatoriaCnv(paramCnv, entradaValida);
        }

        // TPS_MAX_PRAZO
        final String tpsMaxPazo = (String) paramCnv.get(CodedValues.TPS_MAX_PRAZO);
        final int maxPrazo = !TextHelper.isNull(tpsMaxPazo) ? Integer.parseInt(tpsMaxPazo) : -1;

        // Obtém o prazo do contrato
        Integer adePrazo = (Integer) entradaValida.get(ADE_PRAZO);
        // Se o prazo foi informado, mas é maior que 99, então o prazo deve ser indeterminado se o serviço permitir
        if ((adePrazo != null) && (adePrazo.intValue() > 99) && !permitePrazoMaior99(csaCodigoCorrente, responsavel) && (maxPrazo == 0)) {
            adePrazo = null;
        }

        final String adeTipoVlr = paramCnv.containsKey(CodedValues.TPS_TIPO_VLR) && (paramCnv.get(CodedValues.TPS_TIPO_VLR) != null) ? paramCnv.get(CodedValues.TPS_TIPO_VLR).toString() : CodedValues.TIPO_VLR_FIXO;
        Short adeIntFolha = paramCnv.containsKey(CodedValues.TPS_INTEGRA_FOLHA) && (paramCnv.get(CodedValues.TPS_INTEGRA_FOLHA) != null) && !"".equals(paramCnv.get(CodedValues.TPS_INTEGRA_FOLHA)) ? Short.valueOf(paramCnv.get(CodedValues.TPS_INTEGRA_FOLHA).toString()) : CodedValues.INTEGRA_FOLHA_SIM;
        Short adeIncMargem = paramCnv.containsKey(CodedValues.TPS_INCIDE_MARGEM) && (paramCnv.get(CodedValues.TPS_INCIDE_MARGEM) != null) && !"".equals(paramCnv.get(CodedValues.TPS_INCIDE_MARGEM)) ? Short.valueOf(paramCnv.get(CodedValues.TPS_INCIDE_MARGEM).toString()) : CodedValues.INCIDE_MARGEM_SIM;

        BigDecimal adeVlr = (BigDecimal) entradaValida.get(ADE_VLR);
        final BigDecimal adeTaxaJuros = (BigDecimal) entradaValida.get(ADE_TAXA_JUROS);
        final BigDecimal adeVlrLiquido = (BigDecimal) entradaValida.get(ADE_VLR_LIQUIDO);
        final BigDecimal adeVlrTac = (BigDecimal) entradaValida.get(ADE_VLR_TAC);
        final BigDecimal adeVlrMensVinc = (BigDecimal) entradaValida.get(ADE_VLR_MENS_VINC);
        final BigDecimal adeVlrIof = (BigDecimal) entradaValida.get(ADE_VLR_IOF);
        Integer adeCarencia = (Integer) entradaValida.get(ADE_CARENCIA);
        final String adePeriodicidade = (String) entradaValida.get(ADE_PERIODICIDADE);
        final String adeIndice = (String) entradaValida.get(ADE_INDICE);
        final String adeIdentificador = (String) entradaValida.get(ADE_IDENTIFICADOR);
        final Date adeAnoMesFim = (Date) entradaValida.get(ADE_ANO_MES_FIM);
        Date adeAnoMesIni = (Date) entradaValida.get(ADE_ANO_MES_INI);
        final java.sql.Date adeAnoMesIniRef = entradaValida.get(ADE_ANO_MES_INI_REF) != null ? DateHelper.toSQLDate((Date) entradaValida.get(ADE_ANO_MES_INI_REF)) : null;
        final java.sql.Date adeAnoMesFimRef = entradaValida.get(ADE_ANO_MES_FIM_REF) != null ? DateHelper.toSQLDate((Date) entradaValida.get(ADE_ANO_MES_FIM_REF)) : null;
        final String modalidadeOp = (String) entradaValida.get(MODALIDADE_OPERACAO);
        final String matriculaSerCsa = (String) entradaValida.get(MATRICULA_SER_NA_CSA);
        String sadCodigo = null;
        final String tmoCodigo = (String) entradaValida.get(Columns.TMO_CODIGO);

        // Dados bancários do servidor
        final String rseBancoSal = (String) entradaValida.get(RSE_BANCO_SAL);
        final String rseAgenciaSal = (String) entradaValida.get(RSE_AGENCIA_SAL);
        final String rseContaSal = (String) entradaValida.get(RSE_CONTA_SAL);

        // Dados para reserva de benefícios
        final String cbeCodigo = (String) entradaValida.get(CBE_CODIGO);
        final String tlaCodigo = (String) entradaValida.get(TLA_CODIGO);

        /*
         * Se o serviço exige a senha de autorização do servidor mas o parâmetro da
         * consignatária permite que ela faça sem a senha do servidor, então supõe-se
         * que a senha do servidor no sistema da consignatária é equivalente à senha
         * do Gestor.
         */
        boolean comSerSenha = false;
        String senhaServidor = null;
        try {
            if (parametroController.senhaServidorObrigatoriaReserva(rseCodigo, svcCodigo, csaCodigoCorrente, responsavelReserva)) {
                final String rseMatricula = (String) entradaValida.get(RSE_MATRICULA);
                senhaServidor = (String) entradaValida.get("SENHA_SERVIDOR");

                // Valida senha
                SenhaHelper.validarSenhaServidor(rseCodigo, senhaServidor, responsavelReserva.getIpUsuario(), rseMatricula, svcCodigo, true, false, responsavelReserva);
                comSerSenha = true;
            } else if (parametroController.senhaServidorObrigatoriaReserva(rseCodigo, svcCodigo, null, responsavelReserva)) {
                comSerSenha = true;
            }
        } catch (ParametroControllerException | UsuarioControllerException ex) {
            throw new ViewHelperException(ex);
        }

        // Calcula o periodo atual de lançamentos
        final Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavelReserva);
        boolean forcaPeriodoLancamentoCartao = false;
        // Se foi informada a data inicial, verifica se ela é válida
        if (adeAnoMesIni != null) {
            if (!permiteLoteAtrasado) {
                // Permite ou não data inicial anterior ao período atual de acordo com o parâmetro de serviço TPS_PERMITE_DATA_RETROATIVA_IMP_LOTE
                final String permiteDataRetroativa = (String) paramCnv.get(CodedValues.TPS_PERMITE_DATA_RETROATIVA_IMP_LOTE);
                if ((TextHelper.isNull(permiteDataRetroativa) || CodedValues.NAO_PERMITE_DATA_RETROATIVA.equals(permiteDataRetroativa)) && (DateHelper.dayDiff(adeAnoMesIni, periodoAtual) < 0)) {
                    throw new ViewHelperException("mensagem.erro.lote.data.inicial.invalida.anterior.periodo.atual", responsavelReserva);
                }
            } else if (periodoConfiguravel != null) {
                adeAnoMesIni = periodoConfiguravel;
                forcaPeriodoLancamentoCartao = true;
            }
        } else if (permiteLoteAtrasado && (periodoConfiguravel != null)) {
            adeAnoMesIni = periodoConfiguravel;
            forcaPeriodoLancamentoCartao = true;
        }

        // Se não informada a carência, mas foi informada a data inicial,
        // então calcula o valor da carência ser validada
        if (adeCarencia == null) {
            if (adeAnoMesIni != null) {
                // O cálculo deve ser menos 1, pois o cálculo de prazo será sempre 1 quando as
                // datas são iguais, e no caso da carência, quando as datas forem iguais, será zero
                adeCarencia = PeriodoHelper.getInstance().calcularCarencia(orgCodigo, adeAnoMesIni, adePeriodicidade, responsavelReserva);

                // Não valida a carência negativa, pois o mesmo será validado pelo parâmetro que permite importação
                // de lote com data retroativa, e caso seja permitido, a carência com valor negativo não será aceita
                // pelo sistema, desta forma Zero deve ser informado.
                adeCarencia = (adeCarencia.intValue() < 0) ? 0 : adeCarencia;
            } else {
                // Se não foi informado carência nem data inicial, então a carência será
                // sempre zero, com primeiro desconto para periodo atual
                adeCarencia = 0;
            }
        }

        // Se é um serviço de cartão/provisionamento de margem, e permite redução do valor da parcela ...
        boolean reduziuValorLancamentoCartao = false;
        if (paramCnv.containsKey("SERVICO_CARTAOCREDITO") && permiteReducaoLancamentoCartao) {
            // ... busca a margem disponível de cartão para avaliar se é necessário reduzir o ADE_VLR
            final MargemTO margemCartao = validadorCartaoCreditoController.consultarMargemDisponivelLancamento(rseCodigo, csaCodigoCorrente, svcCodigo, permiteLoteAtrasado && (periodoConfiguravel != null) ? adeAnoMesIni : null, responsavelReserva);
            if ((margemCartao != null) && (margemCartao.getMrsMargemRest() != null) && (margemCartao.getMrsMargemRest().signum() > 0) && (adeVlr.compareTo(margemCartao.getMrsMargemRest()) > 0)) {
                // Valor da ADE de lançamento é maior que o valor reservado disponível. Ajusta o valor de acordo com o disponível
                adeVlr = margemCartao.getMrsMargemRest();
                reduziuValorLancamentoCartao = true;
                // Define mensagem de crítica
                if (validar) {
                    mensagemCritica = ApplicationResourcesHelper.getMessage("mensagem.aviso.valor.lancamento.cartao.sera.reduzido", responsavelReserva, NumberHelper.format(adeVlr.doubleValue(), NumberHelper.getLang()));
                } else {
                    mensagemCritica = ApplicationResourcesHelper.getMessage("mensagem.aviso.valor.lancamento.cartao.foi.reduzido", responsavelReserva, NumberHelper.format(adeVlr.doubleValue(), NumberHelper.getLang()));
                }
            }
        }

        // Valida os dados da nova reserva
        final CustomTransferObject reserva = new CustomTransferObject();
        reserva.setAttribute(ADE_PRAZO, adePrazo);
        reserva.setAttribute(ADE_PERIODICIDADE, adePeriodicidade);
        reserva.setAttribute(ADE_CARENCIA, adeCarencia);
        reserva.setAttribute("RSE_PRAZO", dadosServidorConvenio.get(Columns.RSE_PRAZO));
        reserva.setAttribute(ADE_VLR, adeVlr);
        reserva.setAttribute("RSE_CODIGO", rseCodigo);
        reserva.setAttribute("SVC_CODIGO", svcCodigo);
        reserva.setAttribute("CSE_CODIGO", CodedValues.CSE_CODIGO_SISTEMA);

        // Insere parâmetros de svc no map de dados da nova reserva para serem usados na validação
        dadosServidorConvenio.putAll(paramCnv);

        ReservaMargemHelper.validaReserva(dadosServidorConvenio, reserva, responsavelReserva, false, false);

        // Após a validação da reserva, verifica se esta é originada de uma renegociação,
        // e caso seja, pesquisa por contratos liquidados (ou abertos) para o servidor, para os
        // serviços relacionados em renegociação e caso existem prossegue com a reserva
        // para posteriormente relacioná-los.
        final boolean renegociacao = ((entradaValida.get(RENEGOCIACAO) != null) && _1_.equals(entradaValida.get(RENEGOCIACAO)));
        boolean contratosAbertos = false;
        List<String> adeCodigosRenegociacao = null;
        if (renegociacao) {
            adeCodigosRenegociacao = (List<String>) dadosServidorConvenio.get(ADE_LIQUIDADA_RENEGOCIACAO);
            if ((adeCodigosRenegociacao == null) || adeCodigosRenegociacao.isEmpty()) {
                // Não foi possível localizar as consignações liquidadas, pesquisa por consignações abertas
                // através do adeIdentificador informado, que deverá estar associado às consignações a serem renegociadas
                adeCodigosRenegociacao = (List<String>) dadosServidorConvenio.get(ADE_ABERTA_RENEGOCIACAO);
                if ((adeCodigosRenegociacao == null) || adeCodigosRenegociacao.isEmpty()) {
                    // Não encontrou consignações que possam ser renegociadas, nem liquidadas
                    // nem abertas, então emite mensagem de erro para o usuário
                    throw new AutorizacaoControllerException("mensagem.nenhumaConsignacaoEncontrada", responsavelReserva);
                } else {
                    // Os contratos a serem renegociados estão abertos, então a operação
                    // a ser realizada será uma renegociação
                    contratosAbertos = true;
                }
            }
        }

        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_DESCONTO_EM_FILA, CodedValues.TPC_SIM, responsavelReserva) && !renegociacao) {
            final String percentualBaseCalc = paramCnv.containsKey(CodedValues.TPS_PERCENTUAL_BASE_CALC_DESCONTO_EM_FILA) && (paramCnv.get(CodedValues.TPS_PERCENTUAL_BASE_CALC_DESCONTO_EM_FILA) != null) ? paramCnv.get(CodedValues.TPS_PERCENTUAL_BASE_CALC_DESCONTO_EM_FILA).toString() : null;
            final String baseCalc = paramCnv.containsKey(CodedValues.TPS_BASE_CALC_DESCONTO_EM_FILA) && (paramCnv.get(CodedValues.TPS_BASE_CALC_DESCONTO_EM_FILA) != null) ? paramCnv.get(CodedValues.TPS_BASE_CALC_DESCONTO_EM_FILA).toString() : null;

            if (!TextHelper.isNull(percentualBaseCalc) && !TextHelper.isNull(baseCalc)) {
                // Se o sistema permite módulo de desconto em fila e o serviço está configurado para realizar a fila
                // define a não incidência de margem, não integração com a folha, prazo fixo igual a 1 e status Aguard. Margem
                sadCodigo = CodedValues.SAD_AGUARD_MARGEM;
                adeIncMargem = CodedValues.INCIDE_MARGEM_NAO;
                adeIntFolha = CodedValues.INTEGRA_FOLHA_NAO;
                adePrazo = 1;
            }
        }

        // Se é uma renegociação, define a ação correta para não bloquear uma reserva
        // em serviço que não permite inclusão, apenas renegociação
        final String acao = (renegociacao ? RENEGOCIAR : RESERVAR);

        // Objeto de parâmetro de reserva de margem
        final ReservarMargemParametros reservaParam;
        if (renegociacao && contratosAbertos) {
            reservaParam = new RenegociarConsignacaoParametros();
            ((RenegociarConsignacaoParametros) reservaParam).setCompraContrato(Boolean.FALSE);
            reservaParam.setAdeCodigosRenegociacao(adeCodigosRenegociacao);
            ((RenegociarConsignacaoParametros) reservaParam).setTipo(responsavelReserva.getTipoEntidade());
        } else {
            reservaParam = new ReservarMargemParametros();
        }

        reservaParam.setRseCodigo(rseCodigo);
        reservaParam.setAdeVlr(adeVlr);
        reservaParam.setCorCodigo(corCodigo);
        reservaParam.setAdePrazo(adePrazo);
        reservaParam.setAdeAnoMesIni(adeAnoMesIni);
        reservaParam.setAdeAnoMesFim(adeAnoMesFim);
        reservaParam.setAdeCarencia(parametroController.calcularAdeCarenciaDiaCorteCsa(adeCarencia, csaCodigoCorrente, orgCodigo, responsavelReserva));
        reservaParam.setAdeIdentificador(adeIdentificador);
        reservaParam.setCnvCodigo(cnvCodigo);
        reservaParam.setSadCodigo(sadCodigo);
        reservaParam.setSerSenha(senhaServidor);
        reservaParam.setComSerSenha(comSerSenha);
        reservaParam.setAdeTipoVlr(adeTipoVlr);
        reservaParam.setAdeIntFolha(adeIntFolha);
        reservaParam.setAdeIncMargem(adeIncMargem);
        reservaParam.setAdeIndice(adeIndice);
        reservaParam.setAdeVlrTac(adeVlrTac);
        reservaParam.setAdeVlrIof(adeVlrIof);
        reservaParam.setAdeVlrLiquido(adeVlrLiquido);
        reservaParam.setAdeVlrMensVinc(adeVlrMensVinc);
        reservaParam.setValidar(validar);
        reservaParam.setPermitirValidacaoTaxa(Boolean.TRUE);
        reservaParam.setParametros(dadosServidorConvenio);
        reservaParam.setSerAtivo(serAtivo);
        reservaParam.setCnvAtivo(cnvAtivo);
        reservaParam.setSerCnvAtivo(Boolean.TRUE);
        reservaParam.setSvcAtivo(Boolean.TRUE);
        reservaParam.setCsaAtivo(Boolean.TRUE);
        reservaParam.setOrgAtivo(Boolean.TRUE);
        reservaParam.setEstAtivo(Boolean.TRUE);
        reservaParam.setCseAtivo(Boolean.TRUE);
        reservaParam.setAdeAnoMesIniRef(adeAnoMesIniRef);
        reservaParam.setAdeAnoMesFimRef(adeAnoMesFimRef);
        reservaParam.setAcao(acao);
        reservaParam.setAdeCodReg(codReg);
        reservaParam.setAdeTaxaJuros(adeTaxaJuros);
        reservaParam.setAdeBanco(rseBancoSal);
        reservaParam.setAdeAgencia(rseAgenciaSal);
        reservaParam.setAdeConta(rseContaSal);
        reservaParam.setValidaAnexo(false);
        reservaParam.setAdePeriodicidade(adePeriodicidade);
        reservaParam.setTdaModalidadeOperacao(modalidadeOp);
        reservaParam.setTdaMatriculaSerCsa(matriculaSerCsa);
        reservaParam.setCbeCodigo(cbeCodigo);
        reservaParam.setTlaCodigo(tlaCodigo);
        reservaParam.setTmoCodigo(tmoCodigo);

        // Grava os dados de autorização, de forma genérica
        final List<TransferObject> dadList = autorizacaoController.lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.LOTE, svcCodigo, csaCodigoCorrente, responsavelReserva);
        for (final TransferObject dad : dadList) {
            final String tdaCodigo = (String)dad.getAttribute(Columns.TDA_CODIGO);
            final String valor = (String) entradaValida.get(DAD_VALOR + tdaCodigo);
            reservaParam.setDadoAutorizacao(tdaCodigo, valor);
        }

        reservaParam.setForcaPeriodoLancamentoCartao(forcaPeriodoLancamentoCartao);
        // Executa a operação de inclusão da nova consignação
        String adeCodigoNovo = null;

        // Exibe a margem disponível
        if (renegociacao && contratosAbertos && !validar) {
            // Executa uma renegociação, já que os contratos a serem renegociados
            // estão abertos no sistema.
            adeCodigoNovo = renegociarController.renegociar((RenegociarConsignacaoParametros) reservaParam, responsavelReserva);
        } else {
            try {
                adeCodigoNovo = reservarController.reservarMargem(reservaParam, responsavelReserva);
            } catch (final AutorizacaoControllerException e) {
                if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_MARGEM_DISPONIVEL_CRITICA_CARTAO, CodedValues.TPC_SIM, responsavelReserva) && e.getMessage().startsWith(ApplicationResourcesHelper.getMessage("mensagem.lote.inclusao.validada", responsavelReserva))) {
                    mensagemCritica = e.getMessage();
                } else {
                    throw e;
                }
            }

            if (!validar && renegociacao) {
                // Se é uma renegociação via lote, relaciona as consignações liquidadas previamente
                // com a nova consignação incluida.
                loteController.relacionarRenegociacaoViaLote(adeCodigoNovo, adeCodigosRenegociacao, svcCodigo, csaCodigoCorrente, responsavelReserva);
            }
            if (!validar && (entradaValida.get(ADE_MENSALIDADE_BENEFICIO) != null)) {
                // Cria relacionamento entre a mensalidade e o contrato secundário pelo tipo de lançamento
                loteController.relacionarMensalidadeViaLote(adeCodigoNovo, (String) entradaValida.get(ADE_MENSALIDADE_BENEFICIO), tlaCodigo, responsavelReserva);
            }
        }

        LOG.debug("ADE COD:" + adeCodigoNovo);

        // Estamos analisando se o modulo de benificios está ativada e o tla dele é de pro rata
        // Caso esteja vamos chamar o calculo de calcularSubsidioContratosBeneficiosProRata.
        // No metodo que é analisado a necessidade de executar ele ou não.
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, CodedValues.TPC_SIM, responsavelReserva) && !TextHelper.isNull(tlaCodigo)) {
            final TipoLancamento tipoLancamento = loteController.buscarTipoLancamentoPorTlaCodigo(tlaCodigo, responsavelReserva);
            if (CodedValues.TNT_BENEFICIO_PRO_RATA.contains(tipoLancamento.getTipoNatureza().getTntCodigo())) {
                calcularSubsidioBeneficioController.calcularSubsidioContratosBeneficiosProRata(validar, adeCodigoNovo, orgCodigo, responsavelReserva);
            }
        }

        if (!validar) {
            geraOcorrenciaLoteTotal(adeCodigoNovo, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.oca.obs.inclusao.contrato.lote.total", responsavelReserva), responsavelReserva);
        }

        totalIncluidos++;
        totalRegistros++;
        if (validar && !loteFebraban) {
            if (TextHelper.isNull(mensagemCritica)) {
                mensagemCritica = CodedValues.COD_REG_ESTORNO.equals(codReg) ? ApplicationResourcesHelper.getMessage("mensagem.lote.inclusao.validada.credito", responsavelReserva) : ApplicationResourcesHelper.getMessage("mensagem.lote.inclusao.validada", responsavelReserva);
            }
            salvarCriticaLotePadrao(mensagemCritica);
        }
        if (!validar && reduziuValorLancamentoCartao && !TextHelper.isNull(mensagemCritica)) {
            salvarCriticaLotePadrao(mensagemCritica);
        }
        if (loteFebraban) {
            salvarCriticaLoteFebraban("mensagem.inclusaoValidada", true);
        }

        if ((!TextHelper.isNull(csaCodigoCorrente) && !validar && !loteFebraban) && insereAdeNumeroCriticaLote(csaCodigoCorrente, responsavel)) {
            final TransferObject ade = pesquisarConsignacaoController.findAutDesconto(adeCodigoNovo, responsavelReserva);
            final String mensagemInclusaoAdeNumero = ApplicationResourcesHelper.getMessage("mensagem.lote.inclusao.realizada.ade.numero", responsavelReserva, String.valueOf(ade.getAttribute(Columns.ADE_NUMERO)));
            salvarCriticaLotePadrao(mensagemInclusaoAdeNumero);
        }
    }

    /** se parâmetro de sistema TPC_LOTE_CONTEM_TODAS_ADES_ATIVAS estiver ativo,
     gera uma ocorrência de que esta ade foi incluída via lote de toda carteira ativa
     ao final deste tipo de importação de lote, todos contratos ativos sem esta ocorrência
     serão liquidados ou cancelados.
     */
    private void geraOcorrenciaLoteTotal(String adeCodigoNovo, String ocaObs, AcessoSistema responsavel) throws AutorizacaoControllerException {
    	if (ParamSist.getBoolParamSist(CodedValues.TPC_LOTE_CONTEM_TODAS_ADES_ATIVAS, responsavel)) {
    		try {
    			final java.sql.Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(null, responsavel);
    			if (periodoAtual != null) {
    				autorizacaoController.criaOcorrenciaADE(adeCodigoNovo, CodedValues.TOC_ALTERACAO_VIA_LOTE_COM_TODOS_ADES, ocaObs, periodoAtual, responsavel);
    			}
    		} catch (final PeriodoException e) {
    			LOG.error(e.getMessage(), e);
    		}
    	}
    }

    /**
     * Faz a alteração de uma consignação a partir de uma linha do arquivo de lote, representado pelo parâmetro entradaValida.
     * @param entradaValida
     * @param paramCnvAlteracao
     * @throws AutorizacaoControllerException
     * @throws ViewHelperException
     */
    private void alterarConsignacao(Map<String, Object> entradaValida, Map<String, Object> paramCnvAlteracao) throws AutorizacaoControllerException, ViewHelperException {
        final Map<String, Object> parametros = cacheParametrosCnv.get(paramCnvAlteracao.get(Columns.CNV_CODIGO));

        final BigDecimal adeVlr = (BigDecimal) entradaValida.get(ADE_VLR);
        final BigDecimal adeTaxaJuros = (BigDecimal) entradaValida.get(ADE_TAXA_JUROS);
        final BigDecimal adeVlrLiquido = (BigDecimal) entradaValida.get(ADE_VLR_LIQUIDO);
        final BigDecimal adeVlrTac = (BigDecimal) entradaValida.get(ADE_VLR_TAC);
        final BigDecimal adeVlrMensVinc = (BigDecimal) entradaValida.get(ADE_VLR_MENS_VINC);
        final BigDecimal adeVlrIof = (BigDecimal) entradaValida.get(ADE_VLR_IOF);
        final String adeIndice = (String) entradaValida.get(ADE_INDICE);
        final String adeIdentificador = (String) entradaValida.get(ADE_IDENTIFICADOR);
        final Date adeAnoMesFim = (Date) entradaValida.get(ADE_ANO_MES_FIM);
        final String modalidadeOp = (String) entradaValida.get(MODALIDADE_OPERACAO);
        final String matriculaSerCsa = (String) entradaValida.get(MATRICULA_SER_NA_CSA);

        final String codReg = (entradaValida.get(COD_REG) != null) && !"".equals(entradaValida.get(COD_REG).toString()) ? entradaValida.get(COD_REG).toString() : CodedValues.COD_REG_DESCONTO;

        // TPS_MAX_PRAZO
        final String tpsMaxPazo = (String) parametros.get(CodedValues.TPS_MAX_PRAZO);
        final int maxPrazo = !TextHelper.isNull(tpsMaxPazo) ? Integer.parseInt(tpsMaxPazo) : -1;

        // Obtém o prazo do contrato
        Integer adePrazo = (Integer) entradaValida.get(ADE_PRAZO);
        // Se o prazo foi informado, mas é maior que 99, então o prazo deve ser indeterminado se o serviço permitir
        if ((adePrazo != null) && (adePrazo.intValue() > 99) && !permitePrazoMaior99(csaCodigoCorrente, responsavel) && (maxPrazo == 0)) {
            adePrazo = null;
        }

        // Inclui parâmetros de serviços no Map de alteração os quais serão consultados na alteração
        paramCnvAlteracao.putAll(parametros);

        // Faz a alteração do contrato alvo
        final String adeCodigo = (String) paramCnvAlteracao.get(Columns.ADE_CODIGO);

        AlterarConsignacaoParametros alterarParam = null;

        final boolean alteracaoAvancada = responsavel.isCseSup() && (!TextHelper.isNull(entradaValida.get(ALTERACAO_AVANCADA)) &&
                _1_.equals(entradaValida.get(ALTERACAO_AVANCADA)));

        final String tmoCodigo = (String) entradaValida.get(Columns.TMO_CODIGO);
        if (TextHelper.isNull(tmoCodigo) && alteracaoAvancada) {
            throw new ViewHelperException("mensagem.informe.motivo.operacao.alteracao.avancada", responsavel);
        }

        // trata campos de alteração avançada
        if (responsavel.isCseSup() && responsavel.temPermissao(CodedValues.FUN_ALT_AVANCADA_CONSIGNACAO) && alteracaoAvancada) {
            alterarParam = new AlterarConsignacaoParametros(adeCodigo, adeVlr, adePrazo, adeIdentificador, validar, adeIndice, adeVlrTac, adeVlrIof, adeVlrLiquido, adeVlrMensVinc, adeTaxaJuros, adeAnoMesFim, paramCnvAlteracao);

            alterarParam.setAdeIncideMargem(!TextHelper.isNull(entradaValida.get("ADE_INC_MARGEM")) ?
                    ((Integer) entradaValida.get("ADE_INC_MARGEM")).shortValue() : (Short) paramCnvAlteracao.get(Columns.ADE_INC_MARGEM));
            alterarParam.setAdeIntFolha(!TextHelper.isNull(entradaValida.get("ADE_INT_FOLHA")) ?
                    ((Integer) entradaValida.get("ADE_INT_FOLHA")).shortValue() : (Short) paramCnvAlteracao.get(Columns.ADE_INT_FOLHA));
            alterarParam.setPermitePrzNaoCadastrado(!TextHelper.isNull(entradaValida.get(ADE_PRZ_NAO_CADASTRADO)) && _1_.equals(entradaValida.get(ADE_PRZ_NAO_CADASTRADO)));
            alterarParam.setAlteraMargem(!TextHelper.isNull(entradaValida.get("ADE_ALTERA_MARGEM")) && !_1_.equals(entradaValida.get("ADE_ALTERA_MARGEM")) ?
                    false : AlterarConsignacaoParametros.PADRAO_ALTERA_MARGEM);
            alterarParam.setExigeSenha(false); // ateração avançada via lote nunca exigirá senha

            alterarParam.setValidaMargem(!TextHelper.isNull(entradaValida.get("ADE_VALIDA_MARGEM"))
                    && !_1_.equals(entradaValida.get("ADE_VALIDA_MARGEM")) ?
                            false : AlterarConsignacaoParametros.PADRAO_VALIDA_MARGEM);
            alterarParam.setAlterarValorPrazoSemLimite(!TextHelper.isNull(entradaValida.get("ADE_ALT_VLR_PRZ_SEM_LIMITE"))
                    && _1_.equals(entradaValida.get("ADE_ALT_VLR_PRZ_SEM_LIMITE")) ?
                            true : AlterarConsignacaoParametros.PADRAO_ALTERA_VALOR_PRAZO_SEM_LIMITE);
            alterarParam.setCriarNovoContratoDif(!TextHelper.isNull(entradaValida.get("ADE_CRIAR_NOVO_CONTRATO_DIF"))
                    && _1_.equals(entradaValida.get("ADE_CRIAR_NOVO_CONTRATO_DIF")) ?
                            true : AlterarConsignacaoParametros.PADRAO_CRIAR_NOVO_CONTRATO_DIF);
            alterarParam.setIncluiOcorrencia(!TextHelper.isNull(entradaValida.get("ADE_INC_OCORRENCIA"))
                    && !_1_.equals(entradaValida.get("ADE_INC_OCORRENCIA")) ?
                            false : AlterarConsignacaoParametros.PADRAO_INCLUI_OCORRENCIA);
            if (!TextHelper.isNull(entradaValida.get("ADE_SAD_CODIGO"))) {
                alterarParam.setNovaSituacaoContrato((String) entradaValida.get("ADE_SAD_CODIGO"));
            }
            alterarParam.setValidaTaxaJuros(!TextHelper.isNull(entradaValida.get("ADE_VALIDA_TAXA"))
                    && !_1_.equals(entradaValida.get("ADE_VALIDA_TAXA")) ?
                            false : AlterarConsignacaoParametros.PADRAO_VALIDA_TAXA_JUROS);
            alterarParam.setPermiteAltEntidadesBloqueadas(!TextHelper.isNull(entradaValida.get(ADE_ALT_ENT_BLOQUEADAS))
                    && _1_.equals(entradaValida.get(ADE_ALT_ENT_BLOQUEADAS)) ?
                            true : AlterarConsignacaoParametros.PADRAO_PERMITE_ALT_ENTIDADES_BLOQUEADAS);
            alterarParam.setValidaLimiteAde(!TextHelper.isNull(entradaValida.get("VALIDA_LIMITE_ADE"))
                    && _1_.equals(entradaValida.get("VALIDA_LIMITE_ADE")) ?
                            false : AlterarConsignacaoParametros.PADRAO_VALIDA_LIMITE_ADE);
        } else {
            alterarParam = new AlterarConsignacaoParametros(adeCodigo, adeVlr, adePrazo, adeIdentificador, validar, adeIndice, adeVlrTac, adeVlrIof, adeVlrLiquido, adeVlrMensVinc, adeTaxaJuros, adeAnoMesFim, paramCnvAlteracao);

            // seta dados de autorização para sistemas em que estiver configurado
            if (responsavel.isCsaCor()) {
                if (exigeModalidadeOperacao(csaCodigoCorrente, responsavel)) {
                    if (!TextHelper.isNull(modalidadeOp)) {
                        alterarParam.setTdaModalidadeOperacao(modalidadeOp);
                    } else {
                        final String modalidadeOpOld = autorizacaoController.getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_MODALIDADE_OPERACAO, false, responsavel);
                        if (!TextHelper.isNull(modalidadeOpOld)) {
                            alterarParam.setTdaModalidadeOperacao(modalidadeOpOld);
                        }
                    }
                }

                if (exigeMatriculaSerCsa(csaCodigoCorrente, responsavel)) {
                    if (!TextHelper.isNull(matriculaSerCsa)) {
                        alterarParam.setTdaMatriculaSerCsa(matriculaSerCsa);
                    } else {
                        final String matriculaSerCsaOld = autorizacaoController.getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_MATRICULA_SER_NA_CSA, false, responsavel);
                        if (!TextHelper.isNull(matriculaSerCsaOld)) {
                            alterarParam.setTdaMatriculaSerCsa(matriculaSerCsaOld);
                        }
                    }
                }
            }
        }

        if ((responsavel.isCseSup() || responsavel.isCsaCor()) && permiteLoteAtrasado && (periodoConfiguravel != null)) {
            alterarParam.setOcaPeriodo(DateHelper.format(periodoConfiguravel,YYYY_MM_DD));
            alterarParam.setAlteracaoForcaPeriodo(true);
        }

        alterarParam.setAlteracaoAvancada(alteracaoAvancada);
        alterarParam.setTmoCodigo(tmoCodigo);
        alterarParam.setAdePeriodicidade((String) paramCnvAlteracao.get(Columns.ADE_PERIODICIDADE));
        alterarController.alterar(alterarParam, responsavel);

        totalAlterados++;
        totalRegistros++;
        if (validar && !loteFebraban) {
            final String mensagem = CodedValues.COD_REG_ESTORNO.equals(codReg) ? ApplicationResourcesHelper.getMessage("mensagem.lote.alteracao.validada.credito", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.lote.alteracao.validada", responsavel);
            salvarCriticaLotePadrao(mensagem);
        }

        if (loteFebraban) {
            salvarCriticaLoteFebraban("mensagem.alteracaoValidada", true);
        }
    }

    /**
     * Faz a exclusão de nova consignação a partir de uma linha do arquivo de lote, representado pelo parâmetro entradaValida.
     * @param entradaValida
     * @param adeExclusaoParam
     * @throws AutorizacaoControllerException
     * @throws ViewHelperException
     */
    private void excluirConsignacao(Map<String, Object> entradaValida, Map<String, Object> adeExclusaoParam) throws AutorizacaoControllerException, ViewHelperException {

        final String adeIdentificadorReneg = (String) entradaValida.get("ADE_IDENTIFICADOR_RENEG");

        LOG.debug("ADE COD:"+ adeExclusaoParam.get(Columns.ADE_CODIGO));

        final String adeCodigo = adeExclusaoParam.get(Columns.ADE_CODIGO).toString();

        if (TextHelper.isNull(adeIdentificadorReneg)) {
            // Verifica se tem motivo informado
            CustomTransferObject motivo = null;
            if (entradaValida.get(Columns.TMO_CODIGO) != null) {
                motivo = new CustomTransferObject();
                motivo.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                motivo.setAttribute(Columns.TMO_CODIGO, entradaValida.get(Columns.TMO_CODIGO).toString());
                motivo.setAttribute(Columns.OCA_OBS, entradaValida.containsKey(OCA_OBS) ? entradaValida.get(OCA_OBS) : "");
            }

            // Efetua a liquidação da consignação
            final LiquidarConsignacaoParametros parametrosLiquidacao = new LiquidarConsignacaoParametros();
            parametrosLiquidacao.setApenasValidacao(validar);

            if ((responsavel.isCseSup() || responsavel.isCsaCor()) && permiteLoteAtrasado && (periodoConfiguravel != null)) {
                parametrosLiquidacao.setOcaPeriodo(periodoConfiguravel);
            }

            liquidarController.liquidar(adeCodigo, motivo, parametrosLiquidacao, responsavel);

            totalExcluidos++;
            totalRegistros++;

        } else if (!validar) {
            // Se tem o Id do novo contrato para o processo de renegociação, então
            // não liquida a consignação, apenas insere a informação do novo Id
            // para que no processo de inclusão, a renegociação seja efetuada.
            autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_IDENTIFICADOR_RENEGOCIACAO, adeIdentificadorReneg, responsavel);
        }

        if (validar && !loteFebraban) {
            final String codReg = (entradaValida.get(COD_REG) != null) && !"".equals(entradaValida.get(COD_REG).toString()) ? entradaValida.get(COD_REG).toString() : CodedValues.COD_REG_DESCONTO;
            final String mensagem = CodedValues.COD_REG_ESTORNO.equals(codReg) ? ApplicationResourcesHelper.getMessage("mensagem.lote.exclusao.validada.credito", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.lote.exclusao.validada", responsavel);
            salvarCriticaLotePadrao(mensagem);
        }

        if (loteFebraban) {
            salvarCriticaLoteFebraban("mensagem.exclusaoValidada", true);
        }
    }

    /**
     * Faz a confirmação da consignação a partir de uma linha do arquivo de lote, representado pelo parâmetro entradaValida.
     * @param entradaValida
     * @param adeConfirmacaoParam
     * @throws AutorizacaoControllerException
     * @throws ViewHelperException
     */
    private void confirmarConsignacao(Map<String, Object> entradaValida, Map<String, Object> adeConfirmacaoParam) throws AutorizacaoControllerException, ViewHelperException {

        if (!validar) {
            final String adeCodigo = adeConfirmacaoParam.get(Columns.ADE_CODIGO).toString();

            // Busca os parâmetros específicos da operação de confirmação
            final BigDecimal adeVlr = (BigDecimal) entradaValida.get(ADE_VLR);
            final String adeIdentificador = (String) entradaValida.get("ADE_IDENTIFICADOR_NOVO");

            // Dados bancários do servidor
            final String rseBancoSal = (String) entradaValida.get(RSE_BANCO_SAL);
            final String rseAgenciaSal = (String) entradaValida.get(RSE_AGENCIA_SAL);
            final String rseContaSal = (String) entradaValida.get(RSE_CONTA_SAL);

            // Dados de autorização da operação: via lote não passa senha, mas pode passar
            // código de autorização de solicitação.
            final String codAutorizacao = (String) entradaValida.get("CODIGO_AUTORIZACAO");
            final String senhaUtilizada = null;
            final boolean comSerSenha = false;

            // Verifica se tem motivo informado
            CustomTransferObject motivo = null;
            if (entradaValida.get(Columns.TMO_CODIGO) != null) {
                motivo = new CustomTransferObject();
                motivo.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                motivo.setAttribute(Columns.TMO_CODIGO, entradaValida.get(Columns.TMO_CODIGO).toString());
                motivo.setAttribute(Columns.OCA_OBS, entradaValida.containsKey(OCA_OBS) ? entradaValida.get(OCA_OBS) : "");
            }

            //DESENV-20546 - inclusão de deferimento de ade para o caso do SAD_CODIGO = 2
            final String sadCodigo = (String) adeConfirmacaoParam.get(Columns.ADE_SAD_CODIGO);

            if (!CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo)) {
            	// Efetua a confirmação da consignação
            	final ConfirmarConsignacaoParametros confirmarConsignacaoParametros = new ConfirmarConsignacaoParametros();
            	if ((responsavel.isCseSup() || responsavel.isCsaCor()) && permiteLoteAtrasado && (periodoConfiguravel != null)) {
            		confirmarConsignacaoParametros.setOcaPeriodo(periodoConfiguravel);
            	}
            	confirmarController.confirmar(adeCodigo, adeVlr, adeIdentificador, rseBancoSal, rseAgenciaSal, rseContaSal, corCodigo, null, senhaUtilizada, codAutorizacao, comSerSenha, null, null, motivo, confirmarConsignacaoParametros, responsavel);
            } else if (responsavel.temPermissao(CodedValues.FUN_DEF_CONSIGNACAO)) {
            	deferirConsignacaoController.deferir(adeCodigo, motivo, responsavel);
            } else {
            	throw new AutorizacaoControllerException("mensagem.erro.lote.usuario.sem.permissao.deferir", responsavel);
            }

            totalConfirmados++;
            totalRegistros++;

        } else if (!loteFebraban) {
            final String codReg = (entradaValida.get(COD_REG) != null) && !"".equals(entradaValida.get(COD_REG).toString()) ? entradaValida.get(COD_REG).toString() : CodedValues.COD_REG_DESCONTO;
            totalConfirmados++;
            totalRegistros++;
            final String mensagem = CodedValues.COD_REG_ESTORNO.equals(codReg) ? ApplicationResourcesHelper.getMessage("mensagem.lote.confirmacao.validada.credito", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.lote.confirmacao.validada", responsavel);
            salvarCriticaLotePadrao(mensagem);
        }

        if (loteFebraban) {
            salvarCriticaLoteFebraban("mensagem.confirmacaoValidada", true);
        }
    }

    /**
     * Faz a inclusão de nova reserva a partir de uma linha do arquivo de lote, representado pelo parâmetro entradaValida.
     * @param entradaValida
     * @param ignorarRseCodigo
     * @throws AutorizacaoControllerException
     * @throws ViewHelperException
     * @throws PeriodoException
     */
    private void importaReservaMargem(Map<String, Object> entradaValida, List<String> ignorarRseCodigo) throws AutorizacaoControllerException, ViewHelperException, PeriodoException, ZetraException {

        final String codVerba = (String) entradaValida.get(CNV_COD_VERBA);
        final String estIdentificador = (String) entradaValida.get(EST_IDENTIFICADOR);
        final String orgIdentificador = (String) entradaValida.get(ORG_IDENTIFICADOR);
        final String rseMatricula = (String) entradaValida.get(RSE_MATRICULA);
        final String serCpf = (String) entradaValida.get(SER_CPF);
        final String svcIdentificador = (String) entradaValida.get(SVC_IDENTIFICADOR);
        final String nseCodigo = (String) entradaValida.get(NSE_CODIGO);
        final BigDecimal adeVlr = (BigDecimal) entradaValida.get(ADE_VLR);
        final BigDecimal adeVlrLiquido = (BigDecimal) entradaValida.get(ADE_VLR_LIQUIDO);
        final Integer adePrazo = (Integer) entradaValida.get(ADE_PRAZO);
        final Integer adeCarencia = entradaValida.get(ADE_CARENCIA) != null ? (Integer) entradaValida.get(ADE_CARENCIA) : Integer.valueOf(_0_);
        final String adePeriodicidade = (String) entradaValida.get(ADE_PERIODICIDADE);
        final boolean renegociacao = ((entradaValida.get(RENEGOCIACAO) != null) && _1_.equals(entradaValida.get(RENEGOCIACAO)));
        boolean tentarTodosRegistrosServidores = false;

        // Busca uma lista de registros que representam uma tupla convênio + servidor cada.
        List<Map<String, Object>> serCnvRegisters = loteController.lstServidorPorCnv(codVerba, csaCodigoCorrente, tipoEntidade, codigoEntidade, estIdentificador, orgIdentificador, rseMatricula, serCpf, cnvAtivo, serAtivo, svcIdentificador, nseCodigo, true, renegociacao, ignorarRseCodigo, null, false, responsavel);

        if (!serCnvRegisters.isEmpty()) {
            // Atualiza o cache de parâmetros para os registros retornados
            atualizaCacheParamCnv(entradaValida, serCnvRegisters);

            // Filtra os serviços que não permitem importação de lote
            removeSvcSemImportacaoLote(serCnvRegisters);

            // Filtra os servidores que possuem contratos para renegociação,
            // caso seja uma operação de renegociação.
            final boolean fixaServico = tentaPriorizarSvcOrigemRenegociacao(csaCodigoCorrente, responsavel);
            serCnvRegisters = filtraServidoresRenegociacao(entradaValida, serCnvRegisters, fixaServico);

            // Se existe mais de um registro, verifica se pode utilizar o primeiro "disponível".
            // O disponível é aquele onde o servidor não possui bloqueios, não atingiu o limite de contratos,
            // pode incluir novas reservas e não está bloqueado.
            if ((serCnvRegisters.size() > 1) && (utilizarPrimeiroCnvDisponivel(csaCodigoCorrente, responsavel))) {
                serCnvRegisters = filtraReservasPermitidas(entradaValida, serCnvRegisters, adeVlr, adeVlrLiquido, adePrazo, adeCarencia, adePeriodicidade);
            }

            if (serCnvRegisters.size() > 1) {
                // Se existe mais de um registro, provavelmente é porque há mais de um registro servidor.
                // Verifica se pode ser utilizado aquele de maior margem
                final int countServidores = countEntidadesDistintas(serCnvRegisters, Columns.SER_CODIGO);
                if ((countServidores > 1) && !permiteInclusaoComServidorDuplicado(csaCodigoCorrente, responsavel)) {
                    throw new AutorizacaoControllerException(MENSAGEM_MULTIPLOS_SERVIDORES_ENCONTRADOS, responsavel);
                }
                final int countRegistrosServidores = countEntidadesDistintas(serCnvRegisters, Columns.RSE_CODIGO);
                if (countRegistrosServidores > 1) {
                    // Se encontrou mais de um servidor, verifica se o parâmetro de consignatária
                    // permite a tentativa de inclusão em todas as matrículas disponíveis, ou
                    // se escolhe apenas a com a maior margem
                    tentarTodosRegistrosServidores = tentarIncluirTodosRegistrosServidores(csaCodigoCorrente, responsavel);
                    if (utilizarRegistroServidorMaiorMargem(csaCodigoCorrente, responsavel) || tentarTodosRegistrosServidores) {
                        serCnvRegisters = filtraRegistroMargemOrdenada(serCnvRegisters, adeVlr, adePrazo, true);
                    } else if (utilizarRegistroServidorMenorMargem(csaCodigoCorrente, responsavel)) {
                        serCnvRegisters = filtraRegistroMargemOrdenada(serCnvRegisters, adeVlr, adePrazo, false);
                    } else {
                        throw new AutorizacaoControllerException(MENSAGEM_MULTIPLOS_SERVIDORES_ENCONTRADOS, responsavel);
                    }
                }
            }

            // Verifica quantos serviços distintos ainda restaram (deve haver apenas 1)
            final int countServicos = countEntidadesDistintas(serCnvRegisters, Columns.SVC_CODIGO);
            if (countServicos == 0) {
                throw new AutorizacaoControllerException("mensagem.tipoServicoInvalido", responsavel);
            } else if (countServicos > 1) {
                throw new AutorizacaoControllerException("mensagem.maisDeUmServicoEncontrado", responsavel);
            }

            // Verifica quantos registros servidores foram retornados (deve haver apenas 1)
            final int countRegistrosServidores = countEntidadesDistintas(serCnvRegisters, Columns.RSE_CODIGO);
            if (countRegistrosServidores == 0) {
                throw new AutorizacaoControllerException("mensagem.nenhumServidorEncontrado", responsavel);
            } else if (countRegistrosServidores > 1) {
                throw new AutorizacaoControllerException(MENSAGEM_MULTIPLOS_SERVIDORES_ENCONTRADOS, responsavel);
            }

            // Variável com os dados do servidor e do convênio para inclusão de nova reserva
            final Map<String, Object> dadosServidorConvenio = serCnvRegisters.get(0);

            LOG.debug(dadosServidorConvenio);

            try {
                if (!bloqueiaInclusaoRseTipo(dadosServidorConvenio)) {
                    // Executa a inclusão da nova reserva
                    insereReservaMargem(entradaValida, dadosServidorConvenio);
                } else {
                    throw new AutorizacaoControllerException("mensagem.info.inclusao.bloqueada.rse.tipo.lote", responsavel);
                }
            } catch (final ZetraException ex) {
                LOG.debug(ex.getMessage());
                if (!tentarTodosRegistrosServidores) {
                    throw ex;
                } else {
                    try {
                        // Se a lista dos ignorados não existe, cria uma nova
                        if (ignorarRseCodigo == null) {
                            ignorarRseCodigo = new ArrayList<>();
                        }
                        // Adiciona rseCodigo a lista dos ignorados, e realiza nova tentativa de inclusão
                        ignorarRseCodigo.add(dadosServidorConvenio.get(Columns.RSE_CODIGO).toString());
                        importaReservaMargem(entradaValida, ignorarRseCodigo);
                    } catch (final ZetraException nex) {
                        // Em caso de erro na chamada subsequente, envia a exceção do primeiro caso
                        throw ex;
                    }
                }
            }

        } else {
            // Não retornou nenhum registro. Deve-se verificar a causa para retornar mensagem correta ao usuário.
            motivoCnvNaoEncontrado(INCLUSAO, entradaValida);

            if (renegociacao) {
                final boolean fixaServico = tentaPriorizarSvcOrigemRenegociacao(csaCodigoCorrente, responsavel);

                // Filtra os servidores que possuem contratos para renegociação,
                // caso seja uma operação de renegociação.
                serCnvRegisters = filtraServidoresRenegociacao(entradaValida, serCnvRegisters, fixaServico);

                if ((serCnvRegisters == null) || serCnvRegisters.isEmpty()) {
                    throw new AutorizacaoControllerException("mensagem.erro.nao.ha.relacionamento.renegociacao.entre.servicos.envolvidos", responsavel);
                }
            }
        }
    }

    /**
     * Método responsável por realizar operações que modificam uma consignação já existente,
     * como os comandos de alteração, exclusão ou confirmação, identificado pelo campo operação
     * na linha do arquivo de lote.
     * @param operacao
     * @param entradaValida
     * @param criterio
     * @throws AutorizacaoControllerException
     * @throws ViewHelperException
     */
    private void importaModificacaoConsignacao(String operacao, Map<String, Object> entradaValida, TransferObject criterio) throws AutorizacaoControllerException, ViewHelperException {
        final String codVerba = (String) entradaValida.get(CNV_COD_VERBA);
        final String estIdentificador = (String) entradaValida.get(EST_IDENTIFICADOR);
        final String orgIdentificador = (String) entradaValida.get(ORG_IDENTIFICADOR);
        final String rseMatricula = (String) entradaValida.get(RSE_MATRICULA);
        final String serCpf = (String) entradaValida.get(SER_CPF);
        final String svcIdentificador = (String) entradaValida.get(SVC_IDENTIFICADOR);
        final String nseCodigo = (String) entradaValida.get(NSE_CODIGO);

        // Busca registros que são combinações de contrato, convênio e servidor
        final List<Map<String, Object>> autCnvSerRegisters = loteController.buscaConsignacaoPorCnvSer(operacao, codVerba, csaCodigoCorrente, rseMatricula, serCpf, orgIdentificador, svcIdentificador, estIdentificador, cnvAtivo, criterio, nseCodigo, responsavel);

        atualizaCacheParamCnv(entradaValida, autCnvSerRegisters);

        if (!autCnvSerRegisters.isEmpty()) {
            // Filtra os serviços que não permitem importação de lote
            removeSvcSemImportacaoLote(autCnvSerRegisters);

            // Verifica quantos serviços distintos retornaram (deve haver apenas 1)
            final int countServicos = countEntidadesDistintas(autCnvSerRegisters, Columns.SVC_CODIGO);
            if (countServicos == 0) {
                throw new AutorizacaoControllerException("mensagem.tipoServicoInvalido", responsavel);
            } else if (countServicos > 1) {
                throw new AutorizacaoControllerException("mensagem.maisDeUmServicoEncontrado", responsavel);
            } else if (autCnvSerRegisters.size() > 1) {
                // Se tem apenas um serviço, verifica se tem mais de um servidor
                final int countServidores = countEntidadesDistintas(autCnvSerRegisters, Columns.SER_CODIGO);
                if (countServidores > 1) {
                    throw new AutorizacaoControllerException(MENSAGEM_MULTIPLOS_SERVIDORES_ENCONTRADOS, responsavel);
                } else {
                    throw new AutorizacaoControllerException("mensagem.maisDeUmaConsignacaoEncontrada", responsavel);
                }
            } else {
                LOG.debug(autCnvSerRegisters.get(0));

                // Restando apenas um convênio, servidor e contrato; ok para alterar ou excluir
                if (ALTERACAO.equalsIgnoreCase(operacao)) {
                    final boolean alteracaoAvancada = responsavel.isCseSup() && (!TextHelper.isNull(entradaValida.get(ALTERACAO_AVANCADA)) &&
                            _1_.equals(entradaValida.get(ALTERACAO_AVANCADA)));

                    // caso seja uma importação de lote de toda carteira, já gera ocorrência de atualização da ade via lote total
                    // para que este não seja marcado para exclusão caso seja barrado por alguma validação e, deste modoe,
                    // poder ser reenviado em eventual lote complementar.
                    if (!validar) {
                        geraOcorrenciaLoteTotal((String) autCnvSerRegisters.get(0).get(Columns.ADE_CODIGO), ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.oca.obs.alterado.contrato.lote.total", responsavel), responsavel);
                    }

                    // Filtra da lista de tuplas os registros ligados a entidades não ativas
                    if (!alteracaoAvancada || (TextHelper.isNull(entradaValida.get(ADE_ALT_ENT_BLOQUEADAS))
                            || !_1_.equals(entradaValida.get(ADE_ALT_ENT_BLOQUEADAS)))) {
                        validaEntidadesAtivas(autCnvSerRegisters.get(0), entradaValida);
                    }
                    // Valida os prazos para o serviço alvo.
                    if (!alteracaoAvancada || (TextHelper.isNull(entradaValida.get(ADE_PRZ_NAO_CADASTRADO))
                            || _0_.equals(entradaValida.get(ADE_PRZ_NAO_CADASTRADO)))) {
                        validaPrazoCadastrado(entradaValida, autCnvSerRegisters.get(0));
                    }
                    // Executa método de alteração do contrato
                    alterarConsignacao(entradaValida, autCnvSerRegisters.get(0));

                } else if (EXCLUSAO.equalsIgnoreCase(operacao)) {
                    // Executa método de liquidação do contrato
                    excluirConsignacao(entradaValida, autCnvSerRegisters.get(0));

                } else if (CONFIRMACAO.equalsIgnoreCase(operacao)) {
                	// caso seja uma importação de lote de toda carteira, já gera ocorrência de atualização da ade via lote total
                    // para que este não seja marcado para exclusão caso seja barrado por alguma validação e, deste modoe,
                    // poder ser reenviado em eventual lote complementar.
                    if (!validar) {
                        geraOcorrenciaLoteTotal((String) autCnvSerRegisters.get(0).get(Columns.ADE_CODIGO), ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.oca.obs.confirmado.contrato.lote.total", responsavel), responsavel);
                    }

                    // Filtra da lista de tuplas os registros ligados a entidades não ativas
                    validaEntidadesAtivas(autCnvSerRegisters.get(0), entradaValida);
                    // Executa método de confirmação do contrato
                    confirmarConsignacao(entradaValida, autCnvSerRegisters.get(0));
                }
            }
        } else {
            // Não retornou nenhum registro. Deve-se verificar a causa para retornar mensagem correta ao usuário.
            motivoCnvNaoEncontrado(operacao, entradaValida);

            // Se encontrou convênio e servidor, significa que não encontrou o contrato.
            throw new AutorizacaoControllerException("mensagem.nenhumaConsignacaoEncontrada", responsavel);
        }
    }

	/**
     * Busca o motivo pelo qual não houve a importação da linha de lote.
     * @param operacao
     * @param entradaValida
     * @throws AutorizacaoControllerException
     * @throws ConvenioControllerException
     */
    private void motivoCnvNaoEncontrado(String operacao, Map<String, Object> entradaValida) throws AutorizacaoControllerException {
        // Busca se servidor existente
        final List<TransferObject> servidores = loteController.buscaServidor(operacao, tipoEntidade, codigoEntidade, (String) entradaValida.get(EST_IDENTIFICADOR),
                                                                       (String) entradaValida.get(ORG_IDENTIFICADOR), (String) entradaValida.get(RSE_MATRICULA),
                                                                       (String) entradaValida.get(SER_CPF), serAtivo, responsavel);

        if (servidores.isEmpty()) {
            throw new AutorizacaoControllerException("mensagem.nenhumServidorEncontrado", responsavel);
        } else {
            final List<String> listOrgs = new ArrayList<>();

            final Iterator<TransferObject> serIt = servidores.iterator();
            while (serIt.hasNext()) {
                final CustomTransferObject serObjct = (CustomTransferObject) serIt.next();
                listOrgs.add(serObjct.getAttribute(Columns.ORG_CODIGO).toString());
            }
            final String codVerba = (String) entradaValida.get(CNV_COD_VERBA);
            final String svcIdentificador = (String) entradaValida.get(SVC_IDENTIFICADOR);
            final String nseCodigo = (String) entradaValida.get(NSE_CODIGO);

            loteController.verificaConvenioProcessaLote(codVerba, listOrgs, csaCodigoCorrente, svcIdentificador, cnvAtivo, nseCodigo, responsavel);
        }
    }

    /**
     * Retorna uma implementação de comparator de acordo com a coluna de margem que se deve comparar.
     * @param paramMap
     * @return
     */
    private Comparator<Map<String, Object>> margemComparator(Map<String, Object> paramMap, BigDecimal adeVlr, Integer adePrazo, boolean decrescente) {
        final Object tpsIncideMargem = paramMap.get(CodedValues.TPS_INCIDE_MARGEM);
        final Short margemIncidente = (tpsIncideMargem != null ? Short.valueOf(tpsIncideMargem.toString()) : CodedValues.INCIDE_MARGEM_SIM);
        final String margemAComparar =
                (margemIncidente.equals(CodedValues.INCIDE_MARGEM_SIM)) ? Columns.RSE_MARGEM_REST :
                    (margemIncidente.equals(CodedValues.INCIDE_MARGEM_SIM_2)) ? Columns.RSE_MARGEM_REST_2 :
                        (margemIncidente.equals(CodedValues.INCIDE_MARGEM_SIM_3)) ? Columns.RSE_MARGEM_REST_3 :
                            Columns.MRS_MARGEM_REST;

        return (to1, to2) -> {
            final double margem1 = to1.get(margemAComparar) != null ? Double.parseDouble(to1.get(margemAComparar).toString()) : 0;
            final double margem2 = to2.get(margemAComparar) != null ? Double.parseDouble(to2.get(margemAComparar).toString()) : 0;
            final double result = decrescente ? margem1 - margem2 : margem2 - margem1;
            final Integer rsePrazo = !TextHelper.isNull(to1.get(Columns.RSE_PRAZO)) ? Integer.valueOf(to1.get(Columns.RSE_PRAZO).toString()) : null;
            final Integer rsePrazo2 = !TextHelper.isNull(to2.get(Columns.RSE_PRAZO)) ? Integer.valueOf(to2.get(Columns.RSE_PRAZO).toString()) : null;

            if ((result > 0) && (margem1 >= adeVlr.doubleValue()) && ((rsePrazo == null) || (rsePrazo.compareTo(adePrazo) >= 0))) {
                return 1;
            } else if ((result < 0) && (margem2 >= adeVlr.doubleValue()) && ((rsePrazo2 == null) || (rsePrazo2.compareTo(adePrazo) >= 0))) {
                return -1;
            } else if ((margem1 >= adeVlr.doubleValue()) && ((rsePrazo == null) || (rsePrazo.compareTo(adePrazo) >= 0))) {
                return 1;
            } else if ((margem2 >= adeVlr.doubleValue()) && ((rsePrazo2 == null) || (rsePrazo2.compareTo(adePrazo) >= 0))) {
                return -1;
            } else if (result > 0) {
                return 1;
            } else if (result < 0) {
                return -1;
            } else {
                return 0;
            }
        };
    }

    /**
     * Define quantas entidades distintas especifidas pelo nomeColuna está ligada à lista de registros
     * @param serCnvRegisters - lista de Maps
     * @param nomeColuna
     * @return
     */
    public int countEntidadesDistintas(List<Map<String, Object>> serCnvRegisters, String nomeColuna) {
        final List<String> listaEntidades = new ArrayList<>();
        int countEnt = 0;

        for (final Map<String, Object> reg : serCnvRegisters) {
            final String entidade = reg.get(nomeColuna).toString();
            if (!listaEntidades.contains(entidade)) {
                listaEntidades.add(entidade);
                countEnt++;
            }
        }
        return countEnt;
    }

    /**
     * Remove da lista de resultados os registros ligados a serviços sem permissão de importar lote.
     * @param serCnvRegisters
     */
    private void removeSvcSemImportacaoLote(List<Map<String, Object>> serCnvRegisters) {
        final Iterator<Map<String, Object>> it = serCnvRegisters.iterator();
        while (it.hasNext()) {
            final Map<String, Object> serCnvReg = it.next();
            final Map<String, Object> parametros = cacheParametrosCnv.get(serCnvReg.get(Columns.CNV_CODIGO));
            final Object permiteImpLoteVlr = parametros.get(CodedValues.TPS_PERMITE_IMPORTACAO_LOTE);
            final boolean permiteImportarLote = (_1_.equals(permiteImpLoteVlr));
            if (!permiteImportarLote) {
                it.remove();
            }
        }
    }

    /**
     * Da lista de servidores/convênios encontrados para uma inclusão, caso seja renegociação
     * filtra a lista deixando apenas as tuplas onde os contratos a serem renegociados existem.
     * @param entradaValida
     * @param serCnvRegisters
     * @return
     * @throws AutorizacaoControllerException
     */
    private List<Map<String, Object>> filtraServidoresRenegociacao(Map<String, Object> entradaValida, List<Map<String, Object>> serCnvRegisters, boolean fixaServico) throws AutorizacaoControllerException {
        final String adeIdentificador = (String) entradaValida.get(ADE_IDENTIFICADOR);
        final boolean renegociacao = ((entradaValida.get(RENEGOCIACAO) != null) && _1_.equals(entradaValida.get(RENEGOCIACAO)));

        if (renegociacao) {
            final List<Map<String, Object>> serCnvRegistersCandidatos = new ArrayList<>();

            // Para cada tupla <Convenio,Servidor> verifica se é operação de renegociação,
            // e caso seja retorna apenas os servidores que possuem as consignações a serem renegociadas.
            for (final Map<String, Object> serCnvReg : serCnvRegisters) {
                final String svcCodigo = (String) serCnvReg.get(Columns.SVC_CODIGO);
                final String rseCodigo = (String) serCnvReg.get(Columns.RSE_CODIGO);

                // Se é uma renegociação lista as consignações a serem renegociadas e caso existam
                // filtra os servidores deixando apenas aqueles que possuem os contratos a serem renegociados
                List<String> adeCodigosRenegociacao = loteController.buscaConsignacaoLiquidadaParaRenegociacao(rseCodigo, csaCodigoCorrente, svcCodigo, fixaServico, responsavel);
                if ((adeCodigosRenegociacao != null) && !adeCodigosRenegociacao.isEmpty()) {
                    // Encontrou as consignações já liquidadas para associação à renegociação
                    // de acordo com o parâmetro em dias para localização da liquidação
                    serCnvReg.put(ADE_LIQUIDADA_RENEGOCIACAO, adeCodigosRenegociacao);
                } else {
                    // Não encontrou consignações liquidadas que possam ser associadas em renegociação,
                    // verifica então se existem consignações abertas para serem renegociadas de acordo
                    // com o adeIdentificador informado para a inclusão
                    try {
                        adeCodigosRenegociacao = loteController.buscaConsignacaoAbertaParaRenegociacao(rseCodigo, csaCodigoCorrente, svcCodigo, adeIdentificador, fixaServico, responsavel);
                    } catch (final AutorizacaoControllerException e) {
                        if (tentarIncluirTodosRegistrosServidores(csaCodigoCorrente, responsavel)) {
                            continue;
                        } else {
                            throw e;
                        }
                    }
                    if ((adeCodigosRenegociacao != null) && !adeCodigosRenegociacao.isEmpty()) {
                        // Encontrou as consignações abertas pelo adeIdentificador. Guarda a lista
                        // de contratos para serem utilizadas nas rotinas seguintes
                        serCnvReg.put(ADE_ABERTA_RENEGOCIACAO, adeCodigosRenegociacao);
                    }
                }

                // Se encontrou as consignações para renegociação, liquidadas ou abertas,
                // então adiciona esta tupla na lista de candidatos
                if ((adeCodigosRenegociacao != null) && !adeCodigosRenegociacao.isEmpty()) {
                    serCnvRegistersCandidatos.add(serCnvReg);
                }
            }

            // Se não encontrou o serviço fixado, realiza a busca novamente pelos serviços não fixados
            if (serCnvRegistersCandidatos.isEmpty() && fixaServico) {
                filtraServidoresRenegociacao(entradaValida, serCnvRegisters, false);
            }

            // Se é renegociação e nenhum candidato foi listado com contratos para renegociação
            // então retorna mensagem de erro indicando que os contratos não foram localizados
            if (serCnvRegistersCandidatos.isEmpty()) {
                // 22/02/2011 - Em caso de renegociação que não localiza os contratos, proceder a operação de Inclusão
                entradaValida.put(RENEGOCIACAO, _0_);
                erroRenegociacao = true;
                return serCnvRegisters;
            }

            return serCnvRegistersCandidatos;
        } else {
            // Se não é renegociação, retorna todas as tuplas para o prosseguimento da rotina
            return serCnvRegisters;
        }
    }

    public List<Map<String, Object>> filtraReservasPermitidas(Map<String, Object> entradaValida, List<Map<String, Object>> serCnvRegisters, BigDecimal adeVlr, BigDecimal adeVlrLiquido, Integer adePrazo, Integer adeCarencia, String adePeriodicidade) throws AutorizacaoControllerException {
    	return filtraReservasPermitidas(entradaValida, serCnvRegisters, adeVlr, adeVlrLiquido, adePrazo, adeCarencia, adePeriodicidade, null);
    }

    /**
     * Da lista de convênios e registros servidores, filtra apenas aqueles onde novas
     * reservas podem ser inseridas, evitando o erro caso mais de um convênio seja encontrado.
     * @param entradaValida
     * @param serCnvRegisters
     * @param adeVlr
     * @param adeVlrLiquido
     * @param adePrazo
     * @param adeCarencia
     * @param adePeriodicidade
     * @param adeIdentificador
     * @throws AutorizacaoControllerException
     */
    private List<Map<String, Object>> filtraReservasPermitidas(Map<String, Object> entradaValida, List<Map<String, Object>> serCnvRegisters, BigDecimal adeVlr, BigDecimal adeVlrLiquido, Integer adePrazo, Integer adeCarencia, String adePeriodicidade, String adeIdentificador) throws AutorizacaoControllerException {
        AutorizacaoControllerException primeiroErro = null;

        final List<Map<String, Object>> serCnvRegistersCandidatos = new ArrayList<>();
        final List<String> rseCodigoAvaliado = new ArrayList<>();

        final boolean renegociacao = ((entradaValida.get(RENEGOCIACAO) != null) && _1_.equals(entradaValida.get(RENEGOCIACAO)));
        final String acao = (renegociacao ? RENEGOCIAR : RESERVAR);

        // Para cada tupla <Convenio,Servidor> verifica se uma nova reserva
        // pode ser feita. Verifica também, caso a operação seja de renegociação,
        // as consignações a serem renegociadas.
        final Iterator<Map<String, Object>> it = serCnvRegisters.iterator();
        while (it.hasNext()) {
            final Map<String, Object> serCnvReg = it.next();
            final String cnvCodigo = (String) serCnvReg.get(Columns.CNV_CODIGO);
            final String rseCodigo = (String) serCnvReg.get(Columns.RSE_CODIGO);
            final Map<String, Object> paramCnv = cacheParametrosCnv.get(cnvCodigo);

            // Se o registro servidor já foi avaliado, então outros convênios
            // para ele serão ignorados.
            if (rseCodigoAvaliado.contains(rseCodigo)) {
                continue;
            }

            try {
                // Verifica se as entidades estão ativas para fazer novas reservas
                autorizacaoController.validarEntidades(cnvCodigo, corCodigo, responsavel);

                // Se é uma renegociação obtém as consignações a serem renegociadas para passar ao
                // método que analisa se pode reservar margem, por causa de limite de contratos
                List<String> adeCodigosRenegociacao = null;
                if (renegociacao) {
                    adeCodigosRenegociacao = (List<String>) serCnvReg.get(ADE_ABERTA_RENEGOCIACAO);
                }

                // Testa se este convênio pode receber nova reserva para o registro servidor
                autorizacaoController.podeReservarMargem(cnvCodigo, corCodigo, rseCodigo, false, true, true, adeCodigosRenegociacao, adeVlr, adeVlrLiquido, adePrazo, adeCarencia, adePeriodicidade, adeIdentificador, paramCnv, acao, true, false, responsavel);

                if (bloqueiaInclusaoRseTipo(serCnvReg)) {
                    throw new AutorizacaoControllerException("mensagem.info.inclusao.bloqueada.rse.tipo.lote", responsavel);
                }

                // Testa se o prazo informado é valido para o serviço
                validaPrazoCadastrado(entradaValida, serCnvReg);

                // OK: novas reservas podem ser feitas.
                serCnvRegistersCandidatos.add(serCnvReg);
                rseCodigoAvaliado.add(rseCodigo);

            } catch (final AutorizacaoControllerException ex) {
                if (primeiroErro == null) {
                    primeiroErro = ex;
                }
                // Não pode fazer reservas neste convênio, se for o último registro,
                // e nenhum candidato foi listado, então propaga o erro
                if (!it.hasNext() && serCnvRegistersCandidatos.isEmpty()) {
                    throw primeiroErro;
                }
            }
        }
        return serCnvRegistersCandidatos;
    }

    /**
     * Da lista de servidores retornados para uma inclusão,  retorna aquele que possuem
     * maior margem disponível para a inclusão da nova reserva
     * @param serCnvRegisters
     * @return
     */
    public List<Map<String, Object>> filtraRegistroMargemOrdenada(List<Map<String, Object>> serCnvRegisters, BigDecimal adeVlr, Integer adePrazo, boolean decrescente) {
        final List<Map<String, Object>> serCnvRegistersCandidatos = new ArrayList<>();

        // 2.1) O valor de margem restante ao qual o serviço incide é maior ou igual ao valor de parcela da nova consignação (RSE_MARGEM_REST >= ADE_VLR).
        // 2.2) O prazo de contratação do servidor seja indeterminado (RSE_PRAZO = NULL) ou seja maior ou igual ao prazo da nova consignação (RSE_PRAZO >= ADE_PRAZO).

        // Pega os parâmetros do convênio do primeiro registro (teoricamente devem ser do mesmo serviço)
        final Map<String, Object> paramMap = cacheParametrosCnv.get(serCnvRegisters.get(0).get(Columns.CNV_CODIGO));
        // Seleciona o servidor de maior margem, através do "comparator"
        final Comparator<Map<String, Object>> compMargem = margemComparator(paramMap, adeVlr, adePrazo, decrescente);
        final Map<String, Object> dadosRegistroMaiorMargem = Collections.max(serCnvRegisters, compMargem);
        final String rseCodigoMaiorMargem = (String) dadosRegistroMaiorMargem.get(Columns.RSE_CODIGO);

        // Varre a lista de resultado e obtém todos os registros ligados ao
        // registro servidor de maior margem: pode existir mais de um, por
        // exemplo quando há mais de um serviço.
        for (final Map<String, Object> serCnvReg : serCnvRegisters) {
            final String rseCodigo = (String) serCnvReg.get(Columns.RSE_CODIGO);
            if (rseCodigo.equals(rseCodigoMaiorMargem)) {
                serCnvRegistersCandidatos.add(serCnvReg);
            }
        }

        return serCnvRegistersCandidatos;
    }

    /**
     * Valida se as entidades envolvidas na operação estão ativas
     * @param serCnvReg
     * @param entradaValida
     * @throws AutorizacaoControllerException
     * @throws ViewHelperException
     */
    private void validaEntidadesAtivas(Map<String, Object> serCnvReg, Map<String, Object> entradaValida) throws AutorizacaoControllerException, ViewHelperException {
        final String operacao = entradaValida.get(OPERACAO).toString();
        final String cnvCodigo = serCnvReg.get(Columns.CNV_CODIGO).toString();
        final String codReg = (entradaValida.get(COD_REG) != null) && !"".equals(entradaValida.get(COD_REG).toString()) ? entradaValida.get(COD_REG).toString() : CodedValues.COD_REG_DESCONTO;

        final Map<String, Object> paramCnv = cacheParametrosCnv.get(cnvCodigo);
        final boolean permiteAlterarComBloqueio = (!TextHelper.isNull(paramCnv.get(CodedValues.TPS_PERMITE_ALTERAR_ADE_COM_BLOQUEIO)) && !CodedValues.PSE_NAO_PERMITE_ALTERAR_ADE_COM_BLOQUEIO.equals(paramCnv.get(CodedValues.TPS_PERMITE_ALTERAR_ADE_COM_BLOQUEIO)));

        // Se não é Estorno (ex. Desconto), e não é alteração, ou se for alteração não permitir
        // alteração com entidades bloqueadas, então valida o status das entidades previamente
        // para já retornar erro, caso estejam bloqueadas. A validação também é feita no método de negócio.
        if (!CodedValues.COD_REG_ESTORNO.equals(codReg) && (!ALTERACAO.equalsIgnoreCase(operacao) || !permiteAlterarComBloqueio)) {
            autorizacaoController.validarEntidades(cnvCodigo, corCodigo, responsavel);
        }
    }

    /**
     * Verifica se o prazo cadastrado é valido de acordo com o cadastro de prazos do serviço
     * @param entradaValida
     * @param serCnvReg
     * @throws AutorizacaoControllerException
     */
    private void validaPrazoCadastrado(Map<String, Object> entradaValida, Map<String, Object> serCnvReg) throws AutorizacaoControllerException {
        final String codReg = (entradaValida.get(COD_REG) != null) && !"".equals(entradaValida.get(COD_REG).toString()) ? entradaValida.get(COD_REG).toString() : CodedValues.COD_REG_DESCONTO;
        if (!CodedValues.COD_REG_ESTORNO.equals(codReg)) {
            final String svcCodigo = (String) serCnvReg.get(Columns.SVC_CODIGO);
            final String orgCodigo = (String) serCnvReg.get(Columns.ORG_CODIGO);
            Integer adePrazo = (Integer) entradaValida.get(ADE_PRAZO);
            final Date adeAnoMesIni = (Date) entradaValida.get(ADE_ANO_MES_INI);
            final Date adeAnoMesFim = (Date) entradaValida.get(ADE_ANO_MES_FIM);
            final String adePeriodicidade = (String) entradaValida.get(ADE_PERIODICIDADE);

            if ((adePrazo == null) && (adeAnoMesIni != null) && (adeAnoMesFim != null)) {
                try {
                    adePrazo = PeriodoHelper.getInstance().calcularPrazo(orgCodigo, adeAnoMesIni, adeAnoMesFim, adePeriodicidade, responsavel);
                    entradaValida.put(ADE_PRAZO, adePrazo);
                } catch (final NumberFormatException e) {
                    LOG.debug("Erro em validaPrazoCoeficiente ao instanciar o prazo, NumberFormatException: " + e.getMessage());
                    throw new AutorizacaoControllerException(MENSAGEM_QTD_PARCELAS_INVALIDA, responsavel);
                } catch (final Exception e) {
                    LOG.debug("Erro em validaPrazoCoeficiente, Exception: " + e.getMessage());
                    throw new AutorizacaoControllerException(MENSAGEM_QTD_PARCELAS_INVALIDA, responsavel);
                }
            } else if ((adePrazo != null) && (adePrazo >= CodedValues.VLR_ADE_PRAZO_INDETERMINADO)) {
                // Altera o prazo para indeterminado
                entradaValida.put(ADE_PRAZO, null);
            }

            if (!cachePrazos.containsKey(svcCodigo)) {
                final List<TransferObject> prazos = loteController.buscaPrazoCoeficiente(svcCodigo, csaCodigoCorrente, orgCodigo);
                if ((prazos != null) && !prazos.isEmpty()) {
                    final Set<Integer> prazosPossiveis = new TreeSet<>();
                    if (!PeriodoHelper.folhaMensal(responsavel) && !CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(adePeriodicidade)) {
                        prazosPossiveis.addAll(PeriodoHelper.converterListaPrazoMensalEmPeriodicidade(prazos, responsavel));
                    } else {
                        prazos.forEach(p -> prazosPossiveis.add(Integer.valueOf(p.getAttribute(Columns.PRZ_VLR).toString())));
                    }
                    cachePrazos.put(svcCodigo, prazosPossiveis);
                } else {
                    // Serviço não possui prazo cadastrado
                    cachePrazos.put(svcCodigo, null);
                }
            }

            final Set<Integer> valores = cachePrazos.get(svcCodigo);
            if ((valores != null) && ((adePrazo == null) || !valores.contains(adePrazo))) {
                LOG.debug("O prazo informado é inválido.");
                throw new AutorizacaoControllerException(MENSAGEM_QTD_PARCELAS_INVALIDA, responsavel);
            }
        }
    }

    /**
     * Verifica se a data informada no lote para o servidor está de acordo com o cadastrado no sistema
     * para os serviços que demandarem esta verificação.
     * @param entradaValida
     * @param paramCnv
     * @param dadosServidorConvenio
     * @throws ViewHelperException
     */
    private void validaDataNasc(Map<String, Object> entradaValida, Map<String, Object> paramCnv, Map<String, Object> dadosServidorConvenio) throws ViewHelperException {
        try {
            if ((paramCnv.get(CodedValues.TPS_VALIDAR_DATA_NASCIMENTO_NA_RESERVA) != null) &&
                    _1_.equals(paramCnv.get(CodedValues.TPS_VALIDAR_DATA_NASCIMENTO_NA_RESERVA).toString())) {
                final String serDataNascInformada = (String) entradaValida.get("SER_DATA_NASC");
                final String svcCodigo = dadosServidorConvenio.get(Columns.SVC_CODIGO).toString();
                final Date serDataNasc = (Date) dadosServidorConvenio.get(Columns.SER_DATA_NASC);
                final String serDataNascSistema = DateHelper.format(serDataNasc, YYYY_MM_DD);
                if (!servidorController.isDataNascServidorValida(serDataNascInformada, serDataNascSistema, svcCodigo, YYYY_MM_DD, responsavel)) {
                    throw new ViewHelperException("mensagem.dataNascNaoConfere", responsavel);
                }
            }
        } catch (final ServidorControllerException ex) {
            throw new ViewHelperException(ex);
        }
    }

    /**
     * Valida se vínculo do servidor permite reserva de margem
     * @param cnvSerReg
     * @throws AutorizacaoControllerException
     * @throws ViewHelperException
     */
    private void validaVinculoServidor(Map<String, Object> entradaValida, Map<String, Object> cnvSerReg) throws AutorizacaoControllerException {
        final String svcCodigo = (String) cnvSerReg.get(Columns.SVC_CODIGO);
        final String vrsCodigo = (String) cnvSerReg.get(Columns.VRS_CODIGO);

        autorizacaoController.verificaBloqueioVinculoCnv(csaCodigoCorrente, svcCodigo, vrsCodigo, responsavel);
    }

    /**
     * Verifica se matrícula/cpf são obrigatórios e retorna erro caso sejam
     * e não foram informados corretamente.
     * @param rseMatricula
     * @param serCpf
     * @param requerMatriculaCpf
     * @return
     * @throws ViewHelperException
     */
    private static boolean validaMatriculaCpf(String csaCodigo, String rseMatricula, String serCpf, boolean requerMatriculaCpf, AcessoSistema responsavel) throws ViewHelperException {
        final boolean validaCpfPesqServidor = !ParamSist.paramEquals(CodedValues.TPC_VALIDA_CPF_PESQ_SERVIDOR, CodedValues.TPC_NAO, responsavel);

        if (utilizaApenasCpfLote(csaCodigo, responsavel)) {
            if (TextHelper.isNull(serCpf)) {
                LOG.debug("O CPF deve ser informado.");
                throw new ViewHelperException("mensagem.informe.servidor.cpf", responsavel);
            } else if (validaCpfPesqServidor && !TextHelper.cpfOk(TextHelper.dropSeparator(serCpf))) {
                LOG.debug("O CPF informado é inválido.");
                throw new ViewHelperException("mensagem.erro.cpf.servidor.invalido", responsavel);
            }
        } else if (requerMatriculaCpf) {
            if (TextHelper.isNull(rseMatricula) || TextHelper.isNull(serCpf)) {
                LOG.debug("A matrícula e o CPF do servidor devem ser informados.");
                throw new ViewHelperException("mensagem.informe.matricula.cpf", responsavel);
            } else if (validaCpfPesqServidor && !TextHelper.cpfOk(TextHelper.dropSeparator(serCpf))) {
                LOG.debug("O CPF informado é inválido.");
                throw new ViewHelperException("mensagem.erro.cpf.servidor.invalido", responsavel);
            }
        } else if (TextHelper.isNull(rseMatricula) && TextHelper.isNull(serCpf)) {
            LOG.debug("A matrícula e/ou o CPF do servidor devem ser informados.");
            throw new ViewHelperException("mensagem.requerMatrOuCpf", responsavel);
        }

        return true;
    }

    /**
     * Verifica se ADE Identificador deve ser único ou não e retorna erro caso TPA_CODIGO(46) = S e ele nãos eja único
     * e não foram informados corretamente.
     * @param csaCodigo
     * @param adeIdentificador
     * @return
     * @throws ViewHelperException
     * @throws DespesaIndividualControllerException
     */
    private boolean validaAdeIdentificadorUnico(String csaCodigo, String adeIdentificador, String operacao, AcessoSistema responsavel) throws ViewHelperException, DespesaIndividualControllerException {
        if (adeIdentificadorUnicoViaLote(csaCodigo, responsavel) && INCLUSAO.equalsIgnoreCase(operacao)) {
            try {
                return pesquisarConsignacaoController.listaConsignacoesAtivasCsa(csaCodigo, adeIdentificador, responsavel) <= 0;
            } catch (final AutorizacaoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new DespesaIndividualControllerException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel, ex);
            }
        }

        return true;
    }

    /**
     * Mantém um cache de parâmetros de serviço, e de serviço por consignatária.
     * @param serCnvRegs
     * @throws ViewHelperException
     */
    private void atualizaCacheParamCnv(Map<String, Object> entradaValida, List<Map<String, Object>> serCnvRegs) throws AutorizacaoControllerException, ViewHelperException {
        for (final Map<String, Object> cnvCto : serCnvRegs) {
            try {
                // Cache que mapeia os parâmetros de serviço aos códigos dos convênios relacionados
                if (!cacheParametrosCnv.containsKey(cnvCto.get(Columns.CNV_CODIGO).toString())) {
                    final Map<String, Object> parSvc = new HashMap<>();
                    final String svcCodigo = cnvCto.get(Columns.SVC_CODIGO).toString();

                    // Parâmetros de serviços
                    List<TransferObject> parametros = parametroController.selectParamSvcCse(svcCodigo, responsavel);
                    Iterator<TransferObject> itP = parametros.iterator();
                    TransferObject paramCto = null;
                    while (itP.hasNext()) {
                        paramCto = itP.next();
                        parSvc.put(paramCto.getAttribute(Columns.TPS_CODIGO).toString(), paramCto.getAttribute(Columns.PSE_VLR));
                    }

                    // Todos os parametros de serviço que são sobrepostos a nível de CSA e SVC:
                    // ao acrescentar novos parametros, deve-se incluir aqui também
                    final List<String> tpsCodigos = new ArrayList<>();
                    tpsCodigos.add(CodedValues.TPS_INDICE);
                    tpsCodigos.add(CodedValues.TPS_PERMITE_REPETIR_INDICE_CSA);
                    tpsCodigos.add(CodedValues.TPS_PERMITE_CONTRATO_SUPER_SER_CSA);
                    tpsCodigos.add(CodedValues.TPS_CARENCIA_MAXIMA);
                    tpsCodigos.add(CodedValues.TPS_CARENCIA_MINIMA);
                    tpsCodigos.add(CodedValues.TPS_BLOQUEIA_INCLUSAO_LOTE_RSE_TIPO);
                    tpsCodigos.add(CodedValues.TPS_EXIGE_SENHA_SERVIDOR_LOTE);
                    tpsCodigos.add(CodedValues.TPS_INF_BANCARIA_OBRIGATORIA_CSA);

                    parametros = parametroController.selectParamSvcCsa(svcCodigo, csaCodigoCorrente, tpsCodigos, false, responsavel);
                    itP = parametros.iterator();
                    while (itP.hasNext()) {
                        paramCto = itP.next();
                        if (CodedValues.TPS_CARENCIA_MINIMA.equals(paramCto.getAttribute(Columns.TPS_CODIGO))) {
                            parSvc.put("CARENCIA_MINIMA", paramCto.getAttribute(Columns.PSC_VLR));
                        } else if (CodedValues.TPS_CARENCIA_MAXIMA.equals(paramCto.getAttribute(Columns.TPS_CODIGO))) {
                            parSvc.put("CARENCIA_MAXIMA", paramCto.getAttribute(Columns.PSC_VLR));
                        } else {
                            parSvc.put(paramCto.getAttribute(Columns.TPS_CODIGO).toString(), paramCto.getAttribute(Columns.PSC_VLR));
                        }
                    }

                    // Busca o serviço de cartão de crédito do qual o serviço depende, caso exista.
                    if (NaturezaRelSvc.getInstance().exists(CodedValues.TNT_CARTAO)) {
                        final List<TransferObject> servicosCartaoCredito = parametroController.getRelacionamentoSvc(CodedValues.TNT_CARTAO, null, svcCodigo, responsavel);

                        if ((servicosCartaoCredito != null) && !servicosCartaoCredito.isEmpty()) {
                            final TransferObject cto = servicosCartaoCredito.get(0);
                            final String svcCartao = cto.getAttribute(Columns.RSV_SVC_CODIGO_ORIGEM).toString();
                            parSvc.put("SERVICO_CARTAOCREDITO", svcCartao);
                        }
                    }

                    cacheParametrosCnv.put(cnvCto.get(Columns.CNV_CODIGO).toString(), parSvc);
                }
            } catch (final ParametroControllerException e) {
                LOG.error("Erro na busca de parametro: " + e.getMessage());
                throw new ViewHelperException(MENSAGEM_ERRO_INTERNO_SISTEMA, responsavel);
            }
        }
    }

    /**
     * Consulta o parâmetro de consignatária TPA_UTILIZA_PRIMEIRO_CNV_DISPONIVEL_LOTE para
     * determinar se, em caso de haver mais de um convênio ativo disponível para o código de verba,
     * o sistema deve utilizar o primeiro disponível (ou seja podeReservarMargem = true).
     * @return
     */
    private static boolean utilizarPrimeiroCnvDisponivel(String csaCodigo, AcessoSistema responsavel) {
        return TPA_SIM.equals(ParamCsa.getParamCsa(csaCodigo, CodedValues.TPA_UTILIZA_PRIMEIRO_CNV_DISPONIVEL_LOTE, responsavel));
    }

    /**
     * Consulta o parâmetro de consignatária TPA_UTILIZA_SERVIDOR_COM_MAIOR_MARGEM_LOTE para
     * determinar se, em caso de haver mais de um registro servidor disponível,
     * o sistema deve utilizar aquele que possui maior margem.
     * @return
     */
    private static boolean utilizarRegistroServidorMaiorMargem(String csaCodigo, AcessoSistema responsavel) {
        return TPA_SIM.equals(ParamCsa.getParamCsa(csaCodigo, CodedValues.TPA_UTILIZA_SERVIDOR_COM_MAIOR_MARGEM_LOTE, responsavel));
    }

    /**
     * Consulta o parâmetro de consignatária TPA_UTILIZA_SERVIDOR_COM_MENOR_MARGEM_LOTE para
     * determinar se, em caso de haver mais de um registro servidor disponível,
     * o sistema deve utilizar aquele que possui menor margem.
     * @return
     */
    private static boolean utilizarRegistroServidorMenorMargem(String csaCodigo, AcessoSistema responsavel) {
        return !utilizarRegistroServidorMaiorMargem(csaCodigo, responsavel) && TPA_SIM.equals(ParamCsa.getParamCsa(csaCodigo, CodedValues.TPA_UTILIZA_SERVIDOR_COM_MENOR_MARGEM_LOTE, responsavel));
    }

    /**
     * Consulta o parâmetro de consignatária TPA_TENTA_INCLUIR_RESERVA_TODOS_SER_LOTE para
     * determinar se, em caso de haver mais de um registro servidor disponível,
     * o sistema deve tentar incluir uma reserva de margem em todos.
     * @return
     */
    private static boolean tentarIncluirTodosRegistrosServidores(String csaCodigo, AcessoSistema responsavel) {
        return TPA_SIM.equals(ParamCsa.getParamCsa(csaCodigo, CodedValues.TPA_TENTA_INCLUIR_RESERVA_TODOS_SER_LOTE, responsavel));
    }

    /**
     * Consulta o parâmetro de consignatária TPA_PERMITE_INCLUSAO_COM_SER_DUPLICADO_LOTE para
     * determinar se, em caso de haver mais de um servidor disponível,
     * o sistema deve permitir a utilização daquele que possui maior margem ou
     * deve dar erro de duplicidade.
     * @return
     */
    private static boolean permiteInclusaoComServidorDuplicado(String csaCodigo, AcessoSistema responsavel) {
        return TPA_SIM.equals(ParamCsa.getParamCsa(csaCodigo, CodedValues.TPA_PERMITE_INCLUSAO_COM_SER_DUPLICADO_LOTE, responsavel));
    }

    /**
     * Consulta o parâmetro de consignatária TPA_PERMITE_PRAZO_MAIOR_99_LOTE para
     * determinar se é permitido prazos de contratos maiores que 99.
     * @return
     */
    private static boolean permitePrazoMaior99(String csaCodigo, AcessoSistema responsavel) {
        return TPA_SIM.equals(ParamCsa.getParamCsa(csaCodigo, CodedValues.TPA_PERMITE_PRAZO_MAIOR_99_LOTE, responsavel));
    }

    /**
     * Consulta o parâmetro de consignatária TPA_PRIORIZAR_SVC_ORIGEM_RENEGOCIACAO para
     * determinar se, tentar priorizar o serviço de origem na renegociação.
     * @return
     */
    private static boolean tentaPriorizarSvcOrigemRenegociacao(String csaCodigo, AcessoSistema responsavel) {
        return TPA_SIM.equals(ParamCsa.getParamCsa(csaCodigo, CodedValues.TPA_PRIORIZAR_SVC_ORIGEM_RENEGOCIACAO, responsavel));
    }

    /**
     * Consulta o parâmetro de consignatária TPA_ADE_IDENTIFICADOR_UNICO_VIA_LOTE para
     * determinar se o identificador deve ser único
     * @return
     */
    private static boolean adeIdentificadorUnicoViaLote(String csaCodigo, AcessoSistema responsavel) {
        return TPA_SIM.equals(ParamCsa.getParamCsa(csaCodigo, CodedValues.TPA_ADE_IDENTIFICADOR_UNICO_VIA_LOTE, responsavel));
    }

    /**
     * Consulta o parâmetro de consignatária TPA_PERMITE_ALTERAR_ADE_SEM_MOTIVO_OPERACAO_VIA_LOTE para
     * determinar se o motivo de operação deve ser informado
     * @return
     */
    private static boolean permiteAlterarAdeSemMotivoOperacao(String csaCodigo, AcessoSistema responsavel) {
        return TPA_SIM.equals(ParamCsa.getParamCsa(csaCodigo, CodedValues.TPA_PERMITE_ALTERAR_ADE_SEM_MOTIVO_OPERACAO_VIA_LOTE, responsavel));
    }

    /**
     * Consulta o parâmetro de consignatária TPA_INF_MODALIDADE_OPERACAO_OBRIGATORIO para
     * determinar se a modalidade de operação é obrigatória
     * @return
     */
    private static boolean exigeModalidadeOperacao(String csaCodigo, AcessoSistema responsavel) {
        return TPA_SIM.equals(ParamCsa.getParamCsa(csaCodigo, CodedValues.TPA_INF_MODALIDADE_OPERACAO_OBRIGATORIO, responsavel));
    }

    /**
     * Consulta o parâmetro de consignatária TPA_INFORMAR_MATRICULA_NA_CSA_OBRIGATORIO para
     * determinar se a matrícula do servidor é obrigatória
     * @return
     */
    private static boolean exigeMatriculaSerCsa(String csaCodigo, AcessoSistema responsavel) {
        return TPA_SIM.equals(ParamCsa.getParamCsa(csaCodigo, CodedValues.TPA_INFORMAR_MATRICULA_NA_CSA_OBRIGATORIO, responsavel));
    }

    /**
     * Consulta o parâmetro de consignatária TPA_UTILIZA_APENAS_CPF_SERVIDOR_LOTE para
     * determinar se utiliza apenas CPF do servidor na importação via lote
     * @return
     */
    private static boolean utilizaApenasCpfLote(String csaCodigo, AcessoSistema responsavel) {
        return TPA_SIM.equals(ParamCsa.getParamCsa(csaCodigo, CodedValues.TPA_UTILIZA_APENAS_CPF_SERVIDOR_LOTE, responsavel));
    }

    /**
     * Consulta o parâmetro de consignatária TPA_RETORNA_ADE_NUMERO_ARQ_CRITICA_INCLUSAO para
     * determinar se o ADE_NUMERO deve ser retornado na crítica do lote
     * @return
     */
    private static boolean insereAdeNumeroCriticaLote(String csaCodigo, AcessoSistema responsavel) {
        return TPA_SIM.equals(ParamCsa.getParamCsa(csaCodigo, CodedValues.TPA_RETORNA_ADE_NUMERO_ARQ_CRITICA_INCLUSAO, responsavel));
    }

    private boolean bloqueiaInclusaoRseTipo(Map<String, Object> dadosServidorConvenio) {
        // DESENV-14405 : Parâmetro bloqueia inclusão de contratos via lote de servidores de acordo com rse_tipo e a partir da regex
        // cadastrada no parâmetro de sistema 796 retira o servidor ou mantém para continuação da importação.
        final String regex = ParamSist.getInstance().getParam(CodedValues.TPC_EXPRESSAO_REGULAR_RETIRAR_RSE_INCLUSAO_LOTE, responsavel) != null ? (String) ParamSist.getInstance().getParam(CodedValues.TPC_EXPRESSAO_REGULAR_RETIRAR_RSE_INCLUSAO_LOTE, responsavel) : "";
        final String cnvCodigo = (String) dadosServidorConvenio.get(Columns.CNV_CODIGO);
        final String rseTipo = (String) dadosServidorConvenio.get(Columns.RSE_TIPO);

        final Map<String, Object> paramCnv = cacheParametrosCnv.get(cnvCodigo);

        final boolean svcBloqueia = (!TextHelper.isNull(paramCnv.get(CodedValues.TPS_BLOQUEIA_INCLUSAO_LOTE_RSE_TIPO)) && CodedValues.PSC_BOOLEANO_SIM.equals(paramCnv.get(CodedValues.TPS_BLOQUEIA_INCLUSAO_LOTE_RSE_TIPO)));
        return svcBloqueia && !TextHelper.isNull(rseTipo) ? rseTipo.matches(regex) : false;
    }

    private String converterParametrosJson() {
        // Objeto usado para converter o Map com campos de entrada em JSON
        final Gson gson = new Gson();
        final Map<String, Object> paramMap = new HashMap<>();

        paramMap.put("csaCodigo", csaCodigo);
        paramMap.put("corCodigo", corCodigo);
        paramMap.put("validar", validar);
        paramMap.put("serAtivo", serAtivo);
        paramMap.put("cnvAtivo", cnvAtivo);
        paramMap.put("permiteLoteAtrasado", permiteLoteAtrasado);
        paramMap.put("permiteReducaoLancamentoCartao", permiteReducaoLancamentoCartao);
        paramMap.put("periodoConfiguravel", DateHelper.format(periodoConfiguravel, YYYY_MM_DD));
        paramMap.put("nomeArqXmlEntrada", entradaImpLote);
        paramMap.put("nomeArqXmlTradutor", tradutorImpLote);
        paramMap.put("nomeArquivoEntrada", nomeArquivoLote);
        paramMap.put("pastaArquivoLote", pastaArquivoLote);
        paramMap.put("ipAcesso", responsavel.getIpUsuario());

        return gson.toJson(paramMap);
    }
}