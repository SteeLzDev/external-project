package com.zetra.econsig.persistence.dao.oracle;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.RelatorioConciliacaoBeneficioDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlRelatorioConciliacaoBeneficioDAO;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: OracleRelatorioConciliacaoBeneficioDAO</p>
 * <p>Description: DAO Oracle do relatorio de conciliacao do modulo Beneficio</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class OracleRelatorioConciliacaoBeneficioDAO implements RelatorioConciliacaoBeneficioDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlRelatorioConciliacaoBeneficioDAO.class);

    // Alguns valores da linhas usando a classe privada abaixo.
    private List<RelatorioConciliacaoEstrutura> relatorioConciliacaoEstruturas;

    // Classe privada somente para melhor controlar os dados.
    private class RelatorioConciliacaoEstrutura {
        private final String rseMatricula;
        private final String bfcCpf;
        private final String cbeNumero;
        private final String tipoLancamento;
        private final BigDecimal adeVlr;
        private final boolean mapeado;

        public RelatorioConciliacaoEstrutura(String rseMatricula, String bfcCpf, String cbeNumero, String tipoLancamento, BigDecimal adeVlr, boolean mapeado) {
            super();
            this.rseMatricula = rseMatricula;
            this.bfcCpf = bfcCpf;
            this.cbeNumero = cbeNumero;
            this.tipoLancamento = tipoLancamento;
            this.adeVlr = adeVlr;
            this.mapeado = mapeado;
        }

        public String getRseMatricula() {
            return rseMatricula;
        }

        public String getBfcCpf() {
            return bfcCpf;
        }

        public String getCbeNumero() {
            return cbeNumero;
        }

        public String getTipoLancamento() {
            return tipoLancamento;
        }

        public BigDecimal getAdeVlr() {
            return adeVlr;
        }

        public boolean isMapeado() {
            return mapeado;
        }
    }

    // Variaveis necessarias para devolver o valor para a classe que deseja gerar o relatorio.
    private int totalContratoAtivosMensalidadePlanoSaude = Integer.MIN_VALUE;
    private int totalContratoAtivosMensalidadeOdotologico = Integer.MIN_VALUE;
    private List<String> listaContratoNoSistemaNaoConciliacaoPlanoSaude;
    private List<String> listaContratoNaoSistemaNoConciliacaoPlanoSaude;
    private List<String> listaContratoNoSistemaNaoConciliacaoOdontologico;
    private List<String> listaContratoNaoSistemaNoConciliacaoOdontologico;

    @Override
    public int getTotalContratoAtivosMensalidadePlanoSaude() {
        return totalContratoAtivosMensalidadePlanoSaude;
    }

    @Override
    public int getTotalContratoAtivosMensalidadeOdotologico() {
        return totalContratoAtivosMensalidadeOdotologico;
    }

    @Override
    public List<String> getListaContratoNoSistemaNaoConciliacaoPlanoSaude() {
        return listaContratoNoSistemaNaoConciliacaoPlanoSaude;
    }

    @Override
    public List<String> getListaContratoNaoSistemaNoConciliacaoPlanoSaude() {
        return listaContratoNaoSistemaNoConciliacaoPlanoSaude;
    }

    @Override
    public List<String> getListaContratoNoSistemaNaoConciliacaoOdontologico() {
        return listaContratoNoSistemaNaoConciliacaoOdontologico;
    }

    @Override
    public List<String> getListaContratoNaoSistemaNoConciliacaoOdontologico() {
        return listaContratoNaoSistemaNoConciliacaoOdontologico;
    }

    @Override
    public void adcionaLinhaParaRelatorio(String rseMatricula, String bfcCpf, String cbeNumero, String tipoLancamento, BigDecimal adeVlr, boolean mapeado) {
        if (relatorioConciliacaoEstruturas == null) {
            relatorioConciliacaoEstruturas = new ArrayList<>();
        }

        relatorioConciliacaoEstruturas.add(new RelatorioConciliacaoEstrutura(rseMatricula, bfcCpf, cbeNumero, tipoLancamento, adeVlr, mapeado));
    }

    @Override
    public void executa(String csaCodigo) {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("csaCodigo", csaCodigo);

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DBHelper.makeConnection();

            final StringBuilder sql = new StringBuilder();
            sql.append("DROP TEMPORARY TABLE IF EXISTS tb_linhas_conciliacao");
            jdbc.update(sql.toString(), queryParams);

            sql.setLength(0);
            sql.append(" CREATE TEMPORARY TABLE IF NOT EXISTS tb_linhas_conciliacao (");
            sql.append(" RSE_MATRICULA VARCHAR(20), ");
            sql.append(" BFC_CPF VARCHAR(19), ");
            sql.append(" CBE_NUMERO VARCHAR(40), ");
            sql.append(" TLA_CODIGO VARCHAR(32), ");
            sql.append(" ADE_VLR DECIMAL(13,2), ");
            sql.append(" MAPEADO VARCHAR(1), ");
            sql.append(" INDEX IX_CBE_NUMERO (CBE_NUMERO)");
            sql.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
            jdbc.update(sql.toString(), queryParams);

            sql.setLength(0);
            sql.append("INSERT INTO tb_linhas_conciliacao values (?, ?, ?, ?, ?, ?)");
            preparedStatement = conn.prepareStatement(sql.toString());
            for (RelatorioConciliacaoEstrutura estrutura : relatorioConciliacaoEstruturas) {
                preparedStatement.setString(1, estrutura.getRseMatricula());
                preparedStatement.setString(2, estrutura.getBfcCpf());
                preparedStatement.setString(3, estrutura.getCbeNumero());
                preparedStatement.setString(4, estrutura.getTipoLancamento());
                preparedStatement.setBigDecimal(5, estrutura.getAdeVlr());
                preparedStatement.setBoolean(6, estrutura.isMapeado());
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();

            // Iniciando a geração dos resultados do relatorios.
            // Buscando o total de contrato no sistema que são de mensalidade plano saude.
            sql.setLength(0);
            sql.append("SELECT COUNT(*) AS TOTAL FROM tb_aut_desconto ade ");
            sql.append(" INNER JOIN tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo) ");
            sql.append(" INNER JOIN tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo) ");
            sql.append(" INNER JOIN tb_tipo_natureza tnt on (tla.tnt_codigo = tnt.tnt_codigo) ");
            sql.append(" INNER JOIN tb_beneficio ben on (ben.ben_codigo = cbe.ben_codigo) ");
            sql.append(" WHERE 1 = 1 ");
            sql.append(" AND tnt.tnt_codigo = '").append(CodedValues.TNT_MENSALIDADE_PLANO_SAUDE).append("' ");
            sql.append(" AND tla.tla_codigo_pai IS NULL ");
            sql.append(" AND ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "','")).append("') ");
            sql.append(" AND ben.csa_codigo = :csaCodigo");
            totalContratoAtivosMensalidadePlanoSaude = jdbc.queryForObject(sql.toString(), queryParams, Integer.class);

            // Buscando o total de contrato no sistema que são de mensalidade odontologico.
            sql.setLength(0);
            sql.append("SELECT COUNT(*) AS TOTAL FROM tb_aut_desconto ade ");
            sql.append(" INNER JOIN tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo) ");
            sql.append(" INNER JOIN tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo) ");
            sql.append(" INNER JOIN tb_tipo_natureza tnt on (tla.tnt_codigo = tnt.tnt_codigo) ");
            sql.append(" INNER JOIN tb_beneficio ben on (ben.ben_codigo = cbe.ben_codigo) ");
            sql.append(" WHERE 1 = 1 ");
            sql.append(" AND tnt.tnt_codigo = '").append(CodedValues.TNT_MENSALIDADE_ODONTOLOGICO).append("' ");
            sql.append(" AND tla.tla_codigo_pai IS NULL ");
            sql.append(" AND ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "','")).append("') ");
            sql.append(" AND ben.csa_codigo = :csaCodigo");
            totalContratoAtivosMensalidadeOdotologico = jdbc.queryForObject(sql.toString(), queryParams, Integer.class);

            // Buscando o total de contrato que esta no sistema e não tem no arquivo de conciliacao.
            // Plano de saude
            sql.setLength(0);
            sql.append("SELECT tlc.cbe_numero as cbe_numero_tlc, tlc.rse_matricula as rse_matricula_tlc, tlc.ade_vlr as ade_vlr_tlc, tlc.tla_codigo as tla_codigo_tlc, cbe.cbe_numero, rse.rse_matricula, ade.ade_vlr, ade.tla_codigo, cbe.cbe_valor_total ");
            sql.append(" FROM tb_aut_desconto ade ");
            sql.append(" INNER JOIN tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo) ");
            sql.append(" INNER JOIN tb_beneficio ben on (ben.ben_codigo = cbe.ben_codigo) ");
            sql.append(" INNER JOIN tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo) ");
            sql.append(" INNER JOIN tb_servidor ser on (bfc.ser_codigo = ser.ser_codigo) ");
            sql.append(" INNER JOIN tb_registro_servidor rse on (rse.ser_codigo = ser.ser_codigo and ade.rse_codigo = rse.rse_codigo) ");
            sql.append(" INNER JOIN tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo) ");
            sql.append(" INNER JOIN tb_tipo_natureza tnt on (tla.tnt_codigo = tnt.tnt_codigo) ");
            sql.append(" LEFT JOIN tb_linhas_conciliacao tlc on (tlc.cbe_numero = cbe.cbe_numero) ");
            sql.append(" WHERE 1 = 1 ");
            sql.append(" AND tnt.tnt_codigo = '").append(CodedValues.TNT_MENSALIDADE_PLANO_SAUDE).append("' ");
            sql.append(" AND tla.tla_codigo_pai IS NULL ");
            sql.append(" AND ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "','")).append("') ");
            sql.append(" AND ben.csa_codigo = :csaCodigo");
            sql.append(" AND (tlc.cbe_numero IS NULL ");
            sql.append(" OR tlc.cbe_numero IS NOT NULL AND tlc.mapeado = '0') ");

            List<Map<String, Object>> resultSet = jdbc.queryForList(sql.toString(), queryParams);
            listaContratoNoSistemaNaoConciliacaoPlanoSaude = new ArrayList<>();
            for (Map<String, Object> row : resultSet) {
                StringBuilder tmp = new StringBuilder();
                tmp.append(row.get("cbe_numero_tlc"));
                tmp.append(";");
                tmp.append(row.get("rse_matricula_tlc"));
                tmp.append(";");
                tmp.append(row.get("ade_vlr_tlc"));
                tmp.append(";");
                tmp.append(row.get("tla_codigo_tlc"));
                tmp.append(";");
                tmp.append(row.get("cbe_numero"));
                tmp.append(";");
                tmp.append(row.get("rse_matricula"));
                tmp.append(";");
                tmp.append(row.get("ade_vlr"));
                tmp.append(";");
                tmp.append(row.get("tla_codigo"));
                tmp.append(";");
                tmp.append(row.get("cbe.cbe_valor_total"));
                tmp.append(";");
                listaContratoNoSistemaNaoConciliacaoPlanoSaude.add(tmp.toString());
            }

            // Buscando o total de contratos que não estão no sistema e que existem no arquivo de conciliação.
            // Plano de saúde
            sql.setLength(0);
            sql.append("SELECT tlc.cbe_numero, tlc.rse_matricula, tlc.ade_vlr, tlc.tla_codigo");
            sql.append(" FROM tb_linhas_conciliacao tlc ");
            sql.append(" INNER JOIN tb_tipo_lancamento tla on (tlc.tla_codigo = tla.tla_codigo) ");
            sql.append(" INNER JOIN tb_tipo_natureza tnt on (tla.tnt_codigo = tnt.tnt_codigo) ");
            sql.append(" WHERE tlc.mapeado = '0' ");
            sql.append(" AND tnt.tnt_codigo = '").append(CodedValues.TNT_MENSALIDADE_PLANO_SAUDE).append("' ");

            resultSet = jdbc.queryForList(sql.toString(), queryParams);
            listaContratoNaoSistemaNoConciliacaoPlanoSaude = new ArrayList<>();
            for (Map<String, Object> row : resultSet) {
                StringBuilder tmp = new StringBuilder();
                tmp.append(row.get("cbe_numero"));
                tmp.append(";");
                tmp.append(row.get("rse_matricula"));
                tmp.append(";");
                tmp.append(row.get("ade_vlr"));
                tmp.append(";");
                tmp.append(row.get("tla_codigo"));
                tmp.append(";");
                tmp.append("null");
                tmp.append(";");
                tmp.append("null");
                tmp.append(";");
                tmp.append("null");
                tmp.append(";");
                tmp.append("null");
                tmp.append(";");
                tmp.append("null");
                tmp.append(";");
                listaContratoNaoSistemaNoConciliacaoPlanoSaude.add(tmp.toString());
            }

            // Buscando o total de contrato que esta no sistema e não tem no arquivo de conciliacao.
            // Odontologico
            sql.setLength(0);
            sql.append("SELECT tlc.cbe_numero as cbe_numero_tlc, tlc.rse_matricula as rse_matricula_tlc, tlc.ade_vlr as ade_vlr_tlc, tlc.tla_codigo as tla_codigo_tlc, cbe.cbe_numero, rse.rse_matricula, ade.ade_vlr, ade.tla_codigo, cbe.cbe_valor_total ");
            sql.append(" FROM tb_aut_desconto ade ");
            sql.append(" INNER JOIN tb_contrato_beneficio cbe on (ade.cbe_codigo = cbe.cbe_codigo) ");
            sql.append(" INNER JOIN tb_beneficio ben on (ben.ben_codigo = cbe.ben_codigo) ");
            sql.append(" INNER JOIN tb_beneficiario bfc on (cbe.bfc_codigo = bfc.bfc_codigo) ");
            sql.append(" INNER JOIN tb_servidor ser on (bfc.ser_codigo = ser.ser_codigo) ");
            sql.append(" INNER JOIN tb_registro_servidor rse on (rse.ser_codigo = ser.ser_codigo and ade.rse_codigo = rse.rse_codigo) ");
            sql.append(" INNER JOIN tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo) ");
            sql.append(" INNER JOIN tb_tipo_natureza tnt on (tla.tnt_codigo = tnt.tnt_codigo) ");
            sql.append(" LEFT JOIN tb_linhas_conciliacao tlc on (tlc.cbe_numero = cbe.cbe_numero) ");
            sql.append(" WHERE 1 = 1 ");
            sql.append(" AND tnt.tnt_codigo = '").append(CodedValues.TNT_MENSALIDADE_ODONTOLOGICO).append("' ");
            sql.append(" AND tla.tla_codigo_pai IS NULL ");
            sql.append(" AND ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "','")).append("') ");
            sql.append(" AND ben.csa_codigo = :csaCodigo");
            sql.append(" AND (tlc.cbe_numero IS NULL ");
            sql.append(" OR tlc.cbe_numero IS NOT NULL AND tlc.mapeado = '0') ");

            resultSet = jdbc.queryForList(sql.toString(), queryParams);
            listaContratoNoSistemaNaoConciliacaoOdontologico = new ArrayList<>();
            for (Map<String, Object> row : resultSet) {
                StringBuilder tmp = new StringBuilder();
                tmp.append(row.get("cbe_numero_tlc"));
                tmp.append(";");
                tmp.append(row.get("rse_matricula_tlc"));
                tmp.append(";");
                tmp.append(row.get("ade_vlr_tlc"));
                tmp.append(";");
                tmp.append(row.get("tla_codigo_tlc"));
                tmp.append(";");
                tmp.append(row.get("cbe_numero"));
                tmp.append(";");
                tmp.append(row.get("rse_matricula"));
                tmp.append(";");
                tmp.append(row.get("ade_vlr"));
                tmp.append(";");
                tmp.append(row.get("tla_codigo"));
                tmp.append(";");
                tmp.append(row.get("cbe_valor_total"));
                tmp.append(";");
                listaContratoNoSistemaNaoConciliacaoOdontologico.add(tmp.toString());
            }

            // Buscando o total de contrato que nao esta no sistema e tem no arquivo de conciliacao.
            // Odontologico
            sql.setLength(0);
            sql.append("SELECT tlc.cbe_numero, tlc.rse_matricula, tlc.ade_vlr, tlc.tla_codigo");
            sql.append(" FROM tb_linhas_conciliacao tlc ");
            sql.append(" INNER JOIN tb_tipo_lancamento tla on (tlc.tla_codigo = tla.tla_codigo) ");
            sql.append(" INNER JOIN tb_tipo_natureza tnt on (tla.tnt_codigo = tnt.tnt_codigo) ");
            sql.append(" WHERE tlc.mapeado = '0' ");
            sql.append(" AND tnt.tnt_codigo = '").append(CodedValues.TNT_MENSALIDADE_ODONTOLOGICO).append("' ");

            resultSet = jdbc.queryForList(sql.toString(), queryParams);
            listaContratoNaoSistemaNoConciliacaoOdontologico = new ArrayList<>();
            for (Map<String, Object> row : resultSet) {
                StringBuilder tmp = new StringBuilder();
                tmp.append(row.get("cbe_numero"));
                tmp.append(";");
                tmp.append(row.get("rse_matricula"));
                tmp.append(";");
                tmp.append(row.get("ade_vlr"));
                tmp.append(";");
                tmp.append(row.get("tla_codigo"));
                tmp.append(";");
                tmp.append("null");
                tmp.append(";");
                tmp.append("null");
                tmp.append(";");
                tmp.append("null");
                tmp.append(";");
                tmp.append("null");
                tmp.append(";");
                tmp.append("null");
                tmp.append(";");
                listaContratoNaoSistemaNoConciliacaoOdontologico.add(tmp.toString());
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            DBHelper.closeStatement(preparedStatement);
            DBHelper.releaseConnection(conn);
        }
    }
}