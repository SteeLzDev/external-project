package com.zetra.econsig.persistence.dao;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: AutorizacaoDAO</p>
 * <p>Description: Interface do DAO de Autorizacao</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface AutorizacaoDAO {

    public void atualizaAdeExportadas(List<String> orgCodigos, List<String> estCodigos, List<String> adeCodigos, boolean atrasado, AcessoSistema responsavel) throws DAOException;

    public void recalcularParcelasPagas(String complementoAde, AcessoSistema responsavel) throws DAOException;

    public void insereOcorrenciaRelancamento(HashMap<String, String> adeTipoEnvio, String responsavel) throws DAOException;

    public void atualizaValorFolha(String adeCodigo, BigDecimal adeVlrFolha, String adePrazoFolha, String adeAnoMesIniFolha, String adeAnoMesFimFolha) throws DAOException;

    public void retiraDoEstoque(List<String> adeCodigos, String tipoEntidade, String codigoEntidade, String responsavel) throws DAOException;

    public void colocaEmEstoque(List<String> adeCodigos, String dataLimite, String tipoEntidade, String codigoEntidade, String responsavel) throws DAOException;

    public void concluiAdesNaoPagas(AcessoSistema responsavel) throws DAOException;

    public void concluiAdesAguardLiquid(AcessoSistema responsavel) throws DAOException;

    public void concluiAdesNaoIntegramFolha(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws DAOException;

    public void concluiAdesServidorExcluido(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws DAOException;

    public void concluiAdesLancamentoNaoPagos(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws DAOException;

    public void limpaMotivoNaoExportacao(AcessoSistema responsavel) throws DAOException;

    public void setRetAtrasadoSomaAparcela(boolean somaParcela) throws DAOException;

    public List<String> atualizarAdeValorAlteracaoMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    public void atualizaAdeUltPeriodoExportado() throws DAOException;

    public void concluiAdesSuspensasPorDataFim(String responsavelContratoSuspenso, AcessoSistema responsavel) throws DAOException;
}
