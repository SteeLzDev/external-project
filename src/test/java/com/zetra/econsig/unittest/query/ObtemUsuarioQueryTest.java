package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ObtemUsuarioQuery;

public class ObtemUsuarioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemUsuarioQuery query = new ObtemUsuarioQuery();
        query.usuLogin = "123";
        query.usuCodigo = "123";
        query.usuEmail = "123";

        executarConsulta(query);
    }
}

