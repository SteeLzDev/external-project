package com.zetra.econsig.persistence.query.ajuda;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaFuncaoPapelAcessoRecursoQuery</p>
 * <p>Description: Listagem de funções e papeis dos acessos recursos.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaFuncaoPapelAcessoRecursoQuery extends HQuery {

    public List<String> acrCodigos;
    public String acrRecurso;
    public List<String> funCodigos;
    public String acrOperacao;
    public String acrParametro;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        corpo.append("select trim(acessoRecurso.acrCodigo), ");
        corpo.append("    trim(papel.papCodigo), ");
        corpo.append("    papel.papDescricao, ");
        corpo.append("    trim(funcao.funCodigo), ");
        corpo.append("    funcao.funDescricao, ");
        corpo.append("    acessoRecurso.acrRecurso, ");
        corpo.append("    case when exists (select 1 from Ajuda ajuda where ajuda.acrCodigo = acessoRecurso.acrCodigo) then 1 else 0 end as possui_ajuda ");
        corpo.append("from AcessoRecurso acessoRecurso ");
        corpo.append("left outer join acessoRecurso.funcao funcao ");
        corpo.append("left outer join acessoRecurso.papel papel ");
        corpo.append("where 1 = 1");

        if (acrCodigos != null && !acrCodigos.isEmpty()) {
            corpo.append(" and acessoRecurso.acrCodigo ").append(criaClausulaNomeada("acrCodigos", acrCodigos));
        }
        if (!TextHelper.isNull(acrRecurso)) {
            corpo.append(" and acessoRecurso.acrRecurso ").append(criaClausulaNomeada("acrRecurso", acrRecurso));
        }
        if (funCodigos != null && !funCodigos.isEmpty()) {
            corpo.append(" and funcao.funCodigo ").append(criaClausulaNomeada("funCodigos", funCodigos));
        }
        if (!TextHelper.isNull(acrParametro)) {
            corpo.append(" and acessoRecurso.acrParametro ").append(criaClausulaNomeada("acrParametro", acrParametro));
        }
        if (!TextHelper.isNull(acrOperacao)) {
            corpo.append(" and acessoRecurso.acrOperacao ").append(criaClausulaNomeada("acrOperacao", acrOperacao));
        }

        corpo.append(" order by acessoRecurso.acrRecurso, funcao.funDescricao, papel.papCodigo ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (acrCodigos != null && !acrCodigos.isEmpty()) {
            defineValorClausulaNomeada("acrCodigos", acrCodigos, query);
        }
        if (!TextHelper.isNull(acrRecurso)) {
            defineValorClausulaNomeada("acrRecurso", acrRecurso, query);
        }
        if (funCodigos != null && !funCodigos.isEmpty()) {
            defineValorClausulaNomeada("funCodigos", funCodigos, query);
        }
        if (!TextHelper.isNull(acrParametro)) {
            defineValorClausulaNomeada("acrParametro", acrParametro, query);
        }
        if (!TextHelper.isNull(acrOperacao)) {
            defineValorClausulaNomeada("acrOperacao", acrOperacao, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {Columns.ACR_CODIGO,
                Columns.ACR_PAP_CODIGO,
                Columns.PAP_DESCRICAO,
                Columns.ACR_FUN_CODIGO,
                Columns.FUN_DESCRICAO,
                Columns.ACR_RECURSO,
                "possui_ajuda"};
    }
}