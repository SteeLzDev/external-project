package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

public class ListaCsasComAdeStatusAguardoByRseCodigoQuery extends HQuery {
    
    public String rseCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select DISTINCT csa.csaCodigo, csa.csaNomeAbrev ");
        corpoBuilder.append(" FROM AutDesconto ade ");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv");
        corpoBuilder.append(" INNER JOIN cnv.consignataria csa");
        corpoBuilder.append(" WHERE ade.statusAutorizacaoDesconto.sadCodigo in ('").append(CodedValues.SAD_AGUARD_CONF).append("', '").append(CodedValues.SAD_AGUARD_DEFER).append("') ");
        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" and ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }                
        
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
       return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_NOME_ABREV
        };
    }

}
