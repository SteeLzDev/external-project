package com.zetra.econsig.persistence.dao.mysql;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.generic.GenericConsigBIDAO;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: MySqlConsigBIDAO</p>
 * <p>Description: Implementacao do DAO de Business Intelligence para o MySql</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlConsigBIDAO extends GenericConsigBIDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlConsigBIDAO.class);

    /* (non-Javadoc)
     * @see com.zetra.econsig.persistence.dao.ConsigBIDAO#atualizarDimensoes()
     */
    @Override
    public void atualizarDimensoes() throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();

            final String indet = String.format("%.5s", ApplicationResourcesHelper.getMessage("rotulo.indeterminado.abreviado", (AcessoSistema) null));
            final String naoDefinido = ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null);

            // tb_dimensao_consignataria //////////////////////////////////////////////////////////////////////////////
            query.append("DELETE FROM tb_dimensao_consignataria");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("ALTER TABLE tb_dimensao_consignataria AUTO_INCREMENT = 1");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_consignataria (dca_id_febraban, dca_csa_codigo, dca_csa_nome, dca_cor_codigo, dca_cor_nome) ");
            query.append("SELECT coalesce(csa.csa_identificador_interno, '").append(naoDefinido).append("'), csa.csa_codigo, UPPER(COALESCE(NULLIF(csa.csa_nome_abrev, ''), csa.csa_nome)), cor.cor_codigo, UPPER(cor.cor_nome) ");
            query.append("FROM tb_consignataria csa ");
            query.append("INNER JOIN tb_correspondente cor on (csa.csa_codigo = cor.csa_codigo) ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_consignataria (dca_id_febraban, dca_csa_codigo, dca_csa_nome, dca_cor_codigo, dca_cor_nome) ");
            query.append("SELECT coalesce(csa.csa_identificador_interno, '").append(naoDefinido).append("'), csa_codigo, UPPER(COALESCE(NULLIF(csa_nome_abrev, ''), csa_nome)), csa_codigo as cor_codigo, '").append(naoDefinido).append("' as cor_nome ");
            query.append("FROM tb_consignataria csa");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_dimensao_servico ////////////////////////////////////////////////////////////////////////////////////
            query.append("DELETE FROM tb_dimensao_servico");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("ALTER TABLE tb_dimensao_servico AUTO_INCREMENT = 1");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_servico (dse_nse_codigo, dse_nse_descricao, dse_svc_codigo, dse_svc_descricao) ");
            query.append("SELECT nse.nse_codigo, UPPER(nse.nse_descricao), svc.svc_codigo, UPPER(svc.svc_descricao) ");
            query.append("FROM tb_servico svc ");
            query.append("INNER JOIN tb_natureza_servico nse on (svc.nse_codigo = nse.nse_codigo) ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_dimensao_localizacao ////////////////////////////////////////////////////////////////////////////////
            query.append("DELETE FROM tb_dimensao_localizacao");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("ALTER TABLE tb_dimensao_localizacao AUTO_INCREMENT = 1");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_localizacao (dlo_est_codigo, dlo_est_nome, dlo_org_codigo, dlo_org_nome) ");
            query.append("SELECT est.est_codigo, UPPER(est.est_nome), org.org_codigo, UPPER(org.org_nome) ");
            query.append("FROM tb_estabelecimento est ");
            query.append("INNER JOIN tb_orgao org on (est.est_codigo = org.est_codigo) ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_dimensao_status_contrato ////////////////////////////////////////////////////////////////////////////
            query.append("DELETE FROM tb_dimensao_status_contrato");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_status_contrato (dsc_codigo, dsc_descricao, dsc_grupo) ");
            query.append("SELECT CAST(sad_codigo AS UNSIGNED INT), sad_descricao, ");
            query.append("case when sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("') then '").append(ApplicationResourcesHelper.getMessage("rotulo.bi.dimensao.status.contrato.finalizado", (AcessoSistema) null)).append("' else '").append(ApplicationResourcesHelper.getMessage("rotulo.bi.dimensao.status.contrato.ativo", (AcessoSistema) null)).append("' end ");
            query.append("FROM tb_status_autorizacao_desconto ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_dimensao_categoria_servidor /////////////////////////////////////////////////////////////////////////
            query.append("DELETE FROM tb_dimensao_categoria_servidor");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("ALTER TABLE tb_dimensao_categoria_servidor AUTO_INCREMENT = 1");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_categoria_servidor (dcs_descricao) ");
            query.append("SELECT rse_tipo FROM tb_registro_servidor ");
            query.append("WHERE COALESCE(rse_tipo, '') <> '' ");
            query.append("GROUP BY rse_tipo ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_categoria_servidor (dcs_codigo, dcs_descricao) VALUES (9999, '").append(naoDefinido).append("')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_dimensao_cargo_servidor /////////////////////////////////////////////////////////////////////////
            query.append("DELETE FROM tb_dimensao_cargo_servidor");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("ALTER TABLE tb_dimensao_cargo_servidor AUTO_INCREMENT = 1");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_cargo_servidor (dcr_identificador, dcr_descricao) ");
            query.append("SELECT crs_identificador, crs_descricao FROM tb_cargo_registro_servidor ");
            query.append("WHERE nullif(crs_identificador, '') is not null ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_cargo_servidor (dcr_codigo, dcr_identificador, dcr_descricao) VALUES (9999, '").append(naoDefinido).append("', '").append(naoDefinido).append("')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_dimensao_posto_servidor /////////////////////////////////////////////////////////////////////////
            query.append("DELETE FROM tb_dimensao_posto_servidor");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("ALTER TABLE tb_dimensao_posto_servidor AUTO_INCREMENT = 1");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_posto_servidor (dpr_identificador, dpr_descricao) ");
            query.append("SELECT pos_identificador, pos_descricao FROM tb_posto_registro_servidor ");
            query.append("WHERE nullif(pos_identificador, '') is not null ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_posto_servidor (dpr_codigo, dpr_identificador, dpr_descricao) VALUES (9999, '").append(naoDefinido).append("', '").append(naoDefinido).append("')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_dimensao_tipo_margem ////////////////////////////////////////////////////////////////////////////////
            query.append("DELETE FROM tb_dimensao_tipo_margem");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_tipo_margem (dtm_codigo, dtm_descricao) ");
            query.append("SELECT mar_codigo, mar_descricao ");
            query.append("FROM tb_margem mar ");
            query.append("WHERE mar_codigo <> 0 ");
            query.append("AND EXISTS ( ");
            query.append("SELECT 1 FROM tb_param_svc_consignante pse ");
            query.append("WHERE pse.tps_codigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("' ");
            query.append("AND CAST(pse.pse_vlr AS UNSIGNED INT) = mar.mar_codigo ");
            query.append(") ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_dimensao_comprometimento ////////////////////////////////////////////////////////////////////////////
            query.append("DELETE FROM tb_dimensao_comprometimento");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_comprometimento (dco_codigo, dco_descricao) ");
            query.append("VALUES (0, '0%'),(1, '<20%'),(2, '20/25%'),(3, '25/30%'),(4, '30/35%'),(5, '35/40%'),(6, '40/45%'), ");
            query.append("(7, '45/50%'),(8, '50/55%'),(9, '55/60%'),(10, '60/65%'),(11, '65/70%'),(12, '70/75%'), ");
            query.append("(13, '75/80%'),(14, '80/85%'),(15, '85/90%'),(16, '90/95%'),(17, '95/100%'),(18, '>100%') ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_dimensao_prazo_contrato /////////////////////////////////////////////////////////////////////////////
            query.append("DELETE FROM tb_dimensao_prazo_contrato");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_prazo_contrato (dpc_codigo, dpc_prazo, dpc_faixa) ");
            query.append("VALUES (00, 00, '").append(indet).append("'), ");
            query.append("(01, 01, '01-12'),(02, 02, '01-12'),(03, 03, '01-12'),(04, 04, '01-12'),(05, 05, '01-12'),(06, 06, '01-12'),(07, 07, '01-12'),(08, 08, '01-12'),(09, 09, '01-12'),(10, 10, '01-12'),(11, 11, '01-12'),(12, 12, '01-12'), ");
            query.append("(13, 13, '13-24'),(14, 14, '13-24'),(15, 15, '13-24'),(16, 16, '13-24'),(17, 17, '13-24'),(18, 18, '13-24'),(19, 19, '13-24'),(20, 20, '13-24'),(21, 21, '13-24'),(22, 22, '13-24'),(23, 23, '13-24'),(24, 24, '13-24'), ");
            query.append("(25, 25, '25-36'),(26, 26, '25-36'),(27, 27, '25-36'),(28, 28, '25-36'),(29, 29, '25-36'),(30, 30, '25-36'),(31, 31, '25-36'),(32, 32, '25-36'),(33, 33, '25-36'),(34, 34, '25-36'),(35, 35, '25-36'),(36, 36, '25-36'), ");
            query.append("(37, 37, '37-48'),(38, 38, '37-48'),(39, 39, '37-48'),(40, 40, '37-48'),(41, 41, '37-48'),(42, 42, '37-48'),(43, 43, '37-48'),(44, 44, '37-48'),(45, 45, '37-48'),(46, 46, '37-48'),(47, 47, '37-48'),(48, 48, '37-48'), ");
            query.append("(49, 49, '49-60'),(50, 50, '49-60'),(51, 51, '49-60'),(52, 52, '49-60'),(53, 53, '49-60'),(54, 54, '49-60'),(55, 55, '49-60'),(56, 56, '49-60'),(57, 57, '49-60'),(58, 58, '49-60'),(59, 59, '49-60'),(60, 60, '49-60'), ");
            query.append("(61, 61, '61-72'),(62, 62, '61-72'),(63, 63, '61-72'),(64, 64, '61-72'),(65, 65, '61-72'),(66, 66, '61-72'),(67, 67, '61-72'),(68, 68, '61-72'),(69, 69, '61-72'),(70, 70, '61-72'),(71, 71, '61-72'),(72, 72, '61-72'), ");
            query.append("(73, 73, '73-99'),(74, 74, '73-99'),(75, 75, '73-99'),(76, 76, '73-99'),(77, 77, '73-99'),(78, 78, '73-99'),(79, 79, '73-99'),(80, 80, '73-99'),(81, 81, '73-99'),(82, 82, '73-99'),(83, 83, '73-99'),(84, 84, '73-99'), ");
            query.append("(85, 85, '73-99'),(86, 86, '73-99'),(87, 87, '73-99'),(88, 88, '73-99'),(89, 89, '73-99'),(90, 90, '73-99'),(91, 91, '73-99'),(92, 92, '73-99'),(93, 93, '73-99'),(94, 94, '73-99'),(95, 95, '73-99'),(96, 96, '73-99'), ");
            query.append("(97, 97, '73-99'),(98, 98, '73-99'),(99, 99, '73-99'), ");
            query.append("(100, 100, '>=100') ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_dimensao_valor_contrato /////////////////////////////////////////////////////////////////////////////
            query.append("DELETE FROM tb_dimensao_valor_contrato");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_valor_contrato (dvc_codigo, dvc_vlr_ini, dvc_vlr_fim, dvc_faixa) VALUES ");
            query.append("(0,      0.00,   5000.00,      '0-5.000'), ");
            query.append("(1,   5000.01,  10000.00,  '5.000-10.000'), ");
            query.append("(2,  10000.01,  20000.00, '10.000-20.000'), ");
            query.append("(3,  20000.01,  30000.00, '20.000-30.000'), ");
            query.append("(4,  30000.01,  40000.00, '30.000-40.000'), ");
            query.append("(5,  40000.01,  50000.00, '40.000-50.000'), ");
            query.append("(6,  50000.01,  60000.00, '50.000-60.000'), ");
            query.append("(7,  60000.01,  70000.00, '60.000-70.000'), ");
            query.append("(8,  70000.01,  80000.00, '70.000-80.000'), ");
            query.append("(9,  80000.01,  90000.00, '80.000-90.000'), ");
            query.append("(10, 90000.01, 100000.00, '90.000-100.000'), ");

            query.append("(11, 100000.01, 110000.00, '100.000-110.000'), ");
            query.append("(12, 110000.01, 120000.00, '110.000-120.000'), ");
            query.append("(13, 120000.01, 130000.00, '120.000-130.000'), ");
            query.append("(14, 130000.01, 140000.00, '130.000-140.000'), ");
            query.append("(15, 140000.01, 150000.00, '140.000-150.000'), ");
            query.append("(16, 150000.01, 160000.00, '150.000-160.000'), ");
            query.append("(17, 160000.01, 170000.00, '160.000-170.000'), ");
            query.append("(18, 170000.01, 180000.00, '170.000-180.000'), ");
            query.append("(19, 180000.01, 190000.00, '180.000-190.000'), ");
            query.append("(20, 190000.01, 200000.00, '190.000-200.000'), ");

            query.append("(21, 200000.01, 210000.00, '200.000-210.000'), ");
            query.append("(22, 210000.01, 220000.00, '210.000-220.000'), ");
            query.append("(23, 220000.01, 230000.00, '220.000-230.000'), ");
            query.append("(24, 230000.01, 240000.00, '230.000-240.000'), ");
            query.append("(25, 240000.01, 250000.00, '240.000-250.000'), ");
            query.append("(26, 250000.01, 260000.00, '250.000-260.000'), ");
            query.append("(27, 260000.01, 270000.00, '260.000-270.000'), ");
            query.append("(28, 270000.01, 280000.00, '270.000-280.000'), ");
            query.append("(29, 280000.01, 290000.00, '280.000-290.000'), ");
            query.append("(30, 290000.01, 300000.00, '290.000-300.000'), ");

            query.append("(31, 300000.01, 310000.00, '300.000-310.000'), ");
            query.append("(32, 310000.01, 320000.00, '310.000-320.000'), ");
            query.append("(33, 320000.01, 330000.00, '320.000-330.000'), ");
            query.append("(34, 330000.01, 340000.00, '330.000-340.000'), ");
            query.append("(35, 340000.01, 350000.00, '340.000-350.000'), ");
            query.append("(36, 350000.01, 360000.00, '350.000-360.000'), ");
            query.append("(37, 360000.01, 370000.00, '360.000-370.000'), ");
            query.append("(38, 370000.01, 380000.00, '370.000-380.000'), ");
            query.append("(39, 380000.01, 390000.00, '380.000-390.000'), ");
            query.append("(40, 390000.01, 400000.00, '390.000-400.000'), ");

            query.append("(41, 400000.01, 410000.00, '400.000-410.000'), ");
            query.append("(42, 410000.01, 420000.00, '410.000-420.000'), ");
            query.append("(43, 420000.01, 430000.00, '420.000-430.000'), ");
            query.append("(44, 430000.01, 440000.00, '430.000-440.000'), ");
            query.append("(45, 440000.01, 450000.00, '440.000-450.000'), ");
            query.append("(46, 450000.01, 460000.00, '450.000-460.000'), ");
            query.append("(47, 460000.01, 470000.00, '460.000-470.000'), ");
            query.append("(48, 470000.01, 480000.00, '470.000-480.000'), ");
            query.append("(49, 480000.01, 490000.00, '480.000-490.000'), ");
            query.append("(50, 490000.01, 500000.00, '490.000-500.000'), ");

            query.append("(51, 500000.01, 1000000.00, '500.000-1.000.000'), ");
            query.append("(52, 1000000.01, 99999999999.99, '>1.000.000') ");

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_dimensao_valor_parcela /////////////////////////////////////////////////////////////////////////////
            query.append("DELETE FROM tb_dimensao_valor_parcela");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_valor_parcela (dvp_codigo, dvp_vlr_ini, dvp_vlr_fim, dvp_faixa) VALUES ");
            query.append("(0,    0.00,   10.00,    '0-10'), ");
            query.append("(1,   10.01,   50.00,   '10-50'), ");
            query.append("(2,   50.01,  100.00,  '50-100'), ");
            query.append("(3,  100.01,  200.00, '100-200'), ");
            query.append("(4,  200.01,  300.00, '200-300'), ");
            query.append("(5,  300.01,  400.00, '300-400'), ");
            query.append("(6,  400.01,  500.00, '400-500'), ");
            query.append("(7,  500.01,  600.00, '500-600'), ");
            query.append("(8,  600.01,  700.00, '600-700'), ");
            query.append("(9,  700.01,  800.00, '700-800'), ");
            query.append("(10, 800.01,  900.00, '800-900'), ");
            query.append("(11, 900.01, 1000.00, '900-1.000'), ");

            query.append("(12, 1000.01, 1100.00, '1.000-1.100'), ");
            query.append("(13, 1100.01, 1200.00, '1.100-1.200'), ");
            query.append("(14, 1200.01, 1300.00, '1.200-1.300'), ");
            query.append("(15, 1300.01, 1400.00, '1.300-1.400'), ");
            query.append("(16, 1400.01, 1500.00, '1.400-1.500'), ");
            query.append("(17, 1500.01, 1600.00, '1.500-1.600'), ");
            query.append("(18, 1600.01, 1700.00, '1.600-1.700'), ");
            query.append("(19, 1700.01, 1800.00, '1.700-1.800'), ");
            query.append("(20, 1800.01, 1900.00, '1.800-1.900'), ");
            query.append("(21, 1900.01, 2000.00, '1.900-2.000'), ");

            query.append("(22, 2000.01, 2100.00, '2.000-2.100'), ");
            query.append("(23, 2100.01, 2200.00, '2.100-2.200'), ");
            query.append("(24, 2200.01, 2300.00, '2.200-2.300'), ");
            query.append("(25, 2300.01, 2400.00, '2.300-2.400'), ");
            query.append("(26, 2400.01, 2500.00, '2.400-2.500'), ");
            query.append("(27, 2500.01, 2600.00, '2.500-2.600'), ");
            query.append("(28, 2600.01, 2700.00, '2.600-2.700'), ");
            query.append("(29, 2700.01, 2800.00, '2.700-2.800'), ");
            query.append("(30, 2800.01, 2900.00, '2.800-2.900'), ");
            query.append("(31, 2900.01, 3000.00, '2.900-3.000'), ");

            query.append("(32, 3000.01, 3100.00, '3.000-3.100'), ");
            query.append("(33, 3100.01, 3200.00, '3.100-3.200'), ");
            query.append("(34, 3200.01, 3300.00, '3.200-3.300'), ");
            query.append("(35, 3300.01, 3400.00, '3.300-3.400'), ");
            query.append("(36, 3400.01, 3500.00, '3.400-3.500'), ");
            query.append("(37, 3500.01, 3600.00, '3.500-3.600'), ");
            query.append("(38, 3600.01, 3700.00, '3.600-3.700'), ");
            query.append("(39, 3700.01, 3800.00, '3.700-3.800'), ");
            query.append("(40, 3800.01, 3900.00, '3.800-3.900'), ");
            query.append("(41, 3900.01, 4000.00, '3.900-4.000'), ");

            query.append("(42, 4000.01, 4100.00, '4.000-4.100'), ");
            query.append("(43, 4100.01, 4200.00, '4.100-4.200'), ");
            query.append("(44, 4200.01, 4300.00, '4.200-4.300'), ");
            query.append("(45, 4300.01, 4400.00, '4.300-4.400'), ");
            query.append("(46, 4400.01, 4500.00, '4.400-4.500'), ");
            query.append("(47, 4500.01, 4600.00, '4.500-4.600'), ");
            query.append("(48, 4600.01, 4700.00, '4.600-4.700'), ");
            query.append("(49, 4700.01, 4800.00, '4.700-4.800'), ");
            query.append("(50, 4800.01, 4900.00, '4.800-4.900'), ");
            query.append("(51, 4900.01, 5000.00, '4.900-5.000'), ");

            query.append("(52, 5000.01, 10000.00, '5.000-10.000'), ");
            query.append("(53, 10000.01, 99999999999.99, '>10.000') ");

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_dimensao_origem_contrato ////////////////////////////////////////////////////////////////////////////
            query.append("DELETE FROM tb_dimensao_origem_contrato");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_origem_contrato (doc_codigo, doc_descricao) ");
            query.append("VALUES ");
            query.append("(1, '").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.novo.contrato", null)).append("'), ");
            query.append("(2, '").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.renegociacao", null)).append("'), ");
            query.append("(3, '").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.compra", null)).append("') ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_dimensao_termino_contrato ///////////////////////////////////////////////////////////////////////////
            query.append("DELETE FROM tb_dimensao_termino_contrato");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_termino_contrato (dtc_codigo, dtc_descricao) ");
            query.append("VALUES ");
            query.append("(1, '").append(ApplicationResourcesHelper.getMessage("rotulo.bi.dimensao.termino.contrato.liquidacao.antecipada", (AcessoSistema) null)).append("'),");
            query.append("(2, '").append(ApplicationResourcesHelper.getMessage("rotulo.bi.dimensao.termino.contrato.renegociacao", (AcessoSistema) null)).append("'),");
            query.append("(3, '").append(ApplicationResourcesHelper.getMessage("rotulo.bi.dimensao.termino.contrato.venda", (AcessoSistema) null)).append("'),");
            query.append("(4, '").append(ApplicationResourcesHelper.getMessage("rotulo.bi.dimensao.termino.contrato.conclusao", (AcessoSistema) null)).append("'),");
            query.append("(5, '").append(ApplicationResourcesHelper.getMessage("rotulo.bi.dimensao.termino.contrato.cancelamento", (AcessoSistema) null)).append("'),");
            query.append("(9, '").append(naoDefinido).append("') ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_dimensao_status_servidor ////////////////////////////////////////////////////////////////////////////
            query.append("DELETE FROM tb_dimensao_status_servidor");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_status_servidor (dss_codigo, dss_descricao) ");
            query.append("VALUES ");
            query.append("(1, '").append(ApplicationResourcesHelper.getMessage("rotulo.bi.dimensao.status.servidor.ativo", (AcessoSistema) null)).append("'),");
            query.append("(2, '").append(ApplicationResourcesHelper.getMessage("rotulo.bi.dimensao.status.servidor.bloqueado", (AcessoSistema) null)).append("'),");
            query.append("(3, '").append(ApplicationResourcesHelper.getMessage("rotulo.bi.dimensao.status.servidor.excluido", (AcessoSistema) null)).append("') ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_dimensao_sexo_servidor //////////////////////////////////////////////////////////////////////////////
            query.append("DELETE FROM tb_dimensao_sexo_servidor");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_sexo_servidor (dsx_codigo, dsx_descricao) ");
            query.append("VALUES ");
            query.append("(1, '").append(ApplicationResourcesHelper.getMessage("rotulo.servidor.sexo.masculino", (AcessoSistema) null)).append("'),");
            query.append("(2, '").append(ApplicationResourcesHelper.getMessage("rotulo.servidor.sexo.feminino", (AcessoSistema) null)).append("'),");
            query.append("(9, '").append(naoDefinido).append("') ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_dimensao_idade_servidor //////////////////////////////////////////////////////////////////////////////
            query.append("DELETE FROM tb_dimensao_idade_servidor");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_idade_servidor (dis_codigo, dis_idade, dis_faixa_etaria) ");
            query.append("VALUES (00, 00, '").append(indet).append("'), ");
            query.append("(01, 01, '01-18'),(02, 02, '01-18'),(03, 03, '01-18'),(04, 04, '01-18'),(05, 05, '01-18'),(06, 06, '01-18'),(07, 07, '01-18'),(08, 08, '01-18'),(09, 09, '01-18'),(10, 10, '01-18'),(11, 11, '01-18'),(12, 12, '01-18'),(13, 13, '01-18'),(14, 14, '01-18'),(15, 15, '01-18'),(16, 16, '01-18'),(17, 17, '01-18'),(18, 18, '01-18'),");
            query.append("(19, 19, '19-30'),(20, 20, '19-30'),(21, 21, '19-30'),(22, 22, '19-30'),(23, 23, '19-30'),(24, 24, '19-30'),(25, 25, '19-30'),(26, 26, '19-30'),(27, 27, '19-30'),(28, 28, '19-30'),(29, 29, '19-30'),(30, 30, '19-30'),");
            query.append("(31, 31, '31-40'),(32, 32, '31-40'),(33, 33, '31-40'),(34, 34, '31-40'),(35, 35, '31-40'),(36, 36, '31-40'),(37, 37, '31-40'),(38, 38, '31-40'),(39, 39, '31-40'),(40, 40, '31-40'),");
            query.append("(41, 41, '41-50'),(42, 42, '41-50'),(43, 43, '41-50'),(44, 44, '41-50'),(45, 45, '41-50'),(46, 46, '41-50'),(47, 47, '41-50'),(48, 48, '41-50'),(49, 49, '41-50'),(50, 50, '41-50'),");
            query.append("(51, 51, '51-60'),(52, 52, '51-60'),(53, 53, '51-60'),(54, 54, '51-60'),(55, 55, '51-60'),(56, 56, '51-60'),(57, 57, '51-60'),(58, 58, '51-60'),(59, 59, '51-60'),(60, 60, '51-60'),");
            query.append("(61, 61, '61-70'),(62, 62, '61-70'),(63, 63, '61-70'),(64, 64, '61-70'),(65, 65, '61-70'),(66, 66, '61-70'),(67, 67, '61-70'),(68, 68, '61-70'),(69, 69, '61-70'),(70, 70, '61-70'),");
            query.append("(71, 71, '71-80'),(72, 72, '71-80'),(73, 73, '71-80'),(74, 74, '71-80'),(75, 75, '71-80'),(76, 76, '71-80'),(77, 77, '71-80'),(78, 78, '71-80'),(79, 79, '71-80'),(80, 80, '71-80'),");
            query.append("(81, 81, '81-99'),(82, 82, '81-99'),(83, 83, '81-99'),(84, 84, '81-99'),(85, 85, '81-99'),(86, 86, '81-99'),(87, 87, '81-99'),(88, 88, '81-99'),(89, 89, '81-99'),(90, 90, '81-99'),");
            query.append("(91, 91, '81-99'),(92, 92, '81-99'),(93, 93, '81-99'),(94, 94, '81-99'),(95, 95, '81-99'),(96, 96, '81-99'),(97, 97, '81-99'),(98, 98, '81-99'),(99, 99, '81-99'),");
            query.append("(100, 100, '>=100')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_dimensao_verba_convenio //////////////////////////////////////////////////////////////////////////////
            query.append("DELETE FROM tb_dimensao_verba_convenio");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("ALTER TABLE tb_dimensao_verba_convenio AUTO_INCREMENT = 1");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_verba_convenio (dve_descricao) ");
            query.append("SELECT cnv.cnv_cod_verba ");
            query.append("FROM tb_convenio cnv ");
            query.append("WHERE EXISTS ( ");
            query.append("  SELECT 1 FROM tb_aut_desconto ade ");
            query.append("  INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
            query.append("  WHERE vco.cnv_codigo = cnv.cnv_codigo ");
            query.append(") ");
            query.append("GROUP BY cnv.cnv_cod_verba ");
            query.append("ORDER BY cnv.cnv_cod_verba ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_verba_convenio (dve_codigo, dve_descricao) VALUES (9999, '").append(naoDefinido).append("')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_dimensao_lotacao_servidor //////////////////////////////////////////////////////////////////////////////
            query.append("DELETE FROM tb_dimensao_lotacao_servidor");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("ALTER TABLE tb_dimensao_lotacao_servidor AUTO_INCREMENT = 1");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_lotacao_servidor (dls_descricao) ");
            query.append("SELECT UPPER(TRIM(rse.rse_municipio_lotacao)) ");
            query.append("FROM tb_registro_servidor rse ");
            query.append("WHERE NULLIF(TRIM(rse.rse_municipio_lotacao), '') IS NOT NULL ");
            query.append("GROUP BY UPPER(TRIM(rse.rse_municipio_lotacao)) ");
            query.append("ORDER BY UPPER(TRIM(rse.rse_municipio_lotacao)) ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_dimensao_lotacao_servidor (dls_descricao) VALUES ('").append(naoDefinido).append("')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.persistence.dao.ConsigBIDAO#atualizarTabelasAuxiliares()
     */
    @Override
    public void atualizarTabelasAuxiliares() throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final String naoDefinido = ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null);

        // Tipos de ocorrência de concluídos
        final List<String> tocCodigosConcluidosCancelados = new ArrayList<>();
        tocCodigosConcluidosCancelados.add(CodedValues.TOC_TARIF_LIQUIDACAO);
        tocCodigosConcluidosCancelados.add(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO);
        tocCodigosConcluidosCancelados.add(CodedValues.TOC_CONCLUSAO_CONTRATO);
        tocCodigosConcluidosCancelados.add(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO);
        tocCodigosConcluidosCancelados.add(CodedValues.TOC_TARIF_CANCELAMENTO_RESERVA);

        try {
            final StringBuilder query = new StringBuilder();

            // tb_tmp_bi_max_rad_data_origem //////////////////////////////////////////////////////////////////////////
            query.append("DROP TABLE IF EXISTS tb_tmp_bi_max_rad_data_origem");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE TABLE tb_tmp_bi_max_rad_data_origem (ade_codigo varchar(32) not null, max_rad_data datetime not null, tnt_codigo varchar(32) not null, primary key (ade_codigo)) ");
            query.append("select rad.ade_codigo_origem as ade_codigo, max(rad.rad_data) as max_rad_data, 'X' as tnt_codigo ");
            query.append("from tb_relacionamento_autorizacao rad ");
            query.append("where rad.tnt_codigo in ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("','").append(CodedValues.TNT_CONTROLE_COMPRA).append("') ");
            query.append("group by rad.ade_codigo_origem ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("UPDATE tb_tmp_bi_max_rad_data_origem tmp ");
            query.append("inner join tb_relacionamento_autorizacao rad on (tmp.ade_codigo = rad.ade_codigo_origem and rad.tnt_codigo in ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("','").append(CodedValues.TNT_CONTROLE_COMPRA).append("') and rad.rad_data = tmp.max_rad_data) ");
            query.append("set tmp.tnt_codigo = rad.tnt_codigo ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_tmp_bi_max_rad_data_destino /////////////////////////////////////////////////////////////////////////
            query.append("DROP TABLE IF EXISTS tb_tmp_bi_max_rad_data_destino");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE TABLE tb_tmp_bi_max_rad_data_destino (ade_codigo varchar(32) not null, max_rad_data datetime not null, tnt_codigo varchar(32) not null, primary key (ade_codigo)) ");
            query.append("select rad.ade_codigo_destino as ade_codigo, max(rad.rad_data) as max_rad_data, 'X' as tnt_codigo ");
            query.append("from tb_relacionamento_autorizacao rad ");
            query.append("where rad.tnt_codigo in ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("','").append(CodedValues.TNT_CONTROLE_COMPRA).append("') ");
            query.append("group by rad.ade_codigo_destino ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("UPDATE tb_tmp_bi_max_rad_data_destino tmp ");
            query.append("inner join tb_relacionamento_autorizacao rad on (tmp.ade_codigo = rad.ade_codigo_destino and rad.tnt_codigo in ('").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("','").append(CodedValues.TNT_CONTROLE_COMPRA).append("') and rad.rad_data = tmp.max_rad_data) ");
            query.append("set tmp.tnt_codigo = rad.tnt_codigo ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_tmp_bi_max_oca_data_exclusao ////////////////////////////////////////////////////////////////////////
            query.append("DROP TABLE IF EXISTS tb_tmp_bi_max_oca_data_exclusao");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE TABLE tb_tmp_bi_max_oca_data_exclusao (ade_codigo varchar(32) not null, max_oca_data datetime not null, toc_codigo varchar(32) not null, primary key (ade_codigo)) ");
            query.append("select oca.ade_codigo, max(oca.oca_data) as max_oca_data, 'X' as toc_codigo ");
            query.append("from tb_ocorrencia_autorizacao oca ");
            query.append("where oca.toc_codigo in ('").append(TextHelper.join(tocCodigosConcluidosCancelados, "','")).append("') ");
            query.append("group by oca.ade_codigo ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE INDEX idx_max_oca_data_exclusao ON tb_tmp_bi_max_oca_data_exclusao (ade_codigo, max_oca_data)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // Cria tabela auxiliar das ocorrências, pois se for muito grande o inner join vai demorar na tb_ocorrencia_autorizacao
            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_oca_exclusao_aux");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE TEMPORARY TABLE tb_tmp_oca_exclusao_aux (ade_codigo varchar(32) not null, oca_data datetime not null, toc_codigo varchar(32) not null)  ");
            query.append("select oca.ade_codigo, oca.oca_data, toc_codigo ");
            query.append("from tb_ocorrencia_autorizacao oca ");
            query.append("where oca.toc_codigo in ('").append(TextHelper.join(tocCodigosConcluidosCancelados, "','")).append("') ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE INDEX idx_oca_exclusao_aux ON tb_tmp_oca_exclusao_aux (ade_codigo, oca_data)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("UPDATE tb_tmp_bi_max_oca_data_exclusao tmp ");
            query.append("inner join tb_tmp_oca_exclusao_aux oca on (tmp.ade_codigo = oca.ade_codigo and oca.oca_data = tmp.max_oca_data) ");
            query.append("set tmp.toc_codigo = oca.toc_codigo ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_tmp_bi_contrato_origem //////////////////////////////////////////////////////////////////////////////
            // CRIA TABELA PARA DETERMINAR A ORIGEM DOS CONTRATOS
            query.append("DROP TABLE IF EXISTS tb_tmp_bi_contrato_origem");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE TABLE tb_tmp_bi_contrato_origem (ade_codigo varchar(32) not null, origem smallint not null, primary key (ade_codigo)) ");
            query.append("select ade.ade_codigo, min(case ");
            query.append(" coalesce(tmp.tnt_codigo, '0') ");
            query.append("   when '").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("' then 2 "); // RENEGOCIACAO
            query.append("   when '").append(CodedValues.TNT_CONTROLE_COMPRA).append("' then 3 "); // COMPRA
            query.append("   else 1 ");          // NOVO CONTRATO
            query.append(" end) as origem ");
            query.append("from tb_aut_desconto ade ");
            query.append("left outer join tb_tmp_bi_max_rad_data_destino tmp on (ade.ade_codigo = tmp.ade_codigo) ");
            query.append("group by ade.ade_codigo ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE INDEX idx_bi_contrato_origem ON tb_tmp_bi_contrato_origem (ade_codigo, origem)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_tmp_bi_contrato_termino /////////////////////////////////////////////////////////////////////////////
            // CRIA TABELA PARA DETERMINAR O TERMINO DOS CONTRATOS
            query.append("DROP TABLE IF EXISTS tb_tmp_bi_contrato_termino");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE TABLE tb_tmp_bi_contrato_termino (ade_codigo varchar(32) not null, termino smallint not null, primary key (ade_codigo)) ");
            query.append("select ade.ade_codigo, min(case ");
            query.append("  when (tmp2.toc_codigo = '").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("' and tmp1.tnt_codigo is null) then 1 "); // LIQUIDAÇÃO ANTECIPADA
            query.append("  when (tmp2.toc_codigo = '").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("' and tmp1.tnt_codigo = '").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("')   then 2 "); // RENEGOCIAÇÃO
            query.append("  when (tmp2.toc_codigo = '").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("' and tmp1.tnt_codigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("')   then 3 "); // COMPRA
            query.append("  when (tmp2.toc_codigo in ('").append(CodedValues.TOC_CONCLUSAO_CONTRATO).append("','").append(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO).append("'))                    then 4 "); // CONCLUSÃO
            query.append("  when (tmp2.toc_codigo in ('").append(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO).append("','").append(CodedValues.TOC_TARIF_CANCELAMENTO_RESERVA).append("'))                     then 5 "); // CANCELAMENTO
            query.append("  else 9 ");                                                          // 'N/D'
            query.append(" end) as termino ");
            query.append("from tb_aut_desconto ade ");
            query.append("left outer join tb_tmp_bi_max_rad_data_origem tmp1 on (ade.ade_codigo = tmp1.ade_codigo) ");
            query.append("left outer join tb_tmp_bi_max_oca_data_exclusao tmp2 on (ade.ade_codigo = tmp2.ade_codigo) ");
            query.append("group by ade.ade_codigo ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE INDEX idx_bi_contrato_termino ON tb_tmp_bi_contrato_termino (ade_codigo, termino)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // tb_tmp_bi_data_exclusao_rse ////////////////////////////////////////////////////////////////////////////
            // CRIA TABELA PARA APROXIMAR A DATA DE EXCLUSAO DOS SERVIDORES
            // UTILIZADA PARA IGNORAR PARCELAS REJEITADAS DE SERVIDORES EXCLUIDOS
            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_bi_data_exclusao_rse_1");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // Ocorrência de exclusão
            query.append("CREATE TEMPORARY TABLE tb_tmp_bi_data_exclusao_rse_1 (rse_codigo varchar(32) not null, data_excl datetime, primary key (rse_codigo)) ");
            query.append("select rse.rse_codigo, max(ors.ors_data) as data_excl ");
            query.append("from tb_registro_servidor rse ");
            query.append("inner join tb_ocorrencia_registro_ser ors on (rse.rse_codigo = ors.rse_codigo) ");
            query.append("where rse.srs_codigo IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("') ");
            query.append("and ors.toc_codigo = '").append(CodedValues.TOC_RSE_EXCLUSAO_POR_CARGA_MARGEM).append("' ");
            query.append("group by rse.rse_codigo");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_bi_data_exclusao_rse_2");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // Última parcela descontada
            query.append("CREATE TEMPORARY TABLE tb_tmp_bi_data_exclusao_rse_2 (rse_codigo varchar(32) not null, data_excl datetime, primary key (rse_codigo)) ");
            query.append("select rse.rse_codigo, max(prd.prd_data_realizado) as data_excl ");
            query.append("from tb_registro_servidor rse ");
            query.append("inner join tb_aut_desconto ade on (ade.rse_codigo = rse.rse_codigo) ");
            query.append("inner join tb_parcela_desconto prd on (ade.ade_codigo = prd.ade_codigo) ");
            query.append("where rse.srs_codigo IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("') ");
            query.append("and prd.spd_codigo in ('").append(CodedValues.SPD_LIQUIDADAFOLHA).append("', '").append(CodedValues.SPD_LIQUIDADAMANUAL).append("') ");
            query.append("group by rse.rse_codigo");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_bi_data_exclusao_rse_3");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // Último contrato incluído
            query.append("CREATE TEMPORARY TABLE tb_tmp_bi_data_exclusao_rse_3 (rse_codigo varchar(32) not null, data_excl datetime, primary key (rse_codigo)) ");
            query.append("select rse.rse_codigo, max(ade.ade_data) as data_excl ");
            query.append("from tb_registro_servidor rse ");
            query.append("inner join tb_aut_desconto ade on (ade.rse_codigo = rse.rse_codigo) ");
            query.append("where rse.srs_codigo IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("') ");
            query.append("group by rse.rse_codigo");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("DROP TABLE IF EXISTS tb_tmp_bi_data_exclusao_rse");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE TABLE tb_tmp_bi_data_exclusao_rse (rse_codigo varchar(32) not null, data_excl datetime not null, primary key (rse_codigo)) ");
            query.append("select rse.rse_codigo, coalesce(coalesce(coalesce(tmp1.data_excl, tmp2.data_excl), tmp3.data_excl), curdate()) as data_excl ");
            query.append("from tb_registro_servidor rse ");
            query.append("left outer join tb_tmp_bi_data_exclusao_rse_1 tmp1 on (tmp1.rse_codigo = rse.rse_codigo) ");
            query.append("left outer join tb_tmp_bi_data_exclusao_rse_2 tmp2 on (tmp2.rse_codigo = rse.rse_codigo) ");
            query.append("left outer join tb_tmp_bi_data_exclusao_rse_3 tmp3 on (tmp3.rse_codigo = rse.rse_codigo) ");
            query.append("where rse.srs_codigo IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("') ");
            query.append("group by rse.rse_codigo");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("DROP TABLE IF EXISTS tb_tmp_bi_dados_contratos");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CREATE TABLE tb_tmp_bi_dados_contratos (");
            query.append("  ade_codigo varchar(32) NOT NULL,");
            query.append("  sad_codigo smallint NOT NULL,");
            query.append("  cor_codigo varchar(32),");
            query.append("  ade_data datetime NOT NULL,");
            query.append("  ade_ano_mes_ini date NOT NULL,");
            query.append("  ade_prazo smallint NOT NULL,");
            query.append("  ade_prd_pagas int NOT NULL,");
            query.append("  ade_vlr decimal(13,2) NOT NULL,");
            query.append("  ade_vlr_total decimal(13,2) NOT NULL,");
            query.append("  ade_vlr_devido decimal(13,2) NOT NULL,");
            query.append("  ade_vlr_pago decimal(13,2) NOT NULL,");
            query.append("  ade_vlr_liquido decimal(13,2) NOT NULL,");
            query.append("  cft_vlr decimal(13,8),");
            query.append("  csa_codigo varchar(32) NOT NULL,");
            query.append("  org_codigo varchar(32) NOT NULL,");
            query.append("  svc_codigo varchar(32) NOT NULL,");
            query.append("  nse_codigo varchar(32),");
            query.append("  cnv_cod_verba varchar(32) NOT NULL,");
            query.append("  rse_codigo varchar(32) NOT NULL,");
            query.append("  rse_tipo varchar(255) NOT NULL,");
            query.append("  rse_municipio_lotacao varchar(40) NOT NULL,");
            query.append("  srs_codigo varchar(32) NOT NULL,");
            query.append("  ser_idade smallint NOT NULL,");
            query.append("  ser_sexo smallint NOT NULL,");
            query.append("  crs_identificador varchar(40) NOT NULL,");
            query.append("  pos_identificador varchar(40) NOT NULL,");
            query.append("  pse_int_folha char(1) NOT NULL,");
            query.append("  PRIMARY KEY (ade_codigo),");
            query.append("  KEY bi_dados_contratos_csa (csa_codigo),");
            query.append("  KEY bi_dados_contratos_cor (cor_codigo),");
            query.append("  KEY bi_dados_contratos_svc (svc_codigo),");
            query.append("  KEY bi_dados_contratos_org (org_codigo),");
            query.append("  KEY bi_dados_contratos_sad (sad_codigo),");
            query.append("  KEY bi_dados_contratos_prz (ade_prazo),");
            query.append("  KEY bi_dados_contratos_rst (rse_tipo),");
            query.append("  KEY bi_dados_contratos_srs (srs_codigo),");
            query.append("  KEY bi_dados_contratos_sx  (ser_sexo),");
            query.append("  KEY bi_dados_contratos_ida (ser_idade),");
            query.append("  KEY bi_dados_contratos_crs (crs_identificador),");
            query.append("  KEY bi_dados_contratos_pos (pos_identificador),");
            query.append("  KEY bi_dados_contratos_cnv (cnv_cod_verba),");
            query.append("  KEY bi_dados_contratos_lot (rse_municipio_lotacao)");
            query.append(")");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_tmp_bi_dados_contratos (");
            query.append("  ade_codigo, ");
            query.append("  sad_codigo, ");
            query.append("  cor_codigo, ");
            query.append("  ade_data, ");
            query.append("  ade_ano_mes_ini, ");
            query.append("  ade_prazo, ");
            query.append("  ade_prd_pagas, ");
            query.append("  ade_vlr, ");
            query.append("  ade_vlr_total, ");
            query.append("  ade_vlr_devido, ");
            query.append("  ade_vlr_pago, ");
            query.append("  ade_vlr_liquido, ");
            query.append("  cft_vlr, ");
            query.append("  csa_codigo, ");
            query.append("  org_codigo, ");
            query.append("  svc_codigo, ");
            query.append("  nse_codigo, ");
            query.append("  cnv_cod_verba, ");
            query.append("  rse_codigo, ");
            query.append("  rse_tipo, ");
            query.append("  rse_municipio_lotacao, ");
            query.append("  srs_codigo, ");
            query.append("  ser_idade, ");
            query.append("  ser_sexo, ");
            query.append("  crs_identificador, ");
            query.append("  pos_identificador, ");
            query.append("  pse_int_folha");
            query.append(") ");
            query.append("SELECT ");
            query.append("  ade.ade_codigo, ");
            query.append("  cast(ade.sad_codigo as unsigned int), ");
            query.append("  ade.cor_codigo, ");
            query.append("  ade.ade_data, ");
            query.append("  ade.ade_ano_mes_ini, ");
            query.append("  least(coalesce(ade.ade_prazo, 0), 100), ");
            query.append("  coalesce(ade.ade_prd_pagas, 0), ");
            query.append("  ade.ade_vlr, ");
            query.append("  ade.ade_vlr * coalesce(ade.ade_prazo, 1), ");
            query.append("  ade.ade_vlr * coalesce(ade.ade_prazo - coalesce(ade.ade_prd_pagas, 0), 1), ");
            query.append("  ade.ade_vlr * coalesce(ade.ade_prd_pagas, 0), ");
            query.append("  coalesce(ade.ade_vlr_liquido, 0), ");
            query.append("  cft.cft_vlr, ");
            query.append("  csa.csa_codigo, ");
            query.append("  org.org_codigo, ");
            query.append("  svc.svc_codigo, ");
            query.append("  svc.nse_codigo, ");
            query.append("  coalesce(cnv.cnv_cod_verba, '").append(naoDefinido).append("'), ");
            query.append("  rse.rse_codigo, ");
            query.append("  coalesce(nullif(rse.rse_tipo, ''), '").append(naoDefinido).append("'), ");
            query.append("  coalesce(upper(nullif(trim(rse.rse_municipio_lotacao), '')), '").append(naoDefinido).append("'), ");
            query.append("  rse.srs_codigo, ");
            query.append("  coalesce(least(100, timestampdiff(year, ser.ser_data_nasc, curdate())), 0), ");
            query.append("  case when ser.ser_sexo = 'M' then 1 when ser.ser_sexo = 'F' then 2 else 9 end, ");
            query.append("  coalesce(crs.crs_identificador, '").append(naoDefinido).append("'), ");
            query.append("  coalesce(pos.pos_identificador, '").append(naoDefinido).append("'), ");
            query.append("  coalesce(pseIntFolha.pse_vlr, '").append(CodedValues.INTEGRA_FOLHA_SIM).append("') ");
            query.append("FROM tb_aut_desconto ade use index () ");
            query.append("INNER JOIN tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) ");
            query.append("INNER JOIN tb_servidor ser on (rse.ser_codigo = ser.ser_codigo) ");
            query.append("INNER JOIN tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("INNER JOIN tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) ");
            query.append("INNER JOIN tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) ");
            query.append("INNER JOIN tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
            query.append("LEFT OUTER JOIN tb_coeficiente_desconto cde on (ade.ade_codigo = cde.ade_codigo) ");
            query.append("LEFT OUTER JOIN tb_coeficiente cft on (cde.cft_codigo = cft.cft_codigo) ");
            query.append("LEFT OUTER JOIN tb_cargo_registro_servidor crs on (crs.crs_codigo = rse.crs_codigo) ");
            query.append("LEFT OUTER JOIN tb_posto_registro_servidor pos on (pos.pos_codigo = rse.pos_codigo) ");
            query.append("LEFT OUTER JOIN tb_param_svc_consignante pseIntFolha on (svc.svc_codigo = pseIntFolha.svc_codigo and pseIntFolha.tps_codigo = '").append(CodedValues.TPS_INTEGRA_FOLHA).append("')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.persistence.dao.ConsigBIDAO#atualizarFatoContrato()
     */
    @Override
    public void atualizarFatoContrato(boolean populaDados) throws DAOException {
        final String naoDefinido = ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null);

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();
            query.append("DELETE FROM tb_dados_fato_contrato");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("DELETE FROM tb_fato_contrato");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_fato_contrato (dte_codigo, dca_codigo, dse_codigo, dlo_codigo, dsc_codigo, dpc_codigo, dvc_codigo, doc_codigo, dtc_codigo, dcs_codigo, dss_codigo, dsx_codigo, dis_codigo, dcr_codigo, dpr_codigo, dls_codigo, dve_codigo, fac_qtd, fac_vlr_mes, fac_vlr_devido, fac_vlr_total, fac_vlr_liberado, fac_taxa_juros) ");
            query.append("SELECT date_format(tmp.ade_data, '%Y%m%d'), dcaBI.dca_codigo, dseBI.dse_codigo, dloBI.dlo_codigo, ");
            query.append("dscBI.dsc_codigo, dpcBI.dpc_codigo, dvcBI.dvc_codigo, tmpOri.origem, tmpTer.termino, dcsBI.dcs_codigo, dssBI.dss_codigo, dsxBI.dsx_codigo, disBI.dis_codigo, dcrBI.dcr_codigo, dprBI.dpr_codigo, dlsBI.dls_codigo, dveBI.dve_codigo, ");
            query.append("  count(*), sum(tmp.ade_vlr), "); // VALOR MENSAL DAS PARCELAS
            query.append("    sum(case when (tmp.nse_codigo = '").append(CodedValues.NSE_CARTAO).append("' and tmp.pse_int_folha = '0') then tmp.ade_vlr "); // CARTAO RESERVA
            query.append("             when tmp.sad_codigo not in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("') ");
            query.append("             then ade_vlr_devido "); // VALOR A SER PAGO
            query.append("             else ade_vlr_pago "); // VALOR JA PAGO
            query.append("    end), ");
            query.append("    sum(tmp.ade_vlr_total), "); // VALOR TOTAL CONTRATO
            query.append("    sum(tmp.ade_vlr_liquido), "); // VALOR LIBERADO
            query.append("    coalesce(avg(tmp.cft_vlr / 100.00), 0) "); // TAXA JUROS
            query.append("FROM tb_tmp_bi_dados_contratos tmp ");
            query.append("INNER JOIN tb_tmp_bi_contrato_origem tmpOri on (tmpOri.ade_codigo = tmp.ade_codigo) ");
            query.append("INNER JOIN tb_tmp_bi_contrato_termino tmpTer on (tmpTer.ade_codigo = tmp.ade_codigo) ");
            query.append("INNER JOIN tb_dimensao_consignataria dcaBI on (tmp.csa_codigo = dcaBI.dca_csa_codigo and coalesce(tmp.cor_codigo, tmp.csa_codigo) = dcaBI.dca_cor_codigo) ");
            query.append("INNER JOIN tb_dimensao_servico dseBI on (tmp.svc_codigo = dseBI.dse_svc_codigo) ");
            query.append("INNER JOIN tb_dimensao_localizacao dloBI on (tmp.org_codigo = dloBI.dlo_org_codigo) ");
            query.append("INNER JOIN tb_dimensao_status_contrato dscBI on (tmp.sad_codigo = dscBI.dsc_codigo) ");
            query.append("INNER JOIN tb_dimensao_prazo_contrato dpcBI on (tmp.ade_prazo = dpcBI.dpc_prazo) ");
            query.append("INNER JOIN tb_dimensao_valor_contrato dvcBI on (tmp.ade_vlr_total between dvcBI.dvc_vlr_ini and dvcBI.dvc_vlr_fim) ");
            query.append("INNER JOIN tb_dimensao_categoria_servidor dcsBI on (tmp.rse_tipo = dcsBI.dcs_descricao) ");
            query.append("INNER JOIN tb_dimensao_status_servidor dssBI on (CAST(tmp.srs_codigo AS UNSIGNED INT) = dssBI.dss_codigo) ");
            query.append("INNER JOIN tb_dimensao_sexo_servidor dsxBI on (tmp.ser_sexo = dsxBI.dsx_codigo) ");
            query.append("INNER JOIN tb_dimensao_idade_servidor disBI on (tmp.ser_idade = disBI.dis_codigo) ");
            query.append("INNER JOIN tb_dimensao_cargo_servidor dcrBI on (tmp.crs_identificador = dcrBI.dcr_identificador) ");
            query.append("INNER JOIN tb_dimensao_posto_servidor dprBI on (tmp.pos_identificador = dprBI.dpr_identificador) ");
            query.append("INNER JOIN tb_dimensao_verba_convenio dveBI on (tmp.cnv_cod_verba = dveBI.dve_descricao) ");
            query.append("INNER JOIN tb_dimensao_lotacao_servidor dlsBI ON (tmp.rse_municipio_lotacao = dlsBI.dls_descricao) ");
            query.append("WHERE NOT (tmp.nse_codigo = '").append(CodedValues.NSE_CARTAO).append("' and tmp.pse_int_folha = '1') "); // EXCLUI CARTAO LANCAMENTO
            query.append("GROUP BY date_format(tmp.ade_data, '%Y%m%d'), dcaBI.dca_codigo, dseBI.dse_codigo, dloBI.dlo_codigo, dscBI.dsc_codigo, dpcBI.dpc_codigo, dvcBI.dvc_codigo, tmpOri.origem, tmpTer.termino, dcsBI.dcs_codigo, dssBI.dss_codigo, dsxBI.dsx_codigo, disBI.dis_codigo, dcrBI.dcr_codigo, dprBI.dpr_codigo, dlsBI.dls_codigo, dveBI.dve_codigo ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            if (populaDados) {
                query.append("INSERT INTO tb_dados_fato_contrato (FAC_CODIGO, DFC_NOME, DFC_CPF, DFC_MATRICULA, DFC_NUMERO, DFC_IDENTIFICADOR, DFC_INDICE, DFC_VALOR, DFC_VALOR_LIBERADO, DFC_PRAZO, DFC_PAGAS, DFC_TAXA, DFC_DATA, DFC_DATA_INI, DFC_DATA_FIM, DFC_STATUS) ");
                query.append("SELECT fac.FAC_CODIGO, ser.SER_NOME, ser.SER_CPF, rse.RSE_MATRICULA, ade.ADE_NUMERO, ade.ADE_IDENTIFICADOR, ade.ADE_INDICE, ade.ADE_VLR, ade.ADE_VLR_LIQUIDO, ade.ADE_PRAZO, ade.ADE_PRD_PAGAS, COALESCE(cft.CFT_VLR, ade.ADE_TAXA_JUROS), ade.ADE_DATA, ade.ADE_ANO_MES_INI, ade.ADE_ANO_MES_FIM, sad.SAD_DESCRICAO ");
                query.append("FROM tb_fato_contrato fac ");
                query.append("INNER JOIN tb_dimensao_consignataria dca ON (fac.dca_codigo = dca.dca_codigo) ");
                query.append("INNER JOIN tb_consignataria csa ON (csa.csa_codigo = dca.dca_csa_codigo) ");
                query.append("INNER JOIN tb_dimensao_servico dse ON (fac.dse_codigo = dse.dse_codigo) ");
                query.append("INNER JOIN tb_servico svc ON (svc.svc_codigo = dse.dse_svc_codigo) ");
                query.append("INNER JOIN tb_dimensao_localizacao dlo ON (fac.dlo_codigo = dlo.dlo_codigo) ");
                query.append("INNER JOIN tb_orgao org ON (org.org_codigo = dlo.dlo_org_codigo) ");
                query.append("INNER JOIN tb_dimensao_verba_convenio dve ON (fac.dve_codigo = dve.dve_codigo) ");
                query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_cod_verba = dve.dve_descricao AND cnv.org_codigo = org.org_codigo AND cnv.csa_codigo = csa.csa_codigo AND cnv.svc_codigo = svc.svc_codigo) ");
                query.append("INNER JOIN tb_verba_convenio vco ON (cnv.cnv_codigo = vco.cnv_codigo) ");
                query.append("INNER JOIN tb_dimensao_cargo_servidor dcr ON (fac.dcr_codigo = dcr.dcr_codigo) ");
                query.append("INNER JOIN tb_dimensao_posto_servidor dpr ON (fac.dpr_codigo = dpr.dpr_codigo) ");
                query.append("INNER JOIN tb_dimensao_categoria_servidor dcs ON (fac.dcs_codigo = dcs.dcs_codigo) ");
                query.append("INNER JOIN tb_dimensao_status_servidor dss ON (fac.dss_codigo = dss.dss_codigo) ");
                query.append("INNER JOIN tb_dimensao_sexo_servidor dsx ON (fac.dsx_codigo = dsx.dsx_codigo) ");
                query.append("INNER JOIN tb_dimensao_idade_servidor dis ON (fac.dis_codigo = dis.dis_codigo) ");
                query.append("INNER JOIN tb_dimensao_lotacao_servidor dls ON (fac.dls_codigo = dls.dls_codigo) ");
                query.append("INNER JOIN tb_dimensao_status_contrato dsc ON (fac.dsc_codigo = dsc.dsc_codigo) ");
                query.append("INNER JOIN tb_dimensao_prazo_contrato dpc ON (fac.dpc_codigo = dpc.dpc_codigo) ");
                query.append("INNER JOIN tb_dimensao_valor_contrato dvc ON (fac.dvc_codigo = dvc.dvc_codigo) ");
                query.append("INNER JOIN tb_aut_desconto ade ON (vco.vco_codigo = ade.vco_codigo) ");
                query.append("INNER JOIN tb_status_autorizacao_desconto sad ON (ade.sad_codigo = sad.sad_codigo) ");
                query.append("INNER JOIN tb_registro_servidor rse ON (ade.rse_codigo = rse.rse_codigo AND org.org_codigo = rse.org_codigo) ");
                query.append("INNER JOIN tb_servidor ser ON (rse.ser_codigo = ser.ser_codigo) ");
                query.append("INNER JOIN tb_tmp_bi_contrato_origem tmpOri on (tmpOri.ade_codigo = ade.ade_codigo AND tmpOri.origem = fac.doc_codigo) ");
                query.append("INNER JOIN tb_tmp_bi_contrato_termino tmpTer on (tmpTer.ade_codigo = ade.ade_codigo AND tmpTer.termino = fac.dtc_codigo) ");
                query.append("LEFT JOIN tb_correspondente cor ON (cor.csa_codigo = csa.csa_codigo AND cor.cor_codigo = dca.dca_cor_codigo) ");
                query.append("LEFT JOIN tb_cargo_registro_servidor crs ON (crs.crs_identificador = dcr.dcr_identificador) ");
                query.append("LEFT JOIN tb_posto_registro_servidor pos ON (pos.pos_identificador = dpr.dpr_identificador) ");
                query.append("LEFT JOIN tb_coeficiente_desconto cde ON (ade.ade_codigo = cde.ade_codigo) ");
                query.append("LEFT JOIN tb_coeficiente cft ON (cde.cft_codigo = cft.cft_codigo) ");
                query.append("LEFT JOIN tb_param_svc_consignante pseIntFolha on (svc.svc_codigo = pseIntFolha.svc_codigo and pseIntFolha.tps_codigo = '").append(CodedValues.TPS_INTEGRA_FOLHA).append("') ");
                query.append("WHERE NOT (svc.nse_codigo = '").append(CodedValues.NSE_CARTAO).append("' and coalesce(pseIntFolha.pse_vlr, '").append(CodedValues.INTEGRA_FOLHA_SIM).append("') = '").append(CodedValues.INTEGRA_FOLHA_SIM).append("') ");
                query.append("AND coalesce(ade.cor_codigo, '").append(naoDefinido).append("') = coalesce(cor.cor_codigo, '").append(naoDefinido).append("') ");
                query.append("AND coalesce(rse.crs_codigo, '").append(naoDefinido).append("') = coalesce(crs.crs_codigo, '").append(naoDefinido).append("') ");
                query.append("AND coalesce(rse.pos_codigo, '").append(naoDefinido).append("') = coalesce(pos.pos_codigo, '").append(naoDefinido).append("') ");
                query.append("AND ade.sad_codigo = dsc.dsc_codigo ");
                query.append("AND date_format(ade.ade_data, '%Y%m%d') = fac.dte_codigo ");
                query.append("AND least(coalesce(ade.ade_prazo, 0), 100) = dpc.dpc_prazo ");
                query.append("AND (ade.ade_vlr * coalesce(ade.ade_prazo, 1)) between dvc.dvc_vlr_ini and dvc.dvc_vlr_fim ");
                query.append("AND rse.srs_codigo = dss.dss_codigo ");
                query.append("AND coalesce(nullif(rse.rse_tipo, ''), '").append(naoDefinido).append("') = dcs.dcs_descricao ");
                query.append("AND coalesce(upper(nullif(trim(rse.rse_municipio_lotacao), '')), '").append(naoDefinido).append("') = dls.dls_descricao ");
                query.append("AND (case when ser.ser_sexo = 'M' then 1 when ser.ser_sexo = 'F' then 2 else 9 end) = dsx.dsx_codigo ");
                query.append("AND coalesce(least(100, timestampdiff(year, ser.ser_data_nasc, curdate())), 0) = dis.dis_codigo ");
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);
                query.setLength(0);
            }
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.persistence.dao.ConsigBIDAO#atualizarFatoParcela()
     */
    @Override
    public void atualizarFatoParcela(boolean populaDados) throws DAOException {
        final String naoDefinido = ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null);

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();
            query.append("DELETE FROM tb_dados_fato_parcela");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("DELETE FROM tb_fato_parcela");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_fato_parcela (dte_codigo_parcela, dte_codigo_contrato, dca_codigo, dse_codigo, dlo_codigo, dsc_codigo, dpc_codigo, dvc_codigo, dvp_codigo, doc_codigo, dtc_codigo, dcs_codigo, dss_codigo, dsx_codigo, dis_codigo, dcr_codigo, dpr_codigo, dls_codigo, dve_codigo, fap_qtd, fap_qtd_pago, fap_qtd_rejeitado, fap_vlr_previsto, fap_vlr_realizado) ");
            query.append("SELECT date_format(prd.prd_data_desconto, '%Y%m%d'), date_format(tmp.ade_ano_mes_ini, '%Y%m%d'), dcaBI.dca_codigo, dseBI.dse_codigo, dloBI.dlo_codigo, dscBI.dsc_codigo, dpcBI.dpc_codigo, dvcBI.dvc_codigo, dvpBI.dvp_codigo, tmpOri.origem, tmpTer.termino, dcsBI.dcs_codigo, dssBI.dss_codigo, dsxBI.dsx_codigo, disBI.dis_codigo, dcrBI.dcr_codigo, dprBI.dpr_codigo, dlsBI.dls_codigo, dveBI.dve_codigo, ");
            query.append("  count(*), ");                                                         // fap_qtd
            query.append("  sum(case when prd.spd_codigo in ('").append(CodedValues.SPD_LIQUIDADAFOLHA).append("', '").append(CodedValues.SPD_LIQUIDADAMANUAL).append("') then 1 else 0 end), "); // fap_qtd_pago
            query.append("  sum(case when prd.spd_codigo not in ('").append(CodedValues.SPD_LIQUIDADAFOLHA).append("', '").append(CodedValues.SPD_LIQUIDADAMANUAL).append("') then 1 else 0 end), "); // fap_qtd_rejeitado
            query.append("  sum(prd.prd_vlr_previsto), ");                                        // fap_vlr_previsto
            query.append("  sum(case when prd.spd_codigo in ('").append(CodedValues.SPD_LIQUIDADAFOLHA).append("', '").append(CodedValues.SPD_LIQUIDADAMANUAL).append("') ");
            query.append("           then coalesce(prd.prd_vlr_realizado, prd.prd_vlr_previsto) ");
            query.append("           else coalesce(prd.prd_vlr_realizado, 0) ");
            query.append("      end) ");  // fap_vlr_realizado
            query.append("FROM tb_parcela_desconto prd use index () ");
            query.append("INNER JOIN tb_tmp_bi_dados_contratos tmp use index (primary) on (prd.ade_codigo = tmp.ade_codigo) ");
            query.append("INNER JOIN tb_tmp_bi_contrato_origem tmpOri on (tmpOri.ade_codigo = tmp.ade_codigo) ");
            query.append("INNER JOIN tb_tmp_bi_contrato_termino tmpTer on (tmpTer.ade_codigo = tmp.ade_codigo) ");
            query.append("LEFT OUTER JOIN tb_tmp_bi_data_exclusao_rse tmpExc on (tmp.rse_codigo = tmpExc.rse_codigo) ");
            query.append("INNER JOIN tb_dimensao_consignataria dcaBI on (tmp.csa_codigo = dcaBI.dca_csa_codigo and coalesce(tmp.cor_codigo, tmp.csa_codigo) = dcaBI.dca_cor_codigo) ");
            query.append("INNER JOIN tb_dimensao_servico dseBI on (tmp.svc_codigo = dseBI.dse_svc_codigo) ");
            query.append("INNER JOIN tb_dimensao_localizacao dloBI on (tmp.org_codigo = dloBI.dlo_org_codigo) ");
            query.append("INNER JOIN tb_dimensao_status_contrato dscBI on (tmp.sad_codigo = dscBI.dsc_codigo) ");
            query.append("INNER JOIN tb_dimensao_prazo_contrato dpcBI on (tmp.ade_prazo = dpcBI.dpc_prazo) ");
            query.append("INNER JOIN tb_dimensao_valor_contrato dvcBI on (tmp.ade_vlr_total between dvcBI.dvc_vlr_ini and dvcBI.dvc_vlr_fim) ");
            query.append("INNER JOIN tb_dimensao_valor_parcela dvpBI on (prd.prd_vlr_previsto between dvpBI.dvp_vlr_ini and dvpBI.dvp_vlr_fim) ");
            query.append("INNER JOIN tb_dimensao_categoria_servidor dcsBI on (tmp.rse_tipo = dcsBI.dcs_descricao) ");
            query.append("INNER JOIN tb_dimensao_status_servidor dssBI on ((CASE WHEN tmp.srs_codigo IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("') AND tmpExc.data_excl < prd.prd_data_realizado THEN 3 WHEN tmp.srs_codigo = '").append(CodedValues.SRS_BLOQUEADO).append("' THEN 2 ELSE 1 END) = dssBI.dss_codigo) ");
            query.append("INNER JOIN tb_dimensao_sexo_servidor dsxBI on (tmp.ser_sexo = dsxBI.dsx_codigo) ");
            query.append("INNER JOIN tb_dimensao_idade_servidor disBI on (tmp.ser_idade = disBI.dis_codigo) ");
            query.append("INNER JOIN tb_dimensao_cargo_servidor dcrBI on (tmp.crs_identificador = dcrBI.dcr_identificador) ");
            query.append("INNER JOIN tb_dimensao_posto_servidor dprBI on (tmp.pos_identificador = dprBI.dpr_identificador) ");
            query.append("INNER JOIN tb_dimensao_verba_convenio dveBI on (tmp.cnv_cod_verba = dveBI.dve_descricao) ");
            query.append("INNER JOIN tb_dimensao_lotacao_servidor dlsBI ON (tmp.rse_municipio_lotacao = dlsBI.dls_descricao) ");
            query.append("WHERE tmp.pse_int_folha = '1' "); // INTEGRA FOLHA (POIS PODE EXISTIR RUIDO DE CONTRATOS QUE NAO DEVERIAM IR PARA A FOLHA)
            query.append("GROUP BY date_format(prd.prd_data_desconto, '%Y%m%d'), date_format(tmp.ade_ano_mes_ini, '%Y%m%d'), dcaBI.dca_codigo, dseBI.dse_codigo, dloBI.dlo_codigo, dscBI.dsc_codigo, dpcBI.dpc_codigo, dvcBI.dvc_codigo, dvpBI.dvp_codigo, tmpOri.origem, tmpTer.termino, dcsBI.dcs_codigo, dssBI.dss_codigo, dsxBI.dsx_codigo, disBI.dis_codigo, dcrBI.dcr_codigo, dprBI.dpr_codigo, dlsBI.dls_codigo, dveBI.dve_codigo ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            if (populaDados) {
                query.append("INSERT INTO tb_dados_fato_parcela (FAP_CODIGO, DFP_NOME, DFP_CPF, DFP_MATRICULA, DFP_NUMERO, DFP_IDENTIFICADOR, DFP_INDICE, DFP_VALOR, DFP_VALOR_LIBERADO, DFP_PRAZO, DFP_PAGAS, DFP_NUMERO_PARCELA, DFP_PERIODO, DFP_VALOR_PREVISTO, DFP_VALOR_REALIZADO, DFP_DATA_REALIZADO, DFP_STATUS_PARCELA) ");
                query.append("SELECT fap.FAP_CODIGO, ser.SER_NOME, ser.SER_CPF, rse.RSE_MATRICULA, ade.ADE_NUMERO, ade.ADE_IDENTIFICADOR, ade.ADE_INDICE, ade.ADE_VLR, ade.ADE_VLR_LIQUIDO, ade.ADE_PRAZO, ade.ADE_PRD_PAGAS, prd.PRD_NUMERO, prd.PRD_DATA_DESCONTO, prd.PRD_VLR_PREVISTO, prd.PRD_VLR_REALIZADO, prd.PRD_DATA_REALIZADO, spd.SPD_DESCRICAO ");
                query.append("FROM tb_fato_parcela fap ");
                query.append("STRAIGHT_JOIN tb_dimensao_consignataria dca ON (fap.dca_codigo = dca.dca_codigo) ");
                query.append("STRAIGHT_JOIN tb_consignataria csa ON (csa.csa_codigo = dca.dca_csa_codigo) ");
                query.append("STRAIGHT_JOIN tb_dimensao_servico dse ON (fap.dse_codigo = dse.dse_codigo) ");
                query.append("STRAIGHT_JOIN tb_servico svc ON (svc.svc_codigo = dse.dse_svc_codigo) ");
                query.append("STRAIGHT_JOIN tb_dimensao_localizacao dlo ON (fap.dlo_codigo = dlo.dlo_codigo) ");
                query.append("STRAIGHT_JOIN tb_orgao org ON (org.org_codigo = dlo.dlo_org_codigo) ");
                query.append("STRAIGHT_JOIN tb_dimensao_verba_convenio dve ON (fap.dve_codigo = dve.dve_codigo) ");
                query.append("STRAIGHT_JOIN tb_convenio cnv ON (cnv.cnv_cod_verba = dve.dve_descricao AND cnv.org_codigo = org.org_codigo AND cnv.csa_codigo = csa.csa_codigo AND cnv.svc_codigo = svc.svc_codigo) ");
                query.append("STRAIGHT_JOIN tb_verba_convenio vco ON (cnv.cnv_codigo = vco.cnv_codigo) ");
                query.append("STRAIGHT_JOIN tb_dimensao_cargo_servidor dcr ON (fap.dcr_codigo = dcr.dcr_codigo) ");
                query.append("STRAIGHT_JOIN tb_dimensao_posto_servidor dpr ON (fap.dpr_codigo = dpr.dpr_codigo) ");
                query.append("STRAIGHT_JOIN tb_dimensao_categoria_servidor dcs ON (fap.dcs_codigo = dcs.dcs_codigo) ");
                query.append("STRAIGHT_JOIN tb_dimensao_status_servidor dss ON (fap.dss_codigo = dss.dss_codigo) ");
                query.append("STRAIGHT_JOIN tb_dimensao_sexo_servidor dsx ON (fap.dsx_codigo = dsx.dsx_codigo) ");
                query.append("STRAIGHT_JOIN tb_dimensao_idade_servidor dis ON (fap.dis_codigo = dis.dis_codigo) ");
                query.append("STRAIGHT_JOIN tb_dimensao_lotacao_servidor dls ON (fap.dls_codigo = dls.dls_codigo) ");
                query.append("STRAIGHT_JOIN tb_dimensao_status_contrato dsc ON (fap.dsc_codigo = dsc.dsc_codigo) ");
                query.append("STRAIGHT_JOIN tb_dimensao_prazo_contrato dpc ON (fap.dpc_codigo = dpc.dpc_codigo) ");
                query.append("STRAIGHT_JOIN tb_dimensao_valor_contrato dvc ON (fap.dvc_codigo = dvc.dvc_codigo) ");
                query.append("STRAIGHT_JOIN tb_dimensao_valor_parcela dvp ON (fap.dvp_codigo = dvp.dvp_codigo) ");
                query.append("STRAIGHT_JOIN tb_aut_desconto ade ON (vco.vco_codigo = ade.vco_codigo) ");
                query.append("STRAIGHT_JOIN tb_registro_servidor rse ON (ade.rse_codigo = rse.rse_codigo AND org.org_codigo = rse.org_codigo) ");
                query.append("STRAIGHT_JOIN tb_servidor ser ON (rse.ser_codigo = ser.ser_codigo) ");
                query.append("STRAIGHT_JOIN tb_tmp_bi_contrato_origem tmpOri on (tmpOri.ade_codigo = ade.ade_codigo AND tmpOri.origem = fap.doc_codigo) ");
                query.append("STRAIGHT_JOIN tb_tmp_bi_contrato_termino tmpTer on (tmpTer.ade_codigo = ade.ade_codigo AND tmpTer.termino = fap.dtc_codigo) ");
                query.append("STRAIGHT_JOIN tb_parcela_desconto prd ON (ade.ade_codigo = prd.ade_codigo) ");
                query.append("STRAIGHT_JOIN tb_status_parcela_desconto spd ON (prd.spd_codigo = spd.spd_codigo) ");
                query.append("LEFT JOIN tb_correspondente cor ON (cor.csa_codigo = csa.csa_codigo AND cor.cor_codigo = dca.dca_cor_codigo) ");
                query.append("LEFT JOIN tb_cargo_registro_servidor crs ON (crs.crs_identificador = dcr.dcr_identificador) ");
                query.append("LEFT JOIN tb_posto_registro_servidor pos ON (pos.pos_identificador = dpr.dpr_identificador) ");
                query.append("LEFT JOIN tb_coeficiente_desconto cde ON (ade.ade_codigo = cde.ade_codigo) ");
                query.append("LEFT JOIN tb_coeficiente cft ON (cde.cft_codigo = cft.cft_codigo) ");
                query.append("LEFT JOIN tb_tmp_bi_data_exclusao_rse tmpExc on (rse.rse_codigo = tmpExc.rse_codigo) ");
                query.append("LEFT JOIN tb_param_svc_consignante pseIntFolha on (svc.svc_codigo = pseIntFolha.svc_codigo and pseIntFolha.tps_codigo = '").append(CodedValues.TPS_INTEGRA_FOLHA).append("') ");
                query.append("WHERE coalesce(pseIntFolha.pse_vlr, '").append(CodedValues.INTEGRA_FOLHA_SIM).append("') = '").append(CodedValues.INTEGRA_FOLHA_SIM).append("' ");
                query.append("AND coalesce(ade.cor_codigo, '").append(naoDefinido).append("') = coalesce(cor.cor_codigo, '").append(naoDefinido).append("') ");
                query.append("AND coalesce(rse.crs_codigo, '").append(naoDefinido).append("') = coalesce(crs.crs_codigo, '").append(naoDefinido).append("') ");
                query.append("AND coalesce(rse.pos_codigo, '").append(naoDefinido).append("') = coalesce(pos.pos_codigo, '").append(naoDefinido).append("') ");
                query.append("AND ade.sad_codigo = dsc.dsc_codigo ");
                query.append("AND date_format(ade.ade_ano_mes_ini, '%Y%m%d') = fap.dte_codigo_contrato ");
                query.append("AND date_format(prd.prd_data_desconto, '%Y%m%d') = fap.dte_codigo_parcela ");
                query.append("AND least(coalesce(ade.ade_prazo, 0), 100) = dpc.dpc_prazo ");
                query.append("AND (ade.ade_vlr * coalesce(ade.ade_prazo, 1)) between dvc.dvc_vlr_ini and dvc.dvc_vlr_fim ");
                query.append("AND prd.prd_vlr_previsto between dvp.dvp_vlr_ini and dvp.dvp_vlr_fim ");
                query.append("AND (CASE WHEN rse.srs_codigo IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("') AND tmpExc.data_excl < prd.prd_data_realizado THEN 3 WHEN rse.srs_codigo = '").append(CodedValues.SRS_BLOQUEADO).append("' THEN 2 ELSE 1 END) = dss.dss_codigo ");
                query.append("AND coalesce(nullif(rse.rse_tipo, ''), '").append(naoDefinido).append("') = dcs.dcs_descricao ");
                query.append("AND coalesce(upper(nullif(trim(rse.rse_municipio_lotacao), '')), '").append(naoDefinido).append("') = dls.dls_descricao ");
                query.append("AND (case when ser.ser_sexo = 'M' then 1 when ser.ser_sexo = 'F' then 2 else 9 end) = dsx.dsx_codigo ");
                query.append("AND coalesce(least(100, timestampdiff(year, ser.ser_data_nasc, curdate())), 0) = dis.dis_codigo ");
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);
                query.setLength(0);
            }
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /* (non-Javadoc)
     * @see com.zetra.econsig.persistence.dao.ConsigBIDAO#atualizarFatoMargem()
     */
    @Override
    public void atualizarFatoMargem(boolean populaDados) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();

            query.append("DELETE FROM tb_dados_fato_margem");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("DELETE FROM tb_fato_margem");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            for (int marCodigo = 1; marCodigo <= 3; marCodigo++) {
                query.append(montaQueryFatoMargem(marCodigo));
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);
                query.setLength(0);

                if (populaDados) {
                    query.append(montaQueryDadosFatoMargem(marCodigo));
                    LOG.trace(query.toString());
                    jdbc.update(query.toString(), queryParams);
                    query.setLength(0);
                }
            }
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    protected String montaQueryFatoMargem(int marCodigo) {
        final String campoMargemRest = getCampoMargemRest(marCodigo);
        final String naoDefinido = ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null);

        final StringBuilder query = new StringBuilder();

        query.append("INSERT INTO tb_fato_margem (dte_codigo, dco_codigo, dlo_codigo, dcs_codigo, dsx_codigo, dis_codigo, dtm_codigo, dcr_codigo, dpr_codigo, dls_codigo, fam_qtd, fam_vlr_total, fam_vlr_utilizado) ");
        query.append("SELECT date_format(curdate(), '%Y%m%d'), ");
        query.append("CASE ");
        query.append("  WHEN PERCENTUAL =   0  THEN  0 "); // '   0%'
        query.append("  WHEN PERCENTUAL <  20  THEN  1 "); // ' <20%'
        query.append("  WHEN PERCENTUAL <= 25  THEN  2 "); // '20/25%'
        query.append("  WHEN PERCENTUAL <= 30  THEN  3 "); // '25/30%'
        query.append("  WHEN PERCENTUAL <= 35  THEN  4 "); // '30/35%'
        query.append("  WHEN PERCENTUAL <= 40  THEN  5 "); // '35/40%'
        query.append("  WHEN PERCENTUAL <= 45  THEN  6 "); // '40/45%'
        query.append("  WHEN PERCENTUAL <= 50  THEN  7 "); // '45/50%'
        query.append("  WHEN PERCENTUAL <= 55  THEN  8 "); // '50/55%'
        query.append("  WHEN PERCENTUAL <= 60  THEN  9 "); // '55/60%'
        query.append("  WHEN PERCENTUAL <= 65  THEN 10 "); // '60/65%'
        query.append("  WHEN PERCENTUAL <= 70  THEN 11 "); // '65/70%'
        query.append("  WHEN PERCENTUAL <= 75  THEN 12 "); // '70/75%'
        query.append("  WHEN PERCENTUAL <= 80  THEN 13 "); // '75/80%'
        query.append("  WHEN PERCENTUAL <= 85  THEN 14 "); // '80/85%'
        query.append("  WHEN PERCENTUAL <= 90  THEN 15 "); // '85/90%'
        query.append("  WHEN PERCENTUAL <= 95  THEN 16 "); // '90/95%'
        query.append("  WHEN PERCENTUAL <= 100 THEN 17 "); // '95/100%'
        query.append("  ELSE 18 ");
        query.append("END AS FAIXA, ");
        query.append("dloBI.dlo_codigo, ");
        query.append("dcsBI.dcs_codigo, ");
        query.append("dsxBI.dsx_codigo, ");
        query.append("disBI.dis_codigo, ");
        query.append("dtmBI.dtm_codigo, ");
        query.append("dcrBI.dcr_codigo, ");
        query.append("dprBI.dpr_codigo, ");
        query.append("dlsBI.dls_codigo, ");
        query.append("COUNT(*), ");           // fam_qtd
        query.append("SUM(MARGEM_TOTAL), ");  // fam_vlr_total
        query.append("SUM(MARGEM_USADA) ");   // fam_vlr_utilizado
        query.append("FROM ( ");
        query.append("  select ");
        query.append("     rse.rse_codigo, ");
        query.append("     rse.org_codigo as ORGAO, ");
        query.append("     coalesce(nullif(rse.rse_tipo, ''), '").append(naoDefinido).append("') as CATEGORIA, ");
        query.append("     case when ser.ser_sexo = 'M' then 1 when ser.ser_sexo = 'F' then 2 else 9 end as SEXO, ");
        query.append("     coalesce(least(100, timestampdiff(year, ser.ser_data_nasc, curdate())), 0) as IDADE, ");
        query.append("     coalesce(crs_identificador, '").append(naoDefinido).append("') as CARGO, ");
        query.append("     coalesce(pos_identificador, '").append(naoDefinido).append("') as POSTO, ");
        query.append("     coalesce(upper(nullif(trim(rse.rse_municipio_lotacao), '')), '").append(naoDefinido).append("') as MUNICIPIO_LOTACAO, ");
        query.append("     coalesce(sum(coalesce(ade_vlr_folha, ade_vlr)), 0) as MARGEM_USADA, ");
        query.append("     coalesce(sum(coalesce(ade_vlr_folha, ade_vlr)), 0) + coalesce(").append(campoMargemRest).append(", 0) as MARGEM_TOTAL, ");
        query.append("     case (coalesce(sum(coalesce(ade_vlr_folha,   ade_vlr)),  0) + ").append(campoMargemRest).append(") WHEN 0 THEN 0 ELSE ((coalesce(sum(coalesce(ade_vlr_folha,  ade_vlr)),  0)*100.00)/(coalesce(sum(coalesce(ade_vlr_folha, ade_vlr)),  0) + ").append(campoMargemRest).append(")) END as PERCENTUAL ");
        query.append("  from tb_registro_servidor rse ");
        query.append("  inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo) ");
        query.append("  left outer join tb_cargo_registro_servidor crs on (crs.crs_codigo = rse.crs_codigo) ");
        query.append("  left outer join tb_posto_registro_servidor pos on (pos.pos_codigo = rse.pos_codigo) ");
        query.append("  left outer join tb_param_sist_consignante psi091 on (psi091.tpc_codigo = '").append(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3).append("') ");
        query.append("  left outer join tb_param_sist_consignante psi173 on (psi173.tpc_codigo = '").append(CodedValues.TPC_MARGEM_1_2_3_CASADAS).append("') ");
        query.append("  left outer join tb_param_sist_consignante psi218 on (psi218.tpc_codigo = '").append(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA).append("') ");
        query.append("  left outer join tb_param_sist_consignante psi219 on (psi219.tpc_codigo = '").append(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA).append("') ");
        query.append("  left outer join tb_aut_desconto ade on (ade.rse_codigo = rse.rse_codigo and ade.sad_codigo not in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("') ");

        if (marCodigo == 1) {
            query.append("     and (ade.ade_inc_margem = 1 ");
            query.append("      or ( coalesce(psi091.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3 ) ");
            query.append("      or ( coalesce(psi173.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (2,3) ) ");
            query.append("      or ( coalesce(psi218.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3 ) ");
            query.append("      or ( coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (2,3) ) ");
            query.append("     ) ");
        } else if (marCodigo == 2) {
            query.append("     and (ade.ade_inc_margem = 2 ");
            query.append("      or ( coalesce(psi173.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3 ) ");
            query.append("      or ( coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (1,3) ) ");
            query.append("     ) ");
        } else if (marCodigo == 3) {
            query.append("     and (ade.ade_inc_margem = 3 ");
            query.append("      or ( coalesce(psi218.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 1 ) ");
            query.append("      or ( coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (1,2) ) ");
            query.append("     ) ");
        }

        query.append("  ) ");
        query.append("  where rse.srs_codigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("') ");
        query.append("  group by rse.rse_codigo, rse.org_codigo, ");
        query.append("     coalesce(nullif(rse.rse_tipo, ''), '").append(naoDefinido).append("'), ");
        query.append("     case when ser.ser_sexo = 'M' then 1 when ser.ser_sexo = 'F' then 2 else 9 end, ");
        query.append("     coalesce(least(100, timestampdiff(year, ser.ser_data_nasc, curdate())), 0), ");
        query.append("     rse.").append(campoMargemRest).append(" ");
        query.append(") AS X ");
        query.append("INNER JOIN tb_dimensao_localizacao dloBI on (X.ORGAO = dloBI.dlo_org_codigo) ");
        query.append("INNER JOIN tb_dimensao_categoria_servidor dcsBI on (X.CATEGORIA = dcsBI.dcs_descricao) ");
        query.append("INNER JOIN tb_dimensao_sexo_servidor dsxBI on (X.SEXO = dsxBI.dsx_codigo) ");
        query.append("INNER JOIN tb_dimensao_idade_servidor disBI on (X.IDADE = disBI.dis_codigo) ");
        query.append("INNER JOIN tb_dimensao_tipo_margem dtmBI on (dtmBI.dtm_codigo = ").append(marCodigo).append(") ");
        query.append("INNER JOIN tb_dimensao_cargo_servidor dcrBI on (X.CARGO = dcrBI.dcr_identificador) ");
        query.append("INNER JOIN tb_dimensao_posto_servidor dprBI on (X.POSTO = dprBI.dpr_identificador) ");
        query.append("INNER JOIN tb_dimensao_lotacao_servidor dlsBI ON (X.MUNICIPIO_LOTACAO = dlsBI.dls_descricao) ");
        query.append("GROUP BY FAIXA, dloBI.dlo_codigo, dcsBI.dcs_codigo, dsxBI.dsx_codigo, disBI.dis_codigo, dtmBI.dtm_codigo, dcrBI.dcr_codigo, dprBI.dpr_codigo, dlsBI.dls_codigo ");

        return query.toString();
    }

    protected String montaQueryDadosFatoMargem(int marCodigo) {
        final String campoMargemRest = getCampoMargemRest(marCodigo);
        final String naoDefinido = ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null);

        final StringBuilder query = new StringBuilder();

        query.append("INSERT INTO tb_dados_fato_margem (FAM_CODIGO, DFM_NOME, DFM_CPF, DFM_MATRICULA, DFM_MARGEM, DFM_MARGEM_REST, DFM_MARGEM_USADA) ");
        query.append("SELECT fam.FAM_CODIGO, X.NOME, X.CPF, X.MATRICULA, X.MARGEM, X.MARGEM_REST, X.MARGEM_USADA ");
        query.append("FROM ( ");
        query.append("  SELECT ");
        query.append("    ser_nome as NOME, ");
        query.append("    ser_cpf as CPF, ");
        query.append("    rse_matricula as MATRICULA, ");
        query.append("    coalesce(sum(coalesce(ade_vlr_folha, ade_vlr)), 0) + coalesce(").append(campoMargemRest).append(", 0) as MARGEM, ");
        query.append("    coalesce(").append(campoMargemRest).append(", 0) as MARGEM_REST, ");
        query.append("    coalesce(sum(coalesce(ade_vlr_folha, ade_vlr)), 0) as MARGEM_USADA, ");
        query.append("    case (coalesce(sum(coalesce(ade_vlr_folha,   ade_vlr)),  0) + ").append(campoMargemRest).append(") WHEN 0 THEN 0 ELSE ((coalesce(sum(coalesce(ade_vlr_folha,  ade_vlr)),  0)*100.00)/(coalesce(sum(coalesce(ade_vlr_folha, ade_vlr)),  0) + ").append(campoMargemRest).append(")) END as PERCENTUAL, ");
        query.append("    coalesce(nullif(rse.rse_tipo, ''), '").append(naoDefinido).append("') as CATEGORIA, ");
        query.append("    coalesce(upper(nullif(trim(rse.rse_municipio_lotacao), '')), '").append(naoDefinido).append("') as MUNICIPIO_LOTACAO, ");
        query.append("    (case when ser.ser_sexo = 'M' then 1 when ser.ser_sexo = 'F' then 2 else 9 end) as SEXO, ");
        query.append("    coalesce(least(100, timestampdiff(year, ser.ser_data_nasc, curdate())), 0) as IDADE, ");
        query.append("    coalesce(crs_identificador, '").append(naoDefinido).append("') as CARGO, ");
        query.append("    coalesce(pos_identificador, '").append(naoDefinido).append("') as POSTO, ");
        query.append("    rse.org_codigo as ORGAO ");
        query.append("  from tb_registro_servidor rse ");
        query.append("  inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo) ");
        query.append("  left outer join tb_cargo_registro_servidor crs on (crs.crs_codigo = rse.crs_codigo) ");
        query.append("  left outer join tb_posto_registro_servidor pos on (pos.pos_codigo = rse.pos_codigo) ");
        query.append("  left outer join tb_param_sist_consignante psi091 on (psi091.tpc_codigo = '").append(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3).append("') ");
        query.append("  left outer join tb_param_sist_consignante psi173 on (psi173.tpc_codigo = '").append(CodedValues.TPC_MARGEM_1_2_3_CASADAS).append("') ");
        query.append("  left outer join tb_param_sist_consignante psi218 on (psi218.tpc_codigo = '").append(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA).append("') ");
        query.append("  left outer join tb_param_sist_consignante psi219 on (psi219.tpc_codigo = '").append(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA).append("') ");
        query.append("  left outer join tb_aut_desconto ade on (ade.rse_codigo = rse.rse_codigo and ade.sad_codigo not in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("') ");

        if (marCodigo == 1) {
            query.append("     and (ade.ade_inc_margem = 1 ");
            query.append("      or ( coalesce(psi091.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3 ) ");
            query.append("      or ( coalesce(psi173.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (2,3) ) ");
            query.append("      or ( coalesce(psi218.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3 ) ");
            query.append("      or ( coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (2,3) ) ");
            query.append("     ) ");
        } else if (marCodigo == 2) {
            query.append("     and (ade.ade_inc_margem = 2 ");
            query.append("      or ( coalesce(psi173.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 3 ) ");
            query.append("      or ( coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (1,3) ) ");
            query.append("     ) ");
        } else if (marCodigo == 3) {
            query.append("     and (ade.ade_inc_margem = 3 ");
            query.append("      or ( coalesce(psi218.psi_vlr, 'N') = 'S' and ade.ade_inc_margem = 1 ) ");
            query.append("      or ( coalesce(psi219.psi_vlr, 'N') = 'S' and ade.ade_inc_margem in (1,2) ) ");
            query.append("     ) ");
        }

        query.append("  ) ");
        query.append("  where rse.srs_codigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("') ");
        query.append("  group by rse.rse_codigo ");
        query.append(") AS X ");
        query.append("INNER JOIN tb_dimensao_localizacao dlo on (X.ORGAO = dlo.dlo_org_codigo) ");
        query.append("INNER JOIN tb_dimensao_categoria_servidor dcs on (X.CATEGORIA = dcs.dcs_descricao) ");
        query.append("INNER JOIN tb_dimensao_sexo_servidor dsx on (X.SEXO = dsx.dsx_codigo) ");
        query.append("INNER JOIN tb_dimensao_idade_servidor dis on (X.IDADE = dis.dis_codigo) ");
        query.append("INNER JOIN tb_dimensao_cargo_servidor dcr on (X.CARGO = dcr.dcr_identificador) ");
        query.append("INNER JOIN tb_dimensao_posto_servidor dpr on (X.POSTO = dpr.dpr_identificador) ");
        query.append("INNER JOIN tb_dimensao_lotacao_servidor dls ON (X.MUNICIPIO_LOTACAO = dls.dls_descricao) ");
        query.append("INNER JOIN tb_dimensao_tipo_margem dtm on (dtm.dtm_codigo = ").append(marCodigo).append(") ");
        query.append("INNER JOIN tb_dimensao_comprometimento dco on (dco.dco_codigo = ");
        query.append("CASE ");
        query.append("  WHEN PERCENTUAL =   0  THEN  0 "); //     0%
        query.append("  WHEN PERCENTUAL <  20  THEN  1 "); //   <20%
        query.append("  WHEN PERCENTUAL <= 25  THEN  2 "); // 20/25%
        query.append("  WHEN PERCENTUAL <= 30  THEN  3 "); // 25/30%
        query.append("  WHEN PERCENTUAL <= 35  THEN  4 "); // 30/35%
        query.append("  WHEN PERCENTUAL <= 40  THEN  5 "); // 35/40%
        query.append("  WHEN PERCENTUAL <= 45  THEN  6 "); // 40/45%
        query.append("  WHEN PERCENTUAL <= 50  THEN  7 "); // 45/50%
        query.append("  WHEN PERCENTUAL <= 55  THEN  8 "); // 50/55%
        query.append("  WHEN PERCENTUAL <= 60  THEN  9 "); // 55/60%
        query.append("  WHEN PERCENTUAL <= 65  THEN 10 "); // 60/65%
        query.append("  WHEN PERCENTUAL <= 70  THEN 11 "); // 65/70%
        query.append("  WHEN PERCENTUAL <= 75  THEN 12 "); // 70/75%
        query.append("  WHEN PERCENTUAL <= 80  THEN 13 "); // 75/80%
        query.append("  WHEN PERCENTUAL <= 85  THEN 14 "); // 80/85%
        query.append("  WHEN PERCENTUAL <= 90  THEN 15 "); // 85/90%
        query.append("  WHEN PERCENTUAL <= 95  THEN 16 "); // 90/95%
        query.append("  WHEN PERCENTUAL <= 100 THEN 17 "); // 95/100%
        query.append("  ELSE 18 ");
        query.append("END) ");
        query.append("INNER JOIN tb_fato_margem fam on (fam.dls_codigo = dls.dls_codigo ");
        query.append("  AND fam.dsx_codigo = dsx.dsx_codigo ");
        query.append("  AND fam.dis_codigo = dis.dis_codigo ");
        query.append("  AND fam.dcr_codigo = dcr.dcr_codigo ");
        query.append("  AND fam.dpr_codigo = dpr.dpr_codigo ");
        query.append("  AND fam.dcs_codigo = dcs.dcs_codigo ");
        query.append("  AND fam.dlo_codigo = dlo.dlo_codigo ");
        query.append("  AND fam.dtm_codigo = dtm.dtm_codigo ");
        query.append("  AND fam.dco_codigo = dco.dco_codigo ");
        query.append(") ");

        return query.toString();
    }

    private String getCampoMargemRest(int marCodigo) {
        return (marCodigo == 1 ? "rse_margem_rest" : (marCodigo == 2) ? "rse_margem_rest_2" : "rse_margem_rest_3");
    }
}
