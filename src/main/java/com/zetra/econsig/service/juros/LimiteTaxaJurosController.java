package com.zetra.econsig.service.juros;

import java.math.BigDecimal;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.LimiteTaxaJurosControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: LimiteTaxaJurosController</p>
 * <p>Description: Interface bean para manipulação de limite de taxa de juros.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: $
 * $Date: $
 */
public interface LimiteTaxaJurosController {

    public List<TransferObject> listaLimiteTaxaJuros(CustomTransferObject criterio, int offset, int size, AcessoSistema responsavel) throws LimiteTaxaJurosControllerException;

    public int countLimiteTaxaJuros(CustomTransferObject criterio, AcessoSistema responsavel) throws LimiteTaxaJurosControllerException;

    public void removeLimiteTaxaJuros(CustomTransferObject limiteTaxaJuros, AcessoSistema responsavel) throws LimiteTaxaJurosControllerException;

    public String createLimiteTaxaJuros(CustomTransferObject limiteTaxaJuros, AcessoSistema responsavel) throws LimiteTaxaJurosControllerException;

    public void updateLimiteTaxaJuros(CustomTransferObject limiteTaxaJuros, AcessoSistema responsavel) throws LimiteTaxaJurosControllerException;

    public TransferObject findLimiteTaxaJuros(TransferObject limiteTaxaJuros, AcessoSistema responsavel) throws LimiteTaxaJurosControllerException;

    public List<TransferObject> listaLimiteTaxaJurosPorServico(List<TransferObject> servicos, BigDecimal taxaJuros, Short faixaPrazoInicial, Short faixaPrazoFinal, AcessoSistema responsavel) throws LimiteTaxaJurosControllerException;

}
