package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.UsuarioEstPodeModificarUsuQuery;

public class UsuarioEstPodeModificarUsuQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        UsuarioEstPodeModificarUsuQuery query = new UsuarioEstPodeModificarUsuQuery();
        query.usuCodigoResponsavel = "123";
        query.usuCodigoAfetado = "123";

        executarConsulta(query);
    }
}

