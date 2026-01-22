package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.UsuarioCsaCorPodeModificarAdeCompraQuery;

public class UsuarioCsaCorPodeModificarAdeCompraQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        UsuarioCsaCorPodeModificarAdeCompraQuery query = new UsuarioCsaCorPodeModificarAdeCompraQuery();
        query.usuCodigo = "123";
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";

        executarConsulta(query);
    }
}

