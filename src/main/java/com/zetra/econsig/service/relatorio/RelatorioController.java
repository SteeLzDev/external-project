package com.zetra.econsig.service.relatorio;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.helper.Pair;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.TipoFiltroRelatorio;
import com.zetra.econsig.report.jasper.dto.AvaliacaoFaqBean;
import com.zetra.econsig.report.jasper.dto.ComprometimentoBean;
import com.zetra.econsig.report.jasper.dto.ContratosPorCategoriaBean;
import com.zetra.econsig.report.jasper.dto.ContratosPorCrsBean;
import com.zetra.econsig.report.jasper.dto.ContratosPorCsaBean;
import com.zetra.econsig.report.jasper.dto.ContratosPorSvcBean;
import com.zetra.econsig.report.jasper.dto.CorPorCsaBean;
import com.zetra.econsig.report.jasper.dto.EstatisticoProcessamentoBean;
import com.zetra.econsig.report.jasper.dto.GerencialEstatiscoMargemBean;
import com.zetra.econsig.report.jasper.dto.GerencialGeralTaxasEfetivasBean;
import com.zetra.econsig.report.jasper.dto.GerencialInadimplenciaBean;
import com.zetra.econsig.report.jasper.dto.GerencialInadimplenciaEvolucaoBean;
import com.zetra.econsig.report.jasper.dto.GerencialQtdeSerPorFaixaMargemBean;
import com.zetra.econsig.report.jasper.dto.GerencialTaxasBean;
import com.zetra.econsig.report.jasper.dto.InadimplenciaBean;
import com.zetra.econsig.report.jasper.dto.RegrasConvenioMargensBean;
import com.zetra.econsig.report.jasper.dto.RegrasConvenioOrgaoBean;
import com.zetra.econsig.report.jasper.dto.RegrasConvenioParametrosBean;
import com.zetra.econsig.report.jasper.dto.RegrasConvenioServicosBean;
import com.zetra.econsig.report.jasper.dto.RelatorioConfCadMargemBean;
import com.zetra.econsig.report.jasper.dto.ServicoOperacaoMesBean;
import com.zetra.econsig.report.jasper.dto.ServidorPorCrsBean;
import com.zetra.econsig.report.jasper.dto.ServidorPorEstBean;
import com.zetra.econsig.report.jasper.dto.ServidorPorOrgBean;
import com.zetra.econsig.report.jasper.dto.ServidorPorTipoBean;
import com.zetra.econsig.report.jasper.dto.TermoUsoPrivacidadeAdesaoBean;

/**
 * <p>Title: RelatorioController</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface RelatorioController {

    public void geraRelatorioIntegracao(String estCodigo, String orgCodigo, AcessoSistema responsavel) throws RelatorioControllerException;

    public void gerarRelatorioRepasse(AcessoSistema responsavel) throws RelatorioControllerException;

    public List<TransferObject> lstRelatorio (CustomTransferObject filtro) throws RelatorioControllerException;

    public List<TransferObject> lstRelatorioCustomizado(CustomTransferObject filtro, int offset, int count, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<TransferObject> lstRelatorioTipo (CustomTransferObject filtro, AcessoSistema responsavel) throws RelatorioControllerException;

    public Collection<TipoFiltroRelatorio> lstTipoFiltroRelatorio(AcessoSistema responsavel) throws RelatorioControllerException;

    public Collection<TipoFiltroRelatorio> lstTipoFiltroRelatorioEditavel(AcessoSistema responsavel) throws RelatorioControllerException;

    public Pair<String[], List<TransferObject>> geraRelatorioSinteticoConsignacoes(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException;

    public Pair<String[], List<TransferObject>> geraRelatorioSinteticoDescontos(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException;

    public Pair<String[], List<TransferObject>> geraRelatorioSinteticoMovFin(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<TransferObject> lstTaxasEfetivasContratos(String periodo, String orgCodigo, List<String> svcCodigos, List<String> sadCodigos, boolean prazoMultiploDoze, List<Integer> prazosInformados, String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException;

    public GerencialInadimplenciaBean geraRelatorioInadimplencia(Date periodo, AcessoSistema responsavel) throws RelatorioControllerException;

    public GerencialInadimplenciaBean geraRelatorioInadimplenciaConsignataria(Date periodo, List<String> csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<GerencialInadimplenciaEvolucaoBean> geraRelatorioInadimplenciaEvolucao(List<Date> periodos, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<GerencialEstatiscoMargemBean> lstEstatiscoMargem(AcessoSistema responsavel) throws RelatorioControllerException;

    public List<GerencialQtdeSerPorFaixaMargemBean> lstQtdeSerPorFaixaMargem(BigDecimal mediaMargem, BigDecimal desvioMargem, Short incideMargem, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<ComprometimentoBean> lstComprometimento(TransferObject criterio, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<ContratosPorCsaBean> lstContratosPorCsa(int maxResultados, String periodo, boolean csaAtivo, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<ContratosPorCsaBean> lstContratosPorCsa(int maxResultados, String periodo, List<String> csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<CorPorCsaBean> lstCorPorCsa(int maxResultados, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<ContratosPorCrsBean> lstContratosPorCrs(int maxResultados, String periodo, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<ContratosPorCategoriaBean> lstContratosPorCategoria(int maxResultados, String periodo, AcessoSistema responsavel) throws RelatorioControllerException;

    public int qtdeContratosPorCategoria(String periodo, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<ContratosPorCategoriaBean> lstContratosPorCategoria(int maxResultados, String periodo, List<String> csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException;

    public int qtdeContratosPorCategoria(String periodo, List<String> csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<ContratosPorSvcBean> lstContratosPorSvc(int maxResultados, String periodo, boolean internacional, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<ContratosPorSvcBean> lstContratosPorSvcRelGerencialCsa(int maxResultados, String periodo, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<ServidorPorCrsBean> lstServidorPorCrs(int maxResultados, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<ServidorPorEstBean> lstServidorPorEst(AcessoSistema responsavel) throws RelatorioControllerException;

    public List<ServidorPorOrgBean> lstServidorPorOrg(boolean somenteOrgaoServidorAtivo, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<ServidorPorTipoBean> lstServidorPorTipo(int maxResultados, AcessoSistema responsavel) throws RelatorioControllerException;

    public BigDecimal getTotalPrestacaoEmprestimo(AcessoSistema responsavel) throws RelatorioControllerException;

    public BigDecimal getSaldoDevedorEmprestimo(AcessoSistema responsavel) throws RelatorioControllerException;

    public List<TransferObject> lstPercentualCarteira(String dataIni, String dataFim, List<String> svcCodigos, List<String> orgCodigo, List<String> origensAdes, String campo, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<TransferObject> lstPercentualRejeitoTotal(String periodo, List<String> orgCodigos, List<String> estCodigos, boolean integrada, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<TransferObject> lstPercentualRejeitoPeriodo(String periodo, List<String> orgCodigos, List<String> estCodigos, boolean integrada, AcessoSistema responsavel) throws RelatorioControllerException;

    public String insereRelEditavel(String relCodigo, String relTitulo, String funDescricao, String itmDescricao, List<String> papCodigos, Map<String, Map<String, String>> filtros, Map<String, Integer> ordenacaoFiltros, String relTemplateSql, String relAgrupamento, String relTemplateJasper, String relAgendado, AcessoSistema responsavel) throws RelatorioControllerException;

    public void alterarStatusRelatorio(String relCodigo, Short relAtivo, AcessoSistema responsavel) throws RelatorioControllerException;

    public void edtRelEditavel(TransferObject to, List<String> papCodigos, Map<String, Map<String, String>> filtros, Map<String, Integer> ordenacaoFiltros, AcessoSistema responsavel) throws RelatorioControllerException;

    public void removeRelEditavel(String relCodigo, AcessoSistema responsavel) throws RelatorioControllerException;

    public TransferObject findRelEditavel(String relCodigo, AcessoSistema responsavel) throws RelatorioControllerException;

    public Map<String, TransferObject> findRelatorioFiltro(String relCodigo, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<GerencialTaxasBean> lstRakingTaxas(String svcCodigo, TransferObject criterio,AcessoSistema responsavel) throws RelatorioControllerException;

    public List<GerencialGeralTaxasEfetivasBean> lstTaxasEfetivas(String svcCodigo, Date periodo, List<Integer> prazos, AcessoSistema responsavel) throws RelatorioControllerException;

    public TransferObject buscaSvcTaxasQuery(boolean internacional, AcessoSistema responsavel) throws RelatorioControllerException;

    public InadimplenciaBean buscaQuantidadeCarteiraInadimplencia(String prdDtDesconto, String csaCodigo, List<String> spdCodigo, String naturezaServico, List<String> sadCodigos, AcessoSistema responsavel) throws RelatorioControllerException;

    public InadimplenciaBean buscaValorCarteiraInadimplencia(String prdDtDesconto, String csaCodigo, List<String> spdCodigo, String naturezaServico, List<String> sadCodigos, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<String> buscaTopOrgaosInadimplencia(String prdDtDesconto, String csaCodigo, List<String> spdCodigos, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<String> buscaTopValorOrgaosInadimplencia(String prdDtDesconto, String csaCodigo, List<String> spdCodigos, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException;

    public InadimplenciaBean buscaQuantidadeOrgaoInadimplencia(String prdDtDesconto, String csaCodigo, String orgCodigo, List<String> notOrgCodigo, List<String> spdCodigos, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException;

    public InadimplenciaBean buscaValorOrgaoInadimplencia(String prdDtDesconto, String csaCodigo, String orgCodigo, List<String> notOrgCodigo, List<String> spdCodigos, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<InadimplenciaBean> buscaQuantidadeSituacaoServidorInadimplencia(String prdDtDesconto, String csaCodigo, List<String> spdCodigos, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<InadimplenciaBean> buscaValorSituacaoServidorInadimplencia(String prdDtDesconto, String csaCodigo, List<String> spdCodigos, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException;

    public InadimplenciaBean buscaTransferidosInadimplencia(String prdDtDesconto, String csaCodigo, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException;

    public InadimplenciaBean buscaAlongadosInadimplencia(String prdDtDesconto, String csaCodigo, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException;

    public InadimplenciaBean buscaValorTransferidosInadimplencia(String prdDtDesconto, String csaCodigo, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException;

    public InadimplenciaBean buscaValorAlongadosInadimplencia(String prdDtDesconto, String csaCodigo, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException;

    public InadimplenciaBean buscaFalecidosInadimplencia(String prdDtDesconto, String csaCodigo, String naturezaServico, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<ServicoOperacaoMesBean> lstOperacaoMes(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<RelatorioConfCadMargemBean> lstRelConfCadMargem(TransferObject criterio, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<Object> geraRelatorioSaldoDevedorPorCsaPeriodo(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<Object> geraRelatorioPrdPagasCsaPeriodo(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException ;

    public List<Object> geraRelatorioInclusoesPorCsa(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException;

    public Pair<String[], List<TransferObject>> geraRelatorioSinteticoDecisaoJudicial(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<RegrasConvenioServicosBean> listaServicosRegrasConvenio(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<AvaliacaoFaqBean> listaAvaliacaoFaqAnalitico(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<AvaliacaoFaqBean> listaAvaliacaoFaqSintetico(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<TransferObject> listaEstatisticoProcessamentoTipoArquivos(List<String> tarCodigos, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<TransferObject> listaEstatisticoProcessamentoPeriodos(List<String> funCodigos, List<String> tarCodigos, AcessoSistema responsavel) throws RelatorioControllerException;

    public List<EstatisticoProcessamentoBean> listaEstatisticoProcessamento(List<java.sql.Date> harPeriodos, List<String> harPeriodosFormatados, List<TransferObject> estatisticoProcessamentoTipoArquivos, AcessoSistema responsavel) throws RelatorioControllerException;

    public int qtdeContratosPorCrs(String periodo, AcessoSistema responsavel) throws RelatorioControllerException;

    public TransferObject buscaMaxPrazo(AcessoSistema responsavel) throws RelatorioControllerException;

    public HashMap<String, Object> geraInformacoesSinteticoGerencialGeralCsa(HashMap<String, Object> parameters, String ultimoPeriodoProcessado, String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException;

    public void criarPivotAux() throws RelatorioControllerException;

    public List<ContratosPorCsaBean> lstConsignatariaSituacao(AcessoSistema responsavel) throws RelatorioControllerException;
    
    public List<TermoUsoPrivacidadeAdesaoBean> lstTermoUsoPrivacidadeAdesaoAutorizado(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException;
    
    public List<TermoUsoPrivacidadeAdesaoBean> lstTermoAdesaoNaoAutorizado(CustomTransferObject criterios, AcessoSistema responsavel) throws RelatorioControllerException;

	public List<RegrasConvenioOrgaoBean> listaOrgaosSerRegrasConvenio(AcessoSistema responsavel) throws RelatorioControllerException;

	public List<RegrasConvenioMargensBean> lstMargensRegrasConvenio(AcessoSistema responsavel) throws RelatorioControllerException;

	public List<RegrasConvenioParametrosBean> listaParamServicosRegrasConvenio(String csaCodigo, AcessoSistema responsavel) throws RelatorioControllerException;	
    
	public void enviarNotificacaoCsaAlteracaoRegrasConvenio(AcessoSistema responsavel) throws RelatorioControllerException;

	public List<RegrasConvenioParametrosBean> listaParamOrgaosSerRegrasConvenio(AcessoSistema responsavel) throws RelatorioControllerException;

	public List<RegrasConvenioParametrosBean> listaParamMargensRegrasConvenio(AcessoSistema responsavel) throws RelatorioControllerException;	
}