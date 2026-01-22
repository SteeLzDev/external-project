package com.zetra.econsig.persistence.query.senha;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ListaSenhaAutorizacaoServidorExpiradaQuery</p>
 * <p>Description: Listagem de senhas de autorização dos servidores que estão expiradas</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaSenhaAutorizacaoServidorExpiradaQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT sas.usuCodigo, sas.sasSenha ");
        corpoBuilder.append("FROM SenhaAutorizacaoServidor sas ");

        // DESENV-15226 : join com usuário servidor para ignorar usuários que não tem mais ligação
        corpoBuilder.append("INNER JOIN sas.usuario usu ");
        corpoBuilder.append("INNER JOIN usu.usuarioSerSet usr ");

        corpoBuilder.append("WHERE sas.sasDataExpiracao <= current_date() ");

        return instanciarQuery(session, corpoBuilder.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "USU_CODIGO",
                "SENHA"
        };
    }
}
