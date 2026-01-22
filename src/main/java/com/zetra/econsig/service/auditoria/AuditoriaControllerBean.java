package com.zetra.econsig.service.auditoria;

import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AuditoriaControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AuditoriaCor;
import com.zetra.econsig.persistence.entity.AuditoriaCorHome;
import com.zetra.econsig.persistence.entity.AuditoriaCsa;
import com.zetra.econsig.persistence.entity.AuditoriaCsaHome;
import com.zetra.econsig.persistence.entity.AuditoriaCse;
import com.zetra.econsig.persistence.entity.AuditoriaCseHome;
import com.zetra.econsig.persistence.entity.AuditoriaOrg;
import com.zetra.econsig.persistence.entity.AuditoriaOrgHome;
import com.zetra.econsig.persistence.entity.AuditoriaSup;
import com.zetra.econsig.persistence.entity.AuditoriaSupHome;
import com.zetra.econsig.persistence.entity.UsuarioHome;
import com.zetra.econsig.persistence.query.auditoria.ListaLogAuditoriaQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: AuditoriaControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class AuditoriaControllerBean implements AuditoriaController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AuditoriaControllerBean.class);

    @Override
    public int qtdeLogAuditoriaQuery(String codigoEntidade, String tipoEntidade, boolean naoAuditado, TransferObject criterios, AcessoSistema responsavel) throws AuditoriaControllerException {
        try {
            ListaLogAuditoriaQuery query = new ListaLogAuditoriaQuery();
            query.codigoEntidade = codigoEntidade;
            query.tipoEntidade = tipoEntidade;
            query.naoAuditado = naoAuditado;
            query.criterios = criterios;
            query.count = true;

            return query.executarContador();
        } catch (HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new AuditoriaControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public List<TransferObject> lstLogAuditoriaQuery(String codigoEntidade, String tipoEntidade, boolean naoAuditado, TransferObject criterios, AcessoSistema responsavel) throws AuditoriaControllerException {
        return lstLogAuditoriaQuery(codigoEntidade, tipoEntidade, naoAuditado, criterios, -1, -1, responsavel);
    }

    @Override
    public List<TransferObject> lstLogAuditoriaQuery(String codigoEntidade, String tipoEntidade, boolean naoAuditado, TransferObject criterios, int offset, int count, AcessoSistema responsavel) throws AuditoriaControllerException {
        try {
            ListaLogAuditoriaQuery query = new ListaLogAuditoriaQuery();
            query.codigoEntidade = codigoEntidade;
            query.tipoEntidade = tipoEntidade;
            query.naoAuditado = naoAuditado;
            query.criterios = criterios;
            query.count = false;

            if (count != -1) {
                query.maxResults = count;
                query.firstResult = offset;
            }

            return query.executarDTO();
        } catch (HQueryException e) {
            LOG.error(e.getMessage(), e);
            throw new AuditoriaControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public void auditarTodosLogs(String codigoEntidade, String tipoEntidade, TransferObject criterios, AcessoSistema responsavel) throws AuditoriaControllerException {
        // Busca todos os logs não auditados
        List<TransferObject> logsAuditoria =  lstLogAuditoriaQuery(codigoEntidade, tipoEntidade, true, criterios, responsavel);
        if (logsAuditoria != null && !logsAuditoria.isEmpty()) {
            // Audita os logs não auditados
            Iterator<TransferObject> iteLogsAuditoria = logsAuditoria.iterator();
            while (iteLogsAuditoria.hasNext()) {
                auditarLog(Integer.valueOf(iteLogsAuditoria.next().getAttribute("AUDITORIA_CODIGO").toString()), codigoEntidade, tipoEntidade, responsavel);
            }
        }
    }

    @Override
    public void auditarLog(List<Integer> audCodigos, String codigoEntidade, String tipoEntidade, AcessoSistema responsavel) throws AuditoriaControllerException {
        if (audCodigos == null || audCodigos.isEmpty()) {
            throw new AuditoriaControllerException("mensagem.erro.informacoes.obrigatorios.nao.informadas", responsavel);
        }

        for (Integer audCodigo : audCodigos) {
            auditarLog(audCodigo, codigoEntidade, tipoEntidade, responsavel);
        }
    }

    @Override
    public void auditarLog(Integer audCodigo, String codigoEntidade, String tipoEntidade, AcessoSistema responsavel) throws AuditoriaControllerException {
        try {
            if (TextHelper.isNull(audCodigo) || TextHelper.isNull(codigoEntidade) || TextHelper.isNull(tipoEntidade)) {
                throw new AuditoriaControllerException("mensagem.erro.informacoes.obrigatorios.nao.informadas", responsavel);
            }

            if (!responsavel.temPermissao(CodedValues.FUN_USUARIO_AUDITOR)) {
                throw new AuditoriaControllerException("mensagem.erro.usuario.sem.permissao.auditar.log", responsavel);
            }

            String auditado = CodedValues.TPC_SIM;

            if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSE)) {
                AuditoriaCse auditoria = AuditoriaCseHome.findByPrimaryKey(audCodigo);
                auditoria.setAceAuditado(auditado);
                auditoria.setUsuarioAuditor(UsuarioHome.findByPrimaryKey(responsavel.getUsuCodigo()));
                auditoria.setAceDataAuditoria(DateHelper.getSystemDatetime());

                AuditoriaCseHome.update(auditoria);
            } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_CSA)) {
                AuditoriaCsa auditoria = AuditoriaCsaHome.findByPrimaryKey(audCodigo);
                auditoria.setAcsAuditado(auditado);
                auditoria.setUsuarioAuditor(UsuarioHome.findByPrimaryKey(responsavel.getUsuCodigo()));
                auditoria.setAcsDataAuditoria(DateHelper.getSystemDatetime());

                AuditoriaCsaHome.update(auditoria);
            } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_COR)) {
                AuditoriaCor auditoria = AuditoriaCorHome.findByPrimaryKey(audCodigo);
                auditoria.setAcoAuditado(auditado);
                auditoria.setUsuarioAuditor(UsuarioHome.findByPrimaryKey(responsavel.getUsuCodigo()));
                auditoria.setAcoDataAuditoria(DateHelper.getSystemDatetime());

                AuditoriaCorHome.update(auditoria);
            } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_ORG)) {
                AuditoriaOrg auditoria = AuditoriaOrgHome.findByPrimaryKey(audCodigo);
                auditoria.setAorAuditado(auditado);
                auditoria.setUsuarioAuditor(UsuarioHome.findByPrimaryKey(responsavel.getUsuCodigo()));
                auditoria.setAorDataAuditoria(DateHelper.getSystemDatetime());

                AuditoriaOrgHome.update(auditoria);
            } else if (tipoEntidade.equals(AcessoSistema.ENTIDADE_SUP)) {
                AuditoriaSup auditoria = AuditoriaSupHome.findByPrimaryKey(audCodigo);
                auditoria.setAsuAuditado(auditado);
                auditoria.setUsuarioAuditor(UsuarioHome.findByPrimaryKey(responsavel.getUsuCodigo()));
                auditoria.setAsuDataAuditoria(DateHelper.getSystemDatetime());

                AuditoriaSupHome.update(auditoria);
            }
        } catch (FindException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw new AuditoriaControllerException("mensagem.erroInternoSistema", responsavel, e);
        } catch (UpdateException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw new AuditoriaControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }
}
