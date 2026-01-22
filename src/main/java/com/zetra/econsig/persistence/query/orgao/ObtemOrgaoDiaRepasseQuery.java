package com.zetra.econsig.persistence.query.orgao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemOrgaoDiaRepasseQuery</p>
 * <p>Description: Retorna o órgao com os dias de repasse</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemOrgaoDiaRepasseQuery extends HQuery {
    
    public String orgCodigo;
    public String estCodigo;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        
        corpoBuilder.append("SELECT org.orgCodigo, org.orgDiaRepasse ");
        corpoBuilder.append("FROM Orgao org ");
        corpoBuilder.append("WHERE org.orgDiaRepasse IS NOT NULL ");
        
        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }
        if (!TextHelper.isNull(estCodigo)) {
            corpoBuilder.append(" AND org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        }
        
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        
        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        if (!TextHelper.isNull(estCodigo)) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }
        
        return query;
    }
    
    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ORG_CODIGO,
                Columns.ORG_DIA_REPASSE
        };
    }
}
