package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ListaUsuariosSerRseQuery;

public class ListaUsuariosSerRseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaUsuariosSerRseQuery query = new ListaUsuariosSerRseQuery();
        query.count = false;
        query.rseCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

