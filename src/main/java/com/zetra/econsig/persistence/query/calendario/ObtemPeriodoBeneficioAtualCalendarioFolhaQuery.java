package com.zetra.econsig.persistence.query.calendario;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemPeriodoBeneficioAtualCalendarioFolhaQuery</p>
 * <p>Description: Query para lista o periodo beneficio cadastrada nas tabelas de calendario beneficio atual</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemPeriodoBeneficioAtualCalendarioFolhaQuery extends HQuery {

    public List<String> orgCodigos;
    public List<String> estCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select org.orgCodigo, ");
        corpoBuilder.append("   org.orgNome, ");
        corpoBuilder.append("   cbc.cbcPeriodo, ");
        corpoBuilder.append("   cbc.cbcDataIni, ");
        corpoBuilder.append("   cbc.cbcDataFim, ");
        corpoBuilder.append("   cbc.cbcDiaCorte ");
        corpoBuilder.append(" from Orgao org ");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        corpoBuilder.append(" inner join est.consignante cse ");
        corpoBuilder.append(" inner join cse.calendarioBeneficioCseSet cbc with current_date() between cbc.cbcDataIni and cbc.cbcDataFim ");
        corpoBuilder.append(" where 1=1 ");

        // Um deles deve estar preenchido, evitando que seja retornado um resultado com períodos não preenchidos
        corpoBuilder.append(" and (cbc.cbcPeriodo is not null) ");

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
                Columns.ORG_CODIGO,
                Columns.ORG_NOME,
                Columns.PBE_PERIODO,
                Columns.PBE_DATA_INI,
                Columns.PBE_DATA_FIM,
                Columns.PBE_DIA_CORTE
        };
    }
}