package com.zetra.econsig.persistence.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ImpRetornoDAO</p>
 * <p>Description: Interface do DAO de Importação de Retorno</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Date$
 */
public interface ImpRetornoDAO {
    public void setNomeArqRetorno(String nomeArqRetorno);
    public void criarTabelasImportacaoRetorno() throws DAOException;

    public void setRetAtrasadoSomaAparcela(boolean somaParcela) throws DAOException;

    public void zeraCamposFolha(List<String> orgCodigos, List<String> estCodigos, List<String> rseCodigos, AcessoSistema responsavel) throws DAOException;
    public void zeraInformacaoRetornoParcelas(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws DAOException;

    public void iniciaCargaArquivoRetorno(String nomeArqRetorno, boolean mantemArqRetorno, List<String> orgIdentRemocao) throws DAOException;
    public boolean insereLinhaTabelaRetorno(Map<String, Object> entrada, String linha, int numLinha, HashMap<String, List<TransferObject>> conveniosMap, Map<String, String> mapVerbaRef, Map<String, String> mapVerbaFerias, AcessoSistema responsavel) throws DAOException;
    public void encerraCargaArquivoRetorno(boolean mantemArqRetorno) throws DAOException;
    public int countArquivoTabelaRetorno(String nomeArqRetorno) throws DAOException;

    public void criaTabelaParcelasRetorno(List<String> camposChave, CustomTransferObject criterio, boolean atrasado) throws DAOException;
    public void selecionaParcelasPagamentoExato(boolean valorExato) throws DAOException;
    public void pagaParcelasSelecionadasDescontoTotal(boolean atrasado, AcessoSistema responsavel) throws DAOException;
    public void getAdeCodigosAlteracao(Map<String, Map<String, Object>> linhasSemProcessamento, List<String> adeCodigosAlteracao, HashMap<String, String> adeTipoEnvio, boolean exportaMensal, boolean atrasado, boolean critica, boolean ferias) throws DAOException;

    public void getAdeCodigosLiquidacao(List<String> adeCodigosLiquidacao, boolean ferias) throws DAOException;
    public List<String> getAdeCodigosPermiteLiquidacao(List<String> adeCodigosLiquidacao) throws DAOException;

    public void criaTabelaConsolidacaoExata(boolean agrupaPorPeriodo, boolean agrupaPorAdeCodigo) throws DAOException;
    public List<TransferObject> buscaLinhasConsolidacaoExata(boolean ferias, boolean agrupaPorAdeCodigo) throws DAOException;
    public void marcaLinhaConsolidadaComoProcessada(String idLinha) throws DAOException;
    public List<TransferObject> getLinhasSemProcessamento() throws DAOException;
    public void geraArqLinhasSemMapeamento(String nomeArquivo) throws DAOException;

    public void desfazerUltimoRetorno(String orgCodigo, String estCodigo, String periodo, String[] parcelas, String rseCodigo) throws DAOException;
    public void desfazerUltimoMovimento(String orgCodigo, String estCodigo, String periodo) throws DAOException;

    public void atualizarAdeVlrRetorno(String proximoPeriodo, String rseCodigo) throws DAOException;

    public void criaTabelaParcelasRetornoFerias(List<String> camposChave, CustomTransferObject criterio, boolean atrasado) throws DAOException;
    public void selecionaParcelasPagamentoExatoFerias() throws DAOException;
    public void pagaParcelasSelecionadasDescontoTotalFerias(AcessoSistema responsavel) throws DAOException;

    public void criaTabelaConsolidacaoExataFerias() throws DAOException;

    public void associarLinhaRetornoParcelaExata(boolean ferias) throws DAOException;
    public void associarLinhaRetornoParcela(String adeCodigo, Short prdNumero, Date prdDataDesconto, int numLinha) throws DAOException;

    public List<Integer> getLinhasProcessamentoFerias() throws DAOException;

    public void criaTabelaConsolidacaoInversaExata() throws DAOException;
    public List<TransferObject> buscaLinhasConsolidacaoInversaExata() throws DAOException;

    public void atualizarCsaCodigoTbArqRetorno() throws DAOException;
}