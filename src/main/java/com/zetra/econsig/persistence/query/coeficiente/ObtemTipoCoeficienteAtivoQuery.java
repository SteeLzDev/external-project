package com.zetra.econsig.persistence.query.coeficiente;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ObtemTipoCoeficienteAtivoQuery</p>
 * <p>Description: Retorna o tipo de coeficiente que está ativo para um serviço 
 *    de uma consignatária.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTipoCoeficienteAtivoQuery extends HQuery {

    public String csaCodigo;
    public String svcCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder("SELECT DISTINCT CASE WHEN cft.cftDia > 0 THEN 'D' ELSE 'M' END AS TIPO ");

        corpoBuilder.append(" FROM Prazo prz");
        corpoBuilder.append(" INNER JOIN prz.prazoConsignatariaSet pzc");
        corpoBuilder.append(" INNER JOIN pzc.coeficienteAtivoSet cft");
        corpoBuilder.append(" WHERE prz.przAtivo = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append(" AND pzc.przCsaAtivo = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append(" AND cft.cftDataIniVig <= current_date() ");
        corpoBuilder.append(" AND (cft.cftDataFimVig >= current_date() OR cft.cftDataFimVig IS NULL) ");
        corpoBuilder.append(" AND pzc.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" AND prz.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));

        // Define os valores para os parâmetros nomeados
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {        
        return new String[] {
                "TIPO"
        };
    }
}
