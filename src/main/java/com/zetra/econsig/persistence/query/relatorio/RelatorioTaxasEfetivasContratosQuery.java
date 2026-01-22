package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioTaxasEfetivasContratosQuery</p>
 * <p>Description: Consulta de relatório de Taxa Efetivas.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioTaxasEfetivasContratosQuery extends HQuery {

    public String periodo;
    public String orgCodigo;
    public String csaCodigo;
    public List<String> svcCodigos;
    public List<String> sadCodigos;
    public List<Integer> prazosInformados;
    public boolean prazoMultiploDoze = false;

    public RelatorioTaxasEfetivasContratosQuery() {
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder sql = new StringBuilder();

        sql.append("select cnv.consignataria.csaCodigo, cnv.servico.svcCodigo, cnv.orgao.orgCodigo, ade.adeCodigo, ade.adeNumero, ");
        sql.append("ade.adePrazo, ade.adeVlr, ade.adeVlrLiquido, ade.adeVlrTac, ade.adeVlrIof, ");
        sql.append("ade.adeAnoMesIni, ade.adeData, cde.cdeVlrLiberado, ade.adePeriodicidade ");
        sql.append(" from AutDesconto ade");
        sql.append(" inner join ade.verbaConvenio vco");
        sql.append(" inner join vco.convenio cnv");
        sql.append(" left outer join ade.coeficienteDescontoSet cde");

        sql.append(" where ade.adeAnoMesIni = :periodo ");
        if (!TextHelper.isNull(orgCodigo)) {
            sql.append(" and cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
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

        sql.append(" order by cnv.consignataria.csaCodigo, cnv.servico.svcCodigo");

        final Query<Object[]> query = instanciarQuery(session, sql.toString());

        if (!TextHelper.isNull(periodo)) {
            defineValorClausulaNomeada("periodo", parseDateString(periodo), query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
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
                Columns.SVC_CODIGO,
                Columns.ORG_CODIGO,
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_PRAZO,
                Columns.ADE_VLR,
                Columns.ADE_VLR_LIQUIDO,
                Columns.ADE_VLR_TAC,
                Columns.ADE_VLR_IOF,
                Columns.ADE_ANO_MES_INI,
                Columns.ADE_DATA,
                Columns.CDE_VLR_LIBERADO,
                Columns.ADE_PERIODICIDADE
        };
    }
}
