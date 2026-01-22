package com.zetra.econsig.service.sistema;

import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.TipoPenalidade;
import com.zetra.econsig.persistence.entity.TipoPenalidadeHome;
import com.zetra.econsig.persistence.query.penalidade.ListaTipoPenalidadeQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: PenalidadeControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class PenalidadeControllerBean implements PenalidadeController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(PenalidadeControllerBean.class);

    @Override
    public String insereTipoPenalidade(String descricao, Short prazo, AcessoSistema responsavel) throws ConsignanteControllerException {
        String codigo = null;
        try {
            codigo = TipoPenalidadeHome.create(descricao, prazo);

            LogDelegate log = new LogDelegate(responsavel, Log.TIPO_PENALIDADE, Log.CREATE, Log.LOG_INFORMACAO);
            log.setTipoPenalidade(codigo);
            log.addChangedField(Columns.TPE_DESCRICAO, descricao);
            log.write();

        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (com.zetra.econsig.exception.CreateException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ConsignanteControllerException(e);
        }

        return codigo;
    }

    @Override
    public void alteraTipoPenalidade(String codigo, String descricao, Short prazo, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            TipoPenalidade tipoPenalidade = new TipoPenalidade();
            tipoPenalidade.setTpeCodigo(codigo);
            tipoPenalidade.setTpeDescricao(descricao);
            if (!TextHelper.isNull(prazo)) {
                tipoPenalidade.setTpePrazoPenalidade(prazo);
            }
            TipoPenalidadeHome.update(tipoPenalidade);

            LogDelegate log = new LogDelegate(responsavel, Log.TIPO_PENALIDADE, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setTipoPenalidade(codigo);
            log.addChangedField(Columns.TPE_DESCRICAO, descricao);
            log.write();
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (UpdateException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ConsignanteControllerException(e);
        }
    }

    @Override
    public void excluiTipoPenalidade(String codigo, String descricao, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            TipoPenalidade tipoPenalidade = new TipoPenalidade();
            tipoPenalidade.setTpeCodigo(codigo);
            tipoPenalidade.setTpeDescricao(descricao);

            TipoPenalidadeHome.remove(tipoPenalidade);

            LogDelegate log = new LogDelegate(responsavel, Log.TIPO_PENALIDADE, Log.DELETE, Log.LOG_INFORMACAO);
            log.setTipoPenalidade(codigo);
            log.write();
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (com.zetra.econsig.exception.RemoveException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ConsignanteControllerException(e);
        }
    }

    @Override
    public List<TransferObject> lstTiposPenalidade(AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            ListaTipoPenalidadeQuery query = new ListaTipoPenalidadeQuery();
            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.impossivel.listar.penalidade", responsavel, ex);
        }
    }

    @Override
    public TransferObject findTipoPenalidade(String codigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        TransferObject cto = null;
        try {
            ListaTipoPenalidadeQuery query = new ListaTipoPenalidadeQuery(codigo);
            List<TransferObject> lista = query.executarDTO();
            Iterator<TransferObject> it = lista.iterator();
            if (it.hasNext()) {
                cto = it.next();
            }

        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.impossivel.recuperar.penalidade", responsavel, ex);
        }
        return cto;
    }
}
