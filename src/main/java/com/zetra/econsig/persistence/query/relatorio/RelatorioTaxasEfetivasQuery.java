package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioTaxasEfetivasQuery</p>
 * <p>Description: Consulta de relatório de Taxa Efetivas.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioTaxasEfetivasQuery extends ReportHQuery {

    public String periodo;
    public List<String> orgCodigos;
    public String csaCodigo;
    public List<String> svcCodigos;
    public List<String> sadCodigos;
    public List<Integer> prazosInformados;
    public boolean prazoMultiploDoze = false;

    public RelatorioTaxasEfetivasQuery() {
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        periodo = (String) criterio.getAttribute("periodo");
        orgCodigos = (List<String>) criterio.getAttribute("orgCodigo");
        svcCodigos = (List<String>) criterio.getAttribute("svcCodigos");
        sadCodigos = (List<String>) criterio.getAttribute("sadCodigos");
        prazoMultiploDoze = (Boolean) criterio.getAttribute("prazoMultiploDoze");
        prazosInformados = (List<Integer>) criterio.getAttribute("prazosInformados");
        csaCodigo = (String) criterio.getAttribute("csaCodigo");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder sql = new StringBuilder();
        sql.append("select csa.csaCodigo as csa_codigo, csa.csaNomeAbrev as csa_nome_abrev, csa.csaNome as csa_nome, ");
        sql.append("to_decimal(sum(case when coalesce(ade.adeAnoMesIniRef, ade.adeAnoMesIni) = :periodo then 1 else 0 end), 13, 2) as qtde_mes, ");
        sql.append("to_decimal(sum(case when coalesce(ade.adeAnoMesIniRef, ade.adeAnoMesIni) = :periodo then coalesce(cde.cdeVlrLiberado, coalesce(ade.adeVlrLiquido, ade.adeVlr * coalesce(ade.adePrazo, 1))) else 0.00 end), 13, 2) as valor_mes, ");
        sql.append("to_decimal(count(*), 13, 2) as qtde_total, ");
        sql.append("to_decimal(sum(coalesce(cde.cdeVlrLiberado, coalesce(ade.adeVlrLiquido, ade.adeVlr * coalesce(ade.adePrazo, 1)))), 13, 2) as valor_total ");
        sql.append(" from AutDesconto ade");
        sql.append(" inner join ade.verbaConvenio vco");
        sql.append(" inner join vco.convenio cnv");
        sql.append(" inner join cnv.consignataria csa");
        sql.append(" left outer join ade.coeficienteDescontoSet cde");

        sql.append(" where 1=1 ");
        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            sql.append(" and cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if ((svcCodigos != null) && (svcCodigos.size() > 0)) {
            sql.append(" and cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigos", svcCodigos));
        }

        if ((sadCodigos != null) && (sadCodigos.size() > 0)) {
            sql.append(" and ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        }

        if (prazoMultiploDoze) {
            sql.append(" and MOD(ade.adePrazo,12) = 0 ");
        }

        if ((prazosInformados != null) && (prazosInformados.size() > 0)) {
            sql.append(" and ade.adePrazo ").append(criaClausulaNomeada("prazosInformados", prazosInformados));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            sql.append(" and cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        sql.append(" group by csa.csaCodigo, csa.csaNomeAbrev, csa.csaNome order by 6 desc, 7 desc");

        final Query<Object[]> query = instanciarQuery(session, sql.toString());

        if (!TextHelper.isNull(periodo)) {
            defineValorClausulaNomeada("periodo", parseDateString(periodo), query);
        }

        if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if ((svcCodigos != null) && !svcCodigos.isEmpty()) {
            defineValorClausulaNomeada("svcCodigos", svcCodigos, query);
        }
        if ((sadCodigos != null) && !sadCodigos.isEmpty()) {
            defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        }
        if ((prazosInformados != null) && !prazosInformados.isEmpty()) {
            defineValorClausulaNomeada("prazosInformados", prazosInformados, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.CSA_CODIGO,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_NOME,
                "qtde_mes",
                "valor_mes",
                "qtde_total",
                "valor_total"
        };
    }
}
