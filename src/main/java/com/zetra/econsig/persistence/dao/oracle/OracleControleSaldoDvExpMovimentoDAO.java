package com.zetra.econsig.persistence.dao.oracle;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.generic.GenericControleSaldoDvExpMovimentoDAO;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: OracleControleSaldoDvExpMovimentoDAO</p>
 * <p>Description: DAO de Oracle para rotinas de controle de saldo devedor
 * efetuadas na exportação de movimento financeiro.</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor Lucas
 * $Author$
 * $Revision$
 * $Date$
 */
public class OracleControleSaldoDvExpMovimentoDAO extends GenericControleSaldoDvExpMovimentoDAO {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OracleControleSaldoDvExpMovimentoDAO.class);

    private static final String TMP_TABLE_CORRECAO_SALDO_DEVEDOR = "tmp_valor_correcao_saldo_dv";

    /***************************************************************************/
    /**
     * Realiza o ajuste do saldo devedor para contratos que possuem controle
     * do saldo devedor.
     * @throws DAOException
     */
    @Override
    public void ajustarSaldoDevedor() throws DAOException {
        try {
            List<String> servicosControleSaldo = pesquisarServicosControleSaldoDevedor();
            if (servicosControleSaldo != null && servicosControleSaldo.size() > 0) {
                final MapSqlParameterSource queryParams = new MapSqlParameterSource();
                final StringBuilder query = new StringBuilder();
                query.append("UPDATE ").append(Columns.TB_AUTORIZACAO_DESCONTO);
                query.append(" SET ").append(Columns.ADE_VLR_SDO_MOV).append(" = ");
                query.append(Columns.ADE_VLR_SDO_RET);

                // Contratos que não foram concluidos
                query.append(" WHERE ").append(Columns.ADE_SAD_CODIGO);
                query.append(" IN ('").append(TextHelper.join(SAD_CODIGOS, "','")).append("')");

                // Serviços que possuem controle de saldo devedor
                query.append(" AND ").append(Columns.ADE_VCO_CODIGO).append(" IN (");
                query.append(" SELECT ").append(Columns.VCO_CODIGO);
                query.append(" FROM ").append(Columns.TB_VERBA_CONVENIO);
                query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (").append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(")");
                query.append(" WHERE ").append(Columns.CNV_SVC_CODIGO).append(" IN ('").append(TextHelper.join(servicosControleSaldo, "','")).append("'))");

                executeUpdate(query.toString(), queryParams);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Cria tabela temporária onde serão inseridos os dados da correção
     * do saldo devedor dos contratos que possuem parâmetro indicando
     * correção.
     * @throws DataAccessException
     */
    @Override
    protected void criarTabelaTemporariaCorrecaoSdv() throws DataAccessException {
        // Remove a tabela temporária
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("CALL dropTableIfExists('").append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR).append("')");
        executeUpdate(query.toString(), queryParams);

        // Cria a tabela temporária
        query.setLength(0);
        query.append("CREATE TABLE ").append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR);
        query.append(" (ADE_CODIGO VARCHAR2(32), ADE_NUMERO NUMBER(11,0), SVC_CODIGO VARCHAR2(32), VLR NUMBER(13,2), PEX_PERIODO_POS DATE)");
        executeUpdate(query.toString(), queryParams);
    }

    /**
     * Calcula a correção para os contratos sobre o total do saldo devedor.
     * Insere na tabela temporária o adeCodigo dos contratos a serem
     * corrigidos, juntamente com o valor da correção a ser aplicada.
     * O valor calculado é truncado para 2 posições decimais (TRUNCATE
     * e não ROUND)
     * @param servicos List
     * @throws DataAccessException
     */
    @Override
    protected void calcularCorrecaoTotalSaldoDevedor(List<String> servicos) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("INSERT INTO ").append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR).append(" SELECT ");
        query.append(Columns.ADE_CODIGO).append(" AS ADE_CODIGO, ");
        query.append(Columns.ADE_NUMERO).append(" AS ADE_NUMERO, ");
        query.append(Columns.CNV_SVC_CODIGO).append(" AS SVC_CODIGO, TRUNC(");
        query.append(Columns.ADE_VLR_SDO_RET).append(" * ").append(Columns.CCR_VLR).append(", 2) AS VLR, PEX_PERIODO_POS ");
        query.append(" FROM ").append(Columns.TB_AUTORIZACAO_DESCONTO);
        query.append(" INNER JOIN ").append(Columns.TB_VERBA_CONVENIO).append(" ON (");
        query.append(Columns.ADE_VCO_CODIGO).append(" = ").append(Columns.VCO_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (");
        query.append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_PERIODO_EXPORTACAO).append(" ON (");
        query.append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.PEX_ORG_CODIGO).append(")");
        // Parâmetro de serviço TPS_CORRECAO_SOBRE_TOTAL_SALDO_DV
        query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" ON (");
        query.append(Columns.CNV_SVC_CODIGO).append(" = ").append(Columns.PSE_SVC_CODIGO).append(")");
        // Parâmetro de serviço TPS_FORMA_CALCULO_CORRECAO_SALDO_DV
        query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" tpsFormaCorrecao ON (");
        query.append(Columns.CNV_SVC_CODIGO).append(" = tpsFormaCorrecao.SVC_CODIGO)");
        query.append(" INNER JOIN ").append(Columns.TB_COEFICIENTE_CORRECAO).append(" ON (");
        query.append(Columns.CCR_TCC_CODIGO).append(" = tpsFormaCorrecao.PSE_VLR)");
        // Serviços informados no parâmetro
        query.append(" WHERE ").append(Columns.CNV_SVC_CODIGO);
        query.append(" IN ('").append(TextHelper.join(servicos, "','")).append("')");
        // Status do contrato
        query.append(" AND ").append(Columns.ADE_SAD_CODIGO);
        query.append(" IN ('").append(TextHelper.join(SAD_CODIGOS, "','")).append("')");
        // Contratos que não foram feitos no período de exportação
        query.append(" AND ").append(Columns.ADE_ANO_MES_INI).append(" < ");
        query.append(Columns.PEX_DATA_INI);
        // Parâmetro de serviço TPS_CORRECAO_SOBRE_TOTAL_SALDO_DV
        query.append(" AND ").append(Columns.PSE_TPS_CODIGO).append(" = '");
        query.append(CodedValues.TPS_CORRECAO_SOBRE_TOTAL_SALDO_DV).append("'");
        query.append(" AND ").append(Columns.PSE_VLR).append(" = '");
        query.append(CodedValues.CORRECAO_SOBRE_TOTAL_SALDO_DEVEDOR).append("'");
        // Parâmetro de serviço TPS_FORMA_CALCULO_CORRECAO_SALDO_DV
        query.append(" AND tpsFormaCorrecao.TPS_CODIGO = '");
        query.append(CodedValues.TPS_FORMA_CALCULO_CORRECAO_SALDO_DV).append("'");
        // Utiliza o último coeficiente cadastrado
        query.append(" AND ((").append(Columns.CCR_ANO).append(" * 12) + ").append(Columns.CCR_MES).append(") = ");
        query.append(" ( ");
        query.append(" SELECT MAX((").append(Columns.CCR_ANO).append(" * 12) + ").append(Columns.CCR_MES).append(")");
        query.append(" FROM ").append(Columns.TB_COEFICIENTE_CORRECAO);
        query.append(" WHERE ").append(Columns.CCR_TCC_CODIGO).append(" = tpsFormaCorrecao.PSE_VLR");
        query.append(" AND ((").append(Columns.CCR_ANO).append(" * 12) + ").append(Columns.CCR_MES).append(") <= ");
        query.append(" ((").append(" EXTRACT(YEAR FROM ").append(Columns.PEX_PERIODO).append(") * 12) + ");
        query.append(" EXTRACT(MONTH FROM ").append(Columns.PEX_PERIODO).append("))");
        query.append(" ) ");

        executeUpdate(query.toString(), queryParams);
    }

    /**
     * Calcula a correção para os contratos sobre o saldo das parcelas já enviadas
     * para a folha de pagamento. Insere na tabela temporária o adeCodigo dos
     * contratos a serem  corrigidos, juntamente com o valor da correção a ser aplicada
     * @param servicos List
     * @throws DataAccessException
     */
    @Override
    protected void calcularCorrecaoSaldoParcelas(List<String> servicos) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("INSERT INTO ").append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR).append(" SELECT ");
        query.append(Columns.ADE_CODIGO).append(" AS ADE_CODIGO, ");
        query.append(Columns.ADE_NUMERO).append(" AS ADE_NUMERO, ");
        query.append(Columns.CNV_SVC_CODIGO).append(" AS SVC_CODIGO, ");
        // Faz o mínimo entre a correção do saldo de parcelas e a correção
        // do saldo devedor total
        query.append(" LEAST(");
        query.append(" SUM(TRUNC((").append(Columns.PRD_VLR_PREVISTO).append(" - ");
        query.append("COALESCE(");
        query.append("(CASE ").append(Columns.PRD_SPD_CODIGO);
        query.append(" WHEN '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' THEN ").append(Columns.PRD_VLR_REALIZADO);
        query.append(" WHEN '").append(CodedValues.SPD_LIQUIDADAMANUAL).append("' THEN ").append(Columns.PRD_VLR_REALIZADO);
        query.append(" ELSE 0 END)");
        query.append(", 0)");
        query.append(") * ").append(Columns.CCR_VLR).append(", 2))");
        query.append(",");
        query.append(" TRUNC(").append(Columns.ADE_VLR_SDO_RET).append(" * ");
        query.append(Columns.CCR_VLR).append(", 2)");
        query.append(") AS VLR, PEX_PERIODO_POS ");
        query.append(" FROM ").append(Columns.TB_AUTORIZACAO_DESCONTO);
        query.append(" INNER JOIN ").append(Columns.TB_PARCELA_DESCONTO).append(" ON (");
        query.append(Columns.PRD_ADE_CODIGO).append(" = ").append(Columns.ADE_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_VERBA_CONVENIO).append(" ON (");
        query.append(Columns.ADE_VCO_CODIGO).append(" = ").append(Columns.VCO_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (");
        query.append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_PERIODO_EXPORTACAO).append(" ON (");
        query.append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.PEX_ORG_CODIGO).append(")");
        // Parâmetro de serviço TPS_CORRECAO_SOBRE_TOTAL_SALDO_DV
        query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" ON (");
        query.append(Columns.CNV_SVC_CODIGO).append(" = ").append(Columns.PSE_SVC_CODIGO).append(")");
        // Parâmetro de serviço TPS_FORMA_CALCULO_CORRECAO_SALDO_DV
        query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" tpsFormaCorrecao ON (");
        query.append(Columns.CNV_SVC_CODIGO).append(" = tpsFormaCorrecao.SVC_CODIGO)");
        query.append(" INNER JOIN ").append(Columns.TB_COEFICIENTE_CORRECAO).append(" ON (");
        query.append(Columns.CCR_TCC_CODIGO).append(" = tpsFormaCorrecao.PSE_VLR)");
        // Serviços informados no parâmetro
        query.append(" WHERE ").append(Columns.CNV_SVC_CODIGO);
        query.append(" IN ('").append(TextHelper.join(servicos, "','")).append("')");
        // Status do contrato
        query.append(" AND ").append(Columns.ADE_SAD_CODIGO);
        query.append(" IN ('").append(TextHelper.join(SAD_CODIGOS, "','")).append("')");
        // Status da parcela
        query.append(" AND ").append(Columns.PRD_SPD_CODIGO);
        query.append(" IN ('").append(TextHelper.join(SPD_CODIGOS, "','")).append("')");
        // Contratos que não foram feitos no período de exportação
        query.append(" AND ").append(Columns.ADE_ANO_MES_INI).append(" < ");
        query.append(Columns.PEX_DATA_INI);
        // Parâmetro de serviço TPS_CORRECAO_SOBRE_TOTAL_SALDO_DV para CORRECAO_SOBRE_SALDO_PARCELAS
        query.append(" AND ").append(Columns.PSE_TPS_CODIGO).append(" = '");
        query.append(CodedValues.TPS_CORRECAO_SOBRE_TOTAL_SALDO_DV).append("'");
        query.append(" AND ").append(Columns.PSE_VLR).append(" = '");
        query.append(CodedValues.CORRECAO_SOBRE_SALDO_PARCELAS).append("'");
        // Parâmetro de serviço TPS_FORMA_CALCULO_CORRECAO_SALDO_DV
        query.append(" AND tpsFormaCorrecao.TPS_CODIGO = '");
        query.append(CodedValues.TPS_FORMA_CALCULO_CORRECAO_SALDO_DV).append("'");
        // Utiliza o último coeficiente cadastrado
        query.append(" AND ((").append(Columns.CCR_ANO).append(" * 12) + ").append(Columns.CCR_MES).append(") = ");
        query.append(" ( ");
        query.append(" SELECT MAX((").append(Columns.CCR_ANO).append(" * 12) + ").append(Columns.CCR_MES).append(")");
        query.append(" FROM ").append(Columns.TB_COEFICIENTE_CORRECAO);
        query.append(" WHERE ").append(Columns.CCR_TCC_CODIGO).append(" = tpsFormaCorrecao.PSE_VLR");
        query.append(" AND ((").append(Columns.CCR_ANO).append(" * 12) + ").append(Columns.CCR_MES).append(") <= ");
        query.append(" ((EXTRACT(YEAR FROM ").append(Columns.PEX_PERIODO).append(") * 12) + ");
        query.append(" EXTRACT(MONTH FROM ").append(Columns.PEX_PERIODO).append("))");
        query.append(" ) ");
        // Agrupa para cada consignação
        query.append(" GROUP BY ");
        query.append(Columns.ADE_CODIGO).append(",");
        query.append(Columns.ADE_NUMERO).append(",");
        query.append(Columns.CNV_SVC_CODIGO).append(",");
        query.append(Columns.ADE_VLR_SDO_RET).append(",");
        query.append(Columns.CCR_VLR).append(",");
        query.append(Columns.PEX_PERIODO_POS);
        executeUpdate(query.toString(), queryParams);
    }

    /**
     * Corrige o saldo devedor ADE_VLR_SDO_MOV de acordo com a tabela temporária
     * que contém o cálculo da correção, para os serviços com correções no próprio
     * serviço
     * @throws DataAccessException
     */
    @Override
    protected void corrigirSdvProprioServico() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("UPDATE ").append(Columns.TB_AUTORIZACAO_DESCONTO);

        // ADE_VLR_SDO_MOV = ADE_VLR_SDO_RET + VALOR_CORREÇÃO
        query.append(" SET ").append(Columns.ADE_VLR_SDO_MOV).append(" = ");
        query.append(Columns.ADE_VLR_SDO_RET).append(" + (SELECT VLR ");
        query.append(" FROM ").append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR);
        query.append(" WHERE ").append(Columns.ADE_CODIGO).append(" = ");
        query.append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR).append(".ADE_CODIGO)");

        // CORREÇÃO NO PRÓPRIO SERVIÇO
        query.append(" WHERE EXISTS (SELECT 1 ");
        query.append(" FROM ").append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR);
        query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE);
        query.append(" ON (").append(Columns.PSE_SVC_CODIGO).append(" = ");
        query.append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR).append(".SVC_CODIGO)");
        query.append(" WHERE ").append(Columns.ADE_CODIGO).append(" = ");
        query.append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR).append(".ADE_CODIGO");
        query.append(" AND ").append(Columns.PSE_TPS_CODIGO).append(" = '");
        query.append(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR).append("'");
        query.append(" AND ").append(Columns.PSE_VLR).append(" = '");
        query.append(CodedValues.CORRECAO_SALDO_DEVEDOR_PROPRIO_SERVICO).append("')");
        executeUpdate(query.toString(), queryParams);
    }

    /**
     * Corrige o saldo devedor ADE_VLR_SDO_MOV de acordo com a tabela temporária
     * que contém o cálculo da correção, para os serviços que possuem correções
     * em outro serviço
     * @throws DataAccessException
     */
    @Override
    protected void corrigirSdvEmOutroServico() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("UPDATE ").append(Columns.TB_AUTORIZACAO_DESCONTO);
        query.append(" SET ");
        // CONTRATO DE CORREÇÃO: ADE_VLR_SDO_MOV = ADE_VLR_SDO_RET + VALOR_CORREÇÃO
        query.append(Columns.ADE_VLR_SDO_MOV).append(" = ").append(Columns.ADE_VLR_SDO_RET).append(" + (SELECT VLR ");
        query.append(" FROM ").append(Columns.TB_RELACIONAMENTO_AUTORIZACAO);
        query.append(" INNER JOIN ").append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR).append(" ON (").append(Columns.RAD_ADE_CODIGO_ORIGEM).append(" = ").append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR).append(".ADE_CODIGO").append(")");
        query.append(" WHERE ").append(Columns.ADE_CODIGO).append(" = ").append(Columns.RAD_ADE_CODIGO_DESTINO).append(")");
        query.append(" WHERE EXISTS (SELECT 1 FROM ").append(Columns.TB_RELACIONAMENTO_AUTORIZACAO);
        query.append(" INNER JOIN ").append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR).append(" ON (").append(Columns.RAD_ADE_CODIGO_ORIGEM).append(" = ").append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR).append(".ADE_CODIGO").append(")");
        query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" ON (").append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR).append(".SVC_CODIGO").append(" = ").append(Columns.PSE_SVC_CODIGO).append(")");
        query.append(" WHERE ").append(Columns.ADE_CODIGO).append(" = ").append(Columns.RAD_ADE_CODIGO_DESTINO);
        query.append(" AND ").append(Columns.RAD_TNT_CODIGO).append(" = '").append(CodedValues.TNT_CORRECAO_SALDO).append("'");
        query.append(" AND ").append(Columns.PSE_TPS_CODIGO).append(" = '").append(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR).append("'");
        query.append(" AND ").append(Columns.PSE_VLR).append(" = '").append(CodedValues.CORRECAO_SALDO_DEVEDOR_EM_OUTRO_SERVICO).append("')");
        executeUpdate(query.toString(), queryParams);

        query.setLength(0);
        query.append("UPDATE ").append(Columns.TB_AUTORIZACAO_DESCONTO);
        query.append(" SET ");
        // CONTRATO PRINCIPAL: ADE_VLR_SDO_MOV = ADE_VLR_SDO_RET
        query.append(Columns.ADE_VLR_SDO_MOV).append(" = ").append(Columns.ADE_VLR_SDO_RET);
        query.append(" WHERE EXISTS (SELECT 1 FROM ").append(Columns.TB_RELACIONAMENTO_AUTORIZACAO);
        query.append(" INNER JOIN ").append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR).append(" ON (").append(Columns.RAD_ADE_CODIGO_ORIGEM).append(" = ").append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR).append(".ADE_CODIGO").append(")");
        query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" ON (").append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR).append(".SVC_CODIGO").append(" = ").append(Columns.PSE_SVC_CODIGO).append(")");
        query.append(" WHERE ").append(Columns.ADE_CODIGO).append(" = ").append(Columns.RAD_ADE_CODIGO_ORIGEM);
        query.append(" AND ").append(Columns.RAD_TNT_CODIGO).append(" = '").append(CodedValues.TNT_CORRECAO_SALDO).append("'");
        query.append(" AND ").append(Columns.PSE_TPS_CODIGO).append(" = '").append(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR).append("'");
        query.append(" AND ").append(Columns.PSE_VLR).append(" = '").append(CodedValues.CORRECAO_SALDO_DEVEDOR_EM_OUTRO_SERVICO).append("')");
        executeUpdate(query.toString(), queryParams);

        // Acerta os contratos que são correções de outros serviços, mas que
        // também possuem correções. É necessário, pq o passo anterior utiliza
        // o ADE_VLR_SDO_RET para ajustar o valor do contrato, "perdendo" assim
        // a sua correção já aplicada
        query.setLength(0);
        query.append("UPDATE ").append(Columns.TB_AUTORIZACAO_DESCONTO);
        query.append(" SET ");
        // CONTRATO DE CORREÇÃO: ADE_VLR_SDO_MOV = ADE_VLR_SDO_RET + VALOR_CORREÇÃO (Correção dos dois contratos)
        query.append(Columns.ADE_VLR_SDO_MOV).append(" = ").append(Columns.ADE_VLR_SDO_RET);
        query.append(" + (SELECT tmpDestino.VLR + tmpOrigem.VLR ");
        query.append(" FROM ").append(Columns.TB_RELACIONAMENTO_AUTORIZACAO);
        query.append(" INNER JOIN ").append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR).append(" tmpDestino ON (").append(Columns.RAD_ADE_CODIGO_DESTINO).append(" = tmpDestino.ADE_CODIGO)");
        query.append(" INNER JOIN ").append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR).append(" tmpOrigem ON (").append(Columns.RAD_ADE_CODIGO_ORIGEM).append(" = tmpOrigem.ADE_CODIGO)");
        query.append(" WHERE ").append(Columns.ADE_CODIGO).append(" = ").append(Columns.RAD_ADE_CODIGO_DESTINO);
        query.append(" AND ").append(Columns.RAD_TNT_CODIGO).append(" = '").append(CodedValues.TNT_CORRECAO_SALDO).append("')");
        query.append(" WHERE EXISTS (SELECT 1");
        query.append(" FROM ").append(Columns.TB_RELACIONAMENTO_AUTORIZACAO);
        query.append(" INNER JOIN ").append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR).append(" tmpDestino ON (").append(Columns.RAD_ADE_CODIGO_DESTINO).append(" = tmpDestino.ADE_CODIGO)");
        query.append(" INNER JOIN ").append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR).append(" tmpOrigem ON (").append(Columns.RAD_ADE_CODIGO_ORIGEM).append(" = tmpOrigem.ADE_CODIGO)");
        query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" paramServicoDestino ON (paramServicoDestino.SVC_CODIGO = tmpDestino.SVC_CODIGO)");
        query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" paramServicoOrigem ON (paramServicoOrigem.SVC_CODIGO = tmpOrigem.SVC_CODIGO)");
        query.append(" WHERE ").append(Columns.ADE_CODIGO).append(" = ").append(Columns.RAD_ADE_CODIGO_DESTINO);
        query.append(" AND ").append(Columns.RAD_TNT_CODIGO).append(" = '").append(CodedValues.TNT_CORRECAO_SALDO).append("'");
        // O serviço de correção possui tb correção
        query.append(" AND paramServicoDestino.TPS_CODIGO = '").append(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR).append("'");
        query.append(" AND paramServicoDestino.PSE_VLR = '").append(CodedValues.CORRECAO_SALDO_DEVEDOR_PROPRIO_SERVICO).append("'");
        // O serviço principal tem correção em outro servico
        query.append(" AND paramServicoOrigem.TPS_CODIGO = '").append(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR).append("'");
        query.append(" AND paramServicoOrigem.PSE_VLR = '").append(CodedValues.CORRECAO_SALDO_DEVEDOR_EM_OUTRO_SERVICO).append("')");

        executeUpdate(query.toString(), queryParams);
    }

    /**
     * Insere ocorrências de correção de saldo devedor
     * @throws DataAccessException
     */
    @Override
    protected void criarOcorrenciasCorrecao() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tb_ocorrencia_autorizacao (ADE_CODIGO, OCA_CODIGO, TOC_CODIGO, USU_CODIGO, OCA_DATA, OCA_PERIODO, OCA_OBS) ");
        query.append(" SELECT ADE_CODIGO, ");
        query.append("('R' || ");
        query.append("TO_CHAR(CURRENT_TIMESTAMP, 'yymmddhh24miss') || ");
        query.append("SUBSTR(LPAD(ADE_NUMERO, 12, '0'), 1, 12) || ");
        query.append("SUBSTR(LPAD(ROWNUM, 7, '0'), 1, 7)) AS OCA_CODIGO, ");
        query.append("'").append(CodedValues.TOC_CORRECAO_SALDO_DEVEDOR).append("' AS TOC_CODIGO, ");
        query.append("'").append(CodedValues.USU_CODIGO_SISTEMA).append("' AS USU_CODIGO, ");
        query.append("CURRENT_TIMESTAMP AS OCA_DATA, PEX_PERIODO_POS AS OCA_PERIODO, ");
        query.append("('").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.correcao.saldo.devedor", (AcessoSistema) null)).append(": ").append(ApplicationResourcesHelper.getMessage("rotulo.moeda", (AcessoSistema) null)).append(" ' || REPLACE(VLR, '.', ',')) AS OCA_OBS");
        query.append(" FROM ").append(TMP_TABLE_CORRECAO_SALDO_DEVEDOR);
        executeUpdate(query.toString(), queryParams);
    }

    /**
     * Remove as ocorrências de exportação do período atual.
     * Necessário caso o movimento seja reexportado, evitando a duplicação
     * das ocorrências.
     * @throws DataAccessException
     */
    @Override
    protected void removerOcorrenciasCorrecaoDoPeriodo() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("DELETE FROM ").append(Columns.TB_OCORRENCIA_AUTORIZACAO);
        // TOC_CORRECAO_SALDO_DEVEDOR
        query.append(" WHERE ").append(Columns.OCA_TOC_CODIGO).append(" = '").append(CodedValues.TOC_CORRECAO_SALDO_DEVEDOR).append("'");
        // Ocorrências após o período de exportação
        query.append(" AND ").append(Columns.OCA_DATA).append(" > (SELECT ").append(Columns.PEX_DATA_FIM);
        query.append(" FROM ").append(Columns.TB_AUTORIZACAO_DESCONTO);
        query.append(" INNER JOIN ").append(Columns.TB_VERBA_CONVENIO).append(" ON (").append(Columns.ADE_VCO_CODIGO).append(" = ").append(Columns.VCO_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (").append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_PERIODO_EXPORTACAO).append(" ON (").append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.PEX_ORG_CODIGO).append(")");
        query.append(" WHERE ").append(Columns.OCA_ADE_CODIGO).append(" = ").append(Columns.ADE_CODIGO);
        query.append(")");
        executeUpdate(query.toString(), queryParams);
    }

    /**
     * Ajusta o ADE_VLR para os contratos que não tem controle de teto
     * @param servicosControleSaldo List
     * @throws DataAccessException
     */
    @Override
    protected void ajustarAdeValorSemControleTeto(List<String> servicosControleSaldo) throws DataAccessException {
        // Para os serviços que não possuem controle de teto setar no
        // ADE_VLR o valor do saldo ajustado
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("UPDATE ").append(Columns.TB_AUTORIZACAO_DESCONTO);
        query.append(" SET ").append(Columns.ADE_VLR).append(" = COALESCE(");
        query.append(Columns.ADE_VLR_SDO_MOV).append(", 0.00)");
        query.append(" WHERE EXISTS (SELECT 1");
        query.append(" FROM ").append(Columns.TB_VERBA_CONVENIO);
        query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (").append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" ON (").append(Columns.CNV_SVC_CODIGO).append(" = ").append(Columns.PSE_SVC_CODIGO).append(")");
        query.append(" WHERE ").append(Columns.ADE_VCO_CODIGO).append(" = ").append(Columns.VCO_CODIGO);
        // Serviços que possuem controle de saldo devedor
        query.append(" AND ").append(Columns.CNV_SVC_CODIGO).append(" IN ('").append(TextHelper.join(servicosControleSaldo, "','")).append("')");
        // Contratos que não foram concluidos
        query.append(" AND ").append(Columns.ADE_SAD_CODIGO).append(" IN ('").append(TextHelper.join(SAD_CODIGOS, "','")).append("')");
        // Serviços que nao controlam vlr maximo de desconto
        query.append(" AND ").append(Columns.PSE_TPS_CODIGO).append(" = '").append(CodedValues.TPS_CONTROLA_VLR_MAX_DESCONTO).append("'");
        query.append(" AND (").append(Columns.PSE_VLR).append(" IS NULL").append(" OR ").append(Columns.PSE_VLR).append(" = '").append(CodedValues.NAO_CONTROLA_VLR_MAX_DESCONTO).append("')");
        query.append(")");
        executeUpdate(query.toString(), queryParams);
    }

    /**
     * Ajusta o ADE_VLR para os contratos que tem controle de teto pelo valor
     * original da parcela.
     * @param servicosControleSaldo List
     * @throws DataAccessException
     */
    @Override
    protected void ajustarAdeValorComTetoDeParcela(List<String> servicosControleSaldo) throws DataAccessException {
        // Para os serviços que possuem controle de teto pelo valor da
        // parcela, ajusta o ADE_VLR apenas se for maior que o saldo atual
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("UPDATE ").append(Columns.TB_AUTORIZACAO_DESCONTO);
        query.append(" SET ").append(Columns.ADE_VLR).append(" = COALESCE(");
        query.append(Columns.ADE_VLR_SDO_MOV).append(", 0.00)");
        query.append(" WHERE EXISTS (SELECT 1");
        query.append(" FROM ").append(Columns.TB_VERBA_CONVENIO);
        query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (").append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" ON (").append(Columns.CNV_SVC_CODIGO).append(" = ").append(Columns.PSE_SVC_CODIGO).append(")");
        query.append(" WHERE ").append(Columns.ADE_VCO_CODIGO).append(" = ").append(Columns.VCO_CODIGO);
        // Serviços que possuem controle de saldo devedor
        query.append(" AND ").append(Columns.CNV_SVC_CODIGO).append(" IN ('").append(TextHelper.join(servicosControleSaldo, "','")).append("')");
        // Contratos que não foram concluidos
        query.append(" AND ").append(Columns.ADE_SAD_CODIGO).append(" IN ('").append(TextHelper.join(SAD_CODIGOS, "','")).append("')");
        // Serviços que nao controlam vlr maximo de desconto
        query.append(" AND ").append(Columns.PSE_TPS_CODIGO).append(" = '").append(CodedValues.TPS_CONTROLA_VLR_MAX_DESCONTO).append("'");
        query.append(" AND ").append(Columns.PSE_VLR).append(" = '").append(CodedValues.CONTROLA_VLR_MAX_DESCONTO_PELA_PARCELA).append("'");
        query.append(")");
        // Apenas se o ADE_VLR for maior que o saldo
        query.append(" AND ").append(Columns.ADE_VLR).append(" > ").append(Columns.ADE_VLR_SDO_MOV);
        executeUpdate(query.toString(), queryParams);
    }

    /**
     * Ajusta o ADE_VLR para os contratos que possuem controle de teto pelo cargo
     * @param servicosControleSaldo List
     * @throws DataAccessException
     */
    @Override
    protected void ajustarAdeValorComTetoPeloCargo(List<String> servicosControleSaldo) throws DataAccessException {
        // Para os serviços que possuem controle de teto pelo cargo,
        // primeiramente seta o valor do saldo na ADE_VLR, para
        // depois fazer o limite do cargo
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("UPDATE ").append(Columns.TB_AUTORIZACAO_DESCONTO);
        query.append(" SET ").append(Columns.ADE_VLR).append(" = COALESCE(");
        query.append(Columns.ADE_VLR_SDO_MOV).append(", 0.00)");
        query.append(" WHERE EXISTS (SELECT 1");
        query.append(" FROM ").append(Columns.TB_VERBA_CONVENIO);
        query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (").append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" ON (").append(Columns.CNV_SVC_CODIGO).append(" = ").append(Columns.PSE_SVC_CODIGO).append(")");
        query.append(" WHERE ").append(Columns.ADE_VCO_CODIGO).append(" = ").append(Columns.VCO_CODIGO);
        // Serviços que possuem controle de saldo devedor
        query.append(" AND ").append(Columns.CNV_SVC_CODIGO).append(" IN ('").append(TextHelper.join(servicosControleSaldo, "','")).append("')");
        // Contratos que não foram concluidos
        query.append(" AND ").append(Columns.ADE_SAD_CODIGO).append(" IN ('").append(TextHelper.join(SAD_CODIGOS, "','")).append("')");
        // Serviços que controlam vlr maximo de desconto pelo cargo
        query.append(" AND ").append(Columns.PSE_TPS_CODIGO).append(" = '").append(CodedValues.TPS_CONTROLA_VLR_MAX_DESCONTO).append("'");
        query.append(" AND ").append(Columns.PSE_VLR).append(" = '").append(CodedValues.CONTROLA_VLR_MAX_DESCONTO_PELO_CARGO).append("'");
        query.append(")");
        executeUpdate(query.toString(), queryParams);

        // Para os serviços que possuem controle de teto pelo cargo,
        // fazer o mínimo entre os dois valores
        query.setLength(0);
        query.append("UPDATE ").append(Columns.TB_AUTORIZACAO_DESCONTO);
        // ADE_VLR = LEAST(ADE_VLR_SDO_MOV, VLR_MAX_DESC)
        query.append(" SET ").append(Columns.ADE_VLR).append(" = COALESCE(LEAST(");
        query.append(Columns.ADE_VLR_SDO_MOV).append(",");
        query.append("(SELECT ").append(Columns.CRS_VLR_DESC_MAX);
        query.append(" FROM ").append(Columns.TB_REGISTRO_SERVIDOR);
        query.append(" INNER JOIN ").append(Columns.TB_CARGO_REGISTRO_SERVIDOR).append(" ON (").append(Columns.RSE_CRS_CODIGO).append(" = ").append(Columns.CRS_CODIGO).append(")");
        query.append(" WHERE ").append(Columns.ADE_RSE_CODIGO).append(" = ").append(Columns.RSE_CODIGO);
        query.append(")), 0.00)");
        query.append(" WHERE EXISTS (SELECT 1");
        query.append(" FROM ").append(Columns.TB_VERBA_CONVENIO);
        query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (").append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" ON (").append(Columns.CNV_SVC_CODIGO).append(" = ").append(Columns.PSE_SVC_CODIGO).append(")");
        query.append(" WHERE ").append(Columns.ADE_VCO_CODIGO).append(" = ").append(Columns.VCO_CODIGO);
        // Serviços que possuem controle de saldo devedor
        query.append(" AND ").append(Columns.CNV_SVC_CODIGO).append(" IN ('").append(TextHelper.join(servicosControleSaldo, "','")).append("')");
        // Contratos que não foram concluidos
        query.append(" AND ").append(Columns.ADE_SAD_CODIGO).append(" IN ('").append(TextHelper.join(SAD_CODIGOS, "','")).append("')");
        // Serviços que controlam vlr maximo de desconto pelo cargo
        query.append(" AND ").append(Columns.PSE_TPS_CODIGO).append(" = '").append(CodedValues.TPS_CONTROLA_VLR_MAX_DESCONTO).append("'");
        query.append(" AND ").append(Columns.PSE_VLR).append(" = '").append(CodedValues.CONTROLA_VLR_MAX_DESCONTO_PELO_CARGO).append("'");
        query.append(")");
        executeUpdate(query.toString(), queryParams);
    }

    /**
     * Insere ocorrências de alteração do ADE_VLR
     * @throws DataAccessException
     */
    @Override
    protected void criarOcorrenciasAlteracaoAdeVlr(List<String> servicosControleSaldo) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        query.append("INSERT INTO tb_ocorrencia_autorizacao (ADE_CODIGO, OCA_CODIGO, TOC_CODIGO, USU_CODIGO, OCA_DATA, OCA_PERIODO, OCA_OBS) ");
        query.append(" SELECT ADE_CODIGO, ");
        query.append("('Q' || ");
        query.append("TO_CHAR(CURRENT_TIMESTAMP, 'yymmddhh24miss') || ");
        query.append("SUBSTR(LPAD(ADE_NUMERO, 12, '0'), 1, 12) || ");
        query.append("SUBSTR(LPAD(ROWNUM, 7, '0'), 1, 7)) AS OCA_CODIGO, ");
        query.append("'").append(CodedValues.TOC_ALTERACAO_CONTRATO).append("' AS TOC_CODIGO, ");
        query.append("'").append(CodedValues.USU_CODIGO_SISTEMA).append("' AS USU_CODIGO, ");
        query.append("CURRENT_TIMESTAMP AS OCA_DATA, PEX_PERIODO_POS AS OCA_PERIODO, ");
        query.append("'").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.ade.vlr", (AcessoSistema) null)).append("' AS OCA_OBS");
        query.append(" FROM ").append(Columns.TB_AUTORIZACAO_DESCONTO);
        query.append(" INNER JOIN ").append(Columns.TB_VERBA_CONVENIO).append(" ON (");
        query.append(Columns.ADE_VCO_CODIGO).append(" = ").append(Columns.VCO_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" ON (");
        query.append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(")");
        query.append(" INNER JOIN ").append(Columns.TB_PERIODO_EXPORTACAO).append(" ON (");
        query.append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.PEX_ORG_CODIGO).append(")");
        // Contratos que tiveram o ADE_VLR alterado (<> ADE_VLR_FOLHA)
        query.append(" WHERE ").append(Columns.ADE_VLR);
        query.append(" <> ").append(Columns.ADE_VLR_FOLHA);
        // Serviços que possuem controle de saldo devedor
        query.append(" AND ").append(Columns.CNV_SVC_CODIGO);
        query.append(" IN ('").append(TextHelper.join(servicosControleSaldo, "','")).append("')");
        // Contratos que não foram concluidos
        query.append(" AND ").append(Columns.ADE_SAD_CODIGO);
        query.append(" IN ('").append(TextHelper.join(SAD_CODIGOS, "','")).append("')");
        executeUpdate(query.toString(), queryParams);
    }

    /***************************************************************************/
    /**
     * Ajusta o PRD_VLR_PREVISTO das parcelas abertas de acordo com o novo valor
     * da ADE, corrigido e ajustado pelo saldo devedor. É necessário caso seja
     * necessário reexportar o movimento financeiro.
     * @throws DAOException
     */
    @Override
    public void ajustarValorParcelasAbertas() throws DAOException {
        try {
            List<String> servicosControleSaldo = pesquisarServicosControleSaldoDevedor();
            if (servicosControleSaldo != null && servicosControleSaldo.size() > 0) {
                // Seta no ADE_VLR o valor do saldo devedor já ajustado
                final MapSqlParameterSource queryParams = new MapSqlParameterSource();
                final StringBuilder query = new StringBuilder();
                query.append("UPDATE ").append(Columns.TB_PARCELA_DESCONTO_PERIODO);
                query.append(" SET ").append(Columns.PDP_VLR_PREVISTO).append(" = ");
                query.append("(SELECT ").append(Columns.ADE_VLR);
                query.append(" FROM ").append(Columns.TB_AUTORIZACAO_DESCONTO);
                query.append(" WHERE ").append(Columns.PDP_ADE_CODIGO).append(" = ").append(Columns.ADE_CODIGO).append(")");
                query.append(" WHERE EXISTS (SELECT 1");
                query.append(" FROM ").append(Columns.TB_AUTORIZACAO_DESCONTO);
                query.append(" INNER JOIN ").append(Columns.TB_VERBA_CONVENIO);
                query.append(" ON (").append(Columns.ADE_VCO_CODIGO).append(" = ").append(Columns.VCO_CODIGO).append(")");
                query.append(" INNER JOIN ").append(Columns.TB_CONVENIO);
                query.append(" ON (").append(Columns.VCO_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(")");
                query.append(" INNER JOIN ").append(Columns.TB_PERIODO_EXPORTACAO);
                query.append(" ON (").append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.PEX_ORG_CODIGO).append(")");
                query.append(" WHERE ").append(Columns.PDP_ADE_CODIGO).append(" = ").append(Columns.ADE_CODIGO);
                // Serviços que possuem controle de saldo devedor
                query.append(" AND ").append(Columns.CNV_SVC_CODIGO);
                query.append(" IN ('").append(TextHelper.join(servicosControleSaldo, "','")).append("')");
                // Contratos que não foram concluidos
                query.append(" AND ").append(Columns.ADE_SAD_CODIGO);
                query.append(" IN ('").append(TextHelper.join(SAD_CODIGOS, "','")).append("')");
                // Parcelas abertas
                query.append(" AND ").append(Columns.PDP_SPD_CODIGO);
                query.append(" IN ('").append(CodedValues.SPD_EMABERTO).append("','");
                query.append(CodedValues.SPD_EMPROCESSAMENTO).append("')");
                query.append(" AND ").append(Columns.PDP_DATA_DESCONTO);
                query.append(" = ").append(Columns.PEX_PERIODO);
                query.append(")");
                executeUpdate(query.toString(), queryParams);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
