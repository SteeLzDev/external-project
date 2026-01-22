package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaUsuarioAtivoQuery</p>
 * <p>Description: Lista os usuários ativos.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaUsuarioAtivoComEmailQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String statusAtivo = CodedValues.STU_ATIVO;

        final StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select usuario.usuCodigo, ");
        corpoBuilder.append("usuario.usuNome, ");
        corpoBuilder.append("usuario.usuEmail, ");
        corpoBuilder.append("usuario.usuDataExpSenha, ");
        corpoBuilder.append("usuario.usuAutenticaSso, ");
        corpoBuilder.append("perfilUsuario.perfil.papCodigo ");
        corpoBuilder.append("from Usuario usuario ");
        corpoBuilder.append("left outer join usuario.perfilUsuarioSet perfilUsuario ");
        corpoBuilder.append("left outer join perfilUsuario.perfil perfil ");
        corpoBuilder.append("left outer join usuario.usuarioCseSet usuarioCse ");
        corpoBuilder.append("left outer join usuario.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append("left outer join usuario.usuarioCorSet usuarioCor ");
        corpoBuilder.append("left outer join usuario.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append("left outer join usuario.usuarioSerSet usuarioSer ");

        corpoBuilder.append(" where 1 = 1 ");
        corpoBuilder.append(" and usuario.statusLogin.stuCodigo ").append(criaClausulaNomeada("statusAtivo", statusAtivo));
        corpoBuilder.append(" and usuario.usuEmail is not null ");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("statusAtivo", statusAtivo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_CODIGO,
                Columns.USU_NOME,
                Columns.USU_EMAIL,
                Columns.USU_DATA_EXP_SENHA,
                Columns.USU_AUTENTICA_SSO,
                Columns.PER_PAP_CODIGO
        };
    }
}