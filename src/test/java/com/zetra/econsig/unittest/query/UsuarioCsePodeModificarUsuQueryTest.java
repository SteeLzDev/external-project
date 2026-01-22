package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.UsuarioCsePodeModificarUsuQuery;

public class UsuarioCsePodeModificarUsuQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        UsuarioCsePodeModificarUsuQuery query = new UsuarioCsePodeModificarUsuQuery();
        query.usuCodigoAfetado = "123";

        executarConsulta(query);
    }
}

