package com.zetra.econsig.persistence.query.regraconvenio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

public class BuscaRegrasConvenioByCsaQuery extends HQuery {
	
	public String csaCodigo;
	
	@Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
    	
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select rco.rcoCodigo, rco.rcoCampoCodigo, rco.rcoCampoNome, rco.rcoCampoValor, rco.csaCodigo, rco.svcCodigo, rco.orgCodigo, rco.marCodigo ");
        corpoBuilder.append(" from RegraConvenio rco ");
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" where rco.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        corpoBuilder.append(" order by rco.rcoCampoNome ");
        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.RCO_CODIGO,
                Columns.RCO_CAMPO_CODIGO,
                Columns.RCO_CAMPO_NOME,
                Columns.RCO_CAMPO_VALOR,
                Columns.RCO_CSA_CODIGO,
                Columns.RCO_SVC_CODIGO,
                Columns.RCO_ORG_CODIGO,
                Columns.RCO_MAR_CODIGO
        };
    }	
}
