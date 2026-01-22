package com.zetra.econsig.persistence.dao.generic;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.ParcelaDescontoDAO;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: GenericParcelaDescontoDAO</p>
 * <p>Description: Implementacao Genérica do DAO de parcelas. Instruções
 * SQLs contidas aqui devem funcionar em todos os SGDBs suportados pelo
 * sistema.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericParcelaDescontoDAO implements ParcelaDescontoDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericParcelaDescontoDAO.class);
    protected boolean retAtrasadoSomaAparcela = false;

    /**
     * opção que define se no retorno atrasado o valor realizado deste arquivo deve ser somado ao realizado da tabela de histórico de parcela.
     */
    @Override
    public void retornoAtrasadoSomandoAParcela(boolean somaParcela) throws DAOException {
        retAtrasadoSomaAparcela = somaParcela;
    }

    /**
     * Remove as parcelas da tabela do período que estão aguardando processamento (casos de antecipação
     * de período, com dois abertos), que possuem a mesma parcela já paga na tabela histórica, provavelmente
     * por processamento de férias do período.
     * @param orgCodigos
     * @param estCodigos
     */
    @Override
    public void removerParcelasPosPeriodoPagaEmFerias(List<String> orgCodigos, List<String> estCodigos) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder query = new StringBuilder();
        query.append("DELETE FROM tb_parcela_desconto_periodo ");
        query.append("WHERE tb_parcela_desconto_periodo.spd_codigo = '").append(CodedValues.SPD_AGUARD_PROCESSAMENTO).append("' ");
        query.append("AND EXISTS (SELECT 1 FROM tb_parcela_desconto prd ");
        query.append("WHERE prd.ade_codigo = tb_parcela_desconto_periodo.ade_codigo ");
        query.append("AND prd.prd_data_desconto = tb_parcela_desconto_periodo.prd_data_desconto");
        query.append(") ");

        if ((estCodigos != null && estCodigos.size() > 0) || (orgCodigos != null && orgCodigos.size() > 0)) {
            query.append("AND EXISTS (SELECT 1 FROM tb_aut_desconto ade ");
            query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");

            if (estCodigos != null && estCodigos.size() > 0) {
                query.append("INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo) ");
            }

            query.append("WHERE tb_parcela_desconto_periodo.ade_codigo = ade.ade_codigo ");
            if (estCodigos != null && estCodigos.size() > 0) {
                query.append("AND org.est_codigo IN (:estCodigos) ");
                queryParams.addValue("estCodigos", estCodigos);
            } else {
                query.append("AND cnv.org_codigo IN (:orgCodigos) ");
                queryParams.addValue("orgCodigos", orgCodigos);
            }
            query.append(")");
        }

        LOG.trace(query.toString());
        int rows = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + rows);
    }

    /**
     * Insere as ocorrências para as parcelas que estão sendo integradas,
     * parcelas com status = 'Em Processamento' ou 'Sem Retorno'. É utilizado
     * pela conclusão de retorno, portanto irá utilizar sempre a tabela de parcelas do período.
     * @param tipoEntidade   : CSE, ORG ou EST
     * @param codigoEntidade : código da entidade que está realizando a operação, ORG_CODIGO, EST_CODIGO ou CSE_CODIGO
     * @param responsavel    : responsável pela operação
     * @throws DAOException
     */
    @Override
    public void criaOcorrenciaSemRetorno(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final String queryInsert = "INSERT INTO tb_ocorrencia_parcela_periodo (OCP_CODIGO, PRD_CODIGO, TOC_CODIGO, USU_CODIGO, OCP_OBS, OCP_DATA) VALUES (:ocpCodigo, :prdCodigo, :tocCodigo, :usuCodigo, :ocpObs, :ocpData)";

        final StringBuilder query = new StringBuilder();
        query.append("SELECT prd.prd_codigo, mne.mne_descricao ");
        query.append("FROM tb_parcela_desconto_periodo prd ");
        query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = prd.ade_codigo) ");

        if (tipoEntidade.equalsIgnoreCase("ORG") || tipoEntidade.equalsIgnoreCase("EST")) {
            query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");

            if (tipoEntidade.equalsIgnoreCase("EST")) {
                query.append("INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo) ");
            }
        }

        query.append("LEFT OUTER JOIN tb_tipo_motivo_nao_exportacao mne ON (coalesce(prd.mne_codigo, ade.mne_codigo) = mne.mne_codigo) ");
        query.append("WHERE prd.spd_codigo IN ('").append(CodedValues.SPD_EMPROCESSAMENTO).append("','").append(CodedValues.SPD_SEM_RETORNO).append("') ");

        if (tipoEntidade.equalsIgnoreCase("EST")) {
            query.append(" AND org.est_codigo = :codigoEntidade ");
            queryParams.addValue("codigoEntidade", codigoEntidade);
        } else if (tipoEntidade.equalsIgnoreCase("ORG")) {
            query.append(" AND cnv.org_codigo = :codigoEntidade ");
            queryParams.addValue("codigoEntidade", codigoEntidade);
        } else if (tipoEntidade.equalsIgnoreCase("RSE")) {
            query.append(" AND ade.rse_codigo = :codigoEntidade ");
            queryParams.addValue("codigoEntidade", codigoEntidade);
        }

        final Timestamp ocpDataPadrao = new Timestamp(Calendar.getInstance().getTimeInMillis());
        final String ocpObsPadrao = ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.sem.retorno", responsavel);
        final String usuCodigo = (responsavel != null && responsavel.getUsuCodigo() != null ? responsavel.getUsuCodigo() : CodedValues.USU_CODIGO_SISTEMA);

        LOG.trace(query.toString());
        try (Stream<Map<String, Object>> stream = jdbc.queryForStream(query.toString(), queryParams,
                (rs, rowNum) -> {
                    final Map<String, Object> row = new LinkedHashMap<>();
                    row.put("prdCodigo", rs.getInt("prd_codigo"));
                    row.put("ocpObs", rs.getString("mne_descricao"));
                    return row;
                })) {
            stream.forEach(row -> {
                try {
                    String tocCodigo = CodedValues.TOC_RETORNO_PARCELA_SEM_RETORNO;
                    Integer prdCodigo = (Integer) row.get("prdCodigo");
                    String ocpObs = (String) row.get("ocpObs");
                    if (TextHelper.isNull(ocpObs)) {
                        ocpObs = ocpObsPadrao;
                    } else {
                        tocCodigo = CodedValues.TOC_RETORNO_PARCELA_NAO_EXPORTADA;
                        ocpObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.retorno.arg0", responsavel, ocpObs);
                    }

                    MapSqlParameterSource queryInsertParams = new MapSqlParameterSource();
                    queryInsertParams.addValue("ocpCodigo", DBHelper.getNextId());
                    queryInsertParams.addValue("prdCodigo", prdCodigo);
                    queryInsertParams.addValue("tocCodigo", tocCodigo);
                    queryInsertParams.addValue("usuCodigo", usuCodigo);
                    queryInsertParams.addValue("ocpObs", ocpObs);
                    queryInsertParams.addValue("ocpData", ocpDataPadrao);
                    jdbc.update(queryInsert, queryInsertParams);
                } catch (com.zetra.econsig.exception.MissingPrimaryKeyException ex) {
                    throw new RuntimeException(ex);
                }
            });
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
