package com.zetra.econsig.persistence.dao;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.folha.exportacao.ExportaMovimento;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: HistoricoIntegracaoDAO</p>
 * <p>Description: Interface do DAO de Histórico Integração</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface HistoricoIntegracaoDAO {

    public void criarTabelasExportacaoMovFin(List<TransferObject> tdaList) throws DAOException;

    public void setAdeExportacao(List<String> orgCodigos, List<String> estCodigos, List<String> sadCodigos, List<String> tocCodigos) throws DAOException;

    public void selectExportacao(List<String> orgCodigos, List<String> estCodigos, List<String> verbas, List<String> sadCodigos, List<String> spdCodigos, boolean exportaMensal) throws DAOException;

    public void selectExportacaoFutura(List<String> orgCodigos, List<String> estCodigos, List<String> verbas, List<String> sadCodigos) throws DAOException;

    public void corrigeContratosExportacaoEmCarencia(List<String> tocCodigos) throws DAOException;

    public void selectExportacaoFeriasMensal(List<String> orgCodigos, List<String> estCodigos, List<String> verbas) throws DAOException;

    public void selectLiquidacaoExportacao(List<String> orgCodigos, List<String> estCodigos, List<String> verbas, List<String> sadCodigos) throws DAOException;

    public List<String> geraArqExportacao(String opcaoExportacao, String orgCodigo, AcessoSistema responsavel, boolean exportaMensal, boolean exportaPorOrgao,
            String pathLote, String pathConf, String nomeArqConfEntrada, String nomeArqConfTradutor, String nomeArqConfSaida,
            String nomeArqConfEntradaDefault, String nomeArqConfTradutorDefault, String nomeArqConfSaidaDefault, List<TransferObject> tdaList, List<String> adeNumeros, ExportaMovimento exportador,
            ParametrosExportacao parametrosExportacao) throws DAOException;

    public void removeADEValorAbaixoMinimoSvc() throws DAOException;

    public void removeInclusaoAlteracaoSemAnexo() throws DAOException;

    public void alteraInclusaoAlteracaoSemAnexoSituacaoOrigem() throws DAOException;

    public void consolidaExclusaoInclusaoComoAlteracao() throws DAOException;

    public void atualizaAdeVlrServicoPercentual() throws DAOException;

    public void atualizaVlrCapitalPago() throws DAOException;

    public void gravarTabelaExportacao() throws DAOException;

    public void limparTabelaExportacao(List<String> orgCodigos, List<String> estCodigos, List<String> verbas, AcessoSistema responsavel) throws DAOException;

    public void atualizaAdeVlrServicoLimiteMaxDescontoFolha() throws DAOException;

    public void salvarAdePaga(List<String> orgCodigos, List<String> estCodigos) throws DAOException;

    public void recuperarAdePaga() throws DAOException;

    public void moveComandosForaPeriodoBase(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws DAOException;

    public void removeAdeExportacaoForaPeriodoBase() throws DAOException;

    public void atualizaAutorizaPgtParcial() throws DAOException;

    public void excluiContratosRseExcluidosExportacao() throws DAOException;

    public void removeContratosSemPermissaoCse() throws DAOException;
}
