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
public class FuncoesPerfilQuery extends HQuery {

    public String perCodigo;
    public String usuCodigo;
    public String funCodigo;
    public String funDescricao;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select fun.funCodigo, fun.funDescricao"
                       + (!TextHelper.isNull(usuCodigo) ? ", eaf.eafIpAcesso, eaf.eafDdnsAcesso " : ", '','' ")
                       + " from Funcao fun "
                       + " inner join fun.funcaoPerfilSet fpe "
                       + (!TextHelper.isNull(usuCodigo) ? "left join fun.enderecoAcessoFuncaoSet eaf with (eaf.usuCodigo = :usuCodigo) " : " ")
                       + " where fpe.perCodigo = :perCodigo "
                       + (TextHelper.isNull(funCodigo) ? "" : " and fun.funCodigo " + criaClausulaNomeada("funCodigo", funCodigo))
                       + (TextHelper.isNull(funDescricao) ? "" : " and fun.funDescricao " + criaClausulaNomeada("funDescricao", funDescricao))
                       + " order by fun.funDescricao";

        Query<Object[]> query = instanciarQuery(session, corpo);

        if (!TextHelper.isNull(perCodigo)) {
            query.setParameter("perCodigo", perCodigo);
        }

        if (!TextHelper.isNull(usuCodigo)) {
            query.setParameter("usuCodigo", usuCodigo);
        }

        if (!TextHelper.isNull(funCodigo)) {
            defineValorClausulaNomeada("funCodigo", funCodigo, query);
        }

        if (!TextHelper.isNull(funDescricao)) {
            defineValorClausulaNomeada("funDescricao", funDescricao, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.FUN_CODIGO,
                Columns.FUN_DESCRICAO,
                Columns.EAF_IP_ACESSO,
                Columns.EAF_DDNS_ACESSO};
    }
}
