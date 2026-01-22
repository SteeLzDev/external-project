package com.zetra.econsig.persistence.query.menu;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

public class ListaItemMenuQuery extends HQuery {

    public String mnuCodigo;
    public String itmCodigo;
    public String itmCodigoPai;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "select " +
                       "mnu.mnuCodigo, " +
                       "mnu.mnuSequencia, " +
                       "mnu.mnuDescricao, " +
                       "mnu.mnuAtivo, " +
                       "itm.itmCodigo, " +
                       "itm.itemMenu.itmCodigo, " +
                       "itm.itmDescricao, " +
                       "itm.itmAtivo, " +
                       "itm.itmSequencia, " +
                       "itm.itmSeparador ";
        
        StringBuilder corpoBuilder = new StringBuilder(corpo);        
        corpoBuilder.append(" from ItemMenu itm ");
        corpoBuilder.append(" left outer join itm.menu mnu ");  
        corpoBuilder.append(" where 1=1 ");  
        
        if (!TextHelper.isNull(mnuCodigo)) {
            corpoBuilder.append(" and mnu.mnuCodigo ").append(criaClausulaNomeada("mnuCodigo", mnuCodigo));
        }
        
        if (!TextHelper.isNull(itmCodigo)) {
            corpoBuilder.append(" and itm.itmCodigo ").append(criaClausulaNomeada("itmCodigo", itmCodigo));
        }
        
        if (!TextHelper.isNull(itmCodigoPai)) {
            corpoBuilder.append(" and itm.itemMenu.itmCodigo ").append(criaClausulaNomeada("itmCodigoPai", itmCodigoPai));
        }
        
        corpoBuilder.append(" order by mnu.mnuSequencia, itm.itmSequencia ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(mnuCodigo)) {
            defineValorClausulaNomeada("mnuCodigo", mnuCodigo, query);
        }
        
        if (!TextHelper.isNull(itmCodigo)) {
            defineValorClausulaNomeada("itmCodigo", itmCodigo, query);
        }
        
        if (!TextHelper.isNull(itmCodigoPai)) {
            defineValorClausulaNomeada("itmCodigoPai", itmCodigoPai, query);
        }
        
        return query;
    }

    protected String[] getFields() {        
        return new String[] {
                Columns.MNU_CODIGO,
                Columns.MNU_SEQUENCIA,
                Columns.MNU_DESCRICAO,
                Columns.MNU_ATIVO,
                Columns.ITM_CODIGO,
                Columns.ITM_CODIGO_PAI,
                Columns.ITM_DESCRICAO,
                Columns.ITM_ATIVO,
                Columns.ITM_SEQUENCIA,
                Columns.ITM_SEPARADOR
        };
    }

}
