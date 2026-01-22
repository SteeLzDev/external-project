package com.zetra.econsig.persistence.query.calendario;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemPeriodoAposPrazoCalendarioFolhaQuery</p>
 * <p>Description: Retorna as datas do período atual de lançamento
 * configuradas no calendário folha. A data corrente deve estar
 * entre a data ini e fim do período do calendário folha</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemPeriodoAposPrazoCalendarioFolhaQuery extends HNativeQuery {

    public List<String> orgCodigos;
    public String orgCodigo;
    public Date periodoInicial;
    public Integer qtdPeriodos;
    public boolean ignoraPeriodosAgrupados;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select to_date(max(coalesce(coalesce(cfo.cfo_periodo, cfe.cfe_periodo), cfc.cfc_periodo)))");
        corpoBuilder.append(" from tb_orgao org");
        corpoBuilder.append(" inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo)");
        corpoBuilder.append(" inner join tb_consignante cse on (est.cse_codigo = cse.cse_codigo)");

        corpoBuilder.append(" left outer join tb_calendario_folha_cse cfc on (cse.cse_codigo = cfc.cse_codigo)");

        corpoBuilder.append(" left outer join tb_calendario_folha_est cfe on (est.est_codigo = cfe.est_codigo");
        corpoBuilder.append(" and (cfc.cfc_periodo is null or cfc.cfc_periodo = cfe.cfe_periodo)");
        corpoBuilder.append(" )");

        corpoBuilder.append(" left outer join tb_calendario_folha_org cfo on (org.org_codigo = cfo.org_codigo");
        corpoBuilder.append(" and (cfc.cfc_periodo is null or cfc.cfc_periodo = cfo.cfo_periodo)");
        corpoBuilder.append(" and (cfe.cfe_periodo is null or cfe.cfe_periodo = cfo.cfo_periodo)");
        corpoBuilder.append(" )");

        corpoBuilder.append(" where 1=1");

        corpoBuilder.append(" and (coalesce(coalesce(cfo.cfo_periodo, cfe.cfe_periodo), cfc.cfc_periodo) ");
        corpoBuilder.append((qtdPeriodos >= 0) ? ">=" : "<=").append(" :periodoInicial)");

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" and org.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        } else if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" and org.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (ignoraPeriodosAgrupados) {
            // Períodos agrupados tem data inicial igual a data final
            corpoBuilder.append(" and coalesce(coalesce(cfo.cfo_data_ini, cfe.cfe_data_ini), cfc.cfc_data_ini) <> coalesce(coalesce(cfo.cfo_data_fim, cfe.cfe_data_fim), cfc.cfc_data_fim) ");
        }

        corpoBuilder.append(" group by coalesce(coalesce(cfo.cfo_periodo, cfe.cfe_periodo), cfc.cfc_periodo)");
        corpoBuilder.append(" order by 1").append((qtdPeriodos >= 0) ? " asc" : " desc");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("periodoInicial", DateHelper.toSQLDate(periodoInicial), query);

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        } else if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        query.setFirstResult(Math.abs(qtdPeriodos));
        query.setMaxResults(1);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PEX_PERIODO,
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
