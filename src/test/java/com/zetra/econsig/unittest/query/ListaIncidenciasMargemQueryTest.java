package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaIncidenciasMargemQuery;

public class ListaIncidenciasMargemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaIncidenciasMargemQuery query = new ListaIncidenciasMargemQuery();

        executarConsulta(query);
    }
}

