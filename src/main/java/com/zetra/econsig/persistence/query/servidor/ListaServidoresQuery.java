package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaServidoresQuery</p>
 * <p>Description: Retornar informações a respeito de servidores.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServidoresQuery extends HQuery {

    public String serCpf;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" select ser.serNome, ser.serEmail, ser.serCpf ");
        corpoBuilder.append(" from Servidor ser");
        corpoBuilder.append(" inner join ser.registroServidorSet rse");
        corpoBuilder.append(" where rse.statusRegistroServidor.srsCodigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')");

        if (!TextHelper.isNull(serCpf)) {
            corpoBuilder.append(" AND ser.serCpf ").append(criaClausulaNomeada("serCpf", serCpf));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        query.setMaxResults(1);

        if (!TextHelper.isNull(serCpf)) {
            defineValorClausulaNomeada("serCpf", serCpf, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SER_NOME,
                Columns.SER_EMAIL,
                Columns.SER_CPF
        };
    }
}