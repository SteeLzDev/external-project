package com.zetra.econsig.service.sdp;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.persistence.entity.*;
import com.zetra.econsig.persistence.query.posto.ListaValorFixoCsaSvcQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.DespesaIndividualControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.PostoRegistroServidorControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.admin.ListaPostoRegistroServidorQuery;
import com.zetra.econsig.persistence.query.posto.ListaBloqueioPostoCsaSvcQuery;
import com.zetra.econsig.persistence.query.sdp.despesaindividual.ListaDespesaTaxaUsoAtualizacaoQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: PostoRegistroServidorControllerBean</p>
 * <p>Description: Session Façade para operações de manutenção do posto.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class PostoRegistroServidorControllerBean implements PostoRegistroServidorController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(PostoRegistroServidorControllerBean.class);

    @Autowired
    private DespesaIndividualController despesaIndividualController;

    @Override
    public long countPostoRegistroServidor(TransferObject criterio, AcessoSistema responsavel) throws PostoRegistroServidorControllerException {
        try {
            ListaPostoRegistroServidorQuery query = new ListaPostoRegistroServidorQuery();
            query.count = true;
            return query.executarContador();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PostoRegistroServidorControllerException(ex);
        }
    }

    @Override
    public List<TransferObject> lstPostoRegistroServidor(TransferObject criterio, int offset, int count, AcessoSistema responsavel) throws PostoRegistroServidorControllerException {
        try {
            ListaPostoRegistroServidorQuery query = new ListaPostoRegistroServidorQuery();

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (count != -1) {
                query.maxResults = count;
            }

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PostoRegistroServidorControllerException(ex);
        }
    }

    @Override
    public TransferObject buscaPosto(String posCodigo, AcessoSistema responsavel) throws PostoRegistroServidorControllerException {
        try {
            if (TextHelper.isNull(posCodigo)) {
                throw new PostoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel);
            }
            ListaPostoRegistroServidorQuery query = new ListaPostoRegistroServidorQuery();
            query.posCodigo = posCodigo;

            TransferObject retorno = query.executarDTO().get(0);

            LogDelegate logDelegate = new LogDelegate(responsavel, Log.POSTO, Log.FIND, Log.LOG_INFORMACAO);
            logDelegate.setPosto(posCodigo);
            logDelegate.write();

            return retorno;

        } catch (HQueryException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PostoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private PostoRegistroServidor findPostoRegistroServidorBean(TransferObject posto, AcessoSistema responsavel) throws PostoRegistroServidorControllerException {
        PostoRegistroServidor postoBean = null;
        if (posto.getAttribute(Columns.POS_CODIGO) != null) {
            try {
                postoBean = PostoRegistroServidorHome.findByPrimaryKey((String) posto.getAttribute(Columns.POS_CODIGO));
            } catch (FindException ex) {
                throw new PostoRegistroServidorControllerException("mensagem.erro.posto.nao.encontrado", responsavel);
            }
        } else {
            throw new PostoRegistroServidorControllerException("mensagem.erro.posto.nao.encontrado", responsavel);
        }
        return postoBean;
    }

    private TransferObject setPostoRegistroServidorValues(PostoRegistroServidor postoBean) {
        TransferObject posto = new CustomTransferObject();

        posto.setAttribute(Columns.POS_CODIGO, postoBean.getPosCodigo());
        posto.setAttribute(Columns.POS_DESCRICAO, postoBean.getPosDescricao());
        posto.setAttribute(Columns.POS_IDENTIFICADOR, postoBean.getPosIdentificador());
        posto.setAttribute(Columns.POS_PERC_TAXA_USO, postoBean.getPosPercTxUso());
        posto.setAttribute(Columns.POS_PERC_TAXA_USO_COND, postoBean.getPosPercTxUsoCond());
        posto.setAttribute(Columns.POS_VALOR_SOLDO, postoBean.getPosVlrSoldo());

        return posto;
    }

    @Override
    public void updatePosto(TransferObject posto, AcessoSistema responsavel) throws PostoRegistroServidorControllerException {
        try {
            PostoRegistroServidor postoBean = findPostoRegistroServidorBean(posto, responsavel);
            String posCodigo = postoBean.getPosCodigo();

            // Calcula as taxas de uso antes da atualização
            BigDecimal vlrTaxaUsoAntes = postoBean.calcularVlrTaxaUso();
            BigDecimal vlrTaxaUsoCondAntes = postoBean.calcularVlrTaxaUsoCond();

            LogDelegate log = new LogDelegate(responsavel, Log.POSTO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setPosto(posCodigo);

            try {
                // Procurando por posto com mesmo Identificador
                ListaPostoRegistroServidorQuery query = new ListaPostoRegistroServidorQuery();
                query.posIdentificador = (String) posto.getAttribute(Columns.POS_IDENTIFICADOR);
                List<TransferObject> retorno = query.executarDTO();
                if (!retorno.isEmpty() && !(((String) posto.getAttribute(Columns.POS_CODIGO)).equalsIgnoreCase((String) retorno.get(0).getAttribute(Columns.POS_CODIGO)))) {
                    throw new PostoRegistroServidorControllerException("mensagem.erro.alterar.posto.duplicado", responsavel);
                }
            } catch (HQueryException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new PostoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }

            TransferObject postoCache = setPostoRegistroServidorValues(postoBean);
            TransferObject merge = log.getUpdatedFields(posto.getAtributos(), postoCache.getAtributos());

            if (merge.getAtributos().containsKey(Columns.POS_DESCRICAO)) {
                postoBean.setPosDescricao((String) posto.getAttribute(Columns.POS_DESCRICAO));
            }

            if (merge.getAtributos().containsKey(Columns.POS_IDENTIFICADOR)) {
                postoBean.setPosIdentificador((String) posto.getAttribute(Columns.POS_IDENTIFICADOR));
            }

            if (merge.getAtributos().containsKey(Columns.POS_PERC_TAXA_USO)) {
                postoBean.setPosPercTxUso((BigDecimal) posto.getAttribute(Columns.POS_PERC_TAXA_USO));
            }

            if (merge.getAtributos().containsKey(Columns.POS_PERC_TAXA_USO_COND)) {
                postoBean.setPosPercTxUsoCond((BigDecimal) posto.getAttribute(Columns.POS_PERC_TAXA_USO_COND));
            }

            if (merge.getAtributos().containsKey(Columns.POS_VALOR_SOLDO)) {
                postoBean.setPosVlrSoldo((BigDecimal) posto.getAttribute(Columns.POS_VALOR_SOLDO));
            }

            PostoRegistroServidorHome.update(postoBean);

            // Calcula as taxas de uso depois da atualização
            BigDecimal vlrTaxaUsoDepois = postoBean.calcularVlrTaxaUso();
            BigDecimal vlrTaxaUsoCondDepois = postoBean.calcularVlrTaxaUsoCond();

            if (vlrTaxaUsoAntes.compareTo(vlrTaxaUsoDepois) != 0) {
                // Taxa de uso foi alterada: as despesas de taxa de uso devem ser atualizadas
                atualizarDespesasTaxaUso(posCodigo, false, vlrTaxaUsoDepois, responsavel);
            }
            if (vlrTaxaUsoCondAntes.compareTo(vlrTaxaUsoCondDepois) != 0) {
                // Taxa de uso de condomínio foi alterada: as despesas de taxa de uso de condomínios devem ser atualizadas
                atualizarDespesasTaxaUso(posCodigo, true, vlrTaxaUsoCondDepois, responsavel);
            }

            log.write();

        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PostoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (UpdateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PostoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void atualizarDespesasTaxaUso(String posCodigo, boolean condominio, BigDecimal novoValor, AcessoSistema responsavel) throws PostoRegistroServidorControllerException {
        try {
            // Busca as despesas individuais de planos de taxa de uso ligados a permissionários
            // que possuem o mesmo posto que foi alterado.
            ListaDespesaTaxaUsoAtualizacaoQuery query = new ListaDespesaTaxaUsoAtualizacaoQuery();
            query.echCondominio = condominio;
            query.posCodigo = posCodigo;
            List<TransferObject> despesas = query.executarDTO();
            if (despesas != null && despesas.size() > 0) {
                // Obtém interface para executar as alterações de contratos

                Iterator<TransferObject> it = despesas.iterator();
                while (it.hasNext()) {
                    TransferObject despesa = it.next();
                    String adeCodigo = (String) despesa.getAttribute(Columns.ADE_CODIGO);
                    LOG.debug("Atualizando despesa individual de taxa de uso: " + adeCodigo);

                    // Executa a alteração do contrato
                    despesaIndividualController.alterarTaxaUso(adeCodigo, novoValor, responsavel);
                }
            }
        } catch (HQueryException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PostoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (DespesaIndividualControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PostoRegistroServidorControllerException("mensagem.erro.atualizar.taxa.uso.posto.alterado.arg0", responsavel, ex, ex.getMessage());
        }
    }

    @Override
    public List<TransferObject> lstBloqueioPostoPorCsaSvc(String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws PostoRegistroServidorControllerException {
        try {
            ListaBloqueioPostoCsaSvcQuery query = new ListaBloqueioPostoCsaSvcQuery();
            query.csaCodigo = csaCodigo;
            query.svcCodigo = svcCodigo;
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PostoRegistroServidorControllerException(ex);
        }
    }

    @Override
    public void salvarBloqueioPostoPorCsaSvc(String csaCodigo, String svcCodigo, Map<String, Boolean> bloqueiosSolicitacao, Map<String, Boolean> bloqueiosReserva, AcessoSistema responsavel) throws PostoRegistroServidorControllerException {
        try {
            List<TransferObject> lstPostos = lstBloqueioPostoPorCsaSvc(csaCodigo, svcCodigo, responsavel);
            for (TransferObject posto : lstPostos) {
                String posCodigo = posto.getAttribute(Columns.POS_CODIGO).toString();

                boolean bloqSolicitacaoAntigo = "S".equals(posto.getAttribute(Columns.BPC_BLOQ_SOLICITACAO));
                boolean bloqReservaAntigo = "S".equals(posto.getAttribute(Columns.BPC_BLOQ_RESERVA));

                boolean bloqSolicitacaoNovo = bloqueiosSolicitacao.containsKey(posCodigo) && bloqueiosSolicitacao.get(posCodigo);
                boolean bloqReservaNovo = bloqueiosReserva.containsKey(posCodigo) && bloqueiosReserva.get(posCodigo);

                // Se mudou, faz a persistência
                if (bloqSolicitacaoAntigo != bloqSolicitacaoNovo || bloqReservaAntigo != bloqReservaNovo) {
                    alterarBloqueioPostoPorCsaSvc(csaCodigo, svcCodigo, posCodigo, bloqSolicitacaoNovo, bloqReservaNovo, responsavel);
                }
            }
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PostoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (CreateException | UpdateException | RemoveException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new PostoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void alterarBloqueioPostoPorCsaSvc(String csaCodigo, String svcCodigo, String posCodigo, boolean bloqSolicitacao, boolean bloqReserva, AcessoSistema responsavel) throws CreateException, UpdateException, RemoveException, LogControllerException {
        String bpcBloqSolicitacaoNovo = bloqSolicitacao ? "S" : "N";
        String bpcBloqReservaNovo = bloqReserva ? "S" : "N";

        try {
            // Se existe, verifica se deve alterar ou excluir
            BloqueioPostoCsaSvc bloqueio = BloqueioPostoCsaSvcHome.findByPrimaryKey(csaCodigo, svcCodigo, posCodigo);

            if (!bloqSolicitacao && !bloqReserva) {
                // Se nenhum dos dois deve estar bloqueado e o registro existe, então exclui
                BloqueioPostoCsaSvcHome.remove(bloqueio);

                // Registra log de exclusão
                LogDelegate log = new LogDelegate(responsavel, Log.BLOQUEIO_POSTO_CSA_SVC, Log.DELETE, Log.LOG_INFORMACAO);
                log.setConsignataria(csaCodigo);
                log.setServico(svcCodigo);
                log.setPosto(posCodigo);
                log.write();

            } else {
                String bpcBloqSolicitacaoAntigo = bloqueio.getBpcBloqSolicitacao();
                String bpcBloqReservaAntigo = bloqueio.getBpcBloqReserva();

                bloqueio.setBpcBloqSolicitacao(bpcBloqSolicitacaoNovo);
                bloqueio.setBpcBloqReserva(bpcBloqReservaNovo);
                BloqueioPostoCsaSvcHome.update(bloqueio);

                // Registra log de alteração
                LogDelegate log = new LogDelegate(responsavel, Log.BLOQUEIO_POSTO_CSA_SVC, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setConsignataria(csaCodigo);
                log.setServico(svcCodigo);
                log.setPosto(posCodigo);

                if (!bpcBloqSolicitacaoAntigo.equals(bpcBloqSolicitacaoNovo)) {
                    log.addChangedField(Columns.BPC_BLOQ_SOLICITACAO, bpcBloqSolicitacaoNovo, bpcBloqSolicitacaoAntigo);
                }
                if (!bpcBloqReservaAntigo.equals(bpcBloqReservaNovo)) {
                    log.addChangedField(Columns.BPC_BLOQ_RESERVA, bpcBloqReservaNovo, bpcBloqReservaAntigo);
                }
                log.write();
            }

        } catch (FindException ex) {
            // Se não existe, cria
            BloqueioPostoCsaSvcHome.create(csaCodigo, svcCodigo, posCodigo, bpcBloqSolicitacaoNovo, bpcBloqReservaNovo);

            // Registra log de criação
            LogDelegate log = new LogDelegate(responsavel, Log.BLOQUEIO_POSTO_CSA_SVC, Log.CREATE, Log.LOG_INFORMACAO);
            log.setConsignataria(csaCodigo);
            log.setServico(svcCodigo);
            log.setPosto(posCodigo);
            log.addChangedField(Columns.BPC_BLOQ_SOLICITACAO, bpcBloqSolicitacaoNovo);
            log.addChangedField(Columns.BPC_BLOQ_RESERVA, bpcBloqReservaNovo);
            log.write();
        }
    }
    public TransferObject findValorFixoByCsaSvcPos(String svcCodigo, String csaCodigo, String posCodigo, AcessoSistema responsavel) throws PostoRegistroServidorControllerException {
        List<TransferObject> listPosVlrFixo;

        listPosVlrFixo = findValorFixoByCsaSvc(svcCodigo, csaCodigo, posCodigo, responsavel);

        if(!listPosVlrFixo.isEmpty()){
            return listPosVlrFixo.get(0);
        }

        return null;
    }

    public List<TransferObject> findValorFixoByCsaSvc(String svcCodigo, String csaCodigo, String posCodigo, AcessoSistema responsavel) throws PostoRegistroServidorControllerException {
        try {
            ListaValorFixoCsaSvcQuery query = new ListaValorFixoCsaSvcQuery();

            query.svcCodigo = svcCodigo;
            query.csaCodigo = csaCodigo;
            query.posCodigo = posCodigo;

            return query.executarDTO();

        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new PostoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

    }

    public void saveUpdateVlrFixoPosto(List<ParamPostoCsaSvc> postoCsaSvcList, String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws PostoRegistroServidorControllerException {
        try {
            for (ParamPostoCsaSvc paramPostoCsaSvc : postoCsaSvcList) {
                TransferObject postoVlr = findValorFixoByCsaSvcPos(svcCodigo, csaCodigo, paramPostoCsaSvc.getPosCodigo(), responsavel);
                if (TextHelper.isNull(postoVlr) && !paramPostoCsaSvc.getPpoVlr().isEmpty() && !paramPostoCsaSvc.getPpoVlr().equals("0")) {
                    String posCodigo = paramPostoCsaSvc.getPosCodigo();
                    String pcsVlr = paramPostoCsaSvc.getPpoVlr();
                    ParamPostoCsaSvcHome.create(csaCodigo, posCodigo, svcCodigo, pcsVlr);
                } else if (paramPostoCsaSvc.getPpoVlr().equals("0")) {
                    AbstractEntityHome.remove(paramPostoCsaSvc);
                } else if (!paramPostoCsaSvc.getPpoVlr().equals(postoVlr.getAttribute(Columns.PSP_PPO_VALOR))) {
                    AbstractEntityHome.update(paramPostoCsaSvc);
                }
            }

        } catch (CreateException | UpdateException | RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new RuntimeException(ex);
        }
    }
}
