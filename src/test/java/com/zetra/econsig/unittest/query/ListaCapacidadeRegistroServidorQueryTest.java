package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.admin.ListaCapacidadeRegistroServidorQuery;

public class ListaCapacidadeRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCapacidadeRegistroServidorQuery query = new ListaCapacidadeRegistroServidorQuery();

        executarConsulta(query);
    }
}

