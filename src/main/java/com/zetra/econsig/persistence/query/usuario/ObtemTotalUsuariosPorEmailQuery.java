package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListaUsuarioPorEmailQuery</p>
 * <p>Description: Obtém o total de usuários NÃO SERVIDORES por e-mail.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemTotalUsuariosPorEmailQuery extends HQuery {

    public String usuEmail;
    public String usuCpfExceto;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select count(*) ");
        corpoBuilder.append("from Usuario usu ");
        corpoBuilder.append("where usu.statusLogin.stuCodigo <> '").append(CodedValues.STU_EXCLUIDO).append("' ");
        corpoBuilder.append("and usu.usuEmail = :usuEmail ");
        corpoBuilder.append("and not exists (select 1 from usu.usuarioSerSet usr) ");

        if (!TextHelper.isNull(usuCpfExceto)) {
            corpoBuilder.append(" and coalesce(usu.usuCpf, '') <> :usuCpf");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("usuEmail", usuEmail, query);

        if (!TextHelper.isNull(usuCpfExceto)) {
            defineValorClausulaNomeada("usuCpf", usuCpfExceto, query);
        }

        return query;
    }
}