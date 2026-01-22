package com.zetra.econsig.folha.exportacao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.delegate.PeriodoDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: Aeronautica2</p>
 * <p>Description: Implementações específicas para a Aeronautica.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Aeronautica2 extends Aeronautica {

	private static final long serialVersionUID = 7610708089284623143L;

	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Aeronautica2.class);

    // Determina se a validação de margem pelo saldo do período deve ser executado
    private final boolean validaMargemExportacao;

    // Determina se os contratos do período deve ser enviados mesmo que o servidor não tenha margem.
    private final boolean enviaContratosPeriodo;

	protected Map<String, BigDecimal> lancamentosParciais;

    public Aeronautica2() {
        // Habilita a validação de margem, enviando para a folha apenas o que cabe
        // na margem no momento da exportação.
        validaMargemExportacao = true;
        // 09/02/2012: os contratos do período serão marcados para envio, a pedido do Gestor.
        enviaContratosPeriodo = true;
    }

	@Override
	public void posCriacaoTabelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
		super.posCriacaoTabelas(parametrosExportacao, responsavel);

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
			StringBuilder query = new StringBuilder();

			query.append("alter table tb_tmp_exp_relancamentos ");
			query.append("add (nse_codigo char(32), reimplante_alteracao char(1), ade_vlr_folha decimal(13,2), aut_pg_parcial char(1), ade_prd_pagas smallint(6), ade_prazo smallint(6), prioridade_compulsorio smallint(6)) ");

			LOG.debug(query.toString());
			jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }

	}

	@Override
    protected void selectCandidatasRelancamento(List<String> orgCodigos, List<String> estCodigos, List<String> verbas, String periodo) throws DataAccessException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("periodo", periodo);

        final String defaultReimplante = (String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_DEFAULT_PARAM_SVC_REIMPLANTE, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema());
        final String defaultPreservacao = (String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_DEFAULT_PARAM_SVC_PRESERVA_PRD, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema());

        queryParams.addValue("defaultReimplante", defaultReimplante);
        queryParams.addValue("defaultPreservacao", defaultPreservacao);

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

		String query = "DELETE FROM tb_tmp_exp_relancamentos";
		LOG.debug(query);
		jdbc.update(query, queryParams);
		/*
		 * As ADEs candidatas a relançamento são: - Aquelas que estão em estoque (12) -
		 * Aquelas que estão em estoque mensal (16) cujo ade_vlr_folha is null, pois
		 * contratos que vão para estoque por motivo STATUS = N, P, T (ade_vlr_folha not
		 * null) não devem ser relançados. - Aquelas que estão em andamento (5) cujo
		 * ade_vlr_folha < ade_valor e que não têm ocorrência de alteração, ou seja,
		 * alterações rejeitadas pela folha ou alterações que não cabiam na margem. OBS:
		 * O campo pode_reimplantar é preenchido de acordo com a seguinte regra: O
		 * convênio tem reimplante automático, ou houve uma alteração no período atual,
		 * ou houve uma inclusão no período atual. Os dois últimos casos asseguram que
		 * uma alteração ou um novo contrato que foram diretamente para estoque sejam
		 * exportados, se couberem na margem. Lembrete: Alterações do período atual que
		 * não cabiam na margem já foram canceladas (toc_codigo = '14' passado para
		 * toc_codigo = '3'). OBS 2: Precisamos selecionar tmb os contratos que não
		 * podem ser reimplantados, pois eles tmb decrementam a margem disponível e os
		 * valores desses contratos devem ser considerados no cálculo de quais contratos
		 * podem ser reimplantados.
		 */
		query = "INSERT INTO tb_tmp_exp_relancamentos (ade_codigo, sad_codigo, ade_vlr, rse_matricula, ade_inc_margem, rse_margem, margem_rest, enviar, svc_prioridade, cnv_prioridade, nse_codigo, ade_data, ade_ano_mes_ini, ade_ano_mes_fim, ade_numero, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, tem_oca, pode_reimplantar, reimplante_alteracao, aut_pg_parcial, ade_prd_pagas, ade_prazo, ade_vlr_folha, prioridade_compulsorio) "
				+ "select distinct ade.ade_codigo, sad_codigo, ade_vlr as ade_vlr, rse.rse_matricula, "
				+ "ade_inc_margem, case " + " when ade_inc_margem = '1' then rse.rse_margem "
				+ " when ade_inc_margem = '2' then rse.rse_margem_2 "
				+ " when ade_inc_margem = '3' then rse.rse_margem_3 " + "end as RSE_MARGEM, case "
				+ " when ade_inc_margem = '1' then rse.rse_margem_rest "
				+ " when ade_inc_margem = '2' then rse.rse_margem_rest_2 "
				+ " when ade_inc_margem = '3' then rse.rse_margem_rest_3 " + "end as MARGEM_REST, " + "'N' as enviar, "
				+ "IFNULL(svc_prioridade, '99999') + 0 as svc_prioridade, IFNULL(cnv_prioridade, 99999) as cnv_prioridade, "
				+ "svc.nse_codigo, "
				+ "ade_data, ade_ano_mes_ini, ade_ano_mes_fim, ade_numero, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, "
				+ "IF(oca10.oca_codigo is null, 'N', 'S') AS tem_oca, " + "if(ifnull(psc.psc_vlr, :defaultReimplante) = 'S' "
				+ " or oca14.oca_codigo is not null or oca4.oca_codigo is not null, 'S', 'N') AS pode_reimplantar, "
				+ "if(ade.sad_codigo = '5' and ade.ade_vlr_folha <> ade.ade_vlr and oca14.oca_codigo is null, 'S', 'N') as reimplante_alteracao, "
				+ "pcs32.pcs_vlr as aut_pg_parcial, ade.ade_prd_pagas, ade.ade_prazo, "
				+ "coalesce(ade.ade_vlr_folha, 0) as ade_vlr_folha, "
				+ "if(svc.nse_codigo = '" + CodedValues.NSE_COMPULSORIO + "', 1, 999999) as prioridade_compulsorio "
				+ "from tb_aut_desconto ade " + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
				+ "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
				+ "inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo) "
				+ "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
				+ "inner join tb_orgao org on (org.org_codigo = cnv.org_codigo) "
				+ "inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo) "
				+ "inner join tb_consignataria csa on (csa.csa_codigo = cnv.csa_codigo) "
				+ "left outer join tb_param_convenio_registro_ser pcr on (rse.rse_codigo = pcr.rse_codigo and cnv.cnv_codigo = pcr.cnv_codigo and pcr.pcr_vlr = '0' and pcr.tps_codigo = '"
				+ CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO + "') "
				+ "left outer join tb_param_servico_registro_ser psr on (rse.rse_codigo = psr.rse_codigo and cnv.svc_codigo = psr.svc_codigo and psr.psr_vlr = '0' and psr.tps_codigo = '"
				+ CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO + "') "
				+ "left outer join tb_param_nse_registro_ser pnr on (rse.rse_codigo = pnr.rse_codigo and svc.nse_codigo = pnr.nse_codigo and pnr.pnr_vlr = '0' and pnr.tps_codigo = '"
				+ CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO + "') "
				+ "left outer join tb_param_svc_consignataria psc on (cnv.svc_codigo = psc.svc_codigo and cnv.csa_codigo = psc.csa_codigo and psc.tps_codigo = '35' and (psc.psc_ativo = '1' or psc.psc_ativo is null)) "
				+ "left outer join tb_param_svc_consignataria psc1 on (cnv.svc_codigo = psc1.svc_codigo and cnv.csa_codigo = psc1.csa_codigo and psc1.tps_codigo = '36' and (psc1.psc_ativo = '1' or psc1.psc_ativo is null)) "
				+ "left outer join tb_param_consignataria pcs32 on (csa.csa_codigo = pcs32.csa_codigo and pcs32.tpa_codigo = '32') "
				+ "left outer join tb_ocorrencia_autorizacao oca10 on (ade.ade_codigo = oca10.ade_codigo and oca10.toc_codigo = '10' and oca10.oca_data between pex.pex_data_ini and pex.pex_data_fim) "
				+ "left outer join tb_ocorrencia_autorizacao oca14 on (ade.ade_codigo = oca14.ade_codigo and oca14.toc_codigo = '14' and oca14.oca_data between pex.pex_data_ini and pex.pex_data_fim) "
				+ "left outer join tb_ocorrencia_autorizacao oca4  on (ade.ade_codigo = oca4.ade_codigo  and oca4.toc_codigo = '4'   and oca4.oca_data  between pex.pex_data_ini and pex.pex_data_fim) "
				+ "where " + "((sad_codigo = '" + CodedValues.SAD_ESTOQUE + "') or " + " (sad_codigo = '"
				+ CodedValues.SAD_ESTOQUE_MENSAL + "' and ade_vlr_folha is null) or " + " (sad_codigo in ('"
				+ CodedValues.SAD_DEFERIDA + "', '" + CodedValues.SAD_EMANDAMENTO
				+ "') and ade_vlr_folha is null and ade.ade_ano_mes_ini <= pex.pex_periodo) or " + " (sad_codigo = '"
				+ CodedValues.SAD_EMANDAMENTO
				+ "' and ade_vlr_folha is not null and ade_vlr_folha <> ade_vlr and oca14.oca_codigo is null)) "
				+ "and ade.ade_ano_mes_ini <= pex.pex_periodo " + "and ade_int_folha = '1' "
				+ "and ade_inc_margem in ('1', '2', '3') " + "and srs_codigo = '" + CodedValues.SRS_ATIVO + "' "
				+ "and cnv.scv_codigo = '" + CodedValues.SCV_ATIVO + "' "
				// não seleciona contratos que já deveriam ter terminado.
				// Se não preserva parcela, basta testar a data final do contrato.
				+ "and ((ifnull(psc1.psc_vlr, :defaultPreservacao) = 'N' and (ade.ade_ano_mes_fim >= :periodo "
				+ "or ade_ano_mes_fim is null)) or ifnull(psc1.psc_vlr, :defaultPreservacao) = 'S') " 
                + complemento 
                + " and pcr.pcr_vlr is null " 
                + " and psr.psr_vlr is null "
				+ " and pnr.pnr_vlr is null ";

		LOG.debug(query);
		jdbc.update(query, queryParams);
	}

    @Override
    public void preProcessaAutorizacoes(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        List<String> orgCodigos = parametrosExportacao.getOrgCodigos();
        List<String> estCodigos = parametrosExportacao.getEstCodigos();
        List<String> verbas = parametrosExportacao.getVerbas();
        String acao = parametrosExportacao.getAcao();

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final String defaultPreservacao = (String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_DEFAULT_PARAM_SVC_PRESERVA_PRD, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema());
        queryParams.addValue("defaultPreservacao", defaultPreservacao);
        try {
            String query = null;

            // Obtém o período atual
            PeriodoDelegate perDelegate = new PeriodoDelegate();
            TransferObject periodoExportacao = perDelegate.obtemPeriodoExportacaoDistinto(orgCodigos, estCodigos, responsavel);
            String periodo = DateHelper.format((java.util.Date) periodoExportacao.getAttribute(Columns.PEX_PERIODO), "yyyy-MM-dd");
            String periodoDataFim = DateHelper.format((java.util.Date) periodoExportacao.getAttribute(Columns.PEX_DATA_FIM), "yyyy-MM-dd HH:mm");
            LOG.debug("periodoAtual=" + periodo);
            // Período anterior será sempre = periodoAtual - 1
            Calendar calPeriodoAnterior = Calendar.getInstance();
            calPeriodoAnterior.setTime((java.util.Date) periodoExportacao.getAttribute(Columns.PEX_PERIODO));
            calPeriodoAnterior.add(Calendar.MONTH, -1);
            String periodoAnterior = DateHelper.format(calPeriodoAnterior.getTime(), "yyyy-MM-dd");
            LOG.debug("periodoAnterior=" + periodoAnterior);

            // ALTERA OCORRÊNCIAS
            alteraOcorrencias(periodoAnterior);

            if (acao == null || acao.equals(ParametrosExportacao.AcaoEnum.EXPORTAR.getCodigo())) {
                if (validaMargemExportacao) {
                    // COLOCA EM ESTOQUE CONTRATOS CUJO SERVIDOR ESTAVA COM MARGEM RESTANTE NEGATIVA NO DIA DO CORTE
                    moveParaEstoqueMargemNegativa();

                    // CANCELA ALTERAÇÕES DE CONTRATOS COM MARGEM RESTANTE NEGATIVA NO DIA DO CORTE
                    cancelaAlteracaoMargemNegativa();
                }

                // Prepara lista de ADEs candidatas para relançamento
                selectCandidatasRelancamento(orgCodigos, estCodigos, verbas, periodo);

                // DETERMINA QUAIS CANDIDATOS A RELANÇAMENTO CABEM NAS MARGENS E CONFIGURA O ENVIO = S
                String[] adeIncMargens = {"1", "3"};
                for (String adeIncMargen : adeIncMargens) {

                    // Lista de ADEs candidatas a reimplantação
                    query = "select * from tb_tmp_exp_relancamentos "
                            + "where pode_reimplantar = 'S' "
                            + "and ade_inc_margem = '" + adeIncMargen + "' "
                            + "order by rse_matricula, svc_prioridade, cnv_prioridade, ade_numero";
                    LOG.debug(query);
                    final List<Map<String, Object>> resultSet = jdbc.queryForList(query, queryParams);

                    String matriculaAtual, matricula = "", queryMargem;
                    boolean continuarTestandoMatricula = true;
                    BigDecimal adeVlr, margemRest = new BigDecimal("0"), margemRestOrig = new BigDecimal("0"), margem = new BigDecimal("0");
                    BigDecimal adeVlrFolha;
                    String adeCodigo = null;
                    String nseCodigo = null;
                    String autPgParcial = null;

                    List<String> adeCodigos = new ArrayList<>();

                    for (Map<String, Object> row : resultSet) {
                        matriculaAtual = row.get("RSE_MATRICULA").toString();
                        margem = (BigDecimal) row.get("RSE_MARGEM");
                        margemRestOrig = (BigDecimal) row.get("MARGEM_REST");
                        adeVlr = (BigDecimal) row.get("ADE_VLR");
                        adeVlrFolha = (BigDecimal) row.get("ADE_VLR_FOLHA");
                        nseCodigo = (String) row.get("nse_codigo");
                        autPgParcial = (String) row.get("aut_pg_parcial");
                        adeCodigo = (String) row.get("ADE_CODIGO");

                        if (!matriculaAtual.equals(matricula)) {
                            /*
                                * Seleciona todos os candidatos a relancamento, pois todos eles
                                * estão comprometendo a margem, logo devem ser considerados
                                * no cálculo.
                                * Margem 1 casada com a margem 3 pela esquerda: soma os contratos
                                * que incidem nas duas margens.
                                */
                            queryMargem = "select sum(ade_vlr) as total from tb_tmp_exp_relancamentos "
                                        + "where rse_matricula = :matriculaAtual "
                                        + "and ade_inc_margem in ('1', '3')";
                            queryParams.addValue("matriculaAtual", matriculaAtual);
                            BigDecimal sumAdeVlr = jdbc.queryForObject(queryMargem, queryParams, BigDecimal.class);
                            matricula = matriculaAtual;
                            margemRest = (BigDecimal) row.get("MARGEM_REST");
                            continuarTestandoMatricula = true;
                            // margem restante mais o valor relativo a todos os candidatos a relançamento
                            if (sumAdeVlr != null) {
                                margemRest = margemRest.add(sumAdeVlr);
                            }
                        }

                        /*
                            * Quando um contrato da lista de candidatos a reimplante não cabe na margem
                            * o teste para a matrícula em questão deve parar para evitar que contratos
                            * de menor prioridade, mas que caberiam na margem, sejam enviados.
                            */
                        if (continuarTestandoMatricula) {
                            // Selecionar quais contratos podem ser enviados (margem_rest - ade_vlr >= 0) atualizando margem_rest local
                            if (!validaMargemExportacao ||
                                    ((margemRest.subtract(adeVlr).doubleValue() >= 0) &&
                                            (margem.doubleValue() > 0 || margemRestOrig.doubleValue() > 0))) {
                                adeCodigos.add(adeCodigo);
                                margemRest = margemRest.subtract(adeVlr);
                            } else if (margemRest.compareTo(BigDecimal.ZERO) >= 0) {
                                // apenas se houver margem restante positiva que tentará enviar parcial.
                                // contratos compulsórios não devem ser lançados parcialmente, porém deverão ser subtraídos da margem restante.
                                if (TextHelper.isNull(nseCodigo) || !nseCodigo.equals(CodedValues.NSE_COMPULSORIO)) {
                                    BigDecimal vlrParcialCandidato = null;

                                    vlrParcialCandidato = lancamentoParcial(adeCodigos, margemRest, adeVlr, adeVlrFolha, adeCodigo, autPgParcial, responsavel);

                                    if (vlrParcialCandidato == null) {
                                        continuarTestandoMatricula = false;
                                    } else {
                                        margemRest = margemRest.subtract(vlrParcialCandidato);
                                    }
                                } else {
                                    margemRest = margemRest.subtract(adeVlr); // subraíndo valor integral de contrato compulsório da margem restante.
                                }
                            } else {
                                continuarTestandoMatricula = false;
                            }
                        }
                    }

                    LOG.debug("Quantidade de contratos reimplantados = " + adeCodigos.size());
                    if (adeCodigos.size() > 0) {
                        // 3. marcar estes contratos com enviar='S'
                        query = "update tb_tmp_exp_relancamentos set enviar = 'S' where ade_codigo in (:adeCodigos)";
                        queryParams.addValue("adeCodigos", adeCodigos);
                        jdbc.update(query, queryParams);
                    }
                }

                // Marca contratos do período para envio, caso seja determinado.
                if (enviaContratosPeriodo) {
                    query = "update tb_tmp_exp_relancamentos tmp "
                            + "inner join tb_aut_desconto ade on (tmp.ade_codigo = ade.ade_codigo) "
                            + "inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) "
                            + "inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) "
                            + "inner join tb_periodo_exportacao pex on (cnv.org_codigo = pex.org_codigo) "
                            + "inner join tb_ocorrencia_autorizacao oca on (ade.ade_codigo = oca.ade_codigo) "
                            + "set tmp.enviar = 'S' "
                            + "where coalesce(tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_ini) = '" + periodo + "' "
                            + "and oca.oca_data between pex.pex_data_ini and pex.pex_data_fim "
                            + "and oca.toc_codigo = '" + CodedValues.TOC_TARIF_RESERVA + "'";
                    LOG.debug(query);
                    jdbc.update(query, queryParams);
                }

                /*
                    * 4. atualizar contratos enviar='S'
                    * Atualiza as referências dos contratos que serão relançados.
                    * OBS: Atualiza apenas dos contratos sad_codigo in ('12','16'), pois
                    * os sad_codigo = '5' são contratos que tiveram alteração rejeitada pela folha
                    * e os sad_codigo = '4' são contratos do período cujo servidor não possui margem,
                    * logo não devem ter as referências alteradas.
                    */
                // Não preserva parcela.
                // prazo = ano_mes_fim - periodo + 1
                query = "update tb_aut_desconto ade "
                        + "inner join tb_tmp_exp_relancamentos tmp on (tmp.ade_codigo = ade.ade_codigo) "
                        + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                        + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                        + "left outer join tb_param_svc_consignataria psc1 on (cnv.svc_codigo = psc1.svc_codigo and cnv.csa_codigo = psc1.csa_codigo and psc1.tps_codigo = '36' and (psc1.psc_ativo = '1' or psc1.psc_ativo is null)) "
                        + "set ade.ade_ano_mes_ini_ref = ifnull(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini), "
                        + "ade.ade_ano_mes_fim_ref = ifnull(ade.ade_ano_mes_fim_ref, ade.ade_ano_mes_fim), "
                        + "ade.sad_codigo = '4', "
                        + "ade.ade_ano_mes_ini = '" + periodo + "', "
                        + "ade.ade_prd_pagas = 0, "
                        + "ade.ade_prazo = if(ade.ade_prazo is not null, PERIOD_DIFF(concat(year(tmp.ade_ano_mes_fim), right(concat('00', month(tmp.ade_ano_mes_fim)), 2)), concat(year('" + periodo + "'),  right(concat('00', month('" + periodo + "')), 2))) + 1, null) "
                        + "where "
                        + "ifnull(psc1.psc_vlr, :defaultPreservacao) = 'N' and tmp.sad_codigo in ('12','16') and tmp.enviar = 'S'";
                LOG.debug(query);
                jdbc.update(query, queryParams);

                // Preserva parcela.
                // ano_mes_fim = periodo + prazo - 1
                query = "update tb_aut_desconto ade "
                        + "inner join tb_tmp_exp_relancamentos tmp on (tmp.ade_codigo = ade.ade_codigo) "
                        + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                        + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                        + "left outer join tb_param_svc_consignataria psc1 on (cnv.svc_codigo = psc1.svc_codigo and cnv.csa_codigo = psc1.csa_codigo and psc1.tps_codigo = '36' and (psc1.psc_ativo = '1' or psc1.psc_ativo is null)) "
                        + "set ade.ade_ano_mes_ini_ref = ifnull(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini), "
                        + "ade.ade_ano_mes_fim_ref = ifnull(ade.ade_ano_mes_fim_ref, ade.ade_ano_mes_fim), "
                        + "ade.sad_codigo = '4', "
                        + "ade.ade_ano_mes_ini = '" + periodo + "', "
                        + "ade.ade_prd_pagas = 0, "
                        + "ade.ade_prazo = if(ade.ade_prazo is not null, ade.ade_prazo - ifnull(ade.ade_prd_pagas, 0), null), "
                        + "ade.ade_ano_mes_fim = if(ade.ade_ano_mes_fim is not null, date_add('" + periodo + "', INTERVAL (ade.ade_prazo - ifnull(ade.ade_prd_pagas, 0) - 1) MONTH), null) "
                        + "where "
                        + "ifnull(psc1.psc_vlr, :defaultPreservacao) = 'S' and tmp.sad_codigo in ('12','16') and tmp.enviar = 'S'";
                LOG.debug(query);
                jdbc.update(query, queryParams);

                /*
                    * Insere a ocorrência de reimplantação automática para os contratos
                    * que serão enviados. A ocorrência e inserida para todos os contratos.
                    */
                query = "INSERT INTO tb_ocorrencia_autorizacao (OCA_CODIGO, TOC_CODIGO, ADE_CODIGO, USU_CODIGO, OCA_DATA, OCA_PERIODO, OCA_OBS) "
                        + "SELECT DISTINCT CONCAT(LPAD(ade.ade_numero, 10, '0'), '-10-', DATE_FORMAT(NOW(), '%Y%m%d%H%i%S')), '10', ade.ade_codigo, '1', '" + periodoDataFim + "', '" + periodo + "', 'REIMPLANTAÇÃO AUTOMÁTICA' "
                        + "from tb_aut_desconto ade "
                        + "inner join tb_tmp_exp_relancamentos tmp on (tmp.ade_codigo = ade.ade_codigo) "
                        + "where tmp.enviar = 'S' and tem_oca = 'N'";
                LOG.debug(query);
                jdbc.update(query, queryParams);

                // remove ocorrencia do que tinha sido relançado mas não será mais relançado este mês.
                query = "DELETE FROM tb_ocorrencia_autorizacao "
                        + "USING tb_ocorrencia_autorizacao "
                        + "inner join tb_tmp_exp_relancamentos tmp on (tmp.ade_codigo = tb_ocorrencia_autorizacao.ade_codigo) "
                        + "inner join tb_aut_desconto ade on (ade.ade_codigo = tb_ocorrencia_autorizacao.ade_codigo) "
                        + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                        + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                        + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
                        + "where tmp.enviar = 'N' and tem_oca = 'S' "
                        + "and tb_ocorrencia_autorizacao.toc_codigo = '10' and tb_ocorrencia_autorizacao.usu_codigo = '1' and oca_data > pex_data_ini ";
                LOG.debug(query);
                jdbc.update(query, queryParams);

                // REMOVE OCORRENCIA DE ESTOQUE INSERIDAS NO moveParaEstoqueMargemNegativa() E POSTERIORMENTE SELECIONADOS PARA RELANCAMENTO
                query = "DELETE FROM tb_ocorrencia_autorizacao "
                        + "USING tb_ocorrencia_autorizacao "
                        + "INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo=tb_ocorrencia_autorizacao.ade_codigo) "
                        + "INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) "
                        + "INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) "
                        + "INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = cnv.org_codigo) "
                        + "INNER JOIN tb_tmp_estoque_margem_neg emn ON (ade.ade_codigo=emn.ade_codigo) "
                        + "INNER JOIN tb_tmp_exp_relancamentos rel ON (emn.ade_codigo=rel.ade_codigo) "
                        + "WHERE tb_ocorrencia_autorizacao.toc_codigo = '1' AND tb_ocorrencia_autorizacao.usu_codigo = '1' "
                        + "AND tb_ocorrencia_autorizacao.oca_data > pex_data_ini AND rel.enviar = 'S' "
                        + "AND tb_ocorrencia_autorizacao.oca_codigo LIKE CONCAT(LPAD(ade.ade_numero,10,'0'),'-1-%') " // Padrão de ocorrência usado no método moveParaEstoqueMargemNegativa()
                        + "AND ade.sad_codigo = '" + CodedValues.SAD_DEFERIDA + "' ";
                LOG.debug(query);
                jdbc.update(query, queryParams);
            }

        } catch (final DataAccessException | PeriodoException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
    	super.processaTabelaExportacao(parametrosExportacao, responsavel);

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
                * Desconto parcial em duas linhas quando o prazo não é indeterminado e não é um novo contrato: exclusao e inclusão.
                */
            query.setLength(0);
            query.append("drop temporary table if exists tb_tmp_exportacao_alt_det ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);
            query.append("create temporary table tb_tmp_exportacao_alt_det ");
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
            rows = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + rows);

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
            rows = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + rows);

            // Remove da tabela de exportação contratos de servidores bloqueados
            LOG.debug("Aeronautica.removerContratosServidoresBloqueados: " + DateHelper.getSystemDatetime());
            removerContratosServidoresBloqueados();
            LOG.debug("fim - Aeronautica.removerContratosServidoresBloqueados: " + DateHelper.getSystemDatetime());

            // atualiza o registro de parcela do período para o valor parcial para os contratos selecionados para tal.
            if (lancamentosParciais != null && !lancamentosParciais.isEmpty()) {

                try {
                    for (Map.Entry<String, BigDecimal> lancamentoParcial: lancamentosParciais.entrySet()) {
                        String queryAtualizaValor = "update tb_tmp_exportacao set ade_vlr = " + lancamentoParcial.getValue() + " where ade_codigo = '" + lancamentoParcial.getKey() + "'";

                        int linhasAfetadas = 0;

                        LOG.error(query);
                        linhasAfetadas = jdbc.update(queryAtualizaValor, queryParams);
                        LOG.trace("Linhas afetadas: " + linhasAfetadas);
                    }

                } catch (final DataAccessException ex) {
                    LOG.error(ex.getMessage(), ex);
                    throw new ExportaMovimentoException(ex);
                }
            }
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

	@SuppressWarnings("java:S3358")
	private BigDecimal lancamentoParcial(List<String> adeCodigos, BigDecimal margemRest, BigDecimal adeVlr, BigDecimal adeVlrFolha, String adeCodigo, String autPgParcial, AcessoSistema responsavel) throws DataAccessException {
		BigDecimal parcialCandidato = null;

		// se o valor absoluto da margem for maior do que a parcela em questão, não será possível fazer lançamento parcial desta.
		if (!TextHelper.isNull(autPgParcial) && autPgParcial.equals(CodedValues.TPA_SIM) && margemRest.abs().compareTo(adeVlr) <= 0) {

		    // se o adeVlrFolha está zerado, então o contrato não está na folha. manda a margem disponível
		    // ou se o adeVlrFolha for menor que a margem disponível, uso a margem disponível
		    // se o valor parcial calculado for igual ao do último retorno, não deve registrar uma nova inclusão/alteração,
		    // pois o movimento inicial já irá registrar este valor na folha.
		    if (margemRest.signum() > 0 && margemRest.compareTo(adeVlrFolha) != 0) {
		        parcialCandidato = margemRest;
		    } else {
				return null;
			}
			
			registrarValorParcialParaContrato(adeCodigo, adeCodigos, parcialCandidato);
		}

		return parcialCandidato;
	}

	private void registrarValorParcialParaContrato(String adeCodigo, List<String> adeCodigos, BigDecimal parcialCandidato) throws DataAccessException {
		if (parcialCandidato.compareTo(BigDecimal.valueOf(0.0d)) > 0) {
			if (lancamentosParciais == null) {
				lancamentosParciais = new HashMap<>();
			}

			// inclui como contrato selecionado para envio.
			adeCodigos.add(adeCodigo);

			// guarda valor parcial para o contrato para posterir atualização da parcela desconto periodo. Valor de face é retornado no método pos.
			lancamentosParciais.put(adeCodigo, parcialCandidato);
			LOG.info("Contrato adeCodigo " + adeCodigo + " lancado com valor parcial: " + parcialCandidato);
		}
	}

    @Override
    public void preGeraArqLote(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();

        try {
            int rows = 0;

            // Atualiza o valor de desconto com o valor original do contrato
            query.append("UPDATE tb_tmp_exportacao_ordenada tmp ");
            query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_numero = tmp.ade_numero) ");
            query.append("SET tmp.capital_devido = ade.ade_vlr ");

            LOG.debug(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + rows);
            query.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
