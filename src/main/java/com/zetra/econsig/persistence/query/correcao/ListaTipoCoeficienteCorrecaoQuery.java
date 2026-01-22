package com.zetra.econsig.persistence.query.correcao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTipoCoeficienteCorrecaoQuery</p>
 * <p>Description: Listagem de Tipos de Coeficiente de Correção</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTipoCoeficienteCorrecaoQuery extends HQuery {
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "tcc.tccCodigo, " +
                       "tcc.tccFormaCalc, " +
                       "tcc.tccDescricao " +
                       "from TipoCoeficienteCorrecao tcc " +
                       "order by tcc.tccDescricao";
        
        return instanciarQuery(session, corpo);
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
                Columns.TCC_CODIGO,
                Columns.TCC_FORMA_CALC,
                Columns.TCC_DESCRICAO
    	};
    }
}
