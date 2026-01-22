package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: UsuarioOrgPodeModificarAdeQuery</p>
 * <p>Description: Verifica se o usuário de ORG pode modificar a consignação,
 * ou seja se é do órgão ligado ao contrato.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioOrgPodeModificarAdeQuery extends HQuery {

    public String usuCodigo;
    public String adeCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT 1 FROM AutDesconto ade ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("WHERE ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM UsuarioOrg uor WHERE uor.orgCodigo = cnv.orgao.orgCodigo");
        corpoBuilder.append(" AND uor.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo)).append(")");
        corpoBuilder.append(")");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        return query;
    }
}
