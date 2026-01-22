package com.zetra.econsig.persistence.query.usuario;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemUsuarioSupQuery</p>
 * <p>Description: Retornar usuarios de SUPorte que possuem um CPF informado.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemUsuarioSupQuery extends HQuery {

    public String usuCodigo;
    public String usuCpf;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT usu.usuCodigo, usu.usuCpf ");
        corpoBuilder.append("FROM Usuario usu ");
        corpoBuilder.append("LEFT OUTER JOIN usu.usuarioSupSet usuarioSup ");

        corpoBuilder.append("WHERE 1=1 ");
        corpoBuilder.append("AND usuarioSup.usuCodigo IS NOT NULL ");

        if (!TextHelper.isNull(usuCpf)) {
            corpoBuilder.append(" AND usu.usuCpf ").append(criaClausulaNomeada("usuCpf", usuCpf));
        }
        if (!TextHelper.isNull(usuCodigo)) {
            corpoBuilder.append(" AND usu.usuCodigo <> :usuCodigo");
        }

        List<Object> status = new ArrayList<Object>();
        status.add(CodedValues.STU_ATIVO);
        corpoBuilder.append(" AND usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("status", status));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(usuCpf)) {
            defineValorClausulaNomeada("usuCpf", usuCpf, query);
        }
        if (!TextHelper.isNull(usuCodigo)) {
            defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }

        defineValorClausulaNomeada("status", status, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_CODIGO,
                Columns.USU_CPF
        };
    }
}