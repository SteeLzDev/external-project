package com.zetra.econsig.persistence.query.calendario;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemPeriodoAtualCalendarioFolhaQuery</p>
 * <p>Description: Retorna as datas do período atual de lançamento
 * configuradas no calendário folha. A data corrente deve estar
 * entre a data ini e fim do período do calendário folha</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemPeriodoAtualCalendarioFolhaQuery extends HQuery {

    public List<String> orgCodigos;
    public List<String> estCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select org.orgCodigo, ");
        corpoBuilder.append("   coalesce(coalesce(cfo.cfoPeriodo, cfe.cfePeriodo), cfc.cfcPeriodo), ");
        corpoBuilder.append("   coalesce(coalesce(cfo.cfoDataIni, cfe.cfeDataIni), cfc.cfcDataIni), ");
        corpoBuilder.append("   coalesce(coalesce(cfo.cfoDataFim, cfe.cfeDataFim), cfc.cfcDataFim), ");
        corpoBuilder.append("   coalesce(coalesce(cfo.cfoDiaCorte, cfe.cfeDiaCorte), cfc.cfcDiaCorte), ");
        corpoBuilder.append("   coalesce(coalesce(cfo.cfoDataPrevistaRetorno, cfe.cfeDataPrevistaRetorno), cfc.cfcDataPrevistaRetorno), ");
        corpoBuilder.append("   case when cfo.cfoPeriodo is null and cfe.cfePeriodo is null and cfc.cfcPeriodo is not null then 'S' else 'N' end as corte_sistema");
        corpoBuilder.append(" from Orgao org ");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        corpoBuilder.append(" inner join est.consignante cse ");
        corpoBuilder.append(" left outer join cse.calendarioFolhaCseSet cfc with current_date() between cfc.cfcDataIni and cfc.cfcDataFim ");
        corpoBuilder.append(" left outer join est.calendarioFolhaEstSet cfe with current_date() between cfe.cfeDataIni and cfe.cfeDataFim ");
        corpoBuilder.append(" left outer join org.calendarioFolhaOrgSet cfo with current_date() between cfo.cfoDataIni and cfo.cfoDataFim ");
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
                Columns.PEX_ORG_CODIGO,
                Columns.PEX_PERIODO,
                Columns.PEX_DATA_INI,
                Columns.PEX_DATA_FIM,
                Columns.PEX_DIA_CORTE,
                "DATA_PREVISTA_RETORNO",
                "CORTE_SISTEMA"
        };
    }
}
