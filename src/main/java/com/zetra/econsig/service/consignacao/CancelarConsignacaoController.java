package com.zetra.econsig.service.consignacao;

import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: CancelarConsignacaoControllerBean</p>
 * <p>Description: Session Bean para a operação de Cancelamento de Contrato.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface CancelarConsignacaoController {

    public void cancelar(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void cancelar(String adeCodigo, boolean verificaStatusAde, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void cancelar(String adeCodigo, boolean verificaStatusAde, boolean verificaStatusServidor, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void cancelar(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void cancelar(String adeCodigo, boolean verificaStatusAde, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void cancelarExpiradas(List<String> sadCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void cancelarExpiradas(String rseCodigo, String adeNumero, List<String> sadCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void cancelarRenegociacao(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void descancelar(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void cancelarExpiradasCsa(String rseCodigo, String adeNumero, AcessoSistema responsavel) throws AutorizacaoControllerException;
}
