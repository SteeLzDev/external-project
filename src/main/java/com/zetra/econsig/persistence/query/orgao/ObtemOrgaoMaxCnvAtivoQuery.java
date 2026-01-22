package com.zetra.econsig.persistence.query.orgao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemOrgaoMaxCnvAtivoQuery</p>
 * <p>Description: Retorna o órgao com maior número de convênios ativos</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemOrgaoMaxCnvAtivoQuery extends HQuery {

    public String estCodigo;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        
        corpoBuilder.append("select org.orgCodigo ");
        corpoBuilder.append("from Convenio cnv ");
        corpoBuilder.append("inner join cnv.orgao org ");
        corpoBuilder.append("where cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        if (!TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" AND org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        }
        corpoBuilder.append(" GROUP BY org.orgCodigo ");
        corpoBuilder.append(" ORDER BY COUNT(*) DESC ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        query.setMaxResults(1);
        
        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }
        
        return query;
    }
    
    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ORG_CODIGO
        };
    }
}
