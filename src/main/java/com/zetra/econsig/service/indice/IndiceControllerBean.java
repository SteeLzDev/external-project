package com.zetra.econsig.service.indice;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.IndiceControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Indice;
import com.zetra.econsig.persistence.entity.IndiceHome;
import com.zetra.econsig.persistence.entity.IndiceId;
import com.zetra.econsig.persistence.query.indice.ListaIndiceQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: IndiceControllerBean</p>
 * <p>Description: Session Façade para manipulação de indices.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class IndiceControllerBean implements IndiceController{
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(IndiceControllerBean.class);

    // Obtem a lista de indices utilizados pela folha de pagamentos para diferenciar contratos de uma
    // mesma verba para o mesmo servidor cadastrados pela csa ou cse
    @Override
    public List<TransferObject> selectIndices(int size, int offset,  CustomTransferObject criterio, AcessoSistema responsavel) throws IndiceControllerException {
        try {
            ListaIndiceQuery query = new ListaIndiceQuery();

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (size != -1) {
                query.maxResults = size;
            }

            if (criterio != null) {
                query.svcCodigo = (String) criterio.getAttribute(Columns.IND_SVC_CODIGO);
                query.csaCodigo = (String) criterio.getAttribute(Columns.IND_CSA_CODIGO);
                query.indCodigo = (String) criterio.getAttribute(Columns.IND_CODIGO);
                query.indDescricao = (String) criterio.getAttribute(Columns.IND_DESCRICAO);
            }

            return query.executarDTO();

        } catch (DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new IndiceControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int countIndices(CustomTransferObject criterio, AcessoSistema responsavel) throws IndiceControllerException {
        try {
            ListaIndiceQuery query = new ListaIndiceQuery();
            query.count = true;

            if (criterio != null) {
                query.svcCodigo = (String) criterio.getAttribute(Columns.IND_SVC_CODIGO);
                query.csaCodigo = (String) criterio.getAttribute(Columns.IND_CSA_CODIGO);
                query.indCodigo = (String) criterio.getAttribute(Columns.IND_CODIGO);
                query.indDescricao = (String) criterio.getAttribute(Columns.IND_DESCRICAO);
            }

            return query.executarContador();

        } catch (DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new IndiceControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void removeIndice(CustomTransferObject criterio, AcessoSistema responsavel) throws IndiceControllerException {
        String indCodigo = criterio.getAttribute(Columns.IND_CODIGO) != null ? criterio.getAttribute(Columns.IND_CODIGO).toString() : null;
        try {
            IndiceId id = new IndiceId ((String) criterio.getAttribute(Columns.IND_SVC_CODIGO),
                    (String) criterio.getAttribute(Columns.IND_CSA_CODIGO), indCodigo);
            Indice indice = IndiceHome.findByPrimaryKey(id);
            IndiceHome.remove(indice);

            LogDelegate log = new LogDelegate(responsavel, Log.INDICE, Log.DELETE, Log.LOG_INFORMACAO);
            log.setIndice(indCodigo);
            log.write();

        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new IndiceControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new IndiceControllerException(ex);
        } catch (RemoveException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new IndiceControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void createIndice(CustomTransferObject novoIndice, AcessoSistema responsavel) throws IndiceControllerException {
        String indCodigo = novoIndice.getAttribute(Columns.IND_CODIGO) != null ? novoIndice.getAttribute(Columns.IND_CODIGO).toString() : null;
        try {
            IndiceHome.create((String) novoIndice.getAttribute(Columns.IND_SVC_CODIGO),
                    (String) novoIndice.getAttribute(Columns.IND_CSA_CODIGO), indCodigo,
                    (String) novoIndice.getAttribute(Columns.IND_DESCRICAO));

            LogDelegate log = new LogDelegate(responsavel, Log.INDICE, Log.CREATE, Log.LOG_INFORMACAO);
            log.setIndice(indCodigo);
            log.setServico((String) novoIndice.getAttribute(Columns.IND_SVC_CODIGO));
            log.setConsignataria((String) novoIndice.getAttribute(Columns.IND_CSA_CODIGO));
            log.getUpdatedFields(novoIndice.getAtributos(), null);
            log.write();

        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new IndiceControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new IndiceControllerException("mensagem.erro.nao.possivel.inserir.novo.indice.pois.este.ja.existe", responsavel, ex);
        }
    }

    @Override
    public void updateIndice(CustomTransferObject novoIndice, CustomTransferObject anteriorIndice, AcessoSistema responsavel) throws IndiceControllerException {
        try {
            IndiceId id = new IndiceId ((String) anteriorIndice.getAttribute(Columns.IND_SVC_CODIGO),
                    (String) anteriorIndice.getAttribute(Columns.IND_CSA_CODIGO),
                    (String) anteriorIndice.getAttribute(Columns.IND_CODIGO));
            Indice indice = IndiceHome.findByPrimaryKey(id);

            String indCodigoNovo = (String) novoIndice.getAttribute(Columns.IND_CODIGO);
            String indDescricaoNovo = (String) novoIndice.getAttribute(Columns.IND_DESCRICAO);

            LogDelegate log = new LogDelegate(responsavel, Log.INDICE, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setIndice(indCodigoNovo);

            if (!TextHelper.isNull(indCodigoNovo)) {
                IndiceId idNovo = new IndiceId ((String) anteriorIndice.getAttribute(Columns.IND_SVC_CODIGO),
                        (String) anteriorIndice.getAttribute(Columns.IND_CSA_CODIGO), indCodigoNovo);
                Indice indiceNovo = null;
                if (!idNovo.equals(id)) {
                    try {
                        indiceNovo = IndiceHome.findByPrimaryKey(idNovo);
                    } catch (FindException e) {}
                }
                if (indiceNovo != null) {
                    throw new IndiceControllerException("mensagem.erro.nao.possivel.editar.indice.pois.existe.outro.cadastrado.mesmo.codigo", responsavel);

                } else {
                    IndiceHome.remove(indice);
                    indice.setId(idNovo);
                    if (!TextHelper.isNull(indDescricaoNovo)) {
                        indice.setIndDescricao(indDescricaoNovo);
                    }
                    IndiceHome.create(idNovo.getSvcCodigo(), idNovo.getCsaCodigo(), idNovo.getIndCodigo(), indice.getIndDescricao());
                    if (!TextHelper.isNull(idNovo.getSvcCodigo())) {
                        log.setServico(idNovo.getSvcCodigo());
                    }
                    if (!TextHelper.isNull(idNovo.getCsaCodigo())) {
                        log.setConsignataria(idNovo.getCsaCodigo());
                    }
                }

            } else {
                if (!TextHelper.isNull(indDescricaoNovo)) {
                    indice.setIndDescricao(indDescricaoNovo);
                }
                IndiceHome.update(indice);
            }

            log.getUpdatedFields(novoIndice.getAtributos(), anteriorIndice.getAtributos());
            log.write();

        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new IndiceControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new IndiceControllerException(ex);
        } catch (RemoveException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new IndiceControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (com.zetra.econsig.exception.CreateException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new IndiceControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new IndiceControllerException("mensagem.erro.nao.possivel.editar.indice.pois.existe.outro.cadastrado.mesmo.codigo", responsavel, ex);
        }
    }
}
