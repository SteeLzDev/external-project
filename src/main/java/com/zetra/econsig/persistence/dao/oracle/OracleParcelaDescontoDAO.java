package com.zetra.econsig.persistence.dao.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.persistence.dao.mysql.MySqlParcelaDescontoDAO;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: OracleParcelaDescontoDAO</p>
 * <p>Description: Implementação para Oracle do DAO de Parcela Desconto</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OracleParcelaDescontoDAO extends MySqlParcelaDescontoDAO {

    /** TODO Herdar o genérico */

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OracleParcelaDescontoDAO.class);

    /**
     * Insere as parcelas do período atual para os contratos.
     * @param orgCodigos : o código do órgão que está sendo exportado, nulo para todos
     * @param estCodigos : o código do estabelecimento que está sendo exportado, nulo para todos
     * @param operacaoIntegracao : o tipo de operação de integração (movimento ou retorno)
     * @param responsavel : responsável pela operação
     * @throws DAOException
     */
    @Override
    public void insereParcelasFaltantes(List<String> orgCodigos, List<String> estCodigos, List<String> rseCodigos, String operacaoIntegracao, AcessoSistema responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            StringBuilder sufixo = new StringBuilder();
            if (estCodigos != null && estCodigos.size() > 0) {
                sufixo.append(" AND cnv.org_codigo IN (SELECT org.org_codigo FROM tb_orgao org WHERE org.est_codigo IN (:estCodigos))");
                queryParams.addValue("estCodigos", estCodigos);
            } else if (orgCodigos != null && orgCodigos.size() > 0) {
                sufixo.append(" AND cnv.org_codigo IN (:orgCodigos)");
                queryParams.addValue("orgCodigos", orgCodigos);
            } else if (rseCodigos != null && rseCodigos.size() > 0) {
                sufixo.append(" AND ade.rse_codigo IN (:rseCodigos)");
                queryParams.addValue("rseCodigos", rseCodigos);
            }


            StringBuilder query = new StringBuilder();
            int rows = 0;

            // Cria tabela temporária com ocorrências pós-corte por ADE_CODIGO
            List<String> tocCodigos = new ArrayList<>();
            tocCodigos.add(CodedValues.TOC_TARIF_LIQUIDACAO);
            tocCodigos.add(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO);
            tocCodigos.add(CodedValues.TOC_SUSPENSAO_CONTRATO);
            tocCodigos.add(CodedValues.TOC_REATIVACAO_CONTRATO);
            if (ParamSist.paramEquals(CodedValues.TPC_CONCLUI_ADE_EXPORTACAO_MOVIMENTO, CodedValues.TPC_SIM, responsavel)) {
                tocCodigos.add(CodedValues.TOC_CONCLUSAO_CONTRATO);
                tocCodigos.add(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO);
            }

            query.append("CALL dropTableIfExists('tb_tmp_oca_pos_periodo')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CALL createTemporaryTable('tb_tmp_oca_pos_periodo (");
            query.append("ADE_CODIGO varchar2(32) NOT NULL, ");
            query.append("TOC_6   number(6) DEFAULT 0 NOT NULL, ");
            query.append("TOC_7   number(6) DEFAULT 0 NOT NULL, ");
            query.append("TOC_15  number(6) DEFAULT 0 NOT NULL, ");
            query.append("TOC_19  number(6) DEFAULT 0 NOT NULL, ");
            query.append("TOC_84  number(6) DEFAULT 0 NOT NULL, ");
            query.append("TOC_119 number(6) DEFAULT 0 NOT NULL, ");
            query.append("PRIMARY KEY (ADE_CODIGO))')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_tmp_oca_pos_periodo (ADE_CODIGO, TOC_6, TOC_7, TOC_15, TOC_19, TOC_84, TOC_119) ");
            query.append("SELECT oca.ADE_CODIGO, ");
            query.append("  SUM(CASE WHEN oca.TOC_CODIGO = '").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("' THEN 1 ELSE 0 END) AS TOC_6, ");
            query.append("  SUM(CASE WHEN oca.TOC_CODIGO = '").append(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO).append("' THEN 1 ELSE 0 END) AS TOC_7, ");
            query.append("  SUM(CASE WHEN oca.TOC_CODIGO = '").append(CodedValues.TOC_CONCLUSAO_CONTRATO).append("' THEN 1 ELSE 0 END) AS TOC_15, ");
            query.append("  SUM(CASE WHEN oca.TOC_CODIGO = '").append(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO).append("' THEN 1 ELSE 0 END) AS TOC_19, ");
            query.append("  SUM(CASE WHEN oca.TOC_CODIGO = '").append(CodedValues.TOC_SUSPENSAO_CONTRATO).append("'  THEN 1 ELSE 0 END) AS TOC_84, ");
            query.append("  SUM(CASE WHEN oca.TOC_CODIGO = '").append(CodedValues.TOC_REATIVACAO_CONTRATO).append("' THEN 1 ELSE 0 END) AS TOC_119 ");
            query.append("FROM tb_ocorrencia_autorizacao oca ");
            query.append("INNER JOIN tb_aut_desconto ade ON (oca.ade_codigo = ade.ade_codigo) ");
            query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo) ");
            query.append("WHERE oca.toc_codigo IN ('").append(TextHelper.join(tocCodigos, "','")).append("') ");
            query.append("  AND ((oca.oca_periodo is not null and oca.oca_periodo > pex.pex_periodo) OR (oca.oca_periodo is null and oca.oca_data > pex.pex_data_fim)) ");
            query.append(sufixo.toString());
            query.append("GROUP BY oca.ADE_CODIGO");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // Remove a tabela temporária caso exista (Teoricamente ela não deve existir)
            query.append("CALL dropTableIfExists('tb_tmp_max_prd_numero')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // Cria tabela temporária com o próximo número para todos os contratos
            // deferidos, em andamento ou em estoque que podem precisar de parcela.
            query.append("CALL createTemporaryTable('tb_tmp_max_prd_numero (ADE_CODIGO varchar2(32), PRD_NUMERO number(10,0), PRIMARY KEY (ADE_CODIGO))')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_tmp_max_prd_numero (ADE_CODIGO, PRD_NUMERO)");
            query.append(" SELECT prd.ade_codigo AS ADE_CODIGO, MAX(prd.prd_numero) AS PRD_NUMERO ");
            query.append(" FROM tb_parcela_desconto prd ");
            query.append(" GROUP BY prd.ade_codigo");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // Remove a tabela temporária caso exista (Teoricamente ela não deve existir)
            query.append("CALL dropTableIfExists('tb_tmp_prox_prd_numero')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // Cria tabela temporária com o próximo número para todos os contratos
            // deferidos, em andamento ou em estoque que podem precisar de parcela.
            query.append("CALL createTemporaryTable('tb_tmp_prox_prd_numero (ADE_CODIGO varchar2(32), PRD_NUMERO number(10,0), PRIMARY KEY (ADE_CODIGO))')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_tmp_prox_prd_numero (ADE_CODIGO, PRD_NUMERO)");
            query.append(" SELECT ade.ade_codigo AS ADE_CODIGO, COALESCE(tmp.prd_numero, 0) + 1 AS PRD_NUMERO ");
            query.append(" FROM tb_aut_desconto ade");
            query.append(" INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo)");
            query.append(" INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo)");
            query.append(" INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo)");
            query.append(" LEFT OUTER JOIN tb_tmp_max_prd_numero tmp ON (ade.ade_codigo = tmp.ade_codigo)");
            query.append(" WHERE ");
            query.append("ade.ade_int_folha = '").append(CodedValues.INTEGRA_FOLHA_SIM).append("' AND ");
            query.append("ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INCLUSAO_PARCELA, "','")).append("') AND ");
            query.append("ade.ade_ano_mes_ini <= pex.pex_periodo AND ");
            query.append("coalesce(ade.ade_prazo, 999999999) > coalesce(ade.ade_prd_pagas, 0) ");
            query.append(sufixo.toString());
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            List<String> sadCodigosEncerrados = new ArrayList<>();
            sadCodigosEncerrados.add(CodedValues.SAD_CANCELADA);
            sadCodigosEncerrados.add(CodedValues.SAD_LIQUIDADA);
            if (ParamSist.paramEquals(CodedValues.TPC_CONCLUI_ADE_EXPORTACAO_MOVIMENTO, CodedValues.TPC_SIM, responsavel)) {
                sadCodigosEncerrados.add(CodedValues.SAD_CONCLUIDO);
            }

            List<String> tocCodigosEncerrados = new ArrayList<>();
            tocCodigosEncerrados.add(CodedValues.TOC_TARIF_LIQUIDACAO);
            tocCodigosEncerrados.add(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO);
            if (ParamSist.paramEquals(CodedValues.TPC_CONCLUI_ADE_EXPORTACAO_MOVIMENTO, CodedValues.TPC_SIM, responsavel)) {
                tocCodigosEncerrados.add(CodedValues.TOC_CONCLUSAO_CONTRATO);
                tocCodigosEncerrados.add(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO);
            }

            // Insere na tabela de próximo número os contratos cancelados ou liquidados
            // depois do dia de corte, ou seja a data da ocorrência é maior que o fim do período
            // ou o período da ocorrência é maior que período atual
            query.append("INSERT INTO tb_tmp_prox_prd_numero (ADE_CODIGO, PRD_NUMERO)");
            query.append(" SELECT ade.ade_codigo AS ADE_CODIGO, COALESCE(tmp.prd_numero, 0) + 1 AS PRD_NUMERO ");
            query.append(" FROM tb_aut_desconto ade");
            query.append(" INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo)");
            query.append(" INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo)");
            query.append(" INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo)");
            query.append(" LEFT OUTER JOIN tb_tmp_max_prd_numero tmp ON (ade.ade_codigo = tmp.ade_codigo)");
            query.append(" WHERE ");
            query.append("ade.ade_int_folha = '").append(CodedValues.INTEGRA_FOLHA_SIM).append("' AND ");
            query.append("ade.sad_codigo IN ('").append(TextHelper.join(sadCodigosEncerrados, "','")).append("') AND ");
            query.append(" EXISTS (SELECT 1 FROM tb_ocorrencia_autorizacao oca WHERE oca.ade_codigo = ade.ade_codigo AND ");
            query.append("oca.toc_codigo IN ('").append(TextHelper.join(tocCodigosEncerrados, "','")).append("') ");
            query.append(" AND ((oca.oca_periodo is not null and oca.oca_periodo > pex.pex_periodo) OR (oca.oca_periodo is null and oca.oca_data > pex.pex_data_fim))) AND ");
            query.append("ade.ade_ano_mes_ini <= pex.pex_periodo AND ");
            query.append("coalesce(ade.ade_prazo, 999999999) > coalesce(ade.ade_prd_pagas, 0) ");
            query.append("AND EXISTS (SELECT 1 FROM tb_tmp_oca_pos_periodo oca WHERE oca.ade_codigo = ade.ade_codigo AND (oca.TOC_6 > 0 OR oca.TOC_7 > 0 OR oca.TOC_15 > 0 OR oca.TOC_19 > 0)) ");
            query.append(sufixo.toString());
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // Remove da tabela temporária de próximo número os contratos inseridos no insert anterior que estavam suspensos
            query.append("DELETE FROM tb_tmp_prox_prd_numero tmp ");
            query.append("WHERE EXISTS ( ");
            query.append("SELECT * FROM tb_aut_desconto ade ");
            query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo) ");
            query.append("INNER JOIN tb_ocorrencia_autorizacao ocaSusp ON (ocaSusp.ade_codigo = ade.ade_codigo AND ocaSusp.toc_codigo = '").append(CodedValues.TOC_SUSPENSAO_CONTRATO).append("' and ocaSusp.oca_data < pex_data_fim) ");
            query.append("WHERE tmp.ade_codigo = ade.ade_codigo ");
            query.append("AND ade.ade_int_folha = '").append(CodedValues.INTEGRA_FOLHA_SIM).append("' ");
            query.append("AND ade.sad_codigo IN ('").append(TextHelper.join(sadCodigosEncerrados, "','")).append("') ");
            query.append("AND ade.ade_ano_mes_ini <= pex.pex_periodo ");
            query.append("AND coalesce(ade.ade_prazo, 999999999) > coalesce(ade.ade_prd_pagas, 0) ");
            query.append("AND NOT EXISTS (SELECT * FROM tb_ocorrencia_autorizacao ocaReat WHERE ocaReat.ade_codigo = ade.ade_codigo AND ocaReat.toc_codigo = '").append(CodedValues.TOC_REATIVACAO_CONTRATO).append("' AND ocaReat.oca_data > ocaSusp.oca_data) ");
            query.append(sufixo.toString());
            query.append(")");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // Insere na tabela de próximo número os contratos suspensos pela CSE ou CSA
            // depois do dia de corte, ou seja a data da ocorrência é maior que o fim do período
            // ou o período da ocorrência é maior que período atual
            query.append("INSERT INTO tb_tmp_prox_prd_numero (ADE_CODIGO, PRD_NUMERO) ");
            query.append("SELECT ade.ade_codigo AS ADE_CODIGO, COALESCE(prd_numero, 0) + 1 AS PRD_NUMERO ");
            query.append("FROM tb_aut_desconto ade ");
            query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo) ");
            query.append("LEFT OUTER JOIN tb_tmp_max_prd_numero prd ON (ade.ade_codigo = prd.ade_codigo) ");
            query.append("WHERE ade.ade_int_folha = '").append(CodedValues.INTEGRA_FOLHA_SIM).append("' ");
            query.append("AND ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_SUSPENSOS, "','")).append("') ");
            query.append("AND ade.ade_ano_mes_ini <= pex.pex_periodo ");
            query.append("AND coalesce(ade.ade_prazo, 999999999) > coalesce(ade.ade_prd_pagas, 0) ");
            query.append("AND EXISTS (SELECT 1 FROM tb_tmp_oca_pos_periodo oca WHERE oca.ade_codigo = ade.ade_codigo AND oca.TOC_84 > 0 AND oca.TOC_119 <> oca.TOC_84) ");
            query.append(sufixo.toString());
            query.append(" GROUP BY ade.ade_codigo, prd_numero");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // Remove da tabela temporária de próximo número os contratos que já tenham parcela histórica
            // no periodo
            query.append("DELETE FROM tb_tmp_prox_prd_numero");
            query.append(" WHERE EXISTS (");
            query.append(" SELECT 1 FROM tb_parcela_desconto prd");
            query.append(" INNER JOIN tb_aut_desconto ade ON (prd.ade_codigo = ade.ade_codigo)");
            query.append(" INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo)");
            query.append(" INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo)");
            query.append(" INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo)");
            query.append(" WHERE tb_tmp_prox_prd_numero.ade_codigo = ade.ade_codigo");
            query.append(" AND prd.prd_data_desconto = pex.pex_periodo");
            query.append(")");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // Remove da tabela temporária de próximo número os contratos que foram reativados
            // após o fechamento do período e já estavam suspensos no fechamento do período
            query.append("DELETE FROM tb_tmp_prox_prd_numero");
            query.append(" WHERE ADE_CODIGO IN (");
            query.append(" SELECT oca.ade_codigo FROM tb_tmp_oca_pos_periodo oca ");
            query.append(" WHERE oca.TOC_119 > 0 AND oca.TOC_84 = 0");
            query.append(")");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // Remove a tabela temporária caso exista (Teoricamente ela não deve existir)
            query.append("CALL dropTableIfExists('tb_tmp_prd_numero_periodo')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            // Cria tabela temporária com os números de parcelas da tabela de parcelas do
            // periodo mas que não sejam para desconto no periodo em questão
            query.append("CALL createTemporaryTable('tb_tmp_prd_numero_periodo (ADE_CODIGO varchar2(32), PRD_NUMERO number(10,0), PRIMARY KEY (ADE_CODIGO))')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("INSERT INTO tb_tmp_prd_numero_periodo (ADE_CODIGO, PRD_NUMERO) ");
            query.append(" SELECT ade.ade_codigo AS ADE_CODIGO, (COALESCE(MAX(prd.prd_numero), 0) + 1) AS PRD_NUMERO ");
            query.append(" FROM tb_aut_desconto ade");
            query.append(" INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo)");
            query.append(" INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo)");
            query.append(" INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = cnv.org_codigo)");
            query.append(" INNER JOIN tb_parcela_desconto_periodo prd ON (ade.ade_codigo = prd.ade_codigo)");
            query.append(" INNER JOIN tb_tmp_prox_prd_numero tmp ON (tmp.ade_codigo = ade.ade_codigo)");
            query.append(" WHERE prd.prd_data_desconto <> pex.pex_periodo");
            query.append(sufixo.toString());
            query.append(" GROUP BY ade.ade_codigo");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // Atualiza possíveis parcelas que existam na da tabela de parcelas do periodo
            query.append("UPDATE tb_tmp_prox_prd_numero prox ");
            query.append("SET prox.prd_numero = (SELECT per.prd_numero FROM tb_tmp_prd_numero_periodo per where prox.ade_codigo = per.ade_codigo) ");
            query.append("WHERE EXISTS (SELECT 1 FROM tb_tmp_prd_numero_periodo per WHERE prox.ade_codigo = per.ade_codigo and prox.prd_numero < per.prd_numero)");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            // Insere as parcelas faltantes (Status EM_PROCESSAMENTO), para os contratos
            // que ainda não possuem parcela para o período informado na tabela tb_periodo_exportacao
            query.append("INSERT INTO tb_parcela_desconto_periodo");
            query.append(" (ADE_CODIGO, PRD_NUMERO, SPD_CODIGO, PRD_DATA_DESCONTO, PRD_VLR_PREVISTO, MNE_CODIGO) ");
            query.append(" SELECT ade.ade_codigo, tmp.PRD_NUMERO + floor(pex.pex_sequencia / (case when ade.ade_periodicidade = 'M' then 2 else 1 end)) - coalesce((SELECT pex2.pex_sequencia FROM tb_periodo_exportacao pex2 WHERE pex2.org_codigo = cnv.org_codigo AND pex2.pex_periodo = ade.ade_ano_mes_ini), 0),");
            query.append(" '").append(CodedValues.SPD_EMPROCESSAMENTO).append("' AS SPD_CODIGO, pex.pex_periodo AS PRD_DATA_DESCONTO, coalesce(ade.ade_vlr_parcela_folha, ade.ade_vlr) AS PRD_VLR_PREVISTO, ade.mne_codigo");
            query.append(" FROM tb_aut_desconto ade");
            query.append(" INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo)");
            query.append(" INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo)");
            query.append(" INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = cnv.org_codigo)");
            query.append(" INNER JOIN tb_tmp_prox_prd_numero tmp ON (tmp.ade_codigo = ade.ade_codigo)");
            query.append(" LEFT OUTER JOIN tb_parcela_desconto_periodo prd ON (ade.ade_codigo = prd.ade_codigo AND prd.prd_data_desconto = pex.pex_periodo) ");
            query.append(" WHERE prd.ade_codigo IS NULL");
            query.append(" AND pex.pex_periodo >= ade.ade_ano_mes_ini");
            query.append(" AND coalesce(ade.ade_prazo, 999999999) > coalesce(ade.ade_prd_pagas, 0) + floor(pex.pex_sequencia / (case when ade.ade_periodicidade = 'M' then 2 else 1 end)) - coalesce((SELECT pex2.pex_sequencia FROM tb_periodo_exportacao pex2 WHERE pex2.org_codigo = cnv.org_codigo AND pex2.pex_periodo = ade.ade_ano_mes_ini), 0)");
            if (!PeriodoHelper.folhaMensal(responsavel)) {
                // Se for quinzenal e contrato mensal, exporta apenas as parcelas dos períodos da quinzena em que estes foram incluídos
                query.append(" AND (ade.ade_periodicidade <> '").append(CodedValues.PERIODICIDADE_FOLHA_MENSAL).append("'");
                query.append(" OR extract(day from pex.pex_periodo) = extract(day from ade.ade_ano_mes_ini))");
            }
            if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) &&
                    ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_SIM, responsavel)) {
                // Se permite agrupamento de períodos, verifica se os contratos "encerrados" incluídos na listagem para criação
                // de parcelas possui ocorrência de exclusão posterior ao período que será criada a parcela
                query.append(" AND (ade.sad_codigo NOT IN ('").append(TextHelper.join(sadCodigosEncerrados, "','")).append("') ");
                query.append(" OR EXISTS (");
                query.append(" SELECT 1 FROM tb_ocorrencia_autorizacao oca ");
                query.append(" WHERE oca.ade_codigo = ade.ade_codigo ");
                query.append(" AND oca.toc_codigo IN ('").append(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO).append("','").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("') ");
                query.append(" AND oca.oca_periodo > pex.pex_periodo) ");
                query.append(") ");
            }
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            if (operacaoIntegracao.equals(CodedValues.INTEGRACAO_EXPORTACAO_MOV)) {
                // Apaga as parcelas do período que não deveriam ter sido geradas, por exemplo
                // após uma exportação com datas do período incorretas, de contratos finalizados:
                // ATENÇÃO: a inclusão dos demais status é arriscado já que estes podem ter sido
                // modificados pós corte, e poderiam não estar aptos a terem parcelas.
                query.append("DELETE FROM tb_parcela_desconto_periodo ");
                query.append(" WHERE (ade_codigo, prd_data_desconto) IN (");
                query.append(" SELECT ade.ade_codigo, pex.pex_periodo FROM tb_aut_desconto ade");
                query.append(" INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo)");
                query.append(" INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo)");
                query.append(" INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo)");
                query.append(" LEFT OUTER JOIN tb_tmp_prox_prd_numero tmp ON (ade.ade_codigo = tmp.ade_codigo)");
                query.append(" WHERE tmp.ade_codigo IS NULL");
                query.append(" AND ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("')");
                query.append(sufixo.toString());
                query.append(")");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);
                query.setLength(0);

                // Apaga as parcelas do período relacionadas a contratos liquidados ou suspensos que possuem
                // ocorrência de liquidação para o período da parcela, ou seja foram excluídos após o fechamento
                // do corte, no período de extensão para ajustes pelo gestor.
                if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel)) {
                    query.append("DELETE FROM tb_parcela_desconto_periodo ");
                    query.append("WHERE (ade_codigo, prd_data_desconto) in ( ");
                    query.append("  select ade.ade_codigo, oca.oca_periodo ");
                    query.append("  from tb_ocorrencia_autorizacao oca ");
                    query.append("  inner join tb_aut_desconto ade on (oca.ade_codigo = ade.ade_codigo) ");
                    query.append("  inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
                    query.append("  inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
                    query.append("  inner join tb_periodo_exportacao pex on (cnv.org_codigo = pex.org_codigo) ");
                    query.append("  where oca.toc_codigo = '").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("' ");
                    query.append("  and (ade.sad_codigo in ('").append(CodedValues.SAD_SUSPENSA_CSE).append("','").append(CodedValues.SAD_LIQUIDADA).append("') ");
                    query.append("   or (ade.sad_codigo in ('").append(CodedValues.SAD_DEFERIDA).append("','").append(CodedValues.SAD_EMANDAMENTO).append("') ");
                    query.append("  and exists (select 1 from tb_ocorrencia_autorizacao oca2 where oca2.ade_codigo = ade.ade_codigo and oca2.toc_codigo = '").append(CodedValues.TOC_REATIVACAO_CONTRATO).append("' and oca2.oca_periodo > pex.pex_periodo))) ");
                    query.append("  and oca.oca_periodo = pex.pex_periodo ");
                    query.append("  and oca.oca_data > pex.pex_data_fim ");
                    query.append(sufixo.toString());
                    query.append(")");
                    LOG.trace(query.toString());
                    rows = jdbc.update(query.toString(), queryParams);
                    LOG.trace("Linhas afetadas: " + rows);
                    query.setLength(0);
                }


                // Remove a tabela temporária caso exista (Teoricamente ela não deve existir)
                query.append("CALL dropTableIfExists('tb_tmp_ade_alterada')");
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);
                query.setLength(0);

                // Criar tabela com as ocorrência de alteração que foram feitas depois do pex_periodo.
                // para atualizar o valor da parcela pelo valor anterior à alteração do contrato.
                // Deve ser a ocorrência com data mais antiga pós o fechamento do periodo.
                query.append("CALL createTemporaryTable('tb_tmp_ade_alterada (ADE_CODIGO varchar2(32), PERIODO DATE, VALOR_ANTERIOR number(13,2), PRIMARY KEY (ADE_CODIGO, PERIODO))')");
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);
                query.setLength(0);

                query.append("INSERT INTO tb_tmp_ade_alterada (ADE_CODIGO, PERIODO, VALOR_ANTERIOR) ");
                query.append("SELECT ade.ade_codigo, pex.pex_periodo, max(oca.oca_ade_vlr_ant) ");
                query.append("FROM tb_ocorrencia_autorizacao oca ");
                query.append("INNER JOIN tb_aut_desconto ade ON (oca.ade_codigo = ade.ade_codigo) ");
                query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
                query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
                query.append("INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = cnv.org_codigo) ");
                query.append("WHERE oca.oca_periodo > pex.pex_periodo ");
                query.append("AND oca.toc_codigo = '").append(CodedValues.TOC_ALTERACAO_CONTRATO).append("' ");
                query.append("AND NOT EXISTS (");
                query.append("  select 1 from tb_ocorrencia_autorizacao oca2");
                query.append("  where oca2.ade_codigo = oca.ade_codigo");
                query.append("    and oca2.toc_codigo = oca.toc_codigo");
                query.append("    and oca2.oca_periodo > pex.pex_periodo ");
                query.append("    and oca2.oca_data < oca.oca_data");
                query.append(") ");
                query.append(sufixo.toString());
                query.append("GROUP BY ade.ade_codigo, pex.pex_periodo");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);
                query.setLength(0);

                // Atualiza o valor da parcela de contrato alterado pós fechamento do período
                query.append("UPDATE tb_parcela_desconto_periodo prd ");
                query.append("SET prd.prd_vlr_previsto = (SELECT tmp.valor_anterior FROM tb_tmp_ade_alterada tmp WHERE tmp.ade_codigo = prd.ade_codigo AND prd.prd_data_desconto = tmp.periodo) ");
                query.append("WHERE EXISTS (SELECT 1 FROM tb_tmp_ade_alterada tmp WHERE tmp.ade_codigo = prd.ade_codigo AND NULLIF(tmp.valor_anterior, 0.00) IS NOT NULL AND prd.prd_data_desconto = tmp.periodo)");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);
                query.setLength(0);

                // Ajusta o valor das parcelas de contratos alterados após o fechamento do corte e exportação
                // no período de ajustes realizados pelo gestor
                if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel)) {
                    query.append("CALL dropTableIfExists('tb_tmp_ade_alterada_ajustes')");
                    LOG.trace(query.toString());
                    jdbc.update(query.toString(), queryParams);
                    query.setLength(0);

                    query.append("CALL createTemporaryTable('tb_tmp_ade_alterada_ajustes (ADE_CODIGO varchar2(32), PERIODO DATE, VALOR_NOVO number(13,2), PRIMARY KEY (ADE_CODIGO, PERIODO))')");
                    LOG.trace(query.toString());
                    jdbc.update(query.toString(), queryParams);
                    query.setLength(0);

                    query.append("INSERT INTO tb_tmp_ade_alterada_ajustes (ADE_CODIGO, PERIODO, VALOR_NOVO) ");
                    query.append("SELECT ade.ade_codigo, pex.pex_periodo, max(oca.oca_ade_vlr_novo) ");
                    query.append("FROM tb_ocorrencia_autorizacao oca ");
                    query.append("INNER JOIN tb_aut_desconto ade ON (oca.ade_codigo = ade.ade_codigo) ");
                    query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
                    query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
                    query.append("INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = cnv.org_codigo) ");
                    query.append("WHERE oca.oca_periodo = pex.pex_periodo ");
                    query.append("AND oca.oca_data > pex.pex_data_fim ");
                    query.append("AND oca.toc_codigo = '").append(CodedValues.TOC_ALTERACAO_CONTRATO).append("' ");
                    query.append("AND NOT EXISTS (");
                    query.append("  select 1 from tb_ocorrencia_autorizacao oca2");
                    query.append("  where oca2.ade_codigo = oca.ade_codigo");
                    query.append("    and oca2.toc_codigo = oca.toc_codigo");
                    query.append("    and oca2.oca_periodo = pex.pex_periodo ");
                    query.append("    and oca2.oca_data > oca.oca_data");
                    query.append(") ");
                    query.append(sufixo.toString());
                    query.append("GROUP BY ade.ade_codigo, pex.pex_periodo");
                    LOG.trace(query.toString());
                    rows = jdbc.update(query.toString(), queryParams);
                    LOG.trace("Linhas afetadas: " + rows);
                    query.setLength(0);

                    query.append("UPDATE tb_parcela_desconto_periodo prd ");
                    query.append("SET prd.prd_vlr_previsto = (SELECT tmp.valor_novo FROM tb_tmp_ade_alterada_ajustes tmp WHERE tmp.ade_codigo = prd.ade_codigo AND prd.prd_data_desconto = tmp.periodo) ");
                    query.append("WHERE EXISTS (SELECT 1 FROM tb_tmp_ade_alterada_ajustes tmp WHERE tmp.ade_codigo = prd.ade_codigo AND NULLIF(tmp.valor_novo, 0.00) IS NOT NULL AND prd.prd_data_desconto = tmp.periodo)");
                    LOG.trace(query.toString());
                    rows = jdbc.update(query.toString(), queryParams);
                    LOG.trace("Linhas afetadas: " + rows);
                    query.setLength(0);
                }

                // Se permite as consignatárias escolherem a forma de numeração das parcelas ou o padrão é manter ao rejeitar
                // atualiza o PRD_NUMERO das parcelas do período para manter o último PRD_NUMERO rejeitado
                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_CSA_ESCOLHER_FORMA_NUMERACAO_PARCELAS, CodedValues.TPC_SIM, responsavel) ||
                        ParamSist.paramEquals(CodedValues.TPC_PADRAO_FORMA_NUMERACAO_PARCELAS, CodedValues.FORMA_NUMERACAO_PARCELAS_MANTEM_AO_REJEITAR, responsavel)) {
                    throw new UnsupportedOperationException();
                }
            }

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Atualiza o status das parcelas exportadas para "em processamento"
     * @param orgCodigos : o código do órgão que está sendo exportado, nulo para todos
     * @param estCodigos : o código do estabelecimento que está sendo exportado, nulo para todos
     * @param verbas     : os códigos dos serviços exportados
     * @throws DAOException
     */
    @Override
    public void processaParcelas(List<String> orgCodigos, List<String> estCodigos, List<String> verbas) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            StringBuilder updateClause = new StringBuilder();
            updateClause.append("UPDATE tb_parcela_desconto_periodo prd ");
            updateClause.append("SET prd.spd_codigo = '").append(CodedValues.SPD_EMPROCESSAMENTO).append("' ");
            updateClause.append("WHERE prd.spd_codigo <> '").append(CodedValues.SPD_EMPROCESSAMENTO).append("' ");
            updateClause.append("AND EXISTS (");
            updateClause.append("SELECT 1 FROM tb_aut_desconto ade ");
            updateClause.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            updateClause.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            updateClause.append("INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = cnv.org_codigo) ");

            if (estCodigos != null && estCodigos.size() > 0) {
                updateClause.append("INNER JOIN tb_orgao org ON (org.org_codigo = cnv.org_codigo) ");
            }

            StringBuilder whereClause = new StringBuilder();
            whereClause.append("WHERE ade.ade_codigo = prd.ade_codigo ");

            if (verbas != null && verbas.size() > 0) {
                whereClause.append("AND cnv.cnv_cod_verba IN (:verbas) ");
                    queryParams.addValue("verbas", verbas);
            }
            if (orgCodigos != null && orgCodigos.size() > 0) {
                whereClause.append("AND cnv.org_codigo IN (:orgCodigos) ");
                    queryParams.addValue("orgCodigos", orgCodigos);
            }
            if (estCodigos != null && estCodigos.size() > 0) {
                whereClause.append("AND org.est_codigo IN (:estCodigos) ");
                    queryParams.addValue("estCodigos", estCodigos);
            }

            // Status dos contratos para os quais as parcelas serão atualizadas
            List<String> sadCodigos = new ArrayList<>();
            sadCodigos.add(CodedValues.SAD_DEFERIDA);
            sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
            sadCodigos.add(CodedValues.SAD_ESTOQUE);
            sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
            sadCodigos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);
            sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);

            // ADE DEFERIDAS E EM ANDAMENTO
            StringBuilder query = new StringBuilder();
            query.append(updateClause).append(whereClause);
            query.append("AND ade.sad_codigo IN ('").append(TextHelper.join(sadCodigos, "','")).append("') ");
            query.append("AND prd.spd_codigo = '").append(CodedValues.SPD_EMABERTO).append("' ");
            query.append("AND prd.prd_data_desconto = pex.pex_periodo ");
            query.append(")");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // ADE liquidadas e canceladas que devem ser descontadas no periodo atual
            query.setLength(0);
            query.append(updateClause);
            query.append("INNER JOIN tb_ocorrencia_autorizacao oca ON (ade.ade_codigo = oca.ade_codigo) ");
            query.append(whereClause);
            query.append("AND ade.sad_codigo IN ('").append(CodedValues.SAD_LIQUIDADA).append("','").append(CodedValues.SAD_CANCELADA).append("') ");
            query.append("AND prd.prd_data_desconto = pex.pex_periodo ");
            query.append("AND oca.toc_codigo IN ('").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("','").append(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO).append("') ");
            query.append("AND ((oca.oca_periodo is not null and oca.oca_periodo > pex.pex_periodo) OR (oca.oca_periodo is null and oca.oca_data > pex.pex_data_fim)) ");
            query.append("AND ade.ade_ano_mes_ini <= pex.pex_periodo ");
            query.append(")");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Insere as ocorrências para as parcelas que estão sendo integradas,
     * parcelas com status = 'Em Processamento'. É utilizado pela conclusão
     * de retorno ou pelo cadastro manual de retorno, portanto irá utilizar
     * sempre a tabela de parcelas do período.
     *
     * @param tocCodigo      : código do tipo da ocorrência
     * @param ocpObs         : mensagem da ocorrência
     * @param tipoEntidade   : CSE, ORG ou EST
     * @param codigoEntidade : código da entidade que está realizando a operação,
     *                         ORG_CODIGO, EST_CODIGO ou CSE_CODIGO
     * @param usuCodigo      : responsável pela operação
     * @throws DAOException
     */
    @Override
    public void criaOcorrenciaRetorno(String tocCodigo, String ocpObs, String tipoEntidade, String codigoEntidade, String usuCodigo) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        Connection conn = null;
        PreparedStatement preStat = null;
        try {
            final StringBuilder query = new StringBuilder();
            query.append("INSERT INTO tb_ocorrencia_parcela_periodo");
            query.append(" (OCP_CODIGO, PRD_CODIGO, OCP_DATA, TOC_CODIGO, OCP_OBS, USU_CODIGO) ");
            query.append("VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?, ?)");

            conn = DBHelper.makeConnection();
            preStat = conn.prepareStatement(query.toString());

            query.setLength(0);
            query.append("SELECT prd.prd_codigo");
            query.append(" FROM tb_parcela_desconto_periodo prd");

            if (tipoEntidade.equalsIgnoreCase("ORG") || tipoEntidade.equalsIgnoreCase("EST")) {
                query.append(" INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = prd.ade_codigo)");
                query.append(" INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo)");
                query.append(" INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo)");

                if (tipoEntidade.equalsIgnoreCase("EST")) {
                    query.append(" INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo)");
                }
            }

            query.append(" WHERE ");
            query.append("prd.spd_codigo IN ('").append(CodedValues.SPD_EMPROCESSAMENTO);
            query.append("','").append(CodedValues.SPD_SEM_RETORNO).append("')");

            if (tipoEntidade.equalsIgnoreCase("EST")) {
                query.append(" AND org.est_codigo = :codigoEntidade ");
                queryParams.addValue("codigoEntidade", codigoEntidade);
            } else if (tipoEntidade.equalsIgnoreCase("ORG")) {
                query.append(" AND cnv.org_codigo = :codigoEntidade ");
                queryParams.addValue("codigoEntidade", codigoEntidade);
            }

            final List<Integer> resultSet = jdbc.queryForList(query.toString(), queryParams, Integer.class);
            for (Integer prdCodigo : resultSet) {
                preStat.setString(1, DBHelper.getNextId());
                preStat.setInt(2, prdCodigo);
                preStat.setString(3, tocCodigo);
                preStat.setString(4, ocpObs);
                preStat.setString(5, usuCodigo);
                preStat.executeUpdate();
            }
        } catch (final DataAccessException | SQLException | com.zetra.econsig.exception.MissingPrimaryKeyException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeStatement(preStat);
            DBHelper.releaseConnection(conn);;
        }
    }

    /**
     * Seleciona parcelas para integração de acordo com os critérios passados.
     * Se retorno atrasado, consulta as parcelas na tabela histórica, ao invés
     * da tabela do período.
     *
     * @param camposChave : lista dos campos de identificação da parcela
     * @param criterio    : critérios para a busca da parcela em processsamento
     * @param atrasado    : true se for retorno atrasado
     * @return            : lista de parcelas que estão em processamento
     * @throws DAOException
     */
    @Override
    public List<TransferObject> getPrdEmProcessamento(List<String> camposChave, TransferObject criterio, boolean atrasado) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final boolean permitePriorizarServico = ParamSist.paramEquals(CodedValues.TPC_PERMITE_PRIORIZAR_SERVICO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        final boolean permitePriorizarVerba = ParamSist.paramEquals(CodedValues.TPC_PERMITE_PRIORIZAR_VERBA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        String fields = "ADE_CODIGO" + MySqlDAOFactory.SEPARADOR
                      + "ADE_TIPO_VLR" + MySqlDAOFactory.SEPARADOR
                      + "PRD_CODIGO" + MySqlDAOFactory.SEPARADOR
                      + "PRD_NUMERO" + MySqlDAOFactory.SEPARADOR
                      + "PRD_DATA_DESCONTO" + MySqlDAOFactory.SEPARADOR
                      + "PRD_VLR_PREVISTO";

        String campos = "ade.ade_codigo AS ADE_CODIGO, "
                      + "coalesce(ade.ade_tipo_vlr, '" + CodedValues.TIPO_VLR_FIXO + "') AS ADE_TIPO_VLR, "
                      + "prd.prd_codigo AS PRD_CODIGO, "
                      + "prd.prd_numero AS PRD_NUMERO, "
                      + "prd.prd_data_desconto AS PRD_DATA_DESCONTO, "
                      + "prd.prd_vlr_previsto AS PRD_VLR_PREVISTO";

        String camposOrdenacao = "PRD_DATA_DESCONTO, ";

        if (permitePriorizarServico) {
            campos += MySqlDAOFactory.SEPARADOR + " COALESCE(CAST(svc.svc_prioridade AS NUMBER(10,0)), 9999999) AS ORDER_SVC_PRIORIDADE";
            camposOrdenacao += "ORDER_SVC_PRIORIDADE, ";
        }
        if (permitePriorizarVerba) {
            campos += MySqlDAOFactory.SEPARADOR + " COALESCE(CAST(cnv.cnv_prioridade AS NUMBER(10,0)), 9999999) AS ORDER_CNV_PRIORIDADE";
            camposOrdenacao += "ORDER_CNV_PRIORIDADE, ";
        }

        campos += MySqlDAOFactory.SEPARADOR + " COALESCE(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini) AS ORDER_ANO_MES_INI"
                + MySqlDAOFactory.SEPARADOR + " COALESCE(ade.ade_data_ref, ade.ade_data) AS ORDER_DATA"
                + MySqlDAOFactory.SEPARADOR + " prd.prd_vlr_previsto AS ORDER_VLR_PREVISTO"
                + MySqlDAOFactory.SEPARADOR + " ade.ade_numero AS ORDER_ADE_NUMERO";

        camposOrdenacao += "ORDER_ANO_MES_INI, ORDER_DATA, ORDER_VLR_PREVISTO, ORDER_ADE_NUMERO";

        StringBuilder query = new StringBuilder();
        query.append("SELECT DISTINCT ").append(campos).append(" FROM ");
        if (atrasado) {
            query.append("tb_parcela_desconto prd");
        } else {
            query.append("tb_parcela_desconto_periodo prd");
        }
        query.append(" INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = prd.ade_codigo)");
        query.append(" INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo)");
        query.append(" INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo)");
        query.append(" INNER JOIN tb_consignataria csa ON (csa.csa_codigo = cnv.csa_codigo)");
        query.append(" INNER JOIN tb_servico svc ON (svc.svc_codigo = cnv.svc_codigo)");
        query.append(" INNER JOIN tb_orgao org ON (org.org_codigo = cnv.org_codigo)");
        query.append(" INNER JOIN tb_estabelecimento est ON (est.est_codigo = org.est_codigo)");
        query.append(" INNER JOIN tb_registro_servidor rse ON (rse.rse_codigo = ade.rse_codigo AND org.org_codigo = rse.org_codigo)");
        query.append(" INNER JOIN tb_servidor ser ON (ser.ser_codigo = rse.ser_codigo)");
        query.append(" WHERE 1=1 ");

        Iterator<String> it = camposChave.iterator();
        String campo, valor;
        while (it.hasNext()) {
            campo = it.next();
            if (campo.equals("SPD_CODIGOS")) {
                List<String> spdCodigos = (List<String>) criterio.getAttribute(campo);
                if (spdCodigos != null && spdCodigos.size() > 0) {
                    query.append(" AND prd.spd_codigo IN ('").append(TextHelper.join(spdCodigos, "','")).append("')");

                    if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_MULTIPLAS_LINHAS_RETORNO_MESMA_PRD, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                        // Em caso de múltiplas linhas de pagamento para a mesma parcela, só retorna parcelas já liquidadas caso ainda não estejam completamente pagas
                        query.append(" AND (prd.spd_codigo <> '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' OR coalesce(prd.prd_vlr_realizado, 0) < prd.prd_vlr_previsto)");
                    }
                }
            } else if (campo.equals("ANO_MES_DESCONTO")) {
                valor = (String) criterio.getAttribute(campo);
                if (TextHelper.isNull(valor)) {
                    valor = (String) criterio.getAttribute("PERIODO");
                }
                if (!TextHelper.isNull(valor)) {
                    query.append(" AND prd.prd_data_desconto = :prdDataDesconto");
                    queryParams.addValue("prdDataDesconto", valor);
                }
            } else if (campo.equals("ADE_ANO_MES_FIM")) {
                valor = (String) criterio.getAttribute(campo);
                if (!TextHelper.isNull(valor)) {
                    query.append(" AND COALESCE(TO_CHAR(ade.ade_ano_mes_fim, 'yyyy-mm-dd'), '2999-12-01') = :adeAnoMesFim");
                    queryParams.addValue("adeAnoMesFim", valor);
                }
            } else if (campo.equals("ADE_INDICE")) {
                valor = (String) criterio.getAttribute(campo);
                if (!TextHelper.isNull(valor)) {
                    query.append(" AND COALESCE(ade.ade_indice_exp, ade.ade_indice) = :adeIndice");
                    queryParams.addValue("adeIndice", valor);
                }
            } else if (campo.equals("ADE_DATA")) {
                valor = (String) criterio.getAttribute(campo);
                if (!TextHelper.isNull(valor)) {
                    query.append(" AND TO_CHAR(ade.ade_data, 'yyyy-mm-dd') = :adeData");
                    queryParams.addValue("adeData", valor);
                }
            } else if (campo.equals("PRD_VLR_PREVISTO")) {
                valor = (String) criterio.getAttribute(campo);
                if (!TextHelper.isNull(valor)) {
                    query.append(" AND prd.prd_vlr_previsto = :prdVlrPrevisto");
                    queryParams.addValue("prdVlrPrevisto", valor);
                }
            } else if (campo.equals("ADE_CODIGO")) {
                valor = (String) criterio.getAttribute(campo);
                if (!TextHelper.isNull(valor)) {
                    query.append(" AND ade.ade_codigo = :adeCodigo");
                    queryParams.addValue("adeCodigo", valor);
                }
            } else if (campo.equalsIgnoreCase("CNV_COD_VERBA")) {
                valor = (String) criterio.getAttribute(campo);
                if (!TextHelper.isNull(valor)) {
                    query.append(" AND cnv.cnv_cod_verba = :cnvCodVerba");
                    queryParams.addValue("cnvCodVerba", valor);
                }
            } else {
                valor = (String) criterio.getAttribute(campo);
                if (!TextHelper.isNull(valor)) {
                    query.append(" AND ").append(campo).append(" = '").append(TextHelper.escapeSql(valor)).append("'");
                }
            }
        }

        if (atrasado) {
            query.append(" AND NOT EXISTS (SELECT 1 FROM tb_arquivo_retorno_parcela arp where arp.ade_codigo = ade.ade_codigo and arp.prd_numero = prd.prd_numero)");
        }

        //DESENV-20786: Somente considerar parcelas para pagamento que algum momento já foram para a folha
        if(!atrasado && ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_DOIS_PERIODOS_EXPORTACAO_ABERTOS, AcessoSistema.getAcessoUsuarioSistema())) {
            query.append(" AND (EXISTS (SELECT 1 FROM tb_parcela_desconto prdMne WHERE prd.ade_codigo = prdMne.ade_codigo AND prdMne.mne_codigo IS NULL");
            query.append(") OR (EXISTS (SELECT 1 FROM tb_parcela_desconto_periodo pdpMne WHERE prd.ade_codigo = pdpMne.ade_codigo AND pdpMne.mne_codigo IS NULL))) ");
        }

        query.append(" ORDER BY ");
        if (ParamSist.getBoolParamSist(CodedValues.TPC_PRIORIZA_PAG_PARCELAS_CONTRATOS_EXPORTADOS, AcessoSistema.getAcessoUsuarioSistema())) {
            query.append(" CASE WHEN ade.mne_codigo IS NULL THEN 0 ELSE 1 END, ");
        } 
        query.append(camposOrdenacao);

        return MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fields, MySqlDAOFactory.SEPARADOR);
    }

    /**
     * Integra a parcela que identificada pelo adeCodigo e prdNumero
     * @param prdCodigo      : código da parcela
     * @param prdData        : PRD_DATA_REALIZADO, null para CURDATE()
     * @param prdVlr         : PRD_VLR_REALIZADO, null para PRD_VLR_PREVISTO
     * @param spdCodigo      : código a ser atribuido
     * @param tdeCodigo      : código do tipo de desconto desta parcela
     * @param atrasado       : true se retorno atrasado
     * @throws DAOException
     */
    @Override
    public void liquidaParcelas(Integer prdCodigo, String prdData, String prdVlr, String spdCodigo, String tdeCodigo, boolean atrasado) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        if (spdCodigo.equals(CodedValues.SPD_REJEITADAFOLHA)) {
            prdVlr = "0.00";
        } else if (TextHelper.isNull(prdVlr)) {
            LOG.warn("Valor realizado da parcela ausente. Assumindo valor Zero.");
            prdVlr = "0.00";
        }

        StringBuilder query = new StringBuilder();
        if (atrasado) {
            query.append("UPDATE tb_parcela_desconto prd");
        } else {
            query.append("UPDATE tb_parcela_desconto_periodo prd");
        }

        query.append(" SET ");

        if (tdeCodigo != null) {
            query.append("prd.tde_codigo = '").append(tdeCodigo).append("', ");
        }

        if (prdData != null && !retAtrasadoSomaAparcela) {
            query.append("prd.prd_data_realizado = '").append(prdData).append("', ");
        } else {
            query.append("prd.prd_data_realizado = CURRENT_DATE, ");
        }

        // copia o motivo de não exportação do contrato de origem para a parcela
        query.append("prd.mne_codigo = coalesce((select adeSub.mne_codigo from tb_aut_desconto adeSub where adeSub.ade_codigo = prd.ade_codigo), prd.mne_codigo), ");

        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_MULTIPLAS_LINHAS_RETORNO_MESMA_PRD, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            if (!retAtrasadoSomaAparcela) {
                if (spdCodigo.equals(CodedValues.SPD_REJEITADAFOLHA)) {
                    // Se for rejeitar, só o Faz se a parcela ainda não teve status alterado por outra linha de pagamento
                    query.append("prd.spd_codigo =");
                    query.append(" CASE WHEN prd.spd_codigo IN ('").append(CodedValues.SPD_EMPROCESSAMENTO).append("','").append(CodedValues.SPD_SEM_RETORNO).append("')");
                    query.append(" THEN '").append(CodedValues.SPD_REJEITADAFOLHA).append("'");
                    query.append(" ELSE prd.spd_codigo END, ");
                } else {
                    query.append("prd.spd_codigo = '").append(spdCodigo).append("', ");
                }
            } else {
                query.append("prd.spd_codigo = '").append(!spdCodigo.equals(CodedValues.SPD_LIQUIDADAFOLHA) ? CodedValues.SPD_LIQUIDADAFOLHA : spdCodigo).append("', ");
            }
            query.append("prd.prd_vlr_realizado = COALESCE(prd.prd_vlr_realizado, 0) + CAST(").append(prdVlr).append(" AS NUMBER(13,2))");
        } else if (!retAtrasadoSomaAparcela) {
            query.append("prd.spd_codigo = '").append(spdCodigo).append("', ");
            query.append("prd.prd_vlr_realizado = CAST(").append(prdVlr).append(" AS NUMBER(13,2))");
        } else //DESENV-10533: valor realizado no retorno atraso deve ser somado ao valor realizado no histórico de parcela
        if (spdCodigo.equals(CodedValues.SPD_LIQUIDADAFOLHA)) {
            query.append(" prd.prd_vlr_realizado = CASE WHEN prd.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' THEN ");
            query.append(" COALESCE(prd.prd_vlr_realizado, 0) + CAST(");
            query.append(prdVlr).append(" AS DECIMAL(13,2))");
            query.append(" ELSE ").append(prdVlr).append(" END, ");
            query.append("prd.spd_codigo = '").append(spdCodigo).append("' ");
        } else {
            // no retorno atrasado somando a parcela, para todos os demais status que não seja liquidada pela folha, deve-se atribuir este status.
            query.append("prd.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("', ");
            query.append("prd.prd_vlr_realizado = CAST(").append(prdVlr).append(" AS NUMBER(13,2))");
        }

        query.append(" WHERE prd.prd_codigo = '").append(prdCodigo).append("'");

        jdbc.update(query.toString(), queryParams);
    }

    /**
     * Integra todas as parcelas que estão em processamento para a entidade (EST ou ORG) ou para todas
     * @param prdData        : PRD_DATA_REALIZADO, null para CURDATE()
     * @param spdCodigo      : código a ser atribuido
     * @param tipoEntidade   : CSE, ORG ou EST
     * @param codigoEntidade : código da entidade sendo integrado
     * @throws DAOException
     */
    @Override
    public void liquidaParcelas(String prdData, String spdCodigo, String tipoEntidade, String codigoEntidade) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder query = new StringBuilder();

        query.append("UPDATE tb_parcela_desconto_periodo prd");
        query.append(" SET ");
        query.append("prd.spd_codigo = '").append(spdCodigo).append("', ");

        if (prdData != null) {
            query.append("prd.prd_data_realizado = '").append(prdData).append("', ");
        } else {
            query.append("prd.prd_data_realizado = CURRENT_DATE, ");
        }

        // copia o motivo de não exportação do contrato de origem para a parcela
        query.append("prd.mne_codigo = coalesce((select adeSub.mne_codigo from tb_aut_desconto adeSub where adeSub.ade_codigo = prd.ade_codigo), prd.mne_codigo), ");

        if (spdCodigo.equals(CodedValues.SPD_REJEITADAFOLHA)) {
            query.append("prd.prd_vlr_realizado = 0.00");
        } else {
            query.append("prd.prd_vlr_realizado = prd.prd_vlr_previsto");
        }

        query.append(" WHERE (");
        query.append("prd.spd_codigo = '").append(CodedValues.SPD_EMPROCESSAMENTO).append("' OR ");
        query.append("prd.spd_codigo = '").append(CodedValues.SPD_SEM_RETORNO).append("')");

        if (tipoEntidade.equalsIgnoreCase("ORG") || tipoEntidade.equalsIgnoreCase("EST") || tipoEntidade.equalsIgnoreCase("RSE")) {
            query.append(" AND EXISTS (SELECT 1 FROM tb_aut_desconto ade ");

            if (tipoEntidade.equalsIgnoreCase("ORG") || tipoEntidade.equalsIgnoreCase("EST")) {
                query.append(" INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo)");
                query.append(" INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo)");
            }
            if (tipoEntidade.equalsIgnoreCase("EST")) {
                query.append(" INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo)");
            }

            query.append(" WHERE prd.ade_codigo = ade.ade_codigo");

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
            query.append(")");
        }

        jdbc.update(query.toString(), queryParams);
    }

    /**
     * Seta o status das parcelas que estão em processamento para o status de
     * sem retorno. É utilizado pela rotina de retorno normal (não atrasado),
     * portanto deve sempre acessar a tabela de parcelas do período.
     *
     * @param orgCodigo : código do órgão
     * @param estCodigo : código do estabelecimento
     * @param usuCodigo : responsável pela operação
     * @throws DAOException
     */
    @Override
    public void parcelasSemRetorno(String orgCodigo, String estCodigo, String usuCodigo) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder query = new StringBuilder();
        query.append("UPDATE tb_parcela_desconto_periodo prd");
        query.append(" SET ");
        query.append("prd.spd_codigo = '").append(CodedValues.SPD_SEM_RETORNO).append("'");
        query.append(" WHERE ");
        query.append("prd.spd_codigo = '").append(CodedValues.SPD_EMPROCESSAMENTO).append("'");

        if (orgCodigo != null || estCodigo != null) {
            query.append(" AND EXISTS (SELECT 1 FROM tb_aut_desconto ade ");
            query.append(" INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo)");
            query.append(" INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo)");

            if (estCodigo != null) {
                query.append(" INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo)");
            }

            query.append(" WHERE prd.ade_codigo = ade.ade_codigo");

            if (estCodigo != null) {
                query.append(" AND org.est_codigo = :estCodigo");
                queryParams.addValue("estCodigo", estCodigo);
            } else if (orgCodigo != null) {
                query.append(" AND cnv.org_codigo = :orgCodigo");
                queryParams.addValue("orgCodigo", orgCodigo);
            }
            query.append(")");
        }

        LOG.trace(query.toString());
        jdbc.update(query.toString(), queryParams);
    }

    /**
     * Move as parcelas e as ocorrências do período para as tabelas históricas:
     * tb_parcela_desconto_periodo ==> tb_parcela_desconto
     * tb_ocorrencia_parcela_periodo ==> tb_ocorrencia_parcela
     *
     * @param orgCodigos
     * @param estCodigos
     * @param periodo
     * @throws DAOException
     */
    @Override
    public void moverParcelasIntegradas(List<String> orgCodigos, List<String> estCodigos, List<String> rseCodigos, List<String> periodos, boolean ignorarAusenciaParcelas) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("periodos", periodos);
        try {
            int rows = 0;

            StringBuilder query = new StringBuilder();
            StringBuilder sufixo = new StringBuilder();

            // Monta o sufixo de acordo com os parâmetro informados
            if ((estCodigos != null && estCodigos.size() > 0) || (orgCodigos != null && orgCodigos.size() > 0)) {
                sufixo.append(" AND EXISTS (SELECT 1 FROM tb_aut_desconto ade");
                sufixo.append(" INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo)");
                sufixo.append(" INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo)");

                if (estCodigos != null && estCodigos.size() > 0) {
                    query.append(" INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo)");
                }

                sufixo.append(" WHERE tb_parcela_desconto_periodo.ade_codigo = ade.ade_codigo");

                if (estCodigos != null && estCodigos.size() > 0) {
                    sufixo.append(" AND org.est_codigo IN (:estCodigos)");
                    queryParams.addValue("estCodigos", estCodigos);
                } else {
                    sufixo.append(" AND cnv.org_codigo IN (:orgCodigos)");
                    queryParams.addValue("orgCodigos", orgCodigos);
                }
                sufixo.append(")");
            } else if (rseCodigos != null && rseCodigos.size() > 0) {
                sufixo.append(" AND EXISTS (SELECT 1 FROM tb_aut_desconto ade");
                sufixo.append(" WHERE tb_parcela_desconto_periodo.ade_codigo = ade.ade_codigo");
                sufixo.append(" AND ade.rse_codigo IN (:rseCodigos))");
                queryParams.addValue("rseCodigos", rseCodigos);
            }

            LOG.debug("=== MOVE AS PARCELAS E AS OCORRENCIAS PARA AS TABELAS HISTORICAS ===");

            // Insere as parcelas do periodo na tabela de parcelas já integradas
            query.setLength(0);
            query.append("INSERT INTO tb_parcela_desconto (ade_codigo, prd_numero, spd_codigo, tde_codigo, prd_data_desconto, prd_data_realizado, prd_vlr_previsto, prd_vlr_realizado, mne_codigo) ");
            query.append("SELECT ade_codigo, prd_numero, spd_codigo, tde_codigo, prd_data_desconto, prd_data_realizado, prd_vlr_previsto, prd_vlr_realizado, mne_codigo ");
            query.append("FROM tb_parcela_desconto_periodo ");
            query.append("WHERE prd_data_desconto IN (:periodos)");
            query.append(sufixo);
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            if (rows == 0) {
                if (!ignorarAusenciaParcelas) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.atualizar.parcela.periodo.invalido", (AcessoSistema) null, periodos.get(0)));
                    throw new DAOException("mensagem.erro.atualizar.parcela.periodo.invalido", (AcessoSistema) null, periodos.get(0));
                }
                return;
            }

            // Insere as ocorrências de parcelas do periodo na tabela de ocorrências de parcelas já integradas
            query.setLength(0);
            query.append("INSERT INTO tb_ocorrencia_parcela (ocp_codigo, prd_codigo, toc_codigo, usu_codigo, ocp_obs, ocp_data) ");
            query.append("SELECT ocp.ocp_codigo, prd.prd_codigo, ocp.toc_codigo, ocp.usu_codigo, ocp.ocp_obs, ocp.ocp_data ");
            query.append("FROM tb_ocorrencia_parcela_periodo ocp ");
            query.append("INNER JOIN tb_parcela_desconto_periodo pdp ON (pdp.prd_codigo = ocp.prd_codigo) ");
            query.append("INNER JOIN tb_parcela_desconto prd ON (prd.ade_codigo = pdp.ade_codigo and prd.prd_numero = pdp.prd_numero and prd.prd_data_desconto = pdp.prd_data_desconto) ");
            query.append("WHERE prd.prd_data_desconto IN (:periodos)");
            query.append(sufixo);
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Apaga as ocorrencias do período
            query.setLength(0);
            query.append("DELETE FROM tb_ocorrencia_parcela_periodo ");
            query.append("WHERE EXISTS (SELECT 1 FROM tb_parcela_desconto_periodo ");
            query.append("WHERE tb_parcela_desconto_periodo.prd_codigo = tb_ocorrencia_parcela_periodo.prd_codigo ");
            query.append("  AND prd_data_desconto IN (:periodos)");
            query.append(sufixo);
            query.append(")");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Apaga as parcelas do período
            query.setLength(0);
            query.append("DELETE FROM tb_parcela_desconto_periodo ");
            query.append("WHERE prd_data_desconto IN (:periodos)");
            query.append(sufixo);
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            LOG.debug("=== FIM / MOVE AS PARCELAS E AS OCORRENCIAS PARA AS TABELAS HISTORICAS ===");
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void moverParcelasIntegradasPorRse(String rseCodigo, String periodo) throws DAOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Altera o status das parcelas presentes na tabela de parcelas do período, que possuem
     * data de desconto maior que o período atual de processamento, do status "spdOrigem"
     * para "spdDestino". Utilizado quando dois períodos de exportação estão abertos, e
     * durante o retorno as parcelas futuras são movidas para outro status, não interferindo
     * no processamento.
     * @param orgCodigos
     * @param estCodigos
     * @param spdOrigem
     * @param spdDestino
     */
    @Override
    public void alterarStatusParcelasPosPeriodo(List<String> orgCodigos, List<String> estCodigos, String spdOrigem, String spdDestino) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder query = new StringBuilder();
        query.append("UPDATE tb_parcela_desconto_periodo prd ");
        query.append("SET prd.spd_codigo = '").append(spdDestino).append("' ");
        query.append("WHERE prd.spd_codigo = '").append(spdOrigem).append("' ");
        query.append("AND EXISTS (SELECT 1 FROM tb_aut_desconto ade ");
        query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
        query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
        query.append("WHERE ade.ade_codigo = prd.ade_codigo ");
        query.append("AND prd.prd_data_desconto > (SELECT MAX(pex.pex_periodo) FROM tb_periodo_exportacao pex WHERE cnv.org_codigo = pex.org_codigo) ");

        if (estCodigos != null && estCodigos.size() > 0) {
            query.append(" AND cnv.org_codigo IN (SELECT org.org_codigo FROM tb_orgao org WHERE org.est_codigo IN (:estCodigos))");
            queryParams.addValue("estCodigos", estCodigos);
        } else if (orgCodigos != null && orgCodigos.size() > 0) {
            query.append(" AND cnv.org_codigo IN (:orgCodigos)");
            queryParams.addValue("orgCodigos", orgCodigos);
        }
        query.append(")");

        LOG.trace(query.toString());
        int rows = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + rows);
    }

    /**
     * Adiciona/Subtrai da quantidade de parcelas pagas de um contrato, o número de parcelas
     * presentes na tabela de parcelas do período anteriores ao período atual de exportação.
     * É utilizado no caso de exportação com antecipação de período, quando dois ou mais
     * períodos permanecem abertos, sem retorno, simultâneamente. Os períodos subsequentes
     * devem considerar as parcelas dos períodos anteriores como pagas, apesar de ainda
     * não terem recebido retorno.
     * @param orgCodigos
     * @param estCodigos
     * @param incrementar
     */
    @Override
    public void ajustarPrdPagasExpPeriodoSimultaneo(List<String> orgCodigos, List<String> estCodigos, boolean incrementar) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            final String dataMinima = "0001-01-01";


            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            StringBuilder query = new StringBuilder();
            query.append("CALL dropTableIfExists('tb_tmp_acrescimo_pagas')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CALL createTemporaryTable('tb_tmp_acrescimo_pagas (ade_codigo varchar2(32), acrescimo number(10,0), valor_folha number(13,2), maior_data_parcela date, PRIMARY KEY (ade_codigo))')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("insert into tb_tmp_acrescimo_pagas (ade_codigo, acrescimo, valor_folha, maior_data_parcela) ");
            query.append("select ade.ade_codigo, count(*), coalesce(ade.ade_vlr_folha, ade.ade_vlr), max(case when prd.mne_codigo is null then prd.prd_data_desconto else TO_DATE('").append(dataMinima).append("', 'YYYY-MM-DD') end) ");
            query.append("from tb_parcela_desconto_periodo prd ");
            query.append("inner join tb_aut_desconto ade on (prd.ade_codigo = ade.ade_codigo) ");
            query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("where prd.prd_data_desconto < (select min(pex.pex_periodo) from tb_periodo_exportacao pex where cnv.org_codigo = pex.org_codigo) ");

            if (estCodigos != null && estCodigos.size() > 0) {
                query.append("and cnv.org_codigo in (select org.org_codigo from tb_orgao org where org.est_codigo in (:estCodigos)) ");
                queryParams.addValue("estCodigos", estCodigos);
            } else if (orgCodigos != null && orgCodigos.size() > 0) {
                query.append("and cnv.org_codigo in (:orgCodigos) ");
                queryParams.addValue("orgCodigos", orgCodigos);
            }

            // DESENV-6049 : Que não possua ocorrência de reimplante para o período a ser exportado, evitando que seja
            // reconhecido como uma alteração, ao invés de inclusão.
            query.append("and not exists ( ");
            query.append("select 1 from tb_ocorrencia_autorizacao oca ");
            query.append("where ade.ade_codigo = oca.ade_codigo ");
            query.append("and oca.toc_codigo = '").append(CodedValues.TOC_RELANCAMENTO).append("' ");
            query.append("and oca.oca_periodo >= (select min(pex.pex_periodo) from tb_periodo_exportacao pex where cnv.org_codigo = pex.org_codigo) ");
            query.append(") ");

            query.append("group by ade.ade_codigo ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            if (incrementar) {
                // DESENV-17947 : Define o "valor_folha" como sendo o valor previsto da última parcela gerada que não tenha motivo de não exportação
                query.setLength(0);
                query.append("update tb_tmp_acrescimo_pagas tmp ");
                query.append("set tmp.valor_folha = (select prd.prd_vlr_previsto ");
                query.append("from tb_parcela_desconto_periodo prd ");
                query.append("where prd.ade_codigo = tmp.ade_codigo ");
                query.append("and prd.prd_data_desconto = tmp.maior_data_parcela ");
                query.append(") ");
                query.append("where tmp.maior_data_parcela is not null ");
                query.append("and tmp.maior_data_parcela > TO_DATE('").append(dataMinima).append("', 'YYYY-MM-DD')");
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);

                // DESENV-17947 : ade_paga e ade_vlr_folha originais são salvoo no método hisDAO.salvarAdePaga
                query.setLength(0);
                query.append("update tb_aut_desconto ade ");
                query.append("set ade.ade_prd_pagas = coalesce(ade.ade_prd_pagas, 0) + (select tmp.acrescimo from tb_tmp_acrescimo_pagas tmp where ade.ade_codigo = tmp.ade_codigo), ");
                query.append("ade.ade_paga = 'S', ");
                query.append("ade.ade_vlr_folha = (select tmp.valor_folha from tb_tmp_acrescimo_pagas tmp where ade.ade_codigo = tmp.ade_codigo) ");
                query.append("where exists (select 1 from tb_tmp_acrescimo_pagas tmp where ade.ade_codigo = tmp.ade_codigo) ");
                LOG.trace(query.toString());
                int rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);

            } else {
                // DESENV-17947 : Não precisa restaurar o ade_paga nem o ade_vlr_folha, pois é feito no hisDAO.recuperarAdePaga
                query.setLength(0);
                query.append("update tb_aut_desconto ade ");
                query.append("set ade.ade_prd_pagas = coalesce(ade.ade_prd_pagas, 0) - ");
                query.append("(select tmp.acrescimo from tb_tmp_acrescimo_pagas tmp where ade.ade_codigo = tmp.ade_codigo) ");
                query.append("where exists (select 1 from tb_tmp_acrescimo_pagas tmp where ade.ade_codigo = tmp.ade_codigo) ");
                LOG.trace(query.toString());
                int rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);
            }

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Reduz o valor das parcelas do período para as consignações que possuem ocorrência de
     * reimplante com redução de valor da parcela, de acordo com a margem restante do servidor.
     * @param orgCodigos : o código do órgão que está sendo exportado, nulo para todos
     * @param estCodigos : o código do estabelecimento que está sendo exportado, nulo para todos
     * @param verbas     : os códigos dos serviços exportados
     * @throws DAOException
     */
    @Override
    public void reduzirValorParcelaReimplante(List<String> orgCodigos, List<String> estCodigos, List<String> verbas) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            StringBuilder query = new StringBuilder();
            query.append("CALL dropTableIfExists('tb_tmp_reducao_vlr_parcela')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CALL createTemporaryTable('tb_tmp_reducao_vlr_parcela (");
            query.append("prd_codigo number(11) not null, ");
            query.append("rse_codigo varchar2(32) not null, ");
            query.append("ade_inc_margem number(6) not null, ");
            query.append("valor_atual number(13,2) not null, ");
            query.append("valor_novo number(13,2), ");
            query.append("primary key (prd_codigo))')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CALL createIndexOnTemporaryTable('ix_tb_tmp_reducao_vlr_parcela', 'tb_tmp_reducao_vlr_parcela', 'ade_inc_margem, rse_codigo')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("insert into tb_tmp_reducao_vlr_parcela (prd_codigo, rse_codigo, ade_inc_margem, valor_atual, valor_novo) ");
            query.append("select prd.prd_codigo, rse.rse_codigo, coalesce(ade.ade_inc_margem, 1), ade.ade_vlr, ");
            query.append("case when coalesce(ade.ade_inc_margem, 1) = 1 then ade.ade_vlr + rse.rse_margem_rest ");
            query.append("     when coalesce(ade.ade_inc_margem, 1) = 2 then ade.ade_vlr + rse.rse_margem_rest_2 ");
            query.append("     when coalesce(ade.ade_inc_margem, 1) = 3 then ade.ade_vlr + rse.rse_margem_rest_3 ");
            query.append(" else ade.ade_vlr + mrs.mrs_margem_rest end ");
            query.append("from tb_parcela_desconto_periodo prd ");
            query.append("inner join tb_aut_desconto ade on (prd.ade_codigo = ade.ade_codigo) ");
            query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("inner join tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
            query.append("inner join tb_periodo_exportacao pex on (org.org_codigo = pex.org_codigo) ");
            query.append("inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) ");
            query.append("left outer join tb_margem_registro_servidor mrs on (mrs.mar_codigo = ade.ade_inc_margem and mrs.rse_codigo = rse.rse_codigo) ");
            // Contrato incide margem
            query.append("where coalesce(ade.ade_inc_margem, 1) != 0 ");
            // Contrato possui ocorrência de relançamento com redução de valor da parcela
            query.append("and exists (select 1 from tb_ocorrencia_autorizacao oca where ade.ade_codigo = oca.ade_codigo ");
            query.append("and oca.toc_codigo = '").append(CodedValues.TOC_RELANCAMENTO_COM_REDUCAO_VALOR).append("' ");
            query.append("and oca.oca_data between pex.pex_data_ini and pex.pex_data_fim) ");
            // Não atualiza contratos alterados por decisão judicial
            query.append("and ade.ade_vlr_parcela_folha IS NULL ");

            if (verbas != null && verbas.size() > 0) {
                query.append("and cnv.cnv_cod_verba in (:verbas) ");
                    queryParams.addValue("verbas", verbas);
            }
            if (orgCodigos != null && orgCodigos.size() > 0) {
                query.append("and org.org_codigo in (:orgCodigos) ");
                    queryParams.addValue("orgCodigos", orgCodigos);
            }
            if (estCodigos != null && estCodigos.size() > 0) {
                query.append("and org.est_codigo in (:estCodigos) ");
                    queryParams.addValue("estCodigos", estCodigos);
            }
            LOG.trace(query.toString());
            int rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("update tb_parcela_desconto_periodo prd ");
            query.append("set prd_vlr_previsto = (select tmp.valor_novo from tb_tmp_reducao_vlr_parcela tmp where tmp.prd_codigo = prd.prd_codigo) ");
            query.append("where exists (select 1 from tb_tmp_reducao_vlr_parcela tmp where tmp.prd_codigo = prd.prd_codigo ");
            query.append("and tmp.valor_atual > tmp.valor_novo ");
            query.append("and tmp.valor_novo > 0) ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }


    @Override
    public void insereParcelasEdicaoDeFluxo(List<String> orgCodigos, List<String> estCodigos, List<String> rseCodigos, AcessoSistema responsavel) throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void liquidaParcelasPagamentoBoleto(List<String> orgCodigos, List<String> estCodigos, List<String> periodos, AcessoSistema responsavel) throws DAOException {
        throw new UnsupportedOperationException();
    }
}
