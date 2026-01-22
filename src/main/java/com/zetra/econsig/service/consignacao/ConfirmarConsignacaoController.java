package com.zetra.econsig.service.consignacao;

import java.math.BigDecimal;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.parametros.ConfirmarConsignacaoParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ConfirmarConsignacaoControllerBean</p>
 * <p>Description: Session Bean para a operação de Confirmação de Consignação.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ConfirmarConsignacaoController  {

    public void confirmar(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException;
    
    public void confirmar(String adeCodigo, CustomTransferObject tipoMotivoOperacao, ConfirmarConsignacaoParametros confirmAdeParams, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void confirmar(String adeCodigo, BigDecimal adeVlr, String adeIdentificador, String adeBanco, String adeAgencia, String adeConta, String corCodigo, Integer adePrazo,
            String senhaUtilizada, String codAutorizacao, boolean comSerSenha, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void confirmar(String adeCodigo, BigDecimal adeVlr, String adeIdentificador, String adeBanco, String adeAgencia, String adeConta, String corCodigo, Integer adePrazo,
            String senhaUtilizada, String codAutorizacao, boolean comSerSenha, String tdaModalidadeOp, String tdaMatriculaCsa, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException;
    
    public void confirmar(String adeCodigo, BigDecimal adeVlr, String adeIdentificador, String adeBanco, String adeAgencia, String adeConta, String corCodigo, Integer adePrazo,
            String senhaUtilizada, String codAutorizacao, boolean comSerSenha, String tdaModalidadeOp, String tdaMatriculaCsa, CustomTransferObject tipoMotivoOperacao, ConfirmarConsignacaoParametros confirmAdeParams, AcessoSistema responsavel) throws AutorizacaoControllerException;

}
