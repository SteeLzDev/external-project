package com.zetra.econsig.persistence.query.relatorio;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p> Title: RelatorioGeralInadimplenciaQuery</p>
 * <p> Description: Relatório de inadimplência.</p>
 * <p> Copyright: Copyright (c) 2013 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class RelatorioGerencialInadimplenciaQuery extends ReportHNativeQuery{
    public Date periodo = null;

    public AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {
        periodo = (Date) criterio.getAttribute("PERIODO");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder where = new StringBuilder("where 1 = 1 ");

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select * from ");

        // TOTAL CARTEIRA
        corpoBuilder.append("(select sum(ade_vlr * (ade_prazo - coalesce(ade_prd_pagas, 0))) as SUM_TOTAL_CARTEIRA, count(*) as COUNT_TOTAL_CARTEIRA ");
        corpoBuilder.append("from tb_registro_servidor rse ");
        corpoBuilder.append("inner join tb_aut_desconto ade on (rse.rse_codigo = ade.rse_codigo) ");
        corpoBuilder.append("inner join tb_parcela_desconto prd on (ade.ade_codigo = prd.ade_codigo) ");
        corpoBuilder.append("where prd_data_desconto = :periodo ");
        corpoBuilder.append("and srs_codigo in ('").append(CodedValues.SRS_ATIVO).append("', '").append(CodedValues.SRS_BLOQUEADO).append("')) total_carteira, ");

        // INADIMPLENCIA TOTAL
        corpoBuilder.append("(select sum(ade_vlr * (ade_prazo - coalesce(ade_prd_pagas, 0))) as SUM_INADIMPLENCIA_TOTAL, count(*) as COUNT_INADIMPLENCIA_TOTAL ");
        corpoBuilder.append("from tb_registro_servidor rse ");
        corpoBuilder.append("inner join tb_aut_desconto ade on (rse.rse_codigo = ade.rse_codigo) ");
        corpoBuilder.append("inner join tb_parcela_desconto prd on (ade.ade_codigo = prd.ade_codigo) ");
        corpoBuilder.append("where prd_data_desconto = :periodo ");
        corpoBuilder.append("and spd_codigo = '").append(CodedValues.SPD_REJEITADAFOLHA).append("' ");
        corpoBuilder.append("and srs_codigo in ('").append(CodedValues.SRS_ATIVO).append("', '").append(CodedValues.SRS_BLOQUEADO).append("')) inadimplencia_total, ");

        // TOTAL CARTEIRA EMPRESTIMO
        corpoBuilder.append("(select sum(ade_vlr * (ade_prazo - coalesce(ade_prd_pagas, 0))) as SUM_TOTAL_CARTEIRA_EMP, count(ade.ade_codigo) as COUNT_TOTAL_CARTEIRA_EMP ");
        corpoBuilder.append("from tb_registro_servidor rse ");
        corpoBuilder.append("inner join tb_aut_desconto ade on (rse.rse_codigo = ade.rse_codigo) ");
        corpoBuilder.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        corpoBuilder.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        corpoBuilder.append("inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) ");
        corpoBuilder.append("inner join tb_parcela_desconto prd on (ade.ade_codigo = prd.ade_codigo) ");
        corpoBuilder.append("where prd_data_desconto = :periodo ");
        corpoBuilder.append("and NSE_CODIGO = '").append(CodedValues.NSE_EMPRESTIMO).append("' ");
        corpoBuilder.append("and srs_codigo in ('").append(CodedValues.SRS_ATIVO).append("', '").append(CodedValues.SRS_BLOQUEADO).append("')) total_carteira_emprestimo, ");

        // INADIMPLENCIA EMPRESTIMO
        corpoBuilder.append("(select sum(ade_vlr * (ade_prazo - coalesce(ade_prd_pagas, 0))) as SUM_INADIMPLENCIA_EMP, count(ade.ade_codigo) as COUNT_INADIMPLENCIA_EMP ");
        corpoBuilder.append("from tb_registro_servidor rse ");
        corpoBuilder.append("inner join tb_aut_desconto ade on (rse.rse_codigo = ade.rse_codigo) ");
        corpoBuilder.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        corpoBuilder.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        corpoBuilder.append("inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) ");
        corpoBuilder.append("inner join tb_parcela_desconto prd on (ade.ade_codigo = prd.ade_codigo) ");
        corpoBuilder.append("where prd_data_desconto = :periodo ");
        corpoBuilder.append("and spd_codigo = '").append(CodedValues.SPD_REJEITADAFOLHA).append("' ");
        corpoBuilder.append("and srs_codigo in ('").append(CodedValues.SRS_ATIVO).append("', '").append(CodedValues.SRS_BLOQUEADO).append("') ");
        corpoBuilder.append("and NSE_CODIGO = '").append(CodedValues.NSE_EMPRESTIMO).append("') inadimplencia_emprestimo ");

        corpoBuilder.append(where);

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("periodo", periodo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "SUM_TOTAL_CARTEIRA",
                "COUNT_TOTAL_CARTEIRA",
                "SUM_INADIMPLENCIA_TOTAL",
                "COUNT_INADIMPLENCIA_TOTAL",
                "SUM_TOTAL_CARTEIRA_EMPRESTIMO",
                "COUNT_TOTAL_CARTEIRA_EMPRESTIMO",
                "SUM_INADIMPLENCIA_EMPRESTIMO",
                "COUNT_INADIMPLENCIA_EMPRESTIMO"
        };
    }
}


