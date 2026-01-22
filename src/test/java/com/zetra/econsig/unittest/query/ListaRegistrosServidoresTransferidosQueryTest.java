package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaRegistrosServidoresTransferidosQuery;

public class ListaRegistrosServidoresTransferidosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaRegistrosServidoresTransferidosQuery query = new ListaRegistrosServidoresTransferidosQuery();
        query.count = false;

        executarConsulta(query);
    }
}

