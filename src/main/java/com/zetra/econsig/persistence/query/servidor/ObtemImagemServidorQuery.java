package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

public class ObtemImagemServidorQuery extends HQuery {
    public String cpfServidor;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String build = "select ims.imsCpf, " +
                " ims.imsNomeArquivo";

        StringBuilder corpoBuilder = new StringBuilder(build);

        corpoBuilder.append(" from ImagemServidor ims ");
        corpoBuilder.append(" where ims.imsCpf ").append(criaClausulaNomeada("cpfServidor", cpfServidor));

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("cpfServidor", cpfServidor, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.IMS_CPF,
                Columns.IMS_NOME_ARQUIVO
        };
    }
}

