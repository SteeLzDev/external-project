package com.zetra.econsig.service.sistema;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.dto.entidade.TermoAdesaoServicoTO;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.TermoAdesaoServicoControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.TermoAdesaoServico;
import com.zetra.econsig.persistence.entity.TermoAdesaoServicoHome;
import com.zetra.econsig.persistence.entity.TermoAdesaoServicoId;

/**
 * <p>Title: TermoAdesaoServicoControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class TermoAdesaoServicoControllerBean implements TermoAdesaoServicoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TermoAdesaoServicoControllerBean.class);

    @Override
    public void createTermoAdesaoServico(TermoAdesaoServicoTO termoAdesaoServico, AcessoSistema responsavel) throws TermoAdesaoServicoControllerException {
        try {
            TermoAdesaoServicoHome.create(termoAdesaoServico.getCsaCodigo(), termoAdesaoServico.getSvcCodigo(), termoAdesaoServico.getTasTexto());
        } catch (final com.zetra.econsig.exception.CreateException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new TermoAdesaoServicoControllerException("mensagem.erroInternoSistema", responsavel);
        }
    }

    @Override
    public TermoAdesaoServicoTO findTermoAdesaoServico(TermoAdesaoServicoTO termoAdesaoServico, AcessoSistema responsavel) throws TermoAdesaoServicoControllerException {
        return setTermoAdesaoValues(findTermoAdesaoServicoBean(termoAdesaoServico));
    }

    private TermoAdesaoServicoTO setTermoAdesaoValues(TermoAdesaoServico termoAdesaoServicoBean) {
        final TermoAdesaoServicoTO termoAdesaoServico = new TermoAdesaoServicoTO(termoAdesaoServicoBean.getCsaCodigo(), termoAdesaoServicoBean.getSvcCodigo(), termoAdesaoServicoBean.getTasTexto());
        return termoAdesaoServico;
    }

    private TermoAdesaoServico findTermoAdesaoServicoBean(TermoAdesaoServicoTO termoAdesaoServico) throws TermoAdesaoServicoControllerException {
        TermoAdesaoServico termoAdesaoServicoBean = null;
        if (termoAdesaoServico.getCsaCodigo() != null && termoAdesaoServico.getSvcCodigo() != null) {
            try {
                final TermoAdesaoServicoId pk = new TermoAdesaoServicoId(termoAdesaoServico.getCsaCodigo(), termoAdesaoServico.getSvcCodigo());
                termoAdesaoServicoBean = TermoAdesaoServicoHome.findByPrimaryKey(pk);
            } catch (final FindException e) {
                throw new TermoAdesaoServicoControllerException("mensagem.erro.nenhum.termo.adesao.encontrado", (AcessoSistema) null);
            }
        }
        return termoAdesaoServicoBean;
    }

    @Override
    public void removeTermoAdesaoServico(TermoAdesaoServicoTO termoAdesaoServico, AcessoSistema responsavel) throws TermoAdesaoServicoControllerException {
        try {
            final TermoAdesaoServico termoAdesaoServicoBean = findTermoAdesaoServicoBean(termoAdesaoServico);
            TermoAdesaoServicoHome.remove(termoAdesaoServicoBean);
        } catch (final com.zetra.econsig.exception.RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new TermoAdesaoServicoControllerException("mensagem.erro.nao.possivel.excluir.registro.desta.mensagem", responsavel);
        }
    }

    @Override
    public void updateTermoAdesaoServico(TermoAdesaoServicoTO termoAdesaoServico, AcessoSistema responsavel) throws TermoAdesaoServicoControllerException {
        try {
            final TermoAdesaoServico termoAdesaoServicoBean = findTermoAdesaoServicoBean(termoAdesaoServico);

            if (!termoAdesaoServicoBean.getTasTexto().equals(termoAdesaoServico.getTasTexto())) {
                termoAdesaoServicoBean.setTasTexto(termoAdesaoServico.getTasTexto());
            }
            TermoAdesaoServicoHome.update(termoAdesaoServicoBean);

        } catch (final UpdateException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new TermoAdesaoServicoControllerException(e);
        }
    }
}
