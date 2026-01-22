package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.menu.ListaMenuQuery;

public class ListaMenuQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaMenuQuery query = new ListaMenuQuery();
        query.mnuCodigo = "123";
        query.mnuSequencia = 1;
        query.mnuAtivo = 1;

        executarConsulta(query);
    }
}

