package com.zetra.econsig.delegate;

import java.math.BigDecimal;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.PropostaPagamentoDividaTO;
import com.zetra.econsig.dto.entidade.SaldoDevedorTransferObject;
import com.zetra.econsig.exception.SaldoDevedorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacao;
import com.zetra.econsig.service.saldodevedor.SaldoDevedorController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: SaldoDevedorDelegate</p>
 * <p>Description: Delegate de acesso ao Saldo Devedor Controller.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SaldoDevedorDelegate extends AbstractDelegate {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SaldoDevedorDelegate.class);

    private SaldoDevedorController saldoDevedorController;

    public SaldoDevedorDelegate() throws SaldoDevedorControllerException {
        try {
            saldoDevedorController = ApplicationContextProvider.getApplicationContext().getBean(SaldoDevedorController.class);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SaldoDevedorControllerException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    public boolean validarSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO, TransferObject dadosSaldosDevedoresMultiplos, List<PropostaPagamentoDividaTO> propostasPgtSaldo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        return saldoDevedorController.validarSaldoDevedor(saldoDevedorTO, dadosSaldosDevedoresMultiplos, propostasPgtSaldo, responsavel);
    }

    public BigDecimal calcularSaldoDevedor(String adeCodigo, boolean usaTaxaInformada, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        return saldoDevedorController.calcularSaldoDevedor(adeCodigo, usaTaxaInformada, responsavel);
    }

    public BigDecimal calcularSaldoDevedor(AutDesconto adeBean, String svcCodigo, String csaCodigo, String orgCodigo, boolean usaTaxaInformada, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        return saldoDevedorController.calcularSaldoDevedor(adeBean, svcCodigo, csaCodigo, orgCodigo, usaTaxaInformada, responsavel);
    }

    public SaldoDevedorTransferObject getSaldoDevedor(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        return getSaldoDevedor(adeCodigo, false, responsavel);
    }

    public SaldoDevedorTransferObject getSaldoDevedor(String adeCodigo, boolean arquivado, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        return saldoDevedorController.getSaldoDevedor(adeCodigo, arquivado, responsavel);
    }

    public void createSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO, TransferObject dadosSaldosDevedoresMultiplos, List<PropostaPagamentoDividaTO> propostasPgtSaldo, boolean isCompra, CustomTransferObject comunicacao, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        saldoDevedorController.createSaldoDevedor(saldoDevedorTO, dadosSaldosDevedoresMultiplos, propostasPgtSaldo, isCompra, comunicacao, responsavel);
    }

    public void createSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO, TransferObject dadosSaldosDevedoresMultiplos, List<PropostaPagamentoDividaTO> propostasPgtSaldo, boolean isCompra, boolean importacao, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        saldoDevedorController.createSaldoDevedor(saldoDevedorTO, dadosSaldosDevedoresMultiplos, propostasPgtSaldo, isCompra,null, importacao, responsavel);
    }

    public void updateSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO, TransferObject dadosSaldosDevedoresMultiplos, List<PropostaPagamentoDividaTO> propostasPgtSaldo, boolean isCompra, CustomTransferObject comunicacao, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        saldoDevedorController.updateSaldoDevedor(saldoDevedorTO, dadosSaldosDevedoresMultiplos, propostasPgtSaldo, isCompra, comunicacao, responsavel);
    }

    public void updateSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO, TransferObject dadosSaldosDevedoresMultiplos, List<PropostaPagamentoDividaTO> propostasPgtSaldo, boolean isCompra, boolean importacao, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        saldoDevedorController.updateSaldoDevedor(saldoDevedorTO, dadosSaldosDevedoresMultiplos, propostasPgtSaldo, isCompra,null, importacao, responsavel);
    }

    public void informarPagamentoSaldoDevedor(String adeCodigo, String obs, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        saldoDevedorController.informarPagamentoSaldoDevedor(adeCodigo, obs, null, null, null, responsavel);
    }

    public void informarPagamentoSaldoDevedor(String adeCodigo, String obs, String idAnexo, String aadNome, String aadDescricao, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        saldoDevedorController.informarPagamentoSaldoDevedor(adeCodigo, obs, idAnexo, aadNome, aadDescricao, responsavel);
    }

    public String solicitarSaldoDevedor(String adeCodigo, String obs, boolean solicitar, boolean isLiquidacao, int qtdParcelas, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        return saldoDevedorController.solicitarSaldoDevedor(adeCodigo, obs, solicitar, isLiquidacao, qtdParcelas, responsavel);
    }

    public String solicitarSaldoDevedorExclusaoServidor(String rseCodigo, String obs, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        return saldoDevedorController.solicitarSaldoDevedorExclusaoServidor(rseCodigo, obs, responsavel);
    }

    public void solicitarRecalculoSaldoDevedor(String adeCodigo, String obs, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        saldoDevedorController.solicitarRecalculoSaldoDevedor(adeCodigo, obs, responsavel);
    }

    public void rejeitarPagamentoSaldoDevedor(String adeCodigo, String obs, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        saldoDevedorController.rejeitarPagamentoSaldoDevedor(adeCodigo, obs, responsavel);
    }

    public void verificarBloqueioCsaSolicitacaoSaldoDevedor(AcessoSistema responsavel) throws SaldoDevedorControllerException {
        saldoDevedorController.verificarBloqueioCsaSolicitacaoSaldoDevedor(responsavel);
    }

    public boolean temSolicitacaoSaldoDevedor(String adeCodigo, boolean pendente, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        return saldoDevedorController.temSolicitacaoSaldoDevedor(adeCodigo, pendente, responsavel);
    }

    public boolean temSolicitacaoSaldoDevedorRespondida(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        return saldoDevedorController.temSolicitacaoSaldoDevedorRespondida(adeCodigo, responsavel);
    }

    public boolean temSolicitacaoSaldoDevedorLiquidacaoRespondida(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        return saldoDevedorController.temSolicitacaoSaldoDevedorLiquidacaoRespondida(adeCodigo, responsavel);
    }

    public List<SolicitacaoAutorizacao> lstSolicitacaoSaldoExclusaoPendente(String adeCodigo, AcessoSistema responsavel) throws SaldoDevedorControllerException {
        return saldoDevedorController.lstSolicitacaoSaldoExclusaoPendente(adeCodigo, responsavel);
    }
}
