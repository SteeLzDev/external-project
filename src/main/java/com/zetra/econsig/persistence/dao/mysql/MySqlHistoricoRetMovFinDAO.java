package com.zetra.econsig.persistence.dao.mysql;

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
 * <p>Title: MySqlHistoricoRetMovFinDAO</p>
 * <p>Description: Implementação para MySql do DAO de Historico de Retorno</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlHistoricoRetMovFinDAO implements HistoricoRetMovFinDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlHistoricoRetMovFinDAO.class);

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
        query.append("date_add(NOW(), interval ").append(Columns.PEX_SEQUENCIA).append(" second), ");
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

        if (estCodigos != null && estCodigos.size() > 0) {
            query.append(" INNER JOIN ").append(Columns.TB_ORGAO).append(" ON (");
            query.append(Columns.HCR_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO).append(")");
        }

        query.append(" SET ").append(Columns.HCR_DATA_FIM).append(" = NOW() ");
        query.append(" WHERE ").append(Columns.HCR_DATA_FIM).append(" IS NULL");

        if (estCodigos != null && estCodigos.size() > 0) {
            query.append(" AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:estCodigos)");
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

        if (estCodigos != null && estCodigos.size() > 0) {
            query.append(" INNER JOIN ").append(Columns.TB_ORGAO).append(" ON (");
            query.append(Columns.HCR_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO).append(")");
        }

        query.append(" SET ");
        query.append(Columns.HCR_DESFEITO).append(" = 'S', ");
        query.append(Columns.HCR_DATA_FIM).append(" = COALESCE(").append(Columns.HCR_DATA_FIM).append(", NOW()) ");
        query.append(" WHERE ").append(Columns.HCR_PERIODO).append(" = :periodo");

        if (estCodigos != null && estCodigos.size() > 0) {
            query.append(" AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:estCodigos)");
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
            query.append(" UPDATE ").append(Columns.TB_PARCELA_DESCONTO).append(" AS prd ");
            query.append(" INNER JOIN ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" AS ade ON (prd.ade_codigo = ade.ade_codigo) ");
            query.append(" INNER JOIN ").append(Columns.TB_VERBA_CONVENIO).append(" AS vco ON (ade.vco_codigo = vco.vco_codigo)");
            query.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" AS cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append(" INNER JOIN ").append(Columns.TB_SERVICO).append(" AS svc ON (cnv.svc_codigo = svc.svc_codigo) ");
            query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" AS pse1 ON (pse1.svc_codigo = svc.svc_codigo AND ");
            query.append(" pse1.tps_codigo = '").append(CodedValues.TPS_RETEM_MARGEM_SVC_PERCENTUAL).append("' AND ");
            query.append(" pse1.pse_vlr = '1') ");
            query.append(" INNER JOIN ").append(Columns.TB_PARAM_SVC_CONSIGNANTE).append(" AS pse2 ON (pse2.svc_codigo = svc.svc_codigo AND ");
            query.append(" pse2.tps_codigo = '").append(CodedValues.TPS_TIPO_VLR).append("' AND ");
            query.append(" pse2.pse_vlr = '").append(CodedValues.TIPO_VLR_PERCENTUAL).append("') ");
            query.append(" SET prd.prd_vlr_previsto = ade.ade_vlr, ");
            query.append(" prd.prd_vlr_realizado = (CASE WHEN prd.spd_codigo = '").append(CodedValues.SPD_REJEITADAFOLHA).append("' THEN prd.prd_vlr_realizado ELSE ade.ade_vlr END) ");
            query.append(" WHERE ade.ade_codigo IN (:adeCodigos)");
            queryParams.addValue("adeCodigos", adeCodigoLista);

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
        }
    }
}
