package com.zetra.econsig.folha.exportacao.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.geradoradenumero.AdeNumeroHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: Parana</p>
 * <p>Description: Implementações específicas para Governo do Paraná. Solicitação inicial: DESENV-16138.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Parana extends ExportaMovimentoBase {
    private static final long serialVersionUID = 2L;
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Parana.class);

    private static final String VERBA_SALARYPAY = "5874";
    private static final String CODIGO_VIRTUAL = "DESENV-16138";

    /**
     * Incluí contratos abertos da verba SalaryPay dos meses anteriores para envio consolidado pois o desconto desta verba deve ser mensal.
     * @param parametrosExportacao
     * @param responsavel
     */
    @Override
    public void preProcessaAutorizacoes(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        boolean naoEnviaAdeRseExcluido = ParamSist.paramEquals(CodedValues.TPC_ENVIA_CONTRATO_RSE_EXCLUIDO, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema());
        Connection conn = null;
        Statement stat = null;
        try {
            conn = DBHelper.makeConnection();

            StringBuilder query = new StringBuilder();
            query.append("insert into tb_ocorrencia_autorizacao (OCA_CODIGO, TOC_CODIGO, ADE_CODIGO, USU_CODIGO, OCA_DATA, OCA_PERIODO, OCA_OBS) ");
            query.append("select concat(lpad(ade_numero, 10, '0'), '-', date_format(now(), '%Y%m%d%H%i%S')), '");
            query.append(CodedValues.TOC_RELANCAMENTO).append("', ade_codigo, '").append(CodedValues.USU_CODIGO_SISTEMA).append("', pex_data_ini, pex_periodo, '");
            query.append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.desconto.mensal", responsavel)).append("' ");
            query.append("from tb_aut_desconto ade ");
            query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("inner join tb_periodo_exportacao pex on (cnv.org_codigo = pex.org_codigo) ");
            if (naoEnviaAdeRseExcluido) {
                query.append("inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) ");
            }
            query.append("where cnv_cod_verba = '").append(VERBA_SALARYPAY).append("' ");
            query.append("and sad_codigo in ('").append(CodedValues.SAD_DEFERIDA).append("', '").append(CodedValues.SAD_EMANDAMENTO).append("') ");
            query.append("and ade_ano_mes_ini < pex_periodo ");
            if (naoEnviaAdeRseExcluido) {
                query.append("and srs_codigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("') ");
            }
            query.append("and ade_int_folha = ").append(CodedValues.INTEGRA_FOLHA_SIM);
            query.append(" and not exists(");
            query.append("select * ");
            query.append("from tb_ocorrencia_autorizacao oca ");
            query.append("where ade.ade_codigo = oca.ade_codigo ");
            query.append("and oca.toc_codigo = '").append(CodedValues.TOC_RELANCAMENTO).append("' ");
            query.append("and oca.oca_periodo = pex_periodo");
            query.append(")");
            stat = conn.createStatement();
            stat.executeUpdate(query.toString());
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeStatement(stat);
            DBHelper.releaseConnection(conn);
        }
    }

    /**
     * Consolida os contratos da verba SalaryPay e envia comando de prazo 1.
     * @param parametrosExportacao
     * @param responsavel
     */
    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        Date dataAtual = DateHelper.toSQLDate(DateHelper.getSystemDate());
        Connection conn = null;
        Statement stat = null;
        PreparedStatement preStatUpdate = null;
        PreparedStatement preStatDelete = null;
        PreparedStatement preStatUltimo = null;
        try {
            conn = DBHelper.makeConnection();
            // Garante que o contrato que manterá o ADE_NUMERO usado apenas para a exporação existe.
            verficaContratoVirtual(conn);

            stat = conn.createStatement();
            // DESENV-17242 : garante que não haverá contratos que não devem ser descontados no somátorio a ser enviado
            String queryRemoveExclusoes = "delete from tb_tmp_exportacao "
                                        + "where cnv_cod_verba = '" + VERBA_SALARYPAY + "' and sad_codigo not in ('"
                                        + TextHelper.joinWithEscapeSql(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO, "' , '") + "') ";
            stat.executeUpdate(queryRemoveExclusoes);

            String queryUpdate = "/*skip_log*/update tb_tmp_exportacao set ade_numero = ?, ade_vlr = ?, sad_codigo = '" + CodedValues.SAD_DEFERIDA + "', "
                               + "ade_prazo = 1, ade_prd_pagas = 0 "
                               + "where cnv_cod_verba = '" + VERBA_SALARYPAY + "' and rse_codigo = ? and ade_numero = ?";
            preStatUpdate = conn.prepareStatement(queryUpdate);
            String queryDelete = "/*skip_log*/delete from tb_tmp_exportacao "
                               + "where cnv_cod_verba = '" + VERBA_SALARYPAY + "' and rse_codigo = ? and ade_numero <> ?";
            preStatDelete = conn.prepareStatement(queryDelete);

            String query = "select rse_codigo, sum(ade_vlr) total, min(ade_numero) ade_manter "
                         + "from tb_tmp_exportacao "
                         + "where cnv_cod_verba = '" + VERBA_SALARYPAY + "' group by rse_codigo";
            ResultSet rs = stat.executeQuery(query);

            Long adeNumero = null;
            while (rs.next()) {
                String rseCodigo = rs.getString("rse_codigo");
                BigDecimal total = rs.getBigDecimal("total");
                Long adeManter = rs.getLong("ade_manter");
                adeNumero = AdeNumeroHelper.getNext("", dataAtual);

                preStatUpdate.setLong(1, adeNumero);
                preStatUpdate.setBigDecimal(2, total);
                preStatUpdate.setString(3, rseCodigo);
                preStatUpdate.setLong(4, adeManter);
                preStatUpdate.executeUpdate();

                preStatDelete.setString(1, rseCodigo);
                preStatDelete.setLong(2, adeNumero);
                preStatDelete.executeUpdate();
            }
            if (adeNumero != null) {
                // salva o último número para que nenhuma outra ADE o reuse.
                preStatUltimo = conn.prepareStatement("update tb_aut_desconto set ade_numero = ? where ade_codigo = '" + CODIGO_VIRTUAL + "'");
                preStatUltimo.setLong(1, adeNumero);
                int rows = preStatUltimo.executeUpdate();
                // se não atualizou a ADE, dá erro para não ter gerar inconsistência
                if (rows != 1) {
                    throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null);
                }
            }
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeStatement(preStatUltimo);
            DBHelper.closeStatement(preStatUpdate);
            DBHelper.closeStatement(preStatDelete);
            DBHelper.closeStatement(stat);
            DBHelper.releaseConnection(conn);
        }
    }

    /**
     * Cria os registros necessários do contrato que manterá o ADE_NUMERO para evitar conflitos,
     * isto é, que algum contrato utilize a certidão que será enviada para a folha com a consolidação dos valores.
     * @param conn
     * @throws ExportaMovimentoException
     */
    private void verficaContratoVirtual(Connection conn) throws ExportaMovimentoException {
        Statement stat = null;
        try {
            stat = conn.createStatement();
            boolean inserirRSE = false;
            String query = "select ser_codigo from tb_servidor where ser_codigo = '" + CODIGO_VIRTUAL + "'";
            ResultSet rs = stat.executeQuery(query);
            if (!rs.next() || rs.getString("ser_codigo") == null) {
                rs.close();
                query = "insert into tb_servidor (ser_codigo, ser_nome, ser_cpf, ser_permite_alterar_email) values ("
                      + "'" + CODIGO_VIRTUAL + "', 'NÃO EXCLUIR - USO INTERNO', '000.000.000-00', 'N')";
                int rows = stat.executeUpdate(query);
                if (rows == 1) {
                    inserirRSE = true;
                } else {
                    throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null);
                }
            }

            boolean inserirADE = false;
            query = "select rse_codigo from tb_registro_servidor where rse_codigo = '" + CODIGO_VIRTUAL + "'";
            rs = stat.executeQuery(query);
            if (!rs.next() || rs.getString("rse_codigo") == null || inserirRSE) {
                rs.close();
                query = "insert into tb_registro_servidor (rse_codigo, ser_codigo, org_codigo, rse_matricula, "
                      + "rse_margem, rse_margem_rest, rse_margem_usada, rse_margem_2, rse_margem_rest_2, rse_margem_usada_2, "
                      + "rse_margem_3, rse_margem_rest_3, rse_margem_usada_3, SRS_CODIGO, RSE_AUDITORIA_TOTAL) "
                      + "select '" + CODIGO_VIRTUAL + "', '" + CODIGO_VIRTUAL + "', org_codigo, '000000000PR000', "
                      + "0.00, 0.00, 0.00, 0.00, 0.00, 0.00, "
                      + "0.00, 0.00, 0.00, '3', 'N' from tb_orgao where org_codigo <> 'PRPREV' limit 1";
                int rows = stat.executeUpdate(query);
                if (rows == 1) {
                    inserirADE = true;
                } else {
                    throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null);
                }
            }

            query = "select ade_codigo from tb_aut_desconto where ade_codigo = '" + CODIGO_VIRTUAL + "'";
            rs = stat.executeQuery(query);
            if (!rs.next() || rs.getString("ade_codigo") == null || inserirADE) {
                rs.close();
                query = "insert into tb_aut_desconto (ade_codigo, sad_codigo, vco_codigo, rse_codigo, usu_codigo, "
                      + "ade_data, ade_vlr, ade_identificador, ade_numero, ade_paga, ade_periodicidade, ade_exportacao) "
                      + "select '" + CODIGO_VIRTUAL + "', '3', vco_codigo, rse_codigo, '1', "
                      + "now(), 0.00, '" + CODIGO_VIRTUAL + "', 0, 'N', 'M', 'N' "
                      + "from tb_registro_servidor "
                      + "inner join tb_convenio using (org_codigo) "
                      + "inner join tb_verba_convenio using (cnv_codigo) "
                      + "where rse_codigo = '" + CODIGO_VIRTUAL + "' and cnv_cod_verba = '" + VERBA_SALARYPAY + "' limit 1";
                int rows = stat.executeUpdate(query);
                if (rows != 1) {
                    throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null);
                }
            }
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeStatement(stat);
        }
    }
}