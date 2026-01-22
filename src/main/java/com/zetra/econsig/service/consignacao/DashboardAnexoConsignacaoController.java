package com.zetra.econsig.service.consignacao;

import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: DashboardAnexoConsignacaoController</p>
 * <p>Description: Session Bean para operação de dashborad anexo de consignação.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface DashboardAnexoConsignacaoController {

    public int countCsasPendenciaAnexo(CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> listCsasPendenciaAnexo(CustomTransferObject criterio, int offset, int count, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public int countConsignacaoComAnexo(CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public int countConsignacaoSemAnexo(CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> listConsignacaoComAnexo(CustomTransferObject criterio, int offset, int count, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> listConsignacaoSemAnexo(CustomTransferObject criterio, int offset, int count, AcessoSistema responsavel) throws AutorizacaoControllerException;
}
