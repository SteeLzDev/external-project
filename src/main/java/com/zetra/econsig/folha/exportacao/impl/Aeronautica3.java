package com.zetra.econsig.folha.exportacao.impl;

import java.math.BigDecimal;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: Aeronautica3</p>
 * <p>Description: Implementações específicas para a Aeronautica.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Aeronautica3 extends Aeronautica2 {

	private static final long serialVersionUID = 7610708089284623143L;

	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Aeronautica3.class);

    @Override
    public void posCriacaoTabelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        super.posCriacaoTabelas(parametrosExportacao, responsavel);

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            StringBuilder query = new StringBuilder();
            query.append("drop table if exists tb_tmp_exportacao_alt_det");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create table tb_tmp_exportacao_alt_det ( ");
            query.append("ser_nome varchar(255), ");
            query.append("ser_cpf varchar(19), ");
            query.append("rse_matricula varchar(20),");
            query.append("rse_matricula_inst varchar(20), ");
            query.append("rse_tipo varchar(255), ");
            query.append("rse_associado char(1), ");
            query.append("pos_codigo char(32), ");
            query.append("trs_codigo char(32), ");
            query.append("org_identificador varchar(40), ");
            query.append("est_identificador varchar(40), ");
            query.append("csa_identificador varchar(40), ");
            query.append("svc_identificador varchar(40), ");
            query.append("svc_descricao varchar(100), ");
            query.append("cnv_cod_verba varchar(32), ");
            query.append("periodo varchar(32), ");
            query.append("competencia char(6), ");
            query.append("data date, ");
            query.append("pex_periodo date, ");
            query.append("srs_codigo varchar(32), ");
            query.append("org_cnpj varchar(19), ");
            query.append("est_cnpj varchar(19), ");
            query.append("csa_cnpj varchar(19), ");
            query.append("rse_margem decimal(13,2), ");
            query.append("rse_margem_rest decimal(13,2), ");
            query.append("rse_margem_2 decimal(13,2), ");
            query.append("rse_margem_rest_2 decimal(13,2), ");
            query.append("rse_margem_3 decimal(13,2), ");
            query.append("rse_margem_rest_3 decimal(13,2), ");
            query.append("ade_numero bigint, ");
            query.append("ade_prazo int, ");
            query.append("ade_prd_pagas int, ");
            query.append("ade_vlr decimal(13,2), ");
            query.append("ade_vlr_folha decimal(13,2), ");
            query.append("ade_tipo_vlr char(1), ");
            query.append("ade_ano_mes_ini date, ");
            query.append("ade_ano_mes_ini_folha date, ");
            query.append("ade_ano_mes_fim_folha date, ");
            query.append("svc_prioridade varchar(4), ");
            query.append("cnv_prioridade int, ");
            query.append("ade_data_ref datetime, ");
            query.append("ade_ano_mes_ini_ref date, ");
            query.append("ade_ano_mes_fim_ref date, ");
            query.append("ade_cod_reg char(1), ");
            query.append("ade_ano_mes_fim date, ");
            query.append("ade_data datetime, ");
            query.append("prd_data_desconto date, ");
            query.append("prd_numero varchar(32), ");
            query.append("situacao varchar(2), ");
            query.append("ade_indice varchar(32), ");
            query.append("rse_codigo varchar(32), ");
            query.append("org_codigo varchar(32), ");
            query.append("est_codigo varchar(32), ");
            query.append("svc_codigo varchar(32), ");
            query.append("scv_codigo varchar(32), ");
            query.append("csa_codigo varchar(32), ");
            query.append("cnv_codigo varchar(32), ");
            query.append("ade_codigo varchar(32), ");
            query.append("sad_codigo varchar(32), ");
            query.append("consolida char(1), ");
            query.append("key ix_ade (ade_codigo) ");
            query.append(")");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    protected void cancelaAlteracaoMargemNegativa() throws DataAccessException {
        LOG.warn("cancelaAlteracaoMargemNegativa(): Não faz nada");
    }

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            StringBuilder query = new StringBuilder();
            Set<String> ades = new HashSet<>();

            if (lancamentosParciais != null && !lancamentosParciais.isEmpty()) {
                ades = lancamentosParciais.keySet();
            }

            int rows = 0;

            /*
             * Entendimento: Relacionado a informação do Anexo II " A Folha de Pagamento não prevê alteração de descontos que foram implantados com prazo determinado, portanto,
             * a alteração do prazo de uma consignação em andamento somente poderá ser realizada por meio de uma transação que liquide o referido contrato e defira um novo contrato."
             */
            query.append("INSERT INTO tb_tmp_exportacao_alt_det ");
            query.append("select distinct tmp.ser_nome, tmp.ser_cpf, tmp.rse_matricula, tmp.rse_matricula_inst, ");
            query.append("tmp.rse_tipo, tmp.rse_associado, tmp.pos_codigo, tmp.trs_codigo, tmp.org_identificador, tmp.est_identificador, ");
            query.append("tmp.csa_identificador, tmp.svc_identificador, tmp.svc_descricao, tmp.cnv_cod_verba, ");
            query.append("tmp.periodo, tmp.competencia, tmp.data, tmp.pex_periodo, tmp.srs_codigo, tmp.org_cnpj, tmp.est_cnpj, tmp.csa_cnpj, ");
            query.append("tmp.rse_margem, tmp.rse_margem_rest, tmp.rse_margem_2, tmp.rse_margem_rest_2, ");
            query.append("tmp.rse_margem_3, tmp.rse_margem_rest_3, tmp.ade_numero, tmp.ade_prazo, tmp.ade_prd_pagas, ");
            query.append("ifnull(tmp.ade_vlr_folha,tmp.ade_vlr) as ade_vlr, tmp.ade_vlr_folha, tmp.ade_tipo_vlr, tmp.ade_ano_mes_ini, ");
            query.append("tmp.ade_ano_mes_ini_folha, tmp.ade_ano_mes_fim_folha, ");
            query.append("tmp.svc_prioridade, tmp.cnv_prioridade, ");
            query.append("tmp.ade_data_ref, tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_fim_ref, tmp.ade_cod_reg, ");
            query.append("if(ade.ade_vlr <> ade.ade_vlr_folha, tmp.ade_ano_mes_fim, ifnull(tmp.ade_ano_mes_fim_folha, tmp.ade_ano_mes_fim)) as ade_ano_mes_fim, ");
            query.append("tmp.ade_data, tmp.prd_data_desconto, tmp.prd_numero, tmp.situacao, ");
            query.append("ifnull(ade.ade_indice_exp,ade.ade_indice) as ade_indice, ade.rse_codigo, tmp.org_codigo, tmp.est_codigo, tmp.svc_codigo, ");
            query.append("tmp.scv_codigo, tmp.csa_codigo, tmp.cnv_codigo, tmp.ade_codigo, '8' as sad_codigo, tmp.consolida ");
            query.append("from tb_tmp_exportacao tmp ");
            query.append("inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = tmp.ade_codigo) ");
            query.append("inner join tb_aut_desconto ade on (ade.ade_codigo = oca.ade_codigo) ");
            query.append("inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) ");
            query.append("where oca.toc_codigo in ('10', '14') and oca.oca_data between pex.pex_data_ini and pex.pex_data_fim ");
            query.append("and (ade.ade_prazo is not null or (ade.ade_prazo is null and ade.ade_prazo_folha is not null)) and ade.sad_codigo = '5' ");
            query.append("and (ade.ade_indice = ade.ade_indice_exp or ade.ade_indice_exp is null) ");
            query.append("and (ade.ade_vlr <> ade.ade_vlr_folha or ade.ade_ano_mes_fim <> ade.ade_ano_mes_fim_folha) ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            /*
             * Desconto parcial em duas linhas quando o prazo não é indeterminado e não é um novo contrato: exclusao e inclusão.
             */
            query.setLength(0);
            query.append("insert into tb_tmp_exportacao_alt_det ");
            query.append("select distinct tmp.ser_nome, tmp.ser_cpf, tmp.rse_matricula, tmp.rse_matricula_inst, ");
            query.append("tmp.rse_tipo, tmp.rse_associado, tmp.pos_codigo, tmp.trs_codigo, tmp.org_identificador, tmp.est_identificador, ");
            query.append("tmp.csa_identificador, tmp.svc_identificador, tmp.svc_descricao, tmp.cnv_cod_verba, ");
            query.append("tmp.periodo, tmp.competencia, tmp.data, tmp.pex_periodo, tmp.srs_codigo, tmp.org_cnpj, tmp.est_cnpj, tmp.csa_cnpj, ");
            query.append("tmp.rse_margem, tmp.rse_margem_rest, tmp.rse_margem_2, tmp.rse_margem_rest_2, ");
            query.append("tmp.rse_margem_3, tmp.rse_margem_rest_3, tmp.ade_numero, tmp.ade_prazo, tmp.ade_prd_pagas, ");
            query.append("0.00 as ade_vlr, tmp.ade_vlr_folha, tmp.ade_tipo_vlr, tmp.ade_ano_mes_ini, ");
            query.append("tmp.ade_ano_mes_ini_folha, tmp.ade_ano_mes_fim_folha, ");
            query.append("tmp.svc_prioridade, tmp.cnv_prioridade, ");
            query.append("tmp.ade_data_ref, tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_fim_ref, tmp.ade_cod_reg, ");
            query.append("if(ade.ade_vlr <> ade.ade_vlr_folha, tmp.ade_ano_mes_fim, ifnull(tmp.ade_ano_mes_fim_folha, tmp.ade_ano_mes_fim)) as ade_ano_mes_fim, ");
            query.append("tmp.ade_data, tmp.prd_data_desconto, tmp.prd_numero, tmp.situacao, ");
            query.append("ifnull(ade.ade_indice_exp,ade.ade_indice) as ade_indice, ade.rse_codigo, tmp.org_codigo, tmp.est_codigo, tmp.svc_codigo, ");
            query.append("tmp.scv_codigo, tmp.csa_codigo, tmp.cnv_codigo, tmp.ade_codigo, '8' as sad_codigo, tmp.consolida ");
            query.append("from tb_tmp_exportacao tmp ");
            query.append("inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = tmp.ade_codigo) ");
            query.append("inner join tb_aut_desconto ade on (ade.ade_codigo = oca.ade_codigo) ");
            query.append("inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) ");
            query.append("where ade.ade_codigo in ('").append(TextHelper.join(ades, "','")).append("') ");
            query.append("and ade.sad_codigo <> '4' ");
            query.append("and ade.ade_prazo is not null ");
            query.append("and not exists(select 1 from tb_tmp_exportacao_alt_det tmp1 where tmp1.ade_numero = tmp.ade_numero) ");
            LOG.debug(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("insert into tb_tmp_exportacao (ser_nome, ser_cpf, rse_matricula, rse_matricula_inst, ");
            query.append("rse_tipo, rse_associado, pos_codigo, trs_codigo, org_identificador, est_identificador, ");
            query.append("csa_identificador, svc_identificador, svc_descricao, cnv_cod_verba, ");
            query.append("periodo, competencia, data, pex_periodo, srs_codigo, org_cnpj, est_cnpj, csa_cnpj, ");
            query.append("rse_margem, rse_margem_rest, rse_margem_2, rse_margem_rest_2, ");
            query.append("rse_margem_3, rse_margem_rest_3, ade_numero, ade_prazo, ade_prd_pagas, ");
            query.append("ade_vlr, ade_vlr_folha, ade_tipo_vlr, ade_ano_mes_ini, ");
            query.append("ade_ano_mes_ini_folha, ade_ano_mes_fim_folha, ");
            query.append("svc_prioridade, cnv_prioridade, ");
            query.append("ade_data_ref, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, ade_cod_reg, ");
            query.append("ade_ano_mes_fim, ade_data, prd_data_desconto, prd_numero, situacao, ");
            query.append("ade_indice, rse_codigo, org_codigo, est_codigo, svc_codigo, ");
            query.append("scv_codigo, csa_codigo, cnv_codigo, ade_codigo, sad_codigo, consolida) ");
            query.append("select ser_nome, ser_cpf, rse_matricula, rse_matricula_inst, ");
            query.append("rse_tipo, rse_associado, pos_codigo, trs_codigo, org_identificador, est_identificador, ");
            query.append("csa_identificador, svc_identificador, svc_descricao, cnv_cod_verba, ");
            query.append("periodo, competencia, data, pex_periodo, srs_codigo, org_cnpj, est_cnpj, csa_cnpj, ");
            query.append("rse_margem, rse_margem_rest, rse_margem_2, rse_margem_rest_2, ");
            query.append("rse_margem_3, rse_margem_rest_3, ade_numero, ade_prazo, ade_prd_pagas, ");
            query.append("ade_vlr, ade_vlr_folha, ade_tipo_vlr, ade_ano_mes_ini, ");
            query.append("ade_ano_mes_ini_folha, ade_ano_mes_fim_folha, ");
            query.append("svc_prioridade, cnv_prioridade, ");
            query.append("ade_data_ref, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, ade_cod_reg, ");
            query.append("ade_ano_mes_fim, ade_data, prd_data_desconto, prd_numero, situacao, ");
            query.append("ade_indice, rse_codigo, org_codigo, est_codigo, svc_codigo, ");
            query.append("scv_codigo, csa_codigo, cnv_codigo, ade_codigo, sad_codigo, consolida ");
            query.append("from tb_tmp_exportacao_alt_det");
            LOG.debug(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("update tb_tmp_exportacao tmp ");
            query.append("inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = tmp.ade_codigo) ");
            query.append("inner join tb_aut_desconto ade on (ade.ade_codigo = oca.ade_codigo) ");
            query.append("inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) ");
            query.append("set tmp.sad_codigo = '4', tmp.ade_vlr_folha = null, tmp.ade_ano_mes_fim_folha = null ");
            query.append("where oca.toc_codigo in ('10', '14') ");
            query.append("and oca.oca_data between pex.pex_data_ini and pex.pex_data_fim ");
            query.append("and ade.ade_prd_pagas > 0 ");
            query.append("and (ade.ade_prazo is not null or (ade.ade_prazo is null and ade.ade_prazo_folha is not null)) ");
            query.append("and ade.sad_codigo = '5' ");
            query.append("and tmp.sad_codigo = '5' ");
            query.append("and (ade.ade_indice = ade.ade_indice_exp or ade.ade_indice_exp is null) ");
            query.append("and (ade.ade_vlr <> ade.ade_vlr_folha or ade.ade_ano_mes_fim <> ade.ade_ano_mes_fim_folha) ");
            LOG.debug(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + rows);

            query.setLength(0);
            query.append("update tb_tmp_exportacao tmp ");
            query.append("inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = tmp.ade_codigo) ");
            query.append("inner join tb_aut_desconto ade on (ade.ade_codigo = oca.ade_codigo) ");
            query.append("inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) ");
            query.append("set tmp.sad_codigo = '4', tmp.ade_vlr_folha = null, tmp.ade_ano_mes_fim_folha = null, tmp.situacao = 'I' ");
            query.append("where ade.ade_codigo in ('").append(TextHelper.join(ades, "','")).append("') ");
            query.append("and ade.sad_codigo <> '4' ");
            query.append("and ade.ade_prazo is not null ");
            query.append("and tmp.sad_codigo <> '8' ");
            LOG.debug(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + rows);

            // Remove Exclusão e Inclusão de contratos idênticos
            query.setLength(0);
            query.append("delete tmpEx, tmpIn ");
            query.append("from tb_tmp_exportacao tmpEx ");
            query.append("inner join tb_tmp_exportacao tmpIn on (tmpIn.CNV_COD_VERBA = tmpEx.CNV_COD_VERBA) ");
            query.append("where ");
            query.append("tmpEx.RSE_CODIGO = tmpIn.RSE_CODIGO ");
            query.append("and tmpEx.SAD_CODIGO = '").append(CodedValues.SAD_LIQUIDADA).append("' ");
            query.append("and tmpIn.SAD_CODIGO = '").append(CodedValues.SAD_DEFERIDA).append("' ");
            query.append("and tmpEx.ADE_VLR = tmpIn.ADE_VLR ");
            query.append("and tmpEx.ADE_INDICE = tmpIn.ADE_INDICE and ");
            query.append("(tmpEx.ADE_ANO_MES_FIM = tmpIn.ADE_ANO_MES_FIM or (tmpIn.ADE_PRAZO is null and (tmpEx.ADE_PRAZO = 0 or tmpEx.ADE_PRAZO is null)))");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Remove alterações sem efeito
            query.setLength(0);
            query.append("delete from tb_tmp_exportacao using tb_tmp_exportacao ");
            query.append("inner join tb_aut_desconto ade on (ade.ade_codigo = tb_tmp_exportacao.ade_codigo) ");
            query.append("where ");
            query.append("tb_tmp_exportacao.sad_codigo = '").append(CodedValues.SAD_EMANDAMENTO).append("' and ");
            query.append("tb_tmp_exportacao.ade_vlr = tb_tmp_exportacao.ade_vlr_folha and ");
            query.append("tb_tmp_exportacao.ade_ano_mes_fim = tb_tmp_exportacao.ade_ano_mes_fim_folha and ");
            query.append("(ade.ade_indice = ade.ade_indice_exp or ade.ade_indice_exp is null)");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // atualiza o registro de parcela do período para o valor parcial para os contratos selecionados para tal.
            if (lancamentosParciais != null && !lancamentosParciais.isEmpty()) {
                for (Map.Entry<String, BigDecimal> lancamentoParcial : lancamentosParciais.entrySet()) {
                    String queryAtualizaValor = "update tb_tmp_exportacao set ade_vlr = " + lancamentoParcial.getValue() + " where ade_codigo = '" + lancamentoParcial.getKey() + "'";

                    int linhasAfetadas = 0;

                    LOG.error(query);
                    linhasAfetadas = jdbc.update(queryAtualizaValor, queryParams);
                    LOG.trace("Linhas afetadas: " + linhasAfetadas);
                }
            }
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    protected void alteraOcorrencias(String periodoAnterior) throws DataAccessException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        String query = null;

        // ITEM 18 ALTERA AS OCORRENCIAS DE ALTERAÇÃO DOS CONTRATOS SEM RETORNO
        query = "update tb_aut_desconto ade "
              + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) "
              + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
              + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
              + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
              + "left outer join tb_parcela_desconto prd on (prd.ade_codigo = ade.ade_codigo and PRD_DATA_DESCONTO = '" + periodoAnterior + "') "
              + "set toc_codigo = '3' " // -- Transforma em 3 (Informação) apenas pois o reimplante (se for o caso) irá tratar da reinclusão com o novo valor
              + "where "
              + "oca_data between PEX_DATA_INI and PEX_DATA_FIM " // -- após o último corte e antes do corte atual
              + "and sad_codigo = '5' "
              + "and toc_codigo = '14' "
              + "and (prd.prd_numero is null or spd_codigo = '5') ";
        LOG.debug("ITEM 18: " + query);
        jdbc.update(query, queryParams);
    }

    @Override
    public void preGeraArqLote(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        LOG.warn("preGeraArqLote(): Não faz nada");
    }
}
