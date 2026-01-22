package com.zetra.econsig.persistence.query.funcao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaFuncoesQuery</p>
 * <p>Description: Lista funções</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaFuncoesQuery extends HQuery {

    public String tipo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String papCodigo = null;
        if (!TextHelper.isNull(tipo)) {
            papCodigo = UsuarioHelper.getPapCodigo(tipo);
        }

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select fun.funCodigo, fun.funDescricao ");
        corpoBuilder.append("from Funcao fun ");
        corpoBuilder.append("where 1=1 ");

        if (!TextHelper.isNull(papCodigo)) {
            corpoBuilder.append("and exists (select 1 from fun.papelFuncaoSet pf where pf.papCodigo ").append(criaClausulaNomeada("papCodigo", papCodigo)).append(") ");
        }

        corpoBuilder.append("order by fun.funDescricao");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(papCodigo)) {
            defineValorClausulaNomeada("papCodigo", papCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.FUN_CODIGO,
                Columns.FUN_DESCRICAO
        };
    }
}
