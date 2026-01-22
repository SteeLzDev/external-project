package com.zetra.econsig.service.solicitacaosuporte;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.SolicitacaoSuporteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: SolicitacaoSuporteController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface SolicitacaoSuporteController {

    public Map<String, String> lstValoresCampoSolicitacaoSuporte(String campo, AcessoSistema responsavel) throws SolicitacaoSuporteControllerException;

    public String criarSolicitacaoSuporte(TransferObject solicitacaoSuporte, AcessoSistema responsavel) throws SolicitacaoSuporteControllerException;

    public TransferObject findSolicitacaoSuporte(String sosCodigo, AcessoSistema responsavel) throws SolicitacaoSuporteControllerException;

    public List<TransferObject> lstSolicitacaoSuporte(String usuCodigo, AcessoSistema responsavel) throws SolicitacaoSuporteControllerException;

    public String atualizaSolicitacaoSuporte(TransferObject solicitacaoSuporte, String sosCodigo, AcessoSistema responsavel) throws SolicitacaoSuporteControllerException;

    public List<TransferObject> lstTodasSolicitacaoSuporte(AcessoSistema responsavel) throws SolicitacaoSuporteControllerException;

}
