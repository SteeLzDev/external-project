package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaEmailServidorQuery;

public class ListaEmailServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaEmailServidorQuery query = new ListaEmailServidorQuery();

        executarConsulta(query);
    }
}

