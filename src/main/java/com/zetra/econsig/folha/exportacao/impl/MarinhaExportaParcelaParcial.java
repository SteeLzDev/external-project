package com.zetra.econsig.folha.exportacao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

@SuppressWarnings({"java:S1192", "java:S1948"})
public class MarinhaExportaParcelaParcial extends Marinha2 {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MarinhaExportaParcelaParcial.class);

    private Map<String, BigDecimal> lancamentosParciais;

    @Override
    protected void selectCandidatasRelancamento(List<String> orgCodigos, List<String> estCodigos, List<String> verbas, String periodo, AcessoSistema responsavel) throws DataAccessException, ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final String defaultReimplante = (String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_DEFAULT_PARAM_SVC_REIMPLANTE, CodedValues.TPC_NAO, responsavel);
        final String defaultPreservacao = (String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_DEFAULT_PARAM_SVC_PRESERVA_PRD, CodedValues.TPC_NAO, responsavel);

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

        String query = "delete from tb_tmp_exp_relancamentos";
        LOG.debug(query);
        jdbc.update(query, queryParams);
        /*
         * As ADEs candidatas a relançamento são:
         * - Aquelas que estão em estoque (12)
         * - Aquelas que estão deferidas ou em andamento (4, 5) cujo ade_vlr_folha is null, pois
         * contratos não pagos por motivo STATUS = Bloqueado/Suspenso (ade_vlr_folha not null)
         * não devem ser relançados. Seleciona apenas contratos com ade_ano_mes_ini < pex_periodo,
         * para não buscar ADEs novas.
         * - Aquelas que estão em andamento (5) cujo ade_vlr_folha <> ade_valor e que
         * não têm ocorrência de alteração, ou seja, alterações rejeitadas pela folha ou
         * alterações que não cabiam na margem.
         *
         * OBS: O campo pode_reimplantar é preenchido de acordo com a seguinte regra:
         * Se o contrato foi totalmente pago, não reimplanta. Caso contrário verifica se
         * o convênio tem reimplante automático, ou houve uma alteração no período atual,
         * ou houve uma inclusão no período atual. Os dois últimos casos asseguram que
         * uma alteração ou um novo contrato que foram diretamente para estoque
         * sejam exportados, se couberem na margem. Contratos que já estejam abertos e
         * que tenham ade_ano_mes_fim anterior ao periodo atual não devem ser reimplantados.
         * Lembrete: Alterações do período atual que não cabiam na margem
         * já foram canceladas (toc_codigo = '14' passado para toc_codigo = '3').
         *
         * OBS 2: Precisamos selecionar tmb os contratos que não podem ser reimplantados,
         * pois eles tmb decrementam a margem disponível e os valores desses contratos
         * devem ser considerados no cálculo de quais contratos podem ser reimplantados.
         */
        query = "insert into tb_tmp_exp_relancamentos (ade_codigo, sad_codigo, ade_vlr, rse_matricula, ade_inc_margem, rse_margem, margem_rest, enviar, svc_prioridade, cnv_prioridade, nse_codigo, ade_data, ade_ano_mes_ini, ade_ano_mes_fim, ade_numero, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, tem_oca, pode_reimplantar, reimplante_alteracao, aut_pg_parcial, ade_prd_pagas, ade_prazo, ade_vlr_folha, prioridade_compulsorio) "
                + "select distinct ade.ade_codigo, sad_codigo, ade_vlr - ifnull(ade_vlr_folha, 0) as ade_vlr, rse.rse_matricula, "
                + "ade_inc_margem, case "
                + " when ade_inc_margem = '1' then rse.rse_margem "
                + " when ade_inc_margem = '2' then rse.rse_margem_2 "
                + " when ade_inc_margem = '3' then rse.rse_margem_3 "
                + "end as RSE_MARGEM, case "
                + " when ade_inc_margem = '1' then if(tmp.rse_matricula is null, rse.rse_margem_rest, tmp.rse_margem + saldo) "
                + " when ade_inc_margem = '2' then if(tmp.rse_matricula is null, rse.rse_margem_rest_2, tmp.rse_margem_2 + saldo_2) "
                + " when ade_inc_margem = '3' then if(tmp.rse_matricula is null, rse.rse_margem_rest_3, tmp.rse_margem_3 + saldo_3) "
                + "end as MARGEM_REST, "
                +  "'N' as enviar, "
                + "coalesce(if(svc.nse_codigo = '"+ CodedValues.NSE_COMPULSORIO + "', 1, svc.svc_prioridade), 999999) + 0 as svc_prioridade, coalesce(cnv_prioridade, 999999) as cnv_prioridade, "
                + "svc.nse_codigo, "
                + "ade_data, ade_ano_mes_ini, ade_ano_mes_fim, ade_numero, ade_ano_mes_ini_ref, ade_ano_mes_fim_ref, "
                + "if(oca10.oca_codigo is null, 'N', 'S') AS tem_oca, "
                + "if(ifnull(psc.psc_vlr, :defaultReimplante) = 'S' or oca14.oca_codigo is not null or oca4.oca_codigo is not null, 'S', 'N') as pode_reimplantar, "
                + "if(ade.sad_codigo = '5' and ade.ade_vlr_folha <> ade.ade_vlr and oca14.oca_codigo is null, 'S', 'N') as reimplante_alteracao, "
                + "pcs32.pcs_vlr as aut_pg_parcial, ade.ade_prd_pagas, ade.ade_prazo, "
                + "coalesce(ade.ade_vlr_folha, 0) as ade_vlr_folha, "
                + "if(svc.nse_codigo = '" + CodedValues.NSE_COMPULSORIO + "', 1, 999999) as prioridade_compulsorio "  //DESENV-18504: fixa prioridade de compulsórios como máxima na seleção de candidatos
                + "from tb_aut_desconto ade "
                + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                + "inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo) "
                + "inner join tb_periodo_exportacao pex on (pex.org_codigo = cnv.org_codigo) "
                + "inner join tb_orgao org on (org.org_codigo = cnv.org_codigo) "
                + "inner join tb_registro_servidor rse on (rse.rse_codigo = ade.rse_codigo) "
                + "inner join tb_consignataria csa on (csa.csa_codigo = cnv.csa_codigo) "
                + (enviaContratosSerBloqueados ? ""
                        : "left outer join tb_param_convenio_registro_ser pcr on (rse.rse_codigo = pcr.rse_codigo and cnv.cnv_codigo = pcr.cnv_codigo and pcr.pcr_vlr = '0' and pcr.tps_codigo = '" + CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO + "') "
                        + "left outer join tb_param_servico_registro_ser psr on (rse.rse_codigo = psr.rse_codigo and cnv.svc_codigo = psr.svc_codigo and psr.psr_vlr = '0' and psr.tps_codigo = '" + CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO + "') "
                        + "left outer join tb_param_nse_registro_ser pnr on (rse.rse_codigo = pnr.rse_codigo and svc.nse_codigo = pnr.nse_codigo and pnr.pnr_vlr = '0' and pnr.tps_codigo = '" + CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO + "') "
                        )
                + "left outer join tb_param_svc_consignataria psc on (cnv.svc_codigo = psc.svc_codigo and cnv.csa_codigo = psc.csa_codigo and psc.tps_codigo = '35' and (psc.psc_ativo = '1' or psc.psc_ativo is null)) "
                + "left outer join tb_param_svc_consignataria psc1 on (cnv.svc_codigo = psc1.svc_codigo and cnv.csa_codigo = psc1.csa_codigo and psc1.tps_codigo = '36' and (psc1.psc_ativo = '1' or psc1.psc_ativo is null)) "
                + "left outer join tb_param_consignataria pcs32 on (csa.csa_codigo = pcs32.csa_codigo and pcs32.tpa_codigo = '32') "
                + "left outer join tb_ocorrencia_autorizacao oca10 on (ade.ade_codigo = oca10.ade_codigo and oca10.toc_codigo = '10' and oca10.oca_data between pex.pex_data_ini and pex.pex_data_fim) "
                + "left outer join tb_ocorrencia_autorizacao oca14 on (ade.ade_codigo = oca14.ade_codigo and oca14.toc_codigo = '14' and oca14.oca_data between pex.pex_data_ini and pex.pex_data_fim) "
                + "left outer join tb_ocorrencia_autorizacao oca4  on (ade.ade_codigo = oca4.ade_codigo  and oca4.toc_codigo = '4'   and oca4.oca_data  between pex.pex_data_ini and pex.pex_data_fim) "
                + "left outer join tb_tmp_exp_saldo_periodo tmp on (rse.rse_codigo = tmp.rse_codigo) "
                + "where "
                + "((sad_codigo in ('" + CodedValues.SAD_ESTOQUE + "', '" + CodedValues.SAD_ESTOQUE_MENSAL + "')) or "
                + " (sad_codigo in ('" + CodedValues.SAD_DEFERIDA + "', '" + CodedValues.SAD_EMANDAMENTO + "') and ade_vlr_folha is null and ade.ade_ano_mes_ini < pex.pex_periodo) or "
                + " (sad_codigo = '" + CodedValues.SAD_EMANDAMENTO + "' and ade_vlr_folha is not null and ade_vlr_folha <> ade_vlr and oca14.oca_codigo is null)) "
                + "and ade.ade_ano_mes_ini <= pex.pex_periodo "
                + "and ade_int_folha = '" + CodedValues.INTEGRA_FOLHA_SIM + "' "
                + "and srs_codigo = '" + CodedValues.SRS_ATIVO + "' "
                // não seleciona contratos que já deveriam ter terminado.
                // Se não preserva parcela, basta testar a data final do contrato.
                + "and ((ifnull(psc1.psc_vlr, :defaultPreservacao) = 'N' and (ade.ade_ano_mes_fim >= '" + periodo + "' or ade_ano_mes_fim is null)) or ifnull(psc1.psc_vlr, :defaultPreservacao) = 'S') "
                + complemento
                + (enviaContratosSerBloqueados ? ""
                        : " and pcr.pcr_vlr is null "
                        + " and psr.psr_vlr is null "
                        + " and pnr.pnr_vlr is null "
                        )
                ;
        LOG.debug(query);
        jdbc.update(query, queryParams);
        encerrarAdePreservacaoFinal(periodo, defaultPreservacao, responsavel);
    }

    @Override
    public void posCriacaoTabelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        super.posCriacaoTabelas(parametrosExportacao, responsavel);

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            StringBuilder query = new StringBuilder();

            query.append("alter table tb_tmp_exp_relancamentos ");
            query.append(" add (ade_vlr_folha decimal(13,2), aut_pg_parcial char(1), ade_prd_pagas smallint(6), ade_prazo smallint(6), prioridade_compulsorio smallint(6)) ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    @SuppressWarnings("java:S3776")
    protected void defineContratosRelancamentoPorMargem(String adeIncMargen, AcessoSistema responsavel) throws DataAccessException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try  {
            // Lista de ADEs candidatas a reimplantação
            final String query = "select * from tb_tmp_exp_relancamentos "
                               + "where pode_reimplantar = 'S' "
                               + "and enviar = 'N' "
                               + "and ade_inc_margem = '" + adeIncMargen + "' "
                               + "order by rse_matricula, prioridade_compulsorio, svc_prioridade, cnv_prioridade, ade_ano_mes_ini_ref, ade_numero";
            LOG.debug(query);
            final List<Map<String, Object>> resultSet = jdbc.queryForList(query, queryParams);

            String matriculaAtual;
            String matricula = "";
            String queryMargem;
            String nseCodigo = null;
            boolean continuarTestandoMatricula = true;
            BigDecimal adeVlr;
            BigDecimal adeVlrFolha;
            String adeCodigo = null;
            String autPgParcial = null;
            BigDecimal margemRest = new BigDecimal("0");
            BigDecimal margemRestOrig = new BigDecimal("0");
            BigDecimal margem = new BigDecimal("0");
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
                    // Seleciona todos os candidatos a relancamento, pois todos eles
                    // estão comprometendo a margem, logo devem ser considerados
                    // no cálculo.
                    queryMargem = "select sum(ade_vlr) as total from tb_tmp_exp_relancamentos where rse_matricula = :matriculaAtual and ade_inc_margem = '" + adeIncMargen + "'";
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

                // Quando um contrato da lista de candidatos a reimplante não cabe na margem
                // o teste para a matrícula em questão deve parar para evitar que contratos
                // de menor prioridade, mas que caberiam na margem, sejam enviados.
                // DESENV-17888: se a consignatária permitir pagamento parcial, tenta encaixar um valor parcial
                // que caiba na margem restante.

                if (continuarTestandoMatricula) {
                    // Selecionar quais contratos podem ser enviados (margem_rest - ade_vlr >= 0) atualizando margem_rest local
                    if (margemRest.subtract(adeVlr).doubleValue() >= 0 &&
                            (margem.doubleValue() > 0 || margemRestOrig.doubleValue() > 0)) {
                        adeCodigos.add(adeCodigo);
                        margemRest = margemRest.subtract(adeVlr);

                    } else if (margemRest.compareTo(BigDecimal.ZERO) >= 0) { // apenas se houver margem restante positiva que tentará enviar parcial.
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

                LOG.info("CONTRATO CANDIDATO FIM DOS CONTRATOS DA MATRICULA " + matricula);
            }

            atualizarAdesSelecionadasParaEnvio(adeCodigos);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private void atualizarAdesSelecionadasParaEnvio(List<String> adeCodigos) throws DataAccessException {
        LOG.debug("Quantidade de contratos reimplantados = " + adeCodigos.size());
        if (!adeCodigos.isEmpty()) {
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();

            // 3. marcar estes contratos com enviar = 'S'
            String query = "update tb_tmp_exp_relancamentos set enviar = 'S' where ade_codigo in (:adeCodigos)";
            queryParams.addValue("adeCodigos", adeCodigos);
            jdbc.update(query, queryParams);
        }
    }

    @SuppressWarnings("java:S3358")
    private BigDecimal lancamentoParcial(List<String> adeCodigos, BigDecimal margemRest, BigDecimal adeVlr, BigDecimal adeVlrFolha, String adeCodigo, String autPgParcial, AcessoSistema responsavel) throws DataAccessException {
        BigDecimal parcialCandidato = null;

        // se o valor absoluto da margem for maior do que a parcela em questão, não será possível fazer lançamento parcial desta.
        if (!TextHelper.isNull(autPgParcial) && autPgParcial.equals(CodedValues.TPA_SIM) && margemRest.abs().compareTo(adeVlr) <= 0) {

            parcialCandidato = margemRest.compareTo(BigDecimal.ZERO) >= 0 ? margemRest : adeVlr.add(margemRest);

            // os valores calculados são relativos à tabela de candidatos a relançamento. Para contratos que estão sendo alterados (adeVlrFolha != 0)
            // é levado em conta nesta tabela apenas a diferença de valor em relação ao adeVlr antigo, que é o que a folha de movimento inicial desconhece.
            // logo, o lançamento parcial final deve somar o adeVlrFolha ao parcialCandidato recém calculado. Para novos contratos o adeVlrFolha será sempre 0.
            parcialCandidato = adeVlrFolha.add(parcialCandidato);

            // se o valor parcial calculado for igual ao do último retorno, não deve registrar uma nova inclusão/alteração,
            // pois o movimento inicial já irá registrar este valor na folha.
            if (parcialCandidato.compareTo(adeVlrFolha) == 0) {
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
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        super.processaTabelaExportacao(parametrosExportacao, responsavel);

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        // atualiza o registro de parcela do período para o valor parcial para os contratos selecionados para tal.
        if (lancamentosParciais != null && !lancamentosParciais.isEmpty()) {
            try {
                for (Map.Entry<String, BigDecimal> lancamentoParcial: lancamentosParciais.entrySet()) {
                    String query = "update tb_tmp_exportacao set ade_vlr = " + lancamentoParcial.getValue() + " where ade_codigo = '" + lancamentoParcial.getKey() + "'";

                    LOG.error(query);
                    int linhasAfetadas = jdbc.update(query, queryParams);
                    LOG.trace("Linhas afetadas: " + linhasAfetadas);
                }

            } catch (final DataAccessException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ExportaMovimentoException(ex);
            }
        }
    }
}