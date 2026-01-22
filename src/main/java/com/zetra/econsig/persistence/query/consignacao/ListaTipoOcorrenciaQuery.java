package com.zetra.econsig.persistence.query.consignacao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTipoOcorrenciaQuery</p>
 * <p>Description: Listagem de Tipos de Ocorrencia</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTipoOcorrenciaQuery extends HQuery {

    public List<String> tocCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpo = new StringBuilder();
        corpo.append(" select ");
        corpo.append(" toc.tocCodigo, ");
        corpo.append(" toc.tocDescricao ");
        corpo.append(" from TipoOcorrencia toc ");
        corpo.append(" where 1 = 1 ");

        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            corpo.append(" and toc.tocCodigo ").append(criaClausulaNomeada("tocCodigos", tocCodigos));
        }

        corpo.append(" order by toc.tocDescricao ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            defineValorClausulaNomeada("tocCodigos", tocCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TOC_CODIGO,
                Columns.TOC_DESCRICAO
        };
    }
}
