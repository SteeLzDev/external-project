package com.zetra.econsig.service.relatorio;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Subrelatorio;
import com.zetra.econsig.persistence.entity.SubrelatorioHome;
import com.zetra.econsig.persistence.query.subrelatorio.ListaSubrelatorioQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: SubrelatorioControllerBean</p>
 * <p>Description: Bean do subrelatorio</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

@Service
@Transactional
public class SubrelatorioControllerBean implements SubrelatorioController{
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SubrelatorioControllerBean.class);

    
    @Override
    public Collection<Subrelatorio> lstSubrelatorio(String relCodigo) throws FindException{
        Collection<Subrelatorio> subrelatorio = SubrelatorioHome.findByRelCodigo(relCodigo);
        return subrelatorio;
    }
    
    @Override
    public List<TransferObject> listarSubrelatorio(CustomTransferObject filtro, int offset, int count, AcessoSistema responsavel) throws ZetraException {
        try {
            ListaSubrelatorioQuery query = new ListaSubrelatorioQuery();
            if (filtro != null) {
                query.sreCodigo = (String) filtro.getAttribute(Columns.SRE_CODIGO);
                query.relCodigo = (String) filtro.getAttribute(Columns.SRE_REL_CODIGO);
            }
            
            if (count != -1) {
                query.maxResults = count;
                query.firstResult = offset;
            }

            return query.executarDTO();
        } catch (HQueryException ex) {
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
    
    @Override
    public void removeSubrelatorioEditavel(String sreCodigo, String relCodigo) throws RemoveException, FindException{
        Subrelatorio subrelatorio = SubrelatorioHome.findSubrelatorio(sreCodigo, relCodigo);
        SubrelatorioHome.remove(subrelatorio);
    }
    
    @Override
    public Subrelatorio buscaSubrelatorioEditavel(String sreCodigo, String relCodigo) throws FindException{
        Subrelatorio subrelatorio = new Subrelatorio();
        subrelatorio = SubrelatorioHome.findSubrelatorio(sreCodigo, relCodigo);
        return subrelatorio;
    }
    
    @Override
    public void inserirSubrelatorio(String relCodigo, String nomeAnexoSubrelatorio, String sreNomeParametro, String sreTemplateSql, AcessoSistema responsavel) throws MissingPrimaryKeyException, ZetraException {
        try {
            SubrelatorioHome.create(relCodigo, nomeAnexoSubrelatorio, sreNomeParametro, sreTemplateSql, responsavel);
        } catch (com.zetra.econsig.exception.CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new ZetraException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void editarSubrelatorio(String sreCodigo, String relCodigo, String nomeAnexoSubrelatorio, String sreNomeParametro, String sreTemplateSql, boolean removerArquivo, AcessoSistema responsavel) throws ZetraException {
        if (TextHelper.isNull(sreCodigo) && TextHelper.isNull(relCodigo)) {
            throw new ZetraException("mensagem.erro.subrelatorio.editar", responsavel);
        }

        try {
            Subrelatorio subrelatorio = SubrelatorioHome.findSubrelatorio(sreCodigo, relCodigo);
            if(removerArquivo) {
                subrelatorio.setSreTemplateJasper("");    
            }else if(nomeAnexoSubrelatorio.isEmpty()) {
                subrelatorio.setSreTemplateJasper(subrelatorio.getSreTemplateJasper());
            }else {
                subrelatorio.setSreTemplateJasper(nomeAnexoSubrelatorio);
            }
            subrelatorio.setSreNomeParametro(sreNomeParametro);
            subrelatorio.setSreTemplateSql(sreTemplateSql);

            SubrelatorioHome.update(subrelatorio);
        } catch (FindException | UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erro.subrelatorio.editar", responsavel);
        }
    }

}
