package com.zetra.econsig.service.bi;


import com.zetra.econsig.exception.ConsigBIControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ConsigBIController</p>
 * <p>Description: Interface remota do Session Bean para operações de BI</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ConsigBIController {

    public void atualizarBaseBI(int tipo, boolean populaDados, AcessoSistema responsavel) throws ConsigBIControllerException;
}
