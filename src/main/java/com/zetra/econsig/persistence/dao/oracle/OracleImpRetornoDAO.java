package com.zetra.econsig.persistence.dao.oracle;

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
import com.zetra.econsig.persistence.dao.generic.GenericImpRetornoDAO;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: OracleImpRetornoDAO</p>
 * <p>Description: Implementacao do DAO de retorno para o Oracle</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OracleImpRetornoDAO extends GenericImpRetornoDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OracleImpRetornoDAO.class);

    @Override
    public void criarTabelasImportacaoRetorno() throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        try {
            final StringBuilder query = new StringBuilder();

            query.setLength(0);
            query.append("CALL dropTableIfExists('tb_tmp_retorno_parcelas')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TABLE tb_tmp_retorno_parcelas ( ");
            query.append(" id_linha NUMBER(11,0) NOT NULL, ");
            query.append(" ade_codigo VARCHAR2(32) NOT NULL, ");
            query.append(" ade_prd_pagas NUMBER(11,0) DEFAULT 0, ");
            query.append(" ade_data TIMESTAMP NOT NULL, ");
            query.append(" ade_numero NUMBER(24,0) NOT NULL, ");
            query.append(" prd_codigo NUMBER(11,0) NOT NULL, ");
            query.append(" prd_numero NUMBER(6,0) NOT NULL, ");
            query.append(" prd_data_desconto DATE NOT NULL, ");
            query.append(" prd_vlr_previsto NUMBER(13,2) DEFAULT 0.00 NOT NULL, ");
            query.append(" pode_pagar_exato CHAR(1) DEFAULT 'N' NOT NULL, ");
            query.append(" valor_exato CHAR(1) DEFAULT 'N' NOT NULL, ");
            query.append(" processada CHAR(1) DEFAULT 'N' NOT NULL) ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE INDEX tb_tmp_retorno_parcelas_ix1 ON tb_tmp_retorno_parcelas (id_linha)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE INDEX tb_tmp_retorno_parcelas_ix2 ON tb_tmp_retorno_parcelas (ade_codigo, prd_numero)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE INDEX tb_tmp_retorno_parcelas_ix3 ON tb_tmp_retorno_parcelas (prd_codigo)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE INDEX tb_tmp_retorno_parcelas_ix4 ON tb_tmp_retorno_parcelas (id_linha, pode_pagar_exato)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);


            query.setLength(0);
            query.append("CALL dropTableIfExists('tb_tmp_retorno_parcelas_exatas')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TABLE tb_tmp_retorno_parcelas_exatas (id_linha NUMBER(11,0) NOT NULL)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE INDEX retorno_parcelas_exatas_idx ON tb_tmp_retorno_parcelas_exatas (id_linha)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);


            query.setLength(0);
            query.append("CALL dropTableIfExists('tb_tmp_retorno_parcelas_selec')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TABLE tb_tmp_retorno_parcelas_selec (id_linha NUMBER(11,0) NOT NULL)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE INDEX retorno_parcelas_selec_idx ON tb_tmp_retorno_parcelas_selec (id_linha)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);


            query.setLength(0);
            query.append("CALL dropTableIfExists('tb_tmp_retorno_parcelas_consol')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TABLE tb_tmp_retorno_parcelas_consol (");
            query.append("id_linha NUMBER(11,0) NOT NULL, ");
            query.append("ade_codigo VARCHAR2(32) NOT NULL, ");
            query.append("qtde_parcelas NUMBER(11,0) DEFAULT 0 NOT NULL, ");
            query.append("vlr_total NUMBER(13,2)) ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE INDEX retorno_parcelas_consol_idx ON tb_tmp_retorno_parcelas_consol (id_linha)");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

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
            query = "UPDATE tb_arquivo_retorno "
                    + "SET art_ferias = 1 "
                    + "WHERE id_linha IN ( "
                    + "SELECT art.id_linha "
                    + "FROM tb_arquivo_retorno art "
                    + "INNER JOIN tb_orgao org ON (org.org_identificador = art.org_identificador) "
                    + "INNER JOIN tb_periodo_exportacao pex ON (org.org_codigo = pex.org_codigo) "
                    + "WHERE art.mapeada = 'S' "
                    + "AND art.art_ferias IS NULL "
                    + "AND art.ano_mes_desconto > pex.pex_periodo)";
            LOG.trace(query);
            rows = jdbc.update(query, queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Linhas que vieram com a data de desconto com período futuro (usando cnv_cod_verba)
            query = "UPDATE tb_arquivo_retorno "
                    + "SET art_ferias = 1 "
                    + "WHERE id_linha IN ( "
                    + "SELECT art.id_linha "
                    + "FROM tb_arquivo_retorno art "
                    + "INNER JOIN tb_convenio cnv ON (cnv.cnv_cod_verba = art.cnv_cod_verba) "
                    + "INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo) "
                    + "WHERE art.mapeada = 'S' "
                    + "AND art.art_ferias IS NULL "
                    + "AND art.ano_mes_desconto > pex.pex_periodo)";
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
                    + "SET art.ano_mes_desconto = (SELECT ADD_MONTHS(pex.pex_periodo, 1) "
                    + "FROM tb_convenio cnv "
                    + "INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo) "
                    + "WHERE cnv.cnv_cod_verba = art.cnv_cod_verba GROUP BY pex.pex_periodo) "
                    + "WHERE art.mapeada = 'S' "
                    + "AND art.art_ferias = 1 "
                    + "AND (art.ano_mes_desconto IS NULL "
                    + "OR art.ano_mes_desconto = (SELECT pex.pex_periodo "
                    + "FROM tb_convenio cnv "
                    + "INNER JOIN tb_periodo_exportacao pex ON (cnv.org_codigo = pex.org_codigo) "
                    + "WHERE cnv.cnv_cod_verba = art.cnv_cod_verba GROUP BY pex.pex_periodo)) ";
            LOG.trace(query);
            rows = jdbc.update(query, queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Seta o periodo para o próximo das linhas de férias que não tem perido definido (usando org_identificador)
            query = "UPDATE tb_arquivo_retorno art "
                    + "SET art.ano_mes_desconto = (SELECT ADD_MONTHS(pex.pex_periodo, 1) "
                    + "FROM tb_orgao org "
                    + "INNER JOIN tb_periodo_exportacao pex ON (org.org_codigo = pex.org_codigo) "
                    + "WHERE org.org_identificador = art.org_identificador GROUP BY pex.pex_periodo) "
                    + "WHERE art.mapeada = 'S' "
                    + "AND art.art_ferias = 1 "
                    + "AND (art.ano_mes_desconto IS NULL "
                    + "OR art.ano_mes_desconto = (SELECT pex.pex_periodo "
                    + "FROM tb_orgao org "
                    + "INNER JOIN tb_periodo_exportacao pex ON (org.org_codigo = pex.org_codigo) "
                    + "WHERE org.org_identificador = art.org_identificador GROUP BY pex.pex_periodo)) ";
            LOG.trace(query);
            rows = jdbc.update(query, queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Atualiza a observação da parcela de férias para aquelas que estão com a observação padrão
            query = "UPDATE tb_arquivo_retorno art "
                    + "SET art.ocp_obs = '" + ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.com.retorno.ferias", (AcessoSistema) null) + "' "
                    + "WHERE art.art_ferias = 1 AND art.ocp_obs LIKE '" + ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.com.retorno", (AcessoSistema) null) + "'";

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
        query.append("SET art.org_identificador = (SELECT org.org_identificador ");
        query.append("FROM tb_registro_servidor rse ");
        query.append("INNER JOIN tb_orgao org ON (org.org_codigo = rse.org_codigo) ");
        query.append("INNER JOIN tb_servidor ser ON (ser.ser_codigo = rse.ser_codigo) ");
        query.append("WHERE rse.rse_matricula = art.rse_matricula ");
        query.append("AND ser.ser_cpf = art.ser_cpf ");
        query.append("AND rse.srs_codigo  NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("') ");
        query.append("AND rownum = 1) ");
        query.append("WHERE art.org_identificador IS NULL ");

        LOG.trace(query.toString());
        rows = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + rows);
        query.setLength(0);

        // Atribui identificador do órgão quando não enviado filtrando por matrícula ativa
        query.append("UPDATE tb_arquivo_retorno art ");
        query.append("SET art.org_identificador = (SELECT org.org_identificador ");
        query.append("FROM tb_registro_servidor rse ");
        query.append("INNER JOIN tb_orgao org ON (org.org_codigo = rse.org_codigo) ");
        query.append("WHERE rse.rse_matricula = art.rse_matricula ");
        query.append("AND rse.srs_codigo  NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("') ");
        query.append("AND rownum = 1) ");
        query.append("WHERE art.org_identificador IS NULL ");

        LOG.trace(query.toString());
        rows = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + rows);
        query.setLength(0);

        // Atribui identificador do órgão quando não enviado filtrando por matrícula e CPF
        query.append("UPDATE tb_arquivo_retorno art ");
        query.append("SET art.org_identificador = (SELECT org.org_identificador ");
        query.append("FROM tb_registro_servidor rse ");
        query.append("INNER JOIN tb_orgao org ON (org.org_codigo = rse.org_codigo) ");
        query.append("INNER JOIN tb_servidor ser ON (ser.ser_codigo = rse.ser_codigo) ");
        query.append("WHERE rse.rse_matricula = art.rse_matricula ");
        query.append("AND ser.ser_cpf = art.ser_cpf ");
        query.append("AND rownum = 1) ");
        query.append("WHERE art.org_identificador IS NULL ");

        LOG.trace(query.toString());
        rows = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + rows);
        query.setLength(0);

        // Atribui identificador do órgão quando não enviado filtrando por matrícula
        query.append("UPDATE tb_arquivo_retorno art ");
        query.append("SET art.org_identificador = (SELECT org.org_identificador ");
        query.append("FROM tb_registro_servidor rse ");
        query.append("INNER JOIN tb_orgao org ON (org.org_codigo = rse.org_codigo) ");
        query.append("WHERE rse.rse_matricula = art.rse_matricula ");
        query.append("AND rownum = 1) ");
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
        query.append("SET ade_paga = 'N', ade_vlr_folha = null, ade_prazo_folha = null ");
        if (!ParamSist.paramEquals(CodedValues.TPC_MANTEM_DATA_INI_FIM_FOLHA_RETORNO, CodedValues.TPC_SIM, responsavel)) {
            query.append(", ade_ano_mes_ini_folha = null ");
            query.append(", ade_ano_mes_fim_folha = null ");
        }
        query.append("WHERE (1=1) ");

        if ((orgCodigos != null && orgCodigos.size() > 0) || (estCodigos != null && estCodigos.size() > 0)) {
            query.append("AND EXISTS (SELECT 1 FROM tb_verba_convenio vco ");
            query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
            if (estCodigos != null && estCodigos.size() > 0) {
                query.append("INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo) ");
            }

            query.append("WHERE ade.vco_codigo = vco.vco_codigo ");

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
            query.append(") ");
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
        throw new UnsupportedOperationException();
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
                        query.append(" AND (TO_CHAR(ade.ade_data, 'yyyy-mm-dd') = TO_CHAR(art.ade_data, 'yyyy-mm-dd'))");
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
            query.append("DELETE FROM tb_tmp_retorno_parcelas_selec");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Seleciona as parcelas que tiveram apenas uma linha selecionada para o valor exato
            query.setLength(0);
            query.append("INSERT INTO tb_tmp_retorno_parcelas_selec (id_linha) ");
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
            query.append("SET rpd.pode_pagar_exato = 'S' ");
            query.append(valorExato ? "WHERE rpd.valor_exato = 'S' " : "WHERE 1=1 ");
            query.append("AND EXISTS (SELECT 1 FROM tb_tmp_retorno_parcelas_selec rps ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas_exatas rpe ON (rpe.id_linha = rps.id_linha) ");
            query.append("WHERE rps.id_linha = rpd.id_linha AND rpe.id_linha = rpd.id_linha) ");
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

            query.append("DELETE FROM tb_tmp_retorno_parcelas_consol");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            if (agrupaPorAdeCodigo) {
                query.append("INSERT INTO tb_tmp_retorno_parcelas_consol (id_linha, ade_codigo, qtde_parcelas, vlr_total) ");
                query.append("SELECT art.id_linha, rpd.ade_codigo, count(*) as qtde_parcelas, sum(rpd.prd_vlr_previsto) as vlr_total ");
                query.append("FROM tb_arquivo_retorno art ");
                query.append("INNER JOIN tb_tmp_retorno_parcelas rpd ON (rpd.id_linha = art.id_linha AND rpd.processada = 'N') ");
                query.append("WHERE art.processada = 'N' AND art.art_ferias = 0 ");
                query.append("AND art.nome_arquivo = :nomeArqRetorno ");
                query.append("GROUP BY art.id_linha, rpd.ade_codigo ");
            } else {
                query.append("INSERT INTO tb_tmp_retorno_parcelas_consol (id_linha, qtde_parcelas, vlr_total) ");
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
            query.append("SET art.pode_pagar_consolidacao_exata = 'S' ");
            query.append("WHERE art.nome_arquivo = :nomeArqRetorno ");
            query.append("AND art.prd_vlr_realizado = (SELECT rpc.vlr_total ");
            query.append("FROM tb_tmp_retorno_parcelas_consol rpc WHERE rpc.id_linha = art.id_linha) ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
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
                // Se retorno atrasado, apaga as ocorrências de parcela do período
                query.append("DELETE FROM tb_ocorrencia_parcela ");
                query.append("WHERE toc_codigo IN ('").append(TextHelper.join(CodedValues.TOC_CODIGOS_RETORNO_PARCELA, "','")).append("') ");
                query.append("AND EXISTS (SELECT 1 FROM tb_arquivo_retorno art ");
                query.append("INNER JOIN tb_tmp_retorno_parcelas rpd ON (rpd.id_linha = art.id_linha) ");
                query.append("WHERE tb_ocorrencia_parcela.prd_codigo = rpd.prd_codigo ");
                query.append("AND art.mapeada = 'S' ");
                query.append("AND art.nome_arquivo = :nomeArqRetorno ");
                query.append("AND rpd.pode_pagar_exato = 'S')");
                LOG.trace(query.toString());
                rows = jdbc.update(query.toString(), queryParams);
                LOG.trace("Linhas afetadas: " + rows);
            }

            // Insere a ocorrência de pagamento da parcela
            query.setLength(0);
            if (atrasado) {
                query.append("INSERT INTO tb_ocorrencia_parcela");
            } else {
                query.append("INSERT INTO tb_ocorrencia_parcela_periodo");
            }
            query.append(" (OCP_CODIGO, TOC_CODIGO, USU_CODIGO, PRD_CODIGO, OCP_DATA, OCP_OBS) ");
            query.append("SELECT ('Z' || ");
            query.append("TO_CHAR(CURRENT_TIMESTAMP, 'yymmddhh24miss') || ");
            query.append("SUBSTR(LPAD(art.id_linha, 7, '0'), 1, 7) || ");
            query.append("SUBSTR(LPAD(rpd.prd_numero, 5, '0'), 1, 5) || ");
            query.append("SUBSTR(LPAD(ROWNUM, 7, '0'), 1, 7)), ");
            //DESENV-6177 caso o ade_tipo_vlr for diferente de fixo a ocorrência nunca é parcial
            //DESENV-6369 caso seja retorno de rejeito nunca será parcial
            query.append("CASE WHEN rpd.valor_exato = 'S' OR ade.ade_tipo_vlr <> 'F' OR art.spd_codigo = '" + CodedValues.SPD_REJEITADAFOLHA + "' ");
            query.append("THEN '").append(CodedValues.TOC_RETORNO).append("' ELSE '").append(CodedValues.TOC_RETORNO_PARCIAL).append("' END, ");
            query.append(":usuCodigo, ");
            query.append("rpd.prd_codigo, CURRENT_TIMESTAMP, art.ocp_obs ");
            query.append("FROM tb_arquivo_retorno art ");
            query.append("INNER JOIN tb_tmp_retorno_parcelas rpd ON (rpd.id_linha = art.id_linha) ");
            query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = rpd.ade_codigo) ");
            query.append("WHERE art.mapeada = 'S' AND art.art_ferias = 0 ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            query.append("AND rpd.pode_pagar_exato = 'S' ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Atualiza a tabela de retorno, informando que a linha foi processada
            query.setLength(0);
            query.append("UPDATE tb_arquivo_retorno art ");
            query.append("SET art.processada = 'S' ");
            query.append("WHERE art.mapeada = 'S' AND art.art_ferias = 0 ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            query.append("AND EXISTS (SELECT 1 FROM tb_tmp_retorno_parcelas rpd ");
            query.append("WHERE rpd.id_linha = art.id_linha ");
            query.append("AND rpd.pode_pagar_exato = 'S')");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Atualiza a tabela de parcelas do retorno, informando que a linha foi processada
            query.setLength(0);
            query.append("UPDATE tb_tmp_retorno_parcelas rpd ");
            query.append("SET rpd.processada = 'S' ");
            query.append("WHERE rpd.pode_pagar_exato = 'S' ");
            query.append("AND EXISTS (SELECT 1 FROM tb_arquivo_retorno art ");
            query.append("WHERE rpd.id_linha = art.id_linha ");
            query.append("AND art.mapeada = 'S' AND art.art_ferias = 0 ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno)");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Atualiza os dados folha do contrato
            query.setLength(0);
            query.append("UPDATE tb_aut_desconto ade ");
            query.append("SET (ade_vlr_folha, ade_prazo_folha, ade_ano_mes_ini_folha, ade_ano_mes_fim_folha, ade_paga) = (SELECT ");
            if (!retAtrasadoSomaAparcela) {
                query.append("art.prd_vlr_realizado, ");
            } else {
                query.append("(CASE WHEN art.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' THEN ade_vlr_folha + art.prd_vlr_realizado ");
                query.append("ELSE ").append("art.prd_vlr_realizado END, ");
            }
            query.append("COALESCE(art.ade_prazo, ade.ade_prazo), ");
            query.append("COALESCE(art.ade_ano_mes_ini, ade.ade_ano_mes_ini_folha), ");
            query.append("COALESCE(art.ade_ano_mes_fim, ade.ade_ano_mes_fim_folha), ");
            query.append("(CASE WHEN art.spd_codigo = '" + CodedValues.SPD_REJEITADAFOLHA + "' THEN 'N' ELSE 'S' END) ");
            if (atrasado) {
                query.append("FROM tb_parcela_desconto prd ");
            } else {
                query.append("FROM tb_parcela_desconto_periodo prd ");
            }
            query.append("INNER JOIN tb_tmp_retorno_parcelas rpd ON (prd.prd_codigo = rpd.prd_codigo) ");
            query.append("INNER JOIN tb_arquivo_retorno art ON (rpd.id_linha = art.id_linha) ");
            query.append("WHERE prd.ade_codigo = ade.ade_codigo ");
            query.append("AND art.mapeada = 'S' AND art.art_ferias = 0 ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            query.append("AND rpd.pode_pagar_exato = 'S') ");
            query.append("WHERE EXISTS (SELECT 1 ");
            if (atrasado) {
                query.append("FROM tb_parcela_desconto prd ");
            } else {
                query.append("FROM tb_parcela_desconto_periodo prd ");
            }
            query.append("INNER JOIN tb_tmp_retorno_parcelas rpd ON (prd.prd_codigo = rpd.prd_codigo) ");
            query.append("INNER JOIN tb_arquivo_retorno art ON (rpd.id_linha = art.id_linha) ");
            query.append("WHERE prd.ade_codigo = ade.ade_codigo ");
            query.append("AND art.mapeada = 'S' AND art.art_ferias = 0 ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            query.append("AND rpd.pode_pagar_exato = 'S')");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Atualiza os dados de pagamento da parcela
            query.setLength(0);
            if (atrasado) {
                query.append("UPDATE tb_parcela_desconto prd ");
            } else {
                query.append("UPDATE tb_parcela_desconto_periodo prd ");
            }
            query.append("SET (spd_codigo, tde_codigo, prd_data_realizado, prd_vlr_realizado) = (SELECT ");
            if (!retAtrasadoSomaAparcela) {
                query.append("art.spd_codigo, ");
            } else {
                query.append("CASE WHEN art.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' ");
            }
            query.append("art.tde_codigo, ");
            if (!retAtrasadoSomaAparcela) {
                query.append("COALESCE(art.prd_data_realizado, CURRENT_TIMESTAMP), ");
                query.append("(CASE WHEN art.spd_codigo = '" + CodedValues.SPD_REJEITADAFOLHA + "' THEN 0.00 ELSE art.prd_vlr_realizado END) ");
            } else {
                //DESENV-10533: valor realizado no retorno atraso deve ser somado ao valor realizado no histórico de parcela
                query.append("CURRENT_TIMESTAMP, ");
                query.append("(CASE WHEN art.spd_codigo = '" + CodedValues.SPD_LIQUIDADAFOLHA + "' THEN prd.prd_vlr_realizado + art.prd_vlr_realizado ELSE art.prd_vlr_realizado END) ");
            }
            query.append("FROM tb_tmp_retorno_parcelas rpd ");
            query.append("INNER JOIN tb_arquivo_retorno art ON (rpd.id_linha = art.id_linha) ");
            query.append("WHERE prd.prd_codigo = rpd.prd_codigo ");
            query.append("AND art.mapeada = 'S' AND art.art_ferias = 0 ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            query.append("AND rpd.pode_pagar_exato = 'S') ");
            query.append("WHERE EXISTS (SELECT 1 FROM tb_tmp_retorno_parcelas rpd ");
            query.append("INNER JOIN tb_arquivo_retorno art ON (rpd.id_linha = art.id_linha) ");
            query.append("WHERE prd.prd_codigo = rpd.prd_codigo ");
            query.append("AND art.mapeada = 'S' AND art.art_ferias = 0 ");
            query.append("AND art.nome_arquivo = :nomeArqRetorno ");
            query.append("AND rpd.pode_pagar_exato = 'S')");
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
        boolean isRse = !TextHelper.isNull(rseCodigo);

        try {
            final StringBuilder query = new StringBuilder();

            LOG.debug("=== CRIA TABELA COM AS PARCELAS A SEREM DESFEITAS");
            query.append("CALL dropTableIfExists('tmp_desfaz_retorno_parcelas')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("CALL createTemporaryTable('tmp_desfaz_retorno_parcelas (prd_codigo NUMBER(11,0), primary key (prd_codigo))')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);

            query.append("insert into tmp_desfaz_retorno_parcelas (prd_codigo) ");
            query.append("select prd.prd_codigo ");
            query.append("from tb_ocorrencia_parcela ocp ");
            query.append("inner join tb_parcela_desconto prd on (ocp.prd_codigo = prd.prd_codigo) ");
            if (isEst || isOrg || isRse) {
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
            } else if (isRse) {
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
            query.append("update tb_aut_desconto ");
            query.append("set sad_codigo = (case when coalesce(ade_prd_pagas, 0) > 0 then '").append(CodedValues.SAD_EMANDAMENTO).append("' else '").append(CodedValues.SAD_DEFERIDA).append("' end) ");
            query.append("where ade_codigo in (select ade.ade_codigo ");
            query.append("from tb_parcela_desconto prd ");
            query.append("inner join tb_aut_desconto ade on (ade.ade_codigo = prd.ade_codigo) ");
            query.append("inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) ");
            query.append("where prd.prd_codigo in (select prd_codigo from tmp_desfaz_retorno_parcelas) ");
            query.append("and oca.oca_data >= prd.prd_data_realizado ");
            query.append("and oca.toc_codigo in ('").append(CodedValues.TOC_CONCLUSAO_CONTRATO).append("', '").append(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO).append("')) ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);


            LOG.debug("=== EXCLUI OCORRENCIA DE CONCLUSAO DE CONTRATO");
            query.append("delete from tb_ocorrencia_autorizacao ");
            query.append("where toc_codigo in ('").append(CodedValues.TOC_CONCLUSAO_CONTRATO).append("', '").append(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO).append("') ");
            query.append("and exists ( ");
            query.append("select 1 from tb_parcela_desconto prd ");
            query.append("where tb_ocorrencia_autorizacao.ade_codigo = prd.ade_codigo ");
            query.append("and tb_ocorrencia_autorizacao.oca_data >= prd.prd_data_realizado ");
            query.append("and prd.prd_codigo in (select prd_codigo from tmp_desfaz_retorno_parcelas)) ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            LOG.debug("=== AJUSTA VALOR DO CONTRATO AO DESFAZER RETORNO SE O SEU VALOR FOI ALTERADO DURANTE O PROCESSAMENTO=== ");
            query.append("update tb_aut_desconto ade ");
            query.append("set ade.ade_vlr = (");
            query.append("  select prd.prd_vlr_previsto ");
            query.append("  from tb_parcela_desconto prd");
            query.append("  where ade.ade_codigo = prd.ade_codigo");
            query.append("    and prd.prd_codigo in (select prd_codigo from tmp_desfaz_retorno_parcelas) ");
            query.append(") ");
            query.append("where exists ( ");
            query.append("  select 1 ");
            query.append("  from tb_verba_convenio vco ");
            query.append("  inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("  inner join tb_param_svc_consignante pse on (cnv.svc_codigo = pse.svc_codigo) ");
            query.append("  where vco.vco_codigo = ade.vco_codigo ");
            query.append("  and pse.tps_codigo = '").append(CodedValues.TPS_ATUALIZA_ADE_VLR_NO_RETORNO).append("' ");
            query.append("  and pse.pse_vlr = '1' ");
            query.append(") ");
            query.append("and exists (");
            query.append("  select 1 ");
            query.append("  from tb_parcela_desconto prd");
            query.append("  where ade.ade_codigo = prd.ade_codigo");
            query.append("    and prd.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' "); // parcelas pagas
            query.append("    and prd.prd_codigo in (select prd_codigo from tmp_desfaz_retorno_parcelas) ");
            query.append("    and ade.ade_vlr <> prd.prd_vlr_previsto ");
            query.append(") ");
            query.append("and not exists ( ");
            query.append("  select 1 ");
            query.append("  from tb_ocorrencia_autorizacao oca ");
            query.append("  inner join tb_periodo_exportacao pex on (oca.oca_data > pex.pex_data_fim) ");
            query.append("  inner join tb_convenio cnv on (pex.org_codigo = cnv.org_codigo) ");
            query.append("  inner join tb_verba_convenio vco on (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("  where ade.ade_codigo = oca.ade_codigo ");
            query.append("    and ade.vco_codigo = vco.vco_codigo ");
            query.append("    and oca.toc_codigo = '").append(CodedValues.TOC_ALTERACAO_CONTRATO).append("' ");
            query.append(") ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);


            LOG.debug("=== DIMINUI O NUMERO DE PARCELAS PAGAS / VOLTA SAD_CODIGO");
            query.append("update tb_aut_desconto ");
            query.append("set ade_prd_pagas = (case when coalesce(ade_prd_pagas, 0) <= 0 then 0 else ade_prd_pagas - 1 end), ");
            query.append("ade_prd_pagas_total = (case when coalesce(ade_prd_pagas_total, 0) <= 0 then 0 else ade_prd_pagas_total - 1 end), ");
            query.append("sad_codigo = (case ");
            query.append("  when (sad_codigo in ('").append(CodedValues.SAD_EMANDAMENTO).append("','").append(CodedValues.SAD_CONCLUIDO).append("') and ade_prd_pagas > 1) then '").append(CodedValues.SAD_EMANDAMENTO).append("' ");
            query.append("  when (sad_codigo in ('").append(CodedValues.SAD_EMANDAMENTO).append("','").append(CodedValues.SAD_CONCLUIDO).append("') and ade_prd_pagas = 1) then '").append(CodedValues.SAD_DEFERIDA).append("' ");
            query.append("  else sad_codigo ");
            query.append("end) ");
            query.append("where ade_codigo in (select prd.ade_codigo from tb_parcela_desconto prd ");
            query.append("where prd.prd_codigo in (select prd_codigo from tmp_desfaz_retorno_parcelas) ");
            query.append("and prd.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("') "); // parcelas pagas
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
                query.append("where exists (select 1 from tb_orgao ");
                query.append("where tb_orgao.org_identificador = tb_arquivo_retorno.org_identificador ");
                if (isEst) {
                    query.append("and tb_orgao.est_codigo = :estCodigo ");
                    queryParams.addValue("estCodigo", estCodigo);
                } else {
                    query.append("and tb_orgao.org_codigo = :orgCodigo ");
                    queryParams.addValue("orgCodigo", orgCodigo);
                }
                query.append(")");
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
                query.append("where exists (select 1 from tb_aut_desconto ");
                query.append("inner join tb_registro_servidor on (tb_aut_desconto.rse_codigo = tb_registro_servidor.rse_codigo) ");
                if (isEst) {
                    query.append("inner join tb_orgao on (tb_orgao.org_codigo = tb_registro_servidor.org_codigo) ");
                }
                query.append("where tb_aut_desconto.ade_codigo = tb_arquivo_retorno_parcela.ade_codigo ");
                if (isEst) {
                    query.append("and tb_orgao.est_codigo = :estCodigo ");
                    queryParams.addValue("estCodigo", estCodigo);
                } else {
                    query.append("and tb_registro_servidor.org_codigo = :orgCodigo ");
                    queryParams.addValue("orgCodigo", orgCodigo);
                }
                query.append(")");
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
            query.append("where exists (select 1 from tb_parcela_desconto_periodo ");
            if (isEst || isOrg) {
                query.append("inner join tb_aut_desconto on (tb_aut_desconto.ade_codigo = tb_parcela_desconto_periodo.ade_codigo) ");
                query.append("inner join tb_registro_servidor on (tb_aut_desconto.rse_codigo = tb_registro_servidor.rse_codigo) ");
                if (isEst) {
                    query.append("inner join tb_orgao on (tb_orgao.org_codigo = tb_registro_servidor.org_codigo) ");
                }
            }
            query.append("where prd_data_desconto > :periodo ");
            query.append("and tb_ocorrencia_parcela_periodo.prd_codigo = tb_parcela_desconto_periodo.prd_codigo ");
            if (isEst) {
                query.append("and tb_orgao.est_codigo = :estCodigo ");
                queryParams.addValue("estCodigo", estCodigo);
            } else if (isOrg) {
                query.append("and tb_registro_servidor.org_codigo = :orgCodigo ");
                queryParams.addValue("orgCodigo", orgCodigo);
            }
            query.append(")");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            LOG.debug("=== APAGA AS PARCELAS DO PERIODO");
            query.append("delete from tb_parcela_desconto_periodo ");
            query.append("where prd_data_desconto > :periodo ");
            if (isEst || isOrg) {
                query.append("and exists (select 1 from tb_aut_desconto ");
                query.append("inner join tb_registro_servidor on (tb_aut_desconto.rse_codigo = tb_registro_servidor.rse_codigo) ");
                if (isEst) {
                    query.append("inner join tb_orgao on (tb_orgao.org_codigo = tb_registro_servidor.org_codigo) ");
                }
                query.append("where tb_aut_desconto.ade_codigo = tb_parcela_desconto_periodo.ade_codigo ");
                if (isEst) {
                    query.append("and tb_orgao.est_codigo = :estCodigo ");
                    queryParams.addValue("estCodigo", estCodigo);
                } else {
                    query.append("and tb_registro_servidor.org_codigo = :orgCodigo ");
                    queryParams.addValue("orgCodigo", orgCodigo);
                }
                query.append(")");
            }
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);
            query.setLength(0);

            LOG.debug("=== APAGA REGISTROS DA TABELA DE HISTORICO DE EXPORTACAO");
            query.append("delete from tb_historico_exportacao ");
            query.append("where hie_periodo > :periodo ");
            if (isEst || isOrg) {
                query.append("and exists (select 1 from tb_orgao ");
                query.append("where tb_orgao.org_codigo = tb_historico_exportacao.org_codigo ");
                if (isEst) {
                    query.append("and tb_orgao.est_codigo = :estCodigo ");
                    queryParams.addValue("estCodigo", estCodigo);
                } else {
                    query.append("and tb_orgao.org_codigo = :orgCodigo ");
                    queryParams.addValue("orgCodigo", orgCodigo);
                }
                query.append(")");
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
            query.append("CALL dropTableIfExists('tb_tmp_ade_vlr_atualizado')");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("CALL createTemporaryTable('tb_tmp_ade_vlr_atualizado (");
            query.append("ADE_CODIGO varchar2(32) NOT NULL, ");
            query.append("ADE_NUMERO number(25,0) NOT NULL, ");
            query.append("VLR_ANTIGO number(13,2) NOT NULL, ");
            query.append("VLR_NOVO   number(13,2) NOT NULL, ");
            query.append("PERIODO    date         NOT NULL, ");
            query.append("PRIMARY KEY (ADE_CODIGO, ADE_NUMERO, VLR_ANTIGO, VLR_NOVO, PERIODO))')");
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
            query.append("INSERT INTO tb_ocorrencia_autorizacao (OCA_CODIGO, TMO_CODIGO, TOC_CODIGO, ADE_CODIGO, USU_CODIGO, OCA_DATA, OCA_PERIODO, OCA_OBS, OCA_ADE_VLR_NOVO, OCA_ADE_VLR_ANT) ");
            query.append("SELECT ('S' || ");
            query.append("TO_CHAR(CURRENT_TIMESTAMP, 'yymmddhh24miss') || ");
            query.append("SUBSTR(LPAD(ADE_NUMERO, 12, '0'), 1, 12) || ");
            query.append("SUBSTR(LPAD(ROWNUM, 7, '0'), 1, 7)), ");
            query.append("NULL, '").append(CodedValues.TOC_AVISO).append("', ade_codigo, ").append(CodedValues.USU_CODIGO_SISTEMA).append(", CURRENT_TIMESTAMP, periodo, ");
            query.append("'").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.ade.vlr.retorno", (AcessoSistema) null)).append("', ");
            query.append("VLR_NOVO,VLR_ANTIGO ");
            query.append("FROM tb_tmp_ade_vlr_atualizado");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("UPDATE tb_aut_desconto ");
            query.append("SET (ade_vlr_ref, ade_vlr) = (SELECT COALESCE(ade_vlr_ref, ade_vlr), VLR_NOVO ");
            query.append("FROM tb_tmp_ade_vlr_atualizado WHERE tb_aut_desconto.ade_codigo = tb_tmp_ade_vlr_atualizado.ade_codigo) ");
            query.append("WHERE EXISTS (SELECT 1 FROM tb_tmp_ade_vlr_atualizado WHERE tb_aut_desconto.ade_codigo = tb_tmp_ade_vlr_atualizado.ade_codigo) ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("UPDATE tb_aut_desconto ade ");
            query.append("SET ade_ano_mes_ini_ref = '").append(proximoPeriodo).append("' ");
            query.append("WHERE EXISTS (SELECT 1 FROM tb_tmp_ade_vlr_atualizado tmp WHERE ade.ade_codigo = tmp.ade_codigo) ");
            query.append("AND EXISTS (SELECT 1 FROM tb_verba_convenio vco ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_param_svc_consignante pse ON (pse.svc_codigo = cnv.svc_codigo) ");
            query.append("WHERE vco.vco_codigo = ade.vco_codigo ");
            query.append("AND tps_codigo = '").append(CodedValues.TPS_PRESERVA_DATA_ALTERACAO).append("' ");
            query.append("AND pse_vlr = '0')");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            LOG.debug("=== FIM ATUALIZA ADE COM VALOR DIFERENTE NO RETORNO ===");
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void criaTabelaConsolidacaoInversaExata() throws DAOException {
        throw new UnsupportedOperationException();
    }
    @Override
    public List<TransferObject> buscaLinhasConsolidacaoInversaExata() throws DAOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void atualizarCsaCodigoTbArqRetorno() throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            int rows = 0;
            final StringBuilder query = new StringBuilder();
            query.append("UPDATE tb_arquivo_retorno art ");
            query.append("SET art.csa_codigo = (SELECT DISTINCT cnv.csa_codigo ");
            query.append("FROM tb_arquivo_retorno_parcela arp ");
            query.append("INNER JOIN tb_parcela_desconto prd on (arp.ade_codigo = prd.ade_codigo and arp.prd_numero = prd.prd_numero) ");
            query.append("INNER JOIN tb_aut_desconto ade on (prd.ade_codigo = ade.ade_codigo) ");
            query.append("INNER JOIN tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("WHERE art.nome_arquivo = arp.nome_arquivo and art.id_linha = arp.id_linha) ");
            query.append("WHERE art.csa_codigo is null ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("CALL dropTableIfExists('tmp_convenios_retorno')");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CALL createTemporaryTable('tmp_convenios_retorno (");
            query.append("CNV_COD_VERBA varchar2(32), ");
            query.append("CSA_CODIGO varchar2(32), ");
            query.append("CSA_IDENTIFICADOR varchar2(40), ");
            query.append("SVC_IDENTIFICADOR varchar2(40), ");
            query.append("EST_IDENTIFICADOR varchar2(40), ");
            query.append("ORG_IDENTIFICADOR varchar2(40), ");
            query.append("PRIMARY KEY (CNV_COD_VERBA, CSA_IDENTIFICADOR, SVC_IDENTIFICADOR, EST_IDENTIFICADOR, ORG_IDENTIFICADOR)");
            query.append(")')");
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
            query.append("UPDATE tb_arquivo_retorno art ");
            query.append("SET art.csa_codigo = (SELECT DISTINCT tmp.csa_codigo ");
            query.append("FROM tmp_convenios_retorno tmp ");
            query.append("WHERE art.cnv_cod_verba = tmp.cnv_cod_verba ");
            query.append("  AND COALESCE(art.est_identificador, tmp.est_identificador) = tmp.est_identificador ");
            query.append("  AND COALESCE(art.org_identificador, tmp.org_identificador) = tmp.org_identificador ");
            query.append("  AND COALESCE(art.csa_identificador, tmp.csa_identificador) = tmp.csa_identificador ");
            query.append("  AND COALESCE(art.svc_identificador, tmp.svc_identificador) = tmp.svc_identificador ");
            query.append("  AND NOT EXISTS ( ");
            query.append("  SELECT 1 FROM tmp_convenios_retorno tmp2 ");
            query.append("    WHERE tmp.cnv_cod_verba = tmp2.cnv_cod_verba ");
            query.append("      AND tmp.est_identificador = tmp2.est_identificador ");
            query.append("      AND tmp.org_identificador = tmp2.org_identificador ");
            query.append("      AND tmp.csa_identificador = tmp2.csa_identificador ");
            query.append("      AND tmp.svc_identificador = tmp2.svc_identificador ");
            query.append("      AND tmp.csa_codigo <> tmp2.csa_codigo ");
            query.append("  ) ");
            query.append(") ");
            query.append("WHERE art.csa_codigo is null ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void criaTabelaParcelasRetornoFerias(List<String> camposChave, CustomTransferObject criterio, boolean atrasado) throws DAOException {
        throw new UnsupportedOperationException("Unimplemented method 'criaTabelaParcelasRetornoFerias'");
    }

    @Override
    public void selecionaParcelasPagamentoExatoFerias() throws DAOException {
        throw new UnsupportedOperationException("Unimplemented method 'selecionaParcelasPagamentoExatoFerias'");
    }

    @Override
    public void pagaParcelasSelecionadasDescontoTotalFerias(AcessoSistema responsavel) throws DAOException {
        throw new UnsupportedOperationException("Unimplemented method 'pagaParcelasSelecionadasDescontoTotalFerias'");
    }

    @Override
    public void criaTabelaConsolidacaoExataFerias() throws DAOException {
        throw new UnsupportedOperationException("Unimplemented method 'criaTabelaConsolidacaoExataFerias'");
    }
}
