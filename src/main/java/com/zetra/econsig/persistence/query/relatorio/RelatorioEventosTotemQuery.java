package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;

public class RelatorioEventosTotemQuery extends ReportHQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        corpo.append(" SELECT 1 as MATRICULA, 1 as CPF, 1 as DATA, 1 as IP, 1 as DESCRICAO, 1 as FOTO ");
        corpo.append(" FROM Consignante ");
        // Não é necessário utilizar a tabela exata do TOTEM
        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "MATRICULA",
                "CPF",
                "DATA",
                "IP",
                "DESCRICAO",
                "FOTO"
        };
    }

}
