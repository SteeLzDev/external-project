package com.zetra.econsig.service.coeficiente;

import java.math.BigDecimal;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.CoeficienteCorrecaoTransferObject;
import com.zetra.econsig.exception.CoeficienteCorrecaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: CoeficienteCorrecaoController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface CoeficienteCorrecaoController {

    public String createCoeficienteCorrecao(CoeficienteCorrecaoTransferObject coeficiente, AcessoSistema responsavel) throws CoeficienteCorrecaoControllerException;

    public void updateCoeficienteCorrecao(CoeficienteCorrecaoTransferObject coeficiente, AcessoSistema responsavel) throws CoeficienteCorrecaoControllerException;

    public void updateCoeficienteCorrecaoValorAcumulado(CoeficienteCorrecaoTransferObject coeficiente, Boolean exclusao, AcessoSistema responsavel) throws CoeficienteCorrecaoControllerException;

    public void updateTipoCoeficienteCorrecao(CoeficienteCorrecaoTransferObject coeficiente, AcessoSistema responsavel) throws CoeficienteCorrecaoControllerException;

    public void removeCoeficienteCorrecao(CoeficienteCorrecaoTransferObject coeficiente, AcessoSistema responsavel) throws CoeficienteCorrecaoControllerException;

    public List<CoeficienteCorrecaoTransferObject> lstCoeficienteCorrecao(String ccrTccCodigo) throws CoeficienteCorrecaoControllerException;

    public List<TransferObject> lstTipoCoeficienteCorrecao() throws CoeficienteCorrecaoControllerException;

    public BigDecimal getCoeficienteCorrecao(String ccrTccCodigo, int mes, int ano) throws CoeficienteCorrecaoControllerException;

    public BigDecimal getPrimeiroCoeficienteCorrecao(String ccrTccCodigo, int mes, int ano, String strCorrecaoVlr) throws CoeficienteCorrecaoControllerException;

    public BigDecimal getPrimeiroCoeficienteCorrecao(String ccrTccCodigo, int mes, int ano) throws CoeficienteCorrecaoControllerException;

    public BigDecimal getUltimoCoeficienteCorrecao(String ccrTccCodigo, int mes, int ano, String strCorrecaoVlr) throws CoeficienteCorrecaoControllerException;

    public BigDecimal getUltimoCoeficienteCorrecao(String ccrTccCodigo, int mes, int ano) throws CoeficienteCorrecaoControllerException;
}
