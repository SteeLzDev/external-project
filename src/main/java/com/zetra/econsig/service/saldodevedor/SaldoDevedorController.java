package com.zetra.econsig.service.saldodevedor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.PropostaPagamentoDividaTO;
import com.zetra.econsig.dto.entidade.SaldoDevedorTransferObject;
import com.zetra.econsig.exception.SaldoDevedorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacao;
import com.zetra.econsig.values.StatusSolicitacaoEnum;

/**
 * <p>Title: SaldoDevedorControllerException</p>
 * <p>Description: Session Façade para cadastro de saldo devedor.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface SaldoDevedorController  {

    public boolean validarSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO, TransferObject dadosSaldosDevedoresMultiplos, List<PropostaPagamentoDividaTO> propostasPgtSaldo, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public BigDecimal calcularSaldoDevedor(String adeCodigo, boolean usaTaxaInformada, AcessoSistema responsavel) throws SaldoDevedorControllerException;
    public BigDecimal calcularSaldoDevedor(AutDesconto adeBean, String svcCodigo, String csaCodigo, String orgCodigo, boolean usaTaxaInformada, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public SaldoDevedorTransferObject getSaldoDevedor(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException;
    public SaldoDevedorTransferObject getSaldoDevedor(String adeCodigo, boolean arquivado, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public void createSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO, TransferObject dadosSaldosDevedoresMultiplos, List<PropostaPagamentoDividaTO> propostasPgtSaldo, boolean isCompra, CustomTransferObject comunicacao, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public void createSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO, TransferObject dadosSaldosDevedoresMultiplos, List<PropostaPagamentoDividaTO> propostasPgtSaldo, boolean isCompra, CustomTransferObject comunicacao, boolean importacao, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public void updateSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO, TransferObject dadosSaldosDevedoresMultiplos, List<PropostaPagamentoDividaTO> propostasPgtSaldo, boolean isCompra, CustomTransferObject comunicacao, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public void updateSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO, TransferObject dadosSaldosDevedoresMultiplos, List<PropostaPagamentoDividaTO> propostasPgtSaldo, boolean isCompra, CustomTransferObject comunicacao, boolean importacao, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public void removeSaldoDevedor(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public void informarPagamentoSaldoDevedor(String adeCodigo, String obs, AcessoSistema responsavel) throws SaldoDevedorControllerException;
    public void informarPagamentoSaldoDevedor(String adeCodigo, String obs, String idAnexo, String aadNome, String aadDescricao, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public void removePagamentoSaldoDevedor(String adeCodigo) throws SaldoDevedorControllerException;

    public String solicitarSaldoDevedor(String adeCodigo, String obs, boolean solicitar, boolean isLiquidacao, int qtdParcelas, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public String solicitarSaldoDevedorExclusaoServidor(String rseCodigo, String obs, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public void solicitarRecalculoSaldoDevedor(String adeCodigo, String obs, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public void rejeitarPagamentoSaldoDevedor(String adeCodigo, String obs, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public void aprovarSaldoDevedor(String adeCodigo, boolean aprovado, String obs, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public TransferObject recuperaDadosSaldosDevedoresMultiplos(String adeCodigo);

    public void verificarBloqueioCsaSolicitacaoSaldoDevedor(AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public Map<String, Boolean> consignatariaNaoPossuiPendenciaSaldoDevedor(String csaCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public boolean existeSaldoDevedorPago(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public boolean temSolicitacaoSaldoDevedor(String adeCodigo, boolean pendente, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public boolean temSolicitacaoSaldoDevedorRespondida(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public boolean temSolicitacaoSaldoDevedorLiquidacaoRespondida(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public boolean temSolicitacaoSaldoInformacaoApenas(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public void finalizaSolicitacaoSaldoDevedorLiquidacaoContrato(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public void atualizaStatusSolicitacaoSaldoDevedor(String adeCodigo, StatusSolicitacaoEnum novoStatus, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public boolean exibeLinkBoletoSaldo(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public boolean informarComprovantePagamentoSaldoDevedor(String adeCodigo, String idAnexo, String aadNome, String aadDescricao, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public List<ConsignatariaTransferObject> lstSolicitacaoSaldoPagoComAnexoNaoLiquidado(String csaCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public List<SolicitacaoAutorizacao> lstSolicitacaoSaldoExclusaoPendente(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public List<SolicitacaoAutorizacao> lstSolicitacaoSaldoExclusao(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public String solicitarSaldoDevedorRescisao(String rseCodigo, String ocaObs, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public String solicitarSaldoDevedor(String adeCodigo, String obs, boolean solicitar, boolean isLiquidacao, boolean isExclusao, Date soaDataValidade, int qtdParcelas, AcessoSistema responsavel) throws SaldoDevedorControllerException;

    public void alterarAdeTaxaJuros(String adeCodigo, BigDecimal adeTaxaJuros, AcessoSistema responsavel) throws SaldoDevedorControllerException;
}
