package com.zetra.econsig.persistence.dao;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ParcelaDescontoDAO</p>
 * <p>Description: Interface do DAO de Parcela Desconto</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface ParcelaDescontoDAO {
    // Insere parcelas
    public void insereParcelasFaltantes(List<String> orgCodigos, List<String> estCodigos, List<String> rseCodigos, String operacaoIntegracao, AcessoSistema responsavel) throws DAOException;

    // Exportação de Movimento Financeiro
    public void processaParcelas(List<String> orgCodigos, List<String> estCodigos, List<String> verbas) throws DAOException;

    public void reduzirValorParcelaReimplante(List<String> orgCodigos, List<String> estCodigos, List<String> verbas) throws DAOException;

    // Importação do Retorno da Integração
    public List<TransferObject> getPrdEmProcessamento(List<String> camposChave, TransferObject criterio, boolean atrasado) throws DAOException;

    public void liquidaParcelas(String prdData, String spdCodigo, String tipoEntidade, String codigoEntidade) throws DAOException;

    public void liquidaParcelas(Integer prdCodigo, String prdData, String prdVlr, String spdCodigo, String tdeCodigo, boolean atrasado) throws DAOException;

    public void parcelasSemRetorno(String orgCodigo, String estCodigo, String usuCodigo) throws DAOException;

    public void moverParcelasIntegradas(List<String> orgCodigos, List<String> estCodigos, List<String> rseCodigos, List<String> periodo, boolean ignorarAusenciaParcelas) throws DAOException;

    public void moverParcelasIntegradasPorRse(String rseCodigo, String periodo) throws DAOException;

    public List<TransferObject> getPrdProcessamentoFerias(List<String> camposChave, TransferObject criterio) throws DAOException;

    // Antecipação de período com dois abertos simultâneamente
    public void alterarStatusParcelasPosPeriodo(List<String> orgCodigos, List<String> estCodigos, String spdOrigem, String spdDestino) throws DAOException;

    public void removerParcelasPosPeriodoPagaEmFerias(List<String> orgCodigos, List<String> estCodigos) throws DAOException;

    public void ajustarPrdPagasExpPeriodoSimultaneo(List<String> orgCodigos, List<String> estCodigos, boolean incrementar) throws DAOException;

    // Cadastro de Retorno da Integração
    public void criaOcorrenciaRetorno(String tocCodigo, String ocpObs, String tipoEntidade, String codigoEntidade, String usuCodigo) throws DAOException;

    // Conclusão de Retorno da Integração
    public void criaOcorrenciaSemRetorno(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws DAOException;

    public void retornoAtrasadoSomandoAParcela(boolean somaParcela) throws DAOException;

    public void insereParcelasEdicaoDeFluxo(List<String> orgCodigos, List<String> estCodigos, List<String> rseCodigos, AcessoSistema responsavel) throws DAOException;

    public void liquidaParcelasPagamentoBoleto(List<String> orgCodigos, List<String> estCodigos, List<String> periodos, AcessoSistema responsavel) throws DAOException;
}