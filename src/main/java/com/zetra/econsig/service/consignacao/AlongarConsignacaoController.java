package com.zetra.econsig.service.consignacao;



import com.zetra.econsig.dto.parametros.AlongarConsignacaoParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: AlongarConsignacaoController</p>
 * <p>Description: Session Bean para a operação de Alongamento de Contrato.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface AlongarConsignacaoController  {

    public String alongar(AlongarConsignacaoParametros alongarParam, AcessoSistema responsavel) throws AutorizacaoControllerException;
}
