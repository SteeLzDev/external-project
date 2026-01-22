package com.zetra.econsig.persistence.query.relatorio;

import java.text.ParseException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p> Title: RelatorioPercentualRejeitoTotalQuery</p>
 * <p> Description: Recupera o percentual de rejeito total ou do período</p>
 * <p> Copyright: Copyright (c) 2009 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioPercentualRejeitoTotalQuery extends ReportHNativeQuery {

    private final String periodo;
    private final List<String> orgCodigos;
    private final List<String> estCodigos;
    private final boolean integrada;
    private final boolean rejeitoDoPeriodo;

    public RelatorioPercentualRejeitoTotalQuery(String periodo, List<String> orgCodigos, List<String> estCodigos, boolean integrada, boolean rejeitoDoPeriodo) {
        this.periodo = periodo;
        this.orgCodigos = orgCodigos;
        this.estCodigos = estCodigos;
        this.integrada = integrada;
        this.rejeitoDoPeriodo = rejeitoDoPeriodo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select prd_data_desconto as PERIODO, ");
        corpoBuilder.append("csa.csa_codigo as CSA_CODIGO, ");
        corpoBuilder.append("csa.csa_identificador as CSA_IDENTIFICADOR, ");
        corpoBuilder.append("csa_identificador_interno as CSA_ID, ");
        corpoBuilder.append("coalesce(nullif(csa_nome_abrev, ''), csa_nome) as CSA, ");
        corpoBuilder.append("to_decimal(sum(case when spd_codigo in ('").append(CodedValues.SPD_LIQUIDADAFOLHA).append("','").append(CodedValues.SPD_LIQUIDADAMANUAL).append("') then 1 else 0 end), 13, 2) as LIQUIDADAS, ");
        corpoBuilder.append("to_decimal(sum(case when spd_codigo not in ('").append(CodedValues.SPD_LIQUIDADAFOLHA).append("','").append(CodedValues.SPD_LIQUIDADAMANUAL).append("') then 1 else 0 end), 13, 2) as REJEITADAS, ");
        corpoBuilder.append("(to_decimal(sum(case when spd_codigo not in ('").append(CodedValues.SPD_LIQUIDADAFOLHA).append("','").append(CodedValues.SPD_LIQUIDADAMANUAL).append("') then 1 else 0 end), 13, 2) / count(*))*100.00 as PERC_REJEITO ");
        corpoBuilder.append("from tb_registro_servidor rse ");
        corpoBuilder.append("inner join tb_aut_desconto ade on (ade.rse_codigo = rse.rse_codigo) ");
        if (integrada) {
            corpoBuilder.append("inner join tb_parcela_desconto prd on (prd.ade_codigo = ade.ade_codigo) ");
        } else {
            corpoBuilder.append("inner join tb_parcela_desconto_periodo prd on (prd.ade_codigo = ade.ade_codigo) ");
        }
        corpoBuilder.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        corpoBuilder.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        corpoBuilder.append("inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) ");
        corpoBuilder.append("inner join tb_orgao org on (org.org_codigo = rse.org_codigo) ");
        corpoBuilder.append("inner join tb_estabelecimento est on (est.est_codigo = org.est_codigo) ");
        corpoBuilder.append("inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo) ");
        corpoBuilder.append("left outer join tb_param_convenio_registro_ser pcr on (rse.rse_codigo = pcr.rse_codigo and cnv.cnv_codigo = pcr.cnv_codigo and pcr.pcr_vlr = '0' and pcr.tps_codigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO).append("') ");
        corpoBuilder.append("left outer join tb_param_servico_registro_ser psr on (rse.rse_codigo = psr.rse_codigo and cnv.svc_codigo = psr.svc_codigo and psr.psr_vlr = '0' and psr.tps_codigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO).append("') ");
        corpoBuilder.append("left outer join tb_param_nse_registro_ser pnr on (rse.rse_codigo = pnr.rse_codigo and svc.nse_codigo = pnr.nse_codigo and pnr.pnr_vlr = '0' and pnr.tps_codigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO).append("') ");
        corpoBuilder.append("where rse.srs_codigo = '").append(CodedValues.SRS_ATIVO).append("' ");
        corpoBuilder.append("and pcr.rse_codigo is null ");
        corpoBuilder.append("and psr.rse_codigo is null ");
        corpoBuilder.append("and pnr.rse_codigo is null ");
        corpoBuilder.append("and prd_data_desconto ").append(criaClausulaNomeada("periodo", periodo));

        if (rejeitoDoPeriodo) {
            corpoBuilder.append(" and ade_ano_mes_ini ").append(criaClausulaNomeada("periodo", periodo));
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" and org.org_codigo ").append(criaClausulaNomeada("orgCodigos", orgCodigos));
        }

        if (estCodigos != null && !estCodigos.isEmpty()) {
            corpoBuilder.append(" and est.est_codigo ").append(criaClausulaNomeada("estCodigos", estCodigos));
        }

        corpoBuilder.append(" group by prd_data_desconto, csa.csa_codigo, csa.csa_identificador, ");
        corpoBuilder.append("csa_identificador_interno, coalesce(nullif(csa_nome_abrev, ''), csa_nome) ");
        corpoBuilder.append(" order by 8 desc, 5 asc ");

        Query<Object[]> queryInst = instanciarQuery(session, corpoBuilder.toString());

        try {
            defineValorClausulaNomeada("periodo", DateHelper.parse(periodo, "yyyy-MM-dd"), queryInst);
        } catch (ParseException ex) {
            throw new HQueryException("mensagem.erro.data.informada.invalida.arg0", (AcessoSistema) null, periodo);
        }

        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigos", orgCodigos, queryInst);
        }

        if (estCodigos != null && !estCodigos.isEmpty()) {
            defineValorClausulaNomeada("estCodigos", estCodigos, queryInst);
        }

        return queryInst;
    }

    @Override
    protected String[] getFields() {
        return new String [] {
                "PERIODO",
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                "CSA_ID",
                "CSA",
                "LIQUIDADAS",
                "REJEITADAS",
                "PERC_REJEITO"
        };
    }
}
