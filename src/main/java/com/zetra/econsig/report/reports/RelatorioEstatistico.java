package com.zetra.econsig.report.reports;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.RelatorioDAO;
import com.zetra.econsig.values.Columns;

/**
 * <p> Title: RelatorioEstatistico</p>
 * <p> Description: Relatório Estatístico</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioEstatistico extends ReportTemplate {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RelatorioEstatistico.class);

    @Override
    public String getSql(CustomTransferObject criterio) throws DAOException {
        try {
            final RelatorioDAO relatorioDAO = DAOFactory.getDAOFactory().getRelatorioDAO();
            return relatorioDAO.montaQueryRelatorioEstatistico(criterio, getParameters());
        } catch (final Exception e) {
            LOG.debug("Não foi possível gerar o Relatório de Estatistico.", e);
            if (e instanceof DAOException) {
                throw (DAOException)e;
            } else  {
                throw new DAOException("mensagem.erro.gerar.relatorio", getResponsavel() , e);
            }
        }
    }

    @Override
    public void preSqlProcess(Connection conn) {
        final String tableName = (String) parameters.get("TABLE_NAME");

        final StringBuilder query = new StringBuilder();
        Statement stat = null;
        String referencia;
        try {
            stat = conn.createStatement();

            final CustomTransferObject criterio = (CustomTransferObject) parameters.get("CRITERIO");
            final StringBuilder whereClause = new StringBuilder();
            whereClause.append(" WHERE 1=1 ");

            if (criterio != null) {
                if (!TextHelper.isNull(criterio.getAttribute(Columns.CNV_CSA_CODIGO))) {
                    whereClause.append(" AND cnv.csa_codigo = '").append(TextHelper.escapeSql(criterio.getAttribute(Columns.CNV_CSA_CODIGO))).append("' ");
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.CNV_ORG_CODIGO))) {
                    whereClause.append(" AND cnv.org_codigo in ('").append(TextHelper.joinWithEscapeSql((List<String>) criterio.getAttribute(Columns.CNV_ORG_CODIGO), "','")).append("') ");
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.CNV_SVC_CODIGO))) {
                    whereClause.append(" AND cnv.svc_codigo in ('").append(TextHelper.joinWithEscapeSql((List<String>) criterio.getAttribute(Columns.CNV_SVC_CODIGO), "','")).append("') ");
                }
                if (!TextHelper.isNull(criterio.getAttribute(Columns.RSE_SRS_CODIGO))) {
                    whereClause.append(" AND rse.srs_codigo in ('").append(TextHelper.joinWithEscapeSql((List<String>) criterio.getAttribute(Columns.RSE_SRS_CODIGO), "','")).append("') ");
                }
            }

            for (final String element : ((List<String>) parameters.get("REFERENCIAS"))) {
                referencia = element;

                // -- Quantidade de Consignações (QTD) [1]
                query.append("INSERT INTO tb_relatorio_estatistico (REE_NOME, REE_REFERENCIA, REE_ORDEM, REE_VERBA, REE_CSA, REE_VALOR, REE_QTD) ");
                query.append("SELECT '").append(tableName).append("', '").append(referencia).append("', 1, cnv_cod_verba, csa_identificador, count(ade.ade_codigo), count(ade.ade_codigo) ");
                query.append("FROM tb_aut_desconto ade ");
                query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
                query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
                query.append("INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo) ");
                query.append("INNER JOIN tb_historico_exportacao hie ON (cnv.org_codigo = hie.org_codigo) ");
                query.append("INNER JOIN tb_registro_servidor rse ON (ade.rse_codigo = rse.rse_codigo) ");
                query.append(whereClause.toString());
                query.append(" AND hie.hie_periodo =  '").append(referencia).append("' ");
                query.append(" AND NOT EXISTS (SELECT 1 FROM tb_historico_exportacao max_hie ");
                query.append("                 WHERE max_hie.org_codigo = cnv.org_codigo ");
                query.append("                   AND max_hie.hie_periodo = hie.hie_periodo ");
                query.append("                   AND max_hie.hie_data > hie.hie_data ");
                query.append(") ");
                query.append("AND sad_codigo NOT IN ('0', '1', '2', '3', '7') ");
                query.append("AND ade.ade_ano_mes_ini <= hie.hie_periodo ");
                query.append("AND NOT EXISTS (SELECT 1 FROM tb_ocorrencia_autorizacao oca ");
                query.append("                 WHERE oca.ade_codigo = ade.ade_codigo ");
                query.append("                   AND oca.toc_codigo in ('6', '7', '15', '19') ");
                query.append("                   AND oca.oca_periodo <= hie.hie_periodo ");
                query.append(") ");
                query.append("GROUP BY csa_identificador, cnv_cod_verba");
                LOG.debug(query.toString());
                stat.executeUpdate(query.toString());
                query.setLength(0);

                // -- Movimento do Mês (R$) [3]
                query.append("INSERT INTO tb_relatorio_estatistico (REE_NOME, REE_REFERENCIA, REE_ORDEM, REE_VERBA, REE_CSA, REE_VALOR, REE_QTD) ");
                query.append("SELECT '").append(tableName).append("', '").append(referencia).append("', 3, cnv_cod_verba, csa_identificador, sum(prd_vlr_previsto), count(ade.ade_codigo) ");
                query.append("FROM tb_aut_desconto ade ");
                query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
                query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
                query.append("INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo) ");
                query.append("INNER JOIN tb_historico_exportacao hie ON (cnv.org_codigo = hie.org_codigo) ");
                query.append("INNER JOIN tb_registro_servidor rse ON (ade.rse_codigo = rse.rse_codigo) ");
                query.append("INNER JOIN tb_parcela_desconto prd ON (ade.ade_codigo = prd.ade_codigo) ");
                query.append(whereClause.toString());
                query.append(" AND hie.hie_periodo =  '").append(referencia).append("' ");
                query.append(" AND NOT EXISTS (SELECT 1 FROM tb_historico_exportacao max_hie ");
                query.append("                 WHERE max_hie.org_codigo = cnv.org_codigo ");
                query.append("                   AND max_hie.hie_periodo = hie.hie_periodo ");
                query.append("                   AND max_hie.hie_data > hie.hie_data ");
                query.append(") ");
                query.append("AND prd.prd_data_desconto = hie.hie_periodo ");
                query.append("AND ade.sad_codigo NOT IN ('0', '1', '2', '3', '7') ");
                query.append("AND ade.ade_ano_mes_ini = hie.hie_periodo ");
                query.append("GROUP BY csa_identificador, cnv_cod_verba");
                LOG.debug(query.toString());
                stat.executeUpdate(query.toString());
                query.setLength(0);

                // -- Desconto do Mês (R$) [4]
                query.append("INSERT INTO tb_relatorio_estatistico (REE_NOME, REE_REFERENCIA, REE_ORDEM, REE_VERBA, REE_CSA, REE_VALOR, REE_QTD) ");
                query.append("SELECT '").append(tableName).append("', '").append(referencia).append("', 4, cnv_cod_verba, csa_identificador, sum(prd_vlr_realizado), count(ade.ade_codigo) ");
                query.append("FROM tb_aut_desconto ade ");
                query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
                query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
                query.append("INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo) ");
                query.append("INNER JOIN tb_historico_exportacao hie ON (cnv.org_codigo = hie.org_codigo) ");
                query.append("INNER JOIN tb_registro_servidor rse ON (ade.rse_codigo = rse.rse_codigo) ");
                query.append("INNER JOIN tb_parcela_desconto prd ON (ade.ade_codigo = prd.ade_codigo) ");
                query.append(whereClause.toString());
                query.append(" AND hie.hie_periodo =  '").append(referencia).append("' ");
                query.append(" AND NOT EXISTS (SELECT 1 FROM tb_historico_exportacao max_hie ");
                query.append("                 WHERE max_hie.org_codigo = cnv.org_codigo ");
                query.append("                   AND max_hie.hie_periodo = hie.hie_periodo ");
                query.append("                   AND max_hie.hie_data > hie.hie_data ");
                query.append(") ");
                query.append("AND prd.prd_data_desconto = hie.hie_periodo ");
                query.append("AND prd.spd_codigo = '6' ");
                query.append("AND ade.sad_codigo NOT IN ('0', '1', '2', '3', '7') ");
                query.append("AND ade.ade_ano_mes_ini <= hie.hie_periodo ");
                query.append("AND NOT EXISTS (SELECT 1 FROM tb_ocorrencia_autorizacao oca ");
                query.append("                 WHERE oca.ade_codigo = ade.ade_codigo ");
                query.append("                   AND toc_codigo in ('6', '7', '15', '19') ");
                query.append("                   AND oca.oca_periodo <= hie.hie_periodo ");
                query.append(") ");
                query.append("GROUP BY csa_identificador, cnv_cod_verba");
                LOG.debug(query.toString());
                stat.executeUpdate(query.toString());
                query.setLength(0);

                // -- Consignações com Desconto (QTD) [5]
                query.append("INSERT INTO tb_relatorio_estatistico (REE_NOME, REE_REFERENCIA, REE_ORDEM, REE_VERBA, REE_CSA, REE_VALOR, REE_QTD) ");
                query.append("SELECT '").append(tableName).append("', '").append(referencia).append("', 5, cnv_cod_verba, csa_identificador, count(ade.ade_codigo), count(ade.ade_codigo) ");
                query.append("FROM tb_aut_desconto ade ");
                query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
                query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
                query.append("INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo) ");
                query.append("INNER JOIN tb_historico_exportacao hie ON (cnv.org_codigo = hie.org_codigo) ");
                query.append("INNER JOIN tb_registro_servidor rse ON (ade.rse_codigo = rse.rse_codigo) ");
                query.append("INNER JOIN tb_parcela_desconto prd ON (ade.ade_codigo = prd.ade_codigo) ");
                query.append(whereClause.toString());
                query.append(" AND hie.hie_periodo =  '").append(referencia).append("' ");
                query.append(" AND NOT EXISTS (SELECT 1 FROM tb_historico_exportacao max_hie ");
                query.append("                 WHERE max_hie.org_codigo = cnv.org_codigo ");
                query.append("                   AND max_hie.hie_periodo = hie.hie_periodo ");
                query.append("                   AND max_hie.hie_data > hie.hie_data ");
                query.append(") ");
                query.append("AND prd.prd_data_desconto = hie.hie_periodo ");
                query.append("AND prd.spd_codigo = '6' ");
                query.append("AND ade.sad_codigo NOT IN ('0', '1', '2', '3', '7') ");
                query.append("AND ade.ade_ano_mes_ini <= hie.hie_periodo ");
                query.append("AND NOT EXISTS (SELECT 1 FROM tb_ocorrencia_autorizacao oca ");
                query.append("                WHERE oca.ade_codigo = ade.ade_codigo ");
                query.append("                  AND toc_codigo in ('6', '7', '15', '19') ");
                query.append("                  AND oca.oca_periodo <= hie.hie_periodo ");
                query.append(") ");
                query.append("GROUP BY csa_identificador, cnv_cod_verba");
                LOG.debug(query.toString());
                stat.executeUpdate(query.toString());
                query.setLength(0);

                // -- Não Desconto do Mês (R$) [6]
                query.append("INSERT INTO tb_relatorio_estatistico (REE_NOME, REE_REFERENCIA, REE_ORDEM, REE_VERBA, REE_CSA, REE_VALOR, REE_QTD) ");
                query.append("SELECT '").append(tableName).append("', '").append(referencia).append("', 6, cnv_cod_verba, csa_identificador, sum(prd_vlr_previsto), count(ade.ade_codigo) ");
                query.append("FROM tb_aut_desconto ade ");
                query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
                query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
                query.append("INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo) ");
                query.append("INNER JOIN tb_historico_exportacao hie ON (cnv.org_codigo = hie.org_codigo) ");
                query.append("INNER JOIN tb_registro_servidor rse ON (ade.rse_codigo = rse.rse_codigo) ");
                query.append("INNER JOIN tb_parcela_desconto prd ON (ade.ade_codigo = prd.ade_codigo) ");
                query.append(whereClause.toString());
                query.append(" AND hie.hie_periodo =  '").append(referencia).append("' ");
                query.append(" AND NOT EXISTS (SELECT 1 FROM tb_historico_exportacao max_hie ");
                query.append("                 WHERE max_hie.org_codigo = cnv.org_codigo ");
                query.append("                   AND max_hie.hie_periodo = hie.hie_periodo ");
                query.append("                   AND max_hie.hie_data > hie.hie_data ");
                query.append(") ");
                query.append("AND prd.prd_data_desconto = hie.hie_periodo ");
                query.append("AND prd.spd_codigo = '5' ");
                query.append("AND ade.sad_codigo NOT IN ('0', '1', '2', '3', '7') ");
                query.append("AND ade.ade_ano_mes_ini <= hie.hie_periodo ");
                query.append("AND NOT EXISTS (SELECT 1 FROM tb_ocorrencia_autorizacao oca ");
                query.append("                 WHERE oca.ade_codigo = ade.ade_codigo ");
                query.append("                   AND oca.toc_codigo in ('6', '7', '15', '19') ");
                query.append("                   AND oca.oca_periodo <= hie.hie_periodo ");
                query.append(") ");
                query.append("GROUP BY csa_identificador, cnv_cod_verba");
                LOG.debug(query.toString());
                stat.executeUpdate(query.toString());
                query.setLength(0);

                // -- Total de Desconto Previsto (R$) [7]
                query.append("INSERT INTO tb_relatorio_estatistico (REE_NOME, REE_REFERENCIA, REE_ORDEM, REE_VERBA, REE_CSA, REE_VALOR, REE_QTD) ");
                query.append("SELECT '").append(tableName).append("', '").append(referencia).append("', 7, cnv_cod_verba, csa_identificador, sum(prd_vlr_previsto), count(ade.ade_codigo) ");
                query.append("FROM tb_aut_desconto ade ");
                query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
                query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
                query.append("INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo) ");
                query.append("INNER JOIN tb_historico_exportacao hie ON (cnv.org_codigo = hie.org_codigo) ");
                query.append("INNER JOIN tb_registro_servidor rse ON (ade.rse_codigo = rse.rse_codigo) ");
                query.append("INNER JOIN tb_parcela_desconto prd ON (ade.ade_codigo = prd.ade_codigo) ");
                query.append(whereClause.toString());
                query.append(" AND hie.hie_periodo =  '").append(referencia).append("' ");
                query.append(" AND NOT EXISTS (SELECT 1 FROM tb_historico_exportacao max_hie ");
                query.append("                 WHERE max_hie.org_codigo = cnv.org_codigo ");
                query.append("                   AND max_hie.hie_periodo = hie.hie_periodo ");
                query.append("                   AND max_hie.hie_data > hie.hie_data ");
                query.append(") ");
                query.append("AND prd.prd_data_desconto = hie.hie_periodo ");
                query.append("AND ade.sad_codigo NOT IN ('0', '1', '2', '3', '7') ");
                query.append("AND ade.ade_ano_mes_ini <= hie.hie_periodo ");
                query.append("AND NOT EXISTS (SELECT 1 FROM tb_ocorrencia_autorizacao oca ");
                query.append("                 WHERE oca.ade_codigo = ade.ade_codigo ");
                query.append("                   AND oca.toc_codigo in ('6', '7', '15', '19') ");
                query.append("                   AND oca.oca_periodo <= hie.hie_periodo ");
                query.append(") ");
                query.append("GROUP BY csa_identificador, cnv_cod_verba");
                LOG.debug(query.toString());
                stat.executeUpdate(query.toString());
                query.setLength(0);

                // -- Percentual Não Desconto (% do R$) [8]
                query.append("INSERT INTO tb_relatorio_estatistico (REE_NOME, REE_REFERENCIA, REE_ORDEM, REE_VERBA, REE_CSA, REE_VALOR) ");
                query.append("SELECT '").append(tableName).append("', tbTotal.ree_referencia, 8, tbTotal.ree_verba, tbTotal.ree_csa, (coalesce(tbNaoDesconto.ree_valor,0.00)/tbTotal.ree_valor)*100.00 ");
                query.append("FROM tb_relatorio_estatistico tbTotal ");
                query.append("LEFT OUTER JOIN tb_relatorio_estatistico tbNaoDesconto ON (tbTotal.ree_nome = tbNaoDesconto.ree_nome AND tbTotal.ree_referencia = tbNaoDesconto.ree_referencia AND tbNaoDesconto.ree_ordem = 6 AND tbTotal.ree_csa = tbNaoDesconto.ree_csa AND tbTotal.ree_verba = tbNaoDesconto.ree_verba) ");
                query.append("WHERE tbTotal.ree_ordem = 7 AND tbTotal.ree_nome = '").append(tableName).append("'");
                LOG.debug(query.toString());
                stat.executeUpdate(query.toString());
                query.setLength(0);



                // -- Consignações sem Desconto (QTD) [9]
                query.append("INSERT INTO tb_relatorio_estatistico (REE_NOME, REE_REFERENCIA, REE_ORDEM, REE_VERBA, REE_CSA, REE_VALOR, REE_QTD) ");
                query.append("SELECT '").append(tableName).append("', '").append(referencia).append("', 9, cnv_cod_verba, csa_identificador, count(ade.ade_codigo), count(ade.ade_codigo) ");
                query.append("FROM tb_aut_desconto ade ");
                query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
                query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
                query.append("INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo) ");
                query.append("INNER JOIN tb_historico_exportacao hie ON (cnv.org_codigo = hie.org_codigo) ");
                query.append("INNER JOIN tb_registro_servidor rse ON (ade.rse_codigo = rse.rse_codigo) ");
                query.append("INNER JOIN tb_parcela_desconto prd ON (ade.ade_codigo = prd.ade_codigo) ");
                query.append(whereClause.toString());
                query.append(" AND hie.hie_periodo =  '").append(referencia).append("' ");
                query.append(" AND NOT EXISTS (SELECT 1 FROM tb_historico_exportacao max_hie ");
                query.append("                 WHERE max_hie.org_codigo = cnv.org_codigo ");
                query.append("                   AND max_hie.hie_periodo = hie.hie_periodo ");
                query.append("                   AND max_hie.hie_data > hie.hie_data ");
                query.append(") ");
                query.append("AND prd.prd_data_desconto = hie.hie_periodo ");
                query.append("AND prd.spd_codigo = '5' ");
                query.append("AND ade.sad_codigo NOT IN ('0', '1', '2', '3', '7') ");
                query.append("AND ade.ade_ano_mes_ini <= hie.hie_periodo ");
                query.append("AND NOT EXISTS (SELECT 1 FROM tb_ocorrencia_autorizacao oca ");
                query.append("                 WHERE oca.ade_codigo = ade.ade_codigo ");
                query.append("                   AND toc_codigo in ('6', '7', '15', '19') ");
                query.append("                   AND oca.oca_periodo <= hie.hie_periodo ");
                query.append(") ");
                query.append("GROUP BY csa_identificador, cnv_cod_verba");
                LOG.debug(query.toString());
                stat.executeUpdate(query.toString());
                query.setLength(0);

                // -- Percentual Consig. s/ Desconto (% da QTD) [10]
                query.append("INSERT INTO tb_relatorio_estatistico (REE_NOME, REE_REFERENCIA, REE_ORDEM, REE_VERBA, REE_CSA, REE_VALOR) ");
                query.append("SELECT '").append(tableName).append("', tbTotal.ree_referencia, 10, tbTotal.ree_verba, tbTotal.ree_csa, (coalesce(tbNaoDesconto.ree_valor,0.00)/tbTotal.ree_valor)*100.00 ");
                query.append("FROM tb_relatorio_estatistico tbTotal ");
                query.append("LEFT OUTER JOIN tb_relatorio_estatistico tbNaoDesconto ON (tbTotal.ree_nome = tbNaoDesconto.ree_nome AND tbTotal.ree_referencia = tbNaoDesconto.ree_referencia AND tbNaoDesconto.ree_ordem = 9 AND tbTotal.ree_csa = tbNaoDesconto.ree_csa AND tbTotal.ree_verba = tbNaoDesconto.ree_verba) ");
                query.append("WHERE tbTotal.ree_ordem = 1 ");
                query.append("AND tbTotal.ree_nome = '").append(tableName).append("'");
                LOG.debug(query.toString());
                stat.executeUpdate(query.toString());
                query.setLength(0);

                // -- Custeio da Operação [11], [12] e [13]
                query.append("INSERT INTO tb_relatorio_estatistico (REE_NOME, REE_REFERENCIA, REE_ORDEM, REE_VERBA, REE_CSA, REE_VALOR, REE_QTD) ");
                query.append("SELECT '").append(tableName).append("', '").append(referencia).append("', (CASE tpt_codigo WHEN '1' THEN 11 WHEN '2' THEN 12 ELSE 13 END), cnv_cod_verba, csa_identificador, (CASE tpt_codigo WHEN '1' THEN (sum(prd_vlr_realizado * (coalesce(PCV_VLR, 0) / 100))) WHEN '2' THEN (sum(coalesce(PCV_VLR, 0))) ELSE 0 END), count(ade.ade_codigo) ");
                query.append("FROM tb_aut_desconto ade ");
                query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
                query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
                query.append("INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo) ");
                query.append("INNER JOIN tb_historico_exportacao hie ON (cnv.org_codigo = hie.org_codigo) ");
                query.append("INNER JOIN tb_registro_servidor rse ON (ade.rse_codigo = rse.rse_codigo) ");
                query.append("INNER JOIN tb_parcela_desconto prd ON (ade.ade_codigo = prd.ade_codigo) ");
                query.append("LEFT OUTER JOIN tb_param_tarif_consignante ptc ON (ptc.svc_codigo = cnv.svc_codigo AND tpt_codigo IN ('1', '2')) ");
                query.append(whereClause.toString());
                query.append(" AND hie.hie_periodo =  '").append(referencia).append("' ");
                query.append(" AND NOT EXISTS (SELECT 1 FROM tb_historico_exportacao max_hie ");
                query.append("                 WHERE max_hie.org_codigo = cnv.org_codigo ");
                query.append("                   AND max_hie.hie_periodo = hie.hie_periodo ");
                query.append("                   AND max_hie.hie_data > hie.hie_data ");
                query.append(") ");
                query.append("AND prd.prd_data_desconto = hie.hie_periodo ");
                query.append("AND prd.spd_codigo = '6' ");
                query.append("AND ade.sad_codigo NOT IN ('0', '1', '2', '3', '7') ");
                query.append("AND ade.ade_ano_mes_ini <= hie.hie_periodo ");
                query.append("AND NOT EXISTS (SELECT 1 FROM tb_ocorrencia_autorizacao oca ");
                query.append("                 WHERE oca.ade_codigo = ade.ade_codigo ");
                query.append("                   AND toc_codigo in ('6', '7', '15', '19') ");
                query.append("                   AND oca.oca_periodo <= hie.hie_periodo ");
                query.append(") ");
                query.append("GROUP BY csa_identificador, cnv_cod_verba, tpt_codigo");
                LOG.debug(query.toString());
                stat.executeUpdate(query.toString());
                query.setLength(0);

                // -- Total Liquidados no Período [14]
                query.append("INSERT INTO tb_relatorio_estatistico (REE_NOME, REE_REFERENCIA, REE_ORDEM, REE_VERBA, REE_CSA, REE_VALOR, REE_QTD) ");
                query.append("SELECT '").append(tableName).append("', '").append(referencia).append("', 14, cnv_cod_verba, csa_identificador, count(ade.ade_codigo), count(ade.ade_codigo) ");
                query.append("FROM tb_aut_desconto ade ");
                query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
                query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
                query.append("INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo) ");
                query.append("INNER JOIN tb_historico_exportacao hie ON (cnv.org_codigo = hie.org_codigo) ");
                query.append("INNER JOIN tb_registro_servidor rse ON (ade.rse_codigo = rse.rse_codigo) ");
                query.append(whereClause.toString());
                query.append(" AND hie.hie_periodo =  '").append(referencia).append("' ");
                query.append(" AND NOT EXISTS (SELECT 1 FROM tb_historico_exportacao max_hie ");
                query.append("                 WHERE max_hie.org_codigo = cnv.org_codigo ");
                query.append("                   AND max_hie.hie_periodo = hie.hie_periodo ");
                query.append("                   AND max_hie.hie_data > hie.hie_data ");
                query.append(") ");
                query.append("AND ade.sad_codigo = '8' ");
                query.append("AND ade.ade_ano_mes_ini <= hie.hie_periodo ");
                query.append("AND NOT EXISTS (SELECT 1 FROM tb_ocorrencia_autorizacao oca ");
                query.append("                 WHERE oca.ade_codigo = ade.ade_codigo ");
                query.append("                   AND toc_codigo in ('6', '7', '15', '19') ");
                query.append("                   AND oca.oca_periodo <= hie.hie_periodo ");
                query.append(") ");
                query.append("GROUP BY csa_identificador, cnv_cod_verba");
                LOG.debug(query.toString());
                stat.executeUpdate(query.toString());
                query.setLength(0);

                // -- Total Alterados no Período [15]
                query.append("INSERT INTO tb_relatorio_estatistico (REE_NOME, REE_REFERENCIA, REE_ORDEM, REE_VERBA, REE_CSA, REE_VALOR, REE_QTD) ");
                query.append("SELECT '").append(tableName).append("', '").append(referencia).append("', 15, cnv_cod_verba, csa_identificador, count(ade.ade_codigo), count(ade.ade_codigo) ");
                query.append("FROM tb_aut_desconto ade ");
                query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
                query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
                query.append("INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo) ");
                query.append("INNER JOIN tb_historico_exportacao hie ON (cnv.org_codigo = hie.org_codigo) ");
                query.append("INNER JOIN tb_registro_servidor rse ON (ade.rse_codigo = rse.rse_codigo) ");
                query.append(whereClause.toString());
                query.append(" AND hie.hie_periodo =  '").append(referencia).append("' ");
                query.append(" AND NOT EXISTS (SELECT 1 FROM tb_historico_exportacao max_hie ");
                query.append("                 WHERE max_hie.org_codigo = cnv.org_codigo ");
                query.append("                   AND max_hie.hie_periodo = hie.hie_periodo ");
                query.append("                   AND max_hie.hie_data > hie.hie_data ");
                query.append(") ");
                query.append("AND sad_codigo NOT IN ('0', '1', '2', '3', '7') ");
                query.append("AND ade.ade_ano_mes_ini <= hie.hie_periodo ");
                query.append("AND NOT EXISTS (SELECT 1 FROM tb_ocorrencia_autorizacao oca ");
                query.append("                 WHERE oca.ade_codigo = ade.ade_codigo ");
                query.append("                   AND toc_codigo in ('14') ");
                query.append("                   AND oca.oca_periodo <= hie.hie_periodo ");
                query.append(") ");
                query.append("GROUP BY csa_identificador, cnv_cod_verba");
                LOG.debug(query.toString());
                stat.executeUpdate(query.toString());
                query.setLength(0);
            }
        } catch (final SQLException ex) {
            // DO NOTHING
            LOG.error(ex.getMessage(), ex);
        } finally {
            if (stat != null) {
                try {
                    stat.close();
                } catch (final SQLException ex) {
                }
            }
        }
    }

    @Override
    public void postSqlProcess(Connection conn) {
        final String tableName = (String) parameters.get("TABLE_NAME");

        final StringBuilder query = new StringBuilder();
        Statement stat = null;
        try {
            stat = conn.createStatement();
            query.append("DELETE FROM tb_relatorio_estatistico WHERE ree_nome = '").append(tableName).append("'");
            LOG.debug(query.toString());
        } catch (final SQLException ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            if (stat != null) {
                try {
                    stat.close();
                } catch (final SQLException ex) {
                }
            }
        }
    }
}
