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
 * <p>Title: ObtemDatasPeriodoCalendarioFolhaQuery</p>
 * <p>Description: Retorna as datas do período configuradas no calendário folha.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemDatasPeriodoCalendarioFolhaQuery extends HNativeQuery {

    public List<String> orgCodigos;
    public List<String> estCodigos;
    public Date periodo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select org.org_codigo, org.org_identificador, org.org_nome, est.est_codigo, est.est_identificador, est.est_nome, org.org_codigo as pex_org_codigo, ");
        corpoBuilder.append("   coalesce(coalesce(cfo.cfo_periodo, cfe.cfe_periodo), cfc.cfc_periodo) as pex_periodo, ");
        corpoBuilder.append("   coalesce(coalesce(cfo.cfo_data_ini, cfe.cfe_data_ini), cfc.cfc_data_ini) as pex_data_ini, ");
        corpoBuilder.append("   coalesce(coalesce(cfo.cfo_data_fim, cfe.cfe_data_fim), cfc.cfc_data_fim) as pex_data_fim, ");
        corpoBuilder.append("   coalesce(coalesce(cfo.cfo_dia_corte, cfe.cfe_dia_corte), cfc.cfc_dia_corte) as pex_dia_corte, ");
        corpoBuilder.append("   coalesce(coalesce(cfo.cfo_num_periodo, cfe.cfe_num_periodo), cfc.cfc_num_periodo) as pex_num_periodo ");
        corpoBuilder.append(" from tb_orgao org ");
        corpoBuilder.append(" inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo) ");
        corpoBuilder.append(" inner join tb_consignante cse on (est.cse_codigo = cse.cse_codigo) ");
        corpoBuilder.append(" left outer join tb_calendario_folha_cse cfc on (cfc.cse_codigo = cse.cse_codigo and cfc.cfc_periodo = :periodo) ");
        corpoBuilder.append(" left outer join tb_calendario_folha_est cfe on (cfe.est_codigo = est.est_codigo and cfe.cfe_periodo = :periodo) ");
        corpoBuilder.append(" left outer join tb_calendario_folha_org cfo on (cfo.org_codigo = org.org_codigo and cfo.cfo_periodo = :periodo) ");

        corpoBuilder.append(" where 1=1 ");

        // Um deles deve estar preenchido, evitando que seja retornado um resultado com períodos não preenchidos
        corpoBuilder.append(" and (cfc.cfc_periodo is not null or cfe.cfe_periodo is not null or cfo.cfo_periodo is not null) ");

        if (!TextHelper.isNull(estCodigos)) {
            corpoBuilder.append(" and est.est_codigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }
        if (!TextHelper.isNull(orgCodigos)) {
            corpoBuilder.append(" and org.org_codigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        corpoBuilder.append(" order by org.org_nome");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("periodo", DateHelper.toSQLDate(periodo), query);

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
                Columns.ORG_CODIGO,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_NOME,
                Columns.EST_CODIGO,
                Columns.EST_IDENTIFICADOR,
                Columns.EST_NOME,
                Columns.PEX_ORG_CODIGO,
                Columns.PEX_PERIODO,
                Columns.PEX_DATA_INI,
                Columns.PEX_DATA_FIM,
                Columns.PEX_DIA_CORTE,
                Columns.PEX_NUM_PERIODO
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
