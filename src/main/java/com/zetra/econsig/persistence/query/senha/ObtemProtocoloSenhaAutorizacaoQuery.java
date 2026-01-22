package com.zetra.econsig.persistence.query.senha;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemProtocoloSenhaAutorizacaoQuery</p>
 * <p>Description: Retorna o protocolo da senha de autorização de um usuário servidor.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemProtocoloSenhaAutorizacaoQuery extends HQuery {

    public String psaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT " +
                "psa.psaCodigo, " +
                "psa.psaData";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM ProtocoloSenhaAutorizacao psa ");
        corpoBuilder.append(" INNER JOIN psa.usuarioAfetado usuarioAfetado ");
        corpoBuilder.append(" INNER JOIN psa.usuarioResponsavel usuarioResponsavel ");
        corpoBuilder.append(" WHERE psa.psaCodigo ").append(criaClausulaNomeada("psaCodigo", psaCodigo));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("psaCodigo", psaCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PSA_CODIGO,
                Columns.PSA_DATA
        };
    }
}
