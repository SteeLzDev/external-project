package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaServidoresQuery;

public class ListaServidoresQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServidoresQuery query = new ListaServidoresQuery();
        query.serCpf = "123";

        executarConsulta(query);
    }
}

