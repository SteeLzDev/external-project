package com.zetra.econsig.service.financiamentodivida;


import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.PropostaPagamentoDividaTO;
import com.zetra.econsig.dto.entidade.SaldoDevedorTransferObject;
import com.zetra.econsig.dto.parametros.RenegociarConsignacaoParametros;
import com.zetra.econsig.exception.FinanciamentoDividaControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: FinanciamentoDividaController</p>
 * <p>Description: Session Façade para operações do módulo de financiamento
 * de dívida de cartão.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface FinanciamentoDividaController  {

    public List<PropostaPagamentoDividaTO> validarPropostasPgtSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO, List<PropostaPagamentoDividaTO> propostasPgtSaldo, AcessoSistema responsavel) throws FinanciamentoDividaControllerException;
    public void atualizaPropostasPagamentoSaldo(String adeCodigo, List<PropostaPagamentoDividaTO> propostasPgtSaldo, AcessoSistema responsavel) throws FinanciamentoDividaControllerException;
    public void informarPropostasPgtSdvTerceiros(String adeCodigo, List<PropostaPagamentoDividaTO> propostasPgtSaldo, AcessoSistema responsavel) throws FinanciamentoDividaControllerException;
    public List<TransferObject> lstPropostaPagamentoDivida(String adeCodigo, String csaCodigo, String stpCodigo, AcessoSistema responsavel) throws FinanciamentoDividaControllerException;
    public List<TransferObject> lstPropostaPagamentoDivida(String adeCodigo, String csaCodigo, String stpCodigo, boolean arquivado, AcessoSistema responsavel) throws FinanciamentoDividaControllerException;
    public boolean propostaAprovada(String ppdCodigo, AcessoSistema responsavel);

    public boolean exibeLinkSolicitacaoProposta(String adeCodigo, AcessoSistema responsavel) throws FinanciamentoDividaControllerException;
    public boolean temSolicitacaoProposta(String adeCodigo, boolean pendente, AcessoSistema responsavel) throws FinanciamentoDividaControllerException;
    public void solicitarPropostaPagamento(String adeCodigo, AcessoSistema responsavel) throws FinanciamentoDividaControllerException;
    public void aprovarPropostaPagamento(String adeCodigo, String ppdCodigo, AcessoSistema responsavel) throws FinanciamentoDividaControllerException;
    public List<Integer> calcularPrazosObrigProposta(String svcCodigo, String rseCodigo, String orgCodigo, String adeCodigo, boolean compra, AcessoSistema responsavel) throws FinanciamentoDividaControllerException;

    public List<TransferObject> acompanharFinanciamentoDivida(TransferObject criteriosPesquisa,  int offset, int count, AcessoSistema responsavel) throws FinanciamentoDividaControllerException;
    public int contarFinanciamentoDivida(TransferObject criteriosPesquisa,  AcessoSistema responsavel) throws FinanciamentoDividaControllerException;

    public void validarConclusaoFinanciamento(RenegociarConsignacaoParametros renegociarParam, String svcCodigo, AcessoSistema responsavel) throws FinanciamentoDividaControllerException;
    public void concluirFinanciamento(String adeCodigoNovo, List<String> adeCodigosRenegociacao, String ppdCodigo, String svcCodigo, boolean compra, AcessoSistema responsavel) throws FinanciamentoDividaControllerException;

    public void processarPrazoExpiracaoFinancDivida(AcessoSistema responsavel) throws FinanciamentoDividaControllerException;
}
