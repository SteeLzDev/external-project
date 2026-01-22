package com.zetra.econsig.service.cartaocredito;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ValidadorCartaoCreditoControllerBean</p>
 * <p>Description: Session Bean para validações de operaçãos envolvendo consignações de cartão de crédito.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ValidadorCartaoCreditoController {
    public List<TransferObject> validaLancamentoCartaoCredito(String rseCodigo, BigDecimal adeVlr, String cnvCodigo, String codigoServicoCartao, Date periodo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void validaAlteracaoReservaCartaoCredito(String rseCodigo, BigDecimal vlrAlteracaoReserva, String cnvCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void validaProvisiontamentoMargem(String rseCodigo, List<String> adeCodigos, boolean excluirAdesLista, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public MargemTO consultarMargemDisponivelLancamento(String rseCodigo, String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public MargemTO consultarMargemDisponivelLancamento(String rseCodigo, String csaCodigo, String svcCodigo, Date periodoLancamento, AcessoSistema responsavel) throws AutorizacaoControllerException;
    
    public boolean isReservaCartao(String svcCodigo) throws AutorizacaoControllerException;

    List<TransferObject> determinaReservaCartao(List<TransferObject>  contratos) throws AutorizacaoControllerException;

}
