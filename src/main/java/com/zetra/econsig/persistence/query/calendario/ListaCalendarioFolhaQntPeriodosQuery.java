package com.zetra.econsig.persistence.query.calendario;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;

/**
 * <p>Title: ListaCalendarioFolhaCseQuery</p>
 * <p>Description: Lista os registros de calendário da folha geral.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCalendarioFolhaQntPeriodosQuery extends HNativeQuery {

    public Date dataIni;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" SELECT 'CSE' as ENTIDADE, cse.cse_identificador as CODIGO_ENTIDADE, add_month(cfc_max.PERIODO,1) as PERIODO, cfc_max.CORTE, COUNT(*) as QUANTIDADE ");
        corpoBuilder.append(" FROM tb_calendario_folha_cse cfc ");
        corpoBuilder.append(" INNER JOIN tb_consignante cse ON (cse.cse_codigo = cfc.cse_codigo) ");
        corpoBuilder.append(" INNER JOIN ( ");
        corpoBuilder.append("     SELECT cfc1.cse_codigo, MAX(cfc1.cfc_periodo) as PERIODO, ");
        corpoBuilder.append("     MAX(cfc1.cfc_dia_corte) as CORTE ");
        corpoBuilder.append("     FROM tb_calendario_folha_cse cfc1 ");
        corpoBuilder.append("     WHERE cfc1.cfc_data_ini >= :dataIni ");
        corpoBuilder.append("     GROUP BY cfc1.cse_codigo ");
        corpoBuilder.append(" ) cfc_max ON (cfc_max.cse_codigo = cfc.cse_codigo) ");
        corpoBuilder.append(" WHERE cfc.cfc_data_ini >= :dataIni ");
        corpoBuilder.append(" GROUP BY cse.cse_identificador, cfc_max.PERIODO, cfc_max.CORTE ");
        corpoBuilder.append(" UNION ALL ");
        corpoBuilder.append(" SELECT 'ORG' as ENTIDADE, org.org_identificador as CODIGO_ENTIDADE, add_month(cfo_max.PERIODO,1) as PERIODO, cfo_max.CORTE, COUNT(*) as QUANTIDADE ");
        corpoBuilder.append(" FROM tb_calendario_folha_org cfo ");
        corpoBuilder.append(" INNER JOIN tb_orgao org ON (org.org_codigo = cfo.org_codigo) ");
        corpoBuilder.append(" INNER JOIN ( ");
        corpoBuilder.append("     SELECT cfo1.org_codigo, MAX(cfo1.cfo_periodo) as PERIODO, ");
        corpoBuilder.append("     MAX(cfo1.cfo_dia_corte) as CORTE ");
        corpoBuilder.append("     FROM tb_calendario_folha_org cfo1 ");
        corpoBuilder.append("     WHERE cfo1.cfo_data_ini >= :dataIni ");
        corpoBuilder.append("     GROUP BY cfo1.org_codigo ");
        corpoBuilder.append(" ) cfo_max ON (cfo_max.org_codigo = cfo.org_codigo) ");
        corpoBuilder.append(" WHERE cfo.cfo_data_ini >= :dataIni ");
        corpoBuilder.append(" GROUP BY org.org_identificador, cfo_max.PERIODO, cfo_max.CORTE ");
        corpoBuilder.append(" UNION ALL ");
        corpoBuilder.append(" SELECT 'EST' as ENTIDADE, est.est_identificador as CODIGO_ENTIDADE, add_month(cfe_max.PERIODO,1) as PERIODO, cfe_max.CORTE, COUNT(*) as QUANTIDADE ");
        corpoBuilder.append(" FROM tb_calendario_folha_est cfe ");
        corpoBuilder.append(" INNER JOIN tb_estabelecimento est ON (est.est_codigo = cfe.est_codigo) ");
        corpoBuilder.append(" INNER JOIN ( ");
        corpoBuilder.append("     SELECT cfe1.est_codigo, MAX(cfe1.cfe_periodo) as PERIODO, ");
        corpoBuilder.append("     MAX(cfe1.cfe_dia_corte) as CORTE ");
        corpoBuilder.append("     FROM tb_calendario_folha_est cfe1 ");
        corpoBuilder.append("     WHERE cfe1.cfe_data_ini >= :dataIni ");
        corpoBuilder.append("     GROUP BY cfe1.est_codigo ");
        corpoBuilder.append(" ) cfe_max ON (cfe_max.est_codigo = cfe.est_codigo) ");
        corpoBuilder.append(" WHERE cfe.cfe_data_ini >= :dataIni ");
        corpoBuilder.append(" GROUP BY est.est_identificador, cfe_max.PERIODO, cfe_max.CORTE ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("dataIni", dataIni, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "ENTIDADE",
                "CODIGO_ENTIDADE",
                "PERIODO",
                "CORTE",
                "QUANTIDADE"
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
