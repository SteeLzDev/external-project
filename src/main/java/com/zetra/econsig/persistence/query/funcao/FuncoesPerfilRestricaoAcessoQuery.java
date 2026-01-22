package com.zetra.econsig.persistence.query.funcao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: FuncoesPerfilQuery</p>
 * <p>Description: Lista as funções de um usuário com perfil fixo</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FuncoesPerfilRestricaoAcessoQuery extends HQuery {

    public String perCodigo;
    public String papel = null;


    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {


        String corpo = "select funcao.funCodigo, funcao.funDescricao " +
                       "from  AcessoRecurso acr " +
                       "join acr.funcao funcao " +
                       "inner join funcao.funcaoPerfilSet fpe " +
                       "join funcao.papelFuncaoSet papCod " +
                       "where fpe.perCodigo = :perCodigo " +
                       "and papCod.papCodigo = :papel " +
                       "group by funcao.funCodigo " +
                       "order by funcao.funDescricao";


        Query<Object[]> query = instanciarQuery(session, corpo);

        if (!TextHelper.isNull(perCodigo)) {
            query.setParameter("perCodigo", perCodigo);
            query.setParameter("papel", papel);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.FUN_CODIGO,
                Columns.FUN_DESCRICAO};
    }
}
