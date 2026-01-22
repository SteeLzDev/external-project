package com.zetra.econsig.persistence.query.servico;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaGrupoServicoQuery</p>
 * <p>Description: Listagem de Grupos de Serviço</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaGrupoServicoQuery extends HQuery {
    
    public boolean orderById;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "tgs.tgsCodigo, " +
                       "tgs.tgsGrupo, " +
                       "tgs.tgsQuantidade, " +
                       "tgs.tgsQuantidadePorCsa, " +
                       "tgs.tgsIdentificador " +
                       "from TipoGrupoSvc tgs " +
                       "order by " + (orderById ? "tgs.tgsIdentificador" : "tgs.tgsGrupo");
        
        return instanciarQuery(session, corpo);
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
                Columns.TGS_CODIGO,
                Columns.TGS_GRUPO,
                Columns.TGS_QUANTIDADE,
                Columns.TGS_QUANTIDADE_POR_CSA,
                Columns.TGS_IDENTIFICADOR
    	};
    }
}
