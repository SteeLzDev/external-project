package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ObtemConsignatariaUsuarioQuery;

public class ObtemConsignatariaUsuarioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemConsignatariaUsuarioQuery query = new ObtemConsignatariaUsuarioQuery();
        query.usuCodigo = "123";

        executarConsulta(query);
    }
}

