package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: UsuarioCorPodeModificarUsuQuery</p>
 * <p>Description: Verifica se o usuário de COR pode modificar um outro usuário,
 * ou seja se este é do próprio correspondente.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioCorPodeModificarUsuQuery extends HQuery {

    public String usuCodigoResponsavel;
    public String usuCodigoAfetado;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT 1 FROM Usuario usu ");
        corpoBuilder.append("INNER JOIN usu.usuarioCorSet ucoR ");
        corpoBuilder.append("WHERE usu.usuCodigo ").append(criaClausulaNomeada("usuCodigoResponsavel", usuCodigoResponsavel));
        corpoBuilder.append(" AND usu.statusLogin.stuCodigo = '").append(CodedValues.STU_ATIVO).append("'");
        corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM UsuarioCor ucoA");
        corpoBuilder.append(" WHERE ucoA.corCodigo = ucoR.corCodigo");
        corpoBuilder.append(" AND ucoA.usuCodigo ").append(criaClausulaNomeada("usuCodigoAfetado", usuCodigoAfetado)).append(")");
        corpoBuilder.append(")");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("usuCodigoResponsavel", usuCodigoResponsavel, query);
        defineValorClausulaNomeada("usuCodigoAfetado", usuCodigoAfetado, query);
        return query;
    }
}
