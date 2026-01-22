package com.zetra.econsig.persistence.query.ajuda;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTopicosAjudaQuery</p>
 * <p>Description: Listagem dos tópicos de Ajuda</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTopicosAjudaQuery extends HQuery {

    public String papCodigo;
    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        List<String> papeis = new ArrayList<>();
        if (!TextHelper.isNull(papCodigo)) {
            papeis.add(papCodigo);
        } else if (responsavel.isSup()) {
            papeis.add("7");
        } else if (responsavel.isCse()) {
            papeis.add("1");
        } else if (responsavel.isCsa()) {
            papeis.add("2");
        } else if (responsavel.isOrg()) {
            papeis.add("3");
        } else if (responsavel.isCor()) {
            papeis.add("4");
        } else if (responsavel.isSer()) {
            papeis.add("6");
        }

        StringBuilder corpo = new StringBuilder();
        corpo.append("select distinct trim(acessoRecurso.acrCodigo), ");
        corpo.append("    trim(papel.papCodigo), ");
        corpo.append("    papel.papDescricao, ");
        corpo.append("    trim(funcao.funCodigo), ");
        corpo.append("    funcao.funDescricao, ");
        corpo.append("    ajuda.ajuTitulo, ");
        corpo.append("    ajuda.ajuSequencia, ");
        corpo.append("    ajuda.ajuAtivo ");
        corpo.append("from Ajuda ajuda ");
        corpo.append("inner join ajuda.acessoRecurso acessoRecurso ");
        corpo.append("left outer join acessoRecurso.funcao funcao ");
        corpo.append("left outer join acessoRecurso.papel papel ");
        corpo.append("where 1 = 1");

        corpo.append(" and (papel.papCodigo ").append(criaClausulaNomeada("papeis", papeis));
        corpo.append(" or papel.papCodigo ").append(criaClausulaNomeada("", CodedValues.IS_NULL_KEY)).append(") ");

        corpo.append(" order by ajuda.ajuSequencia asc ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        defineValorClausulaNomeada("papeis", papeis, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {Columns.ACR_CODIGO,
                Columns.PAP_CODIGO,
                Columns.PAP_DESCRICAO,
                Columns.FUN_CODIGO,
                Columns.FUN_DESCRICAO,
                Columns.AJU_TITULO,
                Columns.AJU_SEQUENCIA,
                Columns.AJU_ATIVO};
    }
}