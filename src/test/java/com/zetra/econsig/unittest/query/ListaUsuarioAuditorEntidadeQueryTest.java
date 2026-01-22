package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ListaUsuarioAuditorEntidadeQuery;

public class ListaUsuarioAuditorEntidadeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaUsuarioAuditorEntidadeQuery query = new ListaUsuarioAuditorEntidadeQuery();
        query.count = false;

        executarConsulta(query);
    }
}

