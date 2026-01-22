package com.zetra.econsig.folha.exportacao.validacao.regra;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegraValidacaoMovimentoTO;
import com.zetra.econsig.dto.entidade.ResultadoRegraValidacaoMovimentoTO;
import com.zetra.econsig.dto.entidade.ResultadoValidacaoMovimentoTO;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: Regra</p>
 * <p>Description: Interface para classe de execução da regra de validação de movimento.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class Regra {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Regra.class);

    public static final String TIPO_OPERACAO_ALTERACAO = "A";

    public static final String TIPO_OPERACAO_EXCLUSAO = "E";

    public static final String TIPO_OPERACAO_INCLUSAO = "I";

    protected ResultadoRegraValidacaoMovimentoTO resultado;

    protected String rvaCodigo;

    protected String rvmCodigo;

    protected List<String> estCodigos;

    protected List<String> orgCodigos;

    protected String periodo;

    private final List<String> amvCamposPreenchidos = new ArrayList<>();

    public ResultadoRegraValidacaoMovimentoTO getResultado() {
        return resultado;
    }

    /**
     * Busca a quantidade de registros gerados no arquivo.
     * @return
     */
    protected long buscaQtdRegistrosArquivo() {
        return buscaQtdRegistrosArquivo(null);
    }

    /**
     * Busca a quantidade de registros gerados no arquivo,
     * para a operação informada no parâmetro
     * @param operacao
     * @return
     */
    protected long buscaQtdRegistrosArquivo(String operacao) {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("operacao", operacao);

        try {
            final StringBuilder query = new StringBuilder();

            query.append("SELECT COUNT(*) AS 'qtd' ");
            query.append("FROM tb_arquivo_movimento_validacao ");
            query.append("LEFT OUTER JOIN ").append(Columns.TB_CONVENIO).append(" ON (").append(Columns.AMV_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(") ");
            query.append("LEFT OUTER JOIN ").append(Columns.TB_ORGAO).append(" ON (").append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO).append(") ");
            query.append("WHERE 1=1 ");
            if (operacao != null) {
                query.append("AND amv_operacao = :operacao ");
            }
            if (estCodigos != null && estCodigos.size() > 0) {
                query.append("AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:estCodigos) ");
                queryParams.addValue("estCodigos", estCodigos);
            }
            if (orgCodigos != null && orgCodigos.size() > 0) {
                query.append("AND ").append(Columns.ORG_CODIGO).append(" IN (:orgCodigos) ");
                queryParams.addValue("orgCodigos", orgCodigos);
            }

            return Optional.ofNullable(jdbc.queryForObject(query.toString(), queryParams, Long.class)).orElse(0l);

        } catch (DataAccessException e) {
            LOG.error(e.getMessage(), e);
            return -1;
        }
    }

    /**
     *
     * @return
     * @throws DataAccessException
     */
    public List<String> getAmvCamposPreenchidos() throws DataAccessException {
        if (amvCamposPreenchidos.size() == 0) {
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();

            final StringBuilder query = new StringBuilder();
            query.append("SELECT ");
            query.append("SUM(CASE WHEN amv_operacao          IS NOT NULL THEN 1 ELSE 0 END) AS amv_operacao         , ");
            query.append("SUM(CASE WHEN org_identificador     IS NOT NULL THEN 1 ELSE 0 END) AS org_identificador    , ");
            query.append("SUM(CASE WHEN est_identificador     IS NOT NULL THEN 1 ELSE 0 END) AS est_identificador    , ");
            query.append("SUM(CASE WHEN csa_identificador     IS NOT NULL THEN 1 ELSE 0 END) AS csa_identificador    , ");
            query.append("SUM(CASE WHEN svc_identificador     IS NOT NULL THEN 1 ELSE 0 END) AS svc_identificador    , ");
            query.append("SUM(CASE WHEN cnv_cod_verba         IS NOT NULL THEN 1 ELSE 0 END) AS cnv_cod_verba        , ");
            query.append("SUM(CASE WHEN cnv_codigo            IS NOT NULL THEN 1 ELSE 0 END) AS cnv_codigo           , ");
            query.append("SUM(CASE WHEN ser_nome              IS NOT NULL THEN 1 ELSE 0 END) AS ser_nome             , ");
            query.append("SUM(CASE WHEN ser_cpf               IS NOT NULL THEN 1 ELSE 0 END) AS ser_cpf              , ");
            query.append("SUM(CASE WHEN rse_matricula         IS NOT NULL THEN 1 ELSE 0 END) AS rse_matricula        , ");
            query.append("SUM(CASE WHEN rse_matricula_inst    IS NOT NULL THEN 1 ELSE 0 END) AS rse_matricula_inst   , ");
            query.append("SUM(CASE WHEN rse_codigo            IS NOT NULL THEN 1 ELSE 0 END) AS rse_codigo           , ");
            query.append("SUM(CASE WHEN amv_periodo           IS NOT NULL THEN 1 ELSE 0 END) AS amv_periodo          , ");
            query.append("SUM(CASE WHEN amv_competencia       IS NOT NULL THEN 1 ELSE 0 END) AS amv_competencia      , ");
            query.append("SUM(CASE WHEN amv_data              IS NOT NULL THEN 1 ELSE 0 END) AS amv_data             , ");
            query.append("SUM(CASE WHEN pex_periodo           IS NOT NULL THEN 1 ELSE 0 END) AS pex_periodo          , ");
            query.append("SUM(CASE WHEN pex_periodo_ant       IS NOT NULL THEN 1 ELSE 0 END) AS pex_periodo_ant      , ");
            query.append("SUM(CASE WHEN ade_indice            IS NOT NULL THEN 1 ELSE 0 END) AS ade_indice           , ");
            query.append("SUM(CASE WHEN ade_numero            IS NOT NULL THEN 1 ELSE 0 END) AS ade_numero           , ");
            query.append("SUM(CASE WHEN ade_prazo             IS NOT NULL THEN 1 ELSE 0 END) AS ade_prazo            , ");
            query.append("SUM(CASE WHEN ade_vlr               IS NOT NULL THEN 1 ELSE 0 END) AS ade_vlr              , ");
            query.append("SUM(CASE WHEN ade_tipo_vlr          IS NOT NULL THEN 1 ELSE 0 END) AS ade_tipo_vlr         , ");
            query.append("SUM(CASE WHEN ade_vlr_folha         IS NOT NULL THEN 1 ELSE 0 END) AS ade_vlr_folha        , ");
            query.append("SUM(CASE WHEN ade_data              IS NOT NULL THEN 1 ELSE 0 END) AS ade_data             , ");
            query.append("SUM(CASE WHEN ade_data_ref          IS NOT NULL THEN 1 ELSE 0 END) AS ade_data_ref         , ");
            query.append("SUM(CASE WHEN ade_ano_mes_ini       IS NOT NULL THEN 1 ELSE 0 END) AS ade_ano_mes_ini      , ");
            query.append("SUM(CASE WHEN ade_ano_mes_fim       IS NOT NULL THEN 1 ELSE 0 END) AS ade_ano_mes_fim      , ");
            query.append("SUM(CASE WHEN ade_ano_mes_ini_folha IS NOT NULL THEN 1 ELSE 0 END) AS ade_ano_mes_ini_folha, ");
            query.append("SUM(CASE WHEN ade_ano_mes_fim_folha IS NOT NULL THEN 1 ELSE 0 END) AS ade_ano_mes_fim_folha, ");
            query.append("SUM(CASE WHEN ade_ano_mes_ini_ref   IS NOT NULL THEN 1 ELSE 0 END) AS ade_ano_mes_ini_ref  , ");
            query.append("SUM(CASE WHEN ade_ano_mes_fim_ref   IS NOT NULL THEN 1 ELSE 0 END) AS ade_ano_mes_fim_ref  , ");
            query.append("SUM(CASE WHEN ade_cod_reg           IS NOT NULL THEN 1 ELSE 0 END) AS ade_cod_reg            ");
            query.append("FROM tb_arquivo_movimento_validacao ");
            query.append("LIMIT 1 ");

            String campos = "rse_matricula,org_identificador,est_identificador,csa_identificador,svc_identificador,cnv_cod_verba,"
                          + "ade_numero,ade_indice,ade_prazo,ade_vlr,ade_tipo_vlr,ade_vlr_folha,ade_data_ref,ade_ano_mes_ini,ade_ano_mes_fim,ade_cod_reg";
            final List<Map<String, Object>> resultSet = jdbc.queryForList(query.toString(), queryParams);
            if (resultSet != null && !resultSet.isEmpty()) {
                final Map<String, Object> row = resultSet.get(0);
                for (String key : row.keySet()) {
                    String value = row.get(key).toString();
                    if (Integer.valueOf(value) > 0 && campos.contains(key)) {
                        amvCamposPreenchidos.add(key);
                    }
                }
            }
        }

        return amvCamposPreenchidos;
    }

    public String getListaContratosComProblema(boolean hasAdeNumero, List<TransferObject> erros) {
        StringBuilder listaContratos = new StringBuilder();
        try {
            List<String> camposChave = getAmvCamposPreenchidos();
            boolean useAdeNumero = hasAdeNumero || camposChave.contains("ade_numero");
            if (useAdeNumero) {
                listaContratos.append(" - ade_numero");
            } else {
                listaContratos.append(" - ").append(TextHelper.join(camposChave, ":"));
            }
            listaContratos.append("=[");
            for (TransferObject erro : erros) {
                if (useAdeNumero) {
                    listaContratos.append(erro.getAttribute("ade_numero")).append(",");
                } else {
                    for (String string : camposChave) {
                        listaContratos.append(erro.getAttribute(string)).append(":");
                    }
                    listaContratos.setLength(listaContratos.length() - 1);
                    listaContratos.append(",");
                }
            }
            listaContratos.setLength(listaContratos.length() - 1);
            listaContratos.append("];");
        } catch (DataAccessException ex) {
            ex.printStackTrace();
            listaContratos.append("Erro na listagem de contratos: ").append(ex.getMessage());
        }
        return listaContratos.toString();
    }

    // METODOS A SEREM IMPLEMENTADOS NAS CLASSES FILHAS.

    /**
     * Cria possíveis tabelas necessárias para a validação.
     */
    public void criarTabelasValidacao() throws ZetraException {
    }

    /**
     * Excuta a regra para o arquivo específico, definido pelos parâmetros passados.
     * @param estCodigos Lista de estabelecimentos a que se refere o arquivo (opcional).
     * @param orgCodigos Lista de estabelecimentos a que se refere o arquivo (opcional).
     * @param rva        Parâmetros do arquivo sobre o qual será executada a regra.
     * @param regra      Parâmetros da regra sendo executada.
     */
    public abstract void executar(List<String> estCodigos, List<String> orgCodigos, ResultadoValidacaoMovimentoTO rva, RegraValidacaoMovimentoTO regra);
}
