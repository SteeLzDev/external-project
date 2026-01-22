package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.cep.ListaCepQuery;

public class ListaCepQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCepQuery query = new ListaCepQuery();
        query.cepCodigo = "123";

        executarConsulta(query);
    }
}

