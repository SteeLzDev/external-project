package com.zetra.econsig.service.arquivo;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ArquivoControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Arquivo;
import com.zetra.econsig.persistence.entity.ArquivoHome;
import com.zetra.econsig.persistence.entity.ArquivoMensagem;
import com.zetra.econsig.persistence.entity.ArquivoMensagemHome;
import com.zetra.econsig.persistence.entity.ArquivoMensagemId;
import com.zetra.econsig.persistence.entity.ArquivoRse;
import com.zetra.econsig.persistence.entity.ArquivoRseHome;
import com.zetra.econsig.persistence.entity.ArquivoRseId;
import com.zetra.econsig.persistence.entity.ArquivoSer;
import com.zetra.econsig.persistence.entity.ArquivoSerHome;
import com.zetra.econsig.persistence.entity.ArquivoSerId;
import com.zetra.econsig.persistence.query.arquivo.ListaArquivoRegistroServidorQuery;
import com.zetra.econsig.persistence.query.arquivo.ListaArquivoServidorQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ArquivoControllerBean</p>
 * <p>Description: Session Bean para operações sobre arquivo</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ArquivoControllerBean implements ArquivoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ArquivoControllerBean.class);

    @Override
    public void createArquivoServidor(TransferObject criterio, AcessoSistema responsavel) throws ArquivoControllerException {
        try {
            // Cria arquivo do boleto
            String serCodigo = criterio.getAttribute(Columns.SER_CODIGO).toString();
            Object objArqConteudo = criterio.getAttribute(Columns.ARQ_CONTEUDO);
            String tarCodigo = criterio.getAttribute(Columns.ARQ_TAR_CODIGO).toString();

            if (objArqConteudo instanceof List) {
                @SuppressWarnings("unchecked")
                List<TransferObject> listArqConteudo = (List<TransferObject>) objArqConteudo;
                for (TransferObject conteudo : listArqConteudo) {
                    byte[] arqConteudoBase64 = (byte[]) conteudo.getAttribute(Columns.ARQ_CONTEUDO);
                    String arqConteudo = new String(arqConteudoBase64);
                    String aseNome = conteudo.getAttribute(Columns.ASE_NOME).toString();
                    createArquivoServidor(serCodigo, tarCodigo, arqConteudo, aseNome, responsavel);
                }
            } else {
                byte[] arqConteudoBase64 = (byte[]) criterio.getAttribute(Columns.ARQ_CONTEUDO);
                String arqConteudo = new String(arqConteudoBase64);
                String aseNome = criterio.getAttribute(Columns.ASE_NOME).toString();
                createArquivoServidor(serCodigo, tarCodigo, arqConteudo, aseNome, responsavel);
            }

        } catch (CreateException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ArquivoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void createArquivoServidor(String serCodigo, String tarCodigo, String arqConteudo, String aseNome, AcessoSistema responsavel) throws CreateException, LogControllerException {
        Arquivo arquivo = ArquivoHome.create(arqConteudo, tarCodigo);

        String arqCodigo = arquivo.getArqCodigo();
        ArquivoSerHome.create(arqCodigo, serCodigo, responsavel.getUsuCodigo(), aseNome, responsavel.getIpUsuario());

        // Grava log de Erro
        LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.CREATE, Log.LOG_INFORMACAO);
        log.setArquivo(arqCodigo);
        log.setServidor(serCodigo);
        log.write();
    }

    @Override
    public void removeArquivoServidor(String arqCodigo, String serCodigo, AcessoSistema responsavel) throws ArquivoControllerException {
        try {
            if (!responsavel.temPermissao(CodedValues.FUN_CAD_DISPENSA_VALIDACAO_DIGITAL_SER)) {
                throw new ArquivoControllerException("mensagem.usuarioNaoTemPermissao", responsavel);
            }

            ArquivoSerId id = new ArquivoSerId(arqCodigo, serCodigo);
            ArquivoSer arquivoSer = ArquivoSerHome.findByPrimaryKey(id);

            if (arquivoSer == null) {
                throw new ArquivoControllerException("mensagem.arquivo.nao.encontrado", responsavel);
            }

            ArquivoSerHome.remove(arquivoSer);

            Arquivo arquivo = ArquivoHome.findByPrimaryKey(arqCodigo);
            ArquivoHome.remove(arquivo);

            // Grava log de Erro
            LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DELETE, Log.LOG_INFORMACAO);
            log.setArquivo(arqCodigo);
            log.setServidor(serCodigo);
            log.write();

        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ArquivoControllerException("mensagem.boleto.nao.encontrado", responsavel, ex);
        } catch (RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ArquivoControllerException("mensagem.erro.remover.boleto.servidor", responsavel, ex);
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ArquivoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
    
    @Override
    public void removeArquivoReconhecimentoFacial(String arqCodigo, String serCodigo, AcessoSistema responsavel) throws ArquivoControllerException {
        try {
            ArquivoSerId id = new ArquivoSerId(arqCodigo, serCodigo);
            ArquivoSer arquivoSer = ArquivoSerHome.findByPrimaryKey(id);

            if (arquivoSer == null) {
                throw new ArquivoControllerException("mensagem.arquivo.nao.encontrado", responsavel);
            }

            ArquivoSerHome.remove(arquivoSer);

            Arquivo arquivo = ArquivoHome.findByPrimaryKey(arqCodigo);
            ArquivoHome.remove(arquivo);

            // Grava log de Erro
            LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DELETE, Log.LOG_INFORMACAO);
            log.setArquivo(arqCodigo);
            log.setServidor(serCodigo);
            log.write();

        } catch (FindException | ArquivoControllerException | LogControllerException | RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ArquivoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } 
    }

    @Override
    public TransferObject findArquivoServidor(String arqCodigo, String serCodigo, AcessoSistema responsavel) throws ArquivoControllerException {
        try {
            TransferObject retorno = new CustomTransferObject();

            ArquivoSerId id = new ArquivoSerId(arqCodigo, serCodigo);
            ArquivoSer arquivoSer = ArquivoSerHome.findByPrimaryKey(id);

            if (arquivoSer == null) {
                throw new ArquivoControllerException("mensagem.arquivo.nao.encontrado", responsavel);
            }

            Arquivo arquivo = ArquivoHome.findByPrimaryKey(arqCodigo);

            retorno.setAttribute(Columns.ARQ_CODIGO, arquivo.getArqCodigo());
            retorno.setAttribute(Columns.ARQ_CONTEUDO, arquivo.getArqConteudo());
            retorno.setAttribute(Columns.TAR_CODIGO, arquivo.getTipoArquivo().getTarCodigo());
            retorno.setAttribute(Columns.SER_CODIGO, arquivoSer.getServidor().getSerCodigo());
            retorno.setAttribute(Columns.ASE_DATA_CRIACAO, arquivoSer.getAseDataCriacao());
            retorno.setAttribute(Columns.ASE_IP_ACESSO, arquivoSer.getAseIpAcesso());
            retorno.setAttribute(Columns.ASE_NOME, arquivoSer.getAseNome());

            return retorno;
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ArquivoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> listArquivoServidor(String serCodigo, List<String> tarCodigos, AcessoSistema responsavel) throws ArquivoControllerException {
        try {
            ListaArquivoServidorQuery query = new ListaArquivoServidorQuery();
            query.serCodigo = serCodigo;
            query.tarCodigos = tarCodigos;

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ArquivoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void createArquivoRegistroServidor(String rseCodigo, String tarCodigo,  TransferObject criterio, AcessoSistema responsavel) throws ArquivoControllerException {
        try {

            Object objArqConteudo = criterio.getAttribute(Columns.ARQ_CONTEUDO);

            if (objArqConteudo instanceof List) {
                @SuppressWarnings("unchecked")
                List<TransferObject> listArqConteudo = (List<TransferObject>) objArqConteudo;
                for (TransferObject conteudo : listArqConteudo) {
                    byte[] arqConteudoBase64 = (byte[]) conteudo.getAttribute(Columns.ARQ_CONTEUDO);
                    String arqConteudo = new String(arqConteudoBase64);
                    String arsNome = conteudo.getAttribute(Columns.ARS_NOME).toString();

                    createArquivoRegistroServidor(arqConteudo, tarCodigo, rseCodigo, arsNome, responsavel);
                }
            } else {
                byte[] arqConteudoBase64 = (byte[]) criterio.getAttribute(Columns.ARQ_CONTEUDO);
                String arqConteudo = new String(arqConteudoBase64);
                String arsNome = criterio.getAttribute(Columns.ARS_NOME).toString();

                createArquivoRegistroServidor(arqConteudo, tarCodigo, rseCodigo, arsNome, responsavel);
            }

        } catch (CreateException | LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ArquivoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void createArquivoRegistroServidor(String arqConteudo, String tarCodigo, String rseCodigo, String arsNome, AcessoSistema responsavel) throws CreateException, LogControllerException {
        Arquivo arquivo = ArquivoHome.create(arqConteudo, tarCodigo);
        String arqCodigo = arquivo.getArqCodigo();

        ArquivoRseHome.create(arqCodigo, rseCodigo, responsavel.getUsuCodigo(), arsNome, responsavel.getIpUsuario());

        LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.CREATE, Log.LOG_INFORMACAO);
        log.setArquivo(arqCodigo);
        log.setRegistroServidor(rseCodigo);
        log.write();
    }

    @Override
    public List<TransferObject> lstArquivosRse(String rseCodigo, AcessoSistema responsavel) throws ArquivoControllerException {
        try {
            ListaArquivoRegistroServidorQuery query = new ListaArquivoRegistroServidorQuery();
            query.rseCodigo = rseCodigo;

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ArquivoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<ArquivoRse> lstArquivoRse(String rseCodigo, AcessoSistema responsavel) throws ArquivoControllerException {

        List<ArquivoRse> arquivo = null;
        try {

            arquivo = ArquivoRseHome.listByRseCodigo(rseCodigo);

        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ArquivoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
        return arquivo;
    }


    @Override
    public Arquivo getArquivoRse(String arqCodigo, AcessoSistema responsavel) throws ArquivoControllerException {

    	try {

    		return ArquivoHome.findByPrimaryKey(arqCodigo);

    	} catch (FindException ex) {
    		LOG.error(ex.getMessage(), ex);
    		throw new ArquivoControllerException("mensagem.erroInternoSistema", responsavel, ex);
    	}
    }

    @Override
    public TransferObject findArquivoResgistroServidorServidor(String arqCodigo, String rseCodigo, AcessoSistema responsavel) throws ArquivoControllerException {
        try {
            TransferObject retorno = new CustomTransferObject();

            ArquivoRseId id = new ArquivoRseId(arqCodigo, rseCodigo);
            ArquivoRse arquivoRse = ArquivoRseHome.findByPrimaryKey(id);

            if (arquivoRse == null) {
                throw new ArquivoControllerException("mensagem.arquivo.nao.encontrado", responsavel);
            }

            Arquivo arquivo = ArquivoHome.findByPrimaryKey(arqCodigo);

            retorno.setAttribute(Columns.ARQ_CODIGO, arquivo.getArqCodigo());
            retorno.setAttribute(Columns.ARQ_CONTEUDO, arquivo.getArqConteudo());
            retorno.setAttribute(Columns.TAR_CODIGO, arquivo.getTipoArquivo().getTarCodigo());
            retorno.setAttribute(Columns.RSE_CODIGO, arquivoRse.getRegistroServidor().getRseCodigo());
            retorno.setAttribute(Columns.ARS_DATA_CRIACAO, arquivoRse.getArsDataCriacao());
            retorno.setAttribute(Columns.ARS_IP_ACESSO, arquivoRse.getArsIpAcesso());
            retorno.setAttribute(Columns.ARS_NOME, arquivoRse.getArsNome());

            return retorno;
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ArquivoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void removeArquivoRegistroServidor(String arqCodigo, String rseCodigo, AcessoSistema responsavel) throws ArquivoControllerException {
        try {
            if (!responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXOS_REGISTRO_SERVIDOR)) {
                throw new ArquivoControllerException("mensagem.usuarioNaoTemPermissao", responsavel);
            }

            ArquivoRseId id = new ArquivoRseId(arqCodigo, rseCodigo);
            ArquivoRse arquivoRse = ArquivoRseHome.findByPrimaryKey(id);

            if (arquivoRse == null) {
                throw new ArquivoControllerException("mensagem.arquivo.nao.encontrado", responsavel);
            }

            ArquivoRseHome.remove(arquivoRse);

            Arquivo arquivo = ArquivoHome.findByPrimaryKey(arqCodigo);
            ArquivoHome.remove(arquivo);

            LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DELETE, Log.LOG_INFORMACAO);
            log.setArquivo(arqCodigo);
            log.setRegistroServidor(rseCodigo);
            log.write();

        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ArquivoControllerException("mensagem.anexo.nao.encontrado", responsavel, ex);
        } catch (RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ArquivoControllerException("mensagem.erro.remover.anexo.registro.servidor", responsavel, ex);
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ArquivoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void createArquivoMensagem(String menCodigo, String tarCodigo, String arqConteudo, String arqNome, AcessoSistema responsavel) throws CreateException, LogControllerException, ArquivoControllerException {

        ArquivoMensagem arquivoMensagem;

        try {
            arquivoMensagem = ArquivoMensagemHome.findByMenCodigo(menCodigo);
        } catch (FindException e) {
            arquivoMensagem = null;
        }

        if (!TextHelper.isNull(arquivoMensagem)) {
            try {
                ArquivoMensagemHome.remove(arquivoMensagem);
                ArquivoHome.remove(ArquivoHome.findByPrimaryKey(arquivoMensagem.getArqCodigo()));
            } catch (RemoveException | FindException e) {
                LOG.error(e.getMessage(), e);
                throw new ArquivoControllerException("mensagem.erroInternoSistema", responsavel, e);
            }
        }

        Arquivo arquivo = ArquivoHome.create(arqConteudo, tarCodigo);
        String arqCodigo = arquivo.getArqCodigo();

        ArquivoMensagemHome.create(menCodigo, arqCodigo, responsavel.getUsuCodigo(), arqNome,  responsavel.getIpUsuario());

        // Grava log de Erro
        LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.CREATE, Log.LOG_INFORMACAO);
        log.setArquivo(arqCodigo);
        log.write();
    }

    @Override
    public TransferObject findArquivoMensagem(String menCodigo, AcessoSistema responsavel) throws ArquivoControllerException {
        try {
            TransferObject retorno = new CustomTransferObject();
            ArquivoMensagem arquivoMensagem = ArquivoMensagemHome.findByMenCodigo(menCodigo);
            Arquivo arquivo = ArquivoHome.findByPrimaryKey(arquivoMensagem.getArqCodigo());

            retorno.setAttribute(Columns.ARQ_CODIGO, arquivo.getArqCodigo());
            retorno.setAttribute(Columns.ARQ_CONTEUDO, arquivo.getArqConteudo());
            retorno.setAttribute(Columns.TAR_CODIGO, arquivo.getTipoArquivo().getTarCodigo());
            retorno.setAttribute(Columns.MEN_CODIGO, arquivoMensagem.getMenCodigo());
            retorno.setAttribute(Columns.AMN_DATA_CRIACAO, arquivoMensagem.getAmnDataCriacao());
            retorno.setAttribute(Columns.AMN_IP_ACESSO, arquivoMensagem.getAmnIpAcesso());
            retorno.setAttribute(Columns.AMN_NOME, arquivoMensagem.getAmnNome());


            return retorno;
        } catch (FindException ex) {
            //Se não for encontrado resultado para a busca de arquivo, retorna null
            return null;
        }
    }

    @Override
    public void removeArquivoMensagem(String arqCodigo, String menCodigo, AcessoSistema responsavel) throws ArquivoControllerException {
        try {
            ArquivoMensagemId id = new ArquivoMensagemId(arqCodigo, menCodigo);
            ArquivoMensagem arquivoMensagem = ArquivoMensagemHome.findByPrimaryKey(id);

            if (arquivoMensagem == null) {
                throw new ArquivoControllerException("mensagem.arquivo.nao.encontrado", responsavel);
            }

            ArquivoMensagemHome.remove(arquivoMensagem);

            Arquivo arquivo = ArquivoHome.findByPrimaryKey(arqCodigo);
            ArquivoHome.remove(arquivo);

            LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DELETE, Log.LOG_INFORMACAO);
            log.setArquivo(arqCodigo);
            log.setRegistroServidor(menCodigo);
            log.write();

        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ArquivoControllerException("mensagem.anexo.nao.encontrado", responsavel, ex);
        } catch (RemoveException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ArquivoControllerException("mensagem.erro.remover.anexo.registro.servidor", responsavel, ex);
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ArquivoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

}
