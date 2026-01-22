package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaConsignatariaIdentificadorQuery;

public class ListaConsignatariaIdentificadorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignatariaIdentificadorQuery query = new ListaConsignatariaIdentificadorQuery();
        executarConsulta(query);
    }
}

