package com.zetra.econsig.persistence.query.beneficios;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemUltimoDataHistoricoIntegracaoBeneficioQuery</p>
 * <p>Description: Query para listagem do historico de integração beneficio.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemUltimoDataHistoricoIntegracaoBeneficioQuery extends HQuery{

    public String csaCodigo;
    public char hibTipo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select hib.hibCodigo,");
        corpoBuilder.append(" hib.consignataria.csaCodigo,");
        corpoBuilder.append(" hib.usuario.usuCodigo,");
        corpoBuilder.append(" hib.hibDataIni,");
        corpoBuilder.append(" hib.hibDataFim,");
        corpoBuilder.append(" hib.hibData,");
        corpoBuilder.append(" hib.hibTipo");
        corpoBuilder.append(" from HistIntegracaoBeneficio hib");
        corpoBuilder.append(" where 1 = 1");
        corpoBuilder.append(" and hib.hibDataFim = ( select max(hhib.hibDataFim) from HistIntegracaoBeneficio hhib where hhib.hibTipo = :hibTipo and hhib.consignataria.csaCodigo = :csaCodigo )");
        corpoBuilder.append(" and hib.hibTipo = :hibTipo and hib.consignataria.csaCodigo = :csaCodigo");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("hibTipo", String.valueOf(hibTipo), query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.HIB_CODIGO,
                Columns.HIB_CSA_CODIGO,
                Columns.HIB_USU_CODIGO,
                Columns.HIB_DATA_INI,
                Columns.HIB_DATA_FIM,
                Columns.HIB_DATA,
                Columns.HIB_TIPO
        };
    }
}
