package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ListaOcorrenciaUsuarioQuery;

public class ListaOcorrenciaUsuarioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaOcorrenciaUsuarioQuery query = new ListaOcorrenciaUsuarioQuery();
        query.count = false;
        query.ousUsuCodigo = "123";
        query.tocCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

