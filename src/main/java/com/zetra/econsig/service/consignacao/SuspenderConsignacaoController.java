package com.zetra.econsig.service.consignacao;

import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.parametros.ReativarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.SuspenderConsignacaoParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.FuncaoAlteraMargemAde;

/**
 * <p>Title: SuspenderConsignacaoControllerBean</p>
 * <p>Description: Session Bean para a operação de Suspensão/Reativação de Consignação.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface SuspenderConsignacaoController  {

    public void suspender(String adeCodigo, CustomTransferObject tipoMotivoOperacao, SuspenderConsignacaoParametros parametros, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void reativar(String adeCodigo, CustomTransferObject tipoMotivoOperacao, ReativarConsignacaoParametros parametros, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void removerDataReativacaoAutomatica(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void suspenderContratosParcelaRejeitada(AcessoSistema responsavel) throws AutorizacaoControllerException;

    public FuncaoAlteraMargemAde buscarFuncaoAlteraMargemAde(String funCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> verificaContratosForamSuspensosPrdRejeitada(List<String> adeCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public boolean contratoSuspensoPrdRejeitadaReativado(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public boolean contratoSuspensoPrdRejeitadaNaoReativado(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
}