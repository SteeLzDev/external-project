package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.UsuarioCsaCorPodeConsultarAdeCompraQuery;

public class UsuarioCsaCorPodeConsultarAdeCompraQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        UsuarioCsaCorPodeConsultarAdeCompraQuery query = new UsuarioCsaCorPodeConsultarAdeCompraQuery();
        query.origem = true;
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.usuCodigo = "123";

        executarConsulta(query);
    }
}

