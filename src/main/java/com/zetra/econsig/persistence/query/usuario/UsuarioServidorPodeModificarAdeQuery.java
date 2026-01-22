package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: UsuarioServidorPodeModificarAdeQuery</p>
 * <p>Description: Verifica se o usuário servidor pode modificar a consignação</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioServidorPodeModificarAdeQuery extends HQuery {

    public String usuCodigo;
    public String adeCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        
        corpoBuilder.append("SELECT 1 FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.registroServidor rse ");
        corpoBuilder.append("WHERE ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        corpoBuilder.append(" AND EXISTS (SELECT 1 FROM UsuarioSer usr WHERE usr.serCodigo = rse.servidor.serCodigo");
        corpoBuilder.append(" AND usr.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo)).append(")");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        return query;
    }
}
