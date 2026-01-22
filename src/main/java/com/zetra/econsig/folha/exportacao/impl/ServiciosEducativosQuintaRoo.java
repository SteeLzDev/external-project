package com.zetra.econsig.folha.exportacao.impl;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ServiciosEducativosQuintaRoo</p>
 * <p>Description: Implementações específicas para o sistema Servicios Educativos de Quinta Roo.</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ServiciosEducativosQuintaRoo extends Quinzenal {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ServiciosEducativosQuintaRoo.class);

    @Override
    public void posCriacaoTabelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        super.posCriacaoTabelas(parametrosExportacao, responsavel);

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            StringBuilder query = new StringBuilder();
            query.append("drop table if exists tb_tmp_exp_relancamentos");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create table tb_tmp_exp_relancamentos (ade_codigo varchar(32), org_codigo varchar(32), pex_periodo date, pex_data_ini datetime, preserva char(1), tem_oca char(1))");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void preProcessaAutorizacoes(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        List<String> orgCodigos = parametrosExportacao.getOrgCodigos();
        List<String> estCodigos = parametrosExportacao.getEstCodigos();
        List<String> verbas = parametrosExportacao.getVerbas();

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final String defaultReimplante = (String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_DEFAULT_PARAM_SVC_REIMPLANTE, CodedValues.TPC_NAO, responsavel);
        final String defaultPreservacao = (String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_DEFAULT_PARAM_SVC_PRESERVA_PRD, CodedValues.TPC_NAO, responsavel);

        queryParams.addValue("defaultReimplante", defaultReimplante);
        queryParams.addValue("defaultPreservacao", defaultPreservacao);

        try {

            String complemento = "";

            if (orgCodigos != null && orgCodigos.size() > 0) {
                complemento += " and cnv.org_codigo in (:orgCodigos) ";
                queryParams.addValue("orgCodigos", orgCodigos);
            }
            if (estCodigos != null && estCodigos.size() > 0) {
                complemento += " and org.est_codigo in (:estCodigos) ";
                queryParams.addValue("estCodigos", estCodigos);
            }
            if (verbas != null && verbas.size() > 0) {
                complemento += " and cnv.cnv_cod_verba in (:verbas) ";
                queryParams.addValue("verbas", verbas);
            }

            String query = "delete from tb_tmp_exp_relancamentos";
            LOG.debug(query);
            jdbc.update(query, queryParams);

            query = "insert into tb_tmp_exp_relancamentos (ade_codigo, org_codigo, pex_periodo, pex_data_ini, preserva, tem_oca) "
                  + "select ade.ade_codigo, org.org_codigo, pex.pex_periodo, pex.pex_data_ini, "
                  + "coalesce(pscP.psc_vlr, :defaultPreservacao) as preserva, "
                  + "case when oca.oca_codigo is not null then 'S' else 'N' end as tem_oca "

                  + "from tb_aut_desconto ade "
                  + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                  + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                  + "inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo) "
                  + "inner join tb_orgao org on (org.org_codigo = cnv.org_codigo) "
                  + "inner join tb_periodo_exportacao pex on (pex.org_codigo = org.org_codigo) "
                  + "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
                  + "inner join tb_tmp_calendario_quinzenal cal1 on (cal1.org_codigo = org.org_codigo and cal1.periodo = pex.pex_periodo) "
                  + "inner join tb_tmp_calendario_quinzenal cal2 on (cal2.org_codigo = org.org_codigo and cal2.sequencia = cal1.sequencia - 2) "
                  + "left outer join tb_param_svc_consignataria pscR on (cnv.svc_codigo = pscR.svc_codigo and cnv.csa_codigo = pscR.csa_codigo and pscR.tps_codigo = '" + CodedValues.TPS_REIMPLANTACAO_AUTOMATICA + "' and coalesce(pscR.psc_ativo, 1) = 1) "
                  + "left outer join tb_param_svc_consignataria pscP on (cnv.svc_codigo = pscP.svc_codigo and cnv.csa_codigo = pscP.csa_codigo and pscP.tps_codigo = '" + CodedValues.TPS_PRESERVA_PRD_REJEITADA_REIMPL + "' and coalesce(pscP.psc_ativo, 1) = 1) "
                  + "left outer join tb_ocorrencia_autorizacao oca on (ade.ade_codigo = oca.ade_codigo and oca.toc_codigo = '" + CodedValues.TOC_RELANCAMENTO + "' and oca.oca_data between pex.pex_data_ini and pex.pex_data_fim) "

                  // seleciona contratos deferidos ou em andamento, do período atual ou anterior
                  // que integram com a folha e ainda existem parcelas a serem pagas
                  + "where ade.sad_codigo in ('" + CodedValues.SAD_DEFERIDA + "', '" + CodedValues.SAD_EMANDAMENTO + "') "
                  + "and ade.ade_ano_mes_ini <= pex.pex_periodo "
                  + "and ade.ade_int_folha = 1 "
                  + "and coalesce(ade.ade_prazo, 999) > coalesce(ade.ade_prd_pagas, 0) "

                  // seleciona contratos rejeitados no período passado
                  + "and exists (select 1 from tb_parcela_desconto prd where ade.ade_codigo = prd.ade_codigo and prd.prd_data_desconto = pex.pex_periodo_ant and prd.spd_codigo = '" + CodedValues.SPD_REJEITADAFOLHA + "') "

                  // seleciona contratos de servidores com margem positiva ou zero
                  // ou que a parcela de 2 períodos anteriores tenha sido paga
                  + "and ((case "
                  + " when ade_inc_margem = '1' then rse.rse_margem_rest "
                  + " when ade_inc_margem = '2' then rse.rse_margem_rest_2 "
                  + " when ade_inc_margem = '3' then rse.rse_margem_rest_3 "
                  + " else 0 end >= 0) "
                  + " or exists (select 1 from tb_parcela_desconto prd where ade.ade_codigo = prd.ade_codigo and prd.prd_data_desconto = cal2.periodo and prd.spd_codigo in ('" + CodedValues.SPD_LIQUIDADAFOLHA + "', '" + CodedValues.SPD_LIQUIDADAMANUAL + "'))) "

                  // não seleciona contratos de consignatárias que não reimplantam ou que já deveriam ter terminado.
                  // Se não preserva parcela, basta testar a data final do contrato.
                  + "and (coalesce(pscR.psc_vlr, :defaultReimplante) = 'S') "
                  + "and ((coalesce(pscP.psc_vlr, :defaultPreservacao) = 'N' and (ade.ade_ano_mes_fim >= pex.pex_periodo or ade_ano_mes_fim is null)) or coalesce(pscP.psc_vlr, :defaultPreservacao) = 'S') "

                  // não seleciona contratos de servidores bloquados ou excluídos
                  // ou que possuam bloqueio de convênio ou serviço
                  + "and rse.srs_codigo = '" + CodedValues.SRS_ATIVO + "' "
                  + "and not exists (select 1 from tb_param_convenio_registro_ser pcr where rse.rse_codigo = pcr.rse_codigo and cnv.cnv_codigo = pcr.cnv_codigo and pcr.pcr_vlr = '0' and pcr.tps_codigo = '" + CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO + "') "
                  + "and not exists (select 1 from tb_param_servico_registro_ser psr  where rse.rse_codigo = psr.rse_codigo and cnv.svc_codigo = psr.svc_codigo and psr.psr_vlr = '0' and psr.tps_codigo = '" + CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO + "') "
                  + "and not exists (select 1 from tb_param_nse_registro_ser pnr      where rse.rse_codigo = pnr.rse_codigo and svc.nse_codigo = pnr.nse_codigo and pnr.pnr_vlr = '0' and pnr.tps_codigo = '" + CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO + "') "

                  + complemento
                  + " group by ade.ade_codigo"
                  ;
            LOG.debug(query);
            jdbc.update(query, queryParams);

            // Reimplante com preservação de parcela
            query = "update tb_aut_desconto ade "
                  + "inner join tb_tmp_exp_relancamentos tmp on (tmp.ade_codigo = ade.ade_codigo) "
                  + "inner join tb_tmp_calendario_quinzenal cal1 on (cal1.org_codigo = tmp.org_codigo and cal1.periodo = tmp.pex_periodo) "
                  + "inner join tb_tmp_calendario_quinzenal cal2 on (cal2.org_codigo = tmp.org_codigo and cal2.sequencia = cal1.sequencia + coalesce(ade.ade_prazo - coalesce(ade.ade_prd_pagas, 0) - 1, 0)) "
                  + "set ade.sad_codigo = '4', "
                  + "ade.ade_ano_mes_ini_ref = coalesce(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini), "
                  + "ade.ade_ano_mes_fim_ref = coalesce(ade.ade_ano_mes_fim_ref, ade.ade_ano_mes_fim), "
                  + "ade.ade_ano_mes_ini = tmp.pex_periodo, "
                  + "ade.ade_prazo = ade.ade_prazo - coalesce(ade.ade_prd_pagas, 0), "
                  + "ade.ade_prd_pagas = 0, "
                  + "ade.ade_ano_mes_fim = (case when ade.ade_prazo is not null then cal2.periodo else null end) "
                  + "where tmp.preserva = 'S'";
            LOG.debug(query);
            jdbc.update(query, queryParams);

            // Reimplante sem preservação parcela
            query = "update tb_aut_desconto ade "
                  + "inner join tb_tmp_exp_relancamentos tmp on (tmp.ade_codigo = ade.ade_codigo) "
                  + "inner join tb_tmp_calendario_quinzenal cal1 on (cal1.org_codigo = tmp.org_codigo and cal1.periodo = tmp.pex_periodo) "
                  + "inner join tb_tmp_calendario_quinzenal cal2 on (cal2.org_codigo = tmp.org_codigo and cal2.periodo = coalesce(ade.ade_ano_mes_fim, tmp.pex_periodo)) "
                  + "set ade.sad_codigo = '4', "
                  + "ade.ade_ano_mes_ini_ref = coalesce(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini), "
                  + "ade.ade_ano_mes_fim_ref = coalesce(ade.ade_ano_mes_fim_ref, ade.ade_ano_mes_fim), "
                  + "ade.ade_ano_mes_ini = tmp.pex_periodo, "
                  + "ade.ade_prd_pagas = 0, "
                  + "ade.ade_prazo = (case when ade.ade_prazo is not null then cal2.sequencia - cal1.sequencia + 1 else null end) "
                  + "where tmp.preserva = 'N'";
            LOG.debug(query);
            jdbc.update(query, queryParams);

            // Insere a ocorrência de reimplantação automática para os contratos
            // que serão enviados. A ocorrência é inserida para todos os contratos.
            query = "INSERT INTO tb_ocorrencia_autorizacao (OCA_CODIGO, TOC_CODIGO, ADE_CODIGO, USU_CODIGO, OCA_DATA, OCA_PERIODO, OCA_OBS) "
                  + "SELECT concat(lpad(ade.ade_numero, 10, '0'), '-', date_format(now(), '%Y%m%d%H%i%S')), "
                  + "'" + CodedValues.TOC_RELANCAMENTO + "', ade.ade_codigo, '" + CodedValues.USU_CODIGO_SISTEMA + "', tmp.pex_data_ini, tmp.pex_periodo, '" + ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.reimplante.automatico", responsavel) + "' "
                  + "from tb_aut_desconto ade "
                  + "inner join tb_tmp_exp_relancamentos tmp on (tmp.ade_codigo = ade.ade_codigo) "
                  + "where tmp.tem_oca = 'N'"
                  ;
            LOG.debug(query);
            jdbc.update(query, queryParams);

            // Remove ocorrências de relançamento anteriores à execução atual
            query = "DELETE FROM tb_ocorrencia_autorizacao "
                  + "USING tb_ocorrencia_autorizacao "
                  + "inner join tb_aut_desconto ade on (ade.ade_codigo = tb_ocorrencia_autorizacao.ade_codigo) "
                  + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                  + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                  + "inner join tb_orgao org on (org.org_codigo = cnv.org_codigo) "
                  + "inner join tb_periodo_exportacao pex on (pex.org_codigo = org.org_codigo) "
                  + "where tb_ocorrencia_autorizacao.toc_codigo = '" + CodedValues.TOC_RELANCAMENTO + "' "
                  + "and tb_ocorrencia_autorizacao.usu_codigo = '" + CodedValues.USU_CODIGO_SISTEMA + "' "
                  + "and tb_ocorrencia_autorizacao.oca_data between pex.pex_data_ini and pex.pex_data_fim "
                  + "and not exists (select 1 from tb_tmp_exp_relancamentos tmp where tmp.ade_codigo = ade.ade_codigo) "
                  + complemento
                  ;
            LOG.debug(query);
            jdbc.update(query, queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}