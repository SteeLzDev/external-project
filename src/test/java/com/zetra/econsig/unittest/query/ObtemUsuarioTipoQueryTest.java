package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ObtemUsuarioTipoQuery;

public class ObtemUsuarioTipoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemUsuarioTipoQuery query = new ObtemUsuarioTipoQuery();
        query.usuCodigo = "123";
        query.usuLogin = "123";

        executarConsulta(query);
    }
}

