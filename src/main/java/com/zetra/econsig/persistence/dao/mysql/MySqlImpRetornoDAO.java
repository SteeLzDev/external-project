package com.zetra.econsig.persistence.dao.mysql;

import java.util.Iterator;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.generic.GenericImpRetornoDAO;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: MySqlImpRetornoDAO</p>
 * <p>Description: Implementacao do DAO de Importação de Retorno para o MySql</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlImpRetornoDAO extends GenericImpRetornoDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlImpRetornoDAO.class);

    @Override
    public void criarTabelasImportacaoRetorno() throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            int rows = 0;
            final StringBuilder query = new StringBuilder();

            query.setLength(0);
            query.append("DROP TABLE IF EXISTS tb_tmp_retorno_parcelas");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("CREATE TABLE tb_tmp_retorno_parcelas ( ");
            query.append(" id_linha int NOT NULL, ");
            query.append(" ade_codigo varchar(32) NOT NULL, ");
            query.append(" ade_prd_pagas int default 0, ");
            query.append(" ade_data datetime NOT NULL, ");
            query.append(" ade_numero bigint NOT NULL, ");
            query.append(" prd_codigo int NOT NULL, ");
            query.append(" prd_numero smallint NOT NULL, ");
            query.append(" prd_data_desconto date NOT NULL, ");
            query.append(" prd_vlr_previsto decimal(13,2) NOT NULL default 0.00, ");
            query.append(" pode_pagar_exato char(1) NOT NULL default 'N', ");
            query.append(" valor_exato char(1) NOT NULL default 'N', ");
            query.append(" processada char(1) NOT NULL default 'N', ");
            query.append(" KEY IX_LINHA (id_linha), ");
            query.append(" KEY IX_LINHA_PAGA (id_linha, pode_pagar_exato), ");
            query.append(" KEY IX_ADE_PRD (ade_codigo, prd_numero), ");
            query.append(" KEY IX_PRD (prd_codigo) ");
            query.append(")");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);


            query.setLength(0);
            query.append("DROP TABLE IF EXISTS tb_tmp_retorno_parcelas_exatas");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("CREATE TABLE tb_tmp_retorno_parcelas_exatas (");
            query.append(" id_linha int NOT NULL, ");
            query.append(" KEY IX_LINHA (id_linha)");
            query.append(")");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);


            query.setLength(0);
            query.append("DROP TABLE IF EXISTS tb_tmp_retorno_parcelas_selecionadas");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("CREATE TABLE tb_tmp_retorno_parcelas_selecionadas (");
            query.append(" id_linha int NOT NULL, ");
            query.append(" KEY IX_LINHA (id_linha)");
            query.append(")");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);


            query.setLength(0);
            query.append("DROP TABLE IF EXISTS tb_tmp_retorno_parcelas_consolidadas");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("CREATE TABLE tb_tmp_retorno_parcelas_consolidadas (");
            query.append("id_linha int NOT NULL, ");
            query.append("ade_codigo varchar(32) NULL, ");
            query.append("qtde_parcelas int NOT NULL default 0, ");
            query.append("vlr_total decimal(13,2), ");
            query.append("KEY IX_LINHA (id_linha)");
            query.append(")");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);


            query.setLength(0);
            query.append("DROP TABLE IF EXISTS tb_tmp_retorno_parcelas_consolidadas_inversa");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("CREATE TABLE tb_tmp_retorno_parcelas_consolidadas_inversa (");
            query.append("ade_codigo varchar(32) NOT NULL, ");
            query.append("qtde_linhas int NOT NULL default 0, ");
            query.append("vlr_total decimal(13,2) NOT NULL default 0, ");
            query.append("PRIMARY KEY (ade_codigo)");
            query.append(")");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);


            if (ParamSist.paramEquals(CodedValues.TPC_TEM_PROCESSAMENTO_FERIAS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                query.setLength(0);
                query.append("DROP TABLE IF EXISTS tb_tmp_retorno_parcelas_ferias");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);

                query.setLength(0);
                query.append("CREATE TABLE tb_tmp_retorno_parcelas_ferias ( ");
                query.append(" id_linha int NOT NULL, ");
                query.append(" ade_codigo varchar(32) NOT NULL, ");
                query.append(" ade_prd_pagas int default 0, ");
                query.append(" ade_data datetime NOT NULL, ");
                query.append(" ade_numero bigint NOT NULL, ");
                query.append(" prd_codigo int NOT NULL, ");
                query.append(" prd_numero smallint NOT NULL, ");
                query.append(" prd_vlr_previsto decimal(13,2) NOT NULL default 0.00, ");
                query.append(" pode_pagar_exato char(1) NOT NULL default 'N', ");
                query.append(" valor_exato char(1) NOT NULL default 'N', ");
                query.append(" processada char(1) NOT NULL default 'N', ");
                query.append(" KEY IX_LINHA (id_linha), ");
                query.append(" KEY IX_LINHA_PAGA (id_linha, pode_pagar_exato), ");
                query.append(" KEY IX_ADE_PRD (ade_codigo, prd_numero), ");
                query.append(" KEY IX_PRD (prd_codigo) ");
                query.append(")");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);


                query.setLength(0);
                query.append("DROP TABLE IF EXISTS tb_tmp_retorno_parcelas_exatas_ferias");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);

                query.setLength(0);
                query.append("CREATE TABLE tb_tmp_retorno_parcelas_exatas_ferias (");
                query.append(" id_linha int NOT NULL, ");
                query.append(" KEY IX_LINHA (id_linha)");
                query.append(")");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);


                query.setLength(0);
                query.append("DROP TABLE IF EXISTS tb_tmp_retorno_parcelas_selecionadas_ferias");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);

                query.setLength(0);
                query.append("CREATE TABLE tb_tmp_retorno_parcelas_selecionadas_ferias (");
                query.append(" id_linha int NOT NULL, ");
                query.append(" KEY IX_LINHA (id_linha)");
                query.append(")");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);


                query.setLength(0);
                query.append("DROP TABLE IF EXISTS tb_tmp_retorno_parcelas_consolidadas_ferias");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);

                query.setLength(0);
                query.append("CREATE TABLE tb_tmp_retorno_parcelas_consolidadas_ferias (");
                query.append("id_linha int NOT NULL, ");
                query.append("qtde_parcelas int NOT NULL default 0, ");
                query.append("vlr_total decimal(13,2), ");
                query.append("KEY IX_LINHA (id_linha)");
                query.append(")");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);
            }

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Atualiza as linhas que são de férias para que não sejam utilizadas no processamento normal.
     * @param stat
     * @throws DataAccessException
     */
    @Override
    protected void atualizaCampoFerias() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        int rows = 0;
        String query;
        if (ParamSist.getBoolParamSist(CodedValues.TPC_TEM_PROCESSAMENTO_FERIAS, AcessoSistema.getAcessoUsuarioSistema())) {
            // Linhas que vieram com a data de desconto com período futuro (usando org_identificador)
            query = "UPDATE tb_arquivo_retorno art "
                    + "INNER JOIN tb_orgao org ON (org.org_identificador = art.org_identificador) "
                    + "INNER JOIN tb_periodo_exportacao pex ON (org.org_codigo = pex.org_codigo) "
                    + "SET art.art_ferias = 1 "
                    + "WHERE art.mapeada = 'S' AND art.art_ferias IS NULL AND art.ano_mes_desconto > pex.pex_periodo";
            LOG.trace(query);
            rows = jdbc.update(query, queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Linhas que vieram com a data de desconto com período futuro (usando cnv_cod_verba)
            query = "UPDATE tb_arquivo_retorno art "
                    + "INNER JOIN tb_convenio cnv ON (cnv.cnv_cod_verba = art.cnv_cod_verba) "
                    + "INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo) "
                    + "SET art.art_ferias = 1 "
                    + "WHERE art.mapeada = 'S' AND art.art_ferias IS NULL AND art.ano_mes_desconto > pex.pex_periodo";
            LOG.trace(query);
            rows = jdbc.update(query, queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Marca as linhas restantes como não sendo de férias
            query = "UPDATE tb_arquivo_retorno art "
                    + "SET art.art_ferias = 0 "
                    + "WHERE art.mapeada = 'S' AND art.art_ferias IS NULL";
            LOG.trace(query);
            rows = jdbc.update(query, queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Seta o periodo para o próximo das linhas de férias que não tem perido definido (usando cnv_cod_verba)
            query = "UPDATE tb_arquivo_retorno art "
                    + "INNER JOIN tb_convenio cnv ON (cnv.cnv_cod_verba = art.cnv_cod_verba) "
                    + "INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo) "
                    + "SET art.ano_mes_desconto = DATE_ADD(pex.pex_periodo, interval 1 month) "
                    + "WHERE art.mapeada = 'S' AND art.art_ferias = 1 AND (art.ano_mes_desconto IS NULL OR art.ano_mes_desconto = pex.pex_periodo)";
            LOG.trace(query);
            rows = jdbc.update(query, queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Seta o periodo para o próximo das linhas de férias que não tem perido definido (usando org_identificador)
            query = "UPDATE tb_arquivo_retorno art "
                    + "INNER JOIN tb_orgao org ON (org.org_identificador = art.org_identificador) "
                    + "INNER JOIN tb_periodo_exportacao pex ON (org.org_codigo = pex.org_codigo) "
                    + "SET art.ano_mes_desconto = DATE_ADD(pex.pex_periodo, interval 1 month) "
                    + "WHERE art.mapeada = 'S' AND art.art_ferias = 1 AND (art.ano_mes_desconto IS NULL OR art.ano_mes_desconto = pex.pex_periodo)";
            LOG.trace(query);
            rows = jdbc.update(query, queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Atualiza a observação da parcela de férias para aquelas que estão com a observação padrão
            query = "UPDATE tb_arquivo_retorno art "
                    + "SET art.ocp_obs = '" + ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.com.retorno.ferias", (AcessoSistema) null) + "' "
                    + "WHERE art.art_ferias = 1 AND art.ocp_obs = '" + ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.com.retorno", (AcessoSistema) null) + "'";
            LOG.trace(query);
            rows = jdbc.update(query, queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } else {
            // Marca as linhas restantes como não sendo de férias
            query = "UPDATE tb_arquivo_retorno art "
                    + "SET art.art_ferias = 0 "
                    + "WHERE art.mapeada = 'S'";
            LOG.trace(query);
            rows = jdbc.update(query, queryParams);
            LOG.trace("Linhas afetadas: " + rows);
        }
    }

    /**
     * Seta o identificador dos órgãos na tabela tb_arquivo_retorno
     * para aqueles órgãos que não estiverem setados. Obtém a informação
     * através do join com a tabela tb_registro_servidor.
     * @param stat : Statement para execução do SQL
     * @throws DAOException
     */
    @Override
    protected void setarIdnOrgao() throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder query = new StringBuilder();
        int rows = 0;

        // Atribui identificador do órgão quando não enviado filtrando por matrícula ativa e CPF
        query.append("UPDATE tb_arquivo_retorno art ");
        query.append("INNER JOIN tb_registro_servidor rse ON (rse.rse_matricula = art.rse_matricula) ");
        query.append("INNER JOIN tb_orgao org ON (org.org_codigo = rse.org_codigo) ");
        query.append("INNER JOIN tb_servidor ser ON (ser.ser_codigo = rse.ser_codigo AND ser.ser_cpf = art.ser_cpf) ");
        query.append("SET art.org_identificador = org.org_identificador ");
        query.append("WHERE art.org_identificador IS NULL ");
        query.append("AND rse.srs_codigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("') ");

        LOG.trace(query.toString());
        rows = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + rows);
        query.setLength(0);

        // Atribui identificador do órgão quando não enviado filtrando por matrícula ativa
        query.append("UPDATE tb_arquivo_retorno art ");
        query.append("INNER JOIN tb_registro_servidor rse ON (rse.rse_matricula = art.rse_matricula) ");
        query.append("INNER JOIN tb_orgao org ON (org.org_codigo = rse.org_codigo) ");
        query.append("SET art.org_identificador = org.org_identificador ");
        query.append("WHERE art.org_identificador IS NULL ");
        query.append("AND rse.srs_codigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("') ");

        LOG.trace(query.toString());
        rows = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + rows);
        query.setLength(0);

        // Atribui identificador do órgão quando não enviado filtrando por matrícula e CPF
        query.append("UPDATE tb_arquivo_retorno art ");
        query.append("INNER JOIN tb_registro_servidor rse ON (rse.rse_matricula = art.rse_matricula) ");
        query.append("INNER JOIN tb_orgao org ON (org.org_codigo = rse.org_codigo) ");
        query.append("INNER JOIN tb_servidor ser ON (ser.ser_codigo = rse.ser_codigo AND ser.ser_cpf = art.ser_cpf) ");
        query.append("SET art.org_identificador = org.org_identificador ");
        query.append("WHERE art.org_identificador IS NULL ");

        LOG.trace(query.toString());
        rows = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + rows);
        query.setLength(0);

        // Atribui identificador do órgão quando não enviado filtrando por matrícula
        query.append("UPDATE tb_arquivo_retorno art ");
        query.append("INNER JOIN tb_registro_servidor rse ON (rse.rse_matricula = art.rse_matricula) ");
        query.append("INNER JOIN tb_orgao org ON (org.org_codigo = rse.org_codigo) ");
        query.append("SET art.org_identificador = org.org_identificador ");
        query.append("WHERE art.org_identificador IS NULL ");

        LOG.trace(query.toString());
        rows = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + rows);
        query.setLength(0);
    }

    /**
     * Zera os campos folha, indicando se o contrato foi pago, valor e datas da folha
     * @param orgCodigos : os códigos dos órgãos, nulo para todos
     * @param estCodigos : os códigos dos estabelecimentos, nulo para todos
     * @param rseCodigos : os códigos dos registros servidores, nulo para todos
     * @param responsavel : usuário responsável pela operação
     * @throws DAOException
     */
    @Override
    public void zeraCamposFolha(List<String> orgCodigos, List<String> estCodigos, List<String> rseCodigos, AcessoSistema responsavel) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder query = new StringBuilder();

        query.append("UPDATE tb_aut_desconto ade ");

        if ((orgCodigos != null && orgCodigos.size() > 0) || (estCodigos != null && estCodigos.size() > 0)) {
            query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
            if (estCodigos != null && estCodigos.size() > 0) {
                query.append("INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo) ");
            }
        }

        query.append("SET ade_paga = 'N', ade_vlr_folha = null, ade_prazo_folha = null ");
        if (!ParamSist.paramEquals(CodedValues.TPC_MANTEM_DATA_INI_FIM_FOLHA_RETORNO, CodedValues.TPC_SIM, responsavel)) {
            query.append(", ade_ano_mes_ini_folha = null ");
            query.append(", ade_ano_mes_fim_folha = null ");
        }
        query.append("WHERE (1=1) ");

        if (orgCodigos != null && orgCodigos.size() > 0) {
            query.append("AND (cnv.org_codigo in (:orgCodigos)) ");
            queryParams.addValue("orgCodigos", orgCodigos);
        }
        if (estCodigos != null && estCodigos.size() > 0) {
            query.append("AND (org.est_codigo in (:estCodigos)) ");
            queryParams.addValue("estCodigos", estCodigos);
        }
        if (rseCodigos != null && rseCodigos.size() > 0) {
            query.append("AND (ade.rse_codigo in (:rseCodigos)) ");
            queryParams.addValue("rseCodigos", rseCodigos);
        }

        // "Zera" os campos folha de contratos que possuem parcelas pendentes de integração, ou de contratos suspensos
        // que não possuem parcela para o período, pois estes não receberão retorno da folha e podem ficar com a informação incorreta.
        query.append("AND (EXISTS (SELECT 1 FROM tb_parcela_desconto_periodo prd WHERE prd.ade_codigo = ade.ade_codigo AND prd.spd_codigo IN ('4','8')) ");
        query.append("OR  (NOT EXISTS (SELECT 1 FROM tb_parcela_desconto_periodo prd WHERE prd.ade_codigo = ade.ade_codigo) AND ade.sad_codigo in ('6','10')))");

        LOG.trace(query.toString());
        jdbc.update(query.toString(), queryParams);
    }

    /**
     * Zera os campos de valor e data realizada das parcelas que serão processadas no retorno
     * @param orgCodigos : os códigos dos órgãos, nulo para todos
     * @param estCodigos : os códigos dos estabelecimentos, nulo para todos
     * @param responsavel : usuário responsável pela operação
     * @throws DAOException
     */
    @Override
    public void zeraInformacaoRetornoParcelas(List<String> orgCodigos, List<String> estCodigos, AcessoSistema responsavel) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder query = new StringBuilder();

        query.append("UPDATE tb_parcela_desconto_periodo prd ");
        query.append("INNER JOIN tb_aut_desconto ade ON (prd.ade_codigo = ade.ade_codigo) ");
        query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
        query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");

        if (estCodigos != null && estCodigos.size() > 0) {
            query.append("INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo) ");
        }

        query.append("INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo) ");
        query.append("SET prd_vlr_realizado = 0.00, prd_data_realizado = null ");
        query.append("WHERE prd.spd_codigo IN ('").append(CodedValues.SPD_EMPROCESSAMENTO).append("','").append(CodedValues.SPD_SEM_RETORNO).append("') ");
        query.append("AND prd.prd_data_desconto = pex.pex_periodo ");

        if (orgCodigos != null && orgCodigos.size() > 0) {
            query.append("AND (cnv.org_codigo in (':orgCodigos)) ");
            queryParams.addValue("orgCodigos", orgCodigos);
        }
        if (estCodigos != null && estCodigos.size() > 0) {
            query.append("AND (org.est_codigo in (:estCodigos)) ");
            queryParams.addValue("estCodigos", estCodigos);
        }

        LOG.trace(query.toString());
        jdbc.update(query.toString(), queryParams);
    }

    /**
     * Cria tabela para pagamento exato das parcelas, na fase 1, de acordo com as
     * informações do arquivo de retorno carregado previamente.
     * @param camposChave
     * @param criterio
     * @param atrasado
     * @throws DAOException
     */
    @Override
    public void criaTabelaParcelasRetorno(List<String> camposChave, CustomTransferObject criterio, boolean atrasado) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("nomeArqRetorno", nomeArqRetorno);

        try {
            int rows = 0;
            final StringBuilder query = new StringBuilder();

            query.append("DELETE FROM tb_tmp_retorno_parcelas");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("INSERT INTO tb_tmp_retorno_parcelas (id_linha, ade_codigo, ade_prd_pagas, ade_data, ade_numero, pode_pagar_exato, processada, prd_codigo, prd_numero, prd_data_desconto, prd_vlr_previsto, valor_exato) ");
            query.append("SELECT art.id_linha, ade.ade_codigo, ade.ade_prd_pagas, ade.ade_data, ade.ade_numero, ");
            query.append("'N' as pode_pagar_exato, ");
            query.append("'N' as processada, ");
            query.append("prd.prd_codigo, ");
            query.append("prd.prd_numero, ");
            query.append("prd.prd_data_desconto, ");
            query.append("prd.prd_vlr_previsto, ");
            query.append("(CASE WHEN art.prd_vlr_realizado = prd.prd_vlr_previsto THEN 'S' ELSE 'N' END) as valor_exato ");
            if (atrasado) {
                query.append("FROM tb_parcela_desconto prd ");
            } else {
                query.append("FROM tb_parcela_desconto_periodo prd ");
            }
            query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = prd.ade_codigo) ");
            query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_consignataria csa ON (csa.csa_codigo = cnv.csa_codigo) ");
            query.append("INNER JOIN tb_servico svc ON (svc.svc_codigo = cnv.svc_codigo) ");
            query.append("INNER JOIN tb_orgao org ON (org.org_codigo = cnv.org_codigo) ");
            query.append("INNER JOIN tb_estabelecimento est ON (est.est_codigo = org.est_codigo) ");
            query.append("INNER JOIN tb_registro_servidor rse ON (rse.rse_codigo = ade.rse_codigo) ");
            query.append("INNER JOIN tb_servidor ser ON (ser.ser_codigo = rse.ser_codigo) ");
            query.append("INNER JOIN tb_arquivo_retorno art ON (art.mapeada = 'S' AND art.nome_arquivo = :nomeArqRetorno) ");
            query.append("WHERE art.art_ferias = 0 ");
            query.append("AND art.processada = 'N' ");

            Iterator<String> it = camposChave.iterator();
            String campo;
            Object atributo;
            while (it.hasNext()) {
                campo = it.next();
                atributo = criterio.getAttribute(campo);

                if (campo.equalsIgnoreCase("SPD_CODIGOS")) {
                    if (!atrasado) {
                        List<String> spdCodigos = (List<String>) atributo;
                        if (spdCodigos != null && spdCodigos.size() > 0) {
                            query.append(" AND prd.SPD_CODIGO IN ('").append(TextHelper.join(spdCodigos, "','")).append("')");
                        }
                    }
                } else if (campo.equalsIgnoreCase("ANO_MES_DESCONTO")) {
                    if (atrasado) {
                        query.append(" AND (prd.prd_data_desconto = art.ano_mes_desconto)");
                    }
                } else if (campo.equalsIgnoreCase("ORG_IDENTIFICADOR")) {
                    if ("S".equals(atributo)) {
                        query.append(" AND (org.org_identificador = art.org_identificador)");
                    }
                } else if (campo.equalsIgnoreCase("EST_IDENTIFICADOR")) {
                    if ("S".equals(atributo)) {
                        query.append(" AND (est.est_identificador = art.est_identificador)");
                    }
                } else if (campo.equalsIgnoreCase("CSA_IDENTIFICADOR")) {
                    if ("S".equals(atributo)) {
                        query.append(" AND (csa.csa_identificador = art.csa_identificador)");
                    }
                } else if (campo.equalsIgnoreCase("SVC_IDENTIFICADOR")) {
                    if ("S".equals(atributo)) {
                        query.append(" AND (svc.svc_identificador = art.svc_identificador)");
                    }
                } else if (campo.equalsIgnoreCase("CNV_COD_VERBA")) {
                    if ("S".equals(atributo)) {
                        query.append(" AND (cnv.cnv_cod_verba = art.cnv_cod_verba)");
                    }
                } else if (campo.equalsIgnoreCase("RSE_MATRICULA")) {
                    if ("S".equals(atributo)) {
                        query.append(" AND (rse.rse_matricula = art.rse_matricula)");
                    }
                } else if (campo.equalsIgnoreCase("RSE_MATRICULA_OR_INST")) {
                    if ("S".equals(atributo)) {
                        query.append(" AND (rse.rse_matricula = art.rse_matricula OR rse.rse_matricula_inst = art.rse_matricula)");
                    }
                } else if (campo.equalsIgnoreCase("ADE_ANO_MES_FIM")) {
                    if ("S".equals(atributo)) {
                        query.append(" AND (COALESCE(ade.ade_ano_mes_fim, '2999-12-01') = art.ade_ano_mes_fim)");
                    }
                } else if (campo.equalsIgnoreCase("ADE_INDICE")) {
                    if ("S".equals(atributo)) {
                        query.append(" AND (COALESCE(ade.ade_indice_exp, ade.ade_indice) = art.ade_indice)");
                    }
                } else if (campo.equalsIgnoreCase("ADE_COD_REG")) {
                    if ("S".equals(atributo)) {
                        query.append(" AND (ade.ade_cod_reg = art.ade_cod_reg)");
                    }
                } else if (campo.equalsIgnoreCase("ADE_DATA")) {
                    if ("S".equals(atributo)) {
                        query.append(" AND (DATE_FORMAT(ade.ade_data, '%Y-%m-%d') = DATE_FORMAT(art.ade_data, '%Y-%m-%d'))");
                    }
                } else if (campo.equalsIgnoreCase("ADE_NUMERO")) {
                    if ("S".equals(atributo)) {
                        query.append(" AND (ade.ade_numero = art.ade_numero)");
                    }
                } else if (campo.equalsIgnoreCase("SER_CPF")) {
                    if ("S".equals(atributo)) {
                        query.append(" AND (art.ser_cpf = ser.ser_cpf)");
                    }
                } else if (!campo.equalsIgnoreCase("PRD_VLR_PREVISTO")) {
                    throw new DAOException("mensagem.erro.importacao.retorno.campo.chave.invalido", (AcessoSistema) null, campo);
                }
            }

            if (atrasado) {
                query.append(" AND NOT EXISTS (SELECT 1 FROM tb_arquivo_retorno_parcela arp where arp.ade_codigo = ade.ade_codigo and arp.prd_numero = prd.prd_numero)");
            }
            if (ParamSist.paramEquals(CodedValues.TPC_PRIORIZA_PAGAMENTO_PRIORIDADE_SVC_CNV_RETORNO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                // A fase 1 faz pagamento exato, independente da prioridade do serviço. Com este parâmetro habilitado, a prioridade é respeitada,
                // então mesmo que o valor enviado seja exato ao valor esperado, a linha não é lançada como candidata a pagamento deixando à
                // cargo das fases subsequentes de pagamento (em espcial a fase 3.2) que já ordena as parcelas pelas prioridades ao fazer o pagamento.
                query.append(" AND NOT EXISTS (");
                query.append(" SELECT 1 FROM ").append(atrasado ? "tb_parcela_desconto" : "tb_parcela_desconto_periodo").append(" prd2");
                query.append(" INNER JOIN tb_aut_desconto ade2 ON (ade2.ade_codigo = prd2.ade_codigo) ");
                query.append(" INNER JOIN tb_verba_convenio vco2 ON (vco2.vco_codigo = ade2.vco_codigo) ");
                query.append(" INNER JOIN tb_convenio cnv2 ON (cnv2.cnv_codigo = vco2.cnv_codigo) ");
                query.append(" WHERE cnv.svc_codigo <> cnv2.svc_codigo");
                query.append("   AND cnv.csa_codigo = cnv2.csa_codigo");
                query.append("   AND cnv.org_codigo = cnv2.org_codigo");
                query.append("   AND cnv.cnv_cod_verba = cnv2.cnv_cod_verba");
                query.append("   AND ade.rse_codigo = ade2.rse_codigo");
                query.append("   AND prd.prd_data_desconto = prd2.prd_data_desconto");
                query.append(")");
            }

            //DESENV-20786: Somente considerar parcelas para pagamento que algum momento já foram para a folha
            if(!atrasado && ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_DOIS_PERIODOS_EXPORTACAO_ABERTOS, AcessoSistema.getAcessoUsuarioSistema())) {
                query.append(" AND (EXISTS (SELECT 1 FROM tb_parcela_desconto prdMne WHERE prd.ade_codigo = prdMne.ade_codigo AND prdMne.mne_codigo IS NULL");
                query.append(") OR (EXISTS (SELECT 1 FROM tb_parcela_desconto_periodo pdpMne WHERE prd.ade_codigo = pdpMne.ade_codigo AND pdpMne.mne_codigo IS NULL))) ");
            }

            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Seleciona as parcelas que tem pagamento exato e que são unicamente identificadas
     * por somente uma linha do arquivo de retorno.
     * @param valorExato : Indica se deve fazer pagamento apenas com valor exato
     */
    @Override
    public void selecionaParcelasPagamentoExato(boolean valorExato) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            int rows = 0;
            final StringBuilder query = new StringBuilder();

            query.append("DELETE FROM tb_tmp_retorno_parcelas_exatas");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Seleciona as linhas que tiveram apenas uma parcela selecionada para o valor exato
            query.setLength(0);
            query.append("INSERT INTO tb_tmp_retorno_parcelas_exatas (id_linha) ");
            query.append("SELECT rpd.id_linha ");
            query.append("FROM tb_tmp_retorno_parcelas rpd ");
            query.append("WHERE rpd.processada = 'N' ");
            query.append(valorExato ? "AND rpd.valor_exato = 'S' " : "");
            query.append("GROUP BY rpd.id_linha ");
            query.append("HAVING COUNT(*) = 1 ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("DELETE FROM tb_tmp_retorno_parcelas_selecionadas");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Seleciona as parcelas que tiveram apenas uma linha selecionada para o valor exato
            query.setLength(0);
            query.append("INSERT INTO tb_tmp_retorno_parcelas_selecionadas (id_linha) ");
            query.append("SELECT MIN(rpd.id_linha) ");
            query.append("FROM tb_tmp_retorno_parcelas rpd ");
            query.append(valorExato ? "WHERE rpd.valor_exato = 'S' " : "");
            query.append("GROUP BY rpd.ade_codigo, rpd.prd_numero ");
            query.append("HAVING COUNT(*) = 1 ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            /*
             * A linha que pode ser paga é aquela para a qual se encontrou apenas uma parcela,
             * sendo que essa parcela foi selecionada exclusivamente para essa linha.
             */
            query.setLength(0);
            query.append("UPDATE tb_tmp_retorno_parcelas rpd ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas_selecionadas rps ON (rps.id_linha = rpd.id_linha) ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas_exatas rpe ON (rpe.id_linha = rpd.id_linha) ");
            query.append("SET rpd.pode_pagar_exato = 'S' ");
            query.append(valorExato ? "WHERE rpd.valor_exato = 'S' " : "");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Cria tabela com as parcelas que consolidadas possuem linha de retorno para
     * pagamento exato ao valor total somado.
     */
    @Override
    public void criaTabelaConsolidacaoExata(boolean agrupaPorPeriodo, boolean agrupaPorAdeCodigo) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("nomeArqRetorno", nomeArqRetorno);
        try {
            int rows = 0;
            final StringBuilder query = new StringBuilder();

            query.append("DELETE FROM tb_tmp_retorno_parcelas_consolidadas");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            if (agrupaPorAdeCodigo) {
                query.append("INSERT INTO tb_tmp_retorno_parcelas_consolidadas (id_linha, ade_codigo, qtde_parcelas, vlr_total) ");
                query.append("SELECT art.id_linha, rpd.ade_codigo, count(*) as qtde_parcelas, sum(rpd.prd_vlr_previsto) as vlr_total ");
                query.append("FROM tb_arquivo_retorno art ");
                query.append("INNER JOIN tb_tmp_retorno_parcelas rpd ON (rpd.id_linha = art.id_linha AND rpd.processada = 'N') ");
                query.append("WHERE art.processada = 'N' AND art.art_ferias = 0 ");
                query.append("AND art.nome_arquivo = :nomeArqRetorno ");
                query.append("GROUP BY art.id_linha, rpd.ade_codigo ");
            } else {
                query.append("INSERT INTO tb_tmp_retorno_parcelas_consolidadas (id_linha, qtde_parcelas, vlr_total) ");
                query.append("SELECT art.id_linha, count(*) as qtde_parcelas, sum(rpd.prd_vlr_previsto) as vlr_total ");
                query.append("FROM tb_arquivo_retorno art ");
                query.append("INNER JOIN tb_tmp_retorno_parcelas rpd ON (rpd.id_linha = art.id_linha AND rpd.processada = 'N') ");
                query.append("WHERE art.processada = 'N' AND art.art_ferias = 0 ");
                query.append("AND art.nome_arquivo = :nomeArqRetorno ");
                if (agrupaPorPeriodo) {
                    query.append("AND art.ano_mes_desconto = rpd.prd_data_desconto ");
                }
                query.append("GROUP BY art.id_linha ");
            }
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("UPDATE tb_arquivo_retorno art ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas_consolidadas rpc ON (rpc.id_linha = art.id_linha) ");
            query.append("SET art.pode_pagar_consolidacao_exata = 'S' ");
            query.append("WHERE art.prd_vlr_realizado = rpc.vlr_total ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Cria tabela com as linhas que consolidadas possuem parcela para
     * pagamento exato ao valor total somado.
     */
    @Override
    public void criaTabelaConsolidacaoInversaExata() throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("nomeArqRetorno", nomeArqRetorno);

        try {
            int rows = 0;
            final StringBuilder query = new StringBuilder();

            query.append("DELETE FROM tb_tmp_retorno_parcelas_consolidadas_inversa");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("INSERT INTO tb_tmp_retorno_parcelas_consolidadas_inversa (ade_codigo, qtde_linhas, vlr_total) ");
            query.append("SELECT rpd.ade_codigo, count(*) as qtde_linhas, sum(art.prd_vlr_realizado) as vlr_total ");
            query.append("FROM tb_arquivo_retorno art ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas rpd ON (rpd.id_linha = art.id_linha AND rpd.processada = 'N') ");
            query.append("WHERE art.processada = 'N' AND art.art_ferias = 0 ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            query.append("GROUP BY rpd.ade_codigo");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("UPDATE tb_arquivo_retorno art ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas rpd ON (rpd.id_linha = art.id_linha AND rpd.processada = 'N') ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas_consolidadas_inversa rpc ON (rpc.ade_codigo = rpd.ade_codigo) ");
            query.append("SET art.pode_pagar_consolidacao_exata = 'S' ");
            query.append("WHERE rpd.prd_vlr_previsto = rpc.vlr_total ");
            query.append("AND art.processada = 'N' AND art.art_ferias = 0 ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public List<TransferObject> buscaLinhasConsolidacaoInversaExata() throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("nomeArqRetorno", nomeArqRetorno);

        final String fields = "id_linha,ade_codigo";
        final StringBuilder query = new StringBuilder();
        query.append("SELECT art.id_linha, rpd.ade_codigo FROM tb_arquivo_retorno art ");
        query.append("INNER JOIN tb_tmp_retorno_parcelas rpd ON (rpd.id_linha = art.id_linha) ");
        query.append("INNER JOIN tb_tmp_retorno_parcelas_consolidadas_inversa rpc on (rpc.ade_codigo = rpd.ade_codigo) ");
        query.append("WHERE art.mapeada = 'S' ");
        query.append("AND art.processada = 'N' ");
        query.append("AND art.pode_pagar_consolidacao_exata = 'S' ");
        query.append("AND art.nome_arquivo = :nomeArqRetorno ");
        query.append("AND art.art_ferias = 0 ");
        query.append("AND rpd.prd_vlr_previsto = rpc.vlr_total ");
        query.append("GROUP BY rpd.ade_codigo, art.id_linha ");
        query.append("ORDER BY rpd.ade_numero, art.id_linha ");
        LOG.trace(query.toString());

        return MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fields, MySqlDAOFactory.SEPARADOR);
    }

    /**
     * Efetua o pagamento das parcelas que foram unicamente identificadas por uma linha
     * do arquivo de retorno, sendo que esta linha também é exclusiva para a parcela.
     */
    @Override
    public void pagaParcelasSelecionadasDescontoTotal(boolean atrasado, AcessoSistema responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("nomeArqRetorno", nomeArqRetorno);
        queryParams.addValue("usuCodigo", responsavel != null ? responsavel.getUsuCodigo() : CodedValues.USU_CODIGO_SISTEMA);

        try {
            int rows = 0;
            final StringBuilder query = new StringBuilder();

            if (atrasado) {
                query.append("DELETE FROM ocp ");
                query.append("USING tb_arquivo_retorno art ");
                query.append("INNER JOIN tb_tmp_retorno_parcelas rpd ON (rpd.id_linha = art.id_linha) ");
                query.append("INNER JOIN tb_ocorrencia_parcela ocp ON (ocp.prd_codigo = rpd.prd_codigo) ");
                query.append("WHERE art.mapeada = 'S' ");
                query.append("AND art.nome_arquivo = :nomeArqRetorno ");
                query.append("AND ocp.toc_codigo IN ('").append(TextHelper.join(CodedValues.TOC_CODIGOS_RETORNO_PARCELA, "','")).append("') ");
                query.append("AND rpd.pode_pagar_exato = 'S' ");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);
            }

            query.setLength(0);
            query.append("SET @rownum := 0;");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            if (atrasado) {
                query.append("INSERT INTO tb_ocorrencia_parcela");
            } else {
                query.append("INSERT INTO tb_ocorrencia_parcela_periodo");
            }
            query.append(" (OCP_CODIGO, TOC_CODIGO, USU_CODIGO, PRD_CODIGO, OCP_DATA, OCP_OBS) ");
            query.append("SELECT CONCAT('Z', ");
            query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
            query.append("SUBSTRING(LPAD(art.id_linha, 7, '0'), 1, 7), ");
            query.append("SUBSTRING(LPAD(rpd.prd_numero, 5, '0'), 1, 5), ");
            query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), ");
            //DESENV-6177 caso o ade_tipo_vlr for diferente de fixo a ocorrência nunca é parcial
            //DESENV-6369 caso seja retorno de rejeito nunca será parcial
            query.append("CASE WHEN rpd.valor_exato = 'S' OR ade.ade_tipo_vlr <> 'F'  OR art.spd_codigo = '" + CodedValues.SPD_REJEITADAFOLHA + "' ");
            query.append("THEN '").append(CodedValues.TOC_RETORNO).append("' ELSE '").append(CodedValues.TOC_RETORNO_PARCIAL).append("' END, ");
            query.append(":usuCodigo, ");
            query.append("rpd.prd_codigo, now(), art.ocp_obs ");
            query.append("FROM tb_arquivo_retorno art ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas rpd ON (rpd.id_linha = art.id_linha) ");
            query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = rpd.ade_codigo) ");
            query.append("WHERE art.mapeada = 'S' AND art.art_ferias = 0 ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            query.append("AND rpd.pode_pagar_exato = 'S' ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Faz a atualização das tabelas MyISAM separadas, para obter melhor performance
            query.setLength(0);
            query.append("UPDATE tb_arquivo_retorno art ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas rpd ON (rpd.id_linha = art.id_linha) ");
            query.append("SET art.processada = 'S', ");
            query.append("rpd.processada = 'S' ");
            query.append("WHERE art.mapeada = 'S' AND art.art_ferias = 0 ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            query.append("AND rpd.pode_pagar_exato = 'S' ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Executa uma Sub-Query para obter melhor performance, em relação
            // a uma query normal com INNER JOIN
            query.setLength(0);
            query.append("UPDATE tb_aut_desconto ade ");
            if (atrasado) {
                query.append("INNER JOIN tb_parcela_desconto prd ON (prd.ade_codigo = ade.ade_codigo) ");
            } else {
                query.append("INNER JOIN tb_parcela_desconto_periodo prd ON (prd.ade_codigo = ade.ade_codigo) ");
            }
            query.append("INNER JOIN ( ");
            query.append("SELECT rpd.prd_codigo, art.prd_vlr_realizado, art.ade_prazo, art.prd_data_realizado, art.ade_ano_mes_ini, art.ade_ano_mes_fim, art.spd_codigo, art.tde_codigo ");
            query.append("FROM tb_arquivo_retorno art ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas rpd ON (rpd.id_linha = art.id_linha) ");
            query.append("WHERE art.mapeada = 'S' AND art.art_ferias = 0 ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            query.append("AND rpd.pode_pagar_exato = 'S' ");
            query.append(") AS X ");
            query.append("SET ");
            if (!retAtrasadoSomaAparcela) {
                query.append("ade.ade_vlr_folha = X.prd_vlr_realizado, ");
            } else {
                query.append("ade.ade_vlr_folha = if(X.spd_codigo = '" + CodedValues.SPD_LIQUIDADAFOLHA + "', ade.ade_vlr_folha + X.prd_vlr_realizado, X.prd_vlr_realizado), ");
            }
            query.append("ade.ade_prazo_folha = ifnull(X.ade_prazo, ade.ade_prazo), ");
            query.append("ade.ade_ano_mes_ini_folha = ifnull(X.ade_ano_mes_ini, ade.ade_ano_mes_ini_folha), ");
            query.append("ade.ade_ano_mes_fim_folha = ifnull(X.ade_ano_mes_fim, ade.ade_ano_mes_fim_folha), ");
            query.append("ade.ade_paga = if(X.spd_codigo = '" + CodedValues.SPD_REJEITADAFOLHA + "', 'N', 'S'), ");
            if (!retAtrasadoSomaAparcela) {
                query.append("prd.spd_codigo = X.spd_codigo, ");
            } else {
                query.append("prd.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("',");
            }
            query.append("prd.tde_codigo = X.tde_codigo, ");
            if (!retAtrasadoSomaAparcela) {
                query.append("prd.prd_data_realizado = ifnull(X.prd_data_realizado, curdate()), ");
                query.append("prd.prd_vlr_realizado = if(X.spd_codigo = '" + CodedValues.SPD_REJEITADAFOLHA + "', 0.00, X.prd_vlr_realizado) ");
            } else {
                //DESENV-10533: valor realizado no retorno atraso deve ser somado ao valor realizado no histórico de parcela
                query.append("prd.prd_data_realizado = curdate(), ");
                query.append("prd.prd_vlr_realizado = if(X.spd_codigo = '" + CodedValues.SPD_LIQUIDADAFOLHA + "', prd.prd_vlr_realizado + X.prd_vlr_realizado, X.prd_vlr_realizado) ");
            }
            query.append("WHERE prd.prd_codigo = X.prd_codigo ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Desfaz o retorno da folha do período informado por parâmetro.
     * OBS: O retorno deve ter sido concluído, pois a rotina considera que as parcelas
     * integradas estão na tabela tb_parcela_desconto.
     * @param orgCodigo : código do órgão (NULL para todos)
     * @param estCodigo : código do estabelecimento (NULL para todos)
     * @param periodo   : Periodo do retorno a desfeito
     * @param parcelas  : seleção dos períodos, caso de processamento de férias, que devem ser desfeitos
     * @param rseCodigo : código do registro servidor (NULL para todos)
     * @throws DAOException
     */
    @Override
    public void desfazerUltimoRetorno(String orgCodigo, String estCodigo, String periodo, String[] parcelas, String rseCodigo) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("periodo", periodo);

        int rows = 0;
        boolean isEst = !TextHelper.isNull(estCodigo);
        boolean isOrg = !TextHelper.isNull(orgCodigo);
        boolean isSer = !TextHelper.isNull(rseCodigo);

        try {
            final StringBuilder query = new StringBuilder();

            LOG.debug("=== CRIA TABELA COM AS PARCELAS A SEREM DESFEITAS");
            query.append("drop temporary table if exists tmp_desfaz_retorno_parcelas");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("create temporary table tmp_desfaz_retorno_parcelas (prd_codigo int, primary key (prd_codigo))");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("insert into tmp_desfaz_retorno_parcelas (prd_codigo) ");
            query.append("select prd.prd_codigo ");
            query.append("from tb_ocorrencia_parcela ocp ");
            query.append("inner join tb_parcela_desconto prd on (prd.prd_codigo = ocp.prd_codigo) ");
            if (isEst || isOrg || isSer) {
                query.append("inner join tb_aut_desconto ade on (ade.ade_codigo = prd.ade_codigo) ");
                query.append("inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) ");
                query.append("inner join tb_orgao org on (org.org_codigo = rse.org_codigo) ");
            }
            query.append("where prd.spd_codigo in ('").append(CodedValues.SPD_REJEITADAFOLHA).append("', '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("') ");
            if (parcelas == null || parcelas.length == 0) {
                query.append("and prd.prd_data_desconto = :periodo ");
            } else {
                query.append("and (");
                for (int i = 0; i < parcelas.length; i++) {
                    final String[] vlrParcelas = parcelas[i].split(";");

                    final String prdDataDescontoLabel = "prdDataDesconto_" + i;
                    final String prdDataRealizadoLabel = "prdDataRealizado_" + i;
                    final String tocCodigoLabel = "tocCodigo_" + i;

                    queryParams.addValue(prdDataDescontoLabel, vlrParcelas[0]);
                    queryParams.addValue(prdDataRealizadoLabel, vlrParcelas[1]);
                    queryParams.addValue(tocCodigoLabel, vlrParcelas[2]);

                    query.append("(prd.prd_data_desconto = :").append(prdDataDescontoLabel).append(" and");
                    query.append(" prd.prd_data_realizado = :").append(prdDataRealizadoLabel).append(" and");
                    query.append(" ocp.toc_codigo = :").append(tocCodigoLabel).append(")");

                    if (i+1 < parcelas.length) {
                        query.append(" or ");
                    }
                }
                query.append(") ");
            }
            if (isEst) {
                query.append("and org.est_codigo = :estCodigo ");
                queryParams.addValue("estCodigo", estCodigo);
            } else if (isOrg) {
                query.append("and org.org_codigo = :orgCodigo ");
                queryParams.addValue("orgCodigo", orgCodigo);
            } else if (isSer) {
                query.append("and rse.rse_codigo = :rseCodigo ");
                queryParams.addValue("rseCodigo", rseCodigo);
            }
            query.append("group by prd.prd_codigo ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);


            LOG.debug("=== EXCLUI OCORRENCIA DE RETORNO DA PARCELA");
            query.append("delete from tb_ocorrencia_parcela ");
            query.append("where prd_codigo in (select prd_codigo from tmp_desfaz_retorno_parcelas) ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);


            LOG.debug("=== ALTERA O STATUS DOS CONTRATOS CONCLUIDOS PARA DEFERIDOS OU EM ANDAMENTO");
            query.append("update tb_parcela_desconto prd ");
            query.append("inner join tb_aut_desconto ade on (ade.ade_codigo = prd.ade_codigo) ");
            query.append("inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) ");
            query.append("set ade.sad_codigo = (case when coalesce(ade.ade_prd_pagas, 0) > 0 then '").append(CodedValues.SAD_EMANDAMENTO).append("' else '").append(CodedValues.SAD_DEFERIDA).append("' end) ");
            query.append("where prd.prd_codigo in (select prd_codigo from tmp_desfaz_retorno_parcelas) ");
            query.append("and oca.oca_data >= prd.prd_data_realizado ");
            query.append("and oca.toc_codigo in ('").append(CodedValues.TOC_CONCLUSAO_CONTRATO).append("', '").append(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO).append("') ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);


            LOG.debug("=== EXCLUI OCORRENCIA DE CONCLUSAO DE CONTRATO");
            query.append("delete from oca ");
            query.append("using tb_parcela_desconto prd ");
            query.append("inner join tb_aut_desconto ade on (ade.ade_codigo = prd.ade_codigo) ");
            query.append("inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) ");
            query.append("where prd.prd_codigo in (select prd_codigo from tmp_desfaz_retorno_parcelas) ");
            query.append("and oca.oca_data >= prd.prd_data_realizado ");
            query.append("and oca.toc_codigo in ('").append(CodedValues.TOC_CONCLUSAO_CONTRATO).append("', '").append(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO).append("') ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            LOG.debug("=== AJUSTA VALOR DO CONTRATO AO DESFAZER RETORNO SE O SEU VALOR FOI ALTERADO DURANTE O PROCESSAMENTO=== ");
            query.append("update tb_parcela_desconto prd ");
            query.append("inner join tb_aut_desconto ade on (ade.ade_codigo = prd.ade_codigo) ");
            query.append("INNER JOIN tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_param_svc_consignante pse on (cnv.svc_codigo = pse.svc_codigo) ");
            query.append("INNER JOIN tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) ");
            query.append("LEFT OUTER JOIN tb_ocorrencia_autorizacao oca on (ade.ade_codigo = oca.ade_codigo ");
            query.append("AND oca.toc_codigo = '").append(CodedValues.TOC_ALTERACAO_CONTRATO).append("' ");
            query.append("AND oca.oca_data > pex.pex_data_fim) ");
            query.append("set ade.ade_vlr = prd.prd_vlr_previsto ");
            query.append("where prd.prd_codigo in (select prd_codigo from tmp_desfaz_retorno_parcelas) ");
            query.append("and prd.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' "); // parcelas pagas
            query.append("AND pse.tps_codigo = '").append(CodedValues.TPS_ATUALIZA_ADE_VLR_NO_RETORNO).append("' ");
            query.append("AND pse.pse_vlr = '1' ");
            query.append("AND oca.oca_codigo IS NULL ");
            query.append("AND ade.ade_vlr <> prd.prd_vlr_previsto ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            LOG.debug("=== DIMINUI O NUMERO DE PARCELAS PAGAS / VOLTA SAD_CODIGO");
            query.append("update tb_parcela_desconto prd ");
            query.append("inner join tb_aut_desconto ade on (ade.ade_codigo = prd.ade_codigo) ");
            query.append("set ade.ade_prd_pagas = (case when coalesce(ade.ade_prd_pagas, 0) <= 0 then 0 else ade.ade_prd_pagas - 1 end), ");
            query.append("ade.ade_prd_pagas_total = (case when coalesce(ade.ade_prd_pagas_total, 0) <= 0 then 0 else ade.ade_prd_pagas_total - 1 end), ");
            query.append("ade.sad_codigo = (case ");
            query.append("  when (ade.sad_codigo in ('").append(CodedValues.SAD_EMANDAMENTO).append("','").append(CodedValues.SAD_CONCLUIDO).append("') and ade.ade_prd_pagas > 1) then '").append(CodedValues.SAD_EMANDAMENTO).append("' ");
            query.append("  when (ade.sad_codigo in ('").append(CodedValues.SAD_EMANDAMENTO).append("','").append(CodedValues.SAD_CONCLUIDO).append("') and ade.ade_prd_pagas = 1) then '").append(CodedValues.SAD_DEFERIDA).append("' ");
            query.append("  else ade.sad_codigo ");
            query.append("end) ");
            query.append("where prd.prd_codigo in (select prd_codigo from tmp_desfaz_retorno_parcelas) ");
            query.append("and prd.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' "); // parcelas pagas
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);


            LOG.debug("=== MOVE AS PARCELAS (DO PERIODO) PARA A TABELA DO PERIODO ALTERANDO O STATUS PARA EM PROCESSAMENTO");
            query.append("insert into tb_parcela_desconto_periodo (ade_codigo, prd_numero, spd_codigo, prd_data_desconto, prd_vlr_previsto) ");
            query.append("select ade_codigo, prd_numero, '").append(CodedValues.SPD_EMPROCESSAMENTO).append("' as spd_codigo, prd_data_desconto, prd_vlr_previsto from tb_parcela_desconto ");
            query.append("where prd_codigo in (select prd_codigo from tmp_desfaz_retorno_parcelas) ");
            query.append("and prd_data_desconto = :periodo");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);


            LOG.debug("=== APAGA AS PARCELAS MOVIDAS PARA A TABELA DO PERIODO");
            query.append("delete from tb_parcela_desconto ");
            query.append("where prd_codigo in (select prd_codigo from tmp_desfaz_retorno_parcelas) ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);


            LOG.debug("=== APAGA REGISTROS DA TABELA DE ARQUIVO DE RETORNO");
            if (isEst || isOrg) {
                query.append("delete from tb_arquivo_retorno ");
                query.append("using tb_arquivo_retorno ");
                query.append("inner join tb_orgao on (tb_orgao.org_identificador = tb_arquivo_retorno.org_identificador) ");
                query.append("where 1=1 ");
                if (isEst) {
                    query.append("and tb_orgao.est_codigo = :estCodigo ");
                    queryParams.addValue("estCodigo", estCodigo);
                } else {
                    query.append("and tb_orgao.org_codigo = :orgCodigo ");
                    queryParams.addValue("orgCodigo", orgCodigo);
                }
            } else {
                query.append("delete from tb_arquivo_retorno ");
            }
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);


            LOG.debug("=== APAGA REGISTROS DA TABELA DE ARQUIVO DE RETORNO PARCELA");
            if (isEst || isOrg) {
                query.append("delete from tb_arquivo_retorno_parcela ");
                query.append("using tb_arquivo_retorno_parcela ");
                query.append("inner join tb_aut_desconto on (tb_aut_desconto.ade_codigo = tb_arquivo_retorno_parcela.ade_codigo) ");
                query.append("inner join tb_registro_servidor on (tb_aut_desconto.rse_codigo = tb_registro_servidor.rse_codigo) ");
                if (isEst) {
                    query.append("inner join tb_orgao on (tb_orgao.org_codigo = tb_registro_servidor.org_codigo) ");
                }
                query.append("where 1=1 ");
                if (isEst) {
                    query.append("and tb_orgao.est_codigo = :estCodigo ");
                    queryParams.addValue("estCodigo", estCodigo);
                } else {
                    query.append("and tb_registro_servidor.org_codigo = :orgCodigo ");
                    queryParams.addValue("orgCodigo", orgCodigo);
                }
            } else {
                query.append("delete from tb_arquivo_retorno_parcela ");
            }
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Desfaz todos os movimentos financeiros exportado e aguardando retorno
     * com data base maior que o período informado
     * @param orgCodigo : código do órgão (NULL para todos)
     * @param estCodigo : código do estabelecimento (NULL para todos)
     * @param periodo   : Periodo do movimento a partir de onde será desfeito
     * @throws DAOException
     */
    @Override
    public void desfazerUltimoMovimento(String orgCodigo, String estCodigo, String periodo) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("periodo", periodo);

        int rows = 0;
        boolean isEst = !TextHelper.isNull(estCodigo);
        boolean isOrg = !TextHelper.isNull(orgCodigo);

        try {
            final StringBuilder query = new StringBuilder();

            LOG.debug("=== EXCLUI OCORRENCIA DE RETORNO DA PARCELA DO PERIODO");
            query.append("delete from tb_ocorrencia_parcela_periodo ");
            query.append("using tb_parcela_desconto_periodo ");
            if (isEst || isOrg) {
                query.append("inner join tb_aut_desconto on (tb_aut_desconto.ade_codigo = tb_parcela_desconto_periodo.ade_codigo) ");
                query.append("inner join tb_registro_servidor on (tb_aut_desconto.rse_codigo = tb_registro_servidor.rse_codigo) ");
                if (isEst) {
                    query.append("inner join tb_orgao on (tb_orgao.org_codigo = tb_registro_servidor.org_codigo) ");
                }
            }
            query.append("inner join tb_ocorrencia_parcela_periodo on (tb_ocorrencia_parcela_periodo.prd_codigo = tb_parcela_desconto_periodo.prd_codigo) ");
            query.append("where prd_data_desconto > :periodo ");
            if (isEst) {
                query.append("and tb_orgao.est_codigo = :estCodigo ");
                queryParams.addValue("estCodigo", estCodigo);
            } else if (isOrg) {
                query.append("and tb_registro_servidor.org_codigo = :orgCodigo ");
                queryParams.addValue("orgCodigo", orgCodigo);
            }
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            LOG.debug("=== APAGA AS PARCELAS DO PERIODO");
            query.append("delete from tb_parcela_desconto_periodo ");
            if (isEst || isOrg) {
                query.append("using tb_parcela_desconto_periodo ");
                query.append("inner join tb_aut_desconto on (tb_aut_desconto.ade_codigo = tb_parcela_desconto_periodo.ade_codigo) ");
                query.append("inner join tb_registro_servidor on (tb_aut_desconto.rse_codigo = tb_registro_servidor.rse_codigo) ");
                if (isEst) {
                    query.append("inner join tb_orgao on (tb_orgao.org_codigo = tb_registro_servidor.org_codigo) ");
                }
            }
            query.append("where prd_data_desconto > :periodo ");
            if (isEst) {
                query.append("and tb_orgao.est_codigo = :estCodigo ");
                queryParams.addValue("estCodigo", estCodigo);
            } else if (isOrg) {
                query.append("and tb_registro_servidor.org_codigo = :orgCodigo ");
                queryParams.addValue("orgCodigo", orgCodigo);
            }
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            LOG.debug("=== APAGA REGISTROS DA TABELA DE HISTORICO DE EXPORTACAO");
            query.append("delete from tb_historico_exportacao ");
            if (isEst || isOrg) {
                query.append("using tb_historico_exportacao ");
                query.append("inner join tb_orgao on (tb_orgao.org_codigo = tb_historico_exportacao.org_codigo) ");
            }
            query.append("where hie_periodo > :periodo ");
            if (isEst) {
                query.append("and tb_orgao.est_codigo = :estCodigo ");
                queryParams.addValue("estCodigo", estCodigo);
            } else if (isOrg) {
                query.append("and tb_orgao.org_codigo = :orgCodigo ");
                queryParams.addValue("orgCodigo", orgCodigo);
            }
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            LOG.debug("=== APAGA REGISTROS DA TABELA DE ARQUIVO DE MOVIMENTO");
            query.append("delete from tb_arquivo_movimento ");
            query.append("where pex_periodo > :periodo ");
            if (isEst) {
                query.append("and est_identificador in (select est_identificador from tb_estabelecimento where est_codigo = :estCodigo) ");
                queryParams.addValue("estCodigo", estCodigo);
            } else if (isOrg) {
                query.append("and org_identificador in (select org_identificador from tb_orgao where org_codigo = :orgCodigo) ");
                queryParams.addValue("orgCodigo", orgCodigo);
            }
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Atualiza o valor da ADE, cujo parametro de serviço manda atualizar,
     * de com acordo com o valor pago no retorno
     * @param proximoPeriodo : Periodo atual de lançamentos
     * @throws DAOException
     */
    @Override
    public void atualizarAdeVlrRetorno(String proximoPeriodo, String rseCodigo) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            int rows = 0;
            final StringBuilder query = new StringBuilder();

            LOG.debug("=== ATUALIZA ADE COM VALOR DIFERENTE NO RETORNO ===");
            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_ade_vlr_atualizado");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tb_tmp_ade_vlr_atualizado (");
            query.append("ADE_CODIGO varchar(32) NOT NULL, ");
            query.append("ADE_NUMERO bigint NOT NULL, ");
            query.append("VLR_ANTIGO decimal(13,2) NOT NULL, ");
            query.append("VLR_NOVO decimal(13,2) NOT NULL, ");
            query.append("PERIODO date NOT NULL, ");
            query.append("KEY IX_ADE (ADE_CODIGO)");
            query.append(") ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("INSERT INTO tb_tmp_ade_vlr_atualizado (ADE_CODIGO, ADE_NUMERO, VLR_ANTIGO, VLR_NOVO, PERIODO) ");
            query.append("SELECT DISTINCT ade.ade_codigo, ade.ade_numero, ade.ade_vlr, prd.prd_vlr_realizado, pex.pex_periodo_pos ");
            query.append("FROM tb_parcela_desconto_periodo prd ");
            query.append("INNER JOIN tb_aut_desconto ade on (ade.ade_codigo = prd.ade_codigo) ");
            query.append("INNER JOIN tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_param_svc_consignante pse on (cnv.svc_codigo = pse.svc_codigo) ");
            query.append("INNER JOIN tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) ");
            query.append("LEFT OUTER JOIN tb_ocorrencia_autorizacao oca on (ade.ade_codigo = oca.ade_codigo ");
            query.append("AND oca.toc_codigo = '").append(CodedValues.TOC_ALTERACAO_CONTRATO).append("' ");
            query.append("AND oca.oca_data > pex.pex_data_fim) ");
            query.append("WHERE ade.sad_codigo NOT IN ('").append(CodedValues.SAD_LIQUIDADA).append("', '").append(CodedValues.SAD_CONCLUIDO).append("') ");
            query.append("AND pse.tps_codigo = '").append(CodedValues.TPS_ATUALIZA_ADE_VLR_NO_RETORNO).append("' ");
            query.append("AND pse.pse_vlr = '1' ");
            query.append("AND oca.oca_codigo IS NULL ");
            query.append("AND prd.prd_data_desconto = pex.pex_periodo ");
            query.append("AND prd.prd_vlr_realizado <> prd.prd_vlr_previsto ");
            query.append("AND prd.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' ");
            query.append("AND ade.ade_vlr_parcela_folha IS NULL "); // Não atualiza contratos alterados por decisão judicial
            if (!TextHelper.isNull(rseCodigo)) {
                query.append("AND ade.rse_codigo = :rseCodigo ");
                queryParams.addValue("rseCodigo", rseCodigo);
            }
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("SET @rownum := 0;");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("INSERT INTO tb_ocorrencia_autorizacao (OCA_CODIGO, TMO_CODIGO, TOC_CODIGO, ADE_CODIGO, USU_CODIGO, OCA_DATA, OCA_PERIODO, OCA_OBS, OCA_ADE_VLR_NOVO, OCA_ADE_VLR_ANT) ");
            query.append("SELECT CONCAT('S', ");
            query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
            query.append("SUBSTRING(LPAD(ade_numero, 12, '0'), 1, 12), ");
            query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), ");
            query.append("NULL, '").append(CodedValues.TOC_AVISO).append("', ade_codigo, ").append(CodedValues.USU_CODIGO_SISTEMA).append(", NOW(), periodo, ");
            query.append("'").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.ade.vlr.retorno", (AcessoSistema) null)).append("', ");
            query.append("VLR_NOVO,VLR_ANTIGO ");
            query.append("FROM tb_tmp_ade_vlr_atualizado");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("UPDATE tb_aut_desconto INNER JOIN tb_tmp_ade_vlr_atualizado ON (tb_aut_desconto.ade_codigo = tb_tmp_ade_vlr_atualizado.ade_codigo) ");
            query.append("SET ade_vlr_ref = IFNULL(ade_vlr_ref, ade_vlr), ade_vlr = VLR_NOVO");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("UPDATE tb_aut_desconto ade ");
            query.append("INNER JOIN tb_tmp_ade_vlr_atualizado tmp ON (ade.ade_codigo = tmp.ade_codigo) ");
            query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_param_svc_consignante pse ON (pse.svc_codigo = cnv.svc_codigo) ");
            query.append("SET ade_ano_mes_ini_ref = '").append(proximoPeriodo).append("' ");
            query.append("WHERE tps_codigo = '").append(CodedValues.TPS_PRESERVA_DATA_ALTERACAO).append("' ");
            query.append("AND pse_vlr = '0' ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tb_tmp_ade_vlr_atualizado");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            LOG.debug("=== FIM ATUALIZA ADE COM VALOR DIFERENTE NO RETORNO ===");
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Cria tabela para pagamento exato das parcelas de férias, na fase 1, de acordo com as
     * informações do arquivo de retorno carregado previamente.
     * @param camposChave
     * @param criterio
     * @param atrasado
     * @throws DAOException
     */
    @Override
    public void criaTabelaParcelasRetornoFerias(List<String> camposChave, CustomTransferObject criterio, boolean atrasado) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("nomeArqRetorno", nomeArqRetorno);

        try {
            int rows = 0;
            final StringBuilder query = new StringBuilder();

            query.append("DELETE FROM tb_tmp_retorno_parcelas_ferias");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("INSERT INTO tb_tmp_retorno_parcelas_ferias (id_linha, ade_codigo, ade_prd_pagas, ade_data, ade_numero, pode_pagar_exato, processada, prd_codigo, prd_numero, prd_vlr_previsto, valor_exato) ");
            query.append("SELECT art.id_linha, ade.ade_codigo, COALESCE(ade.ade_prd_pagas, 0), ade.ade_data, ade.ade_numero, ");
            query.append("'N' as pode_pagar_exato, ");
            query.append("'N' as processada, ");
            query.append("'0' as prd_codigo, ");
            query.append("'0' as prd_numero, ");
            query.append("coalesce(ade.ade_vlr_parcela_folha, ade.ade_vlr) as prd_vlr_previsto, ");
            query.append("(CASE WHEN art.prd_vlr_realizado = coalesce(ade.ade_vlr_parcela_folha, ade.ade_vlr) THEN 'S' ELSE 'N' END) as valor_exato ");
            query.append("FROM tb_aut_desconto ade ");
            query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_consignataria csa ON (csa.csa_codigo = cnv.csa_codigo) ");
            query.append("INNER JOIN tb_servico svc ON (svc.svc_codigo = cnv.svc_codigo) ");
            query.append("INNER JOIN tb_orgao org ON (org.org_codigo = cnv.org_codigo) ");
            query.append("INNER JOIN tb_estabelecimento est ON (est.est_codigo = org.est_codigo) ");
            query.append("INNER JOIN tb_registro_servidor rse ON (rse.rse_codigo = ade.rse_codigo) ");
            query.append("INNER JOIN tb_servidor ser ON (ser.ser_codigo = rse.ser_codigo) ");
            query.append("INNER JOIN tb_arquivo_retorno art ON (art.mapeada = 'S' AND art.nome_arquivo = :nomeArqRetorno) ");
            query.append("WHERE art.art_ferias = 1 ");
            // Não utiliza linhas de férias que já tenha parcela no mês do desconto de férias
            query.append("AND NOT EXISTS ( ");
            query.append("  SELECT 1 FROM tb_parcela_desconto prd ");
            query.append("  WHERE prd.ade_codigo = ade.ade_codigo ");
            query.append("  AND prd.prd_data_desconto = art.ano_mes_desconto ");
            query.append(") ");
            // Não utiliza linhas de férias que já tenha parcela no mês do desconto de férias
            query.append("AND NOT EXISTS ( ");
            query.append("  SELECT 1 FROM tb_parcela_desconto_periodo pdp ");
            query.append("  WHERE pdp.ade_codigo = ade.ade_codigo ");
            query.append("  AND pdp.prd_data_desconto = art.ano_mes_desconto ");
            query.append("  AND pdp.spd_codigo <> '").append(CodedValues.SPD_AGUARD_PROCESSAMENTO).append("'");
            query.append(") ");

            Iterator<String> it = camposChave.iterator();
            String campo;
            Object atributo;
            while (it.hasNext()) {
                campo = it.next();
                atributo = criterio.getAttribute(campo);

                if ("S".equals(atributo)) {
                    if (campo.equalsIgnoreCase("ORG_IDENTIFICADOR")) {
                        query.append(" AND (org.org_identificador = art.org_identificador)");

                    } else if (campo.equalsIgnoreCase("EST_IDENTIFICADOR")) {
                        query.append(" AND (est.est_identificador = art.est_identificador)");

                    } else if (campo.equalsIgnoreCase("CSA_IDENTIFICADOR")) {
                        query.append(" AND (csa.csa_identificador = art.csa_identificador)");

                    } else if (campo.equalsIgnoreCase("SVC_IDENTIFICADOR")) {
                        query.append(" AND (svc.svc_identificador = art.svc_identificador)");

                    } else if (campo.equalsIgnoreCase("CNV_COD_VERBA")) {
                        query.append(" AND (cnv.cnv_cod_verba = art.cnv_cod_verba)");

                    } else if (campo.equalsIgnoreCase("RSE_MATRICULA")) {
                        query.append(" AND (rse.rse_matricula = art.rse_matricula)");

                    } else if (campo.equalsIgnoreCase("RSE_MATRICULA_OR_INST")) {
                        query.append(" AND (rse.rse_matricula = art.rse_matricula OR rse.rse_matricula_inst = art.rse_matricula)");

                    } else if (campo.equalsIgnoreCase("ADE_ANO_MES_FIM")) {
                        query.append(" AND (COALESCE(ade.ade_ano_mes_fim, '2999-12-01') = art.ade_ano_mes_fim)");

                    } else if (campo.equalsIgnoreCase("ADE_INDICE")) {
                        query.append(" AND (COALESCE(ade.ade_indice_exp, ade.ade_indice) = art.ade_indice)");

                    } else if (campo.equalsIgnoreCase("ADE_COD_REG")) {
                        query.append(" AND (ade.ade_cod_reg = art.ade_cod_reg)");

                    } else if (campo.equalsIgnoreCase("ADE_DATA")) {
                        query.append(" AND (DATE_FORMAT(ade.ade_data, '%Y-%m-%d') = DATE_FORMAT(art.ade_data, '%Y-%m-%d'))");

                    } else if (campo.equalsIgnoreCase("ADE_NUMERO")) {
                        query.append(" AND (ade.ade_numero = art.ade_numero)");

                    } else if (campo.equalsIgnoreCase("SER_CPF")) {
                        query.append(" AND (art.ser_cpf = ser.ser_cpf)");

                    } else  {
                        throw new DAOException("mensagem.erro.importacao.retorno.campo.chave.invalido", (AcessoSistema) null, campo);
                    }
                }
            }
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Atualiza o prd_numero da parcela de férias de acordo com as parcelas existentes na tb_parcela_desconto
            query.setLength(0);
            query.append("UPDATE tb_tmp_retorno_parcelas_ferias rpd ");
            query.append("INNER JOIN (");
            query.append("  SELECT prdX.ade_codigo, max(prdX.prd_numero) as prd_numero ");
            query.append("  FROM tb_parcela_desconto prdX ");
            query.append("  INNER JOIN tb_tmp_retorno_parcelas_ferias rpdX ON (rpdX.ade_codigo = prdX.ade_codigo) ");
            query.append("  GROUP BY prdX.ade_codigo");
            query.append(") AS prd ON (rpd.ade_codigo = prd.ade_codigo) ");
            query.append("SET rpd.prd_numero = prd.prd_numero + 1 ");
            query.append("WHERE prd.prd_numero >= rpd.prd_numero");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Atualiza o prd_numero da parcela de férias de acordo com as parcelas existentes na tb_parcela_desconto_periodo
            query.setLength(0);
            query.append("UPDATE tb_tmp_retorno_parcelas_ferias rpd ");
            query.append("INNER JOIN (");
            query.append("  SELECT prdX.ade_codigo, max(prdX.prd_numero) as prd_numero ");
            query.append("  FROM tb_parcela_desconto_periodo prdX ");
            query.append("  INNER JOIN tb_tmp_retorno_parcelas_ferias rpdX ON (rpdX.ade_codigo = prdX.ade_codigo) ");
            query.append("  GROUP BY prdX.ade_codigo");
            query.append(") AS prd ON (rpd.ade_codigo = prd.ade_codigo) ");
            query.append("SET rpd.prd_numero = prd.prd_numero + 1 ");
            query.append("WHERE prd.prd_numero >= rpd.prd_numero");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Atualiza o prd_numero da parcela de férias, caso o contrato não possua parcela em nenhuma das tabelas
            query.setLength(0);
            query.append("UPDATE tb_tmp_retorno_parcelas_ferias ");
            query.append("SET prd_numero = 1 ");
            query.append("WHERE prd_numero = 0");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Seleciona as parcelas de férias que tem pagamento exato e que são unicamente identificadas
     * por somente uma linha do arquivo de retorno.
     */
    @Override
    public void selecionaParcelasPagamentoExatoFerias() throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            int rows = 0;
            final StringBuilder query = new StringBuilder();

            query.append("DELETE FROM tb_tmp_retorno_parcelas_exatas_ferias");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Seleciona as linhas que tiveram apenas uma parcela selecionada para o valor exato
            query.setLength(0);
            query.append("INSERT INTO tb_tmp_retorno_parcelas_exatas_ferias (id_linha) ");
            query.append("SELECT rpd.id_linha ");
            query.append("FROM tb_tmp_retorno_parcelas_ferias rpd ");
            query.append("WHERE rpd.valor_exato = 'S' ");
            query.append("GROUP BY rpd.id_linha ");
            query.append("HAVING COUNT(*) = 1 ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("DELETE FROM tb_tmp_retorno_parcelas_selecionadas_ferias");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Seleciona as parcelas que tiveram apenas uma linha selecionada para o valor exato
            query.setLength(0);
            query.append("INSERT INTO tb_tmp_retorno_parcelas_selecionadas_ferias (id_linha) ");
            query.append("SELECT MIN(rpd.id_linha) ");
            query.append("FROM tb_tmp_retorno_parcelas_ferias rpd ");
            query.append("WHERE rpd.valor_exato = 'S' ");
            query.append("GROUP BY rpd.ade_codigo, rpd.prd_numero ");
            query.append("HAVING COUNT(*) = 1 ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            /*
             * A linha que pode ser paga é aquela para a qual se encontrou apenas uma parcela,
             * sendo que essa parcela foi selecionada exclusivamente para essa linha.
             */
            query.setLength(0);
            query.append("UPDATE tb_tmp_retorno_parcelas_ferias rpd ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas_selecionadas_ferias rps ON (rps.id_linha = rpd.id_linha) ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas_exatas_ferias rpe ON (rpe.id_linha = rpd.id_linha) ");
            query.append("SET rpd.pode_pagar_exato = 'S' ");
            query.append("WHERE rpd.valor_exato = 'S' ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Efetua o pagamento das parcelas de férias que foram unicamente identificadas por uma linha
     * do arquivo de retorno, sendo que esta linha também é exclusiva para a parcela.
     */
    @Override
    public void pagaParcelasSelecionadasDescontoTotalFerias(AcessoSistema responsavel) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("nomeArqRetorno", nomeArqRetorno);
        queryParams.addValue("usuCodigo", responsavel != null ? responsavel.getUsuCodigo() : CodedValues.USU_CODIGO_SISTEMA);

        try {
            int rows = 0;
            final StringBuilder query = new StringBuilder();

            query.append("SET @rownum := 0;");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("INSERT INTO tb_parcela_desconto ");
            query.append(" (ADE_CODIGO, PRD_NUMERO, TDE_CODIGO, SPD_CODIGO, PRD_DATA_DESCONTO, PRD_VLR_PREVISTO, PRD_VLR_REALIZADO, PRD_DATA_REALIZADO) ");
            query.append("SELECT rpd.ade_codigo, rpd.prd_numero, art.tde_codigo, art.spd_codigo, art.ano_mes_desconto, rpd.prd_vlr_previsto, art.prd_vlr_realizado, curdate() ");
            query.append("FROM tb_arquivo_retorno art ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas_ferias rpd ON (rpd.id_linha = art.id_linha) ");
            query.append("WHERE art.mapeada = 'S' AND art.art_ferias = 1 ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            query.append("AND rpd.pode_pagar_exato = 'S' ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Atualiza a tb_tmp_retorno_parcelas_ferias com os PRD_CODIGO recém gerados
            query.setLength(0);
            query.append("UPDATE tb_tmp_retorno_parcelas_ferias rpd ");
            query.append("INNER JOIN tb_arquivo_retorno art ON (rpd.id_linha = art.id_linha) ");
            query.append("INNER JOIN tb_parcela_desconto prd ON (prd.ade_codigo = rpd.ade_codigo AND prd.prd_numero = rpd.prd_numero) ");
            query.append("SET rpd.prd_codigo = prd.prd_codigo ");
            query.append("WHERE art.ano_mes_desconto = prd.prd_data_desconto ");
            query.append("AND art.mapeada = 'S' AND art.art_ferias = 1 ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            query.append("AND rpd.pode_pagar_exato = 'S' ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("INSERT INTO tb_ocorrencia_parcela (OCP_CODIGO, TOC_CODIGO, USU_CODIGO, PRD_CODIGO, OCP_DATA, OCP_OBS) ");
            query.append("SELECT CONCAT('Z', ");
            query.append("DATE_FORMAT(NOW(), '%y%m%d%H%i%S'), ");
            query.append("SUBSTRING(LPAD(art.id_linha, 7, '0'), 1, 7), ");
            query.append("SUBSTRING(LPAD(rpd.prd_numero, 5, '0'), 1, 5), ");
            query.append("SUBSTRING(LPAD(@rownum := COALESCE(@rownum, 0) + 1, 7, '0'), 1, 7)), ");
            query.append("'").append(CodedValues.TOC_RETORNO_FERIAS).append("', ");
            query.append(":usuCodigo, ");
            query.append("rpd.prd_codigo, now(), art.ocp_obs ");
            query.append("FROM tb_arquivo_retorno art ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas_ferias rpd ON (rpd.id_linha = art.id_linha) ");
            query.append("WHERE art.mapeada = 'S' AND art.art_ferias = 1 ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            query.append("AND rpd.pode_pagar_exato = 'S' ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Faz a atualização das tabelas MyISAM separadas, para obter melhor performance
            query.setLength(0);
            query.append("UPDATE tb_arquivo_retorno art ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas_ferias rpd ON (rpd.id_linha = art.id_linha) ");
            query.append("SET art.processada = 'S', ");
            query.append("rpd.processada = 'S' ");
            query.append("WHERE art.mapeada = 'S' AND art.art_ferias = 1 ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            query.append("AND rpd.pode_pagar_exato = 'S' ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Executa uma Sub-Query para obter melhor performance, em relação
            // a uma query normal com INNER JOIN
            query.setLength(0);
            query.append("UPDATE tb_aut_desconto ade ");
            query.append("INNER JOIN tb_parcela_desconto prd ON (prd.ade_codigo = ade.ade_codigo) ");
            query.append("INNER JOIN ( ");
            query.append("SELECT rpd.prd_codigo, art.prd_vlr_realizado, art.ade_prazo, art.prd_data_realizado, art.ade_ano_mes_ini, art.ade_ano_mes_fim, art.spd_codigo, art.tde_codigo ");
            query.append("FROM tb_arquivo_retorno art ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas_ferias rpd ON (rpd.id_linha = art.id_linha) ");
            query.append("WHERE art.mapeada = 'S' AND art.art_ferias = 1 ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            query.append("AND rpd.pode_pagar_exato = 'S' ");
            query.append(") AS X ");
            query.append("SET ");
            query.append("ade.ade_vlr_folha = X.prd_vlr_realizado, ");
            query.append("ade.ade_prazo_folha = ifnull(X.ade_prazo, ade.ade_prazo), ");
            query.append("ade.ade_ano_mes_ini_folha = ifnull(X.ade_ano_mes_ini, ade.ade_ano_mes_ini_folha), ");
            query.append("ade.ade_ano_mes_fim_folha = ifnull(X.ade_ano_mes_fim, ade.ade_ano_mes_fim_folha), ");
            query.append("ade.ade_paga = if(X.spd_codigo = '" + CodedValues.SPD_REJEITADAFOLHA + "', 'N', 'S'), ");
            query.append("prd.spd_codigo = X.spd_codigo, ");
            query.append("prd.tde_codigo = X.tde_codigo, ");
            query.append("prd.prd_data_realizado = ifnull(X.prd_data_realizado, curdate()), ");
            query.append("prd.prd_vlr_realizado = if(X.spd_codigo = '" + CodedValues.SPD_REJEITADAFOLHA + "', 0.00, X.prd_vlr_realizado) ");
            query.append("WHERE prd.prd_codigo = X.prd_codigo ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Cria tabela com as parcelas de férias que consolidadas possuem linha de retorno para
     * pagamento exato ao valor total somado.
     */
    @Override
    public void criaTabelaConsolidacaoExataFerias() throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("nomeArqRetorno", nomeArqRetorno);

        try {
            int rows = 0;
            final StringBuilder query = new StringBuilder();

            query.append("DELETE FROM tb_tmp_retorno_parcelas_consolidadas_ferias");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("INSERT INTO tb_tmp_retorno_parcelas_consolidadas_ferias (id_linha, qtde_parcelas, vlr_total) ");
            query.append("SELECT art.id_linha, count(*) as qtde_parcelas, sum(rpd.prd_vlr_previsto) as vlr_total ");
            query.append("FROM tb_arquivo_retorno art ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas_ferias rpd ON (rpd.id_linha = art.id_linha AND rpd.processada = 'N') ");
            query.append("WHERE art.processada = 'N' AND art.art_ferias = 1 ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            query.append("GROUP BY art.id_linha ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("UPDATE tb_arquivo_retorno art ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas_consolidadas_ferias rpc ON (rpc.id_linha = art.id_linha) ");
            query.append("SET art.pode_pagar_consolidacao_exata = 'S' ");
            query.append("WHERE art.prd_vlr_realizado = rpc.vlr_total ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void atualizarCsaCodigoTbArqRetorno() throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            int rows = 0;
            final StringBuilder query = new StringBuilder();
            query.append("UPDATE tb_arquivo_retorno art ");
            query.append("INNER JOIN tb_arquivo_retorno_parcela arp on (art.nome_arquivo = arp.nome_arquivo and art.id_linha = arp.id_linha) ");
            query.append("INNER JOIN tb_parcela_desconto prd on (arp.ade_codigo = prd.ade_codigo and arp.prd_numero = prd.prd_numero) ");
            query.append("INNER JOIN tb_aut_desconto ade on (prd.ade_codigo = ade.ade_codigo) ");
            query.append("INNER JOIN tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("SET art.csa_codigo = cnv.csa_codigo ");
            query.append("WHERE art.csa_codigo is null ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tmp_convenios_retorno");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tmp_convenios_retorno (");
            query.append("CNV_COD_VERBA varchar(32), ");
            query.append("CSA_CODIGO varchar(32), ");
            query.append("CSA_IDENTIFICADOR varchar(40), ");
            query.append("SVC_IDENTIFICADOR varchar(40), ");
            query.append("EST_IDENTIFICADOR varchar(40), ");
            query.append("ORG_IDENTIFICADOR varchar(40), ");
            query.append("PRIMARY KEY (CNV_COD_VERBA, CSA_IDENTIFICADOR, SVC_IDENTIFICADOR, EST_IDENTIFICADOR, ORG_IDENTIFICADOR)");
            query.append(")");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("INSERT INTO tmp_convenios_retorno (CNV_COD_VERBA, CSA_CODIGO, CSA_IDENTIFICADOR, SVC_IDENTIFICADOR, EST_IDENTIFICADOR, ORG_IDENTIFICADOR) ");
            query.append("SELECT cnv.cnv_cod_verba, csa.csa_codigo, csa.csa_identificador, svc.svc_identificador, est.est_identificador, org.org_identificador ");
            query.append("FROM tb_convenio cnv ");
            query.append("INNER JOIN tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) ");
            query.append("INNER JOIN tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) ");
            query.append("INNER JOIN tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
            query.append("INNER JOIN tb_estabelecimento est on (org.est_codigo = est.est_codigo) ");
            query.append("WHERE REPLACE(TRIM(COALESCE(CNV_COD_VERBA, '')), '0', '') <> '' ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("DROP TEMPORARY TABLE IF EXISTS tmp_convenios_retorno_2");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tmp_convenios_retorno_2 (");
            query.append("CNV_COD_VERBA varchar(32), ");
            query.append("CSA_CODIGO varchar(32), ");
            query.append("CSA_IDENTIFICADOR varchar(40), ");
            query.append("SVC_IDENTIFICADOR varchar(40), ");
            query.append("EST_IDENTIFICADOR varchar(40), ");
            query.append("ORG_IDENTIFICADOR varchar(40), ");
            query.append("PRIMARY KEY (CNV_COD_VERBA, CSA_IDENTIFICADOR, SVC_IDENTIFICADOR, EST_IDENTIFICADOR, ORG_IDENTIFICADOR)");
            query.append(")");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("INSERT INTO tmp_convenios_retorno_2 (CNV_COD_VERBA, CSA_CODIGO, CSA_IDENTIFICADOR, SVC_IDENTIFICADOR, EST_IDENTIFICADOR, ORG_IDENTIFICADOR) ");
            query.append("SELECT cnv_cod_verba, csa_codigo, csa_identificador, svc_identificador, est_identificador, org_identificador ");
            query.append("FROM tmp_convenios_retorno ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("UPDATE tb_arquivo_retorno art ");
            query.append("INNER JOIN tmp_convenios_retorno tmp ON (art.cnv_cod_verba = tmp.cnv_cod_verba ");
            query.append("    AND COALESCE(art.est_identificador, tmp.est_identificador) = tmp.est_identificador ");
            query.append("    AND COALESCE(art.org_identificador, tmp.org_identificador) = tmp.org_identificador ");
            query.append("    AND COALESCE(art.csa_identificador, tmp.csa_identificador) = tmp.csa_identificador ");
            query.append("    AND COALESCE(art.svc_identificador, tmp.svc_identificador) = tmp.svc_identificador ");
            query.append(") ");
            query.append("SET art.csa_codigo = tmp.csa_codigo ");
            query.append("WHERE art.csa_codigo is null ");
            query.append("AND NOT EXISTS ( ");
            query.append("  SELECT 1 FROM tmp_convenios_retorno_2 tmp2 ");
            query.append("    WHERE tmp.cnv_cod_verba = tmp2.cnv_cod_verba ");
            query.append("      AND tmp.est_identificador = tmp2.est_identificador ");
            query.append("      AND tmp.org_identificador = tmp2.org_identificador ");
            query.append("      AND tmp.csa_identificador = tmp2.csa_identificador ");
            query.append("      AND tmp.svc_identificador = tmp2.svc_identificador ");
            query.append("      AND tmp.csa_codigo <> tmp2.csa_codigo ");
            query.append(") ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
