package com.zetra.econsig.persistence.query.consignacao;

import java.util.Collection;
import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: ListaConsignacaoRenegociavelQuery</p>
 * <p>Description: Listagem de Consignações que podem ser compradas ou renegociadas.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoRenegociavelNativeQuery extends HNativeQuery {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListaConsignacaoRenegociavelNativeQuery.class);

    public Collection<String> adeCodigos;
    public String tipoOperacao;
    public String csaCodigo;
    public String svcCodigo;
    public AcessoSistema responsavel;
    public boolean ignoraParamRestTaxaMenor = false;
    public boolean fixaServico = false;
    public boolean adeSuspensas = false;

    /**
     * Adiciona clausulas para verificação dos parâmetros de renegociação,
     * da quantidade mínima (percentual ou absoluta) necessária de parcelas pagas e de meses de vigência:
     * 1) AND adePrdPagas >= TPS_MINIMO_PRD_PAGAS_RENEGOCIACAO
     * 2) AND adePrdPagas >= (adePrazo * TPS_PERCENTUAL_MINIMO_PRD_PAGAS_RENEG)
     * 3) AND period_diff(adeAnoMesIni, periodoAnterior) >= TPS_MINIMO_VIGENCIA_RENEG
     * 4) AND period_diff(adeAnoMesIni, periodoAnterior) >= coalesce(dadValor, adePrazo * TPS_PERCENTUAL_MINIMO_VIGENCIA_RENEG)
     * OBS: é verificado a sobreposição pelo parâmetro de consignatária/serviço.
     * @param builder
     */
    private void adicionaClausulaRenegociacao(StringBuilder builder) {
        // Define qual parâmetro deve ser verificado, já que cláusulas de restrição de renegociação são validadas na compra dos contratos da própria CSA
        final String tpcCodigo = "comprar".equalsIgnoreCase(tipoOperacao) || "solicitar_portabilidade".equalsIgnoreCase(tipoOperacao) ? CodedValues.TPC_PERMITE_COMPRA_SEM_RESTRICAO_TAXA_MENOR : CodedValues.TPC_PERMITE_RENEG_SEM_RESTRICAO_TAXA_MENOR;
        if (!ParamSist.paramEquals(tpcCodigo, CodedValues.TPC_SIM, responsavel) || ignoraParamRestTaxaMenor) {
            builder.append(" AND ((");
            builder.append(" EXISTS(SELECT relAut.ade_codigo_destino FROM tb_relacionamento_autorizacao relAut where ade.ade_codigo = relAut.ade_codigo_destino AND relAut.tnt_codigo = '").append(CodedValues.TNT_CONTROLE_COMPRA).append("')");
            builder.append(" AND ");
            builder.append(" EXISTS(SELECT pse248.pse_vlr FROM tb_param_svc_consignante pse248 where svc.svc_codigo = pse248.svc_codigo AND pse248.tps_codigo = '").append(CodedValues.TPS_VALIDAR_REGRA_RENEGOCIACAO_CONTRATO_FRUTO_PORTABILIDADE).append("' AND pse248.pse_vlr = '1') ");
            builder.append(" ) OR (");
            builder.append(" (");
            builder.append("coalesce(coalesce(ade.ade_prd_pagas_total, ade.ade_prd_pagas), 0) ");
            builder.append(">= (SELECT coalesce(MAX(CASE isnumeric_ne(pse155.pse_vlr) WHEN 1 THEN to_numeric_ne(pse155.pse_vlr) ELSE 1000 END), 0)");
            builder.append(" FROM tb_param_svc_consignante pse155 where svc.svc_codigo = pse155.svc_codigo AND pse155.tps_codigo = '").append(CodedValues.TPS_MINIMO_PRD_PAGAS_RENEGOCIACAO).append("')");
            builder.append(" OR ");
            builder.append("coalesce(coalesce(ade.ade_prd_pagas_total, ade.ade_prd_pagas), 0) ");
            builder.append(">= (SELECT coalesce(MAX(CASE isnumeric_ne(psc155.psc_vlr) WHEN 1 THEN to_numeric_ne(psc155.psc_vlr) ELSE 1000 END), 1000)");
            builder.append(" FROM tb_param_svc_consignataria psc155 where svc.svc_codigo = psc155.svc_codigo AND psc155.tps_codigo = '").append(CodedValues.TPS_MINIMO_PRD_PAGAS_RENEGOCIACAO).append("'");
            builder.append(" AND psc155.csa_codigo = :csaCodigo)");
            builder.append(")");
            builder.append(" ))");

            builder.append(" AND (");
            builder.append("coalesce(coalesce(ade.ade_prd_pagas_total, ade.ade_prd_pagas), 0) ");
            builder.append(">= (SELECT coalesce(ade.ade_prazo * 0.01 * MAX(CASE isnumeric_ne(pse170.pse_vlr) WHEN 1 THEN to_decimal_ne(pse170.pse_vlr, 5, 2) ELSE 1000 END), 0)");
            builder.append(" FROM tb_param_svc_consignante pse170 where svc.svc_codigo = pse170.svc_codigo AND pse170.tps_codigo = '").append(CodedValues.TPS_PERCENTUAL_MINIMO_PRD_PAGAS_RENEG).append("')");
            builder.append(" OR ");
            builder.append("coalesce(coalesce(ade.ade_prd_pagas_total, ade.ade_prd_pagas), 0) ");
            builder.append(">= (SELECT coalesce(ade.ade_prazo * 0.01 * MAX(CASE isnumeric_ne(psc170.psc_vlr) WHEN 1 THEN to_decimal_ne(psc170.psc_vlr, 5, 2) ELSE 1000 END), 1000)");
            builder.append(" FROM tb_param_svc_consignataria psc170 where svc.svc_codigo = psc170.svc_codigo AND psc170.tps_codigo = '").append(CodedValues.TPS_PERCENTUAL_MINIMO_PRD_PAGAS_RENEG).append("'");
            builder.append(" AND psc170.csa_codigo = :csaCodigo)");
            builder.append(")");

            builder.append(" AND (");
            builder.append("month_diff(coalesce(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini), :periodo) ");
            builder.append(">= (SELECT coalesce(MAX(CASE isnumeric_ne(pse181.pse_vlr) WHEN 1 THEN to_numeric_ne(pse181.pse_vlr) ELSE 1000 END), -1000)");
            builder.append(" FROM tb_param_svc_consignante pse181 where svc.svc_codigo = pse181.svc_codigo AND pse181.tps_codigo = '").append(CodedValues.TPS_MINIMO_VIGENCIA_RENEG).append("')");
            builder.append(" OR ");
            builder.append("month_diff(coalesce(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini), :periodo) ");
            builder.append(">= (SELECT coalesce(MAX(CASE isnumeric_ne(psc181.psc_vlr) WHEN 1 THEN to_numeric_ne(psc181.psc_vlr) ELSE 1000 END), 1000)");
            builder.append(" FROM tb_param_svc_consignataria psc181 where svc.svc_codigo = psc181.svc_codigo AND psc181.tps_codigo = '").append(CodedValues.TPS_MINIMO_VIGENCIA_RENEG).append("'");
            builder.append(" AND psc181.csa_codigo = :csaCodigo)");
            builder.append(")");

            builder.append(" AND (");
            builder.append("month_diff(coalesce(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini), :periodo) ");
            builder.append(">= (SELECT coalesce((case when ade.ade_periodicidade = '").append(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL).append("' then (ade.ade_prazo / 2.0) else ade.ade_prazo end) * 0.01");
            builder.append(" * coalesce(to_decimal_ne(dad12.dad_valor, 5, 2), coalesce(MAX(CASE isnumeric_ne(pse173.pse_vlr) WHEN 1 THEN to_decimal_ne(pse173.pse_vlr, 5, 2) ELSE 1000 END), -1000)), -1000)");
            builder.append(" FROM tb_param_svc_consignante pse173 where svc.svc_codigo = pse173.svc_codigo AND pse173.tps_codigo = '").append(CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_RENEG).append("')");
            builder.append(" OR ");
            builder.append("month_diff(coalesce(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini), :periodo) ");
            builder.append(">= (SELECT coalesce((case when ade.ade_periodicidade = '").append(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL).append("' then (ade.ade_prazo / 2.0) else ade.ade_prazo end) * 0.01");
            builder.append(" * coalesce(to_decimal_ne(dad12.dad_valor, 5, 2), coalesce(MAX(CASE isnumeric_ne(psc173.psc_vlr) WHEN 1 THEN to_decimal_ne(psc173.psc_vlr, 5, 2) ELSE 1000 END), 1000)), 1000)");
            builder.append(" FROM tb_param_svc_consignataria psc173 where svc.svc_codigo = psc173.svc_codigo AND psc173.tps_codigo = '").append(CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_RENEG).append("'");
            builder.append(" AND psc173.csa_codigo = :csaCodigo)");
            builder.append(")");
        }
    }

    /**
     * Adiciona clausulas para verificação dos parâmetros de compra,
     * da quantidade mínima (percentual ou absoluta) necessária de parcelas pagas e de meses de vigência:
     * 1) AND adePrdPagas >= TPS_MINIMO_PRD_PAGAS_COMPRA
     * 2) AND adePrdPagas >= (adePrazo * TPS_PERCENTUAL_MINIMO_PRD_PAGAS_COMPRA)
     * 3) AND period_diff(adeAnoMesIni, periodoAnterior) >= TPS_MINIMO_VIGENCIA_COMPRA
     * 4) AND period_diff(adeAnoMesIni, periodoAnterior) >= coalesce(dadValor, adePrazo * TPS_PERCENTUAL_MINIMO_VIGENCIA_COMPRA)
     * OBS: é verificado a sobreposição pelo parâmetro de consignatária/serviço.
     * @param builder
     */
    private void adicionaClausulaCompra(StringBuilder builder) {
        if (!ParamSist.paramEquals(CodedValues.TPC_PERMITE_COMPRA_SEM_RESTRICAO_TAXA_MENOR, CodedValues.TPC_SIM, responsavel) || ignoraParamRestTaxaMenor) {
            builder.append(" AND (");
            builder.append("coalesce(coalesce(ade.ade_prd_pagas_total, ade.ade_prd_pagas), 0) ");
            builder.append(">= (SELECT coalesce(MAX(CASE isnumeric_ne(pse177.pse_vlr) WHEN 1 THEN to_numeric_ne(pse177.pse_vlr) ELSE 1000 END), 0)");
            builder.append(" FROM tb_param_svc_consignante pse177 where svc.svc_codigo = pse177.svc_codigo AND pse177.tps_codigo = '").append(CodedValues.TPS_MINIMO_PRD_PAGAS_COMPRA).append("')");
            if (!TextHelper.isNull(csaCodigo)) {
                builder.append(" OR ");
                builder.append("coalesce(coalesce(ade.ade_prd_pagas_total, ade.ade_prd_pagas), 0) ");
                builder.append(">= (SELECT coalesce(MAX(CASE isnumeric_ne(psc177.psc_vlr) WHEN 1 THEN to_numeric_ne(psc177.psc_vlr) ELSE 1000 END), 1000)");
                builder.append(" FROM tb_param_svc_consignataria psc177 where svc.svc_codigo = psc177.svc_codigo AND psc177.tps_codigo = '").append(CodedValues.TPS_MINIMO_PRD_PAGAS_COMPRA).append("'");
                builder.append(" AND psc177.csa_codigo = :csaCodigo)");
            }
            builder.append(")");

            builder.append(" AND (");
            builder.append("coalesce(coalesce(ade.ade_prd_pagas_total, ade.ade_prd_pagas), 0) ");
            builder.append(">= (SELECT coalesce(ade.ade_prazo * 0.01 * MAX(CASE isnumeric_ne(pse178.pse_vlr) WHEN 1 THEN to_decimal_ne(pse178.pse_vlr, 5, 2) ELSE 1000 END), 0)");
            builder.append(" FROM tb_param_svc_consignante pse178 where svc.svc_codigo = pse178.svc_codigo AND pse178.tps_codigo = '").append(CodedValues.TPS_PERCENTUAL_MINIMO_PRD_PAGAS_COMPRA).append("')");
            if (!TextHelper.isNull(csaCodigo)) {
                builder.append(" OR ");
                builder.append("coalesce(coalesce(ade.ade_prd_pagas_total, ade.ade_prd_pagas), 0) ");
                builder.append(">= (SELECT coalesce(ade.ade_prazo * 0.01 * MAX(CASE isnumeric_ne(psc178.psc_vlr) WHEN 1 THEN to_decimal_ne(psc178.psc_vlr, 5, 2) ELSE 1000 END), 1000)");
                builder.append(" FROM tb_param_svc_consignataria psc178 where svc.svc_codigo = psc178.svc_codigo AND psc178.tps_codigo = '").append(CodedValues.TPS_PERCENTUAL_MINIMO_PRD_PAGAS_COMPRA).append("'");
                builder.append(" AND psc178.csa_codigo = :csaCodigo)");
            }
            builder.append(")");

            builder.append(" AND (");
            builder.append("month_diff(coalesce(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini), :periodo) ");
            builder.append(">= (SELECT coalesce(MAX(CASE isnumeric_ne(pse179.pse_vlr) WHEN 1 THEN to_numeric_ne(pse179.pse_vlr) ELSE 1000 END), -1000)");
            builder.append(" FROM tb_param_svc_consignante pse179 where svc.svc_codigo = pse179.svc_codigo AND pse179.tps_codigo = '").append(CodedValues.TPS_MINIMO_VIGENCIA_COMPRA).append("')");
            if (!TextHelper.isNull(csaCodigo)) {
                builder.append(" OR ");
                builder.append("month_diff(coalesce(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini), :periodo) ");
                builder.append(">= (SELECT coalesce(MAX(CASE isnumeric_ne(psc179.psc_vlr) WHEN 1 THEN to_numeric_ne(psc179.psc_vlr) ELSE 1000 END), 1000)");
                builder.append(" FROM tb_param_svc_consignataria psc179 where svc.svc_codigo = psc179.svc_codigo AND psc179.tps_codigo = '").append(CodedValues.TPS_MINIMO_VIGENCIA_COMPRA).append("'");
                builder.append(" AND psc179.csa_codigo = :csaCodigo)");
            }
            builder.append(")");

            builder.append(" AND (");
            builder.append("month_diff(coalesce(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini), :periodo) ");
            builder.append(">= (SELECT coalesce((case when ade.ade_periodicidade = '").append(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL).append("' then (ade.ade_prazo / 2.0) else ade.ade_prazo end) * 0.01");
            builder.append(" * coalesce(to_decimal_ne(dad13.dad_valor, 5, 2), coalesce(MAX(CASE isnumeric_ne(pse180.pse_vlr) WHEN 1 THEN to_decimal_ne(pse180.pse_vlr, 5, 2) ELSE 1000 END), -1000)), -1000)");
            builder.append(" FROM tb_param_svc_consignante pse180 where svc.svc_codigo = pse180.svc_codigo AND pse180.tps_codigo = '").append(CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_COMPRA).append("')");
            if (!TextHelper.isNull(csaCodigo)) {
                builder.append(" OR ");
                builder.append("month_diff(coalesce(ade.ade_ano_mes_ini_ref, ade.ade_ano_mes_ini), :periodo) ");
                builder.append(">= (SELECT coalesce((case when ade.ade_periodicidade = '").append(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL).append("' then (ade.ade_prazo / 2.0) else ade.ade_prazo end) * 0.01");
                builder.append(" * coalesce(to_decimal_ne(dad13.dad_valor, 5, 2), coalesce(MAX(CASE isnumeric_ne(psc180.psc_vlr) WHEN 1 THEN to_decimal_ne(psc180.psc_vlr, 5, 2) ELSE 1000 END), 1000)), 1000)");
                builder.append(" FROM tb_param_svc_consignataria psc180 where svc.svc_codigo = psc180.svc_codigo AND psc180.tps_codigo = '").append(CodedValues.TPS_PERCENTUAL_MINIMO_VIGENCIA_COMPRA).append("'");
                builder.append(" AND psc180.csa_codigo = :csaCodigo)");
            }
            builder.append(")");
        }
    }

    /**
     * Adiciona clausulas para verificação se o servidor possui consignação com a consignatária
     * em serviço relacionado ao serviço da operação na natureza TNT_CONTRATO_PREEXISTENTE_LIBERA_COMPRA
     * ou que a consignatária não possua convênio com nenhum dos serviços deste relacionamento
     * @param builder
     */
    protected void adicionaClausulaServidorPossuiAde(StringBuilder builder) {
        builder.append(" AND (");
        // Tem consignação Deferida ou Em Andamento com a Consignatária (de acordo com o relacionamento para liberação de compra)
        builder.append(" EXISTS (select 1 ");
        builder.append("from tb_aut_desconto ade2 ");
        builder.append("inner join tb_verba_convenio vco2 on ade2.vco_codigo = vco2.vco_codigo ");
        builder.append("inner join tb_convenio cnv2 on vco2.cnv_codigo = cnv2.cnv_codigo ");
        builder.append("inner join tb_servico svc2 on cnv2.svc_codigo = svc2.svc_codigo ");
        builder.append("inner join tb_relacionamento_servico rsv2 on rsv2.svc_codigo_destino = svc2.svc_codigo and  ( ");
        builder.append("rsv2.tnt_codigo = '").append(CodedValues.TNT_CONTRATO_PREEXISTENTE_LIBERA_COMPRA).append("' AND ");
        builder.append("rsv2.svc_codigo_origem = :svcCodigo");
        if (fixaServico) {
            builder.append(" AND rsv2.svc_codigo_destino = :svcCodigo");
        }
        builder.append(") ");
        builder.append("where ade2.rse_codigo = ade.rse_codigo ");
        builder.append("and cnv2.csa_codigo = :csaCodigo ");
        builder.append("and ade2.sad_codigo in ('").append(CodedValues.SAD_DEFERIDA).append("','").append(CodedValues.SAD_EMANDAMENTO).append("') ");
        builder.append(")");
        // Ou não possui convênio ativo com os serviços necessários para liberação da compra
        builder.append(" OR NOT EXISTS (select 1 ");
        builder.append("from tb_convenio cnv3 ");
        builder.append("inner join tb_servico svc3 on cnv3.svc_codigo = svc3.svc_codigo ");
        builder.append("inner join tb_relacionamento_servico rsv3 on rsv3.svc_codigo_destino = svc3.svc_codigo and  ( ");
        builder.append("rsv3.tnt_codigo = '").append(CodedValues.TNT_CONTRATO_PREEXISTENTE_LIBERA_COMPRA).append("' AND ");
        builder.append("rsv3.svc_codigo_origem = :svcCodigo");
        if (fixaServico) {
             builder.append(" AND rsv3.svc_codigo_destino = :svcCodigo");
        }
        builder.append(") ");
        builder.append("where cnv3.csa_codigo = :csaCodigo ");
        builder.append("and cnv3.scv_codigo = '").append(CodedValues.SCV_ATIVO).append("' ");
        builder.append(")");

        builder.append(")");
    }

    /**
     * Adiciona cláusula para verificação se a consignação não possui solicitação de saldo devedor para liquidação
     * @param builder
     */
    protected void adicionaClausulaAdePossuiSolicitacaoSaldoLiq(StringBuilder builder) {
        builder.append(" AND NOT EXISTS (select 1 ");
        builder.append("from tb_solicitacao_autorizacao soa where ade.ade_codigo = soa.ade_codigo ");
        builder.append("and soa.tis_codigo = '").append(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO.getCodigo()).append("' ");
        builder.append("and soa.sso_codigo IN ('").append(StatusSolicitacaoEnum.PENDENTE.getCodigo()).append("', '").append(StatusSolicitacaoEnum.FINALIZADA.getCodigo()).append("') ");
        builder.append("and (select count(*) from tb_calendario cal where cal.cal_dia_util = 'S' and cal.cal_data between to_date(soa.soa_data) and current_date()) <= 14");
        builder.append(")");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        tipoOperacao = (tipoOperacao == null) ? "" : tipoOperacao;

        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select distinct ade.ade_codigo ");
        corpoBuilder.append("from tb_aut_desconto ade ");
        corpoBuilder.append("inner join tb_verba_convenio vco on ade.vco_codigo = vco.vco_codigo ");
        corpoBuilder.append("inner join tb_convenio cnv on vco.cnv_codigo = cnv.cnv_codigo ");
        corpoBuilder.append("inner join tb_servico svc on cnv.svc_codigo = svc.svc_codigo ");

        if ("comprar".equalsIgnoreCase(tipoOperacao)) {
            corpoBuilder.append(" inner join tb_relacionamento_servico rsv on rsv.svc_codigo_destino = svc.svc_codigo and  ");
            corpoBuilder.append(" rsv.tnt_codigo = '").append(CodedValues.TNT_COMPRA).append("' AND ");
            corpoBuilder.append(" rsv.svc_codigo_origem = :svcCodigo");

        } else if ("renegociar".equalsIgnoreCase(tipoOperacao)) {
            corpoBuilder.append(" inner join tb_relacionamento_servico rsv on rsv.svc_codigo_destino = svc.svc_codigo and  ");
            corpoBuilder.append(" rsv.tnt_codigo = '").append(CodedValues.TNT_RENEGOCIACAO).append("' AND ");
            corpoBuilder.append(" rsv.svc_codigo_origem = :svcCodigo");
            if (fixaServico) {
                 corpoBuilder.append(" AND rsv.svc_codigo_destino = :svcCodigo");
            }

        } else if ("alongar".equalsIgnoreCase(tipoOperacao)) {
            corpoBuilder.append(" inner join tb_relacionamento_servico rsv on rsv.svc_codigo_destino = svc.svc_codigo and  ");
            corpoBuilder.append(" rsv.tnt_codigo = '").append(CodedValues.TNT_ALONGAMENTO).append("' AND ");
            corpoBuilder.append(" rsv.svc_codigo_origem = :svcCodigo");
        }

        if ("comprar".equalsIgnoreCase(tipoOperacao) || "solicitar_portabilidade".equalsIgnoreCase(tipoOperacao) || "renegociar".equalsIgnoreCase(tipoOperacao)) {
            corpoBuilder.append(" left outer join tb_dados_autorizacao_desconto dad12 on ade.ade_codigo = dad12.ade_codigo and dad12.tda_codigo = '").append(CodedValues.TDA_PERCENTUAL_MINIMO_VIGENCIA_RENEG).append("'");
            corpoBuilder.append(" left outer join tb_dados_autorizacao_desconto dad13 on ade.ade_codigo = dad13.ade_codigo and dad13.tda_codigo = '").append(CodedValues.TDA_PERCENTUAL_MINIMO_VIGENCIA_COMPRA).append("'");
        }

        corpoBuilder.append(" WHERE ade.ade_codigo ").append(criaClausulaNomeada("adeCodigo", adeCodigos));

        if ("solicitar_portabilidade".equalsIgnoreCase(tipoOperacao)) {
            corpoBuilder.append(" AND EXISTS (SELECT 1 FROM tb_relacionamento_servico rsv where rsv.svc_codigo_destino = svc.svc_codigo and ");
            corpoBuilder.append(" rsv.tnt_codigo = '").append(CodedValues.TNT_COMPRA).append("'");
            corpoBuilder.append(") ");
        }

        // Se é compra de contrato, verifica os parâmetros de sistema relativos a compra:
        // 1) Limites de quantidade de parcelas pagas que um contrato deve possuir para fazer parte das negociações de compra
        // 2) Limite de quantidade de meses de vigência um contrato deve possuir para fazer parte das negociações de compra
        // 3) Bloqueio de compra de contratos da própria consignatária
        if ("comprar".equalsIgnoreCase(tipoOperacao) || "solicitar_portabilidade".equalsIgnoreCase(tipoOperacao)) {
            // Adiciona cláusula quanto a limitação de pagas e vigência
            corpoBuilder.append(" AND (1=1 ");
            adicionaClausulaCompra(corpoBuilder);

            if (!TextHelper.isNull(csaCodigo)) {
                // Se é usuário de CSA/COR lista também os contratos da Consignatária,
                // mesmo que a vigência do contrato seja menor que a requerida porém
                // observando o número de parcelas pagas requerido para renegociação de contratos
                corpoBuilder.append(" AND cnv.csa_codigo <> :csaCodigo");
                corpoBuilder.append(" OR (cnv.csa_codigo = :csaCodigo");
                adicionaClausulaRenegociacao(corpoBuilder);
                corpoBuilder.append(")");
            }

            corpoBuilder.append(")");

            // Se bloqueia a compra de contratos da própria consignatária, limita a contratos que não são da consignatária
            final boolean bloqueiaCompraProprioContrato = ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_COMPRA_PROPRIO_CONTRATO, CodedValues.TPC_SIM, responsavel);
            if (bloqueiaCompraProprioContrato && !TextHelper.isNull(csaCodigo)) {
                corpoBuilder.append(" AND cnv.csa_codigo <> :csaCodigo");
            }

            // Parâmetro de Sistema 333 : Bloqueia compra de contratos pelo quantidade no parametro antes de acabar a tarefa EX: TPC = 4, não será permitido comprar contrato que falte 4 parcelas para o fim.
            final String numParcelasBloqueiaCompraParcela = (String) ParamSist.getInstance().getParam(CodedValues.TPC_BLOQUEIA_COMPRA_ULTIMA_PARCELA, responsavel);
            if (!TextHelper.isNull(numParcelasBloqueiaCompraParcela) && !"0".equals(numParcelasBloqueiaCompraParcela)) {
                corpoBuilder.append(" AND coalesce(ade.ade_prazo, 1000) - (coalesce(ade.ade_prd_pagas, 0) + (select count(*) from tb_parcela_desconto_periodo pdp where pdp.ade_codigo = ade.ade_codigo AND pdp.spd_codigo = '").append(CodedValues.SPD_EMPROCESSAMENTO).append("' )) > ").append(numParcelasBloqueiaCompraParcela);
            }

            // Se bloqueia a compra de contratos com data final menor que o período atual de lançamentos
            final boolean bloqueiaCompraDataFimPassada = ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_COMPRA_DATA_FINAL_PASSADA, CodedValues.TPC_SIM, responsavel);
            if (bloqueiaCompraDataFimPassada) {
                corpoBuilder.append(" AND (ade.ade_ano_mes_fim IS NULL OR ade.ade_ano_mes_fim >= :periodo)");
            }

            // Verifica se a compra de contratos só pode ocorrer se o servidor possui contrato com a consignatária compradora,
            // se sim, então verifica através do relacionamento de serviço TNT_CONTRATO_PREEXISTENTE_LIBERA_COMPRA contratos
            // deferidos ou em andamento que o servidor possui com a consignatária.
            final boolean bloqueiaCompraServidorNovo = ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_COMPRA_SERVIDOR_NOVO, CodedValues.TPC_SIM, responsavel);
            if (bloqueiaCompraServidorNovo && !TextHelper.isNull(csaCodigo)) {
                adicionaClausulaServidorPossuiAde(corpoBuilder);
            }

            // Se bloqueia a compra de contratos que possuem ocorrência de solicitação de saldo para liquidação nos últimos X dias (atualmente fixo como 14)
            final boolean bloqueiaCompraSolictSaldoLiq = ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_COMPRA_COM_SOLICI_SALDO_LIQUID, CodedValues.TPC_SIM, responsavel);
            if (bloqueiaCompraSolictSaldoLiq) {
                adicionaClausulaAdePossuiSolicitacaoSaldoLiq(corpoBuilder);
            }

        } else if ("renegociar".equalsIgnoreCase(tipoOperacao)) {
            adicionaClausulaRenegociacao(corpoBuilder);
            if (adeSuspensas) {
                corpoBuilder.append(" AND");
                corpoBuilder.append(" EXISTS");
                corpoBuilder.append(" (SELECT 1 FROM tb_ocorrencia_autorizacao ocaSuspensaoPrdRejeitada where ade.ade_codigo = ocaSuspensaoPrdRejeitada.ade_codigo");
                corpoBuilder.append(" AND ocaSuspensaoPrdRejeitada.toc_codigo = '").append(CodedValues.TOC_SUSPENSAO_CONTRATO_PARCELA_REJEITADA).append("'");
                corpoBuilder.append(" AND NOT EXISTS (select 1 from tb_ocorrencia_autorizacao ocaReativacaoPrdRejeitada where ade.ade_codigo = ocaReativacaoPrdRejeitada.ade_codigo");
                corpoBuilder.append(" AND ocaReativacaoPrdRejeitada.toc_codigo = '" + CodedValues.TOC_REATIVACAO_CONTRATO_PARCELA_REJEITADA + "'");
                corpoBuilder.append(" AND ocaReativacaoPrdRejeitada.oca_data > ocaSuspensaoPrdRejeitada.oca_data) ");
                corpoBuilder.append(")");
            }
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (corpoBuilder.toString().contains(":adeCodigo")) {
            defineValorClausulaNomeada("adeCodigo", adeCodigos, query);
        }
        if (corpoBuilder.toString().contains(":csaCodigo")) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (corpoBuilder.toString().contains(":svcCodigo")) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }
        if (corpoBuilder.toString().contains(":periodo")) {
            Date periodo = null;
            try {
                periodo = PeriodoHelper.getInstance().getPeriodoAtual(null, responsavel);
                defineValorClausulaNomeada("periodo", periodo, query);
            } catch (final PeriodoException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new HQueryException(ex);
            }
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] { Columns.ADE_CODIGO };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setCriterios'");
    }
}
