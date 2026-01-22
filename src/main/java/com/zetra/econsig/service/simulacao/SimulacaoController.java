package com.zetra.econsig.service.simulacao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.PrazoTransferObject;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: SimulacaoController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface SimulacaoController {
    // Prazo
    public PrazoTransferObject findPrazo(PrazoTransferObject prazo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<PrazoTransferObject> findPrazoByServico(String svcCodigo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<PrazoTransferObject> findPrazoByServico(String svcCodigo, Short przAtivo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<PrazoTransferObject> findPrazoAtivoByServico(String svcCodigo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public String createPrazo(String svcCodigo, Short przVlr, AcessoSistema responsavel) throws SimulacaoControllerException;

    public String createPrazo(PrazoTransferObject prazo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public void updatePrazo(String przCodigo, Short przAtivo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public void updatePrazo(PrazoTransferObject prazo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> lstPrazoSvcCsa(AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> getPrazoCoeficiente(String svcCodigo, String csaCodigo, String orgCodigo, int dia, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> getPrazoCoeficiente(String svcCodigo, String csaCodigo, String orgCodigo, int dia, boolean validaPrazoRenegociacao, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> getPrazoCoeficiente(String svcCodigo, String csaCodigo, String orgCodigo, int dia, boolean validaBloqSerCnvCsa, boolean validaLimitePrazo, boolean validaPrazoRenegociacao, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> getPrazoCoeficienteEmprestimo(String orgCodigo, int dia, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> lstServicosSimulacao(String csaCodigo, String svcCodigo, String orgCodigo, short dia, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> lstServicosSimulacao(String csaCodigo, String orgCodigo, short dia, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> lstConsignatariasComTaxasAtivas(String svcCodigo, AcessoSistema responsavel) throws SimulacaoControllerException;

    // Prazo Consignataria
    public List<PrazoTransferObject> findPrazoCsaByServico(String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public String desbloqueiaPrazoCsa(String csaCodigo, String przCodigo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public void bloqueiaPrazoCsa(String csaCodigo, String przCodigo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<String> getSvcCodigosParaCadastroTaxas(String csaCodigo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<String> getSvcCodigosSemPrazoConvenioCsa(String csaCodigo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> lstServicosParaCadastroTaxas(String csaCodigo, AcessoSistema responsavel) throws SimulacaoControllerException;

    // Coeficientes
    public List<TransferObject> getCoeficienteAtivo(String csaCodigo, String svcCodigo, short prazo, BigDecimal vlrParcela,	BigDecimal vlrLiberado, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> getCoeficienteAtivo(String csaCodigo, String svcCodigo, short prazo, short dia, BigDecimal vlrParcela, BigDecimal vlrLiberado, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> getCoeficienteAtivo(String csaCodigo, String svcCodigo, String orgCodigo, String rseCodigo, short prazo, short dia, BigDecimal vlrParcela, BigDecimal vlrLiberado, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> getCoeficienteAtivo(String csaCodigo, String svcCodigo, String orgCodigo, String rseCodigo, short prazo, short dia, boolean validaBloqSerCnvCsa, BigDecimal vlrParcela, BigDecimal vlrLiberado, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> lstRegistrosSolicitacaoAutorizacao(String adeCodigo, List<String> tisCodigos, List<String> ssoCodigos, AcessoSistema responsavel) throws SimulacaoControllerException;

    public void aprovarAnexosSolicitacaoAutorizacao(String adeCodigo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public void reprovarAnexosSolicitacaoAutorizacao(String adeCodigo, String obsReprovacao, AcessoSistema responsavel) throws SimulacaoControllerException;

    public void assinarAnexosSolicitacaoAutorizacao(String adeCodigo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public void verificarAssinaturaAnexosSolicitacao(String adeCodigo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public String getTipoCoeficienteAtivo(String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> getCoeficienteMensal(String csaCodigo, String svcCodigo, int prazo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> getCoeficienteMensal(String csaCodigo, String svcCodigo, int prazo, boolean somenteComBloqueioCad, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> getCoeficienteDiario(String csaCodigo, String svcCodigo, int prazo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> getCoeficienteDiario(String csaCodigo, String svcCodigo, int prazo, boolean somenteComBloqueioCad, AcessoSistema responsavel) throws SimulacaoControllerException;

    public TransferObject getCoeficienteAtivo(String cftCodigo) throws SimulacaoControllerException;

    public String getSvcTaxaCompartilhada(String svcCodigo, boolean somenteComBloqueioCad, AcessoSistema responsavel) throws SimulacaoControllerException;

    // Coeficiente Desconto
    public String createCoeficienteDesconto(String adeCodigo, String cftCodigo, BigDecimal cdeVlrLiberado, BigDecimal cdeVlrLiberadoCalc, String cdeTxtContato, Short cdeRanking, BigDecimal cdeVlrTac, BigDecimal cdeVlrIof, AcessoSistema responsavel) throws SimulacaoControllerException ;

    public void updateCoeficienteDesconto(String cdeCodigo, String cftCodigo, BigDecimal cdeVlrLiberado, AcessoSistema responsavel) throws SimulacaoControllerException;

    public CustomTransferObject findCdeByAdeCodigo(String adeCodigo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public CustomTransferObject findCdeByAdeCodigo(String adeCodigo, boolean arquivado, AcessoSistema responsavel) throws SimulacaoControllerException;

    // Taxa de juros
    public List<TransferObject> getSvcPrazo(String csaCodigo, String svcCodigo, boolean prazo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> getSvcPrazo(String csaCodigo, String svcCodigo, boolean prazo, boolean prazoMultiploDoze, String prazosInformados, AcessoSistema responsavel) throws SimulacaoControllerException;

    public void setTaxaJuros(String csaCodigo, String svcCodigo, List<TransferObject> parametros, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> getTaxas(String periodo, String csaCodigo, String svcCodigo, Integer prazo, boolean ativo, boolean somenteComBloqueioCad, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> getLimiteTaxas(String svcCodigo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> getTaxaSuperiorTaxaLimite(String svcCodigo, Short prazoMax, BigDecimal taxaLimite, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> getRegraJurosTaxaLimite(String svcCodigo, Short prazoMax, BigDecimal taxaLimite, AcessoSistema responsavel) throws SimulacaoControllerException;

    public Map<String, String> getSvcCadTaxaBloqueado(AcessoSistema responsavel) throws SimulacaoControllerException;

    public BigDecimal calculaTAC(String svcCodigo, String csaCodigo, String orgCodigo, BigDecimal vlrLiberado, BigDecimal cftVlr, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> simularConsignacao(String svcCodigo, String orgCodigo, String rseCodigo, BigDecimal vlrParcela, BigDecimal vlrLiberado, short przVlr, Date adeAnoMesIni, boolean validaBloqSerCnvCsa, String adePeriodicidade, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> simularConsignacao(String csaCodigo, String svcCodigo, String orgCodigo, String rseCodigo, BigDecimal vlrParcela, BigDecimal vlrLiberado, short przVlr, Date adeAnoMesIni, boolean validaBloqSerCnvCsa, boolean utilizaLimiteTaxa, String adePeriodicidade, AcessoSistema responsavel) throws SimulacaoControllerException;

    public CustomTransferObject getDefinicaoTaxaJuros(String dtjCodigo) throws SimulacaoControllerException;

    public BigDecimal alterarValorTaxaJuros(boolean alteraVlrLiberado, BigDecimal vlrParcela, BigDecimal vlrLiberado, BigDecimal cftVlrNovo, BigDecimal adeTac, BigDecimal adeOp, int przVlr,
            String orgCodigo, String svcCodigo, String csaCodigo, String adePeridiocidade, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> selecionarLinhasSimulacao(List<TransferObject> simulacao, String rseCodigo, BigDecimal rseMargemRest, int qtdeConsignatariasSimulacao, boolean filtrarSomenteLimite, boolean restringirIdadeSimulacao, AcessoSistema responsavel) throws SimulacaoControllerException;

    public void ativaDesativaSvcPrazo(String svcCodigo, Short przAtivo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public void desativaSvcPrazoPorCsa(String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public void ativaSvcPrazoPorCsa(String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public void informarDocumentacaoCreditoEletronico(String adeCodigo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public TransferObject getParamSvcCsaMensagemSolicitacaoOutroSvc(String svcCodigo, String csaCodigo, short prazo, short dia, AcessoSistema responsavel) throws SimulacaoControllerException;

    public void copiaTaxaJuros(String svcCodigo, String svcCodigoDestino, String csaCodigo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> lstServicosSimulacao(String csaCodigo, String svcCodigo, String orgCodigo, short dia, String corCodigo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> lstRegistrosSolicitacao(String adeCodigo, List<String> tisCodigos, List<String> ssoCodigos, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> selecionarLinhasSimulacao(List<TransferObject> simulacao, String rseCodigo, BigDecimal rseMargemRest, int qtdeConsignatariasSimulacao, boolean filtrarSomenteLimite, boolean restringirIdadeSimulacao, String csaCodigoExclusaoRanking, String funcaoRanking, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> getCoeficienteSimulacao(String csaCodigo, String svcCodigo, String orgCodigo, String rseCodigo, BigDecimal vlrParcela, BigDecimal vlrLiberado, short numParcelas, boolean validaBloqSerCnvCsa, boolean utilizaLimiteTaxa, AcessoSistema responsavel) throws SimulacaoControllerException;

    public List<TransferObject> buscarDefinicaoTaxaJuros(String csaCodigo, String orgCodigo, String svcCodigo, String rseCodigo, BigDecimal valorTotal, BigDecimal valorContrato, Integer prazo, AcessoSistema responsavel) throws SimulacaoControllerException;

    public void setaRankingSimulacao(List<TransferObject> coeficientes, String funcaoRanking, AcessoSistema responsavel);

    public List<TransferObject> buscarTaxasParaConsignatarias(AcessoSistema responsavel, String rseCodigo, String orgCodigo, String svcCodigo, TransferObject ... consignatarias) throws SimulacaoControllerException;
}
