package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: UsuarioOrgPodeModificarUsuQuery</p>
 * <p>Description: Verifica se o usuário de ORG pode modificar um outro usuário,
 * ou seja se este é do próprio órgão.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioOrgPodeModificarUsuQuery extends HQuery {

    public String usuCodigoResponsavel;
    public String usuCodigoAfetado;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT 1 FROM Usuario usu ");
        corpoBuilder.append("INNER JOIN usu.usuarioOrgSet uorR ");
        corpoBuilder.append("WHERE usu.usuCodigo ").append(criaClausulaNomeada("usuCodigoResponsavel", usuCodigoResponsavel));
        corpoBuilder.append(" AND usu.statusLogin.stuCodigo = '").append(CodedValues.STU_ATIVO).append("'");
        corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM UsuarioOrg uorA");
        corpoBuilder.append(" WHERE uorA.orgCodigo = uorR.orgCodigo");
        corpoBuilder.append(" AND uorA.usuCodigo ").append(criaClausulaNomeada("usuCodigoAfetado", usuCodigoAfetado)).append(")");
        corpoBuilder.append(" OR EXISTS (SELECT 1 FROM UsuarioSer usrA");
        corpoBuilder.append(" INNER JOIN usrA.servidor serA");
        corpoBuilder.append(" INNER JOIN serA.registroServidorSet rseA");
        corpoBuilder.append(" WHERE rseA.orgao.orgCodigo = uorR.orgCodigo");
        corpoBuilder.append(" AND usrA.usuCodigo ").append(criaClausulaNomeada("usuCodigoAfetado", usuCodigoAfetado)).append(")");
        corpoBuilder.append(" OR EXISTS (SELECT 1 FROM UsuarioCsa ucaA WHERE ");
        corpoBuilder.append(" ucaA.usuCodigo ").append(criaClausulaNomeada("usuCodigoAfetado", usuCodigoAfetado)).append(")");
        corpoBuilder.append(")");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("usuCodigoResponsavel", usuCodigoResponsavel, query);
        defineValorClausulaNomeada("usuCodigoAfetado", usuCodigoAfetado, query);
        return query;
    }
}
