package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: UsuarioOrgPodeModificarPerfilQuery</p>
 * <p>Description: Verifica se o usuário de ORG pode modificar um perfil,
 * ou seja se este é do próprio órgão.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioOrgPodeModificarPerfilQuery extends HQuery {

    public String usuCodigoResponsavel;
    public String perCodigoAfetado;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT 1 FROM Usuario usu ");
        corpoBuilder.append("INNER JOIN usu.usuarioOrgSet uorR ");
        corpoBuilder.append("WHERE usu.usuCodigo ").append(criaClausulaNomeada("usuCodigoResponsavel", usuCodigoResponsavel));
        corpoBuilder.append(" AND usu.statusLogin.stuCodigo = '").append(CodedValues.STU_ATIVO).append("'");
        corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM PerfilOrg porA");
        corpoBuilder.append(" WHERE porA.orgCodigo = uorR.orgCodigo");
        corpoBuilder.append(" AND porA.perCodigo ").append(criaClausulaNomeada("perCodigoAfetado", perCodigoAfetado)).append(")");
        corpoBuilder.append(" OR EXISTS (SELECT 1 FROM PerfilCsa pcaA WHERE ");
        corpoBuilder.append(" pcaA.perCodigo ").append(criaClausulaNomeada("perCodigoAfetado", perCodigoAfetado)).append(")");
        corpoBuilder.append(")");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("usuCodigoResponsavel", usuCodigoResponsavel, query);
        defineValorClausulaNomeada("perCodigoAfetado", perCodigoAfetado, query);
        return query;
    }
}
