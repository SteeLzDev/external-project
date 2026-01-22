package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.registroservidor.ObtemTotalRegistroServidorQuery;

public class ObtemTotalRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String serCodigo = "123";

        ObtemTotalRegistroServidorQuery query = new ObtemTotalRegistroServidorQuery(serCodigo);

        executarConsulta(query);
    }
}

