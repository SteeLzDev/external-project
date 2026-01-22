package com.zetra.econsig.persistence.query.menu;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

public class ListaMenuQuery extends HQuery {

    public String mnuCodigo;
    public Short mnuSequencia;
    public Short mnuAtivo;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "select " +
                       "mnu.mnuCodigo, " +
                       "mnu.mnuSequencia, " +
                       "mnu.mnuDescricao, " +
                       "mnu.mnuAtivo ";
        
        StringBuilder corpoBuilder = new StringBuilder(corpo);        
        corpoBuilder.append(" from Menu mnu ");
        corpoBuilder.append(" where 1=1 ");  
        
        if (!TextHelper.isNull(mnuCodigo)) {
            corpoBuilder.append(" and mnu.mnuCodigo ").append(criaClausulaNomeada("mnuCodigo", mnuCodigo));
        }
        
        if (mnuSequencia != null) {
            corpoBuilder.append(" and mnu.mnuSequencia ").append(criaClausulaNomeada("mnuSequencia", mnuSequencia));
        }
        
        if (mnuAtivo != null) {
            corpoBuilder.append(" and mnu.mnuAtivo ").append(criaClausulaNomeada("mnuAtivo", mnuAtivo));
        }
        
        corpoBuilder.append(" order by mnu.mnuSequencia ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(mnuCodigo)) {
            defineValorClausulaNomeada("mnuCodigo", mnuCodigo, query);
        }
        
        if (mnuSequencia != null) {
            defineValorClausulaNomeada("mnuSequencia", mnuSequencia, query);
        }
        
        if (mnuAtivo != null) {
            defineValorClausulaNomeada("mnuAtivo", mnuAtivo, query);
        }
        
        return query;
    }

    protected String[] getFields() {        
        return new String[] {
                Columns.MNU_CODIGO,
                Columns.MNU_SEQUENCIA,
                Columns.MNU_DESCRICAO,
                Columns.MNU_ATIVO
        };
    }

}
