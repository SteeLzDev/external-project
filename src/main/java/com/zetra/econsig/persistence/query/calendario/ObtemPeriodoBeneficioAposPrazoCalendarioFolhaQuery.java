package com.zetra.econsig.persistence.query.calendario;

import java.util.Date;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.Columns;


/**
 * <p>Title: ObtemPeriodoBeneficioAposPrazoCalendarioFolhaQuery</p>
 * <p>Description: Retorna as datas do período beneficio atual de lançamento
 * configuradas no calendário folha. A data corrente deve estar
 * entre a data ini e fim do período do calendário folha</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemPeriodoBeneficioAposPrazoCalendarioFolhaQuery extends HNativeQuery {

    public List<String> orgCodigos;
    public String orgCodigo;
    public Date periodoInicial;
    public Integer qtdPeriodos;
    public boolean ignoraPeriodosAgrupados;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select to_date(max(cbc.cbc_periodo))");
        corpoBuilder.append(" from tb_orgao org");
        corpoBuilder.append(" inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo)");
        corpoBuilder.append(" inner join tb_consignante cse on (est.cse_codigo = cse.cse_codigo)");

        corpoBuilder.append(" left outer join tb_calendario_beneficio_cse cbc on (cse.cse_codigo = cbc.cse_codigo)");

        corpoBuilder.append(" where 1=1");

        corpoBuilder.append(" and (cbc.cbc_periodo ");
        corpoBuilder.append((qtdPeriodos >= 0) ? ">=" : "<=").append(" :periodoInicial)");

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" and org.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        } else if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" and org.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        if (ignoraPeriodosAgrupados) {
            // Períodos agrupados tem data inicial igual a data final
            corpoBuilder.append(" and cbc.cbc_data_ini <> cbc.cbc_data_fim ");
        }

        corpoBuilder.append(" group by");
        corpoBuilder.append(" year(cbc.cbc_periodo),");
        corpoBuilder.append(" month(cbc.cbc_periodo)");

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