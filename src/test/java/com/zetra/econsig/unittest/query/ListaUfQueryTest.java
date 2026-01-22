package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.uf.ListaUfQuery;

public class ListaUfQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaUfQuery query = new ListaUfQuery();

        executarConsulta(query);
    }
}

