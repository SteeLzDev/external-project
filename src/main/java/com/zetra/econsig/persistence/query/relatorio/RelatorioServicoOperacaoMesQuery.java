package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioServicoOperacaoMesQuery</p>
 * <p>Description: Consulta de Relatório Serviço de Operação no Mês</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioServicoOperacaoMesQuery extends ReportHNativeQuery{
    private String periodo;
    private String dataIni;
    private String dataFim;
    private Boolean operacaoPorCsa;
    public List<String> svcCodigo;
    public List<String> nseCodigo;

    @Override
    public void setCriterios(TransferObject criterio) {
        periodo = (String) criterio.getAttribute("PERIODO");
        dataIni = (String) criterio.getAttribute("DATA_INI");
        dataFim = (String) criterio.getAttribute("DATA_FIM");
        svcCodigo = (List<String>) criterio.getAttribute("SVC_CODIGO");
        nseCodigo = (List<String>) criterio.getAttribute("NSE_CODIGO");
        operacaoPorCsa = (Boolean) criterio.getAttribute("OPERACAO_POR_CSA");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder sql = new StringBuilder();

        sql.append(" select ");
        if (operacaoPorCsa) {
            sql.append(" CSA_CODIGO AS CSA_CODIGO, CSA_NOME AS CSA_NOME, CSA AS CSA, ");
        } else {
            sql.append(" '1' AS CSA_CODIGO, 'XXXXX' AS CSA_NOME, 'XXX' AS CSA, ");
        }
        sql.append(" SERVICO AS SERVICO, ");
        sql.append(" sum(case when Tipo = '1' then Total else 0 end) AS ATIVO_INICIO_MES, ");
        sql.append(" sum(case when Tipo = '2' then Total else 0 end) AS ATIVO_FIM_MES, ");
        sql.append(" sum(case when Tipo = '3' then Total else 0 end) AS QUITADOS, ");
        sql.append(" sum(case when Tipo = '4' then Total else 0 end) AS RENEGOCIADOS, ");
        sql.append(" sum(case when Tipo = '5' then Total else 0 end) AS NOVOS, ");
        sql.append(" sum(case when Tipo = '6' then Total else 0 end) AS TOTAL_VALOR_DESCONTADO_MES, ");
        sql.append(" sum(case when Tipo = '7' then Total else 0 end) AS PARTICIPACAO_TOTAL_SERVIDORES, ");
        sql.append(" sum(case when Tipo = '8' then Total else 0 end) AS PARTICIPACAO_TOTAL_DESCONTADO, ");
        sql.append(" sum(case when Tipo = '9' then Total else 0 end) AS RETENCAO_GOVERNO ");

        sql.append(" from ");

        //1 = Ativo início do mês
        sql.append(" ( select csa.csa_codigo, csa.csa_nome, ");
        sql.append(" (concat(concat(csa.csa_identificador,'-'),csa.csa_nome)) as CSA,");
        sql.append(" nse.nse_descricao as Servico, '1' as Tipo, count(*) as Total ");
        sql.append(" from tb_aut_desconto ade ");
        sql.append(" inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        sql.append(" inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        sql.append(" inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo) ");
        sql.append(" inner join tb_natureza_servico nse on (nse.nse_codigo = svc.nse_codigo) ");
        sql.append(" inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) ");
        sql.append(" where ade.ade_data < :dataIni");
        if ((svcCodigo != null) && !svcCodigo.isEmpty()){
            sql.append(" and svc.svc_codigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        if ((nseCodigo != null) && !nseCodigo.isEmpty()) {
            sql.append(" and svc.nse_codigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }
        sql.append(" and (ade.sad_codigo not in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("')");
        sql.append(" or (ade.sad_codigo in ('").append(CodedValues.SAD_CANCELADA).append("','").append(CodedValues.SAD_LIQUIDADA).append("') ");
        sql.append(" and exists ( ");
        sql.append("    select 1 from tb_ocorrencia_autorizacao oca ");
        sql.append("    where oca.ade_codigo = ade.ade_codigo ");
        sql.append("    and oca.toc_codigo in ('").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("','").append(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO).append("') ");
        sql.append("    and oca.oca_data > :dataIni ");
        sql.append("   ) ) )   ");
        sql.append(" group by csa.csa_codigo, csa.csa_nome, csa.csa_identificador, nse.nse_descricao ");

        sql.append(" union ");

        //2 = Ativos fim do mês
        sql.append(" select csa.csa_codigo, csa.csa_nome, ");
        sql.append(" (concat(concat(csa.csa_identificador,'-'),csa.csa_nome)) as CSA,");
        sql.append(" nse.nse_descricao as Servico, '2' as Tipo, count(*) as Total ");
        sql.append(" from tb_aut_desconto ade ");
        sql.append(" inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        sql.append(" inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        sql.append(" inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo) ");
        sql.append(" inner join tb_natureza_servico nse on (nse.nse_codigo = svc.nse_codigo) ");
        sql.append(" inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) ");
        sql.append(" where ade.ade_data < :dataFim");
        if ((svcCodigo != null) && !svcCodigo.isEmpty()){
            sql.append(" and svc.svc_codigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        if ((nseCodigo != null) && !nseCodigo.isEmpty()) {
            sql.append(" and svc.nse_codigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }
        sql.append(" and (ade.sad_codigo not in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("')");
        sql.append(" or (ade.sad_codigo in ('").append(CodedValues.SAD_CANCELADA).append("','").append(CodedValues.SAD_LIQUIDADA).append("') ");
        sql.append(" and exists ( ");
        sql.append("    select 1 from tb_ocorrencia_autorizacao oca ");
        sql.append("    where oca.ade_codigo = ade.ade_codigo ");
        sql.append("    and oca.toc_codigo in ('").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("','").append(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO).append("') ");
        sql.append("    and oca.oca_data > :dataFim");
        sql.append(" ) ) ) ");
        sql.append(" group by csa.csa_codigo, csa.csa_nome, csa.csa_identificador, nse.nse_descricao ");

        sql.append(" union ");

        //3 = Quitados no mês
        sql.append(" select csa.csa_codigo, csa.csa_nome, ");
        sql.append(" (concat(concat(csa.csa_identificador,'-'),csa.csa_nome)) as CSA,");
        sql.append(" nse.nse_descricao as Servico, '3' as Tipo, count(*) as Total ");
        sql.append(" from tb_aut_desconto ade ");
        sql.append(" inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        sql.append(" inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        sql.append(" inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo) ");
        sql.append(" inner join tb_natureza_servico nse on (nse.nse_codigo = svc.nse_codigo) ");
        sql.append(" inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) ");
        sql.append(" inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) ");
        sql.append(" where ade.sad_codigo = '").append(CodedValues.SAD_LIQUIDADA).append("' ");
        if ((svcCodigo != null) && !svcCodigo.isEmpty()){
            sql.append(" and svc.svc_codigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        if ((nseCodigo != null) && !nseCodigo.isEmpty()){
            sql.append(" and svc.nse_codigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }
        sql.append(" and oca.toc_codigo = '").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("' ");
        sql.append(" and oca.oca_data between :dataIni and :dataFim ");
        sql.append(" group by csa.csa_codigo, csa.csa_nome, csa.csa_identificador, nse.nse_descricao ");

        sql.append(" union ");

        //4 = Renegociados no mês
        sql.append(" select csa.csa_codigo, csa.csa_nome, ");
        sql.append(" (concat(concat(csa.csa_identificador,'-'),csa.csa_nome)) as CSA,");
        sql.append(" nse.nse_descricao as Servico, '4' as Tipo, count(*) as Total ");
        sql.append(" from tb_aut_desconto ade ");
        sql.append(" inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        sql.append(" inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        sql.append(" inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo) ");
        sql.append(" inner join tb_natureza_servico nse on (nse.nse_codigo = svc.nse_codigo) ");
        sql.append(" inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) ");
        sql.append(" inner join tb_relacionamento_autorizacao rad on (rad.ade_codigo_origem = ade.ade_codigo) ");
        sql.append(" inner join tb_aut_desconto ade2 on (rad.ade_codigo_destino = ade2.ade_codigo) ");
        sql.append(" where ade.sad_codigo in ('").append(CodedValues.SAD_LIQUIDADA).append("','").append(CodedValues.SAD_CONCLUIDO).append("') ");
        if ((svcCodigo != null) && !svcCodigo.isEmpty()){
            sql.append(" and svc.svc_codigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        if ((nseCodigo != null) && !nseCodigo.isEmpty()){
            sql.append(" and svc.nse_codigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }
        sql.append(" and rad.tnt_codigo = '").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("' ");
        sql.append(" and ade2.sad_codigo <> '").append(CodedValues.SAD_CANCELADA).append("' ");
        sql.append(" and rad.rad_data between :dataIni and :dataFim ");

        sql.append(" group by csa.csa_codigo, csa.csa_nome, csa.csa_identificador, nse.nse_descricao ");

        sql.append(" union ");

        //5 = Novos no mês

        sql.append(" select csa.csa_codigo, csa.csa_nome, ");
        sql.append(" (concat(concat(csa.csa_identificador,'-'),csa.csa_nome)) as CSA,");
        sql.append(" nse.nse_descricao as Servico, '5' as Tipo, count(*) as Total ");
        sql.append(" from tb_aut_desconto ade ");
        sql.append(" inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        sql.append(" inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        sql.append(" inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo) ");
        sql.append(" inner join tb_natureza_servico nse on (nse.nse_codigo = svc.nse_codigo) ");
        sql.append(" inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) ");
        sql.append(" where ade.ade_data between :dataIni and :dataFim ");
        if ((svcCodigo != null) && !svcCodigo.isEmpty()){
            sql.append(" and svc.svc_codigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        if ((nseCodigo != null) && !nseCodigo.isEmpty()){
            sql.append(" and svc.nse_codigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }
        sql.append(" group by csa.csa_codigo, csa.csa_nome, csa.csa_identificador, nse.nse_descricao ");

        sql.append(" union ");

        //6 = Total valor descontado no mês
        sql.append(" select csa.csa_codigo, csa.csa_nome, ");
        sql.append(" (concat(concat(csa.csa_identificador,'-'),csa.csa_nome)) as CSA,");
        sql.append(" nse.nse_descricao as Servico, '6' as Tipo, sum(prd_vlr_realizado) ");
        sql.append(" from tb_aut_desconto ade ");
        sql.append(" inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        sql.append(" inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        sql.append(" inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo) ");
        sql.append(" inner join tb_natureza_servico nse on (nse.nse_codigo = svc.nse_codigo) ");
        sql.append(" inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) ");
        sql.append(" inner join tb_parcela_desconto prd on (ade.ade_codigo = prd.ade_codigo) ");
        sql.append(" where prd.prd_data_desconto ").append(criaClausulaNomeada("periodo", periodo));
        if ((svcCodigo != null) && !svcCodigo.isEmpty()){
            sql.append(" and svc.svc_codigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        if ((nseCodigo != null) && !nseCodigo.isEmpty()){
            sql.append(" and svc.nse_codigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }
        sql.append(" and prd.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' ");
        sql.append(" group by csa.csa_codigo, csa.csa_nome, csa.csa_identificador, nse.nse_descricao ");

        sql.append(" union ");

        //7 = Participação da consignatária em relação aos ativos do final do mês pela quantidade total de servidores
        sql.append(" select csa.csa_codigo, csa.csa_nome, ");
        sql.append(" (concat(concat(csa.csa_identificador,'-'),csa.csa_nome)) as CSA,");
        sql.append(" nse.nse_descricao as Servico, '7' as Tipo, ");
        sql.append(" count(*) / (select count(*) from tb_registro_servidor rse where rse.srs_codigo").append(" NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("'))").append(" as Total ");
        sql.append(" from tb_aut_desconto ade ");
        sql.append(" inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        sql.append(" inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        sql.append(" inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo) ");
        sql.append(" inner join tb_natureza_servico nse on (nse.nse_codigo = svc.nse_codigo) ");
        sql.append(" inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) ");
        sql.append(" where (ade.sad_codigo not in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("')");
        sql.append(" or (ade.sad_codigo in ('").append(CodedValues.SAD_CANCELADA).append("','").append(CodedValues.SAD_LIQUIDADA).append("') ");
        sql.append(" and exists ( ");
        sql.append("    select 1 from tb_ocorrencia_autorizacao oca ");
        sql.append("    where oca.ade_codigo = ade.ade_codigo ");
        sql.append("    and oca.toc_codigo in ('").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("','").append(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO).append("') ");
        sql.append("    and oca.oca_data > :dataFim");
        sql.append(" ) ) ) ");
        if ((svcCodigo != null) && !svcCodigo.isEmpty()){
            sql.append(" and svc.svc_codigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        if ((nseCodigo != null) && !nseCodigo.isEmpty()){
            sql.append(" and svc.nse_codigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }
        sql.append(" group by csa.csa_codigo, csa.csa_nome, csa.csa_identificador, nse.nse_descricao ");

        sql.append(" union ");

        //8 = Participação da consignatária em relação aos volume total descontado no mês
        sql.append(" select csa.csa_codigo, csa.csa_nome, ");
        sql.append(" (concat(concat(csa.csa_identificador,'-'),csa.csa_nome)) as CSA,");
        sql.append(" nse.nse_descricao as Servico, '8' as Tipo, ");
        sql.append(" sum(prd.prd_vlr_realizado) / (select sum(prd2.prd_vlr_realizado) from tb_parcela_desconto prd2 ");
        sql.append(" where prd2.prd_data_desconto ").append(criaClausulaNomeada("periodo", periodo));
        sql.append(" and prd2.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("') as Total ");
        sql.append(" from tb_aut_desconto ade ");
        sql.append(" inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        sql.append(" inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        sql.append(" inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo) ");
        sql.append(" inner join tb_natureza_servico nse on (nse.nse_codigo = svc.nse_codigo) ");
        sql.append(" inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) ");
        sql.append(" inner join tb_parcela_desconto prd on (ade.ade_codigo = prd.ade_codigo) ");
        sql.append(" where prd.prd_data_desconto ").append(criaClausulaNomeada("periodo", periodo));
        sql.append(" and prd.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' ");
        if ((svcCodigo != null) && !svcCodigo.isEmpty()){
            sql.append(" and svc.svc_codigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        if ((nseCodigo != null) && !nseCodigo.isEmpty()){
            sql.append(" and svc.nse_codigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }
        sql.append(" group by csa.csa_codigo, csa.csa_nome, csa.csa_identificador, nse.nse_descricao ");

        sql.append(" union ");

        //9 = Retenção Gov.
        sql.append(" select CSA_CODIGO, CSA_NOME, CSA, SERVICO, ");
        sql.append(" '9' AS Tipo, ");
        sql.append(" to_decimal(CASE WHEN FORMA = 'VALOR FIXO' THEN SUM(QUANTIDADE1 * TARIFA) ELSE SUM((VALOR1 * TARIFA)/100) END, 13, 2) AS Total ");
        sql.append(" FROM ( ");
        sql.append(" SELECT ");
        sql.append(" tb_consignataria.csa_codigo AS CSA_CODIGO, tb_consignataria.csa_nome AS CSA_NOME, ");
        sql.append(" (concat(concat(tb_consignataria.csa_identificador,'-'),tb_consignataria.csa_nome)) as CSA,");
        sql.append(" tb_natureza_servico.nse_descricao as SERVICO, ");
        sql.append(" CASE WHEN COALESCE(NULLIF(tb_param_svc_consignataria.psc_vlr_ref,''), to_string(tb_param_tarif_consignante.pcv_forma_calc)) = '1' THEN '"+ApplicationResourcesHelper.getMessage("rotulo.valor.fixo.singular", (AcessoSistema) null)+"' ELSE '"+ApplicationResourcesHelper.getMessage("rotulo.percentual.singular", (AcessoSistema) null)+"' END AS FORMA, ");
        sql.append(" to_decimal(COUNT(DISTINCT tb_parcela_desconto.ade_codigo), 13, 2) AS QUANTIDADE1, ");
        sql.append(" SUM(tb_aut_desconto.ade_vlr) AS VALOR1, ");
        sql.append(" COALESCE(to_decimal(NULLIF(tb_param_svc_consignataria.psc_vlr,''), 13, 2), tb_param_tarif_consignante.pcv_vlr) AS TARIFA ");
        sql.append(" FROM tb_parcela_desconto ");
        sql.append(" INNER JOIN tb_aut_desconto ON (tb_parcela_desconto.ade_codigo = tb_aut_desconto.ade_codigo) ");
        sql.append(" INNER JOIN tb_verba_convenio ON (tb_aut_desconto.vco_codigo = tb_verba_convenio.vco_codigo) ");
        sql.append(" INNER JOIN tb_convenio ON (tb_verba_convenio.cnv_codigo = tb_convenio.cnv_codigo) ");
        sql.append(" INNER JOIN tb_servico ON (tb_convenio.svc_codigo = tb_servico.svc_codigo) ");
        sql.append(" INNER JOIN tb_natureza_servico on (tb_natureza_servico.nse_codigo = tb_servico.nse_codigo) ");
        sql.append(" INNER JOIN tb_param_tarif_consignante ON (tb_convenio.svc_codigo = tb_param_tarif_consignante.svc_codigo) ");
        sql.append(" INNER JOIN tb_consignataria ON (tb_convenio.csa_codigo = tb_consignataria.csa_codigo) ");
        sql.append(" LEFT OUTER JOIN tb_param_svc_consignataria ON ");
        sql.append(" (tb_param_svc_consignataria.tps_codigo = '").append(CodedValues.TPS_VLR_INTERVENIENCIA).append("' ");
        sql.append(" AND tb_param_tarif_consignante.svc_codigo = tb_param_svc_consignataria.svc_codigo ");
        sql.append(" AND tb_consignataria.csa_codigo = tb_param_svc_consignataria.csa_codigo) ");
        sql.append(" WHERE tb_param_tarif_consignante.pcv_base_calc = '2' ");
        sql.append(" AND tb_param_tarif_consignante.pcv_vlr > 0.00 ");
        sql.append(" AND tb_parcela_desconto.spd_codigo = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' ");
        sql.append(" AND tb_parcela_desconto.prd_data_desconto ").append(criaClausulaNomeada("periodo", periodo)).append(" ");
        if ((svcCodigo != null) && !svcCodigo.isEmpty()){
            sql.append(" and tb_servico.svc_codigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        if ((nseCodigo != null) && !nseCodigo.isEmpty()){
            sql.append(" and tb_servico.nse_codigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }
        sql.append(" GROUP BY ");
        sql.append(" tb_consignataria.csa_codigo, tb_consignataria.csa_nome, tb_consignataria.csa_identificador, ");
        sql.append(" tb_natureza_servico.nse_descricao, tb_param_tarif_consignante.pcv_forma_calc, ");
        sql.append(" tb_param_tarif_consignante.pcv_vlr, tb_param_svc_consignataria.psc_vlr, tb_param_svc_consignataria.psc_vlr_ref ");
        sql.append(" ) RETENCAO ");
        sql.append(" GROUP BY CSA_CODIGO, CSA_NOME, CSA, SERVICO, FORMA, TARIFA ");
        sql.append(" ORDER BY CSA_CODIGO, SERVICO ");

        sql.append(" ) X ");
        sql.append(" group by ");
        if (operacaoPorCsa) {
            sql.append(" CSA_CODIGO, CSA_NOME, CSA, ");
        }
        sql.append(" SERVICO ");
        sql.append(" order by ");
        sql.append(" SERVICO ");
        if (operacaoPorCsa) {
            sql.append(" ,CSA_CODIGO, CSA_NOME, CSA ");
        }

        final Query<Object[]> query = instanciarQuery(session, sql.toString());

        if (!TextHelper.isNull(periodo)) {
            defineValorClausulaNomeada("periodo", parseDateString(periodo), query);
        }

        if (!TextHelper.isNull(dataIni)) {
            defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), query);
        }

        if (!TextHelper.isNull(dataFim)) {
            defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);
        }

        if ((svcCodigo != null) && !svcCodigo.isEmpty()) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }
        if ((nseCodigo != null) && !nseCodigo.isEmpty()) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_NOME,
                "CSA",
                "SERVICO",
                "ATIVO_INICIO_MES",
                "ATIVO_FIM_MES",
                "QUITADOS",
                "RENEGOCIADOS",
                "NOVOS",
                "TOTAL_VALOR_DESCONTADO_MES",
                "PARTICIPACAO_TOTAL_SERVIDORES",
                "PARTICIPACAO_TOTAL_DESCONTADO",
                "RETENCAO_GOVERNO"
        };
    }
}