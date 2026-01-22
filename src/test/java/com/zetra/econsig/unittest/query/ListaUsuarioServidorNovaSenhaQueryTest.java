package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ListaUsuarioServidorNovaSenhaQuery;

public class ListaUsuarioServidorNovaSenhaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaUsuarioServidorNovaSenhaQuery query = new ListaUsuarioServidorNovaSenhaQuery();
        query.todosUsuAtivos = true;

        executarConsulta(query);
    }
}

