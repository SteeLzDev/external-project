package com.zetra.econsig.service.consignacao;



import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: AutorizarConsignacaoControllerBean</p>
 * <p>Description: Session Bean para a operação de Autorização de Consignação.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface AutorizarConsignacaoController  {

    public void autorizar(String adeCodigo, String corCodigo, String senhaUtilizada, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException;
}
