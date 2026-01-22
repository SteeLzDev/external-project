package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.UsuarioCsaPodeModificarPerfilQuery;

public class UsuarioCsaPodeModificarPerfilQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        UsuarioCsaPodeModificarPerfilQuery query = new UsuarioCsaPodeModificarPerfilQuery();
        query.usuCodigoResponsavel = "123";
        query.perCodigoAfetado = "123";

        executarConsulta(query);
    }
}

