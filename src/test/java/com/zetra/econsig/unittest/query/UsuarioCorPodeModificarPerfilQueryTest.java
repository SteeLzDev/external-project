package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.UsuarioCorPodeModificarPerfilQuery;

public class UsuarioCorPodeModificarPerfilQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        UsuarioCorPodeModificarPerfilQuery query = new UsuarioCorPodeModificarPerfilQuery();
        query.usuCodigoResponsavel = "123";
        query.perCodigoAfetado = "123";

        executarConsulta(query);
    }
}

