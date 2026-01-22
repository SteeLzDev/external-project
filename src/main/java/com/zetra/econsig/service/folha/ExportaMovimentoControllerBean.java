package com.zetra.econsig.service.folha;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AgendamentoControllerException;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametrosException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.folha.exportacao.ExportaMovimento;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoFactory;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.folha.ExportaMovimentoHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.BatchManager;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.dao.AutorizacaoDAO;
import com.zetra.econsig.persistence.dao.CalendarioFolhaDAO;
import com.zetra.econsig.persistence.dao.ControleSaldoDvExpMovimentoDAO;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.HistoricoIntegracaoDAO;
import com.zetra.econsig.persistence.dao.HistoricoMargemDAO;
import com.zetra.econsig.persistence.dao.ParcelaDescontoDAO;
import com.zetra.econsig.persistence.entity.CalendarioFolhaOrg;
import com.zetra.econsig.persistence.entity.CalendarioFolhaOrgHome;
import com.zetra.econsig.persistence.entity.HistoricoExportacao;
import com.zetra.econsig.persistence.entity.HistoricoExportacaoHome;
import com.zetra.econsig.persistence.query.anexo.ListaAnexoAdeMovFinQuery;
import com.zetra.econsig.persistence.query.consignante.ListaCodigoFolhaQuery;
import com.zetra.econsig.persistence.query.convenio.ListaCodigoVerbaFeriasInvalidoQuery;
import com.zetra.econsig.persistence.query.convenio.ListaCodigoVerbaInvalidoQuery;
import com.zetra.econsig.persistence.query.margem.ListaContratosIncideMargemNulaQuery;
import com.zetra.econsig.persistence.query.movimento.ListaArquivoMovFinDownloadNaoRealizadoQuery;
import com.zetra.econsig.persistence.query.movimento.ListaMovimentoFinanceiroQuery;
import com.zetra.econsig.persistence.query.movimento.ListaOrgaoExpMovQuery;
import com.zetra.econsig.persistence.query.movimento.ListaResumoExportacaoQuery;
import com.zetra.econsig.persistence.query.parcela.ListaParcelasDuplicadasQuery;
import com.zetra.econsig.persistence.query.parcela.ListaParcelasForaPeriodoQuery;
import com.zetra.econsig.persistence.query.periodo.ListaPeriodoExportacaoDataFinalInvalidaQuery;
import com.zetra.econsig.persistence.query.periodo.ListaPeriodoExportacaoQuery;
import com.zetra.econsig.service.agendamento.AgendamentoController;
import com.zetra.econsig.service.consignacao.AlterarConsignacaoController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.CancelarConsignacaoController;
import com.zetra.econsig.service.consignacao.DownloadAnexoContratoController;
import com.zetra.econsig.service.consignacao.ReimplantarConsignacaoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.AgendamentoEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoHistoricoMargemEnum;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;

/**
 * <p>Title: ExportaMovimentoControllerBean</p>
 * <p>Description: Session Façade para Rotina de Exportação de Movimento</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
* $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ExportaMovimentoControllerBean implements ExportaMovimentoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExportaMovimentoControllerBean.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private AlterarConsignacaoController alterarConsignacaoController;

    @Autowired
    private CancelarConsignacaoController cancelarConsignacaoController;

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private AgendamentoController agendamentoController;

    @Autowired
    private ReimplantarConsignacaoController reimplantarConsignacaoController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private PeriodoController periodoController;

    @Autowired
    private MargemController margemController;

    @Autowired
    private DownloadAnexoContratoController downloadAnexoContratoController;

    @Override
    public void criarTabelasExportacaoMovFin(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            ExportaMovimento exportador = null;

            // Busca códigos de tipos de dados de autorização que serão incluídos na tabela de exportação
            final List<TransferObject> lstTipoDadoAdicional = autorizacaoController.lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.EXPORTA, VisibilidadeTipoDadoAdicionalEnum.HOST_A_HOST_LOTE_WEB, null, null, responsavel);

            final String exportadorClassName = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CLASSE_EXPORTADOR, responsavel);
            if (!TextHelper.isNull(exportadorClassName)) {
                exportador = ExportaMovimentoFactory.getExportador(exportadorClassName);
            }

            if (exportador != null) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.exportador.preCriacaoTabelas.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                exportador.preCriacaoTabelas(parametrosExportacao, responsavel);
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.exportador.preCriacaoTabelas.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cria.tabelas.exportacao.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            final HistoricoIntegracaoDAO hisDAO = DAOFactory.getDAOFactory().getHistoricoIntegracaoDAO();
            hisDAO.criarTabelasExportacaoMovFin(lstTipoDadoAdicional);
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cria.tabelas.exportacao.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

            if (exportador != null) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.exportador.posCriacaoTabelas.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                exportador.posCriacaoTabelas(parametrosExportacao, responsavel);
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.exportador.posCriacaoTabelas.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }
        } catch (ExportaMovimentoException | AutorizacaoControllerException | DAOException ex) {
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Executa validações referente ao movimento financeiro: Período, parcelas, códigos de verba.
     * @param orgCodigos  : No caso de exportação por órgão, informa a lista de órgãos
     * @param estCodigos  : No caso de exportação por estabelecimento, informa a lista de estabelecimentos
     * @param responsavel : Usuário responsável pela operação
     * @throws ConsignanteControllerException
     */
    @Override
    public void validarExportacaoMovimento(List<String> orgCodigos, List<String> estCodigos, boolean tipoRetornoIntegracao, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            // Valida a data final do Período de Exportação
            final ListaPeriodoExportacaoDataFinalInvalidaQuery query = new ListaPeriodoExportacaoDataFinalInvalidaQuery(orgCodigos, estCodigos);
            final List<TransferObject> periodoInvalidoList = query.executarDTO();
            if (periodoInvalidoList != null && periodoInvalidoList.size() > 0) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.folha.exportacao.valida.periodo.erro", responsavel));
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.folha.exportacao.orgao.data.periodo.invalida", responsavel));
                for (final TransferObject next : periodoInvalidoList) {
                    LOG.error(next.getAttribute("ORGAO") + ": " + next.getAttribute("DATA_FIM") + " - " + next.getAttribute("DATA_FIM_LIMITE"));
                }

                throw new ConsignanteControllerException("mensagem.erro.folha.exportacao.periodo.invalido.verificar.datas", responsavel);
            } else {
                LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.folha.exportacao.valida.periodo.ok", responsavel));
            }

            // Verifica se existem parcelas em processamento de período diferente do atual
            if (!ParamSist.paramEquals(CodedValues.TPC_PERMITE_DOIS_PERIODOS_EXPORTACAO_ABERTOS, CodedValues.TPC_SIM, responsavel)) {
                final List<TransferObject> parcelasInvalidasList = new ListaParcelasForaPeriodoQuery(orgCodigos, estCodigos).executarDTO();
                if (parcelasInvalidasList != null && parcelasInvalidasList.size() > 0) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.folha.exportacao.valida.parcela.fora.periodo.erro", responsavel));
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.folha.exportacao.parcela.em.processamento.periodo.qtd", responsavel));
                    for (final TransferObject next : parcelasInvalidasList) {
                        LOG.error(next.getAttribute("PERIODO") + " : " + next.getAttribute("QTD"));
                    }

                    throw new ConsignanteControllerException("mensagem.erro.folha.exportacao.periodo.invalido.verificar.parcelas.em.processamento", responsavel);
                } else {
                    LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.folha.exportacao.valida.parcela.fora.periodo.ok", responsavel));
                }
            }

            // Verifica se existem parcelas duplicadas do período atual com alguma parcela histórica
            final List<TransferObject> parcelasDuplicadasList = new ListaParcelasDuplicadasQuery(orgCodigos, estCodigos).executarDTO();
            if (parcelasDuplicadasList != null && parcelasDuplicadasList.size() > 0) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.folha.exportacao.valida.parcela.duplicadas.erro", responsavel));
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.folha.exportacao.parcela.em.processamento.periodoprd.periodopdp.qtd", responsavel));
                for (final TransferObject next : parcelasDuplicadasList) {
                    LOG.error(next.getAttribute("PERIODO_PRD") + " - " + next.getAttribute("PERIODO_PDP") + " : " + next.getAttribute("QTD"));
                }

                throw new ConsignanteControllerException("mensagem.erro.folha.exportacao.periodo.invalido.verificar.parcelas.em.duplicidade", responsavel);
            } else {
                LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.folha.exportacao.valida.parcela.duplicadas.ok", responsavel));
            }

            // Verifica se existem contratos ativos ligado a códigos de verbas inválidos
            final List<TransferObject> codVerbaInvalidoList = new ListaCodigoVerbaInvalidoQuery(orgCodigos, estCodigos).executarDTO();
            if (codVerbaInvalidoList != null && codVerbaInvalidoList.size() > 0) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.folha.exportacao.verifica.contratos.ativos.sem.cod.verba.erro", responsavel));
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.folha.exportacao.convenios.cod.verbas.invalidos.csa.svc.org.qtd", responsavel));
                for (final TransferObject next : codVerbaInvalidoList) {
                    LOG.error(next.getAttribute("CSA") + " - " + next.getAttribute("SVC") + " - " + next.getAttribute("ORG") + " : " + next.getAttribute("QTD"));
                }

                throw new ConsignanteControllerException("mensagem.erro.folha.exportacao.cad.verbas.invalido.verificar.contratos.ativos", responsavel);
            } else {
                LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.folha.exportacao.verifica.contratos.ativos.sem.cod.verba.ok", responsavel));
            }

            // Verifica se existem códigos de verba de férias utilizados em códigos de verbas normais
            final List<TransferObject> codVerbaFeriasInvalidoList = new ListaCodigoVerbaFeriasInvalidoQuery(orgCodigos, estCodigos).executarDTO();
            if (codVerbaFeriasInvalidoList != null && codVerbaFeriasInvalidoList.size() > 0) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.folha.exportacao.verifica.verbas.ferias.erro", responsavel));
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.folha.exportacao.cod.verbas.ferias.invalidos.csa.svc.org.verbaferias", responsavel));
                for (final TransferObject next : codVerbaFeriasInvalidoList) {
                    LOG.error(next.getAttribute("CSA") + " - " + next.getAttribute("SVC") + " - " + next.getAttribute("ORG") + " : " + next.getAttribute("VERBA_FERIAS"));
                }

                throw new ConsignanteControllerException("mensagem.erro.folha.exportacao.cadastro.verbas.ferias.invalido.verificar.cad.verbas", responsavel);
            } else {
                LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.folha.exportacao.verifica.verbas.ferias.ok", responsavel));
            }

            // Validações não executadas para retorno de integração
            if (!tipoRetornoIntegracao) {
                // Verifica se existem contratos ativos que incidem em uma margem que esteja com valor nulo (margem folha ou restante)
                final List<TransferObject> incideMargemNulaList = new ListaContratosIncideMargemNulaQuery(orgCodigos, estCodigos).executarDTO();
                if (incideMargemNulaList != null && incideMargemNulaList.size() > 0) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.folha.exportacao.verifica.contratos.ativos.incidem.margem.nula.erro", responsavel));
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.folha.exportacao.incide.margem.nula.qtd", responsavel));
                    for (final TransferObject next : incideMargemNulaList) {
                        LOG.error(next.getAttribute(Columns.ADE_INC_MARGEM) + " : " + next.getAttribute("QTD"));
                    }
                    throw new ConsignanteControllerException("mensagem.erro.folha.exportacao.indice.margem.nula.verificar.contratos.ativos", responsavel);
                } else {
                    LOG.info(ApplicationResourcesHelper.getMessage("mensagem.log.info.folha.exportacao.verifica.contratos.ativos.incidem.margem.nula.ok", responsavel));
                }
            }
        } catch (final HQueryException ex) {
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Exporta movimento financeiro.
     *
     * @param orgCodigos  : orgãos a serem exportados
     * @param estCodigos  : estabelecimentos a serem  exportados
     * @param verbas      : verbas a serem exportadas
     * @param acao        : exportar ou reexportar
     * @param opcao       : opção para geração dos arquivos
     * @param periodo     : período a ser exportaçao
     * @param responsavel : usuário que realiza o processo de exportação
     * @return Retorna o nome do arquivo gerado pela exportação
     * @throws ConsignanteControllerException
     */
    @Override
    public String exportaMovimentoFinanceiro(ParametrosExportacao parametrosExportacao, List<String> adeNumeros, AcessoSistema responsavel) throws ConsignanteControllerException {
        ExportaMovimento exportador = null;
        String nomeArqLote = null;
        final Date hieDataIniExp = DateHelper.getSystemDatetime();
        try {
            final List<String> orgCodigos = parametrosExportacao.getOrgCodigos();
            final List<String> estCodigos = parametrosExportacao.getEstCodigos();
            final List<String> verbas = parametrosExportacao.getVerbas();
            final String acao = parametrosExportacao.getAcao();

            if (acao != null && !acao.equals(ParametrosExportacao.AcaoEnum.EXPORTAR.getCodigo()) && !acao.equals(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo())) {
                throw new ConsignanteControllerException("mensagem.erro.acao.nao.suportada", responsavel);
            }

            String tipoEntidade = "CSE";
            List<String> entCodigos = null;
            if (orgCodigos != null && !orgCodigos.isEmpty()) {
                tipoEntidade = "ORG";
                entCodigos = orgCodigos;
            }
            if (estCodigos != null && !estCodigos.isEmpty()) {
                tipoEntidade = "EST";
                entCodigos = estCodigos;
            }

            final ParamSist ps = ParamSist.getInstance();

            // Se o código folha está habilitado, verifica se estão preenchidos, se não aborta o processo
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITAR_EDICAO_CODIGO_FOLHA, CodedValues.TPC_SIM, responsavel)){
                final ListaCodigoFolhaQuery query = new ListaCodigoFolhaQuery();
                final boolean valoresPreenchidos = query.verificarPreenchimento();
                if(!valoresPreenchidos) {
                	throw new ConsignanteControllerException("mensagem.erro.cadastrar.codigo.folha", responsavel);
                }
            }

            final String exportadorClassName = (String) ps.getParam(CodedValues.TPC_CLASSE_EXPORTADOR, responsavel);
            if (exportadorClassName != null && !"".equals(exportadorClassName)) {
                exportador = ExportaMovimentoFactory.getExportador(exportadorClassName);
                if (exportador.sobreporExportaMovimentoFinanceiro(responsavel)) {
                    return exportador.exportaMovimentoFinanceiro(parametrosExportacao, responsavel);
                }
            }

            // Cria os Data Access Objects
            final DAOFactory factory = DAOFactory.getDAOFactory();
            final AutorizacaoDAO autDao = factory.getAutorizacaoDAO();
            final ParcelaDescontoDAO prdDAO = factory.getParcelaDescontoDAO();
            final CalendarioFolhaDAO calDAO = factory.getCalendarioFolhaDAO();
            final HistoricoIntegracaoDAO hisDAO = factory.getHistoricoIntegracaoDAO();

            final boolean exportaInicial = ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, responsavel);

            // Define os períodos de exportação para cada órgão
            // Se ação igual a Exportar, então calcula o novo período
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.setPeriodoExportacao.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            final List<TransferObject> periodoExportacao = periodoController.obtemPeriodoExpMovimento(orgCodigos, estCodigos, true, acao.equals(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo()), responsavel);
            ExportaMovimentoHelper.imprimePeriodoExportacao(periodoExportacao);
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.setPeriodoExportacao.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

            // Executa as pré-validações do movimento financeiro
            validarExportacaoMovimento(orgCodigos, estCodigos, false, responsavel);

            // Cria tabela consolidada do calendário folha, para rotinas na folha quinzenal
            calDAO.consolidarCalendarioFolha(orgCodigos, estCodigos, responsavel);

            // Inicia gravação de histórico de margem
            final HistoricoMargemDAO historicoMargemDAO = factory.getHistoricoMargemDAO();
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.historico.margem.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            historicoMargemDAO.iniciarHistoricoMargem(orgCodigos, estCodigos, null, OperacaoHistoricoMargemEnum.EXPORTACAO_MOV_FIN);
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.historico.margem.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

            // Chama rotinas de controle / correção de saldo devedor
            final ControleSaldoDvExpMovimentoDAO controleSaldoDAO = factory.getControleSaldoDvExpMovimentoDAO();
            // Sequencia na rotina de exportação
            if (!controleSaldoDAO.coeficientesCorrecaoAusentes()) {
                controleSaldoDAO.ajustarSaldoDevedor();
                controleSaldoDAO.corrigirSaldoDevedor();
                controleSaldoDAO.ajustarAdeValor();
                controleSaldoDAO.ajustarValorParcelasAbertas();
            } else {
                throw new ConsignanteControllerException("mensagem.erro.cadastrar.coeficientes.correcao.controle.saldo.devedor", responsavel);
            }

            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_DESCONTO_EM_FILA, CodedValues.TPC_SIM, responsavel)) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.alterarConsignacoesDescontoEmFila.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                alterarConsignacaoController.alterarConsignacoesDescontoEmFila(orgCodigos, estCodigos, responsavel);
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.alterarConsignacoesDescontoEmFila.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            // Pré-processa as autorizações usando o metodo da classe especifica do Gestor
            if (exportador != null) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.exportador.preProcessaAutorizacoes.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                exportador.preProcessaAutorizacoes(parametrosExportacao, responsavel);
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.exportador.preProcessaAutorizacoes.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            // Atualiza os códigos de verba
            convenioController.updateCnvCodVerba();

            // limpa o campo de tipo de motivo de exportação dos contratos para revalidar na próxima exportação a iniciar
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.reinicia.campo.nao.exportacao.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            autDao.limpaMotivoNaoExportacao(responsavel);

            // Se o parametro diz que o cancelamento automático não é diário (de consignações
            // ou de solicitações), então executa rotina de cancelamento para todos os servidores
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cancelaAdeExpiradas.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            final List<String> sad = new ArrayList<>();
            final Object adeExpiradas = ParamSist.getInstance().getParam(CodedValues.TPC_CANC_AUT_DIARIO_CONSIGNACOES, responsavel);
            final Object solExpiradas = ParamSist.getInstance().getParam(CodedValues.TPC_CANC_AUT_DIARIO_SOLICITACOES, responsavel);
            if (adeExpiradas == null || CodedValues.TPC_NAO.equals(adeExpiradas)) {
                sad.add(CodedValues.SAD_AGUARD_CONF);
                sad.add(CodedValues.SAD_AGUARD_DEFER);
            }
            if (solExpiradas == null || CodedValues.TPC_NAO.equals(solExpiradas)) {
                sad.add(CodedValues.SAD_SOLICITADO);
            }
            if (sad.size() > 0) {
                cancelarConsignacaoController.cancelarExpiradas(sad, responsavel);
            }
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.cancelaAdeExpiradas.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

            // Se habilita reimplante com redução de valor de parcela, realiza o reimplante das consignações que foram
            // reimplantadas com redução no período anterior e foram pagas, revertendo a alteração de valor na folha
            if (exportaInicial &&
                ParamSist.paramEquals(CodedValues.TPC_HABILITA_REIMPLANTACAO_COM_REDUCAO_VALOR, CodedValues.TPC_SIM, responsavel)) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.reimplantarConsignacoesValorReduzidoPago.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                reimplantarConsignacaoController.reimplantarConsignacoesValorReduzidoPago(orgCodigos, estCodigos, responsavel);
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.reimplantarConsignacoesValorReduzidoPago.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            if (ParamSist.paramEquals(CodedValues.TPC_EXPORTA_INCL_ALT_ADE_SEM_ANEXO_PERIODO, CodedValues.TPC_NAO, responsavel)) {
                // Se não exporta inclusão e alteração para maior sem anexos, bloqueia os contratos para que não sejam exportados
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.removeInclusaoAlteracaoSemAnexo.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                hisDAO.removeInclusaoAlteracaoSemAnexo();
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.removeInclusaoAlteracaoSemAnexo.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

                // Se não exporta inclusão e alteração para maior sem anexos, realiza o reimplante de consignações que foram
                // excluídas dos movimentos dos períodos anteriores que tiveram inclusão do anexo faltante neste período
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.reimplantarConsignacoesInclusaoAnexo.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                reimplantarConsignacaoController.reimplantarConsignacoesInclusaoAnexo(orgCodigos, estCodigos, responsavel);
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.reimplantarConsignacoesInclusaoAnexo.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            if(ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel)) {
                // Se necessita de aprovação do gestor para exportação, bloqueia os contratos para que não sejam exportados
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.removeContratosSemPermissaoCse.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                hisDAO.removeContratosSemPermissaoCse();
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.removeContratosSemPermissaoCse.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

                // Se não exporta contratos sem permissão do gestor, realiza o reimplante de consignações que foram
                // excluídas dos movimentos dos períodos anteriores que tiveram permissão do gestor neste período
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.reimplantarConsignacoesPermissaoCse.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                reimplantarConsignacaoController.reimplantarConsignacoesPermissaoCse(orgCodigos, estCodigos, responsavel);
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.reimplantarConsignacoesPermissaoCse.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            /*
             * DESENV-9664 : Enviar somente contratos do período base
             */
            if (exportaInicial &&
                ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) &&
                ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_NAO, responsavel) &&
                ParamSist.paramEquals(CodedValues.TPC_EXPORTAR_ADE_SOMENTE_DO_PERIODO_BASE, CodedValues.TPC_SIM, responsavel)) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.moveComandosForaPeriodoBase.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                hisDAO.moveComandosForaPeriodoBase(orgCodigos, estCodigos, responsavel);
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.moveComandosForaPeriodoBase.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            // Se permite dois ou mais períodos de exportação abertos simultâneamente
            // ajusta a quantidade de parcelas pagas dos contratos, somando as parcelas
            // em processamento de período anterior ao atual de exportação
            if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_DOIS_PERIODOS_EXPORTACAO_ABERTOS, CodedValues.TPC_SIM, responsavel)) {
                autDao.recalcularParcelasPagas(null, responsavel);
                hisDAO.salvarAdePaga(orgCodigos, estCodigos);
                prdDAO.ajustarPrdPagasExpPeriodoSimultaneo(orgCodigos, estCodigos, true);
            }

            if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_EDITAR_FLUXO_PARCELAS_CONSIGNACAO, CodedValues.TPC_SIM, responsavel)) {
                // Move parcelas em status ABERTO na tb_parcela_desconto para a tabela tb_parcela_desconto_periodo
                prdDAO.insereParcelasEdicaoDeFluxo(orgCodigos, estCodigos, null, responsavel);
            }

            // Insere parcelas para os contratos que não possuem parcela para o período atual.
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.insereParcelasFaltantes.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            prdDAO.insereParcelasFaltantes(orgCodigos, estCodigos, null, CodedValues.INTEGRACAO_EXPORTACAO_MOV, responsavel);
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.insereParcelasFaltantes.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

            // Pré-processa as parcelas usando o metodo da classe especifica do Gestor
            if (exportador != null) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.exportador.preProcessaParcelas.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                exportador.preProcessaParcelas(parametrosExportacao, responsavel);
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.exportador.preProcessaParcelas.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            // Seta o status das parcelas para 'Em Processamento'
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.processaParcelas.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            prdDAO.processaParcelas(orgCodigos, estCodigos, verbas);
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.processaParcelas.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

            // Pós-processa as parcelas usando o metodo da classe especifica do Gestor
            if (exportador != null) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.exportador.posProcessaParcelas.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                exportador.posProcessaParcelas(parametrosExportacao, responsavel);
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.exportador.posProcessaParcelas.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            // Se habilita reimplante com redução de valor de parcela, ajusta o valor das parcelas que possuem
            // ocorrência de reimplante com redução do valor da parcela, de acordo com a margem do servidor
            if (exportaInicial &&
                ParamSist.paramEquals(CodedValues.TPC_HABILITA_REIMPLANTACAO_COM_REDUCAO_VALOR, CodedValues.TPC_SIM, responsavel)) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.reduzirValorParcelaReimplante.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                prdDAO.reduzirValorParcelaReimplante(orgCodigos, estCodigos, verbas);
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.reduzirValorParcelaReimplante.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            // Retorna o arquivo contendo as parcelas a serem enviadas para a folha
            nomeArqLote = geraArqExportacao(parametrosExportacao, adeNumeros, exportador, responsavel);

            // Pós-processa o arquivo usando o metodo da classe especifica do Gestor
            if (exportador != null) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.exportador.posProcessaArqLote.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                nomeArqLote = exportador.posProcessaArqLote(nomeArqLote, parametrosExportacao, responsavel);
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.exportador.posProcessaArqLote.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            // Se permite dois ou mais períodos de exportação abertos simultâneamente
            // ajusta a quantidade de parcelas pagas dos contratos, subtraindo as parcelas
            // em processamento de período anterior ao atual de exportação
            if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_DOIS_PERIODOS_EXPORTACAO_ABERTOS, CodedValues.TPC_SIM, responsavel)) {
                hisDAO.recuperarAdePaga();
                prdDAO.ajustarPrdPagasExpPeriodoSimultaneo(orgCodigos, estCodigos, false);
            }

            // Pós-processa as autorizaçoes usando o metodo da classe especifica do Gestor
            if (exportador != null) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.exportador.posProcessaAutorizacoes.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                exportador.posProcessaAutorizacoes(parametrosExportacao, responsavel);
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.exportador.posProcessaAutorizacoes.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            if (ParamSist.paramEquals(CodedValues.TPC_EXPORTA_INCL_ALT_ADE_SEM_ANEXO_PERIODO, CodedValues.TPC_NAO, responsavel)) {
                // Se não exporta inclusão e alteração para maior sem anexos, desloqueia contratos bloqueados
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.alteraInclusaoAlteracaoSemAnexoSituacaoOrigem.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                hisDAO.alteraInclusaoAlteracaoSemAnexoSituacaoOrigem();
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.alteraInclusaoAlteracaoSemAnexoSituacaoOrigem.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            if (ParamSist.paramEquals(CodedValues.TPC_CONCLUI_ADE_EXPORTACAO_MOVIMENTO, CodedValues.TPC_SIM, responsavel)) {
                final boolean reimplanta = ParamSist.paramEquals(CodedValues.TPC_REIMPLANTACAO_AUTOMATICA, CodedValues.TPC_SIM, responsavel);
                final boolean preservaPrdRejeitada = ParamSist.paramEquals(CodedValues.TPC_PRESERVA_PRD_REJEITADA, CodedValues.TPC_SIM, responsavel);

                if (!reimplanta && !preservaPrdRejeitada) {
                    // Executa atualização das ADEs para a conclusão pela data fim
                    autDao.atualizaAdeExportadas(orgCodigos, estCodigos, null, false, responsavel);

                    // Recalcula a margem dos servidores
                    margemController.recalculaMargem(tipoEntidade, entCodigos, responsavel);
                } else {
                    LOG.warn("Configuração incorreta de parâmetros: para conclusão na exportação (" + CodedValues.TPC_CONCLUI_ADE_EXPORTACAO_MOVIMENTO + "), o sistema não deve reimplantar (" + CodedValues.TPC_REIMPLANTACAO_AUTOMATICA + ") nem preservar parcelas (" + CodedValues.TPC_PRESERVA_PRD_REJEITADA + ").");
                }
            }

            // Finaliza o historico de margem
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.finaliza.historico.margem.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            historicoMargemDAO.finalizarHistoricoMargem(orgCodigos, estCodigos, null, OperacaoHistoricoMargemEnum.EXPORTACAO_MOV_FIN);
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.finaliza.historico.margem.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

            // Grava log de auditoria
            new LogDelegate(responsavel, Log.FOLHA, Log.EXPORTACAO_MOVIMENTO, Log.LOG_INFORMACAO).write();

            // Grava registro em ocorrência de consignante
            consignanteController.createOcorrenciaCse(CodedValues.TOC_EXPORTACAO_MOV_FINANCEIRO, responsavel);

            final Date hieDataFimExp = DateHelper.getSystemDatetime();
            // Gera o histórico de integração
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.geraHistoricoExportacao.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            geraHistoricoExportacao(orgCodigos, estCodigos, hieDataIniExp, hieDataFimExp, responsavel);
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.geraHistoricoExportacao.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);

        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (ex.getClass().equals(ConsignanteControllerException.class)) {
                throw (ConsignanteControllerException) ex;
            } else if (ex.getClass().equals(DAOException.class)) {
                throw new ConsignanteControllerException(ex);
            }

            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        return nomeArqLote;
    }

    /**
     * Atualiza o histórico de integrações para os órgãos exportados.
     * @param orgCodigos : os códigos dos órgãos que estão sendo exportados, nulo para todos
     * @param estCodigos : os códigos dos estabelecimentos que estão sendo exportados, nulo para todos
     * @param responsavel : responsável pela operação
     * @throws ConsignanteControllerException
     */
    private void geraHistoricoExportacao(List<String> orgCodigos, List<String> estCodigos, Date hieDataIniExp, Date hieDataFimExp, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            final ListaPeriodoExportacaoQuery listPeriodoQuery = new ListaPeriodoExportacaoQuery();
            listPeriodoQuery.estCodigos = estCodigos;
            listPeriodoQuery.orgCodigos = orgCodigos;
            final List<TransferObject> periodo = listPeriodoQuery.executarDTO();

            if (periodo != null && periodo.size() > 0) {
                TransferObject next = null;

                final String usuCodigo = responsavel != null ? responsavel.getUsuCodigo() : null;
                String orgCodigo = null;
                java.util.Date pexDataIni = null;
                java.util.Date pexDataFim = null;
                java.util.Date pexPeriodo = null;

                // DESENV-10310 : Faz a limpeza da session (por causa da incapacidade do hibernate).
                final BatchManager batman = new BatchManager(SessionUtil.getSession());
                for (final TransferObject element : periodo) {
                    next = element;
                    orgCodigo = (String) next.getAttribute(Columns.PEX_ORG_CODIGO);
                    pexDataIni = (java.util.Date) next.getAttribute(Columns.PEX_DATA_INI);
                    pexDataFim = (java.util.Date) next.getAttribute(Columns.PEX_DATA_FIM);
                    pexPeriodo = (java.util.Date) next.getAttribute(Columns.PEX_PERIODO);

                    final java.sql.Date pexPeriodoSql = new java.sql.Date(pexPeriodo.getTime());

                    HistoricoExportacaoHome.create(usuCodigo, orgCodigo, pexDataIni, pexDataFim, pexPeriodoSql, hieDataIniExp, hieDataFimExp);

                    // DESENV-10310 : Faz a limpeza da session (por causa da incapacidade do hibernate).
                    batman.iterate();
                }
            }
        } catch (final HQueryException ex) {
            throw new ConsignanteControllerException("mensagem.erro.interno.obter.periodo.exportacao", responsavel, ex);
        } catch (final CreateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new ConsignanteControllerException("mensagem.erro.interno.gravar.historico.exportacao", responsavel, ex);
        }
    }

    /**
     * Gera o arquivo contendo as parcelas a serem exportadas
     *
     * @param orgCodigos  : orgãos a serem exportados
     * @param estCodigos  : estabelecimentos a serem exportados
     * @param verbas      : verbas a serem exportadas
     * @param acao        : exportar ou reexportar
     * @param opcao       : opção para geração dos arquivos
     * @param adeNumeros  : lista de ade números para filtrar a geração do arquivo
     * @param exportador  : classe de exportação específica para o sistema
     * @param responsavel : usuário que realiza o processo de exportação
     * @return Nome do arquivo gerado
     * @throws ConsignanteControllerException
     */
    private String geraArqExportacao(ParametrosExportacao parametrosExportacao, List<String> adeNumeros, ExportaMovimento exportador, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.geraArqExportacao.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

            final List<String> orgCodigos = parametrosExportacao.getOrgCodigos();
            final List<String> estCodigos = parametrosExportacao.getEstCodigos();
            final List<String> verbas = parametrosExportacao.getVerbas();
            final String acao = parametrosExportacao.getAcao();
            final String opcao = parametrosExportacao.getOpcao();

            // Pega os parâmetros de sistema necessários
            final ParamSist ps = ParamSist.getInstance();
            final boolean exportaMensal = ps.getParam(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, responsavel) == null || CodedValues.TPC_NAO.equals(ps.getParam(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, responsavel));
            final boolean exportaPorOrgao = ps.getParam(CodedValues.TPC_EXP_MOV_POR_ESTABELECIMENTO, responsavel) == null || CodedValues.TPC_NAO.equals(ps.getParam(CodedValues.TPC_EXP_MOV_POR_ESTABELECIMENTO, responsavel)) || parametrosExportacao.isForcaExpPorOrgao();
            final boolean temProcessamentoFerias = ps.getParam(CodedValues.TPC_TEM_PROCESSAMENTO_FERIAS, responsavel) != null && CodedValues.TPC_SIM.equals(ps.getParam(CodedValues.TPC_TEM_PROCESSAMENTO_FERIAS, responsavel));
            final boolean enviaContratoPagoFerias = ps.getParam(CodedValues.TPC_ENVIA_CONTRATOS_PAGOS_FERIAS_FOLHA, responsavel) != null && CodedValues.TPC_SIM.equals(ps.getParam(CodedValues.TPC_ENVIA_CONTRATOS_PAGOS_FERIAS_FOLHA, responsavel));
            final boolean enviaExclusoesMovMensal = ParamSist.paramEquals(CodedValues.TPC_ENVIA_EXCLUSOES_MOVIMENTO_MENSAL, CodedValues.TPC_SIM, responsavel);
            final boolean enviaContratosCarencia = ParamSist.paramEquals(CodedValues.TPC_ENVIA_CONTRATOS_CARENCIA_MOV_FIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            final boolean consolidaMovFin = ParamSist.paramEquals(CodedValues.TPC_CONSOLIDA_DESCONTOS_MOVIMENTO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

            //DESENV-13925 - TRF1 - Envio dos contratos com carência no Movimento Financeiro
            if(enviaContratosCarencia && (exportaMensal || consolidaMovFin)) {
                 LOG.warn("Configuração incorreta de parâmetros: para envio de contratos em carência (" + CodedValues.TPC_ENVIA_CONTRATOS_CARENCIA_MOV_FIN + "), o movimento deve ser inicial (" + CodedValues.TPC_EXPORTACAO_APENAS_INICIAL + ") não pode ser consolidado (" + CodedValues.TPC_CONSOLIDA_DESCONTOS_MOVIMENTO + ").");
                 throw new ConsignanteControllerException("mensagem.erro.exportar.contratos.em.carencia", responsavel);
            }

            // Diretório raiz de arquivos eConsig
            final String absolutePath = ParamSist.getDiretorioRaizArquivos();

            // Arquivos de configuração para processamento do retorno
            final String nomeArqConfEntrada = (String) ps.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_EXP_MOV_FIN, responsavel);
            final String nomeArqConfSaida = (String) ps.getParam(CodedValues.TPC_ARQ_CONF_SAIDA_EXP_MOV_FIN, responsavel);
            final String nomeArqConfTradutor = (String) ps.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_EXP_MOV_FIN, responsavel);

            // Pega o código do órgão do usuário, caso este não seja de consignante
            final String orgCodigoUsu = usuarioController.isOrg(responsavel != null ? responsavel.getUsuCodigo() : null);

            // Define o path onde será gravado o arquivo final, e o path dos arquivos de configuração
            String pathLote = absolutePath + File.separatorChar + "movimento" + File.separatorChar + "cse";
            String pathConf = absolutePath + File.separatorChar + "conf";
            String pathConfOrg = null;
            String pathConfEst = null;

            if (orgCodigoUsu != null) {
                pathLote += File.separatorChar + orgCodigoUsu;

                /**
                 * Verifica se quem está processando a margem é usuário de orgão, se for pega os arquivos
                 * de configuração do diretório especifico do órgão, senão pega do diretório raiz.
                 * Se não tiver arquivo de configuração no diretório do órgão, usará o da raiz.
                 * Se não existir em ambos, gerará excessão.
                 */
                final File dirPathConf = new File(pathConf + File.separatorChar + "cse" + File.separatorChar + orgCodigoUsu + File.separatorChar + nomeArqConfEntrada);
                if (dirPathConf.exists()) {
                    pathConfOrg = pathConf + File.separatorChar + "cse" + File.separatorChar + orgCodigoUsu;
                }
            }

            if (estCodigos != null && estCodigos.size() == 1) {
                /**
                 * Caso seja informado apenas um estabelecimento, recupera os arquivos de configuração deste estabelecimento
                 * Verifica se o estabelecimento informado possui arquivos de configuração.
                 * Caso não possua, usa os arquivos do diretório raiz.
                 */
                final File dirPathConfEst = new File(pathConf + File.separatorChar + "est" + File.separatorChar + estCodigos.get(0).toString() + File.separatorChar + nomeArqConfEntrada);
                if (dirPathConfEst.exists()) {
                    pathConfEst = pathConf + File.separatorChar + "est" + File.separatorChar + estCodigos.get(0).toString();
                }
            }
            // Define o diretório para os arquivos de configuração, dando preferência para órgão, depois estabelecimento e, por fim, diretório conf (raiz/conf)
            pathConf = !TextHelper.isNull(pathConfOrg) ? pathConfOrg : !TextHelper.isNull(pathConfEst) ? pathConfEst : pathConf;

            final File dirPahtLote = new File(pathLote);
            if (!dirPahtLote.exists() && !dirPahtLote.mkdir()) {
                throw new ConsignanteControllerException("mensagem.erro.diretorio.exportacao.movimento.nao.existe", responsavel);
            }

            LOG.debug("PathConf: " + pathConf);
            LOG.debug("PathLote: " + pathLote);

            // Arquivos de configuração utilizados na exportação
            final String nomeArqConfEntradaDefault = pathConf + File.separatorChar + nomeArqConfEntrada;
            final String nomeArqConfSaidaDefault = pathConf + File.separatorChar + nomeArqConfSaida;
            final String nomeArqConfTradutorDefault = pathConf + File.separatorChar + nomeArqConfTradutor;

            if (!new File(nomeArqConfEntradaDefault).exists() ||
                    !new File(nomeArqConfSaidaDefault).exists() ||
                    !new File(nomeArqConfTradutorDefault).exists()) {
                throw new DAOException("mensagem.erro.arquivo.configuracao.layout.exportacao.movimento.ausente", responsavel);
            }

            // Cria dao de integração com a folha
            final HistoricoIntegracaoDAO hisDAO = DAOFactory.getDAOFactory().getHistoricoIntegracaoDAO();

            if (!exportaMensal) {
                // Contratos que serão gerados na exportação inicial
                final List<String> sadCodigos = new ArrayList<>();
                sadCodigos.add(CodedValues.SAD_DEFERIDA);
                sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
                sadCodigos.add(CodedValues.SAD_SUSPENSA);
                sadCodigos.add(CodedValues.SAD_SUSPENSA_CSE);
                sadCodigos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);
                sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
                sadCodigos.add(CodedValues.SAD_CANCELADA);
                sadCodigos.add(CodedValues.SAD_LIQUIDADA);
                sadCodigos.add(CodedValues.SAD_CONCLUIDO);
                sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);

                // Se não exporta mensal, cria tabela com os códigos de consignações que devem ir para a folha
                hisDAO.setAdeExportacao(orgCodigos, estCodigos, sadCodigos, CodedValues.TOC_CODIGOS_EXPORTACAO_INICIAL);

                // DESENV-9664 : Enviar somente contratos do período base
                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) &&
                    ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_NAO, responsavel) &&
                    ParamSist.paramEquals(CodedValues.TPC_EXPORTAR_ADE_SOMENTE_DO_PERIODO_BASE, CodedValues.TPC_SIM, responsavel)) {
                    hisDAO.removeAdeExportacaoForaPeriodoBase();
                }

                // DESENV-13925 - TRF1 - Envio dos contratos com carência no Movimento Financeiro
                if (enviaContratosCarencia) {
                    hisDAO.selectExportacaoFutura(orgCodigos, estCodigos, verbas, sadCodigos);
                }

            } else {
                // Contratos que serão gerados na exportação mensal, caso esteja configurado para enviar exclusões
                final List<String> sadCodigos = new ArrayList<>();
                sadCodigos.add(CodedValues.SAD_SUSPENSA);
                sadCodigos.add(CodedValues.SAD_SUSPENSA_CSE);
                sadCodigos.add(CodedValues.SAD_CANCELADA);
                sadCodigos.add(CodedValues.SAD_LIQUIDADA);
                sadCodigos.add(CodedValues.SAD_CONCLUIDO);
                sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);

                // Tipos de ocorrências do período que serão levadas em conta
                final List<String> tocCodigos = new ArrayList<>();
                tocCodigos.add(CodedValues.TOC_TARIF_LIQUIDACAO);
                tocCodigos.add(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO);
                tocCodigos.add(CodedValues.TOC_CONCLUSAO_CONTRATO);
                tocCodigos.add(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO);

                // Se não exporta mensal, cria tabela com os códigos de consignações que devem ir para a folha
                hisDAO.setAdeExportacao(orgCodigos, estCodigos, sadCodigos, tocCodigos);
            }

            if (exportador != null) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.exportador.preProcessaTabelaExportacao.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                exportador.preProcessaTabelaExportacao(parametrosExportacao, responsavel);
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.exportador.preProcessaTabelaExportacao.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            // Coloca os dados a serem exportados em uma tabela
            final List<String> spdCodigos = new ArrayList<>();
            final List<String> sadCodigos = new ArrayList<>();
            spdCodigos.add(CodedValues.SPD_EMPROCESSAMENTO);
            if (acao == null || acao.equals(ParametrosExportacao.AcaoEnum.EXPORTAR.getCodigo())) {
                // Contratos deferidos ou em andamento
                sadCodigos.add(CodedValues.SAD_DEFERIDA);
                sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
                // Contratos cancelados / liquidados após o corte
                sadCodigos.add(CodedValues.SAD_CANCELADA);
                sadCodigos.add(CodedValues.SAD_LIQUIDADA);
                // Exporta as ade's que estão aguardando liquidação
                sadCodigos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);
                sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
                // Contratos suspensos após o corte
                sadCodigos.add(CodedValues.SAD_SUSPENSA);
                sadCodigos.add(CodedValues.SAD_SUSPENSA_CSE);
            }
            hisDAO.selectExportacao(orgCodigos, estCodigos, verbas, sadCodigos, spdCodigos, exportaMensal);

            if(enviaContratosCarencia) {
                hisDAO.corrigeContratosExportacaoEmCarencia(CodedValues.TOC_CODIGOS_EXPORTACAO_INICIAL);
            }

            if (exportaMensal && temProcessamentoFerias && enviaContratoPagoFerias) {
                // Se exporta mensal, tem processamento de retorno de férias e deve enviar contratos pagos
                // no retorno de férias no movimento do mês, então inclui estes registros na tabela de exportação
                hisDAO.selectExportacaoFeriasMensal(orgCodigos, estCodigos, verbas);
            }

            if (!exportaMensal || enviaExclusoesMovMensal) {
                final boolean enviaConclusaoFolha = ParamSist.paramEquals(CodedValues.TPC_ENVIA_CONCLUSAO_FOLHA, CodedValues.TPC_SIM, responsavel);

                // Contratos que serão gerados na exportação inicial
                // ou
                // Contratos que serão gerados na exportação mensal, caso esteja configurado para enviar exclusões
                final List<String> sadCodigosExclusao = new ArrayList<>();
                sadCodigosExclusao.add(CodedValues.SAD_CANCELADA);
                sadCodigosExclusao.add(CodedValues.SAD_LIQUIDADA);
                // Envia exclusão de contratos suspensos, para movimento inicial
                sadCodigosExclusao.add(CodedValues.SAD_SUSPENSA);
                sadCodigosExclusao.add(CodedValues.SAD_SUSPENSA_CSE);
                // Contratos em estoque mensal só são exportados como exclusões
                sadCodigosExclusao.add(CodedValues.SAD_ESTOQUE_MENSAL);
                if (enviaConclusaoFolha) {
                    // Inclui na tabela os contratos concluidos apenas se o sistema envia conclusão para a folha
                    sadCodigosExclusao.add(CodedValues.SAD_CONCLUIDO);
                }

                // Se não exporta mensal ou se envia exclusões no movimento mensal, define quais liquidações devem ir para o lote
                hisDAO.selectLiquidacaoExportacao(orgCodigos, estCodigos, verbas, sadCodigosExclusao);
            }

            if(ParamSist.paramEquals(CodedValues.TPC_ENVIA_CONTRATO_RSE_EXCLUIDO, CodedValues.TPC_NAO, responsavel)) {
                hisDAO.excluiContratosRseExcluidosExportacao();
            }

            // Se exporta o valor percentual de serviços que calculam valor real pela base de calculo do
            // servidor, atualiza a tabela de exportação, ajustando o valor a ser exportado pelo campo
            // ADE_VLR_PERCENTUAL gravado na inclusão do contrato.
            if (ParamSist.paramEquals(CodedValues.TPC_EXPORTA_VLR_PERC_SVC_QUE_CALCULA_VLR_REAL, CodedValues.TPC_SIM, responsavel)) {
                hisDAO.atualizaAdeVlrServicoPercentual();
            }

            // DESENV-18998 : Soma os valores pagos na exportação de movimento, no campo "capital_pago"
            if (ParamSist.paramEquals(CodedValues.TPC_CALCULAR_VALOR_PAGO_EXPORTACAO_MOVIMENTO, CodedValues.TPC_SIM, responsavel)) {
                hisDAO.atualizaVlrCapitalPago();
            }

            // Não exportar ADE com valor abaixo do mínimo do serviço.
            if (ParamSist.paramEquals(CodedValues.TPC_EXPORTA_ADE_MENORES_MINIMO_SVC, CodedValues.TPC_NAO, responsavel)) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.removeADEValorAbaixoMinimoSvc.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                hisDAO.removeADEValorAbaixoMinimoSvc();
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.removeADEValorAbaixoMinimoSvc.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            // Consolida exclusão/inclusão com alteração se for o caso
            if (ParamSist.paramEquals(CodedValues.TPC_CONSOLIDA_EXC_INC_COMO_ALT, CodedValues.TPC_SIM, responsavel)) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.consolidaExclusaoInclusaoComoAlteracao.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                hisDAO.consolidaExclusaoInclusaoComoAlteracao();
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.consolidaExclusaoInclusaoComoAlteracao.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            // Processa a tabela de exportação usando o metodo da classe especifica do Gestor
            if (exportador != null) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.exportador.processaTabelaExportacao.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
                exportador.processaTabelaExportacao(parametrosExportacao, responsavel);
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.exportador.processaTabelaExportacao.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            }

            // Busca códigos de tipos de dados de autorização que serão incluídos na tabela de exportação
            final List<TransferObject> lstTipoDadoAdicional = autorizacaoController.lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.EXPORTA, VisibilidadeTipoDadoAdicionalEnum.HOST_A_HOST_LOTE_WEB, null, null, responsavel);

            // Gera os arquivos de exportação
            final List<String> nomesArquivosSaida = hisDAO.geraArqExportacao(opcao, orgCodigoUsu, responsavel, exportaMensal, exportaPorOrgao,
                                                                       pathLote, pathConf, nomeArqConfEntrada, nomeArqConfTradutor, nomeArqConfSaida,
                                                                       nomeArqConfEntradaDefault, nomeArqConfTradutorDefault, nomeArqConfSaidaDefault, lstTipoDadoAdicional, adeNumeros, exportador,
                                                                       parametrosExportacao);

            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.geraArqExportacao.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

            // Compacta os arquvivos gerados em apenas um
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.compacta.arquivos.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            final String nomeArqZip = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.movimento.prefixo", responsavel) + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy") + ".zip";
            FileHelper.zip(nomesArquivosSaida, pathLote + File.separatorChar + nomeArqZip);
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.compacta.arquivos.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

            return nomeArqZip;

        } catch (final IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.interno.compactar.arquivos", responsavel, ex);
        } catch (ExportaMovimentoException | DAOException | AutorizacaoControllerException | UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.interno.geracao.arquivos", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> listaOrgaosExpMovFin(AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            new LogDelegate(responsavel, Log.ORGAO, Log.LST_EXP_MOV_FIN, Log.LOG_INFORMACAO).write();
            final ListaOrgaoExpMovQuery query = new ListaOrgaoExpMovQuery();
            return query.executarDTO();
        } catch (HQueryException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> selectResumoExportacao(List<String> orgCodigos, List<String> estCodigos, List<String> verbas, boolean exportar, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            // Grava log de listagem do resumo da exportação
            new LogDelegate(responsavel, Log.PARCELA, Log.PRC_EXP_MOV_FIN, Log.LOG_INFORMACAO).write();
            // Define o período de exportação
            periodoController.obtemPeriodoExpMovimento(orgCodigos, estCodigos, true, responsavel);
            // Lista o resumo dos contratos a serem exportados
            final ListaResumoExportacaoQuery query = new ListaResumoExportacaoQuery();
            query.exportar = exportar;
            query.orgCodigos = orgCodigos;
            query.estCodigos = estCodigos;
            return query.executarDTO();
        } catch (PeriodoException | HQueryException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void enviarEmailDownloadNaoRealizadoMovFin(AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            LOG.debug("Envio de email realizado de download de movimento financeiro não realizado");

            // Recupera os parâmetros e verifica se existem
            final Object paramAntes = ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_NOTIFICACAO_DOWNLOAD_MOV_FIN_USU_CSE, responsavel);
            if (paramAntes == null || paramAntes.toString().isBlank()) {
                return;
            }

            // Converte o parâmetro para uma lista de integers
            final List<Integer> diasNotificacao = new ArrayList<>();
            if (paramAntes != null) {
                final String[] diasNotificacaoAntesSt = StringUtils.split(paramAntes.toString(), ",");
                for (final String diaSt : diasNotificacaoAntesSt) {
                    Integer dia;
                    try {
                        dia = Integer.parseInt(diaSt.trim());
                    } catch (final NumberFormatException e) {
                        throw new ConsignanteControllerException("mensagem.erro.interno.parametro.sistema.arg0.contem.valor.incorreto", responsavel, CodedValues.TPC_DIAS_NOTIFICACAO_DOWNLOAD_MOV_FIN_USU_CSE);
                    }
                    diasNotificacao.add(dia);
                }
            }

            final TransferObject agendamento = agendamentoController.findAgendamento(AgendamentoEnum.ENVIO_EMAIL_DOWNLOAD_NAO_REALIZADO_MOV_FIN.getCodigo(), responsavel);
            final Date agdDataCadastro = (Date) agendamento.getAttribute(Columns.AGD_DATA_CADASTRO);

            final ListaArquivoMovFinDownloadNaoRealizadoQuery query = new ListaArquivoMovFinDownloadNaoRealizadoQuery();
            query.dataInicio = agdDataCadastro;
            query.diasEnvioEmail = diasNotificacao;
            final List<TransferObject> listaArquivoMovFinSemDownload = query.executarDTO();

            if (listaArquivoMovFinSemDownload != null && !listaArquivoMovFinSemDownload.isEmpty()) {
                EnviaEmailHelper.enviarEmailDownloadNaoRealizadoMovFin(listaArquivoMovFinSemDownload, responsavel);
            }

        } catch (HQueryException | ViewHelperException | AgendamentoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * cria uma hierarquia de diretórios contendo os anexos dos contratos do período a ser exportado.
     * A hierquia de diretórios segue o padrão : ACAO(I, A, E)/COD_VERBA/NOME_ARQUIVO. Onde NOME-ARQUIVO
     * será CPF-ADE_NUMERO
     * @param orgCodigos - órgãos cujos contratos serão pesquisados
     * @param estCodigos - estabelecimentos cujos contratos serão pesquisados
     * @param zipFileNameOutPut - nome do arquivo zip a ser criado
     * @param responsavel
     * @throws ConsignanteControllerException
     */
    @Override
    public String compactarAnexosAdePeriodo(List<String> orgCodigos, List<String> estCodigos, List<String> verbas, String zipFileNameOutPut, AcessoSistema responsavel) throws ConsignanteControllerException {
        if (TextHelper.isNull(zipFileNameOutPut)) {
            throw new ConsignanteControllerException("mensagem.erro.nome.arquivo.nao.informado", responsavel);
        }

        final ListaAnexoAdeMovFinQuery lstAnexos = new ListaAnexoAdeMovFinQuery();
        lstAnexos.orgCodigos = orgCodigos;
        lstAnexos.estCodigos = estCodigos;
        lstAnexos.verbas = verbas;

        List<TransferObject> infoAnexosList = null;
        try {
            infoAnexosList = lstAnexos.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        final boolean necessitaAprovacaoGestor = ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel);
        final HashMap<Long, List<String>> hashArquivos = new HashMap<>();
        final HashMap<Long, String> hashCaminhos = new HashMap<>();
        final HashMap<Long, HashMap<String, String>> hashCampos = new HashMap<>();

        List<String> arquivos = new ArrayList<>();
        Long adeNumeroAnt = null;
        String caminhoRaiz = null;
        Path pathRaiz = null;
        final String rootDirPath = ParamSist.getDiretorioRaizArquivos();

        if (infoAnexosList != null && !infoAnexosList.isEmpty()) {
            //define pasta raíz onde ficará diretório temporário
            caminhoRaiz = rootDirPath + File.separatorChar + "temp";
            if (!Files.exists(FileSystems.getDefault().getPath(caminhoRaiz), LinkOption.NOFOLLOW_LINKS)) {
                try {
                    Files.createDirectory(FileSystems.getDefault().getPath(caminhoRaiz));
                } catch (final IOException e) {
                    try {
                        FileHelper.deleteDir(caminhoRaiz.toString());
                    } catch (final IOException ex) {
                        LOG.error(ex.getMessage(), ex);
                        throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }
                    throw new ConsignanteControllerException("mensagem.erro.arquivo.nao.criado", responsavel, caminhoRaiz);
                }
            }
        } else {
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.aviso.nenhum.anexo.encontrado", responsavel));
            return null;
        }

        // Cria raíz de anexos temporário
        caminhoRaiz += File.separatorChar + ApplicationResourcesHelper.getMessage("rotulo.nome.subpasta.anexos.ade", responsavel);
        try {
            final Path caminhoRaizPath = FileSystems.getDefault().getPath(caminhoRaiz);
            if (Files.exists(caminhoRaizPath)) {
                FileHelper.deleteDir(caminhoRaiz);
            }
            pathRaiz = Files.createDirectory(caminhoRaizPath);
        } catch (final IOException e1) {
            try {
                FileHelper.deleteDir(caminhoRaiz);
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
            throw new ConsignanteControllerException("mensagem.erro.arquivo.nao.criado", responsavel, caminhoRaiz);
        }

        final String inclusao = ApplicationResourcesHelper.getMessage("rotulo.movimento.financeiro.inclusao", responsavel);
        final String exclusao = ApplicationResourcesHelper.getMessage("rotulo.movimento.financeiro.exclusao", responsavel);
        final String alteracao = ApplicationResourcesHelper.getMessage("rotulo.movimento.financeiro.alteracao", responsavel);

        for (final TransferObject infoAnexo : infoAnexosList) {
            final String situacaoSgl = (String) infoAnexo.getAttribute("SITUACAO");
            final String codVerba = (String) infoAnexo.getAttribute(Columns.CNV_COD_VERBA);
            final String serCpf = (String) infoAnexo.getAttribute(Columns.SER_CPF);
            final Long adeNumero = (Long) infoAnexo.getAttribute(Columns.ADE_NUMERO);
            final String adeCodigo = (String) infoAnexo.getAttribute(Columns.ADE_CODIGO);
            final String rseMatricula = (String) infoAnexo.getAttribute(Columns.RSE_MATRICULA);
            final String serNome = (String) infoAnexo.getAttribute(Columns.SER_NOME);
            final String aadNome = (String) infoAnexo.getAttribute(Columns.AAD_NOME);
            final String adeIdentificador = (String) infoAnexo.getAttribute(Columns.ADE_IDENTIFICADOR);
            final String adeIndice = (String) infoAnexo.getAttribute(Columns.ADE_INDICE);


            String situacao = "";
            switch (situacaoSgl.toUpperCase()) {
                case "I":
                    situacao = inclusao;
                    break;
                case "E":
                    situacao = exclusao;
                    break;
                case "A":
                    situacao = alteracao;
                    break;
            }

            final String caminho = caminhoRaiz
                           + File.separatorChar + situacao
                           + File.separatorChar + codVerba
                           ;
            try {
                Files.createDirectories(Paths.get(caminho));
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ConsignanteControllerException("mensagem.erro.arquivo.nao.criado", responsavel, caminho);
            }

            final Path anexoACopiarPath = FileSystems.getDefault().getPath(ParamSist.getDiretorioRaizArquivos()
                    + File.separatorChar + "anexo" + File.separatorChar + DateHelper.format((Date) infoAnexo.getAttribute(Columns.ADE_DATA), "yyyyMMdd")
                    + File.separatorChar + adeCodigo + File.separatorChar + aadNome);

            //campos que podem ser usados para montar o padrão de nome de arquivo
            final HashMap<String, String> campos = new HashMap<>();
            campos.put("rse_matricula", rseMatricula);
            campos.put("ser_cpf", serCpf);
            campos.put("ser_nome", serNome);
            campos.put("ade_numero", adeNumero.toString());
            campos.put("cnv_cod_verba", codVerba);
            campos.put("ade_identificador", adeIdentificador);
            campos.put("ade_indice", adeIndice);

            if (!necessitaAprovacaoGestor && !adeNumero.equals(adeNumeroAnt)) {
                //como é o primeiro registro deste adeNumero, então é o anexo mais recente deste contrato no período
                //buscar o anexo do diretório padrão eCosing

                // Pega extensão do arquivo, se houver
                final String extensao = aadNome.lastIndexOf(".") != -1 ? aadNome.substring(aadNome.lastIndexOf(".")) : "";

                final String strippedCpf = serCpf.replace(".", "").replace("-", "");

                Path novoArquivo = null;
                try {
                    if (!TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_PADRAO_NOME_ANEXO_PERIODO, responsavel))) {
                        String newName;
                        try {
                            newName = geraNomeAnexosPeriodo(campos, extensao, responsavel);
                        } catch (final ParametrosException ex) {
                            LOG.error(ApplicationResourcesHelper.getMessage("rotulo.prefixo.zip.anexos.ade", responsavel, aadNome) + ex.getMessage(), ex);
                            continue;
                        }
                        novoArquivo = FileSystems.getDefault().getPath(caminho + File.separatorChar + newName);
                    } else {
                        novoArquivo = FileSystems.getDefault().getPath(caminho + File.separatorChar + strippedCpf + "-" + adeNumero + extensao);
                    }

                    // copia arquivo do diretório anexo padrão eConsig para a hierarquia de diretórios de anexos do período
                    LOG.debug("Tentando copiar \"" + anexoACopiarPath + "\" para \"" + novoArquivo + "\".");
                    if (Files.exists(anexoACopiarPath, LinkOption.NOFOLLOW_LINKS)) {
                        Files.copy(anexoACopiarPath, novoArquivo, LinkOption.NOFOLLOW_LINKS, StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        LOG.warn("Arquivo \"" + anexoACopiarPath + "\" não existe e não foi copiado.");
                    }
                } catch (final IOException e) {
                    LOG.error(e.getMessage(), e);
                    try {
                        FileHelper.deleteDir(pathRaiz.toString());
                    } catch (final IOException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }

                    throw new ConsignanteControllerException("mensagem.erro.arquivo.nao.criado", responsavel, e, novoArquivo != null ? novoArquivo.toString() : "-");
                }
                adeNumeroAnt = adeNumero;
            }

            if(necessitaAprovacaoGestor) {
                if(TextHelper.isNull(adeNumeroAnt) || adeNumero.equals(adeNumeroAnt)) {
                    LOG.debug("Separando arquivo para zipar " + anexoACopiarPath.toString());
                    arquivos.add(anexoACopiarPath.toString());
                    if(!hashCaminhos.containsKey(adeNumero)) {
                        hashCaminhos.put(adeNumero, caminho);
                    }
                    adeNumeroAnt = adeNumero;
                } else {
                    if(!hashArquivos.containsKey(adeNumeroAnt)) {
                        hashArquivos.put(adeNumeroAnt, arquivos);
                    }
                    arquivos = new ArrayList<>();
                    LOG.debug("Separando arquivo para zipar " + anexoACopiarPath.toString());
                    arquivos.add(anexoACopiarPath.toString());

                    if(!hashCaminhos.containsKey(adeNumero)) {
                        hashCaminhos.put(adeNumero, caminho);
                    }
                    adeNumeroAnt = adeNumero;
                }
                if(!hashCampos.containsKey(adeNumero)) {
                    hashCampos.put(adeNumero, campos);
                }
            }
        }

            if(necessitaAprovacaoGestor) {
                if(!hashArquivos.containsKey(adeNumeroAnt)) {
                    hashArquivos.put(adeNumeroAnt, arquivos);
                }
                for(final Long key : hashCaminhos.keySet()) {
                    try {
                        final String caminhoArquivo = hashCaminhos.get(key);
                        final HashMap<String, String> camposNomeArquivo = hashCampos.get(key);
                        final List<String> arquivosAde = hashArquivos.get(key);

                        final String extensao = arquivosAde.size() == 1  && arquivosAde.get(0).lastIndexOf(".") != -1 ? arquivosAde.get(0).substring(arquivosAde.get(0).lastIndexOf(".")) : ".zip";
                        String nomeArquivo = "";

                        if (!TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_PADRAO_NOME_ANEXO_PERIODO, responsavel))) {
                            nomeArquivo = downloadAnexoContratoController.geraNomeAnexosPeriodo(camposNomeArquivo, extensao, null, true, responsavel);
                        } else {
                            final String strippedCpf = camposNomeArquivo.get("ser_cpf").replace(".", "").replace("-", "");
                            nomeArquivo = strippedCpf + "-" + key + extensao;
                        }

                        final String arquivoDestino = caminhoArquivo+ File.separatorChar + nomeArquivo;
                        LOG.debug("Tentando copiar \"" + arquivoDestino + "\".");

                        if(arquivosAde.size() == 1) {
                            FileHelper.copyFile(new File(arquivosAde.get(0)), new File(arquivoDestino));
                        } else {
                            FileHelper.zip(arquivosAde, arquivoDestino);
                        }
                    } catch (ParametrosException | IOException ex) {
                        LOG.warn("Arquivo não existe e não foi copiado.");
                    }
                }
            }

        // Compacta todo o diretório
        StringBuilder pathZipAnexos = null;
        try {
            // Pega o código do órgão do usuário, caso este não seja de consignante
            final String orgCodigoUsu = usuarioController.isOrg(responsavel != null ? responsavel.getUsuCodigo() : null);

            // Define o path onde será gravado o arquivo final de anexos
            pathZipAnexos = new StringBuilder(rootDirPath + File.separatorChar + "movimento" + File.separatorChar + "cse");

            if (orgCodigoUsu != null) {
                pathZipAnexos.append(File.separatorChar).append(orgCodigoUsu);
            }

            // Cria diretório caso não exista
            if (!Files.exists(FileSystems.getDefault().getPath(pathZipAnexos.toString()))) {
                Files.createDirectory(FileSystems.getDefault().getPath(pathZipAnexos.toString()));
            }

            //caminho para o arquivo final
            pathZipAnexos.append(File.separatorChar).append(ApplicationResourcesHelper.getMessage("rotulo.prefixo.zip.anexos.ade", responsavel)).append(zipFileNameOutPut);

            if (Files.exists(FileSystems.getDefault().getPath(pathZipAnexos.toString()))) {
                Files.delete(FileSystems.getDefault().getPath(pathZipAnexos.toString()));
            }
            FileHelper.zipFolder(pathRaiz.toString(), pathZipAnexos.toString());
            FileHelper.deleteDir(pathRaiz.toString());
            LOG.info(ApplicationResourcesHelper.getMessage("mensagem.info.anexo.mov.arquivo.criado", responsavel, pathZipAnexos.toString()));

            return pathZipAnexos.toString();

        } catch (final IOException e) {
            LOG.error(e.getMessage(), e);
            throw new ConsignanteControllerException("mensagem.erro.arquivo.nao.criado", responsavel, pathZipAnexos.toString());
        } catch (final UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException(ex.getMessageKey(), responsavel, ex);
        }
    }

    private String geraNomeAnexosPeriodo(HashMap<String, String> campos, String extensao, AcessoSistema responsavel) throws ParametrosException {
        final String nomePattern = ParamSist.getInstance().getParam(CodedValues.TPC_PADRAO_NOME_ANEXO_PERIODO, responsavel).toString();
        final StringBuilder nomeFinal = new StringBuilder();

        //verifica se existe algum prefixo para o nome do arquivo além dos campos dinâmicos
        boolean temPrefixo = nomePattern.indexOf("<") > 0;
        final String[] nomePartes = nomePattern.split("<");

        //itera sob cada bloco de parâmetros
        for (final String parte : nomePartes) {
            //caso tenha prefixo, adiciona ao nome do arquivo e passa para o próximo bloco
            if (temPrefixo) {
                nomeFinal.append(parte);
                temPrefixo = false;
                continue;
            }

            //recupera os parâmetros
            final String[] parametros = parte.split(":");

            //verifica se cada um dos valores dinâmicos possuem todos os campos necessários
            if (parametros.length < 4) {
                throw new ParametrosException("mensagem.erro.anexo.perido.parametros.ausentes", responsavel);
            }

            //verifica se depois do fim do parâmetro existe algum texto fixo
            final boolean temSufixoIn = parametros[3].indexOf(">") < parametros[3].length() - 1;

            final String chave = parametros[0];
            final String complemento = parametros[1];
            final String direcaoComplemento = parametros[2];
            final int comprimento = Integer.parseInt(parametros[3].split(">")[0]);

            String valorCampo = campos.get(chave);

            //verifica se o campo usado é um dos previstos:
            //rse_matricula, ser_cpf, ser_nome, ade_numero, cnv_cod_verba, ade_identificador, ade_indice
            if (valorCampo == null) {
                throw new ParametrosException(ApplicationResourcesHelper.getMessage("mensagem.erro.anexo.perido.campo.sem.suporte", responsavel, parametros[0]), responsavel);
            }

            //substituo caracteres acentuados por versões sem acento
            valorCampo = Normalizer.normalize(valorCampo, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
            //retiro caracteres especiais
            valorCampo = valorCampo.replaceAll("[^a-zA-Z0-9]+", "");

            //Se o campo setado não estiver setado no sistema preenche com X (1 se não houver comprimento definido ou tantos
            // X quanto o comprimento
            if (!"".equals(valorCampo)) {
                if (comprimento != -1) {
                    final int comprimentoDado = valorCampo.length();
                    String complementoDado = "";

                    //Controe o complemento a ser adicionado ao valor do campo
                    if (comprimentoDado < comprimento) {
                        for (int i = 0; i < comprimento - comprimentoDado; i++) {
                            complementoDado += complemento;
                        }
                    }

                    if (!"".equals(complementoDado)) {
                        //Concatena no lado certo do dado
                        if ("E".equalsIgnoreCase(direcaoComplemento)) {
                            nomeFinal.append(complementoDado).append(valorCampo);
                        } else {
                            nomeFinal.append(valorCampo).append(complementoDado);
                        }
                    } else {
                        //nesse caso o valor do campo é cortado para o tamanho do comprimento fornecido
                        nomeFinal.append(valorCampo.substring(0, comprimento));
                    }
                } else {
                    nomeFinal.append(valorCampo);
                }

            } else if (comprimento != -1) {
                final int comprimentoDado = 0;
                String complementoDado = "";

                //Controe o complemento a ser adicionado ao valor do campo
                if (comprimentoDado < comprimento) {
                    for (int i = 0; i < comprimento - comprimentoDado; i++) {
                        complementoDado += "X";
                    }
                }

                if (!"".equals(complementoDado)) {
                    nomeFinal.append(complementoDado);
                } else {
                    //nesse caso o valor do campo é cortado para o tamanho do comprimento fornecido
                    nomeFinal.append(valorCampo.substring(0, comprimento));
                }
            } else {
                nomeFinal.append("X");
            }

            //adiciona qualquer parte fixa do pattern de nome ao nome do arquivo final
            if (temSufixoIn) {
                nomeFinal.append(parametros[3].split(">")[1]);
            }
        }
        nomeFinal.append(extensao);

        return nomeFinal.toString();
    }

    @Override
    public List<TransferObject> consultarMovimentoFinanceiro(String periodo, String rseMatricula, String serCpf, String orgIdentificador, String estIdentificador, String csaIdentificador, String svcIdentificador, String cnvCodVerba, AcessoSistema responsavel) throws ConsignanteControllerException {
        if (TextHelper.isNull(periodo)) {
            LOG.warn("O parâmetro de período é obrigatório para a consulta.");
            return null;
        }

        try {
            final ListaMovimentoFinanceiroQuery query = new ListaMovimentoFinanceiroQuery();
            query.periodo = DateHelper.parsePeriodString(periodo);
            query.rseMatricula = rseMatricula;
            query.serCpf = serCpf;
            query.orgIdentificador = orgIdentificador;
            query.estIdentificador = estIdentificador;
            query.csaIdentificador = csaIdentificador;
            query.svcIdentificador = svcIdentificador;
            query.cnvCodVerba = cnvCodVerba;

            return query.executarDTO();
        } catch (final ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.periodo.invalido", responsavel, ex);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void exportaMovimentoFinanceiroAutomaticoOrgao(AcessoSistema responsavel) throws ConsignanteControllerException {
        if (ParamSist.getBoolParamSist(CodedValues.TPC_EXPORTA_MOVIMENTO_ORGAO_AUTOMATICAMENTE, responsavel)) {
            LOG.debug("Verifica se o sistema exporta por órgão automaticamente, confere a data de exportação e inicia");
            List<String> verbas = null;
            List<TransferObject> lstOrgaos = null;
            List<CalendarioFolhaOrg> calendarioFolhaOrg = null;
            final HashMap<String,CalendarioFolhaOrg> hashPeriodoOrgao = new HashMap<>();
            final HashMap<String,Boolean> hashHistExportacao = new HashMap<>();
            List<String> lstCodOrgaos  = new ArrayList<>();
            final List<Date> periodos = new ArrayList<>();

            try {
                verbas = ExportaMovimentoHelper.getVerbas(responsavel);
                lstOrgaos = consignanteController.lstOrgaos(null, responsavel);
                lstCodOrgaos = lstOrgaos.stream().map(orgao -> orgao.getAttribute(Columns.ORG_CODIGO).toString()).toList();

                // Olhamos sempre o dia anterior para filtrar o periodo do calendário, pois precisamos saber se foi o último dia do periodo.
                final Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                calendarioFolhaOrg = CalendarioFolhaOrgHome.findByDateBetween(cal.getTime());

                if(calendarioFolhaOrg == null || calendarioFolhaOrg.isEmpty()) {
                    throw new ConsignanteControllerException("mensagem.erro.folha.exportar.movimento.orgao.automaticamente.calendario", responsavel);
                }

                for(final CalendarioFolhaOrg calOrg : calendarioFolhaOrg) {
                    hashPeriodoOrgao.put(calOrg.getOrgCodigo(), calOrg);
                    periodos.add(calOrg.getCfoPeriodo());
                }

                if(!periodos.isEmpty() && !lstCodOrgaos.isEmpty()) {
                    final List<HistoricoExportacao> lstHistoricoExportacao = HistoricoExportacaoHome.findByPeriodoOrgcodigo(periodos, lstCodOrgaos);
                    for(final HistoricoExportacao histHM : lstHistoricoExportacao) {
                        hashHistExportacao.put(histHM.getOrgCodigo()+histHM.getHiePeriodo(), Boolean.TRUE);
                    }
                }
            } catch (ConsignanteControllerException | ConvenioControllerException | FindException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }

            for (final String orgCodigo : lstCodOrgaos) {
                final CalendarioFolhaOrg periodoMovimento = hashPeriodoOrgao.get(orgCodigo);
                final Date cfoDataFim = periodoMovimento !=null && periodoMovimento.getCfoDataFim() != null ? periodoMovimento.getCfoDataFim() : null;
                final Date periodo = periodoMovimento !=null && periodoMovimento.getCfoPeriodo() != null ? (Date) periodoMovimento.getCfoPeriodo() : null;
                final boolean existeHistoricoExportacao = hashHistExportacao.get(orgCodigo+periodo) != null;

                if(cfoDataFim != null && DateHelper.dayDiff(DateHelper.getSystemDatetime(), cfoDataFim) == 1 && !existeHistoricoExportacao) {
                    final ParametrosExportacao parametrosExportacao = new ParametrosExportacao();
                    parametrosExportacao.setOrgCodigos(Arrays.asList(orgCodigo))
                                        .setEstCodigos(null)
                                        .setVerbas(verbas)
                                        .setAcao(ParametrosExportacao.AcaoEnum.EXPORTAR.getCodigo())
                                        .setOpcao(CodedValues.EXPORTA_ARQUIVO_POR_ENTIDADE)
                                        .setResponsavel(responsavel)
                                        .setForcaExpPorOrgao(true);
                    try {
                        LOG.info(ApplicationResourcesHelper.getMessage("mensagem.folha.exportar.movimento.orgao.automaticamente", responsavel, orgCodigo));
                        exportaMovimentoFinanceiro(parametrosExportacao, null, responsavel);
                    } catch (final ConsignanteControllerException ex) {
                        LOG.info(ApplicationResourcesHelper.getMessage("mensagem.erro.folha.exportar.movimento.orgao.automaticamente", responsavel, orgCodigo));
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        }
    }
}
