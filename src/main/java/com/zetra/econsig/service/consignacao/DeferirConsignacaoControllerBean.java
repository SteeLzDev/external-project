package com.zetra.econsig.service.consignacao;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.parametros.AlterarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.LiquidarConsignacaoParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.GerenciadorAutorizacaoException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.AutorizacaoHelper;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.gerenciadorautorizacao.GerenciadorAutorizacao;
import com.zetra.econsig.helper.gerenciadorautorizacao.GerenciadorAutorizacaoFactory;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.servico.NaturezaRelSvc;
import com.zetra.econsig.helper.sms.SMSHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessaEnvioEmailEntidadesAltAde;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AnexoAutorizacaoDescontoHome;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacao;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacaoHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacao;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.persistence.entity.ServidorHome;
import com.zetra.econsig.persistence.entity.Usuario;
import com.zetra.econsig.persistence.entity.UsuarioHome;
import com.zetra.econsig.persistence.entity.VerbaConvenio;
import com.zetra.econsig.persistence.entity.VerbaConvenioHome;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoDeferAutomaticoQuery;
import com.zetra.econsig.persistence.query.consignataria.ObtemCsaBloqRelSvcRequerDeferimentoQuery;
import com.zetra.econsig.persistence.query.parametro.ListaRelacionamentosQuery;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoEConsigEnum;

/**
 * <p>Title: DeferirConsignacaoControllerBean</p>
 * <p>Description: Session Bean para a operação de Deferimento/Indeferimento de Contrato.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service("deferirConsignacaoController")
@Transactional
public class DeferirConsignacaoControllerBean extends AutorizacaoControllerBean implements DeferirConsignacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DeferirConsignacaoControllerBean.class);

    @Autowired
    private LiquidarConsignacaoController liquidarConsignacaoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private AlterarConsignacaoController alterarConsignacaoController;

    @Autowired
    private CancelarConsignacaoController cancelarConsignacaoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private PeriodoController periodoController;

    @Autowired
    private EditarAnexoConsignacaoController editarAnexoConsignacaoController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Autowired
    private SuspenderConsignacaoController suspenderConsignacaoController;

    /**
     * Utilizado pelo caso de uso Deferir Consignação e pela Finalização de compra
     * @param adeCodigo
     * @param tipoMotivoOperacao
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    @Override
    public void deferir(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final AutDesconto autdes = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);

            // Defere a reserva consumindo a senha autorização, caso ela exista.
            deferir(autdes, null, true, false, tipoMotivoOperacao, null, responsavel);

            // Gera o Log de auditoria
            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.DEFERIR_CONSIGNACAO, Log.LOG_INFORMACAO);
            logDelegate.setAutorizacaoDesconto(adeCodigo);
            logDelegate.write();

        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) ex;
            } else {
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    /**
     * Utilizado pelo caso de uso Confirmar Reserva e pelo Autorizar Reserva
     * @param autdes
     * @param senhaUtilizada
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    protected String deferir(AutDesconto autdes, String senhaUtilizada, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return this.deferir(autdes, senhaUtilizada, true, true, null, null, responsavel);
    }

    /**
     * Defere a autorização de desconto
     * @param autdes : Autorização de desconto
     * @param senhaUtilizada : Senha utilizada na confirmação da operação
     * @param consumirSenhaAutorizacao : Indica se a senha de autorização deve ser consumida, caso o sistema possua essa funcionalidade.
     * @param exigirSenhaAutorizacaoCadastrada : Indica se, ao consumir a senha de autorização, é exigido que ela esteja cadastrada.
     * @param tipoMotivoOperacao : Dados do motivo de operação
     * @param responsavel Responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    @Override
    public String deferir(AutDesconto autdes, String senhaUtilizada, boolean consumirSenhaAutorizacao, boolean exigirSenhaAutorizacaoCadastrada, CustomTransferObject tipoMotivoOperacao, java.util.Date ocaPeriodo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        final String sadCodigo = autdes.getStatusAutorizacaoDesconto().getSadCodigo().trim();
        if (!CodedValues.SAD_AGUARD_CONF.equals(sadCodigo) && !CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo) && !CodedValues.SAD_SOLICITADO.equals(sadCodigo)) {
            throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.deferida.situacao.atual.dela.nao.permite.operacao", responsavel);
        }

        try {
            final String adeCodigo = autdes.getAdeCodigo();
            final VerbaConvenio verbaConvenio = VerbaConvenioHome.findByPrimaryKey(autdes.getVerbaConvenio().getVcoCodigo());
            final Convenio convenio = ConvenioHome.findByPrimaryKey(verbaConvenio.getConvenio().getCnvCodigo());
            final String orgCodigo = convenio.getOrgao().getOrgCodigo();
            final String svcCodigo = convenio.getServico().getSvcCodigo();
            final String csaCodigo = convenio.getConsignataria().getCsaCodigo();
            final String rseCodigo = autdes.getRegistroServidor().getRseCodigo();
            final String srsCodigo = RegistroServidorHome.findByPrimaryKey(rseCodigo).getStatusRegistroServidor().getSrsCodigo();

            // Não permite deferir contratos de servidores com situação "Pendente"
            if (CodedValues.SRS_PENDENTE.equals(srsCodigo)) {
                throw new AutorizacaoControllerException("mensagem.erro.deferir.consignacao.servidor.pendente", responsavel);
            }

            final String ocaAlteracaoOrigInsereAltera = concluiInsereAlteraNaoAutomatico(autdes, svcCodigo, orgCodigo, responsavel);
            if (!TextHelper.isNull(ocaAlteracaoOrigInsereAltera)) {
                // se contrato a deferir é de relacionamento insere/altera não automático, retorna a ocorrência de alteração do contrato origem.
                return ocaAlteracaoOrigInsereAltera;
            }

            // Verifica se o sistem bloqueia deferimento caso exista um contrato mais antigo aguandando deferimento
            final boolean bloqueiaDeferimentoContratoSemPrioridade = ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_DEFERIMENTO_MANUAL_CONTRATO, CodedValues.TPC_SIM, responsavel);
            if (bloqueiaDeferimentoContratoSemPrioridade) {
                // Verifica se existe um contrato mais antigo que o contrato a ser deferido com o status Aguard. Deferimento
                // Os contratos mais antigos são prioritários
                final List<TransferObject> adeCodigosDeferManual = pesquisarConsignacaoController.pesquisarContratosDeferManualDataMenor(rseCodigo, autdes.getAdeData(), responsavel);
                if ((adeCodigosDeferManual != null) && !adeCodigosDeferManual.isEmpty()) {
                    throw new AutorizacaoControllerException("mensagem.erro.deferir.consignacao.existe.contrato.mais.antigo", responsavel);
                }
            }

            // Verifica Bloquear deferimento/indeferimento caso o CPF do gestor seja igual ao CPF do contrato (tpc 469)
            if (ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_DEFER_INDEFER_CPF_IGUAL, CodedValues.TPC_SIM, responsavel)) {
                final Usuario usuarioResponsavel = UsuarioHome.findByPrimaryKey(responsavel.getUsuCodigo());
                final RegistroServidor rseDonoContrato = RegistroServidorHome.findByPrimaryKey(rseCodigo);
                final Servidor servidorDonoContrato = ServidorHome.findByPrimaryKey(rseDonoContrato.getServidor().getSerCodigo());
                if (!TextHelper.isNull(usuarioResponsavel.getUsuCpf()) && !TextHelper.isNull(servidorDonoContrato.getSerCpf()) && usuarioResponsavel.getUsuCpf().equals(servidorDonoContrato.getSerCpf())) {
                    throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.deferida.cpf.gestor.igual.contrato", responsavel);
                }
            }

            // Caso o contrato seja deferimento, origem de uma reativação por parcela rejeitada, precisamos relançar o contrato e modificar a situção
            final boolean deferirReativacaoRejeitadoFolha = ParamSist.getBoolParamSist(CodedValues.TPC_REATIVAR_CONTRATO_SUSP_PRD_REJEITADA_EXIGE_CONF_GESTOR, responsavel) &&
                                                            ParamSist.getBoolParamSist(CodedValues.TPC_SUSPENDER_CONTRATO_PARCELA_REJEITADA_RETORNO, responsavel) && suspenderConsignacaoController.contratoSuspensoPrdRejeitadaReativado(adeCodigo, responsavel);

            // Somente o Gestor ou suporte podem reativar este tipo de contrato
            if (deferirReativacaoRejeitadoFolha && !responsavel.isCseSup()) {
                throw new AutorizacaoControllerException("mensagem.erro.autorizacao.so.pode.ser.deferido.gestor.suporte", responsavel);
            }

            // Busca o parâmetro de incidência de margem do serviço
            final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
            final Short adeIncMargem = paramSvcCse.getTpsIncideMargem();
            final int diasCancelamentoRenegociacao = !TextHelper.isNull(paramSvcCse.getTpsPrazoDiasCancelamentoRenegociacao()) ? Integer.parseInt(paramSvcCse.getTpsPrazoDiasCancelamentoRenegociacao()) : 0;

            boolean podeConfirmarLiquidacao = true;
            boolean podeConfirmarRenegociacao = false;
            List<AutDesconto> adeRenegociadas = null;
            List<AutDesconto> adeCompradas = null;

            // Verifica se o contrato a ser deferido é fruto de uma compra, para que possamos
            // conferir se todos os contratos comprados já estão liquidados.
            final List<RelacionamentoAutorizacao> radCompra = RelacionamentoAutorizacaoHome.findByDestino(adeCodigo, CodedValues.TNT_CONTROLE_COMPRA);
            if ((radCompra != null) && !radCompra.isEmpty()) {
                adeCompradas = new ArrayList<>();
                for (final RelacionamentoAutorizacao radCompraBean : radCompra) {
                    final String adeCodigoComprada = radCompraBean.getAutDescontoByAdeCodigoOrigem().getAdeCodigo();
                    final AutDesconto adeCompradaBean = AutDescontoHome.findByPrimaryKey(adeCodigoComprada);
                    // Guarda as ades envolvidas na negociação de compra
                    // para evitar ter que busca-las novamente.
                    adeCompradas.add(adeCompradaBean);
                    if (!responsavel.isCseSup()) {
                        final VerbaConvenio vcoCompradaBean = VerbaConvenioHome.findByPrimaryKey(adeCompradaBean.getVerbaConvenio().getVcoCodigo());
                        final Convenio cnvCompradaBean = ConvenioHome.findByPrimaryKey(vcoCompradaBean.getConvenio().getCnvCodigo());
                        final String csaCodigoComprada = cnvCompradaBean.getConsignataria().getCsaCodigo();
                        final String sadCodigoRe = adeCompradaBean.getStatusAutorizacaoDesconto().getSadCodigo().trim();
                        if (!csaCodigoComprada.equals(csaCodigo) && !CodedValues.SAD_LIQUIDADA.equals(sadCodigoRe) && !CodedValues.SAD_CONCLUIDO.equals(sadCodigoRe)) {
                            throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.deferida.situacao.atual.algum.contrato.envolvido.transacao.nao.permite.operacao", responsavel);
                        }
                    }
                }
                // Verifica se pode confirmar a liquidação de todas as consignações envolvidas na compra
                podeConfirmarLiquidacao = usuarioPodeConfirmarLiquidacao(adeCompradas, false, false, responsavel);

            } else {
                // Verifica se o contrato a ser deferido é fruto de uma renegociação, para que possamos
                // conferir se todos os contratos renegociados podem ser liquidados
                final List<RelacionamentoAutorizacao> radRenegociacao = RelacionamentoAutorizacaoHome.findByDestino(adeCodigo, CodedValues.TNT_CONTROLE_RENEGOCIACAO);
                if ((radRenegociacao != null) && !radRenegociacao.isEmpty()) {
                    adeRenegociadas = new ArrayList<>();

                    BigDecimal vlrTotalRenegociacao = new BigDecimal("0.00");
                    for (final RelacionamentoAutorizacao radRenegociacaoBean : radRenegociacao) {
                        final String adeCodigoRenegociada = radRenegociacaoBean.getAutDescontoByAdeCodigoOrigem().getAdeCodigo();
                        final AutDesconto adeRenegociadaBean = AutDescontoHome.findByPrimaryKey(adeCodigoRenegociada);
                        adeRenegociadas.add(adeRenegociadaBean);

                        if (AutorizacaoHelper.valorMargemDisponivelRenegociacao(adeIncMargem, adeRenegociadaBean.getAdeIncMargem(), responsavel)) {
                            vlrTotalRenegociacao = vlrTotalRenegociacao.add(adeRenegociadaBean.getAdeVlr());
                        }
                    }

                    // Se é uma renegociação, verifica se esta pode ser confirmada sem uma segunda operação
                    podeConfirmarRenegociacao = podeConfirmarRenegociacao(autdes.getAdeVlr(), svcCodigo, csaCodigo, vlrTotalRenegociacao, responsavel);

                    // Verifica se pode confirmar a liquidação de todas as consignações envolvidas na renegociação
                    podeConfirmarLiquidacao = usuarioPodeConfirmarLiquidacao(adeRenegociadas, true, podeConfirmarRenegociacao, responsavel);
                }
            }

            String ocaCodigo = null;

            if (!podeConfirmarLiquidacao) {
                if (!CodedValues.SAD_AGUARD_CONF.equals(sadCodigo)) {
                    // Altera o status da autorização para SAD_AGUARD_CONF caso não esteja nesta situação
                    ocaCodigo = modificaSituacaoADE(autdes, CodedValues.SAD_AGUARD_CONF, responsavel);

                    if ((ocaCodigo != null) && (tipoMotivoOperacao != null)) {
                        // grava motivo da operacao
                        tipoMotivoOperacao.setAttribute(Columns.OCA_CODIGO, ocaCodigo);
                        tipoMotivoOperacaoController.gravarMotivoOperacaoConsignacao(tipoMotivoOperacao, responsavel);
                    }
                }

                // Registra ocorrência de confirmação de liquidação nas operações renegociadas/compradas
                if (responsavel.temPermissao(CodedValues.FUN_CONF_LIQUIDACAO_COMPRA) && (adeCompradas != null) && !adeCompradas.isEmpty()) {
                    for (final AutDesconto adeComprada : adeCompradas) {
                        criaOcorrenciaADE(adeComprada.getAdeCodigo(), CodedValues.TOC_CONFIRMACAO_LIQUIDACAO_ADE, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.confirmacao.liquidacao", responsavel), responsavel);
                    }
                } else if (responsavel.temPermissao(CodedValues.FUN_CONFIRMAR_RENEGOCIACAO) && (adeRenegociadas != null) && !adeRenegociadas.isEmpty()) {
                    for (final AutDesconto adeRenegociadaBean : adeRenegociadas) {
                        criaOcorrenciaADE(adeRenegociadaBean.getAdeCodigo(), CodedValues.TOC_CONFIRMACAO_LIQUIDACAO_ADE, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.confirmacao.liquidacao", responsavel), responsavel);
                    }
                }

            } else {
                // Calcula data inicial do período atual
                Date dataInicial = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
                Date adeAnoMesIni = DateHelper.toSQLDate(autdes.getAdeAnoMesIni());

                boolean ocorrenciaInformacaoDataInicioMargem = false;
                // Se o contrato deferido for destino de renegociação, verifica se o período de liquidação do(s) contrato(s) origem
                // confere com o período atual. Caso o período de liquidação seja maior que o período atual, ajustar o período
                // do contrato novo (que está sendo deferido) para o mesmo período de liquidação do(s) contrato(s) renegociado(s)
                // para evitar que sejam realizados dois descontos no mesmo período.
                Date ocaPeriodoRenegociacao = dataInicial;
                Date ocaPeriodoDeferimento = dataInicial;
                if ((adeRenegociadas != null) && !adeRenegociadas.isEmpty()) {
                    // recupera o período em que deve ser criada a ocorrência de liquidação do contrato renegociado
                    ocaPeriodoRenegociacao = (Date) AutorizacaoHelper.recuperarPeriodoOcorrenciaLiquidacao(orgCodigo, csaCodigo, svcCodigo, autdes.getAdeData(), autdes.getAdeAnoMesIni(), responsavel);
                    // se a data inicial do contrato deferido for menor que a data da liquidação do contrato renegociado, altera a data inicial
                    if (ocaPeriodoDeferimento.compareTo(ocaPeriodoRenegociacao) < 0) {
                        ocaPeriodoDeferimento = ocaPeriodoRenegociacao;
                    }
                }

                Date dataInicioFimAde = calcularDataIniFimMargemExtra(rseCodigo, ocaPeriodoDeferimento, adeIncMargem, true, false, responsavel);
                if ((dataInicioFimAde != null) && (dataInicioFimAde.compareTo(ocaPeriodoDeferimento) > 0)) {
                    alterarPeriodoInclusao(autdes, dataInicioFimAde, orgCodigo, responsavel);
                    ocorrenciaInformacaoDataInicioMargem = true;
                }

                boolean alteraDataInicial = true;

                // Verifica se mantem a data inicial do contrato deferido pós-corte e pré-exportação
                final boolean mantemDataIniDeferidaPosCorte = ParamSist.paramEquals(CodedValues.TPC_MANTEM_DATA_INI_ADE_DEFERIDA_POS_CORTE, CodedValues.TPC_SIM, responsavel);
                if (mantemDataIniDeferidaPosCorte) {
                    final java.util.Date ultPeriodoExportado = periodoController.obtemUltimoPeriodoExportado(Arrays.asList(orgCodigo), null, false, null, responsavel);
                    alteraDataInicial = (ultPeriodoExportado != null) && (ultPeriodoExportado.compareTo(adeAnoMesIni) >= 0);
                }

                // Se adeAnoMesIni é menor que a dataInicial do período, ou uma
                // data inicial foi passada, então altera a data incial da autorização
                if (alteraDataInicial && (adeAnoMesIni.compareTo(dataInicial) < 0)) {
                    // Valida o período inicial calculado
                    dataInicial = PeriodoHelper.getInstance().validarAdeAnoMesIni(orgCodigo, dataInicial, responsavel);
                    // Redefine os valores da data inicial e inicial ref
                    adeAnoMesIni = dataInicial;

                    dataInicioFimAde = calcularDataIniFimMargemExtra(rseCodigo, adeAnoMesIni, adeIncMargem, true, false, responsavel);
                    if ((dataInicioFimAde != null) && (dataInicioFimAde.compareTo(adeAnoMesIni) > 0)) {
                        adeAnoMesIni = dataInicioFimAde;
                        ocorrenciaInformacaoDataInicioMargem = true;
                    }
                    alterarPeriodoInclusao(autdes, adeAnoMesIni, orgCodigo, responsavel);
                }

                // Altera o status da autorização
                ocaCodigo = modificaSituacaoADE(autdes, CodedValues.SAD_DEFERIDA, responsavel);

                String msgOcaDeferimento = ApplicationResourcesHelper.getMessage("mensagem.informacao.contrato.deferido", responsavel);

                if (deferirReativacaoRejeitadoFolha) {
                    msgOcaDeferimento = ApplicationResourcesHelper.getMessage("mensagem.informacao.contrato.deferido.parcela.rejeitada", responsavel);

                    if (ParamSist.getBoolParamSist(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, responsavel)) {
                        criaOcorrenciaADE(adeCodigo, CodedValues.TOC_RELANCAMENTO, ApplicationResourcesHelper.getMessage("mensagem.informacao.contrato.relancamento.parcela.rejeitada", responsavel), ocaPeriodoDeferimento, responsavel);
                    }
                }

                // Insere ocorrência de deferimento.
                criaOcorrenciaADE(adeCodigo, CodedValues.TOC_DEFERIMENTO_CONTRATO, msgOcaDeferimento, ocaPeriodoDeferimento, responsavel);

                if ((ocaCodigo != null) && (tipoMotivoOperacao != null)) {
                    // grava motivo da operacao
                    tipoMotivoOperacao.setAttribute(Columns.OCA_CODIGO, ocaCodigo);
                    tipoMotivoOperacaoController.gravarMotivoOperacaoConsignacao(tipoMotivoOperacao, responsavel);
                }

                if (consumirSenhaAutorizacao) {
                    // Consome a senha de autorização utilizada para reserva.
                    consumirSenhaDeAutorizacao(adeCodigo, CodedValues.SAD_DEFERIDA, rseCodigo, svcCodigo, csaCodigo, senhaUtilizada, exigirSenhaAutorizacaoCadastrada, false, CodedValues.SAD_SOLICITADO.equals(sadCodigo), false, responsavel);
                }

                if ((adeCompradas != null) && !adeCompradas.isEmpty()) {
                    for (final AutDesconto adeComprada : adeCompradas) {
                        final String adeCodigoComprada = adeComprada.getAdeCodigo();
                        // Se o contrato esta aguardando liquidação de compra
                        if (CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(adeComprada.getStatusAutorizacaoDesconto().getSadCodigo())) {
                            liquidarConsignacaoController.liquidar(adeCodigoComprada, null, null, responsavel);
                        }
                    }

                    // Atualiza a incidência de margem, apesar do método finalizar compra já o fazer
                    // pois o objeto "autdes" carregado neste método local ainda está com adeIncMargem
                    // incorreto, e o update ao final deste mesmo método poderia sobrepor o valor correto.
                    // Esta situação só ocorre quando o confirmar/deferir é chamado sem que todos os
                    // contratos antigos estejam liquidados.
                    if (!adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
                        autdes.setAdeIncMargem(adeIncMargem);
                    }
                } else if ((adeRenegociadas != null) && !adeRenegociadas.isEmpty()) {
                    // Calcula a carência mínima do novo contrato de acordo com os parâmetros de serviço
                    Integer adeCarenciaMin = AutorizacaoHelper.calcularCarenciaContratoDestinoRenegociacao(orgCodigo, csaCodigo, svcCodigo, autdes.getAdeCarencia(), ocaPeriodoRenegociacao, responsavel);
                    if (adeCarenciaMin.intValue() > autdes.getAdeCarencia().intValue()) {
                        // Redefine os valores da data inicial e inicial ref
                        adeCarenciaMin = parametroController.calcularAdeCarenciaDiaCorteCsa(adeCarenciaMin, csaCodigo, orgCodigo, responsavel);
                        autdes.setAdeCarencia(adeCarenciaMin);
                        adeAnoMesIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, DateHelper.toSQLDate(autdes.getAdeAnoMesIni()), adeCarenciaMin, autdes.getAdePeriodicidade(), responsavel);

                        dataInicioFimAde = calcularDataIniFimMargemExtra(rseCodigo, adeAnoMesIni, adeIncMargem, true, false, responsavel);

                        if ((dataInicioFimAde != null) && (dataInicioFimAde.compareTo(adeAnoMesIni) > 0)) {
                            adeAnoMesIni = dataInicioFimAde;
                            ocorrenciaInformacaoDataInicioMargem = true;
                        }
                        alterarPeriodoInclusao(autdes, adeAnoMesIni, orgCodigo, responsavel);
                    }

                    // Pega código da autorização que foi renegociada e que está
                    // aguardando a confirmação/deferimento desta autorização para ser liquidada
                    BigDecimal totalRenegociado = BigDecimal.ZERO;
                    for (final AutDesconto adeRenegociadaBean : adeRenegociadas) {
                        final String adeCodigoRenegociada = adeRenegociadaBean.getAdeCodigo();

                        final LiquidarConsignacaoParametros parametros = new LiquidarConsignacaoParametros();
                        parametros.setRenegociacao(true);
                        parametros.setPodeConfirmarRenegociacao(podeConfirmarRenegociacao);
                        parametros.setOcaPeriodo(ocaPeriodoRenegociacao);

                        // Verifica se as ADEs renegociadas possuem ocorrência para manutenção da data de encerramento igual à
                        // data de inclusão da nova consignação, e em caso positivo, usa a adeAnoMesIni calculada anteriormente
                        // como período das ocorrências de liquidação
                        final List<OcorrenciaAutorizacao> ocorrenciasManutencaoDtEncerramento = OcorrenciaAutorizacaoHome.findByAdeTocCodigo(adeCodigoRenegociada, CodedValues.TOC_RENEGOCIACAO_ALTERACAO_DT_ENCERRAMENTO);
                        if ((ocorrenciasManutencaoDtEncerramento != null) && !ocorrenciasManutencaoDtEncerramento.isEmpty()) {
                            parametros.setOcaPeriodo(autdes.getAdeAnoMesIni());
                        }

                        // Executa a rotina de liquidação da consignação renegociada
                        liquidarConsignacaoController.liquidar(adeCodigoRenegociada, null, parametros, responsavel);

                        // Soma o valor de todas as ade's renegociadas
                        totalRenegociado = totalRenegociado.add(adeRenegociadaBean.getAdeVlr());
                    }

                    // Se incide margem desta consignação liquidada é igual a sim,
                    // então altera este parâmetro na nova autorização que está
                    // sendo deferida, e atualiza a margem do servidor de acordo
                    // com o valor da nova autrização
                    if (!adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
                        /**
                         * Recupera o parametro que indica se o sistema permite ou não a confirmação de contratos
                         * quando o servidor não possui mais margem. Isso acontece com processos de renegociações
                         * que se prolongaram além da carga de margem, onde o servidor pode ter sofrido uma redução
                         * de margem.
                         */
                        final boolean podeConfirmarMN = ParamSist.paramEquals(CodedValues.TPC_CONFIRMA_ADE_SEM_MARGEM, CodedValues.TPC_SIM, responsavel);
                        // atualiza o valor da variavel que indica se a margem deve ou não ser validada.
                        boolean validaMargem = !podeConfirmarMN;
                        // Antes de confirmar a reserva verifica o relacionamento entre servicos
                        // Se o serviço do contrato é origem de um relacionamento de alongamento, então não valida a margem do servidor
                        final boolean temAlongamento = ParamSist.paramEquals(CodedValues.TPC_TEM_ALONGAMENTO_CONTRATO, CodedValues.TPC_SIM, responsavel);
                        if (temAlongamento) {
                            final ListaRelacionamentosQuery queryRel = new ListaRelacionamentosQuery();
                            queryRel.tntCodigo = CodedValues.TNT_ALONGAMENTO;
                            queryRel.svcCodigoOrigem = svcCodigo;
                            queryRel.svcCodigoDestino = null;
                            final List<TransferObject> relacionamentoAlongamento = queryRel.executarDTO();
                            if ((relacionamentoAlongamento != null) && !relacionamentoAlongamento.isEmpty()) {
                                validaMargem = false;
                            }
                        }

                        // Atualiza a incidência de margem
                        autdes.setAdeIncMargem(adeIncMargem);
                        boolean prendeMargemRenegociacaoMenor = false;
                        String ocaCodigoPrendeMargemReneg = null;

                        if (ParamSist.getBoolParamSist(CodedValues.TPC_PRENDER_MARGEM_RENEG_CONTRATRO_NOVO_MENOR_ANTIGOS, responsavel) && (diasCancelamentoRenegociacao > 0) && (totalRenegociado.subtract(autdes.getAdeVlr()).signum() == 1)) {
                            prendeMargemRenegociacaoMenor = true;
                            ocaCodigoPrendeMargemReneg = criaOcorrenciaADE(autdes.getAdeCodigo(), CodedValues.TOC_RETENCAO_MARGEM_DENTRO_PRAZO_RENEGOCIACAO, ApplicationResourcesHelper.getMessage("mensagem.info.ocorrencia.margem.preza.renegociacao", responsavel), totalRenegociado, autdes.getAdeVlr(), responsavel);
                        }

                        // Se o valor da nova autorização é maior do que o da antiga
                        // prende da margem apenas o valor da antiga, pois a diferença
                        // de valor já está presa OU prende a margem quando o valor da renegociação é menor que o
                        // contrato novo, até que o prazo de cancelamento tenha sido cumprido, predemos o valor total da renegocição que é o que ele já tinha
                        // na margem
                        try {
                            if ((autdes.getAdeVlr().compareTo(totalRenegociado) == 1) || prendeMargemRenegociacaoMenor) {
                                atualizaMargem(rseCodigo, adeIncMargem, totalRenegociado, validaMargem, true, true, TextHelper.isNull(ocaCodigoPrendeMargemReneg) ? ocaCodigo : ocaCodigoPrendeMargemReneg, csaCodigo, svcCodigo, null, responsavel);
                            } else {
                                atualizaMargem(rseCodigo, adeIncMargem, autdes.getAdeVlr(), validaMargem, true, true, ocaCodigo, csaCodigo, svcCodigo, null, responsavel);
                            }
                        } catch (final AutorizacaoControllerException ex) {
                            BigDecimal limite = new BigDecimal("0.00");
                            final CustomTransferObject param = getParametroSvc(CodedValues.TPS_VLR_LIMITE_ADE_SEM_MARGEM, svcCodigo, BigDecimal.ZERO, false, null);
                            if ((param != null) && (param.getAttribute(Columns.PSE_VLR) != null)) {
                                try {
                                    limite = new BigDecimal(param.getAttribute(Columns.PSE_VLR).toString());
                                } catch (final NumberFormatException nfex) {
                                    LOG.debug("Valor do parametro de limite de ade sem margem invalido: " + param.getAttribute(Columns.PSE_VLR));
                                }
                            }
                            /*
                             * Se não foi possível reservar todo o valor, verifica se o serviço tem
                             * limite para autorizações sem margem. Verifica também se não é um compulsório,
                             * e caso positivo, pesquisa o vlr total dos contratos que podem ser estocados
                             * liberando assim vlr de margem para a inclusão
                             */
                            // DESENV-14240 - Ao deferir um consigação feita com usuário sem permissão de confirmar é necessário validar o limite.
                            atualizaMargemCompulsorios(rseCodigo, svcCodigo, csaCodigo, autdes.getAdeCodigo(), null, autdes.getAdeVlr(), limite, adeIncMargem, null, true, true, false, responsavel);
                        }
                    }
                }

                if (ocorrenciaInformacaoDataInicioMargem) {
                    criaOcorrenciaADE(autdes.getAdeCodigo(), CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.info.calcular.data.inicio.fim.margem.extra.rse.data.margem.maior", responsavel), responsavel);
                }

                // Executa a atualização do contrato
                AbstractEntityHome.update(autdes);

                // Defere os contratos de correção de saldo relacionados a este contrato
                final List<RelacionamentoAutorizacao> radCorrecao = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_CORRECAO_SALDO);
                if ((radCorrecao != null) && !radCorrecao.isEmpty()) {
                    for (final RelacionamentoAutorizacao radCorrecaoBean : radCorrecao) {
                        final String adeCodigoCorrecao = radCorrecaoBean.getAutDescontoByAdeCodigoDestino().getAdeCodigo();
                        final AutDesconto adeCorrecao = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigoCorrecao);
                        this.deferir(adeCorrecao, senhaUtilizada, false, false, tipoMotivoOperacao, ocaPeriodo, responsavel);
                    }
                }

                // Finaliza o processo de deferimento do contrato.
                finalizarDeferimentoConsignacao(adeCodigo, svcCodigo, responsavel);

                // DESENV-8933: se responsável pela operação tiver seu papel configurado para disparar e-mail de alerta de alteração na ade
                // para entidades relacionadas a este, também configuradas na tabela tb_destinatario_email
                final ProcessaEnvioEmailEntidadesAltAde processoEmail = new ProcessaEnvioEmailEntidadesAltAde(OperacaoEConsigEnum.DEFERIR_CONSIGNACAO, adeCodigo, null, tipoMotivoOperacao, responsavel);
                processoEmail.start();

                // Envia mensagem de deferimento da consignação para o servidor
                if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_ENVIO_DE_SMS_EMAIL_APOS_DEFERIMENTO_CONSIGNACAO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                    enviaMensagem(autdes.getAdeNumero(), svcCodigo, convenio.getConsignataria(), rseCodigo, responsavel);
                }
            }

            return ocaCodigo;
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (ex.getClass().equals(AutorizacaoControllerException.class) || ex.getClass().equals(GerenciadorAutorizacaoException.class) || ex.getClass().equals(PeriodoException.class)) {
                throw new AutorizacaoControllerException(ex);
            } else {
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    private void alterarPeriodoInclusao(AutDesconto autdes, Date adeAnoMesIni, String orgCodigo, AcessoSistema responsavel) throws PeriodoException, FindException, UpdateException, AutorizacaoControllerException {
        // Atualiza data inicial e data inicial de referência
        autdes.setAdeAnoMesIni(adeAnoMesIni);
        autdes.setAdeAnoMesIniRef(adeAnoMesIni);

        // Se a consignação tem prazo definido, atualiza também a data final
        if (autdes.getAdePrazo() != null) {
            final Date adeAnoMesFim = PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, adeAnoMesIni, autdes.getAdePrazo(), autdes.getAdePeriodicidade(), responsavel);

            calcularDataIniFimMargemExtra(autdes.getRseCodigo(), adeAnoMesFim, autdes.getAdeIncMargem(), false, true, responsavel);
            autdes.setAdeAnoMesFim(adeAnoMesFim);
            autdes.setAdeAnoMesFimRef(adeAnoMesFim);
        }

        // Pesquisa ocorrência de inclusão para atualização do período
        final List<OcorrenciaAutorizacao> ocorrencias = OcorrenciaAutorizacaoHome.findByAdeTocCodigo(autdes.getAdeCodigo(), CodedValues.TOC_TARIF_RESERVA);
        if ((ocorrencias != null) && !ocorrencias.isEmpty()) {
            for (final OcorrenciaAutorizacao ocorrencia : ocorrencias) {
                ocorrencia.setOcaPeriodo(adeAnoMesIni);
                AbstractEntityHome.update(ocorrencia);
            }
        }

        // Atualiza o período dos anexos que a consignação eventualmente possua, de acordo com o período de lançamento no momento do deferimento.
        final CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(Columns.AAD_ADE_CODIGO, autdes.getAdeCodigo());

        try {
            final List<TransferObject> anexos = editarAnexoConsignacaoController.lstAnexoAutorizacaoDesconto(criterio, -1, -1, responsavel);
            for (final TransferObject anexo : anexos) {
                AnexoAutorizacaoDescontoHome.updateDataPeriodo(autdes.getAdeCodigo(), anexo.getAttribute(Columns.AAD_NOME).toString(), adeAnoMesIni);
            }
        } catch (final AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void indeferir(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final AutDesconto adeBean = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
            if (!CodedValues.SAD_AGUARD_DEFER.equals(adeBean.getStatusAutorizacaoDesconto().getSadCodigo())) {
                throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.indeferida.situacao.atual.dela.nao.permite.operacao", responsavel, adeBean.getStatusAutorizacaoDesconto().getSadDescricao());
            }

            final VerbaConvenio verbaConvenio = VerbaConvenioHome.findByPrimaryKey(adeBean.getVerbaConvenio().getVcoCodigo());
            final Convenio convenio = ConvenioHome.findByPrimaryKey(verbaConvenio.getConvenio().getCnvCodigo());
            final String csaCodigo = convenio.getConsignataria().getCsaCodigo();
            final String svcCodigo = convenio.getServico().getSvcCodigo();

            //verifica Bloquear deferimento/indeferimento caso o CPF do gestor seja igual ao CPF do contrato (tpc 469)
            if (ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_DEFER_INDEFER_CPF_IGUAL, CodedValues.TPC_SIM, responsavel)) {
                final Usuario usuarioResponsavel = UsuarioHome.findByPrimaryKey(responsavel.getUsuCodigo());
                final RegistroServidor rseDonoContrato = RegistroServidorHome.findByPrimaryKey(adeBean.getRegistroServidor().getRseCodigo());
                final Servidor servidorDonoContrato = ServidorHome.findByPrimaryKey(rseDonoContrato.getServidor().getSerCodigo());
                if (!TextHelper.isNull(usuarioResponsavel.getUsuCpf()) && !TextHelper.isNull(servidorDonoContrato.getSerCpf()) && usuarioResponsavel.getUsuCpf().equals(servidorDonoContrato.getSerCpf())) {
                    throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.indeferida.cpf.gestor.igual.contrato", responsavel);
                }
            }

            final String ocaCodigo = modificaSituacaoADE(adeBean, CodedValues.SAD_INDEFERIDA, responsavel);

            // Insere ocorrência de deferimento.
            criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INDEFERIMENTO_CONTRATO, ApplicationResourcesHelper.getMessage("mensagem.informacao.contrato.indeferido", responsavel).toUpperCase(), responsavel);

            // Volta a situação da autorização que foi renegociada para esta ade que está sendo cancelada.
            // Pega código da autorização que foi renegociada e que está
            // aguardando a confirmação/deferimento desta autorização para ser liquidada
            final List<RelacionamentoAutorizacao> radRenegociacao = RelacionamentoAutorizacaoHome.findByDestino(adeCodigo, CodedValues.TNT_CONTROLE_RENEGOCIACAO);
            if ((radRenegociacao != null) && !radRenegociacao.isEmpty()) {
                BigDecimal totalRenegociado = BigDecimal.ZERO;
                Short adeIncMargem = CodedValues.INCIDE_MARGEM_SIM;
                for (final RelacionamentoAutorizacao radBean : radRenegociacao) {
                    final String adeCodigoRenegociada = radBean.getAutDescontoByAdeCodigoOrigem().getAdeCodigo();

                    final AutDesconto adeRenegociadaBean = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigoRenegociada);

                    // Somente modifica a ADE que está aguardando liquidação. (DESENV-7630)
                    if (CodedValues.SAD_AGUARD_LIQUIDACAO.equals(adeRenegociadaBean.getStatusAutorizacaoDesconto().getSadCodigo())) {
                        final String sadNovo = ((adeRenegociadaBean.getAdePrdPagas() != null) && (adeRenegociadaBean.getAdePrdPagas().intValue() > 0)) ? CodedValues.SAD_EMANDAMENTO : CodedValues.SAD_DEFERIDA;
                        modificaSituacaoADE(adeRenegociadaBean, sadNovo, responsavel);
                    }

                    totalRenegociado = totalRenegociado.add(adeRenegociadaBean.getAdeVlr());
                    adeIncMargem = (adeRenegociadaBean.getAdeIncMargem() != null) ? adeRenegociadaBean.getAdeIncMargem() : CodedValues.INCIDE_MARGEM_SIM;
                }
                if (!adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
                    // Se o valor da autorização nova é maior do que a renegociada, libera
                    // a diferença de valores presa na renegociação
                    final BigDecimal diff = adeBean.getAdeVlr().subtract(totalRenegociado);
                    if (diff.signum() == 1) {
                        /*
                         * Passa false para validação de margem, pois estamos liberando margem
                         * e não prendendo.
                         */
                        atualizaMargem(adeBean.getRegistroServidor().getRseCodigo(), adeIncMargem, diff.negate(), false, true, true, ocaCodigo, csaCodigo, svcCodigo, null, responsavel);
                    }
                }
            }

            // grava motivo da operacao
            if ((ocaCodigo != null) && (tipoMotivoOperacao != null)) {
                tipoMotivoOperacao.setAttribute(Columns.OCA_CODIGO, ocaCodigo);
                tipoMotivoOperacaoController.gravarMotivoOperacaoConsignacao(tipoMotivoOperacao, responsavel);
            }

            // DESENV-8933: se responsável pela operação tiver seu papel configurado para disparar e-mail de alerta de alteração na ade
            // para entidades relacionadas a este, também configuradas na tabela tb_destinatario_email
            final ProcessaEnvioEmailEntidadesAltAde processoEmail = new ProcessaEnvioEmailEntidadesAltAde(OperacaoEConsigEnum.INDEFERIR_CONSIGNACAO, adeCodigo, null, tipoMotivoOperacao, responsavel);
            processoEmail.start();

            // Gera o Log de auditoria
            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.INDEFERIR_CONSIGNACAO, Log.LOG_INFORMACAO);
            logDelegate.setAutorizacaoDesconto(adeCodigo);
            logDelegate.write();

        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw new AutorizacaoControllerException(ex);
            } else {
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    /**
     * Realiza a finalização do processo de deferimento de consignação.
     * @param adeCodigo Contrato sendo deferido.
     * @param svcCodigo Código do serviço da consignação
     * @param responsavel
     * @throws GerenciadorAutorizacaoException
     */
    private void finalizarDeferimentoConsignacao(String adeCodigo, String svcCodigo, AcessoSistema responsavel) throws GerenciadorAutorizacaoException {
        try {
            // Classe de especialização das operações de autorização por serviço.
            GerenciadorAutorizacao gerenciadorAutorizacaoServico = null;

            // Verifica se há classe específica de manutenção de autorização para o serviço.
            final CustomTransferObject paramClasseGerenciadorAutorizacao = getParametroSvc(CodedValues.TPS_CLASSE_GERENCIADOR_AUTORIZACAO, svcCodigo, "", false, null);
            if ((paramClasseGerenciadorAutorizacao != null) && !TextHelper.isNull(paramClasseGerenciadorAutorizacao.getAttribute(Columns.PSE_VLR))) {
                final String nomeClasseGerenciadorAutorizacao = (String) paramClasseGerenciadorAutorizacao.getAttribute(Columns.PSE_VLR);
                gerenciadorAutorizacaoServico = GerenciadorAutorizacaoFactory.getGerenciadorAutorizacao(nomeClasseGerenciadorAutorizacao);
            }

            // Se há classe específica de manutençao das autorizações do serviço.
            if (gerenciadorAutorizacaoServico != null) {
                final long horaInicioFinalizacao = java.util.Calendar.getInstance().getTimeInMillis();
                gerenciadorAutorizacaoServico.finalizarDeferimentoConsignacao(adeCodigo);
                LOG.debug("TOTAL FINALIZACAO DEFERIMENTO CONSIGNACAO (" + responsavel.getUsuCodigo() + ") = " + (java.util.Calendar.getInstance().getTimeInMillis() - horaInicioFinalizacao) + " ms");
            }
        } catch (final AutorizacaoControllerException ex) {
            throw new GerenciadorAutorizacaoException("mensagem.erro.finalizar.deferimento.consignacao", responsavel, ex);
        }
    }

    /**
     * confere se o contrato a ser confirmado é destino de um relacionamento insere/altera.
     * Se sim, este contrato é cancelado e o contrato origem é alterado de acordo com as regras de insere/altera
     * @param autdesDestino - adeCodigo do contrato candidato a ser destino de relacionamento insere/altera
     * @param svcCodigo
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     */
    private String concluiInsereAlteraNaoAutomatico(AutDesconto autdesDestino, String svcCodigo, String orgCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        List<RelacionamentoAutorizacao> lstRel = null;
        try {
            lstRel = RelacionamentoAutorizacaoHome.findByDestino(autdesDestino.getAdeCodigo(), CodedValues.TNT_CONTRATO_GERADO_INSERE_ALTERA);
        } catch (final FindException e) {
            return null;
        }

        if ((lstRel != null) && !lstRel.isEmpty()) {
            final Integer adePrazoDestino = autdesDestino.getAdePrazo();

            final RelacionamentoAutorizacao relInsereAltera = lstRel.iterator().next();

            final String adeCodigoOrigem = relInsereAltera.getAdeCodigoOrigem();
            AutDesconto autdesOrigem = null;
            try {
                autdesOrigem = AutDescontoHome.findByPrimaryKey(adeCodigoOrigem);
            } catch (final FindException ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new AutorizacaoControllerException("mensagem.erro.contrato.nao.encontrado", responsavel, ex);
            }

            //caso a ADE pai esteja suspensa não faz o deferimento
            if (CodedValues.SAD_CODIGOS_SUSPENSOS.contains(autdesOrigem.getStatusAutorizacaoDesconto().getSadCodigo())) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new AutorizacaoControllerException("mensagem.erro.insere.altera.ade.situacao.nao.permitida", responsavel);
            }

            BigDecimal adeVlr = autdesOrigem.getAdeVlr();
            BigDecimal adeVlrTac = autdesOrigem.getAdeVlrTac();
            BigDecimal adeVlrIof = autdesOrigem.getAdeVlrIof();
            BigDecimal adeVlrLiquido = autdesOrigem.getAdeVlrLiquido();
            BigDecimal adeVlrMensVinc = autdesOrigem.getAdeVlrMensVinc();

            final BigDecimal ocaAdeVlrAnt = adeVlr;
            adeVlr = adeVlr.add(autdesDestino.getAdeVlr());

            if ((adeVlrTac != null) && (autdesDestino.getAdeVlrTac() != null)) {
                adeVlrTac = adeVlrTac.add(autdesDestino.getAdeVlrTac());
            }
            if ((adeVlrIof != null) && (autdesDestino.getAdeVlrIof() != null)) {
                adeVlrIof = adeVlrIof.add(autdesDestino.getAdeVlrIof());
            }
            if ((adeVlrLiquido != null) && (autdesDestino.getAdeVlrLiquido() != null)) {
                adeVlrLiquido = adeVlrLiquido.add(autdesDestino.getAdeVlrLiquido());
            }
            if ((adeVlrMensVinc != null) && (autdesDestino.getAdeVlrMensVinc() != null)) {
                adeVlrMensVinc = adeVlrMensVinc.add(autdesDestino.getAdeVlrMensVinc());
            }

            // insereAlteraUsandoMaiorPrazo : True, se o serviço é do tipo Insere/Altera independente do período
            final CustomTransferObject paramIncAltUsaMaiorPrazo = getParametroSvc(CodedValues.TPS_OP_INSERE_ALTERA_USA_MAIOR_PRAZO, svcCodigo, Boolean.FALSE, false, null);
            final boolean insereAlteraUsandoMaiorPrazo = ((paramIncAltUsaMaiorPrazo != null) && (paramIncAltUsaMaiorPrazo.getAttribute(Columns.PSE_VLR) != null) && ((Boolean) paramIncAltUsaMaiorPrazo.getAttribute(Columns.PSE_VLR)).booleanValue());

            final Integer adePrazoAtual = autdesOrigem.getAdePrazo();
            final Integer adePrdPagas = autdesOrigem.getAdePrdPagas() != null ? autdesOrigem.getAdePrdPagas() : 0;
            java.util.Date adeAnoMesFimFinal = null;
            Integer adePrazoFinal = null;
            if ((adePrazoDestino != null) && insereAlteraUsandoMaiorPrazo) {
                adeAnoMesFimFinal = ((adePrazoAtual - adePrdPagas) > adePrazoDestino) ? ((autdesOrigem.getAdeAnoMesFim() != null) ? autdesOrigem.getAdeAnoMesFim() : autdesOrigem.getAdeAnoMesFimRef()) : ((autdesDestino.getAdeAnoMesFim() != null) ? autdesDestino.getAdeAnoMesFim() : autdesDestino.getAdeAnoMesFimRef());
                final java.util.Date adeAnoMesIniFinal = ((autdesOrigem.getAdeAnoMesIni() != null) ? autdesOrigem.getAdeAnoMesIni() : autdesOrigem.getAdeAnoMesIniRef());
                try {
                    adePrazoFinal = PeriodoHelper.getInstance().calcularPrazo(orgCodigo, adeAnoMesIniFinal, adeAnoMesFimFinal, autdesOrigem.getAdePeriodicidade(), responsavel);
                } catch (final PeriodoException ex) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                }
            } else {
                adeAnoMesFimFinal = (autdesOrigem.getAdeAnoMesFim() != null) ? autdesOrigem.getAdeAnoMesFim() : autdesOrigem.getAdeAnoMesFimRef();
                adePrazoFinal = adePrazoAtual;
            }

            try {
                final String adeCodigo = autdesOrigem.getAdeCodigo();
                LOG.debug("Insere/Altera AdeCodigo: " + adeCodigo);

                // Executa a alteração do contrato original
                final AlterarConsignacaoParametros alterarParam = new AlterarConsignacaoParametros(adeCodigo, adeVlr, adePrazoFinal, autdesOrigem.getAdeIdentificador(), false, autdesOrigem.getAdeIndice(), adeVlrTac, adeVlrIof, adeVlrLiquido, adeVlrMensVinc, autdesOrigem.getAdeTaxaJuros(), adeAnoMesFimFinal, null, null, true, true, true, true, true, true, true, true, (Integer) null, (String) null, (String) null, false);
                alterarParam.setAdePeriodicidade(autdesOrigem.getAdePeriodicidade());
                alterarConsignacaoController.alterar(alterarParam, responsavel);

                // Cancela o contrato destino do relacionamento
                // Insere ocorrencia de Insere/Altera
                final String ocaCodigo = criaOcorrenciaADE(adeCodigo, CodedValues.TOC_ALTERACAO_INCLUSAO_CONTRATO, ApplicationResourcesHelper.getMessage("mensagem.informacao.alteracao.inclusao.contrato", responsavel), ocaAdeVlrAnt, adeVlr, responsavel);

                cancelarConsignacaoController.cancelar(autdesDestino.getAdeCodigo(), responsavel);

                return ocaCodigo;
            } catch (final AutorizacaoControllerException ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw ex;
            }
        }

        return null;
    }

    @Override
    public void executarDeferimentoAutomatico(AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // Busca os contratos para deferimento automático
            final ListaConsignacaoDeferAutomaticoQuery lstConsigQuery = new ListaConsignacaoDeferAutomaticoQuery();
            final List<String> adeCodigos = lstConsigQuery.executarLista();
            if ((adeCodigos != null) && !adeCodigos.isEmpty()) {
                for (final String adeCodigo : adeCodigos) {
                    // Executa rotina de deferimento
                    deferir(adeCodigo, null, responsavel);

                    // Cria ocorrência de aviso de deferimento automático
                    criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AVISO, ApplicationResourcesHelper.getMessage("mensagem.informacao.deferimento.automatico.consignacao.singular", responsavel), responsavel);
                }

                // Se o sistema permite deferimento pelas consignatárias, verifica quais
                // consignatárias estão relacionadas aos contratos que foram automaticamente
                // deferidos, através do relacionamento de serviço que exige deferimento manual
                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_DEFERIMENTO_TERCEIROS_PELA_CSA, CodedValues.TPC_SIM, responsavel) && NaturezaRelSvc.getInstance().exists(CodedValues.TNT_CONTRATO_PREEXISTENTE_REQUER_DEFERIMENTO)) {
                    final ObtemCsaBloqRelSvcRequerDeferimentoQuery query = new ObtemCsaBloqRelSvcRequerDeferimentoQuery();
                    query.adeCodigos = adeCodigos;
                    final List<String> csaCodigos = query.executarLista();
                    if ((csaCodigos != null) && !csaCodigos.isEmpty()) {
                        // Executa desbloqueio, caso existam consignatárias a serem bloqueadas
                        consignatariaController.bloquearConsignatarias(csaCodigos, ApplicationResourcesHelper.getMessage("mensagem.informacao.bloqueio.automatico.consignataria.por.nao.deferir.contratos.dentro.prazo", responsavel), CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, responsavel);
                    }
                }

                // Grava log da operação de deferimento automático
                final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.UPDATE, Log.LOG_INFORMACAO);
                log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.deferimento.automatico.consignacao.plural", responsavel));
                log.write();
            }
        } catch (final LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void enviaMensagem(Long adeNumero, String svcCodigo, Consignataria csa, String rseCodigo, AcessoSistema responsavel) throws ZetraException {
        String emailDestinatario = null;
        String celularDestinatario = null;
        String corpo = null;

        // Busca mensagem a ser enviada
        final List<String> tpsCodigo = new ArrayList<>();
        tpsCodigo.add(CodedValues.TPS_MENSAGEM_PARA_SERVIDOR_APOS_SOLICITACAO_DEFERIDA);

        final List<TransferObject> parametros = parametroController.selectParamSvcCsa(svcCodigo, csa.getCsaCodigo(), tpsCodigo, false, responsavel);

        final Iterator<TransferObject> it = parametros.iterator();
        CustomTransferObject next = null;
        while (it.hasNext()) {
            next = (CustomTransferObject) it.next();
            if (CodedValues.TPS_MENSAGEM_PARA_SERVIDOR_APOS_SOLICITACAO_DEFERIDA.equals(next.getAttribute(Columns.TPS_CODIGO))) {
                corpo = next.getAttribute(Columns.PSC_VLR).toString();
            }
        }

        // Busca dados do servidor
        final ServidorTransferObject servidor = servidorController.findServidorByRseCodigo(rseCodigo, responsavel);

        if (servidor != null) {
            // Formata o telefone para o padrão do país
            celularDestinatario = !TextHelper.isNull(servidor.getSerCelular()) ? LocaleHelper.formataCelular(servidor.getSerCelular()) : null;
            emailDestinatario = servidor.getSerEmail();
        }

        if (!TextHelper.isNull(celularDestinatario)) {
            // Envia o SMS.
            try {
                final String accountSid = ParamSist.getInstance().getParam(CodedValues.TPC_SID_CONTA_SMS, responsavel).toString();
                final String authToken = ParamSist.getInstance().getParam(CodedValues.TPC_TOKEN_AUTENTICACAO_SMS, responsavel).toString();
                final String fromNumber = ParamSist.getInstance().getParam(CodedValues.TPC_NUMERO_REMETENTE_SMS, responsavel).toString();

                // Se a CSA não cadastrou uma mensagem, envia a mensagem padrão.
                if (TextHelper.isNull(corpo)) {
                    corpo = ApplicationResourcesHelper.getMessage("mensagem.sms.consignacao.aprovada", responsavel, adeNumero.toString(), csa.getCsaNome());
                    new SMSHelper(accountSid, authToken, fromNumber).send(celularDestinatario, corpo);
                }
            } catch (final ZetraException e) {
                throw new ViewHelperException("mensagem.erro.sms.enviar", responsavel, e);
            }
        } else if (!TextHelper.isNull(emailDestinatario)) {
            // Envia o e-mail caso o telefone não tenha sido informado
            EnviaEmailHelper.enviaEmailNotificacaoConsignacaoDeferida(csa.getCsaNome(), emailDestinatario, adeNumero.toString(), corpo, responsavel);
        }
    }
}
