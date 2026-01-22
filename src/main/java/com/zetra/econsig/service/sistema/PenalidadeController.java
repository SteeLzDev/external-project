package com.zetra.econsig.service.sistema;


import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: PenalidadeController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface PenalidadeController  {

    public String insereTipoPenalidade(String descricao, Short prazo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void alteraTipoPenalidade(String codigo, String descricao, Short prazo, AcessoSistema responsavel) throws ConsignanteControllerException;

    public void excluiTipoPenalidade(String codigo, String descricao, AcessoSistema responsavel) throws ConsignanteControllerException;

    public List<TransferObject> lstTiposPenalidade(AcessoSistema responsavel) throws ConsignanteControllerException;

    public TransferObject findTipoPenalidade(String codigo, AcessoSistema responsavel) throws ConsignanteControllerException;
}
