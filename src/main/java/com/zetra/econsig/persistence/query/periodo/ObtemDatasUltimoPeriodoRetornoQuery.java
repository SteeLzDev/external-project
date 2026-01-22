package com.zetra.econsig.persistence.query.periodo;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemDatasUltimoPeriodoRetornoQuery</p>
 * <p>Description: Retorna a data inicial e final do período de exportação, relativo
 * ao último periodo que teve retorno.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemDatasUltimoPeriodoRetornoQuery extends HQuery {

    public String orgCodigo;
    public String estCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select hie.hiePeriodo, hie.hieDataIni, hie.hieDataFim ");
        corpoBuilder.append(" from Orgao org ");
        corpoBuilder.append(" inner join org.historicoExportacaoSet hie ");
        corpoBuilder.append(" inner join org.historicoConclusaoRetornoSet hcr ");
        corpoBuilder.append(" where hcr.hcrPeriodo = hie.hiePeriodo ");

        if (!TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" and org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        }
        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        corpoBuilder.append(" order by hie.hiePeriodo desc ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        query.setMaxResults(1);

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
                Columns.HIE_PERIODO,
                Columns.HIE_DATA_INI,
                Columns.HIE_DATA_FIM
        };
    }
}