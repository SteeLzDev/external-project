package com.zetra.econsig.service.consignacao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.parametros.LiquidarConsignacaoParametros;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.persistence.entity.Calendario;
import com.zetra.econsig.service.calendario.CalendarioController;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConvenioTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.GerenciadorAutorizacaoException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LeilaoSolicitacaoControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.consignacao.AutorizacaoHelper;
import com.zetra.econsig.helper.consignacao.StatusAutorizacaoDesconto;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.gerenciadorautorizacao.GerenciadorAutorizacao;
import com.zetra.econsig.helper.gerenciadorautorizacao.GerenciadorAutorizacaoFactory;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.servico.NaturezaRelSvc;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessaEnvioEmailEntidadesAltAde;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.BloqueioRseFun;
import com.zetra.econsig.persistence.entity.BloqueioRseFunHome;
import com.zetra.econsig.persistence.entity.BloqueioRseFunId;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacao;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacaoHome;
import com.zetra.econsig.persistence.entity.ParcelaDesconto;
import com.zetra.econsig.persistence.entity.ParcelaDescontoHome;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodo;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodoHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacao;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.StatusAutorizacaoDescontoHome;
import com.zetra.econsig.persistence.entity.VerbaConvenio;
import com.zetra.econsig.persistence.entity.VerbaConvenioHome;
import com.zetra.econsig.persistence.query.compra.CompraPassivelCancelamentoQuery;
import com.zetra.econsig.persistence.query.compra.CompraPossuiRejeitoPgtSaldoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoCancAutomaticoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoCancelamentoParametroCsaQuery;
import com.zetra.econsig.persistence.query.consignataria.ObtemConsignatariaUsuarioQuery;
import com.zetra.econsig.persistence.query.leilao.ListaLeilaoCancAutomaticoQuery;
import com.zetra.econsig.persistence.query.leilao.ListaSolicitacaoLeilaoCanceladoParaBloqueioQuery;
import com.zetra.econsig.persistence.query.parametro.ListaRelacionamentosQuery;
import com.zetra.econsig.persistence.query.parcela.ObtemTotalParcelasPagasQuery;
import com.zetra.econsig.persistence.query.parcela.ObtemTotalParcelasPeriodoQuery;
import com.zetra.econsig.persistence.query.parcela.ObtemTotalParcelasQuery;
import com.zetra.econsig.persistence.query.periodo.ObtemDatasUltimoPeriodoExportadoQuery;
import com.zetra.econsig.service.compra.CompraContratoController;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoEConsigEnum;
import com.zetra.econsig.values.StatusCompraEnum;

/**
 * <p>Title: CancelarConsignacaoControllerBean</p>
 * <p>Description: Session Bean para a operação de Cancelamento de Contrato.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class CancelarConsignacaoControllerBean extends AutorizacaoControllerBean implements CancelarConsignacaoController {
    private static final org.apache.commons.logging.Log LOG = LogFactory.getLog(CancelarConsignacaoControllerBean.class);

    @Autowired
    private CompraContratoController compraContratoController;

    @Autowired
    private LiquidarConsignacaoController liquidarConsignacaoController;

    @Autowired
    private LeilaoSolicitacaoController leilaoSolicitacaoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Autowired
    private CalendarioController calendarioController;

    @Override
    public void cancelar(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        cancelar(adeCodigo, true, true, false, null, responsavel);
    }

    @Override
    public void cancelar(String adeCodigo, boolean verificaStatusAde, AcessoSistema responsavel) throws AutorizacaoControllerException {
        cancelar(adeCodigo, verificaStatusAde, true, false, null, responsavel);
    }

    @Override
    public void cancelar(String adeCodigo, boolean verificaStatusAde, boolean verificaStatusServidor, AcessoSistema responsavel) throws AutorizacaoControllerException {
        cancelar(adeCodigo, verificaStatusAde, verificaStatusServidor, false, null, responsavel);
    }

    @Override
    public void cancelar(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        cancelar(adeCodigo, true, true, false, tipoMotivoOperacao, responsavel);
    }

    @Override
    public void cancelar(String adeCodigo, boolean verificaStatusAde, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        cancelar(adeCodigo, verificaStatusAde, true, false, tipoMotivoOperacao, responsavel);
    }

    /**
     * Cancela a consignação
     * @param adeCodigo Código da consignação
     * @param verificaStatusAde Indica se a validação de status da consignação deve ser realizada.
     * @param verificaStatusServidor Indica se a validação de status do servidor deve ser realizada.
     * @param verificaStatusAdeCancAutomatico Indica se a validação de status da consignação deve ser realizada no cancelamento automático.
     * @param tipoMotivoOperacao Tipo de motivo da operação.
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    private void cancelar(String adeCodigo, boolean verificaStatusAde, boolean verificaStatusServidor, boolean verificaStatusAdeCancAutomatico, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if (usuarioPodeModificarAde(adeCodigo, responsavel)) {
            try {
                final AutDesconto adeBean = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);

                final String sadCodigo = adeBean.getStatusAutorizacaoDesconto().getSadCodigo().trim();
                if (verificaStatusAde) {
                    if (CodedValues.SAD_CANCELADA.equals(sadCodigo) || CodedValues.SAD_LIQUIDADA.equals(sadCodigo) ||
                            CodedValues.SAD_CONCLUIDO.equals(sadCodigo) || CodedValues.SAD_ENCERRADO.equals(sadCodigo) || CodedValues.SAD_INDEFERIDA.equals(sadCodigo)) {
                        throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.cancelada.porque.situacao.atual.dela.nao.permite.esta.operacao", responsavel, adeBean.getStatusAutorizacaoDesconto().getSadDescricao());
                    } else if (CodedValues.SAD_EMANDAMENTO.equals(sadCodigo)) {
                        throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.cancelar.consignacao.em.andamento.se.necessario.use.liquidar", responsavel);
                    } else if (!(CodedValues.SAD_SOLICITADO.equals(sadCodigo) || CodedValues.SAD_AGUARD_CONF.equals(sadCodigo)) && responsavel.isSer()) {
                        throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.cancelada.porque.situacao.atual.dela.nao.permite.esta.operacao", responsavel, adeBean.getStatusAutorizacaoDesconto().getSadDescricao());
                    }
                }

                /**
                 * No cancelamento automático, verificar se o status do contrato não é solicitação,
                 * aguardando confirmação ou aguardando deferimento.
                 */
                if (verificaStatusAdeCancAutomatico) {
                    if (!(CodedValues.SAD_SOLICITADO.equals(sadCodigo) || CodedValues.SAD_AGUARD_CONF.equals(sadCodigo) ||
                            CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo))) {
                        return;
                    }
                }

                // Cancelar (minhas) reserva(s) opera apenas sobre os três status: SAD_SOLICITADO, SAD_AGUARD_CONF e SAD_AGUARD_DEFER
                if (((responsavel.getFunCodigo() != null) && (CodedValues.FUN_CANC_RESERVA.equals(responsavel.getFunCodigo()) || CodedValues.FUN_CANC_MINHAS_RESERVAS.equals(responsavel.getFunCodigo())))) {
                    if (!(CodedValues.SAD_SOLICITADO.equals(sadCodigo) || CodedValues.SAD_AGUARD_CONF.equals(sadCodigo) ||
                        CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo) || CodedValues.SAD_AGUARD_MARGEM.equals(sadCodigo)) ||
                        ((CodedValues.FUN_CANC_MINHAS_RESERVAS.equals(responsavel.getFunCodigo())) && !adeBean.getUsuario().getUsuCodigo().equals(responsavel.getUsuCodigo()))) {
                        throw new AutorizacaoControllerException("mensagem.erro.usuario.nao.tem.permissao.para.cancelar.esta.autorizacao", responsavel);
                    }
                }

                String ocaCodigo = null;

                // Busca o código do serviço desta consignação
                final VerbaConvenio verbaConvenio = VerbaConvenioHome.findByPrimaryKey(adeBean.getVerbaConvenio().getVcoCodigo());
                final Convenio convenio = ConvenioHome.findByPrimaryKey(verbaConvenio.getConvenio().getCnvCodigo());
                final String svcCodigo = convenio.getServico().getSvcCodigo();
                final String csaCodigo = convenio.getConsignataria().getCsaCodigo();

                // recupera a margem restante antes do cancelamento
                BigDecimal rseMargemRest = null;
                String emailCsaSerCancelouSolicitacao = null;

                //DESENV-10999: para solicitação canceladas pelo próprio servidor, envia e-mail (se esta estiver definida) de alerta à consignatária para a qual foi feita a solicitação.
                if (responsavel.isSer() && CodedValues.SAD_SOLICITADO.equals(sadCodigo)) {
                    final List<String> tpsSvcCsa = new ArrayList<>();
                    tpsSvcCsa.add(CodedValues.TPS_EMAIL_SOLICITACAO_INCLUIDA_OU_CANCELADA_PELO_SERVIDOR);

                    final List<TransferObject> lstSvcCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsSvcCsa, false, responsavel);
                    if ((lstSvcCsa != null) && !lstSvcCsa.isEmpty()) {
                        emailCsaSerCancelouSolicitacao = (String) lstSvcCsa.get(0).getAttribute(Columns.PSC_VLR);
                        final MargemDisponivel margemDisponivel = new MargemDisponivel(adeBean.getRegistroServidor().getRseCodigo(), null, svcCodigo, adeBean.getAdeIncMargem(), responsavel);
                        rseMargemRest = margemDisponivel.getMargemRestante();
                    }
                }

                // Verifica relacionamento de bloqueio de operação
                verficarRelacionametoBloqueioOperacao(adeCodigo, "cancelar", responsavel);

                // Verifica relacionamento de serviço de saldo de parcelas
                if (NaturezaRelSvc.getInstance().exists(CodedValues.TNT_SALDO_PARCELAS)) {
                    final ListaRelacionamentosQuery queryRel = new ListaRelacionamentosQuery();
                    queryRel.tntCodigo = CodedValues.TNT_SALDO_PARCELAS;
                    queryRel.svcCodigoOrigem = null;
                    queryRel.svcCodigoDestino = svcCodigo;
                    final List<TransferObject> servicos = queryRel.executarDTO();
                    if ((servicos != null) && (servicos.size() > 0)) {
                        /*
                         * Se o serviço da consignação é o destino em um relacionamento de saldo
                         * de parcelas transforma o cancelamento em liquidação de contrato
                         */
                        liquidarConsignacaoController.liquidar(adeCodigo, null, null, responsavel);
                        return;
                    }
                }

                // Se for cancelamento de solicitação, executa cancelamento de processo de leilão
                if (CodedValues.SAD_SOLICITADO.equals(sadCodigo)) {
                    cancelarLeilaoSolicitacao(adeCodigo, adeBean.getRegistroServidor().getRseCodigo(), responsavel);
                }

                // Verifica se é uma reserva de cartão de crédito e se ela pode ser cancelada.
                if (CodedValues.SAD_DEFERIDA.equals(sadCodigo)) {
                    verificaReservaCartaoCredito(adeBean.getRegistroServidor().getRseCodigo(), adeBean.getAdeVlr().negate(), convenio.getCnvCodigo(), responsavel);
                }

                if (!CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(sadCodigo)) {
                    final String tocCodigo = (CodedValues.SAD_SOLICITADO.equals(sadCodigo) ||
                                        CodedValues.SAD_AGUARD_CONF.equals(sadCodigo) ||
                                        CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo) ||
                                        CodedValues.SAD_AGUARD_MARGEM.equals(sadCodigo)) ?
                                       CodedValues.TOC_TARIF_CANCELAMENTO_RESERVA :
                                       CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO;
                    verificaSituacaoPRD(adeCodigo, responsavel);
                    ocaCodigo = modificaSituacaoADE(adeBean, CodedValues.SAD_CANCELADA, responsavel);
                    criaOcorrenciaADE(adeCodigo, tocCodigo, ApplicationResourcesHelper.getMessage("mensagem.informacao.ptf.cancelamento.consignacao", responsavel), responsavel);
                }

                /*
                 * volta a situação da autorização que foi renegociada para esta ade que está sendo
                 * cancelada, apenas se o status é aguard-confirmacao ou aguard-deferimento
                 */
                if (CodedValues.SAD_AGUARD_CONF.equals(sadCodigo) || CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo)) {
                    /*
                     * Pega código da autorização que foi renegociada e que está
                     * aguardando a confirmação/deferimento desta autorização para ser liquidada
                     */
                    final List<RelacionamentoAutorizacao> adeRenegociadas = RelacionamentoAutorizacaoHome.findByDestino(adeCodigo, CodedValues.TNT_CONTROLE_RENEGOCIACAO);
                    if (!adeRenegociadas.isEmpty()) {
                        Short adeIncMargem = CodedValues.INCIDE_MARGEM_SIM;
                        BigDecimal totalRenegociado = new BigDecimal("0");
                        for (final RelacionamentoAutorizacao radBean : adeRenegociadas) {
                            final String adeRenegociada = radBean.getAutDescontoByAdeCodigoOrigem().getAdeCodigo();
                            final AutDesconto adeBeanRe = AutDescontoHome.findByPrimaryKeyForUpdate(adeRenegociada);
                            if (CodedValues.SAD_AGUARD_LIQUIDACAO.equals(adeBeanRe.getStatusAutorizacaoDesconto().getSadCodigo())) {
                                String sadNovo = ((adeBeanRe.getAdePrdPagas() != null) && (adeBeanRe.getAdePrdPagas().intValue() > 0)) ? CodedValues.SAD_EMANDAMENTO : CodedValues.SAD_DEFERIDA;
                                if(ParamSist.getBoolParamSist(CodedValues.TPC_SUSPENDER_CONTRATO_PARCELA_REJEITADA_RETORNO, responsavel)) {
                                    final List<OcorrenciaAutorizacao> ocorrenciaSuspensaoRejeitadaFolha = OcorrenciaAutorizacaoHome.findByAdeTocCodigo(adeBeanRe.getAdeCodigo(), CodedValues.TOC_SUSPENSAO_CONTRATO_PARCELA_REJEITADA);
                                    if((ocorrenciaSuspensaoRejeitadaFolha != null) && !ocorrenciaSuspensaoRejeitadaFolha.isEmpty()) {
                                        sadNovo = CodedValues.SAD_SUSPENSA;
                                    }
                                }
                                modificaSituacaoADE(adeBeanRe, sadNovo, responsavel);
                            }
                            adeIncMargem = adeBeanRe.getAdeIncMargem() != null ? adeBeanRe.getAdeIncMargem() : CodedValues.INCIDE_MARGEM_SIM;
                            totalRenegociado = totalRenegociado.add(adeBeanRe.getAdeVlr());
                        }

                        if (!adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
                            adeBean.setAdeIncMargem(adeIncMargem);
                            AbstractEntityHome.update(adeBean);

                            /*
                             * Se o valor da nova autorização é maior do que a soma do valor das renegociadas
                             * então libera o valor da diferença. Se o valor da nova é menor, não é necessário
                             * prender mais margem, pois a diferença já estava presa.
                             */
                            final BigDecimal diff = adeBean.getAdeVlr().subtract(totalRenegociado);
                            if (diff.signum() == 1) {
                                /*
                                 * Passa false para validação de margem, pois estamos liberando margem
                                 * e não prendendo.
                                 */
                                atualizaMargem(adeBean.getRegistroServidor().getRseCodigo(), adeIncMargem, diff.negate(), false, true, verificaStatusServidor, ocaCodigo, csaCodigo, svcCodigo, null, responsavel);
                            }
                        }

                    } else {
                        // Verifica se o contrato é o resultado de uma compra de contratos
                        final List<RelacionamentoAutorizacao> adeCompradas = RelacionamentoAutorizacaoHome.findByDestino(adeCodigo, CodedValues.TNT_CONTROLE_COMPRA);
                        if (adeCompradas.size() > 0) {
                            // Se não é o próprio sistema, verifica se a compra pode ser cancelada
                            if (!responsavel.isSistema()) {
                                // Se é um usuário servidor ou usuário que não possa cancelar compra, então
                                // a compra deve ser passível de cancelamento.
                                if ((responsavel.isSer() || !responsavel.temPermissao(CodedValues.FUN_CANC_COMPRA))) {
                                    final CompraPassivelCancelamentoQuery query = new CompraPassivelCancelamentoQuery();
                                    query.adeCodigo = adeCodigo;
                                    query.isSer = responsavel.isSer();
                                    final int total = query.executarContador();
                                    if (total > 0) {
                                        throw new AutorizacaoControllerException("mensagem.erro.cancelar.consignacao.situacao.compra.nao.permite", responsavel);
                                    }
                                }
                                // Verifica se o rejeito de pagamento bloqueia o cancelamento da compra.
                                if (ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_CANCELAMENTO_COMPRA_COM_REJ_PGT, CodedValues.TPC_SIM, responsavel)) {
                                    final CompraPossuiRejeitoPgtSaldoQuery query = new CompraPossuiRejeitoPgtSaldoQuery();
                                    query.adeCodigoDestino = adeCodigo;
                                    final int total = query.executarContador();
                                    if (total > 0) {
                                        throw new AutorizacaoControllerException("mensagem.erro.cancelar.consignacao.situacao.compra.nao.permite", responsavel);
                                    }
                                }
                            }

                            // Busca parâmetro de incidência de margem do serviço, já que o contrato
                            // por ser destino de compra estará com incidência igual a Zero.
                            final CustomTransferObject paramIncMargem = getParametroSvc(CodedValues.TPS_INCIDE_MARGEM, svcCodigo, Short.valueOf((short)0), false, null);
                            final Short incideMargemReserva = ((paramIncMargem != null) && (paramIncMargem.getAttribute(Columns.PSE_VLR) != null)) ? (Short) paramIncMargem.getAttribute(Columns.PSE_VLR) : CodedValues.INCIDE_MARGEM_SIM;

                            final Map<String, AutDesconto> adeUtilizadas = new HashMap<>();
                            BigDecimal valorAnterior = new BigDecimal("0");

                            for (final RelacionamentoAutorizacao radBean : adeCompradas) {
                                final String adeRenegociada = radBean.getAutDescontoByAdeCodigoOrigem().getAdeCodigo();
                                // Verifica se o ade_codigo ja nao foi utilizado
                                if (!adeUtilizadas.containsKey(adeRenegociada)) {
                                    final AutDesconto adeBeanRe = AutDescontoHome.findByPrimaryKeyForUpdate(adeRenegociada);
                                    final Short adeReIncMargem = (adeBeanRe.getAdeIncMargem() != null) ? adeBeanRe.getAdeIncMargem() : CodedValues.INCIDE_MARGEM_SIM;
                                    if (AutorizacaoHelper.valorMargemDisponivelRenegociacao(incideMargemReserva, adeBeanRe.getAdeIncMargem(), responsavel)) {
                                        valorAnterior = valorAnterior.add(adeBeanRe.getAdeVlr());
                                    }

                                    if (CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(adeBeanRe.getStatusAutorizacaoDesconto().getSadCodigo())) {
                                        final String sadNovo = ((adeBeanRe.getAdePrdPagas() != null) && (adeBeanRe.getAdePrdPagas().intValue() > 0)) ? CodedValues.SAD_EMANDAMENTO : CodedValues.SAD_DEFERIDA;

                                        Date ocaPeriodo = null;
                                        if ((ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) &&
                                                ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_SIM, responsavel)) ||
                                                ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel)) {
                                            if ((tipoMotivoOperacao != null) && !TextHelper.isNull(tipoMotivoOperacao.getAttribute(Columns.OCA_PERIODO))) {
                                                ocaPeriodo = DateHelper.parse(tipoMotivoOperacao.getAttribute(Columns.OCA_PERIODO).toString(), "yyyy-MM-dd");
                                            }
                                        }

                                        modificaSituacaoADE(adeBeanRe, sadNovo, responsavel, true, ocaPeriodo, true);
                                        criaOcorrenciaADE(adeBeanRe.getAdeCodigo(), CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.informacao.negociacao.cancelada.pelo.contrato.arg0", responsavel, adeBean.getAdeNumero().toString()), null, null, null, ocaPeriodo, null, responsavel);

                                        if (ParamSist.paramEquals(CodedValues.TPC_SUSPENDE_DESC_CSA_BLOQ_CONTROLE_COMPRA, CodedValues.TPC_SIM, responsavel)) {
                                            compraContratoController.reativarDescontoAposPendenciaCompra(adeBeanRe.getAdeCodigo(), false, ocaPeriodo, responsavel);
                                        }

                                    } else if (CodedValues.SAD_LIQUIDADA.equals(adeBeanRe.getStatusAutorizacaoDesconto().getSadCodigo())) {
                                        if (!adeReIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO) &&
                                                adeBean.getAdeIncMargem().equals(CodedValues.INCIDE_MARGEM_NAO)) {
                                            /*
                                             * Se estamos cancelando uma ade fruto de compra, e um dos contratos comprados
                                             * já foi liquidado, então libera o valor de margem do liquidado, pois durante
                                             * a sua liquidação nenhuma margem foi liberada. Passa false como validação
                                             * de margem, pois estamos liberando margem.
                                             * OBS: Apenas se a nova autorização ainda está com incide margem zero
                                             * pois se já estiver com o indice margem correto, a margem terá sido
                                             * liberada na alteração da situação de 1 para 7.
                                             * Nunca deve verificar o Status do Servidor no cancelamento de compra.
                                             */
                                            atualizaMargem(adeBeanRe.getRegistroServidor().getRseCodigo(), adeBeanRe.getAdeIncMargem(), adeBeanRe.getAdeVlr().negate(), false, true, false, ocaCodigo, csaCodigo, svcCodigo, null, responsavel);
                                        }
                                    }
                                    adeUtilizadas.put(adeRenegociada, adeBeanRe);
                                }
                            }

                            // Atualiza os relacionamentos de compra para Cancelados
                            compraContratoController.updateStatusRelacionamentoAdesCompradas(adeCodigo, StatusCompraEnum.CANCELADO, responsavel);

                            /*
                             * Se o valor da nova autorização é maior do que a soma do valor das compradas e
                             * o incide margem da nova autorização ainda é zero então libera o valor da diferença.
                             * Se o valor da nova é menor e o incide margem não é mais zero, ou seja a compra
                             * havia sido concluida porém não confirmada, então libera também a diferença pois
                             * no alteração da situação de 1 para 7 do novo este valor não terá sido liberado.
                             * OBS: Passa false para validação de margem, pois estamos liberando margem e não prendendo.
                             * Nunca deve verificar o Status do Servidor no cancelamento de compra.
                             */
                            final BigDecimal diff = adeBean.getAdeVlr().subtract(valorAnterior);
                            if ((diff.signum() == 1) &&
                                    adeBean.getAdeIncMargem().equals(CodedValues.INCIDE_MARGEM_NAO)) {
                                atualizaMargem(adeBean.getRegistroServidor().getRseCodigo(), incideMargemReserva, diff.negate(), false, true, false, ocaCodigo, csaCodigo, svcCodigo, null, responsavel);
                            } else if ((diff.signum() == -1) &&
                                    !adeBean.getAdeIncMargem().equals(CodedValues.INCIDE_MARGEM_NAO)) {
                                atualizaMargem(adeBean.getRegistroServidor().getRseCodigo(), incideMargemReserva, diff, false, true, false, ocaCodigo, csaCodigo, svcCodigo, null, responsavel);
                            }

                            // Chama o método que processa o desbloqueio automático para as consignatárias envolvidas na compra.
                            final List<String> adesEnvolvidasCompra = new ArrayList<>(adeUtilizadas.keySet());
                            adesEnvolvidasCompra.add(adeCodigo);
                            compraContratoController.executarDesbloqueioAutomaticoConsignatarias(adesEnvolvidasCompra, responsavel);
                        }
                    }
                }

                // Controle de compulsórios
                final boolean temControleCompulsorios = ParamSist.paramEquals(CodedValues.TPC_TEM_CONTROLE_DE_ESTOQUE, CodedValues.TPC_SIM, responsavel) &&
                        ParamSist.paramEquals(CodedValues.TPC_TEM_CONTROLE_DE_COMPULSORIOS, CodedValues.TPC_SIM, responsavel);
                /*
                 * Se tem controle de compulsórios, verifica se o contrato é um compulsório, e
                 * caso afirmativo, verifica se é necessário retornar o status de algum contrato
                 */
                if (temControleCompulsorios &&
                        (CodedValues.SAD_AGUARD_CONF.equals(sadCodigo) ||
                                CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo)||
                                CodedValues.SAD_DEFERIDA.equals(sadCodigo))) {
                    // Verifica se tem algum relacionamento de controle de compulsórios
                    final List<RelacionamentoAutorizacao> ades = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_CONTROLE_COMPULSORIOS);
                    if ((ades != null) && (ades.size() > 0)) {
                        /*
                         * Se tem algum relacionamento para controle de compulsórios,
                         * verifica se o contrato cancelado ainda nao foi para a folha
                         */
                        final ObtemTotalParcelasPeriodoQuery query1 = new ObtemTotalParcelasPeriodoQuery();
                        query1.adeCodigo = adeCodigo;
                        int total = query1.executarContador();
                        if (total == 0) {
                            // Se não tem parcelas do periodo, verifica a tabela histórica
                            final ObtemTotalParcelasQuery query2 = new ObtemTotalParcelasQuery();
                            query2.adeCodigo = adeCodigo;
                            total = query2.executarContador();
                        }
                        if (total == 0) {
                            // Se o contrato nao foi para a folha, então retorna os contratos originais
                            final Iterator<RelacionamentoAutorizacao> it = ades.iterator();
                            RelacionamentoAutorizacao relBean = null;
                            while (it.hasNext()) {
                                // Retorna o valor da ade e remove o relacionamento
                                relBean = it.next();
                                retirarDoEstoqueCancComp(relBean.getAutDescontoByAdeCodigoDestino().getAdeCodigo(), adeCodigo, responsavel);
                                AbstractEntityHome.remove(relBean);
                            }
                        }
                    }
                }

                // Cancela os contratos de correção de saldo relacionados a este contrato
                final List<RelacionamentoAutorizacao> radCorrecaoSaldo = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_CORRECAO_SALDO);
                for (final RelacionamentoAutorizacao radCorrecaoBean : radCorrecaoSaldo) {
                    final String adeCorrecaoCodigo = radCorrecaoBean.getAutDescontoByAdeCodigoDestino().getAdeCodigo();
                    cancelar(adeCorrecaoCodigo, verificaStatusAde, verificaStatusServidor, responsavel);
                }

                // Cancela contratos de insere/altera à espera de confirmação ligados a este.
                final List<RelacionamentoAutorizacao> lstRadInsereAltera = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_CONTRATO_GERADO_INSERE_ALTERA);
                for (final Object rad: lstRadInsereAltera) {
                    final RelacionamentoAutorizacao radInsereAltera = (RelacionamentoAutorizacao) rad;
                    final String adeCodigoDestInsAlt = radInsereAltera.getAdeCodigoDestino();
                    final AutDesconto adeDestInsAlt = AutDescontoHome.findByPrimaryKey(adeCodigoDestInsAlt);
                    final String sadCodigoDest = adeDestInsAlt.getStatusAutorizacaoDesconto().getSadCodigo().trim();

                    if (!CodedValues.SAD_CANCELADA.equals(sadCodigoDest) && !CodedValues.SAD_LIQUIDADA.equals(sadCodigoDest) &&
                        !CodedValues.SAD_CONCLUIDO.equals(sadCodigoDest) && !CodedValues.SAD_ENCERRADO.equals(sadCodigoDest) && !CodedValues.SAD_INDEFERIDA.equals(sadCodigoDest)) {
                        cancelar(adeCodigoDestInsAlt, verificaStatusAde, verificaStatusServidor, responsavel);
                    }
                }

                // Cria ocorrência específica de cancelamento automático
                if (verificaStatusAdeCancAutomatico) {
                    final String sadDescricao = StatusAutorizacaoDesconto.getInstance().getDescricao(sadCodigo).toString().toUpperCase();
                    criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AVISO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.cancelamento.automatico.situacao", responsavel, sadDescricao), responsavel);
                }

                // Gera o Log de auditoria
                final LogDelegate logDelegate = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.CANCELAR_CONSIGNACAO, Log.LOG_INFORMACAO);
                logDelegate.setAutorizacaoDesconto(adeCodigo);
                logDelegate.write();

                // Finaliza o processo de cancelamento.
                finalizarCancelamentoConsignacao(adeCodigo, svcCodigo, responsavel);

                if (tipoMotivoOperacao != null) {
                    if (ocaCodigo != null) {
                        tipoMotivoOperacao.setAttribute(Columns.OCA_CODIGO, ocaCodigo);
                    }
                    tipoMotivoOperacaoController.gravarMotivoOperacaoConsignacao(tipoMotivoOperacao, responsavel);
                }

                // DESENV-8933: se responsável pela operação tiver seu papel configurado para disparar e-mail de alerta de alteração na ade
                // para entidades relacionadas a este, também configuradas na tabela tb_destinatario_email
                try {
                    OperacaoEConsigEnum opEnum = null;

                    if (CodedValues.SAD_SOLICITADO.equals(sadCodigo) || CodedValues.SAD_AGUARD_CONF.equals(sadCodigo) ||
                            CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo)) {
                        opEnum = OperacaoEConsigEnum.CANCELAR_RESERVA;
                    } else {
                        opEnum = OperacaoEConsigEnum.CANCELAR_CONSIGNACAO;
                    }

                    final ProcessaEnvioEmailEntidadesAltAde processoEmail = new ProcessaEnvioEmailEntidadesAltAde(opEnum, adeCodigo, null, tipoMotivoOperacao, responsavel);
                    processoEmail.start();
                } catch (final Exception ex) {
                    // exceção no envio de email não faz rollback na operação
                    LOG.error(ex.getMessage(), ex);
                }

                //DESENV-10999: para solicitação canceladas pelo próprio servidor, envia e-mail (se esta estiver definida) de alerta à consignatária para a qual foi feita a solicitação.
                if (!TextHelper.isNull(emailCsaSerCancelouSolicitacao)) {
                    try {
                        final ConvenioTransferObject cnvTo = new ConvenioTransferObject();
                        cnvTo.setCsaCodigo(csaCodigo);
                        cnvTo.setOrgCodigo(convenio.getOrgao().getOrgCodigo());
                        cnvTo.setSvcCodigo(svcCodigo);
                        EnviaEmailHelper.enviarEmailCsaSolicitacaoCanceladaPorSer(emailCsaSerCancelouSolicitacao, adeBean.getRegistroServidor().getRseCodigo(), adeCodigo, rseMargemRest, adeBean.getAdeIncMargem(), cnvTo, responsavel);
                    } catch (final Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            } catch (final Exception ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                LOG.error(ex.getMessage(), ex);
                if (ex.getClass().equals(AutorizacaoControllerException.class) ||
                        ex.getClass().equals(GerenciadorAutorizacaoException.class) ||
                        ex.getClass().equals(LeilaoSolicitacaoControllerException.class)) {
                    throw new AutorizacaoControllerException(ex);
                } else {
                    throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                }
            }
        }
    }

    /**
     * Realiza a finalização do processo de cancelamento de consignação.
     * @param adeCodigo Contrato sendo cancelado.
     * @param svcCodigo Código do serviço da consignação.
     * @param responsavel
     * @throws GerenciadorAutorizacaoException
     */
    private void finalizarCancelamentoConsignacao(String adeCodigo, String svcCodigo, AcessoSistema responsavel) throws GerenciadorAutorizacaoException {
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
                gerenciadorAutorizacaoServico.finalizarCancelamentoConsignacao(adeCodigo);
                LOG.debug("TOTAL FINALIZACAO CANCELAMENTO CONSIGNACAO (" + responsavel.getUsuCodigo() + ") = " + (Calendar.getInstance().getTimeInMillis() - horaInicioFinalizacao) + " ms");
            }
        } catch (final AutorizacaoControllerException ex) {
            throw new GerenciadorAutorizacaoException("mensagem.erro.finalizando.cancelamento.consignacao", responsavel, ex);
        }
    }

    private void verificaSituacaoPRD(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final List<ParcelaDesconto> colParcelaDesconto = ParcelaDescontoHome.findByAutDesconto(adeCodigo);

            if (colParcelaDesconto.size() > 0) {
                final Iterator<ParcelaDesconto> it = colParcelaDesconto.iterator();
                ParcelaDesconto prd = null;
                String spdCodigo = null;
                while (it.hasNext()) {
                    prd = it.next();

                    spdCodigo = prd.getStatusParcelaDesconto().getSpdCodigo();
                    if (CodedValues.SPD_EMPROCESSAMENTO.equals(spdCodigo) ||
                            CodedValues.SPD_SEM_RETORNO.equals(spdCodigo) ||
                            CodedValues.SPD_REJEITADAFOLHA.equals(spdCodigo) ||
                            CodedValues.SPD_LIQUIDADAFOLHA.equals(spdCodigo) ||
                            CodedValues.SPD_LIQUIDADAMANUAL.equals(spdCodigo)) {
                        throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.cancelar.consignacao.existe.parcela.processada.ou.andamento.se.necessario.use.liquidar", responsavel);
                    }
                }
            }

            final List<ParcelaDescontoPeriodo> colParcelaDescontoPeriodo = ParcelaDescontoPeriodoHome.findByAutDesconto(adeCodigo);

            if (colParcelaDescontoPeriodo.size() > 0) {
                final Iterator<ParcelaDescontoPeriodo> it = colParcelaDescontoPeriodo.iterator();
                ParcelaDescontoPeriodo pdp = null;
                String spdCodigo = null;
                while (it.hasNext()) {
                    pdp = it.next();

                    spdCodigo = pdp.getStatusParcelaDesconto().getSpdCodigo();
                    if (CodedValues.SPD_EMPROCESSAMENTO.equals(spdCodigo) ||
                            CodedValues.SPD_SEM_RETORNO.equals(spdCodigo) ||
                            CodedValues.SPD_REJEITADAFOLHA.equals(spdCodigo) ||
                            CodedValues.SPD_LIQUIDADAFOLHA.equals(spdCodigo) ||
                            CodedValues.SPD_LIQUIDADAMANUAL.equals(spdCodigo)) {
                        throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.cancelar.consignacao.existe.parcela.processada.ou.andamento.se.necessario.use.liquidar", responsavel);
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) ex;
            } else {
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    /**
     * Volta o status de um contrato que foi colocado em estoque para
     * inclusão de um compulsório.
     * @param adeCodigo
     * @param adeCodigoCompulsorio
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    private void retirarDoEstoqueCancComp(String adeCodigo, String adeCodigoCompulsorio, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final AutDesconto autdes = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);

            final String sadCodigoNovo = ((autdes.getAdePrdPagas() != null) && (autdes.getAdePrdPagas().intValue() > 0)) ? CodedValues.SAD_EMANDAMENTO : CodedValues.SAD_DEFERIDA;
            final String sadCodigoAtual = autdes.getStatusAutorizacaoDesconto().getSadCodigo();

            if (CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigoAtual)) {
                // Altera o status
                autdes.setStatusAutorizacaoDesconto(StatusAutorizacaoDescontoHome.findByPrimaryKey(sadCodigoNovo));
                AbstractEntityHome.update(autdes);

                // Grava ocorrência de alteração de status
                criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status", responsavel, sadCodigoAtual, sadCodigoNovo), responsavel);
                criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.informacao.contrato.compulsorio.cancelado", responsavel).toUpperCase(), responsavel);

                if (CodedValues.SAD_EMANDAMENTO.equals(sadCodigoNovo)) {
                    // Se o contrato estava em andamento, remove a ocorrência de liquidação inserida anteriormente
                    final List<OcorrenciaAutorizacao> ocorrencias = OcorrenciaAutorizacaoHome.findByAdeTocCodigo(adeCodigo, CodedValues.TOC_TARIF_LIQUIDACAO);
                    if ((ocorrencias != null) && (ocorrencias.size() > 0)) {
                        final Iterator<OcorrenciaAutorizacao> it = ocorrencias.iterator();
                        OcorrenciaAutorizacao ocaBean = null;
                        while (it.hasNext()) {
                            ocaBean = it.next();
                            AbstractEntityHome.remove(ocaBean);
                        }
                    }
                }

                // Gera o Log de auditoria
                final LogDelegate logDelegate = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.REATIVAR_CONSIGNACAO, Log.LOG_INFORMACAO);
                logDelegate.setAutorizacaoDesconto(adeCodigo);
                logDelegate.write();
            }
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            if (ex instanceof AutorizacaoControllerException) {
                throw (AutorizacaoControllerException) ex;
            } else {
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    private void cancelarLeilaoSolicitacao(String adeCodigo, String rseCodigo, AcessoSistema responsavel) throws LeilaoSolicitacaoControllerException {
        try {
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {

                final boolean isOrigemLeilao = leilaoSolicitacaoController.temSolicitacaoLeilao(adeCodigo, true, responsavel);
                if (isOrigemLeilao) {
                    if (responsavel.isSistema()) {
                        // Só o usuário do sistema pode cancelar o leilão (caso o cancelamento
                        // automático de solicitação esteja com tempo menor que o tempo do leilão,
                        // que provavelmente é erro de configuração
                        leilaoSolicitacaoController.cancelarProcessoLeilao(adeCodigo, responsavel);
                    } else {
                        // A solicitação origem de leilão não pode ser cancelada
                        throw new LeilaoSolicitacaoControllerException("mensagem.erro.solicitacao.nao.pode.ser.cancelada.leilao.pendente", responsavel);
                    }

                } else {
                    // O destino do leilão pode ser cancelado. Caso cancelado automaticamente pelo sistema ou pelo servidor
                    // este será negativado em sua pontuação
                    final boolean isDestinoLeilao = pesquisarConsignacaoController.isDestinoRelacionamento(adeCodigo, CodedValues.TNT_LEILAO_SOLICITACAO);
                    final boolean permiteSerCancelarLeilao = ParamSist.paramEquals(CodedValues.TPC_PERMITE_SERVIDOR_CANCELAR_SOLICITACAO_LEILAO, CodedValues.TPC_SIM, responsavel);
                    if (isDestinoLeilao) {
                        if (!responsavel.isSistema() && !responsavel.isCse() && (responsavel.isSer() && !permiteSerCancelarLeilao)) {
                            throw new LeilaoSolicitacaoControllerException("mensagem.erro.solicitacao.nao.pode.ser.cancelada.fruto.leilao", responsavel);
                        }

                        // Insere ocorrência de leilão cancelado para ser negativado
                        criaOcorrenciaADE(adeCodigo, CodedValues.TOC_CANCELAMENTO_LEILAO_COM_PROPOSTA, ApplicationResourcesHelper.getMessage("mensagem.informacao.ptf.cancelamento.consignacao", responsavel), responsavel);

                        // Verifica se o servidor pode iniciar novos leilões, ou seja, não possui bloqueio
                        // pelo fato de ter cancelado outros leilões anteriores
                        final Object paramQtdDiasBloqSer = ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_BLOQ_SERVIDOR_COM_LEILAO_CANCELADO, responsavel);
                        final Object paramQtdCancelamentosBloqSer = ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_LEILOES_CANCELADOS_PARA_BLOQUEIO_SER, responsavel);
                        if (!TextHelper.isNull(paramQtdDiasBloqSer) && TextHelper.isNum(paramQtdDiasBloqSer)) {
                            final int qtdDiasBloqSer = Integer.parseInt(paramQtdDiasBloqSer.toString());
                            if (qtdDiasBloqSer > 0) {
                                // Realiza pesquisa de leilões cancelados
                                final ListaSolicitacaoLeilaoCanceladoParaBloqueioQuery query = new ListaSolicitacaoLeilaoCanceladoParaBloqueioQuery();
                                query.rseCodigo = rseCodigo;
                                int qtdCancelamentos = query.executarContador();

                                // Contabiliza o leilão atual, visto que ainda não foi executada a rotina de cancelamento
                                qtdCancelamentos++;

                                if (qtdCancelamentos >= Integer.parseInt(paramQtdCancelamentosBloqSer.toString())) {
                                    // Inclui bloqueio da função
                                    final BloqueioRseFunId id = new BloqueioRseFunId(rseCodigo, CodedValues.FUN_SOLICITAR_LEILAO_REVERSO);
                                    try {
                                        // Se o bloqueio existir, deve atualiza a data para a data atual
                                        final BloqueioRseFun bloqueio = BloqueioRseFunHome.findByPrimaryKey(id);
                                        bloqueio.setBrsDataLimite(DateHelper.addDays(Calendar.getInstance().getTime(), qtdDiasBloqSer));
                                        AbstractEntityHome.update(bloqueio);

                                    } catch (final FindException ex) {
                                        // Bloqueio não existe, então cria um novo
                                        BloqueioRseFunHome.create(rseCodigo, CodedValues.FUN_SOLICITAR_LEILAO_REVERSO, DateHelper.addDays(Calendar.getInstance().getTime(), qtdDiasBloqSer));
                                    }
                                }
                            }
                        }

                    }
                }
            }
        } catch (final AutorizacaoControllerException ex) {
            throw new LeilaoSolicitacaoControllerException(ex);
        } catch (NumberFormatException | HQueryException | UpdateException | CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new LeilaoSolicitacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void cancelarExpiradas(List<String> sadCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        cancelarExpiradas(null, null, sadCodigos, responsavel);
    }

    /**
     * Executa o cancelamento automático de consignações expiradas. Podem ser solicitações,
     * pré-reservas e negociações de compra. Faz uma busca na base das consignações a serem
     * canceladas e executa o cancelamento um a um.
     * @param rseCodigo
     * @param adeNumero
     * @param sadCodigos
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    @Override
    public void cancelarExpiradas(String rseCodigo, String adeNumero, List<String> sadCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // Se não tem simulação de consignação, não efetua cancelamento automáticos para Solicitações
            final boolean temSimulacao = ParamSist.getBoolParamSist(CodedValues.TPC_SIMULACAO_CONSIGNACAO, responsavel);
            if (!temSimulacao && (sadCodigos != null)) {
                // Remove o status de solicitações
                sadCodigos.remove(CodedValues.SAD_SOLICITADO);
            }

            if ((sadCodigos != null) && (sadCodigos.size() > 0)) {
                // Busca a consignatária do usuário, caso seja usuário de CSA/COR
                // para não cancelar solicitações da consignatária do usuário
                String csaCodigo = null;

                // Busca o código da consignatária do usuário, caso o mesmo seja de CSA ou COR
                if ((responsavel != null) && responsavel.isCsaCor()) {
                    final ObtemConsignatariaUsuarioQuery csaUsuQuery = new ObtemConsignatariaUsuarioQuery();
                    csaUsuQuery.usuCodigo = responsavel.getUsuCodigo();
                    final List<String> csaUsuList = csaUsuQuery.executarLista();
                    if ((csaUsuList != null) && !csaUsuList.isEmpty()) {
                        csaCodigo = csaUsuList.get(0);
                    }
                }

                // Se foi informado o adeNumero e não o rseCodigo, busca o rseCodigo
                // ligado a consignação que possua o adeNumero informado
                if (TextHelper.isNull(rseCodigo) && !TextHelper.isNull(adeNumero)) {
                    try {
                        final Long numero = Long.valueOf(adeNumero);
                        final AutDesconto adeBean = AutDescontoHome.findByAdeNumero(numero);
                        rseCodigo = adeBean.getRegistroServidor().getRseCodigo();
                    } catch (final FindException | NumberFormatException ex) {
                        // Se o adeNumero informado não for um número inteiro, então a pesquisa
                        // não irá retornar nada, de modo que não é necessário um cancelamento
                        // automático de contratos.
                        return;
                    }
                }

                // Busca os contratos para cancelamento automático
                final ListaConsignacaoCancAutomaticoQuery lstConsigCancQuery = new ListaConsignacaoCancAutomaticoQuery();
                lstConsigCancQuery.rseCodigo = rseCodigo;
                lstConsigCancQuery.csaCodigo = csaCodigo;
                lstConsigCancQuery.sadCodigos = sadCodigos;
                final List<String> adeCodigos = lstConsigCancQuery.executarLista();

                final boolean temLeilao = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, responsavel);
                final String qtdDiasConcretizarLeilao = (String) ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_PARA_SER_CONCRETIZAR_LEILAO, AcessoSistema.getAcessoUsuarioSistema());
                if (temLeilao && !TextHelper.isNull(qtdDiasConcretizarLeilao) && (Integer.parseInt(qtdDiasConcretizarLeilao) > 0)) {
                    // Busca contratos de leilão para cancelamento automático
                    final ListaLeilaoCancAutomaticoQuery lstLeilaoCancQuery = new ListaLeilaoCancAutomaticoQuery();
                    lstLeilaoCancQuery.rseCodigo = rseCodigo;
                    lstLeilaoCancQuery.csaCodigo = csaCodigo;
                    final List<String> adeCodigosLeilao = lstLeilaoCancQuery.executarLista();

                    // Adiciona consignações derivadas de leilão para cancelamento
                    adeCodigos.addAll(adeCodigosLeilao);
                }

                // Não grava a ocorrência ligada ao usuário que ocasionou o cancelamento automático,
                // pois deve ficar registrado que foi o sistema que executou o cancelamento
                responsavel = AcessoSistema.getAcessoUsuarioSistema();

                for (final String adeCodigo : adeCodigos) {
                    /**
                     * Executa rotina de cancelamento, verificando status do contrato e
                     * verificando status do contrato vindo de cancelamento automático,
                     * mas não verificando status do servidor
                     */
                    cancelar(adeCodigo, true, false, true, null, responsavel);
                }

                // Grava log da operação de cancelamento automático
                final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setRegistroServidor(rseCodigo);
                log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.cancelamento.automatico.consignacoes", responsavel) + ":");
                log.add(ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel) + "=(" + adeNumero + ") ");
                log.add(Columns.SAD_CODIGO, sadCodigos, StatusAutorizacaoDescontoHome.class);
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

    /**
     * Executa o cancelamento de solicitacoes quando o parametro de consignataria 83
     * estiver ativado e a consignataria pesquisar um rseCodigo nos casos de uso de
     * ConsultarMargem e ConsultarConsignacao. As solicitações são canceladas uma a uma
     * @param rseCodigo
     * @param sadCodigos
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    @Override
    public void cancelarExpiradasCsa(String rseCodigo, String adeNumero, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // Se não tem simulação de consignação, não haverá solicitações para cancelar
            final boolean temSimulacao = ParamSist.getBoolParamSist(CodedValues.TPC_SIMULACAO_CONSIGNACAO, responsavel);
            if (!temSimulacao) {
                return;
            }

            // Se foi informado o adeNumero e não o rseCodigo, busca o rseCodigo
            // ligado a consignação que possua o adeNumero informado
            if (TextHelper.isNull(rseCodigo) && !TextHelper.isNull(adeNumero)) {
                try {
                    final Long numero = Long.valueOf(adeNumero);
                    final AutDesconto adeBean = AutDescontoHome.findByAdeNumero(numero);
                    rseCodigo = adeBean.getRegistroServidor().getRseCodigo();
                } catch (final FindException | NumberFormatException ex) {
                    // Se o adeNumero informado não for um número inteiro, então a pesquisa
                    // não irá retornar nada, de modo que não é necessário um cancelamento
                    // automático de contratos.
                    return;
                }
            }


            if(!TextHelper.isNull(rseCodigo)) {
                final String csaCodigo = responsavel.getCsaCodigo();

                // Busca os contratos para cancelamento automático
                final ListaConsignacaoCancelamentoParametroCsaQuery lstSolicitacaoCancCsaQuery = new ListaConsignacaoCancelamentoParametroCsaQuery();
                lstSolicitacaoCancCsaQuery.rseCodigo = rseCodigo;
                lstSolicitacaoCancCsaQuery.csaCodigo = csaCodigo;
                final List<String> adeCodigos = lstSolicitacaoCancCsaQuery.executarLista();

                for (final String adeCodigo : adeCodigos) {
                    /**
                     * Executa rotina de cancelamento, verificando status do contrato e
                     * verificando status do contrato vindo de cancelamento automático,
                     * mas não verificando status do servidor
                     */
                    cancelar(adeCodigo, true, false, true, null, responsavel);
                }

                // Grava log da operação de cancelamento automático
                // Não grava a ocorrência ligada ao usuário que ocasionou o cancelamento automático,
                // pois deve ficar registrado que foi o sistema que executou o cancelamento
                final LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setRegistroServidor(rseCodigo);
                log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.cancelamento.automatico.consignacoes.parametro.consignataria", AcessoSistema.getAcessoUsuarioSistema()) + ":");
                log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.cancelamento.automatico.consignacoes.parametro.consignataria.codigo", AcessoSistema.getAcessoUsuarioSistema()) + csaCodigo);
                log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.cancelamento.automatico.consignacoes.parametro.consignataria.registro.servidor.codigo", AcessoSistema.getAcessoUsuarioSistema()) + rseCodigo);
                log.add(Columns.SAD_CODIGO, Arrays.asList(CodedValues.SAD_SOLICITADO), StatusAutorizacaoDescontoHome.class);
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

    /**
     * Operação de cancelamento de renegociação. Consiste na união da operação "Cancelar Consignação" para o novo
     * contrato, e na "Desliquidar Consignação" para os contratos antigos.
     *
     * @param adeCodigo          Código do contrato a ser cancelado.
     * @param tipoMotivoOperacao Tipo de motivo da Operação.
     * @param responsável        Responsável pela operação.
     * @throws AutorizacaoControllerException Exceção padrão da classe.
     */
    @Override
    public void cancelarRenegociacao(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if (usuarioPodeModificarAde(adeCodigo, responsavel)) {
            try {
                final boolean tpcCancelaPosCorte = ParamSist.paramEquals(CodedValues.TPC_CANCELAMENTO_RENEGOCIACAO_APOS_DATA_CORTE, CodedValues.TPC_SIM, responsavel);

                final AutDesconto adeBean = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
                boolean isOk = verificaSadCodigo(adeBean, tpcCancelaPosCorte);

                // Verificar situacao do contrato
                if (isOk) {
                    final VerbaConvenio vcoBean = VerbaConvenioHome.findByPrimaryKey(adeBean.getVerbaConvenio().getVcoCodigo());
                    final Convenio cnvBean = ConvenioHome.findByPrimaryKey(vcoBean.getConvenio().getCnvCodigo());

                    final List<RelacionamentoAutorizacao> adeRenegociadas = RelacionamentoAutorizacaoHome.findByDestino(adeCodigo, CodedValues.TNT_CONTROLE_RENEGOCIACAO);
                    Iterator<RelacionamentoAutorizacao> itAdeRenegociadas = adeRenegociadas.iterator();
                    final Date hoje = DateHelper.getSystemDate();

                    // Impedir cancelamento de renegociação caso um dos contratos de origem tenha pagas igual ao prazo.
                    while (itAdeRenegociadas.hasNext()) {
                        final RelacionamentoAutorizacao rad = itAdeRenegociadas.next();
                        final AutDesconto ade = AutDescontoHome.findByPrimaryKey(rad.getAutDescontoByAdeCodigoOrigem().getAdeCodigo());
                        if (ade.getAdePrdPagas().equals(ade.getAdePrazo())) {
                            throw new AutorizacaoControllerException("mensagem.erro.renegociacao.nao.pode.ser.cancelada.contrato.origem.pagas.igual.prazo", responsavel, ade.getAdeNumero().toString());
                        }
                    }

                    itAdeRenegociadas = adeRenegociadas.iterator();

                    if (itAdeRenegociadas.hasNext()) {
                        Date dataLimite = hoje;
                        Date dataLimitePrz = hoje;
                        AutorizacaoControllerException msg = null;
                        RelacionamentoAutorizacao rad = itAdeRenegociadas.next();
                        final Date radData = DateHelper.clearHourTime(rad.getRadData());

                        // Pega as ultimas datas de exportação
                        final ObtemDatasUltimoPeriodoExportadoQuery queryPeriodoExportacao = new ObtemDatasUltimoPeriodoExportadoQuery();
                        queryPeriodoExportacao.orgCodigo = cnvBean.getOrgao().getOrgCodigo();

                        final List<TransferObject> periodoExportacaoList = queryPeriodoExportacao.executarDTO();
                        final Date dataFimExportacao = (periodoExportacaoList != null && !periodoExportacaoList.isEmpty()) ? (Date) periodoExportacaoList.get(0).getAttribute(Columns.HIE_DATA_FIM) : null;
                        if (dataFimExportacao != null && dataFimExportacao.compareTo(radData) >= 0) {
                            dataLimite = dataFimExportacao;
                            msg = new AutorizacaoControllerException("mensagem.erro.renegociacao.nao.pode.ser.cancelada.informacao.liquidacao.ja.foi.enviada.para.folha", responsavel);
                        }

                        //Parametro de csa 95 para verificar se conta somente dias uteis ou nao
                        boolean tpaDiasUteis = parametroController.getParamCsa(cnvBean.getConsignataria().getCsaCodigo(), CodedValues.TPA_PRAZO_CANCELAMENTO_RENEGOCIACAO_DIAS_UTEIS, responsavel) != null && parametroController.getParamCsa(cnvBean.getConsignataria().getCsaCodigo(), CodedValues.TPA_PRAZO_CANCELAMENTO_RENEGOCIACAO_DIAS_UTEIS, responsavel).equals(CodedValues.TPA_SIM);

                        // Parametro de servico TPS_PRAZO_DIAS_CANCELAMENTO_RENEGOCIACAO
                        final CustomTransferObject paramSvc = getParametroSvc(CodedValues.TPS_PRAZO_DIAS_CANCELAMENTO_RENEGOCIACAO, cnvBean.getServico().getSvcCodigo(), 0, false, null);
                        final int diasCancelReneg = (paramSvc != null && !TextHelper.isNull(paramSvc.getAttribute(Columns.PSE_VLR))) ? (Integer) paramSvc.getAttribute(Columns.PSE_VLR) : 0;
                        Calendario dataUtilLimite = new Calendario();
                        boolean validacao = false;
                        if (tpaDiasUteis && diasCancelReneg > 0) {
                            final List<Calendario> diasUteis = calendarioController.lstCalendariosAPartirDe(DateHelper.getSystemDate(), true, diasCancelReneg);
                            dataUtilLimite = diasUteis.get(diasUteis.size() - 1);
                            validacao = dataUtilLimite != null && dataUtilLimite.getCalData().compareTo(dataFimExportacao) < 0;
                        } else {
                            validacao = DateHelper.addDays(radData, diasCancelReneg).compareTo(dataFimExportacao) < 0;
                        }

                        // Se o prazo do parametro é anterior ao dia de corte
                        if (dataFimExportacao == null || (dataFimExportacao.compareTo(radData) < 0 || validacao)) {
                            dataLimitePrz = tpaDiasUteis && diasCancelReneg > 0 ? dataUtilLimite.getCalData() : DateHelper.addDays(radData, diasCancelReneg);
                            msg = new AutorizacaoControllerException("mensagem.erro.renegociacao.nao.pode.ser.cancelada.porque.ultrapassou.limite.arg0.dias.apos.renegociacao", responsavel, String.valueOf(diasCancelReneg));
                        }

                        // Verifica se passou da data limite para cancelar a renegociacao
                        if (hoje.compareTo(dataLimite) > 0 && !tpcCancelaPosCorte || hoje.compareTo(dataLimitePrz) > 0) {
                            throw msg;
                        }

                        // Tipo de motivo da operação
                        final String tmoCodigo = (tipoMotivoOperacao != null ? (String) tipoMotivoOperacao.getAttribute(Columns.TMO_CODIGO) : null);
                        final String ocaObs = ((tipoMotivoOperacao != null) && !TextHelper.isNull(tipoMotivoOperacao.getAttribute(Columns.OCA_OBS)) ? " " + (String) tipoMotivoOperacao.getAttribute(Columns.OCA_OBS) : "");

                        if (hoje.compareTo(dataLimite) > 0 && tpcCancelaPosCorte) {
                            // Liquida contrato apos envio para folha
                            Date periodoAtualDate = PeriodoHelper.getInstance().getPeriodoAtual(cnvBean.getOrgao().getOrgCodigo(), responsavel);
                            LiquidarConsignacaoParametros parametrosLiquidacao = new LiquidarConsignacaoParametros();
                            parametrosLiquidacao.setOcaPeriodo(periodoAtualDate);

                            liquidarConsignacaoController.liquidar(adeCodigo, tipoMotivoOperacao, parametrosLiquidacao, responsavel);
                        } else {
                            // Cancelar contrato deferido
                            cancelar(adeCodigo, responsavel);
                        }

                        // Desliquidar contratos liquidados
                        final List<String> adeCodigos = new ArrayList<>();
                        adeCodigos.add(rad.getAutDescontoByAdeCodigoOrigem().getAdeCodigo());
                        while (itAdeRenegociadas.hasNext()) {
                            rad = itAdeRenegociadas.next();
                            adeCodigos.add(rad.getAutDescontoByAdeCodigoOrigem().getAdeCodigo());
                        }

                        final List<TransferObject> tpsCsaPermiteCancelarRenegMantendoMargemNegativa = parametroController.selectParamSvcCsa(cnvBean.getSvcCodigo(), cnvBean.getCsaCodigo(), Arrays.asList(CodedValues.TPS_PERMITE_CANCELAR_RENEGOCIACAO_MESMO_A_MARGEM_FICANDO_NEGATIVA), false, responsavel);
                        boolean permiteCancelarRenegMantendoMargemNegativa = (tpsCsaPermiteCancelarRenegMantendoMargemNegativa != null) && !tpsCsaPermiteCancelarRenegMantendoMargemNegativa.isEmpty() && "1".equals(tpsCsaPermiteCancelarRenegMantendoMargemNegativa.get(0).getAttribute(Columns.PSC_VLR).toString());

                        //Sendo verdadeiro, precisamos saber se existe contrato com data maior do contrato há ser cancelado, se houver o cancelamento não pode ser feito para margem negativa
                        if (permiteCancelarRenegMantendoMargemNegativa) {
                            final List<TransferObject> contratos = pesquisarConsignacaoController.pesquisaAutorizacao(adeBean.getRseCodigo(), null, CodedValues.SAD_CODIGOS_ATIVOS, responsavel);
                            permiteCancelarRenegMantendoMargemNegativa = contratos.stream().noneMatch(contrato -> {
                                final Date dataContrato = (Date) contrato.getAttribute(Columns.ADE_DATA);
                                return dataContrato.compareTo(adeBean.getAdeData()) >= 0;
                            });
                        }

                        if (hoje.compareTo(dataLimite) > 0 && tpcCancelaPosCorte) {
                            for (String adeCod : adeCodigos) {
                                boolean adeJaEnviadaFolha = liquidarConsignacaoController.liquidacaoJaEnviadaParaFolha(adeCod, responsavel);
                                if (adeJaEnviadaFolha) {
                                    liquidarConsignacaoController.desliquidarPosCorte(adeCod, true, tipoMotivoOperacao, responsavel);
                                    // Cria ocorrência específica de cancelamento de renegociação
                                    criaOcorrenciaADE(adeCodigo, CodedValues.TOC_CANCELAMENTO_RENEGOCIACAO_APOS_DATA_CORTE, ApplicationResourcesHelper.getMessage("mensagem.informacao.cancelamento.renegociacao.pos.corte", responsavel) + ocaObs, tmoCodigo, responsavel);
                                }
                            }
                        } else {
                            liquidarConsignacaoController.desliquidarAoCancelarRenegociacao(adeCodigos, ApplicationResourcesHelper.getMessage("mensagem.informacao.cancelamento.renegociacao", responsavel) + ocaObs, tmoCodigo, permiteCancelarRenegMantendoMargemNegativa, responsavel);
                            // Cria ocorrência específica de cancelamento de renegociação
                            criaOcorrenciaADE(adeCodigo, CodedValues.TOC_CANCELAMENTO_RENEGOCIACAO, ApplicationResourcesHelper.getMessage("mensagem.informacao.cancelamento.renegociacao", responsavel) + ocaObs, tmoCodigo, responsavel);
                        }

                        // Verifica se os limites de contratos não são excedidos após o cancelamento
                        verificaLimiteAoCancelarRenegociacao(adeCodigo, responsavel);

                        // Gera o Log de auditoria
                        final LogDelegate logDelegate = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.CANCELAR_RENEGOCIACAO, Log.LOG_INFORMACAO);
                        logDelegate.setAutorizacaoDesconto(adeCodigo);
                        logDelegate.write();
                    }
                }

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
    }

    private static boolean verificaSadCodigo(AutDesconto adeBean, boolean tpcCancelaPosCorte) {
        String sadCodigo = adeBean.getStatusAutorizacaoDesconto().getSadCodigo().trim();

        boolean isOk;
        if (tpcCancelaPosCorte) {
            isOk = switch (sadCodigo) {
                case CodedValues.SAD_DEFERIDA, CodedValues.SAD_EMANDAMENTO -> true;
                default -> false;
            };
        } else {
            isOk = sadCodigo.equals(CodedValues.SAD_DEFERIDA);
        }
        return isOk;
    }


    /**
     * Método para descancelar um contrato
     * @param adeCodigo Contrato a ser descancelado.
     * @param tipoMotivoOperacao Motivo da operação.
     * @param responsavel Responsável pela operação.
     * @throws AutorizacaoControllerException
     */
    @Override
    public void descancelar(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final AutDesconto adeBean = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
            final String sadCodigo = adeBean.getStatusAutorizacaoDesconto().getSadCodigo().trim();

            if (!CodedValues.SAD_CANCELADA.equals(sadCodigo)) {
                throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.descancelada.situacao.atual.dela.nao.permite.esta.operacao", responsavel, adeBean.getStatusAutorizacaoDesconto().getSadDescricao());
            }

            //Bloquear desfazer o cancelamento de contratos de destino em processo de compra
            final List<RelacionamentoAutorizacao> relacionamentosCompra = RelacionamentoAutorizacaoHome.findByDestino(adeCodigo, CodedValues.TNT_CONTROLE_COMPRA);
            if (!relacionamentosCompra.isEmpty()) {
                final Iterator<RelacionamentoAutorizacao> it = relacionamentosCompra.iterator();
                RelacionamentoAutorizacao rel;
                while(it.hasNext()) {
                    rel = it.next();
                    if(!rel.getStatusCompra().getStcCodigo().equalsIgnoreCase(CodedValues.STC_FINALIZADO.toString())){
                        throw new AutorizacaoControllerException("mensagem.erro.descancelar.contrato.compra", responsavel);
                    }
                }
            }

            //Não deve permitir descancelar contratos que são fruto de renegociação cancelada
            final List<RelacionamentoAutorizacao> relacionamentosRenegociacao = RelacionamentoAutorizacaoHome.findByDestino(adeCodigo, CodedValues.TNT_CONTROLE_RENEGOCIACAO);
            if (!relacionamentosRenegociacao.isEmpty()) {
                final Iterator<RelacionamentoAutorizacao> it = relacionamentosRenegociacao.iterator();
                RelacionamentoAutorizacao rel;
                AutDesconto origem;
                while(it.hasNext()) {
                    rel = it.next();
                    origem = AutDescontoHome.findByPrimaryKey(rel.getAdeCodigoOrigem());
                    if(!origem.getStatusAutorizacaoDesconto().getSadCodigo().equalsIgnoreCase(CodedValues.SAD_LIQUIDADA.toString())){
                        throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.descancelar.contrato.pois.ele.vem.processo.renegociacao", responsavel);
                    }
                }
            }

            final VerbaConvenio vcoBean = VerbaConvenioHome.findByPrimaryKey(adeBean.getVerbaConvenio().getVcoCodigo());
            final Convenio cnvBean = ConvenioHome.findByPrimaryKey(vcoBean.getConvenio().getCnvCodigo());
            final String csaCodigo = cnvBean.getConsignataria().getCsaCodigo();
            final String svcCodigo = cnvBean.getServico().getSvcCodigo();

            // Pega as ultimas datas de exportação
            final ObtemDatasUltimoPeriodoExportadoQuery queryPeriodoExportacao = new ObtemDatasUltimoPeriodoExportadoQuery();
            queryPeriodoExportacao.orgCodigo = cnvBean.getOrgao().getOrgCodigo();
            final List<TransferObject> periodoExportacaoList = queryPeriodoExportacao.executarDTO();
            final Date dataFimExportacao = ((periodoExportacaoList != null) && (periodoExportacaoList.size() > 0)) ? (Date) periodoExportacaoList.get(0).getAttribute(Columns.HIE_DATA_FIM) : null;

            // Pega as autorizações deste servidor para verificar se o servidor já utilizou a margem
            final List<String> sadCodigos = new ArrayList<>();
            sadCodigos.add(CodedValues.SAD_SOLICITADO);
            sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
            sadCodigos.add(CodedValues.SAD_AGUARD_DEFER);
            sadCodigos.add(CodedValues.SAD_DEFERIDA);
            sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
            sadCodigos.add(CodedValues.SAD_SUSPENSA);
            sadCodigos.add(CodedValues.SAD_SUSPENSA_CSE);

            final boolean validaMargem = true;

            // Remove ocorrencia de tarifação de cancelamento
            final List<OcorrenciaAutorizacao> ocorrencias = OcorrenciaAutorizacaoHome.findByAdeTocCodigo(adeCodigo, CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO);
            final Iterator<OcorrenciaAutorizacao> it = ocorrencias.iterator();
            OcorrenciaAutorizacao ocaBean = null;
            while (it.hasNext()) {
                ocaBean = it.next();

                // Se a liquidação já foi enviada para a folha então gera uma exceção
                if ((dataFimExportacao != null) && (dataFimExportacao.compareTo(ocaBean.getOcaData()) >= 0)) {
                    throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.descancelada.porque.informacao.cancelamento.ja.enviada.para.folha", responsavel);
                }

                // Remove a ocorrencia
                AbstractEntityHome.remove(ocaBean);
            }

            // Verifica a quantidade de parcelas pagas do contrato
            final ObtemTotalParcelasPagasQuery query = new ObtemTotalParcelasPagasQuery();
            query.adeCodigo = adeCodigo;
            final int adePrdPagas = query.executarContador();

            // Modifica status da consignação, seta o número de parcelas pagas e insere a ocorrencia de alteração de status
            final String sadNovo = (adePrdPagas > 0) ? CodedValues.SAD_EMANDAMENTO : CodedValues.SAD_DEFERIDA;
            adeBean.setAdePrdPagas(adePrdPagas);
            final String ocaCodigo = modificaSituacaoADE(adeBean, sadNovo, responsavel);

            if ((ocaCodigo != null) && (tipoMotivoOperacao != null)) {
                // grava motivo da operacao
                tipoMotivoOperacao.setAttribute(Columns.OCA_CODIGO, ocaCodigo);
                tipoMotivoOperacaoController.gravarMotivoOperacaoConsignacao(tipoMotivoOperacao, responsavel);
            }

            // Altera a margem do servidor
            try {
                final Short adeIncMargem = (adeBean.getAdeIncMargem() != null) ? adeBean.getAdeIncMargem() : CodedValues.INCIDE_MARGEM_SIM;
                BigDecimal adeVlr = adeBean.getAdeVlr();
                if (CodedValues.TIPO_VLR_PERCENTUAL.equals(adeBean.getAdeTipoVlr()) && (adeBean.getAdeVlrFolha() != null)) {
                    adeVlr = adeBean.getAdeVlrFolha();
                }
                atualizaMargem(adeBean.getRegistroServidor().getRseCodigo(), adeIncMargem, adeVlr, validaMargem, true, true, ocaCodigo, csaCodigo, svcCodigo, null, responsavel);
            } catch (final AutorizacaoControllerException ex) {
                if ((ex != null) && (ex.getMessageKey() != null) && ("mensagem.servidorBloqueado".equals(ex.getMessageKey()) || "mensagem.servidorExcluido".equals(ex.getMessageKey()))) {
                    throw ex;
                }
                throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.descancelada.porque.margem.liberada.pelo.cancelamento.ja.foi.utilizada", responsavel, ex);
            }

            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.DESCANCELAR_CONSIGNACAO, Log.LOG_INFORMACAO);
            logDelegate.setAutorizacaoDesconto(adeCodigo);
            logDelegate.write();

        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) ex;
            } else {
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }
}
