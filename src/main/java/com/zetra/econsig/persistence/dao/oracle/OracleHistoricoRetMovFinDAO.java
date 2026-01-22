package com.zetra.econsig.persistence.dao.oracle;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.HistoricoRetMovFinDAO;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: OracleHistoricoRetMovFinDAO</p>
 * <p>Description: Implementação para Oracle do DAO de Historico de Retorno</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OracleHistoricoRetMovFinDAO implements HistoricoRetMovFinDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OracleHistoricoRetMovFinDAO.class);

    /**
     * Inicia a gravação de histórico de conclusão de retorno. O método deve ser chamado
     * quando dá inicio o processo de retorno, com a importação do primeiro arquivo
     * de retorno. O método insere registros de histórico com data_fim igual a null
     * indicando que o processo de retorno não foi concluído.
     * @param orgCodigos: Lista com os códigos dos órgãos, se nulo considera todos os órgãos.
     * @param estCodigos: Lista com os códigos dos estabelecimentos, se nulo considera todos os estabelecimentos.
     * @param periodo: Período de processamento
     * @param chaveHistMargem : Chave do histórico de margem
     * @throws DAOException
     */
    @Override
    public void iniciarHistoricoConclusaoRetorno(List<String> orgCodigos, List<String> estCodigos, String periodo, String chaveHistMargem) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        String chaveHistMargemSQL = !TextHelper.isNull(chaveHistMargem) ? String.format("'%s'", chaveHistMargem) : "NULL";
        StringBuilder query = new StringBuilder();

        query.append("INSERT INTO ").append(Columns.TB_HISTORICO_CONCLUSAO_RETORNO);
        query.append(" (ORG_CODIGO, HCR_DATA_INICIO, HCR_PERIODO, HCR_CHAVE_HIST_MARGEM) ");
        query.append(" SELECT ").append(Columns.ORG_CODIGO).append(", ");
        /**
         * DESENV-6816: HCR_DATA_INICIO é chave da tabela e para inclusão de dois períodos com a mesma hora de início gera erro de chave.
         * Workaround para gerar chave distinta adicionando segundos na chave para não gerar erro de chave, possibilitando ser liberada em subversão.
         * Futuramente será criada chave primária auto-incremento, podendo ser removida adição de segundos na data de início.
         */
        query.append("(CURRENT_TIMESTAMP + ").append(Columns.PEX_SEQUENCIA).append(" / (24 * 60 * 60)), ");
        query.append(Columns.PEX_PERIODO).append(", ").append(chaveHistMargemSQL);
        query.append(" FROM ").append(Columns.TB_ORGAO);
        query.append(" INNER JOIN ").append(Columns.TB_PERIODO_EXPORTACAO).append(" ON (");
        query.append(Columns.PEX_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO).append(") ");
        query.append(" LEFT OUTER JOIN ").append(Columns.TB_HISTORICO_CONCLUSAO_RETORNO).append(" ON (");
        query.append(Columns.HCR_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO).append(" AND ");
        query.append(Columns.HCR_DATA_FIM).append(" IS NULL)");
        query.append(" WHERE ").append(Columns.HCR_ORG_CODIGO).append(" IS NULL");

        if (estCodigos != null && estCodigos.size() > 0) {
            query.append(" AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:estCodigos)");
            queryParams.addValue("estCodigos", estCodigos);
        }

        if (orgCodigos != null && orgCodigos.size() > 0) {
            query.append(" AND ").append(Columns.ORG_CODIGO).append(" IN (:orgCodigos)");
            queryParams.addValue("orgCodigos", orgCodigos);
        }

        LOG.trace(query.toString());
        jdbc.update(query.toString(), queryParams);
    }

    /**
     * Finaliza a gravação de histórico de conclusão de retorno, mudando a data_fim
     * para a data atual. O método deve ser executado na conclusão do retorno, quando
     * todos os arquivos já tiverem sido importados.
     * @param orgCodigos: Lista com os códigos dos órgãos, se nulo considera todos os órgãos.
     * @param estCodigos: Lista com os códigos dos estabelecimentos, se nulo considera todos os estabelecimentos.
     * @throws DAOException
     */
    @Override
    public void finalizarHistoricoConclusaoRetorno(List<String> orgCodigos, List<String> estCodigos) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder query = new StringBuilder();

        query.append("UPDATE ").append(Columns.TB_HISTORICO_CONCLUSAO_RETORNO);
        query.append(" SET ").append(Columns.HCR_DATA_FIM).append(" = CURRENT_TIMESTAMP ");
        query.append(" WHERE ").append(Columns.HCR_DATA_FIM).append(" IS NULL");

        if (estCodigos != null && estCodigos.size() > 0) {
            query.append(" AND ").append(Columns.HCR_ORG_CODIGO).append(" IN (");
            query.append(" SELECT  ").append(Columns.ORG_CODIGO).append(" FROM ").append(Columns.TB_ORGAO);
            query.append(" WHERE ").append(Columns.ORG_EST_CODIGO).append(" IN (:estCodigos))");
            queryParams.addValue("estCodigos", estCodigos);
        }

        if (orgCodigos != null && orgCodigos.size() > 0) {
            query.append(" AND ").append(Columns.HCR_ORG_CODIGO).append(" IN (:orgCodigos)");
            queryParams.addValue("orgCodigos", orgCodigos);
        }

        LOG.trace(query.toString());
        jdbc.update(query.toString(), queryParams);
    }

    /**
     * Finaliza a gravação de histórico de conclusão de retorno, mudando a data_fim
     * para a data atual. O método deve ser executado na conclusão do retorno, quando
     * todos os arquivos já tiverem sido importados.
     * @param orgCodigos: Lista com os códigos dos órgãos, se nulo considera todos os órgãos.
     * @param estCodigos: Lista com os códigos dos estabelecimentos, se nulo considera todos os estabelecimentos.
     * @param periodo: Período de processamento
     * @throws DAOException
     */
    @Override
    public void desfazerHistoricoConclusaoRetorno(List<String> orgCodigos, List<String> estCodigos, String periodo) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("periodo", periodo);

        final StringBuilder query = new StringBuilder();

        query.append("UPDATE ").append(Columns.TB_HISTORICO_CONCLUSAO_RETORNO);
        query.append(" SET ");
        query.append(Columns.HCR_DESFEITO).append(" = 'S', ");
        query.append(Columns.HCR_DATA_FIM).append(" = COALESCE(").append(Columns.HCR_DATA_FIM).append(", CURRENT_TIMESTAMP) ");
        query.append(" WHERE ").append(Columns.HCR_PERIODO).append(" = :periodo");

        if (estCodigos != null && estCodigos.size() > 0) {
            query.append(" AND ").append(Columns.HCR_ORG_CODIGO).append(" IN (");
            query.append(" SELECT  ").append(Columns.ORG_CODIGO).append(" FROM ").append(Columns.TB_ORGAO);
            query.append(" WHERE ").append(Columns.ORG_EST_CODIGO).append(" IN (:estCodigos))");
            queryParams.addValue("estCodigos", estCodigos);
        }

        if (orgCodigos != null && orgCodigos.size() > 0) {
            query.append(" AND ").append(Columns.HCR_ORG_CODIGO).append(" IN (:orgCodigos)");
            queryParams.addValue("orgCodigos", orgCodigos);
        }

        LOG.trace(query.toString());
        jdbc.update(query.toString(), queryParams);
    }

    /**
     * Atualiza o valor de parcelas de contratos de serviços percentuais na importação de histórico
     * Os valores das parcelas são criados com as informações do arquivo. Na reserva de margem o valor percentual é calculado.
     * Este método é necessário para copiar os valores calculados na reserva para as parcelas dos contratos de serviços percentuais.
     * @param adeCodigoLista: Lista com as ADEs criadas no processo de importação.
     * @throws DAOException
     */
    @Override
    public void atualizaParcelaImportacaoHistorico(List<String> adeCodigoLista) throws DAOException {
        if (adeCodigoLista.size() > 0) {
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            final StringBuilder query = new StringBuilder();
            query.append("UPDATE tb_parcela_desconto prd ");
            query.append("SET (prd.prd_vlr_previsto, prd.prd_vlr_realizado) = (");
            query.append(" SELECT ade.ade_vlr, ");
            query.append(" CASE WHEN prd.spd_codigo = '").append(CodedValues.SPD_REJEITADAFOLHA).append("' THEN prd.prd_vlr_realizado ELSE ade.ade_vlr END ");
            query.append(" FROM tb_aut_desconto ade ");
            query.append(" WHERE prd.ade_codigo = ade.ade_codigo ");
            query.append(") ");
            query.append("WHERE EXISTS (");
            query.append(" SELECT 1 FROM tb_aut_desconto ade ");
            query.append(" INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo)");
            query.append(" INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append(" INNER JOIN tb_servico svc ON (cnv.svc_codigo = svc.svc_codigo) ");
            query.append(" INNER JOIN tb_param_svc_consignante pse1 ON (pse1.svc_codigo = svc.svc_codigo AND pse1.tps_codigo = '").append(CodedValues.TPS_RETEM_MARGEM_SVC_PERCENTUAL).append("' AND pse1.pse_vlr = '1') ");
            query.append(" INNER JOIN tb_param_svc_consignante pse2 ON (pse2.svc_codigo = svc.svc_codigo AND pse2.tps_codigo = '").append(CodedValues.TPS_TIPO_VLR).append("' AND pse2.pse_vlr = '").append(CodedValues.TIPO_VLR_PERCENTUAL).append("') ");
            query.append(" WHERE prd.ade_codigo = ade.ade_codigo ");
            query.append(") ");
            query.append("AND prd.ade_codigo IN (:adeCodigos)");
            queryParams.addValue("adeCodigos", adeCodigoLista);

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
        }
    }
}
