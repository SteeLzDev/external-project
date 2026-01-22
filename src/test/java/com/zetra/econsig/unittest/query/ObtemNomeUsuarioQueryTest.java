package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ObtemNomeUsuarioQuery;

public class ObtemNomeUsuarioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemNomeUsuarioQuery query = new ObtemNomeUsuarioQuery();
        query.count = false;
        query.usuLogin = "123";
        query.usuCodigo = "123";

        executarConsulta(query);
    }
}

