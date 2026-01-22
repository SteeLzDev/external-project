package com.zetra.econsig.persistence.query.calendario;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ObtemUltimoPeriodoCalendarioFolhaQuery</p>
 * <p>Description: Retorna a data base do maior período do calendário folha, onde
 * a data final é menor que a data atual.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemUltimoPeriodoCalendarioFolhaQuery extends HQuery {

    public List<String> orgCodigos;
    public List<String> estCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select max(coalesce(coalesce(cfo.cfoPeriodo, cfe.cfePeriodo), cfc.cfcPeriodo)) ");
        corpoBuilder.append(" from Orgao org ");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        corpoBuilder.append(" inner join est.consignante cse ");
        corpoBuilder.append(" left outer join cse.calendarioFolhaCseSet cfc with cfc.cfcDataFim < current_date() ");
        corpoBuilder.append(" left outer join est.calendarioFolhaEstSet cfe with cfe.cfeDataFim < current_date() ");
        corpoBuilder.append(" left outer join org.calendarioFolhaOrgSet cfo with cfo.cfoDataFim < current_date() ");
        corpoBuilder.append(" where 1=1 ");

        // Um deles deve estar preenchido, evitando que seja retornado um resultado com períodos não preenchidos
        corpoBuilder.append(" and (cfc.cfcPeriodo is not null or cfe.cfePeriodo is not null or cfo.cfoPeriodo is not null) ");

        if (!TextHelper.isNull(estCodigos)) {
            corpoBuilder.append(" and org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }
        if (!TextHelper.isNull(orgCodigos)) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(estCodigos)) {
            defineValorClausulaNomeada("estCodigo", estCodigos, query);
        }
        if (!TextHelper.isNull(orgCodigos)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "PERIODO"
        };
    }
}
