package com.zetra.econsig.service.sistema;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.Ajuda;

/**
 * <p>Title: AjudaController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface AjudaController  {

    public void excluirAjuda(String acrCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void excluirAjuda(List<String> acrCodigos, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void editarAjuda(List<String> acrCodigos, String ajuTitulo, String ajuTexto, Short ajuSequencia, AcessoSistema responsavel) throws ConsignanteControllerException;

    public Ajuda findAjudaByPrimaryKey(String acrCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> lstTopicosAjuda(String papCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> lstFuncoesPapeisAcessoRecurso(List<String> acrCodigos, String acrRecurso, List<String> funCodigos, String acrParametro, String acrOperacao, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> listarAjudaRecurso(String acrCodigo, AcessoSistema responsavel) throws ConsignanteControllerException;
}
