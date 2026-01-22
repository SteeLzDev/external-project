package com.zetra.econsig.persistence.query.funcao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarFuncaoQuery</p>
 * <p>Description: Listar funções para paginação</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarFuncaoQuery extends HQuery {

    public boolean count = false;
    public String funDescricao = null;
    public String grfDescricao = null;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();

        if (count) {
            corpoBuilder.append("select count(*) as total ");
        } else {
            corpoBuilder.append("select ");
            corpoBuilder.append("fun.funCodigo, ");
            corpoBuilder.append("fun.funDescricao, ");
            corpoBuilder.append("grf.grfCodigo, ");
            corpoBuilder.append("grf.grfDescricao ");
        }

        corpoBuilder.append(" from Funcao fun");
        corpoBuilder.append(" inner join fun.grupoFuncao grf");

        corpoBuilder.append(" where 1=1 ");

        if (!TextHelper.isNull(funDescricao)) {
            corpoBuilder.append(" and fun.funDescricao ").append(criaClausulaNomeada("funDescricao",  CodedValues.LIKE_MULTIPLO + funDescricao + CodedValues.LIKE_MULTIPLO));
        }

        if (!TextHelper.isNull(grfDescricao)) {
            corpoBuilder.append(" and grf.grfDescricao ").append(criaClausulaNomeada("grfDescricao",  CodedValues.LIKE_MULTIPLO + grfDescricao + CodedValues.LIKE_MULTIPLO));
        }

        if (!count) {
            corpoBuilder.append(" ORDER BY to_numeric(grf.grfCodigo), fun.funDescricao");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(funDescricao)) {
            defineValorClausulaNomeada("funDescricao", CodedValues.LIKE_MULTIPLO + funDescricao + CodedValues.LIKE_MULTIPLO, query);
        }

        if (!TextHelper.isNull(grfDescricao)) {
            defineValorClausulaNomeada("grfDescricao", CodedValues.LIKE_MULTIPLO + grfDescricao + CodedValues.LIKE_MULTIPLO, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.FUN_CODIGO,
                Columns.FUN_DESCRICAO,
                Columns.GRF_CODIGO,
                Columns.GRF_DESCRICAO
            };
    }

}
