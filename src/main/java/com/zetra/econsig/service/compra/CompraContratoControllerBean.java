package com.zetra.econsig.service.compra;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CompraContratoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.AutorizacaoHelper;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.BloqueioRseFun;
import com.zetra.econsig.persistence.entity.BloqueioRseFunHome;
import com.zetra.econsig.persistence.entity.BloqueioRseFunId;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.ConsignatariaHome;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacao;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacaoHome;
import com.zetra.econsig.persistence.entity.OrgaoHome;
import com.zetra.econsig.persistence.entity.ParamSvcConsignante;
import com.zetra.econsig.persistence.entity.ParamSvcConsignanteHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacao;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.StatusAutorizacaoDescontoHome;
import com.zetra.econsig.persistence.entity.StatusCompra;
import com.zetra.econsig.persistence.entity.StatusCompraHome;
import com.zetra.econsig.persistence.entity.VerbaConvenio;
import com.zetra.econsig.persistence.entity.VerbaConvenioHome;
import com.zetra.econsig.persistence.query.compra.ListaCompraPassivelCancelamentoQuery;
import com.zetra.econsig.persistence.query.compra.ListaCompraPassivelFinalizacaoQuery;
import com.zetra.econsig.persistence.query.compra.ListaComprasParaConclusaoQuery;
import com.zetra.econsig.persistence.query.compra.ListaConsignacaoAprovacaoAutomaticaSaldoQuery;
import com.zetra.econsig.persistence.query.compra.ListaConsignacaoCancelamentoAprovacaoSaldoQuery;
import com.zetra.econsig.persistence.query.compra.ListaConsignacaoCancelamentoInfPgtSaldoQuery;
import com.zetra.econsig.persistence.query.compra.ListaConsignacaoCancelamentoInfSaldoQuery;
import com.zetra.econsig.persistence.query.compra.ListaConsignacaoCancelamentoLiquidacaoQuery;
import com.zetra.econsig.persistence.query.compra.ListaConsignacaoLiquidacaoAutomaticaQuery;
import com.zetra.econsig.persistence.query.compra.ListaCsaBloqueioInfPgtSaldoQuery;
import com.zetra.econsig.persistence.query.compra.ListaCsaBloqueioInfSaldoQuery;
import com.zetra.econsig.persistence.query.compra.ListaCsaBloqueioLiquidacaoQuery;
import com.zetra.econsig.persistence.query.compra.ListaCsaBloqueioRejPgtSaldoDestinoQuery;
import com.zetra.econsig.persistence.query.compra.ListaCsaBloqueioRejPgtSaldoOrigemQuery;
import com.zetra.econsig.persistence.query.compra.ListaCsaCarenciaBloqueioInfPgtSaldoQuery;
import com.zetra.econsig.persistence.query.compra.ListaCsaCarenciaBloqueioInfSaldoQuery;
import com.zetra.econsig.persistence.query.compra.ListaCsaCarenciaBloqueioLiquidacaoQuery;
import com.zetra.econsig.persistence.query.compra.ListaCsaComOcorrenciaPendenciaCompraQuery;
import com.zetra.econsig.persistence.query.compra.ObtemEmailDestinatarioMensagemCompraQuery;
import com.zetra.econsig.persistence.query.compra.ObtemRseParaBloqueioCompraQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoRseNseQuery;
import com.zetra.econsig.persistence.query.convenio.ObtemConsignatariasPorAdeCodigoQuery;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.AutorizacaoControllerBean;
import com.zetra.econsig.service.consignacao.CancelarConsignacaoController;
import com.zetra.econsig.service.consignacao.DeferirConsignacaoController;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoController;
import com.zetra.econsig.service.consignacao.LiquidarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoCompraEnum;
import com.zetra.econsig.values.StatusCompraEnum;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: CompraContratoControllerBean</p>
 * <p>Description: Session Bean para a operações relacionadas a compra de contratos.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class CompraContratoControllerBean extends AutorizacaoControllerBean implements CompraContratoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CompraContratoControllerBean.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private LiquidarConsignacaoController liquidarController;

    @Autowired
    @Qualifier("deferirConsignacaoController")
    private DeferirConsignacaoController deferirConsignacaoController;

    @Autowired
    private CancelarConsignacaoController cancelarConsignacaoController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private EditarAnexoConsignacaoController editarAnexoConsignacaoController;

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    /**
     * De acordo com os parâmetros de serviço, verifica quais consignatárias devem ser bloqueadas
     * devido ao atraso do cumprimento dos prazos do processo de compra, e verifica quais negociações
     * de compra devem ser canceladas.
     * @param responsavel
     * @throws CompraContratoControllerException
     */
    @Override
    public void aplicarPenalidadesPrazosExcedidos(AcessoSistema responsavel) throws CompraContratoControllerException {
        try {
            // Não grava a ocorrência ligada ao usuário que ocasionou o cancelamento automático,
            // pois deve ficar registrado que foi o sistema que executou o cancelamento
            responsavel = AcessoSistema.getAcessoUsuarioSistema();

            // 1) Bloqueia consignatárias que não informaram o saldo devedor de um de seus contratos vendidos
            ListaCsaBloqueioInfSaldoQuery query1 = new ListaCsaBloqueioInfSaldoQuery();
            List<TransferObject> adeBloqueioInformacaoSaldo = query1.executarDTO();

            // 2) Bloqueia consignatárias que não informaram o pagamento de saldo devedor de um de seus contratos comprados
            ListaCsaBloqueioInfPgtSaldoQuery query2 = new ListaCsaBloqueioInfPgtSaldoQuery();
            List<TransferObject> adeBloqueioInformacaoPagamentoSaldo = query2.executarDTO();

            // 3) Bloqueia consignatárias que não liquidaram os contratos vendidos
            ListaCsaBloqueioLiquidacaoQuery query3 = new ListaCsaBloqueioLiquidacaoQuery();
            List<TransferObject> adeBloqueioLiquidacao = query3.executarDTO();

            // 4) Executa os bloqueios após a execução das três querys para criar ocorrências dos três tipos para uma consignatária
            // caso a mesma esteja sendo penalizada pelos três motivos
            List<String> csaBloqueioInfSaldo = consignatariaController.bloquearConsignatariasContratos(adeBloqueioInformacaoSaldo, ApplicationResourcesHelper.getMessage("mensagem.bloqueio.informacao.saldo.devedor", responsavel), CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, responsavel);
            List<String> csaBloqueioInfPagamentoSaldo = consignatariaController.bloquearConsignatariasContratos(adeBloqueioInformacaoPagamentoSaldo, ApplicationResourcesHelper.getMessage("mensagem.bloqueio.informacao.pagamento.saldo.devedor", responsavel), CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, responsavel);
            List<String> csaBloqueioLiquidacao = consignatariaController.bloquearConsignatariasContratos(adeBloqueioLiquidacao, ApplicationResourcesHelper.getMessage("mensagem.bloqueio.liquidacao", responsavel), CodedValues.TOC_BLOQUEIA_CONSIGNATARIA, responsavel);

            // Filtra a lista de ADEs com os códigos de consignatárias retornados que realmente foram bloqueadas
            adeBloqueioInformacaoSaldo = adeBloqueioInformacaoSaldo.stream().filter(t -> csaBloqueioInfSaldo != null && csaBloqueioInfSaldo.contains(t.getAttribute(Columns.CSA_CODIGO))).collect(Collectors.toList());
            adeBloqueioInformacaoPagamentoSaldo = adeBloqueioInformacaoPagamentoSaldo.stream().filter(t -> csaBloqueioInfPagamentoSaldo != null && csaBloqueioInfPagamentoSaldo.contains(t.getAttribute(Columns.CSA_CODIGO))).collect(Collectors.toList());
            adeBloqueioLiquidacao = adeBloqueioLiquidacao.stream().filter(t -> csaBloqueioLiquidacao != null && csaBloqueioLiquidacao.contains(t.getAttribute(Columns.CSA_CODIGO))).collect(Collectors.toList());

            // 5) Envia e-mail para as consignatárias informando sobre o bloqueio.
            try {
                EnviaEmailHelper.enviarEmailBloqueioConsignatarias(adeBloqueioInformacaoSaldo, CodedValues.BLOQUEIO_INF_SALDO_DEVEDOR_COMPRA, responsavel);
                EnviaEmailHelper.enviarEmailBloqueioConsignatarias(adeBloqueioInformacaoPagamentoSaldo, CodedValues.BLOQUEIO_INF_PGT_SALDO_COMPRA, responsavel);
                EnviaEmailHelper.enviarEmailBloqueioConsignatarias(adeBloqueioLiquidacao, CodedValues.BLOQUEIO_LIQUIDACAO_COMPRA, responsavel);
            } catch (ViewHelperException e) {
                LOG.error("Erro ao enviar e-mail de bloqueio de consignatária.", e);
            }

            // 6) Se tem desbloqueio automático, então insere ocorrências para sinalização do bloqueio por pendência de compra
            if (ParamSist.paramEquals(CodedValues.TPC_DESBL_AUTOMATICO_CSA_CONTROLE_COMPRA, CodedValues.TPC_SIM, responsavel)) {
                // Unifica os códigos das consignatárias
                Set<String> csaCodigos = new HashSet<>();
                if (csaBloqueioInfSaldo != null && csaBloqueioInfSaldo.size() > 0) {
                    csaCodigos.addAll(csaBloqueioInfSaldo);
                }
                if (csaBloqueioInfPagamentoSaldo != null && csaBloqueioInfPagamentoSaldo.size() > 0) {
                    csaCodigos.addAll(csaBloqueioInfPagamentoSaldo);
                }
                if (csaBloqueioLiquidacao != null && csaBloqueioLiquidacao.size() > 0) {
                    csaCodigos.addAll(csaBloqueioLiquidacao);
                }

                // Inclui as ocorrências específicas
                if (csaCodigos.size() > 0) {
                    consignatariaController.incluirOcorrenciaConsignatarias(csaCodigos, CodedValues.TOC_CONSIGNATARIA_COM_PENDENCIAS_COMPRA, ApplicationResourcesHelper.getMessage("mensagem.bloqueio.pendencia.processo.compra", responsavel), responsavel);
                }
            }

            // 7) Pesquisa os processos de compra que devam ser cancelados
            // 8) Executa o cancelamento das negociações de compra
            ListaConsignacaoCancelamentoInfSaldoQuery query4 = new ListaConsignacaoCancelamentoInfSaldoQuery();
            List<String> adeCodigosInfSaldo = query4.executarLista();
            cancelarProcessosCompra(adeCodigosInfSaldo, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.cancelamento.automatico.nao.info.saldo", responsavel), responsavel);

            ListaConsignacaoCancelamentoInfPgtSaldoQuery query5 = new ListaConsignacaoCancelamentoInfPgtSaldoQuery();
            List<String> adeCodigosPgtSaldo = query5.executarLista();
            adeCodigosPgtSaldo.removeAll(adeCodigosInfSaldo);
            cancelarProcessosCompra(adeCodigosPgtSaldo, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.cancelamento.automatico.nao.info.pagamento.saldo", responsavel), responsavel);

            ListaConsignacaoCancelamentoLiquidacaoQuery query6 = new ListaConsignacaoCancelamentoLiquidacaoQuery();
            List<String> adeCodigosLiquidacao = query6.executarLista();
            adeCodigosLiquidacao.removeAll(adeCodigosInfSaldo);
            adeCodigosLiquidacao.removeAll(adeCodigosPgtSaldo);
            cancelarProcessosCompra(adeCodigosLiquidacao, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.cancelamento.automatico.nao.liquidacao.processo.compra", responsavel), responsavel);

            // 9) Executa o controle da etapa de aprovação de saldo pelo servidor
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavel)) {
                // 9.1) Cancela os processos de compra onde o prazo de aprovação já passou, e está configurado para cancelar
                ListaConsignacaoCancelamentoAprovacaoSaldoQuery query7 = new ListaConsignacaoCancelamentoAprovacaoSaldoQuery();
                List<String> adeCodigosAprSaldo = query7.executarLista();
                cancelarProcessosCompra(adeCodigosAprSaldo, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.cancelamento.automatico.nao.aprovacao.ser", responsavel), responsavel);

                // 9.2) Bloqueia os servidores para novas compras dos contratos cancelados por aprovação de saldo expirado
                bloqueiaRseNovaCompra(adeCodigosAprSaldo, responsavel);

                // 9.3) Aprova automaticamente o saldo onde o prazo de aprovação já passou, e está configurado para aprovar automaticamente
                ListaConsignacaoAprovacaoAutomaticaSaldoQuery query8 = new ListaConsignacaoAprovacaoAutomaticaSaldoQuery();
                List<String> adeCodigosAprAutSaldo = query8.executarLista();
                aprovaAutomaticamenteSaldoCompra(adeCodigosAprAutSaldo, responsavel);
            }

            if (ParamSist.paramEquals(CodedValues.TPC_SUSPENDE_DESC_CSA_BLOQ_CONTROLE_COMPRA, CodedValues.TPC_SIM, responsavel)) {
                // 10) Lista contratos com pendência de informação de saldo e liquidação de contratos
                // de consignatárias bloqueadas por motivo não temporário, para suspender os descontos em folha
                query1 = new ListaCsaBloqueioInfSaldoQuery();
                query1.csaComBloqManual = true;
                List<TransferObject> adeSuspensaoNaoInformacaoSaldo = query1.executarDTO();
                suspendeDescontosComPendenciaCompra(adeSuspensaoNaoInformacaoSaldo, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.suspensao.automatica.nao.info.saldo.compra", responsavel), responsavel);

                query3 = new ListaCsaBloqueioLiquidacaoQuery();
                query3.csaComBloqManual = true;
                List<TransferObject> adeSuspensaoNaoLiquidacao = query3.executarDTO();
                suspendeDescontosComPendenciaCompra(adeSuspensaoNaoLiquidacao, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.suspensao.automatica.nao.liquidacao.compra", responsavel), responsavel);
            }

        } catch (HQueryException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CompraContratoControllerException("mensagem.erro.listar.consignataria.bloqueio.compra.contrato", responsavel, ex);
        } catch (ConsignatariaControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CompraContratoControllerException(ex);
        } catch (AutorizacaoControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CompraContratoControllerException(ex);
        }
    }

    private void cancelarProcessosCompra(Collection<String> adeCodigos, String ocaObs, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if (adeCodigos != null && adeCodigos.size() > 0) {
            Iterator<String> it = adeCodigos.iterator();
            while (it.hasNext()) {
                String adeCodigo = it.next().toString();

                // Executa rotina de cancelamento, verificando status do contrato mas não verificando status do servidor
                cancelarConsignacaoController.cancelar(adeCodigo, true, false, responsavel);

                // Cria ocorrência específica de cancelamento automático
                criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AVISO, ocaObs, responsavel);
            }
        }
    }

    private void bloqueiaRseNovaCompra(List<String> adeCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_BLOQUEIO_FUNCAO_RSE, CodedValues.TPC_SIM, responsavel)) {
                if (adeCodigos != null && adeCodigos.size() > 0) {
                    ObtemRseParaBloqueioCompraQuery query = new ObtemRseParaBloqueioCompraQuery();
                    query.adeCodigos = adeCodigos;
                    List<TransferObject> bloqueios = query.executarDTO();
                    if (bloqueios != null && bloqueios.size() > 0) {
                        Iterator<TransferObject> it = bloqueios.iterator();
                        while (it.hasNext()) {
                            TransferObject bloqueioTO = it.next();
                            String rseCodigo = (String) bloqueioTO.getAttribute(Columns.BRS_RSE_CODIGO);
                            Date brsDataLimite = (Date) bloqueioTO.getAttribute(Columns.BRS_DATA_LIMITE);

                            BloqueioRseFunId id = new BloqueioRseFunId(rseCodigo, CodedValues.FUN_COMP_CONTRATO);
                            try {
                                // Se o bloqueio existir, deve atualiza a data para a data atual
                                BloqueioRseFun bloqueio = BloqueioRseFunHome.findByPrimaryKey(id);
                                bloqueio.setBrsDataLimite(brsDataLimite);
                                BloqueioRseFunHome.update(bloqueio);
                            } catch (FindException ex) {
                                // Bloqueio não existe, então cria um novo
                                BloqueioRseFunHome.create(rseCodigo, CodedValues.FUN_COMP_CONTRATO, brsDataLimite);
                            }
                        }
                    }
                }
            }
        } catch (com.zetra.econsig.exception.UpdateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (com.zetra.econsig.exception.CreateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (HQueryException ex) {
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void aprovaAutomaticamenteSaldoCompra(List<String> adeCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            if (adeCodigos != null && adeCodigos.size() > 0) {
                Iterator<String> it = adeCodigos.iterator();
                while (it.hasNext()) {
                    String adeCodigo = it.next();
                    // Atualiza o status do relacionamento de compra
                    updateRelAutorizacaoCompra(adeCodigo, OperacaoCompraEnum.APROVAR_SALDO_DEVEDOR, responsavel);
                    // Cria uma ocorrência de aprovação automática
                    criaOcorrenciaADE(adeCodigo, CodedValues.TOC_SALDO_DEVEDOR_APROVADO_SERVIDOR, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.aprovacao.automatica.saldo.devedor", responsavel), responsavel);
                }
            }
        } catch (CompraContratoControllerException ex) {
            throw new AutorizacaoControllerException(ex);
        }
    }

    /**
     * Suspende os descontos em folha das consignações com pendência de compra de consignatárias bloqueadas
     * por motivo não temporário, ou seja, aquelas em que a penalidade de bloqueio não surtirá efeito, visto
     * já estar bloqueada.
     * @param consignacoes
     * @param ocaObs
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    private void suspendeDescontosComPendenciaCompra(List<TransferObject> consignacoes, String ocaObs, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if (consignacoes != null && consignacoes.size() > 0) {
            Iterator<TransferObject> it = consignacoes.iterator();
            while (it.hasNext()) {
                TransferObject consignacao = it.next();
                String adeCodigo = (String) consignacao.getAttribute(Columns.ADE_CODIGO);

                try {
                    // Atualiza flag de integração com a folha, permitindo apenas envio da exclusão
                    AutDesconto ade = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
                    Short adeIntFolhaOld = ade.getAdeIntFolha();
                    Short adeIntFolhaNew = CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO;
                    ade.setAdeIntFolha(adeIntFolhaNew);
                    AutDescontoHome.update(ade);

                    // Cria ocorrência de suspensão do desconto em folha
                    criaOcorrenciaADE(adeCodigo, CodedValues.TOC_SUSPENSAO_DESCONTO_FOLHA, ocaObs, responsavel);

                    // Se exportação somente inicial, inclui ocorrência de liquidação para ser enviada para a folha
                    if (ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, responsavel)) {
                        // Obtém o período atual de lançamento para comparar com as ocorrências de exclusão e relançamento
                        String orgCodigo = OrgaoHome.findByAdeCod(adeCodigo).getOrgCodigo();
                        java.util.Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);

                        // Se a consignação a ter suspensão dos descontos tiver ocorrência de relançamento ainda não exportada, então
                        // remove a ocorrência de relançamento e não precisa incluir ocorrência de exclusão, visto que há uma tentativa
                        // de reimplante da folha ainda não efetivada.
                        boolean enviaExclusao = true;
                        Collection<OcorrenciaAutorizacao> ocorrencias = OcorrenciaAutorizacaoHome.findByAdeTocCodigoOrdenado(adeCodigo, CodedValues.TOC_RELANCAMENTO);
                        if (ocorrencias != null && ocorrencias.size() > 0) {
                            OcorrenciaAutorizacao ocaRel = ocorrencias.iterator().next();
                            if (ocaRel.getOcaPeriodo() != null && ocaRel.getOcaPeriodo().compareTo(periodoAtual) >= 0) {
                                // Se ainda não foi exportada, a ocorrência deve ser removida, evitando que o contrato seja reenviado como inclusão para a folha
                                OcorrenciaAutorizacaoHome.remove(ocaRel);
                                enviaExclusao = false;
                            }
                        }

                        if (enviaExclusao) {
                            // Inclui ocorrencia de liquidação
                            criaOcorrenciaADE(adeCodigo, CodedValues.TOC_TARIF_LIQUIDACAO, ocaObs, responsavel);
                        }
                    }

                    // Gera o Log de auditoria
                    LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.ALTERAR_CONSIGNACAO, Log.LOG_INFORMACAO);
                    log.setAutorizacaoDesconto(adeCodigo);
                    log.addChangedField(Columns.ADE_INT_FOLHA, adeIntFolhaNew, adeIntFolhaOld);
                    log.write();

                } catch (FindException | UpdateException | RemoveException ex) {
                    TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                    throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                } catch (LogControllerException | PeriodoException ex) {
                    throw new AutorizacaoControllerException(ex);
                }
            }
        }
    }

    /**
     * Reativa o desconto em folha da consignação após a resolução da pendência de compra,
     * seja informação de saldo ou liquidação do contrato comprado. Em caso de sistema com
     * movimento incial, verifica as ocorrências de liquidação ou relançamento para a correta
     * integração com a folha.
     * @param adeCodigo
     * @param liquidacao
     * @param responsavel
     * @throws CompraContratoControllerException
     */
    @Override
    public void reativarDescontoAposPendenciaCompra(String adeCodigo, boolean liquidacao, AcessoSistema responsavel) throws CompraContratoControllerException {
        reativarDescontoAposPendenciaCompra(adeCodigo, liquidacao, null, responsavel);
    }

    /**
     * Reativa o desconto em folha da consignação após a resolução da pendência de compra,
     * seja informação de saldo ou liquidação do contrato comprado. Em caso de sistema com
     * movimento incial, verifica as ocorrências de liquidação ou relançamento para a correta
     * integração com a folha.
     * @param adeCodigo
     * @param liquidacao
     * @param responsavel
     * @param ocaPeriodo - explicita para qual período a ocorrência será registrada.
     * @throws CompraContratoControllerException
     */
    @Override
    public void reativarDescontoAposPendenciaCompra(String adeCodigo, boolean liquidacao, java.util.Date ocaPeriodo, AcessoSistema responsavel) throws CompraContratoControllerException {
        try {
            boolean exportacaoInicial = ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, responsavel);

            // Obtém o período atual de lançamento para comparar com as ocorrências de exclusão e relançamento
            String orgCodigo = OrgaoHome.findByAdeCod(adeCodigo).getOrgCodigo();
            java.util.Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);

            // Busca a consignação a ser reativada na folha
            AutDesconto ade = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);

            Short adeIntFolhaOld = ade.getAdeIntFolha();
            Short adeIntFolhaNew = CodedValues.INTEGRA_FOLHA_SIM;

            if (adeIntFolhaOld != null && adeIntFolhaOld.equals(CodedValues.INTEGRA_FOLHA_SOMENTE_EXCLUSAO)) {
                // Atualiza flag de integração com a folha, permitindo apenas envio da exclusão
                ade.setAdeIntFolha(adeIntFolhaNew);
                AutDescontoHome.update(ade);

                // Cria ocorrência de reativação do desconto em folha
                criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.reativacao.automatica.desconto.compra", responsavel), null, null, null, ocaPeriodo, null, responsavel);

                // Se exportação somente inicial, verifica ocorrências de liquidação ou reimplante
                if (exportacaoInicial) {
                    // Em qualquer cenário, considera por padrão que a ocorrência de liquidação
                    // ainda não foi enviada para a folha.
                    boolean exclusaoEnviada = false;

                    // Obtém as ocorrências de liquidação do contrato que está sendo reativado
                    Collection<OcorrenciaAutorizacao> ocorrencias = OcorrenciaAutorizacaoHome.findByAdeTocCodigoOrdenado(adeCodigo, CodedValues.TOC_TARIF_LIQUIDACAO);
                    OcorrenciaAutorizacao ocaLiq = null;
                    if (ocorrencias != null && ocorrencias.size() > 0) {
                        Iterator<OcorrenciaAutorizacao> it = ocorrencias.iterator();
                        ocaLiq = it.next();

                        // Se está "reativando" na operação de liquidação, então obtém a próxima ocorrência
                        // de liquidação, na ordem decrescente, pois a última será a da operação atual de
                        // liquidação, e não da incluída na suspensão do desconto
                        if (liquidacao && it.hasNext()) {
                            ocaLiq = it.next();
                        }

                        // Obtém o período da última ocorrência de liquidação
                        java.util.Date ocaPeriodoLiq = ocaLiq.getOcaPeriodo();

                        // Se o período da última ocorrência de liquidação é menor que o período
                        // atual de lançamento, significa que a liquidação ainda já foi enviada para a folha
                        if (ocaPeriodoLiq != null && ocaPeriodoLiq.compareTo(periodoAtual) < 0) {
                            exclusaoEnviada = true;
                        }
                    }

                    // Se a exclusão não foi enviada, remove a ocorrência de liquidação
                    if (!exclusaoEnviada && ocaLiq != null) {
                        OcorrenciaAutorizacaoHome.remove(ocaLiq);
                    }

                    // Se a exclusão já foi enviada e é operação de liquidação, remove a última
                    // ocorrência de liquidação, incluída pela atual operação de liquidação
                    // pois não é necessário novo envio de exclusão para a folha.
                    if (exclusaoEnviada && liquidacao && !ocorrencias.isEmpty()) {
                        OcorrenciaAutorizacaoHome.remove(ocorrencias.iterator().next());
                    }

                    // Se a exclusão já foi enviada e é operação de inf. saldo, então é necessário
                    // forçar o "reimplante" da consignação para que seja enviada na próxima folha
                    if (exclusaoEnviada && !liquidacao) {
                        // Zera os campos folha, para o sistema identificar este contrato para ser enviado à folha.
                        ade.setAdeVlrFolha(null);
                        ade.setAdePaga("N");
                        AutDescontoHome.update(ade);

                        // Inclui ocorrência de relançamento
                        criaOcorrenciaADE(adeCodigo, CodedValues.TOC_RELANCAMENTO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.oca.obs.reimplante.contrato", responsavel), responsavel);
                    }
                }

                // Gera o Log de auditoria
                LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.ALTERAR_CONSIGNACAO, Log.LOG_INFORMACAO);
                log.setAutorizacaoDesconto(adeCodigo);
                log.addChangedField(Columns.ADE_INT_FOLHA, adeIntFolhaNew, adeIntFolhaOld);
                log.write();
            }

            if (liquidacao && exportacaoInicial) {
                // Independente do valor do campo ADE_INT_FOLHA, verifica se a eventual ocorrência de relançamento incluída
                // na operação de inf. saldo, já foi enviada à folha.
                Collection<OcorrenciaAutorizacao> ocorrencias = OcorrenciaAutorizacaoHome.findByAdeTocCodigoOrdenado(adeCodigo, CodedValues.TOC_RELANCAMENTO);
                if (ocorrencias != null && ocorrencias.size() > 0) {
                    OcorrenciaAutorizacao ocaRel = ocorrencias.iterator().next();
                    if (ocaRel.getOcaPeriodo() != null && ocaRel.getOcaPeriodo().compareTo(periodoAtual) >= 0) {
                        // Se não foi, a ocorrência deve ser removida, evitando que o contrato seja reenviado como inclusão para a folha
                        OcorrenciaAutorizacaoHome.remove(ocaRel);

                        // A nova ocorrência de liquidação, incluída na atual liquidação, também deve ser removida, pois se há
                        // uma tentativa de relançamento ainda não efetivada, o contrato não precisa ser excluído da folha.
                        // DESENV-17353 - Para garantir que não haja erro de usuário, ou seja um reimplante de um contrato que a folha já conhece, validamos o ade_paga
                        if (!TextHelper.isNull(ade.getAdePaga()) && ade.getAdePaga().equals(CodedValues.TPC_NAO)) {
                            removeOcorrenciaADE(adeCodigo, CodedValues.TOC_TARIF_LIQUIDACAO, false, false, true, responsavel);
                        }
                    }
                }
                // Se foi enviada, ou não existe ocorrência de relançamento, então não altera as ocorrências de liquidação
                // de modo que o contrato seja novamente excluído da folha.
            }
        } catch (FindException | UpdateException | RemoveException | LogControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new CompraContratoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (AutorizacaoControllerException | PeriodoException ex) {
            throw new CompraContratoControllerException(ex);
        }
    }

    @Override
    public boolean consignatariaNaoPossuiPendenciaCompra(String csaCodigo, AcessoSistema responsavel) throws CompraContratoControllerException {
        try {
            List<String> csaCodigos = null;

            // Verifica se a consignataria possui pendencia de compra
            ListaCsaBloqueioInfSaldoQuery query1 = new ListaCsaBloqueioInfSaldoQuery();
            query1.csaCodigo = csaCodigo;
            csaCodigos = query1.executarLista();
            if (csaCodigos != null && csaCodigos.size() > 0) {
                return false;
            }

            ListaCsaBloqueioInfPgtSaldoQuery query2 = new ListaCsaBloqueioInfPgtSaldoQuery();
            query2.csaCodigo = csaCodigo;
            csaCodigos = query2.executarLista();
            if (csaCodigos != null && csaCodigos.size() > 0) {
                return false;
            }

            ListaCsaBloqueioLiquidacaoQuery query3 = new ListaCsaBloqueioLiquidacaoQuery();
            query3.csaCodigo = csaCodigo;
            csaCodigos = query3.executarLista();
            if (csaCodigos != null && csaCodigos.size() > 0) {
                return false;
            }

            // Verifica se a consignataria possui pendencia de compra já resolvida,
            // porém dentro do prazo de carência para desbloqueio
            if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_CARENCIA_DESBL_AUT_CSA_COMPRA, CodedValues.TPC_SIM, responsavel)) {
                ListaCsaCarenciaBloqueioInfSaldoQuery query4 = new ListaCsaCarenciaBloqueioInfSaldoQuery();
                query4.csaCodigo = csaCodigo;
                csaCodigos = query4.executarLista();
                if (csaCodigos != null && csaCodigos.size() > 0) {
                    return false;
                }

                ListaCsaCarenciaBloqueioInfPgtSaldoQuery query5 = new ListaCsaCarenciaBloqueioInfPgtSaldoQuery();
                query5.csaCodigo = csaCodigo;
                csaCodigos = query5.executarLista();
                if (csaCodigos != null && csaCodigos.size() > 0) {
                    return false;
                }

                ListaCsaCarenciaBloqueioLiquidacaoQuery query6 = new ListaCsaCarenciaBloqueioLiquidacaoQuery();
                query6.csaCodigo = csaCodigo;
                csaCodigos = query6.executarLista();
                if (csaCodigos != null && csaCodigos.size() > 0) {
                    return false;
                }
            }

            // Verifica se a consignatária possui pendência por causa de um processo de
            // compra que tenha rejeito de pagamento de saldo devedor
            ListaCsaBloqueioRejPgtSaldoOrigemQuery query7 = new ListaCsaBloqueioRejPgtSaldoOrigemQuery();
            query7.csaCodigo = csaCodigo;
            csaCodigos = query7.executarLista();
            if (csaCodigos != null && csaCodigos.size() > 0) {
                return false;
            }

            ListaCsaBloqueioRejPgtSaldoDestinoQuery query8 = new ListaCsaBloqueioRejPgtSaldoDestinoQuery();
            query8.csaCodigo = csaCodigo;
            csaCodigos = query8.executarLista();
            if (csaCodigos != null && csaCodigos.size() > 0) {
                return false;
            }
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CompraContratoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
        return true;
    }

    /**
     * Retorna o contrato originado por uma compra, dado uns dos contratos participantes da operação.
     * @param adeCodigo
     * @return
     * @throws CompraContratoControllerException
     */
    @Override
    public String recuperarAdeDestinoCompra(String adeCodigo) throws CompraContratoControllerException {
        // Recupera os relacionamentos em que o contrato foi comprado.
        List<RelacionamentoAutorizacao> relacionamentos;
        try {
            relacionamentos = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_CONTROLE_COMPRA);
        } catch (FindException e) {
            throw new CompraContratoControllerException("mensagem.erro.falha.recuperar.relacionamentos.compra.contrato", null, e);
        }

        Comparator<RelacionamentoAutorizacao> comparator = (oca1, oca2) -> {
            return oca1.getRadData().compareTo(oca2.getRadData());
        };

        // Recupera as demais ADEs envolvidas na mesma compra.
        RelacionamentoAutorizacao rad = null;
        String adeDestinoCompra = "";
        if (relacionamentos != null && relacionamentos.size() > 0) {
            rad = Collections.max(relacionamentos, comparator);
            adeDestinoCompra = rad.getAutDescontoByAdeCodigoDestino().getAdeCodigo();
        } else {
            adeDestinoCompra = null;
        }

        return adeDestinoCompra;
    }

    /**
     * Verifica se a dada ADE é a única que prende a finalização do processo de compra
     * @param adeCodigo ADE a ser testada
     * @param adeCodigoDestinoCompra ADE criada pela operação de compra
     * @return
     * @throws CompraContratoControllerException
     */
    @Override
    public boolean ultimaAdeFinalizacaoCompra(String adeCodigo, String adeCodigoDestinoCompra) throws CompraContratoControllerException {
        if (adeCodigoDestinoCompra == null) {
            adeCodigoDestinoCompra = recuperarAdeDestinoCompra(adeCodigo);
        }

        if (adeCodigoDestinoCompra == null) {
            throw new CompraContratoControllerException("mensagem.erro.localizar.contrato.originado.compra.contrato", (AcessoSistema) null);
        }

        // Recupera as demais ADEs envolvidas na mesma compra.
        List<RelacionamentoAutorizacao> relacionamentos;
        try {
            relacionamentos = RelacionamentoAutorizacaoHome.findByDestino(adeCodigoDestinoCompra, CodedValues.TNT_CONTROLE_COMPRA);
        } catch (FindException e) {
            throw new CompraContratoControllerException("mensagem.erro.falha.recuperar.relacionamentos.compra.contrato", null, e);
        }

        boolean finalizar = true;
        Iterator<RelacionamentoAutorizacao> it = relacionamentos.iterator();
        while (it.hasNext()) {
            RelacionamentoAutorizacao relAde = it.next();

            AutDesconto adeOrigem;
            try {
                adeOrigem = AutDescontoHome.findByPrimaryKey(relAde.getAutDescontoByAdeCodigoOrigem().getAdeCodigo());
            } catch (FindException e) {
                throw new CompraContratoControllerException("mensagem.erro.recuperar.contrato.compra.contrato", null, e);
            }

            if (!adeOrigem.getAdeCodigo().equals(adeCodigo)) {
                String sadCodigoTemp = adeOrigem.getStatusAutorizacaoDesconto().getSadCodigo();
                finalizar = finalizar && (sadCodigoTemp.equals(CodedValues.SAD_LIQUIDADA) || sadCodigoTemp.equals(CodedValues.SAD_CONCLUIDO));
            }
        }

        return finalizar;
    }

    /**
     * Finaliza o processo de compra, deferindo o novo contrato, ou apenas atualizando a
     * incidência de margem, caso o contrato não possa ser confirmado.
     * @param adeCodigo: Código do contrato destino da compra
     * @param responsavel
     * @throws CompraContratoControllerException
     */
    @Override
    public void finalizarCompra(String adeCodigo, AcessoSistema responsavel) throws CompraContratoControllerException {
        finalizarCompra(adeCodigo, null, responsavel);
    }

    @Override
    public void finalizarCompra(String adeCodigo, java.util.Date ocaPeriodo, AcessoSistema responsavel) throws CompraContratoControllerException {
        try {
            // Não grava a ocorrência ligada ao usuário que ocasionou a conclusão da compra,
            // pois normalmente a compra é finalizada pela última liquidação executada pelo
            // usuário de uma consignatária vendedora, que não deve ser o responsável pelo
            // deferimento do novo contrato.
            responsavel = AcessoSistema.getAcessoUsuarioSistema();

            AutDesconto adeDestino = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
            RegistroServidor registroSer = RegistroServidorHome.findByPrimaryKey(adeDestino.getRegistroServidor().getRseCodigo());
            VerbaConvenio vco = VerbaConvenioHome.findByPrimaryKey(adeDestino.getVerbaConvenio().getVcoCodigo());
            Convenio cnv = ConvenioHome.findByPrimaryKey(vco.getConvenio().getCnvCodigo());
            String svcCodigo = cnv.getServico().getSvcCodigo();
            String csaCodigo = cnv.getConsignataria().getCsaCodigo();
            String rseCodigo = registroSer.getRseCodigo();

            // Se o contrato novo ainda não incide na margem, busca o parâmetro de incidência de margem do
            // serviço ligado ao contrato para realizar a atualização da incidência e da margem
            if (adeDestino.getAdeIncMargem().equals(CodedValues.INCIDE_MARGEM_NAO)) {
                // Cria ocorrência detalhando a finalização do processo de compra
                String ocaCodigo = criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.operacao.compra.finalizada", responsavel), null, null, null, ocaPeriodo, null, responsavel);

                // Atualiza os relacionamentos de compra
                updateStatusRelacionamentoAdesCompradas(adeCodigo, StatusCompraEnum.FINALIZADO, responsavel);

                // Busca o parâmetro de serviço relativo à incidência de margem para atualizar o
                // novo contrato de compra, pois estará não incidindo na margem
                Short incideMargem = CodedValues.INCIDE_MARGEM_SIM;
                try {
                    ParamSvcConsignante pse = null;
                    try {
                        pse = ParamSvcConsignanteHome.findByTipoCseServico(CodedValues.TPS_INCIDE_MARGEM, CodedValues.CSE_CODIGO_SISTEMA, svcCodigo);
                        if (!TextHelper.isNull(pse.getPseVlr())) {
                            incideMargem = Short.parseShort(pse.getPseVlr());
                        }
                    } catch (FindException ex) {
                        // Parâmetro não encontrado, então assume o valor padrão para incide margem
                        incideMargem = CodedValues.INCIDE_MARGEM_SIM;
                    } catch (NumberFormatException ex) {
                        // Parâmetro cadastrado incorretamente
                        LOG.error("O parâmetro de serviço '" + CodedValues.TPS_INCIDE_MARGEM + "' do serviço '" + svcCodigo + "' está cadastrado incorretamente: '" + pse.getPseVlr() + "'.");
                        incideMargem = CodedValues.INCIDE_MARGEM_SIM;
                    }

                    if (!incideMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
                        adeDestino.setAdeIncMargem(incideMargem);
                        AutDescontoHome.update(adeDestino);
                    }
                } catch (UpdateException e) {
                    TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                    throw new CompraContratoControllerException("mensagem.erro.atualizar.incidencia.margem.novo.contrato.compra", responsavel, e);
                }

                try {
                    // Busca os contratos comprados para verificar se a margem deve ser atualizada:
                    // somente o caso do somatório dos contratos comprados ser maior que o novo.
                    if (!incideMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
                        List<RelacionamentoAutorizacao> radCompra = RelacionamentoAutorizacaoHome.findByDestino(adeCodigo, CodedValues.TNT_CONTROLE_COMPRA);
                        Iterator<RelacionamentoAutorizacao> it = radCompra.iterator();
                        BigDecimal valorAntigo = new BigDecimal("0");

                        while (it.hasNext()) {
                            RelacionamentoAutorizacao radCompraBean = it.next();
                            String adeCodigoOrigem = radCompraBean.getAutDescontoByAdeCodigoOrigem().getAdeCodigo();
                            AutDesconto adeOrigem = AutDescontoHome.findByPrimaryKey(adeCodigoOrigem);
                            if (AutorizacaoHelper.valorMargemDisponivelRenegociacao(incideMargem, adeOrigem.getAdeIncMargem(), responsavel)) {
                                valorAntigo = valorAntigo.add(adeOrigem.getAdeVlr());
                            }
                        }

                        // Se o valor da nova autorização é menor do que a soma dos comprados
                        // libera da margem o valor da diferença, pois o sistema terá mantido
                        // na margem o maior valor, que são os comprados
                        BigDecimal diff = adeDestino.getAdeVlr().subtract(valorAntigo);
                        if (diff.signum() == -1) {
                            atualizaMargem(rseCodigo, incideMargem, diff, false, true, true, ocaCodigo, csaCodigo, svcCodigo, null, responsavel);
                        }
                    }
                } catch (AutorizacaoControllerException ex) {
                    throw new CompraContratoControllerException(ex);
                }
            }

            // Se o contrato novo pode ser confirmado (usuário tinha permissão de confirmar reserva) e
            // o contrato não está na situação de Deferido e o servidor não está excluído então
            // procede o deferimento do contrato.
            if (adeDestino.getAdePodeConfirmar() != null && adeDestino.getAdePodeConfirmar().equals("S") && !adeDestino.getStatusAutorizacaoDesconto().getSadCodigo().equals(CodedValues.SAD_DEFERIDA) && !CodedValues.SRS_INATIVOS.contains(registroSer.getStatusRegistroServidor().getSrsCodigo())) {
                try {
                    // Se todos os contratos envolvidos na compra já foram liquidados, e o usuário
                    // que realizou a compra pode confirmar reserva (AdePodeConfirmar), verifica se
                    // requer deferimento manual.
                    if (usuarioPodeDeferir(svcCodigo, csaCodigo, rseCodigo, false, responsavel)) {
                        // Não exige deferimento manual, então realiza o deferimento do novo contrato de compra
                        deferirConsignacaoController.deferir(adeCodigo, null, responsavel);
                    } else {
                        // Exige deferimento, então atualiza o status do contrato para Aguard. Deferimento
                        modificaSituacaoADE(adeDestino, CodedValues.SAD_AGUARD_DEFER, responsavel);
                        // Se contrato na situação SAD_AGUARD_DEFER incluir ocorrência
                        // com prazo para deferimento automático
                        alertarPrazoDeferimentoAut(adeDestino, svcCodigo, null, responsavel);
                    }
                } catch (AutorizacaoControllerException e) {
                    throw new CompraContratoControllerException("mensagem.erro.deferir.novo.contrato.compra", null, e);
                }
            }
        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);

            if (ex.getClass().equals(CompraContratoControllerException.class)) {
                throw (CompraContratoControllerException) ex;
            } else if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw new CompraContratoControllerException(ex);
            }
            throw new CompraContratoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Retira um contrato de uma compra, quando esta envolvia mais de uma ADE.
     * @param adeCodigo
     * @param textoObservacao
     * @param responsavel
     * @throws CompraContratoControllerException
     */
    @Override
    public void retirarContratoCompra(String adeCodigo, String textoObservacao, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws CompraContratoControllerException {
        try {
            // Recupera o contrato
            AutDesconto autdes = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);

            // Recupera o contrato destino da compra
            String adeCodigoDestino = recuperarAdeDestinoCompra(adeCodigo);
            if (adeCodigoDestino == null) {
                throw new CompraContratoControllerException("mensagem.erro.recuperar.contrato.destino.compra", (AcessoSistema) null);
            }

            // DESENV-16086 - Caso seja um contrato que foi permitido com margem negativa ele não pode ser retirado da portabilidade
            Collection<OcorrenciaAutorizacao> ocorrenciaPortabilidadeMargemNegativa = findByAdeTocCodigo(adeCodigoDestino, CodedValues.TOC_PORTABILIDADE_MARGEM_NEGATIVA, responsavel);
            if (ocorrenciaPortabilidadeMargemNegativa != null && !ocorrenciaPortabilidadeMargemNegativa.isEmpty()) {
                throw new CompraContratoControllerException("mensagem.erro.retirar.permite.margem.negativa", responsavel);
            }

            AutDesconto adeDestino = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigoDestino);

            // Verifica se o usuário tem permissão para modificar o contrato destino.
            if (!usuarioPodeModificarAde(adeCodigoDestino, responsavel)) {
                throw new CompraContratoControllerException("mensagem.usuarioNaoTemPermissao", (AcessoSistema) null);
            }

            // Recupera os contratos participantes da compra
            List<RelacionamentoAutorizacao> relacionamentosCompra = RelacionamentoAutorizacaoHome.findByDestino(adeCodigoDestino, CodedValues.TNT_CONTROLE_COMPRA);

            // Impede operação caso seja o único contrato participante da compra.
            if (relacionamentosCompra == null || relacionamentosCompra.size() <= 1) {
                throw new CompraContratoControllerException("mensagem.erro.operacao.minimo.dois.contratos.compra", responsavel);
            }

            // Impede operação caso o valor do contrato retirado seja maior ou igual ao valor do novo contrato
            if (adeDestino.getAdeVlr().compareTo(autdes.getAdeVlr()) <= 0) {
                throw new CompraContratoControllerException("mensagem.erro.valor.contrato.maior.compra", responsavel);
            }

            // Verifica se ao retirar contrato da compra, os limites de contratos não são excedidos
            try {
                verificaLimiteAoRetirarContratoCompra(adeCodigo, adeCodigoDestino, responsavel);
            } catch (AutorizacaoControllerException ex) {
                throw new CompraContratoControllerException(ex);
            }

            java.util.Date ocaPeriodo = null;
            if ((ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_SIM, responsavel)) || ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel)) {
                if (tipoMotivoOperacao != null && !TextHelper.isNull(tipoMotivoOperacao.getAttribute(Columns.OCA_PERIODO))) {
                    ocaPeriodo = DateHelper.parse(tipoMotivoOperacao.getAttribute(Columns.OCA_PERIODO).toString(), "yyyy-MM-dd");
                }
            }

            // Volta contrato para Deferido ou Em Andamento dependendo da qtde de parcelas pagas.
            String sadCodigoNovo = (autdes.getAdePrdPagas() != null && autdes.getAdePrdPagas().intValue() > 0) ? CodedValues.SAD_EMANDAMENTO : CodedValues.SAD_DEFERIDA;
            modificaSituacaoADE(autdes, sadCodigoNovo, responsavel, true, ocaPeriodo, true);

            String ocaObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.retirado.negociacao.contrato", responsavel, adeDestino.getAdeNumero().toString()) + (!TextHelper.isNull(textoObservacao) ? ": " + textoObservacao : ".");
            String ocaCodigo = criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ocaObs, null, null, null, ocaPeriodo, null, responsavel);

            if (ocaCodigo != null && tipoMotivoOperacao != null) {
                // grava motivo da operacao
                tipoMotivoOperacao.setAttribute(Columns.OCA_CODIGO, ocaCodigo);
                tipoMotivoOperacaoController.gravarMotivoOperacaoConsignacao(tipoMotivoOperacao, responsavel);
            }

            // Subtrai do valor do contrato destino o valor do contrato retirado.
            BigDecimal adeVlrAnterior = new BigDecimal(adeDestino.getAdeVlr().doubleValue(), new MathContext(15, RoundingMode.HALF_UP)).setScale(2);
            BigDecimal adeVlrNovo = new BigDecimal(adeDestino.getAdeVlr().subtract(autdes.getAdeVlr()).doubleValue(), new MathContext(15, RoundingMode.HALF_UP)).setScale(2);
            adeDestino.setAdeVlr(adeVlrNovo);
            AutDescontoHome.update(adeDestino);

            // Remove relacionamento de compra.
            List<RelacionamentoAutorizacao> relacionamentos = RelacionamentoAutorizacaoHome.findByOrigemDestino(adeCodigo, adeCodigoDestino, CodedValues.TNT_CONTROLE_COMPRA);
            RelacionamentoAutorizacao relacionamentoCompra = null;
            if (relacionamentos != null && relacionamentos.size() == 1) {
                relacionamentoCompra = relacionamentos.iterator().next();
            } else {
                throw new CompraContratoControllerException("mensagem.erro.recuperar.relacionamento.compra", responsavel);
            }
            RelacionamentoAutorizacaoHome.remove(relacionamentoCompra);

            // Insere a ocorrência de retirada no contrato criado pela compra.
            String ocaDescricao = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.contrato.retirado.compra", responsavel, autdes.getAdeNumero().toString(), NumberHelper.format(autdes.getAdeVlr().doubleValue(), NumberHelper.getLang()), NumberHelper.format(adeVlrAnterior.doubleValue(), NumberHelper.getLang()), NumberHelper.format(adeVlrNovo.doubleValue(), NumberHelper.getLang()));

            criaOcorrenciaADE(adeCodigoDestino, CodedValues.TOC_INFORMACAO, ocaDescricao, adeVlrAnterior, adeVlrNovo, null, ocaPeriodo, null, responsavel);

            // DESENV-19157 : Restaura integração com a folha da consignação caso suspensa pelo fato da CSA estar bloqueada
            if (ParamSist.paramEquals(CodedValues.TPC_SUSPENDE_DESC_CSA_BLOQ_CONTROLE_COMPRA, CodedValues.TPC_SIM, responsavel)) {
                reativarDescontoAposPendenciaCompra(adeCodigo, false, ocaPeriodo, responsavel);
            }

            // Realiza o desbloqueio automático das consignatárias envolvidas na retirada do contrato da compra.
            Collection<String> csaCodigos = new ArrayList<>();
            csaCodigos.add(adeCodigo);
            csaCodigos.add(adeCodigoDestino);
            executarDesbloqueioAutomaticoConsignatarias(csaCodigos, responsavel);

            // Finalizar a compra, caso seja o único contrato aguardando liquidação de compra.
            if (ultimaAdeFinalizacaoCompra(adeCodigo, adeCodigoDestino)) {
                finalizarCompra(adeCodigoDestino, responsavel);
            }
        } catch (Exception e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();

            LOG.error(e.getMessage(), e);

            if (e.getClass().equals(CompraContratoControllerException.class)) {
                throw (CompraContratoControllerException) e;
            }

            throw new CompraContratoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    /**
     * Verifica se as consignatárias dos contratos de origem ou destino da compra
     * foram bloqueadas automaticamente e se podem ser agora desbloqueadas. Jà executa a operação em caso positvo.
     * Se não for passado a lista de contratos, então o desbloqueio será verificado para
     * todas as consignatárias que estão bloqueadas por motivo de compra.
     * @param codAdeOrigem Código da ADE de origem da compra.
     * @param codAdeDestino Código da ADE de destino da compra.
     * @param responsavel Responsável.
     */
    @Override
    public void executarDesbloqueioAutomaticoConsignatarias(Collection<String> adeCodigos, AcessoSistema responsavel) {
        // Se o módulo avançado de compras não está habilitado ou se as consignatárias não podem ser desbloqueadas automaticamente.
        if (!ParamSist.paramEquals(CodedValues.TPC_CONTROLE_DETALHADO_PROCESSO_COMPRA, CodedValues.TPC_SIM, responsavel) || !ParamSist.paramEquals(CodedValues.TPC_DESBL_AUTOMATICO_CSA_CONTROLE_COMPRA, CodedValues.TPC_SIM, responsavel)) {
            return;
        }

        try {
            List<String> consignatarias = null;

            if (adeCodigos != null && adeCodigos.size() > 0) {
                // Se foi passado a lista de consignações, executa o desbloqueio das consignatárias
                // relacionadas a estas consignações
                ObtemConsignatariasPorAdeCodigoQuery query = new ObtemConsignatariasPorAdeCodigoQuery();
                query.adeCodigos = new ArrayList<>(adeCodigos);
                consignatarias = query.executarLista();
            } else {
                // Se não foi passado a lista de consignações, verifica todas as consignatárias
                // que estão bloqueadas por motivo de compra
                ListaCsaComOcorrenciaPendenciaCompraQuery query = new ListaCsaComOcorrenciaPendenciaCompraQuery();
                consignatarias = query.executarLista();
            }

            if (consignatarias != null && consignatarias.size() > 0) {
                for (String csaCodigo : consignatarias) {
                    consignatariaController.verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel);
                }
            }
        } catch (HQueryException e) {
            LOG.error("Erro ao recuperar as consignatárias dos contratos envolvidos no desbloqueio.", e);
        } catch (ConsignatariaControllerException e) {
            LOG.error("Erro ao desbloquear consignatária.", e);
        }
    }

    @Override
    public List<String> recuperarAdesCodigosDestinoCompra(String adeCodigoOrigem) throws CompraContratoControllerException {
        try {
            List<RelacionamentoAutorizacao> adesCompra = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigoOrigem, CodedValues.TNT_CONTROLE_COMPRA);

            Iterator<RelacionamentoAutorizacao> it = adesCompra.iterator();
            RelacionamentoAutorizacao rel;
            List<String> adesCodigos = new ArrayList<>();
            while (it.hasNext()) {
                rel = it.next();
                adesCodigos.add(rel.getAutDescontoByAdeCodigoDestino().getAdeCodigo());
            }

            return adesCodigos;
        } catch (FindException e) {
            throw new CompraContratoControllerException("mensagem.erro.recuperar.contratos.destino.compra", null, e);
        }
    }

    @Override
    public Collection<RelacionamentoAutorizacao> recuperarContratosOrigemCompra(String adeCodigoDestino) throws CompraContratoControllerException {
        try {
            return RelacionamentoAutorizacaoHome.findByDestino(adeCodigoDestino, CodedValues.TNT_CONTROLE_COMPRA);
        } catch (FindException e) {
            throw new CompraContratoControllerException("mensagem.erro.recuperar.contratos.origem.compra", null, e);
        }
    }

    @Override
    public Collection<RelacionamentoAutorizacao> getRelacionamentoCompra(String adeCodigo) throws CompraContratoControllerException {

        List<RelacionamentoAutorizacao> relacionamentos;
        try {
            relacionamentos = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_CONTROLE_COMPRA);
        } catch (FindException e) {
            throw new CompraContratoControllerException("mensagem.erro.falha.recuperar.relacionamentos.compra.contrato", null, e);
        }
        return relacionamentos;
    }

    @Override
    public Boolean temRelacionamentoCompraByOrigem(String adeCodigo) throws CompraContratoControllerException {
        try {
        	List<RelacionamentoAutorizacao> relacionamentos = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_CONTROLE_COMPRA);
            return relacionamentos != null && !relacionamentos.isEmpty();
        } catch (FindException e) {
            throw new CompraContratoControllerException("mensagem.erro.falha.recuperar.relacionamentos.compra.contrato", null, e);
        }
    }

    /**
     * Atualiza o relacionamento de compra do contrato de acordo com a operação que está
     * sendo realizada sobre o contrato de origem de contra, ou seja, o comprado.
     * @param adeCodigoOrigem
     * @param operacao
     * @param responsavel
     * @throws CompraContratoControllerException
     */
    @Override
    public void updateRelAutorizacaoCompra(String adeCodigoOrigem, OperacaoCompraEnum operacao, AcessoSistema responsavel) throws CompraContratoControllerException {
        try {
            if (adeCodigoOrigem != null && operacao != null) {
                Collection<RelacionamentoAutorizacao> adesCompra = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigoOrigem, CodedValues.TNT_CONTROLE_COMPRA, CodedValues.SAD_AGUARD_CONF);
                if (adesCompra != null && !adesCompra.isEmpty()) {
                    RelacionamentoAutorizacao radBean = adesCompra.iterator().next();
                    Timestamp radDataRef = new Timestamp(Calendar.getInstance().getTimeInMillis());
                    Timestamp radDataInfSaldo = null;
                    Timestamp radDataAprSaldo = null;
                    Timestamp radDataPgtSaldo = null;
                    Timestamp radDataLiquidacao = null;

                    AutDesconto adeDestino = AutDescontoHome.findByPrimaryKeyForUpdate(radBean.getAutDescontoByAdeCodigoDestino().getAdeCodigo());
                    VerbaConvenio vco = VerbaConvenioHome.findByPrimaryKey(adeDestino.getVerbaConvenio().getVcoCodigo());
                    Convenio cnv = ConvenioHome.findByPrimaryKey(vco.getConvenio().getCnvCodigo());
                    String svcCodigo = cnv.getServico().getSvcCodigo();
                    boolean consideraDataInfSaldoLiquidacao = false;
                    try {
                        ParamSvcConsignante pse = ParamSvcConsignanteHome.findByTipoCseServico(CodedValues.TPS_CONSIDERA_DATA_INF_SALDO_LIQUIDACAO_ADE_CONTROLE_COMPRA, CodedValues.CSE_CODIGO_SISTEMA, svcCodigo);
                        if (!TextHelper.isNull(pse.getPseVlr())) {
                            consideraDataInfSaldoLiquidacao = pse.getPseVlr().equals(CodedValues.PSE_BOOLEANO_SIM);
                        }
                    } catch (FindException ex) {
                        // Parâmetro não encontrado, então assume o valor padrão para incide margem
                    }

                    // Busca os parâmetros de sistema necessários
                    boolean temEtapaAprovacaoSaldo = ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavel);
                    boolean cicloVidaFixo = ParamSist.paramEquals(CodedValues.TPC_CICLO_VIDA_FIXO_PROCESSO_COMPRA, CodedValues.TPC_SIM, responsavel);

                    boolean permiteLivreLiquidacao = false;
                    if (responsavel.isCseSupOrg()) {
                        permiteLivreLiquidacao = ParamSist.paramEquals(CodedValues.TPC_PERMITE_LIQ_CICLO_VIDA_FIXO_CSE_ORG_SUP, CodedValues.TPC_SIM, responsavel);
                    } else if (responsavel.isCsaCor()) {
                        permiteLivreLiquidacao = ParamSist.paramEquals(CodedValues.TPC_PERMITE_LIQ_CICLO_VIDA_FIXO_CSA_COR, CodedValues.TPC_SIM, responsavel);
                    }

                    boolean avancaFluxoSemCicloFixo = !cicloVidaFixo && ParamSist.paramEquals(CodedValues.TPC_AVANCA_FLUXO_COMPRA_SEM_CICLO_FIXO, CodedValues.TPC_SIM, responsavel);

                    // Define qual o status esperado e o novo a partir da operação
                    StatusCompraEnum statusCompraAtual = StatusCompraEnum.recuperaStatusCompra(radBean.getStatusCompra().getStcCodigo());
                    StatusCompraEnum statusCompraEsperado = null;
                    StatusCompraEnum statusCompraNovo = null;

                    if (operacao.equals(OperacaoCompraEnum.INFORMAR_SALDO_DEVEDOR)) {
                        statusCompraEsperado = StatusCompraEnum.AGUARDANDO_INF_SALDO;
                        statusCompraNovo = (temEtapaAprovacaoSaldo ? StatusCompraEnum.AGUARDANDO_APR_SALDO : StatusCompraEnum.AGUARDANDO_PAG_SALDO);
                        radDataInfSaldo = radDataRef;
                    } else if (operacao.equals(OperacaoCompraEnum.APROVAR_SALDO_DEVEDOR)) {
                        statusCompraEsperado = StatusCompraEnum.AGUARDANDO_APR_SALDO;
                        statusCompraNovo = StatusCompraEnum.AGUARDANDO_PAG_SALDO;
                        radDataAprSaldo = radDataRef;
                    } else if (operacao.equals(OperacaoCompraEnum.REJEITAR_SALDO_DEVEDOR)) {
                        statusCompraEsperado = StatusCompraEnum.AGUARDANDO_APR_SALDO;
                        statusCompraNovo = StatusCompraEnum.AGUARDANDO_INF_SALDO;
                    } else if (operacao.equals(OperacaoCompraEnum.SOLICITAR_RECALCULO_SALDO)) {
                        statusCompraEsperado = StatusCompraEnum.AGUARDANDO_PAG_SALDO;
                        statusCompraNovo = StatusCompraEnum.AGUARDANDO_INF_SALDO;
                    } else if (operacao.equals(OperacaoCompraEnum.PAGAMENTO_SALDO_DEVEDOR)) {
                        statusCompraEsperado = StatusCompraEnum.AGUARDANDO_PAG_SALDO;
                        statusCompraNovo = StatusCompraEnum.AGUARDANDO_LIQUIDACAO;
                        radDataPgtSaldo = radDataRef;
                    } else if (operacao.equals(OperacaoCompraEnum.REJEITAR_PAGAMENTO_SALDO)) {
                        statusCompraEsperado = StatusCompraEnum.AGUARDANDO_LIQUIDACAO;
                        statusCompraNovo = StatusCompraEnum.AGUARDANDO_PAG_SALDO;
                    } else if (operacao.equals(OperacaoCompraEnum.LIQUIDACAO_CONTRATO)) {
                        // Recupera consignatária de origem e destino
                        Consignataria csaOrigem = ConsignatariaHome.findByAdeCodigo(radBean.getAdeCodigoOrigem());
                        Consignataria csaDestino = ConsignatariaHome.findByAdeCodigo(radBean.getAdeCodigoDestino());
                        // Se a consignatária do contrato novo é a própria do contrato sendo liquidadeo, não é necessário verificar o status atual
                        boolean compraPropriaCsa = csaOrigem.getCsaCodigo().equals(csaDestino.getCsaCodigo());

                        // Na operação de liquidação de contrato, se permitir livre liquidação ou
                        // a compradora e vendedora são a mesma consignatária, o status esperado não é validado
                        statusCompraEsperado = (permiteLivreLiquidacao || compraPropriaCsa ? statusCompraAtual : StatusCompraEnum.AGUARDANDO_LIQUIDACAO);
                        statusCompraNovo = StatusCompraEnum.LIQUIDADO;
                        radDataLiquidacao = radDataRef;
                    } else if (operacao.equals(OperacaoCompraEnum.CONCLUSAO_CONTRATO)) {
                        // Na operação de conclusão de contrato, não valida o status esperado
                        statusCompraEsperado = statusCompraAtual;
                        statusCompraNovo = StatusCompraEnum.LIQUIDADO;
                        radDataLiquidacao = radDataRef;
                    }

                    if (!statusCompraAtual.equals(statusCompraEsperado) && cicloVidaFixo) {
                        // Se o status atual é incompatível com o esperado e o ciclo é fixo
                        // então não atualiza a situação da compra
                        return;
                    }

                    // Atualiza as datas dos eventos
                    if (radDataInfSaldo != null) {
                        radBean.setRadDataInfSaldo(radDataInfSaldo);
                    }
                    if (radDataAprSaldo != null) {
                        radBean.setRadDataAprSaldo(radDataAprSaldo);
                    }
                    if (radDataPgtSaldo != null || operacao.equals(OperacaoCompraEnum.REJEITAR_PAGAMENTO_SALDO)) {
                        radBean.setRadDataPgtSaldo(radDataPgtSaldo);
                    }
                    if (radDataLiquidacao != null) {
                        radBean.setRadDataLiquidacao(radDataLiquidacao);
                    }

                    // Só altera a situação e as datas de referência se o fluxo estiver
                    // dentro do ciclo de vida padrão
                    if (statusCompraAtual.equals(statusCompraEsperado) || avancaFluxoSemCicloFixo) {
                        // Atualiza status do banco
                        radBean.setStatusCompra(new StatusCompra(statusCompraNovo.getCodigo()));

                        // Atualiza as datas de referência do acompanhamento do fluxo
                        if (statusCompraNovo.equals(StatusCompraEnum.AGUARDANDO_INF_SALDO)) {
                            radBean.setRadDataRefInfSaldo(radDataRef);
                            radBean.setRadDataInfSaldo(null);
                            radBean.setRadDataAprSaldo(null);
                            radBean.setRadDataRefAprSaldo(null);
                            radBean.setRadDataRefPgtSaldo(null);
                            radBean.setRadDataRefLiquidacao(null);
                        } else if (statusCompraNovo.equals(StatusCompraEnum.AGUARDANDO_APR_SALDO)) {
                            radBean.setRadDataRefAprSaldo(radDataRef);
                            radBean.setRadDataAprSaldo(null);
                            radBean.setRadDataRefPgtSaldo(null);
                            if (consideraDataInfSaldoLiquidacao) {
                                radBean.setRadDataRefLiquidacao(radDataRef);
                            } else {
                                radBean.setRadDataRefLiquidacao(null);
                            }
                        } else if (statusCompraNovo.equals(StatusCompraEnum.AGUARDANDO_PAG_SALDO)) {
                            radBean.setRadDataRefPgtSaldo(radDataRef);
                            radBean.setRadDataPgtSaldo(null);
                            radBean.setRadDataRefLiquidacao(null);
                        } else if (statusCompraNovo.equals(StatusCompraEnum.AGUARDANDO_LIQUIDACAO)) {
                            if (consideraDataInfSaldoLiquidacao) {
                                radBean.setRadDataRefLiquidacao(null);
                            } else {
                                radBean.setRadDataRefLiquidacao(radDataRef);
                            }
                            radBean.setRadDataLiquidacao(null);
                        }
                    } else if (statusCompraNovo.equals(StatusCompraEnum.LIQUIDADO)) {
                        // Em caso de ciclo de vida não fixo, só atualiza o status da compra
                        // caso seja a última etapa do processo, ou seja a liquidação
                        radBean.setStatusCompra(new StatusCompra(statusCompraNovo.getCodigo()));
                    }

                    // Executa a atualização do registro
                    RelacionamentoAutorizacaoHome.update(radBean);
                }
            }
        } catch (FindException e) {
            throw new CompraContratoControllerException("mensagem.erro.recuperar.relacionamento.compra", responsavel, e);
        } catch (UpdateException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new CompraContratoControllerException("mensagem.erro.atualizar.relacionamento.compra", responsavel, e);
        }
    }

    /**
     * Atualiza o status de todos os relacionamentos de compra para o contrato de destino, utilizado no
     * deferimento do contrato de destino, quando a compra é finalizada, e no cancelamento da compra.
     * @param adeCodigoDestino
     * @param statusCompraEnum
     * @param responsavel
     * @throws CompraContratoControllerException
     */
    @Override
    public void updateStatusRelacionamentoAdesCompradas(String adeCodigoDestino, StatusCompraEnum statusCompraEnum, AcessoSistema responsavel) throws CompraContratoControllerException {
        try {
            List<RelacionamentoAutorizacao> adeCompradas = RelacionamentoAutorizacaoHome.findByDestino(adeCodigoDestino, CodedValues.TNT_CONTROLE_COMPRA);
            Iterator<RelacionamentoAutorizacao> itCompra = adeCompradas.iterator();
            while (itCompra.hasNext()) {
                RelacionamentoAutorizacao radBean = itCompra.next();
                radBean.setStatusCompra(StatusCompraHome.findByPrimaryKey(statusCompraEnum.getCodigo()));
                RelacionamentoAutorizacaoHome.update(radBean);
            }
        } catch (FindException e) {
            throw new CompraContratoControllerException("mensagem.erro.recuperar.relacionamento.compra", responsavel, e);
        } catch (UpdateException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new CompraContratoControllerException("mensagem.erro.atualizar.relacionamento.compra", responsavel, e);
        }
    }

    /**
     * Realiza a conclusão dos contratos origem de compra, que estão na situação de aguard. liquidação de compra
     * e tiveram a última parcela paga. Atualiza a situação do relacionamento de compra e finaliza (ou cancela)
     * aquelas compras que podem ser finalizadas (canceladas).
     * @param responsavel
     * @throws CompraContratoControllerException
     */
    @Override
    public void concluiContratosAguardLiquidCompra(AcessoSistema responsavel) throws CompraContratoControllerException {
        try {
            LOG.debug("*************************************************************************************");
            LOG.debug("INÍCIO - CONCLUSÃO DE CONTRATOS AGUARDANDO LIQUIDAÇÃO DE COMPRA");

            // Lista contratos Aguard. Liquidação de Compra que devem ser concluídos (junto com os aguard. confirmação).
            ListaComprasParaConclusaoQuery query1 = new ListaComprasParaConclusaoQuery();
            List<TransferObject> comprasConclusao = query1.executarDTO();
            if (comprasConclusao != null && comprasConclusao.size() > 0) {
                // Conjunto com o código dos contratos de destino que serão confirmados (ou cancelados)
                Set<String> adeCodigosDestino = new HashSet<>();
                // Obtém o código dos contratos destino: usa um Set para pegar os elementos distintos
                Iterator<TransferObject> it = comprasConclusao.iterator();
                while (it.hasNext()) {
                    TransferObject compra = it.next();
                    String adeCodigoDestino = compra.getAttribute(Columns.RAD_ADE_CODIGO_DESTINO).toString();
                    adeCodigosDestino.add(adeCodigoDestino);
                }

                // Recupera o parametro de sistema que informa se deve cancelar a compra em caso de conclusão do contrato comprado
                boolean cancelarCompraNaConclusao = ParamSist.paramEquals(CodedValues.TPC_CANCELA_COMPRA_CONCLUSAO_COMPRADO, CodedValues.TPC_SIM, responsavel);
                List<String> adeCodigosCancelamento = null;
                if (cancelarCompraNaConclusao) {
                    // Dos comprados que serão concluídos, verifica quais compras são passíveis de cancelamento,
                    // ou seja, aqueles que não tem pagamento ou liquidação efetuada. Efetua a pesquisa antes
                    // da conclusão pois o processo de conclusão atualiza o status do relacionamento para 'Liquidado'.
                    ListaCompraPassivelCancelamentoQuery query2 = new ListaCompraPassivelCancelamentoQuery();
                    query2.adeCodigos = adeCodigosDestino;
                    adeCodigosCancelamento = query2.executarLista();
                }

                // Realiza a conclusão dos contratos de origem, atualiza o relacionamento de compra
                // afetando o status (Liquidado) e a data de referência.
                it = comprasConclusao.iterator();
                while (it.hasNext()) {
                    TransferObject compra = it.next();
                    String adeCodigoOrigem = compra.getAttribute(Columns.RAD_ADE_CODIGO_ORIGEM).toString();
                    concluirContratoOrigemCompra(adeCodigoOrigem, responsavel);
                }

                // Executa o cancelamento dos processos de compra que são passíveis de cancelamento,
                // caso o parâmetro de sistema esteja configurado para tal.
                if (adeCodigosCancelamento != null) {
                    cancelarProcessosCompra(adeCodigosCancelamento, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.cancelamento.automatico.conclusao.contrato.comprado", responsavel), responsavel);
                }

                // Realiza a finalização dos demais processos de compra onde os contratos
                // de origem já se encontram todos liquidados (ou concluídos). É necessário mesmo
                // que o parâmetro informe que as compras devem ser canceladas, pois compras que já
                // tiveram pagamento ou liquidação só podem ser canceladas pelo comprador.
                ListaCompraPassivelFinalizacaoQuery query3 = new ListaCompraPassivelFinalizacaoQuery();
                query3.adeCodigos = adeCodigosDestino;
                List<String> comprasFinalizacao = query3.executarLista();
                Iterator<String> itCodigos = comprasFinalizacao.iterator();
                while (itCodigos.hasNext()) {
                    String adeCodigoDestino = itCodigos.next();
                    finalizarCompra(adeCodigoDestino, responsavel);
                }
            }

            LOG.debug("FIM - CONCLUSÃO DE CONTRATOS AGUARDANDO LIQUIDAÇÃO DE COMPRA");
            LOG.debug("*************************************************************************************");
        } catch (Exception ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CompraContratoControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstHistoricoSuspensoesLiquidacoesAntecipadas(String rseCodigo, String nseCodigo, int countLiquidacoes, int countSuspensoes, AcessoSistema responsavel) throws CompraContratoControllerException {
        List<TransferObject> retorno = new ArrayList<>();

        List<String> tocCodigos = new ArrayList<>();
        List<String> sadCodigos = new ArrayList<>();

        List<TransferObject> liquidacoes = null;
        if (countLiquidacoes > 0) {
            tocCodigos.clear();
            tocCodigos.add(CodedValues.TOC_TARIF_LIQUIDACAO);

            sadCodigos.clear();
            sadCodigos.add(CodedValues.SAD_LIQUIDADA);

            liquidacoes = lstHistoricoConsignacao(rseCodigo, nseCodigo, sadCodigos, tocCodigos, countLiquidacoes, responsavel);
        }

        List<TransferObject> suspensoes = null;
        if (countSuspensoes > 0) {
            tocCodigos.clear();
            tocCodigos.add(CodedValues.TOC_SUSPENSAO_CONTRATO);

            sadCodigos.clear();
            sadCodigos.add(CodedValues.SAD_SUSPENSA);
            sadCodigos.add(CodedValues.SAD_SUSPENSA_CSE);

            suspensoes = lstHistoricoConsignacao(rseCodigo, nseCodigo, sadCodigos, tocCodigos, countSuspensoes, responsavel);
        }

        if (liquidacoes != null && !liquidacoes.isEmpty()) {
            retorno.addAll(liquidacoes);
        }
        if (suspensoes != null && !suspensoes.isEmpty()) {
            retorno.addAll(suspensoes);
        }

        Collections.sort(retorno, (c1, c2) -> {
            Timestamp data1 = (Timestamp) c1.getAttribute(Columns.OCA_DATA);
            Timestamp data2 = (Timestamp) c2.getAttribute(Columns.OCA_DATA);
            return data2.compareTo(data1);
        });

        return retorno;
    }

    private List<TransferObject> lstHistoricoConsignacao(String rseCodigo, String nseCodigo, List<String> sadCodigos, List<String> tocCodigos, int count, AcessoSistema responsavel) throws CompraContratoControllerException {
        return lstHistoricoConsignacao(rseCodigo, nseCodigo, sadCodigos, tocCodigos, count, false, responsavel);
    }

    /**
     * listas os contratos de um servidor cuja natureza do serviço é a mesma do serviço, status e tocCodigos dados.
     * @param rseCodigo
     * @param nseCodigo
     * @param sadCodigos
     * @param tocCodigos
     * @param count
     * @param somenteValorReduzido Lista apenas contratos com valores reduzidos ade_vlr_folha < ade_vlr
     * @param responsavel
     * @return
     * @throws CompraContratoControllerException
     */
    @Override
    public List<TransferObject> lstHistoricoConsignacao(String rseCodigo, String nseCodigo, List<String> sadCodigos, List<String> tocCodigos, int count, boolean somenteValorReduzido, AcessoSistema responsavel) throws CompraContratoControllerException {
        try {
            ListaConsignacaoRseNseQuery lstHistoricoQry = new ListaConsignacaoRseNseQuery();

            lstHistoricoQry.rseCodigo = rseCodigo;
            lstHistoricoQry.nseCodigo = nseCodigo;
            lstHistoricoQry.sadCodigos = sadCodigos;
            lstHistoricoQry.tocCodigos = tocCodigos;
            lstHistoricoQry.somenteValorReduzido = somenteValorReduzido;
            if (count > 0) {
                lstHistoricoQry.maxResults = count;
            }
            return lstHistoricoQry.executarDTO();
        } catch (HQueryException ex) {
            throw new CompraContratoControllerException(ex);
        }
    }

    /**
     * Conclusão do contrato de origem da compra: atualiza apenas o status para SAD_CONCLUIDO.
     * Cria ocorrência de alteração de status: de SAD_AGUARD_LIQUI_COMPRA para SAD_CONCLUIDO.
     * Atualiza o relacionamento de compra: Status LIQUIDADO pois não existe um status específico para conclusão
     * e para efeito do andamento da compra o contrato é considerado como liquidado.
     * @param adeCodigoOrigem
     * @param responsavel
     * @throws CompraContratoControllerException
     */
    private void concluirContratoOrigemCompra(String adeCodigoOrigem, AcessoSistema responsavel) throws CompraContratoControllerException {
        try {
            AutDesconto adeOrigem = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigoOrigem);
            adeOrigem.setStatusAutorizacaoDesconto(StatusAutorizacaoDescontoHome.findByPrimaryKey(CodedValues.SAD_CONCLUIDO));
            AutDescontoHome.update(adeOrigem);

            criaOcorrenciaADE(adeCodigoOrigem, CodedValues.TOC_CONCLUSAO_CONTRATO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status", responsavel, CodedValues.SAD_AGUARD_LIQUI_COMPRA, CodedValues.SAD_CONCLUIDO), responsavel);

            updateRelAutorizacaoCompra(adeCodigoOrigem, OperacaoCompraEnum.CONCLUSAO_CONTRATO, responsavel);
        } catch (com.zetra.econsig.exception.FindException ex) {
            throw new CompraContratoControllerException("mensagem.erro.recuperar.contrato.origem.compra.conclusao", responsavel, ex);
        } catch (com.zetra.econsig.exception.UpdateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new CompraContratoControllerException("mensagem.erro.atualizar.status.contrato.compra.concluido", responsavel, ex);
        } catch (AutorizacaoControllerException ex) {
            throw new CompraContratoControllerException("mensagem.erro.criar.ocorrencia.conclusao.contrato.compra", responsavel, ex);
        }
    }

    /**
     * Efetua a liquidação das consignações na situação Aguard. Liquidação de Compra não liquidadas a mais do que
     * X dias, conforme parâmetro passado ao método.
     * @param diasLiqAutomatica
     * @param responsavel
     * @throws CompraContratoControllerException
     */
    @Override
    public void liquidarAdeCompraNaoLiquidada(int diasLiqAutomatica, AcessoSistema responsavel) throws CompraContratoControllerException {
        try {
            ListaConsignacaoLiquidacaoAutomaticaQuery query = new ListaConsignacaoLiquidacaoAutomaticaQuery(diasLiqAutomatica);
            List<String> adeCodigosLiq = query.executarLista();
            if (adeCodigosLiq != null && !adeCodigosLiq.isEmpty()) {

                for (String adeCodigo : adeCodigosLiq) {
                    liquidarController.liquidar(adeCodigo, null, null, responsavel);
                }
            }
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new CompraContratoControllerException(ex);
        } catch (AutorizacaoControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new CompraContratoControllerException(ex);
        }
    }

    /**
     * Recupera email do CSA/COR de destino no processo de compra de contrato
     * @param adeCodigo
     * @param csaCodigo
     * @param responsavel
     * @return
     * @throws CompraContratoControllerException
     */
    @Override
    public String emailDestinatarioMsgCsaDestinoPortabilidade(String adeCodigo, String csaCodigo, AcessoSistema responsavel) throws CompraContratoControllerException {
        try {
            ObtemEmailDestinatarioMensagemCompraQuery query = new ObtemEmailDestinatarioMensagemCompraQuery();
            query.adeCodigoOrigem = adeCodigo;
            query.csaCodigoRemetente = csaCodigo;
            List<String> resultado = query.executarLista();
            String destinatario = null;
            if (resultado != null) {
                destinatario = resultado.get(0);
            }
            return destinatario;
        } catch (HQueryException ex) {
            throw new CompraContratoControllerException(ex);
        }
    }

    /**
     *
     */
    @Override
    public void enviarMsgCsaPortabilidade(UploadHelper uploadHelper, AcessoSistema responsavel) throws CompraContratoControllerException {
        try {
            String adeCodigo = uploadHelper.getValorCampoFormulario("adeCodigo");
            String mensagem = uploadHelper.getValorCampoFormulario("mensagem");
            String absolutePath = ParamSist.getDiretorioRaizArquivos();

            CustomTransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);

            //Grava a ocorrência na tb_ocorrencia_autorizacao.
            autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_MENSAGEM_CSA_PORTABILIDADE, mensagem, responsavel);

            //Grava o registro na tb_log.
            LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.CREATE, Log.LOG_INFORMACAO);
            log.setAutorizacaoDesconto(adeCodigo);
            log.setFuncao(responsavel.getFunCodigo());
            log.write();

            String path = "anexo" + File.separatorChar + DateHelper.format((Date) autdes.getAttribute(Columns.ADE_DATA), "yyyyMMdd") + File.separatorChar + adeCodigo;
            List<String> arquivosAnexoEmail = new ArrayList<>();

            File anexo = null;
            try {
                if (responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO)) {
                    anexo = uploadHelper.salvarArquivo(path, UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO);
                }
            } catch (ZetraException ex) {
                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                LOG.error(ex.getMessage(), ex);
                throw new CompraContratoControllerException(ex);
            }

            if (anexo != null && anexo.exists()) {
                Date aadPeriodo = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI);

                editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigo, anexo.getName(), anexo.getName(), aadPeriodo != null ? new java.sql.Date(aadPeriodo.getTime()) : null, TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_DOC_ADICIONAL_COMPRA, responsavel);

                arquivosAnexoEmail.add(absolutePath + File.separatorChar + path + File.separatorChar + anexo.getName());
            }

            String csaCodigo = responsavel.getCodigoEntidade();

            if (responsavel.isCor()) {
                csaCodigo = responsavel.getCodigoEntidadePai();
            }

            String destinatario = emailDestinatarioMsgCsaDestinoPortabilidade(adeCodigo, csaCodigo, responsavel);
            if (TextHelper.isNull(destinatario)) {
                throw new CompraContratoControllerException("mensagem.erro.nenhum.destinatario.email.cadastrado", responsavel);
            }
            EnviaEmailHelper.enviarEmailMensagemCsaCorPortabilidade(mensagem, destinatario, (arquivosAnexoEmail != null && !arquivosAnexoEmail.isEmpty() ? arquivosAnexoEmail : null), responsavel);
        } catch (LogControllerException | CompraContratoControllerException | AutorizacaoControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new CompraContratoControllerException(ex);
        }

    }

}
