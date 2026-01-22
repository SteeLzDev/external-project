package com.zetra.econsig.persistence.dao;

import java.util.List;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: PontuacaoServidorDAO</p>
 * <p>Description: Interface do DAO de Leilão</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface PontuacaoServidorDAO {

    public void calcularPontuacaoLeilao(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // PontuacaoRseCsa

    // TipoPontuacaoEnum.PONTUACAO_INICIAL
    public void zerarPontuacaoRseCsa(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.QTDE_CONTRATOS_SUSPENSOS
    public void calcularPontuacaoRseCsaQtdeContratosSuspensos(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.QTDE_CONTRATOS_CONCLUIDOS
    public void calcularPontuacaoRseCsaQtdeContratosConcluidos(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.PORCENTAGEM_MARGEM_UTILIZADA
    public void calcularPontuacaoRseCsaPercentualMargemUtilizada(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.PORCENTAGEM_INADIMPLENCIA
    public void calcularPontuacaoRseCsaPercentualInadimplencia(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.PONTOS_PERDIDOS_POR_LEILAO_NAO_CONCRETIZADO
    public void calcularPontuacaoRseCsaQtdeLeilaoNaoConcretizado(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.FAIXA_ETARIA_EM_ANOS
    public void calcularPontuacaoRseCsaFaixaEtariaEmAnos(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.FAIXA_TEMPO_SERVICO_EM_MESES
    public void calcularPontuacaoRseCsaTempoServicoEmMeses(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.FAIXA_SALARIAL
    public void calcularPontuacaoRseCsaFaixaSalarial(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.FAIXA_VALOR_MARGEM
    public void calcularPontuacaoRseCsaFaixaValorMargem(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.QTDE_CONTRATOS_LIQUIDADOS
    public void calcularPontuacaoRseCsaQtdeContratosLiquidados(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.QTDE_CONTRATOS_LIQUIDADOS_GERAL
    public void calcularPontuacaoRseCsaQtdeContratosLiquidadosGeral(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.QTDE_CONTRATOS_SUSPENSOS_GERAL
    public void calcularPontuacaoRseCsaQtdeContratosSuspensosGeral(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.QTDE_CONTRATOS_CONCLUIDOS_GERAL
    public void calcularPontuacaoRseCsaQtdeContratosConcluidosGeral(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.PORCENTAGEM_INADIMPLENCIA_GERAL
    public void calcularPontuacaoRseCsaPercentualInadimplenciaGeral(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.PORCENTAGEM_TURNOVER_CSE
    public void calcularPontuacaoRseCsaPercentualTurnoverCse(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.QTDE_CONTRATOS_AFETADOS_DECISAO_JUDICIAL
    public void calcularPontuacaoRseCsaQtdeContratosAfetadosJudicial(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.QTDE_CONTRATOS_AFETADOS_DECISAO_JUDICIAL_GERAL
    public void calcularPontuacaoRseCsaQtdeContratosAfetadosJudicialGeral(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.QTDE_COLABORADORES_CSE
    public void calcularPontuacaoRseCsaQtdeColaboradoresCse(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.MEDIA_SALARIAL_CSE
    public void calcularPontuacaoRseCsaMediaSalarialCse(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.MEDIA_MARGEM_LIVRE_CSE
    public void calcularPontuacaoRseCsaMediaMargemLivreCse(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

    // TipoPontuacaoEnum.PORCENTAGEM_DESCONTOS_CSA
    public void calcularPontuacaoRseCsaPercentualDescontos(String csaCodigo, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws DAOException;

}
