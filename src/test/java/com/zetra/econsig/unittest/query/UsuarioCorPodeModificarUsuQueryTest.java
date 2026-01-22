package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.UsuarioCorPodeModificarUsuQuery;

public class UsuarioCorPodeModificarUsuQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        UsuarioCorPodeModificarUsuQuery query = new UsuarioCorPodeModificarUsuQuery();
        query.usuCodigoResponsavel = "123";
        query.usuCodigoAfetado = "123";

        executarConsulta(query);
    }
}

