package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ObtemUsuarioSupQuery;

public class ObtemUsuarioSupQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemUsuarioSupQuery query = new ObtemUsuarioSupQuery();
        query.usuCodigo = "123";
        query.usuCpf = "123";

        executarConsulta(query);
    }
}

