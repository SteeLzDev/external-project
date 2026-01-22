package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaRegistrosServidoresExcluidosQuery;

public class ListaRegistrosServidoresExcluidosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaRegistrosServidoresExcluidosQuery query = new ListaRegistrosServidoresExcluidosQuery();
        query.count = false;

        executarConsulta(query);
    }
}

