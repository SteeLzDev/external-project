package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.margem.ObtemTotalValorExcedenteProporcionalQuery;

public class ObtemTotalValorExcedenteProporcionalQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalValorExcedenteProporcionalQuery query = new ObtemTotalValorExcedenteProporcionalQuery();
        query.rseCodigo = "123";
        query.csaCodigo = "267";
        query.adeIncMargem = 1;
        query.sadCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

