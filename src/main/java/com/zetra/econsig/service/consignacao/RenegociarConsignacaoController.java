package com.zetra.econsig.service.consignacao;



import com.zetra.econsig.dto.parametros.RenegociarConsignacaoParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: RenegociarConsignacaoControllerBean</p>
 * <p>Description: Session Bean para a operação de Renegociação de Contrato.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface RenegociarConsignacaoController  {

    public String renegociar(RenegociarConsignacaoParametros renegociarParam, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void liberaMargemRenegociacaoPrazoExpirado(AcessoSistema responsavel) throws AutorizacaoControllerException;
}
