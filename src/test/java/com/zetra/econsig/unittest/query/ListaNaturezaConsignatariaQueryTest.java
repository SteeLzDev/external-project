package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaNaturezaConsignatariaQuery;

public class ListaNaturezaConsignatariaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaNaturezaConsignatariaQuery query = new ListaNaturezaConsignatariaQuery();

        executarConsulta(query);
    }
}

