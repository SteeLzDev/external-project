package com.zetra.econsig.service.coeficiente;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CoeficienteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: CoeficienteAtivoController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface CoeficienteAtivoController {
    public String createCoeficienteAtivo(TransferObject coeficiente, TransferObject coeficienteOld, AcessoSistema responsavel) throws CoeficienteControllerException;

    public void updateCoeficienteAtivo(TransferObject coeficienteAtivo, AcessoSistema responsavel) throws CoeficienteControllerException;

    public void removeCoeficienteAtivo(TransferObject coeficienteAtivo, AcessoSistema responsavel) throws CoeficienteControllerException;

    public TransferObject getCoeficienteAtivo(String cftCodigo) throws CoeficienteControllerException;

    public void limparCoeficientesInativos(AcessoSistema responsavel) throws CoeficienteControllerException;

    public void anteciparDataInicioCoeficiente(String cftData, String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws CoeficienteControllerException;

    public void modificaDataFimVigencia(String cftData, List<TransferObject> lstTaxas, AcessoSistema responsavel) throws CoeficienteControllerException;

    public int countOcorrenciaCoeficiente(String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws CoeficienteControllerException;

    public List<TransferObject> listarOcorrenciaCoeficiente(String csaCodigo, String svcCodigo, int offset, int count, AcessoSistema responsavel) throws CoeficienteControllerException;
}
