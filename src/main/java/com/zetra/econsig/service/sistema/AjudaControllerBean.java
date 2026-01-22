package com.zetra.econsig.service.sistema;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Ajuda;
import com.zetra.econsig.persistence.entity.AjudaHome;
import com.zetra.econsig.persistence.entity.AjudaRecurso;
import com.zetra.econsig.persistence.entity.AjudaRecursoHome;
import com.zetra.econsig.persistence.entity.UsuarioHome;
import com.zetra.econsig.persistence.query.ajuda.ListaFuncaoPapelAcessoRecursoQuery;
import com.zetra.econsig.persistence.query.ajuda.ListaTopicosAjudaQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: AjudaControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class AjudaControllerBean implements AjudaController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AjudaControllerBean.class);

    private String saveOrUpdateAjuda(String acrCodigo, String ajuTitulo, String ajuTexto, Short ajuSequencia, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            if (TextHelper.isNull(acrCodigo)) {
                throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel);
            }

            Ajuda ajuda;

            Date ajuDataAlteracao = DateHelper.getSystemDatetime();
            Short ajuAtivo = CodedValues.STS_ATIVO;

            try {
                ajuda = findAjudaByPrimaryKey(acrCodigo, responsavel);

                ajuda.setAjuTitulo(Jsoup.parse(ajuda.getAjuTitulo()).text());

                if(ajuda.getAjuHtml().equals("S")){
                    ajuda.setAjuTexto(Jsoup.parse(ajuda.getAjuTexto()).text());
                    ajuda.setAjuHtml("N");
                } else {
                    ajuda.setAjuHtml("N");
                }

                if(ajuda.getAjuHtml().equals("S")){
                    ajuda.setAjuTitulo(Jsoup.parse(ajuda.getAjuTitulo()).text());
                    ajuda.setAjuTexto(Jsoup.parse(ajuda.getAjuTexto()).text());
                }

                ajuda.setAjuTitulo(ajuTitulo);
                ajuda.setAjuTexto(ajuTexto);
                ajuda.setAjuSequencia(ajuSequencia);
                ajuda.setAjuDataAlteracao(ajuDataAlteracao);
                ajuda.setAjuAtivo(ajuAtivo);
                ajuda.setUsuario(UsuarioHome.findByPrimaryKey(responsavel.getUsuCodigo()));
                AjudaHome.update(ajuda);

                LogDelegate logDelegate = new LogDelegate(responsavel, Log.AJUDA, Log.UPDATE, Log.LOG_INFORMACAO);
                logDelegate.setAcessoRecurso(ajuda.getAcrCodigo());
                logDelegate.write();

            } catch (ConsignanteControllerException e) {
                ajuda = AjudaHome.create(acrCodigo, responsavel.getUsuCodigo(), ajuTitulo, ajuTexto, ajuDataAlteracao, ajuSequencia, ajuAtivo);

                LogDelegate logDelegate = new LogDelegate(responsavel, Log.AJUDA, Log.CREATE, Log.LOG_INFORMACAO);
                logDelegate.setAcessoRecurso(ajuda.getAcrCodigo());
                logDelegate.write();
            }

            return ajuda.getAcrCodigo();

        } catch (FindException e) {
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel);
        } catch (LogControllerException ex) {
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel);
        } catch (com.zetra.econsig.exception.CreateException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ConsignanteControllerException("mensagem.erro.impossivel.criar.ajuda", responsavel);
        } catch (UpdateException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ConsignanteControllerException("mensagem.erro.impossivel.alterar.ajuda", responsavel);
        }
    }

    @Override
    public void excluirAjuda(String acrCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            Ajuda ajuda = AjudaHome.findByPrimaryKey(acrCodigo);
            AjudaHome.remove(ajuda);

            LogDelegate logDelegate = new LogDelegate(responsavel, Log.AJUDA, Log.DELETE, Log.LOG_INFORMACAO);
            logDelegate.setAcessoRecurso(ajuda.getAcrCodigo());
            logDelegate.write();
        } catch (LogControllerException e) {
            LOG.error("Não foi possível inserir log para o método excluirAjuda, responsável['" + responsavel.getUsuCodigo() + "'], ajuda['" + acrCodigo + "']. " + e.getMessage());
        } catch (RemoveException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ConsignanteControllerException("mensagem.erro.impossivel.excluir.ajuda", responsavel);
        } catch (FindException e) {
            throw new ConsignanteControllerException("mensagem.erro.impossivel.excluir.ajuda", responsavel);
        }
    }

    @Override
    public void excluirAjuda(List<String> acrCodigos, AcessoSistema responsavel) throws ConsignanteControllerException {
        if (acrCodigos == null) {
            throw new ConsignanteControllerException("mensagem.erro.informe.ajuda.exclusao", responsavel);
        }

        try {
            Iterator<String> ite = acrCodigos.iterator();
            while (ite.hasNext()) {
                String acrCodigo = ite.next();
                try {
                    // Verifica se existe ajuda cadastrada para o código passado, se não existir é porque já foi excluída
                    AjudaHome.findByPrimaryKey(acrCodigo);
                    excluirAjuda(acrCodigo, responsavel);
                } catch (FindException e) {
                    LOG.info("Ajuda selecionada para exclusão não existe.");
                }
            }
        } catch (ConsignanteControllerException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ConsignanteControllerException(e);
        }

    }

    @Override
    public void editarAjuda(List<String> acrCodigos, String ajuTitulo, String ajuTexto, Short ajuSequencia, AcessoSistema responsavel) throws ConsignanteControllerException {
        if (acrCodigos == null || acrCodigos.isEmpty()) {
            throw new ConsignanteControllerException("mensagem.erro.informe.funcao.entidade.criar.ajuda", responsavel);
        }

        try {
            Iterator<String> ite = acrCodigos.iterator();
            while (ite.hasNext()) {
                String acrCodigo = ite.next();
                saveOrUpdateAjuda(acrCodigo, ajuTitulo, ajuTexto, ajuSequencia, responsavel);
            }
        } catch (ConsignanteControllerException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ConsignanteControllerException(e);
        }

    }

    @Override
    public Ajuda findAjudaByPrimaryKey(String acrCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        Ajuda ajuda = null;

        if (TextHelper.isNull(acrCodigo)) {
            throw new ConsignanteControllerException("mensagem.erro.nenhuma.ajuda.encontrada", responsavel);
        }

        try {
            ajuda = AjudaHome.findByPrimaryKey(acrCodigo);
        } catch (FindException e) {
            throw new ConsignanteControllerException("mensagem.erro.nenhuma.ajuda.encontrada", responsavel);
        }

        return ajuda;
    }

    @Override
    public List<TransferObject> lstTopicosAjuda(String papCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            ListaTopicosAjudaQuery query = new ListaTopicosAjudaQuery();
            query.papCodigo = papCodigo;
            query.responsavel = responsavel;

            return query.executarDTO();

        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.impossivel.listar.topicos.ajuda", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> lstFuncoesPapeisAcessoRecurso(List<String> acrCodigos, String acrRecurso, List<String> funCodigos, String acrParametro, String acrOperacao, AcessoSistema responsavel) throws ConsignanteControllerException {
        try {
            ListaFuncaoPapelAcessoRecursoQuery query = new ListaFuncaoPapelAcessoRecursoQuery();
            query.acrCodigos = acrCodigos;
            query.acrRecurso = acrRecurso;
            query.funCodigos = funCodigos;
            query.acrParametro = acrParametro;
            query.acrOperacao = acrOperacao;

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> listarAjudaRecurso(String acrCodigo, AcessoSistema responsavel) throws ConsignanteControllerException {
    	try {
    		List<AjudaRecurso> ajudaRecurso = AjudaRecursoHome.findByAcrCodigo(acrCodigo);
    		List<TransferObject> lstAjudaRecurso = new ArrayList<>();

    		if (ajudaRecurso != null) {
				for(AjudaRecurso ajrRecurso : ajudaRecurso) {
					TransferObject to  = new CustomTransferObject();
					to.setAttribute(Columns.AJR_CODIGO, ajrRecurso.getAjrCodigo());
					to.setAttribute(Columns.AJR_ACR_CODIGO, ajrRecurso.getAcessoRecurso().getAcrCodigo());
					to.setAttribute(Columns.AJR_ELEMENTO, ajrRecurso.getAjrElemento());
					to.setAttribute(Columns.AJR_POSICAO, ajrRecurso.getAjrPosicao());
					to.setAttribute(Columns.AJR_SEQUENCIA, ajrRecurso.getAjrSequencia());
					to.setAttribute(Columns.AJR_TEXTO, ajrRecurso.getAjrTexto());

					lstAjudaRecurso.add(to);
				}
    		}

    		return lstAjudaRecurso;

    	} catch(FindException ex) {
    		LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException("mensagem.erro.impossivel.listar.topicos.ajuda", responsavel, ex);
    	}
    }
}
