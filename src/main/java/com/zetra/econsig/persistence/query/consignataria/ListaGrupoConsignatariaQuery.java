package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaGrupoConsignatariaQuery</p>
 * <p>Description: Listagem de Grupo de Consignatária</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaGrupoConsignatariaQuery extends HQuery {
    
	public String tgcCodigo;
	
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "tgc.tgcCodigo, " +
                       "tgc.tgcIdentificador, " +
                       "tgc.tgcDescricao ";
        
        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append("from TipoGrupoConsignataria tgc ");
        
        if (!TextHelper.isNull(tgcCodigo)) {
        	corpoBuilder.append(" where tgc.tgcCodigo ").append(criaClausulaNomeada("tgcCodigo", tgcCodigo));
        }
        
        corpoBuilder.append(" order by tgc.tgcIdentificador");
        
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(tgcCodigo)) {
        	defineValorClausulaNomeada("tgcCodigo", tgcCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
    			Columns.TGC_CODIGO,
    			Columns.TGC_IDENTIFICADOR,
    			Columns.TGC_DESCRICAO
    	};
    }
}
