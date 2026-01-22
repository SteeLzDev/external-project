package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.margem.ListaContratosIncideMargemQuery;

public class ListaContratosIncideMargemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaContratosIncideMargemQuery query = new ListaContratosIncideMargemQuery();
        query.count = false;
        query.rseCodigo = "123";
        query.adeIncideMargens = java.util.List.of((short) 1, (short) 2);

        executarConsulta(query);
    }
}

