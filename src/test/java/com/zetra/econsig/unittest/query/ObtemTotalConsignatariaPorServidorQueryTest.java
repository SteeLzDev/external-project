package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ObtemTotalConsignatariaPorServidorQuery;

public class ObtemTotalConsignatariaPorServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalConsignatariaPorServidorQuery query = new ObtemTotalConsignatariaPorServidorQuery();
        query.rseCodigo = "123";
        query.csaCodigo = "267";
        query.adeCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

