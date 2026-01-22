package com.zetra.econsig.folha.exportacao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.delegate.PeriodoDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: Aeronautica</p>
 * <p>Description: Implementações específicas para a Aeronautica.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Aeronautica extends ExportaMovimentoBase {
    private static final long serialVersionUID = 4664501808233223056L;

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Aeronautica.class);

    // Determina se a validação de margem pelo saldo do período deve ser executado
    private final boolean validaMargemExportacao;

    // Determina se os contratos do período deve ser enviados mesmo que o servidor não tenha margem.
    private final boolean enviaContratosPeriodo;

    public Aeronautica() {
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
            final StringBuilder query = new StringBuilder();
            query.append("drop table if exists tb_tmp_estoque_margem_neg");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create table tb_tmp_estoque_margem_neg (");
            query.append("ade_codigo varchar(32), ");
            query.append("ade_numero bigint(20), ");
            query.append("ade_vlr decimal(13,2), ");
            query.append("ade_vlr_folha decimal(13,2), ");
            query.append("ade_inc_margem smallint(6), ");
            query.append("sad_codigo varchar(32), ");
            query.append("rse_matricula varchar(20), ");
            query.append("pex_periodo_pos date, ");
            query.append("rse_margem decimal(13,2), ");
            query.append("margem_rest decimal(13,2), ");
            query.append("key ix_ade (ade_codigo)");
            query.append(")");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("drop table if exists tb_tmp_exp_relancamentos");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create table tb_tmp_exp_relancamentos (");
            query.append("ade_codigo varchar(32), ");
            query.append("sad_codigo varchar(32), ");
            query.append("ade_vlr decimal(13,2), ");
            query.append("rse_matricula varchar(20), ");
            query.append("ade_inc_margem smallint(6), ");
            query.append("rse_margem decimal(13,2), ");
            query.append("margem_rest decimal(13,2), ");
            query.append("enviar char(1), ");
            query.append("svc_prioridade smallint(6), ");
            query.append("cnv_prioridade smallint(6), ");
            query.append("ade_data datetime, ");
            query.append("ade_ano_mes_ini date, ");
            query.append("ade_ano_mes_fim date, ");
            query.append("ade_numero bigint(20), ");
            query.append("ade_ano_mes_ini_ref date, ");
            query.append("ade_ano_mes_fim_ref date, ");
            query.append("tem_oca char(1), ");
            query.append("pode_reimplantar char(1)");
            query.append(")");
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
        String acao = parametrosExportacao.getAcao();

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final String defaultPreservacao = (String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_DEFAULT_PARAM_SVC_PRESERVA_PRD, CodedValues.TPC_NAO, responsavel);
        queryParams.addValue("defaultPreservacao", defaultPreservacao);

        try {
            String query = null;

            // Obtém o período atual
            PeriodoDelegate perDelegate = new PeriodoDelegate();
            TransferObject periodoExportacao = perDelegate.obtemPeriodoExportacaoDistinto(orgCodigos, estCodigos, responsavel);
            String periodo = DateHelper.format((java.util.Date) periodoExportacao.getAttribute(Columns.PEX_PERIODO), "yyyy-MM-dd");
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
                String[] adeIncMargens = new String[]{"1", "3"};
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
                    List<String> adeCodigos = new ArrayList<>();

                    for (Map<String, Object> row : resultSet) {
                        matriculaAtual = row.get("RSE_MATRICULA").toString();
                        margem = (BigDecimal) row.get("RSE_MARGEM");
                        margemRestOrig = (BigDecimal) row.get("MARGEM_REST");
                        adeVlr = (BigDecimal) row.get("ADE_VLR");

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
                                adeCodigos.add(row.get("ADE_CODIGO").toString());
                                margemRest = margemRest.subtract(adeVlr);
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
                        + "ade_prazo = if(ade.ade_prazo is not null, PERIOD_DIFF(concat(year(tmp.ade_ano_mes_fim), right(concat('00', month(tmp.ade_ano_mes_fim)), 2)), concat(year('" + periodo + "'),  right(concat('00', month('" + periodo + "')), 2))) + 1, null) "
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
                        + "SELECT DISTINCT CONCAT(LPAD(ade.ade_numero, 10, '0'), '-10-', DATE_FORMAT(NOW(), '%Y%m%d%H%i%S')), '10', ade.ade_codigo, '1', '" + periodo + "', '" + periodo + "', 'REIMPLANTAÇÃO AUTOMÁTICA' "
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

    protected void alteraOcorrencias(String periodoAnterior) throws DataAccessException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("periodoAnterior", periodoAnterior);
        String query = null;

        boolean exportaLiqCancNaoPagas = ParamSist.paramEquals(CodedValues.TPC_EXPORTA_LIQCANC_NAO_PAGAS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        if (!exportaLiqCancNaoPagas) {
	    	// ITEM 15 ALTERA AS OCORRENCIAS DE LIQUIDAÇÃO DE CONTRATO SEM PARCELAS PAGAS PARA OCORRENCIA DE CANCELAMENTO
	        query = "update tb_aut_desconto ade "
	              + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) "
	              + "inner join tb_parcela_desconto prd on (prd.ade_codigo = ade.ade_codigo) "
	              + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
	              + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
	              + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
	              + "set toc_codigo = '35', oca_obs = 'LIQUIDAÇÃO NÃO EXPORTADA POR SER SEM EFEITO' "
	              + "where spd_codigo = '5' "
	              + "and prd_data_desconto = :periodoAnterior " // -- último desconto
	              + "and oca_data between PEX_DATA_INI and PEX_DATA_FIM " // -- após o último corte e antes do corte atual
	              + "and toc_codigo = '6'  "
	              + "and ade_vlr_folha is null";
	        LOG.debug("ITEM 15: " + query);
	        jdbc.update(query, queryParams);

	        // ITEM 16 ALTERA AS OCORRENCIAS DE LIQUIDAÇÃO DOS CONTRATOS SEM RETORNO ADE_VLR_FOLHA NULO
	        query = "update tb_aut_desconto ade "
	              + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) "
	              + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
	              + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
	              + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
	              + "set toc_codigo = '35', oca_obs = 'LIQUIDAÇÃO NÃO EXPORTADA POR SER SEM EFEITO' "
	              + "where oca_data between PEX_DATA_INI and PEX_DATA_FIM " // -- após o último corte e antes do corte atual
	              + "and sad_codigo = '8' "
	              + "and toc_codigo = '6' "
	              + "and ade_vlr_folha is null ";
	        LOG.debug("ITEM 16: " + query);
	        jdbc.update(query, queryParams);
        }

        // ITEM 17 ALTERA OCORRENCIA DE ESTOQUES LIQUIDADOS PARA OCORRENCIA DE CANCELAMENTO: resolvido pelo ITEM 16

        // ITEM 18 ALTERA AS OCORRENCIAS DE ALTERAÇÃO DOS CONTRATOS SEM RETORNO
        query = "update tb_aut_desconto ade "
              + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) "
              + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
              + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
              + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
              + "left outer join tb_parcela_desconto prd on (prd.ade_codigo = ade.ade_codigo and PRD_DATA_DESCONTO = :periodoAnterior) "
              + "set toc_codigo = '3' " // -- Transforma em 3 (Informação) apenas pois o reimplante (se for o caso) irá tratar da reinclusão com o novo valor
              + "where "
              + "oca_data between PEX_DATA_INI and PEX_DATA_FIM " // -- após o último corte e antes do corte atual
              + "and sad_codigo = '5' "
              + "and toc_codigo = '14' "
              + "and (prd.prd_numero is null or spd_codigo = '5') ";
        LOG.debug("ITEM 18: " + query);
        jdbc.update(query, queryParams);

        // ITEM 19 ALTERA OCORRENCIA DE LIQUIDAÇÃO PARA OCORRENCIA DE CANCELAMENTO DE CONTRATOS LIQUIDADOS NO MÊS EM QUE SÃO CONCLUÍDOS
        query = "update tb_aut_desconto ade "
              + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) "
              + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
              + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
              + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
              + "set toc_codigo = '35', oca_obs = 'LIQUIDAÇÃO NÃO EXPORTADA POR SER SEM EFEITO' "
              + "where ade_inc_margem in ('1','2','3') "
              + "and toc_codigo = '6' "
              + "and oca_data between PEX_DATA_INI and PEX_DATA_FIM " // -- após o último corte e antes do corte atual
              + "and ifnull(nullif(ade_ano_mes_fim_folha, '0000-00-00'), ade_ano_mes_fim) = :periodoAnterior " // -- com final no mês anterior
              + "and sad_codigo = '8'";
        LOG.debug("ITEM 19: " + query);
        jdbc.update(query, queryParams);

        // ITEM 34 ALTERA AS OCORRENCIAS DE CANCELAMENTO DE CONTRATO COM ÚLTIMA PARCELA PAGA PARA OCORRENCIA DE LIQUIDAÇÃO
        query = "update tb_aut_desconto ade "
              + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) "
              + "inner join tb_parcela_desconto prd on (prd.ade_codigo = ade.ade_codigo) "
              + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
              + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
              + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
              + "set toc_codigo = '6', oca_obs = 'PTF: LIQUIDAÇÃO DE CONSIGNAÇÃO.', sad_codigo = '8' "
              + "where spd_codigo = '6' "
              + "and ade_inc_margem in ('1','2','3') "
              + "and prd_data_desconto = :periodoAnterior " // -- último desconto
              + "and oca_data between PEX_DATA_INI and PEX_DATA_FIM " // -- após o último corte e antes do corte atual
              + "and ifnull(nullif(ade_ano_mes_fim_folha, '0000-00-00'), ade_ano_mes_fim) > :periodoAnterior " // -- com final após o mês anterior
              + "and toc_codigo = '7' and  ade_vlr_folha is not null ";
        LOG.debug("ITEM 34: " + query);
        jdbc.update(query, queryParams);

        // ITEM 35 ALTERA OCORRENCIA DE LIQUIDAO PARA OCORRENCIA DE CANCELAMENTO DE CONTRATOS REJEITADOS NO PRIMEIRO MES, COM STATUS DE NAO PAGAMENTO
        query = "update tb_aut_desconto ade "
              + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
              + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
              + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
              + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) "
              + "inner join tb_parcela_desconto prd on (ade.ade_codigo = prd.ade_codigo and prd.prd_data_desconto = :periodoAnterior and prd.spd_codigo = '5') "
              + "inner join tb_ocorrencia_parcela ocp on (prd.prd_codigo = ocp.prd_codigo) "
              + "set oca.toc_codigo = '35', oca.oca_obs = 'LIQUIDAÇÃO NÃO EXPORTADA POR SER SEM EFEITO' "
              + "where ade.ade_int_folha = '1' "
              + "and ifnull(ade.ade_prd_pagas, 0) = 0 "
              + "and ade.sad_codigo = '8' "
              + "and oca.toc_codigo = '6' "
              + "and oca.oca_data between PEX_DATA_INI and PEX_DATA_FIM "
              + "and ocp.ocp_obs like 'Retorno: STATUS%' ";
        LOG.debug("ITEM 35: " + query);
        jdbc.update(query, queryParams);

    }

    // COLOCA EM ESTOQUE CONTRATOS CUJO SERVIDOR ESTAVA COM MARGEM RESTANTE NEGATIVA NO DIA DO CORTE
    public void moveParaEstoqueMargemNegativa() throws DataAccessException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        /*
         * Esta rotina move para estoque:
         * - Contratos realizados com a margem residual existente entre o dia de corte
         * e o retorno posterior, mas que segundo a margem recebida pelo retorno,
         * não podem mais ser enviados.
         * OBS: Contratos de serviços compulsórios (tps_codigo = '94') são enviados para
         * a folha mesmo com margem negativa, ou seja, <b>não</b> devem ser colocados em estoque.
         */

        String query = "DELETE FROM tb_tmp_estoque_margem_neg";
        LOG.debug(query.toString());
        jdbc.update(query, queryParams);

        query = "INSERT INTO tb_tmp_estoque_margem_neg (ade_codigo, ade_numero, ade_vlr, ade_vlr_folha, ade_inc_margem, sad_codigo, rse_matricula, pex_periodo_pos, rse_margem, margem_rest) "
              + "SELECT ade.ade_codigo, ade.ade_numero, ade.ade_vlr, ade.ade_vlr_folha, ade.ade_inc_margem, ade.sad_codigo, rse.rse_matricula, pex.pex_periodo_pos, "
              + "case "
              + " when ade_inc_margem = '1' then rse.rse_margem "
              + " when ade_inc_margem = '2' then rse.rse_margem_2 "
              + " when ade_inc_margem = '3' then rse.rse_margem_3 "
              + "end as RSE_MARGEM, "
              + "case "
              + " when ade_inc_margem = '1' then rse.rse_margem_rest "
              + " when ade_inc_margem = '2' then rse.rse_margem_rest_2 "
              + " when ade_inc_margem = '3' then rse.rse_margem_rest_3 "
              + "end as MARGEM_REST "
              + "FROM tb_aut_desconto ade "
              + "INNER JOIN tb_registro_servidor rse ON (rse.rse_codigo = ade.rse_codigo) "
              + "INNER JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) "
              + "INNER JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) "
              + "INNER JOIN tb_servico svc ON (cnv.svc_codigo = svc.svc_codigo) "
              + "INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = cnv.org_codigo) "
              + "LEFT OUTER JOIN tb_param_svc_consignante pse ON (cnv.svc_codigo = pse.svc_codigo AND pse.tps_codigo = '94') "
              + "WHERE ade.sad_codigo = '4' "
              + "and ade_int_folha = '1' "
              + "and ade_inc_margem in ('1', '2', '3') "
              + "AND ade.ade_ano_mes_ini <= pex.pex_periodo "
              + "AND (pse.pse_vlr is null OR pse.pse_vlr = '0') "
              + "AND (case when ade.ade_inc_margem = '1' then rse.rse_margem_rest "
              + "          when ade.ade_inc_margem = '2' then rse.rse_margem_rest_2 "
              + "          when ade.ade_inc_margem = '3' then rse.rse_margem_rest_3 "
              + "     end) < 0 ";
        LOG.debug(query.toString());
        jdbc.update(query, queryParams);

        // Insere ocorrencia de aviso para as ADE que serão colocados em estoque
        query = "INSERT INTO tb_ocorrencia_autorizacao (OCA_CODIGO, TOC_CODIGO, ADE_CODIGO, USU_CODIGO, OCA_DATA, OCA_PERIODO, OCA_OBS) "
              + "SELECT CONCAT(LPAD(ade.ade_numero, 10, '0'), '-1-', DATE_FORMAT(NOW(), '%Y%m%d%H%i%S')), '1', ade.ade_codigo, '1', NOW(), pex_periodo_pos, CONCAT('SITUAÇÃO ALTERADA DE ', ade.sad_codigo, ' PARA 16') "
              + "FROM tb_tmp_estoque_margem_neg ade ";
        LOG.debug(query);
        jdbc.update(query, queryParams);

        // Altera a situação das ADE para ESTOQUE MENSAL
        query = "UPDATE tb_aut_desconto ade "
              + "INNER JOIN tb_tmp_estoque_margem_neg tmp ON (ade.ade_codigo = tmp.ade_codigo) "
              + "SET ade.sad_codigo = '16' ";
        LOG.debug(query);
        jdbc.update(query, queryParams);
    }

    // CANCELA ALTERAÇÕES DE CONTRATOS COM MARGEM RESTANTE NEGATIVA NO DIA DO CORTE
    protected void cancelaAlteracaoMargemNegativa() throws DataAccessException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        String query = "update tb_ocorrencia_autorizacao oca "
                     + "inner join tb_aut_desconto ade on (ade.ade_codigo = oca.ade_codigo) "
                     + "inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo) "
                     + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                     + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                     + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
                     + "left outer join tb_param_svc_consignante pse94 on (cnv.svc_codigo = pse94.svc_codigo and pse94.tps_codigo = '94') "
                     + "set oca.toc_codigo = '3' " // Transforma alteração '14' em informação '3'
                     + "where (pse94.pse_vlr is null or pse94.pse_vlr = '0') " // somente serviços que não sejam compulsórios
                     + "and oca.toc_codigo = '14' "
                     + "and sad_codigo = '5' "
                     + "and ade_int_folha = '1' "
                     + "and ade_inc_margem in ('1', '2', '3') "
                     + "and ade.ade_vlr_folha < ade.ade_vlr " // Somente alterações que aumentam o valor
                     + "and oca.oca_data between pex.pex_data_ini and pex.pex_data_fim "
                     + "and (case when ade.ade_inc_margem = '1' then rse.rse_margem_rest "
                     + "          when ade.ade_inc_margem = '2' then rse.rse_margem_rest_2 "
                     + "          when ade.ade_inc_margem = '3' then rse.rse_margem_rest_3 "
                     + "     end) < 0 ";
        LOG.debug("ALTERAÇÃO CANCELADA: " + query);
        jdbc.update(query, queryParams);
    }

    protected void selectCandidatasRelancamento(List<String> orgCodigos, List<String> estCodigos,
                                              List<String> verbas, String periodo) throws DataAccessException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

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
         * As ADEs candidatas a relançamento são:
         * - Aquelas que estão em estoque (12)
         * - Aquelas que estão em estoque mensal (16) cujo ade_vlr_folha is null, pois
         * contratos que vão para estoque por motivo STATUS = N, P, T (ade_vlr_folha not null)
         * não devem ser relançados.
         * - Aquelas que estão em andamento (5) cujo ade_vlr_folha < ade_valor e que
         * não têm ocorrência de alteração, ou seja, alterações rejeitadas pela folha ou
         * alterações que não cabiam na margem.
         * OBS: O campo pode_reimplantar é preenchido de acordo com a seguinte regra:
         * O convênio tem reimplante automático, ou houve uma alteração no período atual,
         * ou houve uma inclusão no período atual. Os dois últimos casos asseguram que
         * uma alteração ou um novo contrato que foram diretamente para estoque
         * sejam exportados, se couberem na margem.
         * Lembrete: Alterações do período atual que não cabiam na margem já foram
         * canceladas (toc_codigo = '14' passado para toc_codigo = '3').
         * OBS 2: Precisamos selecionar tmb os contratos que não podem ser reimplantados,
         * pois eles tmb decrementam a margem disponível e os valores desses contratos
         * devem ser considerados no cálculo de quais contratos podem ser reimplantados.
         */
        query = "INSERT INTO tb_tmp_exp_relancamentos (ade_codigo, sad_codigo, ade_vlr, rse_matricula, ade_inc_margem, rse_margem, margem_rest, enviar, svc_prioridade, cnv_prioridade, ade_data, ade_ano_mes_ini, ade_ano_mes_fim, ade_numero, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, tem_oca, pode_reimplantar) "
              + "select distinct ade.ade_codigo, sad_codigo, ade_vlr - ifnull(ade_vlr_folha, 0) as ade_vlr, rse.rse_matricula, "
              + "ade_inc_margem, case "
              + " when ade_inc_margem = '1' then rse.rse_margem "
              + " when ade_inc_margem = '2' then rse.rse_margem_2 "
              + " when ade_inc_margem = '3' then rse.rse_margem_3 "
              + "end as RSE_MARGEM, case "
              + " when ade_inc_margem = '1' then rse.rse_margem_rest "
              + " when ade_inc_margem = '2' then rse.rse_margem_rest_2 "
              + " when ade_inc_margem = '3' then rse.rse_margem_rest_3 "
              + "end as MARGEM_REST, "
              + "'N' as enviar, "
              + "IFNULL(svc_prioridade, '99999') + 0 as svc_prioridade, IFNULL(cnv_prioridade, 99999) as cnv_prioridade, "
              + "ade_data, ade_ano_mes_ini, ade_ano_mes_fim, ade_numero, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, "
              + "IF(oca10.oca_codigo is null, 'N', 'S') AS tem_oca, "
              + "if(ifnull(psc.psc_vlr, :defaultReimplante) = 'S' or oca14.oca_codigo is not null or oca4.oca_codigo is not null, 'S', 'N') AS pode_reimplantar "
              + "from tb_aut_desconto ade "
              + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
              + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
              + "inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo) "
              + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
              + "inner join tb_orgao org on (org.org_codigo = cnv.org_codigo) "
              + "inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo) "
              + "inner join tb_consignataria csa on (csa.csa_codigo = cnv.csa_codigo) "
              + "left outer join tb_param_convenio_registro_ser pcr on (rse.rse_codigo = pcr.rse_codigo and cnv.cnv_codigo = pcr.cnv_codigo and pcr.pcr_vlr = '0' and pcr.tps_codigo = '" + CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO + "') "
              + "left outer join tb_param_servico_registro_ser psr on (rse.rse_codigo = psr.rse_codigo and cnv.svc_codigo = psr.svc_codigo and psr.psr_vlr = '0' and psr.tps_codigo = '" + CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO + "') "
              + "left outer join tb_param_nse_registro_ser pnr on (rse.rse_codigo = pnr.rse_codigo and svc.nse_codigo = pnr.nse_codigo and pnr.pnr_vlr = '0' and pnr.tps_codigo = '" + CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO + "') "
              + "left outer join tb_param_svc_consignataria psc on (cnv.svc_codigo = psc.svc_codigo and cnv.csa_codigo = psc.csa_codigo and psc.tps_codigo = '35' and (psc.psc_ativo = '1' or psc.psc_ativo is null)) "
              + "left outer join tb_param_svc_consignataria psc1 on (cnv.svc_codigo = psc1.svc_codigo and cnv.csa_codigo = psc1.csa_codigo and psc1.tps_codigo = '36' and (psc1.psc_ativo = '1' or psc1.psc_ativo is null)) "
              + "left outer join tb_ocorrencia_autorizacao oca10 on (ade.ade_codigo = oca10.ade_codigo and oca10.toc_codigo = '10' and oca10.oca_data between pex.pex_data_ini and pex.pex_data_fim) "
              + "left outer join tb_ocorrencia_autorizacao oca14 on (ade.ade_codigo = oca14.ade_codigo and oca14.toc_codigo = '14' and oca14.oca_data between pex.pex_data_ini and pex.pex_data_fim) "
              + "left outer join tb_ocorrencia_autorizacao oca4  on (ade.ade_codigo = oca4.ade_codigo  and oca4.toc_codigo = '4'   and oca4.oca_data  between pex.pex_data_ini and pex.pex_data_fim) "
              + "where "
              + "((sad_codigo = '" + CodedValues.SAD_ESTOQUE + "') or "
              + " (sad_codigo = '" + CodedValues.SAD_ESTOQUE_MENSAL + "' and ade_vlr_folha is null) or "
              + " (sad_codigo in ('" + CodedValues.SAD_DEFERIDA + "', '" + CodedValues.SAD_EMANDAMENTO + "') and ade_vlr_folha is null and ade.ade_ano_mes_ini < pex.pex_periodo) or "
              + " (sad_codigo = '" + CodedValues.SAD_EMANDAMENTO + "' and ade_vlr_folha is not null and ade_vlr_folha < ade_vlr and oca14.oca_codigo is null)) "
              + "and ade.ade_ano_mes_ini <= pex.pex_periodo "
              + "and ade_int_folha = '1' "
              + "and ade_inc_margem in ('1', '2', '3') "
              + "and srs_codigo = '" + CodedValues.SRS_ATIVO + "' "
              + "and cnv.scv_codigo = '" + CodedValues.SCV_ATIVO + "' "
              // não seleciona contratos que já deveriam ter terminado.
              // Se não preserva parcela, basta testar a data final do contrato.
              + "and ((ifnull(psc1.psc_vlr, :defaultPreservacao) = 'N' and (ade.ade_ano_mes_fim >= '" + periodo + "' or ade_ano_mes_fim is null)) or ifnull(psc1.psc_vlr, :defaultPreservacao) = 'S') "
              + complemento
              + " and pcr.pcr_vlr is null "
              + " and psr.psr_vlr is null "
              + " and pnr.pnr_vlr is null "
              ;

        LOG.debug(query);
        jdbc.update(query, queryParams);
    }

    @Override
    public void posProcessaParcelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        List<String> orgCodigos = parametrosExportacao.getOrgCodigos();
        List<String> estCodigos = parametrosExportacao.getEstCodigos();

        try {
            StringBuilder query = new StringBuilder();

            // Obtém o período atual
            PeriodoDelegate perDelegate = new PeriodoDelegate();
            TransferObject periodoExportacao = perDelegate.obtemPeriodoExportacaoDistinto(orgCodigos, estCodigos, responsavel);
            String periodo = DateHelper.format((java.util.Date) periodoExportacao.getAttribute(Columns.PEX_PERIODO), "yyyy-MM-dd");
            LOG.debug("periodoAtual=" + periodo);

            query.append("update tb_aut_desconto ade ");
            query.append("inner join tb_parcela_desconto_periodo pdp on (pdp.ade_codigo = ade.ade_codigo) ");
            query.append("set spd_codigo = '").append(CodedValues.SPD_EMPROCESSAMENTO).append("' ");
            query.append("where ");
            query.append("prd_data_desconto = '").append(periodo).append("' ");
            query.append("and spd_codigo = '").append(CodedValues.SPD_EMABERTO).append("'");
            LOG.debug(query.toString());
            int rows = jdbc.update(query.toString(), queryParams);
            LOG.debug("Linhas afetadas: " + rows);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (PeriodoException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            StringBuilder query = new StringBuilder();

            /*
                * Alteração de valor ou prazo de ADE com prazo determinado e indeterminado (DESENV-17388 inclusão de linhas com prazo indetermidado) em duas linhas: exclusao e inclusão.
                * Só precisa fazer para aquelas ADE que não tiveram alteração de indíce pois estas já irão em duas linhas.
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
            jdbc.update(query.toString(), queryParams);
            query.setLength(0);
            query.append("update tb_tmp_exportacao tmp ");
            query.append("inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = tmp.ade_codigo) ");
            query.append("inner join tb_aut_desconto ade on (ade.ade_codigo = oca.ade_codigo) ");
            query.append("inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) ");
            query.append("inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) ");
            query.append("set tmp.sad_codigo = '4', tmp.ade_vlr_folha = null, tmp.ade_ano_mes_fim_folha = null ");
            query.append("where oca.toc_codigo in ('10', '14') and oca.oca_data between pex.pex_data_ini and pex.pex_data_fim ");
            query.append("and ade.ade_prd_pagas > 0 ");
            query.append("and (ade.ade_prazo is not null or (ade.ade_prazo is null and ade.ade_prazo_folha is not null)) and ade.sad_codigo = '5' and tmp.sad_codigo = '5' ");
            query.append("and (ade.ade_indice = ade.ade_indice_exp or ade.ade_indice_exp is null) ");
            query.append("and (ade.ade_vlr <> ade.ade_vlr_folha or ade.ade_ano_mes_fim <> ade.ade_ano_mes_fim_folha) ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

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

            // Remove da tabela de exportação contratos de servidores bloqueados
            LOG.debug("Aeronautica.removerContratosServidoresBloqueados: " + DateHelper.getSystemDatetime());
            removerContratosServidoresBloqueados();
            LOG.debug("fim - Aeronautica.removerContratosServidoresBloqueados: " + DateHelper.getSystemDatetime());

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
