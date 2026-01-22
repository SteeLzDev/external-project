package com.zetra.econsig.service.coeficiente;

import java.math.BigDecimal;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CoeficienteControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: CoeficienteController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface CoeficienteController {
    public String createCoeficiente(TransferObject coeficiente, AcessoSistema responsavel) throws CoeficienteControllerException;

    public TransferObject getCoeficiente(String cftCodigo, AcessoSistema responsavel) throws CoeficienteControllerException;

    public BigDecimal getCftVlrByAdeCodigo(String adeCodigo, AcessoSistema responsavel) throws CoeficienteControllerException;

    public String insertCoeficiente(TransferObject coeficiente, AcessoSistema responsavel) throws SimulacaoControllerException;
}