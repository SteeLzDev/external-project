package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ListaConsignatariaMesmaVerbaQuery</p>
 * <p>Description: Lista de consignatárias que compartilham o mesmo 
 * código de verba além da consignatária dada pelo parâmetro.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignatariaMesmaVerbaQuery extends HQuery {

    public String csaCodigo;
    public String cnvCodVerba;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select COALESCE(NULLIF(TRIM(csa.csaNomeAbrev), ''), csa.csaNome) as NOME ");
        corpoBuilder.append("from Convenio cnv ");
        corpoBuilder.append("inner join cnv.consignataria csa ");
        corpoBuilder.append("where cnv.cnvCodVerba = :cnvCodVerba ");
        corpoBuilder.append("and csa.csaCodigo != :csaCodigo ");
        corpoBuilder.append("group by COALESCE(NULLIF(TRIM(csa.csaNomeAbrev), ''), csa.csaNome)");
        
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        query.setMaxResults(5);
        
        defineValorClausulaNomeada("cnvCodVerba", cnvCodVerba, query);
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "NOME"
        };
    }
}
