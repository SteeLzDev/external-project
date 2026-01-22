package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ObtemServidorQuery;

public class ObtemServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemServidorQuery query = new ObtemServidorQuery();
        query.rseCodigo = "123";
        query.serCodigo = "123";
        query.vrsCodigo = "123";
        query.retornaMargem = true;

        executarConsulta(query);
    }
}

