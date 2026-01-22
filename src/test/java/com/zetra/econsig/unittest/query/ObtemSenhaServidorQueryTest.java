package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.senha.ObtemSenhaServidorQuery;

public class ObtemSenhaServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemSenhaServidorQuery query = new ObtemSenhaServidorQuery();
        query.rseCodigo = "123";

        executarConsulta(query);
    }
}

