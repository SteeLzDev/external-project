package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.UsuarioOrgPodeModificarPerfilQuery;

public class UsuarioOrgPodeModificarPerfilQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        UsuarioOrgPodeModificarPerfilQuery query = new UsuarioOrgPodeModificarPerfilQuery();
        query.usuCodigoResponsavel = "123";
        query.perCodigoAfetado = "123";

        executarConsulta(query);
    }
}

