package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaUsuarioSolicitacaoSuporteQuery</p>
 * <p>Description: Lista solicitacoes de suporte dos usuários</p>
 * <p>Copyright: Copyright (c) 2021</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaUsuarioSolicitacaoSuporteQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" select sos.sosChave,");
        corpoBuilder.append(" sos.usuario.usuNome");
        corpoBuilder.append(" from SolicitacaoSuporte sos ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SOS_CHAVE,
                Columns.USU_NOME
        };
    }

}
