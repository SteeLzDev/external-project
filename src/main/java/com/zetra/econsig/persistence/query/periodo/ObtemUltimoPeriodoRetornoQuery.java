package com.zetra.econsig.persistence.query.periodo;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemUltimoPeriodoRetornoQuery</p>
 * <p>Description: Retorna o último periodo que teve retorno.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemUltimoPeriodoRetornoQuery extends HQuery {

    public String orgCodigo;
    public String estCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select max(hcr.hcrPeriodo) ");
        corpoBuilder.append(" from HistoricoConclusaoRetorno hcr ");

        if (!TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" inner join hcr.orgao org ");
        }

        corpoBuilder.append(" where 1=1 ");
        corpoBuilder.append(" and hcr.hcrDesfeito = 'N' ");
        corpoBuilder.append(" and hcr.hcrDataFim is not null ");

        if (!TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" and org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        }
        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" and hcr.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }
        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.HCR_PERIODO
        };
    }
}