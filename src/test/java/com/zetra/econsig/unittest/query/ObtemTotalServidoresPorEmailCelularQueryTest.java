package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ObtemTotalServidoresPorEmailCelularQuery;

public class ObtemTotalServidoresPorEmailCelularQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalServidoresPorEmailCelularQuery query = new ObtemTotalServidoresPorEmailCelularQuery();
        query.serEmail = "123";
        query.serCelular = "123";
        query.serCpfExceto = "123";

        executarConsulta(query);
    }
}

