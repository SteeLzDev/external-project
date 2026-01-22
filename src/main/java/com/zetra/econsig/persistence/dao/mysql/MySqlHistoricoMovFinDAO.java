package com.zetra.econsig.persistence.dao.mysql;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.dao.generic.GenericHistoricoMovFinDAO;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: MySqlHistoricoMovFinDAO</p>
 * <p>Description: Implementacao do DAO de HistoricoMovFin para o MySql</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlHistoricoMovFinDAO extends GenericHistoricoMovFinDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlHistoricoMovFinDAO.class);

    @Override
    public void atualizarCamposTabelaArquivo() throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            int rows = 0;
            StringBuilder query = new StringBuilder();

            // Primeiro a chave mais forte: ADE_NUMERO caso preenchido, obtém os dados da consignação
            query.setLength(0);
            query.append("UPDATE tb_arquivo_movimento_validacao tmp ");
            query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_numero = tmp.ade_numero) ");
            query.append("INNER JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("INNER JOIN tb_registro_servidor rse ON (rse.rse_codigo = ade.rse_codigo) ");
            query.append("SET tmp.cnv_codigo = vco.cnv_codigo, ");
            query.append("    tmp.rse_codigo = ade.rse_codigo ");
            query.append("WHERE tmp.ade_numero IS NOT NULL ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Se não tem ADE_NUMERO, busca o servidor pela matrícula, cpf e nome que não possui outro registro ativo com mesma matrícula
            query.setLength(0);
            query.append("UPDATE tb_arquivo_movimento_validacao tmp ");
            query.append("INNER JOIN tb_registro_servidor rse ON (tmp.rse_matricula = rse.rse_matricula) ");
            query.append("INNER JOIN tb_servidor ser ON (rse.ser_codigo = ser.ser_codigo) ");
            query.append("INNER JOIN tb_orgao org ON (rse.org_codigo = org.org_codigo) ");
            query.append("INNER JOIN tb_estabelecimento est ON (org.est_codigo = est.est_codigo) ");
            query.append("SET tmp.rse_codigo = rse.rse_codigo ");
            query.append("WHERE tmp.rse_matricula IS NOT NULL ");
            query.append("AND (tmp.rse_codigo IS NULL) ");
            query.append("AND (tmp.ser_cpf IS NULL OR tmp.ser_cpf = ser.ser_cpf) ");
            query.append("AND (tmp.ser_nome IS NULL OR tmp.ser_nome = ser.ser_nome) ");
            query.append("AND (tmp.org_identificador IS NULL OR tmp.org_identificador = org.org_identificador) ");
            query.append("AND (tmp.est_identificador IS NULL OR tmp.est_identificador = est.est_identificador) ");
            query.append("AND NOT EXISTS ( ");
            query.append("  SELECT 1 FROM tb_registro_servidor rse2 ");
            query.append("  WHERE rse2.rse_codigo <> rse.rse_codigo ");
            query.append("    AND rse2.srs_codigo = '").append(CodedValues.SRS_ATIVO).append("' ");
            query.append("    AND rse2.rse_matricula = rse.rse_matricula ");
            query.append(") ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Agora tenta localizar servidor apenas pelo CPF
            query.setLength(0);
            query.append("UPDATE tb_arquivo_movimento_validacao tmp ");
            query.append("INNER JOIN tb_servidor ser ON (tmp.ser_cpf = ser.ser_cpf) ");
            query.append("INNER JOIN tb_registro_servidor rse ON (rse.ser_codigo = ser.ser_codigo) ");
            query.append("INNER JOIN tb_orgao org ON (rse.org_codigo = org.org_codigo) ");
            query.append("INNER JOIN tb_estabelecimento est ON (org.est_codigo = est.est_codigo) ");
            query.append("SET tmp.rse_codigo = rse.rse_codigo ");
            query.append("WHERE tmp.ser_cpf IS NOT NULL ");
            query.append("AND (tmp.rse_codigo IS NULL) ");
            query.append("AND (tmp.rse_matricula IS NULL OR tmp.rse_matricula = rse.rse_matricula) ");
            query.append("AND (tmp.ser_nome IS NULL OR tmp.ser_nome = ser.ser_nome) ");
            query.append("AND (tmp.org_identificador IS NULL OR tmp.org_identificador = org.org_identificador) ");
            query.append("AND (tmp.est_identificador IS NULL OR tmp.est_identificador = est.est_identificador) ");
            query.append("AND NOT EXISTS ( ");
            query.append("  SELECT 1 FROM tb_servidor ser2 ");
            query.append("  INNER JOIN tb_registro_servidor rse2 ON (rse2.ser_codigo = ser2.ser_codigo) ");
            query.append("  WHERE rse2.rse_codigo <> rse.rse_codigo ");
            query.append("    AND rse2.srs_codigo = '").append(CodedValues.SRS_ATIVO).append("' ");
            query.append("    AND ser2.ser_cpf = tmp.ser_cpf ");
            query.append("    AND (tmp.rse_matricula IS NULL OR tmp.rse_matricula = rse2.rse_matricula) ");
            query.append(") ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Para os servidores localizados, define o código do convênio pela verba
            query.setLength(0);
            query.append("UPDATE tb_arquivo_movimento_validacao tmp ");
            query.append("INNER JOIN tb_convenio cnv ON (tmp.cnv_cod_verba = cnv.cnv_cod_verba) ");
            query.append("INNER JOIN tb_servico svc ON (cnv.svc_codigo = svc.svc_codigo) ");
            query.append("INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo) ");
            query.append("INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo) ");
            query.append("INNER JOIN tb_estabelecimento est ON (org.est_codigo = est.est_codigo) ");
            query.append("INNER JOIN tb_registro_servidor rse ON (rse.rse_codigo = tmp.rse_codigo) ");
            query.append("SET tmp.cnv_codigo = cnv.cnv_codigo ");
            query.append("WHERE (tmp.cnv_codigo IS NULL) ");
            query.append("AND (tmp.org_identificador IS NULL OR tmp.org_identificador = org.org_identificador) ");
            query.append("AND (tmp.est_identificador IS NULL OR tmp.est_identificador = est.est_identificador) ");
            query.append("AND (tmp.csa_identificador IS NULL OR tmp.csa_identificador = csa.csa_identificador) ");
            query.append("AND (tmp.svc_identificador IS NULL OR tmp.svc_identificador = svc.svc_identificador) ");
            query.append("AND EXISTS ( ");
            query.append("SELECT 1 ");
            query.append("FROM tb_aut_desconto ade ");
            query.append("INNER JOIN tb_parcela_desconto_periodo prd ON (ade.ade_codigo = prd.ade_codigo) ");
            query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            query.append("WHERE rse.rse_codigo = ade.rse_codigo AND cnv.cnv_codigo = vco.cnv_codigo ");
            query.append(")");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Para os servidores localizados, define o código do convênio pelos identificadores
            query.setLength(0);
            query.append("UPDATE tb_parcela_desconto_periodo prd ");
            query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = prd.ade_codigo) ");
            query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("INNER JOIN tb_servico svc ON (cnv.svc_codigo = svc.svc_codigo) ");
            query.append("INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo) ");
            query.append("INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo) ");
            query.append("INNER JOIN tb_estabelecimento est ON (org.est_codigo = est.est_codigo) ");
            query.append("INNER JOIN tb_arquivo_movimento_validacao tmp ON (ade.rse_codigo = tmp.rse_codigo ");
            query.append("  AND (tmp.org_identificador IS NULL OR tmp.org_identificador = org.org_identificador) ");
            query.append("  AND (tmp.est_identificador IS NULL OR tmp.est_identificador = est.est_identificador) ");
            query.append("  AND (tmp.csa_identificador IS NULL OR tmp.csa_identificador = csa.csa_identificador) ");
            query.append("  AND (tmp.svc_identificador IS NULL OR tmp.svc_identificador = svc.svc_identificador) ");
            query.append(") ");
            query.append("SET tmp.cnv_codigo = cnv.cnv_codigo ");
            query.append("WHERE (tmp.cnv_codigo IS NULL) ");
            query.append("  AND (tmp.cnv_cod_verba IS NULL) ");
            query.append("  AND (tmp.org_identificador IS NOT NULL OR tmp.est_identificador IS NOT NULL OR tmp.csa_identificador IS NOT NULL OR tmp.svc_identificador IS NOT NULL) ");
            query.append("  AND (prd.prd_vlr_previsto = tmp.ade_vlr) ");
            query.append("  AND NOT EXISTS ( ");
            query.append("   SELECT 1 FROM tb_convenio cnv2 ");
            query.append("   INNER JOIN tb_servico svc2 ON (cnv2.svc_codigo = svc2.svc_codigo) ");
            query.append("   INNER JOIN tb_consignataria csa2 ON (cnv2.csa_codigo = csa2.csa_codigo) ");
            query.append("   INNER JOIN tb_orgao org2 ON (cnv2.org_codigo = org2.org_codigo) ");
            query.append("   INNER JOIN tb_estabelecimento est2 ON (org2.est_codigo = est2.est_codigo) ");
            query.append("   WHERE cnv2.cnv_codigo <> cnv.cnv_codigo ");
            query.append("     AND (tmp.org_identificador IS NULL OR tmp.org_identificador = org2.org_identificador) ");
            query.append("     AND (tmp.est_identificador IS NULL OR tmp.est_identificador = est2.est_identificador) ");
            query.append("     AND (tmp.csa_identificador IS NULL OR tmp.csa_identificador = csa2.csa_identificador) ");
            query.append("     AND (tmp.svc_identificador IS NULL OR tmp.svc_identificador = svc2.svc_identificador) ");
            query.append(")");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Tenta localizar por verba/matrícula para casos ainda não encontrados
            query.setLength(0);
            query.append("UPDATE tb_arquivo_movimento_validacao tmp ");
            query.append("INNER JOIN tb_convenio cnv ON (tmp.cnv_cod_verba = cnv.cnv_cod_verba) ");
            query.append("INNER JOIN tb_servico svc ON (cnv.svc_codigo = svc.svc_codigo) ");
            query.append("INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo) ");
            query.append("INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo) ");
            query.append("INNER JOIN tb_estabelecimento est ON (org.est_codigo = est.est_codigo) ");
            query.append("INNER JOIN tb_registro_servidor rse ON (rse.org_codigo = cnv.org_codigo AND tmp.rse_matricula = rse.rse_matricula) ");
            query.append("INNER JOIN tb_servidor ser ON (rse.ser_codigo = ser.ser_codigo) ");
            query.append("INNER JOIN tb_verba_convenio vco ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_aut_desconto ade ON (rse.rse_codigo = ade.rse_codigo AND vco.vco_codigo = ade.vco_codigo) ");
            query.append("SET tmp.cnv_codigo = coalesce(tmp.cnv_codigo, vco.cnv_codigo), ");
            query.append("    tmp.rse_codigo = coalesce(tmp.rse_codigo, ade.rse_codigo) ");
            query.append("WHERE (tmp.cnv_codigo IS NULL OR tmp.rse_codigo IS NULL) ");
            query.append("AND (tmp.rse_matricula IS NOT NULL) ");
            query.append("AND (tmp.cnv_cod_verba IS NOT NULL) ");
            query.append("AND (tmp.ser_cpf IS NULL OR tmp.ser_cpf = ser.ser_cpf) ");
            query.append("AND (tmp.ser_nome IS NULL OR tmp.ser_nome = ser.ser_nome) ");
            query.append("AND (tmp.org_identificador IS NULL OR tmp.org_identificador = org.org_identificador) ");
            query.append("AND (tmp.est_identificador IS NULL OR tmp.est_identificador = est.est_identificador) ");
            query.append("AND (tmp.csa_identificador IS NULL OR tmp.csa_identificador = csa.csa_identificador) ");
            query.append("AND (tmp.svc_identificador IS NULL OR tmp.svc_identificador = svc.svc_identificador) ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Tenta localizar o convênio dos casos faltantes
            query.setLength(0);
            query.append("UPDATE tb_arquivo_movimento_validacao tmp ");
            query.append("SET tmp.cnv_codigo = ( ");
            query.append("SELECT cnv.cnv_codigo ");
            query.append("FROM tb_parcela_desconto_periodo prd ");
            query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = prd.ade_codigo) ");
            query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_servico svc ON (cnv.svc_codigo = svc.svc_codigo) ");
            query.append("INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo) ");
            query.append("INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo) ");
            query.append("INNER JOIN tb_estabelecimento est ON (org.est_codigo = est.est_codigo) ");
            query.append("INNER JOIN tb_registro_servidor rse ON (rse.rse_codigo = ade.rse_codigo) ");
            query.append("INNER JOIN tb_servidor ser ON (rse.ser_codigo = ser.ser_codigo) ");
            query.append("WHERE (tmp.cnv_cod_verba IS NULL OR tmp.cnv_cod_verba = cnv.cnv_cod_verba) ");
            query.append("AND (tmp.rse_matricula IS NULL OR tmp.rse_matricula = rse.rse_matricula) ");
            query.append("AND (tmp.ser_cpf IS NULL OR tmp.ser_cpf = ser.ser_cpf) ");
            query.append("AND (tmp.ser_nome IS NULL OR tmp.ser_nome = ser.ser_nome) ");
            query.append("AND (tmp.org_identificador IS NULL OR tmp.org_identificador = org.org_identificador) ");
            query.append("AND (tmp.est_identificador IS NULL OR tmp.est_identificador = est.est_identificador) ");
            query.append("AND (tmp.csa_identificador IS NULL OR tmp.csa_identificador = csa.csa_identificador) ");
            query.append("AND (tmp.svc_identificador IS NULL OR tmp.svc_identificador = svc.svc_identificador) ");
            query.append(") ");
            query.append("WHERE tmp.cnv_codigo IS NULL ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Tenta localizar o servidor dos casos faltantes
            query.setLength(0);
            query.append("UPDATE tb_arquivo_movimento_validacao tmp ");
            query.append("SET tmp.rse_codigo = ( ");
            query.append("SELECT rse.rse_codigo ");
            query.append("FROM tb_parcela_desconto_periodo prd ");
            query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = prd.ade_codigo) ");
            query.append("INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("INNER JOIN tb_servico svc ON (cnv.svc_codigo = svc.svc_codigo) ");
            query.append("INNER JOIN tb_consignataria csa ON (cnv.csa_codigo = csa.csa_codigo) ");
            query.append("INNER JOIN tb_orgao org ON (cnv.org_codigo = org.org_codigo) ");
            query.append("INNER JOIN tb_estabelecimento est ON (org.est_codigo = est.est_codigo) ");
            query.append("INNER JOIN tb_registro_servidor rse ON (rse.rse_codigo = ade.rse_codigo) ");
            query.append("INNER JOIN tb_servidor ser ON (rse.ser_codigo = ser.ser_codigo) ");
            query.append("WHERE (tmp.cnv_cod_verba IS NULL OR tmp.cnv_cod_verba = cnv.cnv_cod_verba) ");
            query.append("AND (tmp.rse_matricula IS NULL OR tmp.rse_matricula = rse.rse_matricula) ");
            query.append("AND (tmp.ser_cpf IS NULL OR tmp.ser_cpf = ser.ser_cpf) ");
            query.append("AND (tmp.ser_nome IS NULL OR tmp.ser_nome = ser.ser_nome) ");
            query.append("AND (tmp.org_identificador IS NULL OR tmp.org_identificador = org.org_identificador) ");
            query.append("AND (tmp.est_identificador IS NULL OR tmp.est_identificador = est.est_identificador) ");
            query.append("AND (tmp.csa_identificador IS NULL OR tmp.csa_identificador = csa.csa_identificador) ");
            query.append("AND (tmp.svc_identificador IS NULL OR tmp.svc_identificador = svc.svc_identificador) ");
            query.append(") ");
            query.append("WHERE tmp.rse_codigo IS NULL ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

            // Atualiza RSE_MATRICULA e CNV_COD_VERBA, caso sejam nulos
            query.setLength(0);
            query.append("UPDATE tb_arquivo_movimento_validacao tmp ");
            query.append("INNER JOIN tb_convenio cnv ON (tmp.cnv_codigo = cnv.cnv_codigo) ");
            query.append("INNER JOIN tb_registro_servidor rse ON (tmp.rse_codigo = rse.rse_codigo) ");
            query.append("SET tmp.cnv_cod_verba = coalesce(tmp.cnv_cod_verba, cnv.cnv_cod_verba), ");
            query.append("    tmp.rse_matricula = coalesce(tmp.rse_matricula, rse.rse_matricula) ");
            query.append("WHERE tmp.cnv_cod_verba IS NULL ");
            query.append("OR tmp.rse_matricula IS NULL ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {


        }
    }

    @Override
    public void inserirHistoricoTabelaArquivo() throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            StringBuilder query = new StringBuilder();
            query.append("DELETE FROM tb_historico_mov_fin ");
            query.append("USING tb_historico_mov_fin ");
            query.append("INNER JOIN tb_arquivo_movimento_validacao ON (");
            query.append("tb_historico_mov_fin.cnv_codigo = tb_arquivo_movimento_validacao.cnv_codigo ");
            query.append("AND tb_historico_mov_fin.hmf_periodo = tb_arquivo_movimento_validacao.amv_periodo) ");

            LOG.trace(query.toString());

            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("INSERT INTO tb_historico_mov_fin (cnv_codigo, hmf_periodo, hmf_operacao, hmf_qtd, hmf_valor) ");
            query.append("SELECT cnv_codigo, amv_periodo, amv_operacao, COUNT(*), SUM(ade_vlr) ");
            query.append("FROM tb_arquivo_movimento_validacao ");
            query.append("WHERE cnv_codigo IS NOT NULL ");
            query.append("GROUP BY cnv_codigo, amv_operacao ");

            LOG.trace(query.toString());

            jdbc.update(query.toString(), queryParams);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {


        }
    }
}
