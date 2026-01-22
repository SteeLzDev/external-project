package com.zetra.econsig.service.relatorio;

import java.util.Collection;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.Subrelatorio;
/**
 * <p>Title: SubrelatorioController</p>
 * <p>Description: Interface de subrelatorios</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

public interface SubrelatorioController {
    
    public Collection<Subrelatorio> lstSubrelatorio(String relCodigo) throws FindException;
    
    public Subrelatorio buscaSubrelatorioEditavel(String sreCodigo, String relCodigo) throws FindException;
    
    public void removeSubrelatorioEditavel(String sreCodigo, String relCodigo) throws RemoveException, FindException;
    
    public void inserirSubrelatorio(String relCodigo, String nomeAnexoSubrelatorio, String sreNomeParametro, String sreTemplateSql, AcessoSistema responsavel)  throws MissingPrimaryKeyException , ZetraException;
    
    public void editarSubrelatorio(String sreCodigo, String relCodigo, String nomeAnexoSubrelatorio, String sreNomeParametro, String sreTemplateSql, boolean removerArquivo, AcessoSistema responsavel)  throws MissingPrimaryKeyException , ZetraException;

    List<TransferObject> listarSubrelatorio(CustomTransferObject filtro, int offset, int count, AcessoSistema responsavel) throws ZetraException;

}
