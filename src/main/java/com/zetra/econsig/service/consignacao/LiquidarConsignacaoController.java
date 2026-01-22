package com.zetra.econsig.service.consignacao;


import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.parametros.LiquidarConsignacaoParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: LiquidarConsignacaoControllerBean</p>
 * <p>Description: Session Bean para a operação de Liquidação de Contrato.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface LiquidarConsignacaoController  {
    public void liquidar(String adeCodigo, CustomTransferObject tipoMotivoOperacao, LiquidarConsignacaoParametros parametros, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void desliquidar(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public void desliquidarAoCancelarRenegociacao(List<String> adeCodigos, String ocsObs, String tmoCodigo, boolean permiteCancelarRenegMantendoMargemNegativa, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public void desliquidar(String adeCodigo, boolean validarMargem, boolean reimplantar, boolean ignoraCompra, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public boolean liquidacaoJaEnviadaParaFolha(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void solicitarLiquidacao(List<String> adeCodigos, String ocsObs, String tmoCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public void cancelarSolicitacaoLiquidacao(List<String> adeCodigos, String ocsObs, String tmoCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public void desliquidarPosCorte(String adeCodigo, boolean opcaoValidaMargem, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException;
}
