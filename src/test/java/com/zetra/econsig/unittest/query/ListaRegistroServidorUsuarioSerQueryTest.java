package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaRegistroServidorUsuarioSerQuery;

public class ListaRegistroServidorUsuarioSerQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaRegistroServidorUsuarioSerQuery query = new ListaRegistroServidorUsuarioSerQuery();
        query.usuLogin = "123";
        query.recuperaRseExcluido = true;

        executarConsulta(query);
    }
}

