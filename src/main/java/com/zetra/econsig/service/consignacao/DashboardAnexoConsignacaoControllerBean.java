package com.zetra.econsig.service.consignacao;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.anexo.ListaConsignacaoPendenciaAnexoQuery;
import com.zetra.econsig.persistence.query.anexo.ListaConsignatariasPendenciaAnexoConsignacaoQuery;

/**
 * <p>Title: DashboardAnexoConsignacaoControllerBean</p>
 * <p>Description: Session Bean para operação de dashborad anexo de consignação.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class DashboardAnexoConsignacaoControllerBean implements DashboardAnexoConsignacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DashboardAnexoConsignacaoControllerBean.class);

    @Override
    public int countCsasPendenciaAnexo(CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            ListaConsignatariasPendenciaAnexoConsignacaoQuery query = new ListaConsignatariasPendenciaAnexoConsignacaoQuery(responsavel);
            query.count = true;
            query.setCriterios(criterio);
            return query.executarContador();
        } catch (HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<TransferObject> listCsasPendenciaAnexo(CustomTransferObject criterio, int offset, int count, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            ListaConsignatariasPendenciaAnexoConsignacaoQuery query = new ListaConsignatariasPendenciaAnexoConsignacaoQuery(responsavel);
            query.setCriterios(criterio);
            if (count != -1) {
                query.maxResults = count;
                query.firstResult = offset;
            }
            return query.executarDTO();
        } catch (HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }


    @Override
    public int countConsignacaoComAnexo(CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return countConsignacaoAnexo(criterio, false, responsavel);
    }

    @Override
    public int countConsignacaoSemAnexo(CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return countConsignacaoAnexo(criterio, true, responsavel);
    }

    private int countConsignacaoAnexo(CustomTransferObject criterio, boolean pendente, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            ListaConsignacaoPendenciaAnexoQuery query = new ListaConsignacaoPendenciaAnexoQuery(responsavel);
            query.count = true;
            query.pendenciaAnexo = pendente;
            query.setCriterios(criterio);
            return query.executarContador();
        } catch (HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<TransferObject> listConsignacaoComAnexo(CustomTransferObject criterio, int offset, int count, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return listConsignacaoAnexo(criterio, false, offset, count, responsavel);
    }

    @Override
    public List<TransferObject> listConsignacaoSemAnexo(CustomTransferObject criterio, int offset, int count, AcessoSistema responsavel) throws AutorizacaoControllerException {
        return listConsignacaoAnexo(criterio, true, offset, count, responsavel);
    }

    private List<TransferObject> listConsignacaoAnexo(CustomTransferObject criterio, boolean pendente, int offset, int count, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            ListaConsignacaoPendenciaAnexoQuery query = new ListaConsignacaoPendenciaAnexoQuery(responsavel);
            query.pendenciaAnexo = pendente;
            query.setCriterios(criterio);
            if (count != -1) {
                query.maxResults = count;
                query.firstResult = offset;
            }
            return query.executarDTO();
        } catch (HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

}
