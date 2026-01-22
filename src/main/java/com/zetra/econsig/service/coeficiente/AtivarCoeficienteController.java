package com.zetra.econsig.service.coeficiente;



import com.zetra.econsig.exception.CoeficienteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: AtivarCoeficienteController</p>
 * <p>Description: Session Façade para rotina de ativação de Coeficientes.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface AtivarCoeficienteController {
    public void ativarCoeficienteMensal(String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws CoeficienteControllerException;

    public void ativarCoeficienteMensal(String csaCodigo, String svcCodigo, int prazo, AcessoSistema responsavel) throws CoeficienteControllerException;

    public void ativarCoeficienteDiario(String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws CoeficienteControllerException;

    public void ativarCoeficienteDiario(String csaCodigo, String svcCodigo, int prazo, AcessoSistema responsavel) throws CoeficienteControllerException;
}
