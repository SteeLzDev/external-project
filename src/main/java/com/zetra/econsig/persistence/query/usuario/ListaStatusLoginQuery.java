package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaStatusLoginQuery</p>
 * <p>Description: Listagem status login</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaStatusLoginQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        corpo.append("select stuCodigo as stu_codigo, stuDescricao as stu_descricao from StatusLogin");
        
        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        return query;
    }
    
    @Override
    protected String[] getFields() {
        return new String[] {Columns.STU_CODIGO,
                             Columns.STU_DESCRICAO};
    }
}
