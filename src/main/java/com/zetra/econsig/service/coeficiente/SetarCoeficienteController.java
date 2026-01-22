package com.zetra.econsig.service.coeficiente;


import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CoeficienteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: SetarCoeficienteController</p>
 * <p>Description: Session Façade para rotina de definição de Coeficientes.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface SetarCoeficienteController {
    public void setarCoeficienteMensal(List<TransferObject> coeficientes, AcessoSistema responsavel) throws CoeficienteControllerException;

    public void setarCoeficienteDiario(List<TransferObject> coeficientes, AcessoSistema responsavel) throws CoeficienteControllerException;
}
