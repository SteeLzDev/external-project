package com.zetra.econsig.service.faq;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.AvaliacaoFaqTO;
import com.zetra.econsig.exception.AvaliacaoFaqControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FaqControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AvaliacaoFaq;
import com.zetra.econsig.persistence.entity.AvaliacaoFaqHome;
import com.zetra.econsig.persistence.query.faq.PesquisaFaqQuery;

/**
 * <p>Title: AvaliacaoFaqControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class AvaliacaoFaqControllerBean implements AvaliacaoFaqController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AvaliacaoFaqControllerBean.class);

    @Override
    public String createAvaliacaoFaq(AvaliacaoFaqTO avaliacaoFaqTO, AcessoSistema responsavel) throws AvaliacaoFaqControllerException {
        try {
            AvaliacaoFaq avaliacaoFaqBean = AvaliacaoFaqHome.create(avaliacaoFaqTO.getUsuCodigo(), avaliacaoFaqTO.getFaqCodigo(), avaliacaoFaqTO.getAvfNota(), avaliacaoFaqTO.getAvfData(), avaliacaoFaqTO.getAvfComentario());

            LogDelegate logDelegate = new LogDelegate(responsavel, Log.MENSAGEM, Log.CREATE, Log.LOG_INFORMACAO);
            logDelegate.setFaq(avaliacaoFaqBean.getAvfCodigo());
            logDelegate.write();

            return avaliacaoFaqBean.getAvfCodigo();

        } catch (CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new AvaliacaoFaqControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AvaliacaoFaqControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public AvaliacaoFaqTO findAvaliacaoFaq(AvaliacaoFaqTO avaliacaoFaq, AcessoSistema responsavel) throws AvaliacaoFaqControllerException {
        return setAvaliacaoFaqValues(findAvaliacaoFaqBean(avaliacaoFaq));
    }

    @Override
    public void updateAvaliacaoFaq(AvaliacaoFaqTO avaliacaofaqTO, AcessoSistema responsavel) throws AvaliacaoFaqControllerException {
        try {
            AvaliacaoFaq avaliacaoFaqBean = findAvaliacaoFaqBean(avaliacaofaqTO);
            LogDelegate log = new LogDelegate(responsavel, Log.MENSAGEM, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setFaq(avaliacaoFaqBean.getFaq().getFaqCodigo());
            if (!TextHelper.isNull(avaliacaofaqTO.getUsuCodigo())) {
                log.setUsuario(avaliacaofaqTO.getUsuCodigo());
                log.setUsuario(avaliacaofaqTO.getAvfCodigo());
            }

            avaliacaoFaqBean.setAvfComentario(avaliacaofaqTO.getAvfComentario());
            avaliacaoFaqBean.setAvfNota(avaliacaofaqTO.getAvfNota());

            AvaliacaoFaqHome.update(avaliacaoFaqBean);

            log.write();

        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AvaliacaoFaqControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new AvaliacaoFaqControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private AvaliacaoFaq findAvaliacaoFaqBean(AvaliacaoFaqTO avaliacaoFaq) throws AvaliacaoFaqControllerException {
        AvaliacaoFaq avaliacaoFaqBean = null;

        if (!TextHelper.isNull(avaliacaoFaq.getAvfCodigo())) {
            try {
                avaliacaoFaqBean = AvaliacaoFaqHome.findByPrimaryKey(avaliacaoFaq.getAvfCodigo());
            } catch (FindException ex) {
                throw new AvaliacaoFaqControllerException("mensagem.erro.nenhum.registro.encontrado", (AcessoSistema) null);
            }
        }

        return avaliacaoFaqBean;
    }

    private AvaliacaoFaqTO setAvaliacaoFaqValues(AvaliacaoFaq avaliacaoFaqBean) {
        if (avaliacaoFaqBean == null) {
            return null;
        }

        AvaliacaoFaqTO avaliacaoFaq = new AvaliacaoFaqTO(avaliacaoFaqBean.getAvfCodigo());

        avaliacaoFaq.setAvfCodigo(avaliacaoFaqBean.getAvfCodigo() != null ? avaliacaoFaqBean.getAvfCodigo() : null);
        avaliacaoFaq.setUsuCodigo(avaliacaoFaqBean.getUsuario().getUsuCodigo());
        avaliacaoFaq.setAvfFaqCodigo(avaliacaoFaqBean.getFaq().getFaqCodigo());
        avaliacaoFaq.setAvfNota(avaliacaoFaqBean.getAvfNota());
        avaliacaoFaq.setAvfData(avaliacaoFaqBean.getAvfData());
        avaliacaoFaq.setAvfComentario(avaliacaoFaqBean.getAvfComentario());

        return avaliacaoFaq;
    }

    @Override
    public List<TransferObject> pesquisaAvaliacaoFaq(String pesquisa, AcessoSistema responsavel, int rows) throws AvaliacaoFaqControllerException, FaqControllerException {
        try {
            PesquisaFaqQuery query = new PesquisaFaqQuery();
            query.responsavel = responsavel;
            query.pesquisa = pesquisa;
            query.usuCodigo = responsavel.getUsuCodigo();

            if (rows > 0) {
                query.firstResult = 0;
                query.maxResults = rows;
            }

            List<TransferObject> retorno = query.executarDTO();

            return retorno;

        } catch (HQueryException ex) {
            throw new FaqControllerException(ex);
        }
    }
}
