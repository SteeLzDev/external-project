package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaConsignatariaSaldoDevedorServidorQuery;

public class ListaConsignatariaSaldoDevedorServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignatariaSaldoDevedorServidorQuery query = new ListaConsignatariaSaldoDevedorServidorQuery();
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

