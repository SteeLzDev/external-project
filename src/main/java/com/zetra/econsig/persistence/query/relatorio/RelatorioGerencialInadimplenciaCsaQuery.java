package com.zetra.econsig.persistence.query.relatorio;

import java.util.Date;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;

/**
 * <p> Title: RelatorioGeralInadimplenciaCsaQuery</p>
 * <p> Description: Relatório de inadimplência por Csa.</p>
 * <p> Copyright: Copyright (c) 2013 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class RelatorioGerencialInadimplenciaCsaQuery extends ReportHNativeQuery{
    public Date periodo = null;
    public List<String> csaCodigo = null;
    public AcessoSistema responsavel;

    @Override
    public void setCriterios(TransferObject criterio) {
        periodo = (Date) criterio.getAttribute("PERIODO");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        periodo = DateHelper.toPeriodDate(periodo);

        StringBuilder where = new StringBuilder("where 1 = 1 ");

        StringBuilder corpoBuilder = new StringBuilder();

        // INADIMPLENCIA POR CSA
        corpoBuilder.append("select CSA_NOME, CSA_NOME_ABREV, count(distinct ade.ade_codigo) CSA_QTDE_INADIMPLENCIA, sum(ade_vlr * (ade_prazo - coalesce(ade_prd_pagas, 0))) CSA_SUM_INADIMPLENCIA, sum(ade_vlr) CSA_SUM_ADE_VLR ");
        corpoBuilder.append("from tb_registro_servidor rse ");
        corpoBuilder.append("inner join tb_aut_desconto ade on (rse.rse_codigo = ade.rse_codigo) ");
        corpoBuilder.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        corpoBuilder.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        corpoBuilder.append("inner join tb_servico svc on (cnv.svc_codigo = svc.svc_codigo) ");
        corpoBuilder.append("inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) ");
        corpoBuilder.append("inner join tb_parcela_desconto prd on (ade.ade_codigo = prd.ade_codigo) ");

        if (csaCodigo != null && !csaCodigo.isEmpty()) {
            where.append(" and cnv.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo)).append(" ");
        }

        where.append("and prd_data_desconto = :periodo ");
        where.append("and spd_codigo = '5' ");
        where.append("and srs_codigo = '1' ");
        where.append("group by csa_nome,csa_nome_abrev ");
        where.append("order by CSA_QTDE_INADIMPLENCIA desc ");

        corpoBuilder.append(where);

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        query.setMaxResults(10);

        if (csaCodigo != null && !csaCodigo.isEmpty()) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        defineValorClausulaNomeada("periodo", periodo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "CSA_NOME",
                "CSA_NOME_ABREV",
                "CSA_QTDE_INADIMPLENCIA",
                "CSA_SUM_INADIMPLENCIA",
                "CSA_SUM_ADE_VLR"
        };
    }
}


