package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaBloqueioCsaServidorQuery;

public class ListaBloqueioCsaServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaBloqueioCsaServidorQuery query = new ListaBloqueioCsaServidorQuery();
        query.rseCodigo = "123";
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

