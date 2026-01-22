package com.zetra.econsig.service.leilao;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.CalendarioTO;
import com.zetra.econsig.dto.entidade.FiltroLeilaoSolicitacaoTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.parametros.RenegociarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CalendarioControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LeilaoSolicitacaoControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.NotificacaoDispositivoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.SimulacaoHelper;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.senha.GeradorSenhaUtil;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessaEnviarEmailNotificacaoNovoLeilaoByFiltro;
import com.zetra.econsig.job.process.ProcessaOfertaAutomaticaLeilao;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AnaliseRiscoRegistroSer;
import com.zetra.econsig.persistence.entity.AnaliseRiscoRegistroServidorHome;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.Cidade;
import com.zetra.econsig.persistence.entity.CidadeHome;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.DadosAutorizacaoDesconto;
import com.zetra.econsig.persistence.entity.DadosAutorizacaoDescontoHome;
import com.zetra.econsig.persistence.entity.DadosAutorizacaoDescontoId;
import com.zetra.econsig.persistence.entity.FiltroLeilaoSolicitacao;
import com.zetra.econsig.persistence.entity.FiltroLeilaoSolicitacaoHome;
import com.zetra.econsig.persistence.entity.PropostaLeilaoSolicitacao;
import com.zetra.econsig.persistence.entity.PropostaLeilaoSolicitacaoHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.ServicoHome;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacao;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.StatusProposta;
import com.zetra.econsig.persistence.entity.StatusSolicitacao;
import com.zetra.econsig.persistence.entity.Uf;
import com.zetra.econsig.persistence.entity.UfHome;
import com.zetra.econsig.persistence.entity.UsuarioHome;
import com.zetra.econsig.persistence.entity.VerbaConvenio;
import com.zetra.econsig.persistence.entity.VerbaConvenioHome;
import com.zetra.econsig.persistence.query.leilao.ListaAcompanhamentoLeilaoSolicitacaoQuery;
import com.zetra.econsig.persistence.query.leilao.ListaAdeSolicitadaLeilaoQuery;
import com.zetra.econsig.persistence.query.leilao.ListaDadosPortabilidadeLeilaoQuery;
import com.zetra.econsig.persistence.query.leilao.ListaEmailConsignatariasNotificacaoLeilaoQuery;
import com.zetra.econsig.persistence.query.leilao.ListaFiltroLeilaoSolicitacaoByAdeCodigoQuery;
import com.zetra.econsig.persistence.query.leilao.ListaLeilaoFinalizadoSemContatoQuery;
import com.zetra.econsig.persistence.query.leilao.ListaLeilaoPassivelReversaoQuery;
import com.zetra.econsig.persistence.query.leilao.ListaPropostaLeilaoSolicitacaoOfertaAutQuery;
import com.zetra.econsig.persistence.query.leilao.ListaPropostaLeilaoSolicitacaoQuery;
import com.zetra.econsig.persistence.query.leilao.ListaSolicitacaoLeilaoCanceladoParaBloqueioQuery;
import com.zetra.econsig.persistence.query.leilao.ListaSolicitacaoLeilaoEncerradoQuery;
import com.zetra.econsig.persistence.query.leilao.ListaSolicitacaoLeilaoSemPropostaQuery;
import com.zetra.econsig.persistence.query.leilao.ListaStatusPropostaLeilaoQuery;
import com.zetra.econsig.persistence.query.leilao.ObtemAnaliseDeRiscoRegistroServidorQuery;
import com.zetra.econsig.persistence.query.leilao.ObtemMaiorNumeroPropostaLeilaoQuery;
import com.zetra.econsig.service.calendario.CalendarioController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.CancelarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignacao.RenegociarConsignacaoController;
import com.zetra.econsig.service.consignacao.ReservarMargemController;
import com.zetra.econsig.service.notificacao.NotificacaoDispositivoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.StatusPropostaEnum;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

import jakarta.json.stream.JsonGenerationException;

/**
 * <p>Title: LeilaoSolicitacaoControllerBean</p>
 * <p>Description: Session Façade para operações do módulo de leilão
 * de solicitação via simulação pelo servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class LeilaoSolicitacaoControllerBean implements LeilaoSolicitacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LeilaoSolicitacaoControllerBean.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    @Qualifier("reservarMargemController")
    private ReservarMargemController reservarMargemController;

    @Autowired
    @Qualifier("renegociarConsignacaoController")
    private RenegociarConsignacaoController renegociarConsignacaoController;

    @Autowired
    private CalendarioController calendarioController;

    @Autowired
    private CancelarConsignacaoController cancelarConsignacaoController;

    @Autowired
    private NotificacaoDispositivoController notificacaoDispositivoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private UsuarioController usuarioController;

    /**
     * Verifica se a consignação informada possui solicitação de leilão com situação expirada
     * @param adeCodigo
     * @param pendente
     * @param responsavel
     * @return
     * @throws LeilaoSolicitacaoControllerException
     */
    @Override
    public boolean temSolicitacaoLeilaoExpirada(String adeCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            // verifica se tem solicitação de leilão com status Expirada
            final String[] tisCodigos = {TipoSolicitacaoEnum.SOLICITACAO_PROPOSTA_LEILAO_VIA_SIMULACAO.getCodigo()};
            String[] ssoCodigos = {StatusSolicitacaoEnum.EXPIRADA.getCodigo()};
            List<SolicitacaoAutorizacao> soaList = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigos, ssoCodigos);
            if ((soaList != null) && !soaList.isEmpty()) {
                return true;
            }
            // verifica se tem solicitação de leilão com statu pendente e validade vencida
            ssoCodigos = new String[]{StatusSolicitacaoEnum.PENDENTE.getCodigo(), StatusSolicitacaoEnum.FINALIZADA.getCodigo()};
            soaList = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigos, ssoCodigos);
            if ((soaList != null) && !soaList.isEmpty()) {
                final SolicitacaoAutorizacao soa = soaList.get(0);
                final Date soaDataValidade = soa.getSoaDataValidade();
                final Date dataAtual = Calendar.getInstance().getTime();
                if (soaDataValidade.before(dataAtual)) {
                    return true;
                }
            }
            return false;
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Verifica se a consignação informada por parâmetro possui solicitação de leilão,
     * pendente ou não, de acordo com a tabela de solicitação autorização
     * @param adeCodigo
     * @param pendente
     * @param responsavel
     * @return
     * @throws LeilaoSolicitacaoControllerException
     */
    @Override
    public boolean temSolicitacaoLeilao(String adeCodigo, boolean pendente, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            final String[] tisCodigos = {TipoSolicitacaoEnum.SOLICITACAO_PROPOSTA_LEILAO_VIA_SIMULACAO.getCodigo()};
            String[] ssoCodigos = {StatusSolicitacaoEnum.PENDENTE.getCodigo(), StatusSolicitacaoEnum.EXPIRADA.getCodigo()};
            if (!pendente) {
                ssoCodigos = new String[]{StatusSolicitacaoEnum.PENDENTE.getCodigo(), StatusSolicitacaoEnum.FINALIZADA.getCodigo(), StatusSolicitacaoEnum.EXPIRADA.getCodigo()};
            }
            final List<SolicitacaoAutorizacao> soaList = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigos, ssoCodigos);
            return ((soaList != null) && !soaList.isEmpty());
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Retorna a solicitação de autorização de leilão pendente e com prazo expirado
     * @param adeCodigo
     * @param responsavel
     * @return
     * @throws LeilaoSolicitacaoControllerException
     */
    private SolicitacaoAutorizacao obtemSolicitacaoLeilaoPendente(String adeCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            // Recupera a solicitação de propostas que deve estar pendente ou com prazo expirado
            final String[] tisCodigos = {TipoSolicitacaoEnum.SOLICITACAO_PROPOSTA_LEILAO_VIA_SIMULACAO.getCodigo()};
            final String[] ssoCodigos = {StatusSolicitacaoEnum.PENDENTE.getCodigo(), StatusSolicitacaoEnum.EXPIRADA.getCodigo()};
            final List<SolicitacaoAutorizacao> soaList = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigos, ssoCodigos);
            if ((soaList != null) && !soaList.isEmpty()) {
                return soaList.get(0);
            }
            return null;
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Registra a solicitação de autorização para início do leilão, possibilitando
     * as consignatárias informarem propostas para a participação no leilão.
     * @param adeCodigo
     * @param rseCodigo
     * @param responsavel
     * @throws LeilaoSolicitacaoControllerException
     */
    @Override
    public void iniciarProcessoLeilao(String adeCodigo, String rseCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            // Verifica se o servidor pode iniciar novos leilões, ou seja, não possui bloqueio
            // pelo fato de ter cancelado outros leilões anteriores
            autorizacaoController.verificaBloqueioFuncao(rseCodigo, "LEILAO", responsavel);

            // Calcular data validade, de acordo com a data atual e parâmetro de sistema.
            // Se menor ou igual a Zero ou nulo, o leilão não terá validade, ficando aberto
            // enquanto o servidor não escolher qual a proposta será aceita.
            java.util.Date dataValidade = null;
            final Object paramPrazoMinutosFechamentoLeilao = ParamSist.getInstance().getParam(CodedValues.TPC_MINUTOS_FECHAMENTO_LEILAO_VIA_SIMULACAO, responsavel);
            if (!TextHelper.isNull(paramPrazoMinutosFechamentoLeilao) && TextHelper.isNum(paramPrazoMinutosFechamentoLeilao)) {
                final int qtdMinutosFechamentoLeilao = Integer.parseInt(paramPrazoMinutosFechamentoLeilao.toString());
                if (qtdMinutosFechamentoLeilao > 0) {
                    try {
                        dataValidade = calcularDataValidadeLeilao(qtdMinutosFechamentoLeilao, responsavel);
                    } catch (final CalendarioControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                        throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }
                }
            }

            // Cria o registro de solicitação de propostas de leilão
            SolicitacaoAutorizacaoHome.create(adeCodigo, responsavel.getUsuCodigo(), TipoSolicitacaoEnum.SOLICITACAO_PROPOSTA_LEILAO_VIA_SIMULACAO.getCodigo(), StatusSolicitacaoEnum.PENDENTE.getCodigo(), dataValidade);

            // Inclui ocorrência de consignação informando que o processo foi iniciado
            autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.leilao.solicitacao.iniciado", responsavel), responsavel);
        } catch (CreateException | AutorizacaoControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Realiza o cancelamento do processo de leilão, rejeitando as propostas que estão aguardando
     * aprovação, e cancelando a solicitação de propostas de leilão pendente.
     * @param adeCodigo
     * @param responsavel
     * @throws LeilaoSolicitacaoControllerException
     */
    @Override
    public void cancelarProcessoLeilao(String adeCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            final SolicitacaoAutorizacao soaBean = obtemSolicitacaoLeilaoPendente(adeCodigo, responsavel);
            if (soaBean != null) {
                // Altera o status das propostas para rejeitada
                alterarStatusPropostas(adeCodigo, StatusPropostaEnum.AGUARDANDO_APROVACAO, StatusPropostaEnum.REJEITADA, responsavel);

                // Altera o status da solicitação de propostas
                soaBean.setStatusSolicitacao(new StatusSolicitacao(StatusSolicitacaoEnum.CANCELADA.getCodigo()));
                AbstractEntityHome.update(soaBean);

                // Inclui ocorrência de consignação informando que o processo foi cancelado
                autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.leilao.solicitacao.cancelado", responsavel), responsavel);
            }
        } catch (final UpdateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final ZetraException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new LeilaoSolicitacaoControllerException(ex);
        }
    }

    @Override
    public void cancelarProcessoLeilaoPorErro(String adeCodigo, String motivoErro, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            // Cancela o processo do leilão
            cancelarProcessoLeilao(adeCodigo, responsavel);

            // Cancela a consignação origem do leilão
            cancelarConsignacaoController.cancelar(adeCodigo, responsavel);

            // Registra ocorrência com a causa do erro no cancelamento de leilão
            final String ocaObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.oca.obs.leilao.cancelado.motivo", responsavel, motivoErro);
            autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AVISO, ocaObs, responsavel);
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new LeilaoSolicitacaoControllerException(ex);
        }
    }

    /**
     * Encerra leilão de solicitação aprovando a proposta com melhor taxa informada.
     *
     * @param solicitacao
     * @param responsavel
     * @throws LeilaoSolicitacaoControllerException
     */
    @Override
    public void encerrarLeilaoExpirado(String adeCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            final ListaSolicitacaoLeilaoSemPropostaQuery query = new ListaSolicitacaoLeilaoSemPropostaQuery();
            query.adeCodigo = adeCodigo;
            final boolean leilaoSemProposta = !(query.executarLista().isEmpty());
            boolean servidorComRestricao = false;

            final AutDesconto adeBean = AutDescontoHome.findByPrimaryKey(adeCodigo);
            final RegistroServidor rseBean = RegistroServidorHome.findByPrimaryKey(adeBean.getRegistroServidor().getRseCodigo());

            final String srsCodigo = rseBean.getStatusRegistroServidor().getSrsCodigo();

            if (CodedValues.SRS_BLOQUEADOS.contains(srsCodigo) || CodedValues.SRS_INATIVOS.contains(srsCodigo)) {
            	servidorComRestricao = true;
            }

            try {
            	autorizacaoController.verificaLimiteAoAprovarLeilao(adeCodigo, responsavel);
            } catch (final AutorizacaoControllerException ex) {
            	LOG.error(ex.getMessage(), ex);
            	servidorComRestricao = true;
            }

            if (leilaoSemProposta || servidorComRestricao) {
                // Se tiver apenas uma proposta, e esta tiver a mesma taxa da ADE, significa que não foi ofertada
                // nenhuma proposta nova pelas consignatárias. Desta forma, o leião deve ser cancelado.
            	// Caso o servidor tenha restrições, o leião também deve ser cancelado.
                cancelarProcessoLeilao(adeCodigo, responsavel);

                // Cancela a consignação origem do leilão
                cancelarConsignacaoController.cancelar(adeCodigo, responsavel);

            } else {
                final PropostaLeilaoSolicitacao proposta = PropostaLeilaoSolicitacaoHome.findByMelhorTaxa(adeCodigo);

                final ListaDadosPortabilidadeLeilaoQuery queryPortabilidade = new ListaDadosPortabilidadeLeilaoQuery();
                queryPortabilidade.adeCodigo = adeCodigo;
                final List <TransferObject> listaAdePortabilidade = queryPortabilidade.executarDTO();
                TransferObject adeOrigemPortabilidade = null;

                // Verifica se é leilão de portabilidade ou não
                if ((listaAdePortabilidade != null) && !listaAdePortabilidade.isEmpty()){
                    adeOrigemPortabilidade = listaAdePortabilidade.get(0);
                    final String csaOrigem = adeOrigemPortabilidade.getAttribute(Columns.CSA_CODIGO).toString();
                    final String csaDestino = proposta.getCsaCodigo();
                    adeOrigemPortabilidade.setAttribute("mesmaCsaOrigemDestino", csaOrigem.equals(csaDestino));
                }

                aprovarPropostaLeilaoSolicitacao(adeOrigemPortabilidade, adeCodigo, proposta.getPlsCodigo(), responsavel);
            }
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Altera o status das propostas de leilão de solicitação que estão na situação "statusAtual"
     * para a situação "statusNovo".
     * @param adeCodigo
     * @param statusAtual
     * @param statusNovo
     * @param responsavel
     * @throws LeilaoSolicitacaoControllerException
     */
    private void alterarStatusPropostas(String adeCodigo, StatusPropostaEnum statusAtual, StatusPropostaEnum statusNovo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            final List<PropostaLeilaoSolicitacao> propostas = PropostaLeilaoSolicitacaoHome.findByAdeStp(adeCodigo, statusAtual.getCodigo());
            if ((propostas != null) && !propostas.isEmpty()) {
                for (final PropostaLeilaoSolicitacao plsBean : propostas) {
                    plsBean.setStatusProposta(new StatusProposta(statusNovo.getCodigo()));
                    AbstractEntityHome.update(plsBean);

                    // Grava log da operação
                    final LogDelegate logDelegate = new LogDelegate(responsavel, Log.PROPOSTA_LEILAO_SOLICITACAO, Log.UPDATE, Log.LOG_INFORMACAO);
                    logDelegate.setPropostaLeilaoSolicitacao(plsBean.getPlsCodigo());
                    logDelegate.setAutorizacaoDesconto(adeCodigo);
                    logDelegate.setUsuario(responsavel.getUsuCodigo());
                    logDelegate.setConsignataria(plsBean.getConsignataria().getCsaCodigo());
                    logDelegate.setServico(plsBean.getServico().getSvcCodigo());
                    logDelegate.setStatusProposta(statusNovo.getCodigo());
                    logDelegate.add(ApplicationResourcesHelper.getMessage("mensagem.log.alteracao.status.proposta.leilao.solicitacao", responsavel));
                    logDelegate.write();
                }
            }
        } catch (FindException | UpdateException | LogControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Realiza a contagem da pesquisa para acompanhamento dos leilões de solicitação em aberto,
     * de acordo com os critérios de pesquisa.
     * @param criteriosPesquisa
     * @param responsavel
     * @return
     * @throws LeilaoSolicitacaoControllerException
     */
    @Override
    public int contarLeilaoSolicitacao(TransferObject criteriosPesquisa,  AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
            try {
                final ListaAcompanhamentoLeilaoSolicitacaoQuery query = new ListaAcompanhamentoLeilaoSolicitacaoQuery();
                query.count = true;
                query.adeCodigo = (String) criteriosPesquisa.getAttribute("ADE_CODIGO");
                query.tipoLeilao = (String) criteriosPesquisa.getAttribute("tipo");
                query.tipoFiltro = (String) criteriosPesquisa.getAttribute("filtro");
                query.dataAberturaIni = (String) criteriosPesquisa.getAttribute("dataAberturaIni");
                query.dataAberturaFim = (String) criteriosPesquisa.getAttribute("dataAberturaFim");
                query.horasFimLeilao = (String) criteriosPesquisa.getAttribute("horasFimLeilao");

                String csaCodigo = null;
                if (responsavel.isCseSupOrg()) {
                    csaCodigo = (String) criteriosPesquisa.getAttribute("CSA_CODIGO");
                } else if (responsavel.isCsa()) {
                    csaCodigo = responsavel.getCodigoEntidade();
                } else if (responsavel.isCor()) {
                    csaCodigo = responsavel.getCodigoEntidadePai();
                }
                query.csaCodigo = csaCodigo;

                query.rseMatricula = (String) criteriosPesquisa.getAttribute("RSE_MATRICULA");
                query.serCpf = (String) criteriosPesquisa.getAttribute("SER_CPF");
                query.cidCodigo = (String) criteriosPesquisa.getAttribute("CID_CODIGO");
                query.responsavel = responsavel;
                query.posCodigo = (String) criteriosPesquisa.getAttribute(FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR);
                query.rsePontuacao = (String) criteriosPesquisa.getAttribute("RSE_PONTUACAO");
                query.arrRisco = (String) criteriosPesquisa.getAttribute("ARR_RISCO");
                query.rseMargemLivre = criteriosPesquisa.getAttribute("RSE_MARGEM_LIVRE") != null ? criteriosPesquisa.getAttribute("RSE_MARGEM_LIVRE").toString() : null;

                return query.executarContador();
            } catch (final HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
        return 0;
    }

    /**
     * Realiza a pesquisa para acompanhamento dos leilões de solicitação em aberto,
     * de acordo com os critérios de pesquisa.
     * @param criteriosPesquisa
     * @param offset
     * @param count
     * @param responsavel
     * @return
     * @throws LeilaoSolicitacaoControllerException
     */
    @Override
    public List<TransferObject> acompanharLeilaoSolicitacao(TransferObject criteriosPesquisa, int offset, int count, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
            try {
                final ListaAcompanhamentoLeilaoSolicitacaoQuery query = new ListaAcompanhamentoLeilaoSolicitacaoQuery();
                query.adeCodigo = (String) criteriosPesquisa.getAttribute("ADE_CODIGO");
                query.tipoLeilao = (String) criteriosPesquisa.getAttribute("tipo");
                query.tipoFiltro = (String) criteriosPesquisa.getAttribute("filtro");
                query.dataAberturaIni = (String) criteriosPesquisa.getAttribute("dataAberturaIni");
                query.dataAberturaFim = (String) criteriosPesquisa.getAttribute("dataAberturaFim");
                query.horasFimLeilao = (String) criteriosPesquisa.getAttribute("horasFimLeilao");

                String csaCodigo = null;
                if (responsavel.isCseSupOrg()) {
                    csaCodigo = (String) criteriosPesquisa.getAttribute("CSA_CODIGO");
                } else if (responsavel.isCsa()) {
                    csaCodigo = responsavel.getCodigoEntidade();
                } else if (responsavel.isCor()) {
                    csaCodigo = responsavel.getCodigoEntidadePai();
                }
                query.csaCodigo = csaCodigo;

                query.rseMatricula = (String) criteriosPesquisa.getAttribute("RSE_MATRICULA");
                query.serCpf = (String) criteriosPesquisa.getAttribute("SER_CPF");
                query.cidCodigo = (String) criteriosPesquisa.getAttribute("CID_CODIGO");
                query.responsavel = responsavel;
                query.posCodigo = (String) criteriosPesquisa.getAttribute(FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR);
                query.rsePontuacao = (String) criteriosPesquisa.getAttribute("RSE_PONTUACAO");
                query.arrRisco = (String) criteriosPesquisa.getAttribute("ARR_RISCO");
                query.rseMargemLivre = (String) criteriosPesquisa.getAttribute("RSE_MARGEM_LIVRE");

                if (!TextHelper.isNull(criteriosPesquisa.getAttribute("ORDENACAO"))) {
                    query.ordenacao = criteriosPesquisa.getAttribute("ORDENACAO").toString();
                }

                if (count != -1) {
                    query.maxResults = count;
                    query.firstResult = offset;
                }

                return query.executarDTO();
            } catch (final HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
        return null;
    }

    @Override
    public void informarPropostaLeilaoSolicitacao(String adeCodigo, String svcCodigo, String csaCodigoLeilao, BigDecimal taxaJuros, boolean validaTaxaJuros, String rseCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        informarPropostaLeilaoSolicitacao(adeCodigo, svcCodigo, csaCodigoLeilao, taxaJuros, null, null, null, null, validaTaxaJuros, rseCodigo, null, true, responsavel);
    }

    /**
     * Realiza a inclusão / atualização da proposta de leilão de solicitação para a
     * consignatária do usuário responsável pela operação.
     * @param adeCodigo
     * @param svcCodigo
     * @param csaCodigoLeilao
     * @param taxaJuros
     * @param taxaMinOfertaAut
     * @param decrementoOfertaAut
     * @param emailOfertaAut
     * @param responsavel
     * @param txtContatoCsa
     * @param validaTaxaJuros
     * @param rseCodigo
     * @param dataProposta
     * @param processarOfertasAutomaticas
     * @throws LeilaoSolicitacaoControllerException
     */
    @Override
    public void informarPropostaLeilaoSolicitacao(String adeCodigo, String svcCodigo, String csaCodigoLeilao, BigDecimal taxaJuros, BigDecimal taxaMinOfertaAut, BigDecimal decrementoOfertaAut, String emailOfertaAut, String txtContatoCsa, boolean validaTaxaJuros, String rseCodigo, Date dataProposta, boolean processarOfertasAutomaticas, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {

        boolean bloqueioProposta;
        try {
            String tpaPermiteOferecerProposta;
            tpaPermiteOferecerProposta = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_PERMITE_OFERECER_PROPOSTA_LEILAO, responsavel);
            bloqueioProposta = (!TextHelper.isNull(tpaPermiteOferecerProposta) && !"S".equals(tpaPermiteOferecerProposta));
        } catch (final ParametroControllerException e) {
            LOG.error(e.getMessage(), e);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }

        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, responsavel) && !bloqueioProposta) {
            // Recupera a solicitação de propostas que deve estar pendente
            final SolicitacaoAutorizacao soaBean = obtemSolicitacaoLeilaoPendente(adeCodigo, responsavel);

            boolean enviaEmailNovoLeilao = false;
            BigDecimal valorLiberado = null;
            Integer prazo = null;
            BigDecimal valorParcela = null;
            String adeUsuCodigo = null;
            Long adeNumero = null;

            final Date dataValidadeLeilao = soaBean.getSoaDataValidade();
            final Date dataAtual = DateHelper.getSystemDatetime();
            if ((soaBean == null) || soaBean.getStatusSolicitacao().getSsoCodigo().equals(StatusSolicitacaoEnum.EXPIRADA.getCodigo())) {
                throw new LeilaoSolicitacaoControllerException("mensagem.erro.status.leilao.solicitacao.nao.permite.proposta", responsavel);
            }

            if ((dataValidadeLeilao != null) && dataAtual.after(dataValidadeLeilao) && TextHelper.isNull(dataProposta)) {
                throw new LeilaoSolicitacaoControllerException("mensagem.erro.leilao.solicitacao.nova.proposta.apos.termino", responsavel, DateHelper.format(dataValidadeLeilao, LocaleHelper.getDateTimePattern()));
            }

            final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

            // valida campo taxa juros
            if (TextHelper.isNull(taxaJuros)) {
              if (temCET) {
                  throw new LeilaoSolicitacaoControllerException("mensagem.informe.proposta.leilao.solicitacao.cet", responsavel);
              } else {
                  throw new LeilaoSolicitacaoControllerException("mensagem.informe.proposta.leilao.solicitacao.taxa.juros", responsavel);
              }
            }

            if (taxaJuros.compareTo(BigDecimal.ZERO) <= 0) {
              if (temCET) {
                  throw new LeilaoSolicitacaoControllerException("mensagem.erro.cet.proposta.leilao.solicitacao.incorreto", responsavel);
              } else {
                  throw new LeilaoSolicitacaoControllerException("mensagem.erro.taxa.juros.proposta.leilao.solicitacao.incorreto", responsavel);
              }
            }

            // Consignatária deverá sempre ser a do usuário, e não a do contrato
            String csaCodigo = null;
            if ((responsavel != null) && (responsavel.isSer() || responsavel.isSistema())) {
                csaCodigo = csaCodigoLeilao;
            } else {
                csaCodigo = responsavel.getCsaCodigo();
            }

            // Verifica se já existe proposta
            PropostaLeilaoSolicitacao plsBean = null;
            try {
                plsBean = PropostaLeilaoSolicitacaoHome.findByAdeCsa(adeCodigo, csaCodigo);

                // Se proposta existe e a taxa não foi alterada, então não valida a taxa de juros novamente
                if (plsBean.getPlsTaxaJuros().doubleValue() == taxaJuros.doubleValue()) {
                    validaTaxaJuros = false;
                }
            } catch (final FindException ex) {
            }

            try {
                // Obtém os entity beans necessários
                final AutDesconto adeBean = AutDescontoHome.findByPrimaryKey(adeCodigo);
                final VerbaConvenio vcoBean = VerbaConvenioHome.findByPrimaryKey(adeBean.getVerbaConvenio().getVcoCodigo());
                final Convenio cnvBean = ConvenioHome.findByPrimaryKey(vcoBean.getConvenio().getCnvCodigo());
                final String orgCodigo = cnvBean.getOrgao().getOrgCodigo();
                final String adePeriodicidade = adeBean.getAdePeriodicidade();

                valorLiberado = adeBean.getAdeVlrLiquido();
                prazo = adeBean.getAdePrazo();
                adeUsuCodigo = adeBean.getUsuario().getUsuCodigo();
                adeNumero = adeBean.getAdeNumero();

                if (validaTaxaJuros) {
                    valorParcela = calcularValorPrestacao(adeCodigo, NumberHelper.format(taxaJuros.doubleValue(), "en"), responsavel);

                    // Valida os dados da proposta
                    if ((valorParcela == null) || (valorParcela.signum() <= 0)) {
                        throw new LeilaoSolicitacaoControllerException("mensagem.erro.valor.parcela.proposta.leilao.solicitacao.incorreto", responsavel);
                    }
                    if (valorParcela.compareTo(adeBean.getAdeVlr()) > 0) {
                        throw new LeilaoSolicitacaoControllerException("mensagem.erro.valor.parcela.proposta.leilao.solicitacao.maior.atual", responsavel);
                    }
                    if (TextHelper.isNull(svcCodigo)) {
                        throw new LeilaoSolicitacaoControllerException("mensagem.informe.servico", responsavel);
                    }

                    // se a taxa não for menor do que a melhor proposta da erro
                    final BigDecimal menorProposta = obterMelhorTaxaLeilao(adeCodigo, responsavel);
                    if ((menorProposta != null) && (taxaJuros.compareTo(menorProposta) >= 0)) {
                        if (temCET) {
                            throw new LeilaoSolicitacaoControllerException("mensagem.erro.cet.maior.melhor.proposta.leilao.solicitacao", responsavel);
                        } else {
                            throw new LeilaoSolicitacaoControllerException("mensagem.erro.taxa.juros.maior.melhor.proposta.leilao.solicitacao", responsavel);
                        }
                    }

                    try {
                        final Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
                        autorizacaoController.validarTaxaJuros(valorParcela, valorLiberado, null, null, null,
                				prazo, dataAtual, periodoAtual, svcCodigo, csaCodigo, orgCodigo, false, null,
                				adePeriodicidade, rseCodigo, responsavel);
                    } catch (final AutorizacaoControllerException ex) {
                        final String errorKey = ex.getMessageKey();
                        if (errorKey != null) {
                            if ("mensagem.aviso.sem.cet.prazo.csa".equals(errorKey) || "mensagem.aviso.sem.taxa.prazo.csa".equals(errorKey)) {
                                throw new LeilaoSolicitacaoControllerException("mensagem.erro.cet.inexistente.prazo", responsavel, ex);
                            } else if ("mensagem.erro.cet.calculado.maior.anunciado".equals(errorKey) || "mensagem.erro.taxa.calculada.maior.anunciado".equals(errorKey)) {
                                //DESENV-19717: Não colocamos aqui o valor do cet e da parcela, pois no leilão o contrato é criado automaticamente com o menor CET disponível
                                //assim as outras consignatárias precisam informar um cet menor, por isso o maior não acontece.
                                throw new LeilaoSolicitacaoControllerException("mensagem.erro.cet.superior.anunciado.simples", responsavel, ex);
                            }
                        }
                        throw new LeilaoSolicitacaoControllerException(ex);
                    }
                } else {
                    valorParcela = (plsBean != null ? plsBean.getPlsValorParcela() : adeBean.getAdeVlr());
                }
            } catch (FindException | PeriodoException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }

            try {
                boolean ofertaMudou = false;

                if (plsBean != null) {
                    // Proposta existe, verifica se os valores foram alterados para proceder modificação
                    if ((plsBean.getPlsValorParcela().doubleValue() != valorParcela.doubleValue()) ||
                            (plsBean.getPlsTaxaJuros().doubleValue() != taxaJuros.doubleValue()) ||
                            (!plsBean.getServico().getSvcCodigo().equals(svcCodigo)) ||

                            ((plsBean.getPlsOfertaAutDecremento() != null) && (decrementoOfertaAut == null)) ||
                            ((plsBean.getPlsOfertaAutDecremento() == null) && (decrementoOfertaAut != null)) ||
                            ((plsBean.getPlsOfertaAutDecremento() != null) && (decrementoOfertaAut != null) && (plsBean.getPlsOfertaAutDecremento().doubleValue() != decrementoOfertaAut.doubleValue())) ||

                            ((plsBean.getPlsOfertaAutTaxaMin() != null) && (taxaMinOfertaAut == null)) ||
                            ((plsBean.getPlsOfertaAutTaxaMin() == null) && (taxaMinOfertaAut != null)) ||
                            ((plsBean.getPlsOfertaAutTaxaMin() != null) && (taxaMinOfertaAut != null) && (plsBean.getPlsOfertaAutTaxaMin().doubleValue() != taxaMinOfertaAut.doubleValue())) ||

                            ((plsBean.getPlsOfertaAutEmail() != null) && (emailOfertaAut == null)) ||
                            ((plsBean.getPlsOfertaAutEmail() == null) && (emailOfertaAut != null)) ||
                            ((plsBean.getPlsOfertaAutEmail() != null) && (emailOfertaAut != null) && !plsBean.getPlsOfertaAutEmail().equals(emailOfertaAut)) ||

                            ((plsBean.getPlsTxtContatoCsa() != null) && (txtContatoCsa == null)) ||
                            ((plsBean.getPlsTxtContatoCsa() == null) && (txtContatoCsa != null)) ||
                            ((plsBean.getPlsTxtContatoCsa() != null) && (txtContatoCsa != null) && !plsBean.getPlsTxtContatoCsa().equals(txtContatoCsa))

                            ) {

                        // Proposta alterada, verifica se o status permite a alteração
                        final StatusPropostaEnum statusAtual = StatusPropostaEnum.recuperaStatusProposta(plsBean.getStatusProposta().getStpCodigo());
                        if (!statusAtual.equals(StatusPropostaEnum.AGUARDANDO_APROVACAO)) {
                            throw new LeilaoSolicitacaoControllerException("mensagem.erro.status.proposta.leilao.solicitacao.nao.permite.alteracao", responsavel);
                        }

                        // Grava log da operação
                        final LogDelegate logDelegate = new LogDelegate(responsavel, Log.PROPOSTA_LEILAO_SOLICITACAO, Log.UPDATE, Log.LOG_INFORMACAO);
                        logDelegate.setPropostaLeilaoSolicitacao(plsBean.getPlsCodigo());
                        logDelegate.setAutorizacaoDesconto(adeCodigo);
                        logDelegate.setUsuario(responsavel.getUsuCodigo());
                        logDelegate.setConsignataria(csaCodigo);
                        logDelegate.setServico(svcCodigo);
                        logDelegate.setStatusProposta(StatusPropostaEnum.AGUARDANDO_APROVACAO.getCodigo());

                        if (plsBean.getPlsValorParcela().doubleValue() != valorParcela.doubleValue()) {
                            logDelegate.addChangedField(Columns.PLS_VALOR_PARCELA, valorParcela, plsBean.getPlsValorParcela());
                            plsBean.setPlsValorParcela(valorParcela);
                        }
                        if (plsBean.getPlsTaxaJuros().doubleValue() != taxaJuros.doubleValue()) {
                            logDelegate.addChangedField(Columns.PLS_TAXA_JUROS, taxaJuros, plsBean.getPlsTaxaJuros());
                            plsBean.setPlsTaxaJuros(taxaJuros);

                            // Atualiza a data de cadastrdo da proposta, pois as ofertas automáticas são
                            // feitas ordenadas por esta data, de modo que todos tenham chance
                            if (dataProposta != null) {
                                plsBean.setPlsDataCadastro(dataProposta);
                            } else {
                                plsBean.setPlsDataCadastro(DateHelper.getSystemDatetime());
                                dataProposta = DateHelper.getSystemDatetime();
                            }
                            ofertaMudou = true;
                        }
                        if (!plsBean.getServico().getSvcCodigo().equals(svcCodigo)) {
                            plsBean.setServico(ServicoHome.findByPrimaryKey(svcCodigo));
                        }
                        if (((plsBean.getPlsOfertaAutDecremento() != null) && (decrementoOfertaAut == null)) ||
                            ((plsBean.getPlsOfertaAutDecremento() == null) && (decrementoOfertaAut != null)) ||
                            ((plsBean.getPlsOfertaAutDecremento() != null) && (decrementoOfertaAut != null) && (plsBean.getPlsOfertaAutDecremento().doubleValue() != decrementoOfertaAut.doubleValue()))) {
                            logDelegate.addChangedField(Columns.PLS_OFERTA_AUT_DECREMENTO, decrementoOfertaAut, plsBean.getPlsOfertaAutDecremento());
                            plsBean.setPlsOfertaAutDecremento(decrementoOfertaAut);
                        }
                        if (((plsBean.getPlsOfertaAutTaxaMin() != null) && (taxaMinOfertaAut == null)) ||
                            ((plsBean.getPlsOfertaAutTaxaMin() == null) && (taxaMinOfertaAut != null)) ||
                            ((plsBean.getPlsOfertaAutTaxaMin() != null) && (taxaMinOfertaAut != null) && (plsBean.getPlsOfertaAutTaxaMin().doubleValue() != taxaMinOfertaAut.doubleValue()))) {
                            logDelegate.addChangedField(Columns.PLS_OFERTA_AUT_TAXA_MIN, taxaMinOfertaAut, plsBean.getPlsOfertaAutTaxaMin());
                            plsBean.setPlsOfertaAutTaxaMin(taxaMinOfertaAut);
                        }
                        if (((plsBean.getPlsOfertaAutEmail() != null) && (emailOfertaAut == null)) ||
                            ((plsBean.getPlsOfertaAutEmail() == null) && (emailOfertaAut != null)) ||
                            ((plsBean.getPlsOfertaAutEmail() != null) && (emailOfertaAut != null) && !plsBean.getPlsOfertaAutEmail().equals(emailOfertaAut))) {
                            logDelegate.addChangedField(Columns.PLS_OFERTA_AUT_EMAIL, emailOfertaAut, plsBean.getPlsOfertaAutEmail());
                            plsBean.setPlsOfertaAutEmail(emailOfertaAut);
                        }
                        if (((plsBean.getPlsTxtContatoCsa() != null) && (txtContatoCsa == null)) ||
                            ((plsBean.getPlsTxtContatoCsa() == null) && (txtContatoCsa != null)) ||
                            ((plsBean.getPlsTxtContatoCsa() != null) && (txtContatoCsa != null) && !plsBean.getPlsTxtContatoCsa().equals(txtContatoCsa))) {
                            logDelegate.addChangedField(Columns.PLS_TXT_CONTATO_CSA, txtContatoCsa, plsBean.getPlsTxtContatoCsa());
                            plsBean.setPlsTxtContatoCsa(txtContatoCsa);
                        }

                        // Efetua a atualização e gravação de log das alterações
                        AbstractEntityHome.update(plsBean);
                        logDelegate.write();
                    }

                } else {
                    // Não existe proposta, então cria uma nova
                    final ObtemMaiorNumeroPropostaLeilaoQuery maiorNumQuery = new ObtemMaiorNumeroPropostaLeilaoQuery();
                    maiorNumQuery.adeCodigo = adeCodigo;
                    final Integer numero = maiorNumQuery.executarContador() + 1;

                    if (numero == 1) {
                        // Se está criando a primeira proposta, então envia e-mail de notificação de novo leilão
                        enviaEmailNovoLeilao = true;
                    }

                    // Grava os dados da proposta
                    plsBean = PropostaLeilaoSolicitacaoHome.create(adeCodigo, csaCodigo, svcCodigo, responsavel.getUsuCodigo(),
                            StatusPropostaEnum.AGUARDANDO_APROVACAO.getCodigo(), numero, valorLiberado, valorParcela, prazo, taxaJuros, dataAtual, dataValidadeLeilao,
                            decrementoOfertaAut, taxaMinOfertaAut, emailOfertaAut, txtContatoCsa);

                    ofertaMudou = true;

                    // Grava log da operação
                    final LogDelegate logDelegate = new LogDelegate(responsavel, Log.PROPOSTA_LEILAO_SOLICITACAO, Log.CREATE, Log.LOG_INFORMACAO);
                    logDelegate.setPropostaLeilaoSolicitacao(plsBean.getPlsCodigo());
                    logDelegate.setAutorizacaoDesconto(adeCodigo);
                    logDelegate.setUsuario(responsavel.getUsuCodigo());
                    logDelegate.setConsignataria(csaCodigo);
                    logDelegate.setServico(svcCodigo);
                    logDelegate.setStatusProposta(StatusPropostaEnum.AGUARDANDO_APROVACAO.getCodigo());
                    logDelegate.addChangedField(Columns.PLS_VALOR_LIBERADO, valorLiberado);
                    logDelegate.addChangedField(Columns.PLS_VALOR_PARCELA, valorParcela);
                    logDelegate.addChangedField(Columns.PLS_TAXA_JUROS, taxaJuros);
                    logDelegate.addChangedField(Columns.PLS_PRAZO, prazo);
                    logDelegate.addChangedField(Columns.PLS_NUMERO, numero);
                    logDelegate.addChangedField(Columns.PLS_DATA_VALIDADE, dataValidadeLeilao);

                    if (decrementoOfertaAut != null) {
                        logDelegate.addChangedField(Columns.PLS_OFERTA_AUT_DECREMENTO, decrementoOfertaAut);
                    }
                    if (taxaMinOfertaAut != null) {
                        logDelegate.addChangedField(Columns.PLS_OFERTA_AUT_TAXA_MIN, taxaMinOfertaAut);
                    }
                    logDelegate.addChangedField(Columns.PLS_OFERTA_AUT_EMAIL, emailOfertaAut);
                    logDelegate.addChangedField(Columns.PLS_TXT_CONTATO_CSA, txtContatoCsa);

                    logDelegate.write();
                }

                // Notifica consignatárias sobre a abertura do novo leilão
                try {
                    if (enviaEmailNovoLeilao) {
                        EnviaEmailHelper.enviarEmailNotificacaoNovoLeilao(soaBean, responsavel);

                        //Cria a thread para ser executada depois de um minuto
                        //Isso é feito para evitar que os dados não tenham sido salvos na transação anterior
                        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                        executor.schedule(new ProcessaEnviarEmailNotificacaoNovoLeilaoByFiltro(soaBean, responsavel), 1, TimeUnit.MINUTES);
                    }
                } catch (final ViewHelperException ex) {
                    // Problema no envio de email não interrompe o processo de leilão
                    LOG.error(ex.getMessage(), ex);
                }

                // Cria nova notificação para envio ao dispositivo do usuário
                try {
                    // Não cria notificação para proposta inicial
                    if (!enviaEmailNovoLeilao) {
                        final String deviceToken = usuarioController.findDeviceToken(adeUsuCodigo, responsavel);
                        if (!TextHelper.isNull(deviceToken)) {
                            final ObjectMapper mapper = new ObjectMapper();

                            final String strJsonBody = "{"
                                    +   "\"ade_codigo\": \""+ adeCodigo + "\","
                                    +   "\"ade_numero\": \""+ adeNumero + "\","
                                    +   "\"soa_data\": \""+ DateHelper.toISOString(soaBean.getSoaData()) + "\","
                                    +   "\"soa_data_validade\": \""+  DateHelper.toISOString(dataValidadeLeilao) + "\","
                                    +   "\"ade_vlr\": \""+ mapper.writeValueAsString(valorParcela) + "\","
                                    +   "\"ade_vlr_liquido\": \""+ mapper.writeValueAsString(valorLiberado) + "\","
                                    +   "\"ade_prazo\": \""+ mapper.writeValueAsString(prazo) + "\","
                                    +   "\"tno_codigo\": "+ mapper.writeValueAsString(TipoNotificacaoEnum.NOVA_PROPOSTA_LEILAO.getCodigo()) + ","
                                    +   "\"pls_taxa_juros\": \""+ mapper.writeValueAsString(taxaJuros) + "\""
                                    + "}";

                            notificacaoDispositivoController.createNotificacaoDispositivo(responsavel.getUsuCodigo(), adeUsuCodigo, CodedValues.FUN_INFORMAR_PROPOSTAS_LEILAO, strJsonBody,
                                    DateHelper.getSystemDatetime(), null, CodedValues.NDI_ATIVO,
                                    TipoNotificacaoEnum.NOVA_PROPOSTA_LEILAO.getCodigo(), responsavel);
                        }
                    }
                } catch (ZetraException | JsonGenerationException | IOException ex) {
                    // Problema ao criar notificação
                    LOG.error(ex.getMessage(), ex);
                }

                if (processarOfertasAutomaticas && ofertaMudou) {
                    // Inicia processo paralelo para cadastro das ofertas automáticas
                    // Cria a thread para ser executada depois de 5 segundos
                    // Isso é feito para evitar que os dados não tenham sido salvos na transação atual no momento em que a nova thread precisar deles
                    final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                    executor.schedule(new ProcessaOfertaAutomaticaLeilao(adeCodigo, dataProposta), 5, TimeUnit.SECONDS);
                }

            } catch (CreateException | UpdateException | FindException | HQueryException | LogControllerException ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                LOG.error(ex.getMessage(), ex);
                throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    /**
     * Aprova uma proposta de leilão de solicitação, efetivando a inclusão de nova consignação
     * com os dados da proposta, e cancelando a solicitação original.
     * @param portabilidade
     * @param adeCodigo
     * @param plsCodigo
     * @param responsavel
     * @throws LeilaoSolicitacaoControllerException
     */
    @Override
    public String aprovarPropostaLeilaoSolicitacao(TransferObject portabilidade, String adeCodigo, String plsCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
            // Recupera a solicitação de propostas que deve estar pendente ou com prazo expirado
            final SolicitacaoAutorizacao soaBean = obtemSolicitacaoLeilaoPendente(adeCodigo, responsavel);
            if (soaBean == null) {
                throw new LeilaoSolicitacaoControllerException("mensagem.erro.status.leilao.solicitacao.nao.permite.aprovacao", responsavel);
            }

            try {
                final PropostaLeilaoSolicitacao plsBean = PropostaLeilaoSolicitacaoHome.findByPrimaryKey(plsCodigo);
                final String csaCodigo = plsBean.getConsignataria().getCsaCodigo();
                final String svcCodigo = plsBean.getServico().getSvcCodigo();

                // Altera o status da proposta aprovada
                plsBean.setStatusProposta(new StatusProposta(StatusPropostaEnum.APROVADA.getCodigo()));
                AbstractEntityHome.update(plsBean);

                // Altera o status das demais propostas para rejeitada
                alterarStatusPropostas(adeCodigo, StatusPropostaEnum.AGUARDANDO_APROVACAO, StatusPropostaEnum.REJEITADA, responsavel);

                // Altera o status da solicitação de propostas para finalizada
                soaBean.setStatusSolicitacao(new StatusSolicitacao(StatusSolicitacaoEnum.FINALIZADA.getCodigo()));
                AbstractEntityHome.update(soaBean);

                // Cancela a consignação no status solicitação
                cancelarConsignacaoController.cancelar(adeCodigo, responsavel);

                // Grava log da aprovação da proposta
                final LogDelegate logDelegate = new LogDelegate(responsavel, Log.PROPOSTA_LEILAO_SOLICITACAO, Log.UPDATE, Log.LOG_INFORMACAO);
                logDelegate.setPropostaLeilaoSolicitacao(plsBean.getPlsCodigo());
                logDelegate.setAutorizacaoDesconto(adeCodigo);
                logDelegate.setUsuario(responsavel.getUsuCodigo());
                logDelegate.setConsignataria(csaCodigo);
                logDelegate.setServico(svcCodigo);
                logDelegate.setStatusProposta(StatusPropostaEnum.APROVADA.getCodigo());
                logDelegate.add(ApplicationResourcesHelper.getMessage("mensagem.log.alteracao.status.proposta.leilao.solicitacao", responsavel));
                logDelegate.write();

                // Busca os dados necessários para a inclusão da nova consignação
                final AutDesconto adeBean = AutDescontoHome.findByPrimaryKey(adeCodigo);
                final RegistroServidor rseBean = RegistroServidorHome.findByPrimaryKey(adeBean.getRegistroServidor().getRseCodigo());
                final String rseCodigo = rseBean.getRseCodigo();
                final String orgCodigo = rseBean.getOrgao().getOrgCodigo();
                final String corCodigo = (adeBean.getCorrespondente() != null ? adeBean.getCorrespondente().getCorCodigo() : null);

                // Localiza o convênio do novo contrato
                final Convenio cnvBean = ConvenioHome.findByChave(svcCodigo, csaCodigo, orgCodigo);
                final String cnvCodigo = cnvBean.getCnvCodigo();

                // Obtém os parâmetros de serviço necessários
                final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                int carencia = ((paramSvcCse.getTpsCarenciaMinima() != null) && !"".equals(paramSvcCse.getTpsCarenciaMinima())) ? Integer.parseInt(paramSvcCse.getTpsCarenciaMinima()) : 0;

                // Obtém os parâmetros de serviço por consignatária necessários
                final List<String> tpsCodigosCnv = new ArrayList<>();
                tpsCodigosCnv.add(CodedValues.TPS_CARENCIA_MINIMA);
                final List<TransferObject> paramSvcCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigosCnv, false, responsavel);
                if ((paramSvcCsa != null) && (paramSvcCsa.size() > 0)) {
                    final TransferObject param = paramSvcCsa.get(0);
                    if ((param != null) && (param.getAttribute(Columns.PSC_VLR) != null) && CodedValues.TPS_CARENCIA_MINIMA.equals(param.getAttribute(Columns.TPS_CODIGO))) {
                        final int carenciaMinimaCnv = (!param.getAttribute(Columns.PSC_VLR).toString().isEmpty()) ? Integer.parseInt((String)param.getAttribute(Columns.PSC_VLR)) : 0;
                        if (carenciaMinimaCnv > carencia) {
                            carencia = carenciaMinimaCnv;
                        }
                    }
                }

                // Define os valores da nova consignação
                final BigDecimal adeVlr = plsBean.getPlsValorParcela();
                final BigDecimal adeVlrLiquido = plsBean.getPlsValorLiberado();
                final BigDecimal adeTaxaJuros = plsBean.getPlsTaxaJuros();
                final int adePrazo = plsBean.getPlsPrazo();
                final int adeCarencia = parametroController.calcularAdeCarenciaDiaCorteCsa(carencia, csaCodigo, orgCodigo, responsavel);
                final String adePeriodicidade = adeBean.getAdePeriodicidade();
                final String adeIdentificador = adeBean.getAdeIdentificador();
                final String adeTipoVlr = paramSvcCse.getTpsTipoVlr();
                final short adeIntFolha = paramSvcCse.getTpsIntegraFolha();
                final short adeIncMargem = paramSvcCse.getTpsIncideMargem();

                // Recupera a informação de dados autorização desconto do telefone informado pelo Servidor
                String tdaTelSolicitacaoSer = null;
                try {
                    final DadosAutorizacaoDesconto dado = DadosAutorizacaoDescontoHome.findByPrimaryKey(new DadosAutorizacaoDescontoId(adeCodigo, CodedValues.TDA_SOLICITACAO_TEL_SERVIDOR));
                    tdaTelSolicitacaoSer = dado.getDadValor();
                } catch (final FindException ex) {
                    LOG.error(ex.getMessage(), ex);
                }

                String adeCodigoNovo = null;

                if (portabilidade != null) {
                    final boolean mesmaCsaOrigemDestino = (Boolean) portabilidade.getAttribute("mesmaCsaOrigemDestino");
                    final String adeCodigoOrigem = portabilidade.getAttribute(Columns.ADE_CODIGO).toString();

                    // Cria objeto com os parâmetros para uma renegociação/portabilidade
                    final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();

                    renegociarParam.setTipo(AcessoSistema.ENTIDADE_SER);
                    renegociarParam.setRseCodigo(rseCodigo);
                    renegociarParam.setCnvCodigo(cnvCodigo);
                    renegociarParam.setCorCodigo(corCodigo);
                    renegociarParam.setAdePeriodicidade(adePeriodicidade);
                    renegociarParam.setAdePrazo(adePrazo);
                    renegociarParam.setAdeCarencia(adeCarencia);
                    renegociarParam.setAdeVlr(adeVlr);
                    renegociarParam.setAdeVlrLiquido(adeVlrLiquido);
                    renegociarParam.setAdeTaxaJuros(adeTaxaJuros);
                    renegociarParam.setAdeIdentificador(adeIdentificador);
                    renegociarParam.setAdeCodigosRenegociacao(List.of(adeCodigoOrigem));
                    renegociarParam.setCompraContrato(!mesmaCsaOrigemDestino);
                    renegociarParam.setAcao(!mesmaCsaOrigemDestino ? "COMPRAR" : "RENEGOCIAR");
                    renegociarParam.setComSerSenha(Boolean.FALSE);
                    renegociarParam.setComSerSenha(Boolean.FALSE);
                    renegociarParam.setValidar(Boolean.FALSE);
                    renegociarParam.setPermitirValidacaoTaxa(Boolean.TRUE);
                    renegociarParam.setSerAtivo(Boolean.TRUE);
                    renegociarParam.setCnvAtivo(Boolean.TRUE);
                    renegociarParam.setSerCnvAtivo(Boolean.TRUE);
                    renegociarParam.setSvcAtivo(Boolean.TRUE);
                    renegociarParam.setCsaAtivo(Boolean.FALSE);
                    renegociarParam.setOrgAtivo(Boolean.TRUE);
                    renegociarParam.setEstAtivo(Boolean.TRUE);
                    renegociarParam.setCseAtivo(Boolean.TRUE);
                    renegociarParam.setDestinoAprovacaoLeilaoReverso(true);
                    renegociarParam.setConsomeSenha(Boolean.FALSE);
                    renegociarParam.setValidaAnexo(Boolean.FALSE);
                    renegociarParam.setValidaBloqSerCnvCsa(Boolean.FALSE);
                    renegociarParam.setTdaTelSolicitacaoSer(tdaTelSolicitacaoSer);
                    renegociarParam.setCidCodigo(adeBean.getCidade() != null ? adeBean.getCidade().getCidCodigo() : null);

                    // Executa renegociação/portabilidade de acordo com o vencedor do leilão
                    adeCodigoNovo = renegociarConsignacaoController.renegociar(renegociarParam, responsavel);

                } else {
                    // Cria objeto com os parâmetros para uma nova reserva
                    final ReservarMargemParametros reservaParam = new ReservarMargemParametros();

                    reservaParam.setRseCodigo(rseCodigo);
                    reservaParam.setCnvCodigo(cnvCodigo);
                    reservaParam.setCorCodigo(corCodigo);
                    reservaParam.setSadCodigo(CodedValues.SAD_SOLICITADO);
                    reservaParam.setAdePeriodicidade(adePeriodicidade);
                    reservaParam.setAdePrazo(adePrazo);
                    reservaParam.setAdeCarencia(adeCarencia);
                    reservaParam.setAdeVlr(adeVlr);
                    reservaParam.setAdeVlrLiquido(adeVlrLiquido);
                    reservaParam.setAdeTaxaJuros(adeTaxaJuros);
                    reservaParam.setAdeTipoVlr(adeTipoVlr);
                    reservaParam.setAdeIntFolha(adeIntFolha);
                    reservaParam.setAdeIncMargem(adeIncMargem);
                    reservaParam.setAdeIdentificador(adeIdentificador);
                    reservaParam.setComSerSenha(Boolean.FALSE);
                    reservaParam.setValidar(Boolean.FALSE);
                    reservaParam.setPermitirValidacaoTaxa(Boolean.TRUE);
                    reservaParam.setSerAtivo(Boolean.TRUE);
                    reservaParam.setCnvAtivo(Boolean.TRUE);
                    reservaParam.setSerCnvAtivo(Boolean.TRUE);
                    reservaParam.setSvcAtivo(Boolean.TRUE);
                    reservaParam.setCsaAtivo(Boolean.FALSE);
                    reservaParam.setOrgAtivo(Boolean.TRUE);
                    reservaParam.setEstAtivo(Boolean.TRUE);
                    reservaParam.setCseAtivo(Boolean.TRUE);
                    reservaParam.setAcao("RESERVAR");
                    reservaParam.setDestinoAprovacaoLeilaoReverso(true);
                    reservaParam.setConsomeSenha(Boolean.FALSE);
                    reservaParam.setValidaAnexo(Boolean.FALSE);
                    reservaParam.setValidaBloqSerCnvCsa(Boolean.FALSE);
                    reservaParam.setTdaTelSolicitacaoSer(tdaTelSolicitacaoSer);
                    reservaParam.setCidCodigo(adeBean.getCidade() != null ? adeBean.getCidade().getCidCodigo() : null);

                    // Cria nova consignação com os dados vencedor
                    adeCodigoNovo = reservarMargemController.reservarMargem(reservaParam, responsavel);
                }

                // Cria relacionamento de leilão entre a nova consignação e a geradora do leilão
                RelacionamentoAutorizacaoHome.create(adeCodigo, adeCodigoNovo, CodedValues.TNT_LEILAO_SOLICITACAO, responsavel.getUsuCodigo());

                // Cria ocorrências de informação sobre o encerramento do leilão, e da inclusão via leilão
                autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.leilao.solicitacao.finalizado", responsavel), responsavel);
                autorizacaoController.criaOcorrenciaADE(adeCodigoNovo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.inclusao.via.leilao.solicitacao", responsavel), responsavel);

                // Busca usuário servidor para envio de e-mail e notificações
                final TransferObject usuarioSer = usuarioController.getSenhaServidor(rseCodigo, responsavel);
                if (usuarioSer == null) {
                    throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel);
                }
                final String usuCodigoServidor = usuarioSer.getAttribute(Columns.USU_CODIGO).toString();

                // Gera codigo de autorização da solicitação para ser utilizado na confirmação do contrato
                final boolean exigeCodAutSolicitacao = paramSvcCse.isTpsExigeCodAutorizacaoSolic();
                String codigoAutorizacaoSolicitacao = "";
                if (exigeCodAutSolicitacao) {
                    // Cria código de autorização para a solicitação
                    codigoAutorizacaoSolicitacao = GeradorSenhaUtil.getPasswordNumber(4, responsavel);
                    autorizacaoController.setDadoAutDesconto(adeCodigoNovo, CodedValues.TDA_CODIGO_AUTORIZACAO_SOLICITACAO, codigoAutorizacaoSolicitacao, responsavel);
                }
                // Gera código único, caso o sistema esteja configurado para usar múltiplas senhas de aut. e a operação de confirmação de contrato consuma senha.
                String novaSenhaPlana = "";
                final boolean usaMultiplasSenhasAut = ParamSist.paramEquals(CodedValues.TPC_USA_MULTIPLAS_SENHAS_AUTORIZACAO_SERVIDOR, CodedValues.TPC_SIM, responsavel);
                final boolean exigeSenhaConfirmacaoSolicitacao = paramSvcCse.isTpsExigeSenhaConfirmacaoSolicitacao();
                if (usaMultiplasSenhasAut && exigeSenhaConfirmacaoSolicitacao) {
                    novaSenhaPlana = usuarioController.gerarSenhaAutorizacao(usuCodigoServidor, false, true, false, responsavel);
                }
                // Envia e-mail para o servidor com os dados da proposta aprovada
                EnviaEmailHelper.enviarEmailSerPropostaLeilaoAprovada(adeCodigoNovo, codigoAutorizacaoSolicitacao, novaSenhaPlana, plsBean, responsavel);

                try {
                    // DESENV-10475: envia notificação PUSH ao dispositivo do usuário solicitante de que leilão foi encerrada com aprovação
                    final String deviceToken = usuarioController.findDeviceToken(usuCodigoServidor, responsavel);

                    if (!TextHelper.isNull(deviceToken)) {
                        final ObjectMapper mapper = new ObjectMapper();

                        final String textoPush = ApplicationResourcesHelper.getMessage("mensagem.notificacao.push.leilao.aprovado", responsavel);
                        final String tituloPush = ApplicationResourcesHelper.getMessage("mensagem.notificacao.push.leilao.aprovado.titulo", responsavel);

                        final String strJsonBody = "{"
                                                 +   "\"ade_codigo\": \""+ adeCodigoNovo + "\","
                                                 +   "\"tno_codigo\": "+ mapper.writeValueAsString(TipoNotificacaoEnum.PROPOSTA_LEILAO_APROVADA.getCodigo())
                                                 + "}";

                        final String ndiCodigo = notificacaoDispositivoController.createNotificacaoDispositivo(responsavel.getUsuCodigo(), usuCodigoServidor, CodedValues.FUN_SOLICITAR_LEILAO_REVERSO, strJsonBody,
                                DateHelper.getSystemDatetime(), null, CodedValues.NDI_ATIVO,
                                TipoNotificacaoEnum.PROPOSTA_LEILAO_APROVADA.getCodigo(), responsavel);

                        notificacaoDispositivoController.enviarNotificacao(ndiCodigo, deviceToken, strJsonBody, textoPush, tituloPush, usuCodigoServidor, TipoNotificacaoEnum.NOVA_PROPOSTA_LEILAO.getCodigo(), true, null, responsavel);
                    }
                } catch (final NotificacaoDispositivoControllerException ex) {
                    // Não dá rollback em caso de erro de notificação ao dispositivo
                    LOG.error(ex.getMessage(), ex);
                }

                try {
                    // Envia e-mail para CSAs cuja propostas foram rejeitadas
                    EnviaEmailHelper.enviarEmailCsaPropostaLeilaoRejeitada(adeCodigo, plsBean, responsavel);
                } catch (final ViewHelperException vex) {
                    LOG.error(vex.getMessage(), vex);
                }

                final boolean enviaEmailCsaVencedoraLeilao = ParamSist.getBoolParamSist(CodedValues.TPC_ENVIAR_EMAIL_CONSIGNATARIA_VENCEDORA_DO_LEILAO, responsavel);
                if(enviaEmailCsaVencedoraLeilao) {
                    try {
                        // Envia e-mail para CSA vencedora do leilão
                        EnviaEmailHelper.enviarEmailCsaVencedoraLeilao(adeCodigo, plsBean, responsavel);
                    } catch (final ViewHelperException vex) {
                        LOG.error(vex.getMessage(), vex);
                    }
                }

                return adeCodigoNovo;

            } catch (FindException | UpdateException ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                LOG.error(ex.getMessage(), ex);
                throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            } catch (ZetraException | JsonProcessingException ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new LeilaoSolicitacaoControllerException(ex);
            }
        }
        return null;
    }

    /**
     * Lista as propostas de leilão de solicitação
     * @param adeCodigo
     * @param csaCodigo
     * @param stpCodigo
     * @param responsavel
     * @return
     * @throws LeilaoSolicitacaoControllerException
     */
    @Override
    public List<TransferObject> lstPropostaLeilaoSolicitacao(String adeCodigo, String csaCodigo, String stpCodigo, boolean arquivado, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            final ListaPropostaLeilaoSolicitacaoQuery query = new ListaPropostaLeilaoSolicitacaoQuery();
            query.adeCodigo = adeCodigo;
            query.csaCodigo = csaCodigo;
            query.stpCodigo = stpCodigo;
            query.arquivado = arquivado;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Lista as propostas de leilão de solicitação
     * @param adeCodigo
     * @param responsavel
     * @return
     * @throws LeilaoSolicitacaoControllerException
     */
    @Override
    public List<TransferObject> lstPropostaLeilaoOfertaAutomatica(String adeCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            final ListaPropostaLeilaoSolicitacaoOfertaAutQuery query = new ListaPropostaLeilaoSolicitacaoOfertaAutQuery();
            query.adeCodigo = adeCodigo;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int countSolicitacaoLeilaoCanceladoParaBloqueio(String rseCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            // Realiza pesquisa de leilões cancelados
            final ListaSolicitacaoLeilaoCanceladoParaBloqueioQuery query = new ListaSolicitacaoLeilaoCanceladoParaBloqueioQuery();
            query.rseCodigo = rseCodigo;

            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstSolicitacaoLeilaoEncerrado(AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            final ListaSolicitacaoLeilaoEncerradoQuery query = new ListaSolicitacaoLeilaoEncerradoQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
    @Override
    public BigDecimal calcularValorPrestacao(String adeCodigo, String taxaJuros, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        BigDecimal valorPrestacao = null;
        TransferObject ade = null;
        final BigDecimal taxaJurosDecimal = NumberHelper.parseDecimal(taxaJuros);
        final List<String> adeCodigos = new ArrayList<>();
        adeCodigos.add(adeCodigo);

        try {
            final List<TransferObject> autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigos, false, responsavel);

            if ((autdes != null) && !autdes.isEmpty()) {
              ade = autdes.get(0);
            }

            valorPrestacao = SimulacaoHelper.calcularValorPrestacao((BigDecimal)ade.getAttribute(Columns.ADE_VLR_LIQUIDO), (Integer)ade.getAttribute(Columns.ADE_PRAZO),
                    DateHelper.getSystemDatetime(), (Date)ade.getAttribute(Columns.ADE_ANO_MES_INI), taxaJurosDecimal, taxaJurosDecimal,
                    (String)ade.getAttribute(Columns.ORG_CODIGO), (String)ade.getAttribute(Columns.ADE_PERIODICIDADE), responsavel);

        } catch (com.zetra.econsig.exception.AutorizacaoControllerException | ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        return valorPrestacao;
    }

    @Override
    public BigDecimal obterMelhorTaxaLeilao(String adeCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            final PropostaLeilaoSolicitacao proposta = PropostaLeilaoSolicitacaoHome.findByMelhorTaxa(adeCodigo);
            return proposta.getPlsTaxaJuros();
        } catch (final FindException ex) {
            return null;
        }
    }


    /**
     * Realizar a lista de filtros de Leilao Solicitação com as quantidades de resultados de cada filtro
     * @param responsavel
     * @return
     * @throws LeilaoSolicitacaoControllerException
     */
    @Override
    public List<TransferObject> listarFiltros(AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException{

        final List<TransferObject> result = new ArrayList<>();
        Collection<FiltroLeilaoSolicitacao> filtros = new ArrayList<>();
        try {
            filtros = FiltroLeilaoSolicitacaoHome.findByUsuCodigo(responsavel.getUsuCodigo());
        } catch (final FindException e) {
        }

        for (final FiltroLeilaoSolicitacao filtroLeilaoSolicitacao : filtros) {
            final FiltroLeilaoSolicitacao filtro = filtroLeilaoSolicitacao;

            final TransferObject criterioPesquisa = new CustomTransferObject();
            final TransferObject resultPesquisa = new CustomTransferObject();

            // Seta os valores para a pesquisa
            criterioPesquisa.setAttribute("filtro", "0");
            criterioPesquisa.setAttribute("dataAberturaIni", DateHelper.format(filtro.getFlsDataAberturaIni(), LocaleHelper.getDatePattern()));
            criterioPesquisa.setAttribute("dataAberturaFim", DateHelper.format(filtro.getFlsDataAberturaFim(), LocaleHelper.getDatePattern()));
            criterioPesquisa.setAttribute("horasFimLeilao", filtro.getFlsHorasEncerramento() != null ? filtro.getFlsHorasEncerramento().toString() : null);
            criterioPesquisa.setAttribute("RSE_MATRICULA", filtro.getFlsMatricula());
            criterioPesquisa.setAttribute("SER_CPF", filtro.getFlsCpf());
            criterioPesquisa.setAttribute("RSE_PONTUACAO", filtro.getFlsPontuacaoMin() != null ? filtro.getFlsPontuacaoMin().toString() : null);
            criterioPesquisa.setAttribute("ARR_RISCO", filtro.getFlsAnaliseRisco() != null ? filtro.getFlsAnaliseRisco().toString() : null);
            criterioPesquisa.setAttribute("CSA_CODIGO", responsavel.getCsaCodigo());
            if(filtro.getPostoRegistroServidor() != null){
                criterioPesquisa.setAttribute(FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR, filtro.getPostoRegistroServidor().getPosCodigo());
            }
            criterioPesquisa.setAttribute("RSE_MARGEM_LIVRE", filtro.getFlsMargemLivreMax());

            // Cria o resultado
            resultPesquisa.setAttribute(Columns.FLS_CODIGO, filtro.getFlsCodigo());
            resultPesquisa.setAttribute(Columns.FLS_DESCRICAO, filtro.getFlsDescricao());
            resultPesquisa.setAttribute(Columns.FLS_DATA, filtro.getFlsData());
            resultPesquisa.setAttribute(Columns.FLS_DATA_ABERTURA_INI, filtro.getFlsDataAberturaIni());
            resultPesquisa.setAttribute(Columns.FLS_DATA_ABERTURA_FIM, filtro.getFlsDataAberturaFim());
            resultPesquisa.setAttribute(Columns.FLS_EMAIL_NOTIFICACAO, filtro.getFlsEmailNotificacao());
            resultPesquisa.setAttribute(Columns.FLS_HORAS_ENCERRAMENTO, filtro.getFlsHorasEncerramento());
            resultPesquisa.setAttribute(Columns.FLS_TIPO_PESQUISA, filtro.getFlsTipoPesquisa());
            resultPesquisa.setAttribute(Columns.FLS_POS_CODIGO, filtro.getPostoRegistroServidor() != null ? filtro.getPostoRegistroServidor().getPosCodigo() : null);
            if (filtro.getCidade() != null){
                resultPesquisa.setAttribute(Columns.FLS_CID_CODIGO, filtro.getCidade().getCidCodigo());
                String cidNome, ufNome;
                try {
                    final Cidade cidade = CidadeHome.findByPrimaryKey(filtro.getCidade().getCidCodigo());
                    cidNome = cidade.getCidNome();
                    final Uf uf = UfHome.findByPrimaryKey(cidade.getUf().getUfCod());
                    ufNome = uf.getUfNome();
                    resultPesquisa.setAttribute(Columns.CID_NOME, cidNome + " - " + ufNome);
                } catch (final FindException e) {
                }
            }
            resultPesquisa.setAttribute(Columns.FLS_PONTUACAO_MIN, filtro.getFlsPontuacaoMin());
            resultPesquisa.setAttribute(Columns.FLS_ANALISE_RISCO, filtro.getFlsAnaliseRisco());
            resultPesquisa.setAttribute(Columns.FLS_MATRICULA, filtro.getFlsMatricula());
            resultPesquisa.setAttribute(Columns.FLS_CPF, filtro.getFlsCpf());
            resultPesquisa.setAttribute(Columns.FLS_MARGEM_LIVRE_MAX , filtro.getFlsMargemLivreMax());
            int qtdeNaoInformada = 0;
            try {
                qtdeNaoInformada = contarLeilaoSolicitacao(criterioPesquisa, responsavel);
            } catch (final LeilaoSolicitacaoControllerException e) {
                LOG.error(e.getMessage(), e);
                throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
            }
            resultPesquisa.setAttribute("QTDE_0", qtdeNaoInformada);

            int qtdeInformada = 0;
            criterioPesquisa.setAttribute("filtro", "1");
            try {
                qtdeInformada = contarLeilaoSolicitacao(criterioPesquisa, responsavel);
            } catch (final LeilaoSolicitacaoControllerException e) {
                LOG.error(e.getMessage(), e);
                throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
            }
            resultPesquisa.setAttribute("QTDE_1", qtdeInformada);
            result.add(resultPesquisa);
        }


        return result;

    }

    /**
     * Cria um novo filtro de solicilação de leilão
     * @param filtro
     * @param responsavel
     * @throws LeilaoSolicitacaoControllerException
     */
    @Override
    public void criarFiltroLeilaoSolicitacao(FiltroLeilaoSolicitacaoTO filtro, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException{

        try {
            FiltroLeilaoSolicitacaoHome.create(
                    filtro.getCidCodigo(),
                    filtro.getUsuCodigo(),
                    filtro.getPosCodigo(),
                    filtro.getFlsDescricao(),
                    filtro.getFlsData(),
                    filtro.getFlsEmailNotificacao(),
                    filtro.getFlsDataAberturaInicial(),
                    filtro.getFlsDataAberturaFinal(),
                    filtro.getFlsHorasEncerramento(),
                    filtro.getFlsPontuacaoMinima(),
                    filtro.getFlsAnaliseRisco(),
                    filtro.getFlsMargemLivreMax(),
                    filtro.getFlsTipoPesquisa(),
                    filtro.getFlsMatricula(),
                    filtro.getFlsCpf());
        } catch (final CreateException e) {
            LOG.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }


    /**
     * Exclui um filtro, caso este seja do usuário responsável
     * @param fltCodigo
     * @param responsavel
     * @throws LeilaoSolicitacaoControllerException
     */
    @Override
    public void excluirFiltroLeilaoSolicitacao(String flsCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException{

        FiltroLeilaoSolicitacao filtro;
        try {
            filtro = FiltroLeilaoSolicitacaoHome.findByPrimaryKeyAndUsuCodigo(flsCodigo, responsavel.getUsuCodigo());
            AbstractEntityHome.remove(filtro);
        } catch (final FindException e) {
            // Não precisa remover o que não existe
        } catch (final RemoveException e) {
            LOG.error(e.getMessage(), e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }


    }


    @Override
    public List<TransferObject> listarFiltrosByAdeCodigo(String adeCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        List<TransferObject> result = new ArrayList<>();

        final ListaFiltroLeilaoSolicitacaoByAdeCodigoQuery query = new ListaFiltroLeilaoSolicitacaoByAdeCodigoQuery();
        query.adeCodigo = adeCodigo;

        try {
            result = query.executarDTO();
        } catch (final HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }

        return result;
    }

    /**
     * Retorna a quantidade de contratos incluídos nos últimos 6 meses.
     * @param rseCodigo
     * @param responsavel
     * @return
     * @throws LeilaoSolicitacaoControllerException
     */
    @Override
    public Integer qtdeContratos(String rseCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -6);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);

            final ListaAdeSolicitadaLeilaoQuery query = new ListaAdeSolicitadaLeilaoQuery();
            query.rseCodigo = rseCodigo;
            query.dataInicial = cal.getTime();
            query.solicitacaoLeilao = false;
            query.concretizado = false;

            final Integer contador = query.executarContador();

            if ((contador == null) || (contador < 0)) {
                return 0;
            }

            return contador;
        } catch (final HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    /**
     * Retorna a quantidade de solicitações de Leilões nos últimos 6 meses.
     * @param rseCodigo
     * @param responsavel
     * @return
     * @throws LeilaoSolicitacaoControllerException
     */
    @Override
    public Integer qtdeSolicitacaoLeilao(String rseCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -6);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);

            final ListaAdeSolicitadaLeilaoQuery query = new ListaAdeSolicitadaLeilaoQuery();
            query.rseCodigo = rseCodigo;
            query.dataInicial = cal.getTime();
            query.solicitacaoLeilao = true;
            query.concretizado = false;

            final Integer contador = query.executarContador();

            if ((contador == null) || (contador < 0)) {
                return 0;
            }

            return contador;
        } catch (final HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    /**
     * Retorna a quantidade de leilões concretizados nos últimos 6 meses.
     * @param rseCodigo
     * @param responsavel
     * @return
     * @throws LeilaoSolicitacaoControllerException
     */
    @Override
    public Integer qtdeLeilaoConcretizado(String rseCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -6);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);

            final ListaAdeSolicitadaLeilaoQuery query = new ListaAdeSolicitadaLeilaoQuery();
            query.rseCodigo = rseCodigo;
            query.dataInicial = cal.getTime();
            query.solicitacaoLeilao = true;
            query.concretizado = true;

            final Integer contador = query.executarContador();

            if ((contador == null) || (contador < 0)) {
                return 0;
            }

            return contador;
        } catch (final HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    /**
     * @throws LeilaoSolicitacaoControllerException
     * Busca avaliações de risco feitas pela consignatária
     * @param rseCodigo
     * @param responsavel (somente csa)
     * @return
     * @throws
     */
    @Override
    public TransferObject obterAnaliseDeRiscoRegistroServidor(String rseCodigo,  AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException  {
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_RISCO_SERVIDOR_CSA, CodedValues.TPC_SIM, responsavel) && responsavel.isCsa()) {
            try {
                final ObtemAnaliseDeRiscoRegistroServidorQuery query = new ObtemAnaliseDeRiscoRegistroServidorQuery();
                query.csaCodigo = responsavel.getCsaCodigo();
                query.rseCodigo = rseCodigo;

                final List<TransferObject> resultado = query.executarDTO();

                if(!resultado.isEmpty() && (resultado.size() > 1)) {
                    throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel);
                }

                if(!resultado.isEmpty()) {
                    return resultado.get(0);
                }

              return null;

            } catch (final HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
        return null;
    }

    @Override
    public void informarAnaliseDeRisco(String rseCodigo, String csaCodigo, String arrRisco, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {

        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_RISCO_SERVIDOR_CSA, CodedValues.TPC_SIM, responsavel) && responsavel.isCsa() && responsavel.temPermissao(CodedValues.FUN_CADASTRO_RISCO_SERVIDOR_CSA)) {

            // Verifica se já existe proposta
            AnaliseRiscoRegistroSer arrBean = null;
            try {
                arrBean = AnaliseRiscoRegistroServidorHome.findByRseCsa(rseCodigo, csaCodigo);

            } catch (final FindException ex) {
            }
            try {
                if(arrBean != null) {
                    arrBean.setArrData(DateHelper.getSystemDatetime());
                    arrBean.setUsuario(UsuarioHome.findByPrimaryKey(responsavel.getUsuCodigo()));

                    // Grava log da operação
                    final LogDelegate logDelegate = new LogDelegate(responsavel, Log.ANALISE_RISCO_SERVIDOR, Log.UPDATE, Log.LOG_INFORMACAO);
                    logDelegate.setAnaliseRiscoRegistroServidor(arrBean.getArrCodigo());
                    logDelegate.setUsuario(responsavel.getUsuCodigo());
                    logDelegate.setRegistroServidor(rseCodigo);
                    logDelegate.setConsignataria(csaCodigo);

                    if (!arrBean.getArrRisco().equalsIgnoreCase(arrRisco)) {
                        logDelegate.addChangedField(Columns.ARR_RISCO, arrRisco, arrBean.getArrRisco());
                        arrBean.setArrRisco(arrRisco);
                    }

                    AbstractEntityHome.update(arrBean);
                    logDelegate.write();

                } else {
                    arrBean = AnaliseRiscoRegistroServidorHome.create(responsavel.getUsuCodigo(), rseCodigo, csaCodigo, arrRisco);

                    // Grava log da operação
                    final LogDelegate logDelegate = new LogDelegate(responsavel, Log.ANALISE_RISCO_SERVIDOR, Log.CREATE, Log.LOG_INFORMACAO);
                    logDelegate.setAnaliseRiscoRegistroServidor(arrBean.getArrCodigo());
                    logDelegate.setUsuario(responsavel.getUsuCodigo());
                    logDelegate.setRegistroServidor(rseCodigo);
                    logDelegate.setConsignataria(csaCodigo);
                    logDelegate.addChangedField(Columns.ARR_RISCO, arrRisco);

                    logDelegate.write();

                }
            } catch (CreateException | UpdateException | FindException | LogControllerException ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                LOG.error(ex.getMessage(), ex);
                throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }

    }

    @Override
    public List<String> lstEmailConsignatariasNotificacaoLeilao(String adeCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            final ListaEmailConsignatariasNotificacaoLeilaoQuery query = new ListaEmailConsignatariasNotificacaoLeilaoQuery();
            return query.executarLista();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int contarLeilaoFinalizadoSemContato(AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            final ListaLeilaoFinalizadoSemContatoQuery query = new ListaLeilaoFinalizadoSemContatoQuery();
            query.responsavel = responsavel;
            query.count = true;
            return query.executarContador();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstLeilaoFinalizadoSemContato(AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            final ListaLeilaoFinalizadoSemContatoQuery query = new ListaLeilaoFinalizadoSemContatoQuery();
            query.responsavel = responsavel;
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void recusarInformacaoContatoLeilaoFinalizado(List<TransferObject> leiloes, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException{
        try {
            final String dataHora = DateHelper.toISOString(DateHelper.getSystemDatetime());
            for (final TransferObject ade : leiloes) {
                final String adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
                autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_RECUSA_CONFIRMACAO_DADOS_LEILAO, dataHora, responsavel);
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void salvarInformacaoContatoLeilaoFinalizado(List<TransferObject> leiloes, String email, String ddd, String telefone, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException{
        try {
            for (final TransferObject ade : leiloes) {
                final String adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
                autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_CONFIRMACAO_DADOS_EMAIL_LEILAO, email, responsavel);
                autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_CONFIRMACAO_DADOS_DDD_TEL_LEILAO, ddd, responsavel);
                autorizacaoController.setDadoAutDesconto(adeCodigo, CodedValues.TDA_CONFIRMACAO_DADOS_TEL_LEILAO, telefone, responsavel);
            }
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> listarStatusPropostaLeilao(AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            final ListaStatusPropostaLeilaoQuery query = new ListaStatusPropostaLeilaoQuery();
            return query.executarDTO();
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public boolean podeReverPontuacaoLeilao(String adeCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {

    	try {

    		final ListaLeilaoPassivelReversaoQuery query = new ListaLeilaoPassivelReversaoQuery();
    		query.adeCodigo = adeCodigo;

    		final List<TransferObject> result = query.executarDTO();

    		if (result.isEmpty()) {
    			return false;
    		}
    		return true;

    	} catch (final HQueryException ex) {
			LOG.error(ex.getMessage(), ex);
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
		}

    }

    @Override
    public Date calcularDataValidadeLeilao(int qtdMinutosFechamentoLeilao, AcessoSistema responsavel) throws CalendarioControllerException {
        // data hora atual
        final Calendar dataHoraAtual = Calendar.getInstance();
        // calendário da data atual
        final CalendarioTO calHoje = calendarioController.findCalendario(dataHoraAtual.getTime(), responsavel);
        // calcula minutos para serem adicionados
        final Calendar dataHoraCalculada = Calendar.getInstance();
        dataHoraCalculada.add(Calendar.MINUTE, qtdMinutosFechamentoLeilao);
        // inicializa próximo dia útil
        Date proximoDiaUtil = Calendar.getInstance().getTime();
        // calcula parâmetros para dias úteis
        int dias = qtdMinutosFechamentoLeilao / (24 * 60);
        final int minutosRestantes = qtdMinutosFechamentoLeilao % (24 * 60);

        // verifica se precisa ajustar para o próximo dia útil
        if (CodedValues.TPC_SIM.equals(calHoje.getCalDiaUtil()) && (minutosRestantes > 0) &&
            ((minutosRestantes + (dataHoraAtual.get(Calendar.HOUR_OF_DAY) * 60) + dataHoraAtual.get(Calendar.MINUTE)) >= (24 * 60))) {
            dias += 1;
        }

        // recupera o próximo dia útil
        proximoDiaUtil = calendarioController.findProximoDiaUtil(dataHoraAtual.getTime(), dias);
        // ajusta horas, minutos e segundos do dia útil
        final Calendar calValidade = Calendar.getInstance();
        calValidade.setTime(proximoDiaUtil);
        if (CodedValues.TPC_SIM.equals(calHoje.getCalDiaUtil())) {
            calValidade.add(Calendar.HOUR_OF_DAY, dataHoraCalculada.get(Calendar.HOUR_OF_DAY));
            calValidade.add(Calendar.MINUTE, dataHoraCalculada.get(Calendar.MINUTE));
            calValidade.add(Calendar.SECOND, dataHoraCalculada.get(Calendar.SECOND));
        } else if (minutosRestantes == 0) {
            // tira 1 segundo para evitar de virar o dia na validade
            calValidade.add(Calendar.SECOND, -1);
        } else if (minutosRestantes > 0) {
            calValidade.add(Calendar.MINUTE, minutosRestantes);
        }

        // data de validade do leilão
        return calValidade.getTime();
    }
}
