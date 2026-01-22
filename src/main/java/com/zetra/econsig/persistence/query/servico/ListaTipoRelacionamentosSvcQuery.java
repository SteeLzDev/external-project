package com.zetra.econsig.persistence.query.servico;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTipoRelacionamentosSvcQuery</p>
 * <p>Description: Lista relacionamentos de servicos existentes no sistema para
 * armazenar em cache para evitar validações desnecessárias.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 */
public class ListaTipoRelacionamentosSvcQuery extends HQuery {
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select relServico.tipoNatureza.tntCodigo ");
        corpoBuilder.append("from RelacionamentoServico relServico ");
        corpoBuilder.append("group by relServico.tipoNatureza.tntCodigo");

        return instanciarQuery(session, corpoBuilder.toString());        
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSV_TNT_CODIGO
        };
    }
}
