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
 * <p>Title: ObtemDatasPeriodoCalendarioBeneficioQuery</p>
 * <p>Description: Query para lista o periodo beneficio cadastrada nas tabelas de calendario beneficio com base em um periodo informado.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemDatasPeriodoCalendarioBeneficioQuery extends HNativeQuery {

    public List<String> orgCodigos;
    public List<String> estCodigos;
    public Date periodo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select org.org_codigo, org.org_identificador, org.org_nome, est.est_codigo, est.est_identificador, est.est_nome, org.org_codigo as pbe_org_codigo, ");
        corpoBuilder.append("   cbc.cbc_periodo as pbe_periodo, ");
        corpoBuilder.append("   cbc.cbc_data_ini as pbe_data_ini, ");
        corpoBuilder.append("   cbc.cbc_data_fim as pbe_data_fim, ");
        corpoBuilder.append("   cbc.cbc_dia_corte as pbe_dia_corte ");
        corpoBuilder.append(" from tb_orgao org ");
        corpoBuilder.append(" inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo) ");
        corpoBuilder.append(" inner join tb_consignante cse on (est.cse_codigo = cse.cse_codigo) ");
        corpoBuilder.append(" inner join tb_calendario_beneficio_cse cbc on (cbc.cse_codigo = cse.cse_codigo and cbc.cbc_periodo = :periodo) ");

        corpoBuilder.append(" where 1=1 ");

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
                Columns.PBE_ORG_CODIGO,
                Columns.PBE_PERIODO,
                Columns.PBE_DATA_INI,
                Columns.PBE_DATA_FIM,
                Columns.PBE_DIA_CORTE
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}