package com.zetra.econsig.persistence.query.periodo;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaPeriodoBeneficioQuery</p>
 * <p>Description: Lista o periodo cadastrado na tb_periodo_beneficio</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPeriodoBeneficioQuery extends HQuery {

    public List<String> orgCodigos;
    public List<String> estCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "select " +
                "org.orgCodigo, " +
                "org.orgIdentificador, " +
                "org.orgNome, " +
                "est.estCodigo, " +
                "est.estIdentificador, " +
                "est.estNome, " +
                "pbe.orgCodigo, " +
                "pbe.pbeDiaCorte, " +
                "pbe.pbeDataIni, " +
                "pbe.pbeDataFim, " +
                "pbe.pbePeriodo ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from PeriodoBeneficio pbe");
        corpoBuilder.append(" inner join pbe.orgao org");
        corpoBuilder.append(" inner join org.estabelecimento est");

        corpoBuilder.append(" where 1=1");

        if (estCodigos != null && estCodigos.size() > 0) {
            corpoBuilder.append(" and org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            corpoBuilder.append(" and pbe.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (estCodigos != null && estCodigos.size() > 0) {
            defineValorClausulaNomeada("estCodigo", estCodigos, query);
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
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
                Columns.PBE_DIA_CORTE,
                Columns.PBE_DATA_INI,
                Columns.PBE_DATA_FIM,
                Columns.PBE_PERIODO
        };
    }
}