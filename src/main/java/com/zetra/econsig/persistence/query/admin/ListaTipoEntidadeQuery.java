package com.zetra.econsig.persistence.query.admin;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTipoEntidadeQuery</p>
 * <p>Description: Listagem de Tipos Entidade</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTipoEntidadeQuery extends HQuery {
    public List<String> tipoEntidadeCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo =
            "select " +
            "ten.tenCodigo, " +
            "ten.tenDescricao ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from TipoEntidade ten");
        corpoBuilder.append(" where 1=1 ");

        if (tipoEntidadeCodigo != null && !tipoEntidadeCodigo.isEmpty()) {
            corpoBuilder.append(" and ten.tenCodigo ").append(criaClausulaNomeada("tipoEntidadeCodigo",tipoEntidadeCodigo));
        }

        corpoBuilder.append(" order by ten.tenDescricao ");
        
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (tipoEntidadeCodigo != null && !tipoEntidadeCodigo.isEmpty()) {
            defineValorClausulaNomeada("tipoEntidadeCodigo", tipoEntidadeCodigo, query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TEN_CODIGO,
                Columns.TEN_DESCRICAO
        };
    }

}
