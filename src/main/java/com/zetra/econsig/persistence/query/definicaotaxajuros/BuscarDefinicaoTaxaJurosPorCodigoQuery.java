package com.zetra.econsig.persistence.query.definicaotaxajuros;


import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: BuscarDefinicaoTaxaJurosQuery</p>
 * <p>Description: Listagem de definição de taxa de juros</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author: junio.goncalves $
 * $Revision: 20570 $
 * $Date: 2016-09-20 20:11:04 -0300 (Ter, 20 set 2016) $
 */
public class BuscarDefinicaoTaxaJurosPorCodigoQuery extends HQuery {

	public String dtjCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        
        corpo.append(" select dtj.dtjCodigo, ");
        corpo.append(" dtj.dtjTaxaJuros ");
        corpo.append(" from DefinicaoTaxaJuros dtj where ");
        corpo.append(" dtj.dtjCodigo ").append(criaClausulaNomeada("dtjCodigo", dtjCodigo));
                
        Query<Object[]> query = instanciarQuery(session, corpo.toString());
        
        defineValorClausulaNomeada("dtjCodigo", dtjCodigo, query);
        
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.DTJ_CODIGO,
                Columns.CFT_VLR,
        };
    }
}
