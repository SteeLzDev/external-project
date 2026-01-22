package com.zetra.econsig.persistence.dao.generic;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: MySqlControleSaldoDevedorDAO</p>
 * <p>Description: DAO de MySql para rotinas auxiliares de controle de saldo
 * devedor efetuadas tanto na exportação de movimento financeiro, quanto
 * no retorno. A classe é estendida por MySqlControleSaldoDvExpMovimentoDAO e
 * MySqlControleSaldoDvImpRetornoDAO.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * @author Igor Lucas
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericControleSaldoDevedorDAO {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericControleSaldoDevedorDAO.class);

    /* Status de contratos que não foram concluidos */
    protected static final String[] SAD_CODIGOS = {
            CodedValues.SAD_DEFERIDA, CodedValues.SAD_EMANDAMENTO};
    /* Status de parcelas que foram enviadas para a folha de pagamento */
    protected static final String[] SPD_CODIGOS = {
            CodedValues.SPD_LIQUIDADAFOLHA, CodedValues.SPD_LIQUIDADAMANUAL,
            CodedValues.SPD_REJEITADAFOLHA, CodedValues.SPD_SEM_RETORNO};
    /* Status de parcelas que não foram descontadas na folha de pagamento */
    protected static final String[] SPD_CODIGOS_NAO_DESCONTADOS = {
            CodedValues.SPD_REJEITADAFOLHA, CodedValues.SPD_SEM_RETORNO};

    /**
     * Retorna uma lista de serviços que possui controle de saldo devedor.
     * @return List
     * @throws DAOException
     */
    protected List<String> pesquisarServicosControleSaldoDevedor() throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final StringBuilder query = new StringBuilder();
        query.append("SELECT ").append(Columns.SVC_CODIGO).append(" FROM ").append(Columns.TB_SERVICO);
        query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" ON (");
        query.append(Columns.SVC_CODIGO).append(" = ").append(Columns.PSE_SVC_CODIGO).append(")");
        query.append(" WHERE ").append(Columns.PSE_TPS_CODIGO).append(" = '");
        query.append(CodedValues.TPS_CONTROLA_SALDO).append("'");
        query.append(" AND ").append(Columns.PSE_VLR).append(" = '");
        query.append(CodedValues.POSSUI_CONTROLE_SALDO_DEVEDOR).append("'");
        query.append(" AND ").append(Columns.PSE_CSE_CODIGO).append(" = '");
        query.append(CodedValues.CSE_CODIGO_SISTEMA).append("'");
        // Retorna a lista de SVC_CODIGO
        return jdbc.queryForList(query.toString(), queryParams, String.class);
    }

    /**
     * Retorna uma lista de serviços que possui correção de saldo devedor.
     * O parâmetro tipoCorrecao pode ser: CodedValues.NAO_POSSUI_CORRECAO_SALDO_DEVEDOR,
     * CodedValues.CORRECAO_SALDO_DEVEDOR_PROPRIO_SERVICO,
     * CodedValues.CORRECAO_SALDO_DEVEDOR_EM_OUTRO_SERVICO,
     * ou NULL para listas todos os serviços que possuem correção de saldo devedor,
     * ou seja diferente de CodedValues.NAO_POSSUI_CORRECAO_SALDO_DEVEDOR
     * @param tipoCorrecao String
     * @return List
     * @throws DAOException
     */
    protected List<String> pesquisarServicosCorrecaoSaldoDevedor(String tipoCorrecao) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final StringBuilder query = new StringBuilder();
        query.append("SELECT ").append(Columns.SVC_CODIGO).append(" FROM ").append(Columns.TB_SERVICO);
        query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" ON (");
        query.append(Columns.SVC_CODIGO).append(" = ").append(Columns.PSE_SVC_CODIGO).append(")");
        query.append(" WHERE ").append(Columns.PSE_CSE_CODIGO).append(" = '");
        query.append(CodedValues.CSE_CODIGO_SISTEMA).append("'");
        query.append(" AND ").append(Columns.PSE_TPS_CODIGO).append(" = '");
        query.append(CodedValues.TPS_POSSUI_CORRECAO_SALDO_DEVEDOR).append("'");
        if (tipoCorrecao == null) {
            query.append(" AND ").append(Columns.PSE_VLR).append(" IS NOT NULL ");
            query.append(" AND ").append(Columns.PSE_VLR).append(" <> '");
            query.append(CodedValues.NAO_POSSUI_CORRECAO_SALDO_DEVEDOR).append("'");
        } else if (tipoCorrecao.equals(CodedValues.NAO_POSSUI_CORRECAO_SALDO_DEVEDOR)) {
            query.append(" AND (").append(Columns.PSE_VLR).append(" IS NULL ");
            query.append(" OR ").append(Columns.PSE_VLR).append(" = '");
            query.append(CodedValues.NAO_POSSUI_CORRECAO_SALDO_DEVEDOR).append("')");
        } else {
            query.append(" AND ").append(Columns.PSE_VLR).append(" = '");
            query.append(tipoCorrecao).append("'");
        }
        // Retorna a lista de SVC_CODIGO
        return jdbc.queryForList(query.toString(), queryParams, String.class);
    }

    /**
     * Executa a query no banco de dados
     * @param query String
     * @throws DataAccessException
     */
    protected void executeUpdate(String query, MapSqlParameterSource queryParams) throws DataAccessException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        LOG.trace(query);
        int linhasAfetadas = jdbc.update(query, queryParams);
        LOG.trace("Linhas Alteradas: " + linhasAfetadas);
    }
}
