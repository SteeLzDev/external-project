package com.zetra.econsig.service.consignacao;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: PesquisarConsignacaoController</p>
 * <p>Description: Session Bean para operação de pesquisa de consignação.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface PesquisarConsignacaoController  {

    public List<TransferObject> pesquisaAutorizacao(String rseCodigo, String cnvCodigo, List<String> sadCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public List<TransferObject> pesquisaAutorizacaoPorRseSadNse(String rseCodigo, List<String> sadCodigos, List<String> nseCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public List<TransferObject> pesquisaAutorizacaoPorVerba(String rseCodigo, List<String> cnvCodVerba, List<String> sadCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public int listaConsignacoesAtivasCsa(String csaCodigo, String adeIdentificador, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> pesquisaAutorizacao(String tipo, String codigo, String rseCodigo, List<String> adeNumero, List<String> adeIdentificador, List<String> sadCodigos, List<String> svcCodigos, CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public List<TransferObject> pesquisaAutorizacao(String tipo, String codigo, String rseCodigo, List<String> adeNumero, List<String> adeIdentificador, List<String> sadCodigos, List<String> svcCodigos, int offset, int count, CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public int countPesquisaAutorizacao(String tipo, String codigo, String rseCodigo, List<String> adeNumero, List<String> adeIdentificador, List<String> sadCodigos, List<String> svcCodigos, CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public BigDecimal obtemTotalValorContratosAtivos(AcessoSistema responsavel) throws AutorizacaoControllerException;
    public BigDecimal obtemTotalValorConsignacaoPorCodigo(List<String> adeCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public TransferObject findAutDesconto(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public TransferObject findAutDescontoByAdeNumero(Long adeNumero, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public CustomTransferObject buscaAutorizacao(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public CustomTransferObject buscaAutorizacao(String adeCodigo, boolean arquivado, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public List<TransferObject> buscaAutorizacao(List<String> adeCodigo, boolean validaPermissao, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> pesquisaAdeOrigem(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public int contarCompraContratos(TransferObject parametros, String csaCodigo, String corCodigo, List<String> orgCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public List<TransferObject> pesquisarCompraContratos(TransferObject parametros, String csaCodigo, String corCodigo, String orgCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public List<TransferObject> pesquisarCompraContratos(TransferObject parametros, String csaCodigo, String corCodigo, String orgCodigo,  int offset, int count, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public String getAdeRelacionamentoCompra(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> historicoAutorizacao(String codigo, boolean mostraTodoHistorico, boolean arquivado, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public List<TransferObject> hstOrigemTerceiro(String autCode, String autCodeDest) throws AutorizacaoControllerException;

    public List<TransferObject> lstStatusAutorizacao(AcessoSistema responsavel) throws AutorizacaoControllerException;
    public List<TransferObject> lstStatusAutorizacao(List<String> sadCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public List<TransferObject> lstStatusAutorizacao(List<String> sadCodigos, boolean filtraApenasSadExibeSim, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public Map<String, String> selectStatusAutorizacao(AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> pesquisarContratosIncComp(String rseCodigo, String svcCodigo, String svcPrioridade, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public List<TransferObject> pesquisarContratosPorRseSvc(String rseCodigo, String svcCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> pesquisarContratosComParcela(String rseCodigo, List<String> sadCodigos, List<String> svcCodigos, List<String> csaCodigos, List<Long> adeNumeros, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public boolean isDestinoRenegociacao(String adeCodigo) throws AutorizacaoControllerException;

    public boolean isDestinoRenegociacaoPortabilidade(String adeCodigo) throws AutorizacaoControllerException;

    public boolean isDestinoRelacionamento(String adeCodigo, String tntCodigo) throws AutorizacaoControllerException;

    public BigDecimal pesquisarVlrCapitalDevidoAberto(String rseCodigo, String orgCodigo, String svcCodigo, List<String> adeCodigosRenegociacao, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> pesquisarConsignacaoRelacionamento(String adeCodigoOrigem, String adeCodigoDestino, String csaCodigoOrigem, String csaCodigoDestino, String tntCodigo, List<String> stcCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> pesquisaRejeitoPgtSaldo(String csaCodigo, int offset, int count, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public int countRejeitoPgtSaldo(String codigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> pesquisarAutorizacoes(List<String> adeCodigos, String tipo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public long getNextAdeNumero (String vcoCodigo, Date anoMesIni, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> pesquisarContratosDeferManualDataMenor(String rseCodigo, java.util.Date adeData, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<String> ordenarContratosPorDataCrescente(List<String> adeCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public int obtemTotalConsignacaoPorRse(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> obtemTotalConsignacaoPorCsa(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> pesquisarContratosAguardandoLiquidacao(List<TransferObject> adeCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<String> listarConsignacoesReativacaoAutomatica(AcessoSistema responsavel) throws AutorizacaoControllerException;

    public TransferObject obtemDadosUsuarioUltimaOperacaoAde(String adeCodigo, String tocCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> pesquisaAutorizacaoRsePorCsa(String rseCodigo, String csaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> pesquisarConsignacoesSolicitacoesLiquidacaoNaoAntendida(String csaCodigo, boolean count, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public int countConsignacoesSolicitacoesLiquidacaoNaoAntendida(String csaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public BigDecimal obterTotalAdeVlrPorPeriodoInclusao(String rseCodigo, java.util.Date dataReservaInicial, java.util.Date dataReservaFinal, Short adeIncMargem, List<String> adeCodigosExclusao, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public BigDecimal obterTotalAdeVlrPorPeriodoInclusao(String rseCodigo, java.util.Date dataReservaInicial, java.util.Date dataReservaFinal, Short adeIncMargem, List<String> adeCodigosExclusao, boolean verificaAdeAnoMesIni, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> listaContratosParcelasReimplanteManual(String rseCodigo, String csaCodigo, List<Long> adeNumeros, AcessoSistema responsavel)  throws AutorizacaoControllerException;

    public int countContratosParcelasReimplanteManual(String rseCodigo, String csaCodigo, List<Long> adeNumeros, AcessoSistema responsavel)  throws AutorizacaoControllerException;

    public List<TransferObject> pesquisaAutorizacaoSemParcela(List<String> adeNumero, String rseCodigo, CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public int countPesquisaAutorizacaoSemParcela(List<String> adeNumero, String rseCodigo, CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> pesquisarConsignacaoConciliacao(String orgIdentificador, java.util.Date periodo, List<String> cpf, List<Long> adeNumero, List<String> adeIdentificador, String statusPagamento, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> verificaAdeReservaTrazLancamentos(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public int findAdeReimplanteLoteCount(List<String> adeNumero, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> findAdeReimplanteLote(List<String> adeNumero, int offset, int size, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> lstContratosCsaSemOcorrencias(String csaCodigo, String corCodigo, List<String> tocCodigos, List<String> sadCodigos, Date ocaPeriodo, int offset, int count, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> lstAdeVlrPorPeriodoInclusao(String rseCodigo, java.util.Date dataReservaInicial, java.util.Date dataReservaFinal, Short adeIncMargem, List<String> adeCodigosExclusao, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> findConsignacaoSerByAdeCodigo(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public int obterTotalReservaCartaoSemLancamento(String csaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> lstContratosPortabilidadeCartao(String csaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public int obterTotalContratosPortabilidadeCartaoCsa(String csaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public int contaContratosNaoPossuemRelacionamentoVerbaRescisoria(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public boolean findByOrigemOuDestino(boolean origem, String adeCodigo, String tntCodigo, AcessoSistema responsavel);

    public List<TransferObject> lstConsignacaoParaAutorizacaoDoServidor(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public BigDecimal ObtemTotalValorConsignacaoPorRseCnv(String rseCodigo, List<String> cnvCodigos, Date periodoAtual, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public BigDecimal obtemTotalValorContratosRsePorMargem(String rseCodigo, Short marCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> listaTotalConsignacaoAtivasPorOrgao(AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> listaSolicitacaoSaldoDevedorPorRegistroServidor(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
}
