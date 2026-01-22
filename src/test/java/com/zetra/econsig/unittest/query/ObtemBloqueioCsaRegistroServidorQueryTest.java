package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ObtemBloqueioCsaRegistroServidorQuery;

public class ObtemBloqueioCsaRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemBloqueioCsaRegistroServidorQuery query = new ObtemBloqueioCsaRegistroServidorQuery();
        query.rseCodigo = "123";
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

