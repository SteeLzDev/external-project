package com.zetra.econsig.service.sdp;

import java.math.BigDecimal;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.DespesaIndividualControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: DespesaIndividualController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface DespesaIndividualController {

    public String createDespesaIndividual(TransferObject despesaIndividual, ReservarMargemParametros margemParam, AcessoSistema responsavel) throws DespesaIndividualControllerException;

    public void alterarTaxaUso(String adeCodigo, BigDecimal adeVlr, AcessoSistema responsavel) throws DespesaIndividualControllerException;

    public void alterarTaxaUsoByRse(String rseCodigo, AcessoSistema responsavel) throws DespesaIndividualControllerException;

    public void cancelaDespesaIndividual(String adeCodigo, AcessoSistema responsavel) throws DespesaIndividualControllerException;

    public void cancelaDespesasIndividuais(String prmCodigo, AcessoSistema responsavel) throws DespesaIndividualControllerException;

    public List<TransferObject> findDespesasIndividuais(String decCodigo, AcessoSistema responsavel) throws DespesaIndividualControllerException;
}
