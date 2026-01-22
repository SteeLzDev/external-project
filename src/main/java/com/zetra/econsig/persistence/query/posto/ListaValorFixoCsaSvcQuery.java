package com.zetra.econsig.persistence.query.posto;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class ListaValorFixoCsaSvcQuery extends HQuery {

    public String csaCodigo;
    public String svcCodigo;
    public String posCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String build = "select posCodigo," +
                " csaCodigo," +
                " ppoVlr";

        StringBuilder corpoBuild = new StringBuilder(build);
        corpoBuild.append(" from ParamPostoCsaSvc");
        corpoBuild.append(" where csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuild.append(" and svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));

        if (posCodigo != null && !posCodigo.isEmpty()) {
            corpoBuild.append(" and posCodigo ").append(criaClausulaNomeada("posCodigo", posCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuild.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        if (posCodigo != null && !posCodigo.isEmpty()) {
            defineValorClausulaNomeada("posCodigo", posCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.PSP_POS_CODIGO,
                Columns.PSP_CSA_CODIGO,
                Columns.PSP_PPO_VALOR
        };
    }
}
