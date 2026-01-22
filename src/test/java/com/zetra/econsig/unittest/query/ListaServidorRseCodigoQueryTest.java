package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaServidorRseCodigoQuery;

public class ListaServidorRseCodigoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String rseCodigo = "123";

        ListaServidorRseCodigoQuery query = new ListaServidorRseCodigoQuery(rseCodigo);

        executarConsulta(query);
    }
}

