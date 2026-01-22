package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ListaUsuarioFimVigenciaQuery;

public class ListaUsuarioFimVigenciaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaUsuarioFimVigenciaQuery query = new ListaUsuarioFimVigenciaQuery();

        executarConsulta(query);
    }
}

