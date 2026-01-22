package com.zetra.econsig.service.servidor;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ReclamacaoRegistroServidorControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.ReclamacaoMotivoHome;
import com.zetra.econsig.persistence.entity.ReclamacaoRegistroServidorHome;
import com.zetra.econsig.persistence.entity.TipoMotivoReclamacao;
import com.zetra.econsig.persistence.entity.TipoMotivoReclamacaoHome;
import com.zetra.econsig.persistence.query.reclamacao.ListaMotivoReclamacaoQuery;
import com.zetra.econsig.persistence.query.reclamacao.ListaReclamacaoMotivoQuery;
import com.zetra.econsig.persistence.query.reclamacao.ListaReclamacaoRegistroServidorQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ReclamacaoRegistroServidorControllerBean</p>
 * <p>Description: Session Bean para manipulação de reclamação de servidor.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft</p>
 * $Author $
 * $Revision $
 * $Date $
 */
@Service
@Transactional
public class ReclamacaoRegistroServidorControllerBean implements ReclamacaoRegistroServidorController{
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReclamacaoRegistroServidorControllerBean.class);

    // Obtem a lista de reclamações do servidor
    @Override
    public List<TransferObject> listaReclamacaoRegistroServidor(CustomTransferObject criterio, int offset, int size,  AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException {
        try {

            ListaReclamacaoRegistroServidorQuery query = new ListaReclamacaoRegistroServidorQuery();

            if (offset != -1) {
                query.firstResult = offset;
            }

            if (size != -1) {
                query.maxResults = size;
            }

            if (criterio != null) {
                query.rseCodigo = (String) criterio.getAttribute(Columns.RSE_CODIGO);
                query.csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
                query.rseMatricula = (String) criterio.getAttribute(Columns.RSE_MATRICULA);
                query.serCpf = (String) criterio.getAttribute(Columns.SER_CPF);
                query.serCodigo = (String) criterio.getAttribute(Columns.SER_CODIGO);
                query.tmrCodigos = (List<String>) criterio.getAttribute(Columns.TMR_CODIGO);
                query.periodoIni = (String) criterio.getAttribute("periodoIni");
                query.periodoFim = (String) criterio.getAttribute("periodoFim");
            }
            return query.executarDTO();

        } catch (DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ReclamacaoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public int countReclamacaoRegistroServidor(CustomTransferObject criterio, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException {
        try {
            ListaReclamacaoRegistroServidorQuery query = new ListaReclamacaoRegistroServidorQuery();
            query.count = true;

            if (criterio != null) {
                query.rseCodigo = (String) criterio.getAttribute(Columns.RSE_CODIGO);
                query.csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
                query.rseMatricula = (String) criterio.getAttribute(Columns.RSE_MATRICULA);
                query.serCpf = (String) criterio.getAttribute(Columns.SER_CPF);
                query.serCodigo = (String) criterio.getAttribute(Columns.SER_CODIGO);
                query.tmrCodigos = (List<String>) criterio.getAttribute(Columns.TMR_CODIGO);
                query.periodoIni = (String) criterio.getAttribute("periodoIni");
                query.periodoFim = (String) criterio.getAttribute("periodoFim");
            }
            return query.executarContador();

        } catch (DAOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ReclamacaoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public String createReclamacaoRegistroServidor(CustomTransferObject reclamacaoRegistroServidor, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException {
        String rrsCodigo = null;
        String tmrCodigo = null;
        List<String> motivos = null;
        try {
            validaReclamacaoRegistroServidor(reclamacaoRegistroServidor, responsavel);
            if (((String) reclamacaoRegistroServidor.getAttribute(Columns.RRS_TEXTO)).length() > 65535 ) {
                throw new ReclamacaoRegistroServidorControllerException("mensagem.erro.texto.reclamacao.maior.que.limite.permitido", responsavel);
            }

            // insere o registro da reclamação
            rrsCodigo = ReclamacaoRegistroServidorHome.create((String) reclamacaoRegistroServidor.getAttribute(Columns.RRS_RSE_CODIGO),
                    (String) reclamacaoRegistroServidor.getAttribute(Columns.RRS_CSA_CODIGO),
                    (Date) reclamacaoRegistroServidor.getAttribute(Columns.RRS_DATA),
                    (String) reclamacaoRegistroServidor.getAttribute(Columns.RRS_TEXTO),
                    (String) reclamacaoRegistroServidor.getAttribute(Columns.RRS_IP_ACESSO));

            // insere os motivos da reclamação
            motivos = (List<String>) reclamacaoRegistroServidor.getAttribute(Columns.TMR_CODIGO);
            if (!TextHelper.isNull(rrsCodigo) && motivos != null && !motivos.isEmpty()) {
                Iterator<String> it = motivos.iterator();
                while (it.hasNext()) {
                    tmrCodigo = it.next();
                    ReclamacaoMotivoHome.create(tmrCodigo, rrsCodigo);
                }
            }

            LogDelegate log = new LogDelegate(responsavel, Log.RECLAMACAO, Log.CREATE, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.reclamacao.log", responsavel));
            log.setReclamacaoRegistroServidor(rrsCodigo);
            log.setRegistroServidor((String) reclamacaoRegistroServidor.getAttribute(Columns.RRS_RSE_CODIGO));
            log.setConsignataria((String) reclamacaoRegistroServidor.getAttribute(Columns.RRS_CSA_CODIGO));
            log.getUpdatedFields(reclamacaoRegistroServidor.getAtributos(), null);
            log.write();
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ReclamacaoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ReclamacaoRegistroServidorControllerException("mensagem.erro.nao.possivel.inserir.registro.reclamacao", responsavel, ex);
        }
        return rrsCodigo;
    }

    private void validaReclamacaoRegistroServidor(TransferObject reclamacaoRegistroServidor, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException{
        if (reclamacaoRegistroServidor == null) {
            throw new ReclamacaoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel);
        } else {
            if(TextHelper.isNull(reclamacaoRegistroServidor.getAttribute(Columns.RRS_CSA_CODIGO))){
                throw new ReclamacaoRegistroServidorControllerException("mensagem.informe.consignataria", responsavel);
            }
            if(TextHelper.isNull(reclamacaoRegistroServidor.getAttribute(Columns.RRS_DATA))){
                throw new ReclamacaoRegistroServidorControllerException("mensagem.informe.data", responsavel);
            }
            if(TextHelper.isNull(reclamacaoRegistroServidor.getAttribute(Columns.RRS_TEXTO))){
                throw new ReclamacaoRegistroServidorControllerException("mensagem.informe.relato.reclamacao", responsavel);
            }
            List<String> motivos = (List<String>) reclamacaoRegistroServidor.getAttribute(Columns.TMR_CODIGO);
            if (motivos == null || motivos.isEmpty()) {
                throw new ReclamacaoRegistroServidorControllerException("mensagem.informe.pelo.menos.um.tipo.motivo.reclamacao", responsavel);
            }
        }
    }

    @Override
    public CustomTransferObject buscaReclamacao(String rrsCodigo, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException {
        try {
            CustomTransferObject rrs = null;
            if (!TextHelper.isNull(rrsCodigo)) {

                ListaReclamacaoRegistroServidorQuery query = new ListaReclamacaoRegistroServidorQuery();
                query.rrsCodigo = rrsCodigo;

                List<TransferObject> reclamacoes = query.executarDTO();
                if (reclamacoes != null && reclamacoes.size() > 0) {
                    rrs = (CustomTransferObject) reclamacoes.get(0);
                }
            }

            if (rrs != null) {
                return rrs;
            } else {
                throw new ReclamacaoRegistroServidorControllerException("mensagem.erro.interno.registro.de.arg0.nao.encontrado", responsavel, ApplicationResourcesHelper.getMessage("rotulo.reclamacao.singular", responsavel));
            }
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ReclamacaoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    // Obtem a lista de motivos da reclamação
    @Override
    public List<TransferObject> lstReclamacaoMotivo(String rrsCodigo, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException {
        try {
            if (TextHelper.isNull(rrsCodigo)) {
                throw new ReclamacaoRegistroServidorControllerException("mensagem.erro.interno.registro.de.arg0.nao.encontrado", responsavel, ApplicationResourcesHelper.getMessage("rotulo.reclamacao.singular", responsavel));
            }
            ListaReclamacaoMotivoQuery query = new ListaReclamacaoMotivoQuery();
            query.rrsCodigo = rrsCodigo;

            List<TransferObject> result = query.executarDTO();

            return result;
        } catch (HQueryException ex) {
            throw new ReclamacaoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public String createTipoMotivoReclamacao(String tmrDescricao, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException {
        try {
            // Retira caracteres vazios
            tmrDescricao = tmrDescricao != null ? tmrDescricao.trim() : null;

            if (TextHelper.isNull(tmrDescricao)) {
                throw new ReclamacaoRegistroServidorControllerException("mensagem.informe.tipo.motivo.reclamacao.descricao", responsavel);
            }

            try {
                // Se existe um tipo de motivo de reclamação com a mesma descrição
                TipoMotivoReclamacaoHome.findByDescricao(tmrDescricao);
                throw new ReclamacaoRegistroServidorControllerException("mensagem.erro.nao.possivel.inserir.tipo.motivo.reclamacao.existe.outro.mesma.descricao", responsavel);
            } catch (FindException e) {
            }

            TipoMotivoReclamacao motivoReclamacao = TipoMotivoReclamacaoHome.create(tmrDescricao);
            String tmrCodigo = motivoReclamacao.getTmrCodigo();

            LogDelegate log = new LogDelegate(responsavel, Log.TIPO_MOTIVO_RECLAMACAO, Log.CREATE, Log.LOG_INFORMACAO);
            log.setTipoMotivoReclamacao(tmrCodigo);
            log.add(Columns.TMR_DESCRICAO, tmrDescricao);
            log.write();

            return tmrCodigo;

        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ReclamacaoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ReclamacaoRegistroServidorControllerException("mensagem.erro.nao.possivel.criar.tipo.motivo.reclamacao.excecao.getmessage.arg0", responsavel, ex.getMessage());
        }
    }

    @Override
    public void updateTipoMotivoReclamacao(TransferObject motivoReclamacao, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException {
        try {
            // Retira caracteres vazios
            String tmrCodigo = motivoReclamacao != null && !TextHelper.isNull(motivoReclamacao.getAttribute(Columns.TMR_CODIGO)) ? motivoReclamacao.getAttribute(Columns.TMR_CODIGO).toString() : null;
            String tmrDescricao = motivoReclamacao != null && !TextHelper.isNull(motivoReclamacao.getAttribute(Columns.TMR_DESCRICAO)) ? motivoReclamacao.getAttribute(Columns.TMR_DESCRICAO).toString().trim() : null;

            if (TextHelper.isNull(tmrDescricao)) {
                throw new ReclamacaoRegistroServidorControllerException("mensagem.informe.tipo.motivo.reclamacao.descricao", responsavel);
            }

            try {
                // Se existe um tipo de motivo de reclamação com a mesma descrição
                TipoMotivoReclamacao tmr = TipoMotivoReclamacaoHome.findByDescricao(tmrDescricao);
                if (!tmr.getTmrCodigo().equals(tmrCodigo)) {
                    throw new ReclamacaoRegistroServidorControllerException("mensagem.erro.nao.possivel.alterar.tipo.motivo.reclamacao.existe.outro.mesma.descricao", responsavel, "");
                }
            } catch (FindException e) {
            }

            TipoMotivoReclamacao tmrBean = TipoMotivoReclamacaoHome.findByPrimaryKey(motivoReclamacao.getAttribute(Columns.TMR_CODIGO).toString());

            LogDelegate log = new LogDelegate(responsavel, Log.TIPO_MOTIVO_RECLAMACAO, Log.UPDATE, Log.LOG_INFORMACAO);
            log.setTipoMotivoReclamacao(tmrBean.getTmrCodigo());

            /* Compara a versão do cache com a passada por parâmetro */
            TransferObject motivoReclamacaoCache = new CustomTransferObject();
            motivoReclamacaoCache.setAttribute(Columns.TMR_CODIGO, tmrBean.getTmrCodigo());
            motivoReclamacaoCache.setAttribute(Columns.TMR_DESCRICAO, tmrBean.getTmrDescricao());

            CustomTransferObject merge = log.getUpdatedFields(motivoReclamacao.getAtributos(), motivoReclamacaoCache.getAtributos());

            if (merge.getAtributos().containsKey(Columns.TMR_DESCRICAO)) {
                tmrBean.setTmrDescricao(((String) merge.getAttribute(Columns.TMR_DESCRICAO)).trim());
            }

            TipoMotivoReclamacaoHome.update(tmrBean);
            log.write();
        } catch (UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ReclamacaoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ReclamacaoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ReclamacaoRegistroServidorControllerException("mensagem.erro.nao.possivel.encontrar.tipo.motivo.reclamacao", responsavel, ex);
        }
    }

    @Override
    public void removeTipoMotivoReclamacao(String tmrCodigo, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException {
        try {
            TipoMotivoReclamacao motivoReclamacaoBean = TipoMotivoReclamacaoHome.findByPrimaryKey(tmrCodigo);
            TipoMotivoReclamacaoHome.remove(motivoReclamacaoBean);

            LogDelegate log = new LogDelegate(responsavel, Log.TIPO_MOTIVO_RECLAMACAO, Log.DELETE, Log.LOG_INFORMACAO);
            log.setTipoMotivoReclamacao(tmrCodigo);
            log.write();

        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ReclamacaoRegistroServidorControllerException("mensagem.erro.nao.possivel.encontrar.tipo.motivo.reclamacao", responsavel, ex);
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ReclamacaoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ReclamacaoRegistroServidorControllerException("mensagem.erro.nao.possivel.excluir.tipo.motivo.reclamacao.pois.possui.dependentes", responsavel);
        }
    }

    @Override
    public int countTipoMotivoReclamacao(int offset, int count, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException {
        try {
            ListaMotivoReclamacaoQuery query = new ListaMotivoReclamacaoQuery();
            query.count = true;

            if (count != -1) {
                query.maxResults = count;
                query.firstResult = offset;
            }

            int result = query.executarContador();

            LogDelegate log = new LogDelegate(responsavel, Log.TIPO_MOTIVO_RECLAMACAO, Log.SELECT, Log.LOG_INFORMACAO);
            log.write();

            return result;

        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ReclamacaoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (HQueryException ex) {
            throw new ReclamacaoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstTipoMotivoReclamacao(AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException {
        return lstTipoMotivoReclamacao(-1, -1, responsavel);
    }

    @Override
    public List<TransferObject> lstTipoMotivoReclamacao(int offset, int count, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException {
        try {
            ListaMotivoReclamacaoQuery query = new ListaMotivoReclamacaoQuery();

            if (count != -1) {
                query.maxResults = count;
                query.firstResult = offset;
            }

            List<TransferObject> result = query.executarDTO();

            return result;
        } catch (HQueryException ex) {
            throw new ReclamacaoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public TransferObject findTipoMotivoReclamacao(String tmrCodigo, AcessoSistema responsavel) throws ReclamacaoRegistroServidorControllerException {
        try {
            TransferObject retorno = new CustomTransferObject();

            TipoMotivoReclamacao motivoReclamacao = TipoMotivoReclamacaoHome.findByPrimaryKey(tmrCodigo);
            retorno.setAttribute(Columns.TMR_CODIGO, motivoReclamacao.getTmrCodigo());
            retorno.setAttribute(Columns.TMR_DESCRICAO, motivoReclamacao.getTmrDescricao());

            LogDelegate log = new LogDelegate(responsavel, Log.TIPO_MOTIVO_RECLAMACAO, Log.SELECT, Log.LOG_INFORMACAO);
            log.setTipoMotivoReclamacao(tmrCodigo);
            log.write();

            return retorno;
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ReclamacaoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ReclamacaoRegistroServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}