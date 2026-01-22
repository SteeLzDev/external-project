package com.zetra.econsig.persistence.query.parcela;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemTotalParcelasPagasAtrasadasQuery</p>
 * <p>Description: Obtém o total de parcelas pagas atrasadas na data corrente, com data base
 * anterior à data inicial do contrato.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalParcelasPagasAtrasadasQuery extends HQuery {

    private List<String> orgCodigos;
    private List<String> estCodigos;

    public ObtemTotalParcelasPagasAtrasadasQuery() {
    }

    public ObtemTotalParcelasPagasAtrasadasQuery(List<String> orgCodigos, List<String> estCodigos) {
        this.orgCodigos = orgCodigos;
        this.estCodigos = estCodigos;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select count(*) ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.parcelaDescontoSet prd ");

        if ((estCodigos != null && estCodigos.size() > 0) || (orgCodigos != null && orgCodigos.size() > 0)) {
            corpoBuilder.append("inner join ade.verbaConvenio vco ");
            corpoBuilder.append("inner join vco.convenio cnv ");
            corpoBuilder.append("inner join cnv.orgao org ");
        }

        corpoBuilder.append("where prd.prdDataDesconto < ade.adeAnoMesIni ");
        corpoBuilder.append("and prd.prdDataRealizado = current_date() ");
        corpoBuilder.append("and prd.statusParcelaDesconto.spdCodigo = '");
        corpoBuilder.append(CodedValues.SPD_LIQUIDADAFOLHA).append("' ");

        if (estCodigos != null && estCodigos.size() > 0) {
            corpoBuilder.append(" and org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (estCodigos != null && estCodigos.size() > 0) {
            defineValorClausulaNomeada("estCodigo", estCodigos, query);
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "QTD"
        };
    }
}
