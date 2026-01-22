package com.zetra.econsig.service.folha;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.exception.ImportaMargemException;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ValidaImportacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.folha.margem.ImportaMargem;
import com.zetra.econsig.folha.margem.ImportaMargemFactory;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.criptografia.CriptografiaArquivos;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.folha.CacheDependenciasServidor;
import com.zetra.econsig.helper.folha.HistoricoHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.servidor.GeradorCpfServidor;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorArquivoTexto;
import com.zetra.econsig.parser.EscritorBaseDeDados;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.ITradutor;
import com.zetra.econsig.parser.Leitor;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTextoSimpletl;
import com.zetra.econsig.parser.LeitorArquivoTextoZip;
import com.zetra.econsig.parser.LeitorBaseDeDados;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.parser.TradutorSimpletl;
import com.zetra.econsig.persistence.BatchManager;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.dao.CalculoMargemDAO;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.HistoricoMargemDAO;
import com.zetra.econsig.persistence.dao.MargemDAO;
import com.zetra.econsig.persistence.dao.ServidorDAO;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.Banco;
import com.zetra.econsig.persistence.entity.BaseCalcRegistroServidor;
import com.zetra.econsig.persistence.entity.BaseCalcRegistroServidorHome;
import com.zetra.econsig.persistence.entity.BaseCalcRegistroServidorId;
import com.zetra.econsig.persistence.entity.CapacidadeRegistroSer;
import com.zetra.econsig.persistence.entity.CargoRegistroServidor;
import com.zetra.econsig.persistence.entity.CargoRegistroServidorHome;
import com.zetra.econsig.persistence.entity.Consignante;
import com.zetra.econsig.persistence.entity.ConsignanteHome;
import com.zetra.econsig.persistence.entity.ContrachequeRegistroServidorHome;
import com.zetra.econsig.persistence.entity.EstabelecimentoHome;
import com.zetra.econsig.persistence.entity.HistoricoMargemFolha;
import com.zetra.econsig.persistence.entity.HistoricoMargemFolhaHome;
import com.zetra.econsig.persistence.entity.MargemRegistroServidor;
import com.zetra.econsig.persistence.entity.MargemRegistroServidorHome;
import com.zetra.econsig.persistence.entity.NivelEscolaridade;
import com.zetra.econsig.persistence.entity.NivelEscolaridadeHome;
import com.zetra.econsig.persistence.entity.OcorrenciaRegistroSer;
import com.zetra.econsig.persistence.entity.OcorrenciaRegistroServidorHome;
import com.zetra.econsig.persistence.entity.OrgaoHome;
import com.zetra.econsig.persistence.entity.PadraoRegistroServidor;
import com.zetra.econsig.persistence.entity.PadraoRegistroServidorHome;
import com.zetra.econsig.persistence.entity.PerfilUsuarioHome;
import com.zetra.econsig.persistence.entity.Plano;
import com.zetra.econsig.persistence.entity.PlanoHome;
import com.zetra.econsig.persistence.entity.PostoRegistroServidor;
import com.zetra.econsig.persistence.entity.PostoRegistroServidorHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.persistence.entity.ServidorHome;
import com.zetra.econsig.persistence.entity.StatusBeneficiario;
import com.zetra.econsig.persistence.entity.StatusRegistroServidor;
import com.zetra.econsig.persistence.entity.StatusServidor;
import com.zetra.econsig.persistence.entity.SubOrgao;
import com.zetra.econsig.persistence.entity.SubOrgaoHome;
import com.zetra.econsig.persistence.entity.TipoBeneficiario;
import com.zetra.econsig.persistence.entity.TipoHabitacao;
import com.zetra.econsig.persistence.entity.TipoHabitacaoHome;
import com.zetra.econsig.persistence.entity.TipoRegistroServidor;
import com.zetra.econsig.persistence.entity.TipoRegistroServidorHome;
import com.zetra.econsig.persistence.entity.Unidade;
import com.zetra.econsig.persistence.entity.UnidadeHome;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.persistence.entity.UsuarioHome;
import com.zetra.econsig.persistence.entity.UsuarioSer;
import com.zetra.econsig.persistence.entity.UsuarioSerHome;
import com.zetra.econsig.persistence.entity.UsuarioSerId;
import com.zetra.econsig.persistence.entity.VinculoRegistroServidor;
import com.zetra.econsig.persistence.entity.VinculoRegistroServidorHome;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoTransferenciaQuery;
import com.zetra.econsig.persistence.query.contracheque.ListaContrachequeServidorDestinoQuery;
import com.zetra.econsig.persistence.query.parcela.ObtemTotalParcelasTransfSemRetornoQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicoNaturezaSemTransfQuery;
import com.zetra.econsig.persistence.query.servidor.ListaRegistroServidorQuery;
import com.zetra.econsig.persistence.query.servidor.ListaRegistrosServidoresQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorNaoBeneficiarioQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorTransferenciaQuery;
import com.zetra.econsig.persistence.query.servidor.ObtemQtdRegistroServidorAtivoQuery;
import com.zetra.econsig.persistence.query.servidor.ObtemServidorNaoPertenceEntidadeQuery;
import com.zetra.econsig.persistence.query.servidor.RecuperaMargemExtraMediaTotalQuery;
import com.zetra.econsig.persistence.query.servidor.RecuperaMargemMediaTotalQuery;
import com.zetra.econsig.service.beneficios.BeneficiarioController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.EncerrarConsignacaoController;
import com.zetra.econsig.service.consignacao.TransferirConsignacaoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.sdp.DespesaIndividualController;
import com.zetra.econsig.service.sdp.PermissionarioController;
import com.zetra.econsig.service.sdp.PostoRegistroServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.NaturezaPlanoEnum;
import com.zetra.econsig.values.OperacaoHistoricoMargemEnum;
import com.zetra.econsig.values.StatusBeneficiarioEnum;
import com.zetra.econsig.values.TipoBeneficiarioEnum;
import com.zetra.econsig.values.TipoParamValidacaoArqEnum;

/**
 * <p>Title: ImpCadMargemController</p>
 * <p>Description: Session Bean para a rotina de cadastro de margens.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ImpCadMargemControllerBean implements ImpCadMargemController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImpCadMargemControllerBean.class);

    private boolean margemComplementar = false;
    private boolean verificaVariacaoMargemLimiteDefinidoCsa = false;

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private MargemController margemController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private DespesaIndividualController despesaIndividualController;

    @Autowired
    private BeneficiarioController beneficiarioController;

    @Autowired
    private PermissionarioController permissionarioController;

    @Autowired
    private ValidaImportacaoController validaImportacaoController;

    @Autowired
    private ImpRetornoController impRetornoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PostoRegistroServidorController postoRegistroServidorController;

    @Autowired
    private TransferirConsignacaoController transferirConsignacaoController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private EncerrarConsignacaoController encerrarConsignacaoController;

    /**
     * Realiza a importação do cadastro das margens dos servidores
     * @param nomeArquivoEntrada : nome do arquivo contendo as margens
     * @param tipoEntidade       : EST ou ORG
     * @param codigoEntidade     : código do órgão ou estabelecimento, de acordo com  tipoEntidade
     * @param margemTotal        : informa se é importação total das margens
     * @param geraTransferidos   : informa se deve gerar arquivo de transferidos
     * @param responsavel        : usuário responsável
     * @throws ServidorControllerException
     * @return Nome do arquivo de transferidos gerado, ou null
     */
    @Override
    public String importaCadastroMargens(String nomeArquivoEntrada, String tipoEntidade, String codigoEntidade, boolean margemTotal, boolean geraTransferidos, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            if (nomeArquivoEntrada.endsWith(".crypt")) {
                final File arquivoPlano = CriptografiaArquivos.descriptografarArquivo(nomeArquivoEntrada, true, responsavel);
                if (arquivoPlano != null) {
                    nomeArquivoEntrada = arquivoPlano.getAbsolutePath();
                }
            }

            // Recupera o codigo do orgao/estabelecimento
            String orgCodigo = null;
            String estCodigo = null;
            if ("ORG".equalsIgnoreCase(tipoEntidade)) {
                orgCodigo = codigoEntidade;
            } else if ("EST".equalsIgnoreCase(tipoEntidade)) {
                estCodigo = codigoEntidade;
            }

            // Inicializa a lista de códigos de entidades
            List<String> codigos = null;
            if (!TextHelper.isNull(codigoEntidade)) {
                codigos = new ArrayList<>();
                codigos.add(codigoEntidade);
            }
            final List<String> orgCodigos = ("ORG".equalsIgnoreCase(tipoEntidade) ? codigos : null);
            final List<String> estCodigos = ("EST".equalsIgnoreCase(tipoEntidade) ? codigos : null);

            ImportaMargem importadorMargem = null;
            final String importadorMargemClassName = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CLASSE_IMPORTADOR_MARGEM, responsavel);
            if (!TextHelper.isNull(importadorMargemClassName)) {
                importadorMargem = ImportaMargemFactory.getImportadorMargem(importadorMargemClassName);
                if (importadorMargem.sobreporImportacaoMargem(tipoEntidade, codigos, responsavel)) {
                    return importadorMargem.importaCadastroMargens(nomeArquivoEntrada, tipoEntidade, codigos, margemTotal, geraTransferidos, responsavel);
                }
            }

            // Pré-processamento da importação de margem usando método da classe específica do gestor.
            if (importadorMargem != null) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.preProcessamento.importacao.margem.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                importadorMargem.preImportacaoMargem(tipoEntidade, codigos, responsavel);
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.preProcessamento.importacao.margem.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            // Se importação de margem total, recupera o total de servidores e a média das margens
            // antes da carga para comparar ao final do processo
            int totalSerAtivoAntigo = 0;
            List<TransferObject> margemMediaTotalAntiga = null;
            List<TransferObject> margemExtraMediaTotalAntiga = new ArrayList<>();

            if (margemTotal) {
                final List<TransferObject> lstHisProcMargem = margemController.lstHistoricoProcMargem(orgCodigos, estCodigos, responsavel);
                if ((lstHisProcMargem != null) && !lstHisProcMargem.isEmpty()) {
                    final TransferObject hisProcMargem = lstHisProcMargem.iterator().next();
                    totalSerAtivoAntigo = Integer.parseInt(hisProcMargem.getAttribute(Columns.HPM_QTD_SERVIDORES_DEPOIS).toString());
                }
                final List<TransferObject> lstHisMediaMargem = margemController.lstHistoricoMediaMargem(orgCodigos, estCodigos, responsavel);
                if ((lstHisMediaMargem != null) && !lstHisMediaMargem.isEmpty()) {
                    margemMediaTotalAntiga = lstHisMediaMargem;
                }

                // Caso não exista histórico da quantidade de servidores ativos, faz a pesquisa na tabela principal
                if (totalSerAtivoAntigo == 0) {
                    totalSerAtivoAntigo = contarRegistroServidor(null, orgCodigos, estCodigos, false, responsavel);
                }
                // Caso não exista histórico da média das margens antigas, faz a pesquisa na tabela principal
                if ((margemMediaTotalAntiga == null) || margemMediaTotalAntiga.isEmpty()) {
                    margemMediaTotalAntiga = recuperaMargemMediaTotal(orgCodigos, estCodigos, false, responsavel);
                }
                margemExtraMediaTotalAntiga = recuperaMargemExtraMediaTotal(orgCodigos, estCodigos, false, responsavel);
            } else {
                margemComplementar = true;
            }

            // Determina os nomes dos arquivos de configuração da rotina de carga de margem
            final String[] nomesArquivos = obtemArquivosConfiguracao(nomeArquivoEntrada, estCodigo, orgCodigo, responsavel);
            final String nomeArqConfEntrada = nomesArquivos[0];
            final String nomeArqConfTradutor = nomesArquivos[1];
            final String nomeArqConfSaida = nomesArquivos[2];
            final String nomeArqCritica = nomesArquivos[3];
            final String nomeArqConfUnico = nomesArquivos[4];

            // Inicia o processo de importação de margem
            final ServidorDAO servidor = DAOFactory.getDAOFactory().getServidorDAO();

            // Inicia gravação de histórico de margem
            final HistoricoMargemDAO historicoMargemDAO = DAOFactory.getDAOFactory().getHistoricoMargemDAO();
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.historico.margem.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            historicoMargemDAO.iniciarHistoricoMargem(orgCodigos, estCodigos, null, OperacaoHistoricoMargemEnum.IMPORTACAO_MARGEM);
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.historico.margem.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

            // Cria tabela temporária com o histórico dos registro servidores para comparação
            final MargemDAO margemDAO = DAOFactory.getDAOFactory().getMargemDAO();
            margemDAO.criaTabelaHistoricoRse(orgCodigos, estCodigos);

            // Cria tabela temporária com a margem folha do último período processado para considerar como margem anterior há ser usado para cálculo de variação para limite imposto pela consignatárias
            final List<TransferObject> lstParamVariacaoMargemCsa = parametroController.selectParamCsa(null, CodedValues.TPA_PERCENTUAL_VARIACAO_MENSAL_MARGEM_POR_MATRICULA, responsavel);

            for (final TransferObject paramVaraicaoMargemCsa : lstParamVariacaoMargemCsa) {
                final String pcsVlr = (String) paramVaraicaoMargemCsa.getAttribute(Columns.PCS_VLR);
                if (!TextHelper.isNull(pcsVlr) && (Float.valueOf(pcsVlr) > 0)) {
                    verificaVariacaoMargemLimiteDefinidoCsa = true;
                    break;
                }
            }

            if (verificaVariacaoMargemLimiteDefinidoCsa && margemComplementar) {
                margemDAO.criaTabelaHistoricoRseMargemComplementar();
            }
            
            final boolean zerarMargemExclusao = !ParamSist.paramEquals(CodedValues.TPC_ZERAR_MARGEM_AO_EXCLUIR_SERVIDOR, CodedValues.TPC_NAO, responsavel);

            // Seta a margem dos servidores para um valor de marca
            // que irá identificar os servidores que não foram atualizados
            if (margemTotal) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.zera.margem.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                servidor.zeraMargem(tipoEntidade, codigos, zerarMargemExclusao);
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.zera.margem.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            // Efetua a carga do arquivo
            if (!TextHelper.isNull(nomeArqConfUnico) || TextHelper.isNull(nomeArqConfSaida)) {
                processaArquivoSemXmlSaida(nomeArquivoEntrada, nomeArqConfEntrada, nomeArqConfTradutor, nomeArqCritica, nomeArqConfUnico, importadorMargem, orgCodigos, estCodigos, responsavel);
            } else {
                processaArquivo(nomeArquivoEntrada, nomeArqConfEntrada, nomeArqConfTradutor, nomeArqConfSaida, nomeArqCritica, responsavel);
            }

            final String periodo = impRetornoController.recuperaPeriodoRetorno(CodedValues.TIPO_RETORNO_NORMAL, null, orgCodigos, estCodigos, responsavel);
            if (margemTotal) {
                // Exclui os servidores que não foram enviados pela folha
                final List<String> listaSrs = CodedValues.SRS_ATIVOS;
                final ListaRegistrosServidoresQuery query = new ListaRegistrosServidoresQuery(listaSrs, zerarMargemExclusao ? CodedValues.MARGEM_SERVIDOR_PRESTES_A_SER_EXCLUIDO : null);
                final List<TransferObject> excluidosTO = query.executarDTO();

                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.exclui.servidores.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                servidor.excluiServidores(tipoEntidade, codigos, (margemTotal && geraTransferidos), zerarMargemExclusao);
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.exclui.servidores.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

                // Valida percentual máximo de variação do número de servidores ativos na importação do cadastro de margens
                final int totalSerAtivoAtual = validaVariacaoServidores(orgCodigos, estCodigos, totalSerAtivoAntigo, responsavel);
                // Valida percentual máximo de variação da margem bruta na importação do cadastro de margens
                final Map<Short, Map<String, BigDecimal>> mediaMargem = validaVariacaoMargens(orgCodigos, estCodigos, margemMediaTotalAntiga, margemExtraMediaTotalAntiga, responsavel);

                // Salva histórico do processamento da importação das margens
                final TransferObject historicoMargem = new CustomTransferObject();
                historicoMargem.setAttribute(Columns.HPM_PERIODO, periodo);
                historicoMargem.setAttribute(Columns.HPM_QTD_SERVIDORES_ANTES, totalSerAtivoAntigo);
                historicoMargem.setAttribute(Columns.HPM_QTD_SERVIDORES_DEPOIS, totalSerAtivoAtual);
                final List<Short> lstMarCodigosExtra = new ArrayList<>();
                for (final TransferObject margemExtra : margemExtraMediaTotalAntiga) {
                    if (!TextHelper.isNull(margemExtra.getAttribute("MAR_CODIGO"))) {
                        lstMarCodigosExtra.add(Short.valueOf(margemExtra.getAttribute("MAR_CODIGO").toString()));
                    }
                }
                margemController.createHistoricoMargem(historicoMargem, mediaMargem, lstMarCodigosExtra, orgCodigos, estCodigos, responsavel);

                // Cria ocorrência dos servidores excluídos
                servidorController.criaOcorrenciaRSE(excluidosTO, CodedValues.TOC_RSE_EXCLUSAO_POR_CARGA_MARGEM, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.exclusao.carga.margem", responsavel), null, responsavel);
            }

            // Recalcula a margem dos servidores: Verifica parâmetro de sistema
            if (ParamSist.paramEquals(CodedValues.TPC_RECALCULA_MARGEM_IMP_MARGEM, CodedValues.TPC_SIM, responsavel)) {
                // Inicia gravação de histórico de margem, caso não exista, para matrículas novas
                historicoMargemDAO.iniciarHistoricoMargemCasoNaoExista(orgCodigos, estCodigos, null, OperacaoHistoricoMargemEnum.IMPORTACAO_RET_MOV_FIN);
                // Efetual recálculo de margem sem gravação do histórico, que será criado a parte
                margemController.recalculaMargem(tipoEntidade, codigos, servidor, true, true, responsavel);
            }

            // Gera o arquivo de transferidos somente se a importação for de margem total e
            // o parametro de consignante de gerar transferidos for verdadeiro.
            String nomeArqTransferidos = null;
            if (margemTotal && geraTransferidos) {
                // Pré-processamento da geração de transferidos usando método da classe específica do gestor.
                if (importadorMargem != null) {
                    LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.pre.geracao.transferido.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                    importadorMargem.preGeracaoTransferidos(tipoEntidade, codigos, responsavel);
                    LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.pre.geracao.transferido.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                }

                nomeArqTransferidos = geraArquivoServidorTransferido(responsavel, tipoEntidade, codigoEntidade, importadorMargem);

                // Pos-processamento da geração de transferidos usando método da classe específica do gestor.
                if (importadorMargem != null) {
                    LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.pos.geracao.transferido.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                    importadorMargem.posGeracaoTransferidos(tipoEntidade, codigos, responsavel);
                    LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.pos.geracao.transferido.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                }
            }

            // Compara os registros servidores para ver quais foram alterados e gera ocorrência específica de acordo com a alteração no registro servidor
            // DESENV-21360: Foi necessário alterar para a inclusão ser via DAO por causa de performance
            margemDAO.insereOcorrenciaRseStatusAlterados(orgCodigos, estCodigos, responsavel);

            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_SDP, CodedValues.TPC_SIM, responsavel)) {
                // Compara os registros servidores para ver quais foram alterados e gera ocorrência específica de acordo com a alteração
                final List<TransferObject> rseAlterados = margemDAO.lstTabelaPostoTipoRseAlterados(orgCodigos, estCodigos);

                if ((rseAlterados != null) && !rseAlterados.isEmpty()) {
                    // Monta cache de tipo de registro servidor
                    final Map<String, String> tipos = new HashMap<>();
                    final List<TipoRegistroServidor> lstTiposRse = TipoRegistroServidorHome.list();
                    for (final TipoRegistroServidor tipoRse : lstTiposRse) {
                        tipos.put(tipoRse.getTrsCodigo(), tipoRse.getTrsDescricao());
                    }

                    // Monta cache de postos de registro servidor
                    final Map<String, String> postos = new HashMap<>();
                    final List<TransferObject> lstPostos = postoRegistroServidorController.lstPostoRegistroServidor(null, -1, -1, responsavel);
                    for (final TransferObject posto : lstPostos) {
                        postos.put(posto.getAttribute(Columns.POS_CODIGO).toString(), posto.getAttribute(Columns.POS_DESCRICAO).toString());
                    }

                    // Verifica se existem planos de taxa de uso para executar a atualização das despesas individuais
                    final List<Plano> planosTaxaUso = PlanoHome.findByNatureza(NaturezaPlanoEnum.TAXA_USO);
                    final boolean atualizaTaxaUso = (planosTaxaUso != null) && !planosTaxaUso.isEmpty();

                    final String obsAltPostoRse = ApplicationResourcesHelper.getMessage("mensagem.cadMargem.posto.servidor.alterado.de.para", responsavel);
                    final String obsAltTipoRse = ApplicationResourcesHelper.getMessage("mensagem.cadMargem.tipo.servidor.alterado.de.para", responsavel);

                    for (final TransferObject rseAlterado : rseAlterados) {
                        final String rseCodigo = rseAlterado.getAttribute(Columns.RSE_CODIGO).toString();
                        final String trsCodigo = (String) rseAlterado.getAttribute(Columns.TRS_CODIGO);
                        final String posCodigo = (String) rseAlterado.getAttribute(Columns.POS_CODIGO);
                        final String trsCodigoOld = (String) rseAlterado.getAttribute("trs_codigo_old");
                        final String posCodigoOld = (String) rseAlterado.getAttribute("pos_codigo_old");

                        if ((TextHelper.isNull(trsCodigo) && !TextHelper.isNull(trsCodigoOld)) || (!TextHelper.isNull(trsCodigo) && TextHelper.isNull(trsCodigoOld)) || (!TextHelper.isNull(trsCodigo) && !TextHelper.isNull(trsCodigoOld) && !trsCodigo.equals(trsCodigoOld))) {
                            // Cria ocorrência para alteração de tipo do registro servidor
                            servidorController.criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_ALTERACAO_TIPO_REGISTRO_SERVIDOR, obsAltTipoRse.replace("<DE>", !TextHelper.isNull(trsCodigoOld) ? tipos.get(trsCodigoOld) : "").replace("<PARA>", !TextHelper.isNull(trsCodigo) ? tipos.get(trsCodigo) : ""), null, responsavel);
                        }

                        if ((TextHelper.isNull(posCodigo) && !TextHelper.isNull(posCodigoOld)) || (!TextHelper.isNull(posCodigo) && TextHelper.isNull(posCodigoOld)) || (!TextHelper.isNull(posCodigo) && !TextHelper.isNull(posCodigoOld) && !posCodigo.equals(posCodigoOld))) {
                            if (atualizaTaxaUso) {
                                // Recalcula o valor da taxa de uso de acordo com o novo posto do servidor
                                despesaIndividualController.alterarTaxaUsoByRse(rseCodigo, responsavel);
                            }

                            // Cria ocorrência para alteração de posto do registro servidor
                            servidorController.criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_ALTERACAO_POSTO_REGISTRO_SERVIDOR, obsAltPostoRse.replace("<DE>", !TextHelper.isNull(posCodigoOld) ? postos.get(posCodigoOld) : "").replace("<PARA>", !TextHelper.isNull(posCodigo) ? postos.get(posCodigo) : ""), null, responsavel);
                        }
                    }
                }
            }

            ////DESENV-16846 - Média margem folha
            final CalculoMargemDAO marDAO = DAOFactory.getDAOFactory().getCalculoMargemDAO();
            final int qntPeriodoMediaMargem = ParamSist.getIntParamSist(CodedValues.TPC_QTD_PERIODO_CALCULO_MEDIA_MARGEM, 12, responsavel);
            if (qntPeriodoMediaMargem > 0) {
                marDAO.calcularMediaMargem(qntPeriodoMediaMargem);
            }

            // Finaliza o historico de margem
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.finaliza.historico.margem.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            historicoMargemDAO.finalizarHistoricoMargem(orgCodigos, estCodigos, null, OperacaoHistoricoMargemEnum.IMPORTACAO_MARGEM);
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.finaliza.historico.margem.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

            // Pós-processamento da importação de margem usando método da classe específica do gestor.
            if (importadorMargem != null) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.posProcessamento.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                importadorMargem.posImportacaoMargem(tipoEntidade, codigos, responsavel);
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.posProcessamento.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            // Importa servidores que ainda não estão na tabela beneficiários
            if (ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, responsavel)) {
                importarServidoresModuloBeneficio(responsavel);
            }

            // CPF cadastrado para servidores não pode ser igual a um CPF de usuário CSA/COR ativo
            usuarioController.bloqueiaUsuarioCsaComCPFServidor(null, responsavel);

            final LogDelegate log = new LogDelegate(responsavel, Log.FOLHA, Log.IMPORTACAO_MARGEM, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.arquivo", responsavel, nomeArqConfSaida));
            log.write();

            // log de ocorrência de consignante
            consignanteController.createOcorrenciaCse(CodedValues.TOC_IMPORTACAO_MARGEM, responsavel);

            // log de ocorrência de orgão, se orgCodigo for diferente de nulo
            if (!TextHelper.isNull(orgCodigo)) {
                consignanteController.createOcorrenciaOrg(orgCodigo, CodedValues.TOC_IMPORTACAO_MARGEM, responsavel);
            }

            if (ParamSist.paramEquals(CodedValues.TPC_PREENCHER_VINCULO_REGISTRO_RSE_IMP_CAD_MARGEM, CodedValues.TPC_SIM, responsavel)) {
                margemDAO.alinhaVinculosRse();
            }

            // Renomeia o arquivo despois de concluido com sucesso
            FileHelper.rename(nomeArquivoEntrada, nomeArquivoEntrada + ".ok");

            // Verificamos a partir deste momento quais registros servidores devem ser bloqueados por variação da margem de acordo a definição de limite do parâmetro de CSA (85)
            if (verificaVariacaoMargemLimiteDefinidoCsa) {
                // Cria tabelas de histórico para cálculo do limite da variação de margem da consignatária
                margemDAO.criaTabelaVariacaoMargemLimiteDefinidoCSA(periodo, margemTotal, responsavel);
                iniciaBloqueioRseVariacaoMargemLimiteDefinidoCSA(margemDAO, margemTotal, responsavel);
            }

            return nomeArqTransferidos;
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            LOG.error(ex.getMessage(), ex);
            if (ex instanceof ZetraException) {
                throw new ServidorControllerException(ex);
            } else {
                throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    private void importarServidoresModuloBeneficio(AcessoSistema responsavel) throws Exception {
        final Session session = SessionUtil.getSession();
        final BatchManager batman = new BatchManager(session);
        int count = 0;

        try {
            final ListaServidorNaoBeneficiarioQuery query = new ListaServidorNaoBeneficiarioQuery();
            final List<TransferObject> servidores = query.executarDTO();

            final TipoBeneficiario tipoBeneficiario = session.getReference(TipoBeneficiario.class, TipoBeneficiarioEnum.TITULAR.tibCodigo);
            final StatusBeneficiario statusBeneficiario = session.getReference(StatusBeneficiario.class, StatusBeneficiarioEnum.ATIVO.sbeCodigo);

            for (final TransferObject servidor : servidores) {
                final String serCodigo = (String) servidor.getAttribute(Columns.SER_CODIGO);
                final String rseCodigo = (String) servidor.getAttribute(Columns.RSE_CODIGO);
                final String serNome = (String) servidor.getAttribute(Columns.SER_NOME);
                final String serCpf = (String) servidor.getAttribute(Columns.SER_CPF);
                final String serNroIdt = (String) servidor.getAttribute(Columns.SER_NRO_IDT);

                String serSexo = null;
                final Object sexoObj = servidor.getAttribute(Columns.SER_SEXO);
                if (!TextHelper.isNull(sexoObj)) {
                    if (sexoObj instanceof String) {
                        serSexo = (String) sexoObj;
                    } else if (sexoObj instanceof Character) {
                        serSexo = String.valueOf(sexoObj);
                    }
                }

                final String serTel = (String) servidor.getAttribute(Columns.SER_TEL);
                final Date serDataNasc = (Date) servidor.getAttribute(Columns.SER_DATA_NASC);

                final Object estCivilObj = servidor.getAttribute(Columns.SER_EST_CIVIL);
                String serEstCivil = null;

                if (!TextHelper.isNull(estCivilObj)) {
                    if (estCivilObj instanceof String) {
                        serEstCivil = (String) estCivilObj;
                    } else if (estCivilObj instanceof Character) {
                        serEstCivil = String.valueOf(estCivilObj);
                    }
                }

                final String serCelular = (String) servidor.getAttribute(Columns.SER_CELULAR);
                final String serNomeMae = (String) servidor.getAttribute(Columns.SER_NOME_MAE);

                final Servidor ser = session.getReference(Servidor.class, serCodigo);

                beneficiarioController.create(ser, tipoBeneficiario, null, (short) 0, serNome, serCpf, serNroIdt, serSexo, serTel, serCelular, serNomeMae, null, serDataNasc, serEstCivil, null, null, null, null, statusBeneficiario, null, null, null, null, rseCodigo, null, responsavel);
                batman.iterate();
                count++;

                if (count % 1000 == 0) {
                    LOG.debug("Beneficiarios carregados: " + count);
                }
            }
            LOG.debug("Beneficiarios carregados: " + count);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    @Override
    public String[] obtemArquivosConfiguracao(String nomeArquivoEntrada, String estCodigo, String orgCodigo, AcessoSistema responsavel) throws ServidorControllerException {
        // Diretório raiz do arquivos eConsig
        final String absolutePath = ParamSist.getDiretorioRaizArquivos();
        final String pathConf = absolutePath + File.separatorChar + "conf";

        final File arquivo = new File(nomeArquivoEntrada);
        // Nome do arquivo de Critica
        final String nomeArqCritica = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", responsavel) + arquivo.getName().replaceAll(".zip", ".txt");
        final String nomeArqCriticaCompleto = arquivo.getPath().substring(0, arquivo.getPath().indexOf(new File(nomeArquivoEntrada).getName()) - 1) + File.separatorChar + nomeArqCritica;

        // Nome dos arquivos a serem utilizados, com caminho completo
        String nomeArqConfEntrada = null;
        String nomeArqConfSaida = null;
        String nomeArqConfTradutor = null;

        // DESENV-9468 : Dar preferência à configuração do SimplETL
        String nomeArqConfUnico = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_INTEGRACAO_ORIENTADA_MARGEM, responsavel);
        if (!TextHelper.isNull(nomeArqConfUnico)) {
            final File arqConfUnico = new File(pathConf, nomeArqConfUnico);
            if (!arqConfUnico.exists() || !arqConfUnico.canRead()) {
                throw new ServidorControllerException("mensagem.erro.interno.arquivos.configuracao.unico.margem.nao.existe.ou.nao.pode.ser.lido", responsavel);
            }
            nomeArqConfUnico = arqConfUnico.getAbsolutePath();
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.confUnico.arg0", responsavel, nomeArqConfUnico));
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.critica.arg0", responsavel, nomeArqCriticaCompleto));
        } else {
            // Arquivos de configuração para importação de margem.
            final String nomeArqConfE = ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_MARGEM, responsavel).toString();
            final String nomeArqConfT = ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_MARGEM, responsavel).toString();
            // DESENV-8796 : Arquivo XML de saída opcional
            final String nomeArqConfS = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_SAIDA_IMP_MARGEM, responsavel);

            // Verifica se o processamento da margem é por orgão/estabelecimento, se for pega os arquivos
            // de configuração do diretório especifico, senão pega do diretório raiz. Se não tiver arquivo
            // de configuração no diretório do órgão/estabelecimento, usará o da raiz. Se não existir em ambos,
            // gerará exceção.
            if (!TextHelper.isNull(orgCodigo)) {
                // Arquivos de configuração por Órgão
                nomeArqConfEntrada = pathConf + File.separatorChar + "cse" + File.separatorChar + orgCodigo + File.separatorChar + nomeArqConfE;
                nomeArqConfTradutor = pathConf + File.separatorChar + "cse" + File.separatorChar + orgCodigo + File.separatorChar + nomeArqConfT;
                if (!TextHelper.isNull(nomeArqConfS)) {
                    nomeArqConfSaida = pathConf + File.separatorChar + "cse" + File.separatorChar + orgCodigo + File.separatorChar + nomeArqConfS;
                }
            } else if (!TextHelper.isNull(estCodigo)) {
                // Arquivos de configuração por Estabelecimento
                nomeArqConfEntrada = pathConf + File.separatorChar + "est" + File.separatorChar + estCodigo + File.separatorChar + nomeArqConfE;
                nomeArqConfTradutor = pathConf + File.separatorChar + "est" + File.separatorChar + estCodigo + File.separatorChar + nomeArqConfT;
                if (!TextHelper.isNull(nomeArqConfS)) {
                    nomeArqConfSaida = pathConf + File.separatorChar + "est" + File.separatorChar + estCodigo + File.separatorChar + nomeArqConfS;
                }
            }

            // Se não é importação por órgão/estabelecimento, ou é mas o arquivo não existe,
            // obtém os arquivos de configuração no path raiz.
            if (TextHelper.isNull(nomeArqConfEntrada) || !(new File(nomeArqConfEntrada).exists())) {
                nomeArqConfEntrada = pathConf + File.separatorChar + nomeArqConfE;
            }
            if (TextHelper.isNull(nomeArqConfTradutor) || !(new File(nomeArqConfTradutor).exists())) {
                nomeArqConfTradutor = pathConf + File.separatorChar + nomeArqConfT;
            }
            if ((TextHelper.isNull(nomeArqConfSaida) || !(new File(nomeArqConfSaida).exists())) && !TextHelper.isNull(nomeArqConfS)) {
                nomeArqConfSaida = pathConf + File.separatorChar + nomeArqConfS;
            }

            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.entrada.arg0", responsavel, nomeArqConfEntrada));
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.tradutor.arg0", responsavel, nomeArqConfTradutor));
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.saida.arg0", responsavel, nomeArqConfSaida));
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.critica.arg0", responsavel, nomeArqCriticaCompleto));

            // Verifica se os arquivos de configuração, seja por órgão/estabelecimento ou do padrão, existem e podem ser lidos
            final File arqConfEntrada = new File(nomeArqConfEntrada);
            final File arqConfTradutor = new File(nomeArqConfTradutor);
            final File arqConfSaida = (!TextHelper.isNull(nomeArqConfSaida) ? new File(nomeArqConfSaida) : null);
            if (!arqConfEntrada.exists() || !arqConfEntrada.canRead() || !arqConfTradutor.exists() || !arqConfTradutor.canRead() || ((arqConfSaida != null) && (!arqConfSaida.exists() || !arqConfSaida.canRead()))) {
                throw new ServidorControllerException("mensagem.erro.interno.arquivos.configuracao.entrada.tradutor.saida.margem.nao.existem.ou.nao.podem.ser.lido", responsavel);
            }
        }
        return new String[] { nomeArqConfEntrada, nomeArqConfTradutor, nomeArqConfSaida, nomeArqCriticaCompleto, nomeArqConfUnico };
    }

    private boolean processaArquivo(String nomeArquivoEntrada, String nomeArqConfEntrada, String nomeArqConfTradutor, String nomeArqConfSaida, String nomeArqCritica, AcessoSistema responsavel) throws ParserException, IOException {
        // Faz a importação do arquivo da folha
        LeitorArquivoTexto leitor = null;
        if (nomeArquivoEntrada.toLowerCase().endsWith(".zip")) {
            leitor = new LeitorArquivoTextoZip(nomeArqConfEntrada, nomeArquivoEntrada);
        } else {
            leitor = new LeitorArquivoTexto(nomeArqConfEntrada, nomeArquivoEntrada);
        }

        boolean temCritica = false;
        final String delimitador = leitor.getDelimitador() != null ? leitor.getDelimitador() : "|";

        final EscritorBaseDeDados escritor = new EscritorBaseDeDados(nomeArqConfSaida);
        final Tradutor tradutor = new Tradutor(nomeArqConfTradutor, leitor, escritor);
        LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.traducao.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

        tradutor.iniciaTraducao();
        final StringBuilder critica = new StringBuilder();
        final Map<String, List<TransferObject>> orgCnpjCache = new HashMap<>();
        final Map<String, List<TransferObject>> estCnpjCache = new HashMap<>();
        final Map<String, List<TransferObject>> orgCache = new HashMap<>();
        final Map<String, List<TransferObject>> estCache = new HashMap<>();
        final Map<String, List<TransferObject>> sboCache = new HashMap<>();
        final Map<String, List<TransferObject>> uniCache = new HashMap<>();
        final Map<String, List<TransferObject>> posCache = new HashMap<>();
        final Map<String, List<TransferObject>> trsCache = new HashMap<>();
        final Map<String, List<TransferObject>> capCache = new HashMap<>();

        int count = 0;

        while (tradutor.traduzProximo()) {
            count++;
            if ((count % 1000) == 0) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.linhas.lidas.arg0", responsavel, String.valueOf(count)));
            }

            final boolean possuiErro = escritor.possuiErro();

            // Nao houve insert, linha será incluída na crítica
            if (possuiErro) {
                // Linha do arquivo de entrada, será utilizado no arquivo de crítica
                final String linha = leitor.getLinha();
                // Dados traduzidos que foram enviados para o escritor
                final Map<String, Object> dados = tradutor.getDados();
                // Identificadores que serão validados no arquivo de margem
                final String orgIdent = (String) dados.get("ORG_IDENTIFICADOR");
                final String orgCnpj = (String) dados.get("ORG_CNPJ");
                final String estIdent = (String) dados.get("EST_IDENTIFICADOR");
                final String estCnpj = (String) dados.get("EST_CNPJ");
                final String sboIdent = (String) dados.get("SBO_IDENTIFICADOR");
                final String uniIdent = (String) dados.get("UNI_IDENTIFICADOR");
                final String postoRse = (String) dados.get("POS_CODIGO");
                final String tipoRse = (String) dados.get("TRS_CODIGO");
                final String capacRse = (String) dados.get("CAP_CODIGO");

                String erro = "";

                // Verifica existência de identificador de estabelecimento
                // \__ procura o identificador de estabelecimento, retornando false se nao encontrá-lo
                if ((estIdent != null) &&
                    !existeIdentificador(estCache, estIdent, Columns.EST_IDENTIFICADOR, responsavel)) {
                    erro += ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.erro.estabelecimento.nao.existe.arg0.arg1", responsavel, delimitador, estIdent);
                }

                // Verifica existência de CNPJ de estabelecimento
                // \__ procura o identificador de cnpj de estabelecimento, retornando false se nao encontrá-lo
                if ((estCnpj != null) &&
                    !existeIdentificador(estCnpjCache, estCnpj, Columns.EST_CNPJ, responsavel)) {
                    erro += ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.erro.estabelecimento.com.cnpj.nao.existe.arg0.arg1", responsavel, delimitador, estCnpj);
                }

                // Verifica existencia de identificador de orgao
                if (orgIdent != null) {
                    //procura o identificador de órgão, retornando false se não encontrá-lo
                    if (!existeIdentificador(orgCache, orgIdent, Columns.ORG_IDENTIFICADOR, responsavel)) {
                        erro += ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.erro.orgao.nao.existe.arg0.arg1", responsavel, delimitador, orgIdent);
                    } else if (estIdent != null) {
                        // Verifica se o órgão pertence ao estabelecimento
                        boolean pertence = false;
                        for (final TransferObject orgao : orgCache.get(orgIdent)) {
                            final String estIdentOrgCache = (String) orgao.getAttribute(Columns.EST_IDENTIFICADOR);
                            if (estIdentOrgCache.equals(estIdent)) {
                                pertence = true;
                                break;
                            }
                        }
                        if (!pertence) {
                            erro += ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.erro.orgao.nao.existe.no.estabelecimento", responsavel, delimitador, orgIdent, estIdent);
                        }
                    }
                }

                // Verifica existência de identificador de CNPJ de órgão
                if (orgCnpj != null) {
                    //procura o identificador de cnpj de órgão, retornando false se nao encontra-lo
                    if (!existeIdentificador(orgCnpjCache, orgCnpj, Columns.ORG_CNPJ, responsavel)) {
                        erro += ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.erro.orgao.com.cnpj.nao.existe.arg0.arg1", responsavel, delimitador, orgCnpj);
                    } else if (estCnpj != null) {
                        // Verifica se o órgão pertence ao estabelecimento
                        boolean pertence = false;
                        for (final TransferObject orgao : orgCnpjCache.get(orgCnpj)) {
                            final String estCnpjOrgCache = (String) orgao.getAttribute(Columns.EST_CNPJ);
                            if (estCnpjOrgCache.equals(estCnpj)) {
                                pertence = true;
                                break;
                            }
                        }
                        if (!pertence) {
                            erro += ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.erro.orgao.com.cnpj.nao.existe.no.estabelecimento.com.cnpj", responsavel, delimitador, orgCnpj, estCnpj);
                        }
                    }
                }

                // Verifica existência de identificador de sub-órgão
                // \__ procura o identificador de sub-órgao, retornando false se nao encontrá-lo
                if ((sboIdent != null) &&
                    !existeIdentificador(sboCache, sboIdent, Columns.SBO_IDENTIFICADOR, responsavel)) {
                    erro += ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.erro.suborgao.nao.existe.arg0.arg1", responsavel, delimitador, sboIdent);
                }

                // Verifica existência de identificador de unidade
                // \__ procura o identificador de unidade, retornando false se nao encontrá-lo
                if ((uniIdent != null) &&
                    !existeIdentificador(uniCache, uniIdent, Columns.UNI_IDENTIFICADOR, responsavel)) {
                    erro += ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.erro.unidade.nao.existe.arg0.arg1", responsavel, delimitador, uniIdent);
                }

                // Verifica existência de identificador de posto de registro servidor
                // \__procura o identificador de posto, retornando false se nao encontrá-lo
                if ((postoRse != null) &&
                    !existeIdentificador(posCache, postoRse, Columns.POS_CODIGO, responsavel)) {
                    erro += ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.erro.posto.nao.existe.arg0.arg1", responsavel, delimitador, postoRse);
                }

                // Verifica existência de identificador de tipo de registro servidor
                // \__procura o identificador de tipo, retornando false se nao encontrá-lo
                if ((tipoRse != null) &&
                    !existeIdentificador(trsCache, tipoRse, Columns.TRS_CODIGO, responsavel)) {
                    erro += ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.erro.tipo.nao.existe.arg0.arg1", responsavel, delimitador, tipoRse);
                }

                // Verifica existência de identificador de capacidade civil
                // \__procura o identificador de capacidade civil, retornando false se nao encontrá-lo
                if ((capacRse != null) &&
                    !existeIdentificador(capCache, capacRse, Columns.CAP_CODIGO, responsavel)) {
                    erro += ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.erro.tipo.nao.existe.arg0.arg1", responsavel, delimitador, capacRse);
                }

                if ("".equals(erro.trim())) {
                    // Se teve alguma crítica, porém o erro não foi detectado, inclui
                    // mensagem genérica de registro não atualizado
                    erro = ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.erro.registro.nao.atualizado.arg0", responsavel, delimitador);
                }

                // Grava a crítica no arquivo de saída
                critica.append(linha + erro + System.lineSeparator());
            }
        }

        LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.total.linhas.lidas.arg0", responsavel, String.valueOf(count)));

        // Escreve arquivo de critica
        if (critica.length() > 0) {
            temCritica = true;
            escreveCritica(critica, nomeArqCritica);
        }

        tradutor.encerraTraducao();

        LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.traducao.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
        return temCritica;
    }

    private boolean processaArquivoSemXmlSaida(String nomeArquivoEntrada, String nomeArqConfEntrada, String nomeArqConfTradutor, String nomeArqCritica, String nomeArqConfUnico, ImportaMargem importadorMargem, List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws ParserException, IOException, ServidorControllerException {
        final HashMap<String, Object> entrada = new HashMap<>();
        final EscritorMemoria escritor = new EscritorMemoria(entrada);
        LeitorArquivoTexto leitor = null;
        ITradutor tradutor;
        String delimitador;
        if (!TextHelper.isNull(nomeArqConfUnico)) {
            leitor = new LeitorArquivoTextoSimpletl(nomeArqConfUnico, nomeArquivoEntrada);
            tradutor = new TradutorSimpletl(nomeArqConfUnico, (LeitorArquivoTextoSimpletl) leitor, escritor);
        } else {
            // Faz a importação do arquivo da folha
            if (nomeArquivoEntrada.toLowerCase().endsWith(".zip")) {
                leitor = new LeitorArquivoTextoZip(nomeArqConfEntrada, nomeArquivoEntrada);
            } else {
                leitor = new LeitorArquivoTexto(nomeArqConfEntrada, nomeArquivoEntrada);
            }
            tradutor = new Tradutor(nomeArqConfTradutor, leitor, escritor);
        }

        final StringBuilder critica = new StringBuilder();
        delimitador = leitor.getDelimitador() != null ? leitor.getDelimitador() : "|";

        // Carrega caches necessários
        final CacheDependenciasServidor cacheEntidades = new CacheDependenciasServidor();
        cacheEntidades.carregarCache(responsavel);

        final Session session = SessionUtil.getSession();
        final BatchManager batman = new BatchManager(session).setThresold(1).disableLogDebug();

        try {
            int count = 0;

            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.traducao.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            tradutor.iniciaTraducao();

            String periodo = null;
            try {
                periodo = impRetornoController.recuperaPeriodoRetorno(CodedValues.TIPO_RETORNO_NORMAL, null, orgCodigos, estCodigos, responsavel);
            } catch (final ImpRetornoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }

            while (tradutor.traduzProximo()) {
                count++;
                if ((count % 1000) == 0) {
                    LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.linhas.lidas.arg0", responsavel, String.valueOf(count)));
                }

                // Linha do arquivo de entrada, será utilizado no arquivo de crítica
                final String linha = leitor.getLinha();
                final String erro = processarLinhaMargem(entrada, delimitador, cacheEntidades, session, importadorMargem, periodo, responsavel);

                if (!TextHelper.isNull(erro)) {
                    critica.append(linha + erro + System.lineSeparator());
                }

                // Limpa cache de sessão, removendo os objetos para a próxima iteração (motivo: performance)
                // session.clear();
                batman.iterate();
            }

            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.total.linhas.lidas.arg0", responsavel, String.valueOf(count)));

            tradutor.encerraTraducao();
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.traducao.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

            // Escreve arquivo de critica
            if (critica.length() > 0) {
                escreveCritica(critica, nomeArqCritica);
                return true;
            }
        } finally {
            SessionUtil.closeSession(session);
        }

        return false;
    }

    @Override
    public String processarLinhaMargem(Map<String, Object> entrada, CacheDependenciasServidor cacheEntidades, String periodo, AcessoSistema responsavel) throws ServidorControllerException {
        return processarLinhaMargem(entrada, "|", cacheEntidades, SessionUtil.getSession(), null, periodo, responsavel);
    }

    private String processarLinhaMargem(Map<String, Object> entrada, String delimitador, CacheDependenciasServidor cacheEntidades, Session session, ImportaMargem importadorMargem, String periodo, AcessoSistema responsavel) throws ServidorControllerException {
        final boolean omiteCpf = ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel);
        final boolean possuiPortalServidor = ParamSist.paramEquals(CodedValues.TPC_POSSUI_PORTAL_SERVIDOR, CodedValues.TPC_SIM, responsavel);
        final boolean loginServidorEstOrg = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, responsavel);
        final boolean bloqueiaEdicaoEmail = ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_EDICAO_EMAIL_SERVIDOR_CAD_FOLHA, CodedValues.TPC_SIM, responsavel);
        final boolean encerrarConsignacoes = ParamSist.paramEquals(CodedValues.TPC_ENCERRA_CONSIGNACOES_SERVIDOR_EXCLUIDO_CARGA_MARGEM, CodedValues.TPC_SIM, responsavel);
        final boolean reabrirConsignacoes = ParamSist.paramEquals(CodedValues.TPC_REABRIR_CONSIGNACOES_ENCERRADAS_CARGA_MARGEM, CodedValues.TPC_SIM, responsavel);
        final boolean mantemRseBloqueadoManual = ParamSist.paramEquals(CodedValues.TPC_MANTEM_STATUS_RSE_BLOQUEADO_MANUALMENTE_CARGA_MARGEM, CodedValues.TPC_SIM, responsavel);
        final BigDecimal percVariacaoMargemRse = ParamSist.getFloatParamSist(CodedValues.TPC_PERC_VARIACAO_MARGEM_SERVIDOR, BigDecimal.ZERO, responsavel);

        // Mensagem de erro, caso exista
        String erro = "";

        boolean temEstablecimento = false;
        boolean temOrgao = false;
        boolean bloquearRseMediaForaLimite = false;

        // 1) Estabelecimento, tabela "tb_estabelecimento"
        Object estCodigo = entrada.get("EST_CODIGO");
        final Object estIdentificador = entrada.get("EST_IDENTIFICADOR");
        final Object estCnpj = entrada.get("EST_CNPJ");
        final Object estNome = entrada.get("EST_NOME");

        try {
            estCodigo = validarEntidadeParaCargaMargem(estCodigo, estIdentificador, estCnpj, estNome, Columns.EST_CODIGO, Columns.EST_IDENTIFICADOR, Columns.EST_CNPJ, Columns.TB_ESTABELECIMENTO, cacheEntidades, CodedValues.CSE_CODIGO_SISTEMA, true, responsavel);
            temEstablecimento = true;
        } catch (final ServidorControllerException ex) {
            erro += delimitador + ex.getMessage();
        }

        // 2) Órgão, tabela "tb_orgao"
        Object orgCodigo = entrada.get("ORG_CODIGO");
        final Object orgIdentificador = entrada.get("ORG_IDENTIFICADOR");
        final Object orgCnpj = entrada.get("ORG_CNPJ");
        final Object orgNome = entrada.get("ORG_NOME");

        try {
            orgCodigo = validarEntidadeParaCargaMargem(orgCodigo, orgIdentificador, orgCnpj, orgNome, Columns.ORG_CODIGO, Columns.ORG_IDENTIFICADOR, Columns.ORG_CNPJ, Columns.TB_ORGAO, cacheEntidades, (String) estCodigo, true, responsavel);
            temOrgao = true;
        } catch (final ServidorControllerException ex) {
            erro += delimitador + ex.getMessage();
        }

        // Se o estabelecimento nãoi foi passado, ou não foi identificado, mas o órgão sim, por exemplo quando o código
        // do órgão é passado, ou passado o identificador e é único, ou só existe um órgão, então obtém o estabelecimento
        if (!temEstablecimento && temOrgao) {
            estCodigo = cacheEntidades.getByValorIdentificador(orgCodigo.toString(), Columns.ORG_CODIGO).get(0).getAttribute(Columns.EST_CODIGO);
            // Zera as mensagens de erro
            erro = "";
        }

        // 3) Sub Órgão, tabela "tb_sub_orgao"
        Object sboCodigo = entrada.get("SBO_CODIGO");
        final Object sboIdentificador = entrada.get("SBO_IDENTIFICADOR");
        final Object sboDescricao = entrada.get("SBO_DESCRICAO");

        try {
            sboCodigo = validarEntidadeParaCargaMargem(sboCodigo, sboIdentificador, null, sboDescricao, Columns.SBO_CODIGO, Columns.SBO_IDENTIFICADOR, null, Columns.TB_SUB_ORGAO, cacheEntidades, (String) orgCodigo, false, responsavel);
        } catch (final ServidorControllerException ex) {
            erro += delimitador + ex.getMessage();
        }

        // 4) Unidade, tabela "tb_unidade"
        Object uniCodigo = entrada.get("UNI_CODIGO");
        final Object uniIdentificador = entrada.get("UNI_IDENTIFICADOR");
        final Object uniDescricao = entrada.get("UNI_DESCRICAO");

        try {
            uniCodigo = validarEntidadeParaCargaMargem(uniCodigo, uniIdentificador, null, uniDescricao, Columns.UNI_CODIGO, Columns.UNI_IDENTIFICADOR, null, Columns.TB_UNIDADE, cacheEntidades, (String) sboCodigo, false, responsavel);
        } catch (final ServidorControllerException ex) {
            erro += delimitador + ex.getMessage();
        }

        // 5) Vínculo, tabela "tb_vinculo_registro_servidor"
        Object vrsCodigo = entrada.get("VRS_CODIGO");
        final Object vrsIdentificador = entrada.get("VRS_IDENTIFICADOR");
        final Object vrsDescricao = entrada.get("VRS_DESCRICAO");

        try {
            vrsCodigo = validarEntidadeParaCargaMargem(vrsCodigo, vrsIdentificador, null, vrsDescricao, Columns.VRS_CODIGO, Columns.VRS_IDENTIFICADOR, null, Columns.TB_VINCULO_REGISTRO_SERVIDOR, cacheEntidades, null, false, responsavel);
        } catch (final ServidorControllerException ex) {
            erro += delimitador + ex.getMessage();
        }

        // 6) Cargo, tabela "tb_cargo_registro_servidor"
        Object crsCodigo = entrada.get("CRS_CODIGO");
        final Object crsIdentificador = entrada.get("CRS_IDENTIFICADOR");
        final Object crsDescricao = entrada.get("CRS_DESCRICAO");

        try {
            crsCodigo = validarEntidadeParaCargaMargem(crsCodigo, crsIdentificador, null, crsDescricao, Columns.CRS_CODIGO, Columns.CRS_IDENTIFICADOR, null, Columns.TB_CARGO_REGISTRO_SERVIDOR, cacheEntidades, null, false, responsavel);
        } catch (final ServidorControllerException ex) {
            erro += delimitador + ex.getMessage();
        }

        // 7) Padrão, tabela "tb_padrao_registro_servidor"
        Object prsCodigo = entrada.get("PRS_CODIGO");
        final Object prsIdentificador = entrada.get("PRS_IDENTIFICADOR");
        final Object prsDescricao = entrada.get("PRS_DESCRICAO");

        try {
            prsCodigo = validarEntidadeParaCargaMargem(prsCodigo, prsIdentificador, null, prsDescricao, Columns.PRS_CODIGO, Columns.PRS_IDENTIFICADOR, null, Columns.TB_PADRAO_REGISTRO_SERVIDOR, cacheEntidades, null, false, responsavel);
        } catch (final ServidorControllerException ex) {
            erro += delimitador + ex.getMessage();
        }

        // 8) Posto, tabela "tb_posto_registro_servidor"
        Object posCodigo = entrada.get("POS_CODIGO");
        final Object posIdentificador = entrada.get("POS_IDENTIFICADOR");
        final Object posDescricao = entrada.get("POS_DESCRICAO");

        try {
            posCodigo = validarEntidadeParaCargaMargem(posCodigo, posIdentificador, null, posDescricao, Columns.POS_CODIGO, Columns.POS_IDENTIFICADOR, null, Columns.TB_POSTO_REGISTRO_SERVIDOR, cacheEntidades, null, false, responsavel);
        } catch (final ServidorControllerException ex) {
            erro += delimitador + ex.getMessage();
        }

        // 9) Tipo de Habitação, tabela "tb_tipo_habitacao"
        Object thaCodigo = entrada.get("THA_CODIGO");
        final Object thaIdentificador = entrada.get("THA_IDENTIFICADOR");
        final Object thaDescricao = entrada.get("THA_DESCRICAO");

        try {
            thaCodigo = validarEntidadeParaCargaMargem(thaCodigo, thaIdentificador, null, thaDescricao, Columns.THA_CODIGO, Columns.THA_IDENTIFICADOR, null, Columns.TB_TIPO_HABITACAO, cacheEntidades, null, false, responsavel);
        } catch (final ServidorControllerException ex) {
            erro += delimitador + ex.getMessage();
        }

        // 10) Nível de escolaridade, tabela "tb_nivel_escolaridade"
        Object nesCodigo = entrada.get("NES_CODIGO");
        final Object nesIdentificador = entrada.get("NES_IDENTIFICADOR");
        final Object nesDescricao = entrada.get("NES_DESCRICAO");

        try {
            nesCodigo = validarEntidadeParaCargaMargem(nesCodigo, nesIdentificador, null, nesDescricao, Columns.NES_CODIGO, Columns.NES_IDENTIFICADOR, null, Columns.TB_NIVEL_ESCOLARIDADE, cacheEntidades, null, false, responsavel);
        } catch (final ServidorControllerException ex) {
            erro += delimitador + ex.getMessage();
        }

        // 11) Tipo de Registro de Servidor, tabela "tb_tipo_registro_servidor"
        Object trsCodigo = entrada.get("TRS_CODIGO");
        final Object trsDescricao = entrada.get("TRS_DESCRICAO");

        try {
            trsCodigo = validarEntidadeParaCargaMargem(trsCodigo, null, null, trsDescricao, Columns.TRS_CODIGO, null, null, Columns.TB_TIPO_REGISTRO_SERVIDOR, cacheEntidades, null, false, responsavel);
        } catch (final ServidorControllerException ex) {
            erro += delimitador + ex.getMessage();
        }

        // 12) Capacidade de Registro Servidor, tabela "tb_capacidade_registro_ser"
        Object capCodigo = entrada.get("CAP_CODIGO");
        final Object capDescricao = entrada.get("CAP_DESCRICAO");

        try {
            capCodigo = validarEntidadeParaCargaMargem(capCodigo, null, null, capDescricao, Columns.CAP_CODIGO, null, null, Columns.TB_CAPACIDADE_REGISTRO_SERVIDOR, cacheEntidades, null, false, responsavel);
        } catch (final ServidorControllerException ex) {
            erro += delimitador + ex.getMessage();
        }

        // 13) Servidor e Registro Servidor, tabelas "tb_servidor" e "tb_registro_servidor":
        // 13.1) Pesquisar registro servidor por Matrícula e Órgão (chave única).
        // 13.2) Caso o registro servidor seja encontrado:
        // 13.2.1) Verificar se está ligado a um servidor com mesmo CPF enviado no arquivo.
        // 13.2.2) Não sendo o mesmo CPF entre o servidor associado ao registro servidor e o valor enviado no arquivo, verificar se este registro servidor possui consignação em qualquer situação, e, tendo, reportar erro na crítica informando que uma matrícula já existente está tentando ser reutilizada.
        // 13.2.3) Caso o CPF seja distinto e não possua consignação, atualizar o SER_CODIGO no registro servidor.
        // 13.2.4) Caso o CPF seja o mesmo, validar se servidor associado ao registro servidor é o mesmo pela chave CPF e Nome, e caso não seja, atualizar o SER_CODIGO no registro servidor.
        // 13.2.5) Atualizar os dados do servidor e registro servidor com os dados passados no arquivo.
        // 13.3) Caso o registro servidor NÃO seja encontrado:
        // 13.3.1) Se o parâmetro de sistema 544 for igual a NÃO, pesquisar apenas servidor por CPF.
        // 13.3.2) Se o parâmetro de sistema 544 for igual a SIM, pesquisar apenas servidor por data de nascimento e nome.
        // 13.3.3) Caso seja encontrado mais que um servidor, filtrar por nome idêntico, caso possível, ou filtrar por aquele que possui registro servidor associado.
        // 13.3.4) Caso continue com mais de um servidor, utiliza o primeiro da lista de resultado.
        // 13.3.5) Atualizar o servidor com os dados do arquivo de entrada
        // 13.3.6) Caso nenhum servidor seja encontrado, incluir o novo servidor no banco de dados.
        // 13.8.7) Inserir o novo registro servidor associado ao servidor criado ou atualizado

        Object serNome = entrada.get("SER_NOME");
        final Object serTitulacao = entrada.get("SER_TITULACAO");
        final Object serPrimeiroNome = entrada.get("SER_PRIMEIRO_NOME");
        final Object serNomeMeio = entrada.get("SER_NOME_MEIO");
        final Object serUltimoNome = entrada.get("SER_ULTIMO_NOME");
        Object serCpf = entrada.get("SER_CPF");
        Object serDataNasc = entrada.get("SER_DATA_NASC");
        final Object rseMatricula = entrada.get("RSE_MATRICULA");

        if (!omiteCpf && TextHelper.isNull(serCpf)) {
            erro += delimitador + ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.erro.cpf.deve.ser.informado", responsavel);
        }
        if (omiteCpf && TextHelper.isNull(serDataNasc)) {
            erro += delimitador + ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.erro.data.nasc.deve.ser.informada", responsavel);
        }
        if (TextHelper.isNull(rseMatricula)) {
            erro += delimitador + ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.erro.matricula.deve.ser.informada", responsavel);
        }
        if (!TextHelper.isNull(serDataNasc)) {
            serDataNasc = DateHelper.toSQLDate(DateHelper.objectToDate(serDataNasc));
        }
        if (TextHelper.isNull(serNome)) {
            serNome = JspHelper.montaSerNome((String) serTitulacao, (String) serPrimeiroNome, (String) serNomeMeio, (String) serUltimoNome);
        }

        // 14.1) A rotina de importação de margem deve:
        // 14.1.1) Extrair o código do tipo de dado do nome da ENTRADA conforme padrão "DAS_VALOR_<CODIGO>".
        // 14.1.2) Pesquisar se o código do tipo de dado é válido na "tb_tipo_dado_adicional", e caso não seja, gerar crítica de importação e continuar na próxima linha.
        final List<TransferObject> tdas = cacheEntidades.getListByCampoIdentificador(Columns.TDA_CODIGO);
        final List<String> tdaCodigos = tdas.stream().map((TransferObject to) -> to.getAttribute(Columns.TDA_CODIGO).toString()).collect(Collectors.toList());
        final Set<String> chavesDadValor = entrada.keySet().stream().filter(value -> value.toUpperCase().contains("DAS_VALOR_")).collect(Collectors.toSet());

        for (final String chave : chavesDadValor) {
            final String id = chave.substring("DAS_VALOR_".length(), chave.length());

            if (!tdaCodigos.contains(id)) {
                erro += delimitador + ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.erro.entidade.nao.determinada." + Columns.TB_TIPO_DADO_ADICIONAL, responsavel, id);
            }
        }

        // Se não tem erros, prossegue a rotina
        if (TextHelper.isNull(erro)) {
            Servidor ser = null;
            RegistroServidor rse = null;
            try {
                // Pesquisa o registro servidor por matrícula, órgão, visto que é chave única
                rse = RegistroServidorHome.findByMatriculaOrgaoFetchAutDesconto(rseMatricula.toString(), orgCodigo.toString());

                // Encontrou registro servidor, então obtém o servidor para verificar se é o mesmo CPF
                ser = ServidorHome.findByPrimaryKey(rse.getServidor().getSerCodigo());

                // Se não omite CPF e o CPF do servidor associado ao registro servidor é diferente,
                // verifica se a matrícula pode ser reutilizada e associada a outro CPF
                if (!omiteCpf) {
                    if (!ser.getSerCpf().equals(serCpf.toString())) {
                        // Não sendo o mesmo CPF entre o servidor associado ao registro servidor e o valor de CPF enviado no arquivo,
                        // verifica se este registro servidor possui consignação em qualquer situação, e, tendo, reportar erro na crítica
                        // informando que uma matrícula já existente está tentando ser reutilizada.
                        if (!rse.getAutDescontoSet().isEmpty()) {
                            erro += delimitador + ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.erro.matricula.existente.reutilizada", responsavel, rseMatricula.toString());
                        } else {
                            // Pode prosseguir, e associar o registro servidor atual ao servidor passado no arquivo
                            ser = null;
                        }
                    } else {
                        // Se é o mesmo CPF, valida a unicidade da chave Nome+CPF evitando que dê chave
                        // duplicada ao atualizar o nome do servidor associado ao registro servidor para
                        // outro que já existe idêntico na base
                        try {
                            final Servidor candidato = ServidorHome.findByCPFNome(serCpf.toString(), serNome.toString());
                            if (!candidato.getSerCodigo().equals(ser.getSerCodigo())) {
                                // Se o registro servidor está associado a outro servidor que não tem o mesmo CPF + Nome
                                // passado no arquivo, então troca a referência ao servidor para que o registro servidor
                                // seja atualizado para o servidor correto
                                ser = candidato;
                            }
                        } catch (final Exception ex) {
                            // Não existe servidor com o CPF + Nome, então tudo certo, o nome será atualizado
                            // DESENV-14421 : Não é necessário imprimir esse log
                            // LOG.error(ex.getMessage(), ex);
                        }
                    }
                } else // CPF não é utilizado. Verifica se o Nome ou Data de Nascimento do servidor associado ao
                // registro servidor é igual ao enviado no arquivo. Caso não seja, procede análise se a
                // matrícula pode ser reutilizada
                if (!ser.getSerNome().equals(serNome.toString()) && (ser.getSerDataNasc() != null) && (serDataNasc != null) && !ser.getSerDataNasc().equals((serDataNasc))) {
                    // Tanto nome quanto data de nascimento são diferentes. Verifica se pode reutilizar a matrícula
                    if (!rse.getAutDescontoSet().isEmpty()) {
                        erro += delimitador + ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.erro.matricula.existente.reutilizada", responsavel, rseMatricula.toString());
                    } else {
                        // Pode prosseguir, e associar o registro servidor atual ao servidor passado no arquivo
                        ser = null;
                    }
                }

                /*
                 * Caso o parâmetro seja maior que zero, alterar a rotina de carga de margem para validar a margem folha enviada com a média da margem folha já calculada.
                 * A comparação deve ser feita antes da inclusão do histórico de margem folha referente ao período, de modo que a média não tenha sido afetada pelo valor informado.
                 * Caso a variação de alguma das margens do servidor esteja acima do percentual configurado no sistema, bloquear o registro servidor, evitando que novas operações consumam uma margem potencialmente incorreta.
                 * Somente uma variação positiva deve bloquear o servidor, ou seja se a margem variar acima do limite para baixo, o servidor não deve ser bloqueado.
                 * Se a média não estiver preenchida, ou estiver zerada, o servidor não deve ser bloqueado, já que não é possível calcular variação sobre Zero.
                 */
                if (percVariacaoMargemRse.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal media = BigDecimal.ZERO;

                    List<MargemRegistroServidor> margensRse = new ArrayList<>();
                    try {
                        margensRse = MargemRegistroServidorHome.findByRseCodigo(rse.getRseCodigo());
                    } catch (final FindException ex) {
                        LOG.info("Não há margem extra cadastrada para registro servidor com rseCodigo: " + rse.getRseCodigo());
                    }

                    for (final TransferObject margemTO : cacheEntidades.getListByCampoIdentificador(Columns.MAR_CODIGO)) {
                        final Short marCodigo = (Short) margemTO.getAttribute(Columns.MAR_CODIGO);
                        BigDecimal margem = BigDecimal.ZERO;

                        if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM) && !cacheEntidades.getListByCampoIdentificador(CacheDependenciasServidor.PREFIX_SVC_ATIVO + CodedValues.INCIDE_MARGEM_SIM).isEmpty()) {
                            margem = NumberHelper.objectToBigDecimal(entrada.get("RSE_MARGEM"));
                            media = rse.getRseMediaMargem();
                        } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_2) && !cacheEntidades.getListByCampoIdentificador(CacheDependenciasServidor.PREFIX_SVC_ATIVO + CodedValues.INCIDE_MARGEM_SIM_2).isEmpty()) {
                            margem = NumberHelper.objectToBigDecimal(entrada.get("RSE_MARGEM_2"));
                            media = rse.getRseMediaMargem2();
                        } else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_3) && !cacheEntidades.getListByCampoIdentificador(CacheDependenciasServidor.PREFIX_SVC_ATIVO + CodedValues.INCIDE_MARGEM_SIM_3).isEmpty()) {
                            margem = NumberHelper.objectToBigDecimal(entrada.get("RSE_MARGEM_3"));
                            media = rse.getRseMediaMargem3();
                        } else if (!marCodigo.equals(CodedValues.INCIDE_MARGEM_NAO) && !cacheEntidades.getListByCampoIdentificador(CacheDependenciasServidor.PREFIX_SVC_ATIVO + marCodigo).isEmpty()) {
                            margem = NumberHelper.objectToBigDecimal(entrada.get("MRS_MARGEM_" + marCodigo));
                            final MargemRegistroServidor mrs = margensRse.stream()
                                                                         .filter(customer -> marCodigo.equals(customer.getMarCodigo()))
                                                                         .findFirst().orElse(null);
                            media = mrs != null ? mrs.getMrsMediaMargem() : BigDecimal.ZERO;
                        }

                        // Margem for maior do que a média acrescida do percentual permitido, bloqueia o servidor
                        if (!TextHelper.isNull(media) && (media.compareTo(BigDecimal.ZERO) > 0)) {
                            final BigDecimal variacao = media.multiply(percVariacaoMargemRse).divide(new BigDecimal(100)).add(media);
                            if (!TextHelper.isNull(margem) && (margem.compareTo(variacao) > 0)) {
                                bloquearRseMediaForaLimite = true;
                                break;
                            }
                        }
                    }
                }

            } catch (final FindException ex) {
                // Não encontrou, então deverá ser criado novo registro servidor
                // DESENV-14421 : Não é necessário imprimir esse log
                // LOG.error(ex.getMessage(), ex);
            }

            // Se não tem erros, prossegue a rotina
            if (TextHelper.isNull(erro)) {
                if (ser == null) {
                    try {
                        // Pesquisar o servidor isoladamente, visto que não foi encontrado
                        List<Servidor> servidores = null;
                        if (!omiteCpf) {
                            servidores = ServidorHome.findByCPFFetchRegistroServidor(serCpf.toString());
                        } else {
                            servidores = ServidorHome.findByDataNascNome((java.sql.Date) serDataNasc, serNome.toString());
                        }
                        if ((servidores != null) && !servidores.isEmpty()) {
                            if (servidores.size() == 1) {
                                ser = servidores.get(0);
                            } else if (!omiteCpf) {
                                // Se pesquisou apenas por CPF, filtra agora se algum o nome é idêntico
                                for (final Servidor serCandidato : servidores) {
                                    if (TextHelper.removeAccent(serCandidato.getSerNome()).equalsIgnoreCase(TextHelper.removeAccent(serNome.toString()))) {
                                        ser = serCandidato;
                                        break;
                                    }
                                }
                            }
                            // Se não foi possível filtrar por nome idêntico, ou a pesquisa inicial
                            // já foi feita por nome e data nascimento, e foram encontrados múltiplos
                            // então pega o primeiro da lista
                            if (ser == null) {
                                // Filtrar por aquele que tem registro servidor associado
                                for (final Servidor serCandidato : servidores) {
                                    if (!serCandidato.getRegistroServidorSet().isEmpty()) {
                                        ser = serCandidato;
                                        break;
                                    }
                                }
                                // Se ainda assim permanecer nulo, pega o primeiro da lista
                                if (ser == null) {
                                    ser = servidores.get(0);
                                }
                            }
                        }
                    } catch (final FindException ex) {
                        LOG.error(ex.getMessage(), ex);
                        throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }
                }

                // Valores textuais, não precisam de tratamento
                final Object serNomeMae = entrada.get("SER_NOME_MAE");
                final Object serNomePai = entrada.get("SER_NOME_PAI");
                final Object serNomeConjuge = entrada.get("SER_NOME_CONJUGE");
                final Object serSexo = entrada.get("SER_SEXO");
                final Object serEstCivil = entrada.get("SER_EST_CIVIL");
                final Object serNacionalidade = entrada.get("SER_NACIONALIDADE");
                final Object serCidNasc = entrada.get("SER_CID_NASC");
                final Object serUfNasc = entrada.get("SER_UF_NASC");
                final Object serNroIdt = entrada.get("SER_NRO_IDT");
                final Object serEmissorIdt = entrada.get("SER_EMISSOR_IDT");
                final Object serUfIdt = entrada.get("SER_UF_IDT");
                final Object serCartProf = entrada.get("SER_CART_PROF");
                final Object serPis = entrada.get("SER_PIS");
                final Object serEnd = entrada.get("SER_END");
                final Object serBairro = entrada.get("SER_BAIRRO");
                final Object serCidade = entrada.get("SER_CIDADE");
                final Object serCompl = entrada.get("SER_COMPL");
                final Object serNro = entrada.get("SER_NRO");
                final Object serCep = entrada.get("SER_CEP");
                final Object serUf = entrada.get("SER_UF");
                final Object serTel = entrada.get("SER_TEL");
                final Object serCelular = entrada.get("SER_CELULAR");
                final Object serEmail = entrada.get("SER_EMAIL");
                final Object sseCodigo = entrada.get("SSE_CODIGO");

                // Campos de data, faz conversão caso presente
                Object serDataIdt = DateHelper.objectToDate(entrada.get("SER_DATA_IDT"));
                if (serDataIdt != null) {
                    serDataIdt = DateHelper.toSQLDate((java.util.Date) serDataIdt);
                }

                // Campos numéricos, faz comversão caso presente
                Object serQtdFilhos = entrada.get("SER_QTD_FILHOS");
                if (!TextHelper.isNull(serQtdFilhos)) {
                    serQtdFilhos = (short) TextHelper.parseIntErrorSafe(serQtdFilhos, 0);
                }

                // Se o servidor permanece nulo, então será criado
                if (ser == null) {
                    if (omiteCpf) {
                        serCpf = GeradorCpfServidor.getInstance().getNext();
                    }

                    try {
                        ser = ServidorHome.create((String) serCpf, (java.sql.Date) serDataNasc, (String) serNomeMae, (String) serNomePai, (String) serNome, (String) serPrimeiroNome, (String) serNomeMeio, (String) serUltimoNome, (String) serTitulacao, (String) serSexo, (String) serEstCivil, (Short) serQtdFilhos, (String) serNacionalidade, (String) serNroIdt, (String) serCartProf, (String) serPis, (String) serEnd, (String) serBairro, (String) serCidade, (String) serCompl, (String) serNro, (String) serCep, (String) serUf, (String) serTel, (String) serEmail, (String) serEmissorIdt,
                                                  (String) serUfIdt, (java.sql.Date) serDataIdt, (String) serCidNasc, (String) serUfNasc, (String) serNomeConjuge, null, null, (String) serCelular, null, (String) nesCodigo, (String) thaCodigo, (String) sseCodigo);
                    } catch (final com.zetra.econsig.exception.CreateException ex) {
                        LOG.error(ex.getMessage(), ex);
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }
                } else {
                    try {
                        // Caso contrário atualiza suas informações

                        // DESENV-12892
                        // Bloquear a edição de e-mail caso este tenha sido enviado pela folha
                        if (!TextHelper.isNull(serEmail) && bloqueiaEdicaoEmail) {
                            ser.setSerPermiteAlterarEmail(CodedValues.TPC_NAO);
                        } else {
                            ser.setSerPermiteAlterarEmail(CodedValues.TPC_SIM);
                        }
                        // Forçar nova validação caso o e-mail tenha sido alterado, ou esteja sendo cadastrado pela primeira vez
                        if (!TextHelper.isNull(serEmail) && !serEmail.equals(ser.getSerEmail())) {
                            ser.setSerDataValidacaoEmail(null);
                        }

                        if (entrada.containsKey("SER_DATA_NASC")) {
                            ser.setSerDataNasc((java.sql.Date) serDataNasc);
                        }
                        if (entrada.containsKey("SER_NOME") || entrada.containsKey("SER_PRIMEIRO_NOME")) {
                            ser.setSerNome((String) serNome);
                        }
                        if (entrada.containsKey("SER_TITULACAO")) {
                            ser.setSerTitulacao((String) serTitulacao);
                        }
                        if (entrada.containsKey("SER_PRIMEIRO_NOME")) {
                            ser.setSerPrimeiroNome((String) serPrimeiroNome);
                        }
                        if (entrada.containsKey("SER_NOME_MEIO")) {
                            ser.setSerNomeMeio((String) serNomeMeio);
                        }
                        if (entrada.containsKey("SER_ULTIMO_NOME")) {
                            ser.setSerUltimoNome((String) serUltimoNome);
                        }
                        if (entrada.containsKey("SER_NOME_MAE")) {
                            ser.setSerNomeMae((String) serNomeMae);
                        }
                        if (entrada.containsKey("SER_NOME_PAI")) {
                            ser.setSerNomePai((String) serNomePai);
                        }
                        if (entrada.containsKey("SER_NOME_CONJUGE")) {
                            ser.setSerNomeConjuge((String) serNomeConjuge);
                        }
                        if (entrada.containsKey("SER_SEXO")) {
                            ser.setSerSexo((String) serSexo);
                        }
                        if (entrada.containsKey("SER_EST_CIVIL")) {
                            ser.setSerEstCivil((String) serEstCivil);
                        }
                        if (entrada.containsKey("SER_NACIONALIDADE")) {
                            ser.setSerNacionalidade((String) serNacionalidade);
                        }
                        if (entrada.containsKey("SER_NRO_IDT")) {
                            ser.setSerNroIdt((String) serNroIdt);
                        }
                        if (entrada.containsKey("SER_EMISSOR_IDT")) {
                            ser.setSerEmissorIdt((String) serEmissorIdt);
                        }
                        if (entrada.containsKey("SER_UF_IDT")) {
                            ser.setSerUfIdt((String) serUfIdt);
                        }
                        if (entrada.containsKey("SER_DATA_IDT")) {
                            ser.setSerDataIdt((java.sql.Date) serDataIdt);
                        }
                        if (entrada.containsKey("SER_CART_PROF")) {
                            ser.setSerCartProf((String) serCartProf);
                        }
                        if (entrada.containsKey("SER_PIS")) {
                            ser.setSerPis((String) serPis);
                        }
                        if (entrada.containsKey("SER_END")) {
                            ser.setSerEnd((String) serEnd);
                        }
                        if (entrada.containsKey("SER_BAIRRO")) {
                            ser.setSerBairro((String) serBairro);
                        }
                        if (entrada.containsKey("SER_CIDADE")) {
                            ser.setSerCidade((String) serCidade);
                        }
                        if (entrada.containsKey("SER_COMPL")) {
                            ser.setSerCompl((String) serCompl);
                        }
                        if (entrada.containsKey("SER_NRO")) {
                            ser.setSerNro((String) serNro);
                        }
                        if (entrada.containsKey("SER_CEP")) {
                            ser.setSerCep((String) serCep);
                        }
                        if (entrada.containsKey("SER_UF")) {
                            ser.setSerUf((String) serUf);
                        }
                        if (entrada.containsKey("SER_TEL")) {
                            ser.setSerTel((String) serTel);
                        }
                        if (entrada.containsKey("SER_CELULAR")) {
                            ser.setSerCelular((String) serCelular);
                        }
                        if (entrada.containsKey("SER_EMAIL")) {
                            ser.setSerEmail((String) serEmail);
                        }
                        if (entrada.containsKey("SER_CID_NASC")) {
                            ser.setSerCidNasc((String) serCidNasc);
                        }
                        if (entrada.containsKey("SER_UF_NASC")) {
                            ser.setSerUfNasc((String) serUfNasc);
                        }
                        if (entrada.containsKey("SER_QTD_FILHOS")) {
                            ser.setSerQtdFilhos((Short) serQtdFilhos);
                        }
                        if (entrada.containsKey("NES_CODIGO") || entrada.containsKey("NES_IDENTIFICADOR")) {
                            ser.setNivelEscolaridade(!TextHelper.isNull(nesCodigo) ? session.getReference(NivelEscolaridade.class, nesCodigo.toString()) : null);
                        }
                        if (entrada.containsKey("THA_CODIGO") || entrada.containsKey("THA_IDENTIFICADOR")) {
                            ser.setTipoHabitacao(!TextHelper.isNull(thaCodigo) ? session.getReference(TipoHabitacao.class, thaCodigo.toString()) : null);
                        }
                        if (entrada.containsKey("SSE_CODIGO")) {
                            ser.setStatusServidor(!TextHelper.isNull(sseCodigo) ? session.getReference(StatusServidor.class, sseCodigo.toString()) : null);
                        }

                        AbstractEntityHome.update(ser);
                    } catch (final UpdateException ex) {
                        LOG.error(ex.getMessage(), ex);
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }
                }

                final Object rseMatriculaInst = entrada.get("RSE_MATRICULA_INST");
                final Object rseTipo = entrada.get("RSE_TIPO");
                final Object rseBancoSal = entrada.get("RSE_BANCO_SAL");
                final Object rseAgenciaSal = entrada.get("RSE_AGENCIA_SAL");
                final Object rseAgenciaDvSal = entrada.get("RSE_AGENCIA_DV_SAL");
                final Object rseContaSal = entrada.get("RSE_CONTA_SAL");
                final Object rseContaDvSal = entrada.get("RSE_CONTA_DV_SAL");
                final Object rseBancoSal2 = entrada.get("RSE_BANCO_SAL_2");
                final Object rseAgenciaSal2 = entrada.get("RSE_AGENCIA_SAL_2");
                final Object rseAgenciaDvSal2 = entrada.get("RSE_AGENCIA_DV_SAL_2");
                final Object rseContaSal2 = entrada.get("RSE_CONTA_SAL_2");
                final Object rseContaDvSal2 = entrada.get("RSE_CONTA_DV_SAL_2");
                final Object rseAssociado = entrada.get("RSE_ASSOCIADO");
                final Object rseClt = entrada.get("RSE_CLT");
                final Object rseObs = entrada.get("RSE_OBS");
                final Object rseEstabilizado = entrada.get("RSE_ESTABILIZADO");
                final Object rseMunicipioLotacao = entrada.get("RSE_MUNICIPIO_LOTACAO");
                final Object rsePraca = entrada.get("RSE_PRACA");
                final Object rsePedidoDemissao = entrada.get("RSE_PEDIDO_DEMISSAO");
                Object srsCodigo = entrada.get("SRS_CODIGO");
                boolean reativadoPelaCargaMargem = false;
                boolean excluidoPelaCargaMargem = false;
                boolean desbloqueadoPelaCargaMargem = false;

                Object bcoCodigo = entrada.get("BCO_CODIGO");
                if (!TextHelper.isNull(bcoCodigo)) {
                    bcoCodigo = Short.valueOf(bcoCodigo.toString());
                }

                Object rsePrazo = entrada.get("RSE_PRAZO");
                if (!TextHelper.isNull(rsePrazo)) {
                    rsePrazo = TextHelper.parseIntErrorSafe(rsePrazo, 0);
                }

                final Object rseDataAdmissao = DateHelper.objectToDate(entrada.get("RSE_DATA_ADMISSAO"));
                final Object rseDataCtc = DateHelper.objectToDate(entrada.get("RSE_DATA_CTC"));
                final Object rseDataFimEngajamento = DateHelper.objectToDate(entrada.get("RSE_DATA_FIM_ENGAJAMENTO"));
                final Object rseDataLimitePermanencia = DateHelper.objectToDate(entrada.get("RSE_DATA_LIMITE_PERMANENCIA"));
                final Object rseDataSaida = DateHelper.objectToDate(entrada.get("RSE_DATA_SAIDA"));
                final Object rseDataUltSalario = DateHelper.objectToDate(entrada.get("RSE_DATA_ULT_SALARIO"));
                final Object rseDataRetorno = DateHelper.objectToDate(entrada.get("RSE_DATA_RETORNO"));

                Object rseMargem = NumberHelper.objectToBigDecimal(entrada.get("RSE_MARGEM"));
                if (rseMargem == null) {
                    rseMargem = BigDecimal.ZERO;
                }

                Object rseMargem2 = NumberHelper.objectToBigDecimal(entrada.get("RSE_MARGEM_2"));
                Object rseMargem3 = NumberHelper.objectToBigDecimal(entrada.get("RSE_MARGEM_3"));
                final Object rseBaseCalculo = NumberHelper.objectToBigDecimal(entrada.get("RSE_BASE_CALCULO"));
                final Object rseSalario = NumberHelper.objectToBigDecimal(entrada.get("RSE_SALARIO"));
                final Object rseProventos = NumberHelper.objectToBigDecimal(entrada.get("RSE_PROVENTOS"));
                final Object rseDescontosComp = NumberHelper.objectToBigDecimal(entrada.get("RSE_DESCONTOS_COMP"));
                final Object rseDescontosFacu = NumberHelper.objectToBigDecimal(entrada.get("RSE_DESCONTOS_FACU"));
                final Object rseOutrosDescontos = NumberHelper.objectToBigDecimal(entrada.get("RSE_OUTROS_DESCONTOS"));
                final Object rseMotivoFaltaMargem = entrada.get("RSE_MOTIVO_FALTA_MARGEM");

                // O campo RSE_DATA_CARGA deve ser atualizado com a data corrente da atualização dos dados do registro servidor.
                final java.util.Date rseDataCarga = DateHelper.getSystemDatetime();

                // Executa callback da rotina de importação customizada para cálculo do valor das margens
                if (importadorMargem != null) {
                    try {
                        final BigDecimal margemFolha1 = importadorMargem.calcularValorMargemFolha(CodedValues.INCIDE_MARGEM_SIM, rse, entrada, responsavel);
                        if (margemFolha1 != null) {
                            rseMargem = margemFolha1;
                        }

                        final BigDecimal margemFolha2 = importadorMargem.calcularValorMargemFolha(CodedValues.INCIDE_MARGEM_SIM_2, rse, entrada, responsavel);
                        if (margemFolha2 != null) {
                            rseMargem2 = margemFolha2;
                        }

                        final BigDecimal margemFolha3 = importadorMargem.calcularValorMargemFolha(CodedValues.INCIDE_MARGEM_SIM_3, rse, entrada, responsavel);
                        if (margemFolha3 != null) {
                            rseMargem3 = margemFolha3;
                        }
                    } catch (final ImportaMargemException ex) {
                        LOG.error(ex.getMessage(), ex);
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }
                }

                // Se o registro servidor é nulo, então será criado
                if (rse == null) {
                    try {
                        if (TextHelper.isNull(srsCodigo)) {
                            srsCodigo = CodedValues.SRS_ATIVO;
                        }

                        rse = RegistroServidorHome.create(ser.getSerCodigo(), orgCodigo.toString(), srsCodigo.toString(), rseMatricula.toString(), (BigDecimal) rseMargem, BigDecimal.ZERO, BigDecimal.ZERO, (BigDecimal) rseMargem2, BigDecimal.ZERO, BigDecimal.ZERO, (BigDecimal) rseMargem3, BigDecimal.ZERO, BigDecimal.ZERO, (String) rseTipo, (Integer) rsePrazo, (java.util.Date) rseDataAdmissao, (String) rseClt, null, (Short) bcoCodigo, (String) rseObs, (String) rseAssociado, (String) rseEstabilizado, rseDataCarga, (java.util.Date) rseDataFimEngajamento,
                                                          (java.util.Date) rseDataLimitePermanencia, (String) rseBancoSal, (String) rseAgenciaSal, (String) rseAgenciaDvSal, (String) rseContaSal, (String) rseContaDvSal, (String) rseBancoSal2, (String) rseAgenciaSal2, (String) rseAgenciaDvSal2, (String) rseContaSal2, (String) rseContaDvSal2, (BigDecimal) rseSalario, (BigDecimal) rseProventos, (BigDecimal) rseDescontosComp, (BigDecimal) rseDescontosFacu, (BigDecimal) rseOutrosDescontos, (String) crsCodigo, (String) prsCodigo, (String) sboCodigo, (String) uniCodigo, (String) vrsCodigo,
                                                          (String) posCodigo,
                                                          (String) trsCodigo, (String) capCodigo, (String) rsePraca, null, (String) rseMunicipioLotacao, (String) rseMatriculaInst, (java.util.Date) rseDataCtc, (BigDecimal) rseBaseCalculo, (String) rsePedidoDemissao, (java.util.Date) rseDataSaida, (java.util.Date) rseDataUltSalario, (java.util.Date) rseDataRetorno, (String) rseMotivoFaltaMargem);
                    } catch (final com.zetra.econsig.exception.CreateException ex) {
                        LOG.error(ex.getMessage(), ex);
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }
                } else {
                    final String srsCodigoAtual = rse.getStatusRegistroServidor().getSrsCodigo();

                    if (bloquearRseMediaForaLimite && CodedValues.SRS_ATIVO.equals(srsCodigoAtual)) {
                        // Bloquear servidor por variação da margem
                        srsCodigo = CodedValues.SRS_BLOQUEADO_AUTOMATICAMENTE_VARIACAO_MARGEM;
                    }

                    try {
                        // Caso contrário atualiza suas informações, incluindo a associação ao servidor
                        // rse.setServidor(ser);
                        rse.setServidor(session.getReference(Servidor.class, ser.getSerCodigo()));

                        if (entrada.containsKey("RSE_MARGEM")) {
                            rse.setRseMargem((BigDecimal) rseMargem);
                        }
                        if (entrada.containsKey("RSE_MARGEM_2")) {
                            rse.setRseMargem2((BigDecimal) rseMargem2);
                        }
                        if (entrada.containsKey("RSE_MARGEM_3")) {
                            rse.setRseMargem3((BigDecimal) rseMargem3);
                        }
                        if (entrada.containsKey("RSE_TIPO")) {
                            rse.setRseTipo((String) rseTipo);
                        }
                        if (entrada.containsKey("RSE_PRAZO")) {
                            rse.setRsePrazo((Integer) rsePrazo);
                        }
                        if (entrada.containsKey("RSE_DATA_ADMISSAO")) {
                            rse.setRseDataAdmissao((java.util.Date) rseDataAdmissao);
                        }
                        if (entrada.containsKey("RSE_CLT")) {
                            rse.setRseClt((String) rseClt);
                        }
                        if (entrada.containsKey("RSE_OBS")) {
                            rse.setRseObs((String) rseObs);
                        }
                        if (entrada.containsKey("RSE_ASSOCIADO")) {
                            rse.setRseAssociado((String) rseAssociado);
                        }
                        if (entrada.containsKey("RSE_ESTABILIZADO")) {
                            rse.setRseEstabilizado((String) rseEstabilizado);
                        }
                        if (entrada.containsKey("RSE_DATA_FIM_ENGAJAMENTO")) {
                            rse.setRseDataFimEngajamento((java.util.Date) rseDataFimEngajamento);
                        }
                        if (entrada.containsKey("RSE_DATA_LIMITE_PERMANENCIA")) {
                            rse.setRseDataLimitePermanencia((java.util.Date) rseDataLimitePermanencia);
                        }
                        if (entrada.containsKey("RSE_BANCO_SAL")) {
                            rse.setRseBancoSal((String) rseBancoSal);
                        }
                        if (entrada.containsKey("RSE_AGENCIA_SAL")) {
                            rse.setRseAgenciaSal((String) rseAgenciaSal);
                        }
                        if (entrada.containsKey("RSE_AGENCIA_DV_SAL")) {
                            rse.setRseAgenciaDvSal((String) rseAgenciaDvSal);
                        }
                        if (entrada.containsKey("RSE_CONTA_SAL")) {
                            rse.setRseContaSal((String) rseContaSal);
                        }
                        if (entrada.containsKey("RSE_CONTA_DV_SAL")) {
                            rse.setRseContaDvSal((String) rseContaDvSal);
                        }
                        if (entrada.containsKey("RSE_BANCO_SAL_2")) {
                            rse.setRseBancoSal2((String) rseBancoSal2);
                        }
                        if (entrada.containsKey("RSE_AGENCIA_SAL_2")) {
                            rse.setRseAgenciaSal2((String) rseAgenciaSal2);
                        }
                        if (entrada.containsKey("RSE_AGENCIA_DV_SAL_2")) {
                            rse.setRseAgenciaDvSal2((String) rseAgenciaDvSal2);
                        }
                        if (entrada.containsKey("RSE_CONTA_SAL_2")) {
                            rse.setRseContaSal2((String) rseContaSal2);
                        }
                        if (entrada.containsKey("RSE_CONTA_DV_SAL_2")) {
                            rse.setRseContaDvSal2((String) rseContaDvSal2);
                        }
                        if (entrada.containsKey("RSE_SALARIO")) {
                            rse.setRseSalario((BigDecimal) rseSalario);
                        }
                        if (entrada.containsKey("RSE_PROVENTOS")) {
                            rse.setRseProventos((BigDecimal) rseProventos);
                        }
                        if (entrada.containsKey("RSE_DESCONTOS_COMP")) {
                            rse.setRseDescontosComp((BigDecimal) rseDescontosComp);
                        }
                        if (entrada.containsKey("RSE_DESCONTOS_FACU")) {
                            rse.setRseDescontosFacu((BigDecimal) rseDescontosFacu);
                        }
                        if (entrada.containsKey("RSE_OUTROS_DESCONTOS")) {
                            rse.setRseOutrosDescontos((BigDecimal) rseOutrosDescontos);
                        }
                        if (entrada.containsKey("RSE_PRACA")) {
                            rse.setRsePraca((String) rsePraca);
                        }
                        if (entrada.containsKey("RSE_MUNICIPIO_LOTACAO")) {
                            rse.setRseMunicipioLotacao((String) rseMunicipioLotacao);
                        }
                        if (entrada.containsKey("RSE_MATRICULA_INST")) {
                            rse.setRseMatriculaInst((String) rseMatriculaInst);
                        }
                        if (entrada.containsKey("RSE_DATA_CTC")) {
                            rse.setRseDataCtc((java.util.Date) rseDataCtc);
                        }
                        if (entrada.containsKey("RSE_BASE_CALCULO")) {
                            rse.setRseBaseCalculo((BigDecimal) rseBaseCalculo);
                        }
                        if (entrada.containsKey("RSE_PEDIDO_DEMISSAO")) {
                            rse.setRsePedidoDemissao((String) rsePedidoDemissao);
                        }
                        if (entrada.containsKey("RSE_DATA_SAIDA")) {
                            rse.setRseDataSaida((java.util.Date) rseDataSaida);
                        }
                        if (entrada.containsKey("RSE_DATA_ULT_SALARIO")) {
                            rse.setRseDataUltSalario((java.util.Date) rseDataUltSalario);
                        }
                        if (entrada.containsKey("RSE_DATA_RETORNO")) {
                            rse.setRseDataRetorno((java.util.Date) rseDataRetorno);
                        }
                        if (entrada.containsKey("RSE_MOTIVO_FALTA_MARGEM")) {
                            rse.setRseMotivoFaltaMargem(!TextHelper.isNull(rseMotivoFaltaMargem) ? (String) rseMotivoFaltaMargem : null);
                        }

                        // O campo SRS_CODIGO, caso não enviado no arquivo de entrada, será mantido com o valor no banco,
                        // com exceção se estiver com o valor Excluído (3), que será revertido para Ativo (1), seguindo a lógica: "case when srs_codigo = '3' then '1' else srs_codigo end".
                        if (TextHelper.isNull(srsCodigo) && CodedValues.SRS_EXCLUIDO.equals(srsCodigoAtual)) {
                            srsCodigo = CodedValues.SRS_ATIVO;
                        }
                        if (!TextHelper.isNull(srsCodigo)) {
                            boolean podeAlterarStatus = true;

                            reativadoPelaCargaMargem = (CodedValues.SRS_ATIVO.equals(srsCodigo) && CodedValues.SRS_EXCLUIDO.equals(srsCodigoAtual));
                            excluidoPelaCargaMargem = (CodedValues.SRS_EXCLUIDO.equals(srsCodigo) && !CodedValues.SRS_EXCLUIDO.equals(srsCodigoAtual));
                            desbloqueadoPelaCargaMargem = (CodedValues.SRS_ATIVO.equals(srsCodigo) && CodedValues.SRS_BLOQUEADO.equals(srsCodigoAtual));

                            // Não altera o status do RSE caso esteja bloqueado por segurança ou por variação de margem e não esteja sendo excluído
                            if ((CodedValues.SRS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA.equals(srsCodigoAtual) || CodedValues.SRS_BLOQUEADO_AUTOMATICAMENTE_VARIACAO_MARGEM.equals(srsCodigoAtual)) && !CodedValues.SRS_INATIVOS.contains(srsCodigo)) {
                                podeAlterarStatus = false;
                            }

                            // Se está sendo desbloqueado, e o parâmetro de sistema informa que deve manter, verifica se o servidor
                            // foi bloqueado manualmente ou não, de acordo com as ocorrências de bloqueio/desbloqueio de RSE
                            if (desbloqueadoPelaCargaMargem && mantemRseBloqueadoManual) {
                                // Busca a ocorrência mais nova dos tipos TOC_RSE_BLOQUEIO_STATUS_MANUAL e TOC_RSE_DESBLOQUEIO_STATUS_MANUAL.
                                // Caso esta seja de bloqueio, significa que ele foi bloqueado manualmente e não foi desbloqueado.
                                // Neste caso, não deixa que a carga de margem faça o desbloqueio
                                try {
                                    final OcorrenciaRegistroSer ors = OcorrenciaRegistroServidorHome.findLastByRseTocCodigos(rse.getRseCodigo(), Arrays.asList(CodedValues.TOC_RSE_BLOQUEIO_STATUS_MANUAL, CodedValues.TOC_RSE_DESBLOQUEIO_STATUS_MANUAL));
                                    if ((ors != null) && (ors.getTipoOcorrencia() != null) && (ors.getTipoOcorrencia().getTocCodigo() != null) && CodedValues.TOC_RSE_BLOQUEIO_STATUS_MANUAL.equals(ors.getTipoOcorrencia().getTocCodigo())) {
                                        podeAlterarStatus = false;
                                    }
                                } catch (final FindException ex) {
                                    LOG.error(ex.getMessage(), ex);
                                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                                    throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
                                }
                            }

                            if (podeAlterarStatus) {
                                rse.setStatusRegistroServidor(session.getReference(StatusRegistroServidor.class, srsCodigo.toString()));
                            }
                        }

                        if (entrada.containsKey("BCO_CODIGO")) {
                            rse.setBanco(!TextHelper.isNull(bcoCodigo) ? session.getReference(Banco.class, (Short) bcoCodigo) : null);
                        }
                        if (entrada.containsKey("CRS_CODIGO") || entrada.containsKey("CRS_IDENTIFICADOR")) {
                            rse.setCargoRegistroServidor(!TextHelper.isNull(crsCodigo) ? session.getReference(CargoRegistroServidor.class, crsCodigo.toString()) : null);
                        }
                        if (entrada.containsKey("PRS_CODIGO") || entrada.containsKey("PRS_IDENTIFICADOR")) {
                            rse.setPadraoRegistroServidor(!TextHelper.isNull(prsCodigo) ? session.getReference(PadraoRegistroServidor.class, prsCodigo.toString()) : null);
                        }
                        if (entrada.containsKey("SBO_CODIGO") || entrada.containsKey("SBO_IDENTIFICADOR")) {
                            rse.setSubOrgao(!TextHelper.isNull(sboCodigo) ? session.getReference(SubOrgao.class, sboCodigo.toString()) : null);
                        }
                        if (entrada.containsKey("UNI_CODIGO") || entrada.containsKey("UNI_IDENTIFICADOR")) {
                            rse.setUnidade(!TextHelper.isNull(uniCodigo) ? session.getReference(Unidade.class, uniCodigo.toString()) : null);
                        }
                        if (entrada.containsKey("VRS_CODIGO") || entrada.containsKey("VRS_IDENTIFICADOR")) {
                            rse.setVinculoRegistroServidor(!TextHelper.isNull(vrsCodigo) ? session.getReference(VinculoRegistroServidor.class, vrsCodigo.toString()) : null);
                        }
                        if (entrada.containsKey("POS_CODIGO") || entrada.containsKey("POS_IDENTIFICADOR")) {
                            rse.setPostoRegistroServidor(!TextHelper.isNull(posCodigo) ? session.getReference(PostoRegistroServidor.class, posCodigo.toString()) : null);
                        }
                        if (entrada.containsKey("TRS_CODIGO")) {
                            rse.setTipoRegistroServidor(!TextHelper.isNull(trsCodigo) ? session.getReference(TipoRegistroServidor.class, trsCodigo.toString()) : null);
                        }
                        if (entrada.containsKey("CAP_CODIGO")) {
                            rse.setCapacidadeRegistroSer(!TextHelper.isNull(capCodigo) ? session.getReference(CapacidadeRegistroSer.class, capCodigo.toString()) : null);
                        }

                        rse.setRseDataCarga(rseDataCarga);
                        AbstractEntityHome.update(rse);
                    } catch (final UpdateException ex) {
                        LOG.error(ex.getMessage(), ex);
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }
                }

                // 14.1.3) Inserir/atualizar o dado adicional do servidor na "tb_dados_servidor" para o tipo de dado.
                for (final String chave : chavesDadValor) {
                    final String tdaCodigo = chave.substring("DAS_VALOR_".length(), chave.length());

                    if (tdaCodigos.contains(tdaCodigo)) {
                        final String dasValor = !TextHelper.isNull(entrada.get("DAS_VALOR_" + tdaCodigo)) ? entrada.get("DAS_VALOR_" + tdaCodigo).toString() : null;
                        servidorController.gravarDadoServidor(ser.getSerCodigo(), tdaCodigo, dasValor, responsavel);
                    }
                }

                // 15) Caso registros de margem extra sejam enviados, "MRS_MARGEM_<CODIGO>" atualizar a "tb_margem_registro_servidor":
                // 15.1) Extrair o código de margem do nome do campo conforme padrão "MRS_MARGEM_<CODIGO>".
                // 15.2) Pesquisar se o código de margem é válido na "tb_margem", e caso não seja, ignorar e não atualizar.
                // 15.3) Caso seja um código de margem válido, pesquisar se o registro servidor já possui registro para esta margem na tabela "tb_margem_registro_servidor".
                // 15.4) Não possuindo registro de margem, inserir o novo registro de margem, com o valor informado nos dados traduzidos, e valor restante e usado iguais a zero.
                // 15.5) Possuindo registro de margem, atualizar o valor de margem extra na tabela correspondente.
                final List<MargemRegistroServidor> margensExtraAdd = new ArrayList<>();
                for (final TransferObject margem : cacheEntidades.getListByCampoIdentificador(Columns.MAR_CODIGO)) {
                    final Short marCodigo = (Short) margem.getAttribute(Columns.MAR_CODIGO);

                    if (entrada.containsKey("MRS_MARGEM_" + marCodigo)) {
                        final BigDecimal mrsMargem = NumberHelper.objectToBigDecimal(entrada.get("MRS_MARGEM_" + marCodigo));
                        Date mrsPeriodoIni = null;
                        Date mrsPeriodoFim = null;
                        try {
                            if (entrada.containsKey("MRS_MARGEM_" + marCodigo + "_PERIODO_INI")) {
                                mrsPeriodoIni = DateHelper.parse(entrada.get("MRS_MARGEM_" + marCodigo + "_PERIODO_INI").toString(), "yyyy-MM-dd");
                            }
                            if (entrada.containsKey("MRS_MARGEM_" + marCodigo + "_PERIODO_FIM")) {
                                mrsPeriodoFim = DateHelper.parse(entrada.get("MRS_MARGEM_" + marCodigo + "_PERIODO_FIM").toString(), "yyyy-MM-dd");
                            }

                            if ((mrsPeriodoIni != null) && (mrsPeriodoFim != null) && (mrsPeriodoIni.compareTo(mrsPeriodoFim) > 0)) {
                                erro += delimitador + ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.data.inicio.fim.margem.extra.ini.maior.fim", responsavel);
                                continue;
                            }

                            final MargemRegistroServidor mrs = new MargemRegistroServidor();
                            mrs.setMarCodigo(marCodigo);
                            mrs.setRseCodigo(rse.getRseCodigo());
                            mrs.setMrsMargem(mrsMargem);
                            mrs.setMrsPeriodoIni(mrsPeriodoIni);
                            mrs.setMrsPeriodoFim(mrsPeriodoFim);
                            margensExtraAdd.add(mrs);
                        } catch (final ParseException ex) {
                            LOG.error(ex.getMessage(), ex);
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
                        }
                    }
                }
                //DESENV-20077: Lógica desenvolvida para executar em SQL Batch para melhorar a performance da importação de margem quando existir margem extra
                if (!margensExtraAdd.isEmpty()) {
                    try {
                        //DESENV-20492: Caso haja margem de adequacao preenchida, o valor da margem folha deverá ser sobreposto pelo valor da margem adequacao
                        if (ParamSist.paramEquals(CodedValues.TPC_ADEQUAR_MARGEM_SERVIDOR_CONFORME_MARGEM_LIMITE, CodedValues.TPC_SIM, responsavel)) {
                            final List<MargemRegistroServidor> margensComAdequacao = MargemRegistroServidorHome.lstMargensComAdequacao(rse.getRseCodigo());
                            if((margensComAdequacao != null) && !margensComAdequacao.isEmpty()) {
                                final HashMap<Short, Short> hashMarCodigoMarAdequacao = new HashMap<>();
                                final HashMap<Short, BigDecimal> hashMarCodigoVlr = new HashMap<>();

                                // Mapeamos as margens que tem a adequacao vinculado a elas
                                for (final MargemRegistroServidor marAdequacao : margensComAdequacao) {
                                    hashMarCodigoMarAdequacao.put(marAdequacao.getMarCodigo(), marAdequacao.getMarCodAdequacao());
                                }

                                // Precisamos pegar todos os valores das margens, pois não sabemos quais delas serão atualizadas com o valor da adequacação
                                for (final MargemRegistroServidor margem : margensExtraAdd) {
                                    hashMarCodigoVlr.put(margem.getMarCodigo(), margem.getMrsMargem());
                                }

                                // Atualizamos as margens com o valor da margem que está vinculada a adequação, assim pegamos o valor da margem que está no campo da adequação.
                                for (final MargemRegistroServidor margemExtra : margensExtraAdd) {
                                    if (hashMarCodigoMarAdequacao.get(margemExtra.getMarCodigo()) != null) {
                                        margemExtra.setMrsMargem(hashMarCodigoVlr.get(hashMarCodigoMarAdequacao.get(margemExtra.getMarCodigo())));
                                    }
                                }
                            }
                        }
                        final MargemDAO margemDAO = DAOFactory.getDAOFactory().getMargemDAO();
                        margemDAO.criaAtualizaMargemExtraServidorBatch(margensExtraAdd, periodo, rse.getRseCodigo());
                    } catch (final UpdateException ex) {
                        LOG.error(ex.getMessage(), ex);
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }
                }

                // 16) Caso registros de base de cálculo sejam enviados, "BCS_VALOR_<CODIGO>" atualizar a "tb_base_calc_registro_servidor":
                // 16.1) Extrair o código da base de cálculo do nome do campo conforme padrão "BCS_VALOR_<CODIGO>".
                // 16.2) Pesquisar se o código da base de cálculo é válido na "tb_tipo_base_calculo", e caso não seja, ignorar e não atualizar.
                // 16.3) Caso seja um código de base de cálculo válido, pesquisar se o registro servidor já possui registro para esta base de cálculo na tabela "tb_base_calc_registro_servidor".
                // 16.4) Não possuindo registro de base de cálculo, inserir o novo registro da base de cálculo, com o valor informado nos dados traduzidos.
                // 16.5) Possuindo registro de base de cálculo, atualizar o valor base na tabela correspondente.
                for (final TransferObject baseCalculo : cacheEntidades.getListByCampoIdentificador(Columns.TBC_CODIGO)) {
                    final String tbcCodigo = (String) baseCalculo.getAttribute(Columns.TBC_CODIGO);
                    if (entrada.containsKey("BCS_VALOR_" + tbcCodigo)) {
                        final BigDecimal bcsValor = NumberHelper.objectToBigDecimal(entrada.get("BCS_VALOR_" + tbcCodigo));
                        try {
                            try {
                                final BaseCalcRegistroServidorId id = new BaseCalcRegistroServidorId(tbcCodigo, rse.getRseCodigo());
                                final BaseCalcRegistroServidor bcs = BaseCalcRegistroServidorHome.findByPrimaryKey(id);
                                bcs.setBcsValor(bcsValor);
                                AbstractEntityHome.update(bcs);
                            } catch (final FindException ex) {
                                // Se não existe, cria o registro de base de cálculo
                                BaseCalcRegistroServidorHome.create(tbcCodigo, rse.getRseCodigo(), bcsValor);
                            }
                        } catch (UpdateException | com.zetra.econsig.exception.CreateException ex) {
                            LOG.error(ex.getMessage(), ex);
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
                        }
                    }
                }

                // 17) Usuário Servidor, tabelas "tb_usuario", "tb_usuario_ser"
                // 17.1) Caso o parâmetro de sistema 420 esteja ativo, informando que o sistema dispõe de portal para acesso dos servidores, atualizar os dados do usuário.
                // 17.2) Recuperar o registro do usuário do servidor, conforme o login seja formado por estabelecimento e matrícula ou estabelecimento, órgão e matrícula, como informa o parâmetro de sistema 298.
                // 17.3) Caso não seja localizado, inserir novo usuário para o servidor, incluindo também a associação ao servidor e as permissões habilitadas para o papel de servidor.
                // 17.4) Caso o usuário seja localizado pelo login, certificar que aponta para o servidor correto, conforme relacionamento na tb_usuario_ser, e atualizar suas informações e permissões, caso necessário.
                if (possuiPortalServidor) {
                    final String est = (String) cacheEntidades.getByValorIdentificador(estCodigo.toString(), Columns.EST_CODIGO).get(0).getAttribute(Columns.EST_IDENTIFICADOR);
                    final String org = (String) cacheEntidades.getByValorIdentificador(orgCodigo.toString(), Columns.ORG_CODIGO).get(0).getAttribute(Columns.ORG_IDENTIFICADOR);
                    String loginServidor = null;
                    if (loginServidorEstOrg) {
                        loginServidor = est + "-" + org + "-" + rseMatricula;
                    } else {
                        loginServidor = est + "-" + rseMatricula;
                    }

                    try {
                        Usuario usuario = null;

                        String usuSenha = (String) entrada.get("USU_SENHA");
                        if (!TextHelper.isNull(usuSenha)) {
                            usuSenha = SenhaHelper.criptografarSenha(loginServidor, usuSenha.toString(), true, responsavel);
                        } else {
                            usuSenha = CodedValues.USU_SENHA_SERVIDOR_INICIAL;
                        }

                        try {
                            // Pesquisa pelo login (chave única)
                            usuario = UsuarioHome.findByLogin(loginServidor);

                            // Pesquisar demais ligações deste usuário com outros servidores, e apagar caso existam
                            final List<UsuarioSer> usuariosSer = UsuarioSerHome.listByUsuCodigo(usuario.getUsuCodigo());
                            for (final UsuarioSer usuarioSer : usuariosSer) {
                                if (!usuarioSer.getServidor().getSerCodigo().equals(ser.getSerCodigo())) {
                                    // Remove a ligação
                                    AbstractEntityHome.remove(usuarioSer);
                                }
                            }

                            // Atualiza o usuário encontrado e suas permissões.
                            if (entrada.containsKey("SER_NOME") || entrada.containsKey("SER_PRIMEIRO_NOME")) {
                                usuario.setUsuNome((String) serNome);
                            }
                            if (entrada.containsKey("SER_EMAIL")) {
                                usuario.setUsuEmail((String) serEmail);
                            }
                            if (entrada.containsKey("SER_TEL")) {
                                usuario.setUsuTel((String) serTel);
                            }
                            if (entrada.containsKey("SER_CPF")) {
                                usuario.setUsuCpf((String) serCpf);
                            }
                            if (entrada.containsKey("USU_SENHA")) {
                                usuario.setUsuSenha(usuSenha);
                            }
                            AbstractEntityHome.update(usuario);
                        } catch (final FindException ex) {
                            // Usuário com o login informado não existe. Cria novo usuário jutamente com suas permissões.
                            usuario = UsuarioHome.createUsuarioSenhaExpirada(loginServidor, usuSenha, serNome.toString(), (String) serEmail, (String) serTel, (String) serCpf);
                        }

                        if (usuario != null) {
                            try {
                                // Verifica ligação com servidor
                                final UsuarioSerId id = new UsuarioSerId(ser.getSerCodigo(), usuario.getUsuCodigo());
                                UsuarioSerHome.findByPrimaryKey(id);

                            } catch (final FindException ex) {
                                // Cria ligação com o servidor, caso não exista
                                UsuarioSerHome.create(ser.getSerCodigo(), usuario.getUsuCodigo(), CodedValues.STU_ATIVO);
                            }

                            try {
                                // Verifica ligação com perfil de usuário servidor
                                PerfilUsuarioHome.findByPrimaryKey(usuario.getUsuCodigo());
                            } catch (final FindException ex) {
                                // Caso não tenha o perfil associado, recria a ligação ao perfil de servidor
                                PerfilUsuarioHome.create(usuario.getUsuCodigo(), CodedValues.PER_CODIGO_SERVIDOR);
                            }
                        }
                    } catch (UpdateException | RemoveException | com.zetra.econsig.exception.CreateException ex) {
                        LOG.error(ex.getMessage(), ex);
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }
                }

                // DESENV-15410 - grava histórico de margem de folha
                for (short mar = 1; mar < 4; mar++) {
                    criarHistoricoMargemFolha(rse, mar, periodo, null, responsavel);
                }

                // DESENV-16728 : Encerramento de consignação na carga de margem
                if ((encerrarConsignacoes && excluidoPelaCargaMargem) && (!TextHelper.isNull(entrada.get("ENCERRAMENTO")) && "S".equals(entrada.get("ENCERRAMENTO")))) {
                    final String motivoEncerramento = entrada.get("MOTIVO_ENCERRAMENTO") != null ? entrada.get("MOTIVO_ENCERRAMENTO").toString() : "";
                    try {
                        // Executa as ações como usuário do sistema, de modo que as operações possam ser feitas sem restrições
                        encerrarConsignacaoController.encerrar(rse.getRseCodigo(), motivoEncerramento, AcessoSistema.getAcessoUsuarioSistema());
                    } catch (final AutorizacaoControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        throw new ServidorControllerException(ex);
                    }
                }
                if (reabrirConsignacoes && reativadoPelaCargaMargem) {
                    try {
                        // Executa as ações como usuário do sistema, de modo que as operações possam ser feitas sem restrições
                        encerrarConsignacaoController.reabrir(rse.getRseCodigo(), AcessoSistema.getAcessoUsuarioSistema());
                    } catch (final AutorizacaoControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        throw new ServidorControllerException(ex);
                    }
                }

                if (verificaVariacaoMargemLimiteDefinidoCsa && margemComplementar) {
                    final MargemDAO margemDAO = DAOFactory.getDAOFactory().getMargemDAO();
                    try {
                        margemDAO.insereHistoricoRseMargemComplementar(rse.getRseCodigo());
                    } catch (final DAOException ex) {
                        LOG.error(ex.getMessage(), ex);
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }
                }
            }
        }

        return erro;
    }

    private void criarHistoricoMargemFolha(RegistroServidor rse, Short marCodigo, String periodo, MargemRegistroServidor mrs, AcessoSistema responsavel) throws ServidorControllerException {
        HistoricoMargemFolha hma = null;
        BigDecimal margemAlvo = null;

        if (mrs == null) {
            switch (marCodigo) {
                case 1:
                    margemAlvo = rse.getRseMargem();
                    break;
                case 2:
                    margemAlvo = rse.getRseMargem2();
                    break;
                case 3:
                    margemAlvo = rse.getRseMargem3();
                    break;
                default:
                    break;
            }
        } else {
            margemAlvo = mrs.getMrsMargem();
            marCodigo = mrs.getMarCodigo();
        }

        try {
            final List<HistoricoMargemFolha> hmas = HistoricoMargemFolhaHome.findByRsePeriodoMarCodigo(rse.getRseCodigo(), marCodigo, periodo);
            if ((hmas != null) && !hmas.isEmpty()) {
                hma = hmas.get(0);
                hma.setHmaMargemFolha(margemAlvo != null ? margemAlvo : BigDecimal.ZERO);
                hma.setHmaData(DateHelper.getSystemDatetime());
                AbstractEntityHome.update(hma);
            } else {
                HistoricoMargemFolhaHome.create(rse.getRseCodigo(), marCodigo, DateHelper.objectToDate(periodo), DateHelper.getSystemDatetime(), margemAlvo != null ? margemAlvo : BigDecimal.ZERO);
            }

        } catch (UpdateException | CreateException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private int validaVariacaoServidores(List<String> orgCodigos, List<String> estCodigos, int totalSerAtivoAntigo, AcessoSistema responsavel) throws ServidorControllerException {
        final Object tpcPercMaxSerAtivo = ParamSist.getInstance().getParam(CodedValues.TPC_PERC_MAX_VAR_SER_ATIVO_CAD_MARGENS, responsavel);
        final float percMaxVarSerAtivo = TextHelper.isNotNumeric((String) tpcPercMaxSerAtivo) ? 0 : Float.parseFloat(tpcPercMaxSerAtivo.toString());
        final int totalSerAtivoAtual = contarRegistroServidor(null, orgCodigos, estCodigos, false, responsavel);
        LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.total.servidores.ativos.antes.importacao.arg0", responsavel, String.valueOf(totalSerAtivoAntigo)));
        LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.total.servidores.ativos.apos.importacao.arg0", responsavel, String.valueOf(totalSerAtivoAtual)));
        if ((totalSerAtivoAntigo > 0) && (percMaxVarSerAtivo > 0)) {
            final BigDecimal percSerAtivoCalc = BigDecimal.valueOf(((totalSerAtivoAtual * 100.00) / totalSerAtivoAntigo) - 100.00);
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.percentual.variacao.servidores.ativos.arg0", responsavel, String.valueOf(percSerAtivoCalc.abs().floatValue())));
            if (percSerAtivoCalc.abs().floatValue() > percMaxVarSerAtivo) {
                throw new ServidorControllerException("mensagem.erro.cadMargem.percentual.maximo.variacao.numero.servidores.ativos.importacao.atingido.arg0.arg1.arg2", responsavel, NumberHelper.format(percMaxVarSerAtivo, NumberHelper.getLang()), NumberHelper.formata(totalSerAtivoAntigo, "0"), NumberHelper.formata(totalSerAtivoAtual, "0"));
            }
        }

        return totalSerAtivoAtual;
    }

    private int contarRegistroServidor(String serCodigo, List<String> orgCodigos, List<String> estCodigos, boolean recuperaRseExcluido, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final ListaRegistroServidorQuery query = new ListaRegistroServidorQuery();
            query.count = true;
            query.serCodigo = serCodigo;
            query.orgCodigos = orgCodigos;
            query.estCodigos = estCodigos;
            query.recuperaRseExcluido = recuperaRseExcluido;
            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private Map<Short, Map<String, BigDecimal>> validaVariacaoMargens(List<String> orgCodigos, List<String> estCodigos, List<TransferObject> margemMediaTotalAntiga, List<TransferObject> margemExtraMediaTotalAntiga, AcessoSistema responsavel) throws ServidorControllerException {
        final Map<Short, Map<String, BigDecimal>> mediaMargem = new HashMap<>();

        final Object tpcPercMaxMaiorMargem = ParamSist.getInstance().getParam(CodedValues.TPC_PERC_MAX_VAR_MARGEM_MAIOR_CAD_MARGENS, responsavel);
        final float percMaxVarMaiorMargem = TextHelper.isNotNumeric((String) tpcPercMaxMaiorMargem) ? 0 : Float.parseFloat(tpcPercMaxMaiorMargem.toString());
        final Object tpcPercMaxMenorMargem = ParamSist.getInstance().getParam(CodedValues.TPC_PERC_MAX_VAR_MARGEM_MENOR_CAD_MARGENS, responsavel);
        final float percMaxVarMenorMargem = TextHelper.isNotNumeric((String) tpcPercMaxMenorMargem) ? 0 : Float.parseFloat(tpcPercMaxMenorMargem.toString());
        if ((percMaxVarMaiorMargem > 0) || (percMaxVarMenorMargem > 0)) {
            final List<TransferObject> margemMediaTotalAtual = recuperaMargemMediaTotal(orgCodigos, estCodigos, false, responsavel);
            final TransferObject margemMediaAntiga = margemMediaTotalAntiga.iterator().next();
            final TransferObject margemMediaAtual = margemMediaTotalAtual.iterator().next();

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
            if (!TextHelper.isNull(margemMediaAtual.getAttribute("RSE_MARGEM"))) {
                margemAtual = Float.parseFloat(margemMediaAtual.getAttribute("RSE_MARGEM").toString());
            }
            float margem2Atual = 0;
            if (!TextHelper.isNull(margemMediaAtual.getAttribute("RSE_MARGEM_2"))) {
                margem2Atual = Float.parseFloat(margemMediaAtual.getAttribute("RSE_MARGEM_2").toString());
            }
            float margem3Atual = 0;
            if (!TextHelper.isNull(margemMediaAtual.getAttribute("RSE_MARGEM_3"))) {
                margem3Atual = Float.parseFloat(margemMediaAtual.getAttribute("RSE_MARGEM_3").toString());
            }

            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.media.margem.1.bruta.antiga.arg0", responsavel, String.valueOf(margemAntiga)));
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.media.margem.1.bruta.atual.arg0", responsavel, String.valueOf(margemAtual)));
            validaMargemMedia(percMaxVarMaiorMargem, percMaxVarMenorMargem, margemAntiga, margemAtual, CodedValues.INCIDE_MARGEM_SIM.toString());
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.media.margem.2.bruta.antiga.arg0", responsavel, String.valueOf(margem2Antiga)));
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.media.margem.2.bruta.atual.arg0", responsavel, String.valueOf(margem2Atual)));
            validaMargemMedia(percMaxVarMaiorMargem, percMaxVarMenorMargem, margem2Antiga, margem2Atual, CodedValues.INCIDE_MARGEM_SIM_2.toString());
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.media.margem.3.bruta.antiga.arg0", responsavel, String.valueOf(margem3Antiga)));
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.media.margem.3.bruta.atual.arg0", responsavel, String.valueOf(margem3Atual)));
            validaMargemMedia(percMaxVarMaiorMargem, percMaxVarMenorMargem, margem3Antiga, margem3Atual, CodedValues.INCIDE_MARGEM_SIM_3.toString());

            Map<String, BigDecimal> medias = new HashMap<>();

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

            final List<TransferObject> margemExtraMediaTotalAtual = recuperaMargemExtraMediaTotal(orgCodigos, estCodigos, false, responsavel);

            for (final TransferObject margemExtraMediaAntiga : margemExtraMediaTotalAntiga) {
                for (final TransferObject margemExtraMediaAtual : margemExtraMediaTotalAtual) {
                    if (margemExtraMediaAtual.getAttribute("MAR_CODIGO").equals(margemExtraMediaAntiga.getAttribute("MAR_CODIGO"))) {

                        float margemExtraAntiga = 0;
                        if (!TextHelper.isNull(margemExtraMediaAntiga.getAttribute("MRS_MARGEM"))) {
                            margemExtraAntiga = Float.parseFloat(margemExtraMediaAntiga.getAttribute("MRS_MARGEM").toString());
                        }
                        float margemExtraAtual = 0;
                        if (!TextHelper.isNull(margemExtraMediaAtual.getAttribute("MRS_MARGEM"))) {
                            margemExtraAtual = Float.parseFloat(margemExtraMediaAtual.getAttribute("MRS_MARGEM").toString());
                        }

                        if ((margemExtraAntiga != 0) && (margemExtraAtual != 0)) {
                            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.media.margem.extra.bruta.antiga.arg0", responsavel, String.valueOf(margemExtraAntiga)));
                            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.media.margem.extra.bruta.atual.arg0", responsavel, String.valueOf(margemExtraAtual)));
                            validaMargemMedia(percMaxVarMaiorMargem, percMaxVarMenorMargem, margemExtraAntiga, margemExtraAtual, margemExtraMediaAtual.getAttribute("MAR_CODIGO").toString());

                            // Margem extra
                            medias = new HashMap<>();
                            medias.put(Columns.HMM_MEDIA_MARGEM_ANTES, BigDecimal.valueOf(margemExtraAntiga));
                            medias.put(Columns.HMM_MEDIA_MARGEM_DEPOIS, BigDecimal.valueOf(margemExtraAtual));
                            mediaMargem.put(Short.valueOf(String.valueOf(margemExtraMediaAtual.getAttribute("MAR_CODIGO"))), medias);
                        }
                    }
                }
            }
        }

        return mediaMargem;
    }

    private List<TransferObject> recuperaMargemMediaTotal(List<String> orgCodigos, List<String> estCodigos, boolean recuperaRseExcluido, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final RecuperaMargemMediaTotalQuery query = new RecuperaMargemMediaTotalQuery();
            query.orgCodigos = orgCodigos;
            query.estCodigos = estCodigos;
            query.recuperaRseExcluido = recuperaRseExcluido;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private List<TransferObject> recuperaMargemExtraMediaTotal(List<String> orgCodigos, List<String> estCodigos, boolean recuperaRseExcluido, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final RecuperaMargemExtraMediaTotalQuery query = new RecuperaMargemExtraMediaTotalQuery();
            query.orgCodigos = orgCodigos;
            query.estCodigos = estCodigos;
            query.recuperaRseExcluido = recuperaRseExcluido;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void validaMargemMedia(float percMaxVarMaiorMargem, float percMaxVarMenorMargem, float margemAntiga, float margemAtual, String descricao) throws ServidorControllerException {
        if (margemAntiga > 0) {
            final BigDecimal percMargemCalc = BigDecimal.valueOf(((margemAtual * 100.00) / margemAntiga) - 100.00);
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.percentual.variacao.margem.bruta.arg0.arg1", (AcessoSistema) null, descricao, String.valueOf(percMargemCalc.floatValue())));
            if ((percMaxVarMaiorMargem > 0) && (percMargemCalc.floatValue() > percMaxVarMaiorMargem)) {
                throw new ServidorControllerException("mensagem.erro.cadMargem.percentual.maximo.variacao.margem.bruta.importacao.atingido.arg0.arg1.arg2.arg3", (AcessoSistema) null, NumberHelper.format(percMaxVarMaiorMargem, NumberHelper.getLang()), descricao, NumberHelper.format(margemAntiga, NumberHelper.getLang()), NumberHelper.format(margemAtual, NumberHelper.getLang()));
            } else if ((percMaxVarMenorMargem > 0) && (percMargemCalc.compareTo(BigDecimal.ZERO) < 0) && (percMargemCalc.abs().floatValue() > percMaxVarMenorMargem)) {
                throw new ServidorControllerException("mensagem.erro.cadMargem.percentual.maximo.variacao.margem.bruta.importacao.atingido.arg0.arg1.arg2.arg3", (AcessoSistema) null, NumberHelper.format(percMaxVarMaiorMargem, NumberHelper.getLang()), descricao, NumberHelper.format(margemAntiga, NumberHelper.getLang()), NumberHelper.format(margemAtual, NumberHelper.getLang()));
            }
        }
    }

    private void escreveCritica(StringBuilder buffer, String nomeArquivo) throws IOException {
        final PrintWriter arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArquivo)));
        arqSaida.print(buffer);
        arqSaida.close();
    }

    private String validarEntidadeParaCargaMargem(Object codigo, Object identificador, Object cnpj, Object descricao, String colunaCodigo, String colunaIdentificador, String colunaCnpj, String tabela, CacheDependenciasServidor cacheEntidades, String codigoEntidadePai, boolean obrigatorio, AcessoSistema responsavel) throws ServidorControllerException {
        // 1) Pesquisar a entidade pelo campo XXX_CODIGO, XXX_IDENTIFICADOR ou XXX_CNPJ (caso exista), nesta ordem, de acordo com a existência do campo nos dados traduzidos.
        // 2) Caso a entidade não exista, e o campo XXX_NOME seja informado, proceder o cadastro da nova entidade.
        // 3) Caso a entidade não exista, e o campo XXX_NOME NÃO seja informado, gerar crítica de importação e continuar na próxima linha.
        boolean criarEntidade = false;
        if (!TextHelper.isNull(codigo) && !cacheEntidades.existeIdentificador(codigo.toString(), colunaCodigo)) {
            // Se informar o código, ele deve existir. Caso não exista, gera crítica
            throw new ServidorControllerException("mensagem.cadMargem.critica.erro.entidade.nao.encontrada." + colunaCodigo, responsavel, codigo.toString());
        } else if (!TextHelper.isNull(identificador)) {
            if (cacheEntidades.existeIdentificador(identificador.toString(), colunaIdentificador)) {
                final List<TransferObject> entidades = filtrarEntidadePeloCodigoPai(cacheEntidades.getByValorIdentificador(identificador.toString(), colunaIdentificador), tabela, codigoEntidadePai);
                if (entidades.size() == 1) {
                    codigo = entidades.get(0).getAttribute(colunaCodigo).toString();
                } else {
                    throw new ServidorControllerException("mensagem.cadMargem.critica.erro.entidade.nao.determinada." + tabela, responsavel);
                }
            } else if (!TextHelper.isNull(descricao)) {
                criarEntidade = true;
            } else {
                throw new ServidorControllerException("mensagem.cadMargem.critica.erro.entidade.nao.encontrada." + colunaIdentificador, responsavel, identificador.toString());
            }
        } else if (!TextHelper.isNull(cnpj)) {
            if (cacheEntidades.existeIdentificador(cnpj.toString(), colunaCnpj)) {
                final List<TransferObject> entidades = filtrarEntidadePeloCodigoPai(cacheEntidades.getByValorIdentificador(cnpj.toString(), colunaCnpj), tabela, codigoEntidadePai);
                if (entidades.size() == 1) {
                    codigo = entidades.get(0).getAttribute(colunaCodigo).toString();
                } else {
                    throw new ServidorControllerException("mensagem.cadMargem.critica.erro.entidade.nao.determinada." + tabela, responsavel);
                }
            } else if (!TextHelper.isNull(descricao)) {
                criarEntidade = true;
            } else {
                throw new ServidorControllerException("mensagem.cadMargem.critica.erro.entidade.nao.encontrada." + colunaCnpj, responsavel, cnpj.toString());
            }
        }
        if (criarEntidade) {
            try {
                final String idInclusao = (!TextHelper.isNull(identificador) ? identificador.toString() : (!TextHelper.isNull(cnpj) ? cnpj.toString() : null));
                final String cnpjInclusao = (!TextHelper.isNull(cnpj) ? cnpj.toString() : null);
                // Cria a entidade
                codigo = criarEntidadeParaCargaMargem(tabela, idInclusao, descricao.toString(), cnpjInclusao, codigoEntidadePai);
                // Recarrega os caches de entidades
                cacheEntidades.carregarCache(responsavel);
            } catch (final com.zetra.econsig.exception.CreateException ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new ServidorControllerException("mensagem.cadMargem.critica.erro.entidade.nao.criada." + tabela, responsavel, ex.getMessage());
            }
        }
        if (TextHelper.isNull(codigo) && obrigatorio) {
            final Map<String, List<TransferObject>> codigoCache = cacheEntidades.getMapByCampoIdentificador(colunaCodigo);
            if (codigoCache.size() == 1) {
                return codigoCache.keySet().iterator().next();
            } else {
                final List<String> candidatos = new ArrayList<>();
                for (final String chave : codigoCache.keySet()) {
                    final List<TransferObject> entidades = filtrarEntidadePeloCodigoPai(codigoCache.get(chave), tabela, codigoEntidadePai);
                    if (!entidades.isEmpty()) {
                        candidatos.add(chave);
                    }
                }
                if (candidatos.size() == 1) {
                    return candidatos.get(0);
                } else {
                    throw new ServidorControllerException("mensagem.cadMargem.critica.erro.entidade.nao.determinada." + tabela, responsavel);
                }
            }
        } else if (!TextHelper.isNull(codigo)) {
            return codigo.toString();
        } else {
            return null;
        }
    }

    private List<TransferObject> filtrarEntidadePeloCodigoPai(List<TransferObject> entidades, String tabela, String codigoEntidadePai) {
        String colunaCodigoPai = null;
        if (!TextHelper.isNull(codigoEntidadePai) && (entidades != null) && !entidades.isEmpty()) {
            if (Columns.TB_ORGAO.equals(tabela)) {
                colunaCodigoPai = Columns.EST_CODIGO;
            } else if (Columns.TB_SUB_ORGAO.equals(tabela)) {
                colunaCodigoPai = Columns.ORG_CODIGO;
            } else if (Columns.TB_UNIDADE.equals(tabela)) {
                colunaCodigoPai = Columns.SBO_CODIGO;
            }
        }
        if (colunaCodigoPai != null) {
            final List<TransferObject> candidatos = new ArrayList<>();
            for (final TransferObject entidade : entidades) {
                if (codigoEntidadePai.equals(entidade.getAttribute(colunaCodigoPai))) {
                    candidatos.add(entidade);
                }
            }
            return candidatos;
        }
        return entidades;
    }

    private String criarEntidadeParaCargaMargem(String tabela, String identificador, String descricao, String cnpj, String codigoEntidadePai) throws com.zetra.econsig.exception.CreateException {
        if (Columns.TB_ESTABELECIMENTO.equals(tabela) && !TextHelper.isNull(codigoEntidadePai)) {
            return EstabelecimentoHome.create(codigoEntidadePai, identificador, descricao, cnpj).getEstCodigo();
        } else if (Columns.TB_ORGAO.equals(tabela) && !TextHelper.isNull(codigoEntidadePai)) {
            return OrgaoHome.create(codigoEntidadePai, identificador, descricao, cnpj).getOrgCodigo();
        } else if (Columns.TB_SUB_ORGAO.equals(tabela) && !TextHelper.isNull(codigoEntidadePai)) {
            return SubOrgaoHome.create(codigoEntidadePai, identificador, descricao).getSboCodigo();
        } else if (Columns.TB_UNIDADE.equals(tabela) && !TextHelper.isNull(codigoEntidadePai)) {
            return UnidadeHome.create(codigoEntidadePai, identificador, descricao).getUniCodigo();
        } else if (Columns.TB_VINCULO_REGISTRO_SERVIDOR.equals(tabela)) {
            return VinculoRegistroServidorHome.create(identificador, descricao).getVrsCodigo();
        } else if (Columns.TB_CARGO_REGISTRO_SERVIDOR.equals(tabela)) {
            return CargoRegistroServidorHome.create(identificador, descricao).getCrsCodigo();
        } else if (Columns.TB_PADRAO_REGISTRO_SERVIDOR.equals(tabela)) {
            return PadraoRegistroServidorHome.create(identificador, descricao).getPrsCodigo();
        } else if (Columns.TB_POSTO_REGISTRO_SERVIDOR.equals(tabela)) {
            return PostoRegistroServidorHome.create(identificador, descricao).getPosCodigo();
        } else if (Columns.TB_TIPO_HABITACAO.equals(tabela)) {
            return TipoHabitacaoHome.create(identificador, descricao).getThaCodigo();
        } else if (Columns.TB_NIVEL_ESCOLARIDADE.equals(tabela)) {
            return NivelEscolaridadeHome.create(identificador, descricao).getNesCodigo();
        }
        return null;
    }

    private void carregarCacheEntidade(Map<String, List<TransferObject>> cache, String columnsIdent, AcessoSistema responsavel) {
        try {
            if (cache != null) {
                // Garante que o cache está vazio
                cache.clear();

                List<TransferObject> listaTO = null;
                if (Columns.ORG_CODIGO.equals(columnsIdent) || Columns.ORG_IDENTIFICADOR.equals(columnsIdent) || Columns.ORG_CNPJ.equals(columnsIdent)) {
                    listaTO = consignanteController.lstOrgaos(null, responsavel);
                } else if (Columns.EST_CODIGO.equals(columnsIdent) || Columns.EST_IDENTIFICADOR.equals(columnsIdent) || Columns.EST_CNPJ.equals(columnsIdent)) {
                    listaTO = consignanteController.lstEstabelecimentos(null, responsavel);
                } else if (Columns.SBO_CODIGO.equals(columnsIdent) || Columns.SBO_IDENTIFICADOR.equals(columnsIdent)) {
                    listaTO = servidorController.lstSubOrgao(responsavel, null);
                } else if (Columns.UNI_CODIGO.equals(columnsIdent) || Columns.UNI_IDENTIFICADOR.equals(columnsIdent)) {
                    listaTO = servidorController.lstUnidade(responsavel, null);
                } else if (Columns.VRS_CODIGO.equals(columnsIdent) || Columns.VRS_IDENTIFICADOR.equals(columnsIdent)) {
                    listaTO = servidorController.selectVincRegistroServidor(false, responsavel);
                } else if (Columns.CRS_CODIGO.equals(columnsIdent) || Columns.CRS_IDENTIFICADOR.equals(columnsIdent)) {
                    listaTO = servidorController.lstCargo(responsavel);
                } else if (Columns.PRS_CODIGO.equals(columnsIdent) || Columns.PRS_IDENTIFICADOR.equals(columnsIdent)) {
                    listaTO = servidorController.lstPadrao(responsavel);
                } else if (Columns.POS_CODIGO.equals(columnsIdent) || Columns.POS_IDENTIFICADOR.equals(columnsIdent)) {
                    listaTO = servidorController.lstPosto(responsavel);
                } else if (Columns.THA_CODIGO.equals(columnsIdent) || Columns.THA_IDENTIFICADOR.equals(columnsIdent)) {
                    listaTO = servidorController.getTipoHabitacao(responsavel);
                } else if (Columns.NES_CODIGO.equals(columnsIdent) || Columns.NES_IDENTIFICADOR.equals(columnsIdent)) {
                    listaTO = servidorController.getNivelEscolaridade(responsavel);
                } else if (Columns.TRS_CODIGO.equals(columnsIdent)) {
                    listaTO = servidorController.lstTipoRegistroServidor(responsavel);
                } else if (Columns.CAP_CODIGO.equals(columnsIdent)) {
                    listaTO = servidorController.lstCapacidadeCivil(responsavel);
                }
                for (final TransferObject entidade : listaTO) {
                    if (entidade.getAttribute(columnsIdent) != null) {
                        final String chave = entidade.getAttribute(columnsIdent).toString();
                        if (cache.get(chave) == null) {
                            cache.put(chave, new ArrayList<>());
                        }
                        cache.get(chave).add(entidade);
                    }
                }
            }
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private boolean existeIdentificador(Map<String, List<TransferObject>> cache, String ident, String columnsIdent, AcessoSistema responsavel) {
        if (cache.isEmpty()) {
            carregarCacheEntidade(cache, columnsIdent, responsavel);
        }
        return ((cache.get(ident) != null) && !cache.get(ident).isEmpty());
    }

    private String geraArquivoServidorTransferido(AcessoSistema responsavel, String tipoEntidade, String codigoEntidade, ImportaMargem importadorMargem) throws ServidorControllerException, ImportaMargemException {
        final ServidorDAO servidor = DAOFactory.getDAOFactory().getServidorDAO();
        try {
            // Recupera o codigo do orgao/estabelecimento
            String orgCodigo = null;
            String estCodigo = null;
            if ("ORG".equalsIgnoreCase(tipoEntidade)) {
                orgCodigo = codigoEntidade;
            } else if ("EST".equalsIgnoreCase(tipoEntidade)) {
                estCodigo = codigoEntidade;
            }

            // Inicializa a lista de códigos de entidades
            List<String> codigos = null;
            if (!TextHelper.isNull(codigoEntidade)) {
                codigos = new ArrayList<>();
                codigos.add(codigoEntidade);
            }

            // Chama o metodo que gerará a tabela tb_arq_transferidos e retorna o conteudo dela
            String query = servidor.obtemServidoresTransferidos(tipoEntidade, codigoEntidade, responsavel);

            if (importadorMargem != null) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.preProcessamento.geracao.arquivo.transferidos.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                query = importadorMargem.preGeracaoArqTransferidos(query, tipoEntidade, codigos, responsavel);
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.preProcessamento.geracao.arquivo.transferidos.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            if (query == null) {
                return null;
            }

            // Diretório raiz do arquivos eConsig
            final String absolutePath = ParamSist.getDiretorioRaizArquivos();
            final String pathConf = absolutePath + File.separatorChar + "conf";
            String pathSaida = absolutePath + File.separatorChar + "transferidos";

            // Arquivos de configuração para importação de margem.
            final String nomeArqConfE = ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_EXP_TRANSF, responsavel).toString();
            final String nomeArqConfT = ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_EXP_TRANSF, responsavel).toString();
            final String nomeArqConfS = ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_SAIDA_EXP_TRANSF, responsavel).toString();

            // Nome dos arquivos a serem utilizados, com caminho completo
            String nomeArqConfEntrada = null;
            String nomeArqConfSaida = null;
            String nomeArqConfTradutor = null;

            // Verifica se o processamento da margem é por orgão/estabelecimento, se for pega os arquivos
            // de configuração do diretório especifico, senão pega do diretório raiz. Se não tiver arquivo
            // de configuração no diretório do órgão/estabelecimento, usará o da raiz. Se não existir em ambos,
            // gerará exceção.
            if (!TextHelper.isNull(orgCodigo)) {
                // Arquivos de configuração por Órgão
                nomeArqConfEntrada = pathConf + File.separatorChar + "cse" + File.separatorChar + orgCodigo + File.separatorChar + nomeArqConfE;
                nomeArqConfTradutor = pathConf + File.separatorChar + "cse" + File.separatorChar + orgCodigo + File.separatorChar + nomeArqConfT;
                nomeArqConfSaida = pathConf + File.separatorChar + "cse" + File.separatorChar + orgCodigo + File.separatorChar + nomeArqConfS;
                pathSaida += File.separatorChar + "cse" + File.separatorChar + orgCodigo;
            } else if (!TextHelper.isNull(estCodigo)) {
                // Arquivos de configuração por Estabelecimento
                nomeArqConfEntrada = pathConf + File.separatorChar + "est" + File.separatorChar + estCodigo + File.separatorChar + nomeArqConfE;
                nomeArqConfTradutor = pathConf + File.separatorChar + "est" + File.separatorChar + estCodigo + File.separatorChar + nomeArqConfT;
                nomeArqConfSaida = pathConf + File.separatorChar + "est" + File.separatorChar + estCodigo + File.separatorChar + nomeArqConfS;
                pathSaida += File.separatorChar + "est" + File.separatorChar + estCodigo;
            } else {
                pathSaida += File.separatorChar + "cse";
            }

            // Se não é importação por órgão/estabelecimento, ou é mas o arquivo não existe,
            // obtém os arquivos de configuração no path raiz.
            if (TextHelper.isNull(nomeArqConfEntrada) || !(new File(nomeArqConfEntrada).exists())) {
                nomeArqConfEntrada = pathConf + File.separatorChar + nomeArqConfE;
            }
            if (TextHelper.isNull(nomeArqConfTradutor) || !(new File(nomeArqConfTradutor).exists())) {
                nomeArqConfTradutor = pathConf + File.separatorChar + nomeArqConfT;
            }
            if (TextHelper.isNull(nomeArqConfSaida) || !(new File(nomeArqConfSaida).exists())) {
                nomeArqConfSaida = pathConf + File.separatorChar + nomeArqConfS;
            }

            final File dir = new File(pathSaida);
            if (!dir.exists() && !dir.mkdirs()) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.erro.criacao.diretorio", responsavel));
                return null;
            }

            final String nomeArqSaida = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.transferidos.prefixo", responsavel) + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss") + ".txt";
            pathSaida = pathSaida + File.separatorChar + nomeArqSaida;

            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.entrada.arg0", responsavel, nomeArqConfEntrada));
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.tradutor.arg0", responsavel, nomeArqConfTradutor));
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.saida.arg0", responsavel, nomeArqConfSaida));
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.arquivo.arg0", responsavel, pathSaida));

            final EscritorArquivoTexto escritor = new EscritorArquivoTexto(nomeArqConfSaida, pathSaida);
            final Leitor leitor = new LeitorBaseDeDados(nomeArqConfEntrada, query);
            final Tradutor tradutor = new Tradutor(nomeArqConfTradutor, leitor, escritor);
            tradutor.traduz();
            return nomeArqSaida;
        } catch (DAOException | ParserException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Realiza a importação do cadastro dos servidores transferidos
     * @param nomeArquivo : nome do arquivo contendo os servidores transferidos
     * @param usuCodigo : responsável
     * @throws ServidorControllerException
     */
    @Override
    public String importaServidoresTransferidos(String nomeArquivo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            if (nomeArquivo.endsWith(".crypt")) {
                final File arquivoPlano = CriptografiaArquivos.descriptografarArquivo(nomeArquivo, true, responsavel);
                if (arquivoPlano != null) {
                    nomeArquivo = arquivoPlano.getAbsolutePath();
                }
            }

            // Delegates necessários
            // DAOs necessários
            final ServidorDAO servidorDao = DAOFactory.getDAOFactory().getServidorDAO();

            // Recupera o codigo do orgao/estabelecimento
            String orgCodigo = null;
            String estCodigo = null;
            if ("ORG".equalsIgnoreCase(tipoEntidade)) {
                orgCodigo = codigoEntidade;
            } else if ("EST".equalsIgnoreCase(tipoEntidade)) {
                estCodigo = codigoEntidade;
            }

            // Diretório raiz do arquivos eConsig
            final String absolutePath = ParamSist.getDiretorioRaizArquivos();
            final String pathConf = absolutePath + File.separatorChar + "conf";

            // Arquivos de configuração para importação de margem.
            final String nomeArqConfE = ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_TRANSF, responsavel).toString();
            final String nomeArqConfT = ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_TRANSF, responsavel).toString();

            // Nome dos arquivos a serem utilizados, com caminho completo
            String nomeArqConfEntrada = null;
            String nomeArqConfTradutor = null;

            // Verifica se o processamento da margem é por orgão/estabelecimento, se for pega os arquivos
            // de configuração do diretório especifico, senão pega do diretório raiz. Se não tiver arquivo
            // de configuração no diretório do órgão/estabelecimento, usará o da raiz. Se não existir em ambos,
            // gerará exceção.
            if (!TextHelper.isNull(orgCodigo)) {
                // Arquivos de configuração por Órgão
                nomeArqConfEntrada = pathConf + File.separatorChar + "cse" + File.separatorChar + orgCodigo + File.separatorChar + nomeArqConfE;
                nomeArqConfTradutor = pathConf + File.separatorChar + "cse" + File.separatorChar + orgCodigo + File.separatorChar + nomeArqConfT;
            } else if (!TextHelper.isNull(estCodigo)) {
                // Arquivos de configuração por Estabelecimento
                nomeArqConfEntrada = pathConf + File.separatorChar + "est" + File.separatorChar + estCodigo + File.separatorChar + nomeArqConfE;
                nomeArqConfTradutor = pathConf + File.separatorChar + "est" + File.separatorChar + estCodigo + File.separatorChar + nomeArqConfT;
            }

            // Se não é importação por órgão/estabelecimento, ou é mas o arquivo não existe,
            // obtém os arquivos de configuração no path raiz.
            if (TextHelper.isNull(nomeArqConfEntrada) || !(new File(nomeArqConfEntrada).exists())) {
                nomeArqConfEntrada = pathConf + File.separatorChar + nomeArqConfE;
            }
            if (TextHelper.isNull(nomeArqConfTradutor) || !(new File(nomeArqConfTradutor).exists())) {
                nomeArqConfTradutor = pathConf + File.separatorChar + nomeArqConfT;
            }

            // Inicia o processo de importação dos transferidos
            final File arqConfEntrada = new File(nomeArqConfEntrada);
            final File arqConfTradutor = new File(nomeArqConfTradutor);

            // Verifica se os arquivos existem
            if (!arqConfEntrada.exists() || !arqConfTradutor.exists()) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.sistema.arquivos.configuracao.importacao.transferidos.ausente", responsavel));
                throw new ServidorControllerException("mensagem.erro.sistema.arquivos.configuracao.importacao.transferidos.ausente", responsavel);
            }

            final File arqEntrada = new File(nomeArquivo);
            if (!arqEntrada.exists()) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.sistema.arquivos.transferidos.nao.encontrado", responsavel));
                throw new ServidorControllerException("mensagem.erro.sistema.arquivos.transferidos.nao.encontrado", responsavel);
            }

            if (qtdLinhasArqTransferidosAcimaPermitido(nomeArquivo, tipoEntidade, codigoEntidade, responsavel)) {
                LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.log.warn.cadMargem.qtd.linhas.arq.transferidos.acima.permitido.arg0", responsavel, nomeArquivo));
            }

            // Configura o leitor de acordo com o arquivo de entrada
            LeitorArquivoTexto leitor = null;
            if (nomeArquivo.toLowerCase().endsWith(".zip")) {
                leitor = new LeitorArquivoTextoZip(nomeArqConfEntrada, nomeArquivo);
            } else {
                leitor = new LeitorArquivoTexto(nomeArqConfEntrada, nomeArquivo);
            }

            // Hash que recebe os dados do que serão lidos do arquivo de entrada
            final Map<String, Object> entrada = new HashMap<>();

            // Escritor e tradutor
            final Escritor escritor = new EscritorMemoria(entrada);
            final Tradutor tradutor = new Tradutor(nomeArqConfTradutor, leitor, escritor);

            // Lista para armazenar as linhas de entrada que foram criticadas
            final List<String> critica = new ArrayList<>();

            // Inicia o processo de importação de margem
            // Verifica se existem serviços ligados a natureza que não permitem transferência
            final ListaServicoNaturezaSemTransfQuery svcSemTransfQuery = new ListaServicoNaturezaSemTransfQuery();
            svcSemTransfQuery.count = true;
            final boolean existeSvcSemTransf = (svcSemTransfQuery.executarContador() > 0);

            // Verifica parâmetro de sistema que bloqueia transferência para cnv/svc com bloqueio de servidor
            final boolean bloqTransfSerBloqCnvSvc = ParamSist.paramEquals(CodedValues.TPC_BLOQ_TRANSF_ADE_COM_BLOQ_CNV_SVC_SERVIDOR, CodedValues.TPC_SIM, responsavel);

            // Bloqueia transferência caso o servidor de destino seja de cpf diferente
            final boolean bloqTransfSerCpfDiferente = !ParamSist.paramEquals(CodedValues.TPC_BLOQ_TRANSF_ADE_SERVIDOR_CPF_DIFERENTE, CodedValues.TPC_NAO, responsavel);

            // Verifica se no processamento a nível de ORG/EST são permitidas transferências para outros órgãos
            // que não sejam do usuário atual, para que estes servidores tenha a margem recalculada.
            final boolean permiteTransfOutrosOrgaos = ParamSist.paramEquals(CodedValues.TPC_PERMITE_TRANSFERENCIA_PARA_OUTROS_ORGAOS, CodedValues.TPC_SIM, responsavel) && ((tipoEntidade != null) && ("ORG".equalsIgnoreCase(tipoEntidade) || "EST".equalsIgnoreCase(tipoEntidade)));
            // Lista com servidores para cálculo de margem, caso sejam transferidos para outros órgãos fora do processamento atual.
            final List<String> rseCodigosCalcMargem = new ArrayList<>();

            // Verificar se tem bloqueios
            final boolean temBloqueioCnv = parametroController.temServidorBloqueadoCnv(responsavel);
            final boolean temBloqueioSvc = parametroController.temServidorBloqueadoSvc(responsavel);
            final boolean temBloqueioNse = parametroController.temServidorBloqueadoNse(responsavel);
            final boolean temBloqueioCsa = parametroController.temServidorBloqueadoCsa(responsavel);

            // DESENV-10310 : Faz a limpeza da session (por causa da incapacidade do hibernate).
            final BatchManager batman = new BatchManager(SessionUtil.getSession());

            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.traducao.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            tradutor.iniciaTraducao();
            while (tradutor.traduzProximo()) {
                final String msgErro = processarLinhaTransferidos(entrada, tipoEntidade, codigoEntidade, rseCodigosCalcMargem, temBloqueioCnv, temBloqueioSvc, temBloqueioNse, temBloqueioCsa, existeSvcSemTransf, bloqTransfSerCpfDiferente, bloqTransfSerBloqCnvSvc, permiteTransfOutrosOrgaos, servidorDao, responsavel);

                if (!TextHelper.isNull(msgErro)) {
                    for (final String msg : msgErro.split("\\|")) {
                        adicionaCritica(critica, leitor, msg);
                    }
                }

                // DESENV-10310 : Faz a limpeza da session (por causa da incapacidade do hibernate).
                batman.iterate();
            }

            tradutor.encerraTraducao();

            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.traducao.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            String nomeArqSaida = null;
            try {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.arquivos.critica.inicio.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                // Grava arquivo contendo as parcelas não encontradas no sistema
                if (!critica.isEmpty()) {
                    final String nomeTemp = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", responsavel) + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss") + ".txt";

                    nomeArqSaida = nomeArquivo.replaceAll(arqEntrada.getName(), nomeTemp);

                    LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.nomeTemp.arg0", responsavel, nomeArqSaida));

                    final PrintWriter arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaida)));
                    arqSaida.print(TextHelper.join(critica, System.lineSeparator()));
                    arqSaida.close();
                }
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.arquivos.critica.fim.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            // Recalcula a margem
            List<String> codigosEnt = new ArrayList<>();
            if ((tipoEntidade != null) && (!"CSE".equalsIgnoreCase(tipoEntidade)) && (codigoEntidade != null)) {
                codigosEnt.add(codigoEntidade);
            } else {
                codigosEnt = null;
            }

            // Recalcula a margem dos servidores: Verifica parâmetro de sistema
            final boolean recalculaMargem = ParamSist.getBoolParamSist(CodedValues.TPC_RECALCULA_MARGEM_IMP_TRANSFERIDOS, responsavel);
            if (recalculaMargem) {
                margemController.recalculaMargemComHistorico(tipoEntidade, codigosEnt, responsavel);
            }

            // Se o processamento é por EST/ORG e permite transferência para outros órgãos, recalcula a margem
            // dos servidores que não pertencem à entidade que está realizando o processamento.
            if (permiteTransfOutrosOrgaos && !rseCodigosCalcMargem.isEmpty()) {
                margemController.recalculaMargemComHistorico("RSE", rseCodigosCalcMargem, responsavel);
            }

            final LogDelegate log = new LogDelegate(responsavel, Log.FOLHA, Log.SERVIDORES_TRANSFERIDOS, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.arquivo", responsavel, nomeArqSaida));
            log.write();

            // Corrige possíveis erros de bloqueios de convênios
            parametroController.corrigeBloqueioServidor(responsavel);

            // log auditoria ocorrência de consignante
            consignanteController.createOcorrenciaCse(CodedValues.TOC_IMPORTACAO_TRANSFERIDOS, responsavel);

            // Renomeia o arquivo despois de concluido com sucesso
            FileHelper.rename(nomeArquivo, nomeArquivo + ".ok");

            return nomeArqSaida;

        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            if (ex instanceof ZetraException) {
                throw new ServidorControllerException(ex);
            } else {
                throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    @Override
    public String processarLinhaTransferidos(Map<String, Object> entrada, AcessoSistema responsavel) throws ZetraException {
        try {
            // Delegates necessários
            // DAOs necessários
            final ServidorDAO servidorDao = DAOFactory.getDAOFactory().getServidorDAO();

            // Inicia o processo de importação de margem
            // Verifica se existem serviços ligados a natureza que não permitem transferência
            final ListaServicoNaturezaSemTransfQuery svcSemTransfQuery = new ListaServicoNaturezaSemTransfQuery();
            svcSemTransfQuery.count = true;
            final boolean existeSvcSemTransf = (svcSemTransfQuery.executarContador() > 0);

            // Verifica parâmetro de sistema que bloqueia transferência para cnv/svc com bloqueio de servidor
            final boolean bloqTransfSerBloqCnvSvc = ParamSist.paramEquals(CodedValues.TPC_BLOQ_TRANSF_ADE_COM_BLOQ_CNV_SVC_SERVIDOR, CodedValues.TPC_SIM, responsavel);

            // Bloqueia transferência caso o servidor de destino seja de cpf diferente
            final boolean bloqTransfSerCpfDiferente = !ParamSist.paramEquals(CodedValues.TPC_BLOQ_TRANSF_ADE_SERVIDOR_CPF_DIFERENTE, CodedValues.TPC_NAO, responsavel);

            // Verificar se tem bloqueios
            final boolean temBloqueioCnv = parametroController.temServidorBloqueadoCnv(responsavel);
            final boolean temBloqueioSvc = parametroController.temServidorBloqueadoSvc(responsavel);
            final boolean temBloqueioNse = parametroController.temServidorBloqueadoNse(responsavel);
            final boolean temBloqueioCsa = parametroController.temServidorBloqueadoCsa(responsavel);

            return processarLinhaTransferidos(entrada, null, null, null, temBloqueioCnv, temBloqueioSvc, temBloqueioNse, temBloqueioCsa, existeSvcSemTransf, bloqTransfSerCpfDiferente, bloqTransfSerBloqCnvSvc, false, servidorDao, responsavel);
        } catch (final DAOException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private String processarLinhaTransferidos(Map<String, Object> entrada, String tipoEntidade, String codigoEntidade, List<String> rseCodigosCalcMargem, boolean temBloqueioCnv, boolean temBloqueioSvc, boolean temBloqueioNse, boolean temBloqueioCsa, boolean existeSvcSemTransf, boolean bloqTransfSerCpfDiferente, boolean bloqTransfSerBloqCnvSvc, boolean permiteTransfOutrosOrgaos, ServidorDAO servidorDao, AcessoSistema responsavel) throws ZetraException {
        final List<String> criticas = new ArrayList<>();

        // Valores que podem constar no arquvio de entrada
        final String acao = (entrada.get("__ACAO__") != null ? entrada.get("__ACAO__").toString() : "T");
        final String orgIdentificador = (String) entrada.get("ORG_IDENTIFICADOR");
        final String estIdentificador = (String) entrada.get("EST_IDENTIFICADOR");
        final String rseMatricula = (String) entrada.get("RSE_MATRICULA");
        final String novoEstabelecimento = (String) entrada.get("NOVO_ESTABELECIMENTO");
        final String novoOrgao = (String) entrada.get("NOVO_ORGAO");
        final String novaRseMatricula = (String) entrada.get("NOVA_MATRICULA");
        final String adeNumero = (String) entrada.get("ADE_NUMERO");
        boolean criouServidorDestino = false;

        // Verifica se a linha lida possui informações minimas para a pesquisa do servidor
        if ((rseMatricula != null) && !"".equals(rseMatricula)) {
            // Query para busca de servidores para transferencia
            final ListaServidorTransferenciaQuery lstServidorQuery = new ListaServidorTransferenciaQuery();
            lstServidorQuery.estIdentificador = estIdentificador;
            lstServidorQuery.orgIdentificador = orgIdentificador;
            lstServidorQuery.rseMatricula = rseMatricula;
            final List<String> servidores = lstServidorQuery.executarLista();

            if (servidores.size() == 1) {
                // Passo comum para ambas situações, tanto para acao igual a D ou igual T
                // Pega o rseCodigo do servidor
                final String rseCodigo = servidores.get(0).toString();
                //Seta o servidor para ter rse_prazo null
                final RegistroServidorTO rseFrom = servidorController.findRegistroServidor(rseCodigo, responsavel);

                // Recupera as autorizações do servidor.
                final ListaConsignacaoTransferenciaQuery lstConsignacaoQuery = new ListaConsignacaoTransferenciaQuery();
                lstConsignacaoQuery.rseCodigo = rseCodigo;
                lstConsignacaoQuery.adeNumero = adeNumero;
                final List<String> ade = lstConsignacaoQuery.executarLista();

                if ("T".equalsIgnoreCase(acao)) {
                    // Caso a situação seja igual a T (Transferido)
                    // pesquisa o servidor novo e verifica se ele existe.
                    // Caso o novo servidor exista transfere os contratos para o novo
                    if ((novaRseMatricula != null) && !"".equals(novaRseMatricula)) {

                        //DESENV-15793 - Não permite trasnferêcias caso matricula de destino atenda ao pattern e origem não
                        final String regexLimitadorTrans = (String) ParamSist.getInstance().getParam(CodedValues.TPC_REGEX_LIMITACAO_MATRICULA_TRANSFERENCIA_SERVIDOR, responsavel);

                        if (!TextHelper.isNull(regexLimitadorTrans)) {
                            final Pattern pattern = Pattern.compile(regexLimitadorTrans, Pattern.CANON_EQ);

                            if (!pattern.matcher(rseMatricula).find() && pattern.matcher(novaRseMatricula).find()) {
                                return ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.transferencia.margem.destino", responsavel);
                            }
                        }

                        lstServidorQuery.estIdentificador = novoEstabelecimento;
                        lstServidorQuery.orgIdentificador = novoOrgao;
                        lstServidorQuery.rseMatricula = novaRseMatricula;
                        final List<String> novosServidores = lstServidorQuery.executarLista();

                        if (novosServidores.isEmpty()) {
                            if ((novoOrgao != null) && !"".equals(novoOrgao) && (novoEstabelecimento != null) && !"".equals(novoEstabelecimento)) {
                                try {
                                    LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.novo.servidor.nao.existe", responsavel, rseCodigo));

                                    // Busca parametro para ver se deve criar ou nao servidor transferido
                                    final String permiteCriarServTransf = (String) ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_CRIAR_SERVIDOR_TRANSFERIDO, responsavel);
                                    if ((permiteCriarServTransf == null) || CodedValues.TPC_SIM.equals(permiteCriarServTransf)) {

                                        // Busca os dados do registro servidor antigo para
                                        // setar as informações no novo registro servidor
                                        final RegistroServidorTO oldRseTo = servidorController.findRegistroServidor(rseCodigo, responsavel);

                                        // Busca o código do novo órgão para criar o novo registro servidor
                                        final EstabelecimentoTransferObject estabelecimento = consignanteController.findEstabelecimentoByIdn(novoEstabelecimento, responsavel);
                                        final OrgaoTransferObject orgao = consignanteController.findOrgaoByIdn(novoOrgao, estabelecimento.getEstCodigo(), responsavel);

                                        // Os dados do novo registro servidor serão iguais ao antigo, com exceção
                                        // da matricula, órgão, status, tipo e da primary key
                                        final RegistroServidorTO newRseTo = new RegistroServidorTO();
                                        newRseTo.setAtributos(oldRseTo.getAtributos());
                                        newRseTo.setRseMatricula(novaRseMatricula);
                                        newRseTo.setOrgCodigo(orgao.getOrgCodigo());
                                        newRseTo.setSrsCodigo(CodedValues.SRS_ATIVO);
                                        newRseTo.setRseTipo(null);
                                        newRseTo.setRseDataCarga(new Timestamp(Calendar.getInstance().getTimeInMillis()));

                                        // Cria o novo registro servidor
                                        final String newRseCodigo = servidorController.createRegistroServidor(newRseTo, responsavel);
                                        newRseTo.setAttribute(Columns.RSE_CODIGO, newRseCodigo);
                                        criouServidorDestino = true;

                                        // Copia os registros de margem registro servidor, caso existam
                                        MargemRegistroServidorHome.copy(rseCodigo, newRseCodigo);

                                        if (!ParamSist.paramEquals(CodedValues.TPC_IMPORTA_TRANSFERIDOS_SEM_CADASTRO_MARGEM, CodedValues.TPC_SIM, responsavel)) {
                                            // Atualiza os dados do antigo, excluindo o registro servidor, e zerando as suas margens.
                                            // Não realiza a exclusão caso o parâmetro TPC_IMPORTA_TRANSFERIDOS_SEM_CADASTRO_MARGEM esteja habilitado
                                            // pois será feito na própria rotina específica
                                            oldRseTo.setSrsCodigo(CodedValues.SRS_EXCLUIDO);
                                            oldRseTo.setRseMargem(BigDecimal.valueOf(0));
                                            oldRseTo.setRseMargem2(BigDecimal.valueOf(0));
                                            oldRseTo.setRseMargem3(BigDecimal.valueOf(0));
                                            servidorController.updateRegistroServidorSemHistoricoMargem(oldRseTo, true, responsavel);

                                            // Registra ocorrência de exclusão pela carga de margem
                                            servidorController.criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_EXCLUSAO_POR_CARGA_MARGEM, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.exclusao.carga.margem", responsavel), null, responsavel);
                                        }

                                        // Coloca o novo registro servidor atualizado na lista de servidores
                                        novosServidores.add(newRseCodigo);

                                    }
                                } catch (ConsignanteControllerException | ServidorControllerException ex) {
                                    return ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.impossivel.transferir.servidor.arg0", responsavel, ex.getMessage());
                                }
                            } else {
                                return ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.informacoes.insuficientes.para.transferencia", responsavel);
                            }
                        }

                        if (novosServidores.size() == 1) {
                            final String rseCodigoNovo = novosServidores.get(0);

                            // verifica se o novo servidor possui cpf diferente
                            if (bloqTransfSerCpfDiferente) {
                                // busca dados do servidor de destino
                                final RegistroServidorTO rseDestinoTO = servidorController.findRegistroServidor(rseCodigoNovo, responsavel);
                                if (!rseDestinoTO.getSerCodigo().equals(rseFrom.getSerCodigo())) {
                                    final ServidorTransferObject serDestinoTO = servidorController.findServidor(rseDestinoTO.getSerCodigo(), responsavel);
                                    final ServidorTransferObject serOrigemTO = servidorController.findServidor(rseFrom.getSerCodigo(), responsavel);
                                    if (!serDestinoTO.getSerCpf().equals(serOrigemTO.getSerCpf())) {
                                        // grava crítica de CPFs diferentes
                                        return ApplicationResourcesHelper.getMessage("mensagem.erro.consignacao.arg0.nao.pode.ser.transferida.novo.servidor.cpf.diferente", responsavel, adeNumero);
                                    }
                                }
                            }

                            // Se o servidor antigo tinha contratos, transfere-os para o novo servidor
                            if ((ade != null) && !ade.isEmpty()) {
                                try {
                                    final List<String> criticasTransf = transferirConsignacaoController.transfereAde(ade, rseCodigoNovo, rseCodigo, rseMatricula, orgIdentificador, true, existeSvcSemTransf || bloqTransfSerBloqCnvSvc, responsavel);
                                    if ((criticasTransf != null) && !criticasTransf.isEmpty()) {
                                        // Se tem alguma crítica, adiciona às mensagens de erro
                                        criticas.addAll(criticasTransf);
                                    } else // Se não tem críticas, significa que todos os contratos foram transferidos.
                                    // Verifica então se na transferência, os contratos foram migrados para órgãos
                                    // com retorno já processado (somente se é processamento por órgão/estabelecimento).
                                    if ((tipoEntidade != null) && !"CSE".equalsIgnoreCase(tipoEntidade)) {
                                        final ObtemTotalParcelasTransfSemRetornoQuery prdSemRetQuery = new ObtemTotalParcelasTransfSemRetornoQuery();
                                        prdSemRetQuery.adeCodigos = ade;
                                        final int qtd = prdSemRetQuery.executarContador();
                                        if (qtd > 0) {
                                            criticas.add(ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.atencao.contratos.transferidos.para.orgao.com.processamento.retorno.finalizado", responsavel));
                                        }
                                    }
                                } catch (final AutorizacaoControllerException ex) {
                                    LOG.error(ex.getMessage(), ex);
                                    return ex.getMessage();
                                }

                                if (permiteTransfOutrosOrgaos && (rseCodigosCalcMargem != null)) {
                                    // Verifica se o servidor, seja origem (rseCodigo) ou destino (rseCodigoNovo), da transferência de
                                    // contratos é de ORG/EST que não compõe o processamento atual. Caso sejam, eles devem ter a margem
                                    // recalculada, para evitar que fiquem inconsistentes.
                                    final List<String> rseCodigoCandidatos = new ArrayList<>();
                                    rseCodigoCandidatos.add(rseCodigo);
                                    rseCodigoCandidatos.add(rseCodigoNovo);

                                    final ObtemServidorNaoPertenceEntidadeQuery naoPertenceQuery = new ObtemServidorNaoPertenceEntidadeQuery();
                                    naoPertenceQuery.tipoEntidade = tipoEntidade;
                                    naoPertenceQuery.codigoEntidade = codigoEntidade;
                                    naoPertenceQuery.rseCodigo = rseCodigoCandidatos;

                                    // Adiciona na lista para cálculo de margem
                                    rseCodigosCalcMargem.addAll(naoPertenceQuery.executarLista());
                                }
                            }

                            // Copia os bloqueios de convênios do servidor antigo para o novo (caso em que o servidor ganha um novo RSE_CODIGO)
                            if (temBloqueioCnv) {
                                parametroController.copiaBloqueioCnv(rseCodigoNovo, rseCodigo, responsavel);
                            }
                            // Copia os bloqueios dos serviços do registro antigo do servidor para o novo
                            if (temBloqueioSvc) {
                                parametroController.copiaBloqueioSvc(rseCodigoNovo, rseCodigo, responsavel);
                            }
                            // Copia os bloqueios das naturezas de serviço do registro antigo do servidor para o novo
                            if (temBloqueioNse) {
                                parametroController.copiaBloqueioNse(rseCodigoNovo, rseCodigo, responsavel);
                            }
                            // Copia os bloqueios das consignatarias do registro antigo do servidor para o novo
                            if (temBloqueioCsa) {
                                parametroController.copiaBloqueioCsa(rseCodigoNovo, rseCodigo, responsavel);
                            }
                            /* FIM DA COPIA DOS BLOQUEIOS */

                            // Se tem módulo SDP, move o permissionário, caso exista para o registro servidor origem
                            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_SDP, CodedValues.TPC_SIM, responsavel)) {
                                permissionarioController.movePermissionario(rseCodigo, rseCodigoNovo, responsavel);
                            }

                            // DESENV-8774 : Se importa transferidos em sistema não possui processamento de margem (eConsigLight)
                            // implementa os controles necessários sobre o status dos serviores de origem e destino
                            if (ParamSist.paramEquals(CodedValues.TPC_IMPORTA_TRANSFERIDOS_SEM_CADASTRO_MARGEM, CodedValues.TPC_SIM, responsavel)) {
                                atualizarStatusTransferenciaServidor(rseCodigo, rseCodigoNovo, criouServidorDestino, entrada, responsavel);
                            }

                            //DESENV-15607: Exército - Criar a transferência de contracheques
                            final String importadorContrachequeClassName = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CLASSE_IMPORTADOR_CONTRACHEQUES, responsavel);
                            if (!TextHelper.isNull(importadorContrachequeClassName)) {
                                transfereContraChequeRegistroServidor(rseCodigoNovo, rseCodigo, responsavel);
                            }

                            // Grava a ocorrência de relacionamento para a transferência de servidor.
                            //createRelRegistroServidor(rseCodigo, rseCodigoNovo, CodedValues.TNT_RELACIONAMENTO_REGISTRO_SERVIDOR, responsavel.getUsuCodigo(), DateHelper.getSystemDatetime(), responsavel);
                            servidorDao.createRelRegistroServidor(rseCodigo, rseCodigoNovo, CodedValues.TNT_RELACIONAMENTO_REGISTRO_SERVIDOR, responsavel);

                        } else if (novosServidores.size() > 1) {
                            return ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.multiplos.servidores.encontrados.transferencia", responsavel);
                        } else {
                            return ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.nenhum.servidor.encontrado.transferencia", responsavel);
                        }
                    } else {
                        return ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.informacoes.insuficientes.para.transferencia", responsavel);
                    }

                } else if ("D".equalsIgnoreCase(acao)) {
                    // Se excluido (D), faz a pesquisa das autorizações (ade).
                    // Liquida todas as Ades pendentes, insere uma ocorrencia
                    // e muda o tipo de servidor para EXCLUIDO
                    for (final String adeCodigo : ade) {
                        autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.cadMargem.ocorrencia.ade.servidor.desligado", responsavel), responsavel);
                    }

                    // Muda o tipo do servidor para EXCLUIDO
                    final RegistroServidorTO rseTo = servidorController.findRegistroServidor(rseCodigo, responsavel);
                    rseTo.setSrsCodigo(CodedValues.SRS_EXCLUIDO);
                    rseTo.setRseMargem(BigDecimal.valueOf(0));
                    rseTo.setRseMargem2(BigDecimal.valueOf(0));
                    rseTo.setRseMargem3(BigDecimal.valueOf(0));
                    servidorController.updateRegistroServidorSemHistoricoMargem(rseTo, true, responsavel);

                    // Registra ocorrência de exclusão pela carga de margem
                    servidorController.criaOcorrenciaRSE(rseCodigo, CodedValues.TOC_RSE_EXCLUSAO_POR_CARGA_MARGEM, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.exclusao.carga.margem", responsavel), null, responsavel);
                }

                // Se nao gerou nenhuma critica que fizesse sair do loop, atualiza a rse_data_carga do servidor para a data atual
                final RegistroServidorTO rseFrom2 = new RegistroServidorTO(rseFrom.getRseCodigo());
                rseFrom2.setRseDataCarga(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
                servidorController.updateRegistroServidorSemHistoricoMargem(rseFrom2, responsavel);
            } else if (servidores.size() > 1) {
                return ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.multiplos.servidores.encontrados", responsavel);
            } else {
                return ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.nenhum.servidor.encontrado", responsavel);
            }
        } else {
            return ApplicationResourcesHelper.getMessage("mensagem.cadMargem.critica.informacoes.insuficientes.para.pesquisa", responsavel);
        }

        return TextHelper.join(criticas, "|");
    }

    private void atualizarStatusTransferenciaServidor(String rseCodigoOrigem, String rseCodigoDestino, boolean criouServidorDestino, Map<String, Object> entrada, AcessoSistema responsavel) throws ServidorControllerException {
        final RegistroServidorTO rseOrigem = servidorController.findRegistroServidor(rseCodigoOrigem, responsavel);
        final RegistroServidorTO rseDestino = servidorController.findRegistroServidor(rseCodigoDestino, responsavel);

        boolean excluiuServidorOrigem = false;
        boolean reativouServidorDestino = false;

        // Copiar o status do registro servidor origem para o registro servidor destino, quando este é criado pela transferência,
        // caso esteja no status "bloqueado" (srs_codigo = '2') ou "pendente" (srs_codigo = '5').
        if (criouServidorDestino && (CodedValues.SRS_BLOQUEADOS.contains(rseOrigem.getSrsCodigo()) || CodedValues.SRS_PENDENTE.equals(rseOrigem.getSrsCodigo()))) {
            rseDestino.setSrsCodigo(rseOrigem.getSrsCodigo());
        }

        // Alterar o status do registro servidor origem para "excluído" (srs_codigo = '3').
        if (!CodedValues.SRS_INATIVOS.contains(rseOrigem.getSrsCodigo())) {
            rseOrigem.setSrsCodigo(CodedValues.SRS_EXCLUIDO);
            excluiuServidorOrigem = true;
        }

        // Alterar o status do registro servidor destino para "ativo" (srs_codigo = '1'), caso esteja como excluído (srs_codigo = '3').
        if (CodedValues.SRS_EXCLUIDO.equals(rseDestino.getSrsCodigo())) {
            rseDestino.setSrsCodigo(CodedValues.SRS_ATIVO);
            reativouServidorDestino = true;
        }

        // Caso na exclusão do servidor de origem os campos "data de saída" e "data de último salário" sejam informados no arquivo,
        // estes deverão ser lidos e atualizados no cadastro do servidor, independente do parâmetro 546 estar habilitado.
        final String dataSaida = (String) entrada.get("RSE_DATA_SAIDA");
        final String dataUltSalario = (String) entrada.get("RSE_DATA_ULT_SALARIO");

        if (!TextHelper.isNull(dataSaida)) {
            try {
                rseOrigem.setRseDataSaida(DateHelper.parse(dataSaida, "yyyy-MM-dd"));
            } catch (final ParseException ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.tradutor.campo.formato.incorreto.para.entrada", responsavel, "RSE_DATA_SAIDA", dataSaida));
            }
        }
        if (!TextHelper.isNull(dataUltSalario)) {
            try {
                rseOrigem.setRseDataUltSalario(DateHelper.parse(dataUltSalario, "yyyy-MM-dd"));
            } catch (final ParseException ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.tradutor.campo.formato.incorreto.para.entrada", responsavel, "RSE_DATA_ULT_SALARIO", dataUltSalario));
            }
        }

        // Caso o registro servidor destino seja criado no status "bloqueado" (srs_codigo = '2'), copiar do registro servidor origem
        // as datas de saída (RSE_DATA_SAIDA), último salário (RSE_DATA_ULT_SALARIO) e retorno (RSE_DATA_RETORNO).
        if (CodedValues.SRS_BLOQUEADOS.contains(rseDestino.getSrsCodigo())) {
            if (rseOrigem.getRseDataSaida() != null) {
                rseDestino.setRseDataSaida(rseOrigem.getRseDataSaida());
            }
            if (rseOrigem.getRseDataUltSalario() != null) {
                rseDestino.setRseDataUltSalario(rseOrigem.getRseDataUltSalario());
            }
            if (rseOrigem.getRseDataRetorno() != null) {
                rseDestino.setRseDataRetorno(rseOrigem.getRseDataRetorno());
            }
        }

        if (criouServidorDestino) {
            if (CodedValues.SRS_ATIVO.equals(rseDestino.getSrsCodigo())) {
                // Caso o registro servidor destino seja incluído (srs_codigo = '1') pela rotina de transferência incluir ocorrência do tipo "Inclusão por arquivo de transferidos".
                servidorController.criaOcorrenciaRSE(rseCodigoDestino, CodedValues.TOC_RSE_INCLUSAO_POR_CARGA_TRANSFERIDOS, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.inclusao.carga.transferidos", responsavel), null, responsavel);
            } else {
                // Caso o registro servidor destino seja incluído com status diferente de ativo (srs_codigo != '1') pela rotina de transferência incluir ocorrência do tipo "Alteração por arquivo de transferidos".
                servidorController.criaOcorrenciaRSE(rseCodigoDestino, CodedValues.TOC_RSE_ALTERACAO_POR_CARGA_TRANSFERIDOS, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.alteracao.carga.transferidos", responsavel), null, responsavel);
            }
        } else if (reativouServidorDestino && CodedValues.SRS_ATIVO.equals(rseDestino.getSrsCodigo())) {
            // Caso o registro servidor destino existente tenha seu status alterado para ativo (srs_codigo = '1') pela rotina de transferência incluir ocorrência do tipo "Reativação por arquivo de transferidos".
            servidorController.criaOcorrenciaRSE(rseCodigoDestino, CodedValues.TOC_RSE_REATIVACAO_POR_CARGA_TRANSFERIDOS, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.reativacao.carga.transferidos", responsavel), null, responsavel);
        }

        // Caso o registro servidor origem seja excluído (srs_codigo = '3') pela rotina de transferência incluir ocorrência do tipo "Exclusão por arquivo de transferidos".
        if (excluiuServidorOrigem && CodedValues.SRS_EXCLUIDO.equals(rseOrigem.getSrsCodigo())) {
            servidorController.criaOcorrenciaRSE(rseCodigoOrigem, CodedValues.TOC_RSE_EXCLUSAO_POR_CARGA_TRANSFERIDOS, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ors.obs.exclusao.carga.transferidos", responsavel), null, responsavel);
        }

        // Procede as alterações
        servidorController.updateRegistroServidorSemHistoricoMargem(rseOrigem, true, responsavel);
        servidorController.updateRegistroServidorSemHistoricoMargem(rseDestino, true, responsavel);
    }

    private void adicionaCritica(List<String> critica, LeitorArquivoTexto leitor, String mensagem) {
        critica.add(leitor.getLinha() + formataMsgErro(mensagem, HistoricoHelper.COMPLEMENTO_DEFAULT, HistoricoHelper.TAMANHO_MSG_ERRO_DEFAULT, true));
    }

    private String formataMsgErro(String mensagem, String complemento, int tamanho, boolean alinhaEsquerda) {
        return TextHelper.removeAccent(TextHelper.formataMensagem(mensagem, complemento, tamanho, alinhaEsquerda)).toUpperCase();
    }

    /**
     * Verifica se a quantidade de linhas do arquivo de transferidos está acima do permitido,
     * de acordo com os parâmetros de validação de arquivo.
     * @param nomeArquivo : nome do arquivo de transferidos, com o caminho completo
     * @param tipoEntidade : tipo de entidade que realiza o processamento
     * @param codigoEntidade : código da entidade que realiza o processamento
     * @param responsavel : responsável pelo processamento
     * @return : True se a quantidade de linhas estivar acima, ou False caso contrário
     * @throws ServidorControllerException
     */
    @Override
    public boolean qtdLinhasArqTransferidosAcimaPermitido(String nomeArquivo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ServidorControllerException {
        try {
            final List<String> tvaCodigos = new ArrayList<>();
            tvaCodigos.add(TipoParamValidacaoArqEnum.TRANSFERIDOS_PERCENTUAL_QTD_RSE_ATIVOS_QTD_LINHAS.getCodigo());
            final Map<String, String> paramValidacaoArq = validaImportacaoController.lstParamValidacaoArq(tipoEntidade, codigoEntidade, tvaCodigos, null, responsavel);

            final String paramVlr = paramValidacaoArq.get("transferidos" + TipoParamValidacaoArqEnum.TRANSFERIDOS_PERCENTUAL_QTD_RSE_ATIVOS_QTD_LINHAS.getChave());
            if (!TextHelper.isNull(paramVlr) && TextHelper.isDecimalNum(paramVlr)) {
                final double percentual = Double.parseDouble(paramVlr);
                if (percentual > 0) {
                    // Obtém a quantidade de linhas do arquivo
                    final int qtdLinhas = FileHelper.getNumberOfLines(nomeArquivo);

                    // Obtém a quantidade de registros servidores ativos
                    final ObtemQtdRegistroServidorAtivoQuery query = new ObtemQtdRegistroServidorAtivoQuery();
                    query.tipoEntidade = tipoEntidade;
                    query.codigoEntidade = codigoEntidade;
                    final int qtdRseAtivos = query.executarContador();

                    if (qtdLinhas > Math.round(qtdRseAtivos * percentual)) {
                        return true;
                    }
                }
            }
        } catch (ValidaImportacaoControllerException | HQueryException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        return false;
    }

    private void transfereContraChequeRegistroServidor(String rseCodigoNovo, String rseCodigo, AcessoSistema responsavel) throws ZetraException {
        try {
            final ListaContrachequeServidorDestinoQuery query = new ListaContrachequeServidorDestinoQuery();
            query.rseCodigo = rseCodigo;
            query.rseCodigoNovo = rseCodigoNovo;

            final List<TransferObject> resultContrachequeServidorDestinoQuery = query.executarDTO();
            if ((resultContrachequeServidorDestinoQuery != null) && !resultContrachequeServidorDestinoQuery.isEmpty()) {
                for (final TransferObject contraCheque : resultContrachequeServidorDestinoQuery) {
                    final Date ccqPeriodo = (Date) contraCheque.getAttribute(Columns.CCQ_PERIODO);
                    final String ccqTexto = (String) contraCheque.getAttribute(Columns.CCQ_TEXTO);
                    ContrachequeRegistroServidorHome.create(rseCodigoNovo, ccqPeriodo, ccqTexto);
                }
            }
        } catch (HQueryException | CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException(ex);
        }
    }

    private void iniciaBloqueioRseVariacaoMargemLimiteDefinidoCSA(MargemDAO margemDao, boolean margemTotal, AcessoSistema responsavel) throws Exception {
        // Bloqueia/Desbloqueia os registros servidores que tem ou não contratos com a consignatária e a mesma configurou um limite de variação
        margemDao.bloqueiaVariacaoMargemLimiteDefinidoCSA(margemTotal, responsavel);

        LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.gera.relatorio.varicao.importacao.margem.inicio.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

        // Gera arquivo com os servidores bloqueados por variação de margem.
        final String absolutePath = ParamSist.getDiretorioRaizArquivos();
        final String pathArquivoServidorVariavel = absolutePath + File.separatorChar + "relatorio" + File.separatorChar + "cse" + File.separatorChar + "integracao";
        final String nomeArqConfVariacaoEntrada = absolutePath + File.separatorChar + "conf" + File.separatorChar + "relatorio_rse_cnv_bloq_variacao_csa_entrada.xml";
        final String nomeArqConfVariacaoTradutor = absolutePath + File.separatorChar + "conf" + File.separatorChar + "relatorio_rse_cnv_bloq_variacao_csa_tradutor.xml";
        final String nomeArqConfVariacaoSaida = absolutePath + File.separatorChar + "conf" + File.separatorChar + "relatorio_rse_cnv_bloq_variacao_csa_saida.xml";

        final String dataArquivo = DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss");
        final String nomeArquivoFinal = pathArquivoServidorVariavel + File.separatorChar + "relacao_matriculas_variacao_margem_" + dataArquivo + ".csv";

        final File diretorioRelatorio = new File(pathArquivoServidorVariavel);
        if (!diretorioRelatorio.exists() && !diretorioRelatorio.mkdirs()) {
            throw new RelatorioControllerException("mensagem.erro.diretorio.nao.existe", responsavel, pathArquivoServidorVariavel);
        }

        final EscritorArquivoTexto escritorArquivoTexto = new EscritorArquivoTexto(nomeArqConfVariacaoSaida, nomeArquivoFinal);
        final Leitor leitor = new LeitorBaseDeDados(nomeArqConfVariacaoEntrada, margemDao.montaQueryListaBloqVarMargemCsa(responsavel));
        final Tradutor tradutor = new Tradutor(nomeArqConfVariacaoTradutor, leitor, escritorArquivoTexto);
        tradutor.traduz();

        final File arqRelacao = new File(nomeArquivoFinal);
        if (arqRelacao.exists() && (arqRelacao.length() != 0)) {
            final Consignante cse = ConsignanteHome.findByPrimaryKey(CodedValues.CSE_CODIGO_SISTEMA);
            final String cseEmail = cse.getCseEmail();
            if (!TextHelper.isNull(cseEmail)) {
                try {
                    EnviaEmailHelper.notificaCseArqRelacaoBloqRseCnvVariacaoCsa(cseEmail, responsavel);
                } catch (ViewHelperException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        } else if (arqRelacao.exists()) {
            FileHelper.delete(nomeArquivoFinal);
        }

        final List<TransferObject> lstCsaQntVerbasBloqSer = margemDao.lstCsaQntdaVerbaBloqLimiteVariacaoMargem(responsavel);
        if ((lstCsaQntVerbasBloqSer != null) && !lstCsaQntVerbasBloqSer.isEmpty()) {
            String csaCodigoAnterior = null;
            String csaNome = null;
            String verba = null;
            Long quantidade = null;

            final StringBuilder corpoEmailQntVerbaCsa = new StringBuilder();

            for (final TransferObject csaQntVerbaBloqSer : lstCsaQntVerbasBloqSer) {
                final String csaCodigo = (String) csaQntVerbaBloqSer.getAttribute("CSA_COD");
                final String tpaEmail = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_EMAIL_CSA_NOTIFICACAO_BLOQUEIO_VARIACAO_MARGEM, responsavel);

                if (TextHelper.isNull(tpaEmail)) {
                    LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.desbloqueio.convenio.limite.csa.margem.complemento.email.erro", responsavel, csaNome));
                    continue;
                }

                if (!TextHelper.isNull(csaCodigoAnterior) && !csaCodigo.equals(csaCodigoAnterior)) {
                    EnviaEmailHelper.notificaCsaQuantidadeServidorVerbaBloq(tpaEmail.replace(";", ","), csaNome, corpoEmailQntVerbaCsa.toString(), responsavel);
                    corpoEmailQntVerbaCsa.setLength(0);
                }

                csaCodigoAnterior = csaCodigo;
                csaNome = (String) csaQntVerbaBloqSer.getAttribute("CSA_NOME");
                verba = (String) csaQntVerbaBloqSer.getAttribute("COD_VERBA");
                quantidade = (Long) csaQntVerbaBloqSer.getAttribute("QUANTIDADE");
                corpoEmailQntVerbaCsa.append(verba).append(": ").append(quantidade).append("<br>");
            }

            if (!corpoEmailQntVerbaCsa.isEmpty()) {
                final String tpaEmail = parametroController.getParamCsa(csaCodigoAnterior, CodedValues.TPA_EMAIL_CSA_NOTIFICACAO_BLOQUEIO_VARIACAO_MARGEM, responsavel);
                if (TextHelper.isNull(tpaEmail)) {
                    LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.desbloqueio.convenio.limite.csa.margem.complemento.email.erro", responsavel, csaNome));
                } else {
                    EnviaEmailHelper.notificaCsaQuantidadeServidorVerbaBloq(tpaEmail.replace(";", ","), csaNome, corpoEmailQntVerbaCsa.toString(), responsavel);
                }
            }
        }

        LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cadMargem.gera.relatorio.varicao.importacao.margem.fim.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
    }
}
