package com.zetra.econsig.folha.margem.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ImportaMargemException;
import com.zetra.econsig.folha.margem.ImportaMargemBase;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.values.CodedValues;


public class ImportaMargemTJES extends ImportaMargemBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaMargemTJES.class);

    @Override
    public BigDecimal calcularValorMargemFolha(Short marCodigo, RegistroServidor rse, Map<String, Object> entrada, AcessoSistema responsavel) throws ImportaMargemException {
        // Calcula o valor da margem 3
        if (CodedValues.INCIDE_MARGEM_SIM_3.equals(marCodigo)) {
            BigDecimal valorMargem = BigDecimal.ZERO;

            BigDecimal rseSalario = NumberHelper.objectToBigDecimal(entrada.get("RSE_SALARIO"));
            BigDecimal rseDescontosComp = NumberHelper.objectToBigDecimal(entrada.get("RSE_DESCONTOS_COMP"));

            rseSalario = rseSalario == null ? BigDecimal.ZERO : rseSalario;
            rseDescontosComp = rseDescontosComp == null ? BigDecimal.ZERO : rseDescontosComp;

            if (rse == null) {
                final BigDecimal calcCompulsorioRestante = rseSalario.multiply(BigDecimal.valueOf(0.7)).subtract(rseDescontosComp);
                final BigDecimal calcMargemRestante = rseSalario.multiply(BigDecimal.valueOf(0.4));
                valorMargem = calcMargemRestante.compareTo(calcCompulsorioRestante) <= 0 ? calcMargemRestante : calcCompulsorioRestante;
            } else {
                final List<BigDecimal> rseValoresContratos = getSomaContratosAtivosCompulsorios(rse.getRseCodigo(), responsavel);
                final BigDecimal facultativoEconsig = rseValoresContratos.get(1);
                final BigDecimal compulsorioEconsig = rseValoresContratos.get(0);
                final BigDecimal calcMargemRestante = rseSalario.multiply(BigDecimal.valueOf(0.4)).subtract(facultativoEconsig);
                final BigDecimal calcCompulsorioRestante = rseSalario.multiply(BigDecimal.valueOf(0.7)).subtract(rseDescontosComp).subtract((facultativoEconsig).add(compulsorioEconsig));
                if (calcMargemRestante.compareTo(calcCompulsorioRestante) <= 0) {
                    valorMargem = rseSalario.multiply(BigDecimal.valueOf(0.4));
                } else {
                    valorMargem = rseSalario.multiply(BigDecimal.valueOf(0.7)).subtract(rseDescontosComp);
                }
            }

            return valorMargem;
        }
        return null;
    }

    private List<BigDecimal> getSomaContratosAtivosCompulsorios(String rseCodigo, AcessoSistema responsavel) {
        BigDecimal somaCompulsorios = BigDecimal.ZERO;
        BigDecimal somaNaoCompulsorios = BigDecimal.ZERO;

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("rseCodigo", rseCodigo);
        try {
            final List<Map<String, Object>> rs = jdbc.queryForList("SELECT soma_compulsorios, soma_nao_compulsorios FROM tb_tmp_soma_contratos WHERE rse_codigo = :rseCodigo", queryParams);
            if (rs != null && !rs.isEmpty() && rs.get(0) != null) {
                somaCompulsorios = (BigDecimal) rs.get(0).get("soma_compulsorios");
                somaNaoCompulsorios = (BigDecimal) rs.get(0).get("soma_nao_compulsorios");
            }
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return List.of(somaCompulsorios, somaNaoCompulsorios);
    }

    @Override
    public void preImportacaoMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();

            // DESENV-20965 : Query responsavel por fazer a somatoria dos contratos facultativos e dos contratos compulsorios do servidor;
            query.setLength(0);
            query.append("drop temporary table if exists tb_tmp_soma_contratos");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create temporary table tb_tmp_soma_contratos (rse_codigo varchar(32), soma_compulsorios decimal(13,2), soma_nao_compulsorios decimal(13,2), primary key (rse_codigo))");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("insert into tb_tmp_soma_contratos (rse_codigo, soma_compulsorios, soma_nao_compulsorios) ");
            query.append("select ade.rse_codigo,  ");
            query.append("sum(case when coalesce(pse94.pse_vlr, '0') = '1' and coalesce(pse4.pse_vlr, 'F') = 'F' then ade.ade_vlr ");
            query.append("         when coalesce(pse94.pse_vlr, '0') = '1' and coalesce(pse4.pse_vlr, 'P') = 'P' then coalesce(ade.ade_vlr_folha, 0) ");
            query.append("         else 0 end");
            query.append(") as soma_compulsorios, ");
            query.append("sum(case when coalesce(pse94.pse_vlr, '0') = '0' and coalesce(pse4.pse_vlr, 'F') = 'F' then ade.ade_vlr ");
            query.append("         when coalesce(pse94.pse_vlr, '0') = '0' and coalesce(pse4.pse_vlr, 'P') = 'P' then coalesce(ade.ade_vlr_folha, 0) ");
            query.append("         else 0 end");
            query.append(") as soma_nao_compulsorios ");
            query.append("from tb_aut_desconto ade ");
            query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("left outer join tb_param_svc_consignante pse4 on (pse4.svc_codigo = cnv.svc_codigo and pse4.tps_codigo = '").append(CodedValues.TPS_TIPO_VLR).append("') ");
            query.append("left outer join tb_param_svc_consignante pse94 on (cnv.svc_codigo = pse94.svc_codigo and pse94.tps_codigo = '").append(CodedValues.TPS_SERVICO_COMPULSORIO).append("') ");
            query.append("where ade.sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "','")).append("') ");
            query.append("and ade.ade_inc_margem <> 0 ");
            query.append("group by ade.rse_codigo");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void preRecalculoMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();

            // DESENV-20965 : Tabela temporaria responsavel por definir se a margem escolhida do servidor será a margem 40 ou a margem 70
            query.setLength(0);
            query.append("drop temporary table if exists tb_tmp_escolha_margem");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create temporary table tb_tmp_escolha_margem (rse_codigo varchar(32), margem varchar(10), primary key (rse_codigo))");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("insert into tb_tmp_escolha_margem (rse_codigo, margem) ");
            query.append("select ade.rse_codigo, ");
            query.append("if( ");
            query.append("coalesce(rse.rse_salario, 0) * 0.4 - (sum(case when coalesce(pse94.pse_vlr, '0') = '0' and coalesce(pse4.pse_vlr, 'F') = 'F' then ade.ade_vlr when coalesce(pse94.pse_vlr, '0') = '0' and coalesce(pse4.pse_vlr, 'P') = 'P' then coalesce(ade.ade_vlr_folha, 0) else 0 end)) < ");
            query.append("coalesce(rse.rse_salario, 0) * 0.7 - (coalesce(rse.rse_descontos_comp, 0)) - ((sum(case when coalesce(pse94.pse_vlr, '0') = '1' and coalesce(pse4.pse_vlr, 'F') = 'F' then ade.ade_vlr when coalesce(pse94.pse_vlr, '0') = '1' and coalesce(pse4.pse_vlr, 'P') = 'P' then coalesce(ade.ade_vlr_folha, 0) else 0 end)) + (sum(case when coalesce(pse94.pse_vlr, '0') = '0' and coalesce(pse4.pse_vlr, 'F') = 'F' then ade.ade_vlr when coalesce(pse94.pse_vlr, '0') = '0' and coalesce(pse4.pse_vlr, 'P') = 'P' then coalesce(ade.ade_vlr_folha, 0) else 0 end))), ");
            query.append("'margem40', 'margem70' ");
            query.append(") as margem ");
            query.append("from tb_aut_desconto ade ");
            query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) ");
            query.append("left outer join tb_param_svc_consignante pse4 on (pse4.svc_codigo = cnv.svc_codigo and pse4.tps_codigo = '").append(CodedValues.TPS_TIPO_VLR).append("') ");
            query.append("left outer join tb_param_svc_consignante pse94 on (cnv.svc_codigo = pse94.svc_codigo and pse94.tps_codigo = '").append(CodedValues.TPS_SERVICO_COMPULSORIO).append("') ");
            query.append("where ade.sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "','")).append("') ");
            query.append("and ade.ade_inc_margem <> 0 ");
            query.append("group by ade.rse_codigo");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // DESENV-20965 : Tabela temporario responsavel por guardar os valores originais do campo ade_inc_margem para que após o recalculo do margem esses valores sejam restaurados.
            query.setLength(0);
            query.append("drop temporary table if exists tb_tmp_margem_70_original");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create temporary table tb_tmp_margem_70_original (ade_codigo varchar(32), ade_inc_margem smallint, primary key (ade_codigo))");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("insert into tb_tmp_margem_70_original (ade_codigo, ade_inc_margem) ");
            query.append("select ade.ade_codigo, ade.ade_inc_margem ");
            query.append("from tb_aut_desconto ade ");
            query.append("inner join tb_tmp_escolha_margem mca on (ade.rse_codigo = mca.rse_codigo and mca.margem = 'margem40') ");
            query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("inner join tb_param_svc_consignante pse94 on (cnv.svc_codigo = pse94.svc_codigo and pse94.tps_codigo = '").append(CodedValues.TPS_SERVICO_COMPULSORIO).append("') ");
            query.append("where ade.sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "','")).append("') ");
            query.append("and ade.ade_inc_margem <> 0 ");
            query.append("and pse94.pse_vlr = '1'");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // DESENV-20965 : Update responsavel por setar o campo ade_inc_margem dos contratos compulsorios para zero, para que o recalculo da margem 40 dos
            // servidores seja feito sem levar em consideração os valores dos contratos compulsórios.
            query.setLength(0);
            query.append("update tb_aut_desconto ade ");
            query.append("inner join tb_tmp_escolha_margem mca on (ade.rse_codigo = mca.rse_codigo and mca.margem = 'margem40') ");
            query.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("inner join tb_param_svc_consignante pse94 on (cnv.svc_codigo = pse94.svc_codigo and pse94.tps_codigo = '").append(CodedValues.TPS_SERVICO_COMPULSORIO).append("') ");
            query.append("set ade.ade_inc_margem = 0 ");
            query.append("where ade.sad_codigo in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ATIVOS, "','")).append("') ");
            query.append("and ade.ade_inc_margem <> 0 ");
            query.append("and pse94.pse_vlr = '1'");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void posRecalculoMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();

            // DESENV-20965 Update responsavel por restaurar os valores de ade_inc_margem os contratos compulsorios após o recalculo da margem
            query.setLength(0);
            query.append("update tb_aut_desconto ade ");
            query.append("inner join tb_tmp_margem_70_original mao on (ade.ade_codigo = mao.ade_codigo) ");
            query.append("set ade.ade_inc_margem = mao.ade_inc_margem");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}
