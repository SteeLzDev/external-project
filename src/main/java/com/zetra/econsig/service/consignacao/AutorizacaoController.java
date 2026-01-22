package com.zetra.econsig.service.consignacao;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.consignacao.PriceHelper.TabelaPrice;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.DadosAutorizacaoDesconto;
import com.zetra.econsig.persistence.entity.DadosServidor;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacao;
import com.zetra.econsig.persistence.entity.StatusSolicitacao;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;

/**
 * <p>Title: AutorizacaoControllerBean</p>
 * <p>Description: Interface remota do Session Bean para manipulacao de autorizações</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface AutorizacaoController  {

    // AutorizacaoControllerBean - Geral
    public boolean usuarioPodeModificarAde(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public boolean usuarioPodeModificarAde(String adeCodigo, boolean gravaLog, boolean lancaExcecao, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public boolean usuarioPodeConsultarAde(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public boolean usuarioPodeModificarAdeCompra(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public boolean usuarioPodeModificarAdeCompra(String adeCodigo, boolean gravaLog, boolean lancaExcecao, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void setDadoAutDesconto(String adeCodigo, String tdaCodigo, String dadValor, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public String getValorDadoAutDesconto(String adeCodigo, String tdaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public String getValorDadoAutDesconto(String adeCodigo, String tdaCodigo, boolean arquivado, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public String getValorDadoServidor(String serCodigo, String tdaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> lstDadoAutDesconto(String adeCodigo, String tdaCodigo, VisibilidadeTipoDadoAdicionalEnum visibilidade, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum acao, VisibilidadeTipoDadoAdicionalEnum visibilidade, String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> lstTodosTipoDadoAdicional(AcessoSistema responsavel) throws AutorizacaoControllerException;

    public BigDecimal corrigirValorPresente(BigDecimal adeVlr, java.util.Date dataEvento, String svcCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public boolean validarTetoDescontoPeloCargo(String rseCodigo, String svcCodigo, BigDecimal adeVlr, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public BigDecimal calcularValorDescontoParcela(String rseCodigo, String svcCodigo, BigDecimal adeVlr) throws AutorizacaoControllerException;

    public void liberarEstoque(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> historicoComposicaoMargem(String strRseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void setParamSvcADE(String adeCodigo, List<String> tpsCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public Map<String, String> getParamSvcADE(String adeCodigo, List<String> tpsCodigos, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> lstDuplicaParcela(String csaCodigo, String cnvCodVerba, String adeIndice, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> lstReajustaAde(String csaCodigo, CustomTransferObject regras, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public boolean temMargem(String rseCodigo, String svcCodigo, BigDecimal adeVlr, Short incMargem, boolean serAtivo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public String criaOcorrenciaADE(String adeCodigo, String tocCodigo, String ocaObs, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public String criaOcorrenciaADE(String adeCodigo, String tocCodigo, String ocaObs, java.util.Date ocaPeriodo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public String criaOcorrenciaADE(String adeCodigo, String tocCodigo, String ocaObs, BigDecimal ocaAdeVlrAnt, BigDecimal ocaAdeVlrNovo, Date ocaData, Date ocaPeriodo, String tmoCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public String criaOcorrenciaADEValidando(String adeCodigo, String tocCodigo, String ocaObs, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public String modificaSituacaoADE(AutDesconto autdes, String status, AcessoSistema responsavel) throws AutorizacaoControllerException;

    // Início - ValidacoesControllerBean
    public void validarEntidades(String cnvCodigo, String corCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public boolean podeReservarMargem(String cnvCodigo, String corCodigo, String rseCodigo, boolean validarEntidades, boolean serCnvAtivo, boolean serAtivo, List<String> adeCodigosRenegociacao,
    		BigDecimal adeVlr, BigDecimal adeVlrLiquido, Integer adePrazo, Integer adeCarencia, String adePeriodicidade, String adeIdentificador, Map<String, Object> parametros,
    		String acao, boolean incAvancadaValidaLimites, boolean telaConfirmacaoDuplicidade, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void verificaLimiteAoRetirarContratoCompra(String adeCodigoOrigem, String adeCodigoDestino, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public void verificaLimiteAoCancelarRenegociacao(String adeCodigoDestino, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public String verificaLimiteAoConsultarMargem(String rseCodigo, String orgCodigo, String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public BigDecimal[] validarTaxaJuros(BigDecimal adeVlr, BigDecimal adeVlrLiquido, BigDecimal adeVlrTac,
            BigDecimal adeVlrIof, BigDecimal adeVlrMensVinc, Integer adePrazo,
            Date adeData, Date adeAnoMesIni, String svcCodigo, String csaCodigo,
            String orgCodigo, boolean alteracao, Map<String, Object> parametros, String adePeriodicidade,
            String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void verificaBloqueioFuncao(String rseCodigo, String acao, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public CustomTransferObject getParametroSvc(String tpsCodigo, String svcCodigo, Object tipoRetorno, boolean nuloVerdadeiro, Map<String, Object> parametros) throws AutorizacaoControllerException;

    public void verificaLimiteAoAprovarLeilao(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void validarCancelamentoConsignacaoDentroPrazo(String adeCodigo, String svcCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> verificaLimiteServicosNaoAtigindos(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    // Fim - ValidacoesControllerBean

    public String modificaSituacaoADE(AutDesconto autdes, String status, AcessoSistema responsavel, boolean geraOcorrencia, boolean liberaMargem) throws AutorizacaoControllerException;

    public String modificaSituacaoADE(AutDesconto autdes, String status, AcessoSistema responsavel, boolean geraOcorrencia, java.util.Date ocaPeriodo, boolean liberaMargem) throws AutorizacaoControllerException;

    public void consumirSenhaDeAutorizacao(String adeCodigo, String sadCodigo, String rseCodigo, String svcCodigo, String csaCodigo, String senhaUtilizada, boolean exigeSenhaCadastrada, boolean inclusao, boolean confSolicitacao, boolean consultaMargem, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public TabelaPrice calcularTabelaPrice(CustomTransferObject autdes, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public Collection<OcorrenciaAutorizacao> findOcorrenciaByAdeTocUsuCodigo(String adeCodigo, String tocCodigo, String usuCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public Collection<OcorrenciaAutorizacao> findByAdeTocCodigoOcaPeriodo(String adeCodigo, String tocCodigo, java.sql.Date ocaPeriodo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public Collection<OcorrenciaAutorizacao> findOcorrenciaByAdeTocCsaCodigo(String adeCodigo, String tocCodigo, String csaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> obtemConsignacaoPorCnvSerQuery(String cnvCodigo, String rseCodigo, List<String> sadCodigos, CustomTransferObject criterio, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public boolean podePermitirDuplicidadeMotivadaUsuario(String cnvCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void salvarDadosAutorizacaoConsignacao(DadosServidor dadosServidor, List<DadosAutorizacaoDesconto> listaDadosAde, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public boolean podeConfirmarRenegociacao(BigDecimal adeVlr, String svcCodigo, String csaCodigo, BigDecimal vlrTotalRenegociacao, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> buscarStatusConsignacaoPorServidor(String rseCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public List<TransferObject> buscarNaturezaConsignacaoPorServidor(String rseCodigo, AcessoSistema responsavel)throws AutorizacaoControllerException;

    public Collection<OcorrenciaAutorizacao> findByAdeTocCodigo(String adeCodigo, String tocCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public String verificaAdeIndice(String adeCodigo, String rseCodigo, String cnvCodigo,
            String adeIndice, String adeCodReg, List<String> adeCodigosRenegociacao, boolean ignoraTpcAdeQQStatus, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void registraNotificacoesCse(List<String> adesCodigosIncluir, List<String> adesCodigosRemover, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void registraValorLiberadoConsignacao(List<String> adesCodigosIncluir, List<String> adesCodigosRemover, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public boolean verificaContratoSuspensoPodeRenegociar(String adeCodigo, String sadCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public StatusSolicitacao findStatusSolicitacao(String ssoCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public java.sql.Date calcularDataIniFimMargemExtra(String rseCodigo, java.sql.Date dataInicioFimAde, Short adeIncMargem, boolean periodoIni, boolean periodoFim, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public TransferObject verificaAdeTemDecisaoJudicial(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public void verificaAlteracaoReativacaoDecisaoJudicial(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;

    public boolean efetuarLiquidacaoDuasEtapas(AcessoSistema responsavel) throws ParametroControllerException;
    public boolean exigirDuplaConfirmacaoLiquidacao(AcessoSistema responsavel) throws ParametroControllerException;

    public void verificaBloqueioVinculoCnv(String csaCodigo, String svcCodigo, String vrsCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
    public void verificaBloqueioVinculoCnvAlertaSessao(HttpSession session, String csaCodigo, String svcCodigo, String vrsCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException;
}
