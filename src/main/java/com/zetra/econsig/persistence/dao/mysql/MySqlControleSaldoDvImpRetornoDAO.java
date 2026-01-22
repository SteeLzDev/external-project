package com.zetra.econsig.persistence.dao.mysql;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.generic.GenericControleSaldoDvImpRetornoDAO;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ControleSaldoDvImpRetornoDAO</p>
 * <p>Description: DAO de MySql para rotinas de controle de saldo devedor
 * efetuadas na importação do retorno da folha.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor Lucas
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlControleSaldoDvImpRetornoDAO extends GenericControleSaldoDvImpRetornoDAO {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlControleSaldoDvImpRetornoDAO.class);

    private static final String TMP_TABLE_AJUSTE_SALDO_DEVEDOR = "tmp_ajuste_saldo_devedor";
    private static final String TMP_TABLE_CONCLUSAO_CONTRATO = "tmp_conclusao_saldo_devedor_zerado";
    private static final String TMP_TABLE_INCLUSAO_CONTRATO = "tmp_inclusao_contrato_correcao";
    private static final String TMP_TABLE_CONCLUSAO_CONTRATO_NAO_PAGO = "tmp_conclusao_saldo_devedor_nao_pago";

    /***************************************************************************/
    /**
     * Subtrai do saldo devedor o valor descontado pela folha no mês atual.
     * Utiliza a tabela de período de exportação para definir qual a parcela
     * a ser utilizada. Atualiza também os contratos que não são enviados
     * para a folha, mas que tem controle de saldo.
     * @throws DAOException
     */
    @Override
    public void abaterSaldoDevedor(List<String> orgCodigos, List<String> estCodigos) throws DAOException {
        try {
            List<String> servicosControleSaldo = pesquisarServicosControleSaldoDevedor();
            if (servicosControleSaldo != null && servicosControleSaldo.size() > 0) {
                final MapSqlParameterSource queryParams = new MapSqlParameterSource();
                queryParams.addValue("servicosControleSaldo", servicosControleSaldo);

                // Remove a tabela temporária
                final StringBuilder query = new StringBuilder();
                query.append("DROP TEMPORARY TABLE IF EXISTS ").append(TMP_TABLE_AJUSTE_SALDO_DEVEDOR);
                executeUpdate(query.toString(), queryParams);

                // Cria a tabela temporária
                query.setLength(0);
                query.append("CREATE TEMPORARY TABLE ").append(TMP_TABLE_AJUSTE_SALDO_DEVEDOR);
                query.append(" (ORG_CODIGO CHAR(32) NOT NULL, ");
                query.append("  PERIODO DATE NOT NULL, ");
                query.append("  KEY `tmpidx_org` (`ORG_CODIGO`), ");
                query.append("  KEY `tmpidx_periodo` (`PERIODO`))");
                executeUpdate(query.toString(), queryParams);

                // Insere os dados da tabela temporária
                query.setLength(0);
                query.append("INSERT INTO ").append(TMP_TABLE_AJUSTE_SALDO_DEVEDOR);
                query.append(" (ORG_CODIGO, PERIODO) ");
                query.append(" SELECT ").append(Columns.HIE_ORG_CODIGO).append(", ");
                query.append(" MAX(").append(Columns.HIE_PERIODO).append(") ");
                query.append(" FROM ").append(Columns.TB_HISTORICO_EXPORTACAO);
                query.append(" GROUP BY ").append(Columns.HIE_ORG_CODIGO);
                executeUpdate(query.toString(), queryParams);

                // Atualiza os contratos que possuem controle de saldo devedor
                query.setLength(0);
                query.append("UPDATE ").append(Columns.TB_PARCELA_DESCONTO_PERIODO).append(", ");
                query.append(Columns.TB_AUTORIZACAO_DESCONTO).append(", ");
                query.append(Columns.TB_VERBA_CONVENIO).append(", ");
                query.append(Columns.TB_CONVENIO).append(", ");
                query.append(TMP_TABLE_AJUSTE_SALDO_DEVEDOR);
                query.append(" SET ").append(Columns.ADE_VLR_SDO_RET).append(" = ");
                query.append(Columns.ADE_VLR_SDO_MOV).append(" - ");
                query.append("IFNULL(");
                query.append("(CASE ").append(Columns.PDP_SPD_CODIGO);
                query.append(" WHEN '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' THEN ").append(Columns.PDP_VLR_REALIZADO);
                query.append(" WHEN '").append(CodedValues.SPD_LIQUIDADAMANUAL).append("' THEN ").append(Columns.PDP_VLR_REALIZADO);
                query.append(" ELSE 0 END)");
                query.append(", 0)");
                query.append(" WHERE ").append(Columns.PDP_ADE_CODIGO);
                query.append(" = ").append(Columns.ADE_CODIGO);
                query.append(" AND ").append(Columns.ADE_VCO_CODIGO);
                query.append(" = ").append(Columns.VCO_CODIGO);
                query.append(" AND ").append(Columns.VCO_CNV_CODIGO);
                query.append(" = ").append(Columns.CNV_CODIGO);
                query.append(" AND ").append(Columns.CNV_ORG_CODIGO);
                query.append(" = ").append(TMP_TABLE_AJUSTE_SALDO_DEVEDOR).append(".ORG_CODIGO");
                // Serviços que possuem correção de saldo devedor
                query.append(" AND ").append(Columns.CNV_SVC_CODIGO);
                query.append(" IN (:servicosControleSaldo)");
                // Contratos que não foram concluidos
                query.append(" AND ").append(Columns.ADE_SAD_CODIGO);
                query.append(" IN ('").append(TextHelper.join(SAD_CODIGOS, "','")).append("')");
                // Contratos que vão para a folha
                query.append(" AND ").append(Columns.ADE_INT_FOLHA);
                query.append(" = ").append(CodedValues.INTEGRA_FOLHA_SIM);
                // Parcelas do mês corrente (Tiveram retorno após o periodo de exportação)
                query.append(" AND ").append(Columns.PDP_SPD_CODIGO);
                query.append(" IN ('").append(TextHelper.join(SPD_CODIGOS, "','")).append("')");
                query.append(" AND ").append(Columns.PDP_DATA_DESCONTO);
                query.append(" = ").append(TMP_TABLE_AJUSTE_SALDO_DEVEDOR).append(".PERIODO");
                query.append(getSufixo(orgCodigos, estCodigos, queryParams));
                executeUpdate(query.toString(), queryParams);

                // Atualiza os contratos que tem controle de saldo devedor, mas
                // não são enviados para a folha. Por exemplo, contratos de correção
                // que são descontados apenas no final do montante principal
                query.setLength(0);
                query.append("UPDATE ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(", ");
                query.append(Columns.TB_VERBA_CONVENIO).append(", ");
                query.append(Columns.TB_CONVENIO);
                query.append(" SET ").append(Columns.ADE_VLR_SDO_RET).append(" = ");
                query.append(Columns.ADE_VLR_SDO_MOV);
                query.append(" WHERE ").append(Columns.ADE_VCO_CODIGO);
                query.append(" = ").append(Columns.VCO_CODIGO);
                query.append(" AND ").append(Columns.VCO_CNV_CODIGO);
                query.append(" = ").append(Columns.CNV_CODIGO);
                // Serviços que possuem correção de saldo devedor
                query.append(" AND ").append(Columns.CNV_SVC_CODIGO);
                query.append(" IN (:servicosControleSaldo)");
                // Contratos que não foram concluidos
                query.append(" AND ").append(Columns.ADE_SAD_CODIGO);
                query.append(" IN ('").append(TextHelper.join(SAD_CODIGOS, "','")).append("')");
                // Contratos que não vão para a folha
                query.append(" AND ").append(Columns.ADE_INT_FOLHA);
                query.append(" = ").append(CodedValues.INTEGRA_FOLHA_NAO);
                query.append(getSufixo(orgCodigos, estCodigos, queryParams));
                executeUpdate(query.toString(), queryParams);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /***************************************************************************/
    /**
     * Conclui os contratos que tiverem o saldo devedor zerado após
     * o abatimento do valor descontado pela folha.
     * @throws DAOException
     */
    @Override
    public void concluirContratosSaldoDevedorZerado(List<String> orgCodigos, List<String> estCodigos) throws DAOException {
        try {
            final List<String> servicosControleSaldo = pesquisarServicosControleSaldoDevedor();
            if (servicosControleSaldo != null && servicosControleSaldo.size() > 0) {
                // Insere na tabela temporária contratos com saldo devedor zerado
                listarCanditadosConclusao(orgCodigos, estCodigos, servicosControleSaldo);

                // Elimina da lista de candidatos contratos que nao devem ser concluidos
                eliminarContratosCorrecaoAbertos();

                // Insere ocorrencia de conclusão de contrato
                criarOcorrenciasConclusao();

                // Conclui os contratos que restaram na tabela temporaria
                concluirContratosSemSaldoDevedor();

                // Manda para a folha os contratos de correção vinculados
                lancarContratosCorrecaoVinculados();
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Reimplanta contratos que não tiveram parcela paga referente ao último período,
     * onde o valor do saldo devedor é maior que zero
     * e apenas para os serviços que possuem controle de saldo devedor.
     * Esse método só será executado caso a exportação do movimento financeiro seja inicial.
     * ATENÇÃO: Não estão sendo validados os parâmetros de reimplante de contratos neste método.
     *
     * @param orgCodigos Códigos dos órgãos que serão reimplantados
     * @param estCodigos Códigos dos estabelecimentos que serão reimplantados
     * @throws DAOException
     */
    @Override
    public void reimplantarContratosNaoPagos(List<String> orgCodigos, List<String> estCodigos) throws DAOException {
        boolean exportacaoInicial = ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        if (exportacaoInicial) {
            try {
                List<String> servicosControleSaldo = pesquisarServicosControleSaldoDevedor();
                if (servicosControleSaldo != null && servicosControleSaldo.size() > 0) {
                    final MapSqlParameterSource queryParams = new MapSqlParameterSource();
                    queryParams.addValue("servicosControleSaldo", servicosControleSaldo);

                    // Cria as cláusulas para listagem dos contratos que devem ser reimplatados

                    // Cláusula FROM
                    StringBuilder fromClause = new StringBuilder();
                    fromClause.append(Columns.TB_AUTORIZACAO_DESCONTO).append(" ");
                    fromClause.append("INNER JOIN ").append(Columns.TB_VERBA_CONVENIO).append(" ON (");
                    fromClause.append(Columns.ADE_VCO_CODIGO).append(" = ").append(Columns.VCO_CODIGO).append(") ");
                    fromClause.append("INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (");
                    fromClause.append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(") ");
                    fromClause.append("INNER JOIN ").append(Columns.TB_PERIODO_EXPORTACAO).append(" ON (");
                    fromClause.append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.PEX_ORG_CODIGO).append(") ");
                    fromClause.append("INNER JOIN ").append(TMP_TABLE_AJUSTE_SALDO_DEVEDOR).append(" ON (");
                    fromClause.append(Columns.CNV_ORG_CODIGO).append(" = ");
                    fromClause.append(TMP_TABLE_AJUSTE_SALDO_DEVEDOR).append(".ORG_CODIGO").append(") ");
                    fromClause.append("LEFT OUTER JOIN ").append(Columns.TB_PARCELA_DESCONTO_PERIODO).append(" ON (");
                    fromClause.append(Columns.PDP_ADE_CODIGO).append(" = ").append(Columns.ADE_CODIGO);
                    fromClause.append(" AND ").append(Columns.PDP_DATA_DESCONTO).append(" = ");
                    fromClause.append(TMP_TABLE_AJUSTE_SALDO_DEVEDOR).append(".PERIODO").append(") ");

                    // Cláusula WHERE
                    StringBuilder whereClause = new StringBuilder();
                    whereClause.append(Columns.ADE_ANO_MES_INI);
                    whereClause.append(" <= ").append(TMP_TABLE_AJUSTE_SALDO_DEVEDOR).append(".PERIODO");
                    // Contratos que não possuem parcela paga para o periodo de desconto
                    whereClause.append(" AND (").append(Columns.PDP_ADE_CODIGO).append(" IS NULL");
                    whereClause.append(" OR ").append(Columns.PDP_SPD_CODIGO);
                    whereClause.append(" IN ('").append(TextHelper.join(SPD_CODIGOS_NAO_DESCONTADOS, "','")).append("'))");
                    // Serviços que possuem controle de saldo devedor
                    whereClause.append(" AND ").append(Columns.CNV_SVC_CODIGO);
                    whereClause.append(" IN (:servicosControleSaldo)");
                    // Contratos que estão em aberto
                    whereClause.append(" AND ").append(Columns.ADE_SAD_CODIGO);
                    whereClause.append(" IN ('").append(TextHelper.join(SAD_CODIGOS, "','")).append("')");
                    // Contratos com saldo devedor maior que zero
                    whereClause.append(" AND ").append(Columns.ADE_VLR_SDO_RET).append(" > 0");
                    whereClause.append(getSufixo(orgCodigos, estCodigos, queryParams));

                    final StringBuilder query = new StringBuilder();
                    query.append("SET @rownum := 0;");
                    executeUpdate(query.toString(), queryParams);

                    // Inserir ocorrência de relançamento de contrato
                    query.setLength(0);
                    query.append("INSERT INTO tb_ocorrencia_autorizacao (ADE_CODIGO, OCA_CODIGO, TOC_CODIGO, USU_CODIGO, OCA_DATA, OCA_PERIODO, OCA_OBS) ");
                    query.append(" SELECT ").append(Columns.ADE_CODIGO).append(", ");
                    query.append("CONCAT('M', ");
                    query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
                    query.append("SUBSTRING(LPAD(").append(Columns.ADE_NUMERO).append(", 12, '0'), 1, 12), ");
                    query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)) AS OCA_CODIGO, '");
                    query.append(CodedValues.TOC_RELANCAMENTO).append("' AS TOC_CODIGO, '");
                    query.append(CodedValues.USU_CODIGO_SISTEMA).append("' AS USU_CODIGO, NOW() AS OCA_DATA, PEX_PERIODO_POS AS OCA_PERIODO, ");
                    query.append("'").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.reimplante.contrato.saldo.dev", (AcessoSistema) null)).append("' AS OCA_OBS");
                    query.append(" FROM ").append(fromClause);
                    query.append(" WHERE ").append(whereClause);
                    executeUpdate(query.toString(), queryParams);

                    // Alterar a data de início e fim dos contratos para o próximo período de desconto
                    query.setLength(0);
                    query.append("UPDATE ").append(fromClause);
                    query.append(" SET ");
                    query.append(Columns.ADE_SAD_CODIGO).append(" = '").append(CodedValues.SAD_DEFERIDA).append("', ");
                    query.append(Columns.ADE_PRAZO).append(" = ");
                    query.append("coalesce(").append(Columns.ADE_PRAZO).append(" - coalesce(").append(Columns.ADE_PRD_PAGAS).append(", 0), 1), ");
                    query.append(Columns.ADE_PRD_PAGAS).append(" = 0, ");
                    query.append(Columns.ADE_ANO_MES_INI).append(" = DATE_ADD(");
                    query.append(TMP_TABLE_AJUSTE_SALDO_DEVEDOR).append(".PERIODO").append(", INTERVAL ");
                    query.append("coalesce(").append(Columns.ADE_PRAZO).append(" - coalesce(").append(Columns.ADE_PRD_PAGAS).append(", 0), 1) MONTH), ");
                    query.append(Columns.ADE_ANO_MES_FIM).append(" = DATE_ADD(");
                    query.append(TMP_TABLE_AJUSTE_SALDO_DEVEDOR).append(".PERIODO").append(", INTERVAL ");
                    query.append("coalesce(").append(Columns.ADE_PRAZO).append(" - coalesce(").append(Columns.ADE_PRD_PAGAS).append(", 0), 1) MONTH)");
                    query.append(" WHERE ").append(whereClause);
                    executeUpdate(query.toString(), queryParams);

                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
            }
        }
    }

    /**
     * Insere na tabela temporaria contratos que possuem controle de saldo
     * devedor e que tem saldo devedor menor ou igual a zero. São
     * candidatos a conclusão
     * @param servicosControleSaldo List
     * @throws DataAccessException
     */
    private void listarCanditadosConclusao(List<String> orgCodigos, List<String> estCodigos, List<String> servicosControleSaldo) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("servicosControleSaldo", servicosControleSaldo);

        // Remove a tabela temporária
        final StringBuilder query = new StringBuilder();
        query.append("DROP TEMPORARY TABLE IF EXISTS ").append(TMP_TABLE_CONCLUSAO_CONTRATO);
        executeUpdate(query.toString(), queryParams);

        // Cria a tabela temporária
        query.setLength(0);
        query.append("CREATE TEMPORARY TABLE ").append(TMP_TABLE_CONCLUSAO_CONTRATO);
        query.append(" (ADE_CODIGO CHAR(32), ADE_NUMERO INT(10) UNSIGNED, PEX_PERIODO_POS DATE)");
        executeUpdate(query.toString(), queryParams);

        // Insere na tabela temporaria contratos que possuem controle de saldo
        // devedor e que tem saldo devedor menor ou igual a zero
        query.setLength(0);
        query.append("INSERT INTO ").append(TMP_TABLE_CONCLUSAO_CONTRATO);
        query.append(" (ADE_CODIGO, ADE_NUMERO, PEX_PERIODO_POS) ");
        query.append("SELECT ").append(Columns.ADE_CODIGO).append(",").append(Columns.ADE_NUMERO).append(",").append(Columns.PEX_PERIODO_POS);
        query.append(" FROM ").append(Columns.TB_AUTORIZACAO_DESCONTO);
        query.append(" INNER JOIN ").append(Columns.TB_VERBA_CONVENIO).append(" ON (");
        query.append(Columns.ADE_VCO_CODIGO).append(" = ").append(Columns.VCO_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (");
        query.append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_PERIODO_EXPORTACAO).append(" ON (");
        query.append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.PEX_ORG_CODIGO).append(")");
        // Serviços que possuem controle de saldo devedor
        query.append(" WHERE ").append(Columns.CNV_SVC_CODIGO);
        query.append(" IN (:servicosControleSaldo)");
        // Contratos que não foram concluidos
        query.append(" AND ").append(Columns.ADE_SAD_CODIGO);
        query.append(" IN ('").append(TextHelper.join(SAD_CODIGOS, "','")).append("')");
        // Contratos que são enviados para a folha
        query.append(" AND ").append(Columns.ADE_INT_FOLHA);
        query.append(" = ").append(CodedValues.INTEGRA_FOLHA_SIM);
        // Contratos com saldo devedor zerado
        query.append(" AND ").append(Columns.ADE_VLR_SDO_RET).append(" <= 0");
        query.append(getSufixo(orgCodigos, estCodigos, queryParams));
        executeUpdate(query.toString(), queryParams);
    }

    /**
     * Remove da tabela de contratos a serem concluidos, os contratos que são
     * correção de outros contratos, que ainda não foram concluidos. Portanto
     * estes contratos não devem ser concluidos, mesmo que o saldo devedor
     * seja igual a zero.
     * @throws DataAccessException
     */
    private void eliminarContratosCorrecaoAbertos() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("DELETE FROM ").append(TMP_TABLE_CONCLUSAO_CONTRATO);
        query.append(" USING ").append(TMP_TABLE_CONCLUSAO_CONTRATO).append(",");
        query.append(Columns.TB_RELACIONAMENTO_AUTORIZACAO).append(",");
        query.append(Columns.TB_AUTORIZACAO_DESCONTO);
        query.append(" WHERE ").append(TMP_TABLE_CONCLUSAO_CONTRATO).append(".ADE_CODIGO");
        query.append(" = ").append(Columns.RAD_ADE_CODIGO_DESTINO);
        query.append(" AND ").append(Columns.ADE_CODIGO);
        query.append(" = ").append(Columns.RAD_ADE_CODIGO_ORIGEM);
        query.append(" AND ").append(Columns.RAD_TNT_CODIGO);
        query.append(" = '").append(CodedValues.TNT_CORRECAO_SALDO).append("'");
        // Contratos que não foram concluidos
        query.append(" AND ").append(Columns.ADE_SAD_CODIGO);
        query.append(" IN ('").append(TextHelper.join(SAD_CODIGOS, "','")).append("')");
        // Contratos com saldo devedor maior que zero
        query.append(" AND ").append(Columns.ADE_VLR_SDO_RET).append(" > 0");
        executeUpdate(query.toString(), queryParams);
    }

    /**
     * Insere ocorrências de conclusão de contrato pelo saldo devedor zerado
     * @throws DataAccessException
     */
    private void criarOcorrenciasConclusao() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("SET @rownum := 0;");
        executeUpdate(query.toString(), queryParams);

        query.setLength(0);
        query.append("INSERT INTO tb_ocorrencia_autorizacao (ADE_CODIGO, OCA_CODIGO, TOC_CODIGO, USU_CODIGO, OCA_DATA, OCA_PERIODO, OCA_OBS) ");
        query.append(" SELECT ADE_CODIGO, ");
        query.append("CONCAT('P', ");
        query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
        query.append("SUBSTRING(LPAD(ade_numero, 12, '0'), 1, 12), ");
        query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)) AS OCA_CODIGO, '");
        query.append(CodedValues.TOC_CONCLUSAO_CONTRATO).append("' AS TOC_CODIGO, '");
        query.append(CodedValues.USU_CODIGO_SISTEMA).append("' AS USU_CODIGO, NOW() AS OCA_DATA, PEX_PERIODO_POS AS OCA_PERIODO, ");
        query.append("'").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido", (AcessoSistema) null)).append("' AS OCA_OBS");
        query.append(" FROM ").append(TMP_TABLE_CONCLUSAO_CONTRATO);
        executeUpdate(query.toString(), queryParams);
    }

    /**
     * Conclui os contratos com saldo devedor zerado. Muda o status,
     * e zera os campos de saldo devedor.
     * @throws DataAccessException
     */
    private void concluirContratosSemSaldoDevedor() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("UPDATE ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(", ");
        query.append(TMP_TABLE_CONCLUSAO_CONTRATO);
        query.append(" SET ");
        query.append(Columns.ADE_SAD_CODIGO).append(" = '");
        query.append(CodedValues.SAD_CONCLUIDO).append("', ");
        query.append(Columns.ADE_VLR_SDO_MOV).append(" = 0, ");
        query.append(Columns.ADE_VLR_SDO_RET).append(" = 0 ");
        query.append(" WHERE ");
        query.append(Columns.ADE_CODIGO).append(" = ");
        query.append(TMP_TABLE_CONCLUSAO_CONTRATO).append(".ADE_CODIGO");
        executeUpdate(query.toString(), queryParams);
    }

    /**
     * Para os contratos concluidos com saldo devedor zerado, verifica se existe
     * contrato de correção vinculado que deve ter o INT_FOLHA ajustado para
     * ser enviado para a folha
     * @throws DataAccessException
     */
    private void lancarContratosCorrecaoVinculados() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("UPDATE ").append(TMP_TABLE_CONCLUSAO_CONTRATO).append(",");
        query.append(Columns.TB_RELACIONAMENTO_AUTORIZACAO).append(",");
        query.append(Columns.TB_AUTORIZACAO_DESCONTO);
        query.append(" SET ").append(Columns.ADE_INT_FOLHA);
        query.append(" = ").append(CodedValues.INTEGRA_FOLHA_SIM);
        query.append(" WHERE ").append(TMP_TABLE_CONCLUSAO_CONTRATO).append(".ADE_CODIGO");
        query.append(" = ").append(Columns.RAD_ADE_CODIGO_ORIGEM);
        query.append(" AND ").append(Columns.ADE_CODIGO);
        query.append(" = ").append(Columns.RAD_ADE_CODIGO_DESTINO);
        query.append(" AND ").append(Columns.RAD_TNT_CODIGO);
        query.append(" = '").append(CodedValues.TNT_CORRECAO_SALDO).append("'");
        // Contratos de correção que não foram concluidos
        query.append(" AND ").append(Columns.ADE_SAD_CODIGO);
        query.append(" IN ('").append(TextHelper.join(SAD_CODIGOS, "','")).append("')");
        // Contratos de correção que vão para a folha apenas no final dos descontos
        query.append(" AND ").append(Columns.ADE_INT_FOLHA);
        query.append(" = ").append(CodedValues.INTEGRA_FOLHA_NAO);
        executeUpdate(query.toString(), queryParams);
    }

    /***************************************************************************/
    /**
     * Estende o prazo dos contratos que possuem controle de saldo devedor
     * e que o saldo devedor ainda não é zero. Isso é feito para evitar que o
     * contrato seja concluído no retorno, e para que uma nova parcela seja
     * criada na próxima exportação de movimento financeiro.
     * @throws DAOException
     */
    @Override
    public void estenderPrazo(List<String> orgCodigos, List<String> estCodigos) throws DAOException {
        try {
            List<String> servicosControleSaldo = pesquisarServicosControleSaldoDevedor();
            if (servicosControleSaldo != null && servicosControleSaldo.size() > 0) {
                final MapSqlParameterSource queryParams = new MapSqlParameterSource();
                queryParams.addValue("servicosControleSaldo", servicosControleSaldo);

                // Cria as cláusulas para listagem dos contratos que devem ter o prazo
                // estendido para a continuação do desconto do saldo devedor

                // Cláusula FROM
                StringBuilder fromClause = new StringBuilder();
                fromClause.append(Columns.TB_AUTORIZACAO_DESCONTO).append(", ");
                fromClause.append(Columns.TB_VERBA_CONVENIO).append(", ");
                fromClause.append(Columns.TB_CONVENIO).append(", ");
                fromClause.append(Columns.TB_PERIODO_EXPORTACAO);

                // Cláusula WHERE
                StringBuilder whereClause = new StringBuilder();
                whereClause.append(Columns.ADE_VCO_CODIGO);
                whereClause.append(" = ").append(Columns.VCO_CODIGO);
                whereClause.append(" AND ").append(Columns.VCO_CNV_CODIGO);
                whereClause.append(" = ").append(Columns.CNV_CODIGO);
                whereClause.append(" AND ").append(Columns.CNV_ORG_CODIGO);
                whereClause.append(" = ").append(Columns.PEX_ORG_CODIGO);
                // Serviços que possuem correção de saldo devedor
                whereClause.append(" AND ").append(Columns.CNV_SVC_CODIGO);
                whereClause.append(" IN (:servicosControleSaldo)");
                // Contratos que não foram concluidos
                whereClause.append(" AND ").append(Columns.ADE_SAD_CODIGO);
                whereClause.append(" IN ('").append(TextHelper.join(SAD_CODIGOS, "','")).append("')");
                // Contratos com saldo devedor maior que zero
                whereClause.append(" AND ").append(Columns.ADE_VLR_SDO_RET).append(" > 0");
                // Contratos com prazo determinado e que já tem parcelas pagas = prazo
                whereClause.append(" AND ").append(Columns.ADE_PRAZO).append(" IS NOT NULL");
                whereClause.append(" AND ").append(Columns.ADE_PRAZO);
                whereClause.append(" = ").append(Columns.ADE_PRD_PAGAS);
                whereClause.append(getSufixo(orgCodigos, estCodigos, queryParams));

                final StringBuilder query = new StringBuilder();
                query.append("SET @rownum := 0;");
                executeUpdate(query.toString(), queryParams);

                // Inserir ocorrência de alteração de prazo
                query.setLength(0);
                query.append("INSERT INTO tb_ocorrencia_autorizacao (ADE_CODIGO, OCA_CODIGO, TOC_CODIGO, USU_CODIGO, OCA_DATA, OCA_PERIODO, OCA_OBS) ");
                query.append(" SELECT ADE_CODIGO, ");
                query.append("CONCAT('O', ");
                query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
                query.append("SUBSTRING(LPAD(ade_numero, 12, '0'), 1, 12), ");
                query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)) AS OCA_CODIGO, '");
                query.append(CodedValues.TOC_ALTERACAO_CONTRATO).append("' AS TOC_CODIGO, '");
                query.append(CodedValues.USU_CODIGO_SISTEMA).append("' AS USU_CODIGO, NOW() AS OCA_DATA, PEX_PERIODO_POS AS OCA_PERIODO, ");
                query.append("'").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.extensao.prazo", (AcessoSistema) null)).append("' AS OCA_OBS");
                query.append(" FROM ").append(fromClause);
                query.append(" WHERE ").append(whereClause);
                executeUpdate(query.toString(), queryParams);

                // Estender prazo dos contratos que possuem saldo devedor
                query.setLength(0);
                query.append("UPDATE ").append(fromClause);
                query.append(" SET ").append(Columns.ADE_PRAZO).append(" = ");
                query.append(Columns.ADE_PRAZO).append(" + 1, ");
                query.append(Columns.ADE_ANO_MES_FIM).append(" = DATE_ADD(");
                query.append(Columns.ADE_ANO_MES_FIM).append(", INTERVAL 1 MONTH)");
                query.append(" WHERE ").append(whereClause);
                executeUpdate(query.toString(), queryParams);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /***************************************************************************/
    /**
     * Lista os contratos de correção para serviços que possuem correção de
     * saldo devedor em outro serviço.
     * @throws DAOException
     */
    @Override
    public List<TransferObject> listarContratosCorrecaoOutroServico(List<String> orgCodigos, List<String> estCodigos) throws DAOException {
        try {
            // Lista os serviços que possuem correção de saldo devedor em outro serviço
            List<String> servicosCorrecao = pesquisarServicosCorrecaoSaldoDevedor(CodedValues.CORRECAO_SALDO_DEVEDOR_EM_OUTRO_SERVICO);
            if (servicosCorrecao != null && servicosCorrecao.size() > 0) {
                final MapSqlParameterSource queryParams = new MapSqlParameterSource();

                // Lista os contratos que possuem correção de saldo devedor em outro serviço
                // mas que não possuem relacionamento com outro contrato de correção. Esses
                // contratos são inseridos na tabela temporária para a rotina de inserção
                // de contratos.

                // Remove a tabela temporária
                final StringBuilder query = new StringBuilder();
                query.append("DROP TEMPORARY TABLE IF EXISTS ").append(TMP_TABLE_INCLUSAO_CONTRATO);
                executeUpdate(query.toString(), queryParams);

                // Cria a tabela temporária
                query.setLength(0);
                query.append("CREATE TEMPORARY TABLE ").append(TMP_TABLE_INCLUSAO_CONTRATO);
                query.append(" (ADE_CODIGO_ORIGEM CHAR(32), ADE_INT_FOLHA SMALLINT(6), RSE_CODIGO CHAR(32),");
                query.append("  SVC_CODIGO CHAR(32), ORG_CODIGO CHAR(32), CSA_CODIGO CHAR(32))");
                executeUpdate(query.toString(), queryParams);

                // Insere na tabela temporaria contratos que possuem controle de saldo
                // devedor e que tem saldo devedor menor ou igual a zero
                query.setLength(0);
                query.append("INSERT INTO ").append(TMP_TABLE_INCLUSAO_CONTRATO);
                query.append(" (ADE_CODIGO_ORIGEM, ADE_INT_FOLHA, RSE_CODIGO, SVC_CODIGO, ORG_CODIGO, CSA_CODIGO)");
                query.append(" SELECT ").append(Columns.ADE_CODIGO).append(", ");
                query.append("(CASE ").append(Columns.PSE_VLR);
                query.append(" WHEN ").append(CodedValues.CORRECAO_ENVIADA_JUNTO_PRINCIPAL).append(" THEN ").append(CodedValues.INTEGRA_FOLHA_SIM);
                query.append(" WHEN ").append(CodedValues.CORRECAO_ENVIADA_APOS_PRINCIPAL).append(" THEN ").append(CodedValues.INTEGRA_FOLHA_NAO);
                query.append(" ELSE ").append(CodedValues.INTEGRA_FOLHA_SIM);
                query.append(" END), ");
                query.append(Columns.ADE_RSE_CODIGO).append(", ");
                query.append(Columns.RSV_SVC_CODIGO_DESTINO).append(", ");
                query.append(Columns.CNV_ORG_CODIGO).append(", ");
                query.append(Columns.CNV_CSA_CODIGO);
                query.append(" FROM ").append(Columns.TB_AUTORIZACAO_DESCONTO);
                query.append(" INNER JOIN ").append(Columns.TB_VERBA_CONVENIO).append(" ON (");
                query.append(Columns.ADE_VCO_CODIGO).append(" = ").append(Columns.VCO_CODIGO).append(")");
                query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (");
                query.append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(")");
                query.append(" INNER JOIN ").append(Columns.TB_RELACIONAMENTO_SERVICO).append(" ON (");
                query.append(Columns.CNV_SVC_CODIGO).append(" = ").append(Columns.RSV_SVC_CODIGO_ORIGEM).append(")");
                query.append(" LEFT OUTER JOIN ").append(Columns.TB_RELACIONAMENTO_AUTORIZACAO).append(" ON (");
                query.append(Columns.ADE_CODIGO).append(" = ").append(Columns.RAD_ADE_CODIGO_ORIGEM);
                query.append(" AND ").append(Columns.RAD_TNT_CODIGO).append(" = '");
                query.append(CodedValues.TNT_CORRECAO_SALDO).append("')");
                query.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" ON (");
                query.append(Columns.CNV_SVC_CODIGO).append(" = ").append(Columns.PSE_SVC_CODIGO);
                query.append(" AND ").append(Columns.PSE_TPS_CODIGO);
                query.append(" = '").append(CodedValues.TPS_CORRECAO_ENVIADA_APOS_PRINCIPAL).append("')");
                // Serviços que possuem correção de saldo devedor em outro serviço
                query.append(" WHERE ").append(Columns.CNV_SVC_CODIGO);
                query.append(" IN ('").append(TextHelper.join(servicosCorrecao, "','")).append("')");
                // Serviço com relacionamento de correção de saldo devedor
                query.append(" AND ").append(Columns.RSV_TNT_CODIGO);
                query.append(" = '").append(CodedValues.TNT_CORRECAO_SALDO).append("'");
                // Contratos que não foram concluidos
                query.append(" AND ").append(Columns.ADE_SAD_CODIGO);
                query.append(" IN ('").append(TextHelper.join(SAD_CODIGOS, "','")).append("')");
                // Contratos que são enviados para a folha
                query.append(" AND ").append(Columns.ADE_INT_FOLHA);
                query.append(" = ").append(CodedValues.INTEGRA_FOLHA_SIM);
                // Contratos com saldo devedor maior que zero
                query.append(" AND ").append(Columns.ADE_VLR_SDO_RET).append(" > 0");
                // Contratos sem relacionamento de correção
                query.append(" AND ").append(Columns.RAD_ADE_CODIGO_DESTINO).append(" IS NULL");
                query.append(getSufixo(orgCodigos, estCodigos, queryParams));
                executeUpdate(query.toString(), queryParams);

                // Seleciona os contratos que devem possuir um novo contrato de correção
                query.setLength(0);
                query.append("SELECT ADE_CODIGO_ORIGEM, ");
                query.append(Columns.VCO_CODIGO).append(" AS VCO_CODIGO, RSE_CODIGO, ");
                query.append("DATE_ADD(PERIODO, INTERVAL 1 MONTH) AS ADE_ANO_MES_INI, ");
                query.append("ADE_INT_FOLHA, ").append(Columns.PSE_VLR).append(" AS ADE_INC_MARGEM");
                query.append(" FROM ").append(TMP_TABLE_INCLUSAO_CONTRATO);
                query.append(" INNER JOIN ").append(TMP_TABLE_AJUSTE_SALDO_DEVEDOR).append(" ON (");
                query.append(TMP_TABLE_AJUSTE_SALDO_DEVEDOR).append(".ORG_CODIGO = ");
                query.append(TMP_TABLE_INCLUSAO_CONTRATO).append(".ORG_CODIGO)");
                query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (");
                query.append(Columns.CNV_SVC_CODIGO).append(" = ");
                query.append(TMP_TABLE_INCLUSAO_CONTRATO).append(".SVC_CODIGO AND ");
                query.append(Columns.CNV_ORG_CODIGO).append(" = ");
                query.append(TMP_TABLE_INCLUSAO_CONTRATO).append(".ORG_CODIGO AND ");
                query.append(Columns.CNV_CSA_CODIGO).append(" = ");
                query.append(TMP_TABLE_INCLUSAO_CONTRATO).append(".CSA_CODIGO)");
                query.append(" INNER JOIN ").append(Columns.TB_VERBA_CONVENIO).append(" ON (");
                query.append(Columns.CNV_CODIGO).append(" = ");
                query.append(Columns.VCO_CNV_CODIGO).append(")");
                query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" ON (");
                query.append(Columns.CNV_SVC_CODIGO).append(" = ");
                query.append(Columns.PSE_SVC_CODIGO).append(")");
                query.append(" WHERE ").append(Columns.PSE_TPS_CODIGO);
                query.append(" = '").append(CodedValues.TPS_INCIDE_MARGEM).append("'");
                query.append(getSufixo(orgCodigos, estCodigos, queryParams));

                String fields = "ADE_CODIGO_ORIGEM,VCO_CODIGO,RSE_CODIGO,ADE_ANO_MES_INI,ADE_INT_FOLHA,ADE_INC_MARGEM";

                return MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fields, MySqlDAOFactory.SEPARADOR);
            }

            return null;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Conclui os contratos que não foram pagos totalmente pados dentro do exercício.
     * Apenas contratos que possuem controle de saldo devedor.
     * Verifica parâmetro que determina o mês/quinzena de virada de exercício.
     * @throws DAOException
     */
    @Override
    public void concluirContratosNaoPagosNoExercicio(List<String> orgCodigos, List<String> estCodigos) throws DAOException {
        try {
            List<String> servicosControleSaldo = pesquisarServicosControleSaldoDevedor();
            if (servicosControleSaldo != null && servicosControleSaldo.size() > 0) {
                // Insere na tabela temporária contratos com saldo devedor não pago no exercício
                listarCanditadosConclusaoNaoPagos(orgCodigos, estCodigos, servicosControleSaldo);

                // Insere ocorrencia de conclusão de contrato
                criarOcorrenciasConclusaoNaoPagos();

                // Conclui os contratos presentes na tabela temporaria de contratos não pagos no exercício
                concluirContratosNaoPagos();

            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Insere na tabela temporaria contratos que possuem controle de saldo
     * devedor e que tem saldo devedor maior que zero quando os serviços estão configurados para concluir
     * contratos não pagos dentro do exercício.
     * São candidatos a conclusão.
     * @param orgCodigos List
     * @param estCodigos List
     * @param servicosControleSaldo List
     * @throws DataAccessException
     */
    private void listarCanditadosConclusaoNaoPagos(List<String> orgCodigos, List<String> estCodigos, List<String> servicosControleSaldo) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("servicosControleSaldo", servicosControleSaldo);

        // Fim de exercício
        int mesFimExercicio = 12;
        Object param = ParamSist.getInstance().getParam(CodedValues.TPC_MES_REFERENCIA_MUDANCA_EXERCICIO_FISCAL, AcessoSistema.getAcessoUsuarioSistema());
        if (!TextHelper.isNull(param)) {
            mesFimExercicio = Integer.parseInt(param.toString());
        }
        // calcula fim do exercício pelo número do período
        String mesDia = PeriodoHelper.converterNumPeriodoParaMesDia(mesFimExercicio, AcessoSistema.getAcessoUsuarioSistema());

        // Remove a tabela temporária
        final StringBuilder query = new StringBuilder();
        query.append("DROP TEMPORARY TABLE IF EXISTS ").append(TMP_TABLE_CONCLUSAO_CONTRATO_NAO_PAGO);
        executeUpdate(query.toString(), queryParams);

        // Cria a tabela temporária
        query.setLength(0);
        query.append("CREATE TEMPORARY TABLE ").append(TMP_TABLE_CONCLUSAO_CONTRATO_NAO_PAGO);
        query.append(" (ADE_CODIGO CHAR(32), ADE_NUMERO INT(10) UNSIGNED, PEX_PERIODO_POS DATE)");
        executeUpdate(query.toString(), queryParams);

        // Insere na tabela temporaria contratos que possuem controle de saldo
        // devedor e que tem saldo devedor maior que zero, não pagos no exercício
        query.setLength(0);
        query.append("INSERT INTO ").append(TMP_TABLE_CONCLUSAO_CONTRATO_NAO_PAGO);
        query.append(" (ADE_CODIGO, ADE_NUMERO, PEX_PERIODO_POS) ");
        query.append("SELECT ").append(Columns.ADE_CODIGO).append(",").append(Columns.ADE_NUMERO).append(",").append(Columns.PEX_PERIODO_POS);
        query.append(" FROM ").append(Columns.TB_AUTORIZACAO_DESCONTO);
        query.append(" INNER JOIN ").append(Columns.TB_VERBA_CONVENIO).append(" ON (");
        query.append(Columns.ADE_VCO_CODIGO).append(" = ").append(Columns.VCO_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (");
        query.append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_PERIODO_EXPORTACAO).append(" ON (");
        query.append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.PEX_ORG_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_SERVICO).append(" ON (");
        query.append(Columns.CNV_SVC_CODIGO).append(" = ").append(Columns.SVC_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" ON (");
        query.append(Columns.SVC_CODIGO).append(" = ").append(Columns.PSE_SVC_CODIGO).append(")");
        // Serviços que concluem ade não paga no exercício
        query.append(" WHERE ").append(Columns.PSE_TPS_CODIGO).append(" = ").append(CodedValues.TPS_CONCLUI_ADE_NAO_PAGA_NO_EXERCICIO);
        query.append(" AND ").append(Columns.PSE_VLR).append(" = '1' ");
        // Serviços que possuem controle de saldo devedor
        query.append(" AND ").append(Columns.SVC_CODIGO);
        query.append(" IN (:servicosControleSaldo)");
        // Contratos que não foram concluidos
        query.append(" AND ").append(Columns.ADE_SAD_CODIGO);
        query.append(" IN ('").append(TextHelper.join(SAD_CODIGOS, "','")).append("')");
        // Contratos que são enviados para a folha
        query.append(" AND ").append(Columns.ADE_INT_FOLHA);
        query.append(" = ").append(CodedValues.INTEGRA_FOLHA_SIM);
        // Período de mudança de exercício
        query.append(" AND ").append(Columns.PEX_PERIODO_POS).append(" >= ").append(" CONCAT(").append("YEAR(").append(Columns.PEX_PERIODO_POS).append("),'-','").append(mesDia).append("') ");
        query.append(" AND ").append(Columns.ADE_ANO_MES_INI).append(" <= ").append(" CONCAT(").append("YEAR(").append(Columns.PEX_PERIODO_POS).append("),'-','").append(mesDia).append("') ");
        // Contratos com saldo devedor maior que zero
        query.append(" AND ").append(Columns.ADE_VLR_SDO_RET).append(" > 0");
        query.append(getSufixo(orgCodigos, estCodigos, queryParams));
        executeUpdate(query.toString(), queryParams);
    }

    /**
     * Insere ocorrências de conclusão de contrato pelo saldo devedor não pago no exercicio
     * @throws DataAccessException
     */
    private void criarOcorrenciasConclusaoNaoPagos() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("SET @rownum := 0;");
        executeUpdate(query.toString(), queryParams);

        query.setLength(0);
        query.append("INSERT INTO tb_ocorrencia_autorizacao (ADE_CODIGO, OCA_CODIGO, TOC_CODIGO, USU_CODIGO, OCA_DATA, OCA_PERIODO, OCA_OBS) ");
        query.append(" SELECT ADE_CODIGO, ");
        query.append("CONCAT('P', ");
        query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
        query.append("SUBSTRING(LPAD(ade_numero, 12, '0'), 1, 12), ");
        query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)) AS OCA_CODIGO, '");
        query.append(CodedValues.TOC_CONCLUSAO_CONTRATO).append("' AS TOC_CODIGO, '");
        query.append(CodedValues.USU_CODIGO_SISTEMA).append("' AS USU_CODIGO, NOW() AS OCA_DATA, PEX_PERIODO_POS AS OCA_PERIODO, ");
        query.append("'").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido", (AcessoSistema) null)).append("' AS OCA_OBS");
        query.append(" FROM ").append(TMP_TABLE_CONCLUSAO_CONTRATO_NAO_PAGO);
        executeUpdate(query.toString(), queryParams);
    }

    /**
     * Conclui os contratos com saldo devedor maior que zero. Muda o status e zera os campos de saldo devedor.
     * @throws DataAccessException
     */
    private void concluirContratosNaoPagos() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("UPDATE ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(", ");
        query.append(TMP_TABLE_CONCLUSAO_CONTRATO_NAO_PAGO);
        query.append(" SET ");
        query.append(Columns.ADE_SAD_CODIGO).append(" = '");
        query.append(CodedValues.SAD_CONCLUIDO).append("', ");
        query.append(Columns.ADE_VLR_SDO_MOV).append(" = 0, ");
        query.append(Columns.ADE_VLR_SDO_RET).append(" = 0 ");
        query.append(" WHERE ");
        query.append(Columns.ADE_CODIGO).append(" = ");
        query.append(TMP_TABLE_CONCLUSAO_CONTRATO_NAO_PAGO).append(".ADE_CODIGO");
        executeUpdate(query.toString(), queryParams);
    }
}
