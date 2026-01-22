package com.zetra.econsig.service.consignacao;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.HistoricoStatusAde;
import com.zetra.econsig.persistence.entity.HistoricoStatusAdeHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoEncerramentoQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoReaberturaQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: EncerrarConsignacaoControllerBean</p>
 * <p>Description: Session Bean para a operação de Encerramento e Reabertura de Consignação via Carga de Margem.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class EncerrarConsignacaoControllerBean extends AutorizacaoControllerBean implements EncerrarConsignacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EncerrarConsignacaoControllerBean.class);

    @Autowired
    private CancelarConsignacaoController cancelarConsignacaoController;

    @Override
    public void encerrar(String rseCodigo, String motivoEncerramento, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            List<TransferObject> ades = new ListaConsignacaoEncerramentoQuery(rseCodigo).executarDTO();

            if (ades != null && !ades.isEmpty()) {
                String sadCodigoNovo = CodedValues.SAD_ENCERRADO;
                String ocaObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.encerramento.automatico.sem.motivo", responsavel);
                if (!TextHelper.isNull(motivoEncerramento)) {
                    ocaObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.encerramento.automatico.com.motivo", responsavel, motivoEncerramento);
                }

                for (TransferObject ade : ades) {
                    String adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
                    String sadCodigo = ade.getAttribute(Columns.SAD_CODIGO).toString();

                    if (usuarioPodeModificarAde(adeCodigo, responsavel)) {
                        if (sadCodigo.equals(CodedValues.SAD_SOLICITADO) || sadCodigo.equals(CodedValues.SAD_AGUARD_CONF) || sadCodigo.equals(CodedValues.SAD_AGUARD_DEFER)) {
                            // Se é uma reserva não confirmada, cancela
                            cancelarConsignacaoController.cancelar(adeCodigo, responsavel);

                        } else {
                            // Senão, encerra a consignação, caso já não esteja inativa
                            AutDesconto autdes = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);

                            // OBS: Compara com o status após o find e não com a variável sadCodigo, pois o status pode ter sido alterado
                            // após o cancelamento das reservas não confirmadas
                            if (!CodedValues.SAD_CODIGOS_INATIVOS.contains(autdes.getStatusAutorizacaoDesconto().getSadCodigo())) {
                                // Altera o status da consignação para encerrado. O método modificaSituacaoADE irá alterar o campo ADE_DATA_EXCLUSAO
                                modificaSituacaoADE(autdes, sadCodigoNovo, responsavel);

                                // Cria ocorrência com o motivo do encerramento
                                criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AVISO, ocaObs, responsavel);

                                // Gera o Log de auditoria
                                LogDelegate logDelegate = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.ENCERRAR_CONSIGNACAO, Log.LOG_INFORMACAO);
                                logDelegate.setAutorizacaoDesconto(adeCodigo);
                                logDelegate.setStatusAutorizacao(sadCodigoNovo);
                                logDelegate.write();
                            }
                        }
                    }
                }
            }
        } catch (HQueryException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException(ex);
        }
    }

    @Override
    public void reabrir(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            RegistroServidor rse = RegistroServidorHome.findByPrimaryKey(rseCodigo);
            Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(rse.getOrgCodigo(), responsavel);
            List<String> adeCodigos = new ListaConsignacaoReaberturaQuery(rseCodigo, periodoAtual).executarLista();
            if (adeCodigos != null && !adeCodigos.isEmpty()) {
                String ocaObs = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.reabertura.automatica", responsavel);

                for (String adeCodigo : adeCodigos) {
                    AutDesconto autdes = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);

                    // Busca pelo historico de status o último que muda a consignação para encerrada
                    HistoricoStatusAde historicoStatus = HistoricoStatusAdeHome.findLastByAdeCodigoSadCodigoNovo(adeCodigo, CodedValues.SAD_ENCERRADO);
                    if (historicoStatus != null) {
                        String sadCodigoNovo = historicoStatus.getSadCodigoAnterior();

                        // Altera o status da consignação para encerrado. O método modificaSituacaoADE irá alterar o campo ADE_DATA_EXCLUSAO
                        modificaSituacaoADE(autdes, sadCodigoNovo, responsavel);

                        // Cria ocorrência com o motivo do encerramento
                        criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AVISO, ocaObs, responsavel);

                        // Gera o Log de auditoria
                        LogDelegate logDelegate = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.REABRIR_CONSIGNACAO, Log.LOG_INFORMACAO);
                        logDelegate.setAutorizacaoDesconto(adeCodigo);
                        logDelegate.setStatusAutorizacao(sadCodigoNovo);
                        logDelegate.write();
                    } else {
                        LOG.warn("CONSIGNAÇÃO '" + adeCodigo + "' NÃO REABERTA POR AUSÊNCIA DO HISTÓRICO DE STATUS (tb_historico_status_ade).");
                    }
                }
            }
        } catch (HQueryException | FindException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException(ex);
        }
    }
}
