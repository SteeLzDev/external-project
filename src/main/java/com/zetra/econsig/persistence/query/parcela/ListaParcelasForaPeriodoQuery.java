package com.zetra.econsig.persistence.query.parcela;

import java.util.Date;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListaParcelasForaPeriodoQuery</p>
 * <p>Description: Lista os períodos que possuem parcelas em processamento seja diferente
 * do periodo de exportação atual. Caso existam, indica uma inconsistência que deve ser
 * reportada.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParcelasForaPeriodoQuery extends HQuery {

    private final List<String> orgCodigos;
    private final List<String> estCodigos;
    private Date periodo;

    public ListaParcelasForaPeriodoQuery(List<String> orgCodigos, List<String> estCodigos) {
        this.orgCodigos = orgCodigos;
        this.estCodigos = estCodigos;
    }

    public ListaParcelasForaPeriodoQuery(List<String> orgCodigos, List<String> estCodigos, Date periodo) {
        this.orgCodigos = orgCodigos;
        this.estCodigos = estCodigos;
        this.periodo = periodo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select prd.prdDataDesconto, count(*) ");
        corpoBuilder.append("from ParcelaDescontoPeriodo prd ");
        corpoBuilder.append("inner join prd.autDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.orgao org ");
        corpoBuilder.append("where prd.statusParcelaDesconto.spdCodigo = '").append(CodedValues.SPD_EMPROCESSAMENTO).append("' ");

        if (estCodigos != null && estCodigos.size() > 0) {
            corpoBuilder.append(" and org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (periodo != null) {
            corpoBuilder.append(" and prd.prdDataDesconto <> :periodo");
        } else {
            corpoBuilder.append(" and not exists (select 1 from org.periodoExportacaoSet pex ");
            corpoBuilder.append("where prd.prdDataDesconto = pex.pexPeriodo ");
            corpoBuilder.append(")");
        }

        corpoBuilder.append(" group by prd.prdDataDesconto");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (estCodigos != null && estCodigos.size() > 0) {
            defineValorClausulaNomeada("estCodigo", estCodigos, query);
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }
        if (periodo != null) {
            defineValorClausulaNomeada("periodo", periodo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "PERIODO",
                "QTD"
        };
    }
}
