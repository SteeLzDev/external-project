package com.zetra.econsig.service.consignacao;



import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.AutDesconto;

/**
 * <p>Title: DeferirConsignacaoControllerBean</p>
 * <p>Description: Session Bean para a operação de Deferimento/Indeferimento de Contrato.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface DeferirConsignacaoController  {

    public void deferir(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public void indeferir(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public void executarDeferimentoAutomatico(AcessoSistema responsavel) throws AutorizacaoControllerException;
    public String deferir(AutDesconto autdes, String senhaUtilizada, boolean consumirSenhaAutorizacao, boolean exigirSenhaAutorizacaoCadastrada, CustomTransferObject tipoMotivoOperacao, java.util.Date ocaPeriodo, AcessoSistema responsavel) throws AutorizacaoControllerException;
}
