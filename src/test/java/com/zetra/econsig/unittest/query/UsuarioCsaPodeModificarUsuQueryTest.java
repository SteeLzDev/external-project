package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.UsuarioCsaPodeModificarUsuQuery;

public class UsuarioCsaPodeModificarUsuQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        UsuarioCsaPodeModificarUsuQuery query = new UsuarioCsaPodeModificarUsuQuery();
        query.usuCodigoResponsavel = "123";
        query.usuCodigoAfetado = "123";

        executarConsulta(query);
    }
}

