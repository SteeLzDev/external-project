package com.zetra.econsig.service.comunicacao;

import java.io.File;
import java.security.SecureRandom;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AnexoComunicacao;
import com.zetra.econsig.persistence.entity.AnexoComunicacaoHome;
import com.zetra.econsig.persistence.entity.AnexoComunicacaoId;
import com.zetra.econsig.persistence.entity.Comunicacao;
import com.zetra.econsig.persistence.query.anexo.ListaAnexoComunicacaoQuery;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoControllerBean;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: EditarAnexoComunicacaoControllerBean</p>
 * <p>Description: Session Bean para operação de edição de anexo de consignação.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class EditarAnexoComunicacaoControllerBean implements EditarAnexoComunicacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarAnexoConsignacaoControllerBean.class);

    @Autowired
    private ComunicacaoController comunicacaoController;

    @Override
    public int countAnexoComunicacao(String cmnCodigo, AcessoSistema responsavel) throws ZetraException {
        CustomTransferObject cto = new CustomTransferObject();
        cto.setAttribute(Columns.ACM_CMN_CODIGO, cmnCodigo);
        return countAnexoComunicacao(cto, responsavel);
    }

    @Override
    public int countAnexoComunicacao(CustomTransferObject cto, AcessoSistema responsavel) throws ZetraException {
        String cmnCodigo = (cto != null ? (String) cto.getAttribute(Columns.ACM_CMN_CODIGO) : null);
        if (TextHelper.isNull(cmnCodigo)) {
            throw new ZetraException("mensagem.erro.codigo.comunicacao.nao.informado", responsavel);
        }
        try {
            ListaAnexoComunicacaoQuery query = new ListaAnexoComunicacaoQuery();
            query.count = true;
            query.cmnCodigo = cmnCodigo;

            return query.executarContador();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }


    @Override
    public List<TransferObject> lstAnexoComunicacao(CustomTransferObject cto, int offset, int rows, AcessoSistema responsavel) throws ZetraException {
        String cmnCodigo = (cto != null ? (String) cto.getAttribute(Columns.ACM_CMN_CODIGO) : null);
        if (TextHelper.isNull(cmnCodigo)) {
            throw new ZetraException("mensagem.erro.codigo.comunicacao.nao.informado", responsavel);
        }
        try {
            ListaAnexoComunicacaoQuery query = new ListaAnexoComunicacaoQuery();
            query.cmnCodigo = cmnCodigo;

            if (offset != -1) {
                query.firstResult = offset;
            }
            if (rows != -1) {
                query.maxResults = rows;
            }

            return query.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }


    @Override
    public void createAnexoComunicacao(String cmnCodigo, String acmNome, String acmDescricao, TipoArquivoEnum tipoArquivo, String codePath, AcessoSistema responsavel) throws ZetraException {
        try {
            Comunicacao obj = comunicacaoController.findComunicacaoByPK(cmnCodigo, responsavel);

            // Verifica se o arquivo anexo existe (já foi feito o upload)
            String caminho = ParamSist.getDiretorioRaizArquivos() + File.separatorChar + "comunicacao" + File.separatorChar +  DateHelper.format(obj.getCmnData(), "yyyyMMdd") + File.separatorChar + codePath;
            File arquivoAnexo = new File(caminho + File.separatorChar + acmNome);
            if (arquivoAnexo == null || !arquivoAnexo.exists()) {
                throw new ZetraException("mensagem.erro.copiar.anexo.comunicacao", responsavel);
            }

            // Valida a quantidade máxima de anexos permitidos por comunicação
            String paramQtdeMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_QTE_MAX_ARQ_ANEXO_COMUNICACAO, responsavel);
            int qtdeMaxArqAnexo = (!TextHelper.isNull(paramQtdeMaxArqAnexo) ? Integer.parseInt(paramQtdeMaxArqAnexo) : 30);
            int totalAnexos = countAnexoComunicacao(codePath, responsavel);
            if (totalAnexos >= qtdeMaxArqAnexo) {
                arquivoAnexo.delete();
                throw new ZetraException("mensagem.erro.qtde.anexos.comunicacao.excedido", responsavel);
            }

            // Troca o nome do arquivo anexo para padronização
            String extensaoAnexo = acmNome.substring(acmNome.lastIndexOf("."), acmNome.length());
            String novoNomeAnexo = DateHelper.format(DateHelper.getSystemDatetime(), "yyMMddHHmmssS") + new SecureRandom().nextInt(10) + extensaoAnexo;
            File novoArquivoAnexo = new File(caminho + File.separatorChar + novoNomeAnexo);
            if (arquivoAnexo.renameTo(novoArquivoAnexo)) {
                acmNome = novoNomeAnexo;
            } else {
                arquivoAnexo.delete();
                throw new ZetraException("mensagem.erro.renomear.anexo.comunicacao", responsavel);
            }

            // Define os valores padrões, caso não sejam informados
            tipoArquivo = (tipoArquivo != null ? tipoArquivo : TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_GENERICO);

            // Grava a ligação do anexo com A comunicação
            AnexoComunicacaoHome.create(cmnCodigo, acmNome, acmDescricao, responsavel.getUsuCodigo(), CodedValues.STS_ATIVO, DateHelper.getSystemDatetime(), tipoArquivo);

            // Grava log da associação do arquivo anexo
            LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.UPLOAD_FILE, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.inclusao.anexo.comunicacao", responsavel, novoArquivoAnexo.getAbsolutePath()));
            log.setComunicacao(codePath);
            log.write();

        } catch (Exception e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            if (e.getClass().equals(ZetraException.class)) {
                throw (ZetraException) e;
            }

            LOG.error(e.getMessage(), e);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    @Override
    public void removeAnexoComunicacao(CustomTransferObject cto, AcessoSistema responsavel) throws ZetraException {
        try {
            String cmnCodigo = (String) cto.getAttribute(Columns.ACM_CMN_CODIGO);
            String acmNome = (String) cto.getAttribute(Columns.ACM_NOME);

            if (!TextHelper.isNull(cmnCodigo) && !TextHelper.isNull(acmNome)) {
                AnexoComunicacaoId acpk = new AnexoComunicacaoId(cmnCodigo, acmNome);
                AnexoComunicacao acm = AnexoComunicacaoHome.findByPrimaryKey(acpk);
                removerAnexo(acm, responsavel);
            } else {
                throw new ZetraException("mensagem.erro.anexo.comunicacao.nao.encontrado", responsavel);
            }
        } catch (Exception e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            if (e.getClass().equals(ZetraException.class)) {
                throw (ZetraException) e;
            }
            LOG.error(e.getMessage(), e);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    private void removerAnexo(AnexoComunicacao acm, AcessoSistema responsavel) throws RemoveException, LogControllerException, ZetraException {
        String cmnCodigo = acm.getCmnCodigo();

        String caminho = ParamSist.getDiretorioRaizArquivos()
                + File.separatorChar + "comunicacao"
                + File.separatorChar + DateHelper.format(acm.getAcmData(), "yyyyMMdd")
                + File.separatorChar + cmnCodigo;

        File arquivoAnexo = new File(caminho + File.separatorChar + acm.getAcmNome());


        // Remove o arquivo caso exista
        if (arquivoAnexo.exists()) {
            arquivoAnexo.delete();
        }


        // Remove o registro de ligação
        AnexoComunicacaoHome.remove(acm);

        // Grava log da exclusão
        LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DELETE_FILE, Log.LOG_INFORMACAO);
        log.add(ApplicationResourcesHelper.getMessage("rotulo.log.remocao.anexo.comunicacao", responsavel, arquivoAnexo.getAbsolutePath()));
        log.setAutorizacaoDesconto(cmnCodigo);
        log.write();
    }
}

