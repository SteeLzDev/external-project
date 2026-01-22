package com.zetra.econsig.persistence.query.relatorio;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p> Title: RelatorioMarketShareCsaQuery</p>
 * <p> Description: Relatório Market Share de consignatária de empréstimo consignado.</p>
 * <p> Copyright: Copyright (c) 2013 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioMarketShareCsaQuery extends ReportHNativeQuery {

    public String periodo;
    public List<String> orgCodigos;
    public String estCodigo;
    public List<String> nseCodigos;
    public List<String> svcCodigos;
    public List<String> csaCodigos;

    public AcessoSistema responsavel;

    @Override
    protected String[] getFields() {
        return new String[] {
                "CONSIGNATARIA",
                "ATIVO_INICIO_MES",
                "ATIVO_FIM_MES",
                "QUITADOS",
                "RENEGOCIADOS",
                "NOVOS",
                "TOTAL_VALOR_DESCONTADO_MES",
                "PARTICIPACAO_TOTAL_SERVIDORES",
                "PARTICIPACAO_TOTAL_DESCONTADO"
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        periodo = (String) criterio.getAttribute("PERIODO");
        estCodigo = (String) criterio.getAttribute("EST_CODIGO");
        orgCodigos = (List<String>) criterio.getAttribute("ORG_CODIGO");
        nseCodigos = (List<String>) criterio.getAttribute("NSE_CODIGO");
        svcCodigos = (List<String>) criterio.getAttribute("SVC_CODIGO");
        csaCodigos = (List<String>) criterio.getAttribute("CSA_CODIGO");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String dataIni = null;
        String dataFim = null;
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(DateHelper.parse(periodo, "yyyy-MM-dd"));

            dataIni = DateHelper.reformat(periodo, "yyyy-MM-dd", "yyyy-MM-01 00:00:00");
            dataFim = DateHelper.reformat(periodo, "yyyy-MM-dd", "yyyy-MM-" + cal.getActualMaximum(Calendar.DATE) + " 23:59:59");
        } catch (ParseException e) {
            throw new HQueryException("mensagem.erro.periodo.nao.informado", responsavel);
        }

        StringBuilder where = new StringBuilder("where 1 = 1 ");
        if (!TextHelper.isNull(estCodigo)) {
            where.append(" and est.est_codigo ").append(criaClausulaNomeada("estCodigo", estCodigo)).append(" ");
        }
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            where.append(" and org.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos)).append(" ");
        }
        if (!TextHelper.isNull(nseCodigos)) {
            where.append(" and nse.nse_codigo ").append(criaClausulaNomeada("nseCodigo", nseCodigos)).append(" ");
        }
        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            where.append(" and svc.svc_codigo ").append(criaClausulaNomeada("svcCodigos", svcCodigos)).append(" ");
        }
        if (csaCodigos != null && !csaCodigos.isEmpty()) {
            where.append(" and csa.csa_codigo ").append(criaClausulaNomeada("csaCodigos", csaCodigos)).append(" ");
        }

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select CONSIGNATARIA AS CONSIGNATARIA, ");
        corpoBuilder.append("sum(case when Tipo = '1' then Total else 0 end) AS ATIVO_INICIO_MES, ");
        corpoBuilder.append("sum(case when Tipo = '2' then Total else 0 end) AS ATIVO_FIM_MES, ");
        corpoBuilder.append("sum(case when Tipo = '3' then Total else 0 end) AS QUITADOS, ");
        corpoBuilder.append("sum(case when Tipo = '4' then Total else 0 end) AS RENEGOCIADOS, ");
        corpoBuilder.append("sum(case when Tipo = '5' then Total else 0 end) AS NOVOS, ");
        corpoBuilder.append("sum(case when Tipo = '6' then Total else 0 end) AS TOTAL_VALOR_DESCONTADO_MES, ");
        corpoBuilder.append("sum(case when Tipo = '7' then Total else 0 end) AS PARTICIPACAO_TOTAL_SERVIDORES, ");
        corpoBuilder.append("sum(case when Tipo = '8' then Total else 0 end) AS PARTICIPACAO_TOTAL_DESCONTADO, ");
        corpoBuilder.append("sum(case when Tipo = '9' then Total else 0 end) AS RETENCAO_CONSIGNANTE ");
        corpoBuilder.append("from  ");
        corpoBuilder.append("( ");

        // 1 = Ativo início do mês
        corpoBuilder.append("select csa.").append(Columns.getColumnName(Columns.CSA_NOME)).append(" as CONSIGNATARIA, '1' as Tipo, count(*) as Total ");
        corpoBuilder.append("from ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" ade  ");
        corpoBuilder.append("inner join ").append(Columns.TB_VERBA_CONVENIO).append(" vco on (ade.").append(Columns.getColumnName(Columns.VCO_CODIGO)).append(" = vco.").append(Columns.getColumnName(Columns.VCO_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONVENIO).append(" cnv on (vco.").append(Columns.getColumnName(Columns.CNV_CODIGO)).append(" = cnv.").append(Columns.getColumnName(Columns.CNV_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONSIGNATARIA).append(" csa on (cnv.").append(Columns.getColumnName(Columns.CSA_CODIGO)).append(" = csa.").append(Columns.getColumnName(Columns.CSA_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_SERVICO).append(" svc on (cnv.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(" = svc.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ORGAO).append(" org on (cnv.").append(Columns.getColumnName(Columns.ORG_CODIGO)).append(" = org.").append(Columns.getColumnName(Columns.ORG_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ESTABELECIMENTO).append(" est on (est.").append(Columns.getColumnName(Columns.EST_CODIGO)).append(" = org.").append(Columns.getColumnName(Columns.EST_CODIGO)).append(") ");
        corpoBuilder.append("left outer join ").append(Columns.TB_NATUREZA_SERVICO).append(" nse on (nse.").append(Columns.getColumnName(Columns.NSE_CODIGO)).append(" = svc.").append(Columns.getColumnName(Columns.NSE_CODIGO)).append(") ");
        corpoBuilder.append(where);
        corpoBuilder.append("and ade.").append(Columns.getColumnName(Columns.ADE_DATA)).append(" < :dataIni ");
        corpoBuilder.append("and (ade.").append(Columns.getColumnName(Columns.SAD_CODIGO)).append(" not in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("') ");
        corpoBuilder.append("or (ade.").append(Columns.getColumnName(Columns.SAD_CODIGO)).append(" in ('").append(CodedValues.SAD_CANCELADA).append("','").append(CodedValues.SAD_LIQUIDADA).append("')  ");
        corpoBuilder.append("and exists ( ");
        corpoBuilder.append("select 1 from ").append(Columns.TB_OCORRENCIA_AUTORIZACAO).append(" oca ");
        corpoBuilder.append("where oca.").append(Columns.getColumnName(Columns.ADE_CODIGO)).append(" = ade.").append(Columns.getColumnName(Columns.ADE_CODIGO)).append(" ");
        corpoBuilder.append("and oca.").append(Columns.getColumnName(Columns.TOC_CODIGO)).append(" in ('").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("','").append(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO).append("') ");
        corpoBuilder.append("and oca.").append(Columns.getColumnName(Columns.OCA_DATA)).append(" > :dataIni ");
        corpoBuilder.append(") ");
        corpoBuilder.append(") ");
        corpoBuilder.append(") ");
        corpoBuilder.append("group by csa.").append(Columns.getColumnName(Columns.CSA_NOME)).append(" ");

        corpoBuilder.append("union ");

        // 2 = Ativos fim do mês
        corpoBuilder.append("select csa.").append(Columns.getColumnName(Columns.CSA_NOME)).append(", '2' as Tipo, count(*) as Total ");
        corpoBuilder.append("from ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" ade  ");
        corpoBuilder.append("inner join ").append(Columns.TB_VERBA_CONVENIO).append(" vco on (ade.").append(Columns.getColumnName(Columns.VCO_CODIGO)).append(" = vco.").append(Columns.getColumnName(Columns.VCO_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONVENIO).append(" cnv on (vco.").append(Columns.getColumnName(Columns.CNV_CODIGO)).append(" = cnv.").append(Columns.getColumnName(Columns.CNV_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONSIGNATARIA).append(" csa on (cnv.").append(Columns.getColumnName(Columns.CSA_CODIGO)).append(" = csa.").append(Columns.getColumnName(Columns.CSA_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_SERVICO).append(" svc on (cnv.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(" = svc.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ORGAO).append(" org on (cnv.").append(Columns.getColumnName(Columns.ORG_CODIGO)).append(" = org.").append(Columns.getColumnName(Columns.ORG_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ESTABELECIMENTO).append(" est on (est.").append(Columns.getColumnName(Columns.EST_CODIGO)).append(" = org.").append(Columns.getColumnName(Columns.EST_CODIGO)).append(") ");
        corpoBuilder.append("left outer join ").append(Columns.TB_NATUREZA_SERVICO).append(" nse on (nse.").append(Columns.getColumnName(Columns.NSE_CODIGO)).append(" = svc.").append(Columns.getColumnName(Columns.NSE_CODIGO)).append(") ");
        corpoBuilder.append(where);
        corpoBuilder.append("and ade.").append(Columns.getColumnName(Columns.ADE_DATA)).append(" < :dataFim ");
        corpoBuilder.append("and (ade.").append(Columns.getColumnName(Columns.SAD_CODIGO)).append(" not in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("') ");
        corpoBuilder.append("or (ade.").append(Columns.getColumnName(Columns.SAD_CODIGO)).append(" in ('").append(CodedValues.SAD_CANCELADA).append("','").append(CodedValues.SAD_LIQUIDADA).append("')  ");
        corpoBuilder.append("and exists ( ");
        corpoBuilder.append("select 1 from ").append(Columns.TB_OCORRENCIA_AUTORIZACAO).append(" oca ");
        corpoBuilder.append("where oca.").append(Columns.getColumnName(Columns.ADE_CODIGO)).append(" = ade.").append(Columns.getColumnName(Columns.ADE_CODIGO)).append(" ");
        corpoBuilder.append("and oca.").append(Columns.getColumnName(Columns.TOC_CODIGO)).append(" in ('").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("','").append(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO).append("') ");
        corpoBuilder.append("and oca.").append(Columns.getColumnName(Columns.OCA_DATA)).append(" > :dataFim ");
        corpoBuilder.append(") ");
        corpoBuilder.append(") ");
        corpoBuilder.append(") ");
        corpoBuilder.append("group by csa.").append(Columns.getColumnName(Columns.CSA_NOME)).append(" ");

        corpoBuilder.append("union ");

        // 3 = Quitados no mês
        corpoBuilder.append("select csa.").append(Columns.getColumnName(Columns.CSA_NOME)).append(", '3' as Tipo, count(*) as Total ");
        corpoBuilder.append("from ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" ade  ");
        corpoBuilder.append("inner join ").append(Columns.TB_VERBA_CONVENIO).append(" vco on (ade.").append(Columns.getColumnName(Columns.VCO_CODIGO)).append(" = vco.").append(Columns.getColumnName(Columns.VCO_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONVENIO).append(" cnv on (vco.").append(Columns.getColumnName(Columns.CNV_CODIGO)).append(" = cnv.").append(Columns.getColumnName(Columns.CNV_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONSIGNATARIA).append(" csa on (cnv.").append(Columns.getColumnName(Columns.CSA_CODIGO)).append(" = csa.").append(Columns.getColumnName(Columns.CSA_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_OCORRENCIA_AUTORIZACAO).append(" oca on (oca.").append(Columns.getColumnName(Columns.ADE_CODIGO)).append(" = ade.").append(Columns.getColumnName(Columns.ADE_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_SERVICO).append(" svc on (cnv.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(" = svc.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ORGAO).append(" org on (cnv.").append(Columns.getColumnName(Columns.ORG_CODIGO)).append(" = org.").append(Columns.getColumnName(Columns.ORG_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ESTABELECIMENTO).append(" est on (est.").append(Columns.getColumnName(Columns.EST_CODIGO)).append(" = org.").append(Columns.getColumnName(Columns.EST_CODIGO)).append(") ");
        corpoBuilder.append("left outer join ").append(Columns.TB_NATUREZA_SERVICO).append(" nse on (nse.").append(Columns.getColumnName(Columns.NSE_CODIGO)).append(" = svc.").append(Columns.getColumnName(Columns.NSE_CODIGO)).append(") ");
        corpoBuilder.append(where);
        corpoBuilder.append("and ade.").append(Columns.getColumnName(Columns.SAD_CODIGO)).append(" = '").append(CodedValues.SAD_LIQUIDADA).append("' ");
        corpoBuilder.append("and oca.").append(Columns.getColumnName(Columns.TOC_CODIGO)).append(" = '").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("' ");
        corpoBuilder.append("and oca.").append(Columns.getColumnName(Columns.OCA_DATA)).append(" between :dataIni and :dataFim ");
        corpoBuilder.append("group by csa.").append(Columns.getColumnName(Columns.CSA_NOME)).append(" ");

        corpoBuilder.append("union ");

        // 4 = Renegociados no mês
        corpoBuilder.append("select csa.").append(Columns.getColumnName(Columns.CSA_NOME)).append(", '4' as Tipo, count(*) as Total ");
        corpoBuilder.append("from ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" ade  ");
        corpoBuilder.append("inner join ").append(Columns.TB_VERBA_CONVENIO).append(" vco on (ade.").append(Columns.getColumnName(Columns.VCO_CODIGO)).append(" = vco.").append(Columns.getColumnName(Columns.VCO_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONVENIO).append(" cnv on (vco.").append(Columns.getColumnName(Columns.CNV_CODIGO)).append(" = cnv.").append(Columns.getColumnName(Columns.CNV_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONSIGNATARIA).append(" csa on (cnv.").append(Columns.getColumnName(Columns.CSA_CODIGO)).append(" = csa.").append(Columns.getColumnName(Columns.CSA_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_RELACIONAMENTO_AUTORIZACAO).append(" rad on (rad.").append(Columns.getColumnName(Columns.RAD_ADE_CODIGO_ORIGEM)).append(" = ade.").append(Columns.getColumnName(Columns.ADE_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" ade2 on (rad.").append(Columns.getColumnName(Columns.RAD_ADE_CODIGO_DESTINO)).append(" = ade2.").append(Columns.getColumnName(Columns.ADE_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_SERVICO).append(" svc on (cnv.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(" = svc.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ORGAO).append(" org on (cnv.").append(Columns.getColumnName(Columns.ORG_CODIGO)).append(" = org.").append(Columns.getColumnName(Columns.ORG_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ESTABELECIMENTO).append(" est on (est.").append(Columns.getColumnName(Columns.EST_CODIGO)).append(" = org.").append(Columns.getColumnName(Columns.EST_CODIGO)).append(") ");
        corpoBuilder.append("left outer join ").append(Columns.TB_NATUREZA_SERVICO).append(" nse on (nse.").append(Columns.getColumnName(Columns.NSE_CODIGO)).append(" = svc.").append(Columns.getColumnName(Columns.NSE_CODIGO)).append(") ");
        corpoBuilder.append(where);
        corpoBuilder.append("and ade.").append(Columns.getColumnName(Columns.SAD_CODIGO)).append(" in ('").append(CodedValues.SAD_LIQUIDADA).append("','").append(CodedValues.SAD_CONCLUIDO).append("') ");
        corpoBuilder.append("and rad.").append(Columns.getColumnName(Columns.TNT_CODIGO)).append(" = '").append(CodedValues.TNT_CONTROLE_RENEGOCIACAO).append("' ");
        corpoBuilder.append("and ade2.").append(Columns.getColumnName(Columns.SAD_CODIGO)).append(" <> '").append(CodedValues.SAD_CANCELADA).append("' ");
        corpoBuilder.append("and rad.").append(Columns.getColumnName(Columns.RAD_DATA)).append(" between :dataIni and :dataFim ");
        corpoBuilder.append("group by csa.").append(Columns.getColumnName(Columns.CSA_NOME)).append(" ");

        corpoBuilder.append("union ");

        // 5 = Novos no mês
        corpoBuilder.append("select csa.").append(Columns.getColumnName(Columns.CSA_NOME)).append(", '5' as Tipo, count(*) as Total ");
        corpoBuilder.append("from ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" ade  ");
        corpoBuilder.append("inner join ").append(Columns.TB_VERBA_CONVENIO).append(" vco on (ade.").append(Columns.getColumnName(Columns.VCO_CODIGO)).append(" = vco.").append(Columns.getColumnName(Columns.VCO_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONVENIO).append(" cnv on (vco.").append(Columns.getColumnName(Columns.CNV_CODIGO)).append(" = cnv.").append(Columns.getColumnName(Columns.CNV_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONSIGNATARIA).append(" csa on (cnv.").append(Columns.getColumnName(Columns.CSA_CODIGO)).append(" = csa.").append(Columns.getColumnName(Columns.CSA_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_SERVICO).append(" svc on (cnv.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(" = svc.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ORGAO).append(" org on (cnv.").append(Columns.getColumnName(Columns.ORG_CODIGO)).append(" = org.").append(Columns.getColumnName(Columns.ORG_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ESTABELECIMENTO).append(" est on (est.").append(Columns.getColumnName(Columns.EST_CODIGO)).append(" = org.").append(Columns.getColumnName(Columns.EST_CODIGO)).append(") ");
        corpoBuilder.append("left outer join ").append(Columns.TB_NATUREZA_SERVICO).append(" nse on (nse.").append(Columns.getColumnName(Columns.NSE_CODIGO)).append(" = svc.").append(Columns.getColumnName(Columns.NSE_CODIGO)).append(") ");
        corpoBuilder.append(where);
        corpoBuilder.append("and ade.").append(Columns.getColumnName(Columns.ADE_DATA)).append(" between :dataIni and :dataFim ");
        corpoBuilder.append("group by csa.").append(Columns.getColumnName(Columns.CSA_NOME)).append(" ");

        corpoBuilder.append("union ");

        // 6 = Total valor descontado no mês
        corpoBuilder.append("select csa.").append(Columns.getColumnName(Columns.CSA_NOME)).append(", '6' as Tipo, sum(").append(Columns.getColumnName(Columns.PRD_VLR_REALIZADO)).append(") ");
        corpoBuilder.append("from ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" ade  ");
        corpoBuilder.append("inner join ").append(Columns.TB_VERBA_CONVENIO).append(" vco on (ade.").append(Columns.getColumnName(Columns.VCO_CODIGO)).append(" = vco.").append(Columns.getColumnName(Columns.VCO_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONVENIO).append(" cnv on (vco.").append(Columns.getColumnName(Columns.CNV_CODIGO)).append(" = cnv.").append(Columns.getColumnName(Columns.CNV_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONSIGNATARIA).append(" csa on (cnv.").append(Columns.getColumnName(Columns.CSA_CODIGO)).append(" = csa.").append(Columns.getColumnName(Columns.CSA_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_PARCELA_DESCONTO).append(" prd on (ade.").append(Columns.getColumnName(Columns.ADE_CODIGO)).append(" = prd.").append(Columns.getColumnName(Columns.ADE_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_SERVICO).append(" svc on (cnv.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(" = svc.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ORGAO).append(" org on (cnv.").append(Columns.getColumnName(Columns.ORG_CODIGO)).append(" = org.").append(Columns.getColumnName(Columns.ORG_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ESTABELECIMENTO).append(" est on (est.").append(Columns.getColumnName(Columns.EST_CODIGO)).append(" = org.").append(Columns.getColumnName(Columns.EST_CODIGO)).append(") ");
        corpoBuilder.append("left outer join ").append(Columns.TB_NATUREZA_SERVICO).append(" nse on (nse.").append(Columns.getColumnName(Columns.NSE_CODIGO)).append(" = svc.").append(Columns.getColumnName(Columns.NSE_CODIGO)).append(") ");
        corpoBuilder.append(where);
        corpoBuilder.append("and prd.").append(Columns.getColumnName(Columns.PRD_DATA_DESCONTO)).append(" ").append(criaClausulaNomeada("periodo", periodo)).append(" ");
        corpoBuilder.append("and prd.").append(Columns.getColumnName(Columns.SPD_CODIGO)).append(" = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' ");
        corpoBuilder.append("group by csa.").append(Columns.getColumnName(Columns.CSA_NOME)).append(" ");

        corpoBuilder.append("union ");

        // 7 = Participação da consignatária em relação aos ativos do final do mês pela quantidade total de servidores
        corpoBuilder.append("select csa.").append(Columns.getColumnName(Columns.CSA_NOME)).append(", '7' as Tipo, ");
        corpoBuilder.append("count(*) / greatest(1, (select count(*) from ").append(Columns.TB_REGISTRO_SERVIDOR).append(" rse where rse.").append(Columns.getColumnName(Columns.SRS_CODIGO)).append(" NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')))").append(" as Total ");
        corpoBuilder.append("from ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" ade  ");
        corpoBuilder.append("inner join ").append(Columns.TB_VERBA_CONVENIO).append(" vco on (ade.").append(Columns.getColumnName(Columns.VCO_CODIGO)).append(" = vco.").append(Columns.getColumnName(Columns.VCO_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONVENIO).append(" cnv on (vco.").append(Columns.getColumnName(Columns.CNV_CODIGO)).append(" = cnv.").append(Columns.getColumnName(Columns.CNV_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONSIGNATARIA).append(" csa on (cnv.").append(Columns.getColumnName(Columns.CSA_CODIGO)).append(" = csa.").append(Columns.getColumnName(Columns.CSA_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_SERVICO).append(" svc on (cnv.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(" = svc.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ORGAO).append(" org on (cnv.").append(Columns.getColumnName(Columns.ORG_CODIGO)).append(" = org.").append(Columns.getColumnName(Columns.ORG_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ESTABELECIMENTO).append(" est on (est.").append(Columns.getColumnName(Columns.EST_CODIGO)).append(" = org.").append(Columns.getColumnName(Columns.EST_CODIGO)).append(") ");
        corpoBuilder.append("left outer join ").append(Columns.TB_NATUREZA_SERVICO).append(" nse on (nse.").append(Columns.getColumnName(Columns.NSE_CODIGO)).append(" = svc.").append(Columns.getColumnName(Columns.NSE_CODIGO)).append(") ");
        corpoBuilder.append(where);
        corpoBuilder.append("and (ade.").append(Columns.getColumnName(Columns.SAD_CODIGO)).append(" not in ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("') ");
        corpoBuilder.append("or (ade.").append(Columns.getColumnName(Columns.SAD_CODIGO)).append(" in ('").append(CodedValues.SAD_CANCELADA).append("','").append(CodedValues.SAD_LIQUIDADA).append("')  ");
        corpoBuilder.append("and exists ( ");
        corpoBuilder.append("select 1 from ").append(Columns.TB_OCORRENCIA_AUTORIZACAO).append(" oca ");
        corpoBuilder.append("where oca.").append(Columns.getColumnName(Columns.ADE_CODIGO)).append(" = ade.").append(Columns.getColumnName(Columns.ADE_CODIGO)).append(" ");
        corpoBuilder.append("and oca.").append(Columns.getColumnName(Columns.TOC_CODIGO)).append(" in ('").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("','").append(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO).append("') ");
        corpoBuilder.append("and oca.").append(Columns.getColumnName(Columns.OCA_DATA)).append(" > :dataFim ");
        corpoBuilder.append(") ");
        corpoBuilder.append(") ");
        corpoBuilder.append(") ");
        corpoBuilder.append("group by csa.").append(Columns.getColumnName(Columns.CSA_NOME)).append(" ");

        corpoBuilder.append("union ");

        // 8 = Participação da consignatária em relação aos volume total descontado no mês
        corpoBuilder.append("select csa.").append(Columns.getColumnName(Columns.CSA_NOME)).append(", '8' as Tipo, ");
        corpoBuilder.append("sum(prd.").append(Columns.getColumnName(Columns.PRD_VLR_REALIZADO)).append(") / greatest(1, (select sum(prd2.").append(Columns.getColumnName(Columns.PRD_VLR_REALIZADO)).append(") ");
        corpoBuilder.append("from ").append(Columns.TB_PARCELA_DESCONTO).append(" prd2 ");
        corpoBuilder.append("where prd2.").append(Columns.getColumnName(Columns.PRD_DATA_DESCONTO)).append(" ").append(criaClausulaNomeada("periodo", periodo)).append(" ");
        corpoBuilder.append("and prd2.").append(Columns.getColumnName(Columns.SPD_CODIGO)).append(" = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("')) as Total ");
        corpoBuilder.append("from ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" ade  ");
        corpoBuilder.append("inner join ").append(Columns.TB_VERBA_CONVENIO).append(" vco on (ade.").append(Columns.getColumnName(Columns.VCO_CODIGO)).append(" = vco.").append(Columns.getColumnName(Columns.VCO_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONVENIO).append(" cnv on (vco.").append(Columns.getColumnName(Columns.CNV_CODIGO)).append(" = cnv.").append(Columns.getColumnName(Columns.CNV_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONSIGNATARIA).append(" csa on (cnv.").append(Columns.getColumnName(Columns.CSA_CODIGO)).append(" = csa.").append(Columns.getColumnName(Columns.CSA_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_PARCELA_DESCONTO).append(" prd on (ade.").append(Columns.getColumnName(Columns.ADE_CODIGO)).append(" = prd.").append(Columns.getColumnName(Columns.ADE_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_SERVICO).append(" svc on (cnv.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(" = svc.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ORGAO).append(" org on (cnv.").append(Columns.getColumnName(Columns.ORG_CODIGO)).append(" = org.").append(Columns.getColumnName(Columns.ORG_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ESTABELECIMENTO).append(" est on (est.").append(Columns.getColumnName(Columns.EST_CODIGO)).append(" = org.").append(Columns.getColumnName(Columns.EST_CODIGO)).append(") ");
        corpoBuilder.append("left outer join ").append(Columns.TB_NATUREZA_SERVICO).append(" nse on (nse.").append(Columns.getColumnName(Columns.NSE_CODIGO)).append(" = svc.").append(Columns.getColumnName(Columns.NSE_CODIGO)).append(") ");
        corpoBuilder.append(where);
        corpoBuilder.append("and prd.").append(Columns.getColumnName(Columns.PRD_DATA_DESCONTO)).append(" ").append(criaClausulaNomeada("periodo", periodo)).append(" ");
        corpoBuilder.append("and prd.").append(Columns.getColumnName(Columns.SPD_CODIGO)).append(" = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' ");
        corpoBuilder.append("group by csa.").append(Columns.getColumnName(Columns.CSA_NOME)).append(" ");

        corpoBuilder.append("union ");

        // 9 = Total tarifação consignante.
        corpoBuilder.append("select csa.").append(Columns.getColumnName(Columns.CSA_NOME)).append(", '9' as Tipo, ");
        corpoBuilder.append("to_decimal(sum(case when pcv.").append(Columns.getColumnName(Columns.PCV_FORMA_CALC)).append(" = '1' then coalesce(pcv.").append(Columns.getColumnName(Columns.PCV_VLR)).append(", 0) else (prd.").append(Columns.getColumnName(Columns.PRD_VLR_REALIZADO)).append(" * coalesce(pcv.").append(Columns.getColumnName(Columns.PCV_VLR)).append(", 0) / 100) end), 13, 2) AS RETENCAO_CONSIGNANTE ");
        corpoBuilder.append("from ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" ade  ");
        corpoBuilder.append("inner join ").append(Columns.TB_VERBA_CONVENIO).append(" vco on (ade.").append(Columns.getColumnName(Columns.VCO_CODIGO)).append(" = vco.").append(Columns.getColumnName(Columns.VCO_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONVENIO).append(" cnv on (vco.").append(Columns.getColumnName(Columns.CNV_CODIGO)).append(" = cnv.").append(Columns.getColumnName(Columns.CNV_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_CONSIGNATARIA).append(" csa on (cnv.").append(Columns.getColumnName(Columns.CSA_CODIGO)).append(" = csa.").append(Columns.getColumnName(Columns.CSA_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_PARCELA_DESCONTO).append(" prd on (ade.").append(Columns.getColumnName(Columns.ADE_CODIGO)).append(" = prd.").append(Columns.getColumnName(Columns.ADE_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_SERVICO).append(" svc on (cnv.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(" = svc.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ORGAO).append(" org on (cnv.").append(Columns.getColumnName(Columns.ORG_CODIGO)).append(" = org.").append(Columns.getColumnName(Columns.ORG_CODIGO)).append(") ");
        corpoBuilder.append("inner join ").append(Columns.TB_ESTABELECIMENTO).append(" est on (est.").append(Columns.getColumnName(Columns.EST_CODIGO)).append(" = org.").append(Columns.getColumnName(Columns.EST_CODIGO)).append(") ");
        corpoBuilder.append("left outer join ").append(Columns.TB_NATUREZA_SERVICO).append(" nse on (nse.").append(Columns.getColumnName(Columns.NSE_CODIGO)).append(" = svc.").append(Columns.getColumnName(Columns.NSE_CODIGO)).append(") ");
        corpoBuilder.append("left outer join ").append(Columns.TB_PARAM_TARIF_CONSIGNANTE).append(" pcv on (pcv.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(" = svc.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(") ");
        corpoBuilder.append(where);
        corpoBuilder.append("and prd.").append(Columns.getColumnName(Columns.PRD_DATA_DESCONTO)).append(" ").append(criaClausulaNomeada("periodo", periodo)).append(" ");
        corpoBuilder.append("and prd.").append(Columns.getColumnName(Columns.SPD_CODIGO)).append(" = '").append(CodedValues.SPD_LIQUIDADAFOLHA).append("' ");
        corpoBuilder.append("group by csa.").append(Columns.getColumnName(Columns.CSA_NOME)).append(" ");

        corpoBuilder.append(") X ");
        corpoBuilder.append("GROUP BY CONSIGNATARIA ");
        corpoBuilder.append("ORDER BY 1 ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("periodo", parseDateString(periodo), query);
        defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), query);
        defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }
        if (!TextHelper.isNull(nseCodigos)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigos, query);
        }
        if (svcCodigos != null && !svcCodigos.isEmpty()) {
            defineValorClausulaNomeada("svcCodigos", svcCodigos, query);
        }
        if (csaCodigos != null && !csaCodigos.isEmpty()) {
            defineValorClausulaNomeada("csaCodigos", csaCodigos, query);
        }

        return query;
    }

}
