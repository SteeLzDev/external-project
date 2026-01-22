package com.zetra.econsig.service.ambiente;


import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ValidacaoAmbienteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.RegraValidacaoEnum;

/**
 * <p>Title: ValidacaoAmbienteController</p>
 * <p>Description: Interface remota para os métodos de negócio de Validação de Ambiente.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ValidacaoAmbienteController {

    public void verificarRegraValidacaoAmbiente(AcessoSistema responsavel) throws ValidacaoAmbienteControllerException;

    public List<TransferObject> obterValorRegraValidacaoAmbiente(RegraValidacaoEnum regraValidacaoEnum) throws ValidacaoAmbienteControllerException;
}
